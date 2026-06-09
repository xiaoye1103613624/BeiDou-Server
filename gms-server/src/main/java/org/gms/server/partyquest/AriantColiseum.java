/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
import org.gms.constants.game.GameConstants;
import org.gms.constants.id.ItemId;
import org.gms.constants.id.MapId;
import org.gms.server.TimerManager;
import org.gms.server.expeditions.Expedition;
import org.gms.server.expeditions.ExpeditionType;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 阿里安特竞技场
 * 远征队进入竞技场挑战怪物，通过击杀获取积分和碎片奖励
 * 支持多层级奖励等级和定时计分板
 *
 * @author Ronan
 */
public class AriantColiseum {

    /** 关联的远征队 */
    private Expedition exped;
    /** 竞技场地图 */
    private MapleMap map;

    /** 玩家积分映射 */
    private final Map<Character, Integer> score;
    /** 玩家奖励等级映射 */
    private final Map<Character, Integer> rewardTier;
    /** 积分是否已变更 */
    private boolean scoreDirty = false;

    /** 定时更新任务 */
    private ScheduledFuture<?> ariantUpdate;
    /** 定时结束任务 */
    private ScheduledFuture<?> ariantFinish;
    /** 定时计分板任务 */
    private ScheduledFuture<?> ariantScoreboard;

    /** 丢失的碎片数 */
    private int lostShards = 0;

    /** 事件是否已通关 */
    private boolean eventClear = false;

    public AriantColiseum(MapleMap eventMap, Expedition expedition) {
        exped = expedition;
        exped.finishRegistration();

        map = eventMap;
        map.resetFully();

        long pqTimer = MINUTES.toMillis(10);
        long pqTimerBoard = MINUTES.toMillis(9) + SECONDS.toMillis(50);

        List<Character> players = exped.getActiveMembers();
        score = new HashMap<>();
        rewardTier = new HashMap<>();
        for (Character mc : players) {
            mc.changeMap(map, 0);
            mc.setAriantColiseum(this);
            mc.updateAriantScore();
            rewardTier.put(mc, 0);
        }

        for (Character mc : players) {
            mc.sendPacket(PacketCreator.updateAriantPQRanking(score));
        }

        setAriantScoreBoard(TimerManager.getInstance().schedule(() -> showArenaResults(), pqTimerBoard));

        setArenaFinish(TimerManager.getInstance().schedule(() -> enterKingsRoom(), pqTimer));

        setArenaUpdate(TimerManager.getInstance().register(() -> broadcastAriantScoreUpdate(), 500, 500));
    }

    private void setArenaUpdate(ScheduledFuture<?> ariantUpdate) {
        this.ariantUpdate = ariantUpdate;
    }

    private void setArenaFinish(ScheduledFuture<?> arenaFinish) {
        this.ariantFinish = arenaFinish;
    }

    private void setAriantScoreBoard(ScheduledFuture<?> ariantScore) {
        this.ariantScoreboard = ariantScore;
    }

    private void cancelArenaUpdate() {
        if (ariantUpdate != null) {
            ariantUpdate.cancel(true);
            ariantUpdate = null;
        }
    }

    private void cancelArenaFinish() {
        if (ariantFinish != null) {
            ariantFinish.cancel(true);
            ariantFinish = null;
        }
    }

    private void cancelAriantScoreBoard() {
        if (ariantScoreboard != null) {
            ariantScoreboard.cancel(true);
            ariantScoreboard = null;
        }
    }

    private void cancelAriantSchedules() {
        cancelArenaUpdate();
        cancelArenaFinish();
        cancelAriantScoreBoard();
    }

    /**
     * 获取玩家当前积分
     *
     * @param chr 玩家
     * @return 积分值
     */
    public int getAriantScore(Character chr) {
        Integer chrScore = score.get(chr);
        return chrScore != null ? chrScore : 0;
    }

    /**
     * 清除玩家积分
     *
     * @param chr 玩家
     */
    public void clearAriantScore(Character chr) {
        score.remove(chr);
    }

    /**
     * 更新玩家积分
     *
     * @param chr    玩家
     * @param points 新的积分值
     */
    public void updateAriantScore(Character chr, int points) {
        if (map != null) {
            score.put(chr, points);
            scoreDirty = true;
        }
    }

    /**
     * 向所有玩家广播积分更新
     */
    private void broadcastAriantScoreUpdate() {
        if (scoreDirty) {
            for (Character chr : score.keySet()) {
                chr.sendPacket(PacketCreator.updateAriantPQRanking(score));
            }
            scoreDirty = false;
        }
    }

    /**
     * 获取玩家奖励等级
     *
     * @param chr 玩家
     * @return 奖励等级
     */
    public int getAriantRewardTier(Character chr) {
        Integer reward = rewardTier.get(chr);
        return reward != null ? reward : 0;
    }

    /**
     * 清除玩家奖励等级
     *
     * @param chr 玩家
     */
    public void clearAriantRewardTier(Character chr) {
        rewardTier.remove(chr);
    }

    /**
     * 增加丢失的碎片计数
     *
     * @param quantity 丢失数量
     */
    public void addLostShards(int quantity) {
        lostShards += quantity;
    }

    /**
     * 玩家离开竞技场
     *
     * @param chr 玩家
     */
    public void leaveArena(Character chr) {
        if (!(eventClear && GameConstants.isAriantColiseumArena(chr.getMapId()))) {
            leaveArenaInternal(chr);
        }
    }

    /**
     * 内部离开竞技场逻辑，同步移除并回收碎片
     *
     * @param chr 玩家
     */
    private synchronized void leaveArenaInternal(Character chr) {
        if (exped != null) {
            if (exped.removeMember(chr)) {
                int minSize = eventClear ? 1 : 2;
                if (exped.getActiveMembers().size() < minSize) {
                    dispose();
                }
                chr.setAriantColiseum(null);

                int shards = chr.countItem(ItemId.ARPQ_SPIRIT_JEWEL);
                chr.getAbstractPlayerInteraction().removeAll(ItemId.ARPQ_SPIRIT_JEWEL);
                chr.updateAriantScore(shards);
            }
        }
    }

    /**
     * 玩家断线处理
     *
     * @param chr 玩家
     */
    public void playerDisconnected(Character chr) {
        leaveArenaInternal(chr);
    }

    /**
     * 展示竞技场结果，清除怪物并分配积分
     */
    private void showArenaResults() {
        eventClear = true;

        if (map != null) {
            map.broadcastMessage(PacketCreator.showAriantScoreBoard());
            map.killAllMonsters();

            distributeAriantPoints();
        }
    }

    /**
     * 判断比赛是否不公平
     * 根据冠军分数与其他参赛者分数比例判断
     *
     * @param winnerScore     冠军积分
     * @param secondScore     亚军积分
     * @param lostShardsScore 丢失碎片积分
     * @param runnerupsScore  其他参赛者积分
     * @return true表示比赛不公平
     */
    private static boolean isUnfairMatch(Integer winnerScore, Integer secondScore, Integer lostShardsScore, List<Integer> runnerupsScore) {
        if (winnerScore <= 0) {
            return false;
        }

        double runnerupsScoreCount = 0;
        for (Integer i : runnerupsScore) {
            runnerupsScoreCount += i;
        }

        runnerupsScoreCount += lostShardsScore;
        secondScore += lostShardsScore;

        double matchRes = runnerupsScoreCount / winnerScore;
        double runnerupRes = ((double) secondScore) / winnerScore;

        return matchRes < 0.81770726891980117713114871015349 && (runnerupsScoreCount < 7 || runnerupRes < 0.5929);
    }

    /**
     * 分配竞技场积分奖励
     * 根据各玩家得分确定奖励等级，检测不公平比赛
     */
    public void distributeAriantPoints() {
        int firstTop = -1, secondTop = -1;
        Character winner = null;
        List<Integer> runnerups = new ArrayList<>();

        for (Entry<Character, Integer> e : score.entrySet()) {
            Integer s = e.getValue();
            if (s > firstTop) {
                secondTop = firstTop;
                firstTop = s;
                winner = e.getKey();
            } else if (s > secondTop) {
                secondTop = s;
            }

            runnerups.add(s);
            rewardTier.put(e.getKey(), (int) Math.floor(s / 10));
        }

        runnerups.remove(firstTop);
        if (isUnfairMatch(firstTop, secondTop, map.getDroppedItemsCountById(ItemId.ARPQ_SPIRIT_JEWEL) + lostShards, runnerups)) {
            rewardTier.put(winner, 1);
        }
    }

    /**
     * 根据当前地图ID获取远征队类型
     *
     * @return 远征队类型
     */
    private ExpeditionType getExpeditionType() {
        switch (map.getId()) {
        case MapId.ARPQ_ARENA_1:
            return ExpeditionType.ARIANT;
        case MapId.ARPQ_ARENA_2:
            return ExpeditionType.ARIANT1;
        default:
            return ExpeditionType.ARIANT2;
        }
    }

    /**
     * 进入国王房间，移除频道远征队并取消所有定时任务
     */
    private void enterKingsRoom() {
        exped.removeChannelExpedition(map.getChannelServer());
        cancelAriantSchedules();

        for (Character chr : map.getAllPlayers()) {
            chr.changeMap(MapId.ARPQ_KINGS_ROOM, 0);
        }
    }

    /**
     * 清理竞技场资源，5分钟后释放地图
     */
    private synchronized void dispose() {
        if (exped != null) {
            exped.dispose(false);

            for (Character chr : exped.getActiveMembers()) {
                chr.setAriantColiseum(null);
                chr.changeMap(MapId.ARPQ_LOBBY, 0);
            }

            map.getWorldServer().registerTimedMapObject(() -> {
                score.clear();
                exped = null;
                map = null;
            }, MINUTES.toMillis(5));
        }
    }
}