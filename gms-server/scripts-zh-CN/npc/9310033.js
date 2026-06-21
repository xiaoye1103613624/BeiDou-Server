function start() {
status = -1;

action(1, 0, 0);
}
function action(mode, type, selection) {
            if (mode == -1) {
                cm.dispose();
            }
            else {
                if (status >= 0 && mode == 0) {
                
   cm.sendOk("感谢你的光临！");
   cm.dispose();
   return;                    
                }
                if (mode == 1) {
   status++;
  }
  else {
   status--;
  }
          if (status == 0) {
  cm.sendOk("#b梦回怀旧冒险岛QQ群交流：961405598#k");
    } else if (status == 1) {
           if (selection == 0) {
      cm.sendOk("#e各种兑换材料可以通过打怪打BOSS获得，努力吧少年！如有问题可在#rQQ群交流：961405598#k");
            cm.dispose();
    }else if  (selection == 1) {
           cm.openNpc(9050001, 0);
		       cm.dispose();
    }else if  (selection == 4) {
           cm.openNpc(9330065, 0);
		       cm.dispose();
    }else if  (selection == 6) {
           cm.openNpc(9050006, 0);
		       cm.dispose(); 
    }else if  (selection == 2) {
           cm.openNpc(9310069, 0);
		       cm.dispose(); 
    }else if  (selection == 5) {
           cm.openNpc(9250028, 0);
		       cm.dispose();
    }else if  (selection == 3) {
           cm.openNpc(9250016, 0);
		       cm.dispose();
    }else if  (selection == 7) {
           cm.openNpc(9270051, 0);
		       cm.dispose(); 
    }else if  (selection == 8) {
           cm.openNpc(9270048, 0);
		       cm.dispose();
    }else if  (selection == 9) {
           cm.openNpc(9270049, 0);
		       cm.dispose();
    }else if  (selection == 10) {
           cm.openNpc(2010005, 0);
                       cm.dispose();
    }else if  (selection == 11) {
           cm.openShop(112);
    }else if  (selection == 12) {
           cm.openNpc(2071000, 0);
		   }else if  (selection == 13) {
           cm.openNpc(9310093, 0);
                       cm.dispose();
      

}
}
}
}

