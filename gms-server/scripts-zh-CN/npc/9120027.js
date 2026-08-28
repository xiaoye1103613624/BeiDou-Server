var mapIdBoss = 800040410;
var mapIdRet = 800040200;

function start() {
  status = -1;
  action(1, 0, 0);
}

function action(mode, type, selection) {
  if (mode == -1) {
    cm.dispose();
  } else {
    if (status >= 0 && mode == 0) {
      cm.sendOk("#b好的,下次再见.");
      cm.dispose();
      return;
    }
    if (mode == 1) {
      status++;
    } else {
      status--;
    }
    var curPlayerMapId = cm.getPlayer().getMapId();
    if (status == 0) {
      if (curPlayerMapId != mapIdBoss) {
        var txt = "#e组队副本:枫城Boss#n\r\n";
        txt += "\r\n\r\n";
        txt += "#L1##b挑战副本#k#l\r\n";
        txt += "\r\n\r\n\r\n\r\n.";
        cm.sendSimple(txt);
      } else {
        var txt = "#e您确定要离开 组队副本:枫城Boss 吗?#n\r\n";
        txt += "\r\n\r\n";
        txt += "#L1##b离开副本#k#l\r\n";
        txt += "\r\n\r\n\r\n\r\n.";
        cm.sendSimple(txt);
      }
    } else if (status == 1) {
      if (selection == 1) {
        if (curPlayerMapId != mapIdBoss) {
          if (cm.getPlayer().getParty() == null) {
            cm.playerMessage(1, "你没有队伍，请先创建队伍或加入一个队伍！");
            cm.dispose();
            return;
          }
          if (!cm.isLeader()) {
            cm.sendOk("如果你想尝试，请告诉 #b组队队长#k 跟我说话。");
            cm.dispose();
            return;
          }
          var em = cm.getEventManager("Ninja_Boss");
          if (em == null) {
            cm.sendOk("副本没有启用！");
            cm.dispose();
            return;
          }
          var prop = em.getProperty("state");
          if (prop == null || prop.equals("0")) {
            cm.清除地图物品(mapIdBoss);
            cm.清怪();
            em.startInstance(cm.getParty(), cm.getMap());
          } else {
            cm.sendOk("已经有队伍在里面挑战了。");
            cm.dispose();
            return;
          }
        } else {
          cm.清除地图物品(mapIdBoss);
          cm.清怪();
          cm.warp(mapIdRet, 0);
          cm.dispose();
        }
      }
      cm.dispose();
    }
  }
}
