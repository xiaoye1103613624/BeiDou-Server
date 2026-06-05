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
 * 【类型】ReactorStats，class，包 {@code org.gms.server.maps}。
 *
 * <p>反应堆状态统计类，存储反应堆各状态的触发类型、掉落物品、技能列表、超时时间和下一个状态等信息，
 * 内含 {@code StateData} 内部类封装单个状态数据。</p>
 * 
 * <p>ReactorStats 管理反应堆在不同状态下的行为，包括触发类型、物品掉落、状态转换等。
 * 它是反应堆系统的核心数据结构，定义了反应堆的完整生命周期。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>存储反应堆各状态的信息</li>
 *   <li>管理状态转换规则</li>
 *   <li>定义物品掉落和技能激活条件</li>
 *   <li>处理超时机制</li>
 * </ul>
 *
 * @author Lerk
 * @author Ronan
 */
public class ReactorStats {
    /** 反应堆区域的左上角点 */
    private Point tl;
    /** 反应堆区域的右下角点 */
    private Point br;
    /** 状态信息映射表，键为状态值，值为状态数据列表 */
    private final Map<Byte, List<StateData>> stateInfo = new HashMap<>();
    /** 超时信息映射表，键为状态值，值为超时时间 */
    private final Map<Byte, Integer> timeoutInfo = new HashMap<>();

    /**
     * 设置反应堆区域的左上角点
     * 
     * @param tl 左上角点坐标
     */
    public void setTL(Point tl) {
        this.tl = tl;
    }

    /**
     * 设置反应堆区域的右下角点
     * 
     * @param br 右下角点坐标
     */
    public void setBR(Point br) {
        this.br = br;
    }

    /**
     * 获取反应堆区域的左上角点
     * 
     * @return 左上角点坐标
     */
    public Point getTL() {
        return tl;
    }

    /**
     * 获取反应堆区域的右下角点
     * 
     * @return 右下角点坐标
     */
    public Point getBR() {
        return br;
    }

    /**
     * 添加反应堆状态信息
     * 
     * @param state 状态值
     * @param data 状态数据列表
     * @param timeOut 超时时间（毫秒，-1表示无超时）
     */
    public void addState(byte state, List<StateData> data, int timeOut) {
        stateInfo.put(state, data);
        if (timeOut > -1) {
            timeoutInfo.put(state, timeOut);
        }
    }

    /**
     * 添加反应堆状态信息（简化版本）
     * 
     * @param state 状态值
     * @param type 反应堆类型
     * @param reactItem 反应物品（ID和数量）
     * @param nextState 下一个状态
     * @param timeOut 超时时间（毫秒）
     * @param canTouch 是否可触碰
     */
    public void addState(byte state, int type, Pair<Integer, Integer> reactItem, byte nextState, int timeOut, byte canTouch) {
        List<StateData> data = new ArrayList<>();
        data.add(new StateData(type, reactItem, null, nextState));
        stateInfo.put(state, data);
    }

    /**
     * 获取指定状态的超时时间
     * 
     * @param state 状态值
     * @return 超时时间（毫秒），-1表示无超时
     */
    public int getTimeout(byte state) {
        Integer i = timeoutInfo.get(state);
        return (i == null) ? -1 : i;
    }

    /**
     * 获取指定状态的超时后状态
     * 
     * @param state 状态值
     * @return 超时后的下一个状态
     */
    public byte getTimeoutState(byte state) {
        return stateInfo.get(state).get(stateInfo.get(state).size() - 1).getNextState();
    }

    /**
     * 获取指定状态的状态大小（可能的转换数量）
     * 
     * @param state 状态值
     * @return 状态大小
     */
    public byte getStateSize(byte state) {
        return (byte) stateInfo.get(state).size();
    }

    /**
     * 获取指定状态下指定索引的下一个状态
     * 
     * @param state 当前状态
     * @param index 状态转换索引
     * @return 下一个状态值，-1表示无效
     */
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

    /**
     * 获取指定状态下指定索引的激活技能列表
     * 
     * @param state 当前状态
     * @param index 状态转换索引
     * @return 激活的技能ID列表，null表示无激活技能
     */
    public List<Integer> getActiveSkills(byte state, byte index) {
        StateData nextState = stateInfo.get(state).get(index);
        if (nextState != null) {
            return nextState.getActiveSkills();
        } else {
            return null;
        }
    }

    /**
     * 获取指定状态的类型
     * 
     * @param state 状态值
     * @return 反应堆类型，-1表示无效
     */
    public int getType(byte state) {
        List<StateData> list = stateInfo.get(state);
        if (list != null) {
            return list.get(0).getType();
        } else {
            return -1;
        }
    }

    /**
     * 获取指定状态下指定索引的反应物品
     * 
     * @param state 当前状态
     * @param index 状态转换索引
     * @return 反应物品（ID和数量），null表示无反应物品
     */
    public Pair<Integer, Integer> getReactItem(byte state, byte index) {
        StateData nextState = stateInfo.get(state).get(index);
        if (nextState != null) {
            return nextState.getReactItem();
        } else {
            return null;
        }
    }


    /**
     * 状态数据内部类，封装单个状态的数据
     * 
     * <p>StateData 存储反应堆在特定状态下所需的所有信息，
     * 包括类型、反应物品、激活技能和下一个状态。</p>
     */
    public static class StateData {
        /** 反应堆类型 */
        private final int type;
        /** 反应物品（ID和数量） */
        private final Pair<Integer, Integer> reactItem;
        /** 激活的技能ID列表 */
        private final List<Integer> activeSkills;
        /** 下一个状态 */
        private final byte nextState;

        /**
         * 构造函数：创建状态数据实例
         * 
         * @param type 反应堆类型
         * @param reactItem 反应物品（ID和数量）
         * @param activeSkills 激活的技能ID列表
         * @param nextState 下一个状态
         */
        public StateData(int type, Pair<Integer, Integer> reactItem, List<Integer> activeSkills, byte nextState) {
            this.type = type;
            this.reactItem = reactItem;
            this.activeSkills = activeSkills;
            this.nextState = nextState;
        }

        /**
         * 获取反应堆类型
         * 
         * @return 反应堆类型
         */
        private int getType() {
            return type;
        }

        /**
         * 获取下一个状态
         * 
         * @return 下一个状态
         */
        private byte getNextState() {
            return nextState;
        }

        /**
         * 获取反应物品
         * 
         * @return 反应物品（ID和数量）
         */
        private Pair<Integer, Integer> getReactItem() {
            return reactItem;
        }

        /**
         * 获取激活的技能列表
         * 
         * @return 激活的技能ID列表
         */
        private List<Integer> getActiveSkills() {
            return activeSkills;
        }
    }
}