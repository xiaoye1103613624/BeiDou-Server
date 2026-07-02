/* Fredrick NPC (9030000)
 * @author kevintjuh93
 */

var status;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        if (!cm.hasMerchant() && cm.hasMerchantItems()) {
            cm.showFredrick();
            cm.dispose();
        } else {
            if (cm.hasMerchant()) {
                cm.sendOk("你有一个雇佣商店正在营业中。");
                cm.dispose();
            } else {
                cm.sendOk("您没有任何物品或金币可以取回。");
                cm.dispose();
            }
        }
    }
}
