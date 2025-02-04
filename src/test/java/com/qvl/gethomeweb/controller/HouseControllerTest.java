package com.qvl.gethomeweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qvl.gethomeweb.constant.HouseStatus;
import com.qvl.gethomeweb.constant.HouseType;
import com.qvl.gethomeweb.dto.HouseRequest;
import com.qvl.gethomeweb.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "landlord",password = "123",roles = {"LANDLORD"})
public class HouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
// 查詢房屋(成功)
    public void getHouse_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/houses/{houseId}", 1);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", equalTo(1)))
                .andExpect(jsonPath("$.houseName", equalTo("星光大廈")))
                .andExpect(jsonPath("$.houseType", equalTo("APARTMENT")))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.imageUrl", notNullValue()))
                .andExpect(jsonPath("$.pricePerMonth", equalTo(5000)))
                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastUpdateDate", notNullValue()));
    }
    @Test
//    查詢房屋(失敗)
    public void getHouse_notFound() throws Exception {
        RequestBuilder requestBuilder = get("/houses/{houseId}", 10000);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(404));
    }

    @Transactional
    @Test
    // 新增房屋(成功)
    public void createHouse_success() throws Exception {
        HouseRequest houseRequest = new HouseRequest();
        houseRequest.setUserId(1);
        houseRequest.setHouseType(HouseType.BEDSIT);
        houseRequest.setHouseName("503教室");
        houseRequest.setAddress("台北市中山區民生東路三段67號");
        houseRequest.setImageUrl("http://test.com");
        houseRequest.setPricePerMonth(5000);
        houseRequest.setStatus(HouseStatus.AVAILABLE);

        String json = objectMapper.writeValueAsString(houseRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/landlords/house")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.userId", equalTo(1)))
                .andExpect(jsonPath("$.houseName", equalTo("503教室")))
                .andExpect(jsonPath("$.houseType", equalTo("BEDSIT")))
                .andExpect(jsonPath("$.address", equalTo("台北市中山區民生東路三段67號")))
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
                .andExpect(jsonPath("$.pricePerMonth", equalTo(5000)))
                .andExpect(jsonPath("$.status", equalTo("AVAILABLE")))
                .andExpect(jsonPath("$.description", nullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastUpdateDate", notNullValue()));
    }

    @Transactional
    @Test
//    新增房屋(錯誤參數)
    public void createHouse_illegalArgument() throws Exception {
        HouseRequest houseRequest = new HouseRequest();
        houseRequest.setHouseName("503教室");

        String json = objectMapper.writeValueAsString(houseRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/landlords/house")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    // 更新房屋(成功)
    @Transactional
    @Test
    public void updateHouse_success() throws Exception {
        HouseRequest houseRequest = new HouseRequest();
        houseRequest.setUserId(1);
        houseRequest.setHouseType(HouseType.BEDSIT);
        houseRequest.setHouseName("503教室");
        houseRequest.setAddress("台北市中山區民生東路三段67號");
        houseRequest.setImageUrl("http://test.com");
        houseRequest.setPricePerMonth(10000);
        houseRequest.setStatus(HouseStatus.AVAILABLE);

        String json = objectMapper.writeValueAsString(houseRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/landlords/house/{houseId}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.userId", equalTo(1)))
                .andExpect(jsonPath("$.houseName", equalTo("503教室")))
                .andExpect(jsonPath("$.houseType", equalTo("BEDSIT")))
                .andExpect(jsonPath("$.address", equalTo("台北市中山區民生東路三段67號")))
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
                .andExpect(jsonPath("$.pricePerMonth", equalTo(10000)))
                .andExpect(jsonPath("$.status", equalTo("AVAILABLE")))
                .andExpect(jsonPath("$.description", nullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastUpdateDate", notNullValue()));
    }

    @Transactional
    @Test
//    更新房屋(錯誤參數)
    public void updateHouse_illegalArgument() throws Exception {
        HouseRequest houseRequest = new HouseRequest();
        houseRequest.setHouseName("503教室");

        String json = objectMapper.writeValueAsString(houseRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/landlords/house/{houseId}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));

    }

    @Transactional
    @Test
//    更新房屋(房屋不存在)
    public void updateHouse_houseNotFound() throws Exception {
        HouseRequest houseRequest = new HouseRequest();
        houseRequest.setUserId(1);
        houseRequest.setHouseType(HouseType.BEDSIT);
        houseRequest.setHouseName("503教室");
        houseRequest.setAddress("台北市中山區民生東路三段67號");
        houseRequest.setImageUrl("http://test.com");
        houseRequest.setPricePerMonth(10000);
        houseRequest.setStatus(HouseStatus.AVAILABLE);

        String json = objectMapper.writeValueAsString(houseRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/landlords/house/{houseId}", 100)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(404));
    }

    @Transactional
    @Test
    // 刪除房屋(成功)
    public void deleteHouse_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/landlords/house/{houseId}", 3);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(204));
    }

    @Transactional
    @Test
//    刪除房屋(房屋不存在)
    public void deleteHouse_deleteNonExistingHouse() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/landlords/house/{houseId}", 100);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(204));
    }

    @Test
//    查詢所有房屋
    public void getHouses() throws Exception {
        RequestBuilder requestBuilder = get("/houses");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(5)));
    }

    @Test
//    查詢房屋條件
    public void getHouses_filtering() throws Exception {
        RequestBuilder requestBuilder = get("/houses")
                .param("userId", "1")
                .param("houseType", "APARTMENT")
                .param("search", "新北市")
                .param("status", "AVAILABLE");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(2)));
    }

    @Test
    public void getHouses_sorting() throws Exception {
        RequestBuilder requestBuilder = get("/houses")
                .param("orderBy", "price_per_month")
                .param("orderType", "desc");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(5)))
                .andExpect(jsonPath("$.results[0].houseId", equalTo(4)))
                .andExpect(jsonPath("$.results[1].houseId", equalTo(3)))
                .andExpect(jsonPath("$.results[2].houseId", equalTo(2)))
                .andExpect(jsonPath("$.results[3].houseId", equalTo(1)))
                .andExpect(jsonPath("$.results[4].houseId", equalTo(5)));
    }

    @Transactional
    @Test
    public void getHouses_pagination() throws Exception {
        RequestBuilder requestBuilder = get("/houses")
                .param("limit", "3")
                .param("offset", "2");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(3)))
                .andExpect(jsonPath("$.results[0].houseId", equalTo(3)))
                .andExpect(jsonPath("$.results[1].houseId", equalTo(2)))
                .andExpect(jsonPath("$.results[2].houseId", equalTo(1)));
    }
}
