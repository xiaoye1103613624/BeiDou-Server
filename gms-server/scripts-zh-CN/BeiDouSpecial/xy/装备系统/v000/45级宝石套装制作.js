// 可自行修改：兑换产出装备列表
var rewardList = [
    1003242,//冒险岛宝石贝雷帽	
    1052357,//冒险岛宝石外套
    1072521,//冒险岛宝石靴
    1082314,//冒险岛宝石手套
    1102294,//冒险岛宝石披风
    1132092,//冒险岛宝石腰带
    1112422,//炫色板戒指
    1302169,//冒险岛宝石剑
    1312068,//冒险岛宝石斧
    1322099,//冒险岛宝石锤
    1332144,//冒险岛宝石短刀
    1372096,//冒险岛宝石短杖
//	1382120,//冒险岛宝石长杖	有bug 无属性
    1402106,//冒险岛宝石双手剑
    1412067,//冒险岛宝石双手斧
	1422069,//冒险岛宝石双手钝器
	1432095,//冒险岛宝石枪	
	1442132,//冒险岛宝石矛	
	1452125,//冒险岛宝石弓	
	1462113,//冒险岛宝石弩	
	1472136,//冒险岛宝石拳套		
	1482098,//冒险岛宝石指节		
	1492097//冒险岛宝石手枪		

];

// 可自行修改：所需材料 [物品ID, 数量]，可无限增删
var needMaterial = [
    [4001126,88],//枫叶
    [4000313,1],//黄金枫叶
    [4000019,88],//绿色蜗牛壳
    [4000000,88],//蓝色蜗牛壳
    [4000016,88]//红色蜗牛壳

];

var selectIndex;
var status = -1;

// 固定入口，服务端标准写法
function start() {
    action(1, 0, 0);
}

// 核心交互流程（分段status，和你参考代码完全一致）
function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    // 第一页：展示材料 + 可选装备列表
    if (status == 0) {
        var msg = "";
        msg += "\r\n#d兑换所需材料:#b \r\n\r\n";
        // 遍历展示所有材料
        for (var ii = 0; ii < needMaterial.length; ii++) {
            msg += "#i" + needMaterial[ii][0] + "##z" + needMaterial[ii][0] + "#x" + needMaterial[ii][1];
            // 每3个材料换行，排版美观
            if (ii % 3 == 0) {
                msg += "\r\n";
            }
        }
        msg += "\r\n#g----------------------------------------------\r\n";
        // 遍历展示所有可兑换装备
        for (var i = 0; i < rewardList.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "#i" + rewardList[i] + "##z" + rewardList[i] + "##l\r\n";
        }
        // 发送选择界面
        cm.sendSimple("#r 装备兑换使者 #v4000110#\r\n选择你想要兑换的装备：\r\n" + msg);
    }

    // 第二页：校验背包、材料，弹出确认框
    else if (status == 1) {
        selectIndex = selection;
        var targetItem = rewardList[selectIndex];

        // 1. 检查背包是否有空间
        if (!cm.canHold(targetItem)) {
            cm.sendOk("#r背包空间不足，请清理后再来！固有道具只能持有1个。");
            cm.dispose();
            return;
        }

        // 2. 遍历检查所有材料是否足够
        for (var m = 0; m < needMaterial.length; m++) {
            var matId = needMaterial[m][0];
            var matNum = needMaterial[m][1];
            if (!cm.haveItem(matId, matNum)) {
                cm.sendOk("#b材料不足！缺少 #r#i" + matId + "##t" + matId + "# 共" + matNum + "个");
                cm.dispose();
                return;
            }
        }

        // 确认兑换弹窗
        cm.sendYesNo("#b确定要兑换装备 #r #i" + targetItem + "# 吗？");
    }

    // 第三页：扣除材料 + 发放装备，完成兑换
    else if (status == 2) {
        var targetItem = rewardList[selectIndex];
        // 扣除全部材料
        for (var m = 0; m < needMaterial.length; m++) {
            cm.gainItem(needMaterial[m][0], -needMaterial[m][1]);
        }
        // 发放装备
        cm.gainItem(targetItem, 1);
        cm.sendOk("#b兑换成功！已获得装备 #i" + targetItem + "#");
        cm.dispose();
    }

    // 异常处理
    else {
        //cm.sendOk("#r好的，谢谢惠顾，欢迎下次光临！");
        cm.dispose();
    }
}