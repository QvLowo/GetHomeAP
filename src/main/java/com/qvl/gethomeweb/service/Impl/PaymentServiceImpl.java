package com.qvl.gethomeweb.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.qvl.gethomeweb.dao.HouseDao;
import com.qvl.gethomeweb.dao.RentDao;
import com.qvl.gethomeweb.dto.payment.*;
import com.qvl.gethomeweb.model.RentInfo;
import com.qvl.gethomeweb.service.PaymentService;
import com.qvl.gethomeweb.util.CallLinePayApi;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

import static com.qvl.gethomeweb.constant.HouseStatus.RENTED;
import static com.qvl.gethomeweb.constant.HouseStatus.RESERVED;
import static com.qvl.gethomeweb.constant.RentStatus.COMPLETED;

@Component
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private CallLinePayApi callLinePayApi;
    @Autowired
    private RentDao rentDao;
    @Autowired
    private HouseDao houseDao;

    @Value("${linepay.channel-id}")
    private String channelId;
    @Value("${linepay.channel-secret}")
    private String channelSecret;
    @Value("${linepay.request-url}")
    private String requestUrl;
    @Value("${linepay.request-uri}")
    private String requestUri;
    @Value("${linepay.base-url}")
    private String baseUrl;

    private final static Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    public static String encrypt(final String keys, final String data) {
        return toBase64String(HmacUtils.getHmacSha256(keys.getBytes()).doFinal(data.getBytes()));
    }

    public static String toBase64String(byte[] bytes) {
        byte[] byteArray = Base64.encodeBase64(bytes);
        return new String(byteArray);
    }


    @Transactional
    @Override
    public LinePayRes sendRequestAPI(CheckoutPaymentRequestForm checkoutPaymentRequestForm) {
        String paymentId = checkoutPaymentRequestForm.getOrderId();
        RentInfo rentInfo = rentDao.getRentInfoByPaymentId(paymentId);

        CheckoutPaymentRequestForm form = getCheckoutPaymentRequestForm(rentInfo,paymentId);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String nonce = UUID.randomUUID().toString();
            String body = mapper.writeValueAsString(form);
            String signature = encrypt(channelSecret, channelSecret + requestUri + body + nonce);
            JsonNode response = callLinePayApi.sendPostRequest(channelId, nonce, signature, requestUrl, body);
            String apiCode = response.get("returnCode").asText();
            LinePayRes linePayRes = new LinePayRes();
            if (apiCode.equals("0000")) {
                String url = response.get("info").get("paymentUrl").get("web").asText();
                String transactionId = response.get("info").get("transactionId").asText();
                //            付款連結出現後，將房屋狀態改為預訂中
                houseDao.updateHouseStatus(rentInfo.getHouseId(), RESERVED);
                rentDao.setTransactionId(paymentId, transactionId);
                linePayRes.setUrl(url);
                linePayRes.setTransactionId(transactionId);
                return linePayRes;
            } else {
                log.warn("line pay付款失敗: " + apiCode + " " + response.get("returnMessage").asText());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "line pay api error");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CheckoutPaymentRequestForm getCheckoutPaymentRequestForm(RentInfo rentInfo,String paymentId){
        CheckoutPaymentRequestForm form = new CheckoutPaymentRequestForm();
        form.setAmount(BigDecimal.valueOf(rentInfo.getAmount()));
        form.setCurrency("TWD");
        form.setOrderId(paymentId);

        ProductPackageForm productPackageForm = new ProductPackageForm();
        productPackageForm.setId("package");
        productPackageForm.setName("get_home");
        productPackageForm.setAmount(BigDecimal.valueOf(rentInfo.getAmount()));

        ProductForm productForm = new ProductForm();
        productForm.setId(rentInfo.getHouseId().toString());
//        Line Pay付款畫面顯示名稱
        productForm.setName(rentInfo.getHouseName());
        productForm.setImageUrl(rentInfo.getImageUrl());
        productForm.setQuantity(BigDecimal.valueOf(rentInfo.getMonth()));
        productForm.setPrice(BigDecimal.valueOf(rentInfo.getAmount() / rentInfo.getMonth()));
        productPackageForm.setProducts(Lists.newArrayList(productForm));
        form.setPackages(Lists.newArrayList(productPackageForm));
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setConfirmUrl("http://localhost:8080/confirm-pay");
        redirectUrls.setCancelUrl("");
        form.setRedirectUrls(redirectUrls);
        return form;
    }

    @Transactional
    @Override
    public String sendConfirmAPI(Integer rentId, String transactionId, String paymentId) {
        RentInfo rentInfo = rentDao.getRentInfoByPaymentId(paymentId);
        ConfirmDataRequest confirmDataRequest = new ConfirmDataRequest();
        confirmDataRequest.setAmount(BigDecimal.valueOf(rentInfo.getAmount()));
        confirmDataRequest.setCurrency("TWD");
        ObjectMapper mapper = new ObjectMapper();

        String confirmUri = "/v3/payments/" + transactionId + "/confirm";
        String confirmUrl = baseUrl + confirmUri;
        try {
            String body = mapper.writeValueAsString(confirmDataRequest);
            String nonce = UUID.randomUUID().toString();
            String signature = encrypt(channelSecret, channelSecret + confirmUri + body + nonce);
            JsonNode response = callLinePayApi.sendPostRequest(channelId, nonce, signature, confirmUrl, body);
            String apiCode = response.get("returnCode").asText();
            System.out.println(response);
            int paidAmount = rentDao.getPaid(rentId);
            int totalAmount = rentDao.getRentById(rentId).getTotalAmount();
            int balance = totalAmount - paidAmount;
            if (apiCode.equals("0000") || apiCode.equals("1169")) {
                houseDao.updateHouseStatus(rentInfo.getHouseId(), RENTED);
                rentDao.updateRentStatus(rentId, COMPLETED);
                rentDao.updateAccountPayable(rentId, balance);
                return apiCode;
            } else if (apiCode.equals("1172")) {
                rentDao.deleteRentInfo(paymentId);
                log.warn("line pay 交易重複: " + apiCode + " " + response.get("returnMessage").asText());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "訂單交易序號重複，訂單已失效");
            } else {
                log.warn("line pay 確認訂單失敗: " + apiCode + " " + response.get("returnMessage").asText());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "line pay api error");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
