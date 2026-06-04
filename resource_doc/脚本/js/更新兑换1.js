var status = -1;
var zzks = 0;
var tosend = 0;
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
var 星星 ="#fMap/MapHelper/weather/witch/3#";
var 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";  //
var 兑换中心 = "#fEffect/CharacterEff1.img/QQ1408745/1/11#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var sl;
var mats;
var dds;
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            if (status == 0) {
                cm.sendNext("如果需要矿石兑换服务再来找我吧！");
                cm.dispose();
            }
            status--;
        }
        if (status == 0) {	
            var gsjb = ""+dd+"\r\n\t\t\t"+兑换中心+"\r\n"+群粉心+""

			gsjb += "#L1##b兑换新#v3605015##z3605015##r(需要#v3700296# + 1000金币)#l\r\n\r\n";
			//gsjb += "#L11##b兑换新#v3605015##z3605015##r(需要#v4004000#*100+1000金币)#l\r\n";
			gsjb += "#L2##b兑换新#v3605016##z3605016##r(需要#v3700297# + 1000金币)#l\r\n\r\n";
			//gsjb += "#L22##b兑换新#v3605016##z3605016##r(需要#v4004001#*100+1000金币)#l\r\n";
			gsjb += "#L3##b兑换新#v3605017##z3605017##r(需要#v3700298# + 1000金币)#l\r\n\r\n";
			//gsjb += "#L33##b兑换新#v3605017##z3605017##r(需要#v4004002#*100+1000金币)#l\r\n";
			gsjb += "#L4##b兑换新#v3605018##z3605018##r(需要#v3700299# + 1000金币)#l\r\n\r\n";
			//gsjb += "#L44##b兑换新#v3605018##z3605018##r(需要#v4004003#*100+1000金币)#l\r\n";
			gsjb += "#L5##b兑换新#v3605019##z3605019##r(需要#v3700300# + 1000金币)#l\r\n\r\n";

            cm.sendSimple(gsjb);
        } else if (status == 1) {
            if (cm.getPlayer() >= 5 && cm.getPlayer() <= 5) {
                cm.sendOk("GM不能参与兑换。");
                cm.dispose();
            }
            if (selection == 1) {
                if (cm.haveItem(3700296) == 0 || cm.getMeso() < 1000) {
                    cm.sendNext("#r你的背包内没有足够的#v3700296#\r\n或者你连1000金币都没有\r\n不能进行兑换！");
                    status = -1;
                } else {
                    zzks = 1;
					cm.sendGetNumber("#r请输入需要兑换的#v3605015#的数量:\r\n#b当前拥有#v3700296#的数量为：#r#c3700296#\r\n", 1, 1, 10000);
                }
            }    
            if (selection == 11) {
                if (cm.haveItem(4004000) == 0 || cm.getMeso() < 1000) {
                    cm.sendNext("#r你的背包内没有足够的#v4004000#\r\n或者你连1000金币都没有\r\n不能进行兑换！");
                    status = -1;
                } else {
                    zzks = 11;
					cm.sendGetNumber("#r请输入需要兑换的#v3605015#的数量:\r\n#b当前拥有#v4004000#的数量为：#r#c4004000#\r\n", 1, 1, 10000);
                }
            }  			
			else if (selection == 2) {
                if (cm.haveItem(3700297) == 0 || cm.getMeso() < 1000) {
                    cm.sendNext("#r你的背包内没有足够的#v3700297#\r\n或者你连1000金币都没有\r\n不能进行兑换！");
                    status = -1;
                } else {
                    zzks = 2;
					cm.sendGetNumber("#r请输入需要兑换的#v3605016#的数量:\r\n#b当前拥有#v3700297#的数量为：#r#c3700297#\r\n", 1, 1, 10000);
                }
            }
			else if (selection == 22) {
                if (cm.haveItem(4004001) == 0 || cm.getMeso() < 1000) {
                    cm.sendNext("#r你的背包内没有足够的#v4004001#\r\n或者你连1000金币都没有\r\n不能进行兑换！");
                    status = -1;
                } else {
                    zzks = 22;
					cm.sendGetNumber("#r请输入需要兑换的#v3605016#的数量:\r\n#b当前拥有#v4004001#的数量为：#r#c4004001#\r\n", 1, 1, 10000);
                }
            }
			else if (selection == 3) {
                if (cm.haveItem(3700298) == 0 || cm.getMeso() < 1000) {
                    cm.sendNext("#r你的背包内没有足够的#v3700298#\r\n或者你连1000金币都没有\r\n不能进行兑换！");
                    status = -1;
                } else {
                    zzks = 3;
					cm.sendGetNumber("#r请输入需要兑换的#v3605017#的数量:\r\n#b当前拥有#v3700298#的数量为：#r#c3700298#\r\n", 1, 1, 10000);
                }
            }
			else if (selection == 33) {
                if (cm.haveItem(4004002) == 0 || cm.getMeso() < 1000) {
                    cm.sendNext("#r你的背包内没有足够的#v4004002#\r\n或者你连1000金币都没有\r\n不能进行兑换！");
                    status = -1;
                } else {
                    zzks = 33;
					cm.sendGetNumber("#r请输入需要兑换的#v3605017#的数量:\r\n#b当前拥有#v4004002#的数量为：#r#c4004002#\r\n", 1, 1, 10000);
                }
            }
			else if (selection == 4) {
                if (cm.haveItem(3700299) == 0 || cm.getMeso() < 1000) {
                    cm.sendNext("#r你的背包内没有足够的#v3700299#\r\n或者你连1000金币都没有\r\n不能进行兑换！");
                    status = -1;
                } else {
                    zzks = 4;
					cm.sendGetNumber("#r请输入需要兑换的#v3605018#的数量:\r\n#b当前拥有#v3700299#的数量为：#r#c3700299#\r\n", 1, 1, 10000);
                }
            }
			else if (selection == 44) {
                if (cm.haveItem(4004003) == 0 || cm.getMeso() < 1000) {
                    cm.sendNext("#r你的背包内没有足够的#v4004003#\r\n或者你连1000金币都没有\r\n不能进行兑换！");
                    status = -1;
                } else {
                    zzks = 44;
					cm.sendGetNumber("#r请输入需要兑换的#v3605018#的数量:\r\n#b当前拥有#v4004003#的数量为：#r#c4004003#\r\n", 1, 1, 10000);
                }
            }
			else if (selection == 5) {
                if (cm.haveItem(3700300) == 0 || cm.getMeso() < 1000) {
                    cm.sendNext("#r你的背包内没有足够的#v3700300#\r\n或者你连1000金币都没有\r\n不能进行兑换！");
                    status = -1;
                } else {
                    zzks = 5;
					cm.sendGetNumber("#r请输入需要兑换的#v3605019#的数量:\r\n#b当前拥有#v3700300#的数量为：#r#c3700300#\r\n", 1, 1, 10000);
                }
            }
			else if (selection == 55) {
                if (cm.haveItem(4004004) == 0 || cm.getMeso() < 1000) {
                    cm.sendNext("#r你的背包内没有足够的#v4004004#\r\n或者你连1000金币都没有\r\n不能进行兑换！");
                    status = -1;
                } else {
                    zzks = 55;
					cm.sendGetNumber("#r请输入需要兑换的#v3605019#的数量:\r\n#b当前拥有#v4004004#的数量为：#r#c4004004#\r\n", 1, 1, 10000);
                }
            }
        } else if (status == 2) {
			if (zzks == 1) {
                if (cm.haveItem(3700296, selection) && cm.getMeso() >= selection) {
                    cm.gainItem(3700296, -selection);
					cm.gainMeso(-1000*selection);
					cm.gainItem(3605015, selection);
                    cm.sendOk("#b你已成功使用#v3700296#*#r"+selection+"\r\n#b兑换了#v3605015#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用"+selection+"个力量水晶兑换了"+selection+"个高等力量水晶！"));
                } else {
                    cm.sendNext("#r兑换数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 11) {
                if (cm.haveItem(4004000, selection) && cm.getMeso() >= selection) {
                    cm.gainItem(4004000, -selection);
					cm.gainMeso(-1000*selection);
					cm.gainItem(3605015, selection);
                    cm.sendOk("#b你已成功使用#v4004000#*#r"+selection+"\r\n#b兑换了#v3605015#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用"+selection+"个力量水晶兑换了"+selection+"个高等力量水晶！"));
                } else {
                    cm.sendNext("#r兑换数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 2) {
                if (cm.haveItem(3700297, selection) && cm.getMeso() >= selection) {
                    cm.gainItem(3700297, -selection);
					cm.gainMeso(-1000*selection);
					cm.gainItem(3605016, selection);
                    cm.sendOk("#b你已成功使用#v3700297#*#r"+selection+"\r\n#b兑换了#v3605016#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用"+selection+"个智慧水晶兑换了"+selection+"个高等智慧水晶！"));
                } else {
                    cm.sendNext("#r兑换数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 22) {
                if (cm.haveItem(4004001, selection) && cm.getMeso() >= selection) {
                    cm.gainItem(4004001, -selection);
					cm.gainMeso(-1000*selection);
					cm.gainItem(3605016, selection);
                    cm.sendOk("#b你已成功使用#v4004001#*#r"+selection+"\r\n#b兑换了#v3605016#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用"+selection+"个智慧水晶兑换了"+selection+"个高等智慧水晶！"));
                } else {
                    cm.sendNext("#r兑换数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 3) {
                if (cm.haveItem(3700298, selection) && cm.getMeso() >= selection) {
                    cm.gainItem(3700298, -selection);
					cm.gainMeso(-1000*selection);
					cm.gainItem(3605017, selection);
                    cm.sendOk("#b你已成功使用#v3700298#*#r"+selection+"\r\n#b兑换了#v3605017#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用"+selection+"个敏捷水晶兑换了"+selection+"个高等敏捷水晶！"));
                } else {
                    cm.sendNext("#r兑换数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 33) {
                if (cm.haveItem(4004002, selection) && cm.getMeso() >= selection) {
                    cm.gainItem(4004002, -selection);
					cm.gainMeso(-1000*selection);
					cm.gainItem(3605017, selection);
                    cm.sendOk("#b你已成功使用#v4004002#*#r"+selection+"\r\n#b兑换了#v3605017#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用"+selection+"个敏捷水晶兑换了"+selection+"个高等敏捷水晶！"));
                } else {
                    cm.sendNext("#r兑换数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 4) {
                if (cm.haveItem(3700299, selection) && cm.getMeso() >= selection) {
                    cm.gainItem(3700299, -selection);
					cm.gainMeso(-1000*selection);
					cm.gainItem(3605018, selection);
                    cm.sendOk("#b你已成功使用#v3700299#*#r"+selection+"\r\n#b兑换了#v3605018#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用"+selection+"个幸运水晶兑换了"+selection+"个高等幸运水晶！"));
                } else {
                    cm.sendNext("#r兑换数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 44) {
                if (cm.haveItem(4004003, selection) && cm.getMeso() >= selection) {
                    cm.gainItem(4004003, -selection);
					cm.gainMeso(-1000*selection);
					cm.gainItem(3605018, selection);
                    cm.sendOk("#b你已成功使用#v4004003#*#r"+selection+"\r\n#b兑换了#v3605018#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用"+selection+"个幸运水晶兑换了"+selection+"个高等幸运水晶！"));
                } else {
                    cm.sendNext("#r兑换数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 5) {
                if (cm.haveItem(3700300, selection) && cm.getMeso() >= selection) {
                    cm.gainItem(3700300, -selection);
					cm.gainMeso(-1000*selection);
					cm.gainItem(3605019, selection);
                    cm.sendOk("#b你已成功使用#v3700300#*#r"+selection+"\r\n#b兑换了#v3605019#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用"+selection+"个黑暗水晶兑换了"+selection+"个高等黑暗水晶！"));
                } else {
                    cm.sendNext("#r兑换数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 55) {
                if (cm.haveItem(4004004, selection) && cm.getMeso() >= selection) {
                    cm.gainItem(4004004, -selection);
					cm.gainMeso(-1000*selection);
					cm.gainItem(3605019, selection);
                    cm.sendOk("#b你已成功使用#v4004004#*#r"+selection+"\r\n#b兑换了#v3605019#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用"+selection+"个黑暗水晶兑换了"+selection+"个高等黑暗水晶！"));
                } else {
                    cm.sendNext("#r兑换数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 6) {
                if (cm.haveItem(3605015, selection*1) && cm.haveItem(3605016, selection*1) && cm.haveItem(3605017, selection*1) && cm.haveItem(3605018, selection*1) && cm.haveItem(3605019, selection*1) && cm.getMeso() >= selection000) {
                    cm.gainItem(3605015, -selection*1);
					cm.gainItem(3605016, -selection*1);
					cm.gainItem(3605017, -selection*1);
					cm.gainItem(3605018, -selection*1);
					cm.gainItem(3605019, -selection*1);
					cm.gainMeso(-1000000*selection);
					cm.gainItem(4251202, selection);
                    cm.sendOk("#b你已成功使用#v3605015##v3605016##v3605017##v3605018##v3605019#*#r"+selection*1+"#b兑换了#v4251202#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "高等水晶宝石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用各种高等水晶各"+selection*1+"个兑换了"+selection+"个万能水晶！"));
                } else {
                    cm.sendNext("#r兑换所需的高等水晶或金币数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 7) {
                if (cm.haveItem(4020000, selection) && cm.haveItem(4020001, selection) && cm.haveItem(4020002, selection) && cm.haveItem(4020003, selection) && cm.haveItem(4020004, selection) && cm.haveItem(4020005, selection) && cm.haveItem(4020006, selection) && cm.haveItem(4020007, selection) && cm.haveItem(4020008, selection) && cm.getMeso() >= selection0) {
                    cm.gainItem(4020000, -selection);
					cm.gainItem(4020001, -selection);
					cm.gainItem(4020002, -selection);
					cm.gainItem(4020003, -selection);
					cm.gainItem(4020004, -selection);
					cm.gainItem(4020005, -selection);
					cm.gainItem(4020006, -selection);
					cm.gainItem(4020007, -selection);
					cm.gainItem(4020008, -selection);
					cm.gainMeso(-10000*selection);
					cm.gainItem(4011007, selection);
                    cm.sendOk("#b你已成功使用#v4020000##v4020001##v4020002##v4020003##v4020004##v4020005##v4020006##v4020007##v4020008#*#r"+selection+"\r\n#b兑换了#v4011007#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "月矿石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用各种成品宝石各"+selection+"个兑换了"+selection+"个月石！"));
                } else {
                    cm.sendNext("#r兑换所需的成品宝石或金币数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 77) {
                if (cm.haveItem(4021000, selection*1) && cm.haveItem(4021001, selection*1) && cm.haveItem(4021002, selection*1) && cm.haveItem(4021003, selection*1) && cm.haveItem(4021004, selection*1) && cm.haveItem(4021005, selection*1) && cm.haveItem(4021006, selection*1) && cm.haveItem(4021007, selection*1) && cm.haveItem(4021008, selection*1) && cm.getMeso() >= selection0) {
                    cm.gainItem(4021000, -selection*1);
					cm.gainItem(4021001, -selection*1);
					cm.gainItem(4021002, -selection*1);
					cm.gainItem(4021003, -selection*1);
					cm.gainItem(4021004, -selection*1);
					cm.gainItem(4021005, -selection*1);
					cm.gainItem(4021006, -selection*1);
					cm.gainItem(4021007, -selection*1);
					cm.gainItem(4021008, -selection*1);
					cm.gainMeso(-10000*selection);
					cm.gainItem(4011007, selection);
                    cm.sendOk("#b你已成功使用#v4020000##v4020001##v4020002##v4020003##v4020004##v4020005##v4020006##v4020007##v4020008#*#r"+selection*1+"#b兑换了#v4011007#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "月矿石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用各种成品宝石各"+selection*1+"个兑换了"+selection+"个月石！"));
                } else {
                    cm.sendNext("#r兑换所需的成品宝石或金币数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 8) {
                if (cm.haveItem(4010000, selection) && cm.haveItem(4010001, selection) && cm.haveItem(4010002, selection) && cm.haveItem(4010003, selection) && cm.haveItem(4010004, selection) && cm.haveItem(4010005, selection) && cm.haveItem(4010006, selection) && cm.getMeso() >= selection0) {
                    cm.gainItem(4010000, -selection);
					cm.gainItem(4010001, -selection);
					cm.gainItem(4010002, -selection);
					cm.gainItem(4010003, -selection);
					cm.gainItem(4010004, -selection);
					cm.gainItem(4010005, -selection);
					cm.gainItem(4010006, -selection);
					cm.gainMeso(-10000*selection);
					cm.gainItem(4021009, selection);
                    cm.sendOk("#b你已成功使用#v4010000##v4010001##v4010002##v4010003##v4010004##v4010005##v4010006#*#r"+selection+"\r\n#b兑换了#v4021009#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "锂矿石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用各种成品矿石各"+selection+"个兑换了"+selection+"个锂！"));
                } else {
                    cm.sendNext("#r兑换所需的成品矿石或金币数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
			else if (zzks == 88) {
                if (cm.haveItem(4011000, selection*1) && cm.haveItem(4011001, selection*1) && cm.haveItem(4011002, selection*1) && cm.haveItem(4011003, selection*1) && cm.haveItem(4011004, selection*1) && cm.haveItem(4011005, selection*1) && cm.haveItem(4011006, selection*1) && cm.getMeso() >= selection0) {
                    cm.gainItem(4011000, -selection*1);
					cm.gainItem(4011001, -selection*1);
					cm.gainItem(4011002, -selection*1);
					cm.gainItem(4011003, -selection*1);
					cm.gainItem(4011004, -selection*1);
					cm.gainItem(4011005, -selection*1);
					cm.gainItem(4011006, -selection*1);
					cm.gainMeso(-10000*selection);
					cm.gainItem(4021009, selection);
                    cm.sendOk("#b你已成功使用#v4010000##v4010001##v4010002##v4010003##v4010004##v4010005##v4010006#*#r"+selection+"\r\n#b兑换了#v4021009#*#r"+selection+"#b！");
					//Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(12, cm.getClient().getChannel(), "锂矿石" + " : " + "恭喜" + cm.getChar().getName() + "成功使用各种成品矿石各"+selection+"个兑换了"+selection+"个锂！"));
                } else {
                    cm.sendNext("#r兑换所需的成品矿石或金币数量大于你拥有的数量，请重新操作！");
                    cm.dispose();
                }
            }
        } else {
            cm.dispose();
        }
    }
}