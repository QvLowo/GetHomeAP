package com.qvl.gethomeweb.controller;

import com.qvl.gethomeweb.dto.rent.CancelCheckRequest;
import com.qvl.gethomeweb.dto.rent.CreateRentRequest;
import com.qvl.gethomeweb.dto.rent.RentItem;
import com.qvl.gethomeweb.dto.rent.RentQueryParams;
import com.qvl.gethomeweb.model.Rent;
import com.qvl.gethomeweb.model.RentInfo;
import com.qvl.gethomeweb.service.RentService;
import com.qvl.gethomeweb.util.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Tag(name = "租屋相關")
public class RentController {
    @Autowired
    private RentService rentService;

    @Operation(summary = "租客新增租屋訂單")
//    新增租屋訂單，新增訂單方法限制房客權限
    @PostMapping("/tenants/{userId}/rent")
    public ResponseEntity<Rent> createRent(@PathVariable Integer userId, @RequestBody @Valid CreateRentRequest createRentRequest) {
        Integer rentId = rentService.createRent(userId, createRentRequest);
        Rent rent = rentService.getRentById(rentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(rent);
    }

    @Operation(summary = "租客新增每月租金訂單")
    @PostMapping("/tenants/rent/{rentId}")
    public ResponseEntity<RentInfo> payRent(@PathVariable Integer rentId, @RequestBody @Valid RentItem rentItem) {
        Integer rentInfoId = rentService.payRent(rentId, rentItem);
        RentInfo rentInfo = rentService.getRentInfoById(rentInfoId);
        if (rentInfo!= null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(rentInfo);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "取得所有的租屋訂單")
    @GetMapping("/api/rents")
    public ResponseEntity<Page<Rent>> getRentOrder(@RequestParam(required = false) Integer userId,
                                                   @RequestParam(required = false) Integer rentId,
                                                   @RequestParam(defaultValue = "created_date") String orderBy,
                                                   @RequestParam(defaultValue = "desc") String orderType,
                                                   @RequestParam(defaultValue = "5") @Max(100) @Min(0) Integer limit,
                                                   @RequestParam(defaultValue = "0") @Min(0) Integer offset) {
        RentQueryParams rentQueryParams = new RentQueryParams();
        rentQueryParams.setUserId(userId);
        rentQueryParams.setRentId(rentId);
        rentQueryParams.setOrderBy(orderBy);
        rentQueryParams.setOrderType(orderType);
        rentQueryParams.setLimit(limit);
        rentQueryParams.setOffset(offset);

        List<Rent> rentList = rentService.getRentOrders(rentQueryParams);

        Integer total = rentService.countRentOrder(rentQueryParams);

        Page<Rent> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(total);
        page.setResults(rentList);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @Operation(summary = "取得所有的租屋明細")
    @GetMapping("/api/rent/info")
    public ResponseEntity<Page<RentInfo>> getRentInfo(
            @RequestParam(required = false) Integer houseId,
            @RequestParam(defaultValue = "created_date") String orderBy,
            @RequestParam(defaultValue = "desc") String orderType,
            @RequestParam(defaultValue = "5") @Max(100) @Min(0) Integer limit,
//           跳過筆數最小值預設為0，避免前端回傳負數
            @RequestParam(defaultValue = "0") @Min(0) Integer offset) {
        RentQueryParams rentQueryParams = new RentQueryParams();
        rentQueryParams.setHouseId(houseId);
        rentQueryParams.setOrderBy(orderBy);
        rentQueryParams.setOrderType(orderType);
        rentQueryParams.setLimit(limit);
        rentQueryParams.setOffset(offset);

        List<RentInfo> rentList = rentService.getRentInfo(rentQueryParams);

        Integer total = rentService.countRentInfo(rentQueryParams);

        Page<RentInfo> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(total);
        page.setResults(rentList);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @Operation(summary = "透過rentId取得租屋訂單資料")
    @GetMapping("/api/rent/{rentId}")
    public ResponseEntity<Rent> getRentById(@PathVariable Integer rentId) {
        Rent rent = rentService.getRentById(rentId);
        if (rent != null) {
            return ResponseEntity.status(HttpStatus.OK).body(rent);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "透過rentInfoId取得租屋明細資料")
    @GetMapping("/api/rent/info/{rentInfoId}")
    public ResponseEntity<RentInfo> getRentInfoById(@PathVariable Integer rentInfoId) {
        RentInfo rentInfo = rentService.getRentInfoById(rentInfoId);
        if (rentInfo != null) {
            return ResponseEntity.status(HttpStatus.OK).body(rentInfo);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "租客退租，更新租屋訂單狀態退租待確認")
    @PutMapping("/tenants/rent/{rentId}")
    public ResponseEntity<Rent> endLease(@PathVariable @Valid Integer rentId) {
        Rent rent = rentService.getRentById(rentId);
        if (rent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        rentService.cancelRentById(rentId);
        Rent updateRent = rentService.getRentById(rentId);
        return ResponseEntity.status(HttpStatus.OK).body(updateRent);
    }

    @Operation(summary = "房東審核退租，更新租屋訂單狀態為已退租，房屋狀態更新為待出租")
    @PutMapping("/landlords/rent")
    public ResponseEntity<Rent> checkEndLease(@RequestBody CancelCheckRequest cancelCheckRequest) {
        Rent rent = rentService.getRentById(cancelCheckRequest.getRentId());

        if (rent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        rentService.cancelCheckById(cancelCheckRequest.getRentId(),cancelCheckRequest.getHouseId());
        Rent updateRent = rentService.getRentById(cancelCheckRequest.getRentId());
        return ResponseEntity.status(HttpStatus.OK).body(updateRent);
    }
}
