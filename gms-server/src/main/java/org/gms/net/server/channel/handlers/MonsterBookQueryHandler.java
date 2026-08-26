package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.service.MonsterBookDropSearchService;
import org.gms.util.PacketCreator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * C2S MONSTER_BOOK_QUERY (0x372B) — drop table / item search / item droppers for the Monster Book UI.
 */
public final class MonsterBookQueryHandler extends AbstractPacketHandler {
    private static final int MIN_QUERY_LENGTH = 3;
    private static final int MAX_SEARCH_HITS = 200;
    private static final int MAX_DROPPERS = 200;

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        if (chr == null) {
            return;
        }

        int type = p.readByte();
        switch (type) {
            case 0 -> {
                int mobId = p.readInt();
                LinkedHashMap<Integer, Integer> chances = MonsterBookDropSearchService.mobDropChances(chr, mobId);
                c.sendPacket(PacketCreator.monsterBookDropTable(mobId, chances));
            }
            case 1 -> {
                String query = p.readString();
                if (query == null || query.trim().length() < MIN_QUERY_LENGTH) {
                    c.sendPacket(PacketCreator.monsterBookItemHits(query == null ? "" : query, new int[0]));
                    return;
                }
                int[] hits = MonsterBookDropSearchService.findBookItems(query.trim());
                if (hits.length > MAX_SEARCH_HITS) {
                    hits = Arrays.copyOf(hits, MAX_SEARCH_HITS);
                }
                c.sendPacket(PacketCreator.monsterBookItemHits(query.trim(), hits));
            }
            case 2 -> {
                int itemId = p.readInt();
                LinkedHashMap<Integer, Integer> droppers = MonsterBookDropSearchService.itemDroppers(chr, itemId);
                c.sendPacket(PacketCreator.monsterBookItemDroppers(itemId, cap(droppers, MAX_DROPPERS)));
            }
            default -> {
            }
        }
    }

    private static LinkedHashMap<Integer, Integer> cap(LinkedHashMap<Integer, Integer> src, int max) {
        if (src.size() <= max) {
            return src;
        }
        LinkedHashMap<Integer, Integer> out = new LinkedHashMap<>();
        int n = 0;
        for (Map.Entry<Integer, Integer> e : src.entrySet()) {
            out.put(e.getKey(), e.getValue());
            if (++n >= max) {
                break;
            }
        }
        return out;
    }
}
