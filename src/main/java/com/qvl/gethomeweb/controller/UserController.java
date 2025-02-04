package com.qvl.gethomeweb.controller;

import com.qvl.gethomeweb.dto.user.UserRegisterRequest;
import com.qvl.gethomeweb.model.Member;
import com.qvl.gethomeweb.security.MyUserDetails;
import com.qvl.gethomeweb.service.UserService;
import com.qvl.gethomeweb.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "使用者API")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "註冊，分成房東/租客註冊" )
    //        根據角色id註冊，分成房東及租客
    @PostMapping("/public/sign-up/{roleId}")
    public ResponseEntity<Member> register(@Parameter(description = "1為房東，2為租客",required = true)@PathVariable Integer roleId, @RequestBody @Valid UserRegisterRequest userRegisterRequest) {
//        密碼轉成hash儲存
        String hashedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
        userRegisterRequest.setPassword(hashedPassword);

        Integer userId = userService.register(roleId, userRegisterRequest);
        Member member = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PostMapping("/public/users")
    @Operation(summary = "查詢使用者資料")
    public ResponseEntity<Member> getUserById(@RequestBody Integer userId) {
        Member member = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(member);
    }

    @Operation(summary = "透過token取得使用者個人資料")
    @PostMapping("/public/users/userInfo")
    public Result getUserInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails =(MyUserDetails) authentication.getPrincipal();
        Member member= myUserDetails.getMember();
        return Result.success(member);
    }

    @Operation(summary = "透過token取得使用者權限")
    @PostMapping("/public/users/Auth")
    public Result getAuth(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails =(MyUserDetails) authentication.getPrincipal();
        return Result.success(myUserDetails.getAuthList());
    }
}
