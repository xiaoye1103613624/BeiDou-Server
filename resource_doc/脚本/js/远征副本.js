//var 花花1 = "#fUI/GuildMark/Mark/Pattern/00004020/1#";
var 花花2 = "#fUI/GuildMark/Mark/Pattern/00004020/3#";
var 花花3 = "#fUI/GuildMark/Mark/Pattern/00004020/5#";
var 花花4 = "#fUI/GuildMark/Mark/Pattern/00004020/7#";
var 花花5 = "#fUI/GuildMark/Mark/Pattern/00004020/9#";
var 花花6 = "#fUI/GuildMark/Mark/Pattern/00004020/11#";
var 花花7 = "#fUI/GuildMark/Mark/Pattern/00004020/13#";
var 花花8 = "#fUI/GuildMark/Mark/Pattern/00004020/14#";
var 花花9 = "#fUI/GuildMark/Mark/Pattern/00004020/15#";
var 感叹 = "1";
var 开 = "1";   //有框框 无√
var 关 = "1";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 广播 = "1";
var 花花1 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge1#";

var 刷新 = 2000000;
var 入场 = 2000001;
var 列表,列表1;
var 入场人员 = new Array();
var 不足人员 = new Array();
var Eims = new Array();
var 挑战列表 = [
	{
		事件名称: "拉瓦那", 等级限制: 200,
		挑战记录: "拉瓦那远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8800200,
		入口地点: 252030000, 返回地点: 910001000, 挑战地点: 252030100, 挑战时限: 60, 
		条件: [[4031227, 3]],
		增加血量: 10,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8800200, 数量: 1, 血量: 20000000000, x轴: [780, 780], y轴: 513 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "艾里葛斯", 等级限制: 200,
		挑战记录: "艾里葛斯远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 9300028,
		入口地点: 910540100, 返回地点: 910001000, 挑战地点: 910540200, 挑战时限: 60, 
		条件: [[4031227, 5]],
		增加血量: 10,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 9300028, 数量: 1, 血量: 80000000000, x轴: [209, 209], y轴: 20 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "蜘蛛女王", 等级限制: 200,
		挑战记录: "蜘蛛女王远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8800400,
		入口地点: 240093300, 返回地点: 910001000, 挑战地点: 240093310, 挑战时限: 60, 
		条件: [[3994789, 5]],
		增加血量: 10,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8800400, 数量: 1, 血量: 320000000000, x轴: [202, 202], y轴: 97 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "狂暴威尔", 等级限制: 200,
		挑战记录: "狂暴威尔远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 2600800,
		入口地点: 555000200, 返回地点: 910001000, 挑战地点: 555000201, 挑战时限: 60, 
		条件: [[3994789, 5]],
		增加血量: 10,//每多1个人头增加百分比血量  之前的威尔ID 8880302
		怪物列表: [
			{ 代码: 2600800, 数量: 1, 血量: 640000000000, x轴: [-887, -887], y轴: 90 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "三头犬", 等级限制: 200,
		挑战记录: "三头犬远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 9400897,
		入口地点: 510101300, 返回地点: 910001000, 挑战地点: 510102400, 挑战时限: 60, 
		条件: [[3994789, 5]],
		增加血量: 10,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 9400897, 数量: 1, 血量: 1280000000000, x轴: [1162, 1162], y轴: 33 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "蟾蜍怪", 等级限制: 200,
		挑战记录: "蟾蜍怪远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 6500012,
		入口地点: 910025200, 返回地点: 910001000, 挑战地点: 910025201, 挑战时限: 60, 
		条件: [[3994789, 5]],
		增加血量: 10,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 6500012, 数量: 1, 血量: 2560000000000, x轴: [229, 229], y轴: -213 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "火狐", 等级限制: 200,
		挑战记录: "火狐远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 9700043,
		入口地点: 910141000, 返回地点: 910001000, 挑战地点: 910141030, 挑战时限: 60, 
		条件: [[3994789, 5]],
		增加血量: 10,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 9700043, 数量: 1, 血量: 5120000000000, x轴: [564, 564], y轴: 27 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "黑暗恶狼", 等级限制: 200,
		挑战记录: "黑暗恶狼远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8220109,
		入口地点: 910142080, 返回地点: 910001000, 挑战地点: 910142090, 挑战时限: 60, 
		条件: [[3994789, 5]],
		增加血量: 10,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8220109, 数量: 1, 血量: 10000000000000, x轴: [349, 349], y轴: -23 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "始皇帝", 等级限制: 200,
		挑战记录: "始皇帝远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 9410224,
		入口地点: 745010500, 返回地点: 910001000, 挑战地点: 745090100, 挑战时限: 60, 
		条件: [[3994789, 5]],
		增加血量: 10,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 9410224, 数量: 1, 血量: 30000000000000, x轴: [-24, -24], y轴: 98 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "敦凯尔", 等级限制: 200,
		挑战记录: "敦凯尔远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8645009,
		入口地点: 803100000, 返回地点: 910001000, 挑战地点: 803200000, 挑战时限: 60, 
		条件: [[3994789, 5]],
		增加血量: 10,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8645009, 数量: 1, 血量: 70000000000000, x轴: [-252, -252], y轴: 156 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "戴米安", 等级限制: 200,
		挑战记录: "戴米安远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8880404,
		入口地点: 209000001, 返回地点: 910001000, 挑战地点: 209000002, 挑战时限: 60, 
		条件: [[3994789, 10]],
		增加血量: 10,//每多1个人头增加百分比血量、戴米安ID：8880404
		怪物列表: [
			{ 代码: 8880404, 数量: 1, 血量: 140000000000000, x轴: [-163, -163], y轴: 154 },
		],
		公共奖项: [],
	},
	
	{
		事件名称: "调和精灵", 等级限制: 200,
		挑战记录: "调和精灵远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8644011,
		入口地点: 910142100, 返回地点: 910001000, 挑战地点: 910142110, 挑战时限: 60, 
		条件: [[3994789, 20]],
		增加血量: 20,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8644011, 数量: 1, 血量: 500000000000000, x轴: [182, 182], y轴: -23 },
		],
		公共奖项: [],

	},
	
	{
		事件名称: "穷奇", 等级限制: 200,
		挑战记录: "穷奇远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8880830,
		入口地点: 511000100, 返回地点: 910001000, 挑战地点: 410007001, 挑战时限: 60, //511000110
		条件: [[3994789, 50]],
		增加血量: 20,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8880830, 数量: 1, 血量: 800000000000000, x轴: [-1403, -1403], y轴: 273 },
		],
		公共奖项: [],

	},
	
	{
		事件名称: "铸杌", 等级限制: 200,
		挑战记录: "铸杌远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8880831,
		入口地点: 511000120, 返回地点: 910001000, 挑战地点: 802000825, 挑战时限: 60, //511000130
		条件: [[3994789, 100]],
		增加血量: 20,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8880831, 数量: 1, 血量: 900000000000000, x轴: [-115, -115], y轴: 335 },
		],
		公共奖项: [],

	},
	
	{
		事件名称: "混沌", 等级限制: 200,
		挑战记录: "混沌远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8880832,
		入口地点: 511000140, 返回地点: 910001000, 挑战地点: 410007541, 挑战时限: 60, 
		条件: [[3994789, 150]],
		增加血量: 20,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8880832, 数量: 1, 血量: 1000000000000000, x轴: [122, 122], y轴: 289 },
		],
		公共奖项: [],

	},
	
	{
		事件名称: "卡琳", 等级限制: 200,
		挑战记录: "卡琳远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8880837,
		入口地点: 511000160, 返回地点: 910001000, 挑战地点: 450013830, 挑战时限: 60, //511000170
		条件: [[3994789, 200]],
		增加血量: 20,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8880837, 数量: 1, 血量: 1200000000000000, x轴: [1, 1], y轴: 88 },
		],
		公共奖项: [],

	},

	{
		事件名称: "黑魔法师", 等级限制: 200,
		挑战记录: "黑魔法师远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8880503,
		入口地点: 450013850, 返回地点: 910001000, 挑战地点: 450013840, 挑战时限: 60, 
		条件: [[3994789, 300]],
		增加血量: 20,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8880503, 数量: 1, 血量: 1500000000000000, x轴: [316, 316], y轴: 383 },
		],
		公共奖项: [],

	},
	
	{
		事件名称: "路西德", 等级限制: 200,
		挑战记录: "路西德远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8880140,
		入口地点: 450003740, 返回地点: 910001000, 挑战地点: 450004150, 挑战时限: 60, 
		条件: [[3994789, 400]],
		增加血量: 20,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8880140, 数量: 1, 血量: 3000000000000000, x轴: [1057, 1057], y轴: 48 },
		],
		公共奖项: [],

	},
	
	{
		事件名称: "阿勒玛", 等级限制: 200,
		挑战记录: "黑水灵王远征队", 挑战限制: 3, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 8220104,
		入口地点: 450001219, 返回地点: 910001000, 挑战地点: 450001340, 挑战时限: 60, 
		条件: [[3994789, 500]],
		增加血量: 20,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 8220104, 数量: 1, 血量: 6000000000000000, x轴: [722, 722], y轴: 177 },
		],
		公共奖项: [],

	},
	
	{
		事件名称: "暗影", 等级限制: 200,
		挑战记录: "暗影远征队", 挑战限制: 2, 扩充Log: "远征挑战扩充", 重返金币费用: 100000, 触发奖项怪物: 9601295,
		入口地点: 450012500, 返回地点: 910001000, 挑战地点: 874004002, 挑战时限: 60, 
		条件: [[3994789, 1000]],
		增加血量: 20,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 9601295, 数量: 1, 血量: 9000000000000000, x轴: [23, 23], y轴: 306 },
		],
		公共奖项: [],

	},

	{
		事件名称: "刷金房", 等级限制: 70,
		挑战记录: "刷金BOSS挑战", 挑战限制: 1, 扩充Log: "", 重返金币费用: 100000, 触发奖项怪物: 9420602,
		入口地点: 926010000, 返回地点: 802000101, 挑战地点: 926010004, 挑战时限: 60, 
		条件: [[4310047, 1]],
		增加血量: 20,//每多1个人头增加百分比血量
		怪物列表: [
			{ 代码: 9420602, 数量: 1, 血量: 8888888, x轴: [18, 18], y轴: 88 },
		],
		公共奖项: [],
	},

	
	
	
]
var 扩充Log;
var xx;
function start() {
	status = -1;
	action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            for (var i = 0; i < 挑战列表.length; i++) {
                if (cm.getMapId() == 挑战列表[i].入口地点) {
                    列表 = 挑战列表[i];
                    break;
                }
            }
            for (var ii = 0; ii < 挑战列表.length; ii++) {
                if (cm.getMapId() == 挑战列表[ii].挑战地点) {
                    列表1 = 挑战列表[ii];
                    break;
                }
            }

            if (列表 == null && 列表1 == null) {
                cm.dispose();
                return;
            }
            if (列表1 != null && cm.getMapId() == 列表1.挑战地点) {
                cm.sendYesNo("确定要放弃挑战返回#r#e #m" + 列表1.返回地点 + "##k#n 吗？");
                xx = 0;
            } else {
                扩充Log = 列表.扩充Log;
                var 条件文本 = "";
                for (var j = 0; j < 列表.条件.length; j++) {
                    条件文本 += "#v" + 列表.条件[j][0] + ":#x" + 列表.条件[j][1] + " ";
                }

                var text = "#d\r\n";
                text += "#k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━━┓\r\n";
                text += "  #L" + 刷新 + "#" + xx + "刷新人员状况[当前人数：#r" + cm.getMapFactory().getMap(列表.入口地点).getCharactersSize() + "#d]" + xx + "#l\r\n\r\n";
                text += "\t#d" + 花花1 + " 欢迎来到:[#r" + 列表.事件名称 + "远征队#d]\r\n";
                text += "\t#d" + 花花1 + " 入场要求:等级[#r" + 列表.等级限制 + "#d]级以上！\r\n";
                text += "\t#d" + 花花1 + " 入场限制:每天挑战[#r" + 列表.挑战限制 + "#d]次！" + (扩充Log != "" && 扩充Log != null ? (cm.getPlayer().getBossLog(扩充Log) >= 1 ? " (次数扩充:#r" + cm.getPlayer().getBossLog(扩充Log) + "#d次)" : "") : "") + "\r\n";
                if (列表.条件.length != 0) {
                    text += "\t#d" + 花花1 + " 入场消耗:" + 条件文本 + "\r\n";
                }
                text += "\t#d" + 花花1 + " 副本通关时，全员可获得奖项(#r独立系统#d)！\r\n";
                text += "\t#d" + 花花1 + " #r提示1:所在地图中，所有人员达成要求即可参与！#d\r\n";
                text += "\t#d" + 花花1 + " #r提示2:资质 (#k√#r) 才可参与入场！#d\r\n";
            //    text += "\t#d" + 花花1 + " #r提示3:掉线成员可支付:#k" + 列表.重返金币费用 + "#r金币重返战场！#d\r\n";
               text += "\t\t\t    #L" + 入场 + "#" + xx + "开始执行任务" + xx + "#l\r\n\r\n";
                text += "   " + 分割线 + "当前地图中人员信息" + 分割线 + "\r\n";
                text += 读取地图人员信息(列表.挑战记录, 列表.挑战限制, 列表.入口地点, 列表.条件) + "\r\n";
                text += "#k┗━━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
                cm.sendYesNo(text);
            }
        } else if (status == 1) {
    if (xx == 0) {
        cm.warp(列表1.返回地点, 0);
        cm.dispose();
        return;
    }
    sele1 = selection;
    switch (sele1) {
        case 刷新:
            start();
            return;
        case 入场:
            var em = cm.getEventManager(列表.事件名称);

            if (em == null) {
                cm.sendOk("#e#d副本出错，请联系作者修复！\r\n");
            } else {
                if (cm.getPlayerCount(列表.挑战地点) == 0) {
                    em.setProperty("状态", "0");
                    cm.getMap(列表.挑战地点).resetFully();
                }
                if (cm.getPlayerCount(列表.挑战地点) > 0) {
                    cm.sendOk("当前副本已经在被挑战无法入场");
                    cm.dispose();
                    return;
                }

                if (cm.getParty() == null) {
                    cm.sendOk("请组队再来找我....");
                    cm.dispose();
                }

                // 只允许队长点击进入
                if (!cm.isLeader()) {
                    cm.sendOk("请叫你的队长来找我!");
                    cm.dispose();
                    break;
                }

        var prop = em.getProperty("状态");
        if (parseInt(prop) == 0 || prop == null) {
            var party = cm.getParty();
            var partyMembers = party.getMembers();
            var partyChars = [];
            var 入场人数 = 0;
            var 不足人员 = []; // 存储资质不足的人员信息
            var 提示信息 = ""; // 存储提示信息

            // 获取队伍中所有在线玩家角色
            for (var i = 0; i < partyMembers.size(); i++) {
                var member = partyMembers.get(i);
                var chr = cm.getChannelServer().getPlayerStorage().getCharacterById(member.getId());
                if (chr != null) {
                    partyChars.push(chr);
                }
            }

            for (var i = 0; i < partyChars.length; i++) {
                var chr = partyChars[i];
                // 检查是否在当前入口地图
                if (chr.getMapId() != 列表.入口地点) {
                    不足人员.push({ name: chr.getName(), reason: "不在入口地图" });
                    continue;
                }

                var 门票条件 = true;
                for (var j = 0; j < 列表.条件.length; j++) {
                    if (chr.getItemQuantity(列表.条件[j][0], false) < 列表.条件[j][1]) {
                        门票条件 = false;
                    }
                }
                var 上限次数 = 列表.挑战限制;
                if (扩充Log != "" && 扩充Log != null) {
                    if (chr.getBossLog(扩充Log) >= 1) {
                        上限次数 += chr.getBossLog(扩充Log);
                    }
                }

                // 检查玩家是否满足条件
                if (chr.getLevel() < 列表.等级限制) {
                    不足人员.push({ name: chr.getName(), reason: "#r等级不足#k" });
                } else if (chr.getBossLog(列表.挑战记录) >= 上限次数) {
                    不足人员.push({ name: chr.getName(), reason: "#r挑战次数已达上限#k" });
                } else if (!门票条件) {
                    不足人员.push({ name: chr.getName(), reason: "#r门票条件不足#k" });
                } else {
                    入场人员.push({ chr: chr, name: chr.getName() });
                    入场人数++;
                }
            }

            // 如果有资质不足的人员，提示队长
            if (不足人员.length > 0) {
                for (var n = 0; n < 不足人员.length; n++) {
                    提示信息 += " - " + 不足人员[n].name + "：" + 不足人员[n].reason + "\r\n";
                }
                cm.sendOk("以下队员资质不足，无法进入副本：\r\n" + 提示信息);
                cm.dispose();
                return;
            }

            em.setProperty("怪物列表", JSON.stringify(列表.怪物列表));
            em.setProperty("公共奖项", JSON.stringify(列表.公共奖项));
            em.setProperty("入场人数", 入场人数);
            em.setProperty("触发奖项怪物", 列表.触发奖项怪物);

            for (var k = 0; k < 入场人员.length; k++) {
                var chr = 入场人员[k].chr;
                for (var l = 0; l < 列表.条件.length; l++) {
                    var id = 列表.条件[l][0];
                    var num = 列表.条件[l][1];
                    全员扣除(chr, id, num);
                }
				cm.getMap(列表.挑战地点).killAllMonsters(true); // 添加了布尔参数 true  杀怪，对于有些地图进入多刷一只杀掉
                chr.setBossLog(列表.挑战记录, 1); // 每日LOG
                em.setProperty("Event", chr.getName());
                em.setProperty("Time", 60 * 列表.挑战时限);
                em.setProperty("增加血量", 列表.增加血量);
                em.setProperty("Breakmap", 列表.返回地点);
                em.setProperty("Fieldmap", 列表.挑战地点);
                
                em.startInstance(chr.getClient().getPlayer(), chr.getMap());
            }
            
            // 发送喇叭公告
            var 队长名字 = cm.getPlayer().getName();
            var 副本名称 = 列表.事件名称;
            cm.worldMessage(6, "[" + 队长名字 + "] 带领队伍进入远征副本【" + 副本名称 + "】祝他们挑战成功！");
            cm.worldMessage(6, "[" + 队长名字 + "] 带领队伍进入远征副本【" + 副本名称 + "】祝他们挑战成功！");
            cm.worldMessage(6, "[" + 队长名字 + "] 带领队伍进入远征副本【" + 副本名称 + "】祝他们挑战成功！");
            em.setProperty("入场时间", 读取时间() + 60 * 列表.挑战时限); // 重返成员倒计时
            em.setProperty("入场人员", JSON.stringify(入场人员));

            cm.dispose();
        }
    }
    break;

            }
        }
    }
}



function 全员扣除(chr, id, num) {
	var itemType = Packages.constants.GameConstants.getInventoryType(id);
	Packages.server.MapleInventoryManipulator.removeById(chr.getClient(), itemType, id, num, true, false);
}




function 读取时间() {//读取秒钟
	var ca = new Date();
	var 秒 = Math.ceil(ca / 1000);//
	return 秒;
}

/*
cm.getPlayer().getBossLog("名字");//角色
cm.getPlayer().getBossLog("名字",true);//账号

cm.getPlayer().setBossLog("名字",次数);//永久LOG
cm.getPlayer().setBossLog("名字",次数);//每日LOG
*/

function 读取地图人员信息(Log, limit, mapid, cost) {
    var text = "#d";
    var party = cm.getParty();
    
    // 检查是否有队伍
    if (party == null) {
        return "\t当前未组队，请先组建队伍\r\n";
    }
    
    var partyMembers = party.getMembers();
    text += "\t" + 美化(12, "次数") + "" + 美化(23, "玩家") + "" + 美化(14, "资质") + "\r\n";
    
    // 获取当前入口地图
    var entryMap = cm.getMapFactory().getMap(mapid);
    
    // 检查队伍成员
    for (var i = 0; i < partyMembers.size(); i++) {
        var member = partyMembers.get(i);
        var chr = cm.getChannelServer().getPlayerStorage().getCharacterById(member.getId());
        
        // 检查玩家是否在线且在入口地图
        if (chr != null && chr.getMapId() == mapid) {
            var 门票条件 = true;
            for (var j = 0; j < cost.length; j++) {
                if (chr.getItemQuantity(cost[j][0], false) < cost[j][1]) {
                    门票条件 = false;
                }
            }
            var 上限次数 = limit;
            if (扩充Log != "" && 扩充Log != null) {
                if (chr.getBossLog(扩充Log) >= 1) {
                    上限次数 += chr.getBossLog(扩充Log);
                }
            }
            var 次数 = "" + 美化(20, "（#b" + chr.getBossLog(Log) + "#d/#r" + 上限次数 + "#d）") + "";
            var 玩家 = "" + 美化(24, "" + chr.getName()) + "";
            
            // 检查玩家在入口地图时的资格
            if (chr.getLevel() >= 列表.等级限制 && chr.getBossLog(Log) < 上限次数 && 门票条件 == true) {
                text += "\t" + 次数 + 玩家 + "    (#k√#d)\r\n";
            } else {
                text += "\t" + 次数 + 玩家 + "    (#r×#d)\r\n";
            }
        } else if (chr != null && chr.getMapId() != mapid) {
            // 显示队伍中不在入口地图的成员
            var 玩家 = "" + 美化(24, "" + chr.getName()) + "";
            text += "\t" + 美化(8, "（不在入口）") + 玩家 + "    (#r×#d)\r\n";
        }
    }
    
    // 显示入口地图中的非队伍成员(可选，根据需要取消注释)
    /*
    var list = entryMap.getCharactersThreadsafe();
    for (var i = 0; i < list.length; i++) {
        var chr = list[i];
        // 检查是否已经在队伍成员列表中显示过
        var inParty = false;
        for (var j = 0; j < partyMembers.size(); j++) {
            var member = partyMembers.get(j);
            if (member.getId() == chr.getId()) {
                inParty = true;
                break;
            }
        }
        if (!inParty) {
            var 玩家 = "" + 美化(24, "" + chr.getName()) + "";
            text += "\t" + 美化(18, "（非队友）") + 玩家 + "      (#r×#d)\r\n";
        }
    }
    */
    
    return text;
}


function 美化(length, content) {
	var str = "";
	var cs1 = "";
	var cs2 = "";
	if (content.length > length) {
		str = content;
	} else {
		for (var j = 0; j < length - content.getBytes("GB2312").length; j++) {
			if ((j + 1) % 2 == 0) {
				cs2 += " ";
			} else {
				cs1 += " ";
			}
		}
	}
	str = cs1 + content + cs2;
	return str;
}

function 字符串转换为组(str) {
	var tempString = str.toString().split("");
	return tempString;
}

function 提取字符串数值(str) {
	var num = str.replace(/[^0-9]/ig, "");//提取字符串中的数值  返回 = 字符串数值(较为精准)
	var num = str.match(/\d+(.\d+)?/g);//提取字符串中的数值  返回 = 字符串数值(不太精准)
	return num;
}

function 判断背包空间_素组(list) {
	var text = "#e#d";
	var 检测背包 = true;
	var k1 = 0; var k2 = 0; var k3 = 0; var k4 = 0; var k5 = 0;
	for (var i = 0; i < list.length; i++) {
		var is = list[i];
		if (is.代码 >= 1000000 && is.代码 <= 1999999) { k1++; };
		if (is.代码 >= 2000000 && is.代码 <= 2999999) { k2++; };
		if (is.代码 >= 3000000 && is.代码 <= 3999999) { k3++; };
		if (is.代码 >= 4000000 && is.代码 <= 4999999) { k4++; };
		if (is.代码 >= 5000000 && is.代码 <= 5999999) { k5++; };
	}
	var 装备栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getNumFreeSlot();
	var 消耗栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.USE).getNumFreeSlot();
	var 设置栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.SETUP).getNumFreeSlot();
	var 其他栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.ETC).getNumFreeSlot();
	var 现金栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.CASH).getNumFreeSlot();
	if (装备栏空位 < k1) { 检测背包 = false; text += " 请确保#r装备#d栏有 #r" + k1 + "#d 空间以上！\r\n"; };
	if (消耗栏空位 < k2) { 检测背包 = false; text += " 请确保#r消耗#d栏有 #r" + k2 + "#d 空间以上！\r\n"; };
	if (设置栏空位 < k3) { 检测背包 = false; text += " 请确保#r设置#d栏有 #r" + k3 + "#d 空间以上！\r\n"; };
	if (其他栏空位 < k4) { 检测背包 = false; text += " 请确保#r其他#d栏有 #r" + k4 + "#d 空间以上！\r\n"; };
	if (现金栏空位 < k5) { 检测背包 = false; text += " 请确保#r现金#d栏有 #r" + k5 + "#d 空间以上！\r\n"; };
	return ret = { bool: 检测背包, text: text };
}

function 判断背包空间_单个(itemid) {
	var text = "#e#d";
	var 检测背包 = true;
	var k1 = 0; var k2 = 0; var k3 = 0; var k4 = 0; var k5 = 0;
	if (itemid >= 1000000 && itemid <= 1999999) { k1++; };
	if (itemid >= 2000000 && itemid <= 2999999) { k2++; };
	if (itemid >= 3000000 && itemid <= 3999999) { k3++; };
	if (itemid >= 4000000 && itemid <= 4999999) { k4++; };
	if (itemid >= 5000000 && itemid <= 5999999) { k5++; };
	var 装备栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getNumFreeSlot();
	var 消耗栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.USE).getNumFreeSlot();
	var 设置栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.SETUP).getNumFreeSlot();
	var 其他栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.ETC).getNumFreeSlot();
	var 现金栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.CASH).getNumFreeSlot();
	if (装备栏空位 < k1) { 检测背包 = false; text += " 请确保#r装备#d栏有 #r" + k1 + "#d 空间以上！\r\n"; };
	if (消耗栏空位 < k2) { 检测背包 = false; text += " 请确保#r消耗#d栏有 #r" + k2 + "#d 空间以上！\r\n"; };
	if (设置栏空位 < k3) { 检测背包 = false; text += " 请确保#r设置#d栏有 #r" + k3 + "#d 空间以上！\r\n"; };
	if (其他栏空位 < k4) { 检测背包 = false; text += " 请确保#r其他#d栏有 #r" + k4 + "#d 空间以上！\r\n"; };
	if (现金栏空位 < k5) { 检测背包 = false; text += " 请确保#r现金#d栏有 #r" + k5 + "#d 空间以上！\r\n"; };
	return ret = { bool: 检测背包, text: text };
}

function 分割线1() {
	var text = " ";
	var list = [
		//"#fEffect/CharacterEff.img/1022223/7/0#",
		//"#fEffect/CharacterEff.img/1022223/8/0#",
		"1",
	];
	for (var i = 0; i < 24; i++) {
		var random = Math.floor(Math.random() * list.length);
		text += list[random];
	}
	text += " ";
	return text;
}

function 更改呈现奖励(类型, 数量) {
	switch (类型) {
		case "点券":
			if (数量 != 0 && 数量 != null) { cm.gainNX(数量) };
			break;
		case "抵用":
			if (数量 != 0 && 数量 != null) { cm.gainDY(数量) };
			break;
		case "金币":
			if (数量 != 0 && 数量 != null) { cm.gainMeso(数量) };
			break;
		case "经验":
			if (数量 != 0 && 数量 != null) { cm.gainExp(数量) };
			break;
		default:
			break;
	}
}

function 呈现奖励货币(类型, 数量) {
	var 章鱼 = "1";
	var 蘑菇 = "1";
	var 绿水 = "1";
	var 猪猪 = "1";
	var text = "";
	switch (类型) {
		case "点券":
			text += (数量 != 0 && 数量 != null ? "" + 章鱼 + "奖励: #r" + 数量 + "#d点券" : "");
			break;
		case "抵用":
			text += (数量 != 0 && 数量 != null ? "" + 蘑菇 + "奖励: #r" + 数量 + "#d抵用" : "");
			break;
		case "金币":
			text += (数量 != 0 && 数量 != null ? "" + 绿水 + "奖励: #r" + 数量 + "#d金币" : "");
			break;
		case "经验":
			text += (数量 != 0 && 数量 != null ? "" + 猪猪 + "奖励: #r" + 数量 + "#d经验" : "");
			break;
		default:
			break;
	}
	return text;
}

function sqlSelect(sql) {
	var data = Packages.database.DatabaseConnection;
	var con = data.getConnection();
	var ret = new Array();
	var ps = con.prepareStatement(sql);
	var rs = ps.executeQuery();
	var metaData = ps.getMetaData();
	while (rs.next()) {
		var rsdata = new java.util.HashMap();
		for (var j = 1; j <= metaData.getColumnCount(); j++) {
			columnLabel = metaData.getColumnLabel(j);
			rsdata.put(columnLabel, rs.getObject(columnLabel));
		}
		if (!rsdata.isEmpty()) {
			ret.push(rsdata);
		}
	}
	rs.close();
	ps.close();
	con.close();
	return ret;
}

function sqlMultiPurpose(sql) {//
	var data = Packages.database.DatabaseConnection;
	var con = data.getConnection();
	var ps = con.prepareStatement(sql);
	ret = ps.executeUpdate();
	ps.close();
	con.close();
	return ret;
}