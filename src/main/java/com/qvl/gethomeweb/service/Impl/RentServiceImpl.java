package com.qvl.gethomeweb.service.Impl;

import com.qvl.gethomeweb.constant.RentStatus;
import com.qvl.gethomeweb.dao.HouseDao;
import com.qvl.gethomeweb.dao.RentDao;
import com.qvl.gethomeweb.dao.UserDao;
import com.qvl.gethomeweb.dto.rent.CreateRentRequest;
import com.qvl.gethomeweb.dto.rent.RentItem;
import com.qvl.gethomeweb.dto.rent.RentQueryParams;
import com.qvl.gethomeweb.model.House;
import com.qvl.gethomeweb.model.Rent;
import com.qvl.gethomeweb.model.RentInfo;
import com.qvl.gethomeweb.model.Member;
import com.qvl.gethomeweb.redis.RedisCache;
import com.qvl.gethomeweb.service.RentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.qvl.gethomeweb.constant.HouseStatus.*;

@Service
public class RentServiceImpl implements RentService {
    private final static Logger log = LoggerFactory.getLogger(RentServiceImpl.class);
    @Autowired
    RentDao rentDao;

    @Autowired
    HouseDao houseDao;

    @Autowired
    UserDao userDao;

    @Autowired
    private RedisCache redisCache;

    @Transactional
    @Override
//    建立租屋訂單
    public Integer createRent(Integer userId, CreateRentRequest createRentRequest) {
        LocalDate startDate = createRentRequest.getStartDate();
        LocalDate endDate = createRentRequest.getEndDate();
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if ((endDate.isBefore(startDate))) {
            log.warn("開始日期為{}，結束日期為{}，無法租賃", startDate, endDate);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "結束日期不能小於開始日期");
        } else if (totalDays < 30) {
            log.warn("租期為{}天，小於30天，無法租賃", totalDays);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "租期必須大於30天");
            // 開始日期不能小於今天，結束日期不能小於開始日期
        }
//        檢查使用者是否存在
        Member member = userDao.getUserById(userId);
        if (member == null) {
            log.warn("該使用者{}不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "該使用者不存在");
        }
//        java8後出的功能，計算租期總月數
        Period period = Period.between(createRentRequest.getStartDate(), createRentRequest.getEndDate());
//        取得年數
        int years = period.getYears();
        int months = period.getMonths();
//      取得月數,不足月的部分算一個月
        if (period.getDays() >= 1) {
            months += 1;
        } else {
            months = period.getMonths();
        }
//        租期總月數
        int totalMonths = years * 12 + months;

        int totalAmount = 0;

        List<RentInfo> rentInfoList = new ArrayList<>();
        for (RentItem rentItem : createRentRequest.getRentItemList()) {
//        用houseID取得房屋資料
            House house = houseDao.getHouseById(rentItem.getHouseId());
//        確認房屋狀態
            if (house == null) {
                log.warn("該房屋{}不存在", rentItem.getHouseId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "該房屋不存在");
            } else if (house.getStatus() == RESERVED) {
                log.warn("該房屋{}已被預訂", rentItem.getHouseId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "該房屋已被預訂不可租");
            } else if (house.getStatus() == RENTED) {
                log.warn("該房屋{}已出租", rentItem.getHouseId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "該房屋已出租不可預訂");
            }
//        計算押金+一個月租金
            rentItem.setMonth(3);
            int amount = rentItem.getMonth() * house.getPricePerMonth();
//        計算租期總金額
            totalAmount = house.getPricePerMonth() * totalMonths;
            //            將房屋狀態更新為預訂中
            houseDao.updateHouseStatus(rentItem.getHouseId(), RESERVED);

            RentInfo rentInfo = new RentInfo();
//    產生訂單UUID碼paymentId 對應電子支付的orderId
            rentInfo.setPaymentId(UUID.randomUUID().toString());
            rentInfo.setHouseId(rentItem.getHouseId());
            rentInfo.setMonth(rentItem.getMonth());
            rentInfo.setAmount(amount);
            rentInfoList.add(rentInfo);
        }
        Integer rentId = rentDao.createRent(userId, totalAmount, createRentRequest);

        rentDao.createRentInfo(rentId, rentInfoList);

        return rentId;
    }

    @Transactional
    @Override
    public Integer payRent(Integer rentId, RentItem rentItem) {
        List<RentInfo> rentInfoList = new ArrayList<>();
        //   用houseID取得房屋資料
        House house = houseDao.getHouseById(rentItem.getHouseId());
        RentStatus rentStatus = RentStatus.valueOf(rentDao.getRentById(rentId).getStatus());
        if (rentStatus == RentStatus.CANCELLED || rentStatus == RentStatus.INPROGRESS) {
            log.warn("該租約{}狀態為{}，無法支付", rentId, rentStatus);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "該租約已退租或退租處理中");
//            避免有人在前台租約建立失敗後，到後台支付租金，造成押金未支付
        } else if (rentStatus == RentStatus.PENDING) {
            log.warn("該租約{}尚未支付押金", rentId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "該租約尚未支付押金");
        } else {
//        計算租金
            int amount = rentItem.getMonth() * house.getPricePerMonth();

            int paidAmount = rentDao.getPaid(rentId);
            int totalAmount = rentDao.getRentById(rentId).getTotalAmount();
            int balance = totalAmount - paidAmount;
            if (balance == 0) {
                log.warn("該租約之租金皆已繳清");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "租金已全數繳清，無需支付租金");
            } else if (amount > balance) {
                log.warn("該金額 {} 超過剩餘總租金{}", amount, balance);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "輸入金額超過剩餘總租金");
            } else if (balance < 0) {
                log.warn("該租約之租金餘額為{}，請退回押金", balance);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "尚有押金餘額須退回,無須支付租金");
            }
            RentInfo rentInfo = new RentInfo();
//    產生訂單UUID碼paymentId 對應電子支付的orderId
            rentInfo.setPaymentId(UUID.randomUUID().toString());
            rentInfo.setHouseId(rentItem.getHouseId());
            rentInfo.setMonth(rentItem.getMonth());
            rentInfo.setAmount(amount);
            rentInfoList.add(rentInfo);

            Integer rentInfoId = rentDao.createRentInfo(rentId, rentInfoList);
            return rentInfoId;
        }
    }

    //    租屋訂單與訂單明細
    @Override
    public Rent getRentById(Integer rentId) {
        Rent rent = rentDao.getRentById(rentId);
        List<RentInfo> rentInfoList = rentDao.getRentInfoByRentId(rentId);
        rent.setRentInfoList(rentInfoList);
        return rent;
    }

    @Override
    public RentInfo getRentInfoById(Integer rentInfoId) {
        return rentDao.getRentInfoById(rentInfoId);
    }

    @Override
    public List<Rent> getRentOrders(RentQueryParams rentQueryParams) {
        List<Rent> rentList = rentDao.getRentOrders(rentQueryParams);

        for (Rent rent : rentList) {
            List<RentInfo> rentInfoList = rentDao.getRentInfoByRentId(rent.getRentId());
            rent.setRentInfoList(rentInfoList);
        }
        return rentList;
    }

    @Override
    public List<RentInfo> getRentInfo(RentQueryParams rentQueryParams) {
        List<RentInfo> rentInfoList = rentDao.getRentInfo(rentQueryParams);
        return rentInfoList;
    }

    @Override
    public Integer countRentOrder(RentQueryParams rentQueryParams) {
        return rentDao.countRentOrder(rentQueryParams);
    }

    @Override
    public Integer countRentInfo(RentQueryParams rentQueryParams) {
        return rentDao.countRentInfo(rentQueryParams);
    }

    @Transactional
    @Override
    public void cancelRentById(Integer rentId) {
        Rent rent = rentDao.getRentById(rentId);

        // 首先檢查 rent 是否為 null
        if (rent == null) {
            log.warn("該租約{}不存在", rentId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "該租約不存在");
        }

        // 檢查租約狀態是否已取消
        if (rent.getStatus() == (RentStatus.CANCELLED).toString()) {
            log.warn("該租約{}已退租", rentId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "該租約已退租");
        }

        Date endDate = rent.getEndDate();
        Date today = new Date();
        long betweenMs = endDate.getTime() - today.getTime();
        long leftDays = TimeUnit.MILLISECONDS.toDays(betweenMs) + 1;
        System.out.println("days: " + leftDays);

//        超過一個月(30天)可以退租，狀態改為退租處理中
        if (leftDays > 30) {
            rentDao.updateRentStatus(rentId, RentStatus.INPROGRESS);
            // 檢查租約距離退租日是否不到一個月
        } else if (leftDays > 0 && leftDays <= 30) {
            log.warn("該租約{}距離退租日為{}天，不到一個月，無法退租", rentId, leftDays);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "該租約距離退租日不到一個月，無法退租");
        } else {
            log.info("租約{}已取消", rentId);
            rentDao.updateRentStatus(rentId, RentStatus.CANCELLED);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "該租約已過期");
        }
    }

    @Transactional
    @Override
    public void cancelCheckById(Integer rentId, Integer houseId) {
        houseDao.updateHouseStatus(houseId, AVAILABLE);
        rentDao.updateRentStatus(rentId, RentStatus.CANCELLED);
    }
}
