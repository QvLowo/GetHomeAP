package com.qvl.gethomeweb.service;

import com.qvl.gethomeweb.dto.user.UserRegisterRequest;
import com.qvl.gethomeweb.model.Member;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    Integer register(Integer roleId, UserRegisterRequest userRegisterRequest);

    Member getUserById(Integer userId);

    UserDetails loadUserByUsername(String phone);

}
