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
var 蓝色箭头 = "#fEffect/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fEffect/UIWindow/Quest/icon6/7#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 红方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/0#";
var 蓝方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/1#";
var 绿方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/2#";
var sel;
var 制作材料 = [4011006,4021006,4430056];
var 材料数量 = [1,1,100];
var G链ID = 1132300;
// 该脚本来源于GoldMs服务端，制作于2023/11/20，正版授权QQ849340706

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
			
			
				var add = "    #v"+G链ID+"##e#z"+G链ID+"# - #n[增加怪物刷新数量] \r\n";
				add += ""+分割线+"\r\n"
				add += "   "+绿方+" #e#k打造说明#n\r\n";
				add += "   #k属性腰带可以吸收别的装备的能力来提升自己的属性\r\n";
				add += "   #k每种装备可以吸收5次，一共有53种装备可以吸收\r\n";
				add += "   #k佩戴属性腰带并不会影响套装的总伤加成\r\n";
				add += ""+分割线+"\r\n"
				add += " #L1#"+正方箭头+" #b制作腰带 #r[仅限一次]#l\r\n";
				add += " #L2#"+正方箭头+" #b吸收装备#l\r\n";
				cm.sendSimple(add);

//------------------------------------------------------------------------

        }
		else if (status == 1) 
		{
			if (selection == 1) 
			{
				var txt ="#e#k制作G链需要以下材料：#n\r\n\r\n";
				txt += ""+分割线+"\r\n"
				txt += "   - #v"+制作材料[0]+"# x "+材料数量[0]+"\r\n";
				txt += "   - #v"+制作材料[1]+"# x "+材料数量[1]+"\r\n";
				txt += "   - #v"+制作材料[2]+"# x "+材料数量[2]+"\r\n";
				txt += ""+分割线+"\r\n"
				txt += "                                        #e#r确定制作吗？#n\r\n";
				cm.sendYesNo(txt);
				sel = 1;
				
			}
			if (selection == 2) 
			{
					cm.dispose();
					cm.openNpc(9031003, "腰带吸收装备");
			}
		}
		else if (status == 2) 
		{
			if(sel == 1)
			{
				if(!cm.haveItem(制作材料[0],材料数量[0]) || !cm.haveItem(制作材料[1],材料数量[1]) || !cm.haveItem(制作材料[2],材料数量[2]))
				{
					cm.sendOk("你的材料数量不足，请检查！");
					cm.dispose();
				}
				else if(cm.getPlayer().getOneTimeLog("制作属性腰带") != 0 )
				{
					cm.sendOk("你已经制作过G链了，每个角色只能制作一次！");
					cm.dispose();
				}
				else
				{
					cm.gainItem(制作材料[0],-材料数量[0]);
					cm.gainItem(制作材料[1],-材料数量[1]);
					cm.gainItem(制作材料[2],-材料数量[2]);
					cm.gainItem(G链ID,1);
					cm.sendOk("制作成功！");
					cm.getPlayer().setOneTimeLog("制作属性腰带");
					cm.dispose();
				}
			}
		}
	}
}




// 该脚本来源于GoldMs服务端，制作于2023/8/4，正版授权QQ849340706