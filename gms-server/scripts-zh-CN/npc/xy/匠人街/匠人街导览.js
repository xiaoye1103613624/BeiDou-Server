// 匠人街 · 导览 NPC
// 以匠人街已有NPC为中心，展示所有功能入口

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        var text = "#e#b<匠人街导览>#k#n\r\n";
        text += "欢迎来到匠人街！所有装备成长与挑战玩法都在这里。\r\n\r\n";

        text += "#L1##b[装备制作铁砧] 装备强化大师#k#l\r\n";
        text += "　灵韵觉醒 · 星之力强化 · 洗炼鉴定 · 潜能管理 · 灵魂/星岩\r\n\r\n";

        text += "#L2##b[装备打造师] 装备铸造中心#k#l\r\n";
        text += "　武器进阶 · 套服进阶 · 戒指进阶 · 装备锻造\r\n\r\n";

        text += "#L3##b[希梅尔] 副本与讨伐#k#l\r\n";
        text += "　挑战副本 · 世界Boss · 每日任务 · 混沌扎昆\r\n\r\n";

        text += "#L4##b[炼金术士] 生活技能#k#l\r\n";
        text += "　炼金术 · 炼药术\r\n\r\n";

        text += "#L5##b[材料商人] 商店#k#l\r\n";
        text += "　强化材料 · 潜能材料 · 经验商店 · 限购商店\r\n\r\n";

        text += "#L6##b[分解机器] 回收利用#k#l\r\n";
        text += "　卷轴分解 · 装备分解 · 时装分解 · 卷轴兑换\r\n\r\n";

        text += "#L7##b[修炼大师] 成长辅助#k#l\r\n";
        text += "　修炼挂机 · 师徒系统\r\n\r\n";

        text += "#L8##b[博学大师] 收集成就#k#l\r\n";
        text += "　收藏图鉴 · 知识问答\r\n\r\n";

        text += "#L9##b[矿石精炼] 资源加工#k#l\r\n";
        text += "#L10##b[Boss材料兑换]#k#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(9031012, "xy/匠人街/装备强化大师");
                return;
            case 2:
                cm.dispose();
                cm.openNpc(9031003, "xy/匠人街/装备铸造中心");
                return;
            case 3:
                cm.dispose();
                cm.openNpc(9031000, "xy/匠人街/副本讨伐中心");
                return;
            case 4:
                cm.dispose();
                cm.openNpc(9031005);
                return;
            case 5:
                cm.dispose();
                cm.openNpc(9031007);
                return;
            case 6:
                cm.dispose();
                cm.openNpc(9031011);
                return;
            case 7:
                cm.dispose();
                cm.openNpc(9031000, "xy/匠人街/成长辅助");
                return;
            case 8:
                cm.dispose();
                cm.openNpc(9031000, "xy/匠人街/收藏成就");
                return;
            case 9:
                cm.dispose();
                cm.openNpc(9031011, "xy/匠人街/矿石精炼");
                return;
            case 10:
                cm.dispose();
                cm.openNpc(9031000, "xy/匠人街/Boss材料兑换");
                return;
        }
        cm.dispose();
    }
}
