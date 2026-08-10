package org.gms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ModifyInventory;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.inventory.ItemConstants;
import org.gms.dao.entity.MedalGrowthDO;
import org.gms.dao.mapper.MedalGrowthMapper;
import org.gms.manager.ServerManager;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 成长勋章：地区/野外Boss/远征怪物卡注入属性 + 勋章池幻化。
 * <p>
 * 属性存在角色表，写回到当前成长勋章装备上；幻化只换外观 ID，不丢卡属性。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedalGrowthService {

    public static final int BASE_MEDAL_ID = 1142747;
    public static final int ILLUSION_COST = 5_000_000;

    /** 地区整区注入 */
    public static final int REGION_ALLSTAT = 5;
    public static final int REGION_ATK = 2;

    /** 野外Boss：每种收集满注入一次 */
    public static final int ELITE_ALLSTAT = 1;

    /** 远征Boss：每种收集满注入一次 */
    public static final int EXPED_ALLSTAT = 2;
    public static final int EXPED_ATK = 1;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final MedalGrowthMapper mapper;

    public static MedalGrowthService get() {
        return ServerManager.getApplicationContext().getBean(MedalGrowthService.class);
    }

    // ==================== 进度读写 ====================

    public MedalGrowthDO loadOrCreate(int characterId) {
        MedalGrowthDO row = mapper.selectOneById(characterId);
        if (row != null) {
            return row;
        }
        row = MedalGrowthDO.builder()
                .characterId(characterId)
                .illusionMedalId(0)
                .regionFlags("")
                .eliteFlags("")
                .expedFlags("")
                .poolJson("[]")
                .statStr(0).statDex(0).statInt(0).statLuk(0)
                .statWatk(0).statMatk(0)
                .updateTime(new Date())
                .build();
        mapper.insert(row);
        return row;
    }

    private void save(MedalGrowthDO row) {
        row.setUpdateTime(new Date());
        mapper.insertOrUpdate(row);
    }

    public int currentDisplayId(MedalGrowthDO row) {
        int ill = row.getIllusionMedalId() == null ? 0 : row.getIllusionMedalId();
        return ill > 0 ? ill : BASE_MEDAL_ID;
    }

    public boolean ownsGrowthMedal(Character chr) {
        return findGrowthMedal(chr) != null;
    }

    /**
     * 查找成长勋章：装备栏或背包装备栏中，ID 为当前幻化外观或默认 1142747。
     */
    public Equip findGrowthMedal(Character chr) {
        if (chr == null) {
            return null;
        }
        MedalGrowthDO row = loadOrCreate(chr.getId());
        int displayId = currentDisplayId(row);
        Equip eq = findMedalById(chr, displayId);
        if (eq != null) {
            return eq;
        }
        // 幻化记录与实物不一致时，再找默认本体
        if (displayId != BASE_MEDAL_ID) {
            return findMedalById(chr, BASE_MEDAL_ID);
        }
        return null;
    }

    private Equip findMedalById(Character chr, int itemId) {
        Item equipped = chr.getInventory(InventoryType.EQUIPPED).getItem((short) -49);
        if (equipped instanceof Equip e && e.getItemId() == itemId) {
            return e;
        }
        for (Item it : chr.getInventory(InventoryType.EQUIP).list()) {
            if (it instanceof Equip e && e.getItemId() == itemId) {
                return e;
            }
        }
        return null;
    }

    // ==================== 领取 ====================

    @Transactional
    public String claimBaseMedal(Character chr) {
        if (chr == null) {
            return "角色无效";
        }
        if (ownsGrowthMedal(chr)) {
            return "你已经拥有成长勋章，不可重复领取";
        }
        // 幻化后本体 ID 已变，只要找不到成长载体就允许补领默认外观
        if (!InventoryManipulator.checkSpace(chr.getClient(), BASE_MEDAL_ID, 1, "")) {
            return "装备栏空间不足";
        }
        MedalGrowthDO row = loadOrCreate(chr.getId());
        // 补领时外观重置为默认（池与卡属性保留）
        row.setIllusionMedalId(0);
        save(row);
        Item raw = ItemInformationProvider.getInstance().getEquipById(BASE_MEDAL_ID);
        if (!(raw instanceof Equip equip)) {
            return "勋章数据异常";
        }
        applyStatsToEquip(equip, row);
        markUntradeable(equip);
        InventoryManipulator.addFromDrop(chr.getClient(), equip, false);
        syncEquipPacket(chr, equip);
        return "OK";
    }

    // ==================== 卡注入 ====================

    @Transactional
    public String injectRegion(Character chr, String regionId) {
        if (chr == null || regionId == null || regionId.isBlank()) {
            return "参数无效";
        }
        MedalGrowthDO row = loadOrCreate(chr.getId());
        if (flagContains(row.getRegionFlags(), regionId)) {
            return "该地区已注入过";
        }
        Equip medal = findGrowthMedal(chr);
        if (medal == null) {
            return "请先领取并持有成长勋章 #i" + BASE_MEDAL_ID + "#";
        }
        row.setRegionFlags(appendFlag(row.getRegionFlags(), regionId));
        addAllStat(row, REGION_ALLSTAT);
        addAtk(row, REGION_ATK);
        save(row);
        applyStatsToEquip(medal, row);
        syncEquipPacket(chr, medal);
        return "OK";
    }

    @Transactional
    public String injectElite(Character chr, int cardId) {
        return injectCardFlag(chr, cardId, true);
    }

    @Transactional
    public String injectExped(Character chr, int cardId) {
        return injectCardFlag(chr, cardId, false);
    }

    private String injectCardFlag(Character chr, int cardId, boolean elite) {
        if (chr == null || cardId <= 0) {
            return "参数无效";
        }
        MedalGrowthDO row = loadOrCreate(chr.getId());
        String flags = elite ? row.getEliteFlags() : row.getExpedFlags();
        String key = Integer.toString(cardId);
        if (flagContains(flags, key)) {
            return elite ? "该野外Boss卡已注入过" : "该远征Boss卡已注入过";
        }
        Equip medal = findGrowthMedal(chr);
        if (medal == null) {
            return "请先领取并持有成长勋章 #i" + BASE_MEDAL_ID + "#";
        }
        if (elite) {
            row.setEliteFlags(appendFlag(row.getEliteFlags(), key));
            addAllStat(row, ELITE_ALLSTAT);
        } else {
            row.setExpedFlags(appendFlag(row.getExpedFlags(), key));
            addAllStat(row, EXPED_ALLSTAT);
            addAtk(row, EXPED_ATK);
        }
        save(row);
        applyStatsToEquip(medal, row);
        syncEquipPacket(chr, medal);
        return "OK";
    }

    public boolean hasRegionInjected(Character chr, String regionId) {
        return flagContains(loadOrCreate(chr.getId()).getRegionFlags(), regionId);
    }

    public boolean hasEliteInjected(Character chr, int cardId) {
        return flagContains(loadOrCreate(chr.getId()).getEliteFlags(), Integer.toString(cardId));
    }

    public boolean hasExpedInjected(Character chr, int cardId) {
        return flagContains(loadOrCreate(chr.getId()).getExpedFlags(), Integer.toString(cardId));
    }

    // ==================== 勋章池 ====================

    public List<Integer> getPool(Character chr) {
        return parsePool(loadOrCreate(chr.getId()).getPoolJson());
    }

    @Transactional
    public String addMedalToPool(Character chr, int medalItemId) {
        if (chr == null || !ItemConstants.isMedal(medalItemId)) {
            return "只能注入勋章类道具";
        }
        if (medalItemId == BASE_MEDAL_ID) {
            return "成长勋章本体不能注入勋章池";
        }
        MedalGrowthDO row = loadOrCreate(chr.getId());
        int displayId = currentDisplayId(row);
        if (medalItemId == displayId) {
            return "当前穿戴/持有的成长勋章（含幻化外观）不能注入池";
        }
        List<Integer> pool = parsePool(row.getPoolJson());
        if (pool.contains(medalItemId)) {
            return "该勋章外观已在池中";
        }
        // 优先从背包装备栏扣，再尝试已装备非 -49 的（一般勋章只在 -49）
        if (!removeOneMedal(chr, medalItemId)) {
            return "背包中未找到该勋章";
        }
        pool.add(medalItemId);
        row.setPoolJson(toJson(pool));
        save(row);
        return "OK";
    }

    @Transactional
    public String illusion(Character chr, int targetMedalId) {
        if (chr == null) {
            return "角色无效";
        }
        MedalGrowthDO row = loadOrCreate(chr.getId());
        List<Integer> pool = parsePool(row.getPoolJson());
        boolean toDefault = targetMedalId == BASE_MEDAL_ID || targetMedalId == 0;
        if (!toDefault && !pool.contains(targetMedalId)) {
            return "请先将该勋章注入勋章池";
        }
        if (chr.getMeso() < ILLUSION_COST) {
            return "金币不足，需要 " + (ILLUSION_COST / 10000) + "W";
        }
        Equip old = findGrowthMedal(chr);
        if (old == null) {
            return "未找到成长勋章，请先领取";
        }
        int newId = toDefault ? BASE_MEDAL_ID : targetMedalId;
        if (old.getItemId() == newId) {
            return "已经是此外观，无需幻化";
        }

        chr.gainMeso(-ILLUSION_COST, false);

        short pos = old.getPosition();
        InventoryType invType = pos < 0 ? InventoryType.EQUIPPED : InventoryType.EQUIP;
        InventoryManipulator.removeFromSlot(chr.getClient(), invType, pos, old.getQuantity(), false, false);

        Item raw = ItemInformationProvider.getInstance().getEquipById(newId);
        if (!(raw instanceof Equip neu)) {
            // 回滚金币
            chr.gainMeso(ILLUSION_COST, false);
            return "目标勋章数据不存在";
        }
        neu.setPosition(pos);
        applyStatsToEquip(neu, row);
        markUntradeable(neu);
        chr.getInventory(invType).addItemFromDB(neu);
        chr.getClient().sendPacket(PacketCreator.modifyInventory(false,
                Collections.singletonList(new ModifyInventory(0, neu))));

        row.setIllusionMedalId(toDefault ? 0 : newId);
        save(row);

        if (pos < 0) {
            chr.equipChanged();
        }
        return "OK";
    }

    /** 登录时把 DB 属性刷回成长勋章 */
    public void syncOnLogin(Character chr) {
        if (chr == null) {
            return;
        }
        try {
            MedalGrowthDO row = mapper.selectOneById(chr.getId());
            if (row == null) {
                return;
            }
            Equip medal = findGrowthMedal(chr);
            if (medal == null) {
                return;
            }
            applyStatsToEquip(medal, row);
            syncEquipPacket(chr, medal);
        } catch (Exception e) {
            log.warn("成长勋章登录同步失败 char={}", chr.getId(), e);
        }
    }

    public String describeStats(Character chr) {
        MedalGrowthDO row = loadOrCreate(chr.getId());
        return "四维+" + nz(row.getStatStr())
                + "  攻击+" + nz(row.getStatWatk())
                + "  魔力+" + nz(row.getStatMatk())
                + "  外观:#i" + currentDisplayId(row) + "#";
    }

    // ==================== 内部工具 ====================

    private void addAllStat(MedalGrowthDO row, int v) {
        row.setStatStr(nz(row.getStatStr()) + v);
        row.setStatDex(nz(row.getStatDex()) + v);
        row.setStatInt(nz(row.getStatInt()) + v);
        row.setStatLuk(nz(row.getStatLuk()) + v);
    }

    private void addAtk(MedalGrowthDO row, int v) {
        row.setStatWatk(nz(row.getStatWatk()) + v);
        row.setStatMatk(nz(row.getStatMatk()) + v);
    }

    private void applyStatsToEquip(Equip equip, MedalGrowthDO row) {
        equip.setStr((short) nz(row.getStatStr()));
        equip.setDex((short) nz(row.getStatDex()));
        equip.setInt((short) nz(row.getStatInt()));
        equip.setLuk((short) nz(row.getStatLuk()));
        equip.setWatk((short) nz(row.getStatWatk()));
        equip.setMatk((short) nz(row.getStatMatk()));
        markUntradeable(equip);
    }

    private void markUntradeable(Equip equip) {
        // 固有/不可交易：与项目其它绑定装一致，用 owner 标记便于识别
        if (equip.getOwner() == null || equip.getOwner().isEmpty()) {
            equip.setOwner("成长勋章");
        }
    }

    private void syncEquipPacket(Character chr, Equip equip) {
        try {
            chr.forceUpdateItem(equip);
        } catch (Exception e) {
            log.debug("forceUpdateItem failed, fallback modifyInventory", e);
            chr.getClient().sendPacket(PacketCreator.modifyInventory(false,
                    Collections.singletonList(new ModifyInventory(0, equip))));
        }
    }

    private boolean removeOneMedal(Character chr, int medalItemId) {
        Inventory bag = chr.getInventory(InventoryType.EQUIP);
        for (Item it : bag.list()) {
            if (it.getItemId() == medalItemId) {
                InventoryManipulator.removeFromSlot(chr.getClient(), InventoryType.EQUIP,
                        it.getPosition(), it.getQuantity(), false, false);
                return true;
            }
        }
        Item eq = chr.getInventory(InventoryType.EQUIPPED).getItem((short) -49);
        if (eq != null && eq.getItemId() == medalItemId) {
            // 不允许拆掉当前成长载体；其它已装备勋章极少见
            MedalGrowthDO row = loadOrCreate(chr.getId());
            if (eq.getItemId() == currentDisplayId(row)) {
                return false;
            }
            InventoryManipulator.removeFromSlot(chr.getClient(), InventoryType.EQUIPPED,
                    (short) -49, eq.getQuantity(), false, false);
            return true;
        }
        return false;
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }

    private static boolean flagContains(String flags, String key) {
        if (flags == null || flags.isBlank() || key == null || key.isBlank()) {
            return false;
        }
        for (String p : flags.split(",")) {
            if (key.equals(p.trim())) {
                return true;
            }
        }
        return false;
    }

    private static String appendFlag(String flags, String key) {
        if (flags == null || flags.isBlank()) {
            return key;
        }
        if (flagContains(flags, key)) {
            return flags;
        }
        return flags + "," + key;
    }

    private static List<Integer> parsePool(String json) {
        try {
            if (json == null || json.isBlank()) {
                return new ArrayList<>();
            }
            List<Integer> list = MAPPER.readValue(json, new TypeReference<List<Integer>>() {});
            // 去重保序
            Set<Integer> set = new LinkedHashSet<>(list);
            return new ArrayList<>(set);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static String toJson(List<Integer> pool) {
        try {
            return MAPPER.writeValueAsString(pool);
        } catch (Exception e) {
            return "[]";
        }
    }
}
