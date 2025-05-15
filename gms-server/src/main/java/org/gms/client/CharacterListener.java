package org.gms.client;

import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 玩家 监听器，用于处理角色相关事件。
 */
public class CharacterListener implements AbstractCharacterListener {
    private final Character character;
    public CharacterListener(Character character) {
        this.character = character;
    }

    /**
     * 当角色生命值发生变化时触发的方法
     *
     * @param oldHp 角色变化前的生命值
     */
    @Override
    public void onHpChanged(int oldHp) {
        character.hpChangeAction(oldHp);
    }

    /**
     * 当生命值和魔法值池更新时调用此方法。
     *
     * <p>重新计算角色的本地属性并更新角色的生命值和魔法值。
     * 如果角色的生命值或魔法值超过了本地最大值，则将其设置为本地最大值，并更新角色的属性更新映射。
     */
    @Override
    public void onHpMpPoolUpdate() {
        List<Pair<Stat, Integer>> hpmpupdate = character.recalcLocalStats();
        for (Pair<Stat, Integer> p : hpmpupdate) {
            character.statUpdates.put(p.getLeft(), p.getRight());
        }

        if (character.hp > character.localMaxHp) {
            character.setHp(character.localMaxHp);
            character.statUpdates.put(Stat.HP, character.hp);
        }

        if (character.mp > character.localMaxMp) {
            character.setMp(character.localMaxMp);
            character.statUpdates.put(Stat.MP, character.mp);
        }
    }

    @Override
    public void onStatUpdate() {
        character.recalcLocalStats();
    }

    @Override
    public void onAnnounceStatPoolUpdate() {
        List<Pair<Stat, Integer>> statup = new ArrayList<>(8);
        for (Map.Entry<Stat, Integer> s : character.statUpdates.entrySet()) {
            statup.add(new Pair<>(s.getKey(), s.getValue()));
        }

        character.sendPacket(PacketCreator.updatePlayerStats(statup, true, character));
    }
}
