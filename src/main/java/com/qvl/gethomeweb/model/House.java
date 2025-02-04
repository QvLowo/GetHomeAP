package com.qvl.gethomeweb.model;

import com.qvl.gethomeweb.constant.HouseStatus;
import com.qvl.gethomeweb.constant.HouseType;
import lombok.Data;

import java.util.Date;

@Data
public class House {
    private Integer houseId;
    private Integer userId;
    private String houseName;
    private HouseType houseType;
    private String address;
    private String imageUrl;
    private Integer pricePerMonth;
    private HouseStatus status;
    private String description;
    private Date createdDate;
    private Date lastUpdateDate;
}

