var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";//大红心
var JT = "#fUI/Basic/BtHide3/mouseOver/0#";//小箭头
var s;
var status;
var i;
var slot;
var sl;
var string;
var type1;
var price;
var itemInfo
var sp = false;
var 装备2 = "#fUI/CashShop.img/Base/Tab2/Enable/0#";
var 消耗2 = "#fUI/CashShop.img/Base/Tab2/Enable/1#";  
var 设置2 = "#fUI/CashShop.img/Base/Tab2/Enable/2#"; 
var 其他2 = "#fUI/CashShop.img/Base/Tab2/Enable/3#";   
var 特殊2 = "#fUI/CashShop.img/Base/Tab2/Enable/4#"; 
var count = 10; //上架最多商品
//价格：数量
var 比例 = [1,100000];

var jinbi = false;

//额外允许上架的物品
var items = [
3994731,3994730]

//额外不允许上架的物品
var notitems = [
1113129, //一级进化指环
1113130, //二级进化指环
1113131, //三级进化指环

1302908, //除魔之剑
1302905, //封印之剑
1302904, //黑暗之剑
1302900, //必杀之剑
1402184, // 狂龙战士双手剑
1402142, // 君主双手剑
1402224, // 柳德之剑
1302107, // 神话之境

1112542, //仙剑
1112543, //仙剑
1112575, //仙剑
1112576, //仙剑
1116049, //笑若扶风戒指
1116052, //笑若扶风戒指


1113217,//主线戒指
1352243,
1122017, //精灵吊坠
1112907, //小鱼戒指
1114304,//宇宙重生戒指
1132300,//轮回碑石--千万不能上架
1113401, //超越成长戒指
1116049, //每日充值戒指
1116046, //每日充值戒指
1912339, //地球鞍子
1902339, //地球坐骑
1912347, //滑稽星鞍子
1902347, //滑稽星
2022509, //元宝宝箱
3994659, //封印纹章

1142263, //新手勋章
1143175, //VIP勋章1
1142948, //VIP勋章2
1142947, //VIP勋章3
1142946, //VIP勋章4
1142945, //VIP勋章5
1142944, //VIP勋章6
1142943, //VIP勋章7
1142802, //VIP勋章8
1142803, //VIP勋章9
1142742, //至尊VIP勋章

1115201,//魂魄戒指
1115202,//魂魄戒指
1115203,//魂魄戒指
1115204,//魂魄戒指
1115205,//魂魄戒指
1115206,//魂魄戒指
1115207,//魂魄戒指
1115208,//魂魄戒指
1115209,//魂魄戒指
1115210,//魂魄戒指
1115211,//魂魄戒指
1115212,//魂魄戒指
1115213,//魂魄戒指
1115214,//魂魄戒指
1115215,//魂魄戒指
1115216,//魂魄戒指
1115217,//魂魄戒指
1115218,//魂魄戒指
1115219,//魂魄戒指
1115220,//魂魄戒指
1115221,//魂魄戒指
1115222,//魂魄戒指
1115223,//魂魄戒指
1115224,//魂魄戒指
1115225,//魂魄戒指
1115226,//魂魄戒指
1115227,//魂魄戒指
1115228,//魂魄戒指
1115229,//魂魄戒指
1115230,//魂魄戒指
1115231,//魂魄戒指
1115232,//魂魄戒指
1115233,//魂魄戒指
1115234,//魂魄戒指

2022701, //BOSS奖励箱子
2022702, //BOSS奖励箱子
2022703, //BOSS奖励箱子
2022704, //BOSS奖励箱子
2022705, //BOSS奖励箱子
2022706, //BOSS奖励箱子
2022707, //BOSS奖励箱子
2022708, //BOSS奖励箱子
2022709, //BOSS奖励箱子
2022710, //BOSS奖励箱子
2022711, //BOSS奖励箱子
2022712, //BOSS奖励箱子
2022713, //BOSS奖励箱子
2022714, //BOSS奖励箱子
2022715, //BOSS奖励箱子
2022716, //BOSS奖励箱子
2022717, //BOSS奖励箱子
2022718, //BOSS奖励箱子
2022719, //BOSS奖励箱子
2022720, //BOSS奖励箱子

2049122, //正向混沌卷轴

2614000, //突破之石
2614001, //突破之石
2614002, //突破之石
2614003, //突破之石
2614004, //突破之石
2614005, //突破之石
2614006, //突破之石
2614007, //突破之石
2614008, //突破之石
2614009, //突破之石
2614010, //突破之石
2614011, //突破之石
2614012, //突破之石
2614013, //突破之石
2614014, //突破之石
2614015, //突破之石
2614016, //突破之石
2614017, //突破之石
2614018, //突破之石
2614019, //突破之石
2614020, //突破之石
2614021, //突破之石

1352245,1352246,1352248//技能书
]

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            if(s >=10){
                cm.RemoveSelSuo(s);
            }
            cm.dispose();
            return;
        }
        if (mode == 1){
            status++;
        } else {
            cm.dispose();
            return;
        }
        
        if(status == 0) {
            var txt = "  \r\n" + 心 + " " + 心 + " " + 心 + "  #r#e < " + cm.开服名称() + "交易行 > #k#n  " + 心 + " " + 心 + " " + 心 + "\r\n\r\n";
            txt +="  #r#L1#" + JT + "上架物品#l#k\r\n" 
            txt +="  #r#L3#" + JT + "已售取钱#l#k\r\n\r\n" 
            txt +=" #r______________________#d已上架商品#r_____________________#l#k\r\n\r\n" 
            
            txt += cm.getItemListInfo(cm.getItemListByCid(cm.getPlayer()));
            cm.sendOk(txt);
        } else if(status == 1) {
            s = selection;
            if(s == 1) {
                if(cm.getItemListByCid(cm.getPlayer()).size() >= count) {
                    cm.sendOk("你的货架已满，请清理后在来");
                    cm.dispose();
                    return;
                }
                var txt = "你请选你要上架的物品类型：\r\n\r\n";
            //    txt += "#L1#"+装备2+"#l\t\r\n\r\n";
				txt += "#L3#"+设置2+"#l\t\r\n\r\n";
                cm.sendOk(txt);
            } else if(s == 3) {
                var txt = "你已经售出的商品如下：\r\n";
                var cx = cm.getItemTradingB();
                if(cx == "") {
                    cm.sendOk("你没有商品售出\r\n");
                    cm.dispose();
                    return;
                }
                cm.sendOk(txt + cx);
            } else {
                itemInfo = cm.getItemInfo(s);
                if(itemInfo == null) {
                    cm.sendOk("有人在浏览哦，不能下架");
                    cm.dispose();
                    return;
                }
                if(itemInfo.cid != cm.getPlayer().getId()) {
                    cm.getPlayer().ban("修改交易系统封包",true,true,true);
                    return;
                }
                cm.setSelSuo(s,cm.getPlayer().getId());
                var txt = itemInfo.toString();
                txt +="\r\n#r你确定要下架该商品么#k\r\n";
                cm.sendYesNoS(txt,1);
                sp = true;
            }
        } else if(status == 2) {
            if(s == 1) {
                i = selection;
                if(selection == 6) {
                    status = 3;
                    jinbi = true;
                    cm.sendGetNumber("请输入你要上架的数量\r\n\r\n当前元宝比例#b"+比例[0]+":"+(比例[1]/10000)+"W",比例[1],比例[1],cm.getMeso());
                    return;
                }
                jinbi = false;
                var txt = "请选择你要上架的物品：\r\n";
                var it = cm.getInventory(i).iterator();
                var xx = 1;
                var next = true;
                while(it.hasNext()) {
                    var item = it.next();
                    if(items.indexOf(item.getItemId()) == -1) {
                        continue;
                    }
                    if(item.getExpiration() != -1) {
                        continue;
                    }
                    if(notitems.indexOf(item.getItemId()) != -1) {
                        continue;
                    }
                    txt +="#L"+item.getPosition()+"#位置"+item.getPosition()+".#v"+item.getItemId()+":##l  ";
                    if(xx % 3 == 0) {
                        txt +="\r\n";
                    }
                    xx++;
                    next = false;
                }
                if(next) {
                    cm.sendOk("你没有符合上架的物品");
                    cm.dispose();
                    return;
                }
                cm.sendOk(txt);
			} else if (s == 3) {
					i = selection;
					var nx = cm.getPriceById(i);
					if (nx == null) {
					cm.sendOk("发生错误");
				} else { // 提取货币类型和数量
					var currencyType = nx[0]; // 货币类型，例如 0 表示金币，1 表示元宝
					var currencyAmount = nx[1]; // 货币数量
					var currencyName = currencyType == 0 ? "金币" : "元宝"; // 修正表述
					cm.getPlayer().modifyCSPoints(currencyType, currencyAmount, true); // 修改玩家的货币数量
					cm.deleteItemTradingB(i); // 删除交易记录
					cm.sendOk("已取出 " + currencyAmount + " " + currencyName);
					cm.getPlayer().dropMessage(5, "交易行取钱：+ " + currencyAmount + " " + currencyName);
					var logTitle = "交易行取钱记录"; // 日志标题
					var logMessage = "玩家 " + cm.getPlayer().getName() + " 取出了 " + currencyAmount + " " + currencyName + "（交易ID：" + i + "），当前拥有元宝：" +cm.getmoneyb()+ " \r\n";
					cm.getItemLog(logTitle, logMessage); // 调用日志记录方法
				}
            } else {
                if(sp) {
                    if(mode == 1) {
                        if(cm.isFull(1)||cm.isFull(2)||cm.isFull(3)||cm.isFull(4)||cm.isFull(5)) {
                            cm.RemoveSelSuo(s);
                            cm.sendOk("你的背包空间不够，请各位置留出一个空格或者你的元宝太多了,请放仓库保管一些");
                            cm.dispose();
                            return;
                        }
                        if(itemInfo.meso > 0) {
                            if((cm.getMeso()+itemInfo.meso) > 10000) {
                                cm.RemoveSelSuo(s);
                                cm.sendOk("你的元宝太多了,请放仓库保管一些");
                                cm.dispose();
                                return;
                            }
                            cm.gainMeso(itemInfo.meso);
                        } else {
                            cm.additem(itemInfo.item);
                        }
                        cm.removeItemTrading(s);
                        cm.RemoveSelSuo(s);
                        cm.sendOk("已下架");
                        status = -1;
                        return;
                    }
                }
            }
        } else if(status == 3) {
            if(s == 1) {
                slot = selection;
                var item = cm.getItem(i, slot);
				var quantity = item.getQuantity();
				cm.sendGetNumber("请输入你要上架的数量（当前数量：" + quantity + "）：", 1, 1, quantity);
            } else if(s == 3) {
                cm.dispose();
                cm.openNpc(9900004,"商品处理");
                return;
            }
        } else if(status == 4) {
            if(s == 1) {
                sl = selection;
                if(sl < 比例[1] && jinbi) {
                    return;
                }
                if((jinbi && selection > cm.getMeso())) {
                    return;
                } else if(!jinbi && selection > cm.getItem(i,slot).getQuantity()) {
                    return;
                }
                var text = "请选择你要出售物品的价格类型\r\n";
                if(!jinbi) {
                    text +="#L5##r元宝#k#l\r\n";
                }
                cm.sendOk(text);
            }
        } else if(status == 5) {
            if(s == 1) {
                type1 = selection;
                if(!jinbi) {
                    cm.sendGetNumber("请输入你的上架物品#r总价格#k：",1,1,20000);
                } else {
                    var txt = "当前输入的元宝:#r"+sl/10000+"W#k\r\n\r\n";
                    txt += "您当前最少只能输入 #r" +Math.ceil(sl/比例[1])+"#k 数量金额\r\n\r\n";
                    cm.sendGetNumber("请输入你的出售的价格：\r\n"+txt,Math.ceil(sl/比例[1]),Math.ceil(sl/比例[1]),10000);
                }
            }
        } else if(status == 6) {
            if(s == 1) {
                if(selection <= 0 || selection > 2000000000) {
                    return;
                }
                price = selection;
                if(jinbi) {
                    if(price < sl/比例[1]) {
                        return;
                    }
                }
                cm.sendGetText("请输入对物品描述或者补充:");
            }
        } else if(status == 7) {
            if(s == 1) {
                string = cm.getText();
                var txt = "请确认下信息：\r\n";
                if(i == 6) {
                    txt +="#b你要上架的物品：#r元宝 x " + sl + "\r\n\r\n";
                } else {    
                    var item = cm.getItem(i,slot);
                    txt +="#b你要上架的物品：#v"+item.getItemId()+"# x " + sl + "\r\n\r\n";
                }
                txt +="价格类型：#k#r" + (type1 == 1 ? "点券": type1 == 0 ? "元宝": type1 == 2 ? "抵用券" :"元宝")+"#k\r\n\r\n";
                txt +="#b价格：#k#r"+price+"#k\r\n\r\n";
                txt +="#b商品描述：#k#r"+string+"#k\r\n\r\n";
                txt +="#r请确认是否上架#k\r\n";
                cm.sendYesNoS(txt,1);
            }
        } else if(status == 8) {
            if(s == 1) {
                if(mode == 1) {
				if(cm.getmoneyb() <= 0) {
				cm.sendOk("上架的手续费不够")
				cm.dispose();
				return;
							}
				cm.setmoneyb(-1);
                    var txt = "";
                    txt +="价格类型：#k#r" + (type1 == 1 ? "点券": type1 == 0 ? "元宝": type1 == 2 ? "抵用券" :"元宝")+"#k\r\n\r\n";
                    txt +="#b价格：#k#r"+price+"#k\r\n\r\n";
                    txt +="#b商品描述：#k#r"+string+"#k\r\n\r\n";
                    
                    if(i == 6) {
                        cm.addItemTradingMeso(type1,price,sl,sl,string);
                    } else {
                        var item = cm.getItem(i,slot);
                        txt +="#b物品："+cm.getItemName(item.getItemId())+item.getItemId()+" x " + sl + "\r\n\r\n";
                        cm.addItemTrading(type1,price,sl,cm.getItem(i,slot),string);
                    }
                    cm.getItemLog("交易行上架",cm.getName()+"上架了"+txt);
                    cm.sendOk("上架成功");
                    cm.dispose();
                }
            }
        }
    }
}
