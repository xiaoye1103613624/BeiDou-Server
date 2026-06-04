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
 * 【认证服务】UserDetailsServiceImpl：Spring Security用户详情服务实现类。
 * 
 * <p>实现UserDetailsService接口，用于从数据库加载GM账号信息进行认证。
 * 只有webadmin=1的账号才能登录GM后台。</p>
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    /** 账号数据访问接口 */
    private final AccountsMapper userDao;

    @Autowired
    public UserDetailsServiceImpl(AccountsMapper userRepository) {
        this.userDao = userRepository;
    }

    /**
     * 根据用户名加载用户详情。
     * 
     * <p>仅当账号的webadmin字段为1时，才赋予ROLE_ADMIN权限并返回用户详情。</p>
     * 
     * @param username 用户名
     * @return UserDetails实例（仅GM账号），非GM账号返回null
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountsDO user = userDao.selectOneByName(username);
        if (user == null) {
            return null;
        }

        // 仅webadmin=1的账号可以登录GM后台
        if (user.getWebadmin() != null && user.getWebadmin() == 1) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return UserDetailsImpl.build(user, authorities);
        }
        return null;
    }

}