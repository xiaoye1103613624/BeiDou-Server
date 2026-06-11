var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 挑战中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/10#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = "   "+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var fubenm = "门票BOSS"; //副本名称
var bossm = "职业四兄弟"; //BOSS名称
var bossid1 = 8787125; //BOSSID
var bossid2 = 8787127; //BOSSID
var bossid3 = 8787128; //BOSSID
var bossid4 = 8787129; //BOSSID
var bossid5 = 8787130; //BOSSID
var bossjyxs = "1000万"; //BOSS经验显示
var bossjy = 10000000; //BOSS经验
var bossxlxs = "10亿"; //BOSS血量显示
var bossxl = 1000000000; //BOSS血量
var minLevel = 100; //最低等级
var maxLevel = 250; //最高等级
var minPartySize = 1; //最低人数
var maxPartySize = 1; //最高人数
var 次数 = 2; //限制次数

var cywp = 2022520; //持有物品
var zlyqxs = "10万"; //战力要求显示
var zlyq = 100000; //战力要求
var inmesoxs = "1000万"; //入场金币显示
var inmeso = 10000000; //入场金币
var xhwzid = 3994742; //消耗物品
var xhwzsl = 99; //消耗物品数量
var fubendt = 932100002; //副本地图

var dlwup1 = 2381048; //掉落物品
var dlwup2 = 2381049; //掉落物品
var dlwup3 = 2381050; //掉落物品
var dlwup4 = 2381051; //掉落物品
var dlwup5 = 3605015; //掉落物品
var dlwup6 = 3605016; //掉落物品
var dlwup7 = 3605017; //掉落物品
var dlwup8 = 3605018; //掉落物品
var dlwup9 = 3605019; //掉落物品

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        // 玩家关闭对话框，释放资源
        cm.dispose();
    } else {
        // 玩家取消选择（点击“取消”按钮）
        if (status >= 0 && mode == 0) {
            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        // 根据交互模式更新状态：确认则+1，取消则-1
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        // 状态0：初始对话框，展示副本信息和选择项
        if (status == 0) {
            var text = "";
            // 空循环保持原样（原逻辑保留）
            for (var i = 0; i < 10; i++) {
                text += "";
            }
            // 拼接对话框内容（修正未定义变量TZCS为“次数”）
            text += dd + "\r\n\t\t\t" + 挑战中心 + "\r\n";
            text += "\t\t\t\t#b[" + fubenm + "]----#r[" + bossm + "]#k\r\n";
            text += "\t#k费用消耗: #r " + inmesoxs + " #k金币  \r\n";
            text += "\t#k人数限制:#r " + minPartySize + " #b- #r" + maxPartySize + " #k人  等级限制:#r " + minLevel + " #b- #r" + maxLevel + " #k级 \r\n";
            text += "\t#k每日限挑战: #r" + 次数 + " #k次  今日您已挑战: #r" + cm.getBossLog("职业四兄弟") + " #k次\r\n\r\n";
            text += "\t#k主要掉物: \r\n\t#v" + dlwup1 + "##v" + dlwup2 + "##v" + dlwup3 + "##v" + dlwup4 + "##v" + dlwup5 + "##v" + dlwup6 + "##v" + dlwup7 + "##v" + dlwup8 + "##v" + dlwup9 + "#\r\n\r\n";
            // 挑战选项（原选择项ID=L5保留）
            text += "\t\t#e#L5##b#v4031569#我要打四个#r[" + bossm + "]#v4031569##l#n\r\n";
            // 发送选择对话框
            cm.sendSimple(text);
        } 
        // 状态1：处理玩家选择（选择项ID=5的逻辑）
        else if (status == 1 && selection == 5) {
            // 校验1：等级是否达标
            if (cm.getLevel() < minLevel) {
                cm.sendOk("您的等级太低了，去送死嘛？");
                cm.dispose();
                return;
            }
            // 校验2：金币是否足够
            else if (cm.getPlayer().getMeso() < inmeso) {
                cm.sendOk("你都没有足够的金币，还想白嫖我？");
                cm.dispose();
                return;
            }
            // 校验3：是否处于组队状态
            else if (cm.getPlayer().getParty() != null) {
                cm.sendOk("只能一个人进入，请先退出组队");
                cm.dispose();
                return;
            }
            // 校验4：副本是否有人在挑战
            else if (cm.getPlayerCount(fubendt) > 0) {
                cm.sendOk("有人正在挑战，请稍等一会儿再来");
                cm.dispose();
                return;
            }
            // 校验5：每日挑战次数是否用尽
            else if (cm.getPlayer().getBossLog("职业四兄弟") >= 次数) {
                cm.sendOk("你今天的普通挑战次数已用完！");
                cm.dispose();
                return;
            }
            else {
                // 扣除入场金币
                cm.gainMeso(-inmeso);
                cm.getPlayer().getMap().removeDrops();
				cm.killAllMob(fubendt);
                cm.召唤怪物(bossid2, bossxl, bossjy, 1, fubendt, -91, -181); 
                cm.召唤怪物(bossid3, bossxl, bossjy, 1, fubendt, 75, -181); 
                cm.召唤怪物(bossid4, bossxl, bossjy, 1, fubendt, 155, -181); 
                cm.召唤怪物(bossid5, bossxl, bossjy, 1, fubendt, -254, -181); 
                // 传送玩家到副本
                cm.warp(fubendt);
                // 绑定副本倒计时（180秒后返回新手村910000000）
                var targetMap = cm.getPlayer().getMap();
                var returnMap = cm.getChannelServer().getMapFactory().getMap(910000000);
                cm.getPlayer().startMapTimeLimitTask(300, returnMap);
                // 记录挑战日志（次数+1）
                cm.setBossLog("职业四兄弟");
                // 发送全服喇叭通知
                cm.喇叭(2, " 【"+cm.getName()+"】开始挑战【"+fubenm+"—"+bossm+"】");
                // 释放对话框资源
                cm.dispose();
            }
        }
        // 异常状态处理：防止状态流转出错
        else {
            cm.sendOk("操作异常，请重新尝试！");
            cm.dispose();
        }
    }
}