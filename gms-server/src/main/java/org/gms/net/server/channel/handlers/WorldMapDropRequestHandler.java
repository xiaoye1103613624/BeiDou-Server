package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.Character;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.InPacket;
import org.gms.net.packet.OutPacket;
import org.gms.server.life.MonsterDropEntry;
import org.gms.server.life.MonsterGlobalDropEntry;
import org.gms.server.life.MonsterInformationProvider;

import java.util.Collections;
import java.util.List;

/**
 * World-map drop-data query. The client asks for a mob's drops (plus optional
 * continent-wide global drops) and the server answers with the in-memory drop
 * cache — no per-request DB hit. The effective drop chance is computed against
 * the requesting character's drop-rate / boss-drop-rate / card-rate so the
 * displayed % matches the real probability that character experiences.
 *
 * Wire (request, c2s): int mobId, int mapId, byte includeGlobal
 * Wire (response, s2c WORLD_MAP_DROP_RESULT):
 *   int version, int mobId, int count,
 *   then per entry: int itemId, byte isGlobal, int chanceWeight, int min, int max, int questid
 * where chanceWeight is the effective integer chance (raw * chRate * cardRate),
 * compared against Randomizer.nextInt(999999) on the actual drop roll — divide
 * by 1,000,000 to get the probability. itemId == 0 means meso (min/max = meso range).
 */
public final class WorldMapDropRequestHandler extends AbstractPacketHandler {

    // Matches MapleMap.dropItemsFromMonsterOnMap: Randomizer.nextInt(999999) < chance * chRate * cardRate
    private static final int CHANCE_DENOMINATOR = 1_000_000;
    private static final int CHANCE_CAP = 999_999;

    @Override
    public void handlePacket(InPacket p, Client c) {
        int mobId = p.readInt();
        int mapId = p.readInt();
        boolean includeGlobal = p.readByte() != 0;

        Character self = c.getPlayer();
        if (self == null || mobId <= 0) {
            return;
        }

        MonsterInformationProvider mi = MonsterInformationProvider.getInstance();
        List<MonsterDropEntry> perMob = mi.retrieveEffectiveDrop(mobId);
        List<MonsterGlobalDropEntry> globals = includeGlobal
                ? mi.getRelevantGlobalDrops(mapId > 0 ? mapId : self.getMapId())
                : Collections.emptyList();

        // Player-specific multipliers so the shown % equals this character's real drop rate.
        boolean boss = mi.isBoss(mobId);
        float chRate = boss ? self.getBossDropRate() : self.getDropRate();

        OutPacket out = OutPacket.create(SendOpcode.WORLD_MAP_DROP_RESULT);
        out.writeInt((int) mi.getDropDataVersion());
        out.writeInt(mobId);

        int count = perMob.size() + globals.size();
        out.writeInt(count);

        for (MonsterDropEntry de : perMob) {
            int weight = effectiveWeight(de.chance, chRate, self.getCardRate(de.itemId));
            writeEntry(out, de.itemId, 0, weight, de.Minimum, de.Maximum, de.questid);
        }
        for (MonsterGlobalDropEntry de : globals) {
            int weight = effectiveWeight(de.chance, chRate, self.getCardRate(de.itemId));
            writeEntry(out, de.itemId, 1, weight, de.Minimum, de.Maximum, de.questid);
        }

        c.sendPacket(out);
    }

    private static int effectiveWeight(int rawChance, float chRate, float cardRate) {
        double eff = (double) rawChance * chRate * cardRate;
        if (eff > CHANCE_CAP) {
            eff = CHANCE_CAP;
        }
        return (int) eff;
    }

    private static void writeEntry(OutPacket out, int itemId, int isGlobal, int weight, int min, int max, int questid) {
        out.writeInt(itemId);
        out.writeByte(isGlobal);
        out.writeInt(weight);
        out.writeInt(min);
        out.writeInt(max);
        out.writeInt(questid);
    }
}
