var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 爱心 = "#fEffect/CharacterEff/1032063/0/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
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
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            //text += "\t\t\t  #e#天启冒险岛副本专区 #k#n\r\n"
            text += ""+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+"\r\n"
			
            //text += ""+爱心+爱心+爱心+爱心+爱心+爱心+爱心+"\r\n"
			//text += "#L99##b" + 蓝色箭头 + "嘉年华组队副本 (30-150)  \r\n"
            text += "#L1##b" + 红色箭头 + "邵和村-金融中心\r\n"
            text += "#L2##b" + 蓝色箭头 + "枫叶古城\r\n"
            text += "#L3##b" + 红色箭头 + "中国-台湾\r\n"//3

            text += "#L4##b" + 蓝色箭头 + "可乐村\r\n"
            text += "#L7##b" + 红色箭头 + "黄金寺院\r\n"

            text += "#L5##b" + 蓝色箭头 + "妖精学院\r\n"
			text += "#L6##b" + 红色箭头 + "2022新叶城\r\n"
			text += "#L8##b" + 蓝色箭头 + "列纳海峡\r\n"

			text += "#L38##b" + 蓝色箭头 + "天空克利塞\r\n"//3 
			text += "#L9##b" + 红色箭头 + "金海滩\r\n"//3 
			text += "#L10##b" + 蓝色箭头 + "狮子王城\r\n"//3 
			text += "#L11##b" + 红色箭头 + "消亡旅途\r\n"//3 
			text += "#L12##b" + 蓝色箭头 + "月桥\r\n"//3
			text += "#L13##b" + 蓝色箭头 + "黎曼\r\n"//3
			text += "#L14##b" + 红色箭头 + "台湾不夜城\r\n"
			text += "#L15##b" + 红色箭头 + "台湾101\r\n"

         
            text += ""+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+"\r\n"  
            //text += "#L27##b"+红色箭头+ "二十七宫#l \r\n"//3 
			//text += "#L8#"+ttt+""+xxx+ "遗址公会对抗战(家族副本)#l\r\n"//3
            //text += "#L28##b" + 红色箭头 + "演练副本#l\r\n\r\n"//3
			//text += "#L1##b" + 红色箭头 + "月妙组队副本(10-200)#l #L9##b" + 红色箭头 + "英语学院副本\r\n"//3	  
	  
	  //  text += " #L11#"+ttt+""+xxx+"远征闹钟(100级)#l#b#L12#"+ttt+""+xxx+"远征扎昆(120级)#l#b\r\n";
            
           // text += " #L23#"+ttt+""+xxx+"大王蜈蚣(70级)#l#b#L22#"+ttt+""+xxx+"巨大蝙蝠(90)#l#b\r\n";
	    //text += " #L13#"+ttt+""+xxx+"远征大树(130级)#l#b#L14#"+ttt+""+xxx+"远征妖僧(140级)#l#b\r\n";

            //text += " #L15#"+ttt+""+xxx+"绯红(120级)#l#b#L16#"+ttt+""+xxx+"鱼王(120级)#l#b\r\n\r\n";

            //text += "#L11##dLv120.千年树精王遗迹Ⅱ#l\r\n\r\n"//3
            //text += "#L12##dLv130.人偶师BOSS挑战#l\r\n\r\n"//3
            //text += "" + 蓝色箭头 + "#L13##rLv120级以上.绯红副本挑战#l\r\n\r\n"//3
            //text += "" + 蓝色箭头 + "#L14##rLv140级以上.御姐副本挑战#l\r\n\r\n"//3
          // text += "" + 蓝色箭头 + "#L60##rLv160级以上.挑战高级boss#l\r\n\r\n"//3
            //text += ""+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+星星+"\r\n"
            cm.sendSimple(text);
		} else if (selection == 99) { //嘉年华组队副本 //100000020
            cm.warp(980000000);
            cm.dispose();
        } else if (selection == 1) { //月妙组队副本 //100000020
            cm.warp(801000000);
            cm.dispose();
        } else if (selection == 2) {  //废弃组队副本
            cm.warp(800040000);
            cm.dispose();
        } else if (selection == 3) { //玩具组队副本
            cm.warp(740000000);
            cm.dispose();
        } else if (selection == 4) {//天空组队副本
            cm.warp(219000000);
            cm.dispose();
        } else if (selection == 5) { 
               if (cm.getQuestStatus(32156) == 2) { // 2 表示任务已完成
            cm.warp(101071300); // 完成任务时传送到101071300地图
        } else if (cm.getQuestStatus(32156) == 1) { // 1 表示任务进行中
            cm.warp(101070000,101070100); // 进行中时传送到101070000地图
        } else {
             cm.sendOk("你还未接取相关任务，无法传送。"); //未完成任务时不能传送
        }
            cm.dispose();
        } else if (selection == 6) {//海盗组队副本
            cm.warp(703000000);
            cm.dispose();
        } else if (selection == 7) {//罗密欧与朱丽叶组队副本
	    cm.warp(252000000);
            cm.dispose();
        } else if (selection == 8) {//遗址公会对抗战
	    cm.warp(141000000);
            cm.dispose();
        } else if (selection == 9) {//英语学院副本
            cm.warp(120040000);
            cm.dispose();
         } else if (selection == 21) {//怪物公园
            cm.warp(951000000);
            cm.dispose();
        } else if (selection == 28) {//演练副本
            cm.warp(130020000);
            cm.dispose();
       } else if (selection == 29) {//金字塔副本
            cm.warp(926010000);
            cm.dispose();
       } else if (selection == 23) {//大王蜈蚣
            cm.warp(701010321);
            cm.dispose();
            
        } else if (selection == 22) {//巨大蝙蝠
            cm.warp(105100100);
            cm.dispose();

        } else if (selection == 11) {//闹钟
            cm.warp(450001000);
            cm.dispose();
            //cm.openNpc(9310057, 0);
        } else if (selection == 12) {//扎
            cm.warp(450009000);
            cm.dispose();
			        } else if (selection == 13) {//扎
            cm.warp(450012000);
            cm.dispose();
						        } else if (selection == 14) {//扎
            cm.warp(741000000);
            cm.dispose();
            //cm.openNpc(9310057, 0);
        } else if (selection == 17773) {//大树
            if (cm.getLevel() < 180 && cm.party.size() < 2) {  
            cm.sendOk("本地图限制等级180级。您的能力没有资格挑战副本");
                cm.dispose();
              }else{
			cm.warp(541020800);  
				cm.dispose();
                return;
	      } 
        } else if (selection == 145555) {//妖僧
            if (cm.getLevel() < 140 ) {  
            cm.sendOk("本地图限制等级140级。您的能力没有资格挑战副本");
                cm.dispose();
              }else{
			cm.warp(702070400);  
                cm.dispose();
                return;
	      }
        } else if (selection == 15) {//绯红
            if (cm.getLevel() < 10 ) {  
            cm.sendOk("本地图限制等级120级。您的能力没有资格挑战副本");
                cm.dispose();
              }else{
			cm.warp(742000000  );  
                cm.dispose();
                return;
	      } 
        } else if (selection == 16) {//鱼王
            if (cm.getLevel() < 120 ) {  
            cm.sendOk("本地图限制等级120级。您的能力没有资格挑战副本");
                cm.dispose();
              }else{
			cm.warp(230040420);  
                cm.dispose();
                return;
	      }
        } else if (selection == 10) {//.怪物嘉年华
            cm.warp(211060000);
            cm.dispose();
            //cm.openNpc(9310057, 0);
          } else if (selection == 60) {//.怪物嘉年华
            cm.warp(970030001);
            cm.dispose();
            //cm.openNpc(9310057, 0);
        } else if (selection == 15) {//.阿里安特
            cm.openNpc(2101018, 0); 
        } else if (selection == 27) {//.二十七宫
            cm.warp(970030000);
            cm.showInstruction("#r[二十七宫材料说明]#k\r\n\r\n", 240, 60);
            cm.dispose();
       } else if (selection == 38) {//.武陵道场
            cm.warp(200100000);
            cm.showInstruction("#r[武陵道场材料说明]#k获取腰带\r\n\r\n", 240, 60);
            cm.dispose();
           
        } else if (selection == 31) {//.废弃扫荡
           if (cm.haveItem(4031890) > 0){
           cm.gainItem(4001322,10);
           cm.gainItem(4002000,1);//绿蜗牛邮票
           cm.gainExp(50000);
           cm.gainItem(4031890,-1);
            cm.dispose();
           }
         else{
              cm.sendOk("你没有扫荡卡，不能扫荡副本");
              cm.dispose();
             }
        }
      else if (selection == 32) {//.玩具扫荡
           if (cm.haveItem(4031890) > 0){
           cm.gainItem(4001322,10);
           cm.gainItem(4002000,1);//绿蜗牛邮票
             cm.gainExp(50000);
           cm.gainItem(4031890,-1);
            cm.dispose();
           }
         else{
              cm.sendOk("你没有扫荡卡，不能扫荡副本");
              cm.dispose();
             }
        }
else if (selection == 33) {//.天空扫荡
           if (cm.haveItem(4031890) > 0){
           cm.gainItem(4001322,10);
           cm.gainItem(4002000,1);//绿蜗牛邮票
             cm.gainExp(50000);
           cm.gainItem(4031890,-1);
            cm.dispose();
           }
         else{
              cm.sendOk("你没有扫荡卡，不能扫荡副本");
              cm.dispose();
             }
        }
else if (selection == 34) {//.男女扫荡
           if (cm.haveItem(4031890) > 0){
           cm.gainItem(4001322,10);
           cm.gainItem(4002000,1);//绿蜗牛邮票
             cm.gainExp(50000);
           cm.gainItem(4031890,-1);
            cm.dispose();
           }
         else{
              cm.sendOk("你没有扫荡卡，不能扫荡副本");
              cm.dispose();
             }
        }
else if (selection == 35) {//.毒物扫荡
           if (cm.haveItem(4031890) > 0){
           cm.gainItem(4001322,10);
           cm.gainItem(4002000,1);//绿蜗牛邮票
             cm.gainExp(100000);
           cm.gainItem(4031890,-1);
            cm.dispose();
           }
         else{
              cm.sendOk("你没有扫荡卡，不能扫荡副本");
              cm.dispose();
             }
        }
else if (selection == 36) {//.海盗扫荡
           if (cm.haveItem(4031890) > 0){
           cm.gainItem(4001322,10);
           cm.gainItem(4002000,1);//绿蜗牛邮票
            cm.gainExp(100000);
           cm.gainItem(4031890,-1);
            cm.dispose();
           }
         else{
              cm.sendOk("你没有扫荡卡，不能扫荡副本");
              cm.dispose();
             }
        }
    }
}


