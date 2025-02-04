package com.qvl.gethomeweb.dao.Impl;

import com.qvl.gethomeweb.constant.HouseStatus;
import com.qvl.gethomeweb.dao.HouseDao;
import com.qvl.gethomeweb.dto.house.HouseQueryParams;
import com.qvl.gethomeweb.dto.house.HouseRequest;
import com.qvl.gethomeweb.model.House;
import com.qvl.gethomeweb.rowmapper.HouseRowMapper;
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
public class HouseDaoImpl implements HouseDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    //    查詢一筆房子資訊
    public House getHouseById(Integer houseId) {
        // 設定sql語法透過houseId查詢房屋資訊
        String sql = "SELECT user_id,house_name,house_id,house_type, address, image_url, price_per_month, status, description ,created_date,last_update_date from house  where house_id =:houseId";

        //創建Map物件，用來存放房屋
        Map<String, Object> map = new HashMap<>();
        map.put("houseId", houseId);

        //使用NamedParameterJdbcTemplate查詢一批房屋資訊放到List中
        List<House> houseList = namedParameterJdbcTemplate.query(sql, map, new HouseRowMapper());
        if (houseList.size() > 0) {
            return houseList.get(0);
        } else {
            return null;
        }
    }

    @Override
    //    查詢全部房子資訊．並加上選填的查詢條件
    public List<House> getAllHouses(HouseQueryParams houseQueryParams) {
        // 設定sql語法透過houseId查詢所有房屋資訊，加入where 1=1如果查詢條件不存在，使用原本sql語法，若存在則加上條件查詢的sql語法
        String sql = "SELECT user_id,house_name,house_id,house_type, address, image_url, " +
                "price_per_month, status, description ,created_date,last_update_date" +
                " FROM house WHERE 1=1";

        //創建Map物件，用來存放房屋
        Map<String, Object> map = new HashMap<>();
//        篩選/搜尋查詢條件
        sql = filterSql(sql, map, houseQueryParams);
//        排序
        sql += " ORDER BY " + houseQueryParams.getOrderBy() + " " + houseQueryParams.getOrderType();
//        分頁
        sql += " LIMIT :limit OFFSET :offset";
        map.put("limit", houseQueryParams.getLimit());
        map.put("offset", houseQueryParams.getOffset());

        //使用NamedParameterJdbcTemplate查詢一批房屋資訊放到List中
        List<House> houseList = namedParameterJdbcTemplate.query(sql, map, new HouseRowMapper());
        return houseList;

    }


    @Override
//    建立房屋
    public Integer createHouse(Integer userId, HouseRequest houseRequest) {
//      設定sql語法新增房屋資訊
        String sql = "INSERT INTO House (user_id,house_name, house_type, address, image_url, price_per_month, status, description) values(:userId,:houseName,:houseType,:address,:imageUrl,:pricePerMonth,:status,:description)";
//      創建Map物件，用來存放房屋資訊
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("houseName", houseRequest.getHouseName());
        map.put("houseType", houseRequest.getHouseType().toString());
        map.put("address", houseRequest.getAddress());
        map.put("imageUrl", houseRequest.getImageUrl());
        map.put("pricePerMonth", houseRequest.getPricePerMonth());
        map.put("status", houseRequest.getStatus().toString());
        map.put("description", houseRequest.getDescription());

//使用KeyHolder取得自動生成的houseId
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        int houseId = keyHolder.getKey().intValue();
        return houseId;
    }

    @Override
//    更新房屋
    public void updateHouse(Integer houseId, HouseRequest houseRequest) {
//       設定sql語法更新房屋資訊
        String sql = "UPDATE House SET house_name = :houseName, house_type = :houseType, address = :address, image_url = :imageUrl, price_per_month =:pricePerMonth,  status = :status, description = :description WHERE house_id = :houseId";
//       創建Map物件，用來存放房屋資訊
        Map<String, Object> map = new HashMap<>();
        map.put("houseId", houseId);
        map.put("houseName", houseRequest.getHouseName());
        map.put("houseType", houseRequest.getHouseType().toString());
        map.put("address", houseRequest.getAddress());
        map.put("imageUrl", houseRequest.getImageUrl());
        map.put("pricePerMonth", houseRequest.getPricePerMonth());
        map.put("status", houseRequest.getStatus().toString());
        map.put("description", houseRequest.getDescription());

        namedParameterJdbcTemplate.update(sql, map);
    }


    @Override
//    透過houseId刪除房屋
    public void deleteHouseById(Integer houseId) {
        // 設定sql語法刪除房屋
        String sql = "DELETE FROM house WHERE house_id = :houseId";
        // 創建Map物件，用來存放房屋Id
        Map<String, Object> map = new HashMap<>();
        map.put("houseId", houseId);
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public Integer countAllHouses(HouseQueryParams houseQueryParams) {
        String sql = "SELECT COUNT(*) FROM house WHERE 1=1";

//      創建Map物件，用來存放房屋
        Map<String, Object> map = new HashMap<>();
//      篩選/搜尋查詢條件
        sql = filterSql(sql, map, houseQueryParams);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }

    //        獨立sql查詢條件
    private String filterSql(String sql, Map<String, Object> map, HouseQueryParams houseQueryParams) {
//        篩選/搜尋查詢條件
        if (houseQueryParams.getHouseName() != null) {
            sql += " AND house_name LIKE :houseName";
            map.put("houseName", "%" + houseQueryParams.getHouseName() + "%");
        }
        if (houseQueryParams.getHouseType() != null) {
            sql += " AND house_type = :houseType";
            map.put("houseType", houseQueryParams.getHouseType().name());
        }
        if (houseQueryParams.getSearch() != null) {
            sql += " AND address LIKE :search";
            map.put("search", "%" + houseQueryParams.getSearch() + "%");
        }

        if (houseQueryParams.getStatus() != null) {
            sql += " AND status = :status";
            map.put("status", houseQueryParams.getStatus().name());
        }
        if (houseQueryParams.getUserId() != null) {
            sql += " AND user_id = :userId";
            map.put("userId", houseQueryParams.getUserId());
        }
        return sql;
    }

    @Override
    public void updateHouseStatus(Integer houseId, HouseStatus status) {
        String sql = "UPDATE house SET status = :status WHERE house_id = :houseId";
        Map<String, Object> map = new HashMap<>();
        map.put("houseId", houseId);
        map.put("status", status.toString());

        namedParameterJdbcTemplate.update(sql, map);
    }
}
