package com.qvl.gethomeweb.dao.Impl;

import com.qvl.gethomeweb.constant.RentStatus;
import com.qvl.gethomeweb.dao.RentDao;
import com.qvl.gethomeweb.dto.rent.CreateRentRequest;
import com.qvl.gethomeweb.dto.rent.RentQueryParams;
import com.qvl.gethomeweb.model.Rent;
import com.qvl.gethomeweb.model.RentInfo;
import com.qvl.gethomeweb.rowmapper.RentInfoRowMapper;
import com.qvl.gethomeweb.rowmapper.RentRowMapper;
import com.qvl.gethomeweb.service.Impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RentDaoImpl implements RentDao {
    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    //    創建租屋訂單
    @Override
    public Integer createRent(Integer userId, Integer totalAmount, CreateRentRequest createRentRequest) {
        String sql = "INSERT INTO rent(user_id,total_amount,start_date,end_date) VALUES(:userId,:totalAmount,:startDate,:endDate)";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("totalAmount", totalAmount);
        map.put("startDate", createRentRequest.getStartDate());
        map.put("endDate", createRentRequest.getEndDate());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        int rentId = keyHolder.getKey().intValue();
        return rentId;
    }
//    創建租屋訂單明細
    @Override
    public Integer createRentInfo(Integer rentId, List<RentInfo> rentInfoList) {
        String sql = "INSERT INTO rent_info(rent_id,payment_id,house_id,amount,`month`)VALUES(:rentId,:paymentId,:houseId,:amount,:month)";
        for (int i = 0; i < rentInfoList.size(); i++) {
            RentInfo rentInfo = rentInfoList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("rentId", rentId);
            map.put("paymentId", rentInfo.getPaymentId());
            map.put("houseId", rentInfo.getHouseId());
            map.put("amount", rentInfo.getAmount());
            map.put("month", rentInfo.getMonth());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
            int rentInfoId = keyHolder.getKey().intValue();
            return rentInfoId;
        }


        return rentId;
    }

    @Override
//    取得已付租金的加總總金額
    public Integer getPaid(Integer rentId) {
        String sql = "SELECT SUM(amount) FROM rent_info WHERE rent_id = :rentId";
        Map<String, Object> map = new HashMap<>();
        map.put("rentId", rentId);
        Integer paid = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return paid;
    }

    @Override
    public Rent getRentById(Integer rentId) {
        String sql = "SELECT rent_id,user_id,status,total_amount,start_date,end_date,account_payable,created_date,last_update_date FROM rent WHERE rent_id = :rentId";
        Map<String, Object> map = new HashMap<>();
        map.put("rentId", rentId);

        List<Rent> rentList = namedParameterJdbcTemplate.query(sql, map, new RentRowMapper());
        if (rentList.size() > 0) {
            return rentList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<RentInfo> getRentInfoByRentId(Integer rentId) {
        String sql = "SELECT ri.rent_info_id,ri.payment_id,ri.transaction_id,ri.rent_id,ri.house_id,ri.amount,ri.`month`,ri.created_date,ri.last_update_date,h.house_name,h.image_url " +
                "FROM rent_info as ri " +
                "LEFT JOIN house as h ON ri.house_id = h.house_id " +
                "WHERE rent_id = :rentId";
        Map<String, Object> map = new HashMap<>();
        map.put("rentId", rentId);

        List<RentInfo> rentInfoList = namedParameterJdbcTemplate.query(sql, map, new RentInfoRowMapper());
        return rentInfoList;
    }

    @Override
    public RentInfo getRentInfoByPaymentId(String paymentId) {
        String sql = "SELECT ri.rent_info_id,ri.payment_id,ri.transaction_id,ri.rent_id,ri.house_id,ri.amount,ri.`month`,ri.created_date,ri.last_update_date,h.house_name,h.image_url " +
                "FROM rent_info as ri " + "LEFT JOIN house as h ON ri.house_id = h.house_id WHERE payment_id = :paymentId";
        Map<String, Object> map = new HashMap<>();
        map.put("paymentId", paymentId);

        List<RentInfo> rentInfoList = namedParameterJdbcTemplate.query(sql, map, new RentInfoRowMapper());
        if (!rentInfoList.isEmpty()) {
            return rentInfoList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public RentInfo getRentInfoById(Integer rentInfoId) {
        String sql = "SELECT ri.rent_info_id,ri.payment_id,ri.transaction_id,ri.rent_id,ri.house_id,ri.amount,ri.`month`,ri.created_date,ri.last_update_date,h.house_name,h.image_url " +
                "FROM rent_info as ri " + "LEFT JOIN house as h ON ri.house_id = h.house_id WHERE rent_info_id = :rentInfoId";
        Map<String, Object> map = new HashMap<>();
        map.put("rentInfoId", rentInfoId);
        List<RentInfo> rentInfoList = namedParameterJdbcTemplate.query(sql, map, new RentInfoRowMapper());
        if (rentInfoList.size() > 0) {
            return rentInfoList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer countRentOrder(RentQueryParams rentQueryParams) {
        String sql = "SELECT COUNT(*) FROM rent WHERE 1=1 ";
        Map<String, Object> map = new HashMap<>();
//        檢查使用者是否存在
        sql = addFilteringSql(sql, map, rentQueryParams);
        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return total;
    }

    @Override
    public Integer countRentInfo(RentQueryParams rentQueryParams) {
        String sql = "SELECT COUNT(*) FROM rent_info WHERE 1=1 ";
        Map<String, Object> map = new HashMap<>();
        if (rentQueryParams.getHouseId() != null) {
            sql += " AND house_id = :houseId";
            map.put("houseId", rentQueryParams.getHouseId());
        }
        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return total;
    }

    @Override
    public List<Rent> getRentOrders(RentQueryParams rentQueryParams) {
        String sql = "SELECT rent_id,user_id,status,total_amount,start_date,end_date,account_payable,created_date,last_update_date FROM rent WHERE 1=1 ";
        Map<String, Object> map = new HashMap<>();
//        檢查使用者是否存在
        sql = addFilteringSql(sql, map, rentQueryParams);

//        排序
        sql += " ORDER BY " + rentQueryParams.getOrderBy() + " " + rentQueryParams.getOrderType();
//        分頁
        sql += " LIMIT :limit OFFSET :offset";
        map.put("limit", rentQueryParams.getLimit());
        map.put("offset", rentQueryParams.getOffset());

        List<Rent> rentList = namedParameterJdbcTemplate.query(sql, map, new RentRowMapper());
        return rentList;
    }

    @Override
    public List<RentInfo> getRentInfo(RentQueryParams rentQueryParams) {
        String sql = "SELECT ri.rent_info_id,ri.payment_id,ri.transaction_id,ri.rent_id,ri.house_id,ri.amount,ri.`month`,ri.created_date,ri.last_update_date,h.house_name,h.image_url " +
                "FROM rent_info as ri LEFT JOIN house as h ON ri.house_id = h.house_id WHERE 1=1";
        Map<String, Object> map = new HashMap<>();
        if (rentQueryParams.getHouseId() != null) {
            sql += " AND ri.house_id = :houseId";
            map.put("houseId", rentQueryParams.getHouseId());
        }
        //        排序
        sql += " ORDER BY " + rentQueryParams.getOrderBy() + " " + rentQueryParams.getOrderType();
//        分頁
        sql += " LIMIT :limit OFFSET :offset";
        map.put("limit", rentQueryParams.getLimit());
        map.put("offset", rentQueryParams.getOffset());

        List<RentInfo> rentInfoList = namedParameterJdbcTemplate.query(sql, map, new RentInfoRowMapper());
        return rentInfoList;
    }

    //    檢查使用者
    private String addFilteringSql(String sql, Map<String, Object> map, RentQueryParams rentQueryParams) {
        if (rentQueryParams.getUserId() != null) {
            sql += " AND user_id = :userId";
            map.put("userId", rentQueryParams.getUserId());
        }  if (rentQueryParams.getRentId() != null) {
            sql += " AND rent_id = :rentId";
            map.put("rentId", rentQueryParams.getRentId());
        }
        return sql;
    }

    @Override
    public void updateRentStatus(Integer rentId, RentStatus status) {
        String sql = "UPDATE rent SET status = :status WHERE rent_id = :rentId";
        Map<String, Object> map = new HashMap<>();
        map.put("rentId", rentId);
        map.put("status", status.toString());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void updateAccountPayable(Integer rentId, Integer accountPayable) {
        String sql = "UPDATE rent SET account_payable = :accountPayable WHERE rent_id = :rentId";
        Map<String, Object> map = new HashMap<>();
        map.put("rentId", rentId);
        map.put("accountPayable", accountPayable);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void setTransactionId(String paymentId, String transactionId) {
        String sql = "UPDATE rent_info SET transaction_id = :transactionId WHERE payment_id = :paymentId";
        Map<String, Object> map = new HashMap<>();
        map.put("transactionId", transactionId);
        map.put("paymentId", paymentId);

        namedParameterJdbcTemplate.update(sql, map);

    }

    @Override
    public void deleteRentInfo(String paymentId) {
        String sql = "DELETE FROM rent_info WHERE payment_id = :paymentId";
        Map<String, Object> map = new HashMap<>();
        map.put("paymentId", paymentId);

        namedParameterJdbcTemplate.update(sql, map);
    }
}

