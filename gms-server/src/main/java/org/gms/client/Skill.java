/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.client;

import org.gms.server.StatEffect;
import org.gms.server.life.Element;

import java.util.ArrayList;
import java.util.List;

public class Skill {
    private final int id;
    private final List<StatEffect> effects = new ArrayList<>();
    private Element element;
    private int animationTime;
    private final int job;
    private boolean action;
    /** 玩家手动加点上限（技改前原始最高等级）；战斗生效上限仍为 {@link #getMaxLevel()}。 */
    private int spMaxLevel = -1;

    public Skill(int id) {
        this.id = id;
        this.job = id / 10000;
    }

    public int getId() {
        return id;
    }

    public StatEffect getEffect(int level) {
        int idx = Math.max(0, Math.min(level, effects.size()) - 1);
        return effects.get(idx);
    }

    public int getMaxLevel() {
        return effects.size();
    }

    /**
     * 手动 SP / 技能窗加点上限。未设置技改时等于 WZ 等级节点数。
     */
    public int getSpMaxLevel() {
        if (spMaxLevel > 0) {
            return Math.min(spMaxLevel, getMaxLevel() > 0 ? getMaxLevel() : spMaxLevel);
        }
        return getMaxLevel();
    }

    public void setSpMaxLevel(int spMaxLevel) {
        this.spMaxLevel = spMaxLevel;
    }

    /**
     * 将效果等级扩展到 targetMax（不足则克隆末级并套用覆盖属性）。
     * @param levelOverrides key=等级(1-based)，value=属性覆盖（damage/mpCon/...）
     */
    public void applyTechExtension(int targetMax, java.util.Map<Integer, java.util.Map<String, Integer>> levelOverrides) {
        if (targetMax <= 0) {
            return;
        }
        while (effects.size() < targetMax) {
            StatEffect base = effects.isEmpty() ? null : effects.get(effects.size() - 1);
            int nextLv = effects.size() + 1;
            java.util.Map<String, Integer> ov = levelOverrides != null ? levelOverrides.get(nextLv) : null;
            if (base == null) {
                break;
            }
            effects.add(base.copyWithOverrides(ov));
        }
        if (levelOverrides != null) {
            for (java.util.Map.Entry<Integer, java.util.Map<String, Integer>> e : levelOverrides.entrySet()) {
                int lv = e.getKey() == null ? 0 : e.getKey();
                if (lv <= 0 || lv > effects.size() || e.getValue() == null || e.getValue().isEmpty()) {
                    continue;
                }
                StatEffect cur = effects.get(lv - 1);
                effects.set(lv - 1, cur.copyWithOverrides(e.getValue()));
            }
        }
    }

    /**
     * Whether login / SET_FIELD skill blob writes a trailing masterLevel int.
     * Must match vanilla client {@code sub_4E8F04} (job%10==2 only, plus Evan exceptions).
     * Hyper books (job%10==3) stay false — Cosmic/MapleRoot same; writing masterLevel without
     * a matching decode patch causes EOF-38. Hyper unlock is skillLevel via UPDATE_SKILLS /
     * ijl15 CUISkill unlock, not SET_FIELD masterLevel.
     */
    public boolean isFourthJob() {
        if (job == 2212) {
            return false;
        }
        if (id == 22170001 || id == 22171003 || id == 22171004 || id == 22181002 || id == 22181003) {
            return true;
        }
        return job % 10 == 2;
    }

    public void setElement(Element elem) {
        element = elem;
    }

    public Element getElement() {
        return element;
    }

    public int getAnimationTime() {
        return animationTime;
    }

    public void setAnimationTime(int time) {
        animationTime = time;
    }

    public void incAnimationTime(int time) {
        animationTime += time;
    }

    public boolean isBeginnerSkill() {
        return id % 10000000 < 10000;
    }

    public void setAction(boolean act) {
        action = act;
    }

    public boolean getAction() {
        return action;
    }

    public void addLevelEffect(StatEffect effect) {
        effects.add(effect);
    }
}