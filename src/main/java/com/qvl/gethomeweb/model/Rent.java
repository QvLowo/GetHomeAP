package com.qvl.gethomeweb.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Rent {
    private Integer rentId;
    private Integer userId;
    private String status;
    private Integer totalAmount;
    private Date startDate;
    private Date endDate;
    private Integer accountPayable;
    private Date createDate;
    private Date lastUpdateDate;

    private List<RentInfo> rentInfoList;
}
