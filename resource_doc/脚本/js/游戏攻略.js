var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/15#";
var 红色指向 ="#fUI/UIWindow/Quest/icon0#";
var 幸运草 ="#fUI/GuildMark/Mark/Plant/00003006/15#";
var 香水 ="#fUI/GuildMark/Mark/Pattern/00004008/15#";
var M7 = "#fEffect/CharacterEff/1051296/1/0#";//蓝圆星
var 大箭头 ="#fUI/Basic/BtHide3/mouseOver/0#";
var 银杏叶 ="#fMap/MapHelper/weather/maple/3#";
var 草莓4 = "#fUI/GuildMark/Mark/Plant/00003000/3#"; // 黄色草莓
var 草莓5 = "#fUI/GuildMark/Mark/Plant/00003000/8#"; // 绿色草莓 
var 彩虹1 ="#fUI/ChatBalloon/122/n#";
var 彩虹上1 =  "#fUI/ChatBalloon/122/ne#";
var 彩虹上2 =  "#fUI/ChatBalloon/122/nw#";
var 彩1 =    "#fUI/ChatBalloon/122/e#";
var 彩2 =    "#fUI/ChatBalloon/122/w#";
var 塔罗牌1 = "#fUI/PredictHarmony/card/10#";
var 塔罗牌2 = "#fUI/PredictHarmony/card/10#";  
var 彩虹下 ="#fUI/ChatBalloon/122/s#";
var 彩虹下1 ="#fUI/ChatBalloon/122/se#";
var 彩虹下2 ="#fUI/ChatBalloon/122/sw#";
var 彩虹中 ="#fUI/ChatBalloon/122/head#";
var 红色指向 ="#fUI/UIWindow/Quest/icon0#";
var 师徒UI = "#fEffect/CharacterEff1/1112567/0/0#";
var 师徒B =4310070;
var 师徒戒指=1113047;
var 分割线7 = "┅━━━━━━━━━━━━━━━━━━━━━━━━━┅\r\n"
var 新手指南 = "#fEffect/CharacterEff2.img/QQ1408745/0/9#";
var 提示 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
	
	 if(mode<0){//外挂修改数据判断 
          cm.dispose();
		  return;
      }		
		
	   if(selection<-1){//外挂修改数据判断
		 c.getPlayer().ban("修改封包",true,true,false);//封号处理
        }			
	
    if (status == 0 && mode == 0) {//菜单跳转 
	
        cm.dispose();
		cm.openNpc(9900004, "快捷导航"); 
        return;
    }
	
	   if(selection<-1){//外挂修改数据判断
		 c.getPlayer().ban("修改封包",true,true,false);//封号处理
        }	
		
	   if(selection<0){//外挂修改数据判断
        selection= Math.abs(selection);
        }		
		
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	
	
    if(status == 0){
	  
		   var text = "";

			
            text = "\t\t\t"+新手指南+"";
			text += "\r\n"
			text += ""+分割线7+""
			text +="    #k冒险家:[#r"+cm.getChar().getName()+"#k],欢迎来到#r"+cm.getChannelServer().getServerName()+",#r绿色微变.经"			
			text +="#r典再现#k,市面最具#r稳定服务器#k、高度可玩性、无论你是#r肝帝#k、还是#r佛性玩家#k、#r老板#k、同步时装、发型、脸型,均可有一套自己的玩法.#k\r\n"
			text += ""+分割线7+""
			//text +="本服装备排序：\r\nT5 < T4 < T3 < T2 < T1 < T0 < 神器 \r\n史诗神器 < 上古法器 < 传承灵器 < 神话至宝\r\n"
			text +="本服装备排序： T5 < T4 < T3 < T2 < T1 < T0 < 神器 \r\n"
			text += ""+分割线7+""
			text +="#r所有材料物品由BOSS产出包括野外BOSS或完成特定任务获得等."
			text += "#k"+分割线7+""			
			text +="#b#L0#"+提示+"  1、萌新攻略#r（萌新必看）#l\r\n"
			text +="#b#L1#"+提示+"  2、职业推荐#l\r\n" 
			text +="#b#L2#"+提示+"  3、每日福利、新手礼盒#l\r\n"
			text +="#b#L3#"+提示+"  4、师徒系统介绍#l\r\n"
			text +="#b#L4#"+提示+"  5、野外爆出材料说明#l\r\n"
			text +="#b#L5#"+提示+"  6、免费获得抵用#l\r\n"
			text +="#b#L6#"+提示+"  7、免费获得点券#l\r\n"		
			text +="#b#L7#"+提示+"  8、免费获得元宝与累计充值#l\r\n"
			text +="#b#L8#"+提示+"  9、任务戒指获取攻略#l\r\n"
			text +="#b#L11#"+提示+" 10、宝石戒指出处#l\r\n\r\n"	
		//	text +="#b#L9#"+提示+" 11、角色洗血#l\r\n"
		//	text +="#b#L10#"+提示+" 12、特色五转技改介绍#l\r\n\r\n"	
			text += "#k"+分割线7+""
			text +="\t\t\t  #b#L99#"+提示+"《更新日志》#r(有更新)#l\r\n\r\n"
			text += "#k"+分割线7+""	

			 cm.sendOkS(text,2);
			 

			 
	}else if (status==1){
		var text ="";
		if(selection==0){
			 
			 text +="\t\t\t\t\t  #e#r萌新攻略#n#k\r\n"
			 text += ""+分割线7+""
			 text +="#b特别说明：\r\n本服微变修仙玩法,装备打造与升级速度适中,绝对长久耐玩！\r\n\r\n"
			 text +="#r1、#d新人进服，点击#r拍卖 - 新手礼包#d领取新人礼包.\r\n"
			 text +="#r2、#d到#r30级#d领取等级奖励有送精灵吊坠（#r经验+30%#d），点击#r拍卖 - 新手礼包 - 等级奖励 #d可领取.\r\n"
			 text +="#r3、#d购买双倍经验、双倍爆率卡，点击#r拍卖 - 每日需要#d购买.\r\n"
			 text +="#r4、#d去市场摆摊（#r经验+50%#d）.\r\n"
			 text +="#r5、#d去匠人街下面最右边了解#r新手装备制作#d.\r\n"
			 text +="#r6、#d前期职业随便选，180级与250级等级奖励有送#r#z4310086##d可在自由市场最右边更换职业.#k\r\n"
			 text +="#r7、#r【升级】#d拍卖-快捷传送-副本传送-金字塔.进入门票在快捷商店购买.#k\r\n"
			 text += ""+分割线7+""
			 text +="\t\t\t\t\t  #e#b进阶攻略#n#k\r\n"
			 text += ""+分割线7+""
			 text +="#r1、#d匠人街了解戒指制作详情查看首页第9项[任务戒指]攻略\r\n"
			 text +="#r2、#d匠人街了解#r轮回碑石#d套装收集强化、#r时装鉴定#d、#r装备鉴定#d\r\n"
			 text +="#r3、#d匠人街左下角#r装备吞噬#d,每件装备可以吞噬10次,大幅度提升属性的关键\r\n"
			 text +="#r4、#d点击拍卖-快捷传送-副本传送-#r怪物公园#d制作佩饰，前期可以先制作T5级别佩饰,也会有很大属性提升#k\r\n"
			 text += ""+分割线7+""
			 text +="#r   关于盾牌特别说明：本服T4盾牌以上级别全职业可佩戴#k\r\n"
			 text += ""+分割线7+""
			 text +="#r     其他功能对话框输入[ #b@帮助#r ]可查看游戏指令#k\r\n"
			 text += ""+分割线7+""
			 cm.sendOkS(text,2)		
			 status=-1;
		}else if(selection==1){
			 text += ""+分割线7+""
			 text +="\t\t\t\t\t#e#r职业推荐#n#k\r\n"
			 text += ""+分割线7+""
			 text +="①、#k#e冰雷#n#k:适合#b新手入门#k,#b刷材料赚金币#k首选职业.\r\n"
			 text +="②、#k#e主教#n#k:#b前期#k过度较难,#b中期#k可带人升级#b赚取金币#k,#b后期#k团队不可或缺的#b辅助职业#k.\r\n"
			 text +="③、#k#e火毒#n#k:#b后期#k法系职业#b单体输出最高职业#k,#b需要投资#k才能体现价值.\r\n"
			 text +="④、#k#e侠盗#n#k:#b前期#k个别副本有优势,#b后期#k技能帅气,#b生存能力强.#k\r\n"
			 text +="⑤、#k#e隐士#n#k:#b机动性高#k、#b输出高#k、#b职业体验高#k、#b需要投资#k才能体现价值.\r\n"
			 text +="⑥、#k#e圣骑士#n#k:#b单体伤害爆表#k,#bBUFF技能多#k,唯一缺点手短.#k\r\n"
			 text +="⑦、#k#e龙骑士#n#k:适合#b新手入门#b,#b前中期伤害足#k,缺点后期生存能力较弱.#k\r\n"
			 text +="⑧、#k#e英雄#n#k:#b万精油职业#k,#b体验感好#k,轻舞飞扬技能帅气,可微氪职业。#k\r\n"
			 text +="⑨、#k#e神射手#n#k:#b远程高输出职业#k,需要投资才能体现价值,缺点输出环境差.#k\r\n"
			 text +="⑩、#k#e箭神#n#k:#b0氪、微氪推荐输出职业#k,3转后翻身,输出可观.#k\r\n"
			 text +="⑾、#k#e冲锋队长#n#k:#b平民可做辅助#k,#b投资可做输出#k,变身帅气无比,后期BOOS不可或缺的支柱.#k\r\n"
			 text +="⑿、#k#e船长#n#k:#b远程高输出职业#k,#b需要投资#k才能体现价值,技能趣味性高.#k\r\n"
			 text += ""+分割线7+""
			 text += "#e#r总结#n:#e#d肝帝|土豪#k#n玩家可选需要投资职业,#d#e微氪#n#k玩家可选体验高,输出可观职业,#d#e佛系玩家#n#k可选辅助职业.\r\n"
			 text += "#r【老G强烈推荐职业】:#d【隐士、侠盗、黑骑、英雄、船长】\r\n（选择自己喜欢的职业就行了，180级与250级#r等级奖励#d可以领取共两个#r#z4310086##d，可以去自由市场免费职业更换）.\r\n"
			 text += ""+分割线7+""
 			
			 cm.sendOkS(text,2)		
			 status=-1;			
		}else if(selection==2){
			 text += ""+分割线7+""
			 text +="\t\t\t\t#e#r一、每日福利,新手礼盒#n#k\r\n"
			 text += ""+分割线7+""
			 text +="免费福利打开游戏#b右下角拍卖快捷菜单#k选择[#b新手礼包#k]\r\n"
		//	 text +="#e1、#n免费领取#b#z5041000##k10个.\r\n"
			 text +="#e1、#n免费领取#b#z5030008##k3个.\r\n"
			 text +="#e2、#n免费领取#b#z5030000##k3个.\r\n"
			 text +="#e3、#n免费领取#b#z5150038##k3个.\r\n"
			 text +="#e4、#n免费领取#b#z5151001##k3个.\r\n"
			 text +="#e5、#n免费领取#b#z5153000##k3个.\r\n"
		//	 text +="#e7、#n免费领取#b#z5041000##k10个(交流使用).\r\n"
			 text +="#e6、#n新手礼包 - 药水礼包赠送各种前期过度所需药瓶，再也不需要担心前期没钱买药了。\r\n"
			 text +="#e7、#n#k建议萌新#r购买#z5010019##k(提供自动售卖装备、物品落脚下、自动存金币特权)#k，每日还可领取元宝20个(每月可领取600个元宝)，一亿金币等等大量材料#k\r\n"
             text +="#e8、#n#k自由市场 - 任务系统 - 白嫖签到免费领取累计积分，每月#r可领300元累计赞助#k.\r\n"
			 text +="#e9、#n#k自由市场 - 任务系统 - 每日跳跳免费领取#b#z2022509##k5个与#b#z2614000##k5个.\r\n"
			 text +="#e10、#n#k每天12：00与18：00饭点福利在线即可领取#b#z2022509##k5个与#b#z2614000##k1个.\r\n"
			 text += ""+分割线7+""
			 text +="\t\t  #e#r二、领取每日福利后开始冒险之路#n#k\r\n"
			 text += ""+分割线7+""
			 text +="#e①、#n最重要的是在游戏里面找一位师傅,可以用喇叭或者群里问有没有收徒的,可以让他给你一些过度的装备跟金币,#b出师后师徒双方都会有点卷奖励,#k"
			 text +="如果没有人的话,可以打开#b#e拍卖--查询爆物#n#k查看对应#b装备或材料#k出处,为后面过度做准备."
			 cm.sendOkS(text,2)		
			 status=-1;
		}else if(selection==3){
			 text += "\r\n"+分割线7+""
			 text +="\t\t\t\t\t#e#r师徒系统#n#k\r\n"
			 text += ""+分割线7+""			 
			 
			 text += "    "+师徒UI+"\r\n"
			 text +="\t\t\t  #r师徒系统，传承与成长的桥梁#k\r\n\r\n"
             text += "  "+红色指向+" 角色到达10级可以拜师\r\n";
             text += "  "+红色指向+" 拜师对象必须到达#r筑基#k以上(#r输入拜师码即可#n)\r\n";
             text += "  "+红色指向+" #k由徒弟发起拜师#k(#r师傅自动接受#k)\r\n";
             text += "  "+红色指向+" #k徒弟出师#r师傅#k可获得大量#b#z2049104:i#.#b元宝.累计\r\n";
             text += "  "+红色指向+" #k徒弟在#r筑基#k以前可以解除师徒关系\r\n";
             text += "  "+红色指向+" #b徒弟充值师傅可获得20%返利元宝(#r需找客服领取)#k\r\n";
			 text += "  "+红色指向+" #b拜师前一定要找个负责的师傅#k\r\n";			 
			 cm.sendOkS(text,2)		
			 status=-1;			
		}else if(selection==4){	
			text += "\r\n"+分割线7+""
			text +="\t\t\t\t  #e#r野外BOSS爆出材料说明#n#k\r\n"
			text += ""+分割线7+""
			text +="#v4310143:#  #v2711003:#  #v4321012:#  #v4321010:#  #v2340000:#  #v2049100:#  #v2049122:#  #v2022699:#  #v2614006:#  #v2022692:#  #v2022693:#  #v2022694:#  #v2022695:#  #v2022696:#\r\n"
			text +="#r各种散装矿石#k:可在匠人街矿石制作大师制作#r#z4021009#、#z4011007#\r\n"
			text += "\r\n"+分割线7+""
			text +="\t\t\t\t    #e#r传统BOSS爆物说明#n#k\r\n"
			text += ""+分割线7+""
			text +="妖僧：宝石套、敏捷之心\r\n"
			text +="闹钟：传说套、T5套、自由之心\r\n"
			text +="熊狮：传说套、T5套、自由之心、精准之心\r\n"
			text +="扎昆：传说套、风暴套、T5套、智慧之心\r\n"
			text +="黑龙：风暴套、终极、T5套、五颗星全爆\r\n"
			text +=" PKB：终极套、T5套、五颗星全爆\r\n"
			text +="外星人套在 维利塔斯获得 （拍卖-快捷传送-次元传送-维利塔斯）\r\n"
			 cm.sendOkS(text,2)		
			 status=-1;			
		}else if(selection==5){
			 text += ""+分割线7+""
			 text +="\t\t\t#e #r    免费获得点券抵用券方式#n#k\r\n"	
			  text += ""+分割线7+""
			 
			 text += "#r#e1#n、#d新人上线自动赠送3W点券.3W抵用.#k\r\n"
			 text += "#r#e2#n、#d拍卖-新手礼包可领取2W抵用.#k\r\n"
			 text += "#r#e3#n、#d等级奖励总共可以领取3.3W抵用.#k\r\n"
			 text += "#r#e4#n、#d自由市场相框#v4000313:#兑换.比例 1：1#k\r\n"
			 text += "#r#e5#n、#d每日BOSS副本必得#v2022701:#奖励可以抽取#v2022309:#兑换抵用.\r\n"
			 text += "#r#e6#n、#d每天12：00与18：00饭点福利在线即可领取10000抵用券.\r\n"
			 text += "#r#e7#n、#d离线挂机:#b1分钟可得1抵用券.每天可以离线挂机300分钟.#k\r\n"
			 text += "#r#e8#n、#d怀旧任务:#b每个任务完成后都会奖励大量抵用券.#k\r\n"
			 text += ""+分割线7+""
			 text +="#e#k重点：#r做匠人街副本任务，完成所有副本奖励超多点券抵用兑换卡#n#k\r\n"	
			 text += ""+分割线7+""	
			
			 cm.sendOkS(text,2)		
			 status=-1;			
		}else if(selection==6){
			 text += ""+分割线7+""
			 text +="\t\t\t#e  #r    免费获得点券方式#n#k\r\n"	
			 text += ""+分割线7+""			 
			 text += "#r#e1#n、#d新人上线自动赠送3W点券.#k\r\n"
			 text += "#r#e2#n、#d每日BOSS副本必得#v2022701:#奖励可以抽取#v2022309:#兑换点券.\r\n"
			 text += "#r#e3#n、#d自由市场相框#v4000313:#兑换.比例 2：1#k\r\n"
			 text += "#r#e4#n、#d月卡福利:#b每日领取10000点券奖励.#k\r\n"
			 text += "#r#e5#n、#d每天12：00与18：00饭点福利在线即可领取10000点券.\r\n"
			 text += "#r#e6#n、#d离线挂机:#b1分钟可得1点券.每天可以离线挂机300分钟.#k\r\n"
			 text += "#r#e7#n、#d怀旧任务:#b每个任务完成后都会奖励大量点券.#k\r\n"
			 text += "#r#e8#n、#d怪怪卡片:#b每个任务完成后都会奖励大量点券.#k\r\n"
			 text += ""+分割线7+""
			 text +="#e#k重点：#r做匠人街副本任务，完成所有副本奖励超多点券抵用兑换卡#n#k\r\n"	
			 text += ""+分割线7+""	
			
			 cm.sendOkS(text,2)		
			 status=-1;		
		}else if(selection==7){	
			 text += ""+分割线7+""
			 text +="\t\t\t#e  #r  1、免费获得元宝方式#n#k\r\n"	
			 text += ""+分割线7+""			 
			 text += "#r#e 1#n、#d每天晚上20：00刷新世界福利BOSS，必须爆大量元宝与\r\n    材料，注意屏幕提示.#k\r\n"
			 text += "#r#e 2#n、#d每个整点开始10分钟内可在市场任务系统领取任务，每\r\n    次完成任务获得1元宝奖励.\r\n"
		//	 text += "#r#e 3#n、#d拍卖 - 怀旧任务，任务完成后很大几率获得#r大量元宝#d与暗影币等各种材料.\r\n"
			 text += "#r#e 3#n、#d拍卖 - 怪怪卡片，任务完成后获得#r大量元宝#d.\r\n"
			 text += "#r#e 4#n、#d月卡每日领取20元宝#k\r\n"
			 text += "#r#e 5#n、#d跳跳：市场任务系统每日跳跳.通关可获得5元宝奖励#k\r\n"
			 text += "#r#e 6#n、#d每天12：00与18：00饭点福利在线即可领取#b#z2022509##d5个\r\n    与#b#z2614000##d1个.\r\n"
		//	 text += "#r#e 7#n、#d每周各职业战斗力前三名可领取大量元宝奖励，详情市场相框查看.\r\n"
			 text += "#r#e 7#n、#d击杀BOSS，获得BOSS宝箱也可开出大量元宝.\r\n"
			 text += "#r#e 8#n、#d拍卖 - 每日需要 - 每日任务，完成后获得大量元宝.\r\n"
			 text += "#r#e 9#n、#d每天晚上22：00，每日任务 - 节奏大师可获得35元宝.\r\n"
			 text += "#r#e 9#n、#dVIP挂机房，产出大量元宝.\r\n"
			 text += "#r#e10#n、#d还会有更多获取元宝途径.等待添加.\r\n"
			 text += ""+分割线7+""
			 text +="\t\t\t#e  #r  2、免费获得累计积分方式#n#k\r\n"	
			 text += ""+分割线7+""	
			 text += "#r#e1#n、#d签到：自由市场白嫖签到每7天、14天、21天、30天都可获\r\n   得累计赞助，每月可获得#r300累计.#k\r\n"
			 text += "#r#e2#n、#d拍卖 - 快捷传送 - 副本传送 - 通天塔  [每日登顶送30\r\n   累计充值，每月可得#r900累计].\r\n"
			 text += "#r#e3#n、#d收徒：每个徒弟出师都可以获得#r20累计，无上限.\r\n"
			 text += "#r#e4#n、#d每个徒弟首次充值师傅返利20%，联系客服发放.\r\n"
			 		
			 cm.sendOkS(text,2)		
			 status=-1;		
		}else if(selection==8){	
			  text += ""+分割线7+""	
			 text +="\t   #e#r    溢出经验强化毕戒指(各种戒指获取)#n#k\r\n"	
			  text += ""+分割线7+""
			 text += "#r\t\t\t  溢出经验可制作毕业戒指#k.\r\n"
			// text += "#d毕业戒指:#v1112495##t1112495##b拍卖#k--#b任务装---#k经验戒指#k.\r\n"
			// text += "#k当等级达到世界#b封印等级#k后,可将#r溢出经验#k用来#b制作#k毕业戒指.\r\n"
			 text += "#b#z1113129#：#k通过拍卖 - 成长戒指 点击即可自动获得。\r\n"
			 text += "#b#z1113130#：#k通过拍卖 - 成长戒指 点击即可自动获得。\r\n"
			 text += "#b#z1113131#：#k通过拍百宝箱产出、匠人街饰品大师兑换。\r\n"	
			 text += "#r特别说明：\r\n以上进化指环如果追求极品属性，一定要强化后再砸卷#k\r\n"	
			 text += ""+分割线7+""	
			 text += "#b#z1112907#：#k通过自由市场钓鱼NPC钓鱼后升级小鱼戒指。\r\n" 
			 text += "#b#z1114304#：#k新人礼包上线获得，匠人街中间强化。\r\n"
			 text += "#b#z1113231#：#k通过百宝箱抽奖或击杀BOSS合成。\r\n"
			 text += "#r说明：#k任务戒指都会带来巨大的属性攻击提升,任务装基本属于必做的任务。\r\n"
			  text += ""+分割线7+""		
			 cm.sendOkS(text,2)		
			 status=-1;		
		}else if(selection==9){
			 cm.openNpc(9031000);
			 		
		}else if(selection==10){
				text += "  #b英雄技能   #s11111006#  <剑影分身>数量8，段数8，伤害500% #l\r\n";
				
				text += "#k———————————————————————————\r\n";
			   
				text += "  #k圣骑技能   #s15111007#  <威力神锤>数量8，段数8，伤害500% #l\r\n";		//被动武器单手双手 #s11121065#   #s11121058#
				text += "#k———————————————————————————\r\n";
				
				text += "  #b黑骑技能   #s14001002#  <枪舞旋风>数量8，段数8，伤害500%#l \r\n";  //被动武器枪 #s11121072#   #s11121056#
				text += "#k———————————————————————————\r\n";
				//法师
				text += "  #k火毒技能   #s12101006#  <魔力漩涡>数量8，段数8，伤害500%#l\r\n";  //更新  #s12111004#
				text += "#k———————————————————————————\r\n";

				text += "  #b冰雷技能   #s12001003#  <黑暗灵气>数量8，段数8，伤害500%#l\r\n";  //  #s12101002#
				text += "#k———————————————————————————\r\n";
				
				text += "  #k牧师技能   #s12111006#  <星座法阵>数量8，段数8，伤害500% #l\r\n";   //#s12101004#   #s12111003#
				text += "#k———————————————————————————\r\n";
				
				text += "  #b神射技能   #s13111002#  <释魂射击>数量1，段数8，伤害500%#l\r\n";  		//#s11121061#   #s13101005#			
				text += "#k———————————————————————————\r\n";
			   
				text += "  #k箭神技能   #s13111001#  <箭 扫 射>数量1，段数8，伤害500%#l\r\n";   //#s11121060#   #s13111000#
				text += "#k———————————————————————————\r\n";
			
				text += "  #b隐士技能   #s14111002#  <速射>数量5，段数6，伤害500%  #l\r\n"; 
				text += "#k———————————————————————————\r\n";
			   
				text += "  #k侠客技能   #s11001003#  <利刃风暴>数量8，段数8，伤害500%#l\r\n"; 
				text += "#k———————————————————————————\r\n";				
				//
				text += "  #b船长技能   #s15001001#  <子弹盛宴>数量8，段数8，伤害500%#l\r\n";  			//#s15001001#   #s13001004#		
				text += "#k———————————————————————————\r\n";
			   
				text += "  #k队长技能   #s15111003#  <元气弹>数量10，段数8，伤害500% #l\r\n";  //#s15111007#   #s15101003#
				text += "#k———————————————————————————\r\n";					
				//				
				text += "  #b战神技能   #s11001002#  <虎鹤双击>数量10，段数8，伤害300%#l\r\n";  //#s11121054#   #s11121059#
				text += "#k———————————————————————————\r\n";				 
		 			
			 cm.sendOkS(text,2)		
			 status=-1;	

		}else if(selection==11){	
			text += ""+分割线7+""	
			text +="\t\t\t   #e#r宝石戒指出处说明#n#k\r\n"	
			text += ""+分割线7+""
			text += "#rC级宝石戒指：所有野外BOSS.\r\n"
			text += "#rB级宝石戒指：蜈蚣、妖僧、树妖.\r\n"
			text += "#rA级宝石戒指：闹钟、扎昆、黑龙、PKB.\r\n"
			text += "#rS级宝石戒指：钻机、战舰、贝尔.或者通过百宝箱抽奖\r\n"
			text += "#b本服所有道具都可以通过  拍卖 - 查询爆物  查询.#k\r\n"
			text += ""+分割线7+""		
			 cm.sendOkS(text,2)		
			status=-1;		
			 
		}else if(selection==99){
			 text += ""+分割线7+""	
			 text +="\t\t\t#e   #r       更新日志#n#k\r\n"
			 text += ""+分割线7+""
		//	 text +="#b2025年4月30日：\r\n#k1、维护更新登录器.\r\n2、开放远征副本700W、800W战力地图.\r\n3、人物最大移动速度提高至160.\r\n4、开启二连跳不间断模式（在有二段跳情况下按住跳跃键不放，可持续二段跳）\r\n"
			 text +="遥遥领先、不便说明。\r\n"
		 			
			 cm.sendOkS(text,2)		
			 status=-1;	
		
		}else if(selection==15){	
			 text +="遥遥领先、不便说明。\r\n"
			 cm.sendOkS(text,2)		
			 status=-1;	
		}else if(selection==16){
			 text +="遥遥领先、不便说明。\r\n"
			 cm.sendOkS(text,2)		
			 status=-1;	
		}else if(selection==17){
			 text +="遥遥领先、不便说明。\r\n"
			 cm.sendOkS(text,2)		
			 status=-1;	
		}else if(selection==18){
			 text +="遥遥领先、不便说明。\r\n"	
			 cm.sendOkS(text,2)		
			 status=-1;	
		}else if(selection==19){
			 text +="遥遥领先、不便说明。\r\n"
			 cm.sendOkS(text,2)		
			 status=-1;		 

		}		
    }

   
}











