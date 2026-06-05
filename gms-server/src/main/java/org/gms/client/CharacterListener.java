package org.gms.client;

import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 角色状态监听器实现类
 * <p>
 * 实现了 {@link AbstractCharacterListener} 接口，用于监听角色的各种状态变化，
 * 如血量变化、HP/MP池更新、属性更新等，并触发相应的处理逻辑。
 * </p>
 *
 * @author Ronan
 * @see AbstractCharacterListener
 * @see Character
 */
public class CharacterListener implements AbstractCharacterListener {
    /**
     * 监听的角色对象
     * <p>此监听器所绑定的 {@link Character} 对象，用于执行具体的状态变更操作</p>
     */
    private final Character character;
    
    /**
     * 构造函数
     * <p>创建一个新的角色监听器实例</p>
     *
     * @param character 需要监听的角色对象，不能为空
     */
    public CharacterListener(Character character) {
        this.character = character;
    }

    @Override
    public void onHpChanged(int oldHp) {
        // 当角色血量发生变化时调用此方法，执行相关的血量变化动作
        character.hpChangeAction(oldHp);
    }

    @Override
    public void onHpMpPoolUpdate() {
        // 重新计算本地角色属性统计信息，获取HP/MP更新列表
        List<Pair<Stat, Integer>> hpmpupdate = character.recalcLocalStats();
        for (Pair<Stat, Integer> p : hpmpupdate) {
            // 将每个属性更新值存储到statUpdates映射中
            character.statUpdates.put(p.getLeft(), p.getRight());
        }

        // 确保角色当前血量不超过最大血量限制
        if (character.hp > character.localMaxHp) {
            character.setHp(character.localMaxHp);
            character.statUpdates.put(Stat.HP, character.hp);
        }

        // 确保角色当前魔法值不超过最大魔法值限制
        if (character.mp > character.localMaxMp) {
            character.setMp(character.localMaxMp);
            character.statUpdates.put(Stat.MP, character.mp);
        }
    }

    @Override
    public void onStatUpdate() {
        // 当角色属性需要更新时，重新计算本地角色的统计数据
        character.recalcLocalStats();
    }

    @Override
    public void onAnnounceStatPoolUpdate() {
        // 创建一个新的属性更新列表，容量为8以优化性能
        List<Pair<Stat, Integer>> statup = new ArrayList<>(8);
        // 遍历所有待更新的属性条目，并将其添加到更新列表中
        for (Map.Entry<Stat, Integer> s : character.statUpdates.entrySet()) {
            statup.add(new Pair<>(s.getKey(), s.getValue()));
        }

        // 发送玩家状态更新数据包到客户端，通知属性变更
        character.sendPacket(PacketCreator.updatePlayerStats(statup, true, character));
    }
}