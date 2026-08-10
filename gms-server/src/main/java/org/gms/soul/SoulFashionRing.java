package org.gms.soul;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 时装魂魄戒指（1115201~1115234）：带 CharacterEff，v083 多件叠加易进图闪退。
 * 整系按 onlyEquip 处理——身上同时只能穿 1 件。
 */
public final class SoulFashionRing {
    private static final Logger log = LoggerFactory.getLogger(SoulFashionRing.class);

    public static final int ID_MIN = 1115201;
    public static final int ID_MAX = 1115234;

    private SoulFashionRing() {}

    public static boolean isSoulFashionRing(int itemId) {
        return itemId >= ID_MIN && itemId <= ID_MAX;
    }

    /** 当前已穿戴的魂戒（不含即将穿上的那件）。 */
    public static List<Equip> listEquipped(Character chr) {
        List<Equip> out = new ArrayList<>();
        if (chr == null) {
            return out;
        }
        for (Item it : chr.getInventory(InventoryType.EQUIPPED).list()) {
            if (it instanceof Equip eq && isSoulFashionRing(eq.getItemId())) {
                out.add(eq);
            }
        }
        return out;
    }

    /**
     * 穿戴前：卸下身上全部时装魂戒（新戒尚未入装备栏），保证只播一套 CharacterEff。
     * @return 卸下件数
     */
    public static int unequipOthers(Character chr, int incomingItemId) {
        if (chr == null || chr.getClient() == null) {
            return 0;
        }
        Inventory bag = chr.getInventory(InventoryType.EQUIP);
        int n = 0;
        for (Equip eq : listEquipped(chr)) {
            short bagSlot = bag.getNextFreeSlot();
            if (bagSlot < 0) {
                chr.dropMessage(5, "【魂戒】背包已满，无法卸下多余魂戒。请先腾空位再穿。");
                log.warn("soul fashion ring unequip failed (full bag) char={} item={}",
                        chr.getId(), eq.getItemId());
                break;
            }
            InventoryManipulator.unequip(chr.getClient(), eq.getPosition(), bagSlot);
            n++;
        }
        if (n > 0) {
            chr.dropMessage(5, "【魂戒】时装魂戒只能同时穿戴 1 件，已卸下 " + n + " 件。");
            log.info("soul fashion ring unequipped others char={} count={} incoming={}",
                    chr.getId(), n, incomingItemId);
        }
        return n;
    }

    /**
     * 登录前：多件已穿魂戒只留一件（优先阶数最高），其余进背包，避免进图双 CharacterEff 闪退。
     */
    public static void migrateKeepOneOnLogin(Character chr) {
        if (chr == null) {
            return;
        }
        List<Equip> worn = listEquipped(chr);
        if (worn.size() <= 1) {
            return;
        }
        worn.sort((a, b) -> Integer.compare(b.getItemId(), a.getItemId())); // 高阶优先保留
        Equip keep = worn.get(0);
        Inventory bag = chr.getInventory(InventoryType.EQUIP);
        Inventory eqpd = chr.getInventory(InventoryType.EQUIPPED);
        int moved = 0;
        for (int i = 1; i < worn.size(); i++) {
            Equip eq = worn.get(i);
            short bagSlot = bag.getNextFreeSlot();
            if (bagSlot < 0) {
                log.warn("migrateSoulFashionRing: char {} bag full, left extras equipped", chr.getId());
                break;
            }
            // 登录阶段尚未发 inventory 包：直接挪槽位，避免走 unequip 发包
            short from = eq.getPosition();
            eqpd.removeSlot(from);
            if (eq.getRingId() > -1) {
                try {
                    chr.getRingById(eq.getRingId()).unequip();
                } catch (Exception ignored) {
                }
            }
            chr.unequippedItem(eq);
            eq.setPosition(bagSlot);
            bag.addItemFromDB(eq);
            moved++;
            log.info("migrateSoulFashionRing: char {} unequip {} slot {} → bag {}",
                    chr.getId(), eq.getItemId(), from, bagSlot);
        }
        if (moved > 0) {
            log.info("migrateSoulFashionRing: char {} kept {} removed {}",
                    chr.getId(), keep.getItemId(), moved);
        }
    }
}
