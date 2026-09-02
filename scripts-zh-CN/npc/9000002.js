/*
    This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else if (mode == 0) {
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            var pending = cm.countPendingActivityRewards();
            if (pending > 0) {
                cm.sendNext("轰隆隆隆！！你已经从#b活动#k中赢得了游戏。\r\n你有 #b" + pending + "#k 份活动奖励待领取。");
            } else {
                cm.sendNext("轰隆隆隆！！你已经从#b活动#k中赢得了游戏。恭喜你走到了这一步！");
            }
        } else if (status == 1) {
            var claimed = cm.claimActivityRewards();
            if (claimed > 0) {
                cm.sendNext("已为你发放 #b" + claimed + "#k 份活动奖励。若背包已满，请清理后再次对话。");
            } else if (cm.canHold(4031019)) {
                cm.gainItem(4031019);
                cm.sendNext("你将获得#b#t4031019##k作为胜利纪念。卷轴上写有古代文字的秘密信息。");
            } else {
                cm.sendNext("背包空间不足。请清理后再来领取奖励。");
                cm.dispose();
            }
        } else if (status == 2) {
            var ret = cm.getPlayer().getSavedLocation("EVENT");
            if (ret > 0) {
                cm.warp(ret);
            } else {
                cm.warp(109050001);
            }
            cm.dispose();
        } else {
            cm.dispose();
        }
    }
}
