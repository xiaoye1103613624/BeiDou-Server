

var date = new Date();
var day = date.getDay();

var status = 0;

	function start() {
		status = -1;
		action(1, 0, 0);
		}
	function action(mode, type, selection) {
	if (mode == -1) {
		cm.sendOk("#b好的,下次再见.");
		cm.dispose();
		} else {
	if (status >= 0 && mode == 0) {
		cm.sendOk("#b好的,下次再见.");
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
 
var 状态1 = "#L0#  【 #b领取任务#k 】#l";
 
} else if (cm.getPlayer().get怪物ID1() > 0 && cm.getPlayer().get怪物数量1() == 0) {
    
var 状态 = "#r任务已完成,可以领取奖励#k";
 
var 状态1 = "#L2#  【 #b领取任务奖励#k 】#l";
 
}else{
  
var 状态 = "消灭#r" + cm.getPlayer().get怪物数量1() + "#k只#r#o" + cm.getPlayer().get怪物ID1() + "##k";

var 状态1 = "#L1#  【 #b任务进行中#k 】#l";
 
}
		cm.sendSimple ("#e欢迎来到多多冒险岛#rBOSS悬赏任务#k,本任务每天可以领取五次.等级150以上哦!#n.\r\n#r说明#k:领取任务后会指定让你消灭指定的野外BOSS,消灭后可以获得#b1#k元宝\r\n#b您当前的任务#k["+状态+"]\r\n\r\n#e"+状态1+"\r\n#L3#  【 #d放弃当前任务#r500W#d金币  】 #l");    

	} else if (status == 1) {

        if (selection == 0) {
if (cm.getLevel() < 100 ) {                   
cm.sendOk("本任务需要100级以上领取.");
    
cm.dispose();
    
 } else {

                    
cm.openNpc(9300001, 1); //接受任务
 

                 
}
    
 } else if (selection == 1) { //查询  正在进行
               
cm.sendOk("忘记要消灭的怪物了吗？你需要消灭的怪物是#b#o" + cm.getPlayer().get怪物ID1() + "##k数量为#" + cm.getPlayer().get怪物数量1() + "#r只。");             
 cm.dispose();
      
 } else if (selection == 2) {//完成任务
               
 cm.openNpc(9300001, 2);//完成任务的NPC
  
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

