/*QILIN
QQ1040453090或1500663066
*/
var 枫叶 = "#fUI/ITC.img/Base/Tab/Enable/0#";
var 选择获得 = "#fUI/UIWindow/Quest/select#";
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
			//显示物品ID图片用的代码是  #v这里写入ID#
            text = "\t\t\t\t#e#r"+ 枫叶 +" 福利会员 "+ 枫叶 +"#k#n\r\n\r\n"
			text += "你好,我是会员代理人,这里可以领取每日福利,领取会员!\r\n"//3
			text += "#r"+选择获得+"会员福利#k#n#l\r\n"
            text += "#L1##b#v1142173#每日福利\r\n"
			text += "#L2##b#v1142174#每日福利\r\n"
			text += "#L3##b#v1142176#每日福利\r\n"
			//text += "#L12##b#v1050356#每日福利VIP5\r\n"
			text += "#L13##b#v1142178#每日福利#l\r\n\r\n"
			text += "#L11##b#v1142803#每日福利\r\n"
            
			text += "#r"+选择获得+"领取会员#k#n#l\r\n"
            text += "#L6##b#v1142173#领取首充\r\n"
			text += "#L4##b#v1142174#领取VIP1\r\n"
            text += "#L5##b#v1142176#领取VIP2\r\n"
			//text += "#L8##b#v1050356#领取会员VIP5\r\n"
			//text += "#L9##b#v4031505#领取礼包奖励\r\n"
			text += "#L10##b#v1142178#领取VIP3\r\n"
			text += "#L7##b#v1142803#领取会员VIP4\r\n"
            cm.sendSimple(text);
        } else if (selection == 1) {
			cm.dispose();
		cm.openNpc(9900004, 777);
        }
        else if (selection == 11) {
			cm.dispose();
		cm.openNpc(9900004, 110);
        }
		else if (selection == 12) {
			cm.dispose();
		cm.openNpc(9900004, 120);
        }
		else if (selection == 13) {
			cm.dispose();
		cm.openNpc(9900004, 130);
        }
		else if (selection == 2) {
			cm.dispose();
		cm.openNpc(9900004, 888);
		}
		else if (selection == 9) {
			cm.dispose();
		cm.openNpc(9900004, "万元户");
		}
		else if (selection == 10) {
			cm.dispose();
		cm.openNpc(9900004, "两万元户");
		}

		else if (selection == 3) {
			cm.dispose();
		cm.openNpc(9900004, 9999);
        }

else if (selection == 6) {
	cm.dispose();
		cm.openNpc(9900004, 1238);
        }  
else if (selection == 4) {
	cm.dispose();
		cm.openNpc(9900004, 1236);
        }  
else if (selection == 5) {
	cm.dispose();
		cm.openNpc(9900004, 1237);
        } 
		else if (selection == 7) {
	cm.dispose();
		cm.openNpc(9900004, "充值礼包4");
        } 
		else if (selection == 8) {
	cm.dispose();
		cm.openNpc(9900004, "充值礼包5");
        } 
		
		 
    }
}


