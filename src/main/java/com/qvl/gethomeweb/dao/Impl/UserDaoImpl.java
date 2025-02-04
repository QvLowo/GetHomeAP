package com.qvl.gethomeweb.dao.Impl;

import com.qvl.gethomeweb.dao.UserDao;
import com.qvl.gethomeweb.dto.user.UserRegisterRequest;
import com.qvl.gethomeweb.model.Member;
import com.qvl.gethomeweb.model.Role;
import com.qvl.gethomeweb.rowmapper.RoleRowMapper;
import com.qvl.gethomeweb.rowmapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {
        String sql = "INSERT INTO user (phone,username,gender, password,email) VALUES (:phone,:username,:gender,:password,:email)";
        Map<String, Object> map = new HashMap<>();
        map.put("username", userRegisterRequest.getUsername());
        map.put("phone", userRegisterRequest.getPhone());
        map.put("gender", userRegisterRequest.getGender().toString());
        map.put("password", userRegisterRequest.getPassword());
        map.put("email", userRegisterRequest.getEmail());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        int userId = keyHolder.getKey().intValue();
        return userId;
    }

    @Override
    public Member getUserById(Integer userId) {
        String sql = "SELECT user_id,phone,username,password,email,gender,created_date,last_update_date FROM user WHERE user_id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<Member> memberList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if (memberList.size() > 0) {
            return memberList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Member getUserByPhone(String phone) {
        String sql = "SELECT user_id,phone,username,password,email,gender,created_date,last_update_date FROM user WHERE phone = :phone";
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);

        List<Member> memberList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if (memberList.size() > 0) {
            return memberList.get(0);
        } else {
            return null;
        }
    }

    //    建立user與role關聯，透過userId查看該用戶屬於哪些角色
    @Override
    public List<Role> getRolesByUserId(Integer userId) {
        String sql = """
                SELECT role.role_id ,role.role_name FROM role 
                JOIN user_role ON role.role_id = user_role.role_id 
                WHERE user_role.user_id = :userId
                """;
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        List<Role> roleList = namedParameterJdbcTemplate.query(sql, map, new RoleRowMapper());
        return roleList;
    }

    //    根據userId新增角色
    @Override
    public void addRoleForUserId(Integer userId, Role role) {
        String sql = "INSERT INTO user_role (user_id, role_id) VALUES (:userId, :roleId)";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("roleId", role.getRoleId());

        namedParameterJdbcTemplate.update(sql, map);

    }

    //    根據userId刪除角色
    @Override
    public void deleteRoleFromUserId(Integer userId, Role role) {
        String sql = "DELETE FROM user_role WHERE user_id = :userId AND role_id = :roleId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("roleId", role.getRoleId());
        namedParameterJdbcTemplate.update(sql, map);
    }
}
