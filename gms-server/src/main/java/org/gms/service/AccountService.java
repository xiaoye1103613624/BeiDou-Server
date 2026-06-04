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
 * 【业务服务】AccountService：封装账号管理（登录、注册、封禁、解封、密码校验）相关的业务逻辑。
 *
 * <p>本服务作为账号系统的核心入口，提供账号的 CRUD、登录态管理、封禁/解封（含 IP/Mac 联动处理）
 * 以及密码加密/校验等功能。通过 {@link AccountsMapper} 直接操作 {@code accounts} 表，
 * 并通过 {@link IpbansMapper}、{@link MacbansMapper} 实现多维度封禁。</p>
 */
@Service
@AllArgsConstructor
public class AccountService {
    /** 账号数据访问接口 */
    private final AccountsMapper accountsMapper;
    /** 角色数据访问接口 */
    private final CharactersMapper charactersMapper;
    /** IP封禁数据访问接口 */
    private final IpbansMapper ipbansMapper;
    /** Mac封禁数据访问接口 */
    private final MacbansMapper macbansMapper;
    /** 快捷栏按键映射数据访问接口 */
    private final QuickslotkeymappedMapper quickslotkeymappedMapper;

    /**
     * 根据账号名精确查询账号。
     *
     * @param name 账号名
     * @return 账号实体，不存在则返回 null
     */
    public AccountsDO findByName(String name) {
        return accountsMapper.selectOneByName(name);
    }

    /**
     * 根据主键 ID 查询账号。
     *
     * @param id 账号 ID
     * @return 账号实体，不存在则返回 null
     */
    public AccountsDO findById(int id) {
        return accountsMapper.selectOneById(id);
    }

    /**
     * 获取当前登录用户的账号信息（从 Spring Security 上下文中提取用户名）。
     *
     * @return 当前登录用户的账号实体
     */
    public AccountsDO getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return findByName(userDetails.getUsername());
    }

    /**
     * 分页查询账号列表，支持多条件筛选（ID、名称、最后登录时间、创建时间）。
     *
     * @param page           页码，null 时默认为 1
     * @param size           每页条数，null 时默认返回全部
     * @param id             账号 ID 精确匹配，可选
     * @param name           账号名模糊匹配，可选
     * @param lastLoginStart 最后登录时间起始，可选
     * @param lastLoginEnd   最后登录时间截止，可选
     * @param createdAtStart 创建时间起始，可选
     * @param createdAtEnd   创建时间截止，可选
     * @return 分页后的账号列表
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
     * 按条件更新账号信息（通常由外部组装好 {@link AccountsDO} 后调用）。
     *
     * @param condition 包含待更新字段的账号实体（id 必填）
     */
    public void update(AccountsDO condition) {
        accountsMapper.update(condition);
    }

    /**
     * 注册新账号。
     *
     * <p>校验语言字段非空、账号名不重复后，使用加密密码写入数据库。
     * 密码加密方式根据 {@code bcrypt_migration} 配置决定使用 BCrypt 或 SHA-512。</p>
     *
     * @param submitData 注册表单数据（name、password、birthday、language）
     * @throws NoSuchAlgorithmException 加密算法不可用时抛出
     */
    public void addAccount(AddAccountDTO submitData) throws NoSuchAlgorithmException {
        // 防止swagger调用，后续的语言路由都受影响
        RequireUtil.requireNotNull(submitData.getLanguage(), I18nUtil.getExceptionMessage("LANGUAGE_NOT_SUPPORT"));
        RequireUtil.requireNull(findByName(submitData.getName()), I18nUtil.getExceptionMessage("AccountService.addAccount.exception1"));
        AccountsDO account = AccountsDO.builder()
                .name(submitData.getName())
                .password(encryptPassword(submitData.getPassword()))
                .birthday(submitData.getBirthday())
                .tempban(Timestamp.valueOf(DefaultDates.getTempban()))
                .language(submitData.getLanguage())
                .lastlogin(Timestamp.valueOf(DefaultDates.getTempban()))
                .build();
        // 可以直接用insertSelective忽略null值
        accountsMapper.insertSelective(account);
    }

    /**
     * 用户自行修改账号信息（密码、PIN、头像、生日、昵称、邮箱、语言等）。
     *
     * <p>需要验证旧密码正确后方可修改。</p>
     *
     * @param submitData 用户提交的修改数据
     * @throws NoSuchAlgorithmException 密码加密时算法不可用
     */
    public void updateAccountByUser(UpdateAccountByUserDTO submitData) throws NoSuchAlgorithmException {
        AccountsDO account = getCurrentUser();
        // 校验旧密码
        RequireUtil.requireTrue(checkPassword(submitData.getOldPwd(), account), I18nUtil.getExceptionMessage("AccountService.updateAccountByUser.oldPassword"));
        // 防止swagger调用，后续的语言路由都受影响
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
     * GM（管理员）修改指定账号信息。
     *
     * <p>可修改密码、PIN、头像、生日、NX点券、枫叶点、角色槽位、性别、
     * 管理员权限、昵称、禁言、邮箱等字段。<b>要求目标账号不在线</b>。</p>
     *
     * @param id         目标账号 ID
     * @param submitData GM 提交的修改数据
     * @throws NoSuchAlgorithmException 密码加密时算法不可用
     */
    public void updateAccountByGM(int id, UpdateAccountByGmDTO submitData) throws NoSuchAlgorithmException {
        AccountsDO account = findById(id);
        RequireUtil.requireNotNull(account, I18nUtil.getExceptionMessage("AccountService.id.NotExist"));
        // 防止swagger调用，后续的语言路由都受影响
        RequireUtil.requireNotNull(account.getLanguage(), I18nUtil.getExceptionMessage("LANGUAGE_NOT_SUPPORT"));
        // GM修改时要求目标账号不在线
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
     * GM 删除指定账号（按主键物理删除）。
     *
     * @param id 目标账号 ID
     */
    public void deleteAccountByGM(int id) {
        RequireUtil.requireNotNull(findById(id), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));
        accountsMapper.deleteById(id);
    }

    /**
     * 加密密码。
     *
     * <p>根据配置 {@code bcrypt_migration} 选择加密方式：
     * 开启时使用 BCrypt（强度 12），否则使用 SHA-512。</p>
     *
     * @param password 明文密码
     * @return 加密后的密文
     * @throws NoSuchAlgorithmException 加密算法不可用
     */
    public String encryptPassword(String password) throws NoSuchAlgorithmException {
        return GameConfig.getServerBoolean("bcrypt_migration") ? BCrypt.hashpw(password, BCrypt.gensalt(12)) : BCrypt.hashpwSHA512(password);
    }

    /**
     * 校验密码是否正确。
     *
     * <p>支持多种加密格式的密码比对：BCrypt（$2 开头）、SHA-1、SHA-512 等。</p>
     *
     * @param pwd        待校验的明文密码
     * @param accountsDO 数据库中存储的账号实体（含加密后密码）
     * @return true 表示密码正确
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
     * 使用指定算法对密码做 hash 比对。
     *
     * @param hash     数据库中存储的 hash 值
     * @param type     算法类型（如 SHA-1、SHA-512）
     * @param password 待校验的明文密码
     * @return true 表示 hash 匹配
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
     * 重置指定账号的登录状态为"未登录"。
     *
     * @param id 目标账号 ID
     */
    public void resetAllLoggedIn(int id) {
        RequireUtil.requireNotNull(findById(id), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));

        AccountsDO account = new AccountsDO();
        account.setId(id);
        account.setLoggedin(LOGIN_NOTLOGGEDIN);
        accountsMapper.update(account);
    }

    /**
     * 封禁账号：将账号标记为 banned，并遍历其下所有角色进行在线追封（Mac + IP）。
     *
     * <p>封禁流程：</p>
     * <ol>
     *   <li>更新 accounts 表 banned 字段</li>
     *   <li>查询该账号所有角色及其所在 world</li>
     *   <li>对每个在线的角色：设置 banned 状态、封禁 Mac、封禁 IP、强制断开连接</li>
     * </ol>
     *
     * @param accountId 待封禁的账号 ID
     * @param reason    封禁原因
     */
    public void banAccount(int accountId, String reason) {
        RequireUtil.requireNotNull(findById(accountId), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));

        // 封停账号
        AccountsDO account = new AccountsDO();
        account.setId(accountId);
        account.setBanned(true);
        account.setBanreason(reason);
        accountsMapper.update(account);
        // 遍历账号下的角色，如果在线，追封客户端/Mac/IP
        List<CharactersDO> characterList = charactersMapper.selectIdAndWorldListByAccountId(accountId); // 仅查询角色ID和所在world
        for (CharactersDO chr : characterList) {
            Character player = Server.getInstance()
                    .getWorlds()
                    .get(chr.getWorld())
                    .getPlayerStorage()
                    .getCharacterById(chr.getId());
            if (player == null) return; // 角色离线
            player.setBanned(true);
            Client c = player.getClient(); // 角色在线，获取客户端
            c.banMacs(); // 封禁Mac
            // c.banHWID(); // 封禁客户端 操作不可逆？
            // 封禁IP
            String ip = c.getRemoteAddress();
            IpbansDO ipban = IpbansDO.builder().ip(ip).aid(String.valueOf(accountId)).build();
            ipbansMapper.insertSelective(ipban);
            // 强制离线，这个方法只是中断了连接不会造成客户端退出，但是实际跟掉线没什么区别
            c.disconnect(false, false);
        }
    }

    /**
     * 解封账号：清除 banned 标记，同时删除该账号关联的 Mac 封禁和 IP 封禁记录。
     *
     * @param accountId 待解封的账号 ID
     */
    public void unbanAccount(int accountId) {
        RequireUtil.requireNotNull(findById(accountId), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));

        // 解封账号
        AccountsDO account = new AccountsDO();
        account.setId(accountId);
        account.setBanned(false);
        accountsMapper.update(account);
        // 解封Mac
        macbansMapper.deleteByQuery(new QueryWrapper().eq(MacbansDO::getAid, accountId));
        // 解封Ip
        ipbansMapper.deleteByQuery(new QueryWrapper().eq(IpbansDO::getAid, accountId));
    }

    /**
     * 批量重置所有账号的登录态（通常在服务器启动时调用）。
     */
    public void resetAllLoggedIn() {
        accountsMapper.updateAllLoggedIn(0);
    }

    /**
     * 对指定角色执行封禁（仅更新状态，不走 IP/Mac 联动封禁）。
     *
     * @param chr    待封禁的角色
     * @param reason 封禁原因
     */
    public void ban(Character chr, String reason) {
        accountsMapper.update(AccountsDO.builder().banned(true).id(chr.getAccountId()).banreason(reason).build());
        // 更新在线的ban状态
        chr.setBanned(true);
    }

    /**
     * 通用的封禁入口：支持按 IP 地址、账号名或角色名封禁。
     *
     * <p>输入字符串判定逻辑：</p>
     * <ul>
     *   <li>匹配 IP 格式（如 192.168.x.x） → 封禁 IP</li>
     *   <li>isAccount=true → 按账号名查 ID 后封禁</li>
     *   <li>isAccount=false → 按角色名查 ID 后封禁</li>
     * </ul>
     *
     * @param str       待封禁的标识（IP 地址 / 账号名 / 角色名）
     * @param reason    封禁原因
     * @param isAccount true 表示按账号名查找；false 表示按角色名查找
     * @throws NoSuchElementException 当按名称找不到对应账号时抛出
     */
    public void ban(String str, String reason, boolean isAccount) {
        if (str.matches("[0-9]{1,3}\\..*")) {
            if (isBanned(str)) {
                return;
            }
            ipbansMapper.insertSelective(IpbansDO.builder().ip(str).build());
            return;
        }
        Integer accountId = null;
        if (isAccount) {
            AccountsDO accountsDO = findByName(str);
            if (accountsDO != null) {
                accountId = accountsDO.getId();
            }
        } else {
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
     * 检查指定 IP 地址是否已被封禁。
     *
     * @param ip IP 地址
     * @return true 表示已被封禁
     */
    public boolean isBanned(String ip) {
        return ipbansMapper.selectCountByQuery(QueryWrapper.create().where(IPBANS_D_O.IP.eq(ip))) > 0;
    }

    /**
     * 获取账号的快捷栏按键映射配置。
     *
     * @param accountId 账号 ID
     * @return 快捷栏按键映射实体
     */
    public QuickslotkeymappedDO getQuickSlotKeyMap(int accountId) {
        return quickslotkeymappedMapper.selectOneById(accountId);
    }
}