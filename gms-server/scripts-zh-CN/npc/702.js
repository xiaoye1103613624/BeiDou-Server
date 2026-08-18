// NPC 702 · 米恩 · 遗忘山谷氛围 NPC
function start() {
    cm.sendOk(
        "不久前有个戴绿帽子、扛着大刀的人路过这里。\r\n" +
        "不知道他现在怎么样了……\r\n" +
        "你要是去洞穴深处，可千万小心。"
    );
    cm.dispose();
}

function action(mode, type, selection) {
    cm.dispose();
}
