var status;
var minLevel = 10;
var maxLevel = 250;
var minPlayers = 1;
var maxPlayers = 6;
var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE); //获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE); //获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
var 星星 = "#fEffect/CharacterEff/1051294/1/0#"; //旋转星星1
var 星星1 = "#fEffect/CharacterEff/1051294/1/1#"; //旋转星星2
var 星星2 = "#fEffect/CharacterEff/1051294/1/2#"; //旋转星星3
var 星星3 = "#fEffect/CharacterEff/1051294/1/3#"; //旋转星星4
var 星星4 = "#fEffect/CharacterEff/1051294/1/4#"; //旋转星星5
var 星星5 = "#fEffect/CharacterEff/1051294/1/5#"; //旋转星星6
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 大水滴 = "#fItem/Etc/0427/04270001/Icon10/4#"; //
var 怪物代码 = 9600000;
var 怪物血量 = 30000000;
var 怪物x轴 = 12;
var 怪物y轴 = 144;
var 需要道具 = 3994731;
var 需要数量 = 5;
var 需要点券 = 50000000;
var 需要金币 = 0;

function start() {
//    if (cm.getPlayer().getClient().getChannel() != 3 && cm.getPlayer().getClient().getChannel() != 4 && cm.getPlayer().getClient().getChannel() != 5&& cm.getPlayer().getClient().getChannel() != 1&& cm.getPlayer().getClient().getChannel() != 2&& cm.getPlayer().getClient().getChannel() != 6&& cm.getPlayer().getClient().getChannel() != 7) {
    if (cm.getPlayer().getClient().getChannel() != 2 && cm.getPlayer().getClient().getChannel() != 3 && cm.getPlayer().getClient().getChannel() != 4 && cm.getPlayer().getClient().getChannel() != 5) {
        cm.sendOk("该副本只能在 2 、3 、4 、5 线进行挑战");
        cm.dispose();
        return;

    } else if (hour < 1 || hour > 22) { //30--33
        cm.sendOk("#d当前服务器时间: #r" + hour + " #b点 \r\n\r\n副本将在每日1:00-22:00时之前才能进入#n\r\n\r\n#b请及时参加哦！#k\r\n#k");
        cm.dispose();
        return;
    }
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
        var text = "";
        for (i = 0; i < 10; i++) {
            text += "";
        }
        text += "                    #r欢迎来到无尽洞穴#k\r\n\r\n";
        text += "副本进入要求如下:\n\r\n#l";
        text += "1:人数限制:#r1 - 1 #k组队. \n\r\n";
        text += "2:每天只可进入1次.\n\r\n";
        text += "3:只可在 1.2.3.4.5.6.7 线进行挑战. \n\r\n";
		text += "4:#r一共999个关卡，能爽到不能呼吸！#k \n\r\n";
		text += "5:#k每日最多可挑战#b 3 次#k！每次#b "+需要数量+" 亿金币#k .\n\r\n";
		text += "6:#k注意：掉线无法重返！怪物血量过高前期不建议进入.#k \r\n";
		text += "7:#r注意：每日 1:00-22:00 时之间才能进入.#k \r\n\r\n";
    //    text += "5:挑战不能开启身外化身。\n\r\n";
		text += " [副本产出]：#v4310144# #v3994978# \r\n";
        text += "              #L1##r" + 星星2 + "开始进入无尽洞穴" + 星星2 + "#l\r\n\r\n";
        cm.sendSimple(text);
    } else if (selection == 2) {
        cm.warp(910000000);
    } else if (status == 1) {
        if (selection == 2) {
            cm.openNpc(9050005, 5);
        } else if (selection == 1) {
            if (cm.getParty() == null) {
                cm.sendOk("你没有队伍无法进入！");
                cm.dispose();
		} else if (cm.haveItem(5010019,1)==false){	
				cm.sendOk("你不是月卡会员，无法进入！");
				cm.dispose();
            } else if (!cm.isLeader()) {
                cm.sendOk("请让你的队长和我说话~");
                cm.dispose();
            } else {
                var party = cm.getParty().getMembers();
                var inMap = cm.partyMembersInMap();
                var mapId = cm.getPlayer().getMapId();
                var party = cm.getPlayer().getParty().getMembers();
                var it = party.iterator();
                var cPlayer = it.next();
                var victim = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                var levelValid = 0;
                for (var i = 0; i < party.size(); i++) {
                    /* if (party.get(i).getBossLog("无尽牛鼻", 0) >= 3){
                        cm.sendOk("【"+party.get(i).getName() +"】今日挑战了3次了，不能再挑战了!");
						cm.dispose();
						return;
					} */
                }
                if (!cm.haveItem(需要道具, 需要数量)) {
                    cm.sendOk("作为队长需要准备门票#v" + 需要道具 + "##z" + 需要道具 + "#x" + 需要数量 + "个.");
                    cm.dispose();
                    return;

                } else if (cm.getPlayer().getBossLog("无尽洞穴", 0) >= 3) {
                    cm.sendOk("今日已挑战3次了,换个人来吧!");
                    cm.dispose();
                } else if (party.size() != 1) {
                    cm.sendOk("只能单人挑战");
                    cm.dispose();
        //        }else if (cm.getPlayer().getBossLog('真仙境',1) < 1) {
         //           cm.sendOk("未到达真仙境，无法进入");
         //           cm.dispose();
		//			return;
				}		
				
                else {

                    for each(var cPlayer in cm.getParty().getMembers()) {
                        if (getBossTime(cPlayer.getId(), '征神之路完结') >= 9999999) {
                            cm.sendOk("#r" + cPlayer.getName() + "#k已经通关过了,请退出组队,或等维护后再进入！");
                            cm.dispose();
                            return
                        }
                    }

                    var em = cm.getEventManager("refreshbossroomchaoji3");
                    if (em == null) {
                        cm.sendOk("明天重启后开放.");
                        cm.dispose();

                    } else {
                        if (cm.getPlayerCount(924010000) <= 0) {
							cm.getPlayer().disposeClones();
                            cm.gainItem(需要道具, -需要数量);
                            cm.getPlayer().setBossLog("无尽洞穴", 0, 1)
                            cm.dispose();
							em.setProperty("player", ""+cm.getPlayer().getName()+"");
                            em.startInstance(cm.getParty(), cm.getPlayer().getMap()); //传送队伍进入副本
                            cm.全服漂浮喇叭("玩家[" + cm.getName() + "]进入无尽洞穴，开始超高难度的征神挑战！！", 5121001);
                            cm.喇叭(2, "玩家[" + cm.getName() + "]进入无尽洞穴，开始999关的闭关修炼！！");
                            cm.喇叭(2, "玩家[" + cm.getName() + "]进入无尽洞穴，开始999关的闭关修炼！！");
                            cm.喇叭(2, "玩家[" + cm.getName() + "]进入无尽洞穴，开始999关的闭关修炼！！");
                            cm.喇叭(2, "玩家[" + cm.getName() + "]进入无尽洞穴，开始999关的闭关修炼！！");
                            cm.喇叭(2, "玩家[" + cm.getName() + "]进入无尽洞穴，开始999关的闭关修炼！！");
                        } else {
                            cm.getPlayer().dropMessage(6,"请换个线在来尝试,这个线已经有人了");
                        }
                    }
                    cm.dispose();

                }
            }
        }
    }
}

function getBossTime(id, bossid) //获得BOSS次数
{
    var con1 = Packages.database.DatabaseConnection.getConnection()
        ps1 = con1.prepareStatement("SELECT * FROM bosslog WHERE characterid = ? and bossid = ? ");
    ps1.setInt(1, id);
    ps1.setString(2, bossid);
    var rs1 = ps1.executeQuery();
    var count = 0;
    if (rs1.next()) {
        count = rs1.getInt("count");
    }
    con1.close();
    rs1.close();
    ps1.close();
    return count;
}
