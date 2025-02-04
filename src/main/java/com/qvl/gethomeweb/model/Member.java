package com.qvl.gethomeweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qvl.gethomeweb.constant.Gender;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Member {
    private Integer userId;
    private String userName;
    private String phone;
    private Gender gender;
    //    隱藏密碼不傳給前端
    @JsonIgnore
    private String password;
    private String email;
    @JsonIgnore
    private Date createdDate;
    @JsonIgnore
    private Date lastUpdateDate;
    private List<Role> roles;
}
