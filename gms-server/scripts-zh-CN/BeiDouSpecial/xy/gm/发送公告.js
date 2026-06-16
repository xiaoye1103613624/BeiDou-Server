/*
 * 发送公告脚本（GM工具）
 * 功能：选择公告类型并输入内容，向全服发送公告消息
 * 弹窗类型(1)支持指定玩家ID单独发送
 */

var status = -1;
var selectedIdx = -1;       // 玩家选择的选项索引
var selectedType = -1;      // 实际 serverNotice 的 type 值
var targetPlayerId = -1;    // 弹窗目标玩家ID（0=全服）
var inputMessage = "";

// 公告类型列表（移除 type=4 顶部滚动消息，客户端不支持会闪退）
var typeList = [
    { type: 0, name: "[公告Notice] - 普通系统公告" },
    { type: 1, name: "[弹窗Popup] - 弹出对话框（支持指定玩家ID）" },
    { type: 2, name: "[扩音器Megaphone] - 全服扩音器" },
    { type: 3, name: "[超级扩音器Super Megaphone] - 超级扩音器" },
    { type: 5, name: "[粉色文字] - 粉色醒目文字" },
    { type: 6, name: "[浅蓝色文字] - 浅蓝色系统消息" }
];

var PacketCreator = Java.type('org.gms.util.PacketCreator');
var Server = Java.type('org.gms.net.server.Server');

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else if (mode === -1) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    // ========== 第0步：选择公告类型 ==========
    if (status == 0) {
        if (cm.getPlayer().gmLevel() < 1) {
            cm.sendOk("你没有权限使用此功能！");
            cm.dispose();
            return;
        }

        var txt = "#d#e< 发送公告 >#k#n\r\n\r\n";
        txt += "请选择公告类型：\r\n\r\n";
        for (var i = 0; i < typeList.length; i++) {
            txt += "#L" + i + "##b" + typeList[i].name + "#k#l\r\n";
        }
        cm.sendSimple(txt);
    }

    // ========== 第1步：弹窗类型输入目标玩家ID / 其他类型输入公告内容 ==========
    else if (status == 1) {
        selectedIdx = selection;
        if (selectedIdx < 0 || selectedIdx >= typeList.length) {
            cm.sendOk("无效的公告类型，请重新选择。");
            cm.dispose();
            return;
        }
        selectedType = typeList[selectedIdx].type;  // 映射到实际 serverNotice type 值
        var typeName = typeList[selectedIdx].name;

        if (selectedType == 1) {
            // 弹窗类型 → 先输入目标玩家ID
            cm.sendGetText("已选择类型：#b" + typeName + "#k\r\n\r\n请输入目标玩家的 #r角色ID#k（输入 0 则全服发送）：");
        } else {
            // 其他类型 → 直接输入公告内容
            cm.sendGetText("已选择类型：#b" + typeName + "#k\r\n\r\n请输入公告内容：");
        }
    }

    // ========== 第2步：弹窗类型输入公告内容 / 其他类型确认发送 ==========
    else if (status == 2) {
        if (selectedType == 1) {
            // 弹窗：保存玩家ID，继续输入公告内容
            var idText = cm.getText();
            if (idText == null || idText.length == 0) {
                cm.sendOk("玩家ID不能为空！");
                cm.dispose();
                return;
            }
            targetPlayerId = parseInt(idText);
            if (isNaN(targetPlayerId) || targetPlayerId < 0) {
                cm.sendOk("请输入有效的数字玩家ID！");
                cm.dispose();
                return;
            }

            if (targetPlayerId == 0) {
                // 输入0表示全服发送
                cm.sendGetText("已选择：#b全服弹窗#k\r\n\r\n请输入公告内容：");
            } else {
                cm.sendGetText("已选择类型：#b" + typeList[selectedIdx].name + "#k\r\n目标玩家ID：#r" + targetPlayerId + "#k\r\n\r\n请输入公告内容：");
            }
        } else {
            // 其他类型：保存公告内容，进入确认
            inputMessage = cm.getText();
            if (inputMessage == null || inputMessage.length == 0) {
                cm.sendOk("公告内容不能为空！");
                cm.dispose();
                return;
            }
            showConfirm();
        }
    }

    // ========== 第3步：弹窗类型确认发送 / 其他类型执行发送 ==========
    else if (status == 3) {
        if (selectedType == 1) {
            // 弹窗：保存公告内容，进入确认
            inputMessage = cm.getText();
            if (inputMessage == null || inputMessage.length == 0) {
                cm.sendOk("公告内容不能为空！");
                cm.dispose();
                return;
            }
            showConfirm();
        } else {
            // 其他类型：执行发送
            if (selection == 0) {
                executeBroadcast();
            } else {
                cm.sendOk("已取消发送公告。");
                cm.dispose();
            }
        }
    }

    // ========== 第4步（仅弹窗类型）：执行发送 ==========
    else if (status == 4) {
        if (selection == 0) {
            if (targetPlayerId == 0) {
                // 全服弹窗
                executeBroadcast();
            } else {
                executePlayerPopup();
            }
        } else {
            cm.sendOk("已取消发送公告。");
            cm.dispose();
        }
    }

    // 未知状态 → 安全退出
    else {
        cm.dispose();
    }
}

/** 显示确认对话框 */
function showConfirm() {
    var confirmTxt = "#d#e< 确认发送公告 >#k#n\r\n\r\n";
    confirmTxt += "公告类型：#b" + typeList[selectedIdx].name + "#k\r\n";

    if (selectedType == 1 && targetPlayerId > 0) {
        confirmTxt += "目标玩家ID：#r" + targetPlayerId + "#k\r\n";
    } else {
        confirmTxt += "发送范围：#r全服#k\r\n";
    }

    confirmTxt += "公告内容：#r" + inputMessage + "#k\r\n\r\n";
    confirmTxt += "确认发送？\r\n";
    confirmTxt += "#L0##b确认发送#l\r\n";
    confirmTxt += "#L1##r取消#l\r\n";
    cm.sendSimple(confirmTxt);
}

/** 全服广播 */
function executeBroadcast() {
    if (selectedType == 3) {
        // 超级扩音器需要频道参数
        Server.getInstance().broadcastMessage(
            cm.getClient().getWorld(),
            PacketCreator.serverNotice(3, cm.getClient().getChannel(), inputMessage)
        );
    } else {
        Server.getInstance().broadcastMessage(
            cm.getClient().getWorld(),
            PacketCreator.serverNotice(selectedType, inputMessage)
        );
    }

    cm.sendOk("公告已发送成功！\r\n\r\n类型：" + typeList[selectedIdx].name + "\r\n范围：全服\r\n内容：" + inputMessage);
    cm.getPlayer().dropMessage(5, "[GM公告] 已全服发送公告：" + inputMessage);
    cm.dispose();
}

/** 向指定玩家发送弹窗 */
function executePlayerPopup() {
    // 遍历所有频道查找目标玩家
    var worldId = cm.getClient().getWorld();
    var channels = Server.getInstance().getChannelsFromWorld(worldId);
    var found = false;

    for (var i = 0; i < channels.size(); i++) {
        var ch = channels.get(i);
        var target = ch.getPlayerStorage().getCharacterById(targetPlayerId);
        if (target != null) {
            target.sendPacket(PacketCreator.serverNotice(1, inputMessage));
            found = true;
            break;
        }
    }

    if (found) {
        cm.sendOk("弹窗公告已发送成功！\r\n\r\n类型：" + typeList[selectedIdx].name + "\r\n目标玩家ID：" + targetPlayerId + "\r\n内容：" + inputMessage);
        cm.getPlayer().dropMessage(5, "[GM公告] 已向玩家 " + targetPlayerId + " 发送弹窗：" + inputMessage);
    } else {
        cm.sendOk("发送失败！\r\n\r\n未找到玩家ID：#r" + targetPlayerId + "#k\r\n请确认玩家在线且ID正确。");
        cm.getPlayer().dropMessage(5, "[GM公告] 未找到玩家 " + targetPlayerId + "，弹窗发送失败");
    }
    cm.dispose();
}
