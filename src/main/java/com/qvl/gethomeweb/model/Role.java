package com.qvl.gethomeweb.model;

import com.qvl.gethomeweb.constant.UserRole;
import lombok.Data;

@Data
public class Role {
    private Integer roleId;
    //    設定使用者權限角色名稱(如：房東、房客、管理員等)
    private UserRole roleName;

}
