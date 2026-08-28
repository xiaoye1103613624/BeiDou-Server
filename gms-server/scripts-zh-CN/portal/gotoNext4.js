function enter(pi) {

  pi.warp(211060800, 1); // warp to fourth tower
  if (pi.getQuestStatus(3143) == 1) {
    // if quest is completed, open NPC dialog
    pi.openNpc(2161002, 8);
  }
  return true;
}
