package org.gms.server.partyquest;

import org.gms.client.Character;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;

import java.util.LinkedList;
import java.util.List;

/**
 * 怪物嘉年华队伍
 * 管理嘉年华中单支队伍的成员和状态，支持队伍传送和胜负判定
 *
 * @author Rob
 */
public class MonsterCarnivalParty {

    /** 队伍成员列表 */
    private List<Character> members = new LinkedList<>();
    /** 队长 */
    private final Character leader;
    /** 队伍编号（0=红队，1=蓝队） */
    private final byte team;
    /** 召唤次数 */
    private int summons = 8;
    /** 是否获胜 */
    private boolean winner = false;

    public MonsterCarnivalParty(final Character owner, final List<Character> members1, final byte team1) {
        leader = owner;
        members = members1;
        team = team1;

        for (final Character chr : members) {
            chr.setMonsterCarnivalParty(this);
            chr.setTeam(team);
        }
    }

    /**
     * 获取队长
     *
     * @return 队长
     */
    public final Character getLeader() {
        return leader;
    }

    /**
     * 获取队伍成员列表
     *
     * @return 成员列表
     */
    public List<Character> getMembers() {
        return members;
    }

    /**
     * 获取队伍编号
     *
     * @return 队伍编号（0=红队，1=蓝队）
     */
    public int getTeam() {
        return team;
    }

    /**
     * 将所有成员传送到指定地图并清理队伍状态
     *
     * @param map 目标地图ID
     */
    public void warpOut(final int map) {
        for (Character chr : members) {
            chr.changeMap(map, 0);
            chr.setMonsterCarnivalParty(null);
            chr.setMonsterCarnival(null);
        }
        members.clear();
    }

    /**
     * 将所有成员传送到指定地图的指定传送点
     *
     * @param map      目标地图
     * @param portalid 传送点ID
     */
    public void warp(final MapleMap map, final int portalid) {
        for (Character chr : members) {
            chr.changeMap(map, map.getPortal(portalid));
        }
    }

    /**
     * 根据胜负结果传送成员到对应奖励房间
     */
    public void warpOut() {
        if (winner == true) {
            warpOut(980000003 + (leader.getMonsterCarnival().getRoom() * 100));
        } else {
            warpOut(980000004 + (leader.getMonsterCarnival().getRoom() * 100));
        }
    }

    /**
     * 检查所有成员是否都在指定地图中
     *
     * @param map 地图
     * @return true表示全在
     */
    public boolean allInMap(MapleMap map) {
        boolean status = true;
        for (Character chr : members) {
            if (chr.getMap() != map) {
                status = false;
            }
        }
        return status;
    }

    /**
     * 移除成员并传送回等待地图
     *
     * @param chr 要移除的玩家
     */
    public void removeMember(Character chr) {
        members.remove(chr);
        chr.changeMap(980000010);
        chr.setMonsterCarnivalParty(null);
        chr.setMonsterCarnival(null);
    }

    /**
     * 判断是否获胜
     *
     * @return true表示获胜
     */
    public boolean isWinner() {
        return winner;
    }

    /**
     * 设置获胜状态
     *
     * @param status 获胜状态
     */
    public void setWinner(boolean status) {
        winner = status;
    }

    /**
     * 向所有成员展示比赛结果特效（胜利或失败）
     */
    public void displayMatchResult() {
        final String effect = winner ? "quest/carnival/win" : "quest/carnival/lose";

        for (final Character chr : members) {
            chr.sendPacket(PacketCreator.showEffect(effect));
        }
    }

    /**
     * 消耗一次召唤次数
     */
    public void summon() {
        this.summons--;
    }

    /**
     * 判断是否还有剩余召唤次数
     *
     * @return true表示可以召唤
     */
    public boolean canSummon() {
        return this.summons > 0;
    }
}