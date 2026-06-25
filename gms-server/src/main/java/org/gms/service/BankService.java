package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.BankAccountDO;
import org.gms.dao.entity.BankLogDO;
import org.gms.dao.mapper.BankAccountMapper;
import org.gms.dao.mapper.BankLogMapper;
import org.gms.util.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * 银行服务类，负责银行账户开户、存取、转账、改密等核心业务逻辑。
 * <p>
 * 银行账户与角色一一对应（角色隔离）。存入扣 5% 手续费，转账扣 10% 手续费，
 * 手续费直接从操作金额中扣除，不计入到账金额。所有资金类操作均记录到
 * {@code xy_bank_log} 操作日志表中。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class BankService {

    /** 存入手续费率：5% */
    private static final BigDecimal DEPOSIT_FEE_RATE = BigDecimal.valueOf(0.05);
    /** 转账手续费率：10% */
    private static final BigDecimal TRANSFER_FEE_RATE = BigDecimal.valueOf(0.10);

    /** 操作类型：存入 */
    public static final int TYPE_DEPOSIT = 1;
    /** 操作类型：取出 */
    public static final int TYPE_WITHDRAW = 2;
    /** 操作类型：转账 */
    public static final int TYPE_TRANSFER = 3;

    private final BankAccountMapper accountMapper;
    private final BankLogMapper logMapper;

    // ==================== 账户查询 ====================

    /**
     * 根据角色ID查询银行账户。
     *
     * @param characterId 角色ID
     * @return 银行账户，未开户返回 null
     */
    public BankAccountDO getAccountByCharacterId(Integer characterId) {
        return accountMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
    }

    /**
     * 判断角色是否已开户。
     *
     * @param characterId 角色ID
     * @return 已开户返回 true
     */
    public boolean hasAccount(Integer characterId) {
        return getAccountByCharacterId(characterId) != null;
    }

    /**
     * 根据银行账户ID查询账户。
     *
     * @param bankId 银行账户ID
     * @return 银行账户，不存在返回 null
     */
    public BankAccountDO getAccountById(Long bankId) {
        return accountMapper.selectOneById(bankId);
    }

    // ==================== 开户 ====================

    /**
     * 开户（确认后调用）。新账户主键由数据库自增生成。
     *
     * @param characterId 角色ID
     * @param rawPassword 明文密码（将以 BCrypt 加密存储）
     * @return 新建的银行账户
     * @throws IllegalStateException 该角色已开户
     */
    @Transactional
    public BankAccountDO openAccount(Integer characterId, String rawPassword) {
        if (hasAccount(characterId)) {
            throw new IllegalStateException("该角色已开户，无需重复开户");
        }
        Date now = new Date();
        BankAccountDO account = BankAccountDO.builder()
                .characterId(characterId)
                .password(BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
                .balance(0L)
                .createTime(now)
                .updateTime(now)
                .build();
        accountMapper.insert(account);
        log.info("角色 {} 开户成功，银行账户ID {}", characterId, account.getId());
        return account;
    }

    // ==================== 登录/改密 ====================

    /**
     * 校验密码登录银行账户。
     *
     * @param characterId 角色ID
     * @param rawPassword 明文密码
     * @return 校验成功返回账户，密码错误或未开户返回 null
     */
    public BankAccountDO login(Integer characterId, String rawPassword) {
        BankAccountDO account = getAccountByCharacterId(characterId);
        if (account == null) {
            return null;
        }
        return BCrypt.checkpw(rawPassword, account.getPassword()) ? account : null;
    }

    /**
     * 修改银行账户密码（需先验证旧密码）。
     *
     * @param characterId 角色ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws IllegalStateException   未开户
     * @throws IllegalArgumentException 旧密码错误
     */
    @Transactional
    public void changePassword(Integer characterId, String oldPassword, String newPassword) {
        BankAccountDO account = getAccountByCharacterId(characterId);
        if (account == null) {
            throw new IllegalStateException("尚未开户");
        }
        if (!BCrypt.checkpw(oldPassword, account.getPassword())) {
            throw new IllegalArgumentException("旧密码不正确");
        }
        account.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        account.setUpdateTime(new Date());
        accountMapper.update(account);
        log.info("角色 {} 修改银行密码成功", characterId);
    }

    /**
     * 管理员重置银行账户密码（无需验证旧密码，按银行账户ID操作）。
     *
     * @param bankId      银行账户ID
     * @param newPassword 新密码
     * @throws IllegalStateException 银行账户不存在
     */
    @Transactional
    public void resetPassword(Long bankId, String newPassword) {
        BankAccountDO account = getAccountById(bankId);
        if (account == null) {
            throw new IllegalStateException("银行账户不存在");
        }
        account.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        account.setUpdateTime(new Date());
        accountMapper.update(account);
        log.info("管理员重置银行账户 {} 密码成功（角色ID {}）", bankId, account.getCharacterId());
    }

    // ==================== 存取/转账 ====================

    /**
     * 存入金币到银行账户，扣除 5% 手续费。
     * <p>
     * 调用前需由脚本自行确认并扣除玩家身上的金币（本金 {@code amount}）。
     * </p>
     *
     * @param characterId 角色ID
     * @param amount      存入本金（不含手续费）
     * @return 实际到账金额（本金 - 手续费）
     * @throws IllegalStateException   未开户
     * @throws IllegalArgumentException 金额不合法
     */
    @Transactional
    public long deposit(Integer characterId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("存入金额必须大于0");
        }
        BankAccountDO account = getAccountByCharacterId(characterId);
        if (account == null) {
            throw new IllegalStateException("尚未开户");
        }
        long fee = calcFee(amount, DEPOSIT_FEE_RATE);
        long credited = amount - fee;

        account.setBalance(account.getBalance() + credited);
        account.setUpdateTime(new Date());
        accountMapper.update(account);

        writeLog(account.getId(), TYPE_DEPOSIT, characterId, null, amount, fee);
        return credited;
    }

    /**
     * 从银行账户取出金币（无手续费）。
     *
     * @param characterId 角色ID
     * @param amount      取出金额
     * @return 实际取出金额
     * @throws IllegalStateException   未开户
     * @throws IllegalArgumentException 金额不合法或余额不足
     */
    @Transactional
    public long withdraw(Integer characterId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("取出金额必须大于0");
        }
        BankAccountDO account = getAccountByCharacterId(characterId);
        if (account == null) {
            throw new IllegalStateException("尚未开户");
        }
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("银行余额不足");
        }
        account.setBalance(account.getBalance() - amount);
        account.setUpdateTime(new Date());
        accountMapper.update(account);

        writeLog(account.getId(), TYPE_WITHDRAW, characterId, null, amount, 0);
        return amount;
    }

    /**
     * 银行账户间转账，扣除 10% 手续费（从转出金额中扣除，对方实际到账为转出金额减手续费）。
     *
     * @param fromCharacterId 转出角色ID
     * @param toCharacterId   转入角色ID
     * @param amount          转出金额（手续费已包含在内，从转出方账户全额扣除）
     * @return 对方实际到账金额（amount - fee）
     * @throws IllegalStateException   双方任一未开户
     * @throws IllegalArgumentException 金额不合法、余额不足或转账给自己
     */
    @Transactional
    public long transfer(Integer fromCharacterId, Integer toCharacterId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("转账金额必须大于0");
        }
        if (fromCharacterId.equals(toCharacterId)) {
            throw new IllegalArgumentException("不能转账给自己");
        }
        BankAccountDO fromAccount = getAccountByCharacterId(fromCharacterId);
        if (fromAccount == null) {
            throw new IllegalStateException("尚未开户");
        }
        BankAccountDO toAccount = getAccountByCharacterId(toCharacterId);
        if (toAccount == null) {
            throw new IllegalStateException("对方角色尚未开户银行账户");
        }
        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("银行余额不足");
        }

        long fee = calcFee(amount, TRANSFER_FEE_RATE);
        long credited = amount - fee;

        Date now = new Date();
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        fromAccount.setUpdateTime(now);
        toAccount.setBalance(toAccount.getBalance() + credited);
        toAccount.setUpdateTime(now);
        accountMapper.update(fromAccount);
        accountMapper.update(toAccount);

        writeLog(fromAccount.getId(), TYPE_TRANSFER, fromCharacterId, toCharacterId, amount, fee);
        return credited;
    }

    /**
     * 按对方银行账户ID转账，扣除 10% 手续费（从转出金额中扣除）。
     *
     * @param fromCharacterId 转出角色ID
     * @param toBankId        对方银行账户ID
     * @param amount          转出金额（手续费已包含在内，从转出方账户全额扣除）
     * @return 对方实际到账金额（amount - fee）
     * @throws IllegalStateException   转出方未开户或对方银行账户不存在
     * @throws IllegalArgumentException 金额不合法、余额不足或转账给自己
     */
    @Transactional
    public long transferToBankId(Integer fromCharacterId, Long toBankId, long amount) {
        BankAccountDO toAccount = getAccountById(toBankId);
        if (toAccount == null) {
            throw new IllegalStateException("银行账户不存在");
        }
        return transfer(fromCharacterId, toAccount.getCharacterId(), amount);
    }

    // ==================== 内部辅助 ====================

    /**
     * 计算手续费，四舍五入到分（整数金币）。
     */
    private long calcFee(long amount, BigDecimal rate) {
        return BigDecimal.valueOf(amount).multiply(rate).setScale(0, RoundingMode.HALF_UP).longValue();
    }

    /**
     * 写入银行操作日志。
     */
    private void writeLog(Long bankId, int type, Integer fromCharacterId, Integer toCharacterId, long amount, long fee) {
        BankLogDO bankLog = BankLogDO.builder()
                .bankId(bankId)
                .type(type)
                .fromCharacterId(fromCharacterId)
                .toCharacterId(toCharacterId)
                .amount(amount)
                .fee(fee)
                .createTime(new Date())
                .build();
        logMapper.insert(bankLog);
    }
}
