/* =========================================================
 *  师徒仙级奖励脚本（筑基及以上）最终版
 * =========================================================*/
var 红蓝点 = "#fEffect/CharacterEff.img/1032054/0/0#";
var 蓝星 = "#fEffect/CharacterEff.img/1052203/1/0#";
var 红星 = "#fEffect/CharacterEff.img/1052203/2/0#";
var 大蓝星 = "#fEffect/CharacterEff.img/1022223/2/0#";
var 大红星 = "#fEffect/CharacterEff.img/1022223/1/0#";
var 蓝点 = "#fEffect/CharacterEff.img/1022223/6/0#";
var 红点 = "#fEffect/CharacterEff.img/1022223/7/0#";
var 红枫叶 = "#fMap/MapHelper/weather/maple/1#";
var 银杏叶 = "#fMap/MapHelper/weather/maple/3#";
var 小黄星 = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 空红星 = "#fEffect/CharacterEff/1112926/0/1#";
var 小烟花 = "#fMap/MapHelper/weather/squib/squib4/1#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 师徒系统A = "#fEffect/CharacterEff1.img/QQ1408745/0/7#";
var 箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";
var 群粉心 =" "+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var levelup = "#fEffect/BasicEff.img/ItemLevelUp/20#";
var levelup2 = "#fEffect/BasicEff.img/ItemLevelUp/21#";

var levelToChinese = {
    0:"凡人",1:"筑基",2:"金丹",3:"元婴",4:"出窍",5:"分神",
    6:"合体",7:"渡劫",8:"大乘",9:"天仙",10:"仙君",
    11:"玄仙",12:"仙帝",13:"神人",14:"神将",15:"神君",
    16:"神帝",17:"神皇",18:"神尊",19:"圣人",20:"至尊",
    21:"主宰",22:"永恒",23:"创世",24:"超脱"
};

/* ===== 每档仙级奖励配置（含道具、元宝、累计赞助） ===== */
var rewardConfig = {
    1:  { items:[[2049104,1]],  yb:10,  lj:10,   msg:"筑基奖励：恶魔卷轴*1  + 元宝*10  + 累计*10" },
    2:  { items:[[2049104,2]],  yb:20,  lj:10,   msg:"金丹奖励：恶魔卷轴*2  + 元宝*20  + 累计*10" },
    3:  { items:[[2049104,3]],  yb:30,  lj:10,   msg:"元婴奖励：恶魔卷轴*3  + 元宝*30  + 累计*10" },
    4:  { items:[[2049104,4]],  yb:40,  lj:10,   msg:"出窍奖励：恶魔卷轴*4  + 元宝*40  + 累计*10" },
    5:  { items:[[2049104,5]],  yb:50,  lj:20,   msg:"分神奖励：恶魔卷轴*5  + 元宝*50  + 累计*20" },
    6:  { items:[[2049104,6]],  yb:60,  lj:20,   msg:"合体奖励：恶魔卷轴*6  + 元宝*60  + 累计*20" },
    7:  { items:[[2049104,7]],  yb:70,  lj:20,   msg:"渡劫奖励：恶魔卷轴*7  + 元宝*70  + 累计*20" },
    8:  { items:[[2049104,8]],  yb:80,  lj:20,   msg:"大乘奖励：恶魔卷轴*8  + 元宝*80  + 累计*20" },
    9:  { items:[[2049104,9]],  yb:90,  lj:30,   msg:"天仙奖励：恶魔卷轴*9  + 元宝*90  + 累计*30" },
    10: { items:[[2049104,10]], yb:100, lj:30,   msg:"仙君奖励：恶魔卷轴*10 + 元宝*100 + 累计*30" },
    11: { items:[[2049104,20]], yb:200, lj:30,   msg:"玄仙奖励：恶魔卷轴*20 + 元宝*200 + 累计*30" },
    12: { items:[[2049104,30]], yb:300, lj:30,   msg:"仙帝奖励：恶魔卷轴*30 + 元宝*300 + 累计*30" },
};

/* ===== 工具：生成领奖 key ===== */
function makeRewardKey(徒弟名字, 仙级) {
    return (徒弟名字 + "_" + 仙级 + "_仙级奖励").replace(/\s/g, "").toLowerCase();
}

/* ===== 工具：读玩家仙级 ===== */
function getPlayerXianLevel(playerName) {
    var conn = cm.getConnection();
    var sql = "SELECT x.count FROM xmwnjl x INNER JOIN characters c ON x.characterid = c.id WHERE c.name = ? AND x.bossid = 'XM飞升系统_仙级'";
    var pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, playerName);
    var rs = pstmt.executeQuery();
    var level = 0;
    if (rs.next()) level = rs.getInt("count");
    rs.close(); pstmt.close(); conn.close();
    return level;
}

function start() { status = -1; action(1, 0, 0); }

function action(mode, type, selection) {
	    /* -------------- 一键领取拦截 -------------- */
    if (selection == 9000) {
        var masterXian = getPlayerXianLevel(cm.getPlayer().getName());
        var rankinfo_list = cm.getBossRankCountTop("" + cm.getPlayer().id + "");
        var totalStr = "";
        var totalYb  = 0;
        var totalLj  = 0;
        var count    = 0;

        if (rankinfo_list != null) {
            for (var i = 0; i < rankinfo_list.size(); i++) {
                var info = rankinfo_list.get(i);
                var 徒弟名字 = info.getCname();
                var 徒弟仙级 = getPlayerXianLevel(徒弟名字);

                if (徒弟仙级 < 1) continue;
                if (masterXian <= 徒弟仙级) continue;

                /* 从筑基到徒弟当前仙级逐档补发 */
                for (var lv = 1; lv <= 徒弟仙级; lv++) {
                    var key = makeRewardKey(徒弟名字, lv);
                    if (cm.getAcLog(key) == 0) {
                        var cfg = rewardConfig[lv];
                        if (!cfg) continue;

                        /* 发放 */
                        for (var j = 0; j < cfg.items.length; j++)
                            cm.gainItem(cfg.items[j][0], cfg.items[j][1]);
                        if (cfg.yb) { cm.setmoneyb(cfg.yb); totalYb += cfg.yb; }
                        if (cfg.lj) { cm.getPlayer().setlpjf(cm.getPlayer().getlpjf() + cfg.lj); totalLj += cfg.lj; }
                        cm.setAcLog(key);

                        cm.全服黄色喇叭("师徒系统 : 玩家[" + cm.getPlayer().getName() + "]领取徒弟[" + 徒弟名字 + "]的<" + levelToChinese[lv] + ">奖励！");
						Packages.tools.FileoutputUtil.log("log\\玩家相关\\师徒奖励.log", "玩家 ["+cm.getPlayer().getName()+"] 成功领取徒弟 ["+徒弟名字+"] 的 " + cfg.msg + "");  //  记录日志
                    //    totalStr += "#b徒弟 #r" + cfg.msg + "#k\r\n";
						totalStr += "#r" + padRight(徒弟名字, 12) + "#k" + cfg.msg + "\r\n";
                        count++;
                    }
                }
            }
        }

        if (count == 0) {
            cm.sendOk("当前没有可补领的仙级礼包。");
        } else {
            var pop = "一键领取成功！共发放 " + count + " 档奖励：\r\n" + totalStr;
            if (totalYb) pop += "\r\n#d总计：元宝 * " + totalYb;
            if (totalLj) pop += "\r\n#d总计：累计赞助 * " + totalLj;
            cm.sendOk(pop);
        }
        cm.dispose();
        return;
    }
    if (status <= 0 && mode <= 0) { cm.dispose(); return; }
    mode == 1 ? status++ : status--;

    var 推广码 = cm.getPlayer().id;
    if (status == 0) {
        var selStr = "\t\t  " + 师徒系统A + "\r\n\r\n";
        selStr += 群粉心+ "\r\n";
        var 当前仙级 = getPlayerXianLevel(cm.getPlayer().getName());
		var 当前仙级中文 = levelToChinese[当前仙级] || "凡人";
		var line1 = padRight("#b你的角色码: #e#r" + 推广码, 30) + "#k#n";
		selStr += "\t" + line1 + " #b当前仙级：#e#r" + 当前仙级中文 + "#n#k#n\r\n\r\n";
		selStr += 群粉心+ "\r\n";
        if (cm.getBossRank("推广员", 2) > 0) {
            var masterId   = cm.getBossRank("推广员", 2);
            var masterName = cm.角色ID取名字Z(masterId);
            var masterXian = getPlayerXianLevel(masterName);
            var masterTitle = levelToChinese[masterXian] || "凡人";
			var line2 = padRight("#b你的师傅是: #e#r" + masterName, 30) + "#k#n";
			selStr += "\t" + line2 + "#b  师傅仙级：#e#r" + masterTitle + "#n#k#n\r\n\r\n";
			selStr += 群粉心+ "\r\n";
            selStr += "\t#L2#" + 箭头 + "#b查看我的徒弟（收徒奖励）#l#k\r\n\r\n";
			 /* 未筑基才允许脱离 */
			if (当前仙级 < 1) {
				selStr += "\t#L3#" + 箭头 + "#r脱离师门（重新拜师）#l#k\r\n\r\n";
			}
        } else {
            selStr += "\t#L2#" + 箭头 + "#b查看我的徒弟（收徒奖励）#l#k\r\n\r\n";
            selStr += "\t#L1#" + 箭头 + "#r输入师傅角色码#r#l#k\r\n\r\n";
        }
        selStr += "\t#L5#" + 箭头 + "#b拜师介绍与师傅奖励说明#l#k\r\n";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(9900004, "角色码");
                break;
            case 2:
                showApprenticeList();
                break;
            case 3:
                var 当前仙级 = getPlayerXianLevel(cm.getPlayer().getName());
                if (当前仙级 >= 1) {
                    cm.sendOk("你已筑基，无法脱离师门！");
                } else {
                    var masterCid = cm.getBossRank("推广员", 2);   // 取师傅 cid
                    if (masterCid <= 0) {
                        cm.sendOk("你当前并没有拜师，无需脱离！");
                    } else {
                        if (breakMasterApprentice(masterCid)) {    // ← 把实参传进去
                            cm.sendOk("脱离成功！请重新打开本界面查看菜单。");
	            			cm.喇叭(3, "师徒中心： [" + cm.getName() + "] 叛离了师门！");
						} else {
							cm.sendOk("脱离失败，详见日志 log/脱离师门.log");
						}
					}
				}
				cm.dispose();
				return;
            case 5:
				var desc = "#w              "+ 红星 + ""+ 大红星 + ""+ 红点 + "#e#b师门系统介绍#k#n"+ 红蓝点 + ""+ 蓝点 + ""+ 大蓝星 + ""+ 蓝星 + "#k \r\n\r\n";
                desc += "#d徒弟等级：必须＞10级 且 #b未筑基#k\r\n";
                desc += "#d师父等级：必须#r筑基及以上#k\r\n\r\n";
				desc += "         #r#e—————— 奖励说明 ——————#k#n\r\n\r\n";
                desc += "#r师傅奖励：#d徒弟达到以下仙级且仙级高于徒弟，即可领奖！\r\n";
				desc += "#r徒弟奖励：#d徒弟没有系统奖励，一切找师傅要！\r\n\r\n";
                desc += "         #r#e—————— 奖励总览 ——————#k#n\r\n\r\n";
                for (var lv = 1; lv <= 23; lv++) {
                    var cfg = rewardConfig[lv];
                    if (!cfg) continue;
                    desc += "#b" + levelToChinese[lv] + "#d：" + cfg.msg + "\r\n";
                }
                cm.sendOk(desc);
                cm.dispose();
                break;
        }
	}	
}

/* =========================================================
 *  展示徒弟列表（不提前 dispose，保证按钮能走进 status==2）
 * =========================================================*/
function showApprenticeList() {
    var text = "\t#r" + cm.getChar().getName() + "#k 的徒弟：#n\r\n\r\n";
    var masterXian = getPlayerXianLevel(cm.getPlayer().getName());
    var rankinfo_list = cm.getBossRankCountTop("" + cm.getPlayer().id + "");

    var canCount = 0;   // 统计可领取人数

    if (rankinfo_list != null && rankinfo_list.size() > 0) {
        for (var i = 0; i < rankinfo_list.size(); i++) {
            var info = rankinfo_list.get(i);
            var 徒弟名字 = info.getCname();
            var 徒弟等级 = cm.角色名字取等级(徒弟名字);
            var 徒弟仙级 = getPlayerXianLevel(徒弟名字);
            var 仙级名称 = levelToChinese[徒弟仙级] || "凡人";

            var idxStr = padLeft("" + (i + 1), 2);
            var nameStr = padRight(徒弟名字, 12);
            var xianStr = padRight(仙级名称, 6);
            var lvStr  = padLeft("" + 徒弟等级, 3);

            if (徒弟仙级 <= 0) {
                text += "#k   " + idxStr + ". #b" + nameStr + " #r仙级：" + xianStr + "#k Lv." + lvStr + "    #b[未筑基]#k\r\n";
            } else {
                var key = makeRewardKey(徒弟名字, 徒弟仙级);
                if (masterXian > 徒弟仙级 && cm.getAcLog(key) == 0) {
                    text += "#k   " + idxStr + ". #b" + nameStr + " #r仙级：" + xianStr + "#k Lv." + lvStr + "    #g[可领取]#k\r\n";
                    canCount++;
                }
                else if (cm.getAcLog(key) > 0)
                    text += "#k   " + idxStr + ". #b" + nameStr + " #r仙级：" + xianStr + "#k Lv." + lvStr + "    #k[已领取]#k\r\n";
                else
                    text += "#k   " + idxStr + ". #b" + nameStr + " #r仙级：" + xianStr + "#k Lv." + lvStr + "    #r[待领取]#k\r\n";
            }
        }
    } else {
        text += "\t暂无徒弟。";
    }

    /* ===== 一键入口 ===== */
    if (canCount == 0) {
        text += "\r\n\r\n#d    当前没有可领取的礼包。#k";
        cm.sendOk(text);
        cm.dispose();
    } else {
        text += "\r\n\r\n          #L9000#" + 箭头 + "#e#r一键领取全部 " + canCount + " 名徒弟奖励#k#n#l";
        cm.sendSimple(text);
    }
}

/* ================= 工具：补齐空格 ================= */
function padRight(str, len) {
    var bytes = byteLength(str);
    while (bytes < len) {
        str += ' ';
        bytes++;
    }
    return str;
}
function padLeft(str, len) {
    var bytes = byteLength(str);
    while (bytes < len) {
        str = ' ' + str;
        bytes++;
    }
    return str;
}
function byteLength(str) {
    var b = 0;
    for (var i = 0; i < str.length; i++) {
        b += (str.charCodeAt(i) > 255) ? 2 : 1;
    }
    return b;
}
/* 脱离师门：真正删记录并返回成功/失败 */
/* ===== 脱离师门：最终版 ===== */
function breakMasterApprentice(masterCid) {
    var conn = cm.getConnection();
    var playerId = cm.getPlayer().getId();
    var playerName = cm.getPlayer().getName();

    /* 先取师傅名字 */
    var masterName = "";
    try {
        var psName = conn.prepareStatement("SELECT name FROM characters WHERE id = ?");
        psName.setInt(1, masterCid);
        var rsName = psName.executeQuery();
        if (rsName.next()) masterName = rsName.getString("name");
        rsName.close();
        psName.close();
    } catch (e) {
        masterName = "未知(" + masterCid + ")";
    }

    try {
        /* 1. 徒弟→师傅 */
        var del1 = conn.prepareStatement(
            "DELETE FROM bossrank WHERE cid = ? AND bossname = '推广员'");
        del1.setInt(1, playerId);
        del1.executeUpdate();
        del1.close();

        /* 2. 展示记录（cid=徒弟，bossname=师傅） */
        var del2 = conn.prepareStatement(
            "DELETE FROM bossrank WHERE cid = ? AND bossname = ?");
        del2.setInt(1, playerId);
        del2.setString(2, String(masterCid));
        del2.executeUpdate();
        del2.close();

        /* 3. 二次确认 */
        var check = conn.prepareStatement(
            "SELECT count(*) FROM bossrank WHERE cid = ? AND bossname = '推广员'");
        check.setInt(1, playerId);
        var rs = check.executeQuery();
        var still = 0;
        if (rs.next()) still = rs.getInt(1);
        rs.close(); check.close();

        /* 日志：徒弟 脱离 师傅（带名字） */
        Packages.tools.FileoutputUtil.log("log/脱离师门.log",
            "[" + java.lang.System.currentTimeMillis() + "] " +
            playerName + "(" + playerId + ") 脱离师傅 " +
            masterName + "(" + masterCid + ") 剩余推广员记录=" + still);

        return still === 0;
    } catch (e) {
        Packages.tools.FileoutputUtil.log("log/脱离师门.log",
            "[" + java.lang.System.currentTimeMillis() + "] 删除异常：" + e);
        return false;
    } finally {
        conn.close();
    }
}