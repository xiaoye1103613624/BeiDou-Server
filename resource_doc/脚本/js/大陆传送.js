var 花花1 = "#fUI/GuildMark/Mark/Pattern/00004020/1#";
var 花花2 = "#fUI/GuildMark/Mark/Pattern/00004020/3#";
var 花花3 = "#fUI/GuildMark/Mark/Pattern/00004020/5#";
var 花花4 = "#fUI/GuildMark/Mark/Pattern/00004020/7#";
var 花花5 = "#fUI/GuildMark/Mark/Pattern/00004020/9#";
var 花花6 = "#fUI/GuildMark/Mark/Pattern/00004020/11#";
var 花花7 = "#fUI/GuildMark/Mark/Pattern/00004020/13#";
var 花花8 = "#fUI/GuildMark/Mark/Pattern/00004020/14#";
var 花花9 = "#fUI/GuildMark/Mark/Pattern/00004020/15#";
var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 爱心 = "#fEffect/CharacterEff/1032063/0/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#";
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#";
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#";
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#";
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#"; 
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#"; 
var 任务简介 = "#fUI/UIWindow.img/Quest/summary#"; 
var 任务提示 = "#fUI/UIWindow.img/Quest/BtAlert/mouseOver/0#"; 
var 传送中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/6#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#"; 
var 经验 = "#fUI/UIWindow.img/Family/RightIcon/2#";  
var 经验1 = "#fUI/UIWindow.img/Family/RightIcon/3#";  
var 经验2 = "#fUI/UIWindow.img/Family/RightIcon/4#";  
var daobaMS = 0;

// 仙级名称列表
var 仙级名称 = [
    "凡人", "筑基", "金丹", "元婴", "出窍", "分神", "合体", "渡劫", 
    "大乘", "天仙", "仙君", "玄仙", "仙帝", "神人", "神将", "神君", 
    "神帝", "神皇", "神尊", "圣人", "至尊", "主宰", "永恒", "创世", 
	"超脱"
];

// 检测玩家当前仙级
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
            cm.sendOk("那好吧,切记不断修炼,提升战斗力...平时别偷懒哦");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {//
            // 展示代码
            var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
            var 当前仙级名称 = 仙级名称[当前仙级];
            var text = "";
            text += "\r\n\t\t\t" + 传送中心 + "\r\n" + 群粉心 + "";
            text += "#r当前仙级：#e#b" + 当前仙级名称 + "#n#l#n\r\n";
            text += "\t\t\t  #r#e#L0#" + 花花1 + " 进入圣殿（金丹）" + 花花1 + "#l#n\r\n\r\n\r\n";
            text += "\t\t\t  #r#e#L1#" + 花花5 + " 进入峡谷（元婴）" + 花花5 + "#l\r\n\r\n\r\n";
            if (当前仙级 >= 3) { // 只有达到元婴期的玩家才能看到以下选项
            text += "\t\t\t#r#e#L2#" + 花花2 + " 进入峡谷（出窍）" + 花花2 + "#l\r\n\r\n\r\n";
            text += "\t\t\t#r#e#L3#" + 花花3 + " 进入峡谷（分神）" + 花花3 + "#l\r\n\r\n\r\n";
            }
            cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 0) { // 进入圣殿
                var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
                if (当前仙级 >= 1) { // 检测是否达到元婴
                    cm.warp(230050000, 0); // 示例：传送玩家到地图ID为230050000的位置
                    cm.dispose();
                } else {
                    cm.sendOk("你尚未达到筑基期，无法进入！");
                    cm.dispose();
                }
            } else if (selection == 1) { // 进入峡谷
                var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
                if (当前仙级 >= 2) { // 检测是否达到出窍
                    cm.warp(401100100, 0); // 示例：传送玩家到地图ID为230050000的位置
                    cm.dispose();
                } else {
                    cm.sendOk("你尚未达到金丹期，无法进入！");
                    cm.dispose();
                }
            } else if (selection == 2) { // 进入峡谷
                var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
                if (当前仙级 >= 3) { // 检测是否达到出窍
                    cm.warp(910028201, 0); // 示例：传送玩家到地图ID为230050000的位置
                    cm.dispose();
                } else {
                    cm.sendOk("你尚未达到元婴期，无法进入！");
                    cm.dispose();
                }
            } else if (selection == 3) { // 进入峡谷
                var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
                if (当前仙级 >= 4) { // 检测是否达到出窍
                    cm.warp(910028100, 0); // 示例：传送玩家到地图ID为230050000的位置
                    cm.dispose();
                } else {
                    cm.sendOk("你尚未达到出窍期，无法进入！");
                    cm.dispose();
                }
            }
			
			
        }
    }
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