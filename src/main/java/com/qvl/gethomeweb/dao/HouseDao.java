package com.qvl.gethomeweb.dao;

import com.qvl.gethomeweb.constant.HouseStatus;
import com.qvl.gethomeweb.dto.house.HouseQueryParams;
import com.qvl.gethomeweb.dto.house.HouseRequest;
import com.qvl.gethomeweb.model.House;

import java.util.List;

public interface HouseDao {

    House getHouseById(Integer houseId);

    List<House> getAllHouses(HouseQueryParams houseQueryParams);

    Integer createHouse(Integer userId, HouseRequest houseRequest);

    void updateHouse(Integer houseId, HouseRequest houseRequest);

    void deleteHouseById(Integer houseId);

    Integer countAllHouses(HouseQueryParams houseQueryParams);

    void updateHouseStatus(Integer houseId, HouseStatus status);

}
