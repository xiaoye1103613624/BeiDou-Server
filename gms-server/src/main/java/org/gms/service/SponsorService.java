package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.client.keybind.KeyBinding;
import org.gms.dao.entity.CharacterSponsorDO;
import org.gms.dao.entity.SponsorClaimDO;
import org.gms.dao.entity.SponsorConfigDO;
import org.gms.dao.entity.SponsorRewardDO;
import org.gms.dao.entity.SponsorSkillGrantDO;
import org.gms.dao.entity.SponsorSkillOptionDO;
import org.gms.dao.mapper.CharacterSponsorMapper;
import org.gms.dao.mapper.SponsorClaimMapper;
import org.gms.dao.mapper.SponsorConfigMapper;
import org.gms.dao.mapper.SponsorRewardMapper;
import org.gms.dao.mapper.SponsorSkillGrantMapper;
import org.gms.dao.mapper.SponsorSkillOptionMapper;
import org.gms.exception.BizException;
import org.gms.model.dto.SkillInfoDTO;
import org.gms.model.pojo.SponsorConfigView;
import org.gms.model.pojo.SponsorEquipStats;
import org.gms.model.pojo.SponsorRewardView;
import org.gms.model.pojo.SponsorSkillOptionView;
import org.gms.net.server.Server;
import org.gms.server.CashShop;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;
import org.gms.util.SponsorEquipStatUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 角色赞助服务。
 * <ul>
 *   <li>总赞助 totalSponsor：充值累加，只增不减，用于档位达标领奖</li>
 *   <li>可消费赞助 spendableSponsor：充值增加，商店购买扣减</li>
 *   <li>档位配置实时读库，Web 后台改完立即生效，无需重启</li>
 *   <li>装备奖励支持 default（WZ 模板 addById）或 custom（绝对值属性，缺省=0，覆盖模板）</li>
 *   <li>技能组 skill_group：写入 skills + 默认快捷键绑定，支持 ONE/MULTI/ALL</li>
 * </ul>
 */
@Slf4j
@Service("sponsorService")
@AllArgsConstructor
public class SponsorService {

    private static final Set<String> REWARD_TYPES = Set.of("nx", "maple", "meso", "item", "skill_group");
    private static final Set<String> PICK_MODES = Set.of("ONE", "MULTI", "ALL");
    /** 与 !skill 一致：Insert、F10、F12、End…；避开 F11（轮回默认） */
    private static final int[] PREFERRED_KEYS = {42, 86, 88, 35, 36, 37, 38, 39, 40, 41, 43, 44, 45};

    private final CharacterSponsorMapper sponsorMapper;
    private final SponsorConfigMapper configMapper;
    private final SponsorRewardMapper rewardMapper;
    private final SponsorClaimMapper claimMapper;
    private final SponsorSkillOptionMapper skillOptionMapper;
    private final SponsorSkillGrantMapper skillGrantMapper;

    // ==================== 余额 ====================

    @Transactional
    public CharacterSponsorDO getOrCreate(int characterId) {
        CharacterSponsorDO row = sponsorMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
        if (row != null) {
            return row;
        }
        Date now = new Date();
        row = CharacterSponsorDO.builder()
                .characterId(characterId)
                .totalSponsor(0)
                .spendableSponsor(0)
                .createTime(now)
                .updateTime(now)
                .build();
        sponsorMapper.insert(row);
        return row;
    }

    /** 供赞助中心脚本：getRecordByPlayerId → getTotalSponsor / getSpendableSponsor */
    @Transactional
    public CharacterSponsorDO getRecordByPlayerId(int playerId) {
        return getOrCreate(playerId);
    }

    public int getTotalSponsor(int characterId) {
        return safeInt(getOrCreate(characterId).getTotalSponsor());
    }

    public int getSpendableSponsor(int characterId) {
        return safeInt(getOrCreate(characterId).getSpendableSponsor());
    }

    /**
     * 充值入账：总赞助与可消费赞助同时增加。
     *
     * @return 入账后的记录
     */
    @Transactional
    public CharacterSponsorDO recharge(int characterId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }
        CharacterSponsorDO row = getOrCreate(characterId);
        long total = (long) safeInt(row.getTotalSponsor()) + amount;
        long spendable = (long) safeInt(row.getSpendableSponsor()) + amount;
        if (total > Integer.MAX_VALUE || spendable > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("赞助余额溢出");
        }
        row.setTotalSponsor((int) total);
        row.setSpendableSponsor((int) spendable);
        row.setUpdateTime(new Date());
        sponsorMapper.update(row);
        log.info("角色 {} 赞助充值 +{}，总赞助={}，可消费={}", characterId, amount, total, spendable);
        return row;
    }

    /**
     * 仅增加可消费赞助（不计入总赞助）。一般不用于充值。
     */
    @Transactional
    public CharacterSponsorDO addSpendableOnly(int characterId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("增加金额必须大于0");
        }
        CharacterSponsorDO row = getOrCreate(characterId);
        long spendable = (long) safeInt(row.getSpendableSponsor()) + amount;
        if (spendable > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("可消费赞助溢出");
        }
        row.setSpendableSponsor((int) spendable);
        row.setUpdateTime(new Date());
        sponsorMapper.update(row);
        return row;
    }

    /**
     * 商店扣减可消费赞助；不足则抛异常。
     */
    @Transactional
    public void spend(int characterId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("扣减金额必须大于0");
        }
        CharacterSponsorDO row = getOrCreate(characterId);
        int bal = safeInt(row.getSpendableSponsor());
        if (bal < amount) {
            throw new IllegalArgumentException("赞助不足，需要 " + amount + "，当前 " + bal);
        }
        row.setSpendableSponsor(bal - amount);
        row.setUpdateTime(new Date());
        sponsorMapper.update(row);
        log.info("角色 {} 消费赞助 -{}，剩余可消费={}", characterId, amount, bal - amount);
    }

    /**
     * 尝试扣减；成功返回 true，余额不足返回 false。
     */
    @Transactional
    public boolean trySpend(int characterId, int amount) {
        try {
            spend(characterId, amount);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ==================== 档位配置 / 领取 ====================

    /** 启用中的档位（含奖励），按 sort_order、amount 排序 */
    public List<SponsorConfigView> listConfigs() {
        List<SponsorConfigDO> configs = configMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("enabled = ?", 1)
                        .orderBy("sort_order", true)
                        .orderBy("amount", true));
        List<SponsorConfigView> views = new ArrayList<>();
        for (SponsorConfigDO c : configs) {
            views.add(toView(c));
        }
        return views;
    }

    public boolean isClaimed(int characterId, int configId) {
        SponsorClaimDO claim = claimMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where("character_id = ?", characterId)
                        .and("config_id = ?", configId));
        return claim != null;
    }

    /**
     * 领取档位奖励（无技能选择；若存在 ONE/MULTI 技能组则失败）。
     */
    @Transactional
    public String claimReward(int playerId, int configId, Character chr) {
        return claimReward(playerId, configId, chr, null);
    }

    /**
     * 领取档位奖励。
     *
     * @param skillSelections key=rewardId，value=该技能组选中的 skillId 列表；
     *                        ALL 可不传或空列表；ONE/MULTI 必须符合 pick 规则
     */
    @Transactional
    public String claimReward(int playerId, int configId, Character chr,
                              Map<Integer, List<Integer>> skillSelections) {
        if (chr == null || chr.getId() != playerId) {
            throw new IllegalArgumentException("角色状态异常");
        }
        SponsorConfigDO config = configMapper.selectOneById(configId);
        if (config == null || config.getEnabled() == null || config.getEnabled() != 1) {
            throw new IllegalArgumentException("档位不存在或已停用");
        }
        if (isClaimed(playerId, configId)) {
            throw new IllegalArgumentException("该档奖励已经领取过了");
        }
        int total = getTotalSponsor(playerId);
        int need = safeInt(config.getAmount());
        if (total < need) {
            throw new IllegalArgumentException("总赞助不足，当前 " + total + "，需要 " + need);
        }

        List<SponsorRewardDO> rewards = rewardMapper.selectListByQuery(
                QueryWrapper.create().where("config_id = ?", configId).orderBy("id", true));

        // 预解析技能组选择（校验失败则整单不发）
        Map<Integer, List<SponsorSkillOptionDO>> resolvedSkills = resolveSkillGrants(rewards, skillSelections);

        // 预检背包
        for (SponsorRewardDO r : rewards) {
            if ("item".equalsIgnoreCase(r.getType()) && safeInt(r.getItemId()) > 0) {
                int itemId = safeInt(r.getItemId());
                int qty = Math.max(1, safeInt(r.getQty()));
                if (!InventoryManipulator.checkSpace(chr.getClient(), itemId, (short) qty, null)) {
                    throw new IllegalArgumentException("背包空间不足");
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        boolean anySkillGranted = false;
        for (SponsorRewardDO r : rewards) {
            String type = r.getType() == null ? "" : r.getType().toLowerCase(Locale.ROOT);
            if ("skill_group".equals(type)) {
                List<SponsorSkillOptionDO> toGrant = resolvedSkills.getOrDefault(safeInt(r.getId()), List.of());
                for (SponsorSkillOptionDO opt : toGrant) {
                    String part = grantSkillOption(chr, configId, r.getId(), opt);
                    if (!part.isEmpty()) {
                        sb.append(part).append(' ');
                        anySkillGranted = true;
                    }
                }
                continue;
            }
            int qty = safeInt(r.getQty());
            if (qty <= 0) {
                continue;
            }
            switch (type) {
                case "nx" -> {
                    chr.getCashShop().gainCash(CashShop.NX_CREDIT, qty);
                    sb.append("点券×").append(qty).append(" ");
                }
                case "maple" -> {
                    chr.getCashShop().gainCash(CashShop.MAPLE_POINT, qty);
                    sb.append("抵用券×").append(qty).append(" ");
                }
                case "meso" -> {
                    chr.gainMeso(qty, false);
                    sb.append("金币×").append(qty).append(" ");
                }
                case "item" -> grantItemReward(chr, r, qty, sb);
                default -> log.warn("未知赞助奖励类型: {}", type);
            }
        }

        claimMapper.insert(SponsorClaimDO.builder()
                .characterId(playerId)
                .configId(configId)
                .claimTime(new Date())
                .build());

        String tierName = config.getName() != null && !config.getName().isBlank()
                ? config.getName()
                : ("赞助满" + need);
        broadcastClaim(chr, tierName, need, total, anySkillGranted);

        String msg = sb.length() == 0 ? "领取成功" : ("获得：" + sb);
        log.info("角色 {} 领取赞助档位 {}，{}", playerId, configId, msg);
        return msg;
    }

    /**
     * 按 pick_mode 解析每组要发放的技能选项；非法选择抛异常。
     */
    private Map<Integer, List<SponsorSkillOptionDO>> resolveSkillGrants(
            List<SponsorRewardDO> rewards, Map<Integer, List<Integer>> skillSelections) {
        Map<Integer, List<Integer>> selections = skillSelections != null ? skillSelections : Map.of();
        Map<Integer, List<SponsorSkillOptionDO>> result = new HashMap<>();

        for (SponsorRewardDO r : rewards) {
            if (!"skill_group".equalsIgnoreCase(r.getType())) {
                continue;
            }
            int rewardId = safeInt(r.getId());
            String mode = requirePickMode(r.getPickMode(), false);
            List<SponsorSkillOptionDO> options = listSkillOptions(rewardId);
            if (options.isEmpty()) {
                throw new IllegalArgumentException("技能组奖励未配置可选技能（rewardId=" + rewardId + "）");
            }

            Map<Integer, SponsorSkillOptionDO> bySkill = new HashMap<>();
            for (SponsorSkillOptionDO o : options) {
                bySkill.put(safeInt(o.getSkillId()), o);
            }

            List<Integer> pickedIds = normalizePickedIds(selections.get(rewardId));
            List<SponsorSkillOptionDO> grantList = new ArrayList<>();

            switch (mode) {
                case "ALL" -> grantList.addAll(options);
                case "ONE" -> {
                    if (pickedIds.size() != 1) {
                        throw new IllegalArgumentException("技能组需选择 1 个技能");
                    }
                    SponsorSkillOptionDO opt = bySkill.get(pickedIds.get(0));
                    if (opt == null) {
                        throw new IllegalArgumentException("所选技能不在该技能组内：" + pickedIds.get(0));
                    }
                    grantList.add(opt);
                }
                case "MULTI" -> {
                    int needPick = Math.max(1, safeInt(r.getQty()));
                    if (needPick > options.size()) {
                        needPick = options.size();
                    }
                    if (pickedIds.size() != needPick) {
                        throw new IllegalArgumentException("技能组需选择 " + needPick + " 个技能，当前 " + pickedIds.size());
                    }
                    Set<Integer> seen = new HashSet<>();
                    for (Integer sid : pickedIds) {
                        if (!seen.add(sid)) {
                            throw new IllegalArgumentException("不能重复选择同一技能：" + sid);
                        }
                        SponsorSkillOptionDO opt = bySkill.get(sid);
                        if (opt == null) {
                            throw new IllegalArgumentException("所选技能不在该技能组内：" + sid);
                        }
                        grantList.add(opt);
                    }
                }
                default -> throw new IllegalArgumentException("未知技能组选取模式：" + mode);
            }
            result.put(rewardId, grantList);
        }
        return result;
    }

    private static List<Integer> normalizePickedIds(List<Integer> raw) {
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }
        List<Integer> out = new ArrayList<>();
        for (Integer id : raw) {
            if (id != null && id > 0) {
                out.add(id);
            }
        }
        return out;
    }

    private String grantSkillOption(Character chr, int configId, Integer rewardId, SponsorSkillOptionDO opt) {
        int skillId = safeInt(opt.getSkillId());
        Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            throw new IllegalArgumentException("未知技能：" + skillId);
        }
        int max = skill.getMaxLevel();
        if (max <= 0) {
            max = 1;
        }
        int level = safeInt(opt.getSkillLevel());
        if (level <= 0) {
            level = max;
        } else if (level > max) {
            level = max;
        }

        chr.changeSkillLevel(skill, (byte) level, max, -1);

        int configuredKey = safeInt(opt.getDefaultKey());
        Integer bound = bindSkillHotkey(chr, skillId, configuredKey);
        if (bound != null) {
            chr.sendKeymap();
        }

        Date now = new Date();
        skillGrantMapper.insert(SponsorSkillGrantDO.builder()
                .characterId(chr.getId())
                .configId(configId)
                .rewardId(safeInt(rewardId))
                .skillId(skillId)
                .skillLevel(level)
                .boundKey(bound != null ? bound : 0)
                .grantTime(now)
                .build());

        String name = SkillFactory.getSkillName(skillId);
        if (name == null || name.isBlank()) {
            name = String.valueOf(skillId);
        }
        StringBuilder part = new StringBuilder();
        part.append("技能「").append(name).append("」Lv").append(level);
        if (bound != null) {
            part.append("(键").append(bound).append(')');
        }
        return part.toString();
    }

    /**
     * 绑定技能快捷键：已绑同技能则复用；否则用配置键（空闲时）或偏好空闲键；最后强制 Insert。
     */
    private static Integer bindSkillHotkey(Character player, int skillId, int preferredKey) {
        for (var entry : player.getKeymap().entrySet()) {
            KeyBinding kb = entry.getValue();
            if (kb != null && kb.getType() == 1 && kb.getAction() == skillId) {
                return entry.getKey();
            }
        }
        if (preferredKey > 0) {
            KeyBinding cur = player.getKeymap().get(preferredKey);
            if (cur == null || cur.getType() == 0) {
                player.changeKeybinding(preferredKey, new KeyBinding(1, skillId));
                return preferredKey;
            }
            // 配置键被占用：仍按配置强制写入（产品：领取时默认绑定一次）
            player.changeKeybinding(preferredKey, new KeyBinding(1, skillId));
            return preferredKey;
        }
        for (int key : PREFERRED_KEYS) {
            KeyBinding cur = player.getKeymap().get(key);
            if (cur == null || cur.getType() == 0) {
                player.changeKeybinding(key, new KeyBinding(1, skillId));
                return key;
            }
        }
        for (int key = 0; key < 90; key++) {
            KeyBinding cur = player.getKeymap().get(key);
            if (cur == null || cur.getType() == 0) {
                player.changeKeybinding(key, new KeyBinding(1, skillId));
                return key;
            }
        }
        final int INSERT = 42;
        player.changeKeybinding(INSERT, new KeyBinding(1, skillId));
        return INSERT;
    }

    private void grantItemReward(Character chr, SponsorRewardDO r, int qty, StringBuilder sb) {
        int itemId = safeInt(r.getItemId());
        if (itemId <= 0) {
            return;
        }
        String mode = SponsorEquipStatUtil.normalizeMode(r.getStatMode());
        boolean customEquip = SponsorEquipStatUtil.isEquipItem(itemId)
                && SponsorEquipStatUtil.MODE_CUSTOM.equals(mode);

        if (customEquip) {
            SponsorEquipStats custom = SponsorEquipStatUtil.absolute(
                    SponsorEquipStatUtil.parseJson(r.getStatsJson()));
            for (int i = 0; i < qty; i++) {
                Item raw = ItemInformationProvider.getInstance().getEquipById(itemId);
                if (!(raw instanceof Equip equip)) {
                    throw new IllegalArgumentException("装备数据不存在：" + itemId);
                }
                equip.setQuantity((short) 1);
                SponsorEquipStatUtil.applyToEquip(equip, custom);
                if (!InventoryManipulator.addFromDrop(chr.getClient(), equip, false)) {
                    throw new IllegalArgumentException("发放装备失败，请检查背包空间");
                }
            }
            sb.append("装备#").append(itemId).append("×").append(qty);
            String zh = SponsorEquipStatUtil.formatZh(custom);
            if (!zh.isEmpty()) {
                sb.append('(').append(zh).append(')');
            }
            sb.append(' ');
        } else {
            InventoryManipulator.addById(chr.getClient(), itemId, (short) qty);
            sb.append("道具#").append(itemId).append("×").append(qty).append(" ");
        }
    }

    // ==================== Web 后台 CRUD（实时生效，无缓存） ====================

    /** 管理端：全部档位（含停用），按排序/金额 */
    public List<SponsorConfigDO> listAllConfigsAdmin() {
        return configMapper.selectListByQuery(
                QueryWrapper.create()
                        .orderBy("sort_order", true)
                        .orderBy("amount", true));
    }

    @Transactional
    public SponsorConfigDO saveConfig(SponsorConfigDO data) {
        if (data == null) {
            throw new BizException("档位数据不能为空");
        }
        if (data.getAmount() == null || data.getAmount() <= 0) {
            throw new BizException("达标金额必须大于0");
        }
        Date now = new Date();
        String name = data.getName() == null ? "" : data.getName().trim();
        if (name.isEmpty()) {
            name = "赞助满" + data.getAmount();
        }
        int enabled = data.getEnabled() == null ? 1 : (data.getEnabled() != 0 ? 1 : 0);
        int sortOrder = data.getSortOrder() == null ? data.getAmount() : data.getSortOrder();

        if (data.getId() == null) {
            SponsorConfigDO row = SponsorConfigDO.builder()
                    .name(name)
                    .amount(data.getAmount())
                    .enabled(enabled)
                    .sortOrder(sortOrder)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            configMapper.insert(row);
            return row;
        }

        SponsorConfigDO existing = configMapper.selectOneById(data.getId());
        if (existing == null) {
            throw new BizException("档位不存在：" + data.getId());
        }
        existing.setName(name);
        existing.setAmount(data.getAmount());
        existing.setEnabled(enabled);
        existing.setSortOrder(sortOrder);
        existing.setUpdateTime(now);
        configMapper.update(existing);
        return existing;
    }

    @Transactional
    public void toggleConfigEnabled(int configId) {
        SponsorConfigDO existing = configMapper.selectOneById(configId);
        if (existing == null) {
            throw new BizException("档位不存在：" + configId);
        }
        int enabled = safeInt(existing.getEnabled()) != 0 ? 0 : 1;
        existing.setEnabled(enabled);
        existing.setUpdateTime(new Date());
        configMapper.update(existing);
    }

    @Transactional
    public void deleteConfig(int configId) {
        SponsorConfigDO existing = configMapper.selectOneById(configId);
        if (existing == null) {
            throw new BizException("档位不存在：" + configId);
        }
        List<SponsorRewardDO> rewards = rewardMapper.selectListByQuery(
                QueryWrapper.create().where("config_id = ?", configId));
        for (SponsorRewardDO r : rewards) {
            deleteSkillOptionsOfReward(safeInt(r.getId()));
        }
        skillGrantMapper.deleteByQuery(QueryWrapper.create().where("config_id = ?", configId));
        rewardMapper.deleteByQuery(QueryWrapper.create().where("config_id = ?", configId));
        claimMapper.deleteByQuery(QueryWrapper.create().where("config_id = ?", configId));
        configMapper.deleteById(configId);
        log.info("已删除赞助档位 {} 及其奖励/领取记录", configId);
    }

    public List<SponsorRewardDO> listRewardsAdmin(int configId) {
        return rewardMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("config_id = ?", configId)
                        .orderBy("id", true));
    }

    @Transactional
    public SponsorRewardDO saveReward(SponsorRewardDO data) {
        if (data == null) {
            throw new BizException("奖励数据不能为空");
        }
        if (data.getConfigId() == null || data.getConfigId() <= 0) {
            throw new BizException("所属档位 configId 无效");
        }
        if (configMapper.selectOneById(data.getConfigId()) == null) {
            throw new BizException("档位不存在：" + data.getConfigId());
        }
        String type = data.getType() == null ? "" : data.getType().trim().toLowerCase(Locale.ROOT);
        if (!REWARD_TYPES.contains(type)) {
            throw new BizException("奖励类型无效，仅支持：nx / maple / meso / item / skill_group");
        }

        int qty;
        int itemId = 0;
        String pickMode = null;
        String statMode = SponsorEquipStatUtil.MODE_DEFAULT;
        String statsJson = null;

        if ("skill_group".equals(type)) {
            pickMode = requirePickMode(data.getPickMode(), true);
            if ("ONE".equals(pickMode)) {
                qty = 1;
            } else if ("MULTI".equals(pickMode)) {
                qty = safeInt(data.getQty());
                if (qty <= 0) {
                    throw new BizException("多选多技能组须设置选取数量（qty≥1）");
                }
            } else {
                // ALL
                qty = Math.max(0, safeInt(data.getQty()));
            }
        } else {
            qty = safeInt(data.getQty());
            if (qty <= 0) {
                throw new BizException("数量必须大于0");
            }
            itemId = safeInt(data.getItemId());
            if ("item".equals(type) && itemId <= 0) {
                throw new BizException("道具奖励必须填写有效的 itemId");
            }
            if (!"item".equals(type)) {
                itemId = 0;
            }
            if ("item".equals(type) && SponsorEquipStatUtil.isEquipItem(itemId)) {
                statMode = SponsorEquipStatUtil.normalizeMode(data.getStatMode());
                if (SponsorEquipStatUtil.MODE_CUSTOM.equals(statMode)) {
                    SponsorEquipStats parsed = SponsorEquipStatUtil.parseJson(data.getStatsJson());
                    if (parsed == null && data.getStatsJson() != null && !data.getStatsJson().isBlank()) {
                        throw new BizException("statsJson 格式无效");
                    }
                    statsJson = SponsorEquipStatUtil.toJson(SponsorEquipStatUtil.absolute(parsed));
                }
            }
        }

        Date now = new Date();
        if (data.getId() == null) {
            SponsorRewardDO row = SponsorRewardDO.builder()
                    .configId(data.getConfigId())
                    .type(type)
                    .itemId(itemId)
                    .qty(qty)
                    .statMode(statMode)
                    .statsJson(statsJson)
                    .pickMode(pickMode)
                    .createTime(now)
                    .build();
            rewardMapper.insert(row);
            return row;
        }

        SponsorRewardDO existing = rewardMapper.selectOneById(data.getId());
        if (existing == null) {
            throw new BizException("奖励行不存在：" + data.getId());
        }
        // 类型从 skill_group 改走时清理选项
        if ("skill_group".equalsIgnoreCase(existing.getType()) && !"skill_group".equals(type)) {
            deleteSkillOptionsOfReward(safeInt(existing.getId()));
        }
        existing.setConfigId(data.getConfigId());
        existing.setType(type);
        existing.setItemId(itemId);
        existing.setQty(qty);
        existing.setStatMode(statMode);
        existing.setStatsJson(statsJson);
        existing.setPickMode(pickMode);
        rewardMapper.update(existing);
        return existing;
    }

    @Transactional
    public void deleteReward(int rewardId) {
        SponsorRewardDO existing = rewardMapper.selectOneById(rewardId);
        if (existing == null) {
            throw new BizException("奖励行不存在：" + rewardId);
        }
        deleteSkillOptionsOfReward(rewardId);
        rewardMapper.deleteById(rewardId);
    }

    // ==================== 技能组选项 CRUD ====================

    public List<SponsorSkillOptionDO> listSkillOptionsAdmin(int rewardId) {
        return listSkillOptions(rewardId);
    }

    private List<SponsorSkillOptionDO> listSkillOptions(int rewardId) {
        return skillOptionMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("reward_id = ?", rewardId)
                        .orderBy("sort_order", true)
                        .orderBy("id", true));
    }

    @Transactional
    public SponsorSkillOptionDO saveSkillOption(SponsorSkillOptionDO data) {
        if (data == null) {
            throw new BizException("技能选项不能为空");
        }
        if (data.getRewardId() == null || data.getRewardId() <= 0) {
            throw new BizException("所属奖励 rewardId 无效");
        }
        SponsorRewardDO reward = rewardMapper.selectOneById(data.getRewardId());
        if (reward == null) {
            throw new BizException("奖励行不存在：" + data.getRewardId());
        }
        if (!"skill_group".equalsIgnoreCase(reward.getType())) {
            throw new BizException("仅 skill_group 奖励可配置技能选项");
        }
        int skillId = safeInt(data.getSkillId());
        if (skillId <= 0) {
            throw new BizException("skillId 无效");
        }
        Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            throw new BizException("未知技能：" + skillId + "（检查服务端 Skill.wz）");
        }
        int skillLevel = safeInt(data.getSkillLevel());
        if (skillLevel < 0) {
            skillLevel = 0;
        }
        int defaultKey = Math.max(0, safeInt(data.getDefaultKey()));
        int sortOrder = safeInt(data.getSortOrder());
        Date now = new Date();

        if (data.getId() == null) {
            SponsorSkillOptionDO row = SponsorSkillOptionDO.builder()
                    .rewardId(data.getRewardId())
                    .skillId(skillId)
                    .skillLevel(skillLevel)
                    .defaultKey(defaultKey)
                    .sortOrder(sortOrder)
                    .createTime(now)
                    .build();
            skillOptionMapper.insert(row);
            return row;
        }

        SponsorSkillOptionDO existing = skillOptionMapper.selectOneById(data.getId());
        if (existing == null) {
            throw new BizException("技能选项不存在：" + data.getId());
        }
        existing.setRewardId(data.getRewardId());
        existing.setSkillId(skillId);
        existing.setSkillLevel(skillLevel);
        existing.setDefaultKey(defaultKey);
        existing.setSortOrder(sortOrder);
        skillOptionMapper.update(existing);
        return existing;
    }

    @Transactional
    public void deleteSkillOption(int optionId) {
        SponsorSkillOptionDO existing = skillOptionMapper.selectOneById(optionId);
        if (existing == null) {
            throw new BizException("技能选项不存在：" + optionId);
        }
        skillOptionMapper.deleteById(optionId);
    }

    private void deleteSkillOptionsOfReward(int rewardId) {
        skillOptionMapper.deleteByQuery(QueryWrapper.create().where("reward_id = ?", rewardId));
    }

    public SkillInfoDTO getSkillInfo(int skillId) {
        Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            return SkillInfoDTO.builder()
                    .skillId(skillId)
                    .name("")
                    .maxLevel(0)
                    .exists(false)
                    .build();
        }
        String name = SkillFactory.getSkillName(skillId);
        int max = skill.getMaxLevel();
        if (max <= 0) {
            max = 1;
        }
        return SkillInfoDTO.builder()
                .skillId(skillId)
                .name(name != null ? name : "")
                .maxLevel(max)
                .exists(true)
                .build();
    }

    private void broadcastClaim(Character chr, String tierName, int need, int total, boolean withSkill) {
        try {
            String notice = withSkill
                    ? String.format(
                    "【赞助】恭喜玩家 %s 领取了「%s」档位奖励（含技能）！（达标 %d，当前累计赞助 %d）",
                    chr.getName(), tierName, need, total)
                    : String.format(
                    "【赞助】恭喜玩家 %s 领取了「%s」档位奖励！（达标 %d，当前累计赞助 %d）",
                    chr.getName(), tierName, need, total);
            Server.getInstance().broadcastMessage(chr.getWorld(), PacketCreator.serverNotice(6, notice));
        } catch (Exception e) {
            log.warn("赞助领取全服广播失败 characterId={}", chr.getId(), e);
        }
    }

    private SponsorConfigView toView(SponsorConfigDO c) {
        List<SponsorRewardDO> rewards = rewardMapper.selectListByQuery(
                QueryWrapper.create().where("config_id = ?", c.getId()).orderBy("id", true));
        List<SponsorRewardView> rewardViews = new ArrayList<>();
        for (SponsorRewardDO r : rewards) {
            rewardViews.add(toRewardView(r));
        }
        return SponsorConfigView.builder()
                .id(safeInt(c.getId()))
                .name(c.getName() != null ? c.getName() : "")
                .amount(safeInt(c.getAmount()))
                .rewards(rewardViews)
                .build();
    }

    private SponsorRewardView toRewardView(SponsorRewardDO r) {
        String type = r.getType() != null ? r.getType() : "";
        int itemId = safeInt(r.getItemId());
        boolean equip = "item".equalsIgnoreCase(type) && SponsorEquipStatUtil.isEquipItem(itemId);
        String mode = equip ? SponsorEquipStatUtil.normalizeMode(r.getStatMode()) : "";
        SponsorEquipStats stats = equip
                ? SponsorEquipStatUtil.resolveEffective(itemId, r.getStatMode(), r.getStatsJson())
                : null;

        String pickMode = "";
        List<SponsorSkillOptionView> skillOpts = new ArrayList<>();
        if ("skill_group".equalsIgnoreCase(type)) {
            pickMode = safePickMode(r.getPickMode());
            for (SponsorSkillOptionDO o : listSkillOptions(safeInt(r.getId()))) {
                skillOpts.add(toSkillOptionView(o));
            }
        }

        return SponsorRewardView.builder()
                .type(type)
                .rewardId(safeInt(r.getId()))
                .id(itemId)
                .qty(safeInt(r.getQty()))
                .equip(equip)
                .statMode(mode)
                .stats(stats)
                .statsText(equip ? SponsorEquipStatUtil.formatZh(stats) : "")
                .pickMode(pickMode)
                .skillOptions(skillOpts)
                .build();
    }

    private SponsorSkillOptionView toSkillOptionView(SponsorSkillOptionDO o) {
        int skillId = safeInt(o.getSkillId());
        Skill skill = SkillFactory.getSkill(skillId);
        int max = skill != null ? skill.getMaxLevel() : 0;
        if (max <= 0 && skill != null) {
            max = 1;
        }
        String name = SkillFactory.getSkillName(skillId);
        return SponsorSkillOptionView.builder()
                .id(safeInt(o.getId()))
                .skillId(skillId)
                .skillLevel(safeInt(o.getSkillLevel()))
                .defaultKey(safeInt(o.getDefaultKey()))
                .sortOrder(safeInt(o.getSortOrder()))
                .name(name != null ? name : "")
                .maxLevel(max)
                .build();
    }

    /** 展示用：非法时回落 ALL */
    private static String safePickMode(String raw) {
        String m = raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
        return PICK_MODES.contains(m) ? m : "ALL";
    }

    /**
     * @param admin true → BizException；false → IllegalArgumentException（NPC 领取）
     */
    private static String requirePickMode(String raw, boolean admin) {
        String m = raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
        if (!PICK_MODES.contains(m)) {
            String msg = "pickMode 无效，仅支持：ONE / MULTI / ALL";
            if (admin) {
                throw new BizException(msg);
            }
            throw new IllegalArgumentException(msg);
        }
        return m;
    }

    private static int safeInt(Integer v) {
        return v == null ? 0 : v;
    }
}
