package com.qvl.gethomeweb.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CallLinePayApi {
    //    使用RestTemplate發送HTTP POST請求
    public JsonNode sendPostRequest(String channelId, String nonce, String signature, String requestUrl, String mapperFormData) {
        RestTemplate restTemplate = new RestTemplate();
//      Post Header設定
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-LINE-ChannelId", channelId);
        headers.add("X-LINE-Authorization-Nonce", nonce);
        headers.add("X-LINE-Authorization", signature);
        HttpEntity<String> request = new HttpEntity<>(mapperFormData, headers);
        String responsebody = restTemplate.postForObject(requestUrl, request, String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = mapper.readTree(responsebody);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
