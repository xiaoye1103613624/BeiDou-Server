var 正在进行中 = "#fUI/UIWindow/Quest/Tab/enabled/1#";
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var 正在进行中蓝 = "#fUI/UIWindow/MonsterCarnival/icon1#";
/*DabaiMS
QQ。3116519632
  技能进阶
*/
var 完成红 = "#fUI/UIWindow/MonsterCarnival/icon0#";
var 美化1 = "#fUI/ChatBalloon.img/pet/120/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/pet/120/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/120/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/pet/120/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/120/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/120/s#";//选择道具
var 美化7 = "#fUI/ChatBalloon.img/156/arrow#";//选择道具
var 怪物卡1 = "#fITEM/Consume/0238.img/02380000/info/iconRaw#";
var 怪物卡2 = "#fITEM/Consume/0238.img/02380001/info/iconRaw#";
var 怪物卡3 = "#fITEM/Consume/0238.img/02380002/info/iconRaw#";
var 任务大白ms3116519632 ="#fUI/UIWindow.img/Quest/summary#";
var DabaiMS1 ="#fUI/GuildMark/Mark/Etc/00009004/1#";
var DabaiMS2 ="#fUI/GuildMark/Mark/Etc/00009004/2#";
var DabaiMS3 ="#fUI/GuildMark/Mark/Etc/00009004/3#";
var DabaiMS4 ="#fUI/GuildMark/Mark/Etc/00009004/4#";
var DabaiMS5 ="#fUI/GuildMark/Mark/Etc/00009004/5#";
var DabaiMS6 ="#fUI/GuildMark/Mark/Etc/00009004/6#";
var DabaiMS7 ="#fUI/GuildMark/Mark/Etc/00009004/7#";
////////////////////////////////材料是什么////////////////////////////////////////////////////////////
var 材料1 = 4001126
var 材料2 = 4032398
var 材料3 = 4000038
var 材料1数量 = 30000
var 材料2数量 = 6666
var 材料3数量 = 666



var 金币所需 =  30000000
var 第1个代码 =2110001
var 第2个代码 =2101004
var 第3个代码 =2121004
var 第4个代码 =2121006
var 第5个代码 =2121006
var 第6个代码 =3121004
var 标题dabaims = "『火导进阶技能』"
var 第1个代码加点 = 31
var 第1个代码所需 = 30

var 第2个代码加点 = 31
var 第2个代码所需 = 30

var 第3个代码加点 = 31
var 第3个代码所需 = 30

var 第4个代码加点 = 31
var 第4个代码所需 = 30

var 第5个代码加点 = 32
var 第5个代码所需 = 31

var 第6个代码加点 = 32
var 第6个代码所需 = 31

//////////////////////////////////////////////////////////////////////////////////////////////////////

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
			        //     cm.teachSkill(第1个代码,第1个代码所需,第1个代码所需);
			text +=""+美化1+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+标题dabaims+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"#k\r\n\r\n"
					text += "需要基础等级到达才可以突破技能LVL,每次突破技能需要材料\r\n"//3
                    if(cm.getPlayer().getSkillLevel(第1个代码)<第1个代码加点){
					text += " #L1# [进阶 LVL"+第1个代码所需+" < - > "+第1个代码加点+"级 #s"+第1个代码+"#]#l\r\n\r\n"//3
}
                    if(cm.getPlayer().getSkillLevel(第2个代码)<第2个代码加点){
					text += " #L2# [进阶 LVL"+第2个代码所需+" < - > "+第2个代码加点+"级 #s"+第2个代码+"#]#l\r\n\r\n"//3
}
                    if(cm.getPlayer().getSkillLevel(第3个代码)<第3个代码加点){
					text += " #L3# [进阶 LVL"+第3个代码所需+" < - > "+第3个代码加点+"级 #s"+第3个代码+"#]#l\r\n\r\n"//3
}

                    if(cm.getPlayer().getSkillLevel(第4个代码)<第4个代码加点){
					text += " #L4# [进阶 LVL"+第4个代码所需+" < - > "+第4个代码加点+"级 #s"+第4个代码+"#]#l\r\n\r\n"//3

}
                    if(cm.getPlayer().getSkillLevel(第5个代码)<第5个代码加点){
					text += " #L5# [进阶 LVL"+第5个代码所需+" < - > "+第5个代码加点+"级 #s"+第5个代码+"#]#l\r\n\r\n"//3
            }else{
             //       cm.dispose();
}
					text += "\r\n "+美化4+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化6+""+美化5+""//3
            cm.sendSimple(text);
        }else if (selection == 1) {
 var 当前 = cm.getPlayer().getJob();
		 if (cm.haveItem(材料1, 材料1数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料2, 材料2数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料3, 材料3数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getMeso() <= 金币所需){
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getPlayer().getSkillLevel(第1个代码) <第1个代码所需) {
                    cm.sendOk("本技能没有到达"+第1个代码所需+"级无法进行突破");
                    status = -1;
		}else if (cm.getPlayer().getOneTimeLogcs(""+第1个代码+"") >1) {
                    cm.sendOk("本技能没有到达 "+第1个代码所需+" 级无法进行突破");
                    status = -1;
		}else{
                     cm.gainItem(材料1,-材料1数量);
                    cm.gainItem(材料2,-材料2数量);
                    cm.gainItem(材料3,-材料3数量);
					cm.setmoneyb(-30)				
                    cm.teachSkill(第1个代码,第1个代码加点,第1个代码加点);
                    cm.getPlayer().setOneTimeLogcs(""+第1个代码+"",1);
                    cm.sendOk("进阶成功，技能已为你突破！");
                    cm.dispose();
			}
          }else if (selection == 2) {
 var 当前 = cm.getPlayer().getJob();
		 if (cm.haveItem(材料1, 材料1数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料2, 材料2数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料3, 材料3数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getMeso() <= 金币所需){
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getPlayer().getSkillLevel(第2个代码) <第2个代码所需) {
                    cm.sendOk("本技能没有到达"+第2个代码所需+"级无法进行突破");
                    status = -1;
	//	}else if (cm.getPlayer().getOneTimeLog(""+第2个代码+"") >0) {
     //               cm.sendOk("本技能没有到达 "+第2个代码所需+" 级无法进行突破");
     //               status = -1;
		}else{
                     cm.gainItem(材料1,-材料1数量);
                    cm.gainItem(材料2,-材料2数量);
                    cm.gainItem(材料3,-材料3数量);
					cm.setmoneyb(-30)				
                    cm.teachSkill(第2个代码,第2个代码加点,第2个代码加点);
                    cm.getPlayer().setOneTimeLog(""+第2个代码+"");
                    cm.sendOk("进阶成功，技能已为你突破！");
                    cm.dispose();
			}
			        }else if (selection == 3) {
 var 当前 = cm.getPlayer().getJob();
		 if (cm.haveItem(材料1, 材料1数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料2, 材料2数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料3, 材料3数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getMeso() <= 金币所需){
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getPlayer().getSkillLevel(第3个代码) <第3个代码所需) {
                    cm.sendOk("本技能没有到达"+第3个代码所需+"级无法进行突破");
                    status = -1;
	//	}else if (cm.getPlayer().getOneTimeLog(""+第2个代码+"") >0) {
     //               cm.sendOk("本技能没有到达 "+第2个代码所需+" 级无法进行突破");
     //               status = -1;
		}else{
                     cm.gainItem(材料1,-材料1数量);
                    cm.gainItem(材料2,-材料2数量);
                    cm.gainItem(材料3,-材料3数量);
					cm.setmoneyb(-30)				
                    cm.teachSkill(第3个代码,第3个代码加点,第3个代码加点);
                    cm.getPlayer().setOneTimeLog(""+第3个代码+"");
                    cm.sendOk("进阶成功，技能已为你突破！");
                    cm.dispose();
			}
			         }else if (selection == 4) {
		 if (cm.haveItem(材料1, 材料1数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料2, 材料2数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料3, 材料3数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getMeso() <= 金币所需){
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getPlayer().getSkillLevel(第4个代码) <第4个代码所需) {
                    cm.sendOk("本技能没有到达"+第4个代码所需+"级无法进行突破");
                    status = -1;
	//	}else if (cm.getPlayer().getOneTimeLog(""+第2个代码+"") >0) {
     //               cm.sendOk("本技能没有到达 "+第2个代码所需+" 级无法进行突破");
     //               status = -1;
		}else{
                     cm.gainItem(材料1,-材料1数量);
                    cm.gainItem(材料2,-材料2数量);
                    cm.gainItem(材料3,-材料3数量);
					cm.setmoneyb(-30)				
                    cm.teachSkill(第4个代码,第4个代码加点,第4个代码加点);
                    cm.getPlayer().setOneTimeLog(""+第4个代码+"");
                    cm.sendOk("进阶成功，技能已为你突破！");
                    cm.dispose();
			}
			         }else if (selection == 5) {
		 if (cm.haveItem(材料1, 材料1数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料2, 材料2数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料3, 材料3数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getMeso() <= 金币所需){
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getPlayer().getSkillLevel(第5个代码) <第5个代码所需) {
                    cm.sendOk("本技能没有到达"+第5个代码所需+"级无法进行突破");
                    status = -1;
	//	}else if (cm.getPlayer().getOneTimeLog(""+第2个代码+"") >0) {
     //               cm.sendOk("本技能没有到达 "+第2个代码所需+" 级无法进行突破");
     //               status = -1;
		}else{
                     cm.gainItem(材料1,-材料1数量);
                    cm.gainItem(材料2,-材料2数量);
                    cm.gainItem(材料3,-材料3数量);
					cm.setmoneyb(-30)				
                    cm.teachSkill(第5个代码,第5个代码加点,第5个代码加点);
                    cm.getPlayer().setOneTimeLog(""+第5个代码+"");
                    cm.sendOk("进阶成功，技能已为你突破！");
                    cm.dispose();
			}

			         }else if (selection == 6) {
 		 if (cm.haveItem(材料1, 材料1数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料2, 材料2数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.haveItem(材料3, 材料3数量) == false) {
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getMeso() <= 金币所需){
                    cm.sendOk(""+任务大白ms3116519632+"\r\n#r材料不符合突破技能条件,[代表自身背包材料总数/任务所需]#k#n\r\n需要1,#i"+材料1+"# [#c"+材料1+"#/#r"+材料1数量+"#k]\r\n需要2,#i"+材料2+"# [#c"+材料2+"#/#r"+材料2数量+"#k]\r\n需要3,#i"+材料3+"# [#c"+材料3+"#/#r"+材料3数量+"#k]\r\n金币4,#i2140000#:["+cm.getMeso()+"/#r"+金币所需+"#k]\r\n\r\n元宝5, #d[ 当前元宝：#r"+cm.getmoneyb()+" / 所需：#r30 元宝#n#d ]\r\n\r\n");
                    status = -1;
		}else if (cm.getPlayer().getSkillLevel(第6个代码) <第6个代码所需) {
                    cm.sendOk("本技能没有到达"+第6个代码所需+"级无法进行突破");
                    status = -1;
	//	}else if (cm.getPlayer().getOneTimeLog(""+第2个代码+"") >0) {
     //               cm.sendOk("本技能没有到达 "+第2个代码所需+" 级无法进行突破");
     //               status = -1;
		}else{
                     cm.gainItem(材料1,-材料1数量);
                    cm.gainItem(材料2,-材料2数量);
                    cm.gainItem(材料3,-材料3数量);
					cm.setmoneyb(-30)				
                    cm.teachSkill(第6个代码,第6个代码加点,第6个代码加点);
                    cm.getPlayer().setOneTimeLog(""+第6个代码+"");
                    cm.sendOk("进阶成功，技能已为你突破！");
                    cm.dispose();
			}

			         }else if (selection == 7) {
 var 当前 = cm.getPlayer().getJob();
 if (cm.getPlayer().getOneTimeLog("进阶次数") > 2) {
                    cm.sendOk("目测你不能领取啊,请对应你需要领取的等级\r\n你的突破等级是:"+cm.getPlayer().getOneTimeLog("进阶次数")+"");
                    cm.dispose();//
			}else{
                    cm.teachSkill(3121004, 32, 32);//
                    cm.sendOk("补领成功");
                    cm.dispose();
			}
			         }else if (selection == 8) {
 var 当前 = cm.getPlayer().getJob();
 if (cm.getPlayer().getOneTimeLog("进阶次数") > 3) {
                    cm.sendOk("目测你不能领取啊,请对应你需要领取的等级\r\n你的突破等级是:"+cm.getPlayer().getOneTimeLog("进阶次数")+"");
			}else{
                    cm.teachSkill(3121004, 33, 33);//
                    cm.sendOk("补领成功");
                    cm.dispose();
			}
				 }else if (selection == 9) {
 var 当前 = cm.getPlayer().getJob();
  if (cm.getPlayer().getOneTimeLog("进阶次数") > 4) {
                    cm.sendOk("目测你不能领取啊,请对应你需要领取的等级\r\n你的突破等级是:"+cm.getPlayer().getOneTimeLog("进阶次数")+"");
			}else{
                    cm.teachSkill(3121004, 34, 34);//
                    cm.sendOk("补领成功");
                    cm.dispose();
			}
				  }else if (selection == 10) {
                    cm.teachSkill(3121004, 35, 35);//
                    cm.sendOk("补领成功");
                    cm.dispose();
			//}
//}
}
}
}


