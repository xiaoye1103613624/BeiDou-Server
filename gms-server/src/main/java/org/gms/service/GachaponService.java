package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.inventory.Item;
import org.gms.dao.entity.GachaponRewardDO;
import org.gms.dao.entity.GachaponRewardPoolDO;
import org.gms.dao.mapper.GachaponRewardMapper;
import org.gms.dao.mapper.GachaponRewardPoolMapper;
import org.gms.model.dto.GachaponPoolSearchReqDTO;
import org.gms.model.dto.GachaponPoolSearchRtnDTO;
import org.gms.net.server.Server;
import org.gms.server.ItemInformationProvider;
import org.gms.server.gachapon.Gachapon;
import org.gms.server.life.LifeFactory;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;
import org.gms.util.Randomizer;
import org.gms.util.RequireUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 百宝箱服务类
 * 提供百宝箱奖池的管理和抽奖功能
 * 支持奖池配置、奖励管理、概率计算和抽奖执行
 */
@Slf4j
@Service
public class GachaponService {
    /** 奖池数据访问对象 */
    @Autowired
    private GachaponRewardPoolMapper gachaponRewardPoolMapper;

    /** 奖励数据访问对象 */
    @Autowired
    private GachaponRewardMapper gachaponRewardMapper;

    /** 奖池奖励缓存，key为奖池ID，value为奖励列表 */
    private static final HashMap<Integer, List<GachaponRewardDO>> poolRewardsCache = new HashMap<>();

    /** 读写锁，用于保证缓存的线程安全 */
    private static final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    /** 读锁 */
    private static final Lock rLock = lock.readLock();

    /** 写锁 */
    private static final Lock wLock = lock.writeLock();

    /**
     * 更新奖池配置（新增或更新）
     * 根据奖池类型设置不同的必填参数校验
     *
     * @param submit 奖池配置数据
     */
    public void updatePool(GachaponRewardPoolDO submit) {
        wLock.lock();
        try {
            RequireUtil.requireNotNull(submit.getStartTime(), "生效时间不能为空");
            if (submit.getIsPublic()) {
                submit.setGachaponId(-1);
                submit.setWeight(0);
                RequireUtil.requireNotNull(submit.getProb(), "公共奖池中奖率不能为空");
            } else {
                submit.setProb(0);
                RequireUtil.requireNotNull(submit.getGachaponId(), "百宝箱ID不能为空");
                RequireUtil.requireNotNull(submit.getWeight(), "奖池权重不能为空");
            }
            // 校验公共奖池概率总和不超过 100%
            gachaponRewardPoolMapper.insertOrUpdate(submit);
            // 清除缓存，下次查询时重新加载
            poolRewardsCache.remove(submit.getId());
        } finally {
            wLock.unlock();
        }
    }

    /**
     * 删除奖池（级联删除奖励）
     *
     * @param id 奖池ID
     */
    @Transactional
    public void deletePool(Integer id) {
        wLock.lock();
        try {
            gachaponRewardPoolMapper.deleteById(id);
            gachaponRewardMapper.deleteByQuery(QueryWrapper.create().where("pool_id=?", id));
            poolRewardsCache.remove(id);
        } finally {
            wLock.unlock();
        }
    }

    /**
     * 获取奖池列表
     * 支持按百宝箱ID筛选，并计算真实概率
     *
     * @param condition 查询条件
     * @return 分页的奖池列表
     */
    public Page<GachaponPoolSearchRtnDTO> getPools(GachaponPoolSearchReqDTO condition) {
        rLock.lock();
        try {
            // 数据查询
            QueryWrapper qw = new QueryWrapper().orderBy(GachaponRewardPoolDO::getIsPublic, false);
            if (condition.getGachaponId() != null) {
                qw.eq("gachapon_id", condition.getGachaponId()).or("is_public=1");
            }
            Page<GachaponRewardPoolDO> paginate = gachaponRewardPoolMapper.paginate(
                    condition.getPageNo(),
                    condition.getPageSize(),
                    qw);

            // 类型转换
            List<GachaponPoolSearchRtnDTO> records = new ArrayList<>();
            GachaponPoolSearchRtnDTO data;
            for (GachaponRewardPoolDO record : paginate.getRecords()) {
                data = new GachaponPoolSearchRtnDTO();
                data.setId(record.getId());
                data.setName(record.getName());
                data.setGachaponId(record.getGachaponId());
                data.setWeight(record.getWeight());
                data.setIsPublic(record.getIsPublic());
                data.setProb(record.getProb());
                data.setStartTime(record.getStartTime());
                data.setEndTime(record.getEndTime());
                data.setNotification(record.getNotification());
                data.setComment(record.getComment());
                data.setGachaponName(LifeFactory.getNPCName(record.getGachaponId()));
                records.add(data);
            }

            // 计算真实概率并设置
            if (condition.getGachaponId() != null && condition.getGachaponId() != -1) {
                setRealProb(records);
            }

            return new Page<>(
                    records,
                    paginate.getPageNumber(),
                    paginate.getPageSize(),
                    paginate.getTotalRow()
            );
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 获取奖池的奖励列表
     *
     * @param poolId 奖池ID
     * @return 奖励列表
     */
    public List<GachaponRewardDO> getRewards(Integer poolId) {
        rLock.lock();
        try {
            List<GachaponRewardDO> records = gachaponRewardMapper.selectListByQuery(QueryWrapper.create()
                    .eq("pool_id", poolId)
                    .orderBy(GachaponRewardDO::getItemId, true));
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            for (GachaponRewardDO record : records) {
                record.setItemName(ii.getName(record.getItemId()));
            }
            return records;
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 更新奖励（新增或更新）
     *
     * @param reward 奖励数据
     */
    public void updateReward(GachaponRewardDO reward) {
        wLock.lock();
        try {
            gachaponRewardMapper.insertOrUpdate(reward);
            poolRewardsCache.remove(reward.getPoolId());
        } finally {
            wLock.unlock();
        }
    }

    /**
     * 删除奖励
     *
     * @param id 奖励ID
     */
    public void deleteReward(Integer id) {
        wLock.lock();
        try {
            GachaponRewardDO reward = gachaponRewardMapper.selectOneById(id);
            if (reward != null) {
                gachaponRewardMapper.deleteById(id);
                poolRewardsCache.remove(reward.getPoolId());
            }
        } finally {
            wLock.unlock();
        }
    }

    /**
     * 计算奖池的真实概率
     * 公共奖池使用固定概率，非公共奖池根据权重计算
     *
     * @param pools 奖池列表
     */
    private void setRealProb(List<GachaponPoolSearchRtnDTO> pools) {
        int probTotal = pools.stream().mapToInt(GachaponPoolSearchRtnDTO::getProb).sum();
        int probPoint = 100 * probTotal;
        int weightPoint = 1000000 - probPoint;

        // 公共奖池 = 固定概率 × 100，非公共奖池 = (剩余权重分 × 自身权重 / 总权重)
        int totalWeight = pools.stream().mapToInt(GachaponPoolSearchRtnDTO::getWeight).sum();
        for (GachaponPoolSearchRtnDTO pool : pools) {
            if (pool.getIsPublic()) {
                pool.setRealProb(pool.getProb() * 100);
            } else {
                pool.setRealProb(Math.round((float) weightPoint * pool.getWeight() / totalWeight));
            }
        }
    }

    /**
     * 获取当前有效的奖池列表
     * 根据有效期筛选，包含公共奖池和指定百宝箱的专属奖池
     *
     * @param gachaponId 百宝箱ID
     * @return 有效奖池列表
     */
    private List<GachaponRewardPoolDO> getActivePools(Integer gachaponId) {
        rLock.lock();
        try {
            // 奖池存在有效期，不能缓存
            Timestamp now = new Timestamp(System.currentTimeMillis());

            return gachaponRewardPoolMapper.selectListByQuery(QueryWrapper.create()
                    .where("(gachapon_id=? or is_public=1)", gachaponId)
                    .where("(start_time is null or start_time<=?)", now)
                    .where("(end_time is null or end_time>=?)", now)
                    .orderBy("id")
            );
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 执行百宝箱抽奖
     * 根据概率和权重选择奖池，然后随机抽取奖励
     *
     * @param player     玩家角色
     * @param gachaponId 百宝箱ID
     */
    public void doGachapon(Character player, int gachaponId) {
        rLock.lock();
        try {
            List<GachaponRewardPoolDO> pools = getActivePools(gachaponId);
            if (pools.isEmpty()) {
                player.message("百宝箱为空，请联系管理员，百宝箱id: " + gachaponId);
                log.error("百宝箱奖池为空，百宝箱id:{} 抽奖人:[{}] {}", gachaponId, player.getId(), player.getName());
                return;
            }

            int point;
            int pointTotal = 0;

            int probTotal = pools.stream().mapToInt(GachaponRewardPoolDO::getProb).sum();
            // 公共奖池按比例分配积分为 probPoint，非公共奖池按权重分配 weightPoint
            // 总随机空间 = 1000000（百万分比精度）
            int probPoint = 100 * probTotal;
            int weightPoint = 1000000 - probPoint;

            int totalWeight = pools.stream().mapToInt(GachaponRewardPoolDO::getWeight).sum();
            int random = Randomizer.nextInt(1000000);
            GachaponRewardPoolDO target = null;
            for (GachaponRewardPoolDO pool : pools) {
                // 按权重分配积分：公共 → 固定分，非公共 → 按权重占比分配
                if (pool.getIsPublic()) {
                    point = pool.getProb() * 100;
                } else {
                    point = Math.round((float) weightPoint * pool.getWeight() / totalWeight);
                }

                pointTotal += point;

                if (pointTotal > random) {
                    target = pool;
                    break;
                }
            }

            if (target == null) {
                // 极端情况：所有权重组合导致累计积分为 0 或浮点截断，兜底取第一个奖池
                target = pools.getFirst();
            }
            doReward(player, target);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 根据NPC ID获取所有有效奖励
     *
     * @param npcId NPC ID
     * @return 奖励列表
     */
    public List<GachaponRewardDO> getRewardsByNpcId(Integer npcId) {
        List<GachaponRewardPoolDO> activePools = getActivePools(npcId);
        return activePools.stream().flatMap(pool -> getRewards(pool.getId()).stream()).toList();
    }

    /**
     * 发放奖励给玩家
     * 从奖池中随机选择一个奖励并发放
     *
     * @param player 玩家角色
     * @param pool   奖池
     */
    private void doReward(Character player, GachaponRewardPoolDO pool) {
        List<GachaponRewardDO> poolRewards = getPoolRewards(pool.getId());
        if (poolRewards.isEmpty()) {
            player.message("奖池为空，请联系管理员");
            log.error("百宝箱奖池为空，奖池id:{} 抽奖人:[{}] {}", pool.getId(), player.getId(), player.getName());
            return;
        }

        int random = Randomizer.nextInt(poolRewards.size());
        GachaponRewardDO reward = poolRewards.get(random);
        Item itemGained = player.getAbstractPlayerInteraction().gainItem(reward.getItemId(), reward.getQuantity(), true, true);
        // 防止背包满导致空指针
        if (itemGained == null) {
            return;
        }
        String gachaponMessage = I18nUtil.getMessage("GachaMessage.message1",player.getMap().getMapName(),reward.getQuantity(),ItemInformationProvider.getInstance().getName(reward.getItemId()));
        player.dropMessage(gachaponMessage);
        Gachapon.log(player, reward.getItemId(), player.getMap().getMapName());

        // 如果需要全服广播通知
        if (pool.getNotification()) {
            Server.getInstance().broadcastMessage(player.getWorld(), PacketCreator.gachaponMessage(itemGained, player.getMap().getMapName(), player));
        }
    }

    /**
     * 获取奖池的奖励列表（带缓存）
     *
     * @param poolId 奖池ID
     * @return 奖励列表
     */
    private List<GachaponRewardDO> getPoolRewards(Integer poolId) {
        if (poolRewardsCache.containsKey(poolId)) {
            return poolRewardsCache.get(poolId);
        } else {
            List<GachaponRewardDO> poolRewards = gachaponRewardMapper.selectListByQuery(QueryWrapper.create().where("pool_id=?", poolId));
            poolRewardsCache.put(poolId, poolRewards);
            return poolRewards;
        }
    }
}