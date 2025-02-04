package com.qvl.gethomeweb.dto.user;

import com.qvl.gethomeweb.constant.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @NotBlank
    private String username;
    //    電話號碼不可空白不可空字串，長度固定為10，且必須為0-9的數字組成
    @NotBlank
//    @Pattern(regexp = "^(?=.*[0-9])[\\d]{10}$")
    private String phone;
    //    密碼不可空白不可空字串，長度固定為8-16，且必須包含大小寫字母及數字
    @NotBlank
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,16}$")
    private String password;
    @NotNull
    private Gender gender;
    @NotBlank
    @Email
    private String email;
}
