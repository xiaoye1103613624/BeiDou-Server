package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.AccountStaminaDO;
import org.gms.dao.mapper.AccountStaminaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 账号级体力服务。体力账号通用（同账号下所有角色共享），每日首次领取直接重置为1000点。
 */
@Slf4j
@Service
@AllArgsConstructor
public class StaminaService {

    /** 体力上限 */
    public static final int MAX_STAMINA = 1000;
    /** 每日发放体力 */
    public static final int DAILY_REFILL = 100;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final AccountStaminaMapper staminaMapper;

    /**
     * 获取（或创建）账号体力记录，并自动结算今日是否需要发放每日体力。
     */
    @Transactional
    public AccountStaminaDO getOrCreate(Integer accountId) {
        return getOrCreateWithRefillInfo(accountId).stamina();
    }

    /**
     * 同 {@link #getOrCreate}，附带返回本次调用是否触发了"今日首次发放"，供登录提示等场景使用。
     */
    @Transactional
    public StaminaRefillResult getOrCreateWithRefillInfo(Integer accountId) {
        AccountStaminaDO stamina = staminaMapper.selectOneByQuery(
                QueryWrapper.create().where("account_id = ?", accountId));
        Date now = new Date();
        String today = DATE_FORMAT.format(now);
        if (stamina == null) {
            stamina = AccountStaminaDO.builder()
                    .accountId(accountId)
                    .stamina(DAILY_REFILL)
                    .lastRefillDate(now)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            staminaMapper.insert(stamina);
            return new StaminaRefillResult(stamina, true);
        }
        String lastRefill = stamina.getLastRefillDate() != null ? DATE_FORMAT.format(stamina.getLastRefillDate()) : null;
        if (!today.equals(lastRefill)) {
            int newStamina = MAX_STAMINA;
            stamina.setStamina(newStamina);
            stamina.setLastRefillDate(now);
            stamina.setUpdateTime(now);
            staminaMapper.update(stamina);
            log.info("账号 {} 每日体力发放成功，当前体力 {}", accountId, newStamina);
            return new StaminaRefillResult(stamina, true);
        }
        return new StaminaRefillResult(stamina, false);
    }

    /**
     * 体力查询结果，refilledToday 标记本次调用是否刚发放了每日体力。
     */
    public record StaminaRefillResult(AccountStaminaDO stamina, boolean refilledToday) {
    }

    /**
     * 查询当前体力（含每日发放结算）。
     */
    public int getStamina(Integer accountId) {
        return getOrCreate(accountId).getStamina();
    }

    /**
     * 增加体力（如使用体力药水），不超过上限。
     *
     * @return 增加后的体力值
     */
    @Transactional
    public int addStamina(Integer accountId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("增加的体力值必须大于0");
        }
        AccountStaminaDO stamina = getOrCreate(accountId);
        int newStamina = Math.min(MAX_STAMINA, stamina.getStamina() + amount);
        stamina.setStamina(newStamina);
        stamina.setUpdateTime(new Date());
        staminaMapper.update(stamina);
        return newStamina;
    }

    /**
     * 消耗体力（如炼药），余量不足则抛出异常。
     */
    @Transactional
    public void consumeStamina(Integer accountId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("消耗的体力值必须大于0");
        }
        AccountStaminaDO stamina = getOrCreate(accountId);
        if (stamina.getStamina() < amount) {
            throw new IllegalArgumentException("体力不足");
        }
        stamina.setStamina(stamina.getStamina() - amount);
        stamina.setUpdateTime(new Date());
        staminaMapper.update(stamina);
    }
}
