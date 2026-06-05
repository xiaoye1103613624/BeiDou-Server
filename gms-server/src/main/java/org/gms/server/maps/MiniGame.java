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

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.packet.Packet;
import org.gms.net.server.Server;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 【类型】MiniGame，class，包 {@code org.gms.server.maps}。
 *
 * <p>小游戏（MiniGame）房间对象，实现奥默棋（Omok）和翻牌配对（Match Card）两种休闲对战的完整逻辑，包括回合管理、胜负判定和平局处理。</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理小游戏房间</li>
 *   <li>处理奥默棋和翻牌配对游戏逻辑</li>
 *   <li>管理游戏进度和状态</li>
 *   <li>处理玩家加入和退出</li>
 *   <li>处理游戏结果和计分</li>
 * </ul>
 *
 * @author Matze
 * @author Ronan (HeavenMS)
 */
public class MiniGame extends AbstractMapObject {
    /** 房主 */
    private Character owner;
    /** 访客 */
    private Character visitor;
    /** 密码 */
    private final String password;
    /** 游戏类型 */
    private MiniGameType GameType = MiniGameType.UNDEFINED;
    /** 棋子类型 */
    private int piecetype;
    /** 游戏进行状态 */
    private int inprogress = 0;
    /** 棋盘上的棋子 */
    private final int[] piece = new int[250];
    /** 4x3翻牌游戏列表 */
    private final List<Integer> list4x3 = new ArrayList<>();
    /** 5x4翻牌游戏列表 */
    private final List<Integer> list5x4 = new ArrayList<>();
    /** 6x5翻牌游戏列表 */
    private final List<Integer> list6x5 = new ArrayList<>();
    /** 描述 */
    private final String description;
    /** 失败者 */
    private int loser = 1;
    /** 第一槽位 */
    private int firstslot = 0;
    /** 访客分数和状态变量 */
    private int visitorpoints = 0, visitorscore = 0, visitorforfeits = 0, lastvisitor = -1;
    /** 房主分数和状态变量 */
    private int ownerpoints = 0, ownerscore = 0, ownerforfeits = 0;
    /** 访客和房主退出状态 */
    private boolean visitorquit, ownerquit;
    /** 下次平局可用时间 */
    private long nextavailabletie = 0;
    /** 赢得比赛所需的胜场数 */
    private int matchestowin = 0;

    /**
     * 小游戏类型枚举
     */
    public enum MiniGameType {
        /** 未定义 */
        UNDEFINED(0), 
        /** 奥默棋 */
        OMOK(1), 
        /** 翻牌配对 */
        MATCH_CARD(2);
        /** 类型值 */
        private int value = 0;

        /**
         * 构造函数：创建小游戏类型实例
         * 
         * @param value 类型值
         */
        MiniGameType(int value) {
            this.value = value;
        }

        /**
         * 获取类型值
         * 
         * @return 类型值
         */
        public int getValue() {
            return value;
        }
    }

    /**
     * 小游戏结果枚举
     */
    public enum MiniGameResult {
        /** 胜利 */
        WIN, 
        /** 失败 */
        LOSS, 
        /** 平局 */
        TIE
    }

    /**
     * 构造函数：创建小游戏实例
     * 
     * @param owner 房主
     * @param description 描述
     * @param password 密码
     */
    public MiniGame(Character owner, String description, String password) {
        this.owner = owner;
        this.description = description;
        this.password = password;
    }

    /**
     * 获取密码
     * 
     * @return 密码
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * 检查密码是否正确
     * 
     * @param sentPw 输入的密码
     * @return 如果密码正确则返回true，否则返回false
     */
    public boolean checkPassword(String sentPw) {
        return this.password.length() == 0 || sentPw.toLowerCase().contentEquals(this.password.toLowerCase());
    }

    /**
     * 检查是否有空闲槽位
     * 
     * @return 如果有空闲槽位则返回true，否则返回false
     */
    public boolean hasFreeSlot() {
        return visitor == null;
    }

    /**
     * 检查是否为房主
     * 
     * @param chr 角色
     * @return 如果是房主则返回true，否则返回false
     */
    public boolean isOwner(Character chr) {
        return owner.equals(chr);
    }

    /**
     * 添加访客
     * 
     * @param challenger 挑战者
     */
    public void addVisitor(Character challenger) {
        visitor = challenger;
        if (lastvisitor != challenger.getId()) {
            ownerscore = 0;
            ownerforfeits = 0;

            visitorscore = 0;
            visitorforfeits = 0;
            lastvisitor = challenger.getId();
        }

        Character owner = this.getOwner();
        if (GameType == MiniGameType.OMOK) {
            owner.sendPacket(PacketCreator.getMiniGameNewVisitor(this, challenger, 1));
            owner.getMap().broadcastMessage(PacketCreator.addOmokBox(owner, 2, 0));
        } else if (GameType == MiniGameType.MATCH_CARD) {
            owner.sendPacket(PacketCreator.getMatchCardNewVisitor(this, challenger, 1));
            owner.getMap().broadcastMessage(PacketCreator.addMatchCardBox(owner, 2, 0));
        }
    }

    /**
     * 关闭房间
     * 
     * @param forceClose 是否强制关闭
     */
    public void closeRoom(boolean forceClose) {
        owner.getMap().broadcastMessage(PacketCreator.removeMinigameBox(owner));

        if (forceClose) {
            this.broadcastToOwner(PacketCreator.getMiniGameClose(false, 4));
        }
        this.broadcastToVisitor(PacketCreator.getMiniGameClose(true, 3));

        if (visitor != null) {
            visitor.setMiniGame(null);
            visitor = null;
        }

        owner.setMiniGame(null);
        owner = null;
    }

    /**
     * 移除访客
     * 
     * @param forceClose 是否强制关闭
     * @param challenger 挑战者
     */
    public void removeVisitor(boolean forceClose, Character challenger) {
        if (visitor == challenger) {
            if (isMatchInProgress()) { // owner is winner if visitor leave in progress
                minigameMatchOwnerWins(true);
            }
            if (forceClose) {
                visitor.sendPacket(PacketCreator.getMiniGameClose(true, 4));
            }
            challenger.setMiniGame(null);
            visitor = null;

            this.getOwner().sendPacket(PacketCreator.getMiniGameRemoveVisitor());
            if (GameType == MiniGameType.OMOK) {
                this.getOwner().getMap().broadcastMessage(PacketCreator.addOmokBox(owner, 1, 0));
            } else if (GameType == MiniGameType.MATCH_CARD) {
                this.getOwner().getMap().broadcastMessage(PacketCreator.addMatchCardBox(owner, 1, 0));
            }
        }
    }

    /**
     * 检查是否为访客
     * 
     * @param challenger 挑战者
     * @return 如果是访客则返回true，否则返回false
     */
    public boolean isVisitor(Character challenger) {
        return visitor == challenger;
    }

    /**
     * 向房主广播数据包
     * 
     * @param packet 数据包
     */
    public void broadcastToOwner(Packet packet) {
        Client c = owner.getClient();
        if (c != null) {
            c.sendPacket(packet);
        }
    }

    /**
     * 向访客广播数据包
     * 
     * @param packet 数据包
     */
    public void broadcastToVisitor(Packet packet) {
        if (visitor != null) {
            visitor.sendPacket(packet);
        }
    }

    /**
     * 设置第一槽位
     * 
     * @param type 类型
     */
    public void setFirstSlot(int type) {
        firstslot = type;
    }

    /**
     * 获取第一槽位
     * 
     * @return 第一槽位
     */
    public int getFirstSlot() {
        return firstslot;
    }

    /**
     * 更新小游戏盒子
     */
    private void updateMiniGameBox() {
        this.getOwner().getMap().broadcastMessage(PacketCreator.addOmokBox(owner, visitor != null ? 2 : 1, inprogress));
    }

    /**
     * 结束小游戏匹配
     * 
     * @return 如果匹配结束则返回true，否则返回false
     */
    private synchronized boolean minigameMatchFinish() {
        if (isMatchInProgress()) {
            inprogress = 0;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 小游戏匹配完成
     */
    private void minigameMatchFinished() {
        updateMiniGameBox();

        if (ownerquit) {
            owner.closeMiniGame(true);
        } else if (visitorquit) {
            visitor.closeMiniGame(true);
        }
    }

    /**
     * 小游戏匹配开始
     */
    public void minigameMatchStarted() {
        inprogress = 1;
        ownerquit = false;
        visitorquit = false;
    }

    /**
     * 设置游戏后退出
     * 
     * @param player 玩家
     * @param quit 是否退出
     */
    public void setQuitAfterGame(Character player, boolean quit) {
        if (isOwner(player)) {
            ownerquit = quit;
        } else {
            visitorquit = quit;
        }
    }

    /**
     * 检查比赛是否正在进行中
     * 
     * @return 如果比赛正在进行中则返回true，否则返回false
     */
    public boolean isMatchInProgress() {
        return inprogress != 0;
    }

    /**
     * 拒绝平局
     * 
     * @param chr 角色
     */
    public void denyTie(Character chr) {
        if (this.isOwner(chr)) {
            inprogress |= (1 << 1);
        } else {
            inprogress |= (1 << 2);
        }
    }

    /**
     * 检查平局是否被拒绝
     * 
     * @param chr 角色
     * @return 如果平局被拒绝则返回true，否则返回false
     */
    public boolean isTieDenied(Character chr) {
        if (this.isOwner(chr)) {
            return ((inprogress >> 2) % 2) == 1;
        } else {
            return ((inprogress >> 1) % 2) == 1;
        }
    }

    /**
     * 房主获胜
     * 
     * @param forfeit 是否为认输
     */
    public void minigameMatchOwnerWins(boolean forfeit) {
        if (!minigameMatchFinish()) {
            return;
        }

        owner.setMiniGamePoints(visitor, 1, this.isOmok());

        if (visitorforfeits < 4 || !forfeit) {
            ownerscore += 50;
        }
        visitorscore += (15 * (forfeit ? -1 : 1));
        if (forfeit) {
            visitorforfeits++;
        }

        this.broadcast(PacketCreator.getMiniGameOwnerWin(this, forfeit));

        minigameMatchFinished();
    }

    /**
     * 访客获胜
     * 
     * @param forfeit 是否为认输
     */
    public void minigameMatchVisitorWins(boolean forfeit) {
        if (!minigameMatchFinish()) {
            return;
        }

        owner.setMiniGamePoints(visitor, 2, this.isOmok());

        if (ownerforfeits < 4 || !forfeit) {
            visitorscore += 50;
        }
        ownerscore += (15 * (forfeit ? -1 : 1));
        if (forfeit) {
            ownerforfeits++;
        }

        this.broadcast(PacketCreator.getMiniGameVisitorWin(this, forfeit));

        minigameMatchFinished();
    }

    /**
     * 平局
     */
    public void minigameMatchDraw() {
        if (!minigameMatchFinish()) {
            return;
        }

        owner.setMiniGamePoints(visitor, 3, this.isOmok());

        long timeNow = Server.getInstance().getCurrentTime();
        if (nextavailabletie <= timeNow) {
            visitorscore += 10;
            ownerscore += 10;

            nextavailabletie = timeNow + MINUTES.toMillis(5);
        }

        this.broadcast(PacketCreator.getMiniGameTie(this));

        minigameMatchFinished();
    }

    /**
     * 设置房主分数
     */
    public void setOwnerPoints() {
        ownerpoints++;
        if (ownerpoints + visitorpoints == matchestowin) {
            if (ownerpoints == visitorpoints) {
                minigameMatchDraw();
            } else if (ownerpoints > visitorpoints) {
                minigameMatchOwnerWins(false);
            } else {
                minigameMatchVisitorWins(false);
            }
            ownerpoints = 0;
            visitorpoints = 0;
        }
    }

    /**
     * 设置访客分数
     */
    public void setVisitorPoints() {
        visitorpoints++;
        if (ownerpoints + visitorpoints == matchestowin) {
            if (ownerpoints > visitorpoints) {
                minigameMatchOwnerWins(false);
            } else if (visitorpoints > ownerpoints) {
                minigameMatchVisitorWins(false);
            } else {
                minigameMatchDraw();
            }
            ownerpoints = 0;
            visitorpoints = 0;
        }
    }

    /**
     * 设置赢得比赛所需的胜场数
     * 
     * @param type 类型
     */
    public void setMatchesToWin(int type) {
        matchestowin = type;
    }

    /**
     * 设置棋子类型
     * 
     * @param type 类型
     */
    public void setPieceType(int type) {
        piecetype = type;
    }

    /**
     * 获取棋子类型
     * 
     * @return 棋子类型
     */
    public int getPieceType() {
        return piecetype;
    }

    /**
     * 设置游戏类型
     * 
     * @param game 游戏类型
     */
    public void setGameType(MiniGameType game) {
        GameType = game;
        if (GameType == MiniGameType.MATCH_CARD) {
            if (matchestowin == 6) {
                for (int i = 0; i < 6; i++) {
                    list4x3.add(i);
                    list4x3.add(i);
                }
            } else if (matchestowin == 10) {
                for (int i = 0; i < 10; i++) {
                    list5x4.add(i);
                    list5x4.add(i);
                }
            } else {
                for (int i = 0; i < 15; i++) {
                    list6x5.add(i);
                    list6x5.add(i);
                }
            }
        }
    }

    /**
     * 获取游戏类型
     * 
     * @return 游戏类型
     */
    public MiniGameType getGameType() {
        return GameType;
    }

    /**
     * 检查是否为奥默棋
     * 
     * @return 如果是奥默棋则返回true，否则返回false
     */
    public boolean isOmok() {
        return GameType.equals(MiniGameType.OMOK);
    }

    /**
     * 洗牌列表
     */
    public void shuffleList() {
        if (matchestowin == 6) {
            Collections.shuffle(list4x3);
        } else if (matchestowin == 10) {
            Collections.shuffle(list5x4);
        } else {
            Collections.shuffle(list6x5);
        }
    }

    /**
     * 获取卡片ID
     * 
     * @param slot 槽位
     * @return 卡片ID
     */
    public int getCardId(int slot) {
        int cardid;
        if (matchestowin == 6) {
            cardid = list4x3.get(slot);
        } else if (matchestowin == 10) {
            cardid = list5x4.get(slot);
        } else {
            cardid = list6x5.get(slot);
        }
        return cardid;
    }

    /**
     * 获取赢得比赛所需的胜场数
     * 
     * @return 赢得比赛所需的胜场数
     */
    public int getMatchesToWin() {
        return matchestowin;
    }

    /**
     * 设置失败者
     * 
     * @param type 类型
     */
    public void setLoser(int type) {
        loser = type;
    }

    /**
     * 获取失败者
     * 
     * @return 失败者
     */
    public int getLoser() {
        return loser;
    }

    /**
     * 广播数据包
     * 
     * @param packet 数据包
     */
    public void broadcast(Packet packet) {
        broadcastToOwner(packet);
        broadcastToVisitor(packet);
    }

    /**
     * 聊天
     * 
     * @param c 客户端
     * @param chat 聊天内容
     */
    public void chat(Client c, String chat) {
        broadcast(PacketCreator.getPlayerShopChat(c.getPlayer(), chat, isOwner(c.getPlayer())));
    }

    /**
     * 发送奥默棋
     * 
     * @param c 客户端
     * @param type 类型
     */
    public void sendOmok(Client c, int type) {
        c.sendPacket(PacketCreator.getMiniGame(c, this, isOwner(c.getPlayer()), type));
    }

    /**
     * 发送翻牌配对
     * 
     * @param c 客户端
     * @param type 类型
     */
    public void sendMatchCard(Client c, int type) {
        c.sendPacket(PacketCreator.getMatchCard(c, this, isOwner(c.getPlayer()), type));
    }

    /**
     * 获取房主
     * 
     * @return 房主
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * 获取访客
     * 
     * @return 访客
     */
    public Character getVisitor() {
        return visitor;
    }

    /**
     * 设置棋子
     * 
     * @param move1 移动1
     * @param move2 移动2
     * @param type 类型
     * @param chr 角色
     */
    public void setPiece(int move1, int move2, int type, Character chr) {
        int slot = move2 * 15 + move1 + 1;
        if (piece[slot] == 0) {
            piece[slot] = type;
            this.broadcast(PacketCreator.getMiniGameMoveOmok(this, move1, move2, type));
            for (int y = 0; y < 15; y++) {
                for (int x = 0; x < 11; x++) {
                    if (searchCombo(x, y, type)) {
                        if (this.isOwner(chr)) {
                            this.minigameMatchOwnerWins(false);
                            this.setLoser(0);
                        } else {
                            this.minigameMatchVisitorWins(false);
                            this.setLoser(1);
                        }
                        for (int y2 = 0; y2 < 15; y2++) {
                            for (int x2 = 0; x2 < 15; x2++) {
                                int slot2 = (y2 * 15 + x2 + 1);
                                piece[slot2] = 0;
                            }
                        }
                    }
                }
            }
            for (int y = 0; y < 15; y++) {
                for (int x = 4; x < 15; x++) {
                    if (searchCombo2(x, y, type)) {
                        if (this.isOwner(chr)) {
                            this.minigameMatchOwnerWins(false);
                            this.setLoser(0);
                        } else {
                            this.minigameMatchVisitorWins(false);
                            this.setLoser(1);
                        }
                        for (int y2 = 0; y2 < 15; y2++) {
                            for (int x2 = 0; x2 < 15; x2++) {
                                int slot2 = (y2 * 15 + x2 + 1);
                                piece[slot2] = 0;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 搜索组合
     * 
     * @param x X坐标
     * @param y Y坐标
     * @param type 类型
     * @return 如果找到组合则返回true，否则返回false
     */
    private boolean searchCombo(int x, int y, int type) {
        int slot = y * 15 + x + 1;
        for (int i = 0; i < 5; i++) {
            if (piece[slot + i] == type) {
                if (i == 4) {
                    return true;
                }
            } else {
                break;
            }
        }
        for (int j = 15; j < 17; j++) {
            for (int i = 0; i < 5; i++) {
                if (piece[slot + i * j] == type) {
                    if (i == 4) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        return false;
    }

    /**
     * 搜索组合2
     * 
     * @param x X坐标
     * @param y Y坐标
     * @param type 类型
     * @return 如果找到组合则返回true，否则返回false
     */
    private boolean searchCombo2(int x, int y, int type) {
        int slot = y * 15 + x + 1;
        for (int j = 14; j < 15; j++) {
            for (int i = 0; i < 5; i++) {
                if (piece[slot + i * j] == type) {
                    if (i == 4) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        return false;
    }

    /**
     * 获取描述
     * 
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取房主分数
     * 
     * @return 房主分数
     */
    public int getOwnerScore() {
        return ownerscore;
    }

    /**
     * 获取访客分数
     * 
     * @return 访客分数
     */
    public int getVisitorScore() {
        return visitorscore;
    }

    @Override
    public void sendDestroyData(Client client) {}

    @Override
    public void sendSpawnData(Client client) {}

    @Override
    public MapObjectType getType() {
        return MapObjectType.MINI_GAME;
    }
}