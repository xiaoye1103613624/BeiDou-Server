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
package org.gms.net;

import org.gms.constants.net.ServerConstants;
import org.gms.net.netty.LoginServer;
import org.gms.net.opcodes.Opcode;
import org.gms.net.opcodes.RecvOpcode;
import org.gms.net.server.channel.handlers.*;
import org.gms.net.server.handlers.CustomPacketHandler;
import org.gms.net.server.handlers.KeepAliveHandler;
import org.gms.net.server.handlers.LoginRequiringNoOpHandler;
import org.gms.net.server.handlers.login.AcceptToSHandler;
import org.gms.net.server.handlers.login.AfterLoginHandler;
import org.gms.net.server.handlers.login.CharSelectedHandler;
import org.gms.net.server.handlers.login.CharSelectedWithPicHandler;
import org.gms.net.server.handlers.login.CharlistRequestHandler;
import org.gms.net.server.handlers.login.CheckCharNameHandler;
import org.gms.net.server.handlers.login.CreateCharHandler;
import org.gms.net.server.handlers.login.DeleteCharHandler;
import org.gms.net.server.handlers.login.GuestLoginHandler;
import org.gms.net.server.handlers.login.LoginPasswordHandler;
import org.gms.net.server.handlers.login.RegisterPicHandler;
import org.gms.net.server.handlers.login.RegisterPinHandler;
import org.gms.net.server.handlers.login.RelogRequestHandler;
import org.gms.net.server.handlers.login.ServerStatusRequestHandler;
import org.gms.net.server.handlers.login.ServerlistRequestHandler;
import org.gms.net.server.handlers.login.SetGenderHandler;
import org.gms.net.server.handlers.login.ViewAllCharHandler;
import org.gms.net.server.handlers.login.ViewAllCharRegisterPicHandler;
import org.gms.net.server.handlers.login.ViewAllCharSelectedHandler;
import org.gms.net.server.handlers.login.ViewAllCharSelectedWithPicHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 【类型】PacketProcessor（class），包 `org.gms.net`。
 *
 * 客户端封包路由处理器，负责将客户端发来的 opcode 映射到对应的 {@link PacketHandler} 处理逻辑。
 * 每个服务器实例（world + channel 唯一标识）持有一个独立的处理器实例，各自的 handler 数组通过
 * {@link #reset(int)} 按版本和服务器类型（登录服 / 频道服）注册不同的处理链路。
 *
 * 核心架构：
 * - {@link #handlers} 数组，下标为 opcode 值，值为对应的 {@link PacketHandler} 实现
 * - {@link #registerCommonHandlers()} 注册所有服务器共用的 handler（心跳、自定义封包）
 * - {@link #registerLoginHandlers()} 注册登录服专用 handler（登录、创角、选角等）
 * - {@link #registerChannelHandlers()} 注册频道服专用 handler（聊天、战斗、交易等游戏内操作）
 * - {@link #getHandler(short)} 由 {@link org.gms.client.Client#channelRead} 调用，根据解析出的 opcode 获取 handler 并执行
 *
 * @see PacketHandler
 * @see org.gms.net.opcodes.RecvOpcode
 */
public final class PacketProcessor {
    private static final Logger log = LoggerFactory.getLogger(PacketProcessor.class);

    /** 按 "world channel" 格式的字符串作为 key 缓存各服务器实例的处理器，确保每个服务器只有一份 */
    private static final Map<String, PacketProcessor> instances = new LinkedHashMap<>();

    /** 频道服务器所需的依赖注入（NoteService、FredrickProcessor），由 Spring 在启动时设置 */
    private static ChannelDependencies channelDeps;

    /** handler 数组，下标 = opcode 值，值为对应的处理逻辑。数组大小由 RecvOpcode 枚举的最大值决定 */
    private PacketHandler[] handlers;

    private PacketProcessor() {
        int maxRecvOp = 0;
        for (RecvOpcode op : RecvOpcode.values()) {
            if (op.getValue() > maxRecvOp) {
                maxRecvOp = op.getValue();
            }
        }
        handlers = new PacketHandler[maxRecvOp + 1];
    }

    /**
     * 注册频道服所需的 Spring 依赖注入。在 Server 启动时由上下文调用一次。
     *
     * @param channelDependencies 包含 NoteService 和 FredrickProcessor 的依赖集合
     */
    public static void registerGameHandlerDependencies(ChannelDependencies channelDependencies) {
        PacketProcessor.channelDeps = channelDependencies;
    }

    /**
     * 获取登录服务器的处理器实例（world=WORLD_ID, channel=CHANNEL_ID，channel 为负数）。
     *
     * @return 登录服专用的 PacketProcessor
     */
    public static PacketProcessor getLoginServerProcessor() {
        return getProcessor(LoginServer.WORLD_ID, LoginServer.CHANNEL_ID);
    }

    /**
     * 获取频道服务器的处理器实例。调用前必须已通过 {@link #registerGameHandlerDependencies(ChannelDependencies)} 注入依赖。
     *
     * @param world   大区 ID
     * @param channel 频道号
     * @return 频道服专用的 PacketProcessor
     * @throws IllegalStateException 如果依赖尚未注册
     */
    public static PacketProcessor getChannelServerProcessor(int world, int channel) {
        if (channelDeps == null) {
            throw new IllegalStateException("Unable to get channel server processor - dependencies are not registered");
        }

        return getProcessor(world, channel);
    }

    /**
     * 根据 opcode 值查找对应的 handler。
     *
     * @param packetId 客户端封包中的 opcode 值
     * @return 对应的 PacketHandler，找不到或超出范围返回 null
     */
    public PacketHandler getHandler(short packetId) {
        if (packetId > handlers.length) {
            return null;
        }
        PacketHandler handler = handlers[packetId];
        return handler;
    }

    /**
     * 将一个 opcode 与 handler 绑定注册。
     *
     * @param code    操作码枚举
     * @param handler 对应的处理逻辑实现
     */
    public void registerHandler(Opcode code, PacketHandler handler) {
        try {
            handlers[code.getValue()] = handler;
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Error registering handler {}", code.getName(), e);
        }
    }

    /**
     * 按 world + channel 获取或创建处理器实例（线程安全），确保每个服务器只有一份。
     *
     * @param world   大区 ID
     * @param channel 频道号（登录服为负数）
     * @return 对应服务器的 PacketProcessor
     */
    public synchronized static PacketProcessor getProcessor(int world, int channel) {
        final String processorId = world + " " + channel;
        PacketProcessor processor = instances.get(processorId);
        if (processor == null) {
            processor = new PacketProcessor();
            processor.reset(channel);
            instances.put(processorId, processor);
        }
        return processor;
    }

    /**
     * 重置 handler 数组并按版本注册所有 handler。
     * channel &lt; 0 表示登录服，否则为频道服。
     *
     * @param channel 频道号（负数=登录服）
     */
    public void reset(int channel) {
        handlers = new PacketHandler[handlers.length];

        switch (ServerConstants.VERSION) {
            case 83:
                registerCommonHandlers();

                if (channel < 0) {
                    registerLoginHandlers();
                } else {
                    registerChannelHandlers();
                }
                break;
            default:
                log.warn("找不到指定的版本注册 Opcode Handler");
                break;
        }
    }

    /** 注册所有服务器类型共用的 handler（心跳保活、自定义封包） */
    private void registerCommonHandlers() {
        registerHandler(RecvOpcode.PONG, new KeepAliveHandler());
        registerHandler(RecvOpcode.CUSTOM_PACKET, new CustomPacketHandler());
    }

    /** 注册登录服专用 handler：协议确认、登录认证、角色管理、服务器列表等 */
    private void registerLoginHandlers() {
        registerHandler(RecvOpcode.ACCEPT_TOS, new AcceptToSHandler());
        registerHandler(RecvOpcode.AFTER_LOGIN, new AfterLoginHandler());
        registerHandler(RecvOpcode.SERVERLIST_REREQUEST, new ServerlistRequestHandler());
        registerHandler(RecvOpcode.CHARLIST_REQUEST, new CharlistRequestHandler());
        registerHandler(RecvOpcode.CHAR_SELECT, new CharSelectedHandler());
        registerHandler(RecvOpcode.LOGIN_PASSWORD, new LoginPasswordHandler());
        registerHandler(RecvOpcode.RELOG, new RelogRequestHandler());
        registerHandler(RecvOpcode.SERVERLIST_REQUEST, new ServerlistRequestHandler());
        registerHandler(RecvOpcode.SERVERSTATUS_REQUEST, new ServerStatusRequestHandler());
        registerHandler(RecvOpcode.CHECK_CHAR_NAME, new CheckCharNameHandler());
        registerHandler(RecvOpcode.CREATE_CHAR, new CreateCharHandler());
        registerHandler(RecvOpcode.DELETE_CHAR, new DeleteCharHandler());
        registerHandler(RecvOpcode.VIEW_ALL_CHAR, new ViewAllCharHandler());
        registerHandler(RecvOpcode.PICK_ALL_CHAR, new ViewAllCharSelectedHandler());
        registerHandler(RecvOpcode.REGISTER_PIN, new RegisterPinHandler());
        registerHandler(RecvOpcode.GUEST_LOGIN, new GuestLoginHandler());
        registerHandler(RecvOpcode.REGISTER_PIC, new RegisterPicHandler());
        registerHandler(RecvOpcode.CHAR_SELECT_WITH_PIC, new CharSelectedWithPicHandler());
        registerHandler(RecvOpcode.SET_GENDER, new SetGenderHandler());
        registerHandler(RecvOpcode.VIEW_ALL_WITH_PIC, new ViewAllCharSelectedWithPicHandler());
        registerHandler(RecvOpcode.VIEW_ALL_PIC_REGISTER, new ViewAllCharRegisterPicHandler());
    }

    /** 注册频道服专用 handler：聊天、移动、战斗、背包、技能、社交、宠物、家族、商城等游戏内操作 */
    private void registerChannelHandlers() {
        registerHandler(RecvOpcode.NAME_TRANSFER, new TransferNameHandler());
        registerHandler(RecvOpcode.CHECK_CHAR_NAME, new TransferNameResultHandler());
        registerHandler(RecvOpcode.WORLD_TRANSFER, new TransferWorldHandler());
        registerHandler(RecvOpcode.CHANGE_CHANNEL, new ChangeChannelHandler());
        registerHandler(RecvOpcode.STRANGE_DATA, LoginRequiringNoOpHandler.getInstance());
        registerHandler(RecvOpcode.GENERAL_CHAT, new GeneralChatHandler());
        registerHandler(RecvOpcode.WHISPER, new WhisperHandler());
        registerHandler(RecvOpcode.NPC_TALK, new NPCTalkHandler());
        registerHandler(RecvOpcode.NPC_TALK_MORE, new NPCMoreTalkHandler());
        registerHandler(RecvOpcode.QUEST_ACTION, new QuestActionHandler());
        registerHandler(RecvOpcode.GRENADE_EFFECT, new GrenadeEffectHandler());
        registerHandler(RecvOpcode.NPC_SHOP, new NPCShopHandler());
        registerHandler(RecvOpcode.ITEM_SORT, new InventoryMergeHandler());
        registerHandler(RecvOpcode.ITEM_MOVE, new ItemMoveHandler());
        registerHandler(RecvOpcode.MESO_DROP, new MesoDropHandler());
        registerHandler(RecvOpcode.PLAYER_LOGGEDIN, new PlayerLoggedinHandler(channelDeps.noteService()));
        registerHandler(RecvOpcode.CHANGE_MAP, new ChangeMapHandler());
        registerHandler(RecvOpcode.MOVE_LIFE, new MoveLifeHandler());
        registerHandler(RecvOpcode.CLOSE_RANGE_ATTACK, new CloseRangeDamageHandler());
        registerHandler(RecvOpcode.RANGED_ATTACK, new RangedAttackHandler());
        registerHandler(RecvOpcode.MAGIC_ATTACK, new MagicDamageHandler());
        registerHandler(RecvOpcode.TAKE_DAMAGE, new TakeDamageHandler());
        registerHandler(RecvOpcode.MOVE_PLAYER, new MovePlayerHandler());
        registerHandler(RecvOpcode.USE_CASH_ITEM, new UseCashItemHandler(channelDeps.noteService()));
        registerHandler(RecvOpcode.USE_ITEM, new UseItemHandler());
        registerHandler(RecvOpcode.USE_RETURN_SCROLL, new UseItemHandler());
        registerHandler(RecvOpcode.USE_UPGRADE_SCROLL, new ScrollHandler());
        registerHandler(RecvOpcode.USE_SUMMON_BAG, new UseSummonBagHandler());
        registerHandler(RecvOpcode.FACE_EXPRESSION, new FaceExpressionHandler());
        registerHandler(RecvOpcode.HEAL_OVER_TIME, new HealOvertimeHandler());
        registerHandler(RecvOpcode.ITEM_PICKUP, new ItemPickupHandler());
        registerHandler(RecvOpcode.CHAR_INFO_REQUEST, new CharInfoRequestHandler());
        registerHandler(RecvOpcode.SPECIAL_MOVE, new SpecialMoveHandler());
        registerHandler(RecvOpcode.USE_INNER_PORTAL, new InnerPortalHandler());
        registerHandler(RecvOpcode.CANCEL_BUFF, new CancelBuffHandler());
        registerHandler(RecvOpcode.CANCEL_ITEM_EFFECT, new CancelItemEffectHandler());
        registerHandler(RecvOpcode.PLAYER_INTERACTION, new PlayerInteractionHandler());
        registerHandler(RecvOpcode.RPS_ACTION, new RPSActionHandler());
        registerHandler(RecvOpcode.DISTRIBUTE_AP, new DistributeAPHandler());
        registerHandler(RecvOpcode.DISTRIBUTE_SP, new DistributeSPHandler());
        registerHandler(RecvOpcode.CHANGE_KEYMAP, new KeymapChangeHandler());
        registerHandler(RecvOpcode.CHANGE_MAP_SPECIAL, new ChangeMapSpecialHandler());
        registerHandler(RecvOpcode.STORAGE, new StorageHandler());
        registerHandler(RecvOpcode.GIVE_FAME, new GiveFameHandler());
        registerHandler(RecvOpcode.PARTY_OPERATION, new PartyOperationHandler());
        registerHandler(RecvOpcode.DENY_PARTY_REQUEST, new DenyPartyRequestHandler());
        registerHandler(RecvOpcode.MULTI_CHAT, new MultiChatHandler());
        registerHandler(RecvOpcode.USE_DOOR, new DoorHandler());
        registerHandler(RecvOpcode.ENTER_MTS, new EnterMTSHandler());
        registerHandler(RecvOpcode.ENTER_CASHSHOP, new EnterCashShopHandler());
        registerHandler(RecvOpcode.DAMAGE_SUMMON, new DamageSummonHandler());
        registerHandler(RecvOpcode.MOVE_SUMMON, new MoveSummonHandler());
        registerHandler(RecvOpcode.SUMMON_ATTACK, new SummonDamageHandler());
        registerHandler(RecvOpcode.BUDDYLIST_MODIFY, new BuddylistModifyHandler());
        registerHandler(RecvOpcode.USE_ITEMEFFECT, new UseItemEffectHandler());
        registerHandler(RecvOpcode.USE_CHAIR, new UseChairHandler());
        registerHandler(RecvOpcode.CANCEL_CHAIR, new CancelChairHandler());
        registerHandler(RecvOpcode.DAMAGE_REACTOR, new ReactorHitHandler());
        registerHandler(RecvOpcode.GUILD_OPERATION, new GuildOperationHandler());
        registerHandler(RecvOpcode.DENY_GUILD_REQUEST, new DenyGuildRequestHandler());
        registerHandler(RecvOpcode.BBS_OPERATION, new BBSOperationHandler());
        registerHandler(RecvOpcode.SKILL_EFFECT, new SkillEffectHandler());
        registerHandler(RecvOpcode.MESSENGER, new MessengerHandler());
        registerHandler(RecvOpcode.NPC_ACTION, new NPCAnimationHandler());
        registerHandler(RecvOpcode.CHECK_CASH, new TouchingCashShopHandler());
        registerHandler(RecvOpcode.CASHSHOP_OPERATION, new CashOperationHandler(channelDeps.noteService()));
        registerHandler(RecvOpcode.COUPON_CODE, new CouponCodeHandler());
        registerHandler(RecvOpcode.SPAWN_PET, new SpawnPetHandler());
        registerHandler(RecvOpcode.MOVE_PET, new MovePetHandler());
        registerHandler(RecvOpcode.PET_CHAT, new PetChatHandler());
        registerHandler(RecvOpcode.PET_COMMAND, new PetCommandHandler());
        registerHandler(RecvOpcode.PET_FOOD, new PetFoodHandler());
        registerHandler(RecvOpcode.PET_LOOT, new PetLootHandler());
        registerHandler(RecvOpcode.AUTO_AGGRO, new AutoAggroHandler());
        registerHandler(RecvOpcode.MONSTER_BOMB, new MonsterBombHandler());
        registerHandler(RecvOpcode.CANCEL_DEBUFF, new CancelDebuffHandler());
        registerHandler(RecvOpcode.USE_SKILL_BOOK, new SkillBookHandler());
        registerHandler(RecvOpcode.SKILL_MACRO, new SkillMacroHandler());
        registerHandler(RecvOpcode.NOTE_ACTION, new NoteActionHandler(channelDeps.noteService()));
        registerHandler(RecvOpcode.CLOSE_CHALKBOARD, new CloseChalkboardHandler());
        registerHandler(RecvOpcode.USE_MOUNT_FOOD, new UseMountFoodHandler());
        registerHandler(RecvOpcode.MTS_OPERATION, new MTSHandler());
        registerHandler(RecvOpcode.RING_ACTION, new RingActionHandler(channelDeps.noteService()));
        registerHandler(RecvOpcode.SPOUSE_CHAT, new SpouseChatHandler());
        registerHandler(RecvOpcode.PET_AUTO_POT, new PetAutoPotHandler());
        registerHandler(RecvOpcode.PET_EXCLUDE_ITEMS, new PetExcludeItemsHandler());
        registerHandler(RecvOpcode.OWL_ACTION, new UseOwlOfMinervaHandler());
        registerHandler(RecvOpcode.OWL_WARP, new OwlWarpHandler());
        registerHandler(RecvOpcode.TOUCH_MONSTER_ATTACK, new TouchMonsterDamageHandler());
        registerHandler(RecvOpcode.TROCK_ADD_MAP, new TrockAddMapHandler());
        registerHandler(RecvOpcode.HIRED_MERCHANT_REQUEST, new HiredMerchantRequest());
        registerHandler(RecvOpcode.MOB_BANISH_PLAYER, new MobBanishPlayerHandler());
        registerHandler(RecvOpcode.MOB_DAMAGE_MOB, new MobDamageMobHandler());
        registerHandler(RecvOpcode.REPORT, new ReportHandler());
        registerHandler(RecvOpcode.MONSTER_BOOK_COVER, new MonsterBookCoverHandler());
        registerHandler(RecvOpcode.AUTO_DISTRIBUTE_AP, new AutoAssignHandler());
        registerHandler(RecvOpcode.MAKER_SKILL, new MakerSkillHandler());
        registerHandler(RecvOpcode.USE_TREASUER_CHEST, new UseTreasureChestHandler());
        registerHandler(RecvOpcode.OPEN_FAMILY_PEDIGREE, new OpenFamilyPedigreeHandler());
        registerHandler(RecvOpcode.OPEN_FAMILY, new OpenFamilyHandler());
        registerHandler(RecvOpcode.ADD_FAMILY, new FamilyAddHandler());
        registerHandler(RecvOpcode.SEPARATE_FAMILY_BY_SENIOR, new FamilySeparateHandler());
        registerHandler(RecvOpcode.SEPARATE_FAMILY_BY_JUNIOR, new FamilySeparateHandler());
        registerHandler(RecvOpcode.USE_FAMILY, new FamilyUseHandler());
        registerHandler(RecvOpcode.CHANGE_FAMILY_MESSAGE, new FamilyPreceptsHandler());
        registerHandler(RecvOpcode.FAMILY_SUMMON_RESPONSE, new FamilySummonResponseHandler());
        registerHandler(RecvOpcode.USE_HAMMER, new UseHammerHandler());
        registerHandler(RecvOpcode.SCRIPTED_ITEM, new ScriptedItemHandler());
        registerHandler(RecvOpcode.TOUCHING_REACTOR, new TouchReactorHandler());
        registerHandler(RecvOpcode.BEHOLDER, new BeholderHandler());
        registerHandler(RecvOpcode.ADMIN_COMMAND, new AdminCommandHandler());
        registerHandler(RecvOpcode.ADMIN_LOG, new AdminLogHandler());
        registerHandler(RecvOpcode.ALLIANCE_OPERATION, new AllianceOperationHandler());
        registerHandler(RecvOpcode.DENY_ALLIANCE_REQUEST, new DenyAllianceRequestHandler());
        registerHandler(RecvOpcode.USE_SOLOMON_ITEM, new UseSolomonHandler());
        registerHandler(RecvOpcode.USE_GACHA_EXP, new UseGachaExpHandler());
        registerHandler(RecvOpcode.NEW_YEAR_CARD_REQUEST, new NewYearCardHandler());
        registerHandler(RecvOpcode.CASHSHOP_SURPRISE, new CashShopSurpriseHandler());
        registerHandler(RecvOpcode.USE_ITEM_REWARD, new ItemRewardHandler());
        registerHandler(RecvOpcode.USE_REMOTE, new RemoteGachaponHandler());
        registerHandler(RecvOpcode.ACCEPT_FAMILY, new AcceptFamilyHandler());
        registerHandler(RecvOpcode.DUEY_ACTION, new DueyHandler());
        registerHandler(RecvOpcode.USE_DEATHITEM, new UseDeathItemHandler());
        registerHandler(RecvOpcode.PLAYER_MAP_TRANSFER, new PlayerMapTransitionHandler());
        registerHandler(RecvOpcode.USE_MAPLELIFE, new UseMapleLifeHandler());
        registerHandler(RecvOpcode.USE_CATCH_ITEM, new UseCatchItemHandler());
        registerHandler(RecvOpcode.FIELD_DAMAGE_MOB, new FieldDamageMobHandler());
        registerHandler(RecvOpcode.MOB_DAMAGE_MOB_FRIENDLY, new MobDamageMobFriendlyHandler());
        registerHandler(RecvOpcode.PARTY_SEARCH_REGISTER, new PartySearchRegisterHandler());
        registerHandler(RecvOpcode.PARTY_SEARCH_START, new PartySearchStartHandler());
        registerHandler(RecvOpcode.PARTY_SEARCH_UPDATE, new PartySearchUpdateHandler());
        registerHandler(RecvOpcode.ITEM_SORT2, new InventorySortHandler());
        registerHandler(RecvOpcode.LEFT_KNOCKBACK, new LeftKnockbackHandler());
        registerHandler(RecvOpcode.SNOWBALL, new SnowballHandler());
        registerHandler(RecvOpcode.COCONUT, new CoconutHandler());
        registerHandler(RecvOpcode.ARAN_COMBO_COUNTER, new AranComboHandler());
        registerHandler(RecvOpcode.CLICK_GUIDE, new ClickGuideHandler());
        registerHandler(RecvOpcode.FREDRICK_ACTION, new FredrickHandler(channelDeps.fredrickProcessor()));
        registerHandler(RecvOpcode.MONSTER_CARNIVAL, new MonsterCarnivalHandler());
        registerHandler(RecvOpcode.REMOTE_STORE, new RemoteStoreHandler());
        registerHandler(RecvOpcode.WEDDING_ACTION, new WeddingHandler());
        registerHandler(RecvOpcode.WEDDING_TALK, new WeddingTalkHandler());
        registerHandler(RecvOpcode.WEDDING_TALK_MORE, new WeddingTalkMoreHandler());
        registerHandler(RecvOpcode.WATER_OF_LIFE, new UseWaterOfLifeHandler());
        registerHandler(RecvOpcode.ADMIN_CHAT, new AdminChatHandler());
        registerHandler(RecvOpcode.MOVE_DRAGON, new MoveDragonHandler());
        registerHandler(RecvOpcode.OPEN_ITEMUI, new RaiseUIStateHandler());
        registerHandler(RecvOpcode.USE_ITEMUI, new RaiseIncExpHandler());
        registerHandler(RecvOpcode.CHANGE_QUICKSLOT, new QuickslotKeyMappedModifiedHandler());
        registerHandler(RecvOpcode.SET_HPMPALERT, new SetHpMpAlertHandler());
    }
}
