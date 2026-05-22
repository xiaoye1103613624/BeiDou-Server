package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.gms.dao.entity.AccountsDO;

/**
 * 【映射器】AccountsMapper（interface），包 {@code org.gms.dao.mapper}。
 *
 * 对应数据库表 accounts 的数据访问接口，提供账户信息相关查询操作。
 *
 * @author 萧曵
 */
public interface AccountsMapper extends BaseMapper<AccountsDO> {
    @Update("UPDATE accounts SET loggedin = #{value}")
    void updateAllLoggedIn(Integer value);
    
    @Select("SELECT * FROM accounts WHERE name = #{name}")
    AccountsDO selectOneByName(String name);
    
    @Insert("INSERT INTO accounts(name, password, birthday, tempban, language) VALUES (#{name}, #{password}, #{birthday}, #{tempban}, #{language})")
    void addAccount(AccountsDO accountsDO);
}
