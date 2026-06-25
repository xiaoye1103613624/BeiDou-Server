package org.gms.config;

import org.gms.client.Character;
import org.gms.dao.entity.BankAccountDO;
import org.gms.manager.ServerManager;
import org.gms.service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 银行系统静态门面，供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 * <p>
 * 由于脚本 Context 的 ClassLoader 与 Spring 容器不共享，所有方法均直接通过
 * {@link ServerManager#getApplicationContext()} 获取 {@link BankService} Bean 后转发调用，
 * 并将异常统一转换为 {@code success/message} 形式的 Map 返回，便于脚本读取展示。
 * </p>
 */
public class BankManager {

    private static final Logger log = LoggerFactory.getLogger(BankManager.class);

    private BankManager() {}

    private static BankService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(BankService.class);
    }

    /**
     * 判断角色是否已开户。
     */
    public static boolean hasAccount(Integer characterId) {
        try {
            return getService().hasAccount(characterId);
        } catch (Exception e) {
            log.error("查询银行账户失败", e);
            return false;
        }
    }

    /**
     * 查询银行余额（未开户返回 0）。
     */
    public static long getBalance(Integer characterId) {
        try {
            BankAccountDO account = getService().getAccountByCharacterId(characterId);
            return account != null ? account.getBalance() : 0L;
        } catch (Exception e) {
            log.error("查询银行余额失败", e);
            return 0L;
        }
    }

    /**
     * 查询银行账户ID（未开户返回 -1）。
     */
    public static long getAccountId(Integer characterId) {
        try {
            BankAccountDO account = getService().getAccountByCharacterId(characterId);
            return account != null ? account.getId() : -1L;
        } catch (Exception e) {
            log.error("查询银行账户ID失败", e);
            return -1L;
        }
    }

    /**
     * 根据银行账户ID查询账户信息（用于转账前展示对方角色名）。
     *
     * @return {success, message, characterId, characterName, balance}
     */
    public static Map<String, Object> getAccountInfo(Long bankId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            BankAccountDO account = getService().getAccountById(bankId);
            if (account == null) {
                result.put("success", false);
                result.put("message", "银行账户不存在");
                return result;
            }
            result.put("success", true);
            result.put("characterId", account.getCharacterId());
            result.put("characterName", Character.getNameById(account.getCharacterId()));
            result.put("balance", account.getBalance());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 开户。
     *
     * @return {success, message}
     */
    public static Map<String, Object> openAccount(Integer characterId, String rawPassword) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            getService().openAccount(characterId, rawPassword);
            result.put("success", true);
            result.put("message", "开户成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 登录校验密码。
     *
     * @return {success, message}
     */
    public static Map<String, Object> login(Integer characterId, String rawPassword) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            BankAccountDO account = getService().login(characterId, rawPassword);
            result.put("success", account != null);
            result.put("message", account != null ? "登录成功" : "密码错误");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 修改密码。
     *
     * @return {success, message}
     */
    public static Map<String, Object> changePassword(Integer characterId, String oldPassword, String newPassword) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            getService().changePassword(characterId, oldPassword, newPassword);
            result.put("success", true);
            result.put("message", "密码修改成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 管理员重置银行账户密码（无需验证旧密码，按银行账户ID操作）。
     *
     * @return {success, message}
     */
    public static Map<String, Object> resetPassword(Long bankId, String newPassword) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            getService().resetPassword(bankId, newPassword);
            result.put("success", true);
            result.put("message", "密码重置成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 存入金币（扣5%手续费），脚本需在调用前自行从玩家身上扣除 amount 金币。
     *
     * @return {success, message, credited(实际到账), fee(手续费)}
     */
    public static Map<String, Object> deposit(Integer characterId, long amount) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            long credited = getService().deposit(characterId, amount);
            result.put("success", true);
            result.put("message", "存入成功");
            result.put("credited", credited);
            result.put("fee", amount - credited);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 取出金币（无手续费），成功后脚本需自行将 amount 发放给玩家。
     *
     * @return {success, message, amount}
     */
    public static Map<String, Object> withdraw(Integer characterId, long amount) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            long taken = getService().withdraw(characterId, amount);
            result.put("success", true);
            result.put("message", "取出成功");
            result.put("amount", taken);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 转账（扣10%手续费，从转出方账户全额扣除，对方实际到账为 amount - fee）。
     *
     * @return {success, message, credited(对方实际到账), fee(手续费)}
     */
    public static Map<String, Object> transfer(Integer fromCharacterId, Integer toCharacterId, long amount) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            long credited = getService().transfer(fromCharacterId, toCharacterId, amount);
            result.put("success", true);
            result.put("message", "转账成功");
            result.put("credited", credited);
            result.put("fee", amount - credited);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 按对方银行账户ID转账（扣10%手续费，从转出方账户全额扣除）。
     *
     * @return {success, message, credited(对方实际到账), fee(手续费)}
     */
    public static Map<String, Object> transferToBankId(Integer fromCharacterId, Long toBankId, long amount) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            long credited = getService().transferToBankId(fromCharacterId, toBankId, amount);
            result.put("success", true);
            result.put("message", "转账成功");
            result.put("credited", credited);
            result.put("fee", amount - credited);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
