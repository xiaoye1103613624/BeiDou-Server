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
                cm.sendNext("Bam bam bam!! You won the EVENT!\r\nYou have #b" + pending + "#k pending activity reward(s).");
            } else {
                cm.sendNext("Bam bam bam bam!! You have won the game from the \r\n#bEVENT#k. Congratulations on making it this far!");
            }
        } else if (status == 1) {
            var claimed = cm.claimActivityRewards();
            if (claimed > 0) {
                cm.sendNext("Granted #b" + claimed + "#k reward(s). If your inventory was full, clear space and talk again.");
            } else if (cm.canHold(4031019)) {
                cm.gainItem(4031019);
                cm.sendNext("You'll be awarded the #bScroll of Secrets#k as the winning prize.");
            } else {
                cm.sendNext("Your inventory is full. Please make room, then talk to me.");
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
