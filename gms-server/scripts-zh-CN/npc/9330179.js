var 美化1 = "★"; //选择道具
var 美化3 = "★"; //选择道具
var 美化2 = "━"; //选择道具
var 美化4 = "★"; //选择道具
var 美化5 = "★"; //选择道具
var 美化6 = "━"; //选择道具
var 美化1 = "★";//选择道具
var 美化3 = "★";//选择道具
var 美化2 = "━";//选择道具
var 美化4 = "★";//选择道具
var 美化5 = "★";//选择道具
var 美化6 = "━";//选择道具
var 红色箭头 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var maxjinbi = 5e4,
  材料 = [[4310100, 1]],
  参数 = {
    "最少人数": 1,
    "最多人数": 3,
    "最低等级": 70,
    "最高等级": 250,
    "次数": 10,
    "入场地图": 105200000
  };
function start() {
  status = -1;
  action(1, 0, 0);
}
function action(b, e, c) {
  if (-1 == b) cm.dispose();
  else if (0 <= status && 0 == b)
    cm.sendOk("感谢你的光临！"), cm.dispose();
  else if ((1 == b ? status++ : status--, 0 == status)) {
    已挑战次数 = cm.getParty每日记录("四大天王", 0);
    var a = "";
	var a =  "#r" + 美化1 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "「 四 大 天 王 」#n" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化2 + "" + 美化3 + "#k#k\r\n\r\n";
    a =
      0 < cm.getMap(105200000).getCharactersSize() &&
      0 < cm.getMap(105200100).getCharactersSize() &&
      0 < cm.getMap(105200110).getCharactersSize() &&
      0 < cm.getMap(105200200).getCharactersSize() &&
      0 < cm.getMap(105200210).getCharactersSize() &&
      0 < cm.getMap(105200310).getCharactersSize() &&
      0 < cm.getMap(105200400).getCharactersSize() &&
      0 < cm.getMap(105200410).getCharactersSize()
        ? a +
          "              #k副本状态：#r当前线路挑战中\r\n"
        : a +
          "              #k副本状态：#g当前线路可挑战\r\n";
    a +=
      "              #k组队人数：#r" +
      参数.最少人数 +
      "#k人 -  #k" +
      参数.最多人数 +
      "#k人\r\n";
    a +=
      "              #k组队等级：#r" +
      参数.最低等级 +
      "#k级 -  #k" +
      参数.最高等级 +
      "#k级\r\n";
    a +=
      "              #k可挑战次数：【#g" +
      (参数.次数 - 已挑战次数) +
      "#k / #r" +
      参数.次数 +
      "#k】\r\n";
    a += "              #k需要材料：";
    for (b = 0; b < 材料.length; b++)
      a += "#v" + 材料[b][0] + "#*" + 材料[b][1];
    cm.sendSimple(a +"\r\n          #L0#" + 红色箭头 + "进入挑战#l  #L1#" + 红色箭头 + "招募队友#l\r\n\r\n\r\n#r" + 美化4 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化6 + "" + 美化5 + "" );
  } else if (0 == c) {
    if (0 < cm.getMap(105200410).getCharactersSize())
      cm.sendOk(
        "  #e#r里面已经有人正在挑战！请稍后！"
      );
    else if (null == cm.getParty())
      cm.sendOk("  #e#r请组队再来找我");
    else if (cm.isLeader()) {
      b = cm.getPlayer().getClient().getChannel();
      c = cm.getParty().getMembers();
      e = cm.getMapId();
      for (c = c.iterator(); c.hasNext(); ) {
        var d = c.next();
        a = d.id;
        a = cm.getChannelServer().getPlayerStorage().getCharacterById(a);
        if (!(d.getLevel() >= 参数.最低等级 && d.getLevel() <= 参数.最高等级)) {
          cm.sendOk(
            "  #e#r队伍中【 " +
              a.getName() +
              " 】的等级不在" +
              参数.最低等级 +
              " 和 " +
              参数.最高等级 +
              "之间."
          );
          cm.dispose();
          return;
        }
        if (d.getMapId() != e) {
          cm.sendOk(
            "  #e#r队伍中有人不在线或者不在同一地图！"
          );
          cm.dispose();
          return;
        }
        if (
          0 < cm.getMap(105200000).getCharactersSize() &&
          0 < cm.getMap(105200100).getCharactersSize() &&
          0 < cm.getMap(105200110).getCharactersSize() &&
          0 < cm.getMap(105200200).getCharactersSize() &&
          0 < cm.getMap(105200210).getCharactersSize() &&
          0 < cm.getMap(105200310).getCharactersSize() &&
          0 < cm.getMap(105200400).getCharactersSize() &&
          0 < cm.getMap(105200410).getCharactersSize()
        ) {
          cm.sendOk(
            "当前地图有人在挑战.请换线尝试进入"
          );
          cm.dispose();
          return;
        }
        if (d.getChannel() != b) {
          cm.sendOk(
            "  #e#r队伍中【 " +
              a.getName() +
              " 】与你不在一个频道!"
          );
          cm.dispose();
          return;
        }
        if (0 == a.haveItem(4310100, 1)) {
          cm.sendOk(
            "  #e#r队伍中【" +
              a.getName() +
              "】的#v4310100# #t4310100#不足1!"
          );
          cm.dispose();
          return;
        }
        if (
          cm.partyMembersInMap() < 参数.最少人数 ||
          cm.partyMembersInMap() > 参数.最多人数
        ) {
          cm.sendOk(
            "  #e#r你的队伍人数不足" +
              参数.最少人数 +
              "人"
          );
          cm.dispose();
          return;
        }
      }
      已挑战次数 >= 参数.次数
        ? cm.sendOk(
            "  #e#r队伍中【 " +
              a.getName() +
              " 】没有挑战次数!"
          )
        : (cm.givePartyItems(4310100, -1),
          //cm.givePartyItems(2591435, -999),
          //cm.givePartyItems(2591451, -999),
          //cm.givePartyItems(2591443, -999),
          //cm.givePartyItems(4031473, -999),
          cm.gainItem(4031473, 1),
          cm.giveParty每日记录("四大天王"),
          cm.warpParty(参数.入场地图, 0));
    } else
      cm.sendOk(
        "  #e#r请让你的队长来找我!"
      );
    cm.dispose();
  } else
    1 == c &&
      (cm.getMeso() >= maxjinbi
        ? (cm.gainMeso(-maxjinbi),
          cm.全服黄色喇叭(
            cm.getPlayer().getName() +
              " [副本征集令] : [四大天王]需要勇士一起完成,我已在副本门口!"
          ))
        : cm.sendOk(
            "你的冒险币不足" +
              maxjinbi +
              "。无法发送征集令"
          ),
      cm.dispose());
}