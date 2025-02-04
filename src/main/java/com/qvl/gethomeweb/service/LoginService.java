package com.qvl.gethomeweb.service;

import com.qvl.gethomeweb.dto.user.UserLoginRequest;
import com.qvl.gethomeweb.util.Result;

public interface LoginService {
    Result login(UserLoginRequest userLoginRequest);
}
