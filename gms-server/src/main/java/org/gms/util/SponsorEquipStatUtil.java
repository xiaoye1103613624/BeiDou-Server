package org.gms.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.constants.inventory.ItemConstants;
import org.gms.model.pojo.SponsorEquipStats;
import org.gms.server.ItemInformationProvider;

/**
 * 赞助装备奖励：自定义属性 JSON 解析 / 应用到 Equip / 中文摘要。
 * <p>
 * {@code custom} 为<strong>绝对值</strong>语义：JSON 中缺省/null 的属性视为 0（覆盖模板，非 merge）。
 * 客户端装备 tip 对 0 属性不展示行（封包仍写 short，UI 跳过 0）。
 */
@Slf4j
public final class SponsorEquipStatUtil {

    public static final String MODE_DEFAULT = "default";
    public static final String MODE_CUSTOM = "custom";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SponsorEquipStatUtil() {
    }

    public static boolean isEquipItem(int itemId) {
        return itemId > 0 && ItemConstants.isEquipment(itemId)
                && ItemConstants.getInventoryType(itemId) == InventoryType.EQUIP;
    }

    public static String normalizeMode(String mode) {
        if (mode == null || mode.isBlank()) {
            return MODE_DEFAULT;
        }
        String m = mode.trim().toLowerCase();
        return MODE_CUSTOM.equals(m) ? MODE_CUSTOM : MODE_DEFAULT;
    }

    public static SponsorEquipStats parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(json.trim(), SponsorEquipStats.class);
        } catch (Exception e) {
            log.warn("解析赞助装备属性 JSON 失败: {}", e.getMessage());
            return null;
        }
    }

    public static String toJson(SponsorEquipStats stats) {
        if (stats == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(stats);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("装备属性序列化失败", e);
        }
    }

    /** 从 WZ 模板读取装备基础属性（含 0） */
    public static SponsorEquipStats fromTemplate(int itemId) {
        if (!isEquipItem(itemId)) {
            return null;
        }
        Item raw = ItemInformationProvider.getInstance().getEquipById(itemId);
        if (!(raw instanceof Equip equip)) {
            return null;
        }
        return fromEquip(equip);
    }

    public static SponsorEquipStats fromEquip(Equip equip) {
        if (equip == null) {
            return null;
        }
        return SponsorEquipStats.builder()
                .str(equip.getStr())
                .dex(equip.getDex())
                ._int(equip.getInt())
                .luk(equip.getLuk())
                .hp(equip.getHp())
                .mp(equip.getMp())
                .pAtk(equip.getWatk())
                .mAtk(equip.getMatk())
                .pDef(equip.getWdef())
                .mDef(equip.getMdef())
                .acc(equip.getAcc())
                .avoid(equip.getAvoid())
                .hands(equip.getHands())
                .speed(equip.getSpeed())
                .jump(equip.getJump())
                .upgradeSlot(equip.getUpgradeSlots())
                .build();
    }

    /**
     * 将自定义属性规范化为绝对值：所有已知字段非 null；缺省/null → 0。
     */
    public static SponsorEquipStats absolute(SponsorEquipStats stats) {
        SponsorEquipStats s = stats != null ? stats : new SponsorEquipStats();
        return SponsorEquipStats.builder()
                .str(nzShort(s.getStr()))
                .dex(nzShort(s.getDex()))
                ._int(nzShort(s.get_int()))
                .luk(nzShort(s.getLuk()))
                .hp(nzShort(s.getHp()))
                .mp(nzShort(s.getMp()))
                .pAtk(nzShort(s.getPAtk()))
                .mAtk(nzShort(s.getMAtk()))
                .pDef(nzShort(s.getPDef()))
                .mDef(nzShort(s.getMDef()))
                .acc(nzShort(s.getAcc()))
                .avoid(nzShort(s.getAvoid()))
                .hands(nzShort(s.getHands()))
                .speed(nzShort(s.getSpeed()))
                .jump(nzShort(s.getJump()))
                .upgradeSlot(nzByte(s.getUpgradeSlot()))
                .build();
    }

    /**
     * 解析玩家实际获得的属性：
     * <ul>
     *   <li>default → 纯 WZ 模板</li>
     *   <li>custom → JSON 绝对值（缺省=0，不保留模板非零）</li>
     * </ul>
     */
    public static SponsorEquipStats resolveEffective(int itemId, String statMode, String statsJson) {
        if (!isEquipItem(itemId)) {
            return null;
        }
        if (!MODE_CUSTOM.equals(normalizeMode(statMode))) {
            return fromTemplate(itemId);
        }
        return absolute(parseJson(statsJson));
    }

    /**
     * 将自定义属性以绝对值写入 Equip（null/缺省 → 0，覆盖模板）。
     * 客户端 tip 不展示值为 0 的属性行。
     */
    public static void applyToEquip(Equip equip, SponsorEquipStats stats) {
        if (equip == null) {
            return;
        }
        SponsorEquipStats a = absolute(stats);
        equip.setStr(a.getStr());
        equip.setDex(a.getDex());
        equip.setInt(a.get_int());
        equip.setLuk(a.getLuk());
        equip.setHp(a.getHp());
        equip.setMp(a.getMp());
        equip.setWatk(a.getPAtk());
        equip.setMatk(a.getMAtk());
        equip.setWdef(a.getPDef());
        equip.setMdef(a.getMDef());
        equip.setAcc(a.getAcc());
        equip.setAvoid(a.getAvoid());
        equip.setHands(a.getHands());
        equip.setSpeed(a.getSpeed());
        equip.setJump(a.getJump());
        equip.setUpgradeSlots(a.getUpgradeSlot());
    }

    /** 中文属性行，仅展示大于 0 的项（0 不显示，避免「力量+0」） */
    public static String formatZh(SponsorEquipStats s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        append(sb, "力量", s.getStr());
        append(sb, "敏捷", s.getDex());
        append(sb, "智力", s.get_int());
        append(sb, "运气", s.getLuk());
        append(sb, "HP", s.getHp());
        append(sb, "MP", s.getMp());
        append(sb, "物攻", s.getPAtk());
        append(sb, "魔攻", s.getMAtk());
        append(sb, "物防", s.getPDef());
        append(sb, "魔防", s.getMDef());
        append(sb, "命中", s.getAcc());
        append(sb, "回避", s.getAvoid());
        append(sb, "手技", s.getHands());
        append(sb, "速度", s.getSpeed());
        append(sb, "跳跃", s.getJump());
        if (s.getUpgradeSlot() != null && s.getUpgradeSlot() > 0) {
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append("可升级×").append(s.getUpgradeSlot());
        }
        return sb.toString();
    }

    private static void append(StringBuilder sb, String label, Short v) {
        if (v == null || v == 0) {
            return;
        }
        if (!sb.isEmpty()) {
            sb.append(' ');
        }
        sb.append(label).append('+').append(v);
    }

    private static short nzShort(Short v) {
        return v == null ? 0 : v;
    }

    private static byte nzByte(Byte v) {
        return v == null ? 0 : v;
    }
}
