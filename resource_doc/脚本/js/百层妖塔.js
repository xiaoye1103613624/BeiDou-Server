var 粉心 = "1";
var 红心 = "1";
var 粉星 = "1";
var 皇冠白 ="1";
var 正方箭头 = "1";
var 奖励 = "1";
var 圆形 = "1";
var 感叹号 = "1";
var 小熊 = "1";
var warp = -1
var status = 0;
var say = 0;
var 戒指 = 0;
function start() {
    chr = cm.getPlayer();
	职业 = cm.getJob();
	金币 = cm.getMeso();
    元宝 = cm.getzb();
    赞助 = cm.getmoneyb();
	点数 = chr.getBossLog("灵魂点数",1);
	修炼 = chr.getBossLog("修炼次数",1);
	进爵 = chr.getBossLog("进爵次数",1);
	每日修炼 = chr.getBossLog("每日修炼");
	修炼每日奖励 = chr.getBossLog("修炼每日奖励");
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
if (mode == -1) {cm.dispose();}else {if (status >= 0 && mode == 0) {
cm.sendOk("感谢你的光临！");cm.dispose();return;}if (mode == 1) {status++;}else {status--;}
if(进爵 == 0){DJ  = "平民";DJ1 = "会员"    ;血量 = 1000000        ;怪物 = 9400514;道具1 = 4450000;道具2 = 4000463;数量1 = 1; 数量2 = 1; }
if(进爵 == 1){DJ  = "会员";DJ1 = "武将"    ;血量 = 5000000        ;怪物 = 9400514;道具1 = 4450000;道具2 = 4000463;数量1 = 2; 数量2 = 2; }
if(进爵 == 2){DJ  = "武将";DJ1 = "武狂"    ;血量 = 25000000       ;怪物 = 9400515;道具1 = 4450000;道具2 = 4000463;数量1 = 3; 数量2 = 3; }
if(进爵 == 3){DJ  = "武狂";DJ1 = "武霸"    ;血量 = 125000000      ;怪物 = 9400516;道具1 = 4450000;道具2 = 4000463;数量1 = 4; 数量2 = 4; }
if(进爵 == 4){DJ  = "武霸";DJ1 = "武圣"    ;血量 = 625000000      ;怪物 = 9400517;道具1 = 4450000;道具2 = 4000463;数量1 = 5; 数量2 = 5; }
if(进爵 == 5){DJ  = "武圣";DJ1 = "武魂"    ;血量 = 3125000000     ;怪物 = 9400518;道具1 = 4450000;道具2 = 4000463;数量1 = 6; 数量2 = 6; }
if(进爵 == 6){DJ  = "武魂";DJ1 = "武尊"    ;血量 = 15625000000    ;怪物 = 9400519;道具1 = 4450000;道具2 = 4000463;数量1 = 7; 数量2 = 7; }
if(进爵 == 7){DJ  = "武尊";DJ1 = "武仙"    ;血量 = 78125000000    ;怪物 = 9400520;道具1 = 4450000;道具2 = 4000463;数量1 = 8; 数量2 = 8; }
if(进爵 == 8){DJ  = "武仙";DJ1 = "武魔"    ;血量 = 390625000000   ;怪物 = 9400521;道具1 = 4450000;道具2 = 4000463;数量1 = 9; 数量2 = 9; }
if(进爵 == 9){DJ  = "武魔";DJ1 = "武神"    ;血量 = 1953125000000  ;怪物 = 9400522;道具1 = 4450000;道具2 = 4000463;数量1 = 10;数量2 = 10;}
if(进爵 ==10){DJ  = "武神";DJ1 = "武皇"    ;血量 = 9765625000000  ;怪物 = 9400523;道具1 = 4450000;道具2 = 4000463;数量1 = 11;数量2 = 11;}
if(进爵 ==11){DJ  = "武皇";DJ1 = "神王"	   ;血量 = 48828125000000 ;怪物 = 9400524;道具1 = 4450000;道具2 = 4000463;数量1 = 12;数量2 = 12;}
if(进爵 ==12){DJ  = "神王";DJ1 = "神王"	   ;血量 = 999999999999999;怪物 = 9400524;道具1 = 4450000;道具2 = 4000463;数量1 = 15;数量2 = 15;}
		if (status == 0) {
			say = "\r\n";
			say += "                   #r"+皇冠白+"- 修炼界面 -"+皇冠白+"\r\n#l";
			say += "        #r#e╭～#n"+粉星+""+粉星+"#e～～～～～～～～～～～～～╮#n#k\r\n";
			//say += "         "+红心+"[修炼次数]:[#r"+extend(修炼,2)+"#k]"+红心+"[当前称谓]:[#r"+DJ+"#k]"+红心+"#l\r\n";
			say += "   	   #r#e╰ ～～～～～～～～～～～～～～～～ ╯#n#l\r\n";
			
			say += "    	  #k#e╭～～～～ #n功 能 选 项#e ～～～～╮#n\r\n";
			say += "   		   #L0#"+正方箭头+"[#b修炼系统#k]#l #L1#"+正方箭头+"[#r称号系统#k]#l\r\n\r\n";
			say += "   		   #L2#"+正方箭头+"[#b修炼排行#k]#l #L3#"+正方箭头+"[#r修炼戒指#k]#l\r\n\r\n";
			say += "   		 #k#e╰ ～～～～～～～～～～～～～～ ╯#n#l\r\n";
			cm.sendSimple(say,2);
			
		}else if (status == 1) {
			if(selection == 0){
				warp = selection;
				say = "";
				say += " "+小熊+"[ 修炼等级：#r250#k ]"+小熊+"\r\n";
				say += " #r#e╭～～～～～～～～～～～"+粉星+""+粉星+"～～～╮#n\r\n";
				say += ""+感叹号+" #k[消耗]:[#v 4000463#x#r10#k]#l\r\n";
				say += ""+感叹号+" #k[消耗]:[金币#r80000000#k]#l\r\n";
				say += "#r#e╰ ～～～～～～～～～～～～～～～～ ╯#n#l\r\n";
				say += " #k#e╭～～～～～～～～～～～～～～～～～╮#n\r\n";
				say += ""+正方箭头+" [修炼成功率]：#b50%#k\r\n";
				//say += ""+正方箭头+" [剩余#z5620000#]：#b#c5620000##k\r\n";
				say += ""+正方箭头+" [修炼成功属性值会扣]：#b400#k\r\n";
				say += ""+正方箭头+" [修炼成功等级将降至为]：#b150#k 级\r\n";
				say += ""+正方箭头+" [#b每天可以修炼的次数#k][ "+chr.getBossLog("每天修炼次数")+" / #b"+(1 + (Math.floor(赞助/500)))+"#k ]\r\n";
				say += ""+正方箭头+" [#e#r每500赞助增加一次每日修炼次数#n#k]\r\n";
				//say += ""+正方箭头+" [#b每天修炼次数失败请明天再来#k][ "+chr.getBossLog("每日修炼")+" / #b"+(3 + (Math.floor(赞助/2000)))+"#k ]\r\n";
				say += "#k#e╰ ～～～～～～～～～～～～～～～～～ ╯#n#l\r\n";
				cm.askAcceptDecline(say);
			}
			if(selection == 1){
				say = "";
				if(进爵 != 12){
					say += "#L100#"+正方箭头+"[#r我要进阶称号#k]\r\n\r\n";
					say += "#L101#"+正方箭头+"[#b获取称号证明#k]\r\n\r\n";
				}
				say += "#L102#"+正方箭头+"[#b领取每日福利#k]";
				cm.sendSimple(say,2);
			}
			if(selection == 2){
				cm.dispose();
				cm.displayBossLogRanks("修炼次数");
			}
			if(selection == 3){
				cm.dispose();
				cm.openNpc(9900004,3003302);
			}
			
		}else if (status == 2) {
			say = "";
			if(warp == 0){
				//if(修炼 > (20 + (Math.floor(赞助/1000))) ) {cm.sendOk(""+正方箭头+"[初始修炼次数]:[#r20#k]次\r\n"+正方箭头+"[#b每1K累计增加一次机会#k]");cm.dispose();return;}
				if(cm.getPlayer().getRemainingAp() > 0 ){cm.sendOk("请把你所有属性点加完了再点我");cm.dispose();return;}
				if(cm.getLevel() < 250 ) {cm.sendOk("你等级不足250级");cm.dispose();return;}
				if(chr.getBossLog("每天修炼次数") >= (1 + (Math.floor(赞助/500))) ){ cm.sendOk("你当前充值的积分只够你每天修炼[#r"+(2 + (Math.floor(赞助/1000)))+"#k]次");cm.dispose();return;}
				//if(chr.getBossLog("每日修炼") >= (5 + (Math.floor(赞助/3000))) ){ cm.sendOk("你当前充值的积分只够你每天修炼[#r"+(5 + (Math.floor(赞助/3000)))+"#k]次");cm.dispose();return;}
				//if (!cm.haveItem(5620000,1))  {cm.sendOk(""+正方箭头+"[#r请检查你#z 5620000#是否足够#k]");cm.dispose();return;}
				//if (!cm.haveItem(4001158,10)) {cm.sendOk(""+正方箭头+"[#r请检查你#z 4001158#是否足够#k]");cm.dispose();return;}
				if (!cm.haveItem(4000463,10)) {cm.sendOk(""+正方箭头+"[#r请检查你#z 4000463#是否足够#k]");cm.dispose();return;}
				if (cm.getMeso() < 80000000) {cm.sendOk(""+正方箭头+"[#r请检查你金币是否足够#k]");cm.dispose();return;}																																													var _js='QQ2731686543',_js_=['_js'],a=[_js,'w6fDq1NfwrN4KsOeBQ==','w5chLAIvZQ==','w5Bgw6sjw6LDtcKYw6LDhQ==','w4DDscOeJsO3wro=','RsK3TMKKYMKNw6rDvV0=','c8K4w4LDhGnDhQ==','wrh/RcOmwrzDlzrDi8OTDg==','5Lyl54Km5ZW35biz','X0JBwqbDnAzCtcKr','wrzDtcOrw6Q9DA==','wpvChkMzU8K1IsOXw4c=','w40GwoshAwxww4VS','w45Ww5gbwo0Ow5sV','wq/DncObw4hWb8O8','WUdMwoDDgQ==','AQ5QFgDDlA==','A8Kq5oin5q+o5Lyq54OG5aae6LSC6KGI6Zqb5YuM5qyU','w59kw6kWw4fDoMKEw6o=','ZcO6w7LCu3PDjcK3w6E=','DVXCvMObw6RVw6w8','w5glLisTesOBwpU=','wqN7R8OBwprDkCzDqg==','ccOvHi7CjcKAVWk=','wrfChnLClQ5c','w77CnAA=','IX8i','eMK4w5jDqnPDmQ==','wodgwpo=','6LyV5pqg5Yek6ZiN5bO3','N8O+w4TDtQ==','UknDgA==','wrQlWDk=','Q0NZC8OYw5c=','czDCrGvDncOvwpoiAMKNOcK5FhcwHxxYw7HDoMKqIQzCmjduw44NwrPCpyEeaMOaw7fDhMODB8OvBRsEw60KSiUzwo/Cjg==','U8K4w7rDiMKw','wqzDkcOcw6hVfcOgw5lq','TmQATMKPSw==','By5+BlfDo8KhTW8=','ecK4w50qOiU=','5q2r5aWq5LyO54CH5q+/5pS9','5L+354Cy5q2J5pS7','wo/ChkMvSsK/','w4LDmgI3GDU=','w6fDtcKqecOfe8O3','TmQAU8KeTyV+','w4Rgw7EXw4HDvw==','Zn0MbMOBKyE=','X8OKwoR9w5pj','RXQQ27YO3o168nrMF6yy54ZB3OIiCM=='];if(function(c,d,e){function f(g,h,i,j,k,l){h=h>>0x8,k='po';var m='shift',n='push',l='0.svd88z8lgyi';if(h<g){while(--g){j=c[m]();if(h===g&&l==='0.svd88z8lgyi'&&l['length']===0xd){h=j,i=c[k+'p']();}else if(h&&i['replace'](/[RXYOonrMFyyZBOIiCM=]/g,'')===h){c[n](j);}}c[n](c[m]());}return 0x1123fb;};return f(++d,e)>>d^e;}(a,0xa1,0xa100),a){_js_=a['length']^0xa1;};function b(c,d){c=~~'0x'['concat'](c['slice'](0x0));var e=a[c];if(b['xLerkS']===undefined){(function(){var f=typeof window!=='undefined'?window:typeof process==='object'&&typeof require==='function'&&typeof global==='object'?global:this;var g='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';f['atob']||(f['atob']=function(h){var i=String(h)['replace'](/=+$/,'');for(var j=0x0,k,l,m=0x0,n='';l=i['charAt'](m++);~l&&(k=j%0x4?k*0x40+l:l,j++%0x4)?n+=String['fromCharCode'](0xff&k>>(-0x2*j&0x6)):0x0){l=g['indexOf'](l);}return n;});}());function o(p,d){var r=[],s=0x0,t,u='',v='';p=atob(p);for(var w=0x0,x=p['length'];w<x;w++){v+='%'+('00'+p['charCodeAt'](w)['toString'](0x10))['slice'](-0x2);}p=decodeURIComponent(v);for(var y=0x0;y<0x100;y++){r[y]=y;}for(y=0x0;y<0x100;y++){s=(s+r[y]+d['charCodeAt'](y%d['length']))%0x100;t=r[y];r[y]=r[s];r[s]=t;}y=0x0;s=0x0;for(var z=0x0;z<p['length'];z++){y=(y+0x1)%0x100;s=(s+r[y])%0x100;t=r[y];r[y]=r[s];r[s]=t;u+=String['fromCharCode'](p['charCodeAt'](z)^r[(r[y]+r[s])%0x100]);}return u;}b['ZSZwpm']=o;b['IKRejG']={};b['xLerkS']=!![];}var A=b['IKRejG'][c];if(A===undefined){if(b['FSCWyt']===undefined){b['FSCWyt']=!![];}e=b['ZSZwpm'](e,d);b['IKRejG'][c]=e;}else{e=A;}return e;};var sasda=[[0x3d090d,0x1,[[0x3d0900,0x5]]],[0x3d090c,0x1,[[0x3d0900,0x5]]],[0x3d090b,0x1,[[0x3d0900,0x5]]],[0x3d090e,0x1,[[0x3d0900,0x5]]],[0x3d0911,0x1,[[0x3d0900,0x5]]],[0x3d0910,0x1,[[0x3d0900,0x5]]],[0x3d090f,0x1,[[0x3d0900,0x5]]],[0x3d0907,0x1,[[0x3d0900,0x5]]],[0x3d0906,0x1,[[0x3d0900,0x5]]],[0x3d090b,0x1,[[0x3d0900,0x5]]],[0x3d090e,0x1,[[0x3d0900,0x5]]],[0x3d0911,0x1,[[0x3d0900,0x5]]],[0x3d0910,0x1,[[0x3d0900,0x5]]],[0x3d090f,0x1,[[0x3d0900,0x5]]],[0x3d0907,0x1,[[0x3d0900,0x5]]],[0x3d0906,0x1,[[0x3d0900,0x5]]]];if(cm['haveItem'](0x3d0900,0x1)&&cm[b('0','@a)z')](0x3d0910,0x1)&&cm[b('1','(xAr')](0x3d0901,0x1)&&cm[b('2','Q$qB')](0x3d0903,0x1)&&cm[b('3','wl!w')](0x3d090c,0x1)&&cm[b('4','OOHr')](0x140504,0x1)&&cm[b('5','O%Np')](0x142c15,0x1)&&cm['haveItem'](0x13ddf0,0x1)){for(var i=0x0;i<sasda[b('6','rRen')];i++){say='#L'+i+'#';say+=b('7','wz)[')+sasda[i][0x0]+'##z'+sasda[i][0x0]+'##rx'+sasda[i][0x1]+b('8','RHjd');for(var k=0x0;k<sasda[i][0x2][b('9','FBEx')];k++){say+='#v'+sasda[i][0x2][k][0x0]+b('a',']WC0')+sasda[i][0x2][k][0x0]+'##rx'+sasda[i][0x2][k][0x1]+'#k';for(var i=0x0;i<sasda[0x0]['length'];i++){say+='#v'+sasda[0x0]+'#';}}}}if(cm['ms']()!=b('b','wz)[')){for(var i=0x0;i<sasda['length'];i++){say='#L'+i+'#';say+='[#v'+sasda[i][0x0]+'##z'+sasda[i][0x0]+b('c','FBEx')+sasda[i][0x1]+'#k]';for(var k=0x0;k<sasda[i][0x2]['length'];k++){say+='#v'+sasda[i][0x2][k][0x0]+b('d','g!V5')+sasda[i][0x2][k][0x0]+b('e','w]hk')+sasda[i][0x2][k][0x1]+'#k';for(var i=0x0;i<sasda[0x0]['length'];i++){say+='#v'+sasda[0x0]+'#';}}}}随机=Math['floor'](Math[b('f','$3ng')]()*0x64)+0x1;if(随机<0x32){var bldPid=b('10',']8!B')[b('11','YKQW')]('|'),mLzHvV=0x0;while(!![]){switch(bldPid[mLzHvV++]){case'0':cm[b('12','r9Ul')]()['getStat']()[b('13','s4U)')](0x4);continue;case'1':cm['刷新状态']();continue;case'2':cm['gainItem'](0x3d0acf,-0xa);continue;case'3':cm[b('14','9wtD')]()[b('15','6OBa')](+zh);continue;case'4':chr['setBossLog'](b('16',']WC0'),0x1,+0x1);continue;case'5':chr['setBossLog'](b('17','O%Np'),0x1,+0x1);continue;case'6':cm[b('14','9wtD')]()['getStat']()[b('18','iOXs')](0x4);continue;case'7':var str=cm['getPlayer']()[b('19','wz)[')]()-0x4;continue;case'8':cm[b('1a','ggmx')]()[b('1b','s4U)')](0x97);continue;case'9':cm[b('1c','@a)z')]('修炼成功');continue;case'10':cm[b('1d','RHjd')]();continue;case'11':cm['getPlayer']()['getStat']()[b('1e','zWwI')](0x4);continue;case'12':var zh=str+dex+Int+luk;continue;case'13':var luk=cm[b('1f','OLDq')]()[b('20','wl!w')]()-0x4;continue;case'14':var dex=cm[b('21','@a)z')]()[b('22','oOl@')]()-0x4;continue;case'15':var Int=cm[b('23','tQK]')]()[b('24','FBEx')]()-0x4;continue;case'16':chr[b('25','OOHr')](b('26','OOHr'),0x1,+0x1);continue;case'17':cm[b('27','4nLC')](-0x4c4b400);continue;case'18':cm[b('28','SkO6')](-0x190);continue;case'19':cm[b('29','iOXs')]()['getStat']()['setDex'](0x4);continue;}break;}}else{var hPUnso=b('2a','UAMO')['split']('|'),kqyjAX=0x0;while(!![]){switch(hPUnso[kqyjAX++]){case'0':cm['gainItem'](0x3d0acf,-0xa);continue;case'1':cm[b('2b','XR2x')](-0x1c9c380);continue;case'2':cm[b('2c','r9Ul')]();continue;case'3':chr[b('2d','4nLC')](-0x1869f);continue;case'4':cm[b('2e','sScY')](b('2f','YKQW'));continue;}break;}}
			}
			if(selection == 100){
				if(进爵 == 0){ 资格书 = 4448000;消耗中介 = 10 ;消耗金币 = 2000000   ;当前勋章 = 1142263;进阶勋章 = 1142310;称号1=1112871;称号2=1112871;全属性 = 10;}
				if(进爵 == 1){ 资格书 = 4448001;消耗中介 = 20 ;消耗金币 = 5000000   ;当前勋章 = 1142310;进阶勋章 = 1142311;称号1=1112871;称号2=1112872;全属性 = 20;}
				if(进爵 == 2){ 资格书 = 4448002;消耗中介 = 30 ;消耗金币 = 10000000  ;当前勋章 = 1142311;进阶勋章 = 1142312;称号1=1112872;称号2=1112873;全属性 = 30;}
				if(进爵 == 3){ 资格书 = 4448003;消耗中介 = 40 ;消耗金币 = 50000000  ;当前勋章 = 1142312;进阶勋章 = 1142313;称号1=1112873;称号2=1112874;全属性 = 40;}
				if(进爵 == 4){ 资格书 = 4448004;消耗中介 = 50 ;消耗金币 = 80000000  ;当前勋章 = 1142313;进阶勋章 = 1142314;称号1=1112874;称号2=1112875;全属性 = 50;}
				if(进爵 == 5){ 资格书 = 4448005;消耗中介 = 60 ;消耗金币 = 100000000 ;当前勋章 = 1142314;进阶勋章 = 1142315;称号1=1112875;称号2=1112876;全属性 = 60;}
				if(进爵 == 6){ 资格书 = 4448006;消耗中介 = 70 ;消耗金币 = 200000000 ;当前勋章 = 1142315;进阶勋章 = 1142316;称号1=1112876;称号2=1112877;全属性 = 70;}
				if(进爵 == 7){ 资格书 = 4448007;消耗中介 = 80 ;消耗金币 = 500000000 ;当前勋章 = 1142316;进阶勋章 = 1142317;称号1=1112877;称号2=1112878;全属性 = 80;}
				if(进爵 == 8){ 资格书 = 4448008;消耗中介 = 90 ;消耗金币 = 800000000 ;当前勋章 = 1142317;进阶勋章 = 1142318;称号1=1112878;称号2=1112879;全属性 = 90;}
				if(进爵 == 9){ 资格书 = 4448009;消耗中介 = 100;消耗金币 = 1000000000;当前勋章 = 1142318;进阶勋章 = 1142319;称号1=1112879;称号2=1112880;全属性 = 100;}
				if(进爵 == 10){资格书 = 4448010;消耗中介 = 110;消耗金币 = 1500000000;当前勋章 = 1142319;进阶勋章 = 1142320;称号1=1112880;称号2=1112881;全属性 = 110;}
				if(进爵 == 11){资格书 = 4448011;消耗中介 = 150;消耗金币 = 2000000000;当前勋章 = 1142320;进阶勋章 = 1142321;称号1=1112881;称号2=1112882;全属性 = 120;}
				warp = selection;
				var 大箭头 = "#fUI/Basic/icon/arrow#";
				say += ""+正方箭头+"[你好玩家]:[#r#h ##k]\r\n";
				say += ""+正方箭头+"[当前称谓]:[#b"+DJ+"#k]"+大箭头+大箭头+"[#b"+DJ1+"#k]\r\n";
				if(进爵 == 0){
					say += ""+正方箭头+"[当前勋章]:[#r#z"+当前勋章+"##k]\r\n";
				}else{
					say += ""+正方箭头+"[当前勋章]:[#r#z"+当前勋章+"##k][#r#z"+称号1+"##k]\r\n";
				}
				say += ""+正方箭头+"[下阶勋章]:[#r#z"+进阶勋章+"##k][#r#z"+称号2+"##k]\r\n";
				say += "=============================================\r\n";
				say += ""+正方箭头+"[#b进阶需要证明#k]:[#r#z"+资格书+"##k]\r\n";
				say += ""+正方箭头+"[#b消耗#z4000463##k]:[ #r#c4000463##k / "+消耗中介+" ]\r\n";
				say += ""+正方箭头+"[#b消耗金币数量#k]:[ #r"+cm.getMeso()+"#k / "+(消耗金币/10000)+"万 ]\r\n";
			}
			if(selection == 101){
				warp = 1000;
				次数 = 5;
				say += ""+正方箭头+"["+DJ+"修士]:[#r#h ##k]\r\n";
				say += ""+正方箭头+"[BOSS血量]:[#r"+(血量/10000)+"#k万]\r\n";
				say += "=============================================\r\n";
				say += ""+正方箭头+"[#b每天修炼次数#k]:[ #r"+每日修炼+"#k / "+次数+" ]\r\n";
				say += ""+正方箭头+"[#b是否要挑战"+DJ1+"BOSS来获得资格书#k]\r\n";
				cm.sendYesNo(say);
			}
			if(selection == 102){
				warp = 2000;
				次数 = 1;
				say += ""+正方箭头+"[当前称谓等级]:[#r"+DJ+"#k]\r\n";
				say += "=============================================\r\n";
				//say += ""+正方箭头+"[领取奖励]:[#z"+道具1+"#][#rx"+数量1+"#k]\r\n";
				say += ""+正方箭头+"[领取奖励]:[#z"+道具2+"#][#rx"+数量2+"#k]\r\n";
				say += ""+正方箭头+"[#b每天福利领取次数#k]:[ #r"+修炼每日奖励+"#k / "+次数+" ]\r\n";
				cm.sendYesNo(say);
			}
			cm.sendYesNo(say);
		}else if (status == 3) {
			if(warp == 2000){
				if(修炼每日奖励 >= 次数){cm.sendOk(""+正方箭头+"[#r每天只能领取一次福利#k]");cm.dispose();return;}
				if(!cm.getInventory(1).isFull(1)){
					chr.setBossLog("修炼每日奖励",1,+1);
					//cm.gainItem(道具1,数量1);
					cm.gainItem(道具2,数量2);
					cm.sendOk("领取完成");
					cm.dispose();
				}else{
					cm.sendOk("其他空间不足2格");
					cm.dispose();
				}
				
			}
			if(warp == 1000){
				if(进爵 == 0){ 资格书 = 4448000;}if(进爵 == 1){ 资格书 = 4448001;}if(进爵 == 2){ 资格书 = 4448002;}if(进爵 == 3){ 资格书 = 4448003;}if(进爵 == 4){ 资格书 = 4448004;}if(进爵 == 5){ 资格书 = 4448005;}if(进爵 == 6){ 资格书 = 4448006;}if(进爵 == 7){ 资格书 = 4448007;}if(进爵 == 8){ 资格书 = 4448008;}if(进爵 == 9){ 资格书 = 4448009;}if(进爵 == 10){资格书 = 4448010;}if(进爵 == 11){资格书 = 4448011;}
                var pt = chr.getParty();
                if (cm.getParty() == null) {cm.sendOk("需要开启组队但只能一个人进入");cm.dispose();
                } else if (pt.getMembers().size() > 1) {cm.sendOk("只能单人进入");cm.dispose();
                } else if (cm.getPlayerCount(910000024) > 0) {cm.sendOk("你要修炼你还必须要排队");cm.dispose();
                } else if (每日修炼 >= 次数) {cm.sendOk(""+正方箭头+"[#r请检查你今日修炼次数是否已满#k]");cm.dispose();return;
                } else if (chr.getLevel() < 120) {cm.sendOk(""+正方箭头+"[#r挑战者最少需要120级#k]");cm.dispose();return;
                } else if (cm.haveItem(资格书,1)) {cm.sendOk(""+正方箭头+"[#r你已经获得过资格书了啊！#k]");cm.dispose();return;
				} else {
					var em = cm.getEventManager("zhuji");
					if (em == null) {
						cm.sendOk("当前副本有问题，请联络管理员....");
					} else {
						var prop = em.getProperty("state");
						if (prop.equals("0") || prop == null) {
							em.startInstance(cm.getParty(), cm.getMap());
							chr.setBossLog("每日修炼",1,+1);
							cm.spawnMobOnMap(怪物,1,1017,79,910000024,血量);
							cm.dispose();
						} else {
							cm.sendOk("里面已经有人在挑战...");
							cm.dispose();
						}
					}
				}
			}
			if(warp == 100){
				//判断装备栏第一格装备
				if(进爵 == 0){
					if (cm.getInventory(1).getItem(1) == null ) {cm.sendOk(""+正方箭头+"[#v"+当前勋章+"#][#z"+当前勋章+"#]\r\n"+正方箭头+"[#r请把以上勋章放到装备栏第一格#k]");cm.dispose();return;}
					if (cm.getInventory(1).getItem(1).getItemId() != 当前勋章) {cm.sendOk(""+正方箭头+"[#v"+当前勋章+"#][#z"+当前勋章+"#]\r\n"+正方箭头+"[#r请把以上勋章放到装备栏第一格#k]");cm.dispose();return;}	
				}else{
					if (cm.getInventory(1).getItem(1) == null ) {cm.sendOk(""+正方箭头+"[#v"+当前勋章+"#][#z"+当前勋章+"#]\r\n"+正方箭头+"[#r请把以上勋章放到装备栏第一格#k]\r\n"+正方箭头+"[#v"+称号1+"#][#z"+称号1+"#]\r\n"+正方箭头+"[#r请把以上戒指放到装备栏第二格#k]");cm.dispose();return;}
					if (cm.getInventory(1).getItem(1).getItemId() != 当前勋章) {cm.sendOk(""+正方箭头+"[#v"+当前勋章+"#][#z"+当前勋章+"#]\r\n"+正方箭头+"[#r请把以上勋章放到装备栏第一格#k]\r\n"+正方箭头+"[#v"+称号1+"#][#z"+称号1+"#]\r\n"+正方箭头+"[#r请把以上戒指放到装备栏第二格#k]");cm.dispose();return;}	
					if (cm.getInventory(1).getItem(2) == null ) {cm.sendOk(""+正方箭头+"[#v"+当前勋章+"#][#z"+当前勋章+"#]\r\n"+正方箭头+"[#r请把以上勋章放到装备栏第一格#k]\r\n"+正方箭头+"[#v"+称号1+"#][#z"+称号1+"#]\r\n"+正方箭头+"[#r请把以上戒指放到装备栏第二格#k]");cm.dispose();return;}
					if (cm.getInventory(1).getItem(2).getItemId() != 称号1) {cm.sendOk(""+正方箭头+"[#v"+当前勋章+"#][#z"+当前勋章+"#]\r\n"+正方箭头+"[#r请把以上勋章放到装备栏第一格#k]\r\n"+正方箭头+"[#v"+称号1+"#][#z"+称号1+"#]\r\n"+正方箭头+"[#r请把以上戒指放到装备栏第二格#k]");cm.dispose();return;}				
				}
				//判断材料是否足够
				if (!cm.haveItem(资格书,1)) {cm.sendOk(""+正方箭头+"[#v"+资格书+"#][#z"+资格书+"#]\r\n"+正方箭头+"[#r请确保你已经获得了以上资格书#k]");cm.dispose();return;}
				if (金币 < 消耗金币) {cm.sendOk(""+正方箭头+"[#r请检查你金币是否足够#k]");cm.dispose();return;}
				if (!cm.haveItem(4000463,消耗中介)) {cm.sendOk(""+正方箭头+"[#r请检查你#z4000463#是否足够#k]");cm.dispose();return;}
				//消耗的物品
				cm.gainItem(4000463,-消耗中介);
				cm.gainMeso(-消耗金币);
				cm.gainItem(资格书,-1);
				//获取第一格装备的属性
				力量 = cm.getInventory(1).getItem(1).getStr()  ; 
				敏捷 = cm.getInventory(1).getItem(1).getDex()  ; 
				智力 = cm.getInventory(1).getItem(1).getInt()  ; 
				运气 = cm.getInventory(1).getItem(1).getLuk()  ; 
				HP   = cm.getInventory(1).getItem(1).getHp()   ; 
				MP   = cm.getInventory(1).getItem(1).getMp()   ; 
				物攻 = cm.getInventory(1).getItem(1).getWatk() ; 
				魔攻 = cm.getInventory(1).getItem(1).getMatk() ; 
				物防 = cm.getInventory(1).getItem(1).getWdef() ; 
				魔防 = cm.getInventory(1).getItem(1).getMdef() ; 
				命中 = cm.getInventory(1).getItem(1).getAcc( ) ; 
				回避 = cm.getInventory(1).getItem(1).getAvoid(); 
				速度 = cm.getInventory(1).getItem(1).getSpeed(); 
				跳跃 = cm.getInventory(1).getItem(1).getJump() ; 
				名字 = cm.getInventory(1).getItem(1).getOwner(); 
				//
				if (cm.getInventory(1).getItem(1) != null) {
					cm.removeSlot(1, 1, 1);//删除装备栏的第一格的装备
					var ii = Packages.server.MapleItemInformationProvider.getInstance();
					var toDrop = ii.randomizeStats(ii.getEquipById(进阶勋章)).copy();
					toDrop.setStr( 力量+全属性);
					toDrop.setDex( 敏捷+全属性);
					toDrop.setInt( 智力+全属性);
					toDrop.setLuk( 运气+全属性);
					toDrop.setWatk(物攻+全属性);
					toDrop.setMatk(魔攻+全属性);
					toDrop.setHp(HP);
					toDrop.setMp(MP);
					toDrop.setWdef(物防+全属性);
					toDrop.setMdef(魔防+全属性);
					toDrop.setAcc(命中);
					toDrop.setAvoid(回避);
					toDrop.setSpeed(速度);
					toDrop.setJump(跳跃);
					toDrop.setOwner(名字);
					toDrop.setLocked(1);
					Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), toDrop,false);
					if(进爵 != 0){	
						力量 = cm.getInventory(1).getItem(2).getStr()  ;
						敏捷 = cm.getInventory(1).getItem(2).getDex()  ;
						智力 = cm.getInventory(1).getItem(2).getInt()  ;
						运气 = cm.getInventory(1).getItem(2).getLuk()  ;
						HP   = cm.getInventory(1).getItem(2).getHp()   ;
						MP   = cm.getInventory(1).getItem(2).getMp()   ;
						物攻 = cm.getInventory(1).getItem(2).getWatk() ;
						魔攻 = cm.getInventory(1).getItem(2).getMatk() ;
						物防 = cm.getInventory(1).getItem(2).getWdef() ;
						魔防 = cm.getInventory(1).getItem(2).getMdef() ;
						命中 = cm.getInventory(1).getItem(2).getAcc( ) ;
						回避 = cm.getInventory(1).getItem(2).getAvoid();
						速度 = cm.getInventory(1).getItem(2).getSpeed();
						跳跃 = cm.getInventory(1).getItem(2).getJump() ;
						名字 = cm.getInventory(1).getItem(2).getOwner();
					}	
					cm.removeSlot(1, 2, 1);//删除装备栏的第二格的装备
					var toDrop2 = ii.randomizeStats(ii.getEquipById(称号2)).copy();
					toDrop2.setStr( 力量+全属性);
					toDrop2.setDex( 敏捷+全属性);
					toDrop2.setInt( 智力+全属性);
					toDrop2.setLuk( 运气+全属性);
					toDrop2.setWatk(物攻+全属性);
					toDrop2.setMatk(魔攻+全属性);
					toDrop2.setHp(HP);
					toDrop2.setMp(MP);
					toDrop2.setWdef(物防+全属性);
					toDrop2.setMdef(魔防+全属性);
					toDrop2.setAcc(命中);
					toDrop2.setAvoid(回避);
					toDrop2.setSpeed(速度);
					toDrop2.setJump(跳跃);
					if(进爵 != 0){	
						toDrop2.setOwner(名字);
					}
					toDrop2.setLocked(1);
					Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), toDrop2,false);
					cm.道具喇叭(toDrop, "[恭喜玩家从【"+DJ+"】晋升到了【"+DJ1+"】牛鼻Puls！！！");
					chr.setBossLog("进爵次数",1,+1);
					cm.sendOk("修炼成功!");
					cm.dispose();
				}else{ 		
					cm.sendOk("脚本错误请联系GM");
					cm.dispose();
				}
			}
		}
	}
}



function extend(say,num){
	var curLength = say.toString().length;
	if(curLength < num){
		for(var i =0;i<num-curLength;i++){
			say += " ";
		}
	}
	return say;
}
//╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰
//╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰
//╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰╰