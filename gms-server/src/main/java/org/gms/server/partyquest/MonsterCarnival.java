package org.gms.server.partyquest;

import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.constants.string.LanguageConstants;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.server.TimerManager;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.Reactor;
import org.gms.util.PacketCreator;

import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 怪物嘉年华组队任务
 * 两支队伍在竞技场中召唤怪物互相攻击，通过CP点数决定胜负
 * 支持D、C、B、A四个难度等级
 *
 * @author Drago (Dragohe4rt)
 */
public class MonsterCarnival {

    /** 难度等级：D */
    public static int D = 3;
    /** 难度等级：C */
    public static int C = 2;
    /** 难度等级：B */
    public static int B = 1;
    /** 难度等级：A */
    public static int A = 0;

    /** 红队和蓝队 */
    private Party p1, p2;
    /** 竞技场地图 */
    private MapleMap map;
    /** 定时器、特效定时器、怪物刷新任务 */
    private ScheduledFuture<?> timer, effectTimer, respawnTask;
    /** 开始时间戳 */
    private long startTime = 0;
    /** 红队和蓝队召唤次数 */
    private int summonsR = 0, summonsB = 0;
    /** 房间号 */
    private int room = 0;
    /** 红队队长和蓝队队长 */
    private Character leader1, leader2, team1, team2;
    /** 红队和蓝队CP值 */
    private int redCP, blueCP;
    /** 红队和蓝队总CP值 */
    private int redTotalCP, blueTotalCP;
    /** 红队和蓝队时间结束时的CP值 */
    private int redTimeupCP, blueTimeupCP;
    /** 是否为CPQ1模式 */
    private boolean cpq1;

    public MonsterCarnival(Party p1, Party p2, int mapid, boolean cpq1, int room) {
        try {
            this.cpq1 = cpq1;
            this.room = room;
            this.p1 = p1;
            this.p2 = p2;
            Channel cs = Server.getInstance().getWorld(p2.getLeader().getWorld()).getChannel(p2.getLeader().getChannel());
            p1.setEnemy(p2);
            p2.setEnemy(p1);
            map = cs.getMapFactory().getDisposableMap(mapid);
            startTime = System.currentTimeMillis() + MINUTES.toMillis(10);
            int redPortal = 0;
            int bluePortal = 0;
            if (map.isPurpleCPQMap()) {
                redPortal = 2;
                bluePortal = 1;
            }
            for (PartyCharacter mpc : p1.getMembers()) {
                Character mc = mpc.getPlayer();
                if (mc != null) {
                    mc.setMonsterCarnival(this);
                    mc.setTeam(0);
                    mc.setFestivalPoints(0);
                    mc.forceChangeMap(map, map.getPortal(redPortal));
                    mc.dropMessage(6, LanguageConstants.getMessage(mc, LanguageConstants.CPQEntry));
                    if (p1.getLeader().getId() == mc.getId()) {
                        leader1 = mc;
                    }
                    team1 = mc;
                }
            }
            for (PartyCharacter mpc : p2.getMembers()) {
                Character mc = mpc.getPlayer();
                if (mc != null) {
                    mc.setMonsterCarnival(this);
                    mc.setTeam(1);
                    mc.setFestivalPoints(0);
                    mc.forceChangeMap(map, map.getPortal(bluePortal));
                    mc.dropMessage(6, LanguageConstants.getMessage(mc, LanguageConstants.CPQEntry));
                    if (p2.getLeader().getId() == mc.getId()) {
                        leader2 = mc;
                    }
                    team2 = mc;
                }
            }
            if (team1 == null || team2 == null) {
                for (PartyCharacter mpc : p1.getMembers()) {
                    Character chr = mpc.getPlayer();
                    if (chr != null) {
                        chr.dropMessage(5, LanguageConstants.getMessage(chr, LanguageConstants.CPQError));
                    }
                }
                for (PartyCharacter mpc : p2.getMembers()) {
                    Character chr = mpc.getPlayer();
                    if (chr != null) {
                        chr.dropMessage(5, LanguageConstants.getMessage(chr, LanguageConstants.CPQError));
                    }
                }
                return;
            }

            // thanks Atoot, Vcoc for noting double CPQ functional being sent to players in CPQ start

            timer = TimerManager.getInstance().schedule(() -> timeUp(), SECONDS.toMillis(map.getTimeDefault())); // thanks Atoot for noticing an irregular "event extended" issue here
            effectTimer = TimerManager.getInstance().schedule(() -> complete(), SECONDS.toMillis(map.getTimeDefault() - 10));
            respawnTask = TimerManager.getInstance().register(() -> respawn(), GameConfig.getServerLong("respawn_interval"));

            cs.initMonsterCarnival(cpq1, room);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新竞技场怪物
     */
    private void respawn() {
        map.respawn();
    }

    /**
     * 处理玩家断线，找到其所属队伍并通知所有玩家
     *
     * @param charid 断线玩家ID
     */
    public void playerDisconnected(int charid) {
        int team = -1;
        for (PartyCharacter mpc : leader1.getParty().getMembers()) {
            if (mpc.getId() == charid) {
                team = 0;
            }
        }
        for (PartyCharacter mpc : leader2.getParty().getMembers()) {
            if (mpc.getId() == charid) {
                team = 1;
            }
        }
        for (Character chrMap : map.getAllPlayers()) {
            if (team == -1) {
                team = 1;
            }
            String teamS = "";
            switch (team) {
                case 0:
                    teamS = LanguageConstants.getMessage(chrMap, LanguageConstants.CPQRed);
                    break;
                case 1:
                    teamS = LanguageConstants.getMessage(chrMap, LanguageConstants.CPQBlue);
                    break;
            }
            chrMap.dropMessage(5, teamS + LanguageConstants.getMessage(chrMap, LanguageConstants.CPQPlayerExit));
        }
        earlyFinish();
    }

    /**
     * 提前结束嘉年华
     */
    private void earlyFinish() {
        dispose(true);
    }

    /**
     * 处理玩家离开队伍
     *
     * @param charid 离开玩家ID
     */
    public void leftParty(int charid) {
        playerDisconnected(charid);
    }

    /**
     * 清理资源，不传送玩家
     */
    protected void dispose() {
        dispose(false);
    }

    /**
     * 判断红队是否可以召唤怪物
     *
     * @return true表示可以召唤
     */
    public boolean canSummonR() {
        return summonsR < map.getMaxMobs();
    }

    /**
     * 红队召唤怪物计数加1
     */
    public void summonR() {
        summonsR++;
    }

    /**
     * 判断蓝队是否可以召唤怪物
     *
     * @return true表示可以召唤
     */
    public boolean canSummonB() {
        return summonsB < map.getMaxMobs();
    }

    /**
     * 蓝队召唤怪物计数加1
     */
    public void summonB() {
        summonsB++;
    }

    /**
     * 判断红队是否可以放置守护者
     *
     * @return true表示可以放置
     */
    public boolean canGuardianR() {
        int teamReactors = 0;
        for (Reactor react : map.getAllReactors()) {
            if (react.getName().substring(0, 1).contentEquals("0")) {
                teamReactors += 1;
            }
        }

        return teamReactors < map.getMaxReactors();
    }

    /**
     * 判断蓝队是否可以放置守护者
     *
     * @return true表示可以放置
     */
    public boolean canGuardianB() {
        int teamReactors = 0;
        for (Reactor react : map.getAllReactors()) {
            if (react.getName().substring(0, 1).contentEquals("1")) {
                teamReactors += 1;
            }
        }

        return teamReactors < map.getMaxReactors();
    }

    /**
     * 清理嘉年华资源，可选传送玩家回城
     *
     * @param warpout 是否传送玩家出去
     */
    protected void dispose(boolean warpout) {
        Channel cs = map.getChannelServer();
        MapleMap out;
        if (!cpq1) { // cpq2
            out = cs.getMapFactory().getMap(980030010);
        } else {
            out = cs.getMapFactory().getMap(980000010);
        }
        for (PartyCharacter mpc : leader1.getParty().getMembers()) {
            Character mc = mpc.getPlayer();
            if (mc != null) {
                mc.resetCP();
                mc.setTeam(-1);
                mc.setMonsterCarnival(null);
                if (warpout) {
                    mc.changeMap(out, out.getPortal(0));
                }
            }
        }
        for (PartyCharacter mpc : leader2.getParty().getMembers()) {
            Character mc = mpc.getPlayer();
            if (mc != null) {
                mc.resetCP();
                mc.setTeam(-1);
                mc.setMonsterCarnival(null);
                if (warpout) {
                    mc.changeMap(out, out.getPortal(0));
                }
            }
        }
        if (this.timer != null) {
            this.timer.cancel(true);
            this.timer = null;
        }
        if (this.effectTimer != null) {
            this.effectTimer.cancel(true);
            this.effectTimer = null;
        }
        if (this.respawnTask != null) {
            this.respawnTask.cancel(true);
            this.respawnTask = null;
        }
        redTotalCP = 0;
        blueTotalCP = 0;
        leader1.getParty().setEnemy(null);
        leader2.getParty().setEnemy(null);
        map.dispose();
        map = null;

        cs.finishMonsterCarnival(cpq1, room);
    }

    /**
     * 退出嘉年华
     */
    public void exit() {
        dispose();
    }

    /**
     * 获取定时器
     *
     * @return 定时任务
     */
    public ScheduledFuture<?> getTimer() {
        return this.timer;
    }

    /**
     * 结束嘉年华，根据胜负传送玩家到对应地图并发放节日积分
     *
     * @param winningTeam 获胜队伍编号（0=红队赢，1=蓝队赢）
     */
    private void finish(int winningTeam) {
        try {
            Channel cs = map.getChannelServer();
            if (winningTeam == 0) {
                for (PartyCharacter mpc : leader1.getParty().getMembers()) {
                    Character mc = mpc.getPlayer();
                    if (mc != null) {
                        mc.gainFestivalPoints(this.redTotalCP);
                        mc.setMonsterCarnival(null);
                        if (cpq1) {
                            mc.changeMap(cs.getMapFactory().getMap(map.getId() + 2), cs.getMapFactory().getMap(map.getId() + 2).getPortal(0));
                        } else {
                            mc.changeMap(cs.getMapFactory().getMap(map.getId() + 200), cs.getMapFactory().getMap(map.getId() + 200).getPortal(0));
                        }
                        mc.setTeam(-1);
                        mc.dispelDebuffs();
                    }
                }
                for (PartyCharacter mpc : leader2.getParty().getMembers()) {
                    Character mc = mpc.getPlayer();
                    if (mc != null) {
                        mc.gainFestivalPoints(this.blueTotalCP);
                        mc.setMonsterCarnival(null);
                        if (cpq1) {
                            mc.changeMap(cs.getMapFactory().getMap(map.getId() + 3), cs.getMapFactory().getMap(map.getId() + 3).getPortal(0));
                        } else {
                            mc.changeMap(cs.getMapFactory().getMap(map.getId() + 300), cs.getMapFactory().getMap(map.getId() + 300).getPortal(0));
                        }
                        mc.setTeam(-1);
                        mc.dispelDebuffs();
                    }
                }
            } else if (winningTeam == 1) {
                for (PartyCharacter mpc : leader2.getParty().getMembers()) {
                    Character mc = mpc.getPlayer();
                    if (mc != null) {
                        mc.gainFestivalPoints(this.blueTotalCP);
                        mc.setMonsterCarnival(null);
                        if (cpq1) {
                            mc.changeMap(cs.getMapFactory().getMap(map.getId() + 2), cs.getMapFactory().getMap(map.getId() + 2).getPortal(0));
                        } else {
                            mc.changeMap(cs.getMapFactory().getMap(map.getId() + 200), cs.getMapFactory().getMap(map.getId() + 200).getPortal(0));
                        }
                        mc.setTeam(-1);
                        mc.dispelDebuffs();
                    }
                }
                for (PartyCharacter mpc : leader1.getParty().getMembers()) {
                    Character mc = mpc.getPlayer();
                    if (mc != null) {
                        mc.gainFestivalPoints(this.redTotalCP);
                        mc.setMonsterCarnival(null);
                        if (cpq1) {
                            mc.changeMap(cs.getMapFactory().getMap(map.getId() + 3), cs.getMapFactory().getMap(map.getId() + 3).getPortal(0));
                        } else {
                            mc.changeMap(cs.getMapFactory().getMap(map.getId() + 300), cs.getMapFactory().getMap(map.getId() + 300).getPortal(0));
                        }
                        mc.setTeam(-1);
                        mc.dispelDebuffs();
                    }
                }
            }
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 时间到，判断CP较高的一方获胜，平局则延长比赛时间
     */
    private void timeUp() {
        int cp1 = this.redTimeupCP;
        int cp2 = this.blueTimeupCP;
        if (cp1 == cp2) {
            extendTime();
            return;
        }
        if (cp1 > cp2) {
            finish(0);
        } else {
            finish(1);
        }
    }

    /**
     * 获取剩余时间（毫秒）
     *
     * @return 剩余时间
     */
    public long getTimeLeft() {
        return (startTime - System.currentTimeMillis());
    }

    /**
     * 获取剩余时间（秒）
     *
     * @return 剩余秒数
     */
    public int getTimeLeftSeconds() {
        return (int) (getTimeLeft() / 1000);
    }

    /**
     * 延长比赛时间3分钟
     */
    private void extendTime() {
        for (Character chrMap : map.getAllPlayers()) {
            chrMap.dropMessage(5, LanguageConstants.getMessage(chrMap, LanguageConstants.CPQExtendTime));
        }
        startTime = System.currentTimeMillis() + MINUTES.toMillis(3);

        map.broadcastMessage(PacketCreator.getClock((int) MINUTES.toSeconds(3)));

        timer = TimerManager.getInstance().schedule(() -> timeUp(), SECONDS.toMillis(map.getTimeExpand()));
        // thanks Vcoc for noticing a time set issue here
        effectTimer = TimerManager.getInstance().schedule(() -> complete(), SECONDS.toMillis(map.getTimeExpand() - 10));
    }

    /**
     * 比赛完成时记录双方当前CP值，清除怪物并向玩家发送胜负特效
     */
    public void complete() {
        int cp1 = this.redTotalCP;
        int cp2 = this.blueTotalCP;

        this.redTimeupCP = cp1;
        this.blueTimeupCP = cp2;

        if (cp1 == cp2) {
            return;
        }
        boolean redWin = cp1 > cp2;
        int chnl = leader1.getClient().getChannel();
        int chnl1 = leader2.getClient().getChannel();
        if (chnl != chnl1) {
            throw new RuntimeException("Os lideres estao em canais diferentes.");
        }

        map.killAllMonsters();
        for (PartyCharacter mpc : leader1.getParty().getMembers()) {
            Character mc = mpc.getPlayer();
            if (mc != null) {
                if (redWin) {
                    mc.sendPacket(PacketCreator.showEffect("quest/carnival/win"));
                    mc.sendPacket(PacketCreator.playSound("MobCarnival/Win"));
                    mc.dispelDebuffs();
                } else {
                    mc.sendPacket(PacketCreator.showEffect("quest/carnival/lose"));
                    mc.sendPacket(PacketCreator.playSound("MobCarnival/Lose"));
                    mc.dispelDebuffs();
                }
            }
        }
        for (PartyCharacter mpc : leader2.getParty().getMembers()) {
            Character mc = mpc.getPlayer();
            if (mc != null) {
                if (!redWin) {
                    mc.sendPacket(PacketCreator.showEffect("quest/carnival/win"));
                    mc.sendPacket(PacketCreator.playSound("MobCarnival/Win"));
                    mc.dispelDebuffs();
                } else {
                    mc.sendPacket(PacketCreator.showEffect("quest/carnival/lose"));
                    mc.sendPacket(PacketCreator.playSound("MobCarnival/Lose"));
                    mc.dispelDebuffs();
                }
            }
        }
    }

    public Party getRed() {
        return p1;
    }

    public void setRed(Party p1) {
        this.p1 = p1;
    }

    public Party getBlue() {
        return p2;
    }

    public void setBlue(Party p2) {
        this.p2 = p2;
    }

    public Character getLeader1() {
        return leader1;
    }

    public void setLeader1(Character leader1) {
        this.leader1 = leader1;
    }

    public Character getLeader2() {
        return leader2;
    }

    public void setLeader2(Character leader2) {
        this.leader2 = leader2;
    }

    /**
     * 获取敌方队伍的队长
     *
     * @param team 本方队伍编号（0=红队，返回蓝队队长；1=蓝队，返回红队队长）
     * @return 敌方队长
     */
    public Character getEnemyLeader(int team) {
        switch (team) {
            case 0:
                return leader2;
            case 1:
                return leader1;
        }
        return null;
    }

    public int getBlueCP() {
        return blueCP;
    }

    public void setBlueCP(int blueCP) {
        this.blueCP = blueCP;
    }

    public int getBlueTotalCP() {
        return blueTotalCP;
    }

    public void setBlueTotalCP(int blueTotalCP) {
        this.blueTotalCP = blueTotalCP;
    }

    public int getRedCP() {
        return redCP;
    }

    public void setRedCP(int redCP) {
        this.redCP = redCP;
    }

    public int getRedTotalCP() {
        return redTotalCP;
    }

    public void setRedTotalCP(int redTotalCP) {
        this.redTotalCP = redTotalCP;
    }

    /**
     * 获取指定队伍的总CP值
     *
     * @param team 队伍编号（0=红队，1=蓝队）
     * @return 总CP值
     */
    public int getTotalCP(int team) {
        if (team == 0) {
            return redTotalCP;
        } else if (team == 1) {
            return blueTotalCP;
        } else {
            throw new RuntimeException("Equipe desconhecida");
        }
    }

    /**
     * 设置指定队伍的总CP值
     *
     * @param totalCP 总CP值
     * @param team    队伍编号（0=红队，1=蓝队）
     */
    public void setTotalCP(int totalCP, int team) {
        if (team == 0) {
            this.redTotalCP = totalCP;
        } else if (team == 1) {
            this.blueTotalCP = totalCP;
        }
    }

    /**
     * 获取指定队伍的当前CP值
     *
     * @param team 队伍编号（0=红队，1=蓝队）
     * @return 当前CP值
     */
    public int getCP(int team) {
        if (team == 0) {
            return redCP;
        } else if (team == 1) {
            return blueCP;
        } else {
            throw new RuntimeException("Equipe desconhecida" + team);
        }
    }

    /**
     * 设置指定队伍的当前CP值
     *
     * @param CP   当前CP值
     * @param team 队伍编号（0=红队，1=蓝队）
     */
    public void setCP(int CP, int team) {
        if (team == 0) {
            this.redCP = CP;
        } else if (team == 1) {
            this.blueCP = CP;
        }
    }

    /**
     * 获取房间号
     *
     * @return 房间号
     */
    public int getRoom() {
        return this.room;
    }

    /**
     * 获取竞技场地图
     *
     * @return 竞技场地图
     */
    public MapleMap getEventMap() {
        return this.map;
    }
}