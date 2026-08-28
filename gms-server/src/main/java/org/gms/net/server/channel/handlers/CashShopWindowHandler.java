package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.manager.ServerManager;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.packet.Packet;
import org.gms.server.cashshop.CashShopCatalog;
import org.gms.server.cashshop.CashShopClickType;
import org.gms.server.cashshop.CashShopWindowPackets;
import org.gms.server.cashshop.CashShopWindowPurchase;
import org.gms.service.WindowCashShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CP 0x3730 — 窗口版现金商城操作（不进入商城 Stage）。
 */
public final class CashShopWindowHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(CashShopWindowHandler.class);

    @Override
    public void handlePacket(InPacket p, Client c) {
        final Character chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        if (chr.getCashShop().isOpened()) {
            return;
        }

        final int action = p.readByte();
        switch (action) {
            case CashShopWindowPackets.ACTION_REQUEST_CATALOG -> sendCatalog(c, chr);
            case CashShopWindowPackets.ACTION_REQUEST_CATEGORY -> sendCategory(c, chr, p);
            case CashShopWindowPackets.ACTION_BUY -> buy(c, chr, p);
            case CashShopWindowPackets.ACTION_BUY_CART -> buyCart(c, chr, p);
            case CashShopWindowPackets.ACTION_CLICK_CATEGORY -> clickCategory(c, chr, p);
            default -> log.warn("Cash Shop window: unhandled action {}", action);
        }
    }

    public static void sendCatalog(Client c, Character chr) {
        try {
            final WindowCashShopService svc =
                    ServerManager.getApplicationContext().getBean(WindowCashShopService.class);
            c.sendPacket(CashShopWindowPackets.taxonomy(svc.buildTaxonomy(chr)));
        } catch (Exception e) {
            log.warn("Cash Shop taxonomy skipped: {}", e.toString());
            c.sendPacket(CashShopWindowPackets.taxonomy(java.util.List.of()));
        }
        c.sendPacket(CashShopWindowPackets.index(CashShopCatalog.index()));
        c.sendPacket(CashShopWindowPackets.cash(chr));
    }

    private void sendCategory(Client c, Character chr, InPacket p) {
        if (p.available() < 2) {
            return;
        }
        final int tab = p.readByte() & 0xFF;
        final int cat = p.readByte() & 0xFF;
        try {
            final WindowCashShopService svc =
                    ServerManager.getApplicationContext().getBean(WindowCashShopService.class);
            if (!svc.canAccessLegacyBucket(chr, tab, cat)) {
                for (Packet packet : CashShopWindowPackets.category(tab, cat, java.util.List.of())) {
                    c.sendPacket(packet);
                }
                return;
            }
        } catch (Exception ignored) {
            // fall through
        }
        for (Packet packet : CashShopWindowPackets.category(tab, cat, CashShopCatalog.bucket(tab, cat))) {
            c.sendPacket(packet);
        }
    }

    private void clickCategory(Client c, Character chr, InPacket p) {
        if (p.available() < 4) {
            return;
        }
        final int categoryId = p.readInt();
        try {
            final WindowCashShopService svc =
                    ServerManager.getApplicationContext().getBean(WindowCashShopService.class);
            final CashShopClickType type = svc.resolveClickType(chr, categoryId);
            // OPEN_WINDOW / SHOW_ITEMS 由客户端处理；SEND_PACKET 等服务端可在此扩展白名单
            if (type == CashShopClickType.SEND_PACKET) {
                log.info("Cash Shop SEND_PACKET click categoryId={} chr={}", categoryId, chr.getName());
            }
        } catch (Exception e) {
            log.warn("Cash Shop clickCategory failed: {}", e.toString());
        }
    }

    private void buy(Client c, Character chr, InPacket p) {
        if (p.available() < 4) {
            return;
        }
        final int itemId = p.readInt();
        final CashShopWindowPurchase.Result r =
                CashShopWindowPurchase.buy(c, chr, new int[]{itemId});
        c.sendPacket(CashShopWindowPackets.buyResult(r.code(), itemId));
        if (r.ok()) {
            c.sendPacket(CashShopWindowPackets.cash(chr));
        }
    }

    private void buyCart(Client c, Character chr, InPacket p) {
        if (p.available() < 1) {
            return;
        }
        final int count = p.readByte() & 0xFF;
        if (count <= 0 || count > CashShopWindowPurchase.MAX_CART || p.available() < count * 4) {
            c.sendPacket(CashShopWindowPackets.cartResult(
                    CashShopWindowPackets.BUY_BAD_CART, 0, 0, 0));
            return;
        }
        final int[] sns = new int[count];
        for (int i = 0; i < count; i++) {
            sns[i] = p.readInt();
        }
        final CashShopWindowPurchase.Result r = CashShopWindowPurchase.buy(c, chr, sns);
        c.sendPacket(CashShopWindowPackets.cartResult(
                r.code(), r.failedSn(), r.delivered(), r.spent()));
        if (r.ok()) {
            c.sendPacket(CashShopWindowPackets.cash(chr));
        }
    }
}
