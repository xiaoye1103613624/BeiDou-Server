/**
 * 宠物成长喂养：消耗 4310337 初级 / 4310338 高级宠物精华
 * 入口：便民工具 / cm.openNpc(9900001, "xy/other/宠物成长")
 */

var JUNIOR = 4310337;
var SENIOR = 4310338;
var status = -1;
var petSlot = -1;
var foodId = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;

    if (status == 0) {
        var pets = cm.getPlayer().getPets();
        var text = "宠物成长喂养\r\n";
        text += "材料：#v" + JUNIOR + "# 初级精华 / #v" + SENIOR + "# 高级精华\r\n";
        text += "当前：初级 #b" + cm.getItemQuantity(JUNIOR) + "#k  高级 #b" + cm.getItemQuantity(SENIOR) + "#k\r\n\r\n";
        text += "请选择要喂养的#r已召唤#k成长宠：\r\n";
        var count = 0;
        for (var i = 0; i < 3; i++) {
            var pet = pets[i];
            if (pet == null) {
                continue;
            }
            if (!cm.isPetGrowthPet(pet.getItemId())) {
                continue;
            }
            var cur = cm.getPetGrowthExp(pet.getUniqueId());
            text += "#L" + i + "##v" + pet.getItemId() + "# " + pet.getName()
                + "（成长经验 #b" + cur + "#k）#l\r\n";
            count++;
        }
        if (count == 0) {
            cm.sendOk("没有召唤中的可成长宠物。\r\n请先召唤后台「宠物成长进阶」配置里的高版本宠。");
            cm.dispose();
            return;
        }
        cm.sendSimple(text);
    } else if (status == 1) {
        petSlot = selection;
        var pet = cm.getPlayer().getPet(petSlot);
        if (pet == null || !cm.isPetGrowthPet(pet.getItemId())) {
            cm.sendOk("宠物无效。");
            cm.dispose();
            return;
        }
        var text = "喂养 #v" + pet.getItemId() + "# #b" + pet.getName() + "#k\r\n";
        text += "成长经验：#b" + cm.getPetGrowthExp(pet.getUniqueId()) + "#k\r\n\r\n";
        text += "选择精华（经验可在参数管理调整）：\r\n";
        text += "#L0##v" + JUNIOR + "# 初级精华（默认+10） 拥有 #b" + cm.getItemQuantity(JUNIOR) + "#k#l\r\n";
        text += "#L1##v" + SENIOR + "# 高级精华（默认+50） 拥有 #b" + cm.getItemQuantity(SENIOR) + "#k#l\r\n";
        cm.sendSimple(text);
    } else if (status == 2) {
        foodId = selection == 1 ? SENIOR : JUNIOR;
        if (cm.getItemQuantity(foodId) < 1) {
            cm.sendOk("没有 #v" + foodId + "#。");
            cm.dispose();
            return;
        }
        var err = cm.feedPetEssence(foodId, petSlot);
        if (err && err.length > 0) {
            cm.sendOk(err);
        } else {
            cm.sendOk("喂养完成！");
        }
        cm.dispose();
    } else {
        cm.dispose();
    }
}
