package com.qvl.gethomeweb.dto.payment;

import lombok.Data;

@Data
public class RedirectUrls {
    private String confirmUrl;
    private String cancelUrl;
}
