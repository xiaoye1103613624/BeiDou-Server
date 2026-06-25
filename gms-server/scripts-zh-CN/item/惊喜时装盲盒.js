/**
 * 惊喜时装盲盒（消耗品，itemId=2431144，复用项目预留的"500积分材料礼包"WZ节点改造而来）
 * 使用后随机获得一件现金时装/装备；
 * 有 HAS_STAT_RATE 概率获得"有属性"版本（调用 gainItem 的随机属性参数），否则获得"无属性"白板版本。
 * 抽到有属性版本时，进行全服广播。
 *
 * WZ 关联：wz-zh-CN/Item.wz/Consume/0243.img.xml 的 02431144 节点 spec/script="惊喜时装盲盒"，
 * 对应本脚本文件名；图标暂复用 02432343 的现成图标(outlink)，可后续替换为专属图标。
 * POOL 数组中的现金装备 itemId 为占位示例，请替换为本服现金商城实际在用的时装/装备 itemId。
 */

// 触发该脚本的消耗道具ID（对应 WZ 节点 02431144）
var ITEM_ID = 2431144;

// 抽奖奖池：weight 越大越容易抽到；rare=true 的物品一旦抽中，无论是否有属性都会全服广播
var POOL = [
    { id: 1003800, weight: 50, name: "占位：现金帽子", rare: false },
    { id: 1043800, weight: 50, name: "占位：现金上衣", rare: false },
    { id: 1053800, weight: 30, name: "占位：现金套服", rare: false },
    { id: 1063800, weight: 30, name: "占位：现金裤子", rare: false },
    { id: 1073800, weight: 10, name: "占位：现金鞋子（稀有）", rare: true }
];

// 抽到"有属性"版本的概率（千分制，150 = 15%）
var HAS_STAT_RATE = 150;
var RATE_BASE = 1000;

// 按 weight 加权随机抽取奖池中的一项
function pickWeighted(pool) {
    var totalWeight = 0;
    for (var i = 0; i < pool.length; i++) {
        totalWeight += pool[i].weight;
    }
    var roll = Math.random() * totalWeight;
    for (var j = 0; j < pool.length; j++) {
        roll -= pool[j].weight;
        if (roll < 0) {
            return pool[j];
        }
    }
    return pool[pool.length - 1];
}

function start() {
    // 优先校验道具数量，避免道具不足时误扣
    if (!im.haveItem(ITEM_ID, 1)) {
        im.getPlayer().dropMessage(5, "盲盒道具数量不足。");
        im.dispose();
        return;
    }

    // 消耗1个盲盒道具
    im.gainItem(ITEM_ID, -1);

    // 从奖池中按权重随机抽取一件现金装备
    var won = pickWeighted(POOL);
    var wonItemId = won.id;

    // 判定本次是否抽到"有属性"版本
    var hasStat = Math.floor(Math.random() * RATE_BASE) < HAS_STAT_RATE;

    // gainItem 的第三个参数 randomStats=true 时会调用 ItemInformationProvider.randomizeStats 生成随机属性
    im.gainItem(wonItemId, 1, hasStat, true);

    // 抽到稀有物品(rare=true)或抽到带属性版本，任一满足即全服广播
    if (won.rare || hasStat) {
        var text = "[惊喜时装盲盒] " + im.getPlayer().getName() + " 抽中了" + won.name +
            (hasStat ? "（带属性）" : "") + "！恭喜！！";
        im.getPlayer().getWorldServer().broadcastPacket(PacketCreator.serverNotice(6, text));
    } else {
        im.getPlayer().dropMessage(6, "恭喜获得一件时装（无属性白板）。");
    }

    im.dispose();
}
