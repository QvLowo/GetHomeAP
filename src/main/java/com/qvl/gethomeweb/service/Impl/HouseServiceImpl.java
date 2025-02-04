package com.qvl.gethomeweb.service.Impl;

import com.qvl.gethomeweb.dao.HouseDao;
import com.qvl.gethomeweb.dto.house.HouseQueryParams;
import com.qvl.gethomeweb.dto.house.HouseRequest;
import com.qvl.gethomeweb.model.House;
import com.qvl.gethomeweb.service.HouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.qvl.gethomeweb.constant.HouseStatus.RENTED;
import static com.qvl.gethomeweb.constant.HouseStatus.RESERVED;

@Service
public class HouseServiceImpl implements HouseService {
    private final static Logger log = LoggerFactory.getLogger(RentServiceImpl.class);
    //注入HouseDao
    @Autowired
    private HouseDao houseDao;

    //    查詢全部房子資訊．並加上選填的查詢條件
    @Override
    public House getHouseById(Integer houseId) {
        return houseDao.getHouseById(houseId);
    }

    //查詢所有房屋資訊
    @Override
    public List<House> getAllHouses(HouseQueryParams houseQueryParams) {
        return houseDao.getAllHouses(houseQueryParams);
    }

    //新增房屋資訊
    @Override
    public Integer createHouse(Integer userId, HouseRequest houseRequest) {
        return houseDao.createHouse(userId, houseRequest);
    }

    //更新房屋資訊
    @Override
    public void updateHouse(Integer houseId, HouseRequest houseRequest) {
        houseDao.updateHouse(houseId, houseRequest);
    }

    //刪除房屋資訊
    @Override
    public void deleteHouseById(Integer houseId) {
        House house = houseDao.getHouseById(houseId);
        if (house.getStatus() == RESERVED || house.getStatus() == RENTED) {
            log.warn("該房屋{}狀態為{}，無法刪除", houseId, house.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "該房屋已被預訂或出租，無法刪除");
        }
        houseDao.deleteHouseById(houseId);
    }

    @Override
    public Integer countAllHouses(HouseQueryParams houseQueryParams) {
        return houseDao.countAllHouses(houseQueryParams);
    }
}
