package org.gms.server.life;

import org.gms.client.Character;

/**
 * 【接口】MonsterListener：由 `life` 模块实现的契约。
 */
public interface MonsterListener {

    void monsterKilled(int aniTime);
    void monsterDamaged(Character from, int trueDmg);
    void monsterHealed(int trueHeal);
}
