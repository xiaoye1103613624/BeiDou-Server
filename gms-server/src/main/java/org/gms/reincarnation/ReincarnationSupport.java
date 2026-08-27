package org.gms.reincarnation;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.client.keybind.KeyBinding;
import org.gms.constants.game.GameConstants;
import org.gms.constants.skills.Beginner;
import org.gms.constants.skills.Evan;
import org.gms.constants.skills.Legend;
import org.gms.constants.skills.Noblesse;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 轮回碑石/石碑：拥有指定装备或消耗品即获得「轮回」技能（专用 ID 1021 系），
 * 施放时在允许地图调用 {@link MapleMap#setDbg} 开启刷怪加成。
 */
public final class ReincarnationSupport {
    private static final Logger log = LoggerFactory.getLogger(ReincarnationSupport.class);

    public static final int EQUIP_BELT = 1132300;
    public static final int EQUIP_TOTEM = 1202193;
    public static final int EQUIP_ALT = 1602008;
    public static final int CONSUME_ITEM = 2430023;

    public static final Set<Integer> EQUIP_IDS = Set.of(EQUIP_BELT, EQUIP_TOTEM, EQUIP_ALT);

    private static final Set<Integer> RELEVANT_ITEMS;

    static {
        RELEVANT_ITEMS = Set.of(EQUIP_BELT, EQUIP_TOTEM, EQUIP_ALT, CONSUME_ITEM);
    }

    /** 英雄之回声（1005 系）：键位/宏若仍引用会在进图时 E_POINTER。 */
    private static final int[] LEGACY_ECHO_SKILL_IDS = {
            Beginner.ECHO_OF_HERO, Noblesse.ECHO_OF_HERO, Legend.ECHO_OF_HERO, Evan.ECHO_OF_HERO
    };

    private ReincarnationSupport() {}

    public static boolean isReincarnationEquip(int itemId) {
        return EQUIP_IDS.contains(itemId);
    }

    public static boolean isRelevantItem(int itemId) {
        return RELEVANT_ITEMS.contains(itemId);
    }

    public static boolean isReincarnationSkill(int skillId) {
        return ReincarnationSkills.isReincarnationSkill(skillId);
    }

    public static boolean isMapAllowed(int mapId) {
        return ReincarnationMapRules.isMapAllowed(mapId);
    }

    /** 背包/装备栏/现金栏等持有轮回装备或消耗品石碑即视为有权限。 */
    public static boolean hasReincarnationAccess(Character chr) {
        if (chr == null) {
            return false;
        }
        for (int equipId : EQUIP_IDS) {
            if (chr.haveItemWithId(equipId, true)) {
                return true;
            }
        }
        return chr.getItemQuantity(CONSUME_ITEM, false) > 0;
    }

    public static void onInventoryChanged(Character chr) {
        if (chr == null) {
            return;
        }
        syncSkill(chr, Announce.SILENT, true);
    }

    public static void onInventoryChanged(Character chr, int itemId) {
        if (chr == null || !isRelevantItem(itemId)) {
            return;
        }
        syncSkill(chr, Announce.SILENT, true);
    }

    public static void onEquipped(Character chr, int itemId) {
        if (chr == null || !isRelevantItem(itemId)) {
            return;
        }
        syncSkill(chr, Announce.GRANTED, true);
    }

    public static void onUnequipped(Character chr, int itemId) {
        if (chr == null || !isRelevantItem(itemId)) {
            return;
        }
        syncSkill(chr, Announce.SILENT, true);
    }

    public static void onEquipChanged(Character chr) {
        if (chr == null) {
            return;
        }
        syncSkill(chr, Announce.SILENT, true);
    }

    public static void onLogin(Character chr) {
        if (chr == null) {
            return;
        }
        // 仅清理 DB 残留键位/宏/技能行；轮回授予延至 getCharInfo+键位/宏封包之后，避免 SET_FIELD 技能表带入无 WZ 的 1021。
        boolean dirty = sanitizeCharacterBindings(chr);
        if (dirty) {
            persistSanitizedBindings(chr);
        }
    }

    /** getCharInfo / sendKeymap / sendMacros 之后调用：按装备授予轮回并必要时刷新键位。 */
    public static void afterLoginPackets(Character chr) {
        if (chr == null) {
            return;
        }
        syncSkill(chr, Announce.SILENT, true);
    }

    /**
     * 角色从 DB 载入内存后立刻清理（早于任何进图封包）。
     * 轮回技能 1021 系在客户端 WZ 未部署前不得出现在键位/宏里，否则 KeyConfig 绘制 E_POINTER。
     */
    public static void sanitizeOnCharacterLoad(Character chr) {
        if (chr == null) {
            return;
        }
        if (sanitizeCharacterBindings(chr)) {
            persistSanitizedBindings(chr);
        }
    }

    private enum Announce {
        SILENT, GRANTED, REVOKED
    }

    private static boolean sanitizeCharacterBindings(Character chr) {
        boolean changed = sanitizeUnsafeSkillGrants(chr);
        changed |= sanitizeLegacyEchoBindings(chr);
        changed |= sanitizeReincarnationBindings(chr);
        if (changed) {
            logKeymapDiagnostics(chr, "sanitize");
        }
        return changed;
    }

    /** 从 DB/内存剥离回声与轮回技能行；轮回在 onLogin/sync 时按权限仅在内存重授。 */
    private static boolean sanitizeUnsafeSkillGrants(Character chr) {
        boolean changed = false;
        for (int id : LEGACY_ECHO_SKILL_IDS) {
            Skill s = SkillFactory.getSkill(id);
            if (s != null && chr.getSkillLevel(s) > 0) {
                chr.changeSkillLevel(s, (byte) -1, 0, -1);
                changed = true;
            }
        }
        for (int id : ReincarnationSkills.ALL_SKILL_IDS) {
            Skill s = SkillFactory.getSkill(id);
            if (s != null && chr.getSkillLevel(s) > 0) {
                chr.changeSkillLevel(s, (byte) -1, 0, -1);
                changed = true;
            }
        }
        return changed;
    }

    private static void persistSanitizedBindings(Character chr) {
        try {
            chr.saveCharToDB();
            log.info("Persisted sanitized keymap/macros for char id={} name={}", chr.getId(), chr.getName());
        } catch (Exception e) {
            log.warn("Failed to persist sanitized bindings for char id={}", chr.getId(), e);
        }
    }

    private static void syncSkill(Character chr, Announce announce, boolean pushKeymap) {
        stripLegacyPqAndEchoGrants(chr);

        final boolean hasAccess = hasReincarnationAccess(chr);
        final int skillId = ReincarnationSkills.skillIdFor(chr);
        final Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            sanitizeCharacterBindings(chr);
            return;
        }

        boolean keymapChanged = sanitizeCharacterBindings(chr);

        if (hasAccess) {
            final boolean newlyGranted = chr.getSkillLevel(skill) <= 0;
            if (newlyGranted) {
                chr.changeSkillLevel(skill, (byte) 1, 1, -1);
            }
            removeOtherLineReincarnationSkills(chr, skillId);
            keymapChanged |= sanitizeReincarnationKeybindings(chr, skillId);
            if (announce == Announce.GRANTED && newlyGranted) {
                chr.dropMessage(5, "已获得「轮回」技能（新手栏）。请打开技能栏或键盘设置自行绑定。请在野外/训练场施放。");
            }
        } else {
            removeAllReincarnationSkills(chr);
            keymapChanged |= clearReincarnationKeybindings(chr);
            if (announce == Announce.REVOKED) {
                chr.dropMessage(6, "已失去「轮回」技能（需持有轮回碑石或轮回石碑）。");
            }
        }

        if (pushKeymap && keymapChanged) {
            chr.sendKeymap();
        }
    }

    private static void removeAllReincarnationSkills(Character chr) {
        for (int id : ReincarnationSkills.ALL_SKILL_IDS) {
            Skill s = SkillFactory.getSkill(id);
            if (s != null && chr.getSkillLevel(s) > 0) {
                chr.changeSkillLevel(s, (byte) -1, 0, -1);
            }
        }
    }

    private static void removeOtherLineReincarnationSkills(Character chr, int keepSkillId) {
        for (int id : ReincarnationSkills.ALL_SKILL_IDS) {
            if (id == keepSkillId) {
                continue;
            }
            Skill s = SkillFactory.getSkill(id);
            if (s != null && chr.getSkillLevel(s) > 0) {
                chr.changeSkillLevel(s, (byte) -1, 0, -1);
            }
        }
    }

    /** 清键位/宏里残留的英雄之回声（1005 系），避免进图绘制快捷键时 E_POINTER。 */
    private static boolean sanitizeLegacyEchoBindings(Character chr) {
        boolean changed = false;
        for (var entry : chr.getKeymap().entrySet()) {
            KeyBinding kb = entry.getValue();
            if (kb != null && kb.getType() == 1 && GameConstants.isLegacyEchoSkill(kb.getAction())) {
                chr.changeKeybinding(entry.getKey(), new KeyBinding(0, 0));
                changed = true;
            }
        }
        for (int i = 0; i < 5; i++) {
            var macro = chr.getSkillMacro(i);
            if (macro == null) {
                continue;
            }
            if (GameConstants.isLegacyEchoSkill(macro.getSkill1())) {
                macro.setSkill1(0);
                changed = true;
            }
            if (GameConstants.isLegacyEchoSkill(macro.getSkill2())) {
                macro.setSkill2(0);
                changed = true;
            }
            if (GameConstants.isLegacyEchoSkill(macro.getSkill3())) {
                macro.setSkill3(0);
                changed = true;
            }
        }
        return changed;
    }

    /** 清键位/宏里残留的轮回技能；客户端 WZ 未部署时绘制会 E_POINTER。 */
    private static boolean sanitizeReincarnationBindings(Character chr) {
        boolean changed = false;
        for (var entry : chr.getKeymap().entrySet()) {
            KeyBinding kb = entry.getValue();
            if (kb != null && kb.getType() == 1 && isReincarnationSkill(kb.getAction())) {
                chr.changeKeybinding(entry.getKey(), new KeyBinding(0, 0));
                changed = true;
            }
        }
        for (int i = 0; i < 5; i++) {
            var macro = chr.getSkillMacro(i);
            if (macro == null) {
                continue;
            }
            if (isReincarnationSkill(macro.getSkill1())) {
                macro.setSkill1(0);
                changed = true;
            }
            if (isReincarnationSkill(macro.getSkill2())) {
                macro.setSkill2(0);
                changed = true;
            }
            if (isReincarnationSkill(macro.getSkill3())) {
                macro.setSkill3(0);
                changed = true;
            }
        }
        return changed;
    }

    private static void logKeymapDiagnostics(Character chr, String phase) {
        List<Integer> keymapSkills = new ArrayList<>();
        List<Integer> macroSkills = new ArrayList<>();
        List<Integer> issues = new ArrayList<>();
        for (var entry : chr.getKeymap().entrySet()) {
            KeyBinding kb = entry.getValue();
            if (kb == null || kb.getType() != 1 || kb.getAction() <= 0) {
                continue;
            }
            keymapSkills.add(kb.getAction());
            if (GameConstants.isLegacyEchoSkill(kb.getAction()) || isReincarnationSkill(kb.getAction())) {
                issues.add(kb.getAction());
            }
        }
        for (int i = 0; i < 5; i++) {
            var macro = chr.getSkillMacro(i);
            if (macro == null) {
                continue;
            }
            for (int sid : new int[]{macro.getSkill1(), macro.getSkill2(), macro.getSkill3()}) {
                if (sid <= 0) {
                    continue;
                }
                macroSkills.add(sid);
                if (GameConstants.isLegacyEchoSkill(sid) || isReincarnationSkill(sid)) {
                    issues.add(sid);
                }
            }
        }
        List<Integer> heldSkills = new ArrayList<>();
        for (var entry : chr.getSkills().entrySet()) {
            if (entry.getValue().skillLevel > 0) {
                int sid = entry.getKey().getId();
                heldSkills.add(sid);
                if (GameConstants.isLegacyEchoSkill(sid) || isReincarnationSkill(sid)) {
                    issues.add(sid);
                }
            }
        }
        log.info("Keymap diag [{}] char id={} name={} keymapSkills={} macroSkills={} heldSkills={} issues={}",
                phase, chr.getId(), chr.getName(), keymapSkills, macroSkills, heldSkills, issues);
    }

    /** 已有轮回绑定时对齐职业线技能 ID；不自动占 F11。 */
    private static boolean sanitizeReincarnationKeybindings(Character chr, int skillId) {
        boolean changed = false;
        for (var entry : chr.getKeymap().entrySet()) {
            KeyBinding kb = entry.getValue();
            if (kb != null && kb.getType() == 1 && isReincarnationSkill(kb.getAction())
                    && kb.getAction() != skillId) {
                chr.changeKeybinding(entry.getKey(), new KeyBinding(1, skillId));
                changed = true;
            }
        }
        return changed;
    }

    private static boolean clearReincarnationKeybindings(Character chr) {
        boolean changed = false;
        for (var entry : chr.getKeymap().entrySet()) {
            KeyBinding kb = entry.getValue();
            if (kb != null && kb.getType() == 1 && isReincarnationSkill(kb.getAction())) {
                chr.changeKeybinding(entry.getKey(), new KeyBinding(0, 0));
                changed = true;
            }
        }
        return changed;
    }

    /** 清掉历史上误发的 PQ 技能、宇宙船，以及旧版劫持的英雄之回声。 */
    private static void stripLegacyPqAndEchoGrants(Character chr) {
        for (int id : new int[]{
                Beginner.BAMBOO_RAIN, Noblesse.BAMBOO_RAIN, Legend.BAMBOO_THRUST, Evan.BAMBOO_THRUST,
                Beginner.INVINCIBLE_BARRIER, 10001010, 20001010, 20011010,
                Beginner.POWER_EXPLOSION, 10001011, 20001011, 20011011,
                Beginner.SPACESHIP, 10001013, 20001013, 20011013,
                Beginner.ECHO_OF_HERO, Noblesse.ECHO_OF_HERO, Legend.ECHO_OF_HERO, Evan.ECHO_OF_HERO
        }) {
            Skill s = SkillFactory.getSkill(id);
            // 仅清 level=1 且 master=1 的误发副本，避免误删玩家任务获得的回声
            if (s != null && chr.getSkillLevel(s) == 1 && chr.getMasterLevel(s) == 1) {
                chr.changeSkillLevel(s, (byte) -1, 0, -1);
            }
        }
    }

    public static boolean tryHandleSkill(Client c, Character chr, int skillId) {
        if (chr == null || !isReincarnationSkill(skillId)) {
            return false;
        }
        if (!hasReincarnationAccess(chr)) {
            chr.dropMessage(5, "需要持有轮回碑石或轮回石碑才能使用「轮回」技能。");
            c.sendPacket(PacketCreator.enableActions());
            return true;
        }
        return tryActivate(c, chr);
    }

    /** 脚本/技能共用：地图校验 + 召唤轮回。 */
    public static boolean tryActivate(Character chr) {
        if (chr == null || chr.getClient() == null) {
            return false;
        }
        return tryActivate(chr.getClient(), chr);
    }

    public static boolean tryActivate(Client c, Character chr) {
        final int mapId = chr.getMapId();
        if (!isMapAllowed(mapId)) {
            chr.dropMessage(5, "您当前所在的地图无法使用此功能。（地图ID:" + mapId + "）");
            c.sendPacket(PacketCreator.enableActions());
            return false;
        }
        if (chr.getMap() == null) {
            c.sendPacket(PacketCreator.enableActions());
            return false;
        }
        boolean ok = chr.getMap().setDbg(chr);
        c.sendPacket(PacketCreator.enableActions());
        return ok;
    }

    /** 消耗品石碑：成功激活后才扣道具。 */
    public static boolean tryActivateConsume(Character chr, int itemId) {
        if (chr == null || itemId != CONSUME_ITEM) {
            return false;
        }
        if (!hasReincarnationAccess(chr)) {
            chr.dropMessage(5, "需要持有轮回石碑才能使用。");
            return false;
        }
        final int mapId = chr.getMapId();
        if (!isMapAllowed(mapId)) {
            chr.dropMessage(5, "您当前所在的地图无法使用此功能。（地图ID:" + mapId + "）");
            return false;
        }
        if (chr.getMap() == null) {
            return false;
        }
        if (!chr.getMap().setDbg(chr)) {
            return false;
        }
        InventoryManipulator.removeById(chr.getClient(), org.gms.client.inventory.InventoryType.USE,
                itemId, 1, false, true);
        return true;
    }
}
