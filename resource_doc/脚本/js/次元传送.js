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
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#"; 
var 经验 = "#fUI/UIWindow.img/Family/RightIcon/2#";  
var 经验1 = "#fUI/UIWindow.img/Family/RightIcon/3#";  
var 经验2 = "#fUI/UIWindow.img/Family/RightIcon/4#";  
var daobaMS = 0;

// 仙级名称列表（索引=境界等级：0=凡人、1=筑基……21=主宰、22=创世、23=永恒、24=超脱）
var 仙级名称 = [
    "凡人", "筑基", "金丹", "元婴", "出窍", "分神", "合体", "渡劫", 
    "大乘", "天仙", "仙君", "玄仙", "仙帝", "神人", "神将", "神君", 
    "神帝", "神皇", "神尊", "主宰", "创世"
];

// 大陆配置（核心修正：境界要求 严格对应仙级名称索引，其余原配置不变）
var 大陆列表 = [
    {id: 0, 名称: "青苍凡界（0大陆" + 小蓝星 + "凡人）", 图标: 花花9, 境界要求: 0, 地图ID: 910000025},  // 凡人=0
    {id: 1, 名称: "维利塔斯（1大陆" + 小蓝星 + "筑基）", 图标: 花花1, 境界要求: 1, 地图ID: 230050000},  // 筑基=1
    {id: 2, 名称: "丹霞丹域（2大陆" + 小蓝星 + "金丹）", 图标: 花花5, 境界要求: 2, 地图ID: 910000026},  // 金丹=2
    {id: 3, 名称: "世界之树（3大陆" + 小蓝星 + "元婴）", 图标: 花花5, 境界要求: 3, 地图ID: 105200000},  // 元婴=3
    {id: 4, 名称: "云渺窍天（4大陆" + 小蓝星 + "出窍）", 图标: 花花2, 境界要求: 4, 地图ID: 910000027},  // 出窍=4
    {id: 5, 名称: "苍梧神洲（5大陆" + 小蓝星 + "分神）", 图标: 花花2, 境界要求: 5, 地图ID: 910000028},  // 分神=5
    {id: 6, 名称: "枫叶城堡（6大陆" + 小蓝星 + "合体）", 图标: 花花2, 境界要求: 6, 地图ID: 910028300},  // 合体=6
    {id: 7, 名称: "陨劫渊陆（7大陆" + 小蓝星 + "渡劫）", 图标: 花花3, 境界要求: 7, 地图ID: 910000029},  // 渡劫=7
    {id: 8, 名称: "大乘天洲（8大陆" + 小蓝星 + "大乘）", 图标: 花花3, 境界要求: 8, 地图ID: 910000030},  // 大乘=8
    {id: 9, 名称: "魔法庭院（9大陆" + 小蓝星 + "天仙）", 图标: 花花4, 境界要求: 9, 地图ID: 910024000},  // 天仙=9
    {id: 10, 名称: "紫宸君陆（10大陆" + 小蓝星 + "仙君）", 图标: 花花4, 境界要求: 10, 地图ID: 910000031}, // 仙君=10
    {id: 11, 名称: "玄清仙域（11大陆" + 小蓝星 + "玄仙）", 图标: 花花4, 境界要求: 11, 地图ID: 910000032}, // 玄仙=11
    {id: 12, 名称: "云林仙宫（12大陆" + 小蓝星 + "仙帝）", 图标: 花花4, 境界要求: 12, 地图ID: 410000124}, // 仙帝=12
    {id: 13, 名称: "浮天神洲（13大陆" + 小蓝星 + "神人）", 图标: 花花6, 境界要求: 13, 地图ID: 910000033}, // 神人=13
    {id: 14, 名称: "曜武神陆（14大陆" + 小蓝星 + "神将）", 图标: 花花6, 境界要求: 14, 地图ID: 910000034}, // 神将=14
    {id: 15, 名称: "大 神 殿（15大陆" + 小蓝星 + "神君）", 图标: 花花6, 境界要求: 15, 地图ID: 940011070}, // 神君=15
    {id: 16, 名称: "万仞神帝洲（16大陆" + 小蓝星 + "神帝）", 图标: 花花7, 境界要求: 16, 地图ID: 910000035}, // 神帝=16
    {id: 17, 名称: "苍宇神皇域（17大陆" + 小蓝星 + "神皇）", 图标: 花花7, 境界要求: 17, 地图ID: 910000036},//神皇=17（原标注无，保留）
    {id: 18, 名称: "圣地庭院（18大陆" + 小蓝星 + "神尊）", 图标: 花花7, 境界要求: 18, 地图ID: 350012020}, // 神尊=18
    {id: 19, 名称: "黑暗领域（19大陆" + 小蓝星 + "主宰）", 图标: 花花8, 境界要求: 19, 地图ID: 927020070}, // 主宰=21（按仙级列表索引）
    {id: 20, 名称: "忘却森林（20大陆" + 小蓝星 + "创世）", 图标: 花花6, 境界要求: 20, 地图ID: 802000101}  // 创世=22（按仙级列表索引）
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
        if (status == 0) {
            // 展示代码
            var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
            var 当前仙级名称 = 仙级名称[当前仙级];
            var text = "";
            text += "\r\n\t\t\t" + 传送中心 + "\r\n" + 群粉心 + "";
            text += "#r当前仙级：#e#k" + 当前仙级名称 + "#n#l#n\r\n";
            
            // 按0-24顺序遍历大陆列表，拼接展示文本
            for (var i = 0; i < 大陆列表.length; i++) {
                var 大陆 = 大陆列表[i];
                // 所有大陆都显示（按你的要求，判断条件为>=0）
                if (当前仙级 >= 0) {
                    text += "\t   #r#e#L" + 大陆.id + "#" + 大陆.图标 + " " + 大陆.名称 + 大陆.图标 + "#l#n\r\n\r\n\r\n";
                }
            }
            
            cm.sendSimple(text);
        } else if (status == 1) {
            var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
            // 按选择项匹配传送逻辑
            var 选中大陆 = 大陆列表[selection];
            if (选中大陆) {
                if (当前仙级 <= 选中大陆.境界要求) {
                    cm.warp(选中大陆.地图ID, 0); 
                    cm.dispose();
                } else {
                    cm.sendOk("你尚未达到" + 仙级名称[选中大陆.境界要求] + "期，无法进入！");
                    cm.dispose();
                }
            } else {
                cm.sendOk("无效的选择！");
                cm.dispose();
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