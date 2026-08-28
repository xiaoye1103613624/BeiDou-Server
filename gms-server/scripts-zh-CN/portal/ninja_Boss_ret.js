function enter(pi) {
  // var mapRet = 800040200;
  // if (pi.getPlayer().getParty() != null) {
  //   if (!pi.isLeader()) {
  //     pi.playerMessage(5, "请让队长离开哦!");
  //     return false;
  //   }
  //   pi.warpParty(mapRet, 0);
  //   pi.playPortalSE();
  //   return true;
  // } else {
  //   pi.warp(mapRet, 0);
  //   pi.playPortalSE();
  //   return true;
  // }
  pi.playerMessage(5, "请通过NPC离开！!");
  return false;
}
