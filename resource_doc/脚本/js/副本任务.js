/* ==================
 脚本类型: 每日副本一条龙, 同账号不同角色可领   
 脚本作者：野原广志 
 联系方式：871337167
 =====================
 */
var random1=java.lang.Math.floor(4E5*Math.random()+2E5),itemSetSel=Math.random(),itemSet,itemSetQty,hasQty=!1,prizeIdEtc=[4170002,4170005,4170001,4170006,4170009],prizeIdEtc1=[4000464,4310097,4310098,4310156,2531000,4310174];
var myDate = new Date();
var year = myDate.getFullYear();
var month = myDate.getMonth() + 1;
var days = myDate.getDate();
var DatabaseConnection = Java.type('database.DatabaseConnection');


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
            //text += "\t\t  #r#e欢迎来到开心#k副本奖励#n\r\n\r\n"
			text += "单个副本奖励:#v4170002##v4170005##v4170001##v4170006##v4170009##l\r\n#k";
			text += "全部副本奖励:#v4310174#*2#v4001126#*500#v4000038#*100#v4000313#*100#l\r\n#v4310108##v4170007##v4170016##v2049100##v2340000##v2049124##v4310156##v4310097##v4310098#*1\r\n随机奖励:#v4310174##v2049124##v4000464##v4310097##v4310098##v4310156##v2531000##v2049104##l\r\n\r\n#k";
			text += "#r#L1##n通关废弃任务：#k[#b" + (getBossLog1("每日废弃") < 0 ? 0 : getBossLog1("每日废弃"))  + "#k/#r1#k]次 【奖励：#k[#b" + (getBossLog1("每日废弃奖励") < 0 ? 0 : getBossLog1("每日废弃奖励")) + "#k/#r1#k]次】#l\r\n";		
            text += "#r#L2##n通关天空任务：#k[#b" + (getBossLog1("每日天空") < 0 ? 0 : getBossLog1("每日天空"))  + "#k/#r1#k]次 【奖励：#k[#b" + (getBossLog1("每日天空奖励") < 0 ? 0 : getBossLog1("每日天空奖励")) + "#k/#r1#k]次】#l\r\n";
            text += "#r#L3##n通关玩具任务：#k[#b" + (getBossLog1("每日玩具") < 0 ? 0 : getBossLog1("每日玩具"))  + "#k/#r1#k]次 【奖励：#k[#b" + (getBossLog1("每日玩具奖励") < 0 ? 0 : getBossLog1("每日玩具奖励")) + "#k/#r1#k]次】#l\r\n#k";
			text += "#r#L4##n通关海盗任务：#k[#b" + (getBossLog1("每日海盗") < 0 ? 0 : getBossLog1("每日海盗"))  + "#k/#r1#k]次 【奖励：#k[#b" + (getBossLog1("每日海盗奖励") < 0 ? 0 : getBossLog1("每日海盗奖励")) + "#k/#r1#k]次】#l\r\n";
            text += "#r#L5##n通关毒物任务：#k[#b" + (getBossLog1("每日毒雾") < 0 ? 0 : getBossLog1("每日毒雾")) + "#k/#r1#k]次 【奖励：#k[#b" + (getBossLog1("每日毒雾奖励") < 0 ? 0 : getBossLog1("每日毒雾奖励")) + "#k/#r1#k]次】#l\r\n\r\n#k";
			text += "#r#L6##n完成以上任务领取：#k[#b" + (getBossLog1("每日副本完成") < 0 ? 0 :  getBossLog1("每日副本完成")) + "#k/#r1#k]次 \r\n#l\r\n#k";
            cm.sendOk(text); 
       } 
	   else if (selection == 1) {
            if (getBossLog1("每日废弃") < 1) {
                cm.sendOk("通关次数未达成，当前完成了：" + getBossLog1("每日废弃") + "次。");
                cm.dispose();
			} else if (getBossLog1("每日废弃奖励") >= 1) {
                cm.sendOk("你已经领取过了本次奖励。");
                cm.dispose();
            } else {
				itemSet = prizeIdEtc;
				var sel = Math.floor(Math.random() * itemSet.length);
				var qty = 1;
				cm.gainItem(itemSet[sel], qty);	
				setBossLog1("每日废弃奖励");
                setBossLog1("每日副本完成");
                cm.sendOk("奖励以发放至背包内，请确认。");
				cm.worldMessage(6,"恭喜["+cm.getName()+"]领取了副本一条龙奖励--废弃奖励!");
				status = -1;
            }
		}  
		else if (selection == 2) {
            if (getBossLog1("每日天空") < 1) {
                cm.sendOk("通关次数未达成，当前完成了：" + getBossLog1("每日天空") + "次。");
                cm.dispose();
			} else if (getBossLog1("每日天空奖励") >= 1) {
                cm.sendOk("你已经领取过了本次奖励。");
                cm.dispose();
            } else {
				itemSet = prizeIdEtc;
				var sel = Math.floor(Math.random() * itemSet.length);
				var qty = 1;
				cm.gainItem(itemSet[sel], qty);
				setBossLog1("每日天空奖励");
                setBossLog1("每日副本完成");
                cm.sendOk("奖励以发放至背包内，请确认。");
				cm.worldMessage(6,"恭喜["+cm.getName()+"]领取了副本一条龙奖励--天空奖励!");
				status = -1;
            }
		} 
		else if (selection == 3) {
            if (getBossLog1("每日玩具") < 1) {
                cm.sendOk("通关次数未达成，当前完成了：" + getBossLog1("每日玩具") + "次。");
                cm.dispose();
			} else if (getBossLog1("每日玩具奖励") >= 1) {
                cm.sendOk("你已经领取过了本次奖励。");
                cm.dispose();
            } else {
				itemSet = prizeIdEtc;
				var sel = Math.floor(Math.random() * itemSet.length);
				var qty = 1;
				cm.gainItem(itemSet[sel], qty);
				setBossLog1("每日玩具奖励");
                setBossLog1("每日副本完成");
                cm.sendOk("奖励以发放至背包内，请确认。");
				cm.worldMessage(6,"恭喜["+cm.getName()+"]领取了副本一条龙奖励--玩具奖励!");
				status = -1;
            }
		} 
		else if (selection == 4) {
            if (getBossLog1("每日海盗") < 1) {
                cm.sendOk("通关次数未达成，当前完成了：" + getBossLog1("每日海盗") + "次。");
                cm.dispose();
			} else if (getBossLog1("每日海盗奖励") >= 1) {
                cm.sendOk("你已经领取过了本次奖励。");
                cm.dispose();
            } else {
				itemSet = prizeIdEtc;
				var sel = Math.floor(Math.random() * itemSet.length);
				var qty = 1;
				cm.gainItem(itemSet[sel], qty);
				setBossLog1("每日海盗奖励");
                setBossLog1("每日副本完成");
                cm.sendOk("奖励以发放至背包内，请确认。");
				cm.worldMessage(6,"恭喜["+cm.getName()+"]领取了副本一条龙奖励--海盗奖励!");
				status = -1;
            }
		} 
		else if (selection == 5) {
            if (getBossLog1("每日毒雾") < 1) {
                cm.sendOk("通关次数未达成，当前完成了：" + getBossLog1("每日毒雾") + "次。");
                cm.dispose();
			} else if (getBossLog1("每日毒雾奖励") >= 1) {
                cm.sendOk("你已经领取过了本次奖励。");
                cm.dispose();
            } else {
				itemSet = prizeIdEtc;
				var sel = Math.floor(Math.random() * itemSet.length);
				var qty = 1;
				cm.gainItem(itemSet[sel], qty);
				setBossLog1("每日毒雾奖励");
                setBossLog1("每日副本完成");
                cm.sendOk("奖励以发放至背包内，请确认。");
				cm.worldMessage(6,"恭喜["+cm.getName()+"]领取了副本一条龙奖励--毒物奖励!");
				status = -1;
            }
		} 
		else if (selection == 6) {//1个#v4310156#+随机获得金币20-100万
			if (cm.getInventory(4).isFull(0)){//判断第四个也就是其它栏的装备栏是否有一个空格
			cm.sendOk("#b请保证其它栏位至少有1个空格,否则无法兑换.");
			cm.dispose();
			} else if (cm.getInventory(2).isFull(0)){//判断第二个也就是消耗栏的装备栏是否有一个空格
			cm.sendOk("#b请保证消耗栏位至少有1个空格,否则无法兑换.");
			cm.dispose();
			} else if (getBossLog1("每日副本完成") < 5) {
				cm.sendOk("通关次数未达成，当前完成了：" + getBossLog1("每日副本完成") + "次。");
				cm.dispose();
			} else if (getBossLog1("副本一条龙奖励") >= 1) {
				cm.sendOk("你已经领取过了本次奖励。");
				cm.dispose();
			} else {
				itemSet=prizeIdEtc1;var sel=Math.floor(Math.random()*itemSet.length),qty=1;
				cm.gainMeso(20000000);
				cm.gainItem(itemSet[sel], qty);
				cm.gainItem(4310174, 2);
				cm.gainItem(4001126,500);
				cm.gainItem(4000038, 100);
				cm.gainItem(4000313, 100);
				cm.gainItem(4310108, 1);
				cm.gainItem(4170007, 1);
				cm.gainItem(4170016, 1);
				cm.gainItem(2049100, 1);
				cm.gainItem(2340000, 1);
				cm.gainItem(2049124, 1);
				cm.gainItem(4310156, 1);
				cm.gainItem(2100009, 1);
				cm.gainItem(4310097, 1);
				cm.gainItem(4310098, 1);
				setBossLog1("副本一条龙奖励");
				cm.sendOk("奖励以发放至背包内，请确认。");
				cm.喇叭(3, "玩家 "+cm.getName()+" 完成全部一条龙副本，获得大量奖励!");
				status = -1;
			}
		}
	}
}

function setBossLog1(log) {
	var id = cm.getPlayer().getId();
    var con1 = DatabaseConnection.getConnection();
	var day = ""+year+"-"+month+"-"+days+"";
    var ps = con1.prepareStatement("insert into bosslog1 (characterid, bossid, count, time) values (?,?,?,?)");
    ps.setInt(1, id);
    ps.setString(2, log);
	ps.setInt(3, 1);
	ps.setString(4, day);
    ps.executeUpdate();
    ps.close();
}

function getBossLog1(log) {
		var id = cm.getPlayer().getId();
        var con = DatabaseConnection.getConnection();
        var count = 0;
        var ps;
        ps = con.prepareStatement("SELECT * FROM bosslog1 WHERE characterid = ? and bossid = ? and time = CURDATE()");
        ps.setInt(1, id);
		ps.setString(2, log);
        var rs = ps.executeQuery();
        if (rs.next()) {
            count = rs.getInt("count");
        } else {
            count = 0;
        }
        rs.close();
        ps.close();
        return count;
}