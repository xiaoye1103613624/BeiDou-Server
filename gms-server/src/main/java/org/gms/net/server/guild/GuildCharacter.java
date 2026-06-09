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

import org.gms.client.Character;

/**
 * 公会角色
 * 封装公会中角色的基本信息，包括等级、职业、公会等级、联盟等级等
 */
public class GuildCharacter {
    /** 角色对象 */
    private Character character;
    /** 角色等级 */
    private int level;
    /** 角色ID */
    private final int id;
    /** 所在世界 */
    private int world;
    /** 所在频道 */
    private int channel;
    /** 职业ID */
    private int jobid;
    /** 公会等级 */
    private int guildrank;
    /** 公会ID */
    private int guildid;
    /** 联盟等级 */
    private int allianceRank;
    /** 是否在线 */
    private boolean online;
    /** 角色名称 */
    private final String name;

    /**
     * 从在线角色构造公会角色
     *
     * @param chr 在线角色
     */
    public GuildCharacter(Character chr) {
        this.character = chr;
        this.name = chr.getName();
        this.level = chr.getLevel();
        this.id = chr.getId();
        this.channel = chr.getClient().getChannel();
        this.world = chr.getWorld();
        this.jobid = chr.getJob().getId();
        this.guildrank = chr.getGuildRank();
        this.guildid = chr.getGuildId();
        this.online = true;
        this.allianceRank = chr.getAllianceRank();
    }

    /**
     * 从数据库数据构造公会角色
     *
     * @param chr           角色对象（可为null）
     * @param _id           角色ID
     * @param _lv           等级
     * @param _name         名称
     * @param _channel      频道
     * @param _world        世界
     * @param _job          职业ID
     * @param _rank         公会等级
     * @param _gid          公会ID
     * @param _on           是否在线
     * @param _allianceRank 联盟等级
     */
    public GuildCharacter(Character chr, int _id, int _lv, String _name, int _channel, int _world, int _job, int _rank, int _gid, boolean _on, int _allianceRank) {
        this.character = chr;
        this.level = _lv;
        this.id = _id;
        this.name = _name;
        if (_on) {
            this.channel = _channel;
            this.world = _world;
        }
        this.jobid = _job;
        this.online = _on;
        this.guildrank = _rank;
        this.guildid = _gid;
        this.allianceRank = _allianceRank;
    }

    /**
     * 设置角色对象
     *
     * @param ch 角色对象
     */
    public void setCharacter(Character ch) {
        this.character = ch;
    }

    /**
     * 获取角色对象
     *
     * @return 角色对象
     */
    public Character getCharacter() {
        return character;
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
     * 设置角色等级
     *
     * @param l 等级
     */
    public void setLevel(int l) {
        level = l;
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
     * 设置频道
     *
     * @param ch 频道号
     */
    public void setChannel(int ch) {
        channel = ch;
    }

    /**
     * 获取频道
     *
     * @return 频道号
     */
    public int getChannel() {
        return channel;
    }

    /**
     * 获取世界ID
     *
     * @return 世界ID
     */
    public int getWorld() {
        return world;
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
     * 设置职业ID
     *
     * @param job 职业ID
     */
    public void setJobId(int job) {
        jobid = job;
    }

    /**
     * 获取公会ID
     *
     * @return 公会ID
     */
    public int getGuildId() {
        return guildid;
    }

    /**
     * 设置公会ID
     *
     * @param gid 公会ID
     */
    public void setGuildId(int gid) {
        guildid = gid;
        character.setGuildId(gid);
    }

    /**
     * 获取公会等级
     *
     * @return 公会等级
     */
    public int getGuildRank() {
        return guildrank;
    }

    /**
     * 设置离线公会等级
     *
     * @param rank 公会等级
     */
    public void setOfflineGuildRank(int rank) {
        guildrank = rank;
    }

    /**
     * 设置公会等级
     *
     * @param rank 公会等级
     */
    public void setGuildRank(int rank) {
        guildrank = rank;
        character.setGuildRank(rank);
    }

    /**
     * 获取联盟等级
     *
     * @return 联盟等级
     */
    public int getAllianceRank() {
        return allianceRank;
    }

    /**
     * 设置联盟等级
     *
     * @param rank 联盟等级
     */
    public void setAllianceRank(int rank) {
        allianceRank = rank;
        character.setAllianceRank(rank);
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
     * @param f 在线状态
     */
    public void setOnline(boolean f) {
        online = f;
    }

    /**
     * 获取角色名称
     *
     * @return 角色名称
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof GuildCharacter o) {
            return (o.getId() == id && o.getName().equals(name));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.id;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}