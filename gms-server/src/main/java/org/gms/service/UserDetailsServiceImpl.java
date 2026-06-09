package org.gms.service;

import org.gms.dao.entity.AccountsDO;
import org.gms.dao.mapper.AccountsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户详情服务实现类
 * 从数据库加载用户信息并验证是否为Web管理员
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    /** 账号数据访问对象 */
    private final AccountsMapper userDao;

    /**
     * 构造函数，注入账号数据访问对象
     *
     * @param userRepository 账号数据访问对象
     */
    @Autowired
    public UserDetailsServiceImpl(AccountsMapper userRepository) {
        this.userDao = userRepository;
    }

    /**
     * 根据用户名加载用户详情
     * 仅允许webadmin=1的用户登录后台管理系统
     *
     * @param username 用户名
     * @return 用户详情，非管理员返回null
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountsDO user = userDao.selectOneByName(username);
        if (user == null) {return null;}

        if (user.getWebadmin() != null && user.getWebadmin() == 1) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return UserDetailsImpl.build(user, authorities);
        }
        return null;
    }

}