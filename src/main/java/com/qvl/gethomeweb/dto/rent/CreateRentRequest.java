package com.qvl.gethomeweb.dto.rent;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateRentRequest {
    @NotEmpty
    private List<RentItem> rentItemList;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;


}
