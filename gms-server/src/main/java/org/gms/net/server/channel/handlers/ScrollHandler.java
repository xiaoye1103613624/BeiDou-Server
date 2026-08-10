/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Equip.ScrollResult;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ModifyInventory;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.id.ItemId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matze
 * @author Frz
 */
public final class ScrollHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {
        if (c.tryacquireClient()) {
            try {
                p.readInt(); // 读取一个整数，但未使用
                short scrollSlot = p.readShort(); // 读取卷轴所在的槽位
                short equipSlot = p.readShort(); // 读取装备所在的槽位
                byte ws = (byte) p.readShort(); // 读取一些标志位
                boolean whiteScroll = false; // 是否使用白色卷轴
                boolean legendarySpirit = false; // 是否使用传奇精神技能

                if ((ws & 2) == 2) {
                    whiteScroll = true;
                }

                ItemInformationProvider ii = ItemInformationProvider.getInstance(); // 获取物品信息提供者实例
                Character chr = c.getPlayer(); // 获取当前玩家
                // Addon −54…−62 ↔ −154…−162：绿客户端可能发 UI 座，物品在 cash 镜像座
                if (equipSlot < 0) {
                    equipSlot = org.gms.constants.inventory.ExtendedEquipRegistry.resolveEquippedSlotAlias(
                            chr.getInventory(InventoryType.EQUIPPED), equipSlot);
                }
                Equip toScroll = (Equip) chr.getInventory(InventoryType.EQUIPPED).getItem(equipSlot);
                Skill LegendarySpirit = SkillFactory.getSkill(1003); // 获取传奇精神技能
                if (chr.getSkillLevel(LegendarySpirit) > 0 && equipSlot >= 0) {
                    legendarySpirit = true;
                    toScroll = (Equip) chr.getInventory(InventoryType.EQUIP).getItem(equipSlot);
                }

                Inventory useInventory = chr.getInventory(InventoryType.USE); // 获取玩家的使用栏库存
                Item scroll = useInventory.getItem(scrollSlot); // 获取使用的卷轴
                if (scroll == null) {
                    announceCannotScroll(c, legendarySpirit);
                    return;
                }

                // Phase1~4：潜能/Hyper/附加/魔方/品阶/灵魂/星岩卷（不走普通砸卷）
                if (org.gms.potential.PotentialHyperConfig.isPotentialFamilyScroll(scroll.getItemId())) {
                    if (toScroll == null && equipSlot >= 0) {
                        Item invEq = chr.getInventory(InventoryType.EQUIP).getItem(equipSlot);
                        if (invEq instanceof Equip eq) {
                            toScroll = eq;
                            legendarySpirit = true;
                        }
                    }
                    if (toScroll == null) {
                        chr.dropMessage(5, "请把卷拖到已穿戴装备上，或拖到装备栏中的装备。");
                        announceCannotScroll(c, legendarySpirit);
                        return;
                    }
                    handlePotentialOrHyper(c, chr, toScroll, scroll, equipSlot, legendarySpirit, whiteScroll);
                    return;
                }

                if (toScroll == null) {
                    announceCannotScroll(c, legendarySpirit);
                    return;
                }

                byte oldLevel = toScroll.getLevel(); // 记录装备的原始等级
                byte oldSlots = toScroll.getUpgradeSlots(); // 记录装备的原始升级插槽数量
                short oldFlag = toScroll.getFlag();
                Item wscroll = null;

                final boolean protectFamily = ItemConstants.isProtectFamilyScroll(scroll.getItemId());

                if (ItemConstants.isCleanSlate(scroll.getItemId()) && !ii.canUseCleanSlate(toScroll)) {
                    announceCannotScroll(c, legendarySpirit);
                    return;
                } else if (!protectFamily && !ItemConstants.isModifierScroll(scroll.getItemId()) && toScroll.getUpgradeSlots() < 1) {
                    announceCannotScroll(c, legendarySpirit);
                    return;
                }

                // Soft095：特殊保护卷 enhance>=8 不可用（Cash 涂盾另走 UseCashItem 细规则）
                if (protectFamily && toScroll.getEnhance() >= 8) {
                    chr.dropMessage(5, "强化阶段过高，无法使用该保护卷轴。");
                    announceCannotScroll(c, legendarySpirit);
                    return;
                }

                List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
                if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
                    announceCannotScroll(c, legendarySpirit);
                    return;
                }
                if (whiteScroll) {
                    wscroll = useInventory.findById(ItemId.WHITE_SCROLL);
                    if (wscroll == null) {
                        whiteScroll = false;
                    }
                }

                if (!protectFamily && !ItemConstants.isChaosScroll(scroll.getItemId()) && !ItemConstants.isCleanSlate(scroll.getItemId())) {
                    if (!canScroll(scroll.getItemId(), toScroll.getItemId())) {
                        announceCannotScroll(c, legendarySpirit);
                        return;
                    }
                }

                Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll.getItemId(), whiteScroll, 0, chr.isGM());
                ScrollResult scrollSuccess = Equip.ScrollResult.FAIL;
                if (scrolled == null) {
                    // Soft095 SHIELD_WARD：诅咒炸装 → FAIL + 清盾
                    if (ItemConstants.hasFlag(oldFlag, ItemConstants.SHIELD_WARD)) {
                        scrolled = toScroll;
                        scrolled.setFlag(ItemConstants.withoutFlag(oldFlag, ItemConstants.SHIELD_WARD));
                        scrollSuccess = Equip.ScrollResult.FAIL;
                        chr.dropMessage(5, "保护之盾生效，装备未损坏（护盾已消耗）。");
                    } else {
                        scrollSuccess = Equip.ScrollResult.CURSE;
                    }
                } else if (protectFamily) {
                    // 保护卷涂 flag 成功：flag 增加即 SUCCESS
                    if (scrolled.getFlag() != oldFlag) {
                        scrollSuccess = Equip.ScrollResult.SUCCESS;
                    } else {
                        scrollSuccess = Equip.ScrollResult.FAIL;
                        chr.dropMessage(5, "无法对该装备使用此保护卷轴。");
                    }
                } else if (scrolled.getLevel() > oldLevel || (ItemConstants.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() == oldSlots + 1) || ItemConstants.isFlagModifier(scroll.getItemId(), scrolled.getFlag())) {
                    scrollSuccess = Equip.ScrollResult.SUCCESS;
                }

                // Soft095：普通砸卷后消耗 SHIELD_WARD（成功/失败均消耗；保护卷本身不扣盾）
                if (!protectFamily && scrolled != null
                        && ItemConstants.hasFlag(oldFlag, ItemConstants.SHIELD_WARD)
                        && !ItemConstants.isCleanSlate(scroll.getItemId())) {
                    scrolled.setFlag(ItemConstants.withoutFlag(scrolled.getFlag(), ItemConstants.SHIELD_WARD));
                }

                // Soft095 String：SCROLL_PROTECT — 失败时保留卷轴并消耗防护；成功也消耗防护
                boolean keepScroll = false;
                if (!protectFamily
                        && ItemConstants.hasFlag(oldFlag, ItemConstants.SCROLL_PROTECT)
                        && !ItemConstants.isCleanSlate(scroll.getItemId())) {
                    if (scrolled != null) {
                        scrolled.setFlag(ItemConstants.withoutFlag(scrolled.getFlag(), ItemConstants.SCROLL_PROTECT));
                    }
                    if (scrollSuccess == Equip.ScrollResult.FAIL || scrollSuccess == Equip.ScrollResult.CURSE) {
                        keepScroll = true;
                        chr.dropMessage(5, "卷轴防护生效，使用的卷轴没有消失。");
                    }
                }

                if (!protectFamily && scrollSuccess == Equip.ScrollResult.FAIL
                        && scrolled != null && scrolled.getUpgradeSlots() == oldSlots
                        && ItemConstants.hasFlag(oldFlag, ItemConstants.SLOTS_PROTECT)
                        && !ItemConstants.hasFlag(scrolled.getFlag(), ItemConstants.SLOTS_PROTECT)) {
                    chr.dropMessage(5, "由于卷轴效果，升级次数没有减少。");
                }

                useInventory.lockInventory();
                try {
                    if (scroll.getQuantity() < 1) {
                        announceCannotScroll(c, legendarySpirit);
                        return;
                    }

                    if (whiteScroll && !ItemConstants.isCleanSlate(scroll.getItemId())) {
                        if (wscroll.getQuantity() < 1) {
                            announceCannotScroll(c, legendarySpirit);
                            return;
                        }

                        InventoryManipulator.removeFromSlot(c, InventoryType.USE, wscroll.getPosition(), (short) 1, false, false);
                    }

                    if (!keepScroll) {
                        InventoryManipulator.removeFromSlot(c, InventoryType.USE, scroll.getPosition(), (short) 1, false);
                    }
                } finally {
                    useInventory.unlockInventory();
                }

                final List<ModifyInventory> mods = new ArrayList<>();
                if (scrollSuccess == Equip.ScrollResult.CURSE) {
                    if (!ItemId.isWeddingRing(toScroll.getItemId())) {
                        mods.add(new ModifyInventory(3, toScroll));
                        if (equipSlot < 0) {
                            Inventory inv = chr.getInventory(InventoryType.EQUIPPED);

                            inv.lockInventory();
                            try {
                                chr.unequippedItem(toScroll);
                                inv.removeItem(toScroll.getPosition());
                            } finally {
                                inv.unlockInventory();
                            }
                        } else {
                            Inventory inv = chr.getInventory(InventoryType.EQUIP);

                            inv.lockInventory();
                            try {
                                inv.removeItem(toScroll.getPosition());
                            } finally {
                                inv.unlockInventory();
                            }
                        }
                    } else {
                        scrolled = toScroll;
                        scrollSuccess = Equip.ScrollResult.FAIL;

                        mods.add(new ModifyInventory(3, scrolled));
                        mods.add(new ModifyInventory(0, scrolled));
                    }
                } else {
                    mods.add(new ModifyInventory(3, scrolled));
                    mods.add(new ModifyInventory(0, scrolled));
                }
                c.sendPacket(PacketCreator.modifyInventory(true, mods));
                chr.getMap().broadcastMessage(PacketCreator.getScrollEffect(chr.getId(), scrollSuccess, legendarySpirit, whiteScroll));
                if (equipSlot < 0 && (scrollSuccess == Equip.ScrollResult.SUCCESS || scrollSuccess == Equip.ScrollResult.CURSE)) {
                    chr.equipChanged();
                }
            } finally {
                c.releaseClient(); // 释放客户端资源
            }
        }
    }

    private static void handlePotentialOrHyper(Client c, Character chr, Equip equip, Item scroll,
                                               short equipSlot, boolean legendarySpirit) {
        handlePotentialOrHyper(c, chr, equip, scroll, equipSlot, legendarySpirit, false);
    }

    private static void handlePotentialOrHyper(Client c, Character chr, Equip equip, Item scroll,
                                               short equipSlot, boolean legendarySpirit, boolean whiteScroll) {
        org.gms.potential.PotentialHyperService.Result result;
        final int scrollId = scroll.getItemId();
        boolean isHyper = org.gms.potential.PotentialHyperConfig.isHyperScroll(scrollId);
        boolean isBonus = org.gms.potential.PotentialHyperConfig.isBonusPotentialScroll(scrollId);
        // Phase4 精确 ID 落在 20499xx，须先于 /100==20499 的附加潜能卷判断
        boolean isPhase4 = org.gms.potential.PotentialHyperConfig.isCubeOrGradeOrSocketScroll(scrollId)
                || org.gms.potential.PotentialHyperConfig.isSoulScroll(scrollId);
        boolean isReset = org.gms.constants.inventory.ItemConstants.isResetScroll(scrollId);
        boolean isFlame = org.gms.flame.FlameConfig.isFlameItem(scrollId);
        if (isHyper) {
            // Soft095：Hyper 仅认装备上预涂 SHIELD_WARD；ws/白卷不参与防炸
            result = org.gms.potential.PotentialHyperService.applyHyperScroll(
                    chr, equip, scrollId, false, false);
        } else if (org.gms.potential.PotentialHyperConfig.isMagnifyingGlass(scrollId)) {
            result = org.gms.potential.PotentialHyperService.applyMagnify(chr, equip, scrollId);
        } else if (isReset) {
            result = org.gms.potential.EquipResetService.applyResetScroll(chr, equip, scrollId, chr.isGM());
        } else if (isFlame) {
            org.gms.flame.FlameService.Result fr =
                    org.gms.flame.FlameService.applyFlameItem(chr, equip, scrollId, chr.isGM());
            result = switch (fr) {
                case SUCCESS -> org.gms.potential.PotentialHyperService.Result.SUCCESS;
                case FAIL -> org.gms.potential.PotentialHyperService.Result.FAIL;
                default -> org.gms.potential.PotentialHyperService.Result.INVALID;
            };
        } else if (isPhase4) {
            result = org.gms.potential.PotentialHyperService.applyPhase4Scroll(chr, equip, scrollId, chr.isGM());
        } else if (isBonus) {
            result = org.gms.potential.PotentialHyperService.applyBonusPotentialScroll(chr, equip, scrollId, chr.isGM());
        } else {
            result = org.gms.potential.PotentialHyperService.applyPotentialScroll(chr, equip, scrollId, chr.isGM());
        }

        if (result == org.gms.potential.PotentialHyperService.Result.INVALID) {
            announceCannotScroll(c, legendarySpirit);
            return;
        }

        InventoryManipulator.removeFromSlot(c, InventoryType.USE, scroll.getPosition(), (short) 1, false);

        final List<ModifyInventory> mods = new ArrayList<>();
        ScrollResult scrollSuccess;
        if (result == org.gms.potential.PotentialHyperService.Result.CURSE) {
            scrollSuccess = ScrollResult.CURSE;
            mods.add(new ModifyInventory(3, equip));
            if (equipSlot < 0) {
                Inventory inv = chr.getInventory(InventoryType.EQUIPPED);
                inv.lockInventory();
                try {
                    chr.unequippedItem(equip);
                    inv.removeItem(equip.getPosition());
                } finally {
                    inv.unlockInventory();
                }
            } else {
                Inventory inv = chr.getInventory(InventoryType.EQUIP);
                inv.lockInventory();
                try {
                    inv.removeItem(equip.getPosition());
                } finally {
                    inv.unlockInventory();
                }
            }
        } else {
            scrollSuccess = result == org.gms.potential.PotentialHyperService.Result.SUCCESS
                    ? ScrollResult.SUCCESS : ScrollResult.FAIL;
            mods.add(new ModifyInventory(3, equip));
            mods.add(new ModifyInventory(0, equip));
        }
        c.sendPacket(PacketCreator.modifyInventory(true, mods));
        chr.getMap().broadcastMessage(PacketCreator.getScrollEffect(chr.getId(), scrollSuccess, legendarySpirit, false));
        if (equipSlot < 0) {
            chr.equipChanged();
        }
        if (result == org.gms.potential.PotentialHyperService.Result.SUCCESS) {
            String desc = org.gms.potential.PotentialHyperService.describe(equip);
            if (isHyper) {
                chr.dropMessage(5, "【Hyper】成功 → ★" + equip.getEnhance());
            } else if (org.gms.potential.PotentialHyperConfig.isMagnifyingGlass(scrollId)) {
                chr.dropMessage(5, "【放大镜】鉴定成功。 " + desc);
            } else if (isBonus) {
                chr.dropMessage(5, "【附加潜能】附加成功。 " + desc);
            } else if (org.gms.potential.PotentialHyperConfig.isMainCube(scrollId)) {
                // 仅本人聊天可见（dropMessage）；095：隐藏待放大镜
                org.gms.potential.PotentialHyperService.notifyCubeResult(chr, "神奇魔方",
                        cubeResultHint(desc));
            } else if (org.gms.potential.PotentialHyperConfig.isPremiumCube(scrollId)) {
                org.gms.potential.PotentialHyperService.notifyCubeResult(chr, "高级神奇魔方",
                        cubeResultHint(desc));
            } else if (org.gms.potential.PotentialHyperConfig.isSuperCube(scrollId)) {
                org.gms.potential.PotentialHyperService.notifyCubeResult(chr, "超级神奇魔方",
                        cubeResultHint(desc));
            } else if (org.gms.potential.PotentialHyperConfig.isUltimateCube(scrollId)) {
                org.gms.potential.PotentialHyperService.notifyCubeResult(chr, "终极神奇魔方",
                        cubeResultHint(desc));
            } else if (org.gms.potential.PotentialHyperConfig.isWeirdCube(scrollId)) {
                org.gms.potential.PotentialHyperService.notifyCubeResult(chr, "怪异魔方",
                        cubeResultHint(desc));
            } else if (org.gms.potential.PotentialHyperConfig.isBonusCube(scrollId)) {
                chr.dropMessage(5, "【魔方】附加潜能已重随。 " + desc);
            } else if (isFlame) {
                chr.dropMessage(5, "【火花】涅槃火焰已附加。");
                org.gms.server.equipgrowth.EquipGrowthTipManager.pushGrowthTipIfNeeded(chr, equip);
            } else if (isReset) {
                chr.dropMessage(5, "【还原】已清除砸卷属性/Hyper/黄金锤（潜能与白金锤保留）。 " + desc);
            } else if (org.gms.potential.PotentialHyperConfig.isGradeUpgradeScroll(scrollId)) {
                chr.dropMessage(5, "【品阶】提升成功（未鉴定，请用放大镜）。 " + desc);
            } else if (org.gms.potential.PotentialHyperConfig.isSoulClearScroll(scrollId)) {
                chr.dropMessage(5, "【灵魂】已清除。");
            } else if (org.gms.potential.PotentialHyperConfig.isSoulOrbItem(scrollId)) {
                chr.dropMessage(5, "【灵魂】镶嵌成功。 " + desc + " 放技能: !soulskill");
                PacketCreator.broadcastSoulWeaponEffect(chr);
            } else if (org.gms.potential.PotentialHyperConfig.isSoulApplyScroll(scrollId)) {
                chr.dropMessage(5, "【灵魂】开槽成功。可镶 2591000~2591009。 " + desc);
                PacketCreator.broadcastSoulWeaponEffect(chr);
            } else if (org.gms.potential.PotentialHyperConfig.isSocketScroll(scrollId)) {
                chr.dropMessage(5, "【星岩】镶嵌成功。 " + desc);
            } else if (org.gms.potential.PotentialHyperConfig.isPotentialScroll(scrollId)
                    || org.gms.potential.PotentialHyperConfig.isClassicGradePotentialScroll(scrollId)) {
                chr.dropMessage(5, "【潜能】已附加（未鉴定）。请用放大镜（2460000~2460003）鉴定。 " + desc);
            } else {
                chr.dropMessage(5, "【潜能】附加成功。 " + desc);
            }
            if (equipSlot >= 0) {
                chr.dropMessage(5, "提示：强化的是背包中的装备，角色面板属性不会变化；请穿上后再强化，或穿上后重登/换装刷新。");
            }
        } else if (result == org.gms.potential.PotentialHyperService.Result.FAIL) {
            if (isHyper) {
                int rate = org.gms.potential.PotentialHyperConfig.getHyperSuccessRate(
                        scroll.getItemId(), equip.getEnhance());
                boolean hasWard = org.gms.constants.inventory.ItemConstants.hasFlag(
                        equip.getFlag(), org.gms.constants.inventory.ItemConstants.SHIELD_WARD);
                chr.dropMessage(5, "【Hyper】强化失败（当前★" + (equip.getEnhance() & 0xFF)
                        + "，成功率" + rate + "%）。"
                        + (hasWard ? "" : " 防炸请先对装备使用保护之盾（5064000/2531*），不要勾选祝福卷。"));
            } else if (isBonus) {
                chr.dropMessage(5, "【附加潜能】附加失败。");
            } else if (isReset) {
                chr.dropMessage(5, "【还原】失败。");
            } else if (org.gms.potential.PotentialHyperConfig.isGradeUpgradeScroll(scrollId)) {
                chr.dropMessage(5, "【品阶】提升失败。");
            } else if (org.gms.potential.PotentialHyperConfig.isSoulScroll(scrollId)) {
                chr.dropMessage(5, "【灵魂】失败。");
            } else if (org.gms.potential.PotentialHyperConfig.isSocketScroll(scrollId)) {
                chr.dropMessage(5, "【星岩】镶嵌失败。");
            } else if (org.gms.potential.PotentialHyperConfig.isMagnifyingGlass(scrollId)) {
                // applyMagnify 已 dropMessage 具体原因（档位不足等）
            } else if (isPhase4) {
                chr.dropMessage(5, "【魔方】失败。");
            } else {
                chr.dropMessage(5, "【潜能】附加失败。");
            }
        } else if (result == org.gms.potential.PotentialHyperService.Result.CURSE) {
            if (isHyper) {
                chr.dropMessage(5, "【Hyper】强化失败，装备被损毁！"
                        + "防炸请先对装备使用保护之盾（5064000/2531*）。"
                        + "（GM 可用 !potential star 安全设星）");
            } else {
                chr.dropMessage(5, "强化失败，装备被损坏。");
            }
        } else {
            chr.dropMessage(5, "强化失败，装备被损坏。");
        }
    }

    private static String cubeResultHint(String desc) {
        if (org.gms.potential.PotentialHyperConfig.CUBE_RESET_TO_HIDDEN) {
            return "重随成功（未鉴定，请用放大镜 2460000~3）。 " + desc;
        }
        return desc;
    }

    private static void announceCannotScroll(Client c, boolean legendarySpirit) {
        c.sendPacket(PacketCreator.getInventoryFull());

        if (legendarySpirit) {
            // c.sendPacket(PacketCreator.getScrollEffect(c.getPlayer().getId(), Equip.ScrollResult.FAIL, false, false));
            // 上面是原来的，下面三行是新加的，具体原理我也不懂，纯属瞎猫碰到死耗子。
            // 不更新Inventory的话，客户端会假死；legendarySpirit 不改成 true 的话，客户端匠人之魂就不会播放动画，取消和关闭按钮也不能恢复成可点击状态
            // 修复思路及推测结论：直接给身上的装备砸卷，当砸卷次数为0时服务端会发送else里的Inventory封包，而匠人之魂在次数为0时没发这个包（>0时有），所以由此推测 ->
            // 砸卷操作无论装备剩余次数是否为0，客户端都会向服务器发起砸卷请求，在这个过程中客户端会给背包加锁，客户端收到Inventory封包才会解除这个锁，所以原来没有这个封包的时候客户端的锁就解不了，导致假死。
            c.sendPacket(PacketCreator.getScrollEffect(c.getPlayer().getId(), ScrollResult.FAIL, true, false));
            c.getPlayer().message("由于砸卷次数不足或其他原因导致的砸卷失败，本次不消耗卷轴。");
        }
    }

    private static boolean canScroll(int scrollid, int itemid) {
        int sid = scrollid / 100;

        switch (sid) {
            case 20492: //scroll for accessory (pendant, belt, ring)
                return canScroll(ItemId.RING_STR_100_SCROLL, itemid) || canScroll(ItemId.DRAGON_STONE_SCROLL, itemid) ||
                        canScroll(ItemId.BELT_STR_100_SCROLL, itemid);

            default:
                return (scrollid / 100) % 100 == (itemid / 10000) % 100;
        }
    }
}
