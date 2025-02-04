package com.qvl.gethomeweb.dto.payment;

import lombok.Data;

@Data
public class ResponseMessage {
    private String message;
    private String paymentUrl;
    public ResponseMessage(String paymentUrl,String message) {
        this.paymentUrl = paymentUrl;
        this.message = message;
    }
}
