/* ==================
 �ű�����: ÿ�ո���һ����, ͬ�˺Ų�ͬ��ɫ����   
 �ű����ߣ�Ұԭ��־ 
 ��ϵ��ʽ��871337167
 =====================
 */
var random1=java.lang.Math.floor(4E5*Math.random()+2E5),itemSetSel=Math.random(),itemSet,itemSetQty,hasQty=!1,prizeIdEtc=[4170002,4170005,4170001,4170006,4170009],prizeIdEtc1=[4000464,4310097,4310098,4310156,2531000,4310174];
var myDate = new Date();
var year = myDate.getFullYear();
var month = myDate.getMonth() + 1;
var days = myDate.getDate();
var DatabaseConnection = Java.type('database.DatabaseConnection');


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } 
	else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("��л��Ĺ��٣�");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } 
		else {
            status--;
        }
        if (status == 0) {
			var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            //text += "\t\t  #r#e��ӭ��������#k��������#n\r\n\r\n"
			text += "������������:#v4170002##v4170005##v4170001##v4170006##v4170009##l\r\n#k";
			text += "ȫ����������:#v4310174#*2#v4001126#*500#v4000038#*100#v4000313#*100#l\r\n#v4310108##v4170007##v4170016##v2049100##v2340000##v2049124##v4310156##v4310097##v4310098#*1\r\n�������:#v4310174##v2049124##v4000464##v4310097##v4310098##v4310156##v2531000##v2049104##l\r\n\r\n#k";
			text += "#r#L1##nͨ�ط�������#k[#b" + (getBossLog1("ÿ�շ���") < 0 ? 0 : getBossLog1("ÿ�շ���"))  + "#k/#r1#k]�� ��������#k[#b" + (getBossLog1("ÿ�շ�������") < 0 ? 0 : getBossLog1("ÿ�շ�������")) + "#k/#r1#k]�Ρ�#l\r\n";		
            text += "#r#L2##nͨ���������#k[#b" + (getBossLog1("ÿ�����") < 0 ? 0 : getBossLog1("ÿ�����"))  + "#k/#r1#k]�� ��������#k[#b" + (getBossLog1("ÿ����ս���") < 0 ? 0 : getBossLog1("ÿ����ս���")) + "#k/#r1#k]�Ρ�#l\r\n";
            text += "#r#L3##nͨ���������#k[#b" + (getBossLog1("ÿ�����") < 0 ? 0 : getBossLog1("ÿ�����"))  + "#k/#r1#k]�� ��������#k[#b" + (getBossLog1("ÿ����߽���") < 0 ? 0 : getBossLog1("ÿ����߽���")) + "#k/#r1#k]�Ρ�#l\r\n#k";
			text += "#r#L4##nͨ�غ�������#k[#b" + (getBossLog1("ÿ�պ���") < 0 ? 0 : getBossLog1("ÿ�պ���"))  + "#k/#r1#k]�� ��������#k[#b" + (getBossLog1("ÿ�պ�������") < 0 ? 0 : getBossLog1("ÿ�պ�������")) + "#k/#r1#k]�Ρ�#l\r\n";
            text += "#r#L5##nͨ�ض�������#k[#b" + (getBossLog1("ÿ�ն���") < 0 ? 0 : getBossLog1("ÿ�ն���")) + "#k/#r1#k]�� ��������#k[#b" + (getBossLog1("ÿ�ն�������") < 0 ? 0 : getBossLog1("ÿ�ն�������")) + "#k/#r1#k]�Ρ�#l\r\n\r\n#k";
			text += "#r#L6##n�������������ȡ��#k[#b" + (getBossLog1("ÿ�ո������") < 0 ? 0 :  getBossLog1("ÿ�ո������")) + "#k/#r1#k]�� \r\n#l\r\n#k";
            cm.sendSimple(text); // 使用sendSimple支持#L选择项，sendOk只有一个确定按钮无法选择 
       } 
	   else if (selection == 1) {
            if (getBossLog1("ÿ�շ���") < 1) {
                cm.sendOk("ͨ�ش���δ��ɣ���ǰ����ˣ�" + getBossLog1("ÿ�շ���") + "�Ρ�");
                cm.dispose();
			} else if (getBossLog1("ÿ�շ�������") >= 1) {
                cm.sendOk("���Ѿ���ȡ���˱��ν�����");
                cm.dispose();
            } else {
				itemSet = prizeIdEtc;
				var sel = Math.floor(Math.random() * itemSet.length);
				var qty = 1;
				cm.gainItem(itemSet[sel], qty);	
				setBossLog1("ÿ�շ�������");
                setBossLog1("ÿ�ո������");
                cm.sendOk("�����Է����������ڣ���ȷ�ϡ�");
				cm.worldMessage(6,"��ϲ["+cm.getName()+"]��ȡ�˸���һ��������--��������!");
				status = -1;
            }
		}  
		else if (selection == 2) {
            if (getBossLog1("ÿ�����") < 1) {
                cm.sendOk("ͨ�ش���δ��ɣ���ǰ����ˣ�" + getBossLog1("ÿ�����") + "�Ρ�");
                cm.dispose();
			} else if (getBossLog1("ÿ����ս���") >= 1) {
                cm.sendOk("���Ѿ���ȡ���˱��ν�����");
                cm.dispose();
            } else {
				itemSet = prizeIdEtc;
				var sel = Math.floor(Math.random() * itemSet.length);
				var qty = 1;
				cm.gainItem(itemSet[sel], qty);
				setBossLog1("ÿ����ս���");
                setBossLog1("ÿ�ո������");
                cm.sendOk("�����Է����������ڣ���ȷ�ϡ�");
				cm.worldMessage(6,"��ϲ["+cm.getName()+"]��ȡ�˸���һ��������--��ս���!");
				status = -1;
            }
		} 
		else if (selection == 3) {
            if (getBossLog1("ÿ�����") < 1) {
                cm.sendOk("ͨ�ش���δ��ɣ���ǰ����ˣ�" + getBossLog1("ÿ�����") + "�Ρ�");
                cm.dispose();
			} else if (getBossLog1("ÿ����߽���") >= 1) {
                cm.sendOk("���Ѿ���ȡ���˱��ν�����");
                cm.dispose();
            } else {
				itemSet = prizeIdEtc;
				var sel = Math.floor(Math.random() * itemSet.length);
				var qty = 1;
				cm.gainItem(itemSet[sel], qty);
				setBossLog1("ÿ����߽���");
                setBossLog1("ÿ�ո������");
                cm.sendOk("�����Է����������ڣ���ȷ�ϡ�");
				cm.worldMessage(6,"��ϲ["+cm.getName()+"]��ȡ�˸���һ��������--��߽���!");
				status = -1;
            }
		} 
		else if (selection == 4) {
            if (getBossLog1("ÿ�պ���") < 1) {
                cm.sendOk("ͨ�ش���δ��ɣ���ǰ����ˣ�" + getBossLog1("ÿ�պ���") + "�Ρ�");
                cm.dispose();
			} else if (getBossLog1("ÿ�պ�������") >= 1) {
                cm.sendOk("���Ѿ���ȡ���˱��ν�����");
                cm.dispose();
            } else {
				itemSet = prizeIdEtc;
				var sel = Math.floor(Math.random() * itemSet.length);
				var qty = 1;
				cm.gainItem(itemSet[sel], qty);
				setBossLog1("ÿ�պ�������");
                setBossLog1("ÿ�ո������");
                cm.sendOk("�����Է����������ڣ���ȷ�ϡ�");
				cm.worldMessage(6,"��ϲ["+cm.getName()+"]��ȡ�˸���һ��������--��������!");
				status = -1;
            }
		} 
		else if (selection == 5) {
            if (getBossLog1("ÿ�ն���") < 1) {
                cm.sendOk("ͨ�ش���δ��ɣ���ǰ����ˣ�" + getBossLog1("ÿ�ն���") + "�Ρ�");
                cm.dispose();
			} else if (getBossLog1("ÿ�ն�������") >= 1) {
                cm.sendOk("���Ѿ���ȡ���˱��ν�����");
                cm.dispose();
            } else {
				itemSet = prizeIdEtc;
				var sel = Math.floor(Math.random() * itemSet.length);
				var qty = 1;
				cm.gainItem(itemSet[sel], qty);
				setBossLog1("ÿ�ն�������");
                setBossLog1("ÿ�ո������");
                cm.sendOk("�����Է����������ڣ���ȷ�ϡ�");
				cm.worldMessage(6,"��ϲ["+cm.getName()+"]��ȡ�˸���һ��������--���ｱ��!");
				status = -1;
            }
		} 
		else if (selection == 6) {//1��#v4310156#+�����ý��20-100��
			if (cm.getInventory(4).isFull(0)){//�жϵ��ĸ�Ҳ������������װ�����Ƿ���һ���ո�
			cm.sendOk("#b�뱣֤������λ������1���ո�,�����޷��һ�.");
			cm.dispose();
			} else if (cm.getInventory(2).isFull(0)){//�жϵڶ���Ҳ������������װ�����Ƿ���һ���ո�
			cm.sendOk("#b�뱣֤������λ������1���ո�,�����޷��һ�.");
			cm.dispose();
			} else if (getBossLog1("ÿ�ո������") < 5) {
				cm.sendOk("ͨ�ش���δ��ɣ���ǰ����ˣ�" + getBossLog1("ÿ�ո������") + "�Ρ�");
				cm.dispose();
			} else if (getBossLog1("����һ��������") >= 1) {
				cm.sendOk("���Ѿ���ȡ���˱��ν�����");
				cm.dispose();
			} else {
				itemSet=prizeIdEtc1;var sel=Math.floor(Math.random()*itemSet.length),qty=1;
				cm.gainMeso(20000000);
				cm.gainItem(itemSet[sel], qty);
				cm.gainItem(4310174, 2);
				cm.gainItem(4001126,500);
				cm.gainItem(4000038, 100);
				cm.gainItem(4000313, 100);
				cm.gainItem(4310108, 1);
				cm.gainItem(4170007, 1);
				cm.gainItem(4170016, 1);
				cm.gainItem(2049100, 1);
				cm.gainItem(2340000, 1);
				cm.gainItem(2049124, 1);
				cm.gainItem(4310156, 1);
				cm.gainItem(2100009, 1);
				cm.gainItem(4310097, 1);
				cm.gainItem(4310098, 1);
				setBossLog1("����һ��������");
				cm.sendOk("�����Է����������ڣ���ȷ�ϡ�");
				cm.����(3, "��� "+cm.getName()+" ���ȫ��һ������������ô�������!");
				status = -1;
			}
		}
	}
}

function setBossLog1(log) {
	var id = cm.getPlayer().getId();
    var con1 = DatabaseConnection.getConnection();
	var day = ""+year+"-"+month+"-"+days+"";
    var ps = con1.prepareStatement("insert into bosslog1 (characterid, bossid, count, time) values (?,?,?,?)");
    ps.setInt(1, id);
    ps.setString(2, log);
	ps.setInt(3, 1);
	ps.setString(4, day);
    ps.executeUpdate();
    ps.close();
}

function getBossLog1(log) {
		var id = cm.getPlayer().getId();
        var con = DatabaseConnection.getConnection();
        var count = 0;
        var ps;
        ps = con.prepareStatement("SELECT * FROM bosslog1 WHERE characterid = ? and bossid = ? and time = CURDATE()");
        ps.setInt(1, id);
		ps.setString(2, log);
        var rs = ps.executeQuery();
        if (rs.next()) {
            count = rs.getInt("count");
        } else {
            count = 0;
        }
        rs.close();
        ps.close();
        return count;
}