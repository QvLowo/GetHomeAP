package com.qvl.gethomeweb.rowmapper;

import com.qvl.gethomeweb.constant.Gender;
import com.qvl.gethomeweb.model.Member;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserRowMapper implements RowMapper<Member> {
    @Override
    public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
        Member member = new Member();
        member.setUserId(rs.getInt("user_id"));
        member.setUserName(rs.getString("username"));
        member.setPhone(rs.getString("phone"));
        member.setGender(Gender.valueOf(rs.getString("gender")));
        member.setPassword(rs.getString("password"));
        member.setEmail(rs.getString("email"));
        member.setCreatedDate(rs.getTimestamp("created_Date"));
        member.setLastUpdateDate(rs.getTimestamp("last_update_date"));
        return member;
    }
}
