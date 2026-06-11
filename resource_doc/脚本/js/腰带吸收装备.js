importPackage(java.util);
importPackage(Packages.client);
importPackage(Packages.server);
importPackage(Packages.tools);
importPackage(Packages.tools.packet);
var FY0 = "┏━━━━━━━━━━━┓";
var FY1 = "┃    - GoldMS -    ┃";
var FY2 = "┃ 脚本仿制  　定制脚本 ┃";
var FY3 = "┃ 技术支持 　 游戏顾问 ┃";
var FY4 = "┃ ＷＺ添加　  地图制作 ┃";
var FY5 = "┃ 售登陆器    售下载器 ┃";
var FY6 = "┣━━━━━━━━━━━┫";
var FY7 = "┃唯一QQ: 849340706 ┃";
var FY8 = "┗━━━━━━━━━━━┛";
var FY9 = "该脚本来源于GoldMS服务端，正版授权QQ849340706";
var 分割线 = "-----------------------------------------------";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 红方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/0#";
var 蓝方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/1#";
var 绿方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/2#";
var 任务简述 = "#fUI/UIWindow.img/Quest/summary#";
var 奖励 = "#fUI/UIWindow.img/Quest/reward#";
var sel;
var 最大次数 = 5;
var G链ID = 1132300;
// 序号，收集ID，收集数量，展示内容，四维，攻击力，魔法力，Hp，Mp,
var 池 = new Array(
Array(1,1302149,1,"装备1",1,1,1,10,10),
Array(2,1302153,1,"装备2",1,1,1,10,10),
Array(53,1372102,1,"装备53",1,1,1,10,10)
)

// 该脚本来源于GoldMs服务端，制作于2023/8/4，正版授权QQ849340706

function start() 
{
	if(FY7!="\u2503\u552f\u4e00\x51\x51\x3a \x38\x34\x39\x33\x34\x30\x37\x30\x36 \u2503" || FY9!="\u8be5\u811a\u672c\u6765\u6e90\u4e8e\x47\x6f\x6c\x64\x4d\x53\u670d\u52a1\u7aef\uff0c\u6b63\u7248\u6388\u6743\x51\x51\x38\x34\x39\x33\x34\x30\x37\x30\x36")	{		cm["\x73\x65\x6e\x64\x4f\x6b"]("\u8be5\u811a\u672c\u6765\u6e90\u4e8e\x23\x72\x47\x6f\x6c\x64\x4d\x73\u670d\u52a1\u7aef\x23\x6b\uff0c\u4f60\u65e0\u6cd5\u5728\u522b\u7684\u670d\u52a1\u7aef\u4f7f\u7528\r\n\u8bf7\u8054\u7cfb\u4f5c\u8005  \x23\x62\u6b63\u7248\u6388\u6743\x51\x51\x38\x34\x39\x33\x34\x30\x37\x30\x36");		cm["\x64\x69\x73\x70\x6f\x73\x65"]();		return;	}
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) 
	{
       
        cm.dispose();
    } 
	else 
	{
        if (status >= 0 && mode == 0) 
		{
            cm.sendOk("#b好的,下次再见.");
            cm.dispose();
            return;
        }
        if (mode == 1) 
		{
            status++;
        } else 
		{
            status--;
        }
        if (status == 0) 
		{

			if(cm.getPlayer().getMapId()==180000001)
			{
				cm.dispose();
				cm.openNpc(9900005);
				return;
			}
			
			
				var add = ""+绿方+" #e#k你想吸收哪件装备？#l#n\r\n";
				add += ""+分割线+"\r\n"
				for(var i =0;i<池.length;i++)
				{
					if(cm.getPlayer().getOneTimeLogcs(池[i][3]) < 最大次数)
					{
						if (cm.getPlayer().getItemQuantity(parseInt(池[i][1]), false) != 0)
						{
							add += "   #b#L"+i+"##r[背包#c"+池[i][1]+"#件]  "+正方箭头+" #k#z"+池[i][1]+"# #b["+cm.getPlayer().getOneTimeLogcs(池[i][3])+"#b/5]#l\r\n"

						}
						else
						{
							add += "   #b#L"+i+"##k[背包#c"+池[i][1]+"##k件]  "+正方箭头+" #k#z"+池[i][1]+"# #b["+cm.getPlayer().getOneTimeLogcs(池[i][3])+"#b/5]#l\r\n"
						}
					}
					else
					{
						add += "   #L999#"+正方箭头+" #r[已完成]#b["+池[i][0]+"] #z"+池[i][1]+"##l\r\n"
					}
				}
				add += "\r\n"+分割线+"\r\n"
				cm.sendSimple(add);

//------------------------------------------------------------------------

        }
		else if (status == 1) 
		{
			sel = selection;
			if (selection == 999) 
			{
				cm.sendOk("你已经吸收过这个装备了，每个装备只能吸收五次");
				cm.dispose();
				return;
			}
			
			var txt =""+任务简述+"\r\n";
			txt += "   #e#k"+绿方+" 吸收装备：#n\r\n\r\n";
			txt += "      #k#v"+池[sel][1]+"# #z"+池[sel][1]+"# x #b"+池[sel][2]+"#k\r\n";
			txt += "\r\n\r\n"+奖励+"\r\n";
			txt += "   #e#k"+绿方+" 属性奖励：#n\r\n";
			txt += "   #k- [四维] = #b"+池[sel][4]+"\r\n"; 
			txt += "   #k- [攻击力] = #b"+池[sel][5]+"\r\n";
			txt += "   #k- [魔法力] = #b"+池[sel][6]+"\r\n";
			txt += "   #k- [生命值] = #b"+池[sel][7]+"\r\n";
			txt += "   #k- [魔法值] = #b"+池[sel][8]+"#k\r\n";
			txt += "\r\n";
			txt += "                                    #r#e是否提交任务？#n\r\n";
			cm.sendYesNo(txt);
		}
		else if (status == 2) 
		{
			if(cm.getInventory(1).getItem(1) == null)
			{
				cm.sendOk("装备栏第一格没有装备，请将#v"+G链ID+"#放在装备栏第一格。");
				cm.dispose();
			}
			else if(cm.getInventory(1).getItem(1).getItemId() != G链ID)
			{
				cm.sendOk("装备栏第一格不是#v"+G链ID+"#，请将#t"+G链ID+"#放在装备栏第一格。");
				cm.dispose();
			}
			else if(!cm.haveItem(池[sel][1],池[sel][2]))
			{
				cm.sendOk("你的材料不足，无法完成\r\n需要#v"+池[sel][1]+"# ["+池[sel][2]+"]，实际你只有#c"+池[sel][1]+"个");
				cm.dispose();
			}
			else
			{
				// ① 扣除材料
				cm.gainItem(parseInt(池[sel][1]), -parseInt(池[sel][2]));

				// ② 拿装备栏第一格物品
				var itemId1 = cm.getInventory(1).getItem(1).getItemId();
				var citem   = cm.getInventory(1).getItem(1).copy();

				// ③ 加属性（全部显式转 int）
				citem.setStr(citem.getStr() + parseInt(池[sel][4]));
				citem.setDex(citem.getDex() + parseInt(池[sel][4]));
				citem.setInt(citem.getInt() + parseInt(池[sel][4]));
				citem.setLuk(citem.getLuk() + parseInt(池[sel][4]));
				citem.setHp(citem.getHp() + parseInt(池[sel][7]));
				citem.setMp(citem.getMp() + parseInt(池[sel][8]));
				citem.setMatk(citem.getMatk() + parseInt(池[sel][5]));
				citem.setWatk(citem.getWatk() + parseInt(池[sel][6]));

				// ④ 删除旧装备 → 放回新装备（JS 友好接口）
				cm.removeSlot(1, 1, 1);   // 装备栏第 1 格删 1 个
				cm.addFromDrop(citem);    // 把改好的装备丢回背包

				// ⑤ 提示 & 广播
				var txt = "吸收成功，腰带增加了以下属性\r\n" +
				          "#k- [四维] = #b" + 池[sel][4] + "\r\n" +
				          "#k- [攻击力] = #b" + 池[sel][5] + "\r\n" +
				          "#k- [魔法力] = #b" + 池[sel][6] + "\r\n" +
				          "#k- [生命值] = #b" + 池[sel][7] + "\r\n" +
				          "#k- [魔法值] = #b" + 池[sel][8] + "#k\r\n";
				cm.sendOk(txt);

				// ⑥ 全服滚动喇叭（2=黄色滚动条）
				cm.喇叭(2, "玩家[" + cm.getPlayer().getName() + "]完成吸收，将<轮回碑石>强化至全新境界！");

				// ⑦ 记录本次吸收次数（key 转成字符串）
				cm.getPlayer().setOneTimeLog(String(池[sel][3]));

				cm.dispose();
			}
		}
	}
}




// 该脚本来源于GoldMs服务端，制作于2023/11/20，正版授权QQ849340706