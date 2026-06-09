/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gms.util.packets;

import org.gms.client.Character;
import org.gms.client.inventory.Item;
import org.gms.constants.id.ItemId;
import org.gms.constants.id.MapId;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.OutPacket;
import org.gms.net.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.PacketCreator;
import org.gms.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CField_Wedding, CField_WeddingPhoto, CWeddingMan, OnMarriageResult, and all Wedding/Marriage enum/structs.
 *
 * @author Eric
 * <p>
 * Wishlists edited by Drago (Dragohe4rt)
 */
public class WeddingPackets extends PacketCreator {
    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(WeddingPackets.class);

    // 00000000 CWeddingMan     struc ; (sizeof=0x104)
    // 00000000 vfptr           dd ?                    ; offset
    // 00000004 ___u1           $01CBC6800BD386B8A8FD818EAD990BEC ?
    // 0000000C m_mCharIDToMarriageNo ZMap<unsigned long,unsigned long,unsigned long> ?
    // 00000024 m_mReservationPending ZMap<unsigned long,ZRef<GW_WeddingReservation>,unsigned long> ?
    // 0000003C m_mReservationPendingGroom ZMap<unsigned long,ZRef<CUser>,unsigned long> ?
    // 00000054 m_mReservationPendingBride ZMap<unsigned long,ZRef<CUser>,unsigned long> ?
    // 0000006C m_mReservationStartUser ZMap<unsigned long,unsigned long,unsigned long> ?
    // 00000084 m_mReservationCompleted ZMap<unsigned long,ZRef<GW_WeddingReservation>,unsigned long> ?
    // 0000009C m_mGroomWishList ZMap<unsigned long,ZRef<ZArray<ZXString<char> > >,unsigned long> ?
    // 000000B4 m_mBrideWishList ZMap<unsigned long,ZRef<ZArray<ZXString<char> > >,unsigned long> ?
    // 000000CC m_mEngagementPending ZMap<unsigned long,ZRef<GW_MarriageRecord>,unsigned long> ?
    // 000000E4 m_nCurrentWeddingState dd ?
    // 000000E8 m_dwCurrentWeddingNo dd ?
    // 000000EC m_dwCurrentWeddingMap dd ?
    // 000000F0 m_bIsReservationLoaded dd ?
    // 000000F4 m_dwNumGuestBless dd ?
    // 000000F8 m_bPhotoSuccess dd ?
    // 000000FC m_tLastUpdate   dd ?
    // 00000100 m_bStartWeddingCeremony dd ?
    // 00000104 CWeddingMan     ends

    /**
     * 婚礼场地信息（婚礼进度、祝福计时等）
     */
    public class Field_Wedding {
        /** 公告计数 */
        public int m_nNoticeCount;
        /** 当前进度步骤 */
        public int m_nCurrentStep;
        /** 祝福开始时间 */
        public int m_nBlessStartTime;
    }

    /**
     * 婚礼拍照信息
     */
    public class Field_WeddingPhoto {
        /** 是否已拍照 */
        public boolean m_bPictureTook;
    }

    /**
     * 婚礼预约信息
     */
    public class GW_WeddingReservation {
        /** 预约编号 */
        public int dwReservationNo;
        /** 新郎角色ID */
        public int dwGroom;
        /** 新娘角色ID */
        public int dwBride;
        /** 新郎名称 */
        public String sGroomName;
        /** 新娘名称 */
        public String sBrideName;
        /** 婚礼类型 */
        public int usWeddingType;
    }

    /**
     * 婚礼心愿单
     */
    public class WeddingWishList {
        /** 所属角色 */
        public Character pUser;
        /** 婚姻编号 */
        public int dwMarriageNo;
        /** 性别 */
        public int nGender;
        /** 心愿单类型 */
        public int nWLType;
        /** 槽位数 */
        public int nSlotCount;
        /** 心愿物品名称列表 */
        public List<String> asWishList = new ArrayList<>();
        // dword
        /** 修改标记位 */
        public int usModifiedFlag;
        /** 是否已加载 */
        public boolean bLoaded;
    }

    /**
     * 婚礼心愿单条目
     */
    public class GW_WeddingWishList {
        // enum WEDDINGWL
        /** 心愿单最大条目数 */
        public final int WEDDINGWL_MAX = 0xA;
        /** 预约编号 */
        public int dwReservationNo;
        /** 性别 */
        public byte nGender;
        /** 物品名称 */
        public String sItemName;
    }

    /**
     * 婚姻状态枚举
     */
    public enum MarriageStatus {
        /** 单身 */
        SINGLE(0x0),
        /** 已订婚 */
        ENGAGED(0x1),
        /** 已预约 */
        RESERVED(0x2),
        /** 已婚 */
        MARRIED(0x3);

        /** 状态值 */
        private final int ms;

        MarriageStatus(int ms) {
            this.ms = ms;
        }

        /**
         * 获取婚姻状态值
         *
         * @return 状态值
         */
        public int getMarriageStatus() {
            return ms;
        }
    }

    /**
     * 婚姻请求操作类型枚举
     */
    public enum MarriageRequest {
        /** 添加婚姻记录 */
        AddMarriageRecord(0x0),
        /** 设置婚姻记录 */
        SetMarriageRecord(0x1),
        /** 删除婚姻记录 */
        DeleteMarriageRecord(0x2),
        /** 加载预约 */
        LoadReservation(0x3),
        /** 添加预约 */
        AddReservation(0x4),
        /** 删除预约 */
        DeleteReservation(0x5),
        /** 获取预约 */
        GetReservation(0x6);

        /** 请求类型值 */
        private final int req;

        MarriageRequest(int req) {
            this.req = req;
        }

        /**
         * 获取婚姻请求类型值
         *
         * @return 请求类型值
         */
        public int getMarriageRequest() {
            return req;
        }
    }

    /**
     * 婚礼类型枚举
     */
    public enum WeddingType {
        /** 大教堂 */
        CATHEDRAL(0x1),
        /** 拉斯维加斯 */
        VEGAS(0x2),
        /** 大教堂豪华版 */
        CATHEDRAL_PREMIUM(0xA),
        /** 大教堂普通版 */
        CATHEDRAL_NORMAL(0xB),
        /** 拉斯维加斯豪华版 */
        VEGAS_PREMIUM(0x14),
        /** 拉斯维加斯普通版 */
        VEGAS_NORMAL(0x15);

        /** 类型值 */
        private final int wt;

        WeddingType(int wt) {
            this.wt = wt;
        }

        /**
         * 获取婚礼类型值
         *
         * @return 类型值
         */
        public int getType() {
            return wt;
        }
    }

    /**
     * 婚礼地图枚举
     */
    public enum WeddingMap {
        /** 婚礼小镇（阿莫里亚） */
        WEDDINGTOWN(MapId.AMORIA),
        /** 小教堂婚礼祭坛 */
        CHAPEL_STARTMAP(MapId.CHAPEL_WEDDING_ALTAR),
        /** 大教堂婚礼祭坛 */
        CATHEDRAL_STARTMAP(MapId.CATHEDRAL_WEDDING_ALTAR),
        /** 婚礼拍照地图 */
        PHOTOMAP(MapId.WEDDING_PHOTO),
        /** 婚礼出口地图 */
        EXITMAP(MapId.WEDDING_EXIT);

        /** 地图ID */
        private final int wm;

        WeddingMap(int wm) {
            this.wm = wm;
        }

        /**
         * 获取地图ID
         *
         * @return 地图ID
         */
        public int getMap() {
            return wm;
        }
    }

    /**
     * 婚礼相关物品枚举
     */
    public enum WeddingItem {
        // Wedding Ring
        /** 月光石婚戒 */
        WR_MOONSTONE(ItemId.WEDDING_RING_MOONSTONE),
        // Wedding Ring
        /** 星光宝石婚戒 */
        WR_STARGEM(ItemId.WEDDING_RING_STAR),
        // Wedding Ring
        /** 金心婚戒 */
        WR_GOLDENHEART(ItemId.WEDDING_RING_GOLDEN),
        // Wedding Ring
        /** 银天鹅婚戒 */
        WR_SILVERSWAN(ItemId.WEDDING_RING_SILVER),
        // Engagement Ring Box
        /** 月光石订婚戒指盒 */
        ERB_MOONSTONE(ItemId.ENGAGEMENT_BOX_MOONSTONE),
        // Engagement Ring Box
        /** 星光宝石订婚戒指盒 */
        ERB_STARGEM(ItemId.ENGAGEMENT_BOX_STAR),
        // Engagement Ring Box
        /** 金心订婚戒指盒 */
        ERB_GOLDENHEART(ItemId.ENGAGEMENT_BOX_GOLDEN),
        // Engagement Ring Box
        /** 银天鹅订婚戒指盒 */
        ERB_SILVERSWAN(ItemId.ENGAGEMENT_BOX_SILVER),
        // Engagement Ring Box (Empty)
        /** 月光石空订婚戒指盒 */
        ERBE_MOONSTONE(ItemId.EMPTY_ENGAGEMENT_BOX_MOONSTONE),
        // Engagement Ring
        /** 月光石订婚戒指 */
        ER_MOONSTONE(ItemId.ENGAGEMENT_RING_MOONSTONE),
        // Engagement Ring Box (Empty)
        /** 星光宝石空订婚戒指盒 */
        ERBE_STARGEM(ItemId.EMPTY_ENGAGEMENT_BOX_STAR),
        // Engagement Ring
        /** 星光宝石订婚戒指 */
        ER_STARGEM(ItemId.ENGAGEMENT_RING_STAR),
        // Engagement Ring Box (Empty)
        /** 金心空订婚戒指盒 */
        ERBE_GOLDENHEART(ItemId.EMPTY_ENGAGEMENT_BOX_GOLDEN),
        // Engagement Ring
        /** 金心订婚戒指 */
        ER_GOLDENHEART(ItemId.ENGAGEMENT_RING_GOLDEN),
        // Engagement Ring Box (Empty)
        /** 银天鹅空订婚戒指盒 */
        ERBE_SILVERSWAN(ItemId.EMPTY_ENGAGEMENT_BOX_SILVER),
        // Engagement Ring
        /** 银天鹅订婚戒指 */
        ER_SILVERSWAN(ItemId.ENGAGEMENT_RING_SILVER),
        // Parents Blessing
        /** 父母的祝福 */
        PARENTS_BLESSING(ItemId.PARENTS_BLESSING),
        // Officiator's Permission
        /** 主婚人许可 */
        OFFICIATORS_PERMISSION(ItemId.OFFICIATORS_PERMISSION),
        // Wedding Ring?
        /** 大教堂豪华预约收据 */
        WR_CATHEDRAL_PREMIUM(ItemId.PREMIUM_CATHEDRAL_RESERVATION_RECEIPT),
        // Wedding Ring?
        /** 小教堂豪华预约收据 */
        WR_VEGAS_PREMIUM(ItemId.PREMIUM_CHAPEL_RESERVATION_RECEIPT),
        // toSend invitation
        /** 小教堂邀请函（发送用） */
        IB_VEGAS(ItemId.INVITATION_CHAPEL),
        // toSend invitation
        /** 大教堂邀请函（发送用） */
        IB_CATHEDRAL(ItemId.INVITATION_CATHEDRAL),
        // rcvd invitation
        /** 小教堂邀请函（已接收） */
        IG_VEGAS(ItemId.RECEIVED_INVITATION_CHAPEL),
        // rcvd invitation
        /** 大教堂邀请函（已接收） */
        IG_CATHEDRAL(ItemId.RECEIVED_INVITATION_CATHEDRAL),
        // Onyx Box? For Couple
        /** 情侣缟玛瑙宝箱 */
        OB_FORCOUPLE(ItemId.ONYX_CHEST_FOR_COUPLE),
        // Wedding Ring?
        /** 大教堂普通预约收据 */
        WR_CATHEDRAL_NORMAL(ItemId.NORMAL_CATHEDRAL_RESERVATION_RECEIPT),
        // Wedding Ring?
        /** 小教堂普通预约收据 */
        WR_VEGAS_NORMAL(ItemId.NORMAL_CHAPEL_RESERVATION_RECEIPT),
        // Wedding Ticket
        /** 大教堂普通婚礼入场券 */
        WT_CATHEDRAL_NORMAL(ItemId.NORMAL_WEDDING_TICKET_CATHEDRAL),
        // Wedding Ticket
        /** 小教堂普通婚礼入场券 */
        WT_VEGAS_NORMAL(ItemId.NORMAL_WEDDING_TICKET_CHAPEL),
        // Wedding Ticket
        /** 小教堂豪华婚礼入场券 */
        WT_VEGAS_PREMIUM(ItemId.PREMIUM_WEDDING_TICKET_CHAPEL),
        // Wedding Ticket
        /** 大教堂豪华婚礼入场券 */
        WT_CATHEDRAL_PREMIUM(ItemId.PREMIUM_WEDDING_TICKET_CATHEDRAL);

        /** 物品ID */
        private final int wi;

        WeddingItem(int wi) {
            this.wi = wi;
        }

        /**
         * 获取物品ID
         *
         * @return 物品ID
         */
        public int getItem() {
            return wi;
        }
    }

    /**
     * <name> has requested engagement. Will you accept this proposal?
     *
     * @param name
     * @param playerid
     * @return mplew
     */
    public static Packet onMarriageRequest(String name, int playerid) {
        OutPacket p = OutPacket.create(SendOpcode.MARRIAGE_REQUEST);
        // mode, 0 = engage, 1 = cancel, 2 = answer.. etc
        p.writeByte(0);
        // name
        p.writeString(name);
        // playerid
        p.writeInt(playerid);
        return p;
    }

    /**
     * A quick rundown of how (I think based off of enough BMS searching) WeddingPhoto_OnTakePhoto works:
     * - We send this packet with (first) the Groom / Bride IGNs
     * - We then send a fieldId (unsure about this part at the moment, 90% sure it's the id of the map)
     * - After this, we write an integer of the amount of characters within the current map (which is the Cake Map -- exclude users within Exit Map)
     * - Once we've retrieved the size of the characters, we begin to write information about them (Encode their name, guild, etc info)
     * - Now that we've Encoded our character data, we begin to Encode the ScreenShotPacket which requires a TemplateID, IGN, and their positioning
     * - Finally, after encoding all of our data, we send this packet out to a MapGen application server
     * - The MapGen server will then retrieve the packet byte array and convert the bytes into a ImageIO 2D JPG output
     * - The result after converting into a JPG will then be remotely uploaded to /weddings/ with ReservedGroomName_ReservedBrideName to be displayed on the web server.
     * <p>
     * - Will no longer continue Wedding Photos, needs a WvsMapGen :(
     *
     * @param ReservedGroomName The groom IGN of the wedding
     * @param ReservedBrideName The bride IGN of the wedding
     * @param m_dwField         The current field id (the id of the cake map, ex. 680000300)
     * @param m_dwUsers         The List of all Character guests within the current cake map to be encoded
     * @return mplew (MaplePacket) Byte array to be converted and read for byte[]->ImageIO
     */
    // OnIFailedAtWeddingPhotos
    public static Packet onTakePhoto(String ReservedGroomName, String ReservedBrideName, int m_dwField, List<Character> m_dwUsers) {
        // v53 header, convert -> v83
        OutPacket p = OutPacket.create(SendOpcode.WEDDING_PHOTO);
        p.writeString(ReservedGroomName);
        p.writeString(ReservedBrideName);
        // field id?
        p.writeInt(m_dwField);
        p.writeInt(m_dwUsers.size());

        for (Character guest : m_dwUsers) {
            // Begin Avatar Encoding
            // CUser::EncodeAvatar
            addCharLook(p, guest, false);
            // v20 = *(_DWORD *)(v13 + 2192) -- new groom marriage ID??
            p.writeInt(30000);
            // v20 = *(_DWORD *)(v13 + 2192) -- new bride marriage ID??
            p.writeInt(30000);
            p.writeString(guest.getName());
            p.writeString(guest.getGuildId() > 0 && guest.getGuild() != null ? guest.getGuild().getName() : "");
            p.writeShort(guest.getGuildId() > 0 && guest.getGuild() != null ? guest.getGuild().getLogoBG() : 0);
            p.writeByte(guest.getGuildId() > 0 && guest.getGuild() != null ? guest.getGuild().getLogoBGColor() : 0);
            p.writeShort(guest.getGuildId() > 0 && guest.getGuild() != null ? guest.getGuild().getLogo() : 0);
            p.writeByte(guest.getGuildId() > 0 && guest.getGuild() != null ? guest.getGuild().getLogoColor() : 0);
            // v18 = *(_DWORD *)(v13 + 3204);
            p.writeShort(guest.getPosition().x);
            // v20 = *(_DWORD *)(v13 + 3208);
            p.writeShort(guest.getPosition().y);
            // Begin Screenshot Encoding
            // if ( *(_DWORD *)(v13 + 288) ) { COutPacket::Encode1(&thisa, v20);
            p.writeByte(1);
            // CPet::EncodeScreenShotPacket(*(CPet **)(v13 + 288), &thisa);
            // dwTemplateID
            p.writeInt(1);
            // m_sName
            p.writeString(guest.getName());
            // m_ptCurPos.x
            p.writeShort(guest.getPosition().x);
            // m_ptCurPos.y
            p.writeShort(guest.getPosition().y);
            // guest.m_bMoveAction
            p.writeByte(guest.getStance());
        }

        return p;
    }

    /**
     * Enable spouse chat and their engagement ring without @relog
     *
     * @param marriageId
     * @param chr
     * @param wedding
     * @return mplew
     */
    public static Packet OnMarriageResult(int marriageId, Character chr, boolean wedding) {
        OutPacket p = OutPacket.create(SendOpcode.MARRIAGE_RESULT);
        p.writeByte(11);
        p.writeInt(marriageId);
        p.writeInt(chr.getGender() == 0 ? chr.getId() : chr.getPartnerId());
        p.writeInt(chr.getGender() == 0 ? chr.getPartnerId() : chr.getId());
        p.writeShort(wedding ? 3 : 1);
        if (wedding) {
            p.writeInt(chr.getMarriageItemId());
            p.writeInt(chr.getMarriageItemId());
        } else {
            // Engagement Ring's Outcome (doesn't matter for engagement)
            p.writeInt(ItemId.WEDDING_RING_MOONSTONE);
            // Engagement Ring's Outcome (doesn't matter for engagement)
            p.writeInt(ItemId.WEDDING_RING_MOONSTONE);
        }
        p.writeFixedString(StringUtil.getRightPaddedStr(chr.getGender() == 0 ? chr.getName() : Character.getNameById(chr.getPartnerId()), '\0', 13));
        p.writeFixedString(StringUtil.getRightPaddedStr(chr.getGender() == 0 ? Character.getNameById(chr.getPartnerId()) : chr.getName(), '\0', 13));

        return p;
    }

    /**
     * To exit the Engagement Window (Waiting for her response...), we send a GMS-like pop-up.
     *
     * @param msg
     * @return mplew
     */
    public static Packet OnMarriageResult(final byte msg) {
        OutPacket p = OutPacket.create(SendOpcode.MARRIAGE_RESULT);
        p.writeByte(msg);
        if (msg == 36) {
            p.writeByte(1);
            p.writeString("You are now engaged.");
        }
        return p;
    }

    /**
     * The World Map includes 'loverPos' in which this packet controls
     *
     * @param partner
     * @param mapid
     * @return mplew
     */
    public static Packet OnNotifyWeddingPartnerTransfer(int partner, int mapid) {
        OutPacket p = OutPacket.create(SendOpcode.NOTIFY_MARRIED_PARTNER_MAP_TRANSFER);
        p.writeInt(mapid);
        p.writeInt(partner);
        return p;
    }

    /**
     * The wedding packet to display Pelvis Bebop and enable the Wedding Ceremony Effect between two characters
     * CField_Wedding::OnWeddingProgress - Stages
     * CField_Wedding::OnWeddingCeremonyEnd - Wedding Ceremony Effect
     *
     * @param setBlessEffect
     * @param groom
     * @param bride
     * @param step
     * @return mplew
     */
    public static Packet OnWeddingProgress(boolean setBlessEffect, int groom, int bride, byte step) {
        OutPacket p = OutPacket.create(setBlessEffect ? SendOpcode.WEDDING_CEREMONY_END : SendOpcode.WEDDING_PROGRESS);
        // in order for ceremony packet to send, byte step = 2 must be sent first
        if (!setBlessEffect) {
            p.writeByte(step);
        }
        p.writeInt(groom);
        p.writeInt(bride);
        return p;
    }

    /**
     * When we open a Wedding Invitation, we display the Bride & Groom
     *
     * @param groom
     * @param bride
     * @return mplew
     */
    public static Packet sendWeddingInvitation(String groom, String bride) {
        OutPacket p = OutPacket.create(SendOpcode.MARRIAGE_RESULT);
        p.writeByte(15);
        p.writeString(groom);
        p.writeString(bride);
        // 0 = Cathedral Normal?, 1 = Cathedral Premium?, 2 = Chapel Normal?
        p.writeShort(1);
        return p;
    }

    /**
     * 打开心愿单 UI 界面
     */
    public static Packet sendWishList() {
        OutPacket p = OutPacket.create(SendOpcode.MARRIAGE_REQUEST);
        p.writeByte(9);
        return p;
    }

    /**
     * Handles all of WeddingWishlist packets
     *
     * @param mode
     * @param itemnames
     * @param items
     * @return mplew
     */
    public static Packet onWeddingGiftResult(byte mode, List<String> itemnames, List<Item> items) {
        OutPacket p = OutPacket.create(SendOpcode.WEDDING_GIFT_RESULT);
        p.writeByte(mode);
        switch (mode) {
            // 12 : You cannot give more than one present for each wishlist
            case 0xC:
            // 14 : Failed to send the gift.
            case 0xE:
                break;

            // Load Wedding Registry
            case 0x09: {
                p.writeByte(itemnames.size());
                for (String names : itemnames) {
                    p.writeString(names);
                }
                break;
            }
            // Load Bride's Wishlist
            case 0xA:
            // 10, 15, 16 = CWishListRecvDlg::OnPacket
            case 0xF:
            // Add Item to Wedding Registry
            case 0xB: {
                // 11 : You have sent a gift | | 13 : Failed to send the gift. | 
                if (mode == 0xB) {
                    p.writeByte(itemnames.size());
                    for (String names : itemnames) {
                        p.writeString(names);
                    }
                }
                p.writeLong(32);
                p.writeByte(items.size());
                for (Item item : items) {
                    addItemInfo(p, item, true);
                }
                break;
            }
            default: {
                log.warn("Unknown Wishlist Mode: {}", mode);
                break;
            }
        }
        return p;
    }
}