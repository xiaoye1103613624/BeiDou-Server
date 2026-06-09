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
package org.gms.net.server.world;

import org.gms.client.Character;
import org.gms.client.Job;

/**
 * 组队角色
 * 封装组队中角色的基本信息，包括等级、职业、地图ID等
 *
 * @author kevintjuh93
 */
public class PartyCharacter {
    /** 角色名称 */
    private final String name;
    /** 角色ID */
    private int id;
    /** 角色等级 */
    private int level;
    /** 所在频道 */
    private int channel;
    /** 所在世界 */
    private int world;
    /** 职业ID */
    private int jobid;
    /** 所在地图ID */
    private int mapid;
    /** 是否在线 */
    private boolean online;
    /** 职业对象 */
    private Job job;
    /** 角色对象 */
    private Character character;

    /**
     * 从在线角色构造组队角色
     *
     * @param maplechar 在线角色
     */
    public PartyCharacter(Character maplechar) {
        this.character = maplechar;
        this.name = maplechar.getName();
        this.level = maplechar.getLevel();
        this.channel = maplechar.getClient().getChannel();
        this.world = maplechar.getWorld();
        this.id = maplechar.getId();
        this.jobid = maplechar.getJob().getId();
        this.mapid = maplechar.getMapId();
        this.online = true;
        this.job = maplechar.getJob();
    }

    /**
     * 无参构造（创建空组队角色）
     */
    public PartyCharacter() {
        this.name = "";
    }

    /**
     * 获取角色对象
     *
     * @return 角色对象
     */
    public Character getPlayer() {
        return character;
    }

    /**
     * 获取职业对象
     *
     * @return 职业对象
     */
    public Job getJob() {
        return job;
    }

    /**
     * 获取角色等级
     *
     * @return 等级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 获取所在频道
     *
     * @return 频道号
     */
    public int getChannel() {
        return channel;
    }

    /**
     * 设置所在频道
     *
     * @param channel 频道号
     */
    public void setChannel(int channel) {
        this.channel = channel;
    }

    /**
     * 是否为队长
     *
     * @return 是否队长
     */
    public boolean isLeader() {
        return getPlayer().isPartyLeader();
    }

    /**
     * 是否在线
     *
     * @return 在线状态
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * 设置在线状态
     *
     * @param online 在线状态
     */
    public void setOnline(boolean online) {
        this.online = online;
        if (!online) {
            // thanks Feras for noticing offline party members retaining whole character object unnecessarily
            this.character = null;
        }
    }

    /**
     * 获取所在地图ID
     *
     * @return 地图ID
     */
    public int getMapId() {
        return mapid;
    }

    /**
     * 设置所在地图ID
     *
     * @param mapid 地图ID
     */
    public void setMapId(int mapid) {
        this.mapid = mapid;
    }

    /**
     * 获取角色名称
     *
     * @return 角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取角色ID
     *
     * @return 角色ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取职业ID
     *
     * @return 职业ID
     */
    public int getJobId() {
        return jobid;
    }

    /**
     * 获取公会ID
     *
     * @return 公会ID
     */
    public int getGuildId() {
        return character.getGuildId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PartyCharacter other = (PartyCharacter) obj;
        if (name == null) {
            return other.name == null;
        } else {
            return name.equals(other.name);
        }
    }

    /**
     * 获取所在世界
     *
     * @return 世界ID
     */
    public int getWorld() {
        return world;
    }

}