package com.qvl.gethomeweb.service;

import com.qvl.gethomeweb.dto.house.HouseQueryParams;
import com.qvl.gethomeweb.dto.house.HouseRequest;
import com.qvl.gethomeweb.model.House;

import java.util.List;

public interface HouseService {
    //透過ID查詢一筆房屋資訊
    House getHouseById(Integer houseId);

    //    查詢全部房子資訊．並加上選填的查詢條件
    List<House> getAllHouses(HouseQueryParams houseQueryParams);

    //新增房屋資訊
    Integer createHouse(Integer userId, HouseRequest houseRequest);

    //更新房屋資訊
    void updateHouse(Integer houseId, HouseRequest houseRequest);

    //刪除房屋資訊
    void deleteHouseById(Integer houseId);

    Integer countAllHouses(HouseQueryParams houseQueryParams);
}
