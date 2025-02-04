package com.qvl.gethomeweb.security;

import com.qvl.gethomeweb.model.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class MyUserDetails implements UserDetails {

    private Member member;

    private List<SimpleGrantedAuthority> simpleGrantedAuthorityList;

    public MyUserDetails(Member member, List<SimpleGrantedAuthority> simpleGrantedAuthorityList) {
        this.member = member;
        this.simpleGrantedAuthorityList = simpleGrantedAuthorityList;
    }



    //取得所有權限
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return simpleGrantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public List<String> getAuthList() {
        return simpleGrantedAuthorityList.stream().map(x -> x.getAuthority()).collect(Collectors.toList());
    }
}
