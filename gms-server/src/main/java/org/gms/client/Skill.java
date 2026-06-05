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

/**
 * 【类型】Skill（class），包 {@code org.gms.client}。
 * 
 * <p>技能数据对象，封装游戏中的技能信息，包括技能ID、不同等级的效果、
 * 元素属性、动画时间等。此类用于存储和管理游戏中各种职业技能的定义
 * 和属性。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>存储技能的基本信息（ID、职业归属）</li>
 *   <li>管理技能各等级的效果</li>
 *   <li>跟踪技能的元素属性（火/冰/雷等）</li>
 *   <li>管理技能动画播放时间</li>
 *   <li>提供技能类型判断功能</li>
 * </ul>
 * 
 * <p>设计特点：</p>
 * <ul>
 *   <li>不可变ID：技能ID一旦设定不可更改</li>
 *   <li>等级扩展：支持多等级技能效果</li>
 *   <li>元素系统：支持不同元素类型的技能</li>
 *   <li>动画支持：包含动画时间管理</li>
 * </ul>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public class Skill {
    /** 技能的唯一标识符，用于在游戏中识别特定技能 */
    private final int id;
    /** 存储技能各等级对应的效果实例，索引从0开始对应等级1 */
    private final List<StatEffect> effects = new ArrayList<>();
    /** 技能的元素属性，如火、冰、雷等元素类型 */
    private Element element;
    /** 技能动画的持续时间（以毫秒为单位） */
    private int animationTime;
    /** 技能所属的职业编号，通过ID计算得出（ID / 10000） */
    private final int job;
    /** 标识技能是否具有动作效果，影响角色动画播放 */
    private boolean action;

    /**
     * 构造函数，创建技能实例
     * 
     * @param id 技能的唯一标识符
     */
    public Skill(int id) {
        this.id = id;
        this.job = id / 10000;  // 从技能ID中提取职业信息
    }

    /**
     * 获取技能的唯一标识符
     * 
     * @return 技能ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取指定等级的技能效果
     * 
     * @param level 技能等级（从1开始）
     * @return 对应等级的技能效果实例
     */
    public StatEffect getEffect(int level) {
        return effects.get(level - 1);
    }

    /**
     * 获取技能的最大等级
     * 
     * @return 技能可达到的最大等级数
     */
    public int getMaxLevel() {
        return effects.size();
    }

    /**
     * 判断是否为第四转职技能
     * 
     * <p>根据职业代码和特定技能ID判断该技能是否属于第四转职技能。
     * 第四转职技能通常具有特殊的游戏机制和效果。</p>
     * 
     * @return 如果是第四转职技能则返回true，否则返回false
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

    /**
     * 设置技能的元素属性
     * 
     * @param elem 技能的元素类型
     */
    public void setElement(Element elem) {
        element = elem;
    }

    /**
     * 获取技能的元素属性
     * 
     * @return 技能的元素类型
     */
    public Element getElement() {
        return element;
    }

    /**
     * 获取技能动画的持续时间
     * 
     * @return 动画持续时间（毫秒）
     */
    public int getAnimationTime() {
        return animationTime;
    }

    /**
     * 设置技能动画的持续时间
     * 
     * @param time 动画持续时间（毫秒）
     */
    public void setAnimationTime(int time) {
        animationTime = time;
    }

    /**
     * 增加技能动画的持续时间
     * 
     * @param time 要增加的时间（毫秒）
     */
    public void incAnimationTime(int time) {
        animationTime += time;
    }

    /**
     * 判断是否为新手技能
     * 
     * <p>新手技能是指那些不属于特定高级职业的通用技能，
     * 通常ID格式符合特定规则。</p>
     * 
     * @return 如果是新手技能则返回true，否则返回false
     */
    public boolean isBeginnerSkill() {
        return id % 10000000 < 10000;
    }

    /**
     * 设置技能的动作效果状态
     * 
     * @param act 动作效果状态
     */
    public void setAction(boolean act) {
        action = act;
    }

    /**
     * 获取技能的动作效果状态
     * 
     * @return 动作效果状态
     */
    public boolean getAction() {
        return action;
    }

    /**
     * 为技能添加指定等级的效果
     * 
     * <p>此方法用于构建技能的多等级效果体系，每个效果对应一个等级。
     * 效果按添加顺序存储，第一个添加的效果对应等级1，以此类推。</p>
     * 
     * @param effect 要添加的技能效果实例
     */
    public void addLevelEffect(StatEffect effect) {
        effects.add(effect);
    }
}