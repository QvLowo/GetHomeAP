package com.qvl.gethomeweb.dto.payment;

import lombok.Data;

@Data
public class LinePayRes {
    private String url;
    private String transactionId;
}
