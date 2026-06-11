var status = 0;
var job;


function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if ((mode == 0 && status == 2) || (mode == 0 && status == 13)) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			//var password = cm.getGMPassWord();//获取源里的管理员密码
			var text  = "请输入管理员密码以使用功能"
			cm.sendGetNumber(text ,0, 1, 100000000 )
		}else if(status == 1){
			
			var password = cm.getGMPassWord();
			if(selection == password){//对比成功
				//逻辑处理 判断管理员等级
				cm.openNpc(9900004,"怪物攻城");	
				//cm.sendOk("1")
				//cm.dispose();
			}else{
				//逻辑处理
				cm.sendOk("本次操作失败！\r\n\r\n#r#e已在服务端生成异常操作文档")
				//cm.dispose();
			}
		}
	}
}	
