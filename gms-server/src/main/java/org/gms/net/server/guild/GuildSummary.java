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
package org.gms.net.server.guild;

/**
 * 公会摘要
 * 存储公会的基本信息用于快速展示，包括公会名称、徽标和联盟ID
 */
public class GuildSummary {
    /** 公会名称 */
    private final String name;
    /** 徽标背景 */
    private final short logoBG;
    /** 徽标背景颜色 */
    private final byte logoBGColor;
    /** 徽标 */
    private final short logo;
    /** 徽标颜色 */
    private final byte logoColor;
    /** 联盟ID */
    private final int allianceId;

    /**
     * 从公会对象构造摘要
     *
     * @param g 公会对象
     */
    public GuildSummary(Guild g) {
        this.name = g.getName();
        this.logoBG = (short) g.getLogoBG();
        this.logoBGColor = (byte) g.getLogoBGColor();
        this.logo = (short) g.getLogo();
        this.logoColor = (byte) g.getLogoColor();
        this.allianceId = g.getAllianceId();
    }

    /**
     * 获取公会名称
     *
     * @return 公会名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取徽标背景
     *
     * @return 徽标背景
     */
    public short getLogoBG() {
        return logoBG;
    }

    /**
     * 获取徽标背景颜色
     *
     * @return 徽标背景颜色
     */
    public byte getLogoBGColor() {
        return logoBGColor;
    }

    /**
     * 获取徽标
     *
     * @return 徽标
     */
    public short getLogo() {
        return logo;
    }

    /**
     * 获取徽标颜色
     *
     * @return 徽标颜色
     */
    public byte getLogoColor() {
        return logoColor;
    }

    /**
     * 获取联盟ID
     *
     * @return 联盟ID
     */
    public int getAllianceId() {
        return allianceId;
    }
}