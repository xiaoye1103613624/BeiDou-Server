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
package org.gms.server.life;

import org.gms.server.maps.AbstractAnimatedMapObject;

/**
 * 已加载的生命体抽象基类
 * 继承AbstractAnimatedMapObject，为NPC和怪物提供共同的基础属性，包括：
 * 朝向(f)、隐藏状态(hide)、站立平台(fh)、Y坐标偏移(cy)、边界范围(rx0/rx1)
 * 子类包括{@link NPC}和{@link Monster}
 *
 * @author OdinMS Team
 */
public abstract class AbstractLoadedLife extends AbstractAnimatedMapObject {
    /** 生命体ID（怪物ID或NPC ID） */
    private final int id;
    /** 朝向（0=右, 1=左） */
    private int f;
    /** 是否隐藏 */
    private boolean hide;
    /** 当前站立平台ID */
    private int fh;
    /** 初始站立平台ID */
    private int start_fh;
    /** Y坐标偏移量 */
    private int cy;
    /** 左边界X偏移 */
    private int rx0;
    /** 右边界X偏移 */
    private int rx1;

    /**
     * 根据ID构造生命体
     *
     * @param id 生命体ID
     */
    public AbstractLoadedLife(int id) {
        this.id = id;
    }

    /**
     * 复制构造（从已有生命体复制属性）
     * 用于怪物重生等场景，保留原始配置
     *
     * @param life 源生命体
     */
    public AbstractLoadedLife(AbstractLoadedLife life) {
        this(life.getId());
        this.f = life.f;
        this.hide = life.hide;
        this.fh = life.fh;
        this.start_fh = life.fh;
        this.cy = life.cy;
        this.rx0 = life.rx0;
        this.rx1 = life.rx1;
    }

    /**
     * 获取朝向
     *
     * @return 朝向（0=右, 1=左）
     */
    public int getF() {
        return f;
    }

    /**
     * 设置朝向
     *
     * @param f 朝向（0=右, 1=左）
     */
    public void setF(int f) {
        this.f = f;
    }

    /**
     * 是否隐藏
     *
     * @return true表示隐藏，不显示在地图上
     */
    public boolean isHidden() {
        return hide;
    }

    /**
     * 设置隐藏状态
     *
     * @param hide 隐藏状态
     */
    public void setHide(boolean hide) {
        this.hide = hide;
    }

    /**
     * 获取当前站立平台ID
     *
     * @return 平台ID
     */
    public int getFh() {
        return fh;
    }

    /**
     * 设置当前站立平台ID
     *
     * @param fh 平台ID
     */
    public void setFh(int fh) {
        this.fh = fh;
    }

    /**
     * 获取初始站立平台ID
     *
     * @return 初始平台ID
     */
    public int getStartFh() {
        return start_fh;
    }

    /**
     * 获取Y坐标偏移量
     *
     * @return Y坐标偏移
     */
    public int getCy() {
        return cy;
    }

    /**
     * 设置Y坐标偏移量
     *
     * @param cy Y坐标偏移
     */
    public void setCy(int cy) {
        this.cy = cy;
    }

    /**
     * 获取左边界X偏移
     *
     * @return 左边界X偏移
     */
    public int getRx0() {
        return rx0;
    }

    /**
     * 设置左边界X偏移
     *
     * @param rx0 左边界X偏移
     */
    public void setRx0(int rx0) {
        this.rx0 = rx0;
    }

    /**
     * 获取右边界X偏移
     *
     * @return 右边界X偏移
     */
    public int getRx1() {
        return rx1;
    }

    /**
     * 设置右边界X偏移
     *
     * @param rx1 右边界X偏移
     */
    public void setRx1(int rx1) {
        this.rx1 = rx1;
    }

    /**
     * 获取生命体ID
     *
     * @return 生命体ID（怪物ID或NPC ID）
     */
    public int getId() {
        return id;
    }
}