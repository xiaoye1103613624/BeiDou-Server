var 红箭头 = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var xmxsz = new Array(
	//第一行为占位用，请勿操作
	{ 仙级: "凡人", 层数: 0, 属性点: 0 , 飞升奖励: Array([4000017,10],[0,500],[1,100],[2,200]) , 飞升率: 100 , 飞升材料: Array([4000000,10],[0,10000],[1,500],[2,1000]) , 渡劫率 : 100 , 渡劫材料: Array([4000001,10],[4000002,10],[0,200000],[1,1000],[2,5000])   },
	//这里开始后就阔以修改参数  奖励和材料数据格式 其他ID=物品类型  0-金币  1是点卷 2是抵用
	//这里开始后就阔以修改参数  奖励和材料数据格式 其他ID=物品类型  0-金币  1是点卷 2是抵用
	{ 仙级: "筑基", 层数: 2, 属性点: 10 , 飞升奖励: Array([2614006,1]) ,飞升率: 100 , 飞升材料: Array([4310143,10],[1,500],[2,1000]) , 渡劫率 : 100 , 渡劫材料: Array([4001084,1],[4001083,1])   },
	{ 仙级: "金丹", 层数: 3, 属性点: 10 , 飞升奖励: Array([2614006,2]) ,飞升率: 100 , 飞升材料: Array([3605011,15],[1,1000],[2,2000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,10],[3700290,10])   },
	{ 仙级: "元婴", 层数: 15, 属性点: 10 , 飞升奖励: Array([2614006,3]) ,飞升率: 100 , 飞升材料: Array([3605011,20],[1,2000],[2,4000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,30],[3700290,20])   },
	{ 仙级: "出窍", 层数: 20, 属性点: 10 , 飞升奖励: Array([2614012,3]) ,飞升率: 100 , 飞升材料: Array([3605011,25],[1,3000],[2,6000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,50],[3700290,30])   }, 
	
	{ 仙级: "分神", 层数: 25, 属性点: 10 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3605011,30],[1,4000],[2,8000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,70],[3700290,50])   },  
	{ 仙级: "合体", 层数: 30, 属性点: 10 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3605011,35],[1,5000],[2,10000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,100],[3605009,10])   },
	{ 仙级: "渡劫", 层数: 35, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3605011,40],[1,6000],[2,12000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,300],[3605009,20])   },
	{ 仙级: "大乘", 层数: 40, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3605011,50],[1,7000],[2,14000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,500],[3605009,30])   },
	
	{ 仙级: "天仙", 层数: 45, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3605011,100],[1,8000],[2,16000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,700],[3605009,50])   },
	{ 仙级: "仙君", 层数: 50, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3605011,150],[1,9000],[2,18000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,1000],[3700291,10])   },
	{ 仙级: "玄仙", 层数: 55, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3605011,200],[1,10000],[2,20000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,3000],[3700291,30])   },
	{ 仙级: "仙帝", 层数: 60, 属性点: 15 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3605011,250],[1,11000],[2,22000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,5000],[3700291,50])   },
	
	{ 仙级: "神人", 层数: 65, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3700292,5],[1,22000],[2,34000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,15000],[3700294,10])   },
	{ 仙级: "神将", 层数: 70, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3700292,5],[1,23000],[2,36000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,16000],[3700294,15])   },
	{ 仙级: "神君", 层数: 75, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3700292,5],[1,24000],[2,38000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,17000],[3700294,20])   }, 
	{ 仙级: "神帝", 层数: 80, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3700292,10],[1,25000],[2,40000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,18000],[3700294,25])   }, 
	{ 仙级: "神皇", 层数: 85, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3700292,10],[1,26000],[2,42000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,19000],[3700294,30])   }, 
	{ 仙级: "神尊", 层数: 90, 属性点: 20 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3700292,10],[1,27000],[2,44000]) , 渡劫率 : 100 , 渡劫材料: Array([4031952,20000],[3700294,40])   },  
	
	{ 仙级: "圣人", 层数: 95, 属性点: 30 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3700292,15],[1,28000],[2,46000]) , 渡劫率 : 100 , 渡劫材料: Array([4321026,1000],[3700293,50])   },  
	{ 仙级: "至尊", 层数: 100, 属性点: 30 , 飞升奖励: Array([2614012,1]) ,飞升率: 100 , 飞升材料: Array([3700292,20],[1,29000],[2,48000]) , 渡劫率 : 100 , 渡劫材料: Array([4321026,2000],[3700293,100])   },  
	{ 仙级: "主宰", 层数: 200, 属性点: 30 , 飞升奖励: Array([2614007,1]) ,飞升率: 100 , 飞升材料: Array([3700292,25],[3994731,25],[1,88888],[2,88888]) , 渡劫率 : 100 , 渡劫材料: Array([4321026,3000],[3700293,200])   },// 3605020 卡飞升
	
	{ 仙级: "永恒", 层数: 999 , 属性点: 0 , 飞升奖励: Array([2350014,1]) ,飞升率: 100 , 飞升材料: Array([4001126,10000],[4000313,10000],[3994731,10],[1,100000],[2,100000]) , 渡劫率 : 100 , 渡劫材料: Array([4321026,10000],[3605012,200])   },
	{ 仙级: "创世", 层数: 3999, 属性点: 0 , 飞升奖励: Array([2550003,1]) ,飞升率: 100 , 飞升材料: Array([4001126,20000],[4000313,20000],[3994731,20],[1,100000],[2,100000]) , 渡劫率 : 100 , 渡劫材料: Array([4321026,20000],[3605012,400])   },
	{ 仙级: "超脱", 层数: 9999, 属性点: 0 , 飞升奖励: Array([3602000,1]) ,飞升率: 100 , 飞升材料: Array([4001126,30000],[4000313,30000],[3994731,30],[1,100000],[2,100000]) , 渡劫率 : 100 , 渡劫材料: Array([4321026,30000],[3605012,800])   }
//总属性+25800   1250层50、100、150、200、250、300、525、600、750、825、900、1300、1400、1500、1600、1700、1800、2850、3000、6000总和是25800  ---需要 17302500点券飞升满级
);
/*
| 区间 | 累计层数  | 每层 AP | 小计 AP | 区间累计 AP |
|  --  | ----- | ----- | ----- | ------- |
| 筑基 | 2     | 10    | 20    | 20      |
| 金丹 | 3     | 10    | 30    | 50      |
| 元婴 | 15    | 10    | 150   | 200     |
| 出窍 | 20    | 10    | 200   | 400     |
| 分神 | 25    | 10    | 250   | 650     |
| 合体 | 30    | 10    | 300   | 950     |
| 渡劫 | 35    | 15    | 525   | 1 475   |
| 大乘 | 40    | 15    | 600   | 2 075   |
| 天仙 | 45    | 15    | 675   | 2 750   |
| 仙君 | 50    | 15    | 750   | 3 500   |
| 玄仙 | 55    | 15    | 825   | 4 325   |
| 仙帝 | 60    | 15    | 900   | 5 225   |
| 神人 | 65    | 20    | 1 300 | 6 525   |
| 神将 | 70    | 20    | 1 400 | 7 925   |
| 神君 | 75    | 20    | 1 500 | 9 425   |
| 神帝 | 80    | 20    | 1 600 | 11 025  |
| 神皇 | 85    | 20    | 1 700 | 12 725  |
| 神尊 | 90    | 20    | 1 800 | 14 525  |
| 圣人 | 95    | 30    | 2 850 | 17 375  |
| 至尊 | 100   | 30    | 3 000 | 20 375  |
| 主宰 | 200   | 30    | 6 000 | 26 375  |
| 永恒 | 999   | 0     | 0     | 26 375  |
| 创世 | 3 999 | 0     | 0     | 26 375  |
| 超脱 | 9 999 | 0     | 0     | 26 375  |
*/

var xiaomi = {
    转生等级: 200,
    转生等级模式: -1
};
var status = -1;
var target = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 1) status++;
    else status--;

    if (status === 0) {
        var txt = "          #e┣━━━ GM 一键飞升 ━━━┫#n\r\n请选择目标仙级：\r\n";
        for (var i = 1; i < xmxsz.length; i++) {
            txt += "               #L" + i + "#" + 红箭头 + " #r" + xmxsz[i].仙级 + "#k（#b满层 " + xmxsz[i].层数 + "#k）#l\r\n";
        }
        cm.sendSimple(txt);
    } else if (status === 1) {
        target = sel;
        if (target <= 0 || target >= xmxsz.length) {
            cm.sendOk("选择异常。");
            cm.dispose();
            return;
        }
        cm.sendYesNo("确认直接飞升至【" + xmxsz[target].仙级 + "】满层？\r\n"
                   + "将会一次性补齐层数、总转生次数、属性点并发放奖励！");
} else if (status === 2) {
    /* 1. 直接覆盖层数、仙级、总转生次数 */
    var totalCeng = 0;
    for (var i = 1; i <= target; i++) totalCeng += xmxsz[i].层数;

    /* 覆盖而不是累加 */
    setxmwnjljsc("XM飞升系统_仙级",   target);
    setxmwnjljsc("XM飞升系统_当前层", xmxsz[target].层数);
    setxmwnjljsc("XM飞升系统_总转生", totalCeng);

    /* 2. 奖励与属性点发放（不变） */
    // gainItemxiaomizuhe(xmxsz[target].飞升奖励);

    var shouldAp = 0;
    for (var i = 1; i <= target; i++) shouldAp += xmxsz[i].属性点 * xmxsz[i].层数;
	
	shouldAp += xiaomi.转生等级 * 5;   // 200 × 5

    cm.getPlayer().setLevel(xiaomi.转生等级 + xiaomi.转生等级模式);
    cm.getPlayer().levelUp();
    cm.getChar().resetStats(4, 4, 4, 4);
    cm.getPlayer().resetAPSP();
    cm.gainAp(shouldAp);
	cm.gainExp(500000);

    cm.sendOk("飞升完成！当前仙级：" + xmxsz[target].仙级 + "　层数：" + xmxsz[target].层数 + "　总转生：" + totalCeng + " 次");
    cm.dispose();
} else {
    cm.dispose();
}
}

/* ========== 以下直接复用你原脚本里的工具函数 ========== */
/* 覆盖写入：先删再插，保证值准确 */
function setxmwnjljsc(log, val) {
    var acc = cm.getPlayer().getId();
    var con = cm.getConnection();
    var ps = con.prepareStatement("DELETE FROM xmwnjl WHERE characterid=? AND bossid=?");
    ps.setInt(1, acc);
    ps.setString(2, log);
    ps.executeUpdate();
    ps.close();

    ps = con.prepareStatement("INSERT INTO xmwnjl(characterid,bossid,count,time) VALUES (?,?,?,CURRENT_TIMESTAMP())");
    ps.setInt(1, acc);
    ps.setString(2, log);
    ps.setInt(3, val);
    ps.executeUpdate();
    ps.close();
    con.close();
}
function getxmwnjljsc(log) {
    var ret = 0;
    var ps = cm.getConnection().prepareStatement("SELECT count FROM xmwnjl WHERE characterid=? AND bossid=?");
    ps.setInt(1, cm.getPlayer().getId());
    ps.setString(2, log);
    var rs = ps.executeQuery();
    if (rs.next()) ret = rs.getInt(1);
    rs.close(); ps.close();
    return ret;
}
function gainxmwnjljsc(log, add) {
    var acc = cm.getPlayer().getId();
    var con = cm.getConnection();
    var ps = con.prepareStatement("SELECT count FROM xmwnjl WHERE characterid=? AND bossid=?");
    ps.setInt(1, acc); ps.setString(2, log);
    var rs = ps.executeQuery();
    if (rs.next()) {
        rs.close(); ps.close();
        ps = con.prepareStatement("UPDATE xmwnjl SET count=count+? WHERE characterid=? AND bossid=?");
        ps.setInt(1, add); ps.setInt(2, acc); ps.setString(3, log);
        ps.executeUpdate();
    } else {
        rs.close(); ps.close();
        ps = con.prepareStatement("INSERT INTO xmwnjl(characterid,bossid,count,time) VALUES (?,?,?,CURRENT_TIMESTAMP())");
        ps.setInt(1, acc); ps.setString(2, log); ps.setInt(3, add);
        ps.executeUpdate();
    }
    ps.close(); con.close();
}
function gainItemxiaomizuhe(arr) {
    for (var i = 0; i < arr.length; i++) {
        var it = arr[i];
        if (it[0] === 0) cm.gainMeso(it[1]);
        else if (it[0] === 1) cm.getPlayer().modifyCSPoints(1, it[1], true);
        else if (it[0] === 2) cm.getPlayer().modifyCSPoints(2, it[1], true);
        else cm.gainItem(it[0], it[1]);
    }
}