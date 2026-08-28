// 9031012 装备制作铁砧 → 装备强化大师（统一入口）
// 灵韵觉醒 · 星之力 · 洗炼 · 潜能 · 灵魂 · 星岩

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) { cm.dispose(); return; }
    status++;

    if (status === 0) {
        var t = "#e#b<装备制作铁砧 · 强化大师>#k#n\r\n\r\n";
        t += "打造你的终极装备！\r\n\r\n";
        t += "#L1##b⚔ 灵韵觉醒#k - 给武器注入技能之力#l\r\n";
        t += "#L2##b⭐ 星之力强化#k - 使用卷轴提升装备星级★1~10#l\r\n";
        t += "#L3##b🔮 潜能管理#k - 附加/鉴定/重随主潜能+附加潜能#l\r\n";
        t += "#L4##b✨ 洗炼鉴定#k - ①~⑤级16种词条鉴定/重洗#l\r\n";
        t += "#L5##b💎 灵魂宝珠#k - 给武器注入灵魂之力#l\r\n";
        t += "#L6##b🔩 星岩镶嵌#k - 装备镶孔提升属性#l\r\n";
        t += "#L7##b⚡ 装备注能#k - 注入雷电之力，累积属性⚡1~10#l\r\n";
        t += "\r\n#L0#离开#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 0) { cm.dispose(); return; }
        cm.dispose();
        switch (selection) {
            case 1: cm.openNpc(9031012, "xy/匠人街/灵韵觉醒"); break;
            case 2: cm.openNpc(9031012, "xy/匠人街/装备强化中心"); break;
            case 3: cm.openNpc(9031012, "xy/匠人街/装备强化中心"); break;
            case 4: cm.openNpc(9031012, "xy/匠人街/洗炼鉴定"); break;
            case 5: cm.openNpc(9031012, "xy/匠人街/装备强化中心"); break;
            case 6: cm.openNpc(9031012, "xy/匠人街/装备强化中心"); break;
            case 7: cm.openNpc(9031012, "xy/匠人街/装备注能"); break;
        }
    }
}
