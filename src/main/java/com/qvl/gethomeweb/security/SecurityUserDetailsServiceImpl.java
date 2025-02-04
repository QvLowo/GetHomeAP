package com.qvl.gethomeweb.security;

import com.qvl.gethomeweb.dao.UserDao;
import com.qvl.gethomeweb.model.Role;
import com.qvl.gethomeweb.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
//    根據使用者帳號查詢使用者詳細數據
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        從資料庫中根據使用者電話號碼查詢使用者數據
        Member member = userDao.getUserByPhone(username);
//        檢查使用者是否存在，存在的話取得登入用的電話及密碼
        if (member == null) {
            throw new UsernameNotFoundException("使用者{}不存在" + username);
        }

//            String userPhone = member.getPhone();
//            String userPassword = member.getPassword();

//            權限相關設定
        List<Role> roleList = userDao.getRolesByUserId(member.getUserId());
        List<SimpleGrantedAuthority> authorities = convertToAuthorities(roleList);
        member.setRoles(roleList);
//            轉換成 Spring Security 指定的 User 格式
        return new MyUserDetails(member, authorities);

    }

    private List<SimpleGrantedAuthority> convertToAuthorities(List<Role> roleList) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roleList) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName().toString()));
        }

        return authorities;
    }
}
