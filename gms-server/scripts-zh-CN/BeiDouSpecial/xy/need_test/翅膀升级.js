
/**
 079 085脚本
 QQ:870074996
 作者:小猫
**/
var status = 0;
var cost;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0){
	cm.sendOk("感谢你的光临！");
	cm.dispose();
	return;	
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
            var text = "";
                text += "0你想拥有翅膀吗???\r\n";
                text += "我这里为您准备了以下翅膀#b[属性点装]#k\r\n";
                text += "翅膀材料出处说明:\r\n";
                text += "#v4003005##v4003004# - 动物系列的怪物都爆(天空彩云公园#b星光精灵#k，冰封雪域#b雪人#k等. )\r\n";
                text += "#v4000064#-古代神社 - 乌鸦森林打乌鸦\r\n";
                text += "#v4001006# - 幽灵船5 -水手掉落\r\n";
                text += "#v4032056#闹钟BOSS掉落(保底1个/只),会员签到获得\r\n";
				if(cm.getBossRank("翅膀等阶",1) <= 0){
                text += "当前翅膀等阶:[#r未激活#k]\r\n";
				}else if(cm.getBossRank("翅膀等阶",1) == 1){
                text += "当前翅膀等阶:[#r翅膀一阶#k]\r\n";
				}else if(cm.getBossRank("翅膀等阶",1) == 2){
                text += "当前翅膀等阶:[#r翅膀二阶#k]\r\n";
				}else if(cm.getBossRank("翅膀等阶",1) == 3){
                text += "当前翅膀等阶:[#r翅膀三阶#k]\r\n";
				}else if(cm.getBossRank("翅膀等阶",1) == 4){
                text += "当前翅膀等阶:[#r翅膀四阶#k]\r\n";
				}else if(cm.getBossRank("翅膀等阶",1) == 5){
                text += "当前翅膀等阶:[#r翅膀五阶#k]\r\n";
				}
                text += "#v1102074##v1102153##v1102389##v1102390##v1102378##v1102376##v1102377##v1102702##v1102703##v1102386##v1102385##v1102604##v1102039##v1102709##v1102605#\r\n";
                text += "#d每阶翅膀都可以自由选取哟#k\r\n";
                text += "\t\t\t#e是否激活/升级翅膀?\r\n";
	    cm.sendYesNo(text);
    } else if (status == 1) {
				if(cm.getBossRank("翅膀等阶",1) <= 0){
				cm.dispose();
            	cm.openNpc(9310072, "翅膀一阶");
				}else if(cm.getBossRank("翅膀等阶",1) == 1){
				cm.dispose();
            	cm.openNpc(9310072, "翅膀二阶");
				}else if(cm.getBossRank("翅膀等阶",1) == 2){
				cm.dispose();
            	cm.openNpc(9310072, "翅膀三阶");
				}else if(cm.getBossRank("翅膀等阶",1) == 3){
				cm.dispose();
            	cm.openNpc(9310072, "翅膀四阶");
				}else if(cm.getBossRank("翅膀等阶",1) == 4){
				cm.dispose();
            	cm.openNpc(9310072, "翅膀五阶");
				}else if(cm.getBossRank("翅膀等阶",1) == 5){
				cm.sendOk("你点翅膀已经#r五阶#k无法继续提升了！");
				cm.dispose();
				return;	
				}
    }
}