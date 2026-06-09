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
package org.gms.server.maps;

import org.gms.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 反应器属性
 * 存储反应器的碰撞框、状态数据和超时配置
 * 支持多状态切换，每个状态可配置触发条件和掉落物品
 *
 * @author Lerk
 * @author Ronan
 */
public class ReactorStats {
    /** 左上角坐标 */
    private Point tl;
    /** 右下角坐标 */
    private Point br;
    /** 状态数据映射（状态 -> 状态数据列表） */
    private final Map<Byte, List<StateData>> stateInfo = new HashMap<>();
    /** 超时信息映射（状态 -> 超时时间） */
    private final Map<Byte, Integer> timeoutInfo = new HashMap<>();

    public void setTL(Point tl) {
        this.tl = tl;
    }

    public void setBR(Point br) {
        this.br = br;
    }

    public Point getTL() {
        return tl;
    }

    public Point getBR() {
        return br;
    }

    /**
     * 添加状态数据（含超时）
     *
     * @param state   状态编号
     * @param data    状态数据列表
     * @param timeOut 超时时间（毫秒），-1表示无超时
     */
    public void addState(byte state, List<StateData> data, int timeOut) {
        stateInfo.put(state, data);
        if (timeOut > -1) {
            timeoutInfo.put(state, timeOut);
        }
    }

    public void addState(byte state, int type, Pair<Integer, Integer> reactItem, byte nextState, int timeOut, byte canTouch) {
        List<StateData> data = new ArrayList<>();
        data.add(new StateData(type, reactItem, null, nextState));
        stateInfo.put(state, data);
    }

    public int getTimeout(byte state) {
        Integer i = timeoutInfo.get(state);
        return (i == null) ? -1 : i;
    }

    public byte getTimeoutState(byte state) {
        return stateInfo.get(state).get(stateInfo.get(state).size() - 1).getNextState();
    }

    public byte getStateSize(byte state) {
        return (byte) stateInfo.get(state).size();
    }

    public byte getNextState(byte state, byte index) {
        if (stateInfo.get(state) == null || stateInfo.get(state).size() < (index + 1)) {
            return -1;
        }
        StateData nextState = stateInfo.get(state).get(index);
        if (nextState != null) {
            return nextState.getNextState();
        } else {
            return -1;
        }
    }

    public List<Integer> getActiveSkills(byte state, byte index) {
        StateData nextState = stateInfo.get(state).get(index);
        if (nextState != null) {
            return nextState.getActiveSkills();
        } else {
            return null;
        }
    }

    public int getType(byte state) {
        List<StateData> list = stateInfo.get(state);
        if (list != null) {
            return list.get(0).getType();
        } else {
            return -1;
        }
    }

    public Pair<Integer, Integer> getReactItem(byte state, byte index) {
        StateData nextState = stateInfo.get(state).get(index);
        if (nextState != null) {
            return nextState.getReactItem();
        } else {
            return null;
        }
    }


    /**
     * 反应器状态数据（内部类）
     * 存储单个状态的反应类型、反应物品、激活技能和下一状态
     */
    public static class StateData {
        /** 反应类型 */
        private final int type;
        /** 反应物品（物品ID, 数量） */
        private final Pair<Integer, Integer> reactItem;
        /** 激活技能ID列表 */
        private final List<Integer> activeSkills;
        /** 下一状态编号 */
        private final byte nextState;

        public StateData(int type, Pair<Integer, Integer> reactItem, List<Integer> activeSkills, byte nextState) {
            this.type = type;
            this.reactItem = reactItem;
            this.activeSkills = activeSkills;
            this.nextState = nextState;
        }

        private int getType() {
            return type;
        }

        private byte getNextState() {
            return nextState;
        }

        private Pair<Integer, Integer> getReactItem() {
            return reactItem;
        }

        private List<Integer> getActiveSkills() {
            return activeSkills;
        }
    }
}