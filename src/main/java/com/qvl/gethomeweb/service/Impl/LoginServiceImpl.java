package com.qvl.gethomeweb.service.Impl;

import com.qvl.gethomeweb.dto.user.UserLoginRequest;
import com.qvl.gethomeweb.redis.RedisCache;
import com.qvl.gethomeweb.service.LoginService;
import com.qvl.gethomeweb.util.JwtUtil;
import com.qvl.gethomeweb.util.Result;
import com.qvl.gethomeweb.util.captcha.CaptchaException;
import com.qvl.gethomeweb.util.captcha.CaptchaExpireException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Result login(UserLoginRequest userLoginRequest) {
        checkCaptcha(userLoginRequest.getPhone(), userLoginRequest.getCode(), userLoginRequest.getUuid());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userLoginRequest.getPhone(), userLoginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);
            if (authentication == null) {
                throw new RuntimeException("登入失敗");
            }
        Map<String, String> map = new HashMap<>();
        map.put("phone", userLoginRequest.getPhone());
        String token = JwtUtil.getToken(map, "gethomeinasecond");
        return Result.success(token);
    }

    public void checkCaptcha(String phone, String code, String uuid)
    {
        String verifyKey = "captcha_codes:" + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}
