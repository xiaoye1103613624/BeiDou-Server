package org.gms.reincarnation;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.keybind.KeyBinding;
import org.gms.constants.game.GameConstants;
import org.gms.constants.skills.Beginner;
import org.gms.constants.skills.Evan;
import org.gms.constants.skills.Legend;
import org.gms.constants.skills.Noblesse;
import org.gms.util.PacketCreator;

import java.util.Set;

/**
 * 装备版「轮回碑石」：穿戴后获得可施放技能，施放时开启当前地图刷怪加成（与消耗品石碑共用 {@code MapleMap#setDbg}）。
 * <p>
 * 技能载体必须避开客户端 PQ 专用技能：
 * {@code skillId % 10000000 ∈ [1009,1011] ∪ {1020}} 仅在道场/金字塔可用，
 * 普通地图会提示「该技能无法在当前地图使用。」（纯客户端拦截，服务端收不到包）。
 * <p>
 * 因此改用各职业线「英雄之回声」后缀 <b>1005</b>（非 PQ），穿戴时劫持为轮回；卸下不删除该技能（避免误删玩家已有回声）。
 * 仍会清掉历史上误发的竹雨 1009 / 宇宙船 1013。
 */
public final class ReincarnationSupport {
    public static final int EQUIP_BELT = 1132300;
    public static final int EQUIP_TOTEM = 1202193;
    public static final int EQUIP_ALT = 1602008;

    public static final Set<Integer> EQUIP_IDS = Set.of(EQUIP_BELT, EQUIP_TOTEM, EQUIP_ALT);

    /**
     * 默认快捷键：87 = F11（DirectInput）。勿用 88=F12（ijl15 DamageRank 会截走）。
     * 仅在键位空闲或首次授予时写入；不覆盖玩家自定义绑定。
     */
    private static final int DEFAULT_KEY = 87;

    private static final Set<Integer> ALLOWED_MAPS = Set.of(
            104040000, 104040001, 104040002,
            100040001, 100040002, 100040003, 100040004,
            101020002, 101020003, 101020004, 101020005, 101020006,
            101020007, 101020008, 101020009, 101020010
    );

    private static final Set<Integer> FORBIDDEN_MAPS = Set.of(
            10000,
            952010000, 952010100, 952010200, 952010300, 952010400, 952010500,
            803001200,
            910000000,
            922010900,
            200000301,
            749030000,
            104000000, 100000000, 101000000, 102000000, 103000000, 120000000, 140000000, 105040300,
            200000000, 211000000, 230000000, 222000000, 220000000, 701000000, 250000000, 500000000,
            260000000, 261000000, 600000000, 240000000, 221000000, 251000000, 701000200, 550000000,
            551000000, 801000000, 540010000, 541000000, 300000000, 702100000, 800000000, 702090400,
            700000000, 749020000,
            701010323, 800010100, 105070002, 260010201, 230020100,
            220050100, 250010304, 200010300, 261030000, 250010503, 222010310,
            240020401, 240020101, 105090900, 240040401, 270010500, 270020500, 270030500,
            230040420, 541020800, 702060000, 220080001, 551030200, 280030000, 240060200, 270050100,
            327090420, 703020101, 350060200, 240070603,
            105200110, 105200210, 105200310, 105200410,
            910028310, 910028330, 910028350,
            350060160, 970000106, 970000104, 861000050,
            555001100, 555001101, 555001102,
            970050110, 401060200, 271040100,
            252030100, 252030000, 910540200, 910540100, 240093310, 240093300, 555000201, 555000200,
            510102400, 510101300, 910025201, 910025200, 910141030, 910141000, 910142090, 910142080,
            745090100, 745010500, 803200000, 803100000, 209000002, 209000001, 910142110, 910142100,
            910000251, 802000101, 910001000
    );

    private ReincarnationSupport() {}

    public static boolean isReincarnationEquip(int itemId) {
        return EQUIP_IDS.contains(itemId);
    }

    /** 英雄之回声系（1005 / 10001005 / 20001005 / 20011005），非客户端 PQ 技能。 */
    public static boolean isReincarnationSkill(int skillId) {
        return skillId == Beginner.ECHO_OF_HERO
                || skillId == Noblesse.ECHO_OF_HERO
                || skillId == Legend.ECHO_OF_HERO
                || skillId == Evan.ECHO_OF_HERO;
    }

    public static boolean isMapAllowed(int mapId) {
        if (ALLOWED_MAPS.contains(mapId)) {
            return true;
        }
        return !FORBIDDEN_MAPS.contains(mapId);
    }

    public static boolean isWearing(Character chr) {
        if (chr == null) {
            return false;
        }
        for (Item item : chr.getInventory(InventoryType.EQUIPPED).list()) {
            if (item != null && isReincarnationEquip(item.getItemId())) {
                return true;
            }
        }
        return false;
    }

    public static int skillIdFor(Character chr) {
        if (chr == null) {
            return Beginner.ECHO_OF_HERO;
        }
        int jobId = chr.getJob().getId();
        if (GameConstants.isCygnus(jobId)) {
            return Noblesse.ECHO_OF_HERO;
        }
        if (GameConstants.isAran(jobId)) {
            return Legend.ECHO_OF_HERO;
        }
        if (jobId == 2001 || (jobId >= 2200 && jobId <= 2218)) {
            Skill evan = SkillFactory.getSkill(Evan.ECHO_OF_HERO);
            if (evan != null) {
                return Evan.ECHO_OF_HERO;
            }
        }
        return Beginner.ECHO_OF_HERO;
    }

    public static void onEquipped(Character chr, int itemId) {
        if (chr == null || !isReincarnationEquip(itemId)) {
            return;
        }
        syncSkill(chr, Announce.EQUIP);
    }

    public static void onUnequipped(Character chr, int itemId) {
        if (chr == null || !isReincarnationEquip(itemId)) {
            return;
        }
        syncSkill(chr, Announce.UNEQUIP);
    }

    public static void onEquipChanged(Character chr) {
        onLogin(chr);
    }

    public static void onLogin(Character chr) {
        if (chr == null) {
            return;
        }
        syncSkill(chr, Announce.SILENT);
    }

    private enum Announce {
        SILENT, EQUIP, UNEQUIP
    }

    private static void syncSkill(Character chr, Announce announce) {
        final boolean wearing = isWearing(chr);
        stripLegacyPqAndSpaceshipGrants(chr);

        final int skillId = skillIdFor(chr);
        final Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            return;
        }

        if (wearing) {
            final boolean newlyGranted = chr.getSkillLevel(skill) <= 0;
            if (newlyGranted) {
                chr.changeSkillLevel(skill, (byte) 1, 1, -1);
            }
            stripOtherLineEchoGrants(chr, skillId);
            final boolean keymapChanged = ensureKeybinding(chr, skillId, newlyGranted);
            if (keymapChanged) {
                chr.sendKeymap();
            }
            if (announce == Announce.EQUIP) {
                chr.dropMessage(5, "穿戴轮回碑石后可使用「轮回」技能（新手栏）。默认 F11，可自行改键。请在野外/训练场施放。");
            }
        } else {
            // 不删除 1005：避免误删玩家任务获得的英雄之回声；未穿戴时该技能走原回声效果
            stripOtherLineEchoGrants(chr, -1);
            if (announce == Announce.UNEQUIP) {
                chr.dropMessage(6, "卸下轮回碑石后，「轮回」刷怪效果不可用（需穿戴施放）。");
            }
        }
    }

    private static boolean ensureKeybinding(Character chr, int skillId, boolean newlyGranted) {
        Integer boundKey = null;
        for (var entry : chr.getKeymap().entrySet()) {
            KeyBinding kb = entry.getValue();
            if (kb != null && kb.getType() == 1 && isReincarnationSkill(kb.getAction())) {
                boundKey = entry.getKey();
                break;
            }
        }
        if (boundKey != null) {
            KeyBinding cur = chr.getKeymap().get(boundKey);
            if (cur.getAction() != skillId) {
                chr.changeKeybinding(boundKey, new KeyBinding(1, skillId));
                return true;
            }
            return false;
        }
        KeyBinding atDefault = chr.getKeymap().get(DEFAULT_KEY);
        if (atDefault == null || atDefault.getType() == 0) {
            chr.changeKeybinding(DEFAULT_KEY, new KeyBinding(1, skillId));
            return true;
        }
        if (newlyGranted) {
            chr.dropMessage(5, "F11 已被占用，「轮回」未自动绑定。请打开技能栏或键盘设置自行绑定。");
        }
        return false;
    }

    /** 清掉客户端 PQ 技能（竹雨等）与禁用的宇宙船误发。 */
    private static void stripLegacyPqAndSpaceshipGrants(Character chr) {
        for (int id : new int[]{
                Beginner.BAMBOO_RAIN, Noblesse.BAMBOO_RAIN, Legend.BAMBOO_THRUST, Evan.BAMBOO_THRUST,
                Beginner.INVINCIBLE_BARRIER, 10001010, 20001010, 20011010,
                Beginner.POWER_EXPLOSION, 10001011, 20001011, 20011011,
                Beginner.SPACESHIP, 10001013, 20001013, 20011013
        }) {
            Skill s = SkillFactory.getSkill(id);
            if (s != null && chr.getSkillLevel(s) == 1) {
                chr.changeSkillLevel(s, (byte) -1, 0, -1);
            }
        }
    }

    private static void stripOtherLineEchoGrants(Character chr, int keepSkillId) {
        for (int id : new int[]{
                Beginner.ECHO_OF_HERO,
                Noblesse.ECHO_OF_HERO,
                Legend.ECHO_OF_HERO,
                Evan.ECHO_OF_HERO
        }) {
            if (id == keepSkillId) {
                continue;
            }
            Skill s = SkillFactory.getSkill(id);
            // 仅清 master=1 且 level=1 的「我们可能发过的」跨职业线副本；保守：只在 keep 有效时清其它线
            if (keepSkillId > 0 && s != null && chr.getSkillLevel(s) == 1) {
                chr.changeSkillLevel(s, (byte) -1, 0, -1);
            }
        }
    }

    public static boolean tryHandleSkill(Client c, Character chr, int skillId) {
        if (chr == null || !isReincarnationSkill(skillId) || !isWearing(chr)) {
            return false;
        }
        final int mapId = chr.getMapId();
        if (!isMapAllowed(mapId)) {
            chr.dropMessage(5, "您当前所在的地图无法使用此功能。（地图ID:" + mapId + "）");
            c.sendPacket(PacketCreator.enableActions());
            return true;
        }
        if (chr.getMap() != null) {
            chr.getMap().setDbg(chr);
        }
        c.sendPacket(PacketCreator.enableActions());
        return true;
    }
}
