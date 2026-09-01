package org.gms.client.creator.novice;

import org.gms.client.creator.CharacterFactoryRecipe;

/**
 * 冒险家创建角色投骰属性（单项 4～13，总和 25）。
 * 客户端封包末尾追加的四字节视为不可信输入，必须经 {@link #isValid()} 校验后再落库。
 */
public record AdventurerDiceStats(int str, int dex, int intelligence, int luk) {

    public static final int MIN_STAT = 4;
    public static final int MAX_STAT = 13;
    public static final int REQUIRED_TOTAL = 25;

    public boolean isValid() {
        return inRange(str) && inRange(dex) && inRange(intelligence) && inRange(luk)
                && str + dex + intelligence + luk == REQUIRED_TOTAL;
    }

    public void applyTo(CharacterFactoryRecipe recipe) {
        if (!isValid()) {
            throw new IllegalStateException("invalid dice stats");
        }
        recipe.setStr(str);
        recipe.setDex(dex);
        recipe.setInt(intelligence);
        recipe.setLuk(luk);
        // 经典投骰：四维已占满 25 点，1 级剩余 AP 为 0
        recipe.setRemainingAp(0);
    }

    private static boolean inRange(int value) {
        return value >= MIN_STAT && value <= MAX_STAT;
    }
}
