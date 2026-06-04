package org.gms.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.gms.dao.entity.AccountsDO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Objects;

/**
 * 【认证组件】UserDetailsImpl：Spring Security用户详情实现类。
 * 
 * <p>实现Spring Security的UserDetails接口，封装GM账号的认证信息，包括用户ID、用户名、密码和权限列表。</p>
 */
public class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;
    
    /** 用户ID */
    private final Integer id;
    /** 用户名 */
    private final String username;
    /** 密码（JSON序列化时忽略） */
    @JsonIgnore
    private final String password;
    /** 权限列表 */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * 构造函数。
     * 
     * @param id 用户ID
     * @param name 用户名
     * @param password 密码
     * @param authorities 权限列表
     */
    public UserDetailsImpl(Integer id, String name, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = name;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * 从AccountsDO构建UserDetailsImpl实例。
     * 
     * @param user 账号DO对象
     * @param authorities 权限列表
     * @return UserDetailsImpl实例
     */
    public static UserDetailsImpl build(AccountsDO user, Collection<? extends GrantedAuthority> authorities) {
        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Integer getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}