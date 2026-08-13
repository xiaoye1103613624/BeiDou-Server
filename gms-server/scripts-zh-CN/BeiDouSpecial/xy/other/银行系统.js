/**
 * @description 银行系统
 * 1. 开户：需输入并确认银行密码，确认后调用 BankManager.openAccount 创建账户（主键自增，角色隔离）
 * 2. 登录：输入密码校验通过后直接进入主菜单（存入/取出/转账/改密都在主菜单一级操作）
 * 3. 主菜单展示：银行账户ID、角色名称、余额（大额自动转换为 百万/千万/亿 单位辅助显示）
 *    - 存入扣 5% 手续费，取出无手续费
 *    - 转账：按对方银行账户ID转账，扣 10% 手续费（从转出金额中扣除）
 *    - 修改密码需先验证旧密码
 * 数据库操作全部在 org.gms.service.BankService 中完成，本脚本只负责交互和金币收发。
 * 入口：9900001.js case 179
 */

var BankManager = Java.type("org.gms.config.BankManager");

// 取出/转账金额输入框上限（避免超出 int 范围）
var MAX_INPUT_AMOUNT = 2000000000;

var characterId;
var loginAttempts = 0;
var tempPassword = null;
var transferTargetBankId = -1;
var transferTargetName = "";
var adminResetBankId = -1;

// ==================== 金额格式化 ====================

/**
 * 将金额格式化为带单位的可读字符串，大额自动换算为 百万/千万/亿。
 * 例：5634090000 -> "5,634,090,000 (56.34亿)"
 */
function formatMoney(amount) {
    var n = Number(amount);
    var raw = n.toLocaleString();
    var unit = "";
    if (n >= 100000000) {
        unit = " (" + (n / 100000000).toFixed(2) + "亿)";
    } else if (n >= 10000000) {
        unit = " (" + (n / 10000000).toFixed(2) + "千万)";
    } else if (n >= 1000000) {
        unit = " (" + (n / 1000000).toFixed(2) + "百万)";
    }
    return raw + unit;
}

// ==================== 入口 ====================

function start() {
    characterId = cm.getPlayer().getId();
    loginAttempts = 0;
    if (BankManager.hasAccount(characterId)) {
        levelAskPassword();
    } else {
        levelConfirmOpen();
    }
}

// ==================== 开户流程 ====================

function levelConfirmOpen() {
    var text = "#e银行系统#n\r\n\r\n您还没有开通银行账户。\r\n"
        + "开户后可存入/取出金币、向其他角色转账。\r\n"
        + "#r存入手续费5%，转账手续费10%，取出免手续费#k。\r\n\r\n"
        + "是否确认开户？";
    cm.sendYesNoLevel("Cancel", "InputNewPassword", text);
}

function levelCancel() {
    cm.dispose();
    cm.openNpc(9900001);
}

function levelInputNewPassword() {
    cm.getInputTextLevel("ConfirmNewPassword", "请输入您要设置的银行密码（至少4位，用于后续登录）：");
}

function levelConfirmNewPassword(inputText) {
    var pwd = (inputText || "").trim();
    if (pwd.length < 4) {
        cm.sendOkLevel("Restart", "密码长度至少需要4位，请重新开户。");
        return;
    }
    tempPassword = pwd;
    cm.getInputTextLevel("DoOpenAccount", "请再次输入密码以确认：");
}

function levelDoOpenAccount(inputText) {
    var confirmPwd = (inputText || "").trim();
    if (confirmPwd !== tempPassword) {
        tempPassword = null;
        cm.sendOkLevel("Restart", "两次输入的密码不一致，请重新开户。");
        return;
    }
    var result = BankManager.openAccount(characterId, tempPassword);
    tempPassword = null;
    if (result.get("success")) {
        cm.sendOkLevel("Restart", "开户成功！您的银行账户已创建，请使用刚设置的密码登录。");
    } else {
        cm.sendOkLevel("Restart", "开户失败：" + result.get("message"));
    }
}

function levelRestart() {
    start();
}

// ==================== 登录流程 ====================

function levelAskPassword() {
    cm.getInputTextLevel("DoLogin", "请输入银行密码登录：");
}

function levelDoLogin(inputText) {
    var result = BankManager.login(characterId, (inputText || "").trim());
    if (result.get("success")) {
        loginAttempts = 0;
        levelMenuMain();
        return;
    }
    loginAttempts++;
    if (loginAttempts >= 3) {
        cm.sendOkLevel("Exit", "密码错误次数过多，请稍后再来。");
        return;
    }
    cm.getInputTextLevel("DoLogin", "密码错误，请重新输入（" + loginAttempts + "/3）：");
}

function levelExit() {
    cm.dispose();
    cm.openNpc(9900001);
}

// ==================== 主菜单（登录后直接可办理存取/转账/改密） ====================

function levelMenuMain() {
    var bankId = BankManager.getAccountId(characterId);
    var balance = BankManager.getBalance(characterId);
    var text = "#e银行系统#n\r\n\r\n";
    text += "#r*请勿将账户密码告诉他人，避免造成不可挽回的损失#k\r\n\r\n";
    text += "账户：#b" + bankId + "#k\r\n";
    text += "户主：#b" + cm.getPlayer().getName() + "#k\r\n";
    text += "金币：#r" + formatMoney(balance) + "#k\r\n\r\n";
    text += "#L1#存入金币#l\t#L2#取出金币#l\t#L3##r转账金币#k#l\r\n\r\n";
    text += "#L4#修改密码#l\r\n\r\n";
    if (cm.getPlayer().isGM()) {
        text += "#L5##r[管理员]重置密码#k#l\r\n\r\n";
    }
    text += "#L0##g退出账户#k#l\r\n";
    cm.sendNextSelectLevel("HandleMenuMain", text);
}

function levelHandleMenuMain(selection) {
    if (selection === 0) { cm.dispose(); cm.openNpc(9900001); return; }
    if (selection === 1) { levelAskDepositAmount(); return; }
    if (selection === 2) { levelAskWithdrawAmount(); return; }
    if (selection === 3) { levelTransferAskTarget(); return; }
    if (selection === 4) { levelChangePwdAskOld(); return; }
    if (selection === 5 && cm.getPlayer().isGM()) { levelAdminResetAskBankId(); return; }
    levelMenuMain();
}

// ==================== 存入 ====================

function levelAskDepositAmount() {
    var meso = cm.getMeso();
    if (meso <= 0) {
        cm.sendOkLevel("MenuMain", "您身上没有金币可以存入。");
        return;
    }
    cm.getInputNumberLevel("DoDeposit", `拥有金币：\t#r${formatMoney(meso)}#k \r\n请输入存入金额,手续费#r5%#k：`, 1, 1, Math.min(meso, MAX_INPUT_AMOUNT));
}

function levelDoDeposit(inputNum) {
    var amount = parseInt(inputNum, 10);
    if (isNaN(amount) || amount <= 0) {
        cm.sendOkLevel("MenuMain", "金额不合法。");
        return;
    }
    if (cm.getMeso() < amount) {
        cm.sendOkLevel("MenuMain", "您身上的金币不足。");
        return;
    }
    var result = BankManager.deposit(characterId, amount);
    if (result.get("success")) {
        cm.gainMeso(-amount);
        cm.sendOkLevel("MenuMain", "存入成功！\r\n本金：#r" + formatMoney(amount) + "#k 金币\r\n手续费：#r" + formatMoney(result.get("fee")) + "#k 金币\r\n实际到账：#r" + formatMoney(result.get("credited")) + "#k 金币");
    } else {
        cm.sendOkLevel("MenuMain", "存入失败：" + result.get("message"));
    }
}

// ==================== 取出 ====================

function levelAskWithdrawAmount() {
    var balance = BankManager.getBalance(characterId);
    if (balance <= 0) {
        cm.sendOkLevel("MenuMain", "银行账户余额为0。");
        return;
    }
    cm.getInputNumberLevel("DoWithdraw", `拥有金币：\t#r${formatMoney(balance)}#k \r\n请输入取款金额#k：`, 1, 1, Math.min(balance, MAX_INPUT_AMOUNT));
}

function levelDoWithdraw(inputNum) {
    var amount = parseInt(inputNum, 10);
    if (isNaN(amount) || amount <= 0) {
        cm.sendOkLevel("MenuMain", "金额不合法。");
        return;
    }
    var result = BankManager.withdraw(characterId, amount);
    if (result.get("success")) {
        cm.gainMeso(amount);
        cm.sendOkLevel("MenuMain", "取出成功！获得 #r" + formatMoney(amount) + "#k 金币。");
    } else {
        cm.sendOkLevel("MenuMain", "取出失败：" + result.get("message"));
    }
}

// ==================== 转账（按对方银行账户ID） ====================

function levelTransferAskTarget() {
    cm.getInputTextLevel("TransferTargetEntered", "请输入对方的银行账户ID：");
}

function levelTransferTargetEntered(inputText) {
    var input = (inputText || "").trim();
    var bankId = parseInt(input, 10);
    if (isNaN(bankId) || bankId <= 0) {
        cm.sendOkLevel("MenuMain", "银行账户ID不合法。");
        return;
    }

    var myBankId = BankManager.getAccountId(characterId);
    if (bankId === myBankId) {
        cm.sendOkLevel("MenuMain", "不能转账给自己。");
        return;
    }

    var info = BankManager.getAccountInfo(bankId);
    if (!info.get("success")) {
        cm.sendOkLevel("MenuMain", "未找到该银行账户：" + info.get("message"));
        return;
    }

    transferTargetBankId = bankId;
    transferTargetName = info.get("characterName");

    var balance = BankManager.getBalance(characterId);
    if (balance <= 0) {
        cm.sendOkLevel("MenuMain", "银行账户余额为0，无法转账。");
        return;
    }
    var text = "转账对象：#r" + transferTargetName + "#k（账户：" + transferTargetBankId + "）\r\n"
        + "银行余额：#b" + formatMoney(balance) + "#k\r\n\r\n"
        + "请输入转账金额（手续费10%，将从转出金额中扣除）：";
    cm.getInputNumberLevel("DoTransfer", text, 1, 1, Math.min(balance, MAX_INPUT_AMOUNT));
}

function levelDoTransfer(inputNum) {
    var amount = parseInt(inputNum, 10);
    if (isNaN(amount) || amount <= 0) {
        cm.sendOkLevel("MenuMain", "金额不合法。");
        return;
    }
    if (transferTargetBankId === -1) {
        cm.sendOkLevel("MenuMain", "转账流程异常，请重新操作。");
        return;
    }
    var result = BankManager.transferToBankId(characterId, transferTargetBankId, amount);
    var targetName = transferTargetName;
    transferTargetBankId = -1;
    transferTargetName = "";
    if (result.get("success")) {
        cm.sendOkLevel("MenuMain", "转账成功！\r\n转出：#r" + formatMoney(amount) + "#k 金币\r\n手续费：#r" + formatMoney(result.get("fee")) + "#k 金币\r\n对方【" + targetName + "】实际到账：#r" + formatMoney(result.get("credited")) + "#k 金币");
    } else {
        cm.sendOkLevel("MenuMain", "转账失败：" + result.get("message"));
    }
}

// ==================== 修改密码 ====================

function levelChangePwdAskOld() {
    cm.getInputTextLevel("ChangePwdOldEntered", "请输入当前银行密码（旧密码）：");
}

function levelChangePwdOldEntered(inputText) {
    tempPassword = (inputText || "").trim();
    cm.getInputTextLevel("ChangePwdNewEntered", "请输入新密码（至少4位）：");
}

function levelChangePwdNewEntered(inputText) {
    var newPwd = (inputText || "").trim();
    if (newPwd.length < 4) {
        tempPassword = null;
        cm.sendOkLevel("MenuMain", "新密码长度至少需要4位，请重新操作。");
        return;
    }
    var oldPwd = tempPassword;
    tempPassword = newPwd;
    var result = BankManager.changePassword(characterId, oldPwd, tempPassword);
    tempPassword = null;
    if (result.get("success")) {
        cm.sendOk("密码修改成功！请重新进入银行系统登录。");
    } else {
        cm.sendOkLevel("MenuMain", "密码修改失败：" + result.get("message"));
    }
}

// ==================== 管理员重置密码（无需旧密码，按银行账户ID操作） ====================

function levelAdminResetAskBankId() {
    if (!cm.getPlayer().isGM()) {
        cm.sendOkLevel("MenuMain", "无权限。");
        return;
    }
    cm.getInputTextLevel("AdminResetBankIdEntered", "[管理员]请输入要重置密码的银行账户ID：");
}

function levelAdminResetBankIdEntered(inputText) {
    var input = (inputText || "").trim();
    var bankId = parseInt(input, 10);
    if (isNaN(bankId) || bankId <= 0) {
        cm.sendOkLevel("MenuMain", "银行账户ID不合法。");
        return;
    }
    var info = BankManager.getAccountInfo(bankId);
    if (!info.get("success")) {
        cm.sendOkLevel("MenuMain", "未找到该银行账户：" + info.get("message"));
        return;
    }
    adminResetBankId = bankId;
    var text = "目标账户：#r" + bankId + "#k（户主：" + info.get("characterName") + "）\r\n\r\n"
        + "请输入要设置的新密码（至少4位）：";
    cm.getInputTextLevel("AdminResetPasswordEntered", text);
}

function levelAdminResetPasswordEntered(inputText) {
    var newPwd = (inputText || "").trim();
    if (newPwd.length < 4) {
        adminResetBankId = -1;
        cm.sendOkLevel("MenuMain", "新密码长度至少需要4位，请重新操作。");
        return;
    }
    var bankId = adminResetBankId;
    adminResetBankId = -1;
    var result = BankManager.resetPassword(bankId, newPwd);
    if (result.get("success")) {
        cm.sendOkLevel("MenuMain", "银行账户【" + bankId + "】密码重置成功！");
    } else {
        cm.sendOkLevel("MenuMain", "密码重置失败：" + result.get("message"));
    }
}
//---------------------------------------------------------------------------
var acc = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 美化ne = "#fUI/UIWindow/Quest/icon6/7#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 中条猫 ="━";
var 猫右 =  "★";
var 猫左 =  "★";
var 右 =    "▎";
var 左 =    "▎";
var 下条猫 ="━";
var 猫下右 ="★";
var 猫下左 ="★";
var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/16#";
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 草莓 = "#fUI/GuildMark/Mark/Plant/00003000/1#"; // 红色草莓
var 草莓1 = "#fUI/GuildMark/Mark/Plant/00003000/10#"; // 淡蓝色草莓
var 草莓2 = "#fUI/GuildMark/Mark/Plant/00003000/11#"; // 紫色草莓
var 草莓3 = "#fUI/GuildMark/Mark/Plant/00003000/15#"; // 白色草莓
var 草莓4 = "#fUI/GuildMark/Mark/Plant/00003000/3#"; // 黄色草莓
var 草莓5 = "#fUI/GuildMark/Mark/Plant/00003000/8#"; // 绿色草莓
var 小黄星 = "#fItem/Etc/0427/04270001/Icon9/0#";  //
var 彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";
var 大黄星 = "#fItem/Etc/0427/04270001/Icon9/1#";  //
var 小兔 = "#fEffect/CharacterEff/1112960/3/0#";  //邪恶小兔 【小】
var 小水滴 = "#fItem/Etc/0427/04270001/Icon10/5#";  //
var 大水滴 = "#fItem/Etc/0427/04270001/Icon10/4#";  //
var 红爱心 ="#fEffect/CharacterEff/1112905/0/1#";
var 金币图标 = "#fUI/UIWindow.img/QuestIcon/7/0#";
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";