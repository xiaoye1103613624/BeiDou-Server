var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 挑战中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/10#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = "   "+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 提示 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 图标1 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 图标2 = "#fUI/UIWindow/Minigame/Omok/stone/0/black/0#";
var 图标3 = "#fUI/UIWindow/Minigame/Omok/stone/1/white/0#";
var 图标4 = "#fUI/UIWindow/Minigame/Omok/stone/2/black/0#";
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge#";
var fubenm = "希纳斯"; //副本名称
var bossm = "神殿"; //BOSS名称+大陆扩充
var bossid = 8850011; //BOSSID
var bossjyxs = "500万"; //BOSS经验显示
var bossjy = 5000000; //BOSS经验
var bossxlxs = "50000 亿"; //BOSS血量显示
var bossxl = 5000000000000; //BOSS血量
var minPartySize = 1; //最低人数
var maxPartySize = 6; //最高人数
var minLevel = 120; //最低等级
var maxLevel = 250; //最高等级
var TZCS = 2; //限制次数

var cywp = 4110001; //消耗物品
var xhwzsl = 5; //消耗物品数量
var zlyqxs = "神皇"; //战力要求显示
var zlyq = 17; //仙级要求最高21
var fubendt = 271040100; //副本地图
var FBSJ = 1800; //限制时间 秒
var FBSJXS = 30; //显示时间分钟
var inmeso = 1; //入场金币

var propsToDiscard = [2022701, 2022702, 2022703, 2022704, 2022705, 2022706, 2022707, 2022708, 2022709, 2022710, 2022711, 2022712, 2022713, 2022714, 2022715, 2022716, 2022717, 2022718, 2022719, 2022720, 2022721, 2022722, 2022723, 2022724, 2022725]; // 需要丢弃的道具ID列表
// 导入 MapleInventoryType 枚举
var MapleInventoryType = Java.type('client.inventory.MapleInventoryType');
var 仙级名称 = [
    "凡人", "筑基", "金丹", "元婴", "出窍", "分神", "合体", "渡劫", 
    "大乘", "天仙", "仙君", "玄仙", "仙帝", "神人", "神将", "神君", 
    "神帝", "神皇", "神尊", "圣人", "至尊", "主宰", "永恒", "创世", 
	"超脱"
];
function getxmwnjlc(log) {
    return getxmwnjljsc(log);
}

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
			var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
            var 当前仙级名称 = 仙级名称[当前仙级];
            var bossLogCount = cm.getPlayer().getBossLog(fubenm);  // 获取玩家的副本挑战次数
			var 扩充次数 = cm.getPlayer().getBossLog(bossm); // 获取玩家的扩充次数
            var text = "";
            text += "\r\n\t\t  " + 挑战中心 + "\r\n" + 群粉心 + "\r\n";
            text += "                 #r#e" + 图标2 + "<" + fubenm + ">" + 图标2 + "#n\r\n\r\n";
            text += "                 " + 图标1 + "#d 仙级要求:#r " + zlyqxs + " #k\r\n\r\n";
            text += "                 " + 图标1 + "#d 当前仙级:#r " + 当前仙级名称 + "\r\n\r\n";
			text += "                 " + 图标1 + "#d 今日次数:#r " + bossLogCount + "#b/" + (TZCS + 扩充次数) + "次    \r\n\r\n"; // 显示扩充后的总次数
            text += "                 " + 图标1 + "#d 副本时间:#b " + FBSJXS + " 分钟\r\n\r\n";
			text += "                 " + 图标1 + "#d BOSS血量:#r " + bossxlxs + " \r\n";
            text += "                 " + 图标1 + "#d 进入要求:#r #v" + cywp + "# * 5 #k张" + "\r\n\r\n";
			
            if (当前仙级 >= zlyq) { // 假设仙级5以上可以进入
                text += "\r\n\r\n    #L1#" + 图标3 + "#r确定进入" + 图标3 + "#l  ";
                text += "    #L2#" + 图标3 + "#r掉落查询" + 图标3 + "#l     \r\n\r\n\r\n";
            } else {
                text += "\r\n\r\n\t\t\t  #r#e你的仙级不满足,无法带队进入#n\r\n\r\n";
				cm.dispose(); // 结束对话
            }

            text += "" + 群粉心 + "\r\n\r\n";
            text += " \r\n";
            cm.sendSimple(text);
        } else if (selection == 1) {
            if (cm.getPlayerCount(fubendt) > 0) {
                cm.sendOk("有人正在挑战，请稍等一会儿再来");
                cm.dispose();
                return;
            } else if (!cm.haveItem(cywp, xhwzsl)) {
                cm.sendOk("你没有#v" + cywp + "#，或者数量不足，无法进入");
                cm.dispose();
                return;
			} else if (cm.getBossLog(fubenm) >= (TZCS + cm.getPlayer().getBossLog(bossm))) { // 检查扩充后的总次数
                cm.sendOk("您今天已经挑战太多次了，请明天再来");
                cm.dispose();
                return;
            } else if (cm.getPlayer().getMeso() < inmeso) {
                cm.sendOk("你都没有足够的金币，还想白嫖我？");
                cm.dispose();
                return;
            } else if (cm.getParty() == null) {
                cm.sendOk("你没有队伍无法进入！");
                cm.dispose();
                return;
            } else if (cm.partyMembersInMap() < minPartySize || cm.partyMembersInMap() > maxPartySize) {
                cm.sendOk("请核对你的队伍人数！");
                cm.dispose();
                return;
            } else {
    var msg = '';
        var notHere = '';
        var party = cm.getPlayer().getParty().getMembers();  // 获取队伍成员
        var it = party.iterator();

        while (it.hasNext()) {
            var cPlayer = it.next();
            var chr = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());  // 获取队员角色
            if (chr != null) {
                // 获取队员的仙级
			var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
			var 当前仙级名称 = 仙级名称[当前仙级];

			// 检查队员仙级是否满足要求（假设仙级5以上可以进入）
			if (当前仙级 < zlyq) {
				msg += chr.getName() + " 仙级不足（低于 " + 仙级名称[zlyq] + "），无法进入！\r\n";
				break;  // 找到第一个不满足条件的队员，跳出循环
			}
                // 检查队员背包中是否有需要丢弃的道具
                for (var i = 0; i < propsToDiscard.length; i++) {
                    if (chr.haveItem(propsToDiscard[i])) {
                        msg += chr.getName() + " 不能携带道具 #v" + propsToDiscard[i] + "# 进入！\r\n";
                        break;  // 找到第一个不满足条件的队员，跳出循环
                    }
                }
                if (msg.length > 0) break;  // 如果已经有消息，跳出循环
            } else {
                notHere += cPlayer.getName() + " ";  // 不在本地图的队员
            }
        }

        // 如果有队员不在当前地图，显示提示
        if ('' !== notHere) {
            cm.sendOk(notHere + '不在本地图.');
            cm.dispose();
            return;
        }

        // 如果有队员战力不足或背包中有需要丢弃的道具，显示提示
        if (msg.length > 0) {
            cm.sendOk(msg);
            cm.dispose();
            return;
        }
                var party = cm.getParty().getMembers();
                var memberCheckPassed = true;
                var memberStatusText = "";

                var it = party.iterator();
                while (it.hasNext()) {
                    var cPlayer = it.next();
                    var chr = getPlayerStatus(cPlayer.getId());  // 使用新的 getPlayerStatus 函数
                    if (chr == null) {
                        memberCheckPassed = false;
                        memberStatusText += cPlayer.getName() + " 不在线！\r\n";
                    } else {
                        if (chr.getMapId() != cm.getPlayer().getMapId()) {
                            memberCheckPassed = false;
                            memberStatusText += chr.getName() + " 不在当前地图！\r\n";
                        }

                        if (chr.getClient().getChannel() != cm.getClient().getChannel()) {
                            memberCheckPassed = false;
                            memberStatusText += chr.getName() + " 不在当前频道！\r\n";
                        }

                        // 正确调用 getInventory 方法
                        var inventory = chr.getInventory(MapleInventoryType.ETC);  //这个ETC是背包其他栏，消耗道具使用 MapleInventoryType.USE 来检查
                        if (inventory.countById(cywp) < xhwzsl) {
                            memberCheckPassed = false;
                            memberStatusText += chr.getName() + " 没有#v" + cywp + "#或数量不足！\r\n";
                        }

                    //    if (chr.getBossLog(fubenm) >= (TZCS + cm.getPlayer().getBossLog(bossm))) { // 检查扩充后的总次数
						var 扩充次数 = chr.getBossLog(bossm); // 获取当前队员的扩充次数
						if (chr.getBossLog(fubenm) >= (TZCS + 扩充次数)) { // 检查扩充后的总次数
                            memberCheckPassed = false;
                            memberStatusText += chr.getName() + " 每日挑战次数已达上限！\r\n";
                        }
                    }
                }

                if (memberCheckPassed) {
                    // 执行副本进入的逻辑
                    var map = cm.getMap(fubendt);
                    map.killAllMonsters(false);
                    cm.gainMeso(-inmeso); // 扣除金币
					cm.warpParty(fubendt); // 传送队伍
					cm.getMap(fubendt).resetFully(); //刷新地图
                    cm.召唤怪物(bossid, bossxl, bossjy, 1, fubendt, 225, 115);
                    cm.给团队每日(fubenm);
					cm.给团队道具(cywp,-xhwzsl); 
                   // cm.getPlayer().startMapTimeLimitTask(FBSJ, cm.getChannelServer().getMapFactory().getMap(910000000));       //这个给个人地图记时
					// 遍历队伍成员并为每个成员启动限时任务
					var party = cm.getParty().getMembers();
					var it = party.iterator();
					while (it.hasNext()) {
					var cPlayer = it.next();
					var chr = getPlayerStatus(cPlayer.getId()); // 获取队员角色
					if (chr != null) {
					// 为每个队员启动地图时间限制任务
					chr.startMapTimeLimitTask(FBSJ, cm.getChannelServer().getMapFactory().getMap(910000000));
					}
				}

                    cm.喇叭(2, "【" + cm.getName() + "】 队伍开始挑战 【" + bossm + " — " + fubenm + "】");
                    cm.dispose();
                } else {
                    cm.sendOk("#r队伍成员存在问题：#k\r\n" + memberStatusText);
                    cm.dispose();
                }
            }
        } else if (selection == 2) {
            if (cm.getLevel() < minLevel) {
                cm.sendOk("您的等级不足" + minLevel + ",无法查看该副本掉落.");
                cm.dispose();
                return;
            }
            TT = cm.checkDrop(bossid);
            cm.sendSimple(TT);
            status = -1;
        }
    }
}

function getPlayerStatus(id) {
    var cchr;
    var 循环 = true;
    if (循环 == true) {
        var channels = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
        while (channels.hasNext()) {
            var channel = channels.next();
            var chrs = channel.getPlayerStorage().getAllCharacters().iterator();
            while (chrs.hasNext()) {
                var chr = chrs.next();
                if (chr.getId() == id) {
                    cchr = chr;
                    循环 = false;
                    break;
                }
            }
        }
    }
    return cchr;
}


function getConnection() {
    return cm.getConnection();
}

function getxmwnjljsc(jiluid) {
    var xmsjfh = 0;
    zhjsid = cm.getPlayer().getId();
    var conn = getConnection();
    var sql = "SELECT * FROM xmwnjl WHERE characterid = " + zhjsid + " AND bossid = '" + jiluid + "' ;";
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();		
    if (result.next()) {
        xmsjfh = result.getInt("count");
    } 
    result.close();
    pstmt.close();
    conn.close();
    return xmsjfh;
}