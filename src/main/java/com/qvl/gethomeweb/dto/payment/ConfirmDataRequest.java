package com.qvl.gethomeweb.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ConfirmDataRequest {
    private String transactionId;
    private String paymentId;
    private BigDecimal amount;
    private String currency;
}
