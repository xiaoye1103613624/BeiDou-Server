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
 * 【业务服务】GachaponService：百宝箱服务类，负责百宝箱抽奖系统的管理。
 * 
 * <p>提供百宝箱奖池的增删改查、奖励管理和抽奖功能。支持公共奖池和专属奖池两种模式，
 * 通过读写锁保证并发安全性，使用内存缓存优化奖励查询性能。</p>
 */
@Slf4j
@Service
public class GachaponService {
    /** 百宝箱奖池数据访问接口 */
    @Autowired
    private GachaponRewardPoolMapper gachaponRewardPoolMapper;
    /** 百宝箱奖励数据访问接口 */
    @Autowired
    private GachaponRewardMapper gachaponRewardMapper;

    /** 奖池奖励缓存，key为奖池ID，value为奖励列表 */
    private static final HashMap<Integer, List<GachaponRewardDO>> poolRewardsCache = new HashMap<>();
    /** 读写锁，用于保证并发安全 */
    private static final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    /** 读锁 */
    private static final Lock rLock = lock.readLock();
    /** 写锁 */
    private static final Lock wLock = lock.writeLock();

    /**
     * 更新奖池配置。
     * 
     * <p>根据是否为公共奖池设置不同的必填字段：
     * <ul>
     *   <li>公共奖池：需要设置中奖率(prob)，百宝箱ID和权重设为默认值</li>
     *   <li>非公共奖池：需要设置百宝箱ID和权重，中奖率设为0</li>
     * </ul>
     * 更新后会清除该奖池的奖励缓存。</p>
     * 
     * @param submit 奖池配置数据
     */
    public void updatePool(GachaponRewardPoolDO submit) {
        wLock.lock();
        try {
            // 校验生效时间必填
            RequireUtil.requireNotNull(submit.getStartTime(), "生效时间不能为空");
            
            if (submit.getIsPublic()) {
                // 公共奖池：设置默认值并校验中奖率
                submit.setGachaponId(-1);
                submit.setWeight(0);
                RequireUtil.requireNotNull(submit.getProb(), "公共奖池中奖率不能为空");
            } else {
                // 非公共奖池：设置中奖率为0并校验必填字段
                submit.setProb(0);
                RequireUtil.requireNotNull(submit.getGachaponId(), "百宝箱ID不能为空");
                RequireUtil.requireNotNull(submit.getWeight(), "奖池权重不能为空");
            }
            
            // 保存或更新奖池配置
            gachaponRewardPoolMapper.insertOrUpdate(submit);
            // 清除该奖池的奖励缓存
            poolRewardsCache.remove(submit.getId());
        } finally {
            wLock.unlock();
        }
    }

    /**
     * 删除奖池。
     * 
     * <p>级联删除奖池及其关联的奖励记录，并清除缓存。</p>
     * 
     * @param id 奖池ID
     */
    @Transactional
    public void deletePool(Integer id) {
        wLock.lock();
        try {
            // 删除奖池
            gachaponRewardPoolMapper.deleteById(id);
            // 删除关联的奖励
            gachaponRewardMapper.deleteByQuery(QueryWrapper.create().where("pool_id=?", id));
            // 清除缓存
            poolRewardsCache.remove(id);
        } finally {
            wLock.unlock();
        }
    }

    /**
     * 分页查询百宝箱奖池列表。
     * <p>
     * 根据查询条件从数据库检索奖池记录，将实体转换为DTO并附加NPC名称。
     * 当指定了有效的百宝箱ID时，会计算并填充各奖池的真实中奖概率。
     * </p>
     *
     * @param condition 查询条件，包含百宝箱ID、分页页码和每页条数
     * @return 分页后的奖池DTO列表，包含基本信息和真实中奖概率
     */
    public Page<GachaponPoolSearchRtnDTO> getPools(GachaponPoolSearchReqDTO condition) {
        rLock.lock();
        try {
            // 构建查询条件：按是否公开降序排列，指定百宝箱时同时包含公共奖池
            QueryWrapper qw = new QueryWrapper().orderBy(GachaponRewardPoolDO::getIsPublic, false);
            if (condition.getGachaponId() != null) {
                qw.eq("gachapon_id", condition.getGachaponId()).or("is_public=1");
            }
            Page<GachaponRewardPoolDO> paginate = gachaponRewardPoolMapper.paginate(
                    condition.getPageNo(),
                    condition.getPageSize(),
                    qw);

            // 将数据库实体转换为DTO，并附加NPC名称
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

            // 指定了有效百宝箱ID时，计算各奖池的真实中奖概率
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
     * 获取奖池的奖励列表。
     * 
     * <p>查询指定奖池的所有奖励，并附加物品名称。</p>
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
            
            // 附加物品名称
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
     * 更新奖励配置。
     * 
     * <p>保存或更新奖励记录，并清除所属奖池的缓存。</p>
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
     * 删除奖励。
     * 
     * <p>删除奖励记录，并清除所属奖池的缓存。</p>
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
     * 计算奖池的真实中奖概率。
     * 
     * <p>公共奖池直接使用设定的中奖率，非公共奖池根据权重分配剩余概率。
     * 总概率分为两部分：公共奖池概率（probPoint）和非公共奖池概率（weightPoint），
     * 其中 weightPoint = 1000000 - probPoint。</p>
     * 
     * @param pools 奖池列表
     */
    private void setRealProb(List<GachaponPoolSearchRtnDTO> pools) {
        // 计算公共奖池总概率
        int probTotal = pools.stream().mapToInt(GachaponPoolSearchRtnDTO::getProb).sum();
        int probPoint = 100 * probTotal;
        // 非公共奖池概率 = 100% - 公共奖池概率
        int weightPoint = 1000000 - probPoint;

        // 计算非公共奖池总权重
        int totalWeight = pools.stream().mapToInt(GachaponPoolSearchRtnDTO::getWeight).sum();
        
        for (GachaponPoolSearchRtnDTO pool : pools) {
            if (pool.getIsPublic()) {
                // 公共奖池直接使用设定概率
                pool.setRealProb(pool.getProb() * 100);
            } else {
                // 非公共奖池按权重分配概率
                pool.setRealProb(Math.round((float) weightPoint * pool.getWeight() / totalWeight));
            }
        }
    }

    /**
     * 获取指定百宝箱的有效奖池列表。
     * 
     * <p>查询当前时间在有效期内的奖池，包括指定百宝箱的专属奖池和公共奖池。
     * 由于奖池存在有效期，不使用缓存。</p>
     * 
     * @param gachaponId 百宝箱ID
     * @return 有效奖池列表
     */
    private List<GachaponRewardPoolDO> getActivePools(Integer gachaponId) {
        rLock.lock();
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());

            return gachaponRewardPoolMapper.selectListByQuery(QueryWrapper.create()
                    // 查询指定百宝箱的专属奖池或公共奖池
                    .where("(gachapon_id=? or is_public=1)", gachaponId)
                    // 生效时间为空或已生效
                    .where("(start_time is null or start_time<=?)", now)
                    // 结束时间为空或未过期
                    .where("(end_time is null or end_time>=?)", now)
                    .orderBy("id")
            );
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 执行百宝箱抽奖。
     * 
     * <p>根据百宝箱ID获取有效奖池，通过概率/权重算法选择目标奖池，然后发放奖励。
     * 概率计算分为两部分：公共奖池使用固定概率，非公共奖池按权重分配剩余概率。</p>
     * 
     * @param player 抽奖玩家
     * @param gachaponId 百宝箱ID
     */
    public void doGachapon(Character player, int gachaponId) {
        rLock.lock();
        try {
            // 获取有效奖池列表（已按ID排序）
            List<GachaponRewardPoolDO> pools = getActivePools(gachaponId);
            if (pools.isEmpty()) {
                player.message("百宝箱为空，请联系管理员，百宝箱id: " + gachaponId);
                log.error("百宝箱奖池为空，百宝箱id:{} 抽奖人:[{}] {}", gachaponId, player.getId(), player.getName());
                return;
            }

            int point; // 当前奖池的积分
            int pointTotal = 0; // 累计积分

            // 计算公共奖池总概率和非公共奖池剩余概率
            int probTotal = pools.stream().mapToInt(GachaponRewardPoolDO::getProb).sum();
            int probPoint = 100 * probTotal; // 公共奖池积分总额（百分制转换）
            int weightPoint = 1000000 - probPoint; // 非公共奖池积分总额

            // 计算非公共奖池总权重
            int totalWeight = pools.stream().mapToInt(GachaponRewardPoolDO::getWeight).sum();
            int random = Randomizer.nextInt(1000000); // 生成0-999999的随机数
            
            GachaponRewardPoolDO target = null;
            for (GachaponRewardPoolDO pool : pools) {
                // 按概率/权重分配积分
                if (pool.getIsPublic()) {
                    point = pool.getProb() * 100;
                } else {
                    point = Math.round((float) weightPoint * pool.getWeight() / totalWeight);
                }

                pointTotal += point;

                // 判断随机数是否落在当前奖池范围内
                if (pointTotal > random) {
                    target = pool;
                    break;
                }
            }

            // 处理极端情况下的null（概率极低，如权重组合导致边界问题）
            if (target == null) {
                target = pools.getFirst();
            }
            
            // 发放奖励
            doReward(player, target);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 根据NPC ID获取所有有效奖励。
     * 
     * <p>获取指定NPC对应的有效奖池，并返回所有奖池的奖励列表。</p>
     * 
     * @param npcId NPC ID
     * @return 奖励列表
     */
    public List<GachaponRewardDO> getRewardsByNpcId(Integer npcId) {
        List<GachaponRewardPoolDO> activePools = getActivePools(npcId);
        return activePools.stream().flatMap(pool -> getRewards(pool.getId()).stream()).toList();
    }

    /**
     * 发放奖励给玩家。
     * 
     * <p>从奖池中随机选择一个奖励物品，发放给玩家。如果奖池为空则提示错误。
     * 如果玩家背包满，则不发放奖励。</p>
     * 
     * @param player 玩家
     * @param pool 奖池
     */
    private void doReward(Character player, GachaponRewardPoolDO pool) {
        // 获取奖池奖励列表（带缓存）
        List<GachaponRewardDO> poolRewards = getPoolRewards(pool.getId());
        if (poolRewards.isEmpty()) {
            player.message("奖池为空，请联系管理员");
            log.error("百宝箱奖池为空，奖池id:{} 抽奖人:[{}] {}", pool.getId(), player.getId(), player.getName());
            return;
        }

        // 随机选择一个奖励
        int random = Randomizer.nextInt(poolRewards.size());
        GachaponRewardDO reward = poolRewards.get(random);
        
        // 发放物品
        Item itemGained = player.getAbstractPlayerInteraction().gainItem(
                reward.getItemId(), reward.getQuantity(), true, true);
        
        // 背包满时返回
        if (itemGained == null) {
            return;
        }
        
        // 发送提示消息
        String gachaponMessage = I18nUtil.getMessage("GachaMessage.message1",
                player.getMap().getMapName(), reward.getQuantity(),
                ItemInformationProvider.getInstance().getName(reward.getItemId()));
        player.dropMessage(gachaponMessage);
        
        // 记录抽奖日志
        Gachapon.log(player, reward.getItemId(), player.getMap().getMapName());

        // 如果需要广播通知
        if (pool.getNotification()) {
            Server.getInstance().broadcastMessage(player.getWorld(), 
                    PacketCreator.gachaponMessage(itemGained, player.getMap().getMapName(), player));
        }
    }

    /**
     * 获取奖池的奖励列表（带缓存）。
     * 
     * <p>优先从缓存获取，缓存不存在时从数据库查询并放入缓存。</p>
     * 
     * @param poolId 奖池ID
     * @return 奖励列表
     */
    private List<GachaponRewardDO> getPoolRewards(Integer poolId) {
        if (poolRewardsCache.containsKey(poolId)) {
            return poolRewardsCache.get(poolId);
        } else {
            List<GachaponRewardDO> poolRewards = gachaponRewardMapper.selectListByQuery(
                    QueryWrapper.create().where("pool_id=?", poolId));
            poolRewardsCache.put(poolId, poolRewards);
            return poolRewards;
        }
    }
}