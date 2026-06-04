load("nashorn:mozilla_compat.js");
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.tools);

var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";
var jmsz = Array(
             
    Array(1051421,2888,"未定义","永久"),//人气模范生连衣裙
	Array(1052586,2888,"未定义","永久")//伶俐猫咪套服
    );


var jmwq = Array(
    Array(1092108,3333,"未定义","永久"),//
    Array(1702627,3333,"未定义","永久")//樱花之刃
    

	
    );  


var jzmp = Array(
    Array(1112150,5333,"未定义","永久"),//童趣降临名片戒指
    Array(1112277,5333,"未定义","永久")//绿光森林聊天戒指
    );  

var tjId = Array(
    Array(3010163,2000,"未定义","10天",60000 * 60 * 24 * 10,1)//满月椅子
    );

var syxh = Array(
    Array(5150040,13500,"未定义",10)//皇家美发卡
	//Array(1112404,3333,"333333","1")//极光戒指
    );  

var tscl = Array(
    Array(4000151,1000,100),//时间门神的轴标
    Array(4001109,1000,5)//强化玻璃瓶
    );  

var xswp = Array(
    //Array(1062054,2388,"未定义","永久"),//
    Array(1062054,2388,"未定义","永久"),//
    Array(1032052,1388,"未定义","永久")//绿水灵耳钉
    //Array(5360015,1000,"1天权",60000 * 60 * 24 * 1,1)
    //Array(5211047,1000,"1天权",60000 * 60 * 24 * 1,1)
    );

var jmpf = Array(
    Array(1102142,2588,"未定义","永久"),
    Array(1102287,1888,"未定义","永久")//晴天娃娃(淡黄色)
    );


var jmmz = Array(
    Array(1004530,2666,"未定义","永久"),//蓝色熊猫玩偶帽子
    Array(1003595,2666,"未定义","永久")//卷发兔子
    );

var jmsp = Array(
	Array(1082102,1,"未定义","永久"),//透明手套
	Array(1002186,2,"未定义","永久"),//透明帽
	Array(1012289,3,"未定义","永久"),//透明面饰品
	Array(1022048,4,"未定义","永久"),//透明眼饰
	Array(1032024,5,"未定义","永久"),//透明耳环
	Array(1102039,6,"未定义","永久"),//透明披风
	Array(1072153,7,"未定义","永久"),//透明鞋
	Array(1802100,8,"未定义","永久"),//宠物项圈
	Array(1902409,9,"未定义","永久"),//粉色凤凰
	Array(1912409,10,"未定义","永久"),//粉色凤凰鞍子
	Array(1902412,10,"未定义","永久"),//梦魔 
	Array(1912412,10,"未定义","永久"),//梦魔鞍子
	Array(1902024,10,"未定义","永久"),//天马 
	Array(1912017,10,"未定义","永久")//天马鞍子
    );

var qtwp = Array(
 
    );




var ns = Array(
    
    Array(1061067,3222,"未定义","永久")//热裤
    );

var qltz = Array(
    
    Array(1042337,2121,"未定义","永久")//熊熊春游T
    );

var stxz = Array(
     
    Array(1082511,2828,"未定义","永久"),//小恐龙绿豆手套
    
    Array(1072448,2828,"未定义","永久")//皇家彩虹鞋
    );

var pets = Array(
     Array(5000077,9999,"未定义","90天"),//波斯猫
    
	 Array(5000268,9999,"未定义","90天")//甜心蝴蝶
);
	
	
var status = -1;
var xx = -1;
var jiage = -1;

function start() {
    action(1, 0, 0);
}


function action(mode, type, selection) {
    //cm.getPlayer().dropMessage("mode ： "+ mode + " status : "+  status + " sel: " + selection);
    if (mode == -1) {
        cm.sendOk("#b好的,下次再见.");
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendOk("#b好的,下次再见.");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }

        //---------------------------------------------------------------------------------

        if (status == 0) {
            if (selection == 0) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                //add += "   您当前位置:#b商城首页>>属性时装商城#k\r\n\r\n";
				add += "   您当前位置:#b商城首页>>元宝商城#k\r\n\r\n";
                //add += "   当前元宝余额:#r" + cm.getChar().getCSPoints(1) + "#k\r\n\r\n#b";
				add += "   当前元宝余额:#r " + cm.getmoneyb() + "#k\r\n\r\n#b";
               // add += "#L1#套服衣裤";
               // add += "#L2#名片戒指";
                //		add += "#L3#本周推荐";
               // add += "#L4#精美勋章";
              //  add += "#L5#耳环裤子\r\n\r\n";
              //  add += "#L6#精品武器";
              //  add += "#L7#精美披风";
              //  add += "#L8#可爱帽子";
                //add += "#L9#属性时装\r\n\r\n";
				add += "#L9#精美饰品\r\n\r\n";
                //	add += "#L10#皇家发型";
              //  add += "#L13#精品上衣";
             //   add += "#L12#我是女神";
              //  add += "#L14#手套鞋子";
				//add += "#L15#新品宠物\r\n\r\n";
                cm.sendSimple (add);   
            }

        //////////////////////////////////////////////////////////////////////////////////////////////////////

        } else if (status == 1) {
            if (selection == 1) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>精美时装#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < jmsz.length; i++) {	
                    add += "\r\n#L" + i + "##v" + jmsz[i][0] + "##z" + jmsz[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + jmsz[i][1] + " #d    使用期限:#r " + jmsz[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   

                xx = 1

            } else if (selection == 2) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>精美名片#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < jzmp.length; i++) {	
                    add += "\r\n#L" + i + "##v" + jzmp[i][0] + "##z" + jzmp[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + jzmp[i][1] + " #d    使用期限:#r " + jzmp[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 2
            } else if (selection == 3) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>本周推荐#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "\r\n#b";
                for (var i = 0; i < tjId.length; i++) {
                    add += "\r\n#L" + i + "##v" + tjId[i][0] + "##z" + tjId[i][0] + "##l#d\r\n\r\n		";   
                    add += "需要元宝:#r " + tjId[i][1] + " #d    使用期限:#r " + tjId[i][3] + "#k#b";
                }
                cm.sendSimple (add);   
                xx = 3;
            } else if (selection == 4) {
                cm.openNpc(9000436, 90);
               
              
            } else if (selection == 5) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>双倍物品#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "\r\n#b";
                for (var i = 0; i < xswp.length; i++) {	
                    add += "\r\n#L" + i + "##v" + xswp[i][0] + "##z" + xswp[i][0] + "##l#d\r\n\r\n		";   
                    add += "需要元宝:#r " + xswp[i][1] + " #d    购买数量:#r " + xswp[i][2] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 5
            } else  if (selection == 6) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>精美武器#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < jmwq.length; i++) {	
                    add += "\r\n#L" + i + "##v" + jmwq[i][0] + "##z" + jmwq[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + jmwq[i][1] + " #d    使用期限:#r " + jmwq[i][3] + "#k#b";
                }
                cm.sendSimple (add);   
                xx = 6
            } else  if (selection == 7) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>精美披风#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < jmpf.length; i++) {	
                    add += "\r\n#L" + i + "##v" + jmpf[i][0] + "##z" + jmpf[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + jmpf[i][1] + " #d    使用期限:#r " + jmpf[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 7
            } else  if (selection == 8) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>精美帽子#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < jmmz.length; i++) {	
                    add += "\r\n#L" + i + "##v" + jmmz[i][0] + "##z" + jmmz[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + jmmz[i][1] + " #d    使用期限:#r " + jmmz[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 8
            } else  if (selection == 9) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>精美饰品#k\r\n\r\n";
                //add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
				add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < jmsp.length; i++) {	
                    add += "\r\n#L" + i + "##v" + jmsp[i][0] + "##z" + jmsp[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + jmsp[i][1] + " #d    使用期限:#r " + jmsp[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 9
            } else  if (selection == 10) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>其他物品#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "\r\n#b";
                for (var i = 0; i < qtwp.length; i++) {
                    add += "\r\n#L" + i + "##v" + qtwp[i][0] + "##z" + qtwp[i][0] + "##l#d\r\n\r\n		";   
                    add += "需要元宝:#r " + qtwp[i][1] + " #d    购买数量:#r " + qtwp[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 10
            } else  if (selection == 11) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>我是高富帅#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < gfs.length; i++) {	
                    add += "\r\n#L" + i + "##v" + gfs[i][0] + "##z" + gfs[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + gfs[i][1] + " #d    使用期限:#r " + gfs[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 11;
            } else  if (selection == 12) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>我是女神#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < ns.length; i++) {	
                    add += "\r\n#L" + i + "##v" + ns[i][0] + "##z" + ns[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + ns[i][1] + " #d    使用期限:#r " + ns[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 12;
            } else  if (selection == 13) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>情侣套装#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < qltz.length; i++) {	
                    add += "\r\n#L" + i + "##v" + qltz[i][0] + "##z" + qltz[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + qltz[i][1] + " #d    使用期限:#r " + qltz[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 13;
            } else  if (selection == 14) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>手套鞋子#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < stxz.length; i++) {	
                    add += "\r\n#L" + i + "##v" + stxz[i][0] + "##z" + stxz[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + stxz[i][1] + " #d    使用期限:#r " + stxz[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 14;
            } else  if (selection == 15) {
                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n#r请注意宠物不一定全有技能,别还需要打技能\r\n请考虑之后妥善购买\r\n\r\n";
                add += "   您当前位置:#b商城首页>>元宝商城>>新品宠物#k\r\n\r\n";
                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n#b";
                for (var i = 0; i < pets.length; i++) {	
                    add += "\r\n#L" + i + "##v" + pets[i][0] + "##z" + pets[i][0] + "##l#d\r\n\r\n		";
                    add += "需要元宝:#r " + pets[i][1] + " #d    使用期限:#r " + pets[i][3] + "#k#b";
                }		
                cm.sendSimple (add);   
                xx = 15;
            }

        ////////////////////////////////////////////////////////////////////////////////////

        } else if (status == 2) {

            if (xx == 1) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>精美时装#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + jmsz[selection][0] + "# #z" + jmsz[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + jmsz[selection][1] + " #d    使用期限:#r " + jmsz[selection][3] + "\r\n                              ";

                add += "   #L1#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }

            if (xx == 2) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>精美名片#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + jzmp[selection][0] + "# #z" + jzmp[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + jzmp[selection][1] + " #d    使用期限:#r " + jzmp[selection][3] + "\r\n                              ";

                add += "   #L2#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }

            if (xx == 3) {	

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";
                add += "   您当前位置:#b商城首页>>本周推荐#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "\r\n\r\n#d";

                add += "   物品:#v" + tjId[selection][0] + "# #z" + tjId[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r" + tjId[selection][1] + " #d    使用期限:#r " + tjId[selection][3] + "\r\n                              ";

                add += "   #L3#立即购买！买买买！#l";

                cm.sendSimple (add);  

                jiage = selection; 


            }

            if (xx == 4) {	

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>使用消耗#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "\r\n\r\n#d";

                add += "   物品:#v" + syxh[selection][0] + "# #z" + syxh[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r" + syxh[selection][1] + " #d    购买数量:#r " + syxh[selection][3] + "\r\n                              ";

                add += "   #L4#立即购买！买买买！#l";

                cm.sendSimple (add);  

                jiage = selection; 


            }

            if (xx == 5) {	

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>限时物品#k\r\n\r\n";

                add += "   物品:#v" + xswp[selection][0] + "# #z" + xswp[selection][0] + "##k\r\n\r\n";

                add += "   需要元宝:#r" + xswp[selection][1] + " #d        使用期限:#r " + xswp[selection][2] + "\r\n";

                add += "   #L5#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;


            }


            if (xx == 6) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>精美武器#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + jmwq[selection][0] + "# #z" + jmwq[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + jmwq[selection][1] + " #d    使用期限:#r " + jmwq[selection][3] + "\r\n                              ";

                add += "   #L6#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }

            if (xx == 7) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>精美披风#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + jmpf[selection][0] + "# #z" + jmpf[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + jmpf[selection][1] + " #d    使用期限:#r " + jmpf[selection][3] + "\r\n                              ";

                add += "   #L7#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }


            if (xx == 8) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>精美帽子#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + jmmz[selection][0] + "# #z" + jmmz[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + jmmz[selection][1] + " #d    使用期限:#r " + jmmz[selection][3] + "\r\n                              ";

                add += "   #L8#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }

            if (xx == 9) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>精美饰品#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + jmsp[selection][0] + "# #z" + jmsp[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + jmsp[selection][1] + " #d    使用期限:#r " + jmsp[selection][3] + "\r\n                              ";

                add += "   #L9#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }

            if (xx == 10) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>其他物品#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + qtwp[selection][0] + "# #z" + qtwp[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + qtwp[selection][1] + " #d    使用期限:#r " + qtwp[selection][3] + "\r\n                              ";

                add += "   #L10#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }

            if (xx == 11) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>我要当高富帅#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + gfs[selection][0] + "# #z" + gfs[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + gfs[selection][1] + " #d    使用期限:#r " + gfs[selection][3] + "\r\n                              ";

                add += "   #L11#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }

            if (xx == 12) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>我是女神#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + ns[selection][0] + "# #z" + ns[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + ns[selection][1] + " #d    使用期限:#r " + ns[selection][3] + "\r\n                              ";

                add += "   #L12#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }

            if (xx == 13) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>情侣套装#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + qltz[selection][0] + "# #z" + qltz[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + qltz[selection][1] + " #d    使用期限:#r " + qltz[selection][3] + "\r\n                              ";

                add += "   #L13#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }

            if (xx == 14) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>手套鞋子#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + stxz[selection][0] + "# #z" + stxz[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + stxz[selection][1] + " #d    使用期限:#r " + stxz[selection][3] + "\r\n                              ";

                add += "   #L14#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }
            if (xx == 15) {

                var add = "欢迎来到#r开心冒险岛#k,下面是本服的游戏商城区.\r\n\r\n";

                add += "   您当前位置:#b商城首页>>元宝商城>>新品宠物#k\r\n\r\n";

                add += "   当前元宝余额:#r" + cm.getmoneyb() + "#k\r\n\r\n#d";

                add += "   物品:#v" + pets[selection][0] + "# #z" + pets[selection][0] + "#\r\n\r\n";

                add += "   需要元宝:#r " + pets[selection][1] + " #d    使用期限:#r " + pets[selection][3] + "\r\n                              ";

                add += "   #L15#立即购买！买买买！#l";

                cm.sendSimple (add);

                jiage = selection;

            }
        ///////////////////////////////////////////////////////////////////////////////////////////

        } else if (status == 3) {


            if (selection == 1) {
                if (cm.getmoneyb() >= jmsz[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-jmsz[jiage][1]);	
                        if (jmsz[jiage][2] != "未定义") {
                            cm.gainItem(jmsz[jiage][0], 1, jmsz[jiage][2]);
                        } else {
                            cm.gainItem(jmsz[jiage][0], 1);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }


            } else if (selection == 2) {
                if (cm.getmoneyb() >= jzmp[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-jzmp[jiage][1]);	
                        if (jzmp[jiage][2] != "未定义") {
                            cm.gainItem(jzmp[jiage][0], 1, jzmp[jiage][2]);
                        } else {
                            cm.gainItem(jzmp[jiage][0], 1);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }


            } else if (selection == 3) {
                if (cm.getmoneyb() >= tjId[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else { 
                        cm.cm.setmoneyb(-tjId[jiage][1]);	
                        var ii = MapleItemInformationProvider.getInstance();
                        var type = ii.getInventoryType(tjId[jiage][0]);
                        var toDrop = ii.randomizeStats(ii.getEquipById(tjId[jiage][0])).copy();
                        var temptime = (java.lang.System.currentTimeMillis() + tjId[jiage][4]); 
                        toDrop.setExpiration(temptime);
                        toDrop.setLocked(1);	
                        cm.getInventory(type).addItem(toDrop);
                        cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); 
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请登陆网站冲值.");
                    cm.dispose();
                }

            } else if (selection == 4) {
                if (cm.getmoneyb() >= syxh[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-syxh[jiage][1]);		
                        if (syxh[jiage][2] != "未定义") {
                            cm.gainItem(syxh[jiage][0], syxh[jiage][3], syxh[jiage][2]);
                        } else {
                            cm.gainItem(syxh[jiage][0],syxh[jiage][3]);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请登陆网站冲值.");
                    cm.dispose();
                }


            } else if (selection == 5) {
                if (cm.getmoneyb() >= xswp[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-xswp[jiage][1]);
                        var ii = MapleItemInformationProvider.getInstance();
                        var type = ii.getInventoryType(xswp[jiage][0]);
                        var toDrop = ii.randomizeStats(ii.getEquipById(xswp[jiage][0])).copy();
                        var temptime = (java.lang.System.currentTimeMillis() + xswp[jiage][3]); 
						if (xswp[jiage][3] != "永久") {
							toDrop.setExpiration(temptime);
						}
                      //  toDrop.setLocked(1);	
                        cm.getPlayer().getInventory(type).addItem(toDrop);
                        cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); 
					    cm.getC().getSession().write(MaplePacketCreator.getShowItemGain(xswp[jiage][0], 1, true));
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请登陆网站冲值.");
                    cm.dispose();
                }

            } else if (selection == 6) {
                if (cm.getmoneyb() >= jmwq[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-jmwq[jiage][1]);	
                        if (jmwq[jiage][2] != "未定义") {
                            cm.gainItem(jmwq[jiage][0], 1, jmwq[jiage][2]);
                        } else {
                            cm.gainItem(jmwq[jiage][0], 1);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }


            } else if (selection == 7) {
                if (cm.getmoneyb() >= jmpf[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-jmpf[jiage][1]);	
                        if (jmpf[jiage][2] != "未定义") {
                            cm.gainItem(jmpf[jiage][0], 1, jmpf[jiage][2]);
                        } else {
                            cm.gainItem(jmpf[jiage][0], 1);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }

            } else if (selection == 8) {
                if (cm.getmoneyb() >= jmmz[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.setmoneyb(-jmmz[jiage][1]);	
                        if (jmmz[jiage][2] != "未定义") {
                            cm.gainItem(jmmz[jiage][0], 1, jmmz[jiage][2]);
                        } else {
                            cm.gainItem(jmmz[jiage][0], 1);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }

            } else if (selection == 9) {
                if (cm.getmoneyb() >= jmsp[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.setmoneyb(-jmsp[jiage][1]);	
                        if (jmsp[jiage][2] != "未定义") {
                            cm.gainItem(jmsp[jiage][0], 1, jmsp[jiage][2]);
                        } else {
                            cm.gainItem(jmsp[jiage][0], 10,10,10,10,10,10,10,10,10,10,10,10,10,10);
							//cm.gainItem(jmsp[jiage][0], 1,2,3,4,5,6,7,8,9,10,11,12,13,14);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }


            } else if (selection == 10) {
                if (cm.getmoneyb() >= qtwp[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-qtwp[jiage][1]);	
                        if (syxh[jiage][2] != "未定义") {
                            cm.gainItem(syxh[jiage][0],syxh[jiage][3], syxh[jiage][2]);
                        } else {
                            cm.gainItem(syxh[jiage][0], syxh[jiage][3]);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请登陆网站冲值.");
                    cm.dispose();
                }



            } else if (selection == 11) {
                if (cm.getmoneyb() >= gfs[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-gfs[jiage][1]);	
                        if (gfs[jiage][2] != "未定义") {
                            cm.gainItem(gfs[jiage][0],1, gfs[jiage][2]);
                        } else {
                            cm.gainItem(gfs[jiage][0], 1);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }


            } else if (selection == 12) {
                if (cm.getmoneyb() >= ns[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-ns[jiage][1]);
                        if (ns[jiage][2] != "未定义") {
                            cm.gainItem(ns[jiage][0],1, ns[jiage][2]);
                        } else {
                            cm.gainItem(ns[jiage][0], 1);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }

            } else if (selection == 13) {
                if (cm.getmoneyb() >= qltz[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-qltz[jiage][1]);	
                        if (qltz[jiage][2] != "未定义") {
                            cm.gainItem(qltz[jiage][0],1, qltz[jiage][2]);
                        } else {
                            cm.gainItem(qltz[jiage][0], 1);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }

            } else if (selection == 14) {
                if (cm.getmoneyb() >= stxz[jiage][1]) {
                    if (cm.getInventory(1).isFull(0)){
                        cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-stxz[jiage][1]);		
                        if (stxz[jiage][2] != "未定义") {
                            cm.gainItem(stxz[jiage][0],1, stxz[jiage][2]);
                        } else {
                            cm.gainItem(stxz[jiage][0], 1);
                        }
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }
            } else if (selection == 15) {
                if (cm.getmoneyb() >= pets[jiage][1]) {
                    if (cm.getInventory(5).isFull(0)){
                        cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法购买.");
                        cm.dispose();
                 /*   } else if (cm.getInventory(2).isFull()){
                        cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(3).isFull()){
                        cm.sendOk("#b请保证设置栏位至少有2个空格,否则无法购买.");
                        cm.dispose();
                    } else if (cm.getInventory(4).isFull()){
                        cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法购买.");
                        cm.dispose();*/
                    } else {
                        cm.cm.setmoneyb(-pets[jiage][1]);		
						cm.gainPet(pets[jiage][0], "", 1, 0, 100, 90);
                        cm.sendOk("#b购买成功,请查看背包.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#b您没有足够的元宝进行购买,请充值.");
                    cm.dispose();
                }
            }
			
        }
    }

}
