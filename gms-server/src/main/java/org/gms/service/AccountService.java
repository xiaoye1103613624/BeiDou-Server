package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.DefaultDates;
import org.gms.config.GameConfig;
import org.gms.dao.entity.*;
import org.gms.dao.mapper.*;
import org.gms.model.dto.AddAccountDTO;
import org.gms.model.dto.UpdateAccountByGmDTO;
import org.gms.model.dto.UpdateAccountByUserDTO;
import org.gms.net.server.Server;
import org.gms.util.BCrypt;
import org.gms.util.HexTool;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;

import static org.gms.client.Client.LOGIN_LOGGEDIN;
import static org.gms.client.Client.LOGIN_NOTLOGGEDIN;
import static org.gms.dao.entity.table.CharactersDOTableDef.CHARACTERS_D_O;
import static org.gms.dao.entity.table.IpbansDOTableDef.IPBANS_D_O;

/**
 * 账号服务类
 * 提供账号的增删改查、密码验证、封禁解封等核心功能
 */
@Service
@AllArgsConstructor
public class AccountService {
    /**
     * 账号数据访问对象
     */
    private final AccountsMapper accountsMapper;

    /**
     * 角色数据访问对象
     */
    private final CharactersMapper charactersMapper;

    /**
     * IP封禁数据访问对象
     */
    private final IpbansMapper ipbansMapper;

    /**
     * MAC封禁数据访问对象
     */
    private final MacbansMapper macbansMapper;

    /**
     * 快捷栏按键映射数据访问对象
     */
    private final QuickslotkeymappedMapper quickslotkeymappedMapper;

    /**
     * 根据用户名查询账号
     *
     * @param name 用户名
     * @return 账号实体，如果不存在返回null
     */
    public AccountsDO findByName(String name) {
        return accountsMapper.selectOneByName(name);
    }

    /**
     * 根据账号ID查询账号
     *
     * @param id 账号ID
     * @return 账号实体，如果不存在返回null
     */
    public AccountsDO findById(int id) {
        return accountsMapper.selectOneById(id);
    }

    /**
     * 获取当前登录用户的账号信息
     * 通过SecurityContext获取当前认证用户
     *
     * @return 当前登录用户的账号实体
     */
    public AccountsDO getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return findByName(userDetails.getUsername());
    }

    /**
     * 分页查询账号列表
     * 支持按ID、名称、登录时间、创建时间进行筛选
     *
     * @param page           页码，默认1
     * @param size           每页大小，默认Integer.MAX_VALUE
     * @param id             账号ID筛选条件
     * @param name           用户名筛选条件（模糊匹配）
     * @param lastLoginStart 最后登录时间起始
     * @param lastLoginEnd   最后登录时间结束
     * @param createdAtStart 创建时间起始
     * @param createdAtEnd   创建时间结束
     * @return 分页账号列表
     */
    public Page<AccountsDO> getAccountList(Integer page,
                                           Integer size,
                                           Integer id,
                                           String name,
                                           String lastLoginStart,
                                           String lastLoginEnd,
                                           String createdAtStart,
                                           String createdAtEnd) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (id != null) queryWrapper.eq("id", id);
        if (name != null) queryWrapper.like("name", name);
        if (lastLoginStart != null) queryWrapper.ge(AccountsDO::getLastlogin, lastLoginStart);
        if (lastLoginEnd != null) queryWrapper.le(AccountsDO::getLastlogin, lastLoginEnd);
        if (createdAtStart != null) queryWrapper.ge(AccountsDO::getCreatedat, createdAtStart);
        if (createdAtEnd != null) queryWrapper.le(AccountsDO::getCreatedat, createdAtEnd);

        if (page == null) page = 1;
        if (size == null) size = Integer.MAX_VALUE;
        return accountsMapper.paginateWithRelations(page, size, queryWrapper);
    }

    /**
     * 更新账号信息
     *
     * @param condition 包含更新字段的账号实体
     */
    public void update(AccountsDO condition) {
        accountsMapper.update(condition);
    }

    /**
     * 创建新账号
     * 验证语言参数，检查用户名是否已存在，加密密码后插入数据库
     *
     * @param submitData 账号创建请求数据
     * @throws NoSuchAlgorithmException 加密算法不存在异常
     */
    public void addAccount(AddAccountDTO submitData) throws NoSuchAlgorithmException {
        RequireUtil.requireNotNull(submitData.getLanguage(), I18nUtil.getExceptionMessage("LANGUAGE_NOT_SUPPORT"));
        // 检查用户名是否已存在
        RequireUtil.requireNull(findByName(submitData.getName()), I18nUtil.getExceptionMessage("AccountService.addAccount.exception1"));

        AccountsDO account = AccountsDO.builder()
                .name(submitData.getName())
                .password(encryptPassword(submitData.getPassword()))
                .birthday(submitData.getBirthday())
                .tempban(Timestamp.valueOf(DefaultDates.getTempban()))
                .language(submitData.getLanguage())
                .lastlogin(Timestamp.valueOf(DefaultDates.getTempban()))
                .build();

        accountsMapper.insertSelective(account);
    }

    /**
     * 用户自行更新账号信息
     * 验证旧密码，支持更新密码、PIN、生日、昵称、邮箱、语言等字段
     *
     * @param submitData 用户更新请求数据
     * @throws NoSuchAlgorithmException 加密算法不存在异常
     */
    public void updateAccountByUser(UpdateAccountByUserDTO submitData) throws NoSuchAlgorithmException {
        AccountsDO account = getCurrentUser();
        // 验证旧密码
        RequireUtil.requireTrue(checkPassword(submitData.getOldPwd(), account), I18nUtil.getExceptionMessage("AccountService.updateAccountByUser.oldPassword"));
        RequireUtil.requireNotNull(submitData.getLanguage(), I18nUtil.getExceptionMessage("LANGUAGE_NOT_SUPPORT"));

        AccountsDO newData = new AccountsDO();
        newData.setId(account.getId());

        if (submitData.getNewPwd() != null && submitData.getNewPwd().length() >= 6) {
            newData.setPassword(encryptPassword(submitData.getNewPwd()));
        }
        newData.setPin(submitData.getPin());
        newData.setPic(submitData.getPic());
        newData.setBirthday(submitData.getBirthday());
        newData.setNick(submitData.getNick());
        newData.setEmail(submitData.getEmail());
        newData.setLanguage(submitData.getLanguage());

        accountsMapper.update(newData);
    }

    /**
     * GM更新账号信息
     * 支持更新账号的所有字段，包括游戏点数、角色槽位、权限等
     *
     * @param id         账号ID
     * @param submitData GM更新请求数据
     * @throws NoSuchAlgorithmException 加密算法不存在异常
     */
    public void updateAccountByGM(int id, UpdateAccountByGmDTO submitData) throws NoSuchAlgorithmException {
        AccountsDO account = findById(id);
        RequireUtil.requireNotNull(account, I18nUtil.getExceptionMessage("AccountService.id.NotExist"));
        RequireUtil.requireNotNull(account.getLanguage(), I18nUtil.getExceptionMessage("LANGUAGE_NOT_SUPPORT"));
        // 在线状态的账号不能修改
        RequireUtil.requireFalse(account.getLoggedin() == LOGIN_LOGGEDIN, I18nUtil.getExceptionMessage("AccountService.isOnline"));

        if (submitData.getNewPwd() != null && submitData.getNewPwd().length() >= 6) {
            account.setPassword(encryptPassword(submitData.getNewPwd()));
        }
        account.setPin(submitData.getPin());
        account.setPic(submitData.getPic());
        account.setBirthday(submitData.getBirthday());
        account.setNxCredit(submitData.getNxCredit());
        account.setMaplePoint(submitData.getMaplePoint());
        account.setNxPrepaid(submitData.getNxPrepaid());
        account.setCharacterslots(submitData.getCharacterslots());
        account.setGender(submitData.getGender());
        account.setWebadmin(submitData.getWebadmin());
        account.setNick(submitData.getNick());
        account.setMute(submitData.getMute());
        account.setEmail(submitData.getEmail());
        account.setRewardpoints(submitData.getRewardpoints());
        account.setVotepoints(submitData.getVotepoints());
        account.setLanguage(submitData.getLanguage());

        accountsMapper.update(account);
    }

    /**
     * GM删除账号
     *
     * @param id 账号ID
     */
    public void deleteAccountByGM(int id) {
        RequireUtil.requireNotNull(findById(id), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));
        accountsMapper.deleteById(id);
    }

    /**
     * 加密密码
     * 根据配置决定使用BCrypt或SHA-512算法加密密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     * @throws NoSuchAlgorithmException 加密算法不存在异常
     */
    public String encryptPassword(String password) throws NoSuchAlgorithmException {
        return GameConfig.getServerBoolean("bcrypt_migration") ? BCrypt.hashpw(password, BCrypt.gensalt(12)) : BCrypt.hashpwSHA512(password);
    }

    /**
     * 验证密码
     * 支持BCrypt、明文、SHA-1、SHA-512多种密码格式验证
     *
     * @param pwd        待验证的密码
     * @param accountsDO 账号实体
     * @return 如果密码匹配返回true，否则返回false
     */
    public boolean checkPassword(String pwd, AccountsDO accountsDO) {
        String passHash = accountsDO.getPassword();
        if (passHash.charAt(0) == '$' && passHash.charAt(1) == '2' && BCrypt.checkpw(pwd, passHash)) {
            return true;
        } else {
            return pwd.equals(passHash) || checkHash(passHash, "SHA-1", pwd) || checkHash(passHash, "SHA-512", pwd);
        }
    }

    /**
     * 验证哈希密码
     * 使用指定的消息摘要算法验证密码
     *
     * @param hash     存储的哈希值
     * @param type     算法类型（SHA-1或SHA-512）
     * @param password 待验证的密码
     * @return 如果匹配返回true，否则返回false
     */
    private static boolean checkHash(String hash, String type, String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance(type);
            digester.update(password.getBytes(StandardCharsets.UTF_8), 0, password.length());
            return HexTool.toHexString(digester.digest()).replace(" ", "").toLowerCase().equals(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Encoding the string failed", e);
        }
    }

    /**
     * 重置账号登录状态
     * 将指定账号的登录状态设为未登录
     *
     * @param id 账号ID
     */
    public void resetAllLoggedIn(int id) {
        RequireUtil.requireNotNull(findById(id), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));

        AccountsDO account = new AccountsDO();
        account.setId(id);
        account.setLoggedin(LOGIN_NOTLOGGEDIN);
        accountsMapper.update(account);
    }

    /**
     * 封禁账号
     * 封禁账号并处理在线角色：封禁MAC、封禁IP、强制离线
     *
     * @param accountId 账号ID
     * @param reason    封禁原因
     */
    public void banAccount(int accountId, String reason) {
        RequireUtil.requireNotNull(findById(accountId), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));

        AccountsDO account = new AccountsDO();
        account.setId(accountId);
        account.setBanned(true);
        account.setBanreason(reason);
        accountsMapper.update(account);

        List<CharactersDO> characterList = charactersMapper.selectIdAndWorldListByAccountId(accountId);
        for (CharactersDO chr : characterList) {
            Character player = Server.getInstance()
                    .getWorlds()
                    .get(chr.getWorld())
                    .getPlayerStorage()
                    .getCharacterById(chr.getId());
            // 角色不在线则跳过
            if (player == null) continue;

            player.setBanned(true);
            Client c = player.getClient();
            // 封禁该玩家的MAC地址
            c.banMacs();

            String ip = c.getRemoteAddress();
            IpbansDO ipban = IpbansDO.builder().ip(ip).aid(String.valueOf(accountId)).build();
            ipbansMapper.insertSelective(ipban);
            // 强制断开连接
            c.disconnect(false, false);
        }
    }

    /**
     * 解封账号
     * 解封账号并清除相关的MAC封禁和IP封禁记录
     *
     * @param accountId 账号ID
     */
    public void unbanAccount(int accountId) {
        RequireUtil.requireNotNull(findById(accountId), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));

        AccountsDO account = new AccountsDO();
        account.setId(accountId);
        account.setBanned(false);
        accountsMapper.update(account);

        macbansMapper.deleteByQuery(new QueryWrapper().eq(MacbansDO::getAid, accountId));

        ipbansMapper.deleteByQuery(new QueryWrapper().eq(IpbansDO::getAid, accountId));
    }

    /**
     * 重置所有账号的登录状态
     * 将所有账号的登录状态设为未登录
     */
    public void resetAllLoggedIn() {
        accountsMapper.updateAllLoggedIn(0);
    }

    /**
     * 封禁角色所属账号
     * 直接封禁角色所属的账号并更新角色的封禁状态
     *
     * @param chr    角色对象
     * @param reason 封禁原因
     */
    public void ban(Character chr, String reason) {
        accountsMapper.update(AccountsDO.builder().banned(true).id(chr.getAccountId()).banreason(reason).build());
        chr.setBanned(true);
    }

    /**
     * 根据字符串封禁账号或IP
     * 如果字符串匹配IP格式则封禁IP，否则封禁账号或角色所属账号
     *
     * @param str       待封禁的字符串（IP地址、用户名或角色名）
     * @param reason    封禁原因
     * @param isAccount 是否为账号（true为账号名，false为角色名）
     */
    public void ban(String str, String reason, boolean isAccount) {
        // 如果是IP格式，则封禁IP
        if (str.matches("[0-9]{1,3}\\..*")) {
            if (isBanned(str)) {
                return;
            }
            ipbansMapper.insertSelective(IpbansDO.builder().ip(str).build());
            return;
        }

        Integer accountId = null;
        if (isAccount) {
            // 按账号名查找
            AccountsDO accountsDO = findByName(str);
            if (accountsDO != null) {
                accountId = accountsDO.getId();
            }
        } else {
            // 按角色名查找所属账号
            List<CharactersDO> charactersDOS = charactersMapper.selectListByQuery(QueryWrapper.create().where(CHARACTERS_D_O.NAME.eq(str)));
            if (!charactersDOS.isEmpty()) {
                accountId = charactersDOS.getFirst().getAccountid();
            }
        }

        if (accountId == null) {
            throw new NoSuchElementException();
        }

        accountsMapper.update(AccountsDO.builder()
                .id(accountId)
                .banreason(reason)
                .banned(true)
                .build());
    }

    /**
     * 检查IP是否被封禁
     *
     * @param ip IP地址
     * @return 如果IP被封禁返回true，否则返回false
     */
    public boolean isBanned(String ip) {
        return ipbansMapper.selectCountByQuery(QueryWrapper.create().where(IPBANS_D_O.IP.eq(ip))) > 0;
    }

    /**
     * 获取快捷栏按键映射
     *
     * @param accountId 账号ID
     * @return 快捷栏按键映射实体
     */
    public QuickslotkeymappedDO getQuickSlotKeyMap(int accountId) {
        return quickslotkeymappedMapper.selectOneById(accountId);
    }
}