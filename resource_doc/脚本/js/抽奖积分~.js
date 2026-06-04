var mi0 = "┏━━━━━━━━━━━┓";
var mi1 = "┃     - XiaoMiMS -     ┃";
var mi2 = "┃ 脚本仿制  　定制脚本 ┃";
var mi3 = "┃ 技术支持 　 游戏顾问 ┃";
var mi4 = "┃ ＷＺ添加　  地图制作 ┃";
var mi5 = "┣━━━━━━━━━━━┫";
var mi6 = "┃　唯一QQ:526703257    ┃";
var mi7 = "┗━━━━━━━━━━━┛";


var xmxsz = new Array(
{ 获得物品: 4310088, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 1, 限额: 0, 备注: "RED" },
{ 获得物品: 2049100, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 1, 限额: 0, 备注: "混沌60%" },
{ 获得物品: 2049124, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 4, 限额: 0, 备注: "混沌20%" },
{ 获得物品: 2049104, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 5, 限额: 0, 备注: "恶魔" },
{ 获得物品: 4310088, 获得数量: 10, 获得限时: 0, 需要类型: 1, 需要数量: 10, 限额: 0, 备注: "RED" },
{ 获得物品: 2049100, 获得数量: 10, 获得限时: 0, 需要类型: 1, 需要数量: 10, 限额: 0, 备注: "混沌60%" },
{ 获得物品: 2049124, 获得数量: 10, 获得限时: 0, 需要类型: 1, 需要数量: 40, 限额: 0, 备注: "混沌20%" },
{ 获得物品: 2049104, 获得数量: 10, 获得限时: 0, 需要类型: 1, 需要数量: 50, 限额: 0, 备注: "恶魔" },
{ 获得物品: 1112443, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 1000, 限额: 0, 备注: "忆江南" },
{ 获得物品: 1112444, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 1000, 限额: 0, 备注: "卷珠帘" },
{ 获得物品: 1022066, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 40, 限额: 0, 备注: "蓝海星眼镜" },
{ 获得物品: 1032061, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 40, 限额: 0, 备注: "发光的阿尔秦耳环%" },
{ 获得物品: 1122265, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 50, 限额: 0, 备注: "中级贝勒德刻印吊坠%" },
{ 获得物品: 03700288, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 50, 限额: 0, 备注: "2倍轮回石碑" },
{ 获得物品: 1112426, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 40, 限额: 0, 备注: "蒲公英戒指" },
{ 获得物品: 2079995, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 80, 限额: 0, 备注: "龙背镖%" },
{ 获得物品: 1022132, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 100, 限额: 0, 备注: "升级眼镜%" },
{ 获得物品: 1032222, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 100, 限额: 0, 备注: "高级贝勒德耳环" },
{ 获得物品: 1122266, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 100, 限额: 0, 备注: "高级贝勒德刻印吊坠" },
{ 获得物品: 1112672, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 100, 限额: 0, 备注: "传说枫叶银戒指" },
{ 获得物品: 1113075, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 300, 限额: 0, 备注: "最高级贝勒德戒指" },
{ 获得物品: 1122267, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 300, 限额: 0, 备注: "最高级贝勒德刻印吊坠" },
{ 获得物品: 1032129, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 1000, 限额: 0, 备注: "希望之树之传说耳环" },
{ 获得物品: 1122185, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 1000, 限额: 0, 备注: "希望之树之传说项链" },
{ 获得物品: 1132135, 获得数量: 1, 获得限时: 0, 需要类型: 1, 需要数量: 1000, 限额: 0, 备注: "希望之树之传说腰带" }



);






var sels;
var 积分;
var status = -1;
var 白金礼包 = "#fItem/Special/0910.img/9101038/icon#";
var 红枫叶 ="#fMap/MapHelper/weather/maple/1#";
var 点券图标 = "#fUI/CashShop/CashItem/0#";
var 金币 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
var M14 = "#fEffect/CharacterEff/1112925/0/0#"; //蓝星
function start() {
    action(1, 0, 0);
}
function action(mode, type, selection) {
  if (mode == 1) {
    status++;
  } else if (mode == 0) {
    //status--;
    cm.dispose();
    return;
  } else {
    cm.dispose();
    return;
  }
  if (status == 0) {
    //积分 = getxmwnjlc("抽奖积分");
    积分 = cm.getBossRankCount2("抽奖积分");
    var msg = "";
    msg +=
      "#r┌───" + 红枫叶 + "────抽奖积分商城#n────" + 红枫叶 + "────┐\r\n\r\n";
    msg += "   #r⊙本商城上架产品种类多多,物廉价美,欢迎选购！\r\n";
    msg += "   #b⊙当前积分：" + 积分 + "\r\n";
    for (var i = 0; i < xmxsz.length; i++) {
      msg +=
        "#b#L" +
        i +
        "##i" +
        xmxsz[i].获得物品 +
        ":##z" +
        xmxsz[i].获得物品 +
        "# * " +
        xmxsz[i].获得数量 +
        " 需要 " +
        M14 +
        "#r积分 × " +
        xmxsz[i].需要数量 +
        "#l\r\n";
    }

    msg +=
      "\r\n\r\n#r└───" + 红枫叶 + "───────────────" + 红枫叶 + "───┘\r\n\r\n";

    cm.sendSimple("" + msg + "");
  } else if (status == 1) {
    if (
      mi6 != "┃　唯一QQ:526703257    ┃" ||
      mi1 != "┃     - XiaoMiMS -     ┃"
    ) {
      cm.dispose();
      return;
    }
    // if (cm.getChannelServer().getServerName()!="猪猪冒险岛"){cm.dispose();return;}
    sels = selection;

    时间 =
      xmxsz[sels].获得限时 == null || xmxsz[sels].获得限时 == 0
        ? ""
        : "#b[" + xmxsz[sels].获得限时 + " 小时]";
    var msg = "";
    msg +=
      "#i" +
      xmxsz[sels].获得物品 +
      ":##z" +
      xmxsz[sels].获得物品 +
      "#" +
      时间 +
      "\r\n";

    msg += "请输入需要的数量\r\n积分余额：" + 积分 + "";
    cm.sendGetNumber(msg, 1, 1, 1000);
  } else if (status == 2) {
    selssl = selection;

    if (xmxsz[sels].限额 != null && xmxsz[sels].限额 != 0) {
      gmcs = getmeitiana("抽奖积分_购买次数_" + xmxsz[sels].获得物品);
      if (gmcs + selssl >= xmxsz[sels].限额) {
        cm.sendOk(
          "#r输入超过每日购买数量超过：" +
            xmxsz[sels].限额 +
            " \r\n已购数量：" +
            gmcs
        );
        cm.dispose();
        return;
      }
    }

    if (cm.getBossRankCount2("抽奖积分") < xmxsz[sels].需要数量 * selssl) {
      cm.sendOk("#r积分不足无法兑换");
      cm.dispose();
      return;
    }
    if (!cm.canHold(xmxsz[sels].获得物品)) {
      cm.sendOk("#r背包空间不足");
      cm.dispose();
      return;
    }

    cm.sendYesNo(
      "#b是否要兑换#r #i" +
        xmxsz[sels].获得物品 +
        ":##z" +
        xmxsz[sels].获得物品 +
        "# * " +
        selssl +
        " " +
        时间 +
        "\r\n使用积分：" +
        xmxsz[sels].需要数量 * selssl
    );
  } else if (status == 3) {
    // cm.gainNX(-xmxsz[sels].需要数量*selssl);
        cm.setBossRankCount2("抽奖积分",-xmxsz[sels].需要数量 * selssl);
    //gainxmwnjlc("抽奖积分", -xmxsz[sels].需要数量 * selssl);
    if (xmxsz[sels].获得限时 == null || xmxsz[sels].获得限时 == 0) {
      cm.gainItem(xmxsz[sels].获得物品, selssl);
    } else {
      cm.gainItem(xmxsz[sels].获得物品, selssl, xmxsz[sels].获得限时);
    }

    if (xmxsz[sels].限额 != null && xmxsz[sels].限额 != 0) {
      gainmeitiana("抽奖积分_购买次数_" + xmxsz[sels].获得物品, selssl);
    }
    cm.dispose();
  } else {
    cm.sendOk("#r发生错误: mode : " + mode + " status : " + status);
    cm.dispose();
  }
}

function getmeitiana(jiluid) {
  return cm.getPlayer().getxmdailyloga(jiluid);
}

function gainmeitiana(wnjllog, cs) {
  cm.getPlayer().gainxmdailyloga(wnjllog, cs);
}

function getxmwnjlc(jiluid) {
  return cm.getPlayer().getxmwnjlc(jiluid);
}

function setxmwnjlc(wnjllog, cs) {}

function gainxmwnjlc(wnjllog, cs) {
  cm.getPlayer().gainxmwnjlc(wnjllog, cs);
}
