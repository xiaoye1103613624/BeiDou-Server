/**
 * @触发条件：开拍卖功能
 * @每日签到：领取物品 npc
 * @npcName：冒险岛运营员
 * @npcID：   9000431
 **/

var 忠告 = "#k温馨提示：任何非法程序和外挂封号处理.封杀侥幸心理.";
function start() {
    status = -1;
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
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {

            var txt1 = "#d#L1##b【装备栏】#r会自动删相同道具(谨慎操作)#K#l#l\r\n\r\n";

            var txt2 = "#d#L2##b【消耗栏】#r会自动删相同道具(谨慎操作)#K#l#l\r\n\r\n";

            var txt3 = "#d#L3##b【设置栏】#r会自动删相同道具(谨慎操作)#K#l#l\r\n\r\n";

            var txt4 = "#d#L4##b【其他栏】#r会自动删相同道具(谨慎操作)#K#l#l\r\n\r\n";
		
            //var txt5 = "#d#L5##b【特殊栏】#r会自动删相同道具(谨慎操作)#K#l#l\r\n\r\n";
            cm.sendSimple("#b     请选择要删除的道具栏！ \r\n\r\n#r     选择后【一键删除24格之后的所有物品】(谨慎操作)\r\n误删GM不会管理！！#l \r\n\r\n"+ txt1 + txt2 + txt3 +  txt4 +"" );// txt3 +

        } else if (status == 1) {
            if (selection == 1) {  //装备
			cm.dispose();
             cm.openNpc(9900004, "装备栏");

            } else if (selection == 2) { //消耗
			cm.dispose();
             cm.openNpc(9900004, "消耗栏");

            } else if (selection == 3) {//设置
			cm.dispose();
                cm.openNpc(9900004, "设置栏");

            } else if (selection == 4) { //其他
			cm.dispose();
                cm.openNpc(9900004, "其他栏");

            } else if (selection == 5) { // 特殊
			cm.dispose();
                cm.openNpc(9900004, "特殊栏");

            }
        }
    }
}
