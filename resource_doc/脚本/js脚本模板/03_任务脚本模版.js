/* ============================================================
 * 脚本类型: 任务脚本
 * 模版说明: 处理任务接取(start)和完成(end)的对话逻辑
 * 文件命名: 任务ID.js（如 20000.js）
 *
 * 调用链路:
 *   QuestActionManager.startQuest(questId) → start(mode, type, selection)
 *   QuestActionManager.completeQuest(questId) → end(mode, type, selection)
 *
 * 全局变量:
 *   qm = QuestActionManager 实例
 *       (API 与 NPC 脚本的 cm 类似，但增加了任务专用方法)
 *
 * mode 参数:
 *   -1 = 玩家点"结束聊天"
 *    0 = 玩家点"上一步"
 *    1 = 玩家点"下一步"
 *
 * type 参数:
 *    0 = 普通点击
 *    1 = 选项选择(对应 sendSimple/sendYesNo 等)
 * ============================================================ */

var status = -1;

/* ===== 接取任务 ===== */
function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
        return;
    }
    if (mode == 0 && type > 0) {
        qm.dispose();
        return;
    }

    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        /* ----- 接取任务对话 ----- */
        qm.sendNext("你好，冒险家！我需要你的帮助。#b打倒10只蓝蜗牛#k，然后回来找我。");

    } else if (status == 1) {
        qm.sendYesNo("你愿意接受这个任务吗？");

    } else if (status == 2) {
        if (selection == 0) {
            /* 强制接取任务（无视前置条件） */
            qm.forceStartQuest();
            qm.sendOk("太好了！去#b射手村训练场#k找我说的怪物吧。");
        } else {
            qm.sendOk("好吧，等你准备好了再来找我。");
        }
        qm.dispose();
    }
}

/* ===== 完成任务 ===== */
function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
        return;
    }
    if (mode == 0 && type > 0) {
        qm.dispose();
        return;
    }

    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        /* ----- 交任务对话 ----- */
        qm.sendNext("你回来了！让我看看... 干得漂亮！你确实打倒了不少蓝蜗牛。");

    } else if (status == 1) {
        /* 发放奖励 + 完成任务 */
        qm.gainExp(500);
        qm.gainItem(4000000, 5);
        qm.gainMeso(1000);
        qm.forceCompleteQuest();
        qm.sendOk("这是你的奖励，拿好了！");
        qm.dispose();
    }
}

/* ============================================================
 * 【任务脚本 qm 专用方法】
 *
 * ---- 任务控制 ----
 * qm.forceStartQuest()
 *     强制接取当前任务
 * qm.forceStartQuest(questId)
 *     强制接取指定任务
 * qm.forceCompleteQuest()
 *     强制完成当前任务
 * qm.forceCompleteQuest(questId)
 *     强制完成指定任务
 * qm.canHold()
 *     检查背包是否有足够空间
 * qm.getQuestRecordEx(key)
 *     获取自定义任务进度值
 *
 * ---- 通用方法（与 cm 共用）----
 * qm.dispose()
 *     结束对话
 * qm.sendNext(text)
 *     显示文本
 * qm.sendOk(text)
 *     显示文本 + 确定按钮
 * qm.sendYesNo(text)
 *     确认弹窗
 * qm.sendSimple(text)
 *     选项菜单
 * qm.gainItem(itemId, qty)
 *     给予/扣除道具
 * qm.gainExp(amount)
 *     给予经验
 * qm.gainMeso(amount)
 *     给予/扣除金币
 * qm.getPlayer()
 *     获取 Character 对象
 * qm.warp(mapId)
 *     传送玩家
 * ============================================================ */
