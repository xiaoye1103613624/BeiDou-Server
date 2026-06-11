var date = new Date();
var day = date.getDay();
var status = 0;
	function start() {
		status = -1;
		action(1, 0, 0);
		}
	function action(mode, type, selection) {
	if (mode == -1) {
		//cm.sendOk("#b好的,下次再见.");
		cm.dispose();
		} else {
	if (status >= 0 && mode == 0) {
		//cm.sendOk("#b好的,下次再见.");
		cm.dispose();
		return;
		}
	if (mode == 1)
		status++;
		else
		status--;
	if (status == 0) {
if (cm.getPlayer().get怪物ID1() == 0 && cm.getPlayer().get怪物数量1() == 0) {
var 状态 = "#r尚未领取任务#k";
var 状态1 = "#L0#【#b领取任务#k】#l";
} else if (cm.getPlayer().get怪物ID1() > 0 &&cm.haveItem(cm.getPlayer().get怪物ID1(), cm.getPlayer().get怪物数量1())) {//cm.getPlayer().get怪物数量1( 
var 状态 = "#r任务已完成,可以领取奖励#k";
var 状态1 = "#L2#【#b恭喜你完成了任务点击领取奖励#k】#l";
}else{
var 状态 = "#r#i" + cm.getPlayer().get怪物ID1() + "# #r" + cm.getPlayer().get怪物数量1() + "#k个#k";
var 状态1 = "#L1#【#b当前背包#i"+cm.getPlayer().get怪物ID1()+"# #c"+cm.getPlayer().get怪物ID1()+"#个,未满足提交】#l";
}
		cm.sendSimple ("#r- 每周家族建设任务#k\r\n1.本任务每周可以领取5次.需等级70以上!#n.\r\n2.领取的任务随机等级范围内材料999之间\r\n3.完成获得#r枫叶300张#k ,出席勋章*2个 家族贡献/家族积分/贡献积分+5\r\n本周已完成:"+cm.getBossRankCount3("每周任务")+" 次\r\n#b您当前的任务#k["+状态+"]\r\n\r\n"+状态1+"\r\n\r\n");    
	} else if (status == 1) {
        if (selection == 0) {
if (cm.getLevel() < 70 ) {                   
cm.sendOk("本家族建设任务需要70级以上领取.");
cm.dispose();
 } else {            
cm.openNpc(9900004,1234522111); //接受任务            
}   
 } else if (selection == 1) { //查询  正在进行
               
cm.sendOk("你需要#b#i" + cm.getPlayer().get怪物ID1() + "##k数量为#r " + cm.getPlayer().get怪物数量1() + "#r个.");             
 cm.dispose();
      
 } else if (selection == 2) {//完成任务
               
 cm.openNpc(9900004,1234522122);//完成任务的NPC
  
} else if (selection == 3) {//放弃任务
                 
if (cm.getPlayer().get怪物ID1() > 0 && cm.getPlayer().get怪物数量1() > 0) {
                    
 if(cm.getPlayer().getBeans()>= 50){          
cm.sendOk("放弃成功！消耗500W金币。");
                        
cm.gainMeso(-5000000);                    
cm.getPlayer().取消怪物ID1();                    
cm.getPlayer().取消怪物数量1();                        
 cm.dispose();
                    
}else{
                         
cm.sendOk("对不起，你的金币不足。");
                         
cm.dispose();
                     }
               
  }else{
                     
cm.sendOk("你没有接受任务。无法放弃！");
                   
  cm.dispose();
               
  }
    
                } else if(selection == 54) {
                 cm.openNpc(9900001); 


               
               
                }					
		}
		}
		}

