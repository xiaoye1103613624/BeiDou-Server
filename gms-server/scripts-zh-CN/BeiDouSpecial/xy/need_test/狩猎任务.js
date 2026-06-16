/*
 * 
 * @WNMS
 * 每日随机任务npc
 * 消灭随机怪物
 */



var status = 0;

var 黑水晶 = 4021008;

var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";

var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";

var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";

var 美化new = "#fUI/UIWindow/Quest/icon5/1#";

var 感叹号 = "#fUI/UIWindow/Quest/icon0#";

var 感叹号2 = "#fUI/UIWindow/Quest/icon1#";

var 奖励 = "#fUI/UIWindow/Quest/reward#";

var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";

var 任务描述 = "#fUI/UIWindow/Quest/summary#"

var 忠告 = "#k温馨提示：任何非法程序和外挂封号处理.封杀侥幸心理.";

var 几率获得 = "#fUI/UIWindow/Quest/prob#";

var 无条件获得 = "#fUI/UIWindow/Quest/basic#";

var 第一关几率获得 = "#v4001038# = 1 #v4001039# = 1 #v4001040# = 1 #v4001041# = 1 #v4001042# = 1 #v4001043# = 1 ";

var 第一关无条件获得 = " #v4001254# ";

var 数二 = 200;
var 数三 = 300;
var 数四 = 500;

var 数五 = 800;

function start() {
    
status = -1;
    
action(1, 0, 0);
}

var qd = "#v1142000# #v2001000# #v2022448# #v2022252# #v2022484# #v2040308# #v3012003#";

function action(mode, type, selection) {
    
if (mode == -1) {
        
cm.dispose();
    
} else {
        
if (status >= 0 && mode == 0) {
            
cm.dispose();
            
return;
        }
        
if (mode == 1)
            
status++;
        
else
            
status--;
        
if (status == 0) {   
var 怪物数量 = Math.floor(Math.random()  *150+50); 
var 怪物随机 = Math.floor(Math.random() * 38+1);         
if (cm.getBossLog('sk123') >0 ) {                
cm.sendOk("#b本任务每天可以领取1次哦");                
cm.dispose(); 

} else {   
var 怪物ID = "4000000";                         
//-------------------------- 30 ----------------------------------
if (cm.getLevel() > 1 ) {      
if (怪物随机 == 1) {                   
var 怪物ID = "4000000"; //红飞龙
} else if (怪物随机 == 2) {var 怪物ID = "4000019";// 
} else if (怪物随机 == 2) {var 怪物ID = "4000016";// 
} else if (怪物随机 == 3) {var 怪物ID = "4000019";// 
} else if (怪物随机 == 4) {var 怪物ID = "4000019";// 
} else if (怪物随机 == 5) {var 怪物ID = "4000019"; // 绿蜗牛
} else if (怪物随机 == 6) {var 怪物ID = "4000003"; // 树枝
} else if (怪物随机 == 7) {var 怪物ID = "4000012"; // 绿蘑菇盖
} else if (怪物随机 == 8) {var 怪物ID = "4000015"; // 刺蘑菇盖
} else if (怪物随机 == 9) {var 怪物ID = "4000009"; // 蓝蘑菇盖
} else if (怪物随机 == 10) {var 怪物ID = "4000001"; // 花蘑菇盖
} else if (怪物随机 == 11) {var 怪物ID = "4000011"; // 蘑菇芽孢
} else if (怪物随机 == 12) {var 怪物ID = "4000010"; // 绿水灵珠
} else if (怪物随机 == 13) {var 怪物ID = "4000037"; // 蓝水灵大水珠	
} else if (怪物随机 == 14) {var 怪物ID = "4000042"; // 蝙蝠翅膀
} else if (怪物随机 == 15) {var 怪物ID = "4000004"; // 绿水灵球
} else if (怪物随机 == 16) {var 怪物ID = "4000002"; // 蝴蝶结
} else if (怪物随机 == 17) {var 怪物ID = "4000005"; // 叶子
} else if (怪物随机 == 18) {var 怪物ID = "4000006"; // 三眼章鱼触角	
} else if (怪物随机 == 19) {var 怪物ID = "4000007"; // 火独眼兽之尾
} else if (怪物随机 == 20) {var 怪物ID = "4000008"; // 道符
} else if (怪物随机 == 21) {var 怪物ID = "4000013"; // 风独眼兽之尾
} else if (怪物随机 == 22) {var 怪物ID = "4000014"; // 龙的头骨
} else if (怪物随机 == 23) {var 怪物ID = "4000017"; // 猪头
} else if (怪物随机 == 24) {var 怪物ID = "4000018"; // 木块
} else if (怪物随机 == 25) {var 怪物ID = "4000020"; // 野猪尖牙
} else if (怪物随机 == 26) {var 怪物ID = "4000021"; // 动物皮
} else {                 
var 怪物ID = "4000017"; // 猪头
} 
//-------------------------------  70  ----------------------------------------
} else if (cm.getLevel() > 70) {  
 if (怪物随机 == 1) {var 怪物ID = "4000022"; // 石块	
} else if (怪物随机 == 2) {var 怪物ID = "4000023"; // 冰独眼兽之尾
} else if (怪物随机 == 3) {var 怪物ID = "4000024"; // 火野猪尖牙
} else if (怪物随机 == 4) {var 怪物ID = "4000025"; // 黑石块	
} else if (怪物随机 == 5){var 怪物ID = "4000026"; // 猴子娃娃	
} else if (怪物随机 == 6) {var 怪物ID = "4000027"; // 怪猫的眼	
} else if (怪物随机 == 7) {var 怪物ID = "4000028"; // 月牙牛魔王的角	
} else if (怪物随机 == 8) {var 怪物ID = "4000029"; // 香蕉
} else if (怪物随机 == 9) {var 怪物ID = "4000030"; // 龙皮	
} else if (怪物随机 == 10){var 怪物ID = "4000031"; // 诅咒娃娃	
} else if (怪物随机 == 11) {var 怪物ID = "4000032"; // 鳄鱼皮	
} else if (怪物随机 == 12){var 怪物ID = "4000033"; // 黑鳄鱼皮	
} else if (怪物随机 == 13) {var 怪物ID = "4000034"; // 蛇皮
} else if (怪物随机 == 14) {var 怪物ID = "4000035"; // 桌布
} else if (怪物随机 == 15) {var 怪物ID = "4000036"; // 奇妙的药	
} else if (怪物随机 == 16) {var 怪物ID = "4000039"; // 铁甲猪蹄	
} else if (怪物随机 == 18) {var 怪物ID = "4000041"; // 巫婆的试验用青蛙	
} else if (怪物随机 == 19) {var 怪物ID = "4000043"; // 红螃蟹钳	
} else if (怪物随机 == 20) {var 怪物ID = "4000044"; // 青螃蟹钳	
} else if (怪物随机 == 21) {var 怪物ID = "4000045"; // 乌龟壳	
} else if (怪物随机 == 22) {var 怪物ID = "4000046"; // 长枪牛魔王之角	
} else if (怪物随机 == 23) {var 怪物ID = "4000048"; // 小白雪人皮		
} else if (怪物随机 == 24) {var 怪物ID = "4000050"; // 企鹅王的嘴		
} else if (怪物随机 == 25) {var 怪物ID = "4000051"; // 野狼之尾		
} else if (怪物随机 == 26) {var 怪物ID = "4000052"; // 白狼之尾		
} else if (怪物随机 == 27) {var 怪物ID = "4000053"; // 狼人脚趾甲		
} else if (怪物随机 == 28) {var 怪物ID = "4000054"; // 白狼人脚趾甲		
} else if (怪物随机 == 29) {var 怪物ID = "4000055"; // 小黑雪人皮		
} else if (怪物随机 == 30) {var 怪物ID = "4000058"; // 食人花的种子		
} else if (怪物随机 == 31) {var 怪物ID = "4000059"; // 星光精灵的星块		
} else if (怪物随机 == 32) {var 怪物ID = "4000060"; // 月光精灵的月块		
} else if (怪物随机 == 33) {var 怪物ID = "4000061"; // 日光精灵的日块		
} else if (怪物随机 == 34) {var 怪物ID = "4000062"; // 黑食人花的种子		
} else if (怪物随机 == 35) {var 怪物ID = "4000063"; // 石片		
} else if (怪物随机 == 36) {var 怪物ID = "4000069"; // 僵尸丢失的臼齿		
} else {                 
var 怪物ID = "4000052"; // 白狼之尾	
} 
//-------------------------------  120  ----------------------------------------
} else if (cm.getLevel() > 120) { 
 if (怪物随机 == 1) {var 怪物ID = "4000070"; // 红独角狮尾		
} else if (怪物随机 == 2) {var 怪物ID = "4000071"; // 黄独角狮尾		
} else if (怪物随机 == 3) {var 怪物ID = "4000072"; // 蓝独角狮尾		
} else if (怪物随机 == 4) {var 怪物ID = "4000073"; // 独角狮硬角		
} else if (怪物随机 == 5) {var 怪物ID = "4000074"; // 黑色飞狮尾		
} else if (怪物随机 == 6) {var 怪物ID = "4000078"; // 小猎犬的尖牙		
} else if (怪物随机 == 7) {var 怪物ID = "4000079"; // 猎犬的尖牙		
} else if (怪物随机 == 8) {var 怪物ID = "4000080"; // 火焰猎犬的项链		
} else if (怪物随机 == 9) {var 怪物ID = "4000083"; // 小石球的石片		
} else if (怪物随机 == 10) {var 怪物ID = "4000084"; // 冰石球的石片		
} else if (怪物随机 == 11) {var 怪物ID = "4000085"; // 火石球的石片		
} else if (怪物随机 == 12) {var 怪物ID = "4000106"; // 玩具熊猫的棉花团		
} else if (怪物随机 == 13) {var 怪物ID = "4000107"; // 玩具熊猫的黄色丝带		
} else if (怪物随机 == 14) {var 怪物ID = "4000108"; // 熊猫娃娃		
} else if (怪物随机 == 15) {var 怪物ID = "4000111"; // 便宜的电池		
} else if (怪物随机 == 16) {var 怪物ID = "4000112"; // 机器心脏	
} else if (怪物随机 == 17) {var 怪物ID = "4000114"; // 小桌表		
} else if (怪物随机 == 18) {var 怪物ID = "4000115"; // 齿轮		
} else if (怪物随机 == 19) {var 怪物ID = "4000143"; // 僵尸娃娃		
} else if (怪物随机 == 20) {var 怪物ID = "4000157"; // 海豹肉		
} else if (怪物随机 == 21) {var 怪物ID = "4000150"; // 时间鬼王的冰块		
} else if (怪物随机 == 22) {var 怪物ID = "4000156"; // 海象尖牙		
} else if (怪物随机 == 23) {var 怪物ID = "4000169"; // 捣米棒		
} else if (怪物随机 == 24) {var 怪物ID = "4000177"; // 混种石块		
} else if (怪物随机 == 25) {var 怪物ID = "4000172"; // 三尾狐的尾巴		
} else if (怪物随机 == 26) {var 怪物ID = "4000171"; // 虎皮	
} else if (怪物随机 == 27) {var 怪物ID = "4000170"; // 老虎脚印		
} else if (怪物随机 == 28) {var 怪物ID = "4000180"; // 鲨鱼假牙		
} else if (怪物随机 == 29) {var 怪物ID = "4000181"; // 冷冻鱼翅			
} else if (怪物随机 == 30) {var 怪物ID = "4000229"; // 黑暗莱西毛球			
} else if (怪物随机 == 31) {var 怪物ID = "4000227"; // 树果实		
} else if (怪物随机 == 32) {var 怪物ID = "4000226"; // 莱西毛球		
} else if (怪物随机 == 33) {var 怪物ID = "4000239"; // 血腥哈维的王冠		
} else if (怪物随机 == 34) {var 怪物ID = "4000238"; // 哈维羽毛		
} else if (怪物随机 == 35) {var 怪物ID = "4000237"; // 金属甲虫角		
} else if (怪物随机 == 36) {var 怪物ID = "4000236"; // 橡木甲虫角		
} else if (怪物随机 == 37) {var 怪物ID = "4000234"; // 半人马的骨头		
} else if (怪物随机 == 38) {var 怪物ID = "4000233"; // 半人马的净水		
} else if (怪物随机 == 39) {var 怪物ID = "4000232"; // 半人马的火花	
} else {                 
var 怪物ID = "4000234"; // 半人马的骨头	
//} 

} 
}  
cm.sendOk("" + 任务描述 + "\r\n需要：#b#i" + 怪物ID + "##k。怪物数量:#r" + 怪物数量 + "#k");            
                cm.getPlayer().gain怪物ID1(""+怪物ID+"");
                cm.getPlayer().gain怪物数量1(""+怪物数量+"");
                            
cm.dispose();            }        }    }}