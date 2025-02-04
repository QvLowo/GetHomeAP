package com.qvl.gethomeweb.dao;

import com.qvl.gethomeweb.dto.user.UserRegisterRequest;
import com.qvl.gethomeweb.model.Member;
import com.qvl.gethomeweb.model.Role;

import java.util.List;

public interface UserDao {
    Integer createUser(UserRegisterRequest userRegisterRequest);

    Member getUserById(Integer userId);

    Member getUserByPhone(String phone);

    //    權限相關設定
    List<Role> getRolesByUserId(Integer userId);

    void addRoleForUserId(Integer userId, Role role);

    void deleteRoleFromUserId(Integer userId, Role role);
}


