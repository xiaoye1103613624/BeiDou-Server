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

package org.gms.server.partyquest;

import org.gms.client.Character;
import org.gms.net.server.Server;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 组队任务抽象基类
 * 定义组队任务的基本框架，管理队伍成员、频道和世界信息
 * 子类需实现具体任务逻辑
 *
 * @author kevintjuh93
 */
public class PartyQuest {
    private static final Logger log = LoggerFactory.getLogger(PartyQuest.class);

    /** 频道 */
    int channel;
    /** 世界 */
    int world;
    /** 队伍 */
    Party party;
    /** 参与者列表 */
    List<Character> participants = new ArrayList<>();

    /**
     * 构造组队任务
     * 自动筛选同频道同地图的队伍成员
     *
     * @param party 队伍
     */
    public PartyQuest(Party party) {
        this.party = party;
        PartyCharacter leader = party.getLeader();
        channel = leader.getChannel();
        world = leader.getWorld();
        int mapid = leader.getMapId();
        for (PartyCharacter pchr : party.getMembers()) {
            if (pchr.getChannel() == channel && pchr.getMapId() == mapid) {
                Character chr = Server.getInstance().getWorld(world).getChannel(channel).getPlayerStorage().getCharacterById(pchr.getId());
                if (chr != null) {
                    this.participants.add(chr);
                }
            }
        }
    }

    /**
     * 获取组队
     *
     * @return 队伍对象
     */
    public Party getParty() {
        return party;
    }

    /**
     * 获取参与者列表
     *
     * @return 参与者列表
     */
    public List<Character> getParticipants() {
        return participants;
    }

    /**
     * 移除参与者
     *
     * @param chr 要移除的参与者
     * @throws Throwable 可能抛出异常
     */
    public void removeParticipant(Character chr) throws Throwable {
        synchronized (participants) {
            participants.remove(chr);
            chr.setPartyQuest(null);
        }
    }

    /**
     * 根据组队任务名称和玩家等级计算经验奖励
     *
     * @param PQ    组队任务名称
     * @param level 玩家等级
     * @return 经验值
     */
    public static int getExp(String PQ, int level) {
        switch (PQ) {
        case "HenesysPQ":
            return 1250 * level / 5;
        case "KerningPQFinal":
            return 500 * level / 5;
        case "KerningPQ4th":
            return 400 * level / 5;
        case "KerningPQ3rd":
            return 300 * level / 5;
        case "KerningPQ2nd":
            return 200 * level / 5;
        case "KerningPQ1st":
            return 100 * level / 5;
        case "LudiMazePQ":
            return 2000 * level / 5;
        case "LudiPQ1st":
            return 100 * level / 5;
        case "LudiPQ2nd":
            return 250 * level / 5;
        case "LudiPQ3rd":
            return 350 * level / 5;
        case "LudiPQ4th":
            return 350 * level / 5;
        case "LudiPQ5th":
            return 400 * level / 5;
        case "LudiPQ6th":
            return 450 * level / 5;
        case "LudiPQ7th":
            return 500 * level / 5;
        case "LudiPQ8th":
            return 650 * level / 5;
        case "LudiPQLast":
            return 800 * level / 5;
        default:
            log.warn("Unhandled PartyQuest: {}", PQ);
            return 0;
        }
    }
}