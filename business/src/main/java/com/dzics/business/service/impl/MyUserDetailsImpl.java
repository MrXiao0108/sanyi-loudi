package com.dzics.business.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class MyUserDetailsImpl implements UserDetails {
    /**
     * 密码
     */
    String password;
    /**
     * /用户名
     */
    String username;
    /**
     * 是否没过期
     */
    boolean accountNonExpired;
    /**
     * 是否没被锁定
     */
    boolean accountNonLocked;
    /**
     * 是否没过期
     */
    boolean credentialsNonExpired;
    /**
     * 账号是否可用
     */
    boolean enabled;
    /**
     * 用户的权限集合
     */
    Collection<? extends GrantedAuthority> authorities;
    /**
     * 用户秘钥
     */
    String secret;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 刷新token秘钥
     */
    String refSecret;


    String code;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }


    public String getRefSecret() {
        return refSecret;
    }

    public void setRefSecret(String refSecret) {
        this.refSecret = refSecret;
    }

}
