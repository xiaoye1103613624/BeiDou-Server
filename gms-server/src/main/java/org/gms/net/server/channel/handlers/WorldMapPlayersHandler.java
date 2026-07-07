package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.InPacket;
import org.gms.net.packet.OutPacket;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class WorldMapPlayersHandler extends AbstractPacketHandler {

    private static final int MAX_PLAYERS = 40;

    private static final long CACHE_TTL_MS = 750L;
    private record CacheEntry(long expiresAt, List<Found> found) {}
    private static final ConcurrentHashMap<String, CacheEntry> CACHE = new ConcurrentHashMap<>();

    private record Found(String name, int level, int channel) {}

    @Override
    public void handlePacket(InPacket p, Client c) {
        int mapId = p.readInt();
        Character self = c.getPlayer();
        if (self == null || mapId <= 0) {
            return;
        }

        World world = c.getWorldServer();
        if (world == null) {
            return;
        }

        boolean requesterIsGM = self.isGM();

        long now = System.currentTimeMillis();
        String cacheKey = world.getId() + ":" + mapId + ":" + (requesterIsGM ? 1 : 0);
        CacheEntry cached = CACHE.get(cacheKey);
        List<Found> found;
        if (cached != null && now < cached.expiresAt()) {
            found = cached.found();
        } else {
            found = new ArrayList<>();
            try {
                outer:
                for (Channel ch : world.getChannels()) {
                    int channelId = ch.getId();
                    for (Character chr : ch.getPlayerStorage().getAllCharacters()) {
                        if (chr == null || chr.getMapId() != mapId) {
                            continue;
                        }
                        if (chr.isHidden() && !requesterIsGM) {
                            continue;
                        }
                        found.add(new Found(chr.getName(), chr.getLevel(), channelId));
                        if (found.size() >= MAX_PLAYERS) {
                            break outer;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            CACHE.values().removeIf(e -> now >= e.expiresAt());
            CACHE.put(cacheKey, new CacheEntry(now + CACHE_TTL_MS, found));
        }

        OutPacket out = OutPacket.create(SendOpcode.WORLD_MAP_PLAYERS);
        out.writeInt(mapId);
        out.writeShort(found.size());
        for (Found f : found) {
            out.writeString(f.name());
            out.writeShort(f.level());
            out.writeByte(f.channel());
        }
        c.sendPacket(out);
    }
}
