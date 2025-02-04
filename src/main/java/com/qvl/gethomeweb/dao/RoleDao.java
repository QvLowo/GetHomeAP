package com.qvl.gethomeweb.dao;

import com.qvl.gethomeweb.model.Role;

public interface RoleDao {

    //  取得角色名稱
    Role getRoleByName(String roleName);

    Role getRoleById(Integer roleId);
}
