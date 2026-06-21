var status = -1;

function action(mode, type, selection) {
    var em = cm.getEventManager("Romeo");
    if (em == null) {
        cm.dispose();
        return;
    }
    if (!cm.canHold(4001130, 1)) {
        cm.sendOk("I will need 1 ETC space.");
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getMapId() == 926100000) { //just first stage
                em.setProperty("stage1", "1");
                cm.getMap().setReactorState();     
                cm.gainItem(4001130, 1);
				cm.warpParty(926100001);
			    cm.dispose();
           
	}
    
}