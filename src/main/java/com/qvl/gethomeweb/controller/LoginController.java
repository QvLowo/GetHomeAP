package com.qvl.gethomeweb.controller;

import com.qvl.gethomeweb.dto.user.UserLoginRequest;
import com.qvl.gethomeweb.service.LoginService;
import com.qvl.gethomeweb.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="使用者傳統登入API")
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Operation(summary = "登入")

    @PostMapping("/public/login")
    public Result login(@RequestBody UserLoginRequest userLoginRequest) {
        return loginService.login(userLoginRequest);
    }
}
