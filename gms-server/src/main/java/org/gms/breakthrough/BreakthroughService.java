package org.gms.breakthrough;

import org.gms.client.inventory.Equip;
import org.gms.util.Randomizer;

import java.util.ArrayList;
import java.util.List;

/**
 * 装备破界系统。
 * <p>破界等级 N（0~50）= 使用强化卷次数（普通装 2439102 / 点装 2439101），每次次数 +1，封顶 50。</p>
 * <p>每次破界：随机重掷 13 属性池，每条独立激活（+其固定值）或 +0，本次结果【覆盖】上
 * 一次（不累计）。激活状态用 {@code breakthroughPool} 掩码持久化，升级时先还原旧加值再写入新加值。</p>
 * <p>每次消耗：对应强化卷 ×1 + 500 点券；成功/失败消息随机，只影响提示，N 一律 +1。</p>
 */
public final class BreakthroughService {

    private BreakthroughService() {
    }

    public static final int MAX_LEVEL = 50;
    public static final int SCROLL_NORMAL = 2439102; // 装备强化卷
    public static final int SCROLL_CASH = 2439101;   // 时装强化卷
    public static final int COST_NX = 500;           // 每次 500 点券

    /** 十三属性池：固定加成值（顺序与掩码 bit 一致）。 */
    public static final class Attr {
        public final String name;
        public final int value;

        Attr(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    /** 第 0~12 位。 */
    public static final Attr[] POOL = {
            new Attr("力量", 1),
            new Attr("敏捷", 1),
            new Attr("智力", 1),
            new Attr("运气", 1),
            new Attr("手技", 1),
            new Attr("攻击力", 1),
            new Attr("魔法力", 2),
            new Attr("防御力", 5),
            new Attr("魔法防御力", 5),
            new Attr("命中率", 1),
            new Attr("闪避率", 1),
            new Attr("MAXHP", 10),
            new Attr("MAXMP", 10)
    };

    public static int poolSize() {
        return POOL.length;
    }

    public static int levelOf(Equip equip) {
        if (equip == null) {
            return 0;
        }
        return Math.max(0, Math.min(MAX_LEVEL, equip.getBreakthrough() & 0xFF));
    }

    public static boolean isMax(Equip equip) {
        return levelOf(equip) >= MAX_LEVEL;
    }

    public static int maskOf(Equip equip) {
        if (equip == null) {
            return 0;
        }
        return equip.getBreakthroughPool() & 0x1FFF;
    }

    /** 当前某条是否激活（掩码位判定）。 */
    public static boolean isActive(Equip equip, int idx) {
        if (idx < 0 || idx >= POOL.length) {
            return false;
        }
        return (maskOf(equip) & (1 << idx)) != 0;
    }

    /** 计算某掩码下激活属性固定值之和。 */
    public static int sumByMask(int mask) {
        int sum = 0;
        for (int i = 0; i < POOL.length; i++) {
            if ((mask & (1 << i)) != 0) {
                sum += POOL[i].value;
            }
        }
        return sum;
    }

    /**
     * 重掷 13 条属性并【覆盖】写入装备本体（先还原旧加值，再写新加值）。
     *
     * @return 新掩码
     */
    public static int reroll(Equip equip) {
        if (equip == null) {
            return 0;
        }
        int oldMask = maskOf(equip);
        int newMask = 0;
        for (int i = 0; i < POOL.length; i++) {
            if (Randomizer.nextInt(100) < 50) {
                newMask |= (1 << i);
            }
        }
        applyMask(equip, newMask, oldMask);
        return newMask;
    }

    /** 把掩码从 oldMask 迁移到 newMask：先减去旧值，再加新值。 */
    private static void applyMask(Equip equip, int newMask, int oldMask) {
        for (int i = 0; i < POOL.length; i++) {
            int oldActive = (oldMask & (1 << i)) != 0 ? POOL[i].value : 0;
            int newActive = (newMask & (1 << i)) != 0 ? POOL[i].value : 0;
            int delta = newActive - oldActive;
            if (delta == 0) {
                continue;
            }
            applyStat(equip, i, delta);
        }
        equip.setBreakthroughPool(newMask & 0x1FFF);
    }

    private static void applyStat(Equip equip, int idx, int delta) {
        switch (idx) {
            case 0 -> equip.setStr((short) (equip.getStr() + delta));
            case 1 -> equip.setDex((short) (equip.getDex() + delta));
            case 2 -> equip.setInt((short) (equip.getInt() + delta));
            case 3 -> equip.setLuk((short) (equip.getLuk() + delta));
            case 4 -> equip.setHands((short) (equip.getHands() + delta));
            case 5 -> equip.setWatk((short) (equip.getWatk() + delta));
            case 6 -> equip.setMatk((short) (equip.getMatk() + delta));
            case 7 -> equip.setWdef((short) (equip.getWdef() + delta));
            case 8 -> equip.setMdef((short) (equip.getMdef() + delta));
            case 9 -> equip.setAcc((short) (equip.getAcc() + delta));
            case 10 -> equip.setAvoid((short) (equip.getAvoid() + delta));
            case 11 -> equip.setHp((short) (equip.getHp() + delta));
            case 12 -> equip.setMp((short) (equip.getMp() + delta));
            default -> { }
        }
    }

    /** 破界 +1（封顶 50），同时重掷属性。若已是 50 返回 false。 */
    public static boolean upgrade(Equip equip) {
        if (equip == null || isMax(equip)) {
            return false;
        }
        int lv = levelOf(equip);
        equip.setBreakthrough((byte) (lv + 1));
        return true;
    }

    /** 需要的强化卷 ID：点装（cash）用时装强化卷，普通装备用装备强化卷。 */
    public static int scrollFor(int itemId) {
        return org.gms.server.ItemInformationProvider.getInstance().isCash(itemId)
                ? SCROLL_CASH : SCROLL_NORMAL;
    }

    /** 清除破界：还原本体旧加值并清零等级/掩码（供 EquipSourceOps）.BREAKTHROUGH）。 */
    public static void detach(Equip equip) {
        if (equip == null) {
            return;
        }
        applyMask(equip, 0, maskOf(equip));
        equip.setBreakthrough((byte) 0);
    }

    /** 迁移：把 from 的破界原样搬给 to（掩码 + 等级），不碰本体——调用方需在同一 stat 拷贝中带上加值。 */
    public static void attach(Equip to, Equip from) {
        if (to == null || from == null) {
            return;
        }
        to.setBreakthrough(from.getBreakthrough());
        to.setBreakthroughPool(maskOf(from));
    }

    /** 当前激活属性描述（如 "力量+1 攻击力+1 MAXHP+10"；空串=未激活）。 */
    public static String describeActives(Equip equip) {
        if (equip == null) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        int mask = maskOf(equip);
        for (int i = 0; i < POOL.length; i++) {
            if ((mask & (1 << i)) != 0) {
                parts.add(POOL[i].name + "+" + POOL[i].value);
            }
        }
        return String.join(" ", parts);
    }

    /** 全部 13 条固定值描述（NPC 展示用）。 */
    public static String describePool() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < POOL.length; i++) {
            if (i > 0) {
                sb.append("、");
            }
            sb.append(POOL[i].name).append('+').append(POOL[i].value);
        }
        return sb.toString();
    }
}