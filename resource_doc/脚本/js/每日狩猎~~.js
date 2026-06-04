

var date = new Date();
var day = date.getDay();
var 签到币 = 4000487
var 黄金枫叶 = 4000313
var 绿水灵橡皮檫 = 4001013
var 超级药水 =2000005



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
if (cm.getPlayer().get怪物ID() == 0 && cm.getPlayer().get怪物数量() == 0) {
 
var 状态 = "#r尚未领取任务#k";
 
var 状态1 = "#L0#【#b领取任务#k】#l";
 
} else if (cm.getPlayer().get怪物ID() > 0 &&cm.haveItem(cm.getPlayer().get怪物ID(), cm.getPlayer().get怪物数量())) {//cm.getPlayer().get怪物数量(
    
var 状态 = "#r任务已完成,可以领取奖励#k";
 
var 状态1 = "#L2#【#b恭喜你完成了任务点击领取奖励#k】#l";
 
}else{
  
var 状态 = "#r#i" + cm.getPlayer().get怪物ID() + "# #r" + cm.getPlayer().get怪物数量() + "#k个#k";

var 状态1 = "#L1#【#b当前背包#i"+cm.getPlayer().get怪物ID()+"# #c"+cm.getPlayer().get怪物ID()+"#个,未满足提交】#l";
 
}//1.#z"+签到币+"# *1\r\n2.#z"+黄金枫叶+"# *8-16\r\n3.#z"+绿水灵橡皮檫+"# *3-6\r\n4.#z"+超级药水+"# *3-6\r\n5.等级 * 1000金币\r\n6.等级 * 8000点经验
		cm.sendSimple ("\t\t\t\t#r#e每日狩猎#k#n\r\n\r\n本任务每天可以领取1次.领取随机狩猎收集材料50~200个#l\r\n\r\n 任务奖励为:\r\n1.#z"+签到币+"# *1\r\n2.#z"+黄金枫叶+"# *8-16\r\n3.#z"+绿水灵橡皮檫+"# *3-6\r\n4.#z"+超级药水+"# *3-12\r\n5.等级 * 1000金币\r\n6.等级 * 8000点经验\r\n\r\n#b您当前的任务#k["+状态+"]#l\r\n\r\n"+状态1+"#l      #L3#重置任务(500w)#l\r\n");   

	} else if (status == 1) {

        if (selection == 0) {
if (cm.getLevel() < 30 ) {                   
cm.sendOk("本任务需要30级以上领取.");
    
cm.dispose();
    
 } else {
 cm.openNpc(9900004,"狩猎任务");//完成任务的NPC
                    
cm.openNpc(9900004,"狩猎奖励"); //接受任务
 

                 
}
    
 } else if (selection == 1) { //查询  正在进行
               
cm.sendOk("你需要#b#i" + cm.getPlayer().get怪物ID() + "##k数量为#r " + cm.getPlayer().get怪物数量() + "#r个.");             
 cm.dispose();
       } else if (selection == 66) {//完成任务
               
 cm.openNpc(9900004,180835);//完成任务的NPC
 } else if (selection == 2) {//完成任务
               
 cm.openNpc(9900004,"狩猎奖励");//完成任务的NPC
  
} else if (selection == 3) {//放弃任务
                 
if (cm.getPlayer().get怪物ID() > 0 && cm.getPlayer().get怪物数量() > 0) {
                    
 if(cm.getPlayer().getBeans()>= 50){          
cm.sendOk("放弃成功！消耗500W金币。");
                        
cm.gainMeso(-5000000);                    
cm.getPlayer().取消怪物ID();                    
cm.getPlayer().取消怪物数量();                        
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

