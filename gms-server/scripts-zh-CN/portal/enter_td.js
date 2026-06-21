function enter(pi) {
            if (pi.haveItem(4032263)) {
                pi.warp(674030300, 0);
                pi.gainItem(4032263,-1);
                pi.playerMessage("现在移动至藏宝城..");
            } else {
                pi.playerMessage("请击杀副本盖福克斯获得 盖福克斯的道符 才能进入藏宝城...");
            }
    }