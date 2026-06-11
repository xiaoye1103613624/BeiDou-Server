//koshi.3.14更


// 这里写装备代码 ,必须带身上 然后下面描述自己改 判断的是如果不是GM 才会触发

function start() {
	if (cm.getPlayer().hasEquipped(1143066)) {  
        cm.喇叭(3,"["+cm.getName()+"]：VIP1玩家上线了！每一次上线都是新的开始！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：VIP1玩家上线了！每一次上线都是新的开始！", 5121007);
		cm.dispose();
	} else if (cm.getPlayer().hasEquipped(1143067)) {
		cm.喇叭(3,"["+cm.getName()+"]：VIP2玩家荣耀登场！尊贵的您，游戏世界因您而精彩！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：VIP2玩家荣耀登场！尊贵的您，游戏世界因您而精彩！", 5121007);
		cm.dispose();
	} else if (cm.getPlayer().hasEquipped(1143068)) {
		cm.喇叭(3,"["+cm.getName()+"]：VIP3玩家闪耀登场！星光闪耀，欢迎您的回归！");
		cm.喇叭(3,"["+cm.getName()+"]：VIP3玩家闪耀登场！星光闪耀，欢迎您的回归！");
		cm.全服漂浮喇叭("["+cm.getName()+"]：VIP3玩家闪耀登场！星光闪耀，欢迎您的回归！", 5121007);
		cm.dispose();	
	} else if (cm.getPlayer().hasEquipped(1143069)) {
		cm.喇叭(3,"["+cm.getName()+"]：VIP4玩家华丽登场！每一次上线都充满魅力！");
		cm.喇叭(3,"["+cm.getName()+"]：VIP4玩家华丽登场！每一次上线都充满魅力！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：VIP4玩家华丽登场！每一次上线都充满魅力！", 5121006);//落神
		cm.dispose();
	} else if (cm.getPlayer().hasEquipped(1143070)) {
		cm.喇叭(1,"["+cm.getName()+"]：VIP5玩家威风登场！您的每一步都备受瞩目！");
		cm.喇叭(1,"["+cm.getName()+"]：VIP5玩家威风登场！您的每一步都备受瞩目！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：VIP5玩家威风登场！您的每一步都备受瞩目！", 5121008);//曾经的射手
		cm.dispose();
	} else if (cm.getPlayer().hasEquipped(1143071)) {
		cm.喇叭(1,"["+cm.getName()+"]：VIP6玩家霸气登场！尊贵身份，注定不凡！");
		cm.喇叭(1,"["+cm.getName()+"]：VIP6玩家霸气登场！尊贵身份，注定不凡！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：VIP6玩家霸气登场！尊贵身份，注定不凡！", 5121007);//云天霸霸
		cm.dispose();
	} else if (cm.getPlayer().hasEquipped(1143072)) {
		cm.喇叭(1,"["+cm.getName()+"]：尊贵的VIP7玩家威震八方！祝您在游戏中所向披靡！");
		cm.喇叭(1,"["+cm.getName()+"]：尊贵的VIP7玩家威震八方！祝您在游戏中所向披靡！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：尊贵的VIP7玩家威震八方！祝您在游戏中所向披靡！", 5121007);//云天霸霸
		cm.dispose();
	} else if (cm.getPlayer().hasEquipped(1143073)) {
		cm.喇叭(1,"["+cm.getName()+"]：尊贵的VIP8玩家霸气归来！全体玩家热烈欢迎！");
		cm.喇叭(1,"["+cm.getName()+"]：尊贵的VIP8玩家霸气归来！全体玩家热烈欢迎！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：尊贵的VIP8玩家霸气归来！全体玩家热烈欢迎！", 5121007);//王富贵
		cm.dispose();
	} else if (cm.getPlayer().hasEquipped(1143074)) {
		cm.喇叭(1,"["+cm.getName()+"]：【土豪】赞助大佬上线！全体起立！敬礼！");
		cm.喇叭(1,"["+cm.getName()+"]：【土豪】赞助大佬上线！全体起立！敬礼！");
		cm.喇叭(1,"["+cm.getName()+"]：【土豪】赞助大佬上线！全体起立！敬礼！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：【神豪】赞助大佬上线！全体起立！敬礼！", 5121007);
		cm.dispose();
	} else if (cm.getPlayer().hasEquipped(1143075)) {
		cm.喇叭(1,"["+cm.getName()+"]：【神豪】赞助大佬驾到！全体起立！敬礼！");
		cm.喇叭(1,"["+cm.getName()+"]：【神豪】赞助大佬驾到！全体起立！敬礼！");
		cm.喇叭(1,"["+cm.getName()+"]：【神豪】赞助大佬驾到！全体起立！敬礼！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：【神豪】赞助大佬驾到！全体起立！敬礼！", 5121006);
		cm.dispose();
	} else if (cm.getPlayer().hasEquipped(1143076)) {
		cm.喇叭(1,"["+cm.getName()+"]：【至尊王者神豪】赞助大佬驾到！全体起立！敬礼！");
		cm.喇叭(1,"["+cm.getName()+"]：【至尊王者神豪】赞助大佬驾到！全体起立！敬礼！");
		cm.喇叭(1,"["+cm.getName()+"]：【至尊王者神豪】赞助大佬驾到！全体起立！敬礼！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：【至尊王者神豪】赞助大佬驾到！全体起立！敬礼！", 5121005);
		cm.dispose();
	} else if (cm.getPlayer().hasEquipped(1142371)) {
		cm.喇叭(1,"["+cm.getName()+"]：闪亮登场，闪瞎众人！");
		cm.喇叭(1,"["+cm.getName()+"]：闪亮登场，闪瞎众人！");
		cm.喇叭(1,"["+cm.getName()+"]：闪亮登场，闪瞎众人！");
	    cm.全服漂浮喇叭("["+cm.getName()+"]：顺我者昌，                 逆我者亡！", 5121006);
		cm.dispose();	
	}else{	
	cm.dispose();
	}
}