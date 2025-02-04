package com.qvl.gethomeweb.service;

import com.qvl.gethomeweb.dto.payment.CheckoutPaymentRequestForm;
import com.qvl.gethomeweb.dto.payment.LinePayRes;

public interface PaymentService {
    LinePayRes sendRequestAPI(CheckoutPaymentRequestForm checkoutPaymentRequestForm);

    String sendConfirmAPI(Integer rentId, String transactionId, String paymentId);
}
