/*
 * ´Ë½Å±¾ÓÉÀÖÕÂÍøÂçÖÆ×÷Íê³É
 */

// ¶¨ÒåÃÀ»¯±äÁ¿
var ´«ËÍÖĞĞÄ = "#fEffect/CharacterEff1.img/QQ1408745/0/6#";
var ÎÏÅ£Íõ = "#fUI/UIWindow.img/MobGage/Mob/2220000#";
var Ä¢¹½Íõ = "#fUI/UIWindow.img/MobGage/Mob/6130101#";
var À¶Ä¢¹½Íõ = "#fUI/UIWindow.img/MobGage/Mob/9400205#";
var ½©Ä¢¹½Íõ = "#fUI/UIWindow.img/MobGage/Mob/6300005#";
var Ê÷ÑıÍõ = "#fUI/UIWindow.img/MobGage/Mob/3220000#";
var ÏÉÈËÕÆ = "#fUI/UIWindow.img/MobGage/Mob/3220001#";
var ±´¿Ç¾« = "#fUI/UIWindow.img/MobGage/Mob/4220000#";
var ½©Ê¬ºï = "#fUI/UIWindow.img/MobGage/Mob/5220002#";
var Ã¨Í·Ó¥ = "#fUI/UIWindow.img/MobGage/Mob/5220003#";
var ±´¿ÇÍõ = "#fUI/UIWindow.img/MobGage/Mob/5220000#";
var Á÷ÀËĞÜ = "#fUI/UIWindow.img/MobGage/Mob/7220000#";
var °¬Á¦½Ü = "#fUI/UIWindow.img/MobGage/Mob/8220000#";
var ¼ªÃ×À² = "#fUI/UIWindow.img/MobGage/Mob/8220002#";
var ´óÃîÏÉ = "#fUI/UIWindow.img/MobGage/Mob/7220002#";
var ¾ÅÎ²ºü = "#fUI/UIWindow.img/MobGage/Mob/7220001#";
var Åç»ğÁú = "#fUI/UIWindow.img/MobGage/Mob/8180000#";
var ¸ñÈğ·Ò = "#fUI/UIWindow.img/MobGage/Mob/8180001#";
var òùòğ¹Ö = "#fUI/UIWindow.img/MobGage/Mob/8130100#";
var ´ó¶à¶û = "#fUI/UIWindow.img/MobGage/Mob/6220000#";
var º£ÊŞ = "#fUI/UIWindow.img/MobGage/Mob/8220003#";
var ¶à¶à = "#fUI/UIWindow.img/MobGage/Mob/8220004#";
var ¶À½ÇÊŞ = "#fUI/UIWindow.img/MobGage/Mob/8220005#";
var À×¿¨ = "#fUI/UIWindow.img/MobGage/Mob/8220006#";
var ÓãÍõ = "#fUI/UIWindow.img/MobGage/Mob/8510000#";
var Ê÷¾« = "#fUI/UIWindow.img/MobGage/Mob/9420521#";
var ÑıÉ® = "#fUI/UIWindow.img/MobGage/Mob/9600025#";
var Æ·¿Ë±ö = "#fUI/UIWindow.img/MobGage/Mob/8820001#";
var ºÚÁú = "#fUI/UIWindow.img/MobGage/Mob/8810018#";
var ÄÖÖÓ = "#fUI/UIWindow.img/MobGage/Mob/8500001#";
var ĞÜÊ¨ = "#fUI/UIWindow.img/MobGage/Mob/9420542#";
var ÔúÀ¥ = "#fUI/UIWindow.img/MobGage/Mob/8800001#";

// ¶¨ÒåÊÀ½çBOSSµØÍ¼Êı×é
var bossmaps = [
    [ÎÏÅ£Íõ, "[ÎÏ Å£ Íõ]", 104000400, 1000, 2220000],
    [Ä¢¹½Íõ, "[Ä¢ ¹½ Íõ]", 100000005, 1000, 6130101],
    [À¶Ä¢¹½Íõ, "[À¶Ä¢¹½Íõ]", 800010100, 1000, 9400205],
    [½©Ä¢¹½Íõ, "[½©Ä¢¹½Íõ]", 105070002, 1000, 6300005],
    [Ê÷ÑıÍõ, "[Ê÷ Ñı Íõ]", 101030404, 1000, 3220000],
    [ÏÉÈËÕÆ, "[ÏÉ ÈË ÕÆ]", 260010201, 1000, 3220001],
    [±´¿Ç¾«, "[±´ ¿Ç ¾«]", 230020100, 1000, 4220000],
    [½©Ê¬ºï, "[½© Ê¬ ºï]", 100040106, 1000, 5220002],
    [Ã¨Í·Ó¥, "[Ã¨ Í· Ó¥]", 220050100, 1000, 5220003],
    [±´¿ÇÍõ, "[±´ ¿Ç Íõ]", 110040000, 1000, 5220001],
    [Á÷ÀËĞÜ, "[Á÷ ÀË ĞÜ]", 250010304, 1000, 7220000],
    [°¬Á¦½Ü, "[°¬ Á¦ ½Ü]", 200010300, 1000, 8220000],
    [¼ªÃ×À², "[¼ª Ã× À²]", 261030000, 1000, 8220002],
    [´óÃîÏÉ, "[´ó Ãî ÏÉ]", 250010503, 1000, 7220002],
    [¾ÅÎ²ºü, "[¾Å Î² ºü]", 222010310, 1000, 7220001],
    [Åç»ğÁú, "[Åç »ğ Áú]", 240020401, 2000, 8180000],
    [¸ñÈğ·Ò, "[¸ñ Èğ ·Ò]", 240020101, 2000, 8180001],
    [òùòğ¹Ö, "[òù òğ ¹Ö]", 105090900, 2000, 8130100],
    [´ó¶à¶û, "[´ó ¶à ¶û]", 107000300, 2000, 6220000],
    [º£ÊŞ, "[ º£  ÊŞ ]", 240040401, 2000, 8220003],
    [¶à¶à, "[ ¶à  ¶à ]", 270010500, 2000, 8220004],
    [¶À½ÇÊŞ, "[¶À ½Ç ÊŞ]", 270020500, 2000, 8220005],
    [À×¿¨, "[ À×  ¿¨ ]", 270030500, 2000, 8220006],
    [ÓãÍõ, "[ Óã  Íõ ]", 230040420, 500000, 8510000],
    [Ê÷¾«, "[ Ê÷  ¾« ]", 541020700, 500000, 9420521],
    [ÑıÉ®, "[ Ñı  É® ]", 702070400, 500000, 9600025],
    [ÄÖÖÓ, "[ ÄÖ  ÖÓ ]", 220080000, 500000, 8500001],
    [ĞÜÊ¨, "[ ĞÜ  Ê¨ ]", 551030100, 500000, 9420542],
    [ÔúÀ¥, "[ Ôú  À¥ ]", 211042300, 500000, 8800001],
    [ºÚÁú, "[ ºÚ  Áú ]", 240050400, 500000, 8810018],
    [Æ·¿Ë±ö, "[Æ· ¿Ë ±ö]", 270050000, 500000, 8820001]
];

// ½Å±¾Ö÷Ìå
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        if (status == 0) {
            var add = "";
				add += "\t\t\t" + ´«ËÍÖĞĞÄ + "\r\n";
            for (var i = 0; i < bossmaps.length; i++) {
                var map = bossmaps[i];
                var mobId = map[4];
                var mapId = map[2];
                var mobExists = isMonsterInMap(mobId, mapId);
                var color = mobExists ? "#g" : "#r";
                add += "#L" + i + "#" + map[0] + color + map[1] + "#k#l";
                if ((i + 1) % 3 === 0) {
                    add += "\r\n\r\n";
                } else {
                    add += "";
                }
            }
            cm.sendSimple(add);
        } else if (status == 1) {
            var map = bossmaps[selection];
            if (cm.getMeso() >= map[3]) {
                cm.warp(map[2], 0);
                cm.gainMeso(-map[3]);
            } else {
                cm.sendOk("ÄãÃ»ÓĞ×ã¹»µÄ½ğ±ÒÅ¶!");
            }
            cm.dispose();
        }
    }
}

// ¼ì²é¹ÖÎïÊÇ·ñÔÚµØÍ¼ÖĞ
function isMonsterInMap(mobId, mapId) {
    var map = cm.getMap(mapId);
    if (!map) return false;
    var monsters = map.getAllMonstersThreadsafe();
    if (!monsters) return false;
    for (var i = 0; i < monsters.size(); i++) {
        if (monsters.get(i).getId() == mobId) {
            return true;
        }
    }
    return false;
}