	function start() {
        if (cm.getPlayer().getMapId() == 800021115||cm.getPlayer().getMapId() == 100030301||cm.getPlayer().getMapId() == 450004450) {
			cm.playerMessage(5, "[初号机]:当前地图无法使用");
            cm.dispose();
    }else{
            cm.useSkill(5121003,20);//使用技能
            cm.playerMessage(5, "[初号机]:变身成功 开启加速攻击");
		   	cm.dispose();
		}
	}