package com.pointers.ices4hu.security.jwt;

import com.pointers.ices4hu.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class JwtUserDetails implements UserDetails {

    private Long id;
    private String loginID;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    private JwtUserDetails(Long id,
                           String loginID,
                           String password,
                           Collection<? extends GrantedAuthority> authorities)
    {
        this.id = id;
        this.loginID = loginID;
        this.password = password;
        this.authorities = authorities;
    }

    public static JwtUserDetails getUserDetailsInstanceFor(User user) {
        if (user == null) {
            return null;
        }

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("user"));

        Byte userType = user.getUserType();
        switch (userType) {
            case 0:
                authorityList.add(new SimpleGrantedAuthority("admin"));
                break;
            case 1:
                authorityList.add(new SimpleGrantedAuthority("student"));
                break;
            case 2:
                authorityList.add(new SimpleGrantedAuthority("department_manager"));
                break;
            case 3:
                authorityList.add(new SimpleGrantedAuthority("instructor"));
        }


        return new JwtUserDetails(user.getId(),
                user.getLoginID(),
                user.getPassword(),
                authorityList);
    }

    @Override
    public java.lang.String getUsername() {
        return loginID;
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
}
