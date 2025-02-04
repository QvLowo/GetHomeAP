package com.qvl.gethomeweb.dto.rent;

import lombok.Data;

@Data
public class CancelCheckRequest {
    private Integer rentId;
    private Integer houseId;
}
