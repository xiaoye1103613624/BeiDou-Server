/* Kedrick
    Fishking King NPC
    钓鱼系统NPC - 全新视觉美化版（功能1:1保留）
*/
// 视觉装饰变量（用户提供，完整保留）
var 花花1 = "#fUI/GuildMark/Mark/Pattern/00004020/1#";
var 花花2 = "#fUI/GuildMark/Mark/Pattern/00004020/3#";
var 花花3 = "#fUI/GuildMark/Mark/Pattern/00004020/5#";
var 花花4 = "#fUI/GuildMark/Mark/Pattern/00004020/7#";
var 花花5 = "#fUI/GuildMark/Mark/Pattern/00004020/9#";
var 花花6 = "#fUI/GuildMark/Mark/Pattern/00004020/11#";
var 花花7 = "#fUI/GuildMark/Mark/Pattern/00004020/13#";
var 花花8 = "#fUI/GuildMark/Mark/Pattern/00004020/14#";
var 花花9 = "#fUI/GuildMark/Mark/Pattern/00004020/15#";
var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 爱心 = "#fEffect/CharacterEff/1032063/0/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#";
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#";
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#";
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#";
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#"; 
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#"; 
var 任务简介 = "#fUI/UIWindow.img/Quest/summary#"; 
var 任务提示 = "#fUI/UIWindow.img/Quest/BtAlert/mouseOver/0#";

// 核心变量（保留原脚本所有定义）
var status = -1;
var sel;

function action(mode, type, selection) {
    // 状态切换逻辑（完全保留原代码）
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        var text = "";
        // 顶部对称装饰边框 - 花花+星星组合，视觉聚焦
        text += "        " + 花花1 + 花花2 + 花花3 + 星星 + 爱心 + 星星 + 花花4 + 花花5 + 花花6 + "        \r\n";
        text += "        " + 花花7 + 小红星 + 小蓝星 + 蓝色小兔子 + 小红星 + 小蓝星 + 花花8 + 花花9 + "        \r\n\r\n";
        
        // 标题区 - 带表情点缀，醒目且活泼
        text += "            #e#b【开心钓鱼中心】" + 表情1 + " 欢迎您！#k#n            \r\n\r\n";
        
        // 钓鱼须知 - 任务提示图标前缀，规则清晰易读
        text += 任务提示 + " 【钓鱼须知】\r\n";
        text += 蓝色箭头 + " 准备物品：钓鱼杆#v5340001# + 鱼饵#v2300001#\r\n";
        text += 蓝色箭头 + " 钓鱼规则：自由市场内找椅子坐下即可开始钓鱼\r\n";
        text += 红色箭头 + " 参与条件：角色等级≥30级 " + 表情2 + "\r\n\r\n";
        
        // 分隔线 - 正方形+星星组合，区分须知和功能区
        text += "        " + 正方形 + 星星 + 正方形 + 星星 + 正方形 + 星星 + 正方形 + 星星 + 正方形 + "        \r\n\r\n";

        // 道具购买区 - 音符装饰，专属红色箭头前缀，视觉统一
        text += 音符3 + " #e#r【道具购买区】#k#n " + 音符3 + "\r\n";
        text += "#L3#" + 红色箭头 + " 购买：#v5340001# + #v3010184# (10000点券)#l\r\n";
        text += "#L4#" + 红色箭头 + " 购买：100个#v2300001# (500点券)#l\r\n";
        text += "#L7#" + 红色箭头 + " 购买：1000个#v2300001# (5000点券)#l\r\n\r\n";

        // 功能服务区 - 任务简介图标装饰，蓝色箭头前缀，与购买区区分
        text += 任务简介 + " #e#b【功能服务区】#k#n " + 任务简介 + "\r\n";
        text += "#L2#" + 蓝色箭头 + " #v1112907# 戒指强化 #v1112907##l\r\n";
        text += "#L5#" + 蓝色箭头 + " #v4031636# 钓鱼兑换 #v3994742##l\r\n\r\n";
        
        // 底部收尾装饰 - 爱心+星星组合，与顶部呼应，视觉完整
        text += "        " + 爱心 + 小红星 + 小蓝星 + 星星 + 爱心 + 小蓝星 + 小红星 + 星星 + 爱心 + "        \r\n";

        // 原场景&等级限制逻辑（1:1保留，无任何修改）
        if (cm.getPlayer().getMapId() == 910000000) {
            cm.sendSimple(text);
        } else {
            if (cm.getLevel() > 29) {
                cm.sendSimple(text);
            } else {
                cm.sendOk(爱心 + 小红星 + " ?? 钓鱼系统参与条件 ?? " + 小蓝星 + 爱心 + "\r\n\r\n" + 红色箭头 + " 角色等级需达到30级以上才可参加！\r\n" + 蓝色箭头 + " 请升级后再来尝试哦~ " + 表情);
                cm.dispose();
            }
        }

    } else if (status == 1) {
        // 原所有功能逻辑（完全1:1保留，未做任何修改）
        if (sel == 0) {

        } else if (selection == 2) {
            cm.openNpc(9330108, "小鱼戒指强化");

        } else if (selection == 1) {
            cm.warp(910000000, 0);
            cm.dispose();
        } else if (selection == 10) {
            if (cm.getBossLog('钓鱼大赛') == 1) {
                cm.sendOk("只能领取一次！");
                cm.dispose();
            } else {
                if (cm.haveItem(3011000)) {
                    cm.sendOk("你已经有一把钓鱼椅。每个角色只能有1个钓鱼椅。");
                } else {
                    cm.gainItem(3011000, 1, 1);
                    cm.gainItem(5340001, 1, 1);
                    cm.gainItem(2300001, 100, 1);
                    cm.setBossLog('钓鱼大赛');
                    cm.sendOk("祝你快乐钓鱼！");
                    cm.dispose();
                }
            }
        } else if (selection == 11) {
            cm.sendOk("稍等");
            cm.dispose();
        } else if (selection == 2) {
            if (cm.haveItem(3011000)) {
                cm.sendOk("你已经有一把钓鱼椅。每个角色只能有1个钓鱼椅。");
            } else {
                if (cm.getLevel() > 49) {
                    if (cm.canHold(3011000) && cm.getMeso() >= 3000000) {
                        cm.gainMeso(-3000000);
                        cm.gainItem(3011000, 1, 12);
                        cm.sendOk("祝你快乐钓鱼！");
                        cm.dispose();
                    } else {
                        cm.sendOk("请检查是否有所需的金币或足够的背包空间。");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("最少要50级才可以来钓鱼！");
                    cm.dispose();
                }
            }
        } else if (selection == 3) {
            if (cm.getPlayer().getCSPoints(1) >= 10000) {
                cm.gainNX(-10000);	//加减点券
                cm.gainItem(5340001, 1);
                cm.gainItem(3010184, 1);
                cm.sendOk("祝你快乐钓鱼！");
                cm.dispose();
            } else {
                cm.sendOk("请检查是否有足够的点卷。");
                cm.dispose();
            }

        } else if (selection == 6) {
            if (cm.canHold(2300001) && cm.getMeso() >= 2000000) {
                cm.gainMeso(-2000000);
                cm.gainItem(2300000, 100);
                cm.sendOk("祝你快乐钓鱼！");
                cm.dispose();
            } else {
                cm.sendOk("请检查是否有所需的金币或足够的背包空间。");
                cm.dispose();
            }
        } else if (selection == 4) {
            if (cm.getPlayer().getCSPoints(1) >= 500) {
                cm.gainNX(-500);	//加减点券
                cm.gainItem(2300001, 100);
                cm.sendOk("祝你快乐钓鱼！");
                cm.dispose();
            } else {
                cm.sendOk("请检查是否有足够的点卷。");
                cm.dispose();
            }
        } else if (selection == 7) {
            if (cm.getPlayer().getCSPoints(1) >= 5000) {
                cm.gainNX(-5000);	//加减点券
                cm.gainItem(2300001, 1000);
                cm.sendOk("祝你快乐钓鱼！");
                cm.dispose();
            } else {
                cm.sendOk("请检查是否有足够的点卷。");
                cm.dispose();
            }
        } else if (selection == 5) {
            cm.openNpc(9330108, "回收鱼");
        }
    } else if (status == 2) {
        // 原二级菜单逻辑（完全1:1保留，无任何修改）
        if (sel == 1) {
            if (cm.canHold(2300001, 120) && cm.getMeso() >= 300000) {
                if (!cm.haveItem(2300001)) {
                    cm.gainMeso(-300000);
                    cm.gainItem(2300001, 120);
                    cm.sendNext("快乐钓鱼~");
                } else {
                    cm.sendNext("你已经有了钓鱼的诱饵。");
                }
            } else {
                cm.sendOk("请检查是否有所需的300000金币或足够的背包空间。");
            }
            cm.safeDispose();
        }
    }
}