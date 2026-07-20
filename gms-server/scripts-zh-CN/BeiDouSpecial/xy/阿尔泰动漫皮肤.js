/**
脚本作者：江奈Mizuki
二次元皮肤。
 **/
var ComicList = Array(
    Array(1008956, "佩恩"),
    Array(1008955, "萨博"),
    Array(1008953, "娜美"),
    Array(1008950, "哈尔"),
    Array(1008948, "圣女贞德"),
    Array(1008946, "星见亚砂"),
    Array(1008960, "纲"),
    Array(1008942, "星"),
    Array(1008940, "沢田纲吉"),
    Array(1008939, "天使-艾丽莎"),
    Array(1008933, "露米"),
    Array(1008959, "黑色少女"),
    Array(1008927, "天使"),
    Array(1008926, "迪达拉"),
    Array(1008923, "花江"),
    Array(1008918, "黑岩射手"),
    Array(1008913, "血月"),
    Array(1008910, "潘多拉"),
    Array(1008901, "斗笠死灵"),
    Array(1008900, "带土"),
    Array(1008906, "莉央"),
    Array(1008929, "咖喱柴犬"),
    Array(1009911, "宇智波鼬"),
    Array(1009912, "火影四代目"),
    Array(1009913, "鸣人发光-最终"),
    Array(1009914, "鸣人小新"),
    Array(1009915, "佐助3"),
    Array(1009916, "雏田"),
    Array(1009917, "纲手"),
    Array(1009918, "八门夜凯"),
    Array(1009919, "小南"),
    Array(1009920, "小樱"),
    Array(1009921, "迪达拉"),
    Array(1009923, "止水"),
    Array(1009930, "卡卡西坐下"),
    Array(1009943, "蝎"),
    Array(1009922, "艾斯"),
    Array(1009924, "尼卡路飞"),
    Array(1009927, "海贼王大和"),
    Array(1009937, "四皇路飞"),
    Array(1009938, "威尔"),
    Array(1009939, "夏油杰"),
    Array(1009928, "黑崎一护"),
    Array(1009929, "红发香克斯"),
    Array(1009931, "桔梗X犬夜叉"),
    Array(1009932, "弗利沙"),
    Array(1009925, "不知火舞"),
    Array(1009926, "草薙京"),
    Array(1009934, "魔人布欧"),
    Array(1009933, "eva明日香"),
    Array(1009935, "圣斗士白羊座wu"),
    Array(1009936, "手枪+大刀版吴彦祖（最终版 水印）"),
    Array(1009940, "自在极意 白悟空"),
    Array(1009941, "艾尼路"),
    Array(1009942, "阿拉蕾"),
    Array(1009944, "帝皇龙甲兽"),
    Array(1009945, "粉红幻影大剑 全残影"),
    Array(1009946, "杀生丸"),
    Array(1009947, "天女兽"),
    Array(1009948, "军曹"),
    Array(1009949, "杰尼龟"),
    Array(1009950, "女帝"),
    Array(1009951, "远坂凛"),
    Array(1009952, "黑saber"),
    Array(1009953, "黑saber2"),
    Array(1009954, "高达"),
    Array(1009955, "光能使者阿祖"),
    Array(1009956, "海绵宝宝"),
    Array(1009957, "恐龙小新"),
    Array(1009958, "蜡笔小新 黑道"),
    Array(1009959, "蜡笔小新"),
    Array(1009960, "雷电将军"),
    Array(1009961, "墨镜五条悟"),
    Array(1009962, "尼卡定制3版本混合（水印）"),
    Array(1009963, "尼卡小新"),
    Array(1009964, "尼卡小新~(绝版不出售)"),
    Array(1009965, "骑车小新"),
    Array(1009966, "睡衣小新"),
    Array(1009967, "五条悟合体"),
    Array(1009968, "星见雅"),
    Array(1009969, "星穹1"),
    Array(1009970, "一拳超人"),
    Array(1009971, "一拳超人1"),
    Array(1009972, "泳衣成品"),
    Array(1009973, "泳装枪手"),
    Array(1009974, "圆神"),
    Array(1009975, "天使法"),
    Array(1009976, "天使枪"),
    Array(1009977, "托尔龙女仆"),
    Array(1009978, "家庭教师"),
    Array(1009979, "芙莉莲"),
    Array(1009980, "麻仓叶"),
    Array(1009981, "白贞德"),
    Array(1009982, "ALN4"),
    Array(1009983, "Q版星见雅"),
    Array(1009984, "超天酱"),
    Array(1009985, "纯爱战神"),
    Array(1009986, "独自升级"),
    Array(1009987, "独自升级程小雨"),
    Array(1009988, "红莲暗影"),
    Array(1009989, "瞌睡兔"),
    Array(1009990, "莉央完成"),
    Array(1009991, "千寻"),
    Array(1009992, "蛇女"),
    Array(1009993, "水兵月"),
    Array(1009994, "死灵姐姐"),
    Array(1009995, "妖梦"),
    Array(1009996, "小恶魔"),
    Array(1009997, "铃仙"),
    Array(1009998, "琪露诺"),
    Array(1009999, "蕾米莉亚")
);
var status = -1;
var special = true;
var normalFlag = false;

// 配置兑换所需道具（可自定义ID、数量、名称）
var EXCHANGE_ITEM = {
    id: 4031312,    // 道具ID（示例：雪花结晶球）
    count: 1,       // 所需数量
    name: "雪花结晶球" // 道具名称（用于提示文案）
};

function start() {
    cm.sendNext("哦~嚯嚯嚯！年轻的冒险者，你好呀。\r\n这边是#b超痛超潮的二次元皮肤#k!在获得这份礼物之前，请阅读这份来自分享礼物的朋友的信息：\r\n#r永远记住本款二次元皮肤是MapleAS（746320329）安生大老板出钱，多名等大佬还有本公主Q459937607出力！#k\r\n多亏了他们，在现在的冒险岛公益服之中，我们才能享受这些皮肤！请牢记，他们是无偿分享的，任何人都没有权利在自己的服务器里把这些皮肤作为付费项目。");
} 

function action(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (mode == 0) {
            cm.sendOk("祝你幸福，天天开心。哦嚯嚯嚯。");
        }
        cm.dispose();
        return;
    } 
    
    if (status == 0) {
        var isGm = cm.getPlayer().isGM();
        if (isGm) {
            cm.sendYesNo("那么你想获得#b超痛超潮的二次元皮肤#k吗？\r\n#r[GM模式]#k 无需兑换道具，直接领取180天时效皮肤。");
        } else {
            cm.sendYesNo("那么你想获得#b超痛超潮的二次元皮肤#k吗？拿些稀有的道具来交换吧！\r\n#r兑换条件：需要消耗" + EXCHANGE_ITEM.count + "个#v" + EXCHANGE_ITEM.id + "# " + EXCHANGE_ITEM.name + "#k\r\n");
        }
    } else if (status == 1) {
        if (!cm.getPlayer().isGM() && !cm.haveItem(EXCHANGE_ITEM.id, EXCHANGE_ITEM.count)) {
            cm.sendOk("哦嚯嚯嚯！你没有足够的#v" + EXCHANGE_ITEM.id + "# #b" + EXCHANGE_ITEM.name + "#k（需要" + EXCHANGE_ITEM.count + "个），无法兑换皮肤哦！");
            cm.dispose();
            return;
        }
        if (!(special || normalFlag)) {
            cm.sendOk("哦嚯嚯嚯，没有认真听我说话呀小伙子~");
            cm.dispose();
        } else {
            text = "请选择一款皮肤（共" + ComicList.length + "款）：#b\r\n";
            for (var i = 0; i < ComicList.length; i++) {
                text += "#L" + i + "##v" + ComicList[i][0] + "##k " + ComicList[i][1] + "#l\r\n";
            }
            cm.sendSimple(text);
        }
    } else if (status == 2) {
        if (selection < 0 || selection >= ComicList.length) {
            cm.sendOk("选择无效。");
            cm.dispose();
            return;
        }
        // GM 直接领取；玩家消耗兑换道具
        var isGm = cm.getPlayer().isGM();
        if (!isGm) {
            if (!cm.haveItem(EXCHANGE_ITEM.id, EXCHANGE_ITEM.count)) {
                cm.sendOk("兑换道具不足。");
                cm.dispose();
                return;
            }
            cm.gainItem(EXCHANGE_ITEM.id, -EXCHANGE_ITEM.count);
        }
        if (!cm.canHold(ComicList[selection][0])) {
            cm.sendOk("背包装备栏空间不足。");
            cm.dispose();
            return;
        }
        // 发放180天时效皮肤（expireTime 单位：分钟）
        var Short = Java.type("java.lang.Short");
        var Long = Java.type("java.lang.Long");
        var z = Short.valueOf(0);
        cm.getPlayer().gainEquip(
            ComicList[selection][0],
            z, z, z, z, z, z, z, z, z, z, z, z, z, z, z, z,
            Long.valueOf(180 * 60 * 24)
        );
        var tip = isGm ? "#r[GM]#k 已发放" : ("已扣除#b" + EXCHANGE_ITEM.count + "个#v" + EXCHANGE_ITEM.id + "# " + EXCHANGE_ITEM.name + "#k！\r\n");
        cm.sendOk(tip + "你可以享受180天的#b" + ComicList[selection][1] + "#k皮肤！");
        cm.dispose();
    }
}