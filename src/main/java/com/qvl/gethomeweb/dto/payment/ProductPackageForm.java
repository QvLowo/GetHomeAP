package com.qvl.gethomeweb.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductPackageForm {
    private String id;
    private String name;
    private BigDecimal amount;
    private List<ProductForm> products;
}
