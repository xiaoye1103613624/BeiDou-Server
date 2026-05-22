/*
 * ==================
 * 脚本类型: 玩家商店查找器
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 查看自由市场中开设的玩家商店
 *   2. 按道具名称搜索玩家商店中的商品
 * ==================
 */

var status = -1;
var Server = Java.type('org.gms.server.Server');
var searchResults = [];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0 && status === 0) {
        cm.dispose();
        return;
    }

    status++;

    if (status === 0) {
        var text = "#e#b=== 玩家商店查找器 ===#k#n\r\n\r\n";
        var fmPlayers = getFreeMarketPlayers();
        text += "自由市场当前玩家数：#b" + fmPlayers.length + "#k\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

        if (fmPlayers.length > 0) {
            for (var i = 0; i < fmPlayers.length; i++) {
                text += "#b" + fmPlayers[i] + "#k";
                if (i < fmPlayers.length - 1) text += ", ";
            }
            text += "\r\n\r\n";
        }

        text += "#L0##b按道具名称搜索#k#l\r\n";
        text += "#L1##b传送到自由市场#k#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            cm.sendGetText("请输入要搜索的道具名称：");
        } else if (selection === 1) {
            cm.getPlayer().saveLocation("FREE_MARKET");
            cm.warp(910000000, "out00");
            cm.dispose();
        }
    } else if (status === 2) {
        var keyword = cm.getText();
        searchInShops(keyword);
    }
}

function getFreeMarketPlayers() {
    var players = [];
    try {
        var worlds = Server.getInstance().getWorlds();
        for (var w = 0; w < worlds.size(); w++) {
            var channels = worlds.get(w).getChannels();
            for (var c = 0; c < channels.size(); c++) {
                var allPlayers = channels.get(c).getPlayerStorage().getAllCharacters().toArray();
                for (var p = 0; p < allPlayers.length; p++) {
                    var mapId = allPlayers[p].getMapId();
                    // 自由市场地图ID范围: 910000000-910000009
                    if (mapId >= 910000000 && mapId <= 910000009) {
                        players.push(allPlayers[p].getName());
                    }
                }
            }
        }
    } catch (e) {}
    return players;
}

function searchInShops(keyword) {
    var text = "#e#b=== 搜索结果 ===#k#n\r\n\r\n";
    text += "搜索关键字：#b" + keyword + "#k\r\n\r\n";

    var foundCount = 0;
    try {
        var worlds = Server.getInstance().getWorlds();
        for (var w = 0; w < worlds.size(); w++) {
            var channels = worlds.get(w).getChannels();
            for (var c = 0; c < channels.size(); c++) {
                var allPlayers = channels.get(c).getPlayerStorage().getAllCharacters().toArray();
                for (var p = 0; p < allPlayers.length; p++) {
                    var player = allPlayers[p];
                    var mapId = player.getMapId();
                    if (mapId >= 910000000 && mapId <= 910000009) {
                        // 检查玩家商店
                        var shop = player.getPlayerShop();
                        if (shop != null) {
                            var shopItems = shop.getItems();
                            for (var s = 0; s < shopItems.length; s++) {
                                var item = shopItems[s];
                                var itemId = item.getItemId();
                                var ItemInformationProvider = Java.type('org.gms.provider.ItemInformationProvider').getInstance();
                                var itemName = ItemInformationProvider.getName(itemId);
                                if (itemName != null && itemName.toLowerCase().indexOf(keyword.toLowerCase()) >= 0) {
                                    foundCount++;
                                    if (foundCount <= 20) {
                                        text += "#i" + itemId + "# #b" + itemName + "#k x" + item.getQuantity() + "  ";
                                        text += "#r" + item.getPrice().toLocaleString() + "金币#k  ";
                                        text += "(卖家: " + player.getName() + ")\r\n";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } catch (e) {
        text += "搜索过程出现异常。\r\n";
    }

    if (foundCount === 0) {
        text += "未找到匹配的商品。\r\n";
        text += "提示：请前往自由市场(#b910000000#k)直接浏览玩家商店。\r\n";
    } else if (foundCount > 20) {
        text += "... 仅显示前20个结果 (共" + foundCount + "个)\r\n";
    }

    cm.sendOk(text);
    cm.dispose();
}
