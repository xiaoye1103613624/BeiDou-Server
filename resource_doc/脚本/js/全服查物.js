/* global cm */

var status, str, select, list;

function start() {
    status = -1;
    str = "";
    select = -1;
    //str += "================#e高级检索工具#n================";
    str += "\r\n#L1#道具#l";
    str += "\t#L2#NPC#l";
    str += "\t#L3#地图#l";
    str += "\r\n\r\n#L4#怪物#l";
 //   str += "\r\n#L5#任务#l";
    str += "\t#L6#技能#l";
    str += "\t#L7#职业#l";
    //str += "\r\n#L8#伺服器包头#l";
    //str += "\r\n#L9#用户端包头#l";
    //str += "\r\n#L10#发型#l";
    //str += "\r\n#L11#脸型#l";
    //str += "\r\n#L12#肤色#l";
    cm.sendSimple(str);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
        cm.dispose();
        return;
    }
    switch (status) {
        case 0:
            str = selection;
            cm.sendGetText("请输入需要检索的讯息:");
            break;
        case 1:
            switch (str) {
                case 10:
                case 11:
                case 12:
                    list = cm.getSearchData(str, cm.getText());
                    if (list == null) {
                        cm.sendOk("搜寻不到讯息");
                        cm.dispose();
                        return;
                    }
                    cm.sendStyle("", list);
                    break;
                default:
                    cm.sendOk(cm.searchData(str, cm.getText()));
            }
            break;
        case 2:
            if (cm.getPlayerStat("ADMIN")) {
                if (!cm.foundData(str, cm.getText())) {
                    cm.dispose();
                    return;
                }
                if (select == -1) {
                    select = selection;
                }
                switch (str) {
                    case 1:
                        if (select < 1000000) {
                            if (select / 10000 == 2) {
                                cm.setFace(select);
                            } else if (select / 10000 == 3) {
                                cm.setHair(select);
                            }
                            cm.dispose();
                        } else if (select < 2000000) {
                            if (cm.canHold(select)) {
                                cm.getItemLog("高级检索", "\r\n【"+cm.getName()+"】  刷物品ID  " + select + "  (" + cm.getItemName(select) + ") 1个\r\n");
                                cm.gainItem(select, 1);
                            }

                            cm.dispose();
                        } else if (select >= 5000000 && select < 5010000) {
                            cm.sendGetNumber("选中的宠物为#i" + select + ":# #z" + select + "#\r\n请输入生命时间(天):", 1, 1, 100);
                        } else {
                            cm.sendGetNumber("选中的道具为#i" + select + ":# #z" + select + "#\r\n请输入製作数量:", 1, 1, 10000);
                        }
                        break;
                    case 2:
                        cm.dispose();
                        cm.playerMessage(5, "打开NPC,ID:" + select);
                        cm.openNpc(select);
                        break;
                    case 3:
                        cm.playerMessage(5, "传送到地图,ID:" + select);
                        cm.warp(select, 0);
                        cm.dispose();
                        break;
                    case 4:
                        cm.sendGetNumber("选中的怪物为#o" + select + "#\r\n请输入召唤数量:", 1, 1, 1000);
                        break;
                    case 5:
                        cm.sendSimple("选中的任务ID为" + select + "\r\n请选择需要执行的操作:\r\n#L0#开始任务#l\r\n#L1#完成任务#l");
                        break;
                    case 6:
                        cm.sendGetNumber("选中的技能ID为" + select + "\r\n请输入使用等级:", 1, 1, 30);
                        break;
                    case 7:
                        cm.playerMessage(5, "转职,职业代码:" + select);
                        cm.changeJob(select);
                        cm.dispose();
                        break;
                    case 8:
                    case 9:
                        cm.dispose();
                        break;
                    case 10:
                        cm.playerMessage(5, "更变发型, 发型代码:" + list[select]);
                        cm.setHair(list[select]);
                        cm.dispose();
                        break;
                    case 11:
                        cm.playerMessage(5, "更变脸型, 脸型代码:" + list[select]);
                        cm.setFace(list[select]);
                        cm.dispose();
                        break;
                    case 12:
                        cm.playerMessage(5, "更变肤色, 肤色代码:" + list[select]);
                        cm.setSkin(list[select]);
                        cm.dispose();
                        break;
                    default:
                        cm.dispose();
                }
            } else {
                cm.sendOk("您的权限不够。");
                cm.dispose();
                return;
            }
            break;
        case 3:
            if (cm.getPlayerStat("ADMIN")) {
                switch (str) {
                    case 1:
                        if (select < 2000000) {
                            if (cm.canHold(select)) {
                                //cm.getItemLog("高级检索", " 物品 " + select + "(" + cm.getItemName(select) + ") 1个。");
								cm.getItemLog("高级检索", "\r\n【"+cm.getName()+"】  刷物品ID  " + select + "  (" + cm.getItemName(select) + ") 1个\r\n");
                                cm.gainItem(select, 1);
                            }
                        } else if (select >= 5000000 && select < 5010000) {
                            if (cm.canHold(select)) {
                                //cm.getItemLog("高级检索", " 物品 " + select + "(" + cm.getItemName(select) + ") 时间 " + selection + " 1个。");
								cm.getItemLog("高级检索", "\r\n【"+cm.getName()+"】  刷物品ID  " + select + "  (" + cm.getItemName(select) + ") 1个\r\n");
                                cm.gainItem(select, 1, selection);
                            }
                        } else {
                            //for (var i = 0; i < selection; i++) {
                            if (cm.canHold(select, selection)) {
                                //cm.getItemLog("高级检索", " 物品 " + select + "(" + cm.getItemName(select) + ") " + selection + "个。");
								cm.getItemLog("高级检索", "\r\n【"+cm.getName()+"】  刷物品ID  " + select + "  (" + cm.getItemName(select) + ") " + selection + "个\r\n");
                                cm.gainItem(select, selection);
                            }
                            //}
                        }
                        cm.dispose();
                        break;
                    case 4:
                        cm.spawnMonster(select, selection);
                        cm.dispose();
                        break;
                    case 5:
                        cm.dispose();
                        switch (selection) {
                            case 0:
                                cm.startQuest(select);
                                break;
                            case 1:
                                cm.completeQuest(select);
                                break;
                        }
                        break;
                    case 6:
					    //cm.getPlayer().changeKeybinding(11, selection, select);
						//im.teachSkill(学习技能id, 学习技能等级, 能手等级);
                        //cm.useSkill(11,select, selection);
						cm.teachSkill(select, selection);
						cm.设置键盘(11, 1, select);
                        cm.dispose();
                        break;
                    default:
                        cm.dispose();
                }
            } else {
                cm.sendOk("您的权限不够。");
                cm.dispose();
                return;
            }
               /*     case 6:
                        cm.teachSkill(select, selection);
				im.teachSkill(selection, select);
				if(键盘位置 >= 1 && 键盘位置 <= 9){
					im.getPlayer().changeKeybinding(键盘位置 + 1, 1, selection);
				}else if(键盘位置 == 0){
					im.getPlayer().changeKeybinding(11, 1, selection);
				}else{
					im.sendOk("你设置的键盘位置不在范围内！");
					im.dispose();
					return;
				}
                        cm.dispose();
                        break;
                    default:
                        cm.dispose();
                }
            } else {
                cm.sendOk("您的权限不够。");
                cm.dispose();
                return;
            }*/
        default:
            cm.dispose();
    }
}