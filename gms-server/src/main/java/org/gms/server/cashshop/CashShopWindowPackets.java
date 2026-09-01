package org.gms.server.cashshop;

import org.gms.client.Character;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.OutPacket;
import org.gms.net.packet.Packet;
import org.gms.server.CashShop;

import java.util.ArrayList;
import java.util.List;

/**
 * 窗口版现金商城封包（LP 0x3731 / CP 0x3730）。
 * 字段顺序必须与客户端 {@code CashShopWnd_HandleSync} 一致。
 */
public final class CashShopWindowPackets {

    public static final int ACTION_REQUEST_CATALOG = 0;
    public static final int ACTION_BUY = 1;
    public static final int ACTION_BUY_CART = 2;
    public static final int ACTION_REQUEST_CATEGORY = 3;
    /** 非 SHOW_ITEMS 分类点击：+ categoryId(int) */
    public static final int ACTION_CLICK_CATEGORY = 4;
    /**
     * 背包 UI「扩充」：+ cashType(int) + invType(byte)。
     * 与经典商城 0x06 mode0 同价同量（4000 NX → +4），不要求 cs.isOpened。
     */
    public static final int ACTION_EXPAND_SLOTS = 5;

    public static final int RESP_OPEN = 0;
    public static final int RESP_CATALOG = 1;
    public static final int RESP_BUY = 2;
    public static final int RESP_CASH = 3;
    public static final int RESP_BUY_CART = 4;
    public static final int RESP_INDEX = 5;
    /** 动态分类树（门控过滤后） */
    public static final int RESP_TAXONOMY = 6;

    public static final int MAX_NAME = 40;

    /** clickType 字节：与 CashShopClickType 序一致 */
    public static final int CLICK_SHOW_ITEMS = 0;
    public static final int CLICK_OPEN_WINDOW = 1;
    public static final int CLICK_SEND_PACKET = 2;
    public static final int CLICK_RUN_NPC = 3;
    public static final int CLICK_WARP = 4;

    public record TaxonomyNode(int id, int parentId, int clickType, String clickParam,
                               int legacyTab, int legacyCat, int itemCount, String name) {
    }

    public static final int BUY_OK = 0;
    public static final int BUY_NO_NX = 1;
    public static final int BUY_UNKNOWN_ITEM = 2;
    public static final int BUY_INVENTORY_FULL = 3;
    public static final int BUY_NOT_ON_SALE = 4;
    public static final int BUY_BUSY = 5;
    public static final int BUY_BAD_CART = 6;

    private static final int CHUNK = 400;

    private CashShopWindowPackets() {
    }

    public static Packet open(Character chr) {
        final OutPacket p = OutPacket.create(SendOpcode.CASHSHOP_WINDOW_SYNC);
        p.writeByte(RESP_OPEN);
        writeCash(p, chr);
        return p;
    }

    public static Packet cash(Character chr) {
        final OutPacket p = OutPacket.create(SendOpcode.CASHSHOP_WINDOW_SYNC);
        p.writeByte(RESP_CASH);
        writeCash(p, chr);
        return p;
    }

    public static Packet buyResult(int code, int itemId) {
        final OutPacket p = OutPacket.create(SendOpcode.CASHSHOP_WINDOW_SYNC);
        p.writeByte(RESP_BUY);
        p.writeByte(code);
        p.writeInt(itemId);
        return p;
    }

    public static Packet cartResult(int code, int itemId, int delivered, int spent) {
        final OutPacket p = OutPacket.create(SendOpcode.CASHSHOP_WINDOW_SYNC);
        p.writeByte(RESP_BUY_CART);
        p.writeByte(code);
        p.writeInt(itemId);
        p.writeByte(delivered);
        p.writeInt(spent);
        return p;
    }

    public static Packet index(List<int[]> idx) {
        final OutPacket p = OutPacket.create(SendOpcode.CASHSHOP_WINDOW_SYNC);
        p.writeByte(RESP_INDEX);
        p.writeShort(idx.size());
        for (int[] e : idx) {
            p.writeByte(e[0]);
            p.writeByte(e[1]);
            p.writeInt(e[2]);
        }
        return p;
    }

    /**
     * 动态分类。字段顺序须与客户端 {@code kResp_Taxonomy} 一致。
     */
    public static Packet taxonomy(List<TaxonomyNode> nodes) {
        final OutPacket p = OutPacket.create(SendOpcode.CASHSHOP_WINDOW_SYNC);
        p.writeByte(RESP_TAXONOMY);
        p.writeShort(nodes.size());
        for (TaxonomyNode n : nodes) {
            p.writeInt(n.id());
            p.writeInt(n.parentId());
            p.writeByte(n.clickType());
            p.writeString(n.clickParam() == null ? "" : n.clickParam());
            p.writeByte(Math.max(0, Math.min(255, n.legacyTab())));
            p.writeByte(Math.max(0, Math.min(255, n.legacyCat())));
            p.writeInt(n.itemCount());
            String name = n.name() == null ? "" : n.name();
            if (name.length() > MAX_NAME) {
                name = name.substring(0, MAX_NAME);
            }
            p.writeString(name);
        }
        return p;
    }

    public static List<Packet> category(int tab, int cat, List<CashShopCatalog.Row> rows) {
        final List<Packet> out = new ArrayList<>();
        final int total = rows.size();

        for (int i = 0; i < total || i == 0; i += CHUNK) {
            final int end = Math.min(i + CHUNK, total);
            final OutPacket p = OutPacket.create(SendOpcode.CASHSHOP_WINDOW_SYNC);
            p.writeByte(RESP_CATALOG);
            p.writeByte(i == 0 ? 1 : 0);
            p.writeByte(tab);
            p.writeByte(cat);
            p.writeShort(end - i);
            for (int j = i; j < end; j++) {
                final CashShopCatalog.Row r = rows.get(j);
                p.writeInt(r.itemId());
                p.writeInt(r.price());
                p.writeShort(r.count());
                p.writeByte(r.tab());
                p.writeByte(r.category());
                p.writeString(r.name());
            }
            out.add(p);
            if (total == 0) {
                break;
            }
        }
        return out;
    }

    private static void writeCash(OutPacket p, Character chr) {
        final CashShop cs = chr.getCashShop();
        p.writeInt(cs.getCash(CashShop.NX_CREDIT));
        p.writeInt(cs.getCash(CashShop.MAPLE_POINT));
        p.writeInt(cs.getCash(CashShop.NX_PREPAID));
    }
}
