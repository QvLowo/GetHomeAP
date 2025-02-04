package com.qvl.gethomeweb.controller;

import com.qvl.gethomeweb.dto.payment.CheckoutPaymentRequestForm;
import com.qvl.gethomeweb.dto.payment.ConfirmDataRequest;
import com.qvl.gethomeweb.dto.payment.ResponseMessage;
import com.qvl.gethomeweb.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenants/payments")
@Tag(name = "LinePay支付API")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "租客請求支付，取得付款連結及交易ID")
    @PostMapping("/request")
    public ResponseEntity<ResponseMessage> request(@RequestBody CheckoutPaymentRequestForm checkoutPaymentRequestForm) {
        String url = paymentService.sendRequestAPI(checkoutPaymentRequestForm).getUrl();
        String transactionId = paymentService.sendRequestAPI(checkoutPaymentRequestForm).getTransactionId();
//        如果response得到付款連結不為空，代表LINE PAY端驗證成功，回傳付款連結及交易ID
        if (url != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(url, transactionId));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        }
    }

    @Operation(summary = "租客支付完成後跳轉頁面進行確認，LinePay回傳付款結果，成功後台會入帳")
    @PostMapping("/confirm/{rentId}")
    public ResponseEntity<ResponseMessage> confirm(@PathVariable Integer rentId, @RequestBody ConfirmDataRequest confirmDataRequest) {
        String apiCode = paymentService.sendConfirmAPI(rentId, confirmDataRequest.getTransactionId(), confirmDataRequest.getPaymentId());
//      code 0000為成功，1169為LINE PAY端驗證問題，故皆視為成功
        if (apiCode.equals("0000") || apiCode.equals("1169"))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("", "確認付款成功"));
//     code 1172為訂單編號已存在，故視為失敗
        else if (apiCode.equals("1172")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("", "付款失敗，請重新下訂"));
        }
//        其他錯誤碼視為失敗
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
