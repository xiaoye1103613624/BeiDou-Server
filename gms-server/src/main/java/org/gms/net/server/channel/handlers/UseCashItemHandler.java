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
import org.gms.client.SkillMacro;
import org.gms.client.creator.veteran.BowmanCreator;
import org.gms.client.creator.veteran.MagicianCreator;
import org.gms.client.creator.veteran.PirateCreator;
import org.gms.client.creator.veteran.ThiefCreator;
import org.gms.client.creator.veteran.WarriorCreator;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Equip.ScrollResult;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ModifyInventory;
import org.gms.client.inventory.Pet;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.client.inventory.manipulator.KarmaManipulator;
import org.gms.client.processor.npc.DueyProcessor;
import org.gms.client.processor.stat.AssignAPProcessor;
import org.gms.client.processor.stat.AssignSPProcessor;
import org.gms.config.GameConfig;
import org.gms.constants.game.GameConstants;
import org.gms.manager.ServerManager;
import org.gms.service.PetGrowthService;
import org.gms.constants.id.ItemId;
import org.gms.constants.id.MapId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.packet.out.SendNoteSuccessPacket;
import org.gms.net.server.Server;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ItemInformationProvider;
import org.gms.server.Shop;
import org.gms.server.ShopFactory;
import org.gms.server.StatEffect;
import org.gms.server.TimerManager;
import org.gms.server.beauty.BeautyData;
import org.gms.server.beauty.BeautyPackets;
import org.gms.server.beauty.BeautyStorage;
import org.gms.server.maps.AbstractMapObject;
import org.gms.server.maps.FieldLimit;
import org.gms.server.maps.Kite;
import org.gms.server.maps.Mist;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.MapleTVEffect;
import org.gms.server.maps.PlayerShopItem;
import org.gms.service.NoteService;
import org.gms.util.PacketCreator;
import org.gms.util.Randomizer;
import org.gms.util.Pair;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class UseCashItemHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(UseCashItemHandler.class);

    private final NoteService noteService;

    public UseCashItemHandler(NoteService noteService) {
        this.noteService = noteService;
    }

    @Override
    public void handlePacket(InPacket p, Client c) {
        final Character player = c.getPlayer();

        long timeNow = currentServerTime();
        if (timeNow - player.getLastUsedCashItem() < 3000) {
            player.dropMessage(1, I18nUtil.getMessage("UseCashItemHandler.handlePacket.message9"));
            c.enableActions();
            return;
        }
        player.setLastUsedCashItem(timeNow);

        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        short position = p.readShort(); //使用的道具位置
        int itemId = p.readInt();
        int itemType = itemId / 10000;  //装备类型

        Inventory cashInv = player.getInventory(InventoryType.CASH);
        Item toUse = cashInv.getItem(position);
        if (toUse == null || toUse.getItemId() != itemId) {
            toUse = cashInv.findById(itemId);

            if (toUse == null) {
                c.enableActions();
                return;
            }

            position = toUse.getPosition();
        }

        if (toUse.getQuantity() < 1) {
            c.enableActions();
            return;
        }

        String medal = "";
        Item medalItem = player.getInventory(InventoryType.EQUIPPED).getItem((short) -49);
        if (medalItem != null) {
            medal = "<" + ii.getName(medalItem.getItemId()) + "> ";
        }

        if (itemType == 504) { // vip teleport rock//缩地石
            String error1 = I18nUtil.getMessage("UseCashItemHandler.handlePacket.error1");
            boolean vip = p.readByte() == 1 && itemId / 1000 >= 5041;
            remove(c, position, itemId);
            boolean success = false;
            if (!vip) {
                int mapId = p.readInt();
                if (itemId / 1000 >= 5041 || mapId / 100000000 == player.getMapId() / 100000000) { //check vip or same continent
                    MapleMap targetMap = c.getChannelServer().getMapFactory().getMap(mapId);
                    if (!FieldLimit.CANNOTVIPROCK.check(targetMap.getFieldLimit()) && (targetMap.getForcedReturnId() == MapId.NONE || MapId.isMapleIsland(mapId))) {
                        player.forceChangeMap(targetMap, targetMap.getRandomPlayerSpawnpoint());
                        success = true;
                    } else {
                        player.dropMessage(1, error1);
                    }
                } else {
                    player.dropMessage(1, I18nUtil.getMessage("UseCashItemHandler.handlePacket.message1"));
                }
            } else {
                String name = p.readString();
                Character victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);

                if (victim != null) {
                    MapleMap targetMap = victim.getMap();
                    if (!FieldLimit.CANNOTVIPROCK.check(targetMap.getFieldLimit()) && (targetMap.getForcedReturnId() == MapId.NONE || MapId.isMapleIsland(targetMap.getId()))) {
                        if (!victim.isGM() || victim.gmLevel() <= player.gmLevel()) {   // thanks Yoboes for noticing non-GM's being unreachable through rocks
                            player.forceChangeMap(targetMap, targetMap.findClosestPlayerSpawnpoint(victim.getPosition()));
                            success = true;
                        } else {
                            player.dropMessage(1, error1);
                        }
                    } else {
                        player.dropMessage(1, I18nUtil.getMessage("UseCashItemHandler.handlePacket.message2"));
                    }
                } else {
                    player.dropMessage(1, I18nUtil.getMessage("UseCashItemHandler.handlePacket.message3"));
                }
            }

            if (!success) {
                InventoryManipulator.addById(c, itemId, (short) 1);
                c.enableActions();
            }
        } else if (itemType == 505) { // AP/SP reset //能力、技能点重置卷轴
            if (!player.isAlive()) {
                c.enableActions();
                return;
            }

            if (itemId > ItemId.AP_RESET) {
                int SPTo = p.readInt();
                if (!AssignSPProcessor.canSPAssign(c, SPTo)) {  // exploit found thanks to Arnah
                    return;
                }

                int SPFrom = p.readInt();
                Skill skillSPTo = SkillFactory.getSkill(SPTo);
                Skill skillSPFrom = SkillFactory.getSkill(SPFrom);
                // SP 重置卷：只动原始等级，且不超过 spMaxLevel
                byte curLevel = player.getSkillLevelRaw(skillSPTo);
                byte curLevelSPFrom = player.getSkillLevelRaw(skillSPFrom);
                if ((curLevel < skillSPTo.getSpMaxLevel()) && curLevelSPFrom > 0) {
                    player.changeSkillLevel(skillSPFrom, (byte) (curLevelSPFrom - 1), player.getMasterLevel(skillSPFrom), -1);
                    player.changeSkillLevel(skillSPTo, (byte) (curLevel + 1), player.getMasterLevel(skillSPTo), -1);

                    // update macros, thanks to Arnah
                    if ((curLevelSPFrom - 1) == 0) {
                        boolean updated = false;
                        for (SkillMacro macro : player.getSkillMacros()) {
                            if (macro == null) {
                                continue;
                            }

                            boolean update = false;// cleaner?
                            if (macro.getSkill1() == SPFrom) {
                                update = true;
                                macro.setSkill1(0);
                            }
                            if (macro.getSkill2() == SPFrom) {
                                update = true;
                                macro.setSkill2(0);
                            }
                            if (macro.getSkill3() == SPFrom) {
                                update = true;
                                macro.setSkill3(0);
                            }
                            if (update) {
                                updated = true;
                                player.updateMacros(macro.getPosition(), macro);
                            }
                        }
                        if (updated) {
                            player.sendMacros();
                        }
                    }
                }
            } else {
                int APTo = p.readInt();
                int APFrom = p.readInt();

                if (!AssignAPProcessor.APResetAction(c, APFrom, APTo)) {
                    return;
                }
            }
            remove(c, position, itemId);
        } else if (itemType == 506) {//操作道具的现金物品、取名、封印、孵化、魔方
            // Phase9：官方 Cash 魔方（5062000/01/02/2100）— 对齐 095 UseCashItem
            if (org.gms.potential.PotentialHyperConfig.isCashCube(itemId)) {
                handleCashCube(c, player, position, itemId, p);
                return;
            }
            // Soft095 防-x Cash：预涂 SHIELD_WARD / SLOTS_PROTECT / SCROLL_PROTECT / LUCKS_KEY
            if (itemId == 5064000 || itemId == 5064003
                    || itemId == 5064100 || itemId == 5064101
                    || itemId == 5064300
                    || itemId == 5063000 || itemId == 5063100
                    || itemId == 5068000 || itemId == 5068100 || itemId == 5068200) {
                handleProtectCashItem(c, player, position, itemId, p);
                return;
            }
            Item eq = null;
            if (itemId == 5060000) { // Item tag.
                int equipSlot = p.readShort();
                if (equipSlot == 0) {
                    return;
                }
                short eqPos = (short) equipSlot;
                if (eqPos < 0) {
                    eqPos = org.gms.constants.inventory.ExtendedEquipRegistry.resolveEquippedSlotAlias(
                            player.getInventory(InventoryType.EQUIPPED), eqPos);
                }
                eq = player.getInventory(InventoryType.EQUIPPED).getItem(eqPos);
                eq.setOwner(player.getName());
            } else if (itemId == 5060001 || itemId == 5061000 || itemId == 5061001 || itemId == 5061002 || itemId == 5061003) { // Sealing lock
                InventoryType type = InventoryType.getByType((byte) p.readInt());
                eq = player.getInventory(type).getItem((short) p.readInt());
                if (eq == null) { //Check if the type is EQUIPMENT?
                    return;
                }
                short flag = eq.getFlag();
                if (eq.getExpiration() > -1 && (eq.getFlag() & ItemConstants.LOCK) != ItemConstants.LOCK) {
                    return; //No perma items pls
                }
                flag |= ItemConstants.LOCK;
                eq.setFlag(flag);

                long period = 0;
                if (itemId == 5061000) {
                    period = 7;
                } else if (itemId == 5061001) {
                    period = 30;
                } else if (itemId == 5061002) {
                    period = 90;
                } else if (itemId == 5061003) {
                    period = 365;
                }

                if (period > 0) {
                    long expiration = eq.getExpiration() > -1 ? eq.getExpiration() : currentServerTime();
                    eq.setExpiration(expiration + DAYS.toMillis(period));
                }

                // double-remove found thanks to BHB
            } else if (itemId == 5060002) { // Incubator
                byte inventory2 = (byte) p.readInt();
                short slot2 = (short) p.readInt();
                Item item2 = player.getInventory(InventoryType.getByType(inventory2)).getItem(slot2);
                if (item2 == null) // hacking
                {
                    return;
                }
                if (getIncubatedItem(c, itemId)) {
                    InventoryManipulator.removeFromSlot(c, InventoryType.getByType(inventory2), slot2, (short) 1, false);
                    remove(c, position, itemId);
                }
                return;
            }
            p.readInt(); // time stamp
            if (eq != null) {
                player.forceUpdateItem(eq);
                remove(c, position, itemId);
            }
        } else if (itemType == 507) {   //喇叭
            boolean whisper;
            switch ((itemId / 1000) % 10) {
                case 1: // Megaphone
                    if (player.getLevel() > 9) {
                        player.getClient().getChannelServer().broadcastPacket(PacketCreator.serverNotice(2, medal + player.getName() + " : " + p.readString()));
                    } else {
                        player.dropMessage(1, I18nUtil.getMessage("UseCashItemHandler.handlePacket.message4"));
                        return;
                    }
                    break;
                case 2: // Super megaphone
                    Server.getInstance().broadcastMessage(c.getWorld(), PacketCreator.serverNotice(3, c.getChannel(), medal + player.getName() + " : " + p.readString(), (p.readByte() != 0)));
                    break;
                case 5: // Maple TV
                    int tvType = itemId % 10;
                    boolean megassenger = false;
                    boolean ear = false;
                    Character victim = null;
                    if (tvType != 1) {
                        if (tvType >= 3) {
                            megassenger = true;
                            if (tvType == 3) {
                                p.readByte();
                            }
                            ear = 1 == p.readByte();
                        } else if (tvType != 2) {
                            p.readByte();
                        }
                        if (tvType != 4) {
                            victim = c.getChannelServer().getPlayerStorage().getCharacterByName(p.readString());
                        }
                    }
                    List<String> messages = new LinkedList<>();
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < 5; i++) {
                        String message = p.readString();
                        if (megassenger) {
                            builder.append(" ").append(message);
                        }
                        messages.add(message);
                    }
                    p.readInt();

                    if (!MapleTVEffect.broadcastMapleTVIfNotActive(player, victim, messages, tvType)) {
                        player.dropMessage(1, I18nUtil.getMessage("UseCashItemHandler.handlePacket.message5"));
                        return;
                    }

                    if (megassenger) {
                        Server.getInstance().broadcastMessage(c.getWorld(), PacketCreator.serverNotice(3, c.getChannel(), medal + player.getName() + " : " + builder, ear));
                    }

                    break;
                case 6: //item megaphone
                    String msg = medal + player.getName() + " : " + p.readString();
                    whisper = p.readByte() == 1;
                    Item item = null;
                    if (p.readByte() == 1) { //item
                        item = player.getInventory(InventoryType.getByType((byte) p.readInt())).getItem((short) p.readInt());
                        if (item == null) //hack
                        {
                            return;
                        }

                        // thanks Conrad for noticing that untradeable items should be allowed in megas
                    }
                    Server.getInstance().broadcastMessage(c.getWorld(), PacketCreator.itemMegaphone(msg, whisper, c.getChannel(), item));
                    break;
                case 7: //triple megaphone
                    int lines = p.readByte();
                    if (lines < 1 || lines > 3) //hack
                    {
                        return;
                    }
                    String[] msg2 = new String[lines];
                    for (int i = 0; i < lines; i++) {
                        msg2[i] = medal + player.getName() + " : " + p.readString();
                    }
                    whisper = p.readByte() == 1;
                    Server.getInstance().broadcastMessage(c.getWorld(), PacketCreator.getMultiMegaphone(msg2, c.getChannel(), whisper));
                    break;
            }
            remove(c, position, itemId);
        } else if (itemType == 508) {   //风筝    // thanks tmskdl12 for graduation banner; thanks ratency for first pointing lack of Kite handling
            Kite kite = new Kite(player, p.readString(), itemId);

            if (!GameConstants.isFreeMarketRoom(player.getMapId())) {
                player.getMap().spawnKite(kite);
                remove(c, position, itemId);
            } else {
                c.sendPacket(PacketCreator.sendCannotSpawnKite());
            }
        } else if (itemType == 509) {   //离线消息道具
            String sendTo = p.readString();
            String msg = p.readString();
            try {
                noteService.sendNormal(msg, player.getName(), sendTo);
                remove(c, position, itemId);
                c.sendPacket(new SendNoteSuccessPacket());
            } catch (Exception e) {
                log.error("Error sending note", e);
            }
        } else if (itemType == 510) {//音乐盒
            player.getMap().broadcastMessage(PacketCreator.musicChange("Jukebox/Congratulation"));
            remove(c, position, itemId);
        } else if (itemType == 512) {//场景消息
            if (ii.getStateChangeItem(itemId) != 0) {
                for (Character mChar : player.getMap().getCharacters()) {
                    ii.getItemEffect(ii.getStateChangeItem(itemId)).applyTo(mChar);
                }
            }
            player.getMap().startMapEffect(ii.getMsg(itemId).replaceFirst("%s", player.getName()).replaceFirst("%s", p.readString()), itemId);
            remove(c, position, itemId);
        } else if (itemType == 517) {//宠物取名
            Pet pet = player.getPet(0);
            if (pet == null) {
                c.enableActions();
                return;
            }
            String newName = p.readString();
            pet.setName(newName);
            pet.saveToDb();

            Item item = player.getInventory(InventoryType.CASH).getItem(pet.getPosition());
            if (item != null) {
                player.forceUpdateItem(item);
            }

            player.getMap().broadcastMessage(player, PacketCreator.changePetName(player, newName, (byte)0), true);
            c.enableActions();
            remove(c, position, itemId);
        } else if (itemType == 520) {//钱袋子
            player.gainMeso(ii.getMeso(itemId), true, false, true);
            remove(c, position, itemId);
            c.enableActions();
        } else if (itemType == 523) {//猫头鹰商店搜索器
            int itemid = p.readInt();

            if (!GameConfig.getServerBoolean("use_enforce_item_suggestion")) {
                c.getWorldServer().addOwlItemSearch(itemid);
            }
            player.setOwlSearch(itemid);
            List<Pair<PlayerShopItem, AbstractMapObject>> hmsAvailable = c.getWorldServer().getAvailableItemBundles(itemid);
            if (!hmsAvailable.isEmpty()) {
                remove(c, position, itemId);
            }

            c.sendPacket(PacketCreator.owlOfMinerva(c, itemid, hmsAvailable));
            c.enableActions();

        } else if (itemType == 524) {//宠物食品
            boolean isUse = false;
            for (byte i = 0; i < 3; i++) {
                Pet pet = player.getPet(i);
                if (pet != null) {
                    Pair<Integer, Boolean> pair = pet.canConsume(itemId);
                    if (pair.getRight()) {
                        isUse = true;
                        pet.gainTamenessFullness(player, pair.getLeft(), 100, 1, true);
                        remove(c, position, itemId);
                        try {
                            var ctx = ServerManager.getApplicationContext();
                            if (ctx != null) {
                                ctx.getBean(PetGrowthService.class).onPetFed(player, pet, itemId);
                            }
                        } catch (Exception ignored) {
                        }
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!isUse)
                player.dropMessage(1, I18nUtil.getMessage("UseCashItemHandler.handlePacket.message10")); //所有宠物都不匹配时弹出提示。
            c.enableActions();
        } else if (itemType == 528) {//臭屁和花香，改变周围角色表情
    //
            //以下修复不完美。
            if (itemId == 5281000) {//原理为在角色面前释放一个小型绿色雾气
                Rectangle bounds = new Rectangle((int) player.getPosition().getX(), (int) player.getPosition().getY(), 1, 1);
                StatEffect mse = new StatEffect();
                mse.setSourceId(2111003);
                Mist mist = new Mist(bounds, player, mse);
                player.getMap().spawnMist(mist, 10000, false, true,false);
    //                player.getMap().broadcastMessage(PacketCreator.getChatText(player.getId(), "Oh no, I farted!", false, 1));
                int emote = 8;   //设定表情为呕吐表情
                for (Character mChr : player.getMap().getCharacters()) {//循环当前地图的角色
                    if (mChr.isLoggedInWorld()) {
                        mChr.getMap().broadcastMessage(PacketCreator.facialExpression(mChr, emote));//向所有符合条件的角色发送更改表情的封包
                        mChr.changeFaceExpression(8);
                    }
                }
                } else {
                    notEnabled(player);
                }

        } else if (itemType == 529) {//家族表情留言板
            notEnabled(player);
        } else if (itemType == 530) {//变身石
            ii.getItemEffect(itemId).applyTo(player);
            remove(c, position, itemId);
        } else if (itemType == 533) {//特快使用券
            DueyProcessor.dueySendTalk(c, true);
        } else if (itemType == 537) {//黑板
            if (GameConstants.isFreeMarketRoom(player.getMapId())) {
                player.dropMessage(5, I18nUtil.getMessage("UseCashItemHandler.handlePacket.message6"));
                player.enableActions();
                return;
            }

            player.setChalkboard(p.readString());
            player.getMap().broadcastMessage(PacketCreator.useChalkboard(player, false));
            player.enableActions();
            //remove(c, position, itemId);  thanks Conrad for noticing chalkboards shouldn't be depleted upon use
        } else if (itemType == 539) {//情景喇叭
            List<String> strLines = new LinkedList<>();
            for (int i = 0; i < 4; i++) {
                strLines.add(p.readString());
            }

            final int world = c.getWorld();
            Server.getInstance().broadcastMessage(world, PacketCreator.getAvatarMega(player, medal, c.getChannel(), itemId, strLines, (p.readByte() != 0)));
            TimerManager.getInstance().schedule(() -> Server.getInstance().broadcastMessage(world, PacketCreator.byeAvatarMega()), SECONDS.toMillis(10));
            remove(c, position, itemId);
        } else if (itemType == 540) {//改名卡和换区卡
            p.readByte();
            p.readInt();
            if (itemId == ItemId.NAME_CHANGE) {//改名卡
                c.sendPacket(PacketCreator.showNameChangeCancel(player.cancelPendingNameChange()));
            } else if (itemId == ItemId.WORLD_TRANSFER) {//换区卡
                c.sendPacket(PacketCreator.showWorldTransferCancel(player.cancelPendingWorldTransfer()));
            }
            remove(c, position, itemId);
            c.enableActions();
        } else if (itemType == 543) {//角色卡
            if (itemId == ItemId.MAPLE_LIFE_B && !c.gainCharacterSlot()) {
                player.dropMessage(1, I18nUtil.getMessage("UseCashItemHandler.handlePacket.message7"));
                c.enableActions();
                return;
            }

            String name = p.readString();
            int face = p.readInt();
            int hair = p.readInt();
            int haircolor = p.readInt();
            int skin = p.readInt();
            int gender = p.readInt();
            int jobid = p.readInt();
            int improveSp = p.readInt();
            if (ItemConstants.notValidHairColor(haircolor)) {
                log.warn("{} want to create a character with a not valid hair color {}", player.getName(), haircolor);
                c.enableActions();
                return;
            }

            int createStatus = switch (jobid) {
                case 0 -> WarriorCreator.createCharacter(c, name, face, hair + haircolor, skin, gender, improveSp);
                case 1 -> MagicianCreator.createCharacter(c, name, face, hair + haircolor, skin, gender, improveSp);
                case 2 -> BowmanCreator.createCharacter(c, name, face, hair + haircolor, skin, gender, improveSp);
                case 3 -> ThiefCreator.createCharacter(c, name, face, hair + haircolor, skin, gender, improveSp);
                default -> PirateCreator.createCharacter(c, name, face, hair + haircolor, skin, gender, improveSp);
            };

            if (createStatus == 0) {
                c.sendPacket(PacketCreator.sendMapleLifeError(0));   // success!

                player.showHint(I18nUtil.getMessage("UseCashItemHandler.handlePacket.message8"));
                remove(c, position, itemId);
            } else {
                if (createStatus == -1) {    // check name
                    c.sendPacket(PacketCreator.sendMapleLifeNameError());
                } else {
                    c.sendPacket(PacketCreator.sendMapleLifeError(-1 * createStatus));
                }
            }
        } else if (itemType == 545) { // MiuMiu's travel store  //包裹商人妙妙
            if (player.getShop() == null) {
                Shop shop = ShopFactory.getInstance().getShop(1338);
                if (shop != null) {
                    shop.sendShop(c);
                    remove(c, position, itemId);
                }
            } else {
                c.enableActions();
            }
        } else if (itemType == 550) { //Extend item expiration  //魔法沙漏 ，延长道具使用时长
            /*
            修复流程：
            1、读取传入的装备槽位
            2、判断是否现金装备，是否允许延长现金装备
            3、获取指定槽位的装备是否存在并判断是否为时限装备
            4、判断增加后时限是否超过指定天数
            5、修改有效时间
            6、移除魔法沙漏
             */
            boolean useCashEquip = false;       //是否允许对现金装备进行延期，需要客户端配合插件才能支持

            short itemSlot = p.readShort(); // 读取装备所在的槽位
            if (itemSlot < 0) {
                itemSlot = org.gms.constants.inventory.ExtendedEquipRegistry.resolveEquippedSlotAlias(
                        player.getInventory(InventoryType.EQUIPPED), itemSlot);
            }

            Item equip = player.getInventory(InventoryType.EQUIPPED).getItem(itemSlot);// 获取指定槽位的装备
            ItemInformationProvider.ItemCashInfo itemHourglass = ii.getItemCashInfo(itemId);        // 获取魔法沙漏的增加时间和天数上限

            if (itemSlot >= 0 || equip == null || equip.getExpiration() <= 0) { //判断是否为身上的装备以及装备是否存在时限
                player.dropMessage(1,I18nUtil.getMessage("UseCashItemHandler.handlePacket.message11"));
                c.enableActions();    //释放客户端锁，解除假死
                return;
            } else if (ii.isCash(equip.getItemId()) && !useCashEquip) { //判断是否为现金装备 以及 是否允许增加现金装备时限，需要客户端配合插件才能支持
                player.dropMessage(1,I18nUtil.getMessage("UseCashItemHandler.handlePacket.message12"));
                c.enableActions();    //释放客户端锁，解除假死
                return;
            } else if (itemHourglass == null) { //如果获取到的道具为空则表示不是xml文件里的魔法沙漏，如自定义添加的沙漏需要同步服务端xml
                notEnabled(player); // 如果不是预期的itemId，直接返回
                return;
            } else if (itemHourglass.addTime <= 0) {    //如果沙漏没有提供增加的时间则返回
                player.dropMessage(1,I18nUtil.getMessage("UseCashItemHandler.handlePacket.message13"));
                c.enableActions();    //释放客户端锁，解除假死
                return;
            }

            long addTime = itemHourglass.addTime * 1000;    //增加的毫秒数，wz的值为秒数，因此需要转为毫秒
            short addDay = (short) (itemHourglass.addTime / 60 / 60 / 24);         //增加的天数
            if (itemHourglass.maxDays > 0 && (equip.getExpiration() + addTime) > (System.currentTimeMillis() + (itemHourglass.maxDays * 24 * 60 * 60 * 1000L))) {  //如果天数大于0则计算两个时间戳的间隔时间增加后大于指定天数，则返回
                player.dropMessage(1,I18nUtil.getMessage("UseCashItemHandler.handlePacket.message14",itemHourglass.maxDays));
                c.enableActions();    //释放客户端锁，解除假死
                return;
            }
            equip.setExpiration(equip.getExpiration() + addTime); //给装备加上指定时间
            player.forceUpdateItem(equip);      //强制刷新装备状态属性
            remove(c, position, itemId); // 移除指定位置的物品

            player.dropMessage(5,I18nUtil.getMessage("UseCashItemHandler.handlePacket.message15",ii.getName(equip.getItemId()),addDay));
        } else if (itemType == 552) {
            InventoryType type = InventoryType.getByType((byte) p.readInt());
            short slot = (short) p.readInt();
            Item item = player.getInventory(type).getItem(slot);
            if (item == null || item.getQuantity() <= 0 || KarmaManipulator.hasKarmaFlag(item) || !ii.isKarmaAble(item.getItemId())) {
                c.enableActions();
                return;
            }

            KarmaManipulator.setKarmaFlag(item);
            player.forceUpdateItem(item);
            remove(c, position, itemId);
            c.enableActions();
        } else if (itemType == 552) { //DS EGG THING    //宿命剪刀
            c.enableActions();
        } else if (itemType == 557) {//金锤子 / 白金锤
            p.readInt();
            int itemSlot = p.readInt();
            p.readInt();
            final Equip equip = (Equip) player.getInventory(InventoryType.EQUIP).getItem((short) itemSlot);
            if (equip == null) {
                c.enableActions();
                return;
            }
            if (itemId == ItemId.PLATINUM_HAMMER) {
                if (equip.getPlatinum() >= 5
                        || player.getInventory(InventoryType.CASH).findById(ItemId.PLATINUM_HAMMER) == null) {
                    player.dropMessage(5, "白金锤最多使用5次，或背包无白金锤。");
                    c.enableActions();
                    return;
                }
                int rate = Math.max(20, 100 - equip.getPlatinum() * 20);
                if (Randomizer.nextInt(100) >= rate) {
                    remove(c, position, itemId);
                    player.dropMessage(5, "【白金锤】强化失败（成功率" + rate + "%），未留下可修复次数。");
                    c.enableActions();
                    return;
                }
                equip.setPlatinum((byte) (equip.getPlatinum() + 1));
                equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() + 1));
                remove(c, position, itemId);
                c.enableActions();
                c.sendPacket(PacketCreator.sendHammerData(equip.getPlatinum()));
                player.forceUpdateItem(equip);
                player.dropMessage(5, "【白金锤】成功 → 永久升级次数+" + equip.getPlatinum() + "/5");
                return;
            }
            if (equip.getVicious() >= 2 || player.getInventory(InventoryType.CASH).findById(ItemId.VICIOUS_HAMMER) == null) {
                c.enableActions();
                return;
            }
            equip.setVicious(equip.getVicious() + 1);
            equip.setUpgradeSlots(equip.getUpgradeSlots() + 1);
            remove(c, position, itemId);
            c.enableActions();
            c.sendPacket(PacketCreator.sendHammerData(equip.getVicious()));
            player.forceUpdateItem(equip);
        } else if (itemType == 591) { // 伤害皮肤选择器（5910000，不消耗）
            log.info("chr {} opened damage skin picker via item {} slot {}",
                    player.getId(), itemId, position);
            c.sendPacket(PacketCreator.damageSkinCatalog());
            c.sendPacket(PacketCreator.damageSkinInventory(player));
            c.enableActions();
        } else if (itemType == 592) { // 美容院栏位解锁券（5920000）
            int unlocked = BeautyStorage.getUnlockedSlots(player.getId());
            if (unlocked < 6) {
                remove(c, position, itemId);
                BeautyStorage.setUnlockedSlots(player.getId(), unlocked + 1);
                player.dropMessage(5, "美容院栏位已解锁 (" + (unlocked + 1) + "/6)");
            }
            c.sendPacket(BeautyPackets.beautyData(
                    BeautyStorage.getUnlockedSlots(player.getId()),
                    BeautyStorage.loadAll(player.getId())));
            c.enableActions();
        } else if (itemType == 590) { // 融合外观锻造锤（5900000）
            short skinPos = p.readShort();
            short basePos = p.readShort();

            Item baseItem = player.getInventory(
                    basePos < 0 ? InventoryType.EQUIPPED : InventoryType.EQUIP).getItem(basePos);
            Item skinItem = player.getInventory(
                    skinPos < 0 ? InventoryType.EQUIPPED : InventoryType.EQUIP).getItem(skinPos);

            if (!(baseItem instanceof Equip) || !(skinItem instanceof Equip)) {
                player.dropMessage(1, "两件物品都必须是装备。");
                c.enableActions();
                return;
            }
            Equip baseEquip = (Equip) baseItem;
            Equip skinEquip = (Equip) skinItem;

            if (baseEquip.getItemId() == skinEquip.getItemId()) {
                player.dropMessage(1, "两件装备不能相同。");
                c.enableActions();
                return;
            }
            if (baseEquip.getItemId() / 10000 != skinEquip.getItemId() / 10000) {
                player.dropMessage(1, "两件装备必须是同类型。");
                c.enableActions();
                return;
            }
            if (skinPos < 0) {
                player.dropMessage(1, "请先将外观源装备放入装备栏（不要穿戴）。");
                c.enableActions();
                return;
            }

            baseEquip.setAnvilItemId(skinEquip.getItemId());

            List<ModifyInventory> mods = new ArrayList<>();
            mods.add(new ModifyInventory(3, baseEquip));
            mods.add(new ModifyInventory(0, baseEquip));
            c.sendPacket(PacketCreator.modifyInventory(true, mods));
            player.equipChanged();

            InventoryManipulator.removeFromSlot(c, InventoryType.EQUIP, skinPos, (short) 1, false);
            remove(c, position, itemId);
            player.saveCharToDB(true);
            c.enableActions();
        } else if (itemType == 561) { //VEGA'S SPELL
            if (p.readInt() != 1) {
                return;
            }

            final byte eSlot = (byte) p.readInt();
            final Item eitem = player.getInventory(InventoryType.EQUIP).getItem(eSlot);

            if (p.readInt() != 2) {
                return;
            }

            final byte uSlot = (byte) p.readInt();
            final Item uitem = player.getInventory(InventoryType.USE).getItem(uSlot);
            if (eitem == null || uitem == null) {
                return;
            }

            Equip toScroll = (Equip) eitem;
            if (toScroll.getUpgradeSlots() < 1) {
                c.sendPacket(PacketCreator.getInventoryFull());
                return;
            }

            //should have a check here against PE hacks
            if (itemId / 1000000 != 5) {
                itemId = 0;
            }

            player.toggleBlockCashShop();

            final int curlevel = toScroll.getLevel();
            c.sendPacket(PacketCreator.sendVegaScroll(0x40));

            final Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, uitem.getItemId(), false, itemId, player.isGM());
            c.sendPacket(PacketCreator.sendVegaScroll(scrolled.getLevel() > curlevel ? 0x41 : 0x43));
            //opcodes 0x42, 0x44: "this item cannot be used"; 0x39, 0x45: crashes

            InventoryManipulator.removeFromSlot(c, InventoryType.USE, uSlot, (short) 1, false);
            remove(c, position, itemId);

            final Client client = c;
            TimerManager.getInstance().schedule(() -> {
                if (!player.isLoggedIn()) {
                    return;
                }

                player.toggleBlockCashShop();

                final List<ModifyInventory> mods = new ArrayList<>();
                mods.add(new ModifyInventory(3, scrolled));
                mods.add(new ModifyInventory(0, scrolled));
                client.sendPacket(PacketCreator.modifyInventory(true, mods));

                ScrollResult scrollResult = scrolled.getLevel() > curlevel ? ScrollResult.SUCCESS : ScrollResult.FAIL;
                player.getMap().broadcastMessage(PacketCreator.getScrollEffect(player.getId(), scrollResult, false, false));
                if (eSlot < 0 && (scrollResult == ScrollResult.SUCCESS)) {
                    player.equipChanged();
                }

                client.enableActions();
            }, SECONDS.toMillis(3));
        } else {
            log.warn("NEW CASH ITEM TYPE: {}, packet: {}", itemType, p);
            c.enableActions();
        }
        c.enableActions();
    }

    /**
     * Soft095 防-x Cash 预涂：拖到目标装备上。
     * 5064000/03 → SHIELD_WARD；5064100/01/8100 → SLOTS_PROTECT；
     * 5064300/8200 → SCROLL_PROTECT；5063000/3100/8000 → LUCKS_KEY（3100 兼 SHIELD_WARD）。
     */
    private static void handleProtectCashItem(Client c, Character player, short cashPos, int itemId, InPacket p) {
        InventoryType type;
        short slot;
        try {
            type = InventoryType.getByType((byte) p.readInt());
            slot = (short) p.readInt();
        } catch (Exception e) {
            player.dropMessage(5, "请把保护道具拖到目标装备上使用。");
            c.enableActions();
            return;
        }
        if (slot < 0) {
            slot = org.gms.constants.inventory.ExtendedEquipRegistry.resolveEquippedSlotAlias(
                    player.getInventory(InventoryType.EQUIPPED), slot);
            type = InventoryType.EQUIPPED;
        }
        Item raw = player.getInventory(type).getItem(slot);
        if (!(raw instanceof Equip equip)) {
            player.dropMessage(5, "只能对装备使用该保护道具。");
            c.enableActions();
            return;
        }

        if (!org.gms.potential.PotentialHyperService.applyProtectCashFlag(player, equip, itemId)) {
            c.enableActions();
            return;
        }
        player.forceUpdateItem(equip);
        remove(c, cashPos, itemId);
        c.enableActions();
    }

    /**
     * @deprecated 保留旧名；实际走 {@link #handleProtectCashItem}
     */
    private static void handleShieldWard(Client c, Character player, short cashPos, int itemId, InPacket p) {
        handleProtectCashItem(c, player, cashPos, itemId, p);
    }

    /**
     * 官方 Cash 魔方：封包在 itemId 后读装备栏槽位（int），对齐 095 InventoryHandler.UseCashItem。
     * <p>
     * v083 客户端无奇迹魔方「点选装备」UI，双击时常不带有效槽位；因此：
     * <ul>
     *   <li>优先用封包槽位（装备栏 &gt;0 / 已穿戴 &lt;0）</li>
     *   <li>无效时自动选【背包·装备栏】第一件已有潜能的装备</li>
     *   <li>仍无目标则提示：USE 别名拖放 / {@code !potential cube &lt;槽&gt;}</li>
     * </ul>
     */
    private static void handleCashCube(Client c, Character player, short cashPos, int itemId, InPacket p) {
        int minLevel = org.gms.potential.PotentialHyperConfig.isSuperCube(itemId) ? 100 : 10;
        if (player.getLevel() < minLevel) {
            player.dropMessage(1, "等级不足" + minLevel + "，无法使用该魔方。");
            c.enableActions();
            return;
        }
        Equip equip = resolveCashCubeTarget(player, p);
        if (equip == null) {
            player.dropMessage(5, "未找到可用装备。请：①从【现金栏】拖魔方到【背包装备栏】目标装备上；"
                    + "②或把已鉴定潜能的装备放在装备栏后双击魔方（自动选第一件）；"
                    + "③或 !potential cube <装备栏槽位>；"
                    + "④或 !item 2049910/2049916 后从【消耗栏】拖到装备上。"
                    + "魔方成功后需放大镜再次鉴定。");
            c.enableActions();
            return;
        }
        // 095：使用前需 USE 栏有空位放碎片
        if (player.getInventory(InventoryType.USE).isFull()) {
            player.dropMessage(5, "消耗栏已满，请留出空位存放方块碎片。");
            c.enableActions();
            return;
        }
        org.gms.potential.PotentialHyperService.Result result;
        boolean force = player.isGM();
        if (org.gms.potential.PotentialHyperConfig.isMainCube(itemId)) {
            result = org.gms.potential.PotentialHyperService.applyMainCube(player, equip, itemId, force);
        } else if (org.gms.potential.PotentialHyperConfig.isPremiumCube(itemId)) {
            result = org.gms.potential.PotentialHyperService.applyPremiumCube(player, equip, itemId, force);
        } else if (org.gms.potential.PotentialHyperConfig.isSuperCube(itemId)) {
            result = org.gms.potential.PotentialHyperService.applySuperCube(player, equip, itemId, force);
        } else if (org.gms.potential.PotentialHyperConfig.isUltimateCube(itemId)) {
            result = org.gms.potential.PotentialHyperService.applyUltimateCube(player, equip, itemId, force);
        } else if (org.gms.potential.PotentialHyperConfig.isWeirdCube(itemId)) {
            result = org.gms.potential.PotentialHyperService.applyWeirdCube(player, equip, itemId, force);
        } else if (org.gms.potential.PotentialHyperConfig.isBonusCube(itemId)) {
            result = org.gms.potential.PotentialHyperService.applyBonusCube(player, equip, itemId, force);
        } else {
            c.enableActions();
            return;
        }
        if (result == org.gms.potential.PotentialHyperService.Result.INVALID
                || result == org.gms.potential.PotentialHyperService.Result.FAIL) {
            c.enableActions();
            return;
        }
        remove(c, cashPos, itemId);
        List<ModifyInventory> mods = new ArrayList<>();
        mods.add(new ModifyInventory(3, equip));
        mods.add(new ModifyInventory(0, equip));
        c.sendPacket(PacketCreator.modifyInventory(true, mods));
        // 碎片
        int frag = org.gms.potential.PotentialHyperConfig.isSuperCube(itemId)
                || org.gms.potential.PotentialHyperConfig.isUltimateCube(itemId)
                ? org.gms.potential.PotentialHyperConfig.ITEM_SUPER_CUBE_FRAGMENT
                : org.gms.potential.PotentialHyperConfig.ITEM_CUBE_FRAGMENT;
        InventoryManipulator.addById(c, frag, (short) 1);
        String tag;
        if (org.gms.potential.PotentialHyperConfig.isUltimateCube(itemId)) {
            tag = "终极神奇魔方";
        } else if (org.gms.potential.PotentialHyperConfig.isWeirdCube(itemId)) {
            tag = "怪异魔方";
        } else if (org.gms.potential.PotentialHyperConfig.isBonusCube(itemId)) {
            tag = "大师附加神奇魔方";
        } else if (org.gms.potential.PotentialHyperConfig.isSuperCube(itemId)) {
            tag = "超级神奇魔方";
        } else if (org.gms.potential.PotentialHyperConfig.isPremiumCube(itemId)) {
            tag = "高级神奇魔方";
        } else {
            tag = "神奇魔方";
        }
        String where = equip.getPosition() < 0 ? "已穿戴" : ("装备栏第" + equip.getPosition() + "格");
        org.gms.potential.PotentialHyperService.notifyCubeResult(player, tag,
                (org.gms.potential.PotentialHyperConfig.CUBE_RESET_TO_HIDDEN
                        ? "重随成功（" + where + "，未鉴定，请用放大镜）。 "
                        : "重随成功（" + where + "）。 ")
                        + org.gms.potential.PotentialHyperService.describe(equip));
        org.gms.potential.PotentialHyperService.sendMiracleCubeResultWindow(player, equip, itemId, true);
        c.enableActions();
    }

    /**
     * 解析 Cash 魔方目标装备：封包槽位 → 装备栏/已穿戴；无效则自动选装备栏第一件有潜能的。
     */
    private static Equip resolveCashCubeTarget(Character player, InPacket p) {
        int eqSlot = 0;
        if (p.available() >= 4) {
            eqSlot = p.readInt();
        } else if (p.available() >= 2) {
            eqSlot = p.readShort();
        }
        if (eqSlot < 0) {
            short resolved = org.gms.constants.inventory.ExtendedEquipRegistry.resolveEquippedSlotAlias(
                    player.getInventory(InventoryType.EQUIPPED), (short) eqSlot);
            Item worn = player.getInventory(InventoryType.EQUIPPED).getItem(resolved);
            if (worn instanceof Equip equip) {
                return equip;
            }
        } else if (eqSlot > 0) {
            Item bag = player.getInventory(InventoryType.EQUIP).getItem((short) eqSlot);
            if (bag instanceof Equip equip) {
                return equip;
            }
        }
        // v083 双击常无槽位：自动选【背包装备栏】第一件已有潜能的装备
        Equip auto = null;
        short bestPos = Short.MAX_VALUE;
        for (Item it : player.getInventory(InventoryType.EQUIP).list()) {
            if (!(it instanceof Equip e)) {
                continue;
            }
            if (e.getPotentialGrade() <= 0 && e.getPotential1() <= 0) {
                continue;
            }
            if (e.getPosition() > 0 && e.getPosition() < bestPos) {
                bestPos = e.getPosition();
                auto = e;
            }
        }
        if (auto != null) {
            player.dropMessage(5, "083无选装UI：已自动选择装备栏第" + auto.getPosition()
                    + "格。指定其它装备请拖魔方到该装备上，或 !potential cube <槽>。");
        }
        return auto;
    }

    private static void remove(Client c, short position, int itemid) {
        Inventory cashInv = c.getPlayer().getInventory(InventoryType.CASH); // 获取玩家的现金库存
        cashInv.lockInventory(); // 锁定现金库存，防止并发修改
        try {
            Item it = cashInv.getItem(position); // 获取指定位置的物品
            if (it == null || it.getItemId() != itemid) { // 如果指定位置的物品为空或ID不匹配
                it = cashInv.findById(itemid); // 通过物品ID查找物品
                if (it != null) { // 如果找到物品
                    position = it.getPosition(); // 更新位置为找到物品的位置
                }
            }

            InventoryManipulator.removeFromSlot(c, InventoryType.CASH, position, (short) 1, true, false); // 从指定位置移除一个物品
        } finally {
            cashInv.unlockInventory(); // 解锁现金库存
        }
    }

    private static boolean getIncubatedItem(Client c, int id) {
        final int[] ids = {1012070, 1302049, 1302063, 1322027, 2000004, 2000005, 2020013, 2020015, 2040307, 2040509, 2040519, 2040521, 2040533, 2040715, 2040717, 2040810, 2040811, 2070005, 2070006, 4020009,};
        final int[] quantitys = {1, 1, 1, 1, 240, 200, 200, 200, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3};
        int amount = 0;
        for (int i = 0; i < ids.length; i++) {
            if (i == id) {
                amount = quantitys[i];
            }
        }
        if (c.getPlayer().getInventory(InventoryType.getByType((byte) (id / 1000000))).isFull()) {
            return false;
        }
        InventoryManipulator.addById(c, id, (short) amount);
        return true;
    }

    private static void notEnabled(Character player) {
        player.dropMessage(1, I18nUtil.getMessage("UseCashItemHandler.handlePacket.message0"));
        player.enableActions();
    }
}
