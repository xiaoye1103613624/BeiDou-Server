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
    private int spMaxLevel = -1;

    public Skill(int id) {
        this.id = id;
        this.job = id / 10000;
    }

    public int getId() {
        return id;
    }

    public StatEffect getEffect(int level) {
        return effects.get(level - 1);
    }

    public int getMaxLevel() {
        return effects.size();
    }

    public int getSpMaxLevel() {
        if (spMaxLevel > 0) {
            return Math.min(spMaxLevel, getMaxLevel() > 0 ? getMaxLevel() : spMaxLevel);
        }
        return getMaxLevel();
    }

    public void setSpMaxLevel(int spMaxLevel) {
        this.spMaxLevel = spMaxLevel;
    }

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