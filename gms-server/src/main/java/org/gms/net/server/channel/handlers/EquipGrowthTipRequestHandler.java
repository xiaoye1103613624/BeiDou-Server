package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Equip;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.packet.Packet;
import org.gms.server.equipgrowth.EquipGrowthTipManager;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端悬停装备时可选拉取成长属性 tip（Recv 0x17D）。
 * 优先由变更点推送（forceUpdateItem / 升级 / 强化）；悬停仅在缺动态数据时请求。
 * <p>
 * 性能：同角色+装备指纹短缓存，避免悬停重复 build；热路径日志用 debug。
 * 规范见 resource_doc/开发文档/客户端展示与服务端校验-性能安全规范.md
 */
public final class EquipGrowthTipRequestHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(EquipGrowthTipRequestHandler.class);
    private static final Path DIAG_LOG = Path.of("logs", "equip_growth_tip.log");
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long CACHE_TTL_MS = 3_000L;
    private static final ConcurrentHashMap<String, CacheEntry> BUILD_CACHE = new ConcurrentHashMap<>();
    private static final String VERSION = "GROWTH_TIP_CHAIN_20260803";

    private record CacheEntry(boolean has, String text, int[] growthBonus, long expireAtMs) {
        boolean alive(long now) {
            return now < expireAtMs;
        }
    }

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        int itemId = p.readInt();
        if (itemId <= 0) {
            return;
        }

        boolean has = false;
        String text = "";
        int enhance = -1;
        int itemLevel = -1;
        int scroll = -1;
        boolean found = false;
        int[] growthBonus = new int[15];
        int[] flameBonus = null;
        boolean fromCache = false;

        try {
            Equip equip = EquipGrowthTipManager.findEquip(chr, itemId);
            found = equip != null;
            if (equip != null) {
                enhance = equip.getEnhance() & 0xFF;
                itemLevel = equip.getItemLevel() & 0xFF;
                scroll = equip.getLevel() & 0xFF;
                flameBonus = EquipGrowthTipManager.flameBonusByStatIndex(equip);
                String cacheKey = chr.getId() + ":" + itemId + ":" + enhance + ":" + itemLevel + ":"
                        + scroll + ":" + equip.getExGradeOption();
                long now = System.currentTimeMillis();
                CacheEntry cached = BUILD_CACHE.get(cacheKey);
                if (cached != null && cached.alive(now)) {
                    has = cached.has();
                    text = cached.text();
                    growthBonus = cached.growthBonus() != null
                            ? Arrays.copyOf(cached.growthBonus(), cached.growthBonus().length)
                            : new int[15];
                    fromCache = true;
                } else {
                    has = EquipGrowthTipManager.hasGrowthData(equip);
                    text = has ? EquipGrowthTipManager.buildGrowthText(equip) : "";
                    if (has) {
                        growthBonus = EquipGrowthTipManager.growthBonusByStatIndex(itemId, itemLevel);
                    }
                    BUILD_CACHE.put(cacheKey, new CacheEntry(has, text,
                            has ? Arrays.copyOf(growthBonus, growthBonus.length) : null,
                            now + CACHE_TTL_MS));
                }
            } else if (EquipGrowthTipManager.hasGrowthDataForItemId(itemId)) {
                // WZ 可成长但实例查找失败时仍回包，避免客户端永久空缓存
                String cacheKey = chr.getId() + ":" + itemId + ":wzonly";
                long now = System.currentTimeMillis();
                CacheEntry cached = BUILD_CACHE.get(cacheKey);
                if (cached != null && cached.alive(now)) {
                    has = cached.has();
                    text = cached.text();
                    fromCache = true;
                } else {
                    has = true;
                    itemLevel = 1;
                    text = EquipGrowthTipManager.buildWzOnlyGrowthText(itemId);
                    BUILD_CACHE.put(cacheKey, new CacheEntry(true, text, null, now + CACHE_TTL_MS));
                }
            }
        } catch (Throwable t) {
            log.error("equipGrowthTip failed chr={} itemId={}", chr.getId(), itemId, t);
            diag("ERR chr=" + chr.getId() + " itemId=" + itemId + " ex=" + t.getClass().getSimpleName()
                    + " " + t.getMessage());
            has = false;
            text = "";
            flameBonus = null;
        }

        log.debug("equipGrowthTip chr={} itemId={} found={} enhance={} itemLevel={} scroll={} has={} textLen={} cache={}",
                chr.getId(), itemId, found, enhance, itemLevel, scroll, has,
                text == null ? 0 : text.length(), fromCache);
        diag(String.format("chr=%d itemId=%d found=%s enhance=%d itemLevel=%d scroll=%d has=%s textLen=%d cache=%s",
                chr.getId(), itemId, found, enhance, itemLevel, scroll, has,
                text == null ? 0 : text.length(), fromCache));

        // 成长/火花均可单独下发：无成长文案时 has 仍可为 false，但 flame 尾块照常
        Packet packet = PacketCreator.equipGrowthTip(
                itemId, has, text, has ? growthBonus : null, flameBonus);
        c.sendPacket(packet);
        int nbytes = 0;
        try {
            byte[] raw = packet.getBytes();
            nbytes = raw != null ? raw.length : -1;
        } catch (Throwable ignored) {
            nbytes = -2;
        }
        diag(String.format("sent opcode=0x17B chr=%d itemId=%d has=%s textLen=%d bytes=%d VERSION=%s",
                chr.getId(), itemId, has, text == null ? 0 : text.length(), nbytes, VERSION));

        // 偶发清理过期项，避免 map 无限涨
        if (BUILD_CACHE.size() > 2048) {
            long now = System.currentTimeMillis();
            BUILD_CACHE.entrySet().removeIf(e -> !e.getValue().alive(now));
        }
    }

    private static void diag(String line) {
        try {
            Path parent = DIAG_LOG.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            String row = TS.format(LocalDateTime.now()) + " " + line + System.lineSeparator();
            Files.writeString(DIAG_LOG, row, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {
            // 诊断日志失败不影响游戏
        }
    }
}
