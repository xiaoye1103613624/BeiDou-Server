/**
 * @description GM工具：全服发送公告
 * 功能：GM输入公告内容，以全服广播方式发送（支持多种播报类型）
 * 入口：9900001.js case 900
 */

var PacketCreator = Java.type("org.gms.util.PacketCreator");
var Server = Java.type("org.gms.net.server.Server");

var status = -1;
var noticeType = 6;  // 默认：粉红色全服广播

// 公告类型说明
var NOTICE_TYPES = [
    { type: 0,  label: "白色文字（频道内）" },
    { type: 1,  label: "弹出对话框" },
    { type: 5,  label: "蓝色文字（全服）" },
    { type: 6,  label: "粉红色文字（全服推荐）" },
    { type: 9,  label: "右上角通知气泡" }
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    // GM权限检查
    if (!cm.getPlayer().isGM()) {
        cm.sendOk("你没有权限使用此功能！");
        cm.dispose();
        return;
    }

    if (status === 0) {
        showTypeMenu();
    } else if (status === 1) {
        // 选择公告类型
        if (selection >= 0 && selection < NOTICE_TYPES.length) {
            noticeType = NOTICE_TYPES[selection].type;
        }
        cm.sendGetText("请输入要发送的公告内容（支持 #r 变红等格式码）：");
    } else if (status === 2) {
        sendNotice(cm.getText());
    }
}

function showTypeMenu() {
    var text = "#eGM全服公告#n\r\n\r\n";
    text += "请选择公告类型：\r\n\r\n";
    for (var i = 0; i < NOTICE_TYPES.length; i++) {
        text += "#L" + i + "#" + NOTICE_TYPES[i].label + "#l\r\n";
    }
    cm.sendSimple(text);
}

function sendNotice(content) {
    if (!content || content.trim() === "") {
        cm.sendOk("#r公告内容不能为空！#k");
        cm.dispose();
        return;
    }

    // 添加GM前缀标识
    var fullMsg = "[GM公告] " + content.trim();

    try {
        // 全服广播
        Server.getInstance().broadcastMessage(
            PacketCreator.serverNotice(noticeType, fullMsg)
        );

        cm.sendOk("公告已发送！\r\n\r\n类型：" + getTypeName(noticeType) + "\r\n内容：" + fullMsg);
    } catch (e) {
        cm.sendOk("#r发送失败：#k" + e.getMessage());
    }
    cm.dispose();
}

function getTypeName(type) {
    for (var i = 0; i < NOTICE_TYPES.length; i++) {
        if (NOTICE_TYPES[i].type === type) return NOTICE_TYPES[i].label;
    }
    return "未知类型";
}
