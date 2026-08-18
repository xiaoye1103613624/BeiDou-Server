var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 套装1= [1005802,1052593,1082620,1072658];
var 套装2= [1005967,1053922,1082551,1072998];
var 套装3= [1006066,1053563,1082169,1073328];
var 套装4= [1004095,1052709,1082750,1072911];
var 套装5= [1004202,1052774,1082591,1072945];
var 套装6= [1005862,1053143,1082700,1073117];
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
			text += "#r                   #b时装套装属性激活\r\n"
			text += "#r                #k激活需要消耗#r2000万#k金币\r\n\r\n"
			text += "#L1##v"+套装1[0]+"##v"+套装1[1]+"##v"+套装1[2]+"##v"+套装1[3]+"#\r\n#L1##b[套装1] #k激活套装属性：每件时装#r10点#k全属性#l\r\n\r\n";
			text += "#L2##v"+套装2[0]+"##v"+套装2[1]+"##v"+套装2[2]+"##v"+套装2[3]+"#\r\n#L2##b[套装2] #k激活套装属性：每件时装#r10点#k全属性#l\r\n\r\n";
			text += "#L3##v"+套装3[0]+"##v"+套装3[1]+"##v"+套装3[2]+"##v"+套装3[3]+"#\r\n#L3##b[套装3] #k激活套装属性：每件时装#r10点#k全属性#l\r\n\r\n";
			text += "#L4##v"+套装4[0]+"##v"+套装4[1]+"##v"+套装4[2]+"##v"+套装4[3]+"#\r\n#L4##b[套装4] #k激活套装属性：每件时装#r10点#k全属性#l\r\n\r\n";
			text += "#L5##v"+套装5[0]+"##v"+套装5[1]+"##v"+套装5[2]+"##v"+套装5[3]+"#\r\n#L5##b[套装5] #k激活套装属性：每件时装#r10点#k全属性#l\r\n\r\n";
			text += "#L6##v"+套装6[0]+"##v"+套装6[1]+"##v"+套装6[2]+"##v"+套装6[3]+"#\r\n#L6##b[套装6] #k激活套装属性：每件时装#r10点#k全属性#l\r\n\r\n";
			//text += "#L7##v"+套装7[0]+"##v"+套装7[1]+"##v"+套装7[2]+"##v"+套装7[3]+"#\r\n#L7##k激活套装属性：每件时装#r10点#k全属性#l\r\n\r\n";
            cm.sendSimple(text);
        } 
		else if (selection == 1) 
		{ //套装1
			if( cm.haveItem(套装1[0],1) &&cm.haveItem(套装1[1],1) && cm.haveItem(套装1[2],1)&& cm.haveItem(套装1[3],1)&& cm.getMeso()>=20000000) 
			{
				cm.gainItem(套装1[0],-1);
				cm.gainItem(套装1[1],-1);
				cm.gainItem(套装1[2],-1);
				cm.gainItem(套装1[3],-1);
				cm.gainItem(套装1[0],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装1[1],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装1[2],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装1[3],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainMeso(-20000000);
				cm.sendOk("套装属性激活成功!");
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"时装套装" + " : 玩家" + cm.getPlayer().getName() +"激活了一套时装套装的隐藏属性,每件时装获得了10点全属性",true));
				cm.dispose();
		   }
		   else
		   {
				cm.sendOk("#r你的时装没有凑齐,或是金币不够!\r\n\r\n#k- #k#z"+套装1[0]+"# #b[#r#c"+套装1[0]+"##b/1]\r\n- #k#z"+套装1[1]+"# #b[#r#c"+套装1[1]+"##b/1]\r\n- #k#z"+套装1[2]+"# #b[#r#c"+套装1[2]+"##b/1]\r\n- #k#z"+套装1[3]+"# #b[#r#c"+套装1[3]+"##b/1]\r\n#k- 金币 #b[#r"+cm.判断金币()+"#b/20000000]");
				cm.dispose();
		   }
        }
		else if (selection == 2) 
		{ //套装2
			if( cm.haveItem(套装2[0],1) &&cm.haveItem(套装2[1],1) && cm.haveItem(套装2[2],1)&& cm.haveItem(套装2[3],1)&& cm.getMeso()>=20000000) 
			{
				cm.gainItem(套装2[0],-1);
				cm.gainItem(套装2[1],-1);
				cm.gainItem(套装2[2],-1);
				cm.gainItem(套装2[3],-1);
				cm.gainItem(套装2[0],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装2[1],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装2[2],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装2[3],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainMeso(-20000000);
				cm.sendOk("套装属性激活成功!");
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"时装套装" + " : 玩家" + cm.getPlayer().getName() +"激活了一套时装套装的隐藏属性,每件时装获得了10点全属性",true));
				cm.dispose();
		   }
		   else
		   {
				cm.sendOk("#r你的时装没有凑齐,或是金币不够!\r\n\r\n#k- #k#z"+套装2[0]+"# #b[#r#c"+套装2[0]+"##b/1]\r\n- #k#z"+套装2[1]+"# #b[#r#c"+套装2[1]+"##b/1]\r\n- #k#z"+套装2[2]+"# #b[#r#c"+套装2[2]+"##b/1]\r\n- #k#z"+套装2[3]+"# #b[#r#c"+套装2[3]+"##b/1]\r\n#k- 金币 #b[#r"+cm.判断金币()+"#b/20000000]");
				cm.dispose();
		   }
        }
		else if (selection == 3) 
		{ //套装3
			if( cm.haveItem(套装3[0],1) &&cm.haveItem(套装3[1],1) && cm.haveItem(套装3[2],1)&& cm.haveItem(套装3[3],1)&& cm.getMeso()>=20000000) 
			{
				cm.gainItem(套装3[0],-1);
				cm.gainItem(套装3[1],-1);
				cm.gainItem(套装3[2],-1);
				cm.gainItem(套装3[3],-1);
				cm.gainItem(套装3[0],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装3[1],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装3[2],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装3[3],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainMeso(-20000000);
				cm.sendOk("套装属性激活成功!");
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"时装套装" + " : 玩家" + cm.getPlayer().getName() +"激活了一套时装套装的隐藏属性,每件时装获得了10点全属性",true));
				cm.dispose();
		   }
		   else
		   {
				cm.sendOk("#r你的时装没有凑齐,或是金币不够!\r\n\r\n#k- #k#z"+套装3[0]+"# #b[#r#c"+套装3[0]+"##b/1]\r\n- #k#z"+套装3[1]+"# #b[#r#c"+套装3[1]+"##b/1]\r\n- #k#z"+套装3[2]+"# #b[#r#c"+套装3[2]+"##b/1]\r\n- #k#z"+套装3[3]+"# #b[#r#c"+套装3[3]+"##b/1]\r\n#k- 金币 #b[#r"+cm.判断金币()+"#b/20000000]");
				cm.dispose();
		   }
        }
		else if (selection == 4) 
		{ //套装4
			if( cm.haveItem(套装4[0],1) &&cm.haveItem(套装4[1],1) && cm.haveItem(套装4[2],1)&& cm.haveItem(套装4[3],1)&& cm.getMeso()>=20000000) 
			{
				cm.gainItem(套装4[0],-1);
				cm.gainItem(套装4[1],-1);
				cm.gainItem(套装4[2],-1);
				cm.gainItem(套装4[3],-1);
				cm.gainItem(套装4[0],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装4[1],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装4[2],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装4[3],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainMeso(-20000000);
				cm.sendOk("套装属性激活成功!");
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"时装套装" + " : 玩家" + cm.getPlayer().getName() +"激活了一套时装套装的隐藏属性,每件时装获得了10点全属性",true));
				cm.dispose();
		   }
		   else
		   {
				cm.sendOk("#r你的时装没有凑齐,或是金币不够!\r\n\r\n#k- #k#z"+套装4[0]+"# #b[#r#c"+套装4[0]+"##b/1]\r\n- #k#z"+套装4[1]+"# #b[#r#c"+套装4[1]+"##b/1]\r\n- #k#z"+套装4[2]+"# #b[#r#c"+套装4[2]+"##b/1]\r\n- #k#z"+套装4[3]+"# #b[#r#c"+套装4[3]+"##b/1]\r\n#k- 金币 #b[#r"+cm.判断金币()+"#b/20000000]");
				cm.dispose();
		   }
        }
		else if (selection == 5) 
		{ //套装5
			if( cm.haveItem(套装5[0],1) &&cm.haveItem(套装5[1],1) && cm.haveItem(套装5[2],1)&& cm.haveItem(套装5[3],1)&& cm.getMeso()>=20000000) 
			{
				cm.gainItem(套装5[0],-1);
				cm.gainItem(套装5[1],-1);
				cm.gainItem(套装5[2],-1);
				cm.gainItem(套装5[3],-1);
				cm.gainItem(套装5[0],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装5[1],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装5[2],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装5[3],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainMeso(-20000000);
				cm.sendOk("套装属性激活成功!");
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"时装套装" + " : 玩家" + cm.getPlayer().getName() +"激活了一套时装套装的隐藏属性,每件时装获得了10点全属性",true));
				cm.dispose();
		   }
		   else
		   {
				cm.sendOk("#r你的时装没有凑齐,或是金币不够!\r\n\r\n#k- #k#z"+套装5[0]+"# #b[#r#c"+套装5[0]+"##b/1]\r\n- #k#z"+套装5[1]+"# #b[#r#c"+套装5[1]+"##b/1]\r\n- #k#z"+套装5[2]+"# #b[#r#c"+套装5[2]+"##b/1]\r\n- #k#z"+套装5[3]+"# #b[#r#c"+套装5[3]+"##b/1]\r\n#k- 金币 #b[#r"+cm.判断金币()+"#b/20000000]");
				cm.dispose();
		   }
        }
		else if (selection == 6) 
		{ //套装6
			if( cm.haveItem(套装6[0],1) &&cm.haveItem(套装6[1],1) && cm.haveItem(套装6[2],1)&& cm.haveItem(套装6[3],1)&& cm.getMeso()>=20000000) 
			{
				cm.gainItem(套装6[0],-1);
				cm.gainItem(套装6[1],-1);
				cm.gainItem(套装6[2],-1);
				cm.gainItem(套装6[3],-1);
				cm.gainItem(套装6[0],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装6[1],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装6[2],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainItem(套装6[3],10,10,10,10,10,10,10,10,0,0,0,0,0,0,0,"激活套装");
				cm.gainMeso(-20000000);
				Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(9,cm.getClient().getChannel(),"时装套装" + " : 玩家" + cm.getPlayer().getName() +"激活了一套时装套装的隐藏属性,每件时装获得了10点全属性",true));
				cm.sendOk("套装属性激活成功!");
				cm.dispose();
		   }
		   else
		   {
				cm.sendOk("#r你的时装没有凑齐,或是金币不够!\r\n\r\n#k- #k#z"+套装6[0]+"# #b[#r#c"+套装6[0]+"##b/1]\r\n- #k#z"+套装6[1]+"# #b[#r#c"+套装6[1]+"##b/1]\r\n- #k#z"+套装6[2]+"# #b[#r#c"+套装6[2]+"##b/1]\r\n- #k#z"+套装6[3]+"# #b[#r#c"+套装6[3]+"##b/1]\r\n#k- 金币 #b[#r"+cm.判断金币()+"#b/20000000]");
				cm.dispose();
		   }
        }
    }
}


