package com.qvl.gethomeweb.dao;

import com.qvl.gethomeweb.constant.RentStatus;
import com.qvl.gethomeweb.dto.rent.CreateRentRequest;
import com.qvl.gethomeweb.dto.rent.RentQueryParams;
import com.qvl.gethomeweb.model.Rent;
import com.qvl.gethomeweb.model.RentInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RentDao {

    Integer createRent(Integer userId, Integer totalAmount, CreateRentRequest createRentRequest);
    Integer createRentInfo(Integer rentId, List<RentInfo> rentInfoList);

    Integer getPaid(Integer rentId);
    Rent getRentById(Integer rentId);

    List<RentInfo> getRentInfoByRentId(Integer rentId);
    RentInfo getRentInfoByPaymentId(String paymentId);

    RentInfo getRentInfoById(Integer rentInfoId);

    Integer countRentOrder(RentQueryParams rentQueryParams);

    List<Rent> getRentOrders(RentQueryParams rentQueryParams);

    void updateRentStatus(Integer rentId, RentStatus status);
    void updateAccountPayable(Integer rentId, Integer accountPayable);
    void setTransactionId(String paymentId, String transactionId);

    void deleteRentInfo(String paymentId);

    List<RentInfo> getRentInfo(RentQueryParams rentQueryParams);

    Integer countRentInfo(RentQueryParams rentQueryParams);
}
