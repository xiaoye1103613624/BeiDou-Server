/*
	װ������ NPC�ű�  by ���� 2017/6/30 ������ ���� 12:03:48
	����ѧϰ�ο�����ֹ�����κ���ҵ��Ϊ
*/


var status;
var text;
var itemid, item;
var selstatus = -1;
var itemList = new Array();
var deleteSlot;
var itemstr, itemdex, itemluk, itemint, itemhp, itemmp, itemwatk, itemmatk, itemwdef, itemmdef, itemavoid, itemacc,
    itemjump, itemspeed;
// str,dex,luk,int,hp,mp,watk,matk,wdef,mdef,avoid,acc,jump,speed,time
var str_random = Math.floor(Math.random() * 4);
var dex_random = Math.floor(Math.random() * 4);
var luk_random = Math.floor(Math.random() * 4);
var int_random = Math.floor(Math.random() * 4);
var hp_random = Math.floor(Math.random() * 11);
var mp_random = Math.floor(Math.random() * 11);
var watk_random = Math.floor(Math.random() * 4);
var matk_random = Math.floor(Math.random() * 4);
var wdef_random = Math.floor(Math.random() * 4);
var mdef_random = Math.floor(Math.random() * 4);
var avoid_random = Math.floor(Math.random() * 4);
var acc_random = Math.floor(Math.random() * 4);
var jump_random = Math.floor(Math.random() * 2);
var speed_random = Math.floor(Math.random() * 2);

var chance_random = Math.floor(Math.random() * 100);
var chance_value = 100;
var chance_level1 = 80, chance_level2 = 60, chance_level3 = 35, chance_level4 = 15, chance_level5 = 5;
var item_level1 = 50, item_level2 = 150, item_level3 = 350, item_level4 = 600, item_level5 = 1000;
var price = 1, price1 = 2, price2 = 3, price3 = 4, price4 = 5, price5 = 6;


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode <= 0) {
        cm.dispose();
        return;
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {

            text = "#d�ҿ���Ϊ����ڵ�װ��ˢ����������Ŷ����Ȼ����Ҫ��ȡһ�����á�\r\n";
            text += "#L0##e#d ����������ָ������#l\r\n";
            text += "\r\n\r\n\r\n\r\n";
            cm.sendSimple(text);
        } else {
            if (selstatus == -1) {
                selstatus = selection;
            }
            switch (selstatus) {
                case 0:
                    deleteItemBySlot(selection);
                    break;
                case 1:
                    cm.openNpc(cm.getNpc(), 501);
            }
        }
    }
}

function deleteItemBySlot(selection) {
    if (status == 1) {
        itemList = cm.getInventory(1).list().iterator();
        text = "#e- ��ѡ��Ҫ�����ĵ��� -#n\r\n\r\n#b";
        var indexof = 1;
        while (itemList.hasNext()) {
            var item = itemList.next();
            text += "#L" + item.getPosition() + "##v" + item.getItemId() + "#";
            if (indexof > 1 && indexof % 5 == 0) {
                text += "\r\n";
            }
            indexof++;
        }
        cm.sendSimple(text);
    } else if (status == 2) {
        item = cm.getInventory(1).getItem(selection);
        itemid = item.getItemId();

        itemstr = item.getStr();
        itemdex = item.getDex();
        itemluk = item.getLuk();
        itemint = item.getInt();
        itemhp = item.getHp();
        itemmp = item.getMp();
        itemwatk = item.getWatk();
        itemmatk = item.getMatk();
        itemwdef = item.getWdef();
        itemmdef = item.getMdef();
        itemavoid = item.getAvoid();
        itemacc = item.getAcc();
        itemjump = item.getJump();
        itemspeed = item.getSpeed();

        deleteSlot = selection;
        if ((itemstr > item_level5) || (itemdex > item_level5) || (itemluk > item_level5) || (itemint > item_level5)) {
            chance_value = chance_level5;
            price = price5;
        } else if (((itemstr > item_level4) && (itemstr <= item_level5)) || ((itemdex > item_level4) && (itemdex <= item_level5)) || ((itemluk > item_level4) && (itemluk <= item_level5)) || ((itemint > item_level4) && (itemint <= item_level5))) {
            chance_value = chance_level4;
            price = price4;
        } else if (((itemstr > item_level3) && (itemstr <= item_level4)) || ((itemdex > item_level3) && (itemdex <= item_level4)) || ((itemluk > item_level3) && (itemluk <= item_level4)) || ((itemint > item_level3) && (itemint <= item_level4))) {
            chance_value = chance_level3;
            price = price3;
        } else if (((itemstr > item_level2) && (itemstr <= item_level3)) || ((itemdex > item_level2) && (itemdex <= item_level3)) || ((itemluk > item_level2) && (itemluk <= item_level3)) || ((itemint > item_level2) && (itemint <= item_level3))) {
            chance_value = chance_level2;
            price = price2;
        } else if (((itemstr > item_level1) && (itemstr <= item_level2)) || ((itemdex > item_level1) && (itemdex <= item_level2)) || ((itemluk > item_level1) && (itemluk <= item_level2)) || ((itemint > item_level1) && (itemint <= item_level2))) {
            chance_value = chance_level1;
            price = price1;
        }
        text = "#eȷ��Ҫ����#r#v" + item.getItemId() + "# " + "���ɹ�����Ϊ��" + chance_value + "%\r\n";
        text += "������" + itemstr + "\r\n";
        text += "���ݣ�" + itemdex + "\r\n";
        text += "������" + itemluk + "\r\n";
        text += "������" + itemint + "\r\n";
        text += "������" + itemwatk + "\r\n";
        text += "ħ����" + itemmatk + "\r\n";
        cm.sendNextPrev(text);
    } else if (status == 3) {
        if (itemspeed >= 295) {
            cm.sendOk("��װ���޷�����");
            status = 0;
            cm.dispose();
        } else {
            if (cm.getPlayer().getItemQuantity(4001126, false) < price)
                //if(cm.getPlayer().getItemQuantity(04001126, false) < price)
            {
                cm.sendOk("�������ϲ��㣬����ʧ�ܣ�������Ҫ" + price + "��#r#v04001126#���㵱ǰ��" + cm.getPlayer().getItemQuantity(4001126, false));
                status = 0;
                cm.dispose();
            } else {
                if (chance_random <= chance_value) {
                    cm.removeSlot(1, deleteSlot, 1);//��һ������1��װ�������ڶ��ز����Ǳ����е�λ�ã�����������������
                    cm.gainItem(4001126, -price);
                    if (itemspeed > 40)
                        speed_random = 0;
                    if (itemjump > 23)
                        jump_random = 0;																		// str,dex,luk,int,hp,mp,watk,matk,wdef,mdef,avoid,acc,jump,speed,time
                    cm.gainItem(itemid, itemstr + str_random, itemdex + dex_random, itemluk + luk_random, itemint + int_random, itemhp + hp_random, itemmp + mp_random, itemwatk + watk_random, itemmatk + matk_random, itemwdef + wdef_random, itemmdef + mdef_random, itemavoid + avoid_random, itemacc + acc_random, itemjump + jump_random, itemspeed + speed_random);//item.getItemId(),  ,speed=295

                    text = "#e�ɹ�Ϊװ���������ԣ�#b\r\n";
                    text += "������" + itemstr + "+" + str_random + "\r\n";
                    text += "���ݣ�" + itemdex + "+" + dex_random + "\r\n";
                    text += "������" + itemluk + "+" + luk_random + "\r\n";
                    text += "������" + itemint + "+" + int_random + "\r\n";
                    text += "������" + itemwatk + "+" + watk_random + "\r\n";
                    text += "ħ����" + itemmatk + "+" + matk_random + "\r\n";
                    cm.sendOk(text);
                    cm.����(1, "��ϲ��ң�[" + cm.getName() + "]�ɹ�������װ�����������ͽ������쵽���°ɣ�");
                    //1��ɫ��2��ɫ��3��ɫ����
                    status = 0;
                    cm.dispose();
                } else {
                    cm.sendOk("����ʧ�ܣ����ٽ�������");
                    cm.gainItem(4001126, -price);
                    status = 0;
                    cm.dispose();
                }
            }
        }
    }
}
