package org.gms.server.life;

import org.gms.client.Character;

/**
 * 怪物事件监听器接口
 * 定义怪物生命周期事件的回调：击杀、受伤和治愈
 */
public interface MonsterListener {

    void monsterKilled(int aniTime);
    void monsterDamaged(Character from, Long trueDmg);
    void monsterHealed(Long trueHeal);
}