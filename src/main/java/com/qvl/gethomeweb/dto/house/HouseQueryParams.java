package com.qvl.gethomeweb.dto.house;

import com.qvl.gethomeweb.constant.HouseStatus;
import com.qvl.gethomeweb.constant.HouseType;
import lombok.Data;

@Data
public class HouseQueryParams {
    private Integer userId;
    private String houseName;
    private HouseType houseType;
    private String search;
    private HouseStatus status;
    private String orderBy;
    private String orderType;
    private Integer limit;
    private Integer offset;
}
