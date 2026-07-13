/**
 * @description 职业群工具方法
 *   根据角色 jobId 返回职业群编号，供其他 JS 通过 load() 引用
 *
 * 职业群编号（按约定顺序）：
 *   0 = 新手（含贵族、传说等）
 *   1 = 战士（含圣魂、战神）
 *   2 = 弓箭手（含风灵）
 *   3 = 法师（含炎术、龙神）
 *   4 = 飞侠（含夜行）
 *   5 = 海盗（含雷击）
 *
 * 用法示例：
 *   load("scripts-zh-CN/BeiDouSpecial/xy/职业群.js");
 *   var group = getJobGroup(chr.getJob().getId());  // chr 为玩家对象
 *   var name  = getJobGroupName(group);
 */

// ==================== 职业群常量 ====================
var JOB_GROUP = {
    BEGINNER: 0,  // 新手
    WARRIOR:  1,  // 战士
    ARCHER:   2,  // 弓箭手
    MAGICIAN: 3,  // 法师
    THIEF:    4,  // 飞侠
    PIRATE:   5   // 海盗
};

// ==================== 职业群中文名 ====================
var JOB_GROUP_NAME = {};
JOB_GROUP_NAME[JOB_GROUP.BEGINNER] = "新手";
JOB_GROUP_NAME[JOB_GROUP.WARRIOR]  = "战士";
JOB_GROUP_NAME[JOB_GROUP.ARCHER]   = "弓箭手";
JOB_GROUP_NAME[JOB_GROUP.MAGICIAN] = "法师";
JOB_GROUP_NAME[JOB_GROUP.THIEF]    = "飞侠";
JOB_GROUP_NAME[JOB_GROUP.PIRATE]   = "海盗";

/**
 * 根据 jobId 获取职业群编号 0~5
 * 兼容冒险家、骑士团(1100~)、战神(2100~)、龙神(2200~) 等分支
 * @param {number} jobId - 角色职业ID
 * @returns {number} 职业群编号（0=新手 1=战士 2=弓箭手 3=法师 4=飞侠 5=海盗）
 */
function getJobGroup(jobId) {
    // 新手职业：0, 1000(贵族), 2000(传说), 800(枫叶旅团), 900(GM)
    if (jobId === 0 || jobId === 1000 || jobId === 2000 || jobId === 800 || jobId === 900 || jobId === 910) {
        return JOB_GROUP.BEGINNER;
    }

    // 用 (jobId / 100) % 10 提取职业系，再按约定映射
    var rawGroup = Math.floor(jobId / 100) % 10;

    // 内部原始分组 → 约定分组映射
    // 原始: 0=新手 1=战士 2=法师 3=弓箭手 4=飞侠 5=海盗
    // 约定: 0=新手 1=战士 2=弓箭手 3=法师 4=飞侠 5=海盗（即交换 2 和 3）
    var mapping = {};
    mapping[0] = JOB_GROUP.BEGINNER;
    mapping[1] = JOB_GROUP.WARRIOR;
    mapping[2] = JOB_GROUP.MAGICIAN;  // 原始法师 → 约定3
    mapping[3] = JOB_GROUP.ARCHER;    // 原始弓箭手 → 约定2
    mapping[4] = JOB_GROUP.THIEF;
    mapping[5] = JOB_GROUP.PIRATE;

    return mapping[rawGroup] !== undefined ? mapping[rawGroup] : JOB_GROUP.BEGINNER;
}

/**
 * 获取职业群中文名
 * @param {number} groupId - 职业群编号（0~5）
 * @returns {string} 中文名称
 */
function getJobGroupName(groupId) {
    return JOB_GROUP_NAME[groupId] || "未知";
}

/**
 * 根据 jobId 直接获取职业群中文名（便捷方法）
 * @param {number} jobId - 角色职业ID
 * @returns {string} 中文名称
 */
function getJobGroupNameByJobId(jobId) {
    return getJobGroupName(getJobGroup(jobId));
}

// ==================== 快捷判断方法 ====================

/** 是否为新手 */
function isBeginner(jobId) { return getJobGroup(jobId) === JOB_GROUP.BEGINNER; }

/** 是否为战士（含圣魂、战神） */
function isWarrior(jobId) { return getJobGroup(jobId) === JOB_GROUP.WARRIOR; }

/** 是否为弓箭手（含风灵） */
function isArcher(jobId) { return getJobGroup(jobId) === JOB_GROUP.ARCHER; }

/** 是否为法师（含炎术、龙神） */
function isMagician(jobId) { return getJobGroup(jobId) === JOB_GROUP.MAGICIAN; }

/** 是否为飞侠（含夜行） */
function isThief(jobId) { return getJobGroup(jobId) === JOB_GROUP.THIEF; }

/** 是否为海盗（含雷击） */
function isPirate(jobId) { return getJobGroup(jobId) === JOB_GROUP.PIRATE; }
