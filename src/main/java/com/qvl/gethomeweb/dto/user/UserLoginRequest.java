package com.qvl.gethomeweb.dto.user;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String phone;
    private String password;
    private String code;
    private String uuid = "";
}
