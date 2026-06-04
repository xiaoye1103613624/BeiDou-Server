/* ==================
 脚本类型: NPC	    
 脚本作者：一线海-维多   
 联系方式：297870163
 =====================
 */
var 邪恶小兔 = "#fEffect/CharacterEff/1112960/3/0#";  //邪恶小兔 【小】

function start() {
    status = -1;

    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
			var tex2 = "";
            var text = "";
			text += "  #k#e欢迎来到梦之岛---#r强化突破系统（合计12次）\r\n"
			text += "#L2##d" + 邪恶小兔 + "强化突破（可突破2次）#l    \r\n\r\n"//#L1##d" + 邪恶小兔 + "二次突破#l
			
			text += "#L1##d" + 邪恶小兔 + "二次突破（方式一）#l      \r\n\r\n"// #L17##d" + 邪恶小兔 + "下等五彩水晶合成#l\r\n\r\n"
			
			text += "#L3##d" + 邪恶小兔 + "二次突破（方式二）#l \r\n\r\n"//#L7##d" + 邪恶小兔 + "提升伤害上限#l 
		    text += "#L1##d" + 邪恶小兔 + "装备进阶转移#l       \r\n\r\n"
			text += "#L4##d" + 邪恶小兔 + "强化突破#l  #L3##d" + 邪恶小兔 + "龙魂升星#l   #L5##d" + 邪恶小兔 + "时装升星#l\r\n\r\n"  
            text += "#L7##d" + 邪恶小兔 + "伤害上限#l   \r\n\r\n"//   \r\n\r\n"//
			//
			
			 cm.sendOk(text); 
        } else if (selection == 1) {
			cm.dispose();
            cm.openNpc(9900004, 987);
        } else if (selection == 2) {
			cm.dispose();
            //cm.openNpc(9900004, "强化系统_各类鉴定"); 
            cm.openNpc(9900004, 618); 			
		} else if (selection == 3) {
			cm.dispose();
            cm.openNpc(9900004, 985);
		} else if (selection == 4) {
			cm.dispose();
            cm.openNpc(9900004, 618);
		} else if (selection == 5) {
			cm.dispose();
            cm.openNpc(9900004, 620);
			} else if (selection == 6) {
            cm.openNpc(9900004, "强化系统_装备觉醒");
			cm.dispose();
			} else if (selection == 7) {
		    //cm.sendOk("装备次数提升系统维护中，后续开放！");
			//cm.dispose();
			cm.dispose();			
            cm.openNpc(9900004, 630);
			} else if (selection == 8) {
			cm.dispose();	
            cm.openNpc(9900004, 640);
			} else if (selection == 9) {
			cm.dispose();				
            cm.openNpc(9900004,650);
			} else if (selection == 10) {
		    cm.dispose();
            cm.openNpc(9900004, 508);
			} else if (selection == 11) {
		    cm.dispose();
		    cm.openNpc(9900004, 605);//永恒系统
		    } else if (selection == 12) {
		    cm.dispose();
		    cm.openNpc(9900004,918);//梦之岛指环
			} else if (selection == 13) {
		    cm.openNpc(9900004, "点装强化");
			cm.dispose();
		    } else if (selection == 14) {
			cm.dispose();
            //cm.openNpc(9900004, "强化系统_各类鉴定"); 
            cm.openNpc(9900004, 681); 			
		}  else if (selection == 15) {
			cm.dispose();	
            cm.openNpc(9100003,0);	
            } else if (selection == 16) {
			cm.dispose();	
		    cm.warp(922241000, 0);
        }else if(selection == 20){
			cm.dispose();	
            cm.openNpc(9900004, 606);	
        }else if(selection == 50){
			cm.dispose();	
            cm.openNpc(9330030, 0);	
			}else if(selection == 99){
			cm.dispose();	
            cm.openNpc(9900004, 917);	
			
		}else if (selection == 17) {
			cm.dispose();	
		    cm.openNpc(9900004, "下等五彩水晶合成");
        }
    }
}