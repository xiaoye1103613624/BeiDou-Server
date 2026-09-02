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
/**
-- Odin JavaScript --------------------------------------------------------------------------------
	VIP Cab - Victoria Road : Lith Harbor (104000000)
-- By ---------------------------------------------------------------------------------------------
	Xterminator
-- Version Info -----------------------------------------------------------------------------------
	1.0 - First Version by Xterminator
---------------------------------------------------------------------------------------------------
**/

var status = 0;
var co = 100000000;
var qty;
var cost;

function start() {
    status = -1;
    action(1, 0, 0);
    var text = "兑换黄金枫叶，1E一个。\r\n";
    text += "\r\n\r\n";
    cm.sendGetNumber(text,1,1,100);
}

function action(mode, type, selection) {
    status++;
    if (mode == -1){
        cm.dispose();
        return;
    } else if (mode == 0) {
        cm.sendOk("穷B快滚!.");
    	cm.dispose();
    	return;
    }
    if (status == 1) {
	qty = (selection > 0) ? selection : (selection < 0 ? -selection : 1);
	cost=qty*co;
            var add ="请确认您的信息\r\n";
            add += "☆#r您要兑换：" + qty + "#k个枫叶\r\n";
            add += "☆您要支付:" + cost + " 金币#n#b #k\r\n";
            add += "☆#r当前您拥有金币：" + cm.getMeso() + "#k\r\n";
            cm.sendYesNo(add);
    } else if (status == 2) {
                    if (cm.getMeso() < cost) {
                        cm.sendOk("#b穷屌");
	        cm.dispose();
                    } else {
                        cm.gainMeso(-cost);
                        cm.gainItem(4000313,qty);
	        cm.dispose();
                   }
    }
}