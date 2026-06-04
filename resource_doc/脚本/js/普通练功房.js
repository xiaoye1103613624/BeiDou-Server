

var status;
var lgfjl = " 普通VIP练功房 "; //副本名称
var minLevel = 30; //最低等级
var maxLevel = 250; //最高等级
var minPartySize = 1; //最低人数
var maxPartySize = 1; //最高人数
var cishuxianzhi = 1; //限制次数
var ditu = 910000005; //限制次数
var gwxl = 100000; //血量
var gwjy = 100000; //经验
var ljjf = 100; //判断累计
var inmeso = 100000; //入场金币
var 希拉 = "#fUI/UIWindow.img/MobGage/Mob/8870000#";
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        var tex2 = "";
        var text = "";
        for (i = 0; i < 10; i++) {
            text += "";
        }
        //显示物品ID图片用的代码是  #v这里写入ID#
        // minPartySize = cm.getPlayer().isGM() ? 1 : 1;
        // cishuxianzhi = cm.getPlayer().isGM() ? 12 : 2;
        // minLevel = cm.getPlayer().isGM() ? 1 : 121;
        text += "\t\t\t\t#r#e 【" + lgfjl + "】#k#n\r\n\r\n进入要求：\r\n#b\t累计赞助积分达到 #r" + ljjf + " #b点\t每日可进入 #r" + cishuxianzhi + " #b次\r\n#b\t进入需支付 #r" + inmeso + " #b金币\t每次限 #r10 #b分钟\r\n#b\t每次只能 #r " + minPartySize + " #b人进入\t等级限 #r " + minLevel + " #b- #r" + maxLevel + " #b级 #k\r\n"
		
        text += "#k练功房介绍:\r\n#b\t怪物血量 #r" + gwxl + " #b点#b\t怪物经验 #r" + gwjy + " #b点\r\n#b\t怪物随机爆物几率 #r10 #b%\r\n\r\n"
		

        text += "\t#b您当前累计积分 #r" + cm.getPlayer().getlpjf() + " #b点#b\t今日已进入 #r"+cm.getBossLog('lgfjl')+" #b次\r\n\r\n"

        if (cm.getBossLog('lgfjl') <= 0 && cm.getPlayer().getlpjf() >= 100 && cm.getPlayer().getMeso() >= inmeso && cm.getParty()<=1 && cm.getLevel() >= 30) { //判断组队是否2次
        text += "\t\t\t\t#e#L1##r进入练功房#n#l\r\n\r\n"
        }else {
        text += "\t#k#e您当前不满足要求；或者已挑战过了！\r\n\r\n"
        }
        
        cm.sendSimple(text);
    } else if (selection == 1) {

        if (cm.getParty() >= 1) {
            cm.sendOk("练功房只能一人进入！");
            cm.dispose();
            return;
		/*} else if (!cm.getParty每日记录("希拉次数", cishuxianzhi)) { //判断组队是否2次
			cm.sendOk("队伍中队友挑战次数已经用完2次！");
			cm.dispose();*/
        } else if (cm.getPlayer().getMeso() < inmeso) {
            cm.sendOk("你的金币都不够，无法进入");
            cm.dispose();
            return;
		//} else if (cm.getBossLog("lgfjl") > 0) {
			//cm.sendOk ("只能传送 1 次。"); 
		cm.dispose();
			} else if (cm.getPlayerCount(ditu) > 0) {
        cm.sendNext("当前有人正在挑战，稍后再试");
        cm.dispose();
		} else {
			cm.setBossLog("lgfjl")
			cm.warp(ditu);
			cm.喇叭(1,cm.getPlayer().getName() + " 开开心心的" + "进入 " + "[" + lgfjl + "] 真是羡煞旁人啊！！");
			cm.喇叭(1,cm.getPlayer().getName() + " 开开心心的" + "进入 " + "[" + lgfjl + "] 真是羡煞旁人啊！！");
			//cm.刷新地图(541020800);
			//cm.warp(240060200);
			/*cm.dispose()
        } else if (!cm.isLeader()) {
            cm.sendOk("请让你的队长和我说话~");
            cm.dispose();
            return;*/
      //  } else {
            //var party = cm.getParty().getMembers();
            //var inMap = cm.partyMembersInMap();
            //var levelValid = 0;
            /*for (var i = 0; i < party.size(); i++) {
                if (party.get(i).getLevel() >= minLevel && party.get(i).getLevel() <= maxLevel)
                    levelValid++;
            }
            if (inMap <= maxPartySize) {//inMap < minPartySize || 
                cm.sendOk("你的队伍人数太多，只能1人进入.");
                cm.dispose();
                return;
           /* } else if (levelValid != inMap) {
                cm.sendOk("请确保你的队伍里所有人员都在本地图，且最小等级在 " + minLevel + " 和 " + maxLevel + "之间.");
                cm.dispose();
                return;
         //   } else if (cm.getPlayer().getClient().getChannel()== 3 && cm.getPlayer().getClient().getChannel() == 4){ 
              //   cm.sendOk(" 亲，你不在3线或者4线呦！");
             //    cm.dispose();
              //   return;
            } else if (cm.getPlayer().getBossLog("希拉次数") >= cishuxianzhi) {
                 cm.sendOk("您好,限定每天只能挑战" + cishuxianzhi + "次！");
                 cm.dispose();
                 return;*/
           /* } else {
				var msg = '';
				var notHere = '';
				var party = cm.getPlayer().getParty().getMembers();
                var it = party.iterator();
                while (it.hasNext()) {
                    var cPlayer = it.next();
                    var chr = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
					if(chr != null) {
						if(chr.GetCombat() < 400000) {
							msg += chr.getName() + " "
						}
					} else {
						notHere += cPlayer.getName() + " ";
					}
                }
				if('' !== notHere) {
					cm.sendOk(notHere + '不在本地图.');
					cm.dispose();
					return;
				}
				if('' !== msg) {
					cm.sendOk(msg + '没有足够的"战力低于#b40万#k，无法进入！"');
					cm.dispose();
					return;
				}
                var em = cm.getEventManager("XL");
                if (em == null) {
                    cm.sendOk("这台电脑是当前不可用.");
                } else {
                    if (cm.getPlayerCount(ditu) <= 0) {//判定地图人数
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
						cm.giveParty每日记录("lgfjl");
	            	    cm.召唤怪物(8870000, 25500000000, 10000000, 1, ditu, -19,184);   
                     //   AAA();//添加重返检测
                    } else {
                        cm.sendOk("请稍等...任务正在进行中.");
                    }

                }
                cm.dispose();
            }*/
        }
    } else if (selection == 2) {
        if (cm.getMeso() >= ljjf) { //判断多少金币
            cm.gainMeso(-ljjf); //扣除多少金币
            cm.喇叭(1,cm.getPlayer().getName() + " [征集令]" + " : " + "[" + lgfjl + "Boss] 需要勇士一起完成");
            cm.dispose();
        } else {
            cm.sendOk("你的冒险币不足" + ljjf + "。无法发送征集令");
            cm.dispose();
        }

    }
}


function qianzhi() { //收取boss
    if (cm.getParty() == null) {
        cm.sendOk("你没有队伍无法进入！");
        cm.dispose();
        return false;
    }

    var leonys = cishuxianzhi;
    var party = cm.getParty().getMembers();
    var it = party.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        var teamID = cPlayer.getId();
        var curChar = cm.getMap().getCharacterById(teamID);
        if (curChar != null) {
            if (curChar.getQuestStatus(8534) == 0) {
                return false || cm.getPlayer().isGM();
            }

        }
    }
    return (true && cm.getPartyBosslog("huangdics", leonys));
}

function AAA() { //给队伍的bosslog 给远征军bosslog
    var party = cm.getParty().getMembers();
    var partyid = cm.getParty().getId();
    var bossid = "shaolin_l" + partyid;
    var mapId = cm.getMapId();
    var next = true;
    var levelValid = 0;
    var inMap = 1;
    var it = party.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        var teamID = cPlayer.getId();
        var curChar = cm.getMap().getCharacterById(teamID);
        if (curChar != null && cPlayer.getMapid() == mapId) {
            curChar.cm.giveParty每日记录("皇帝BOSS");
            //inMap += (cPlayer.getJobId() == 900 ? 3 : 1);
        }
    }
    //return;
}