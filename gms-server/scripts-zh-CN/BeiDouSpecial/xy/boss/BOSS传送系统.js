// ================================================================
// BOSS传送系统（纯脚本实现，无数据库表，无Java改动）
// 功能：查看各频道大地图BOSS存活状态(绿=存活 红=已死亡/未刷新)，名字前展示BOSS头像图标，点击BOSS名直接传送过去
// 挂载：scripts-zh-CN/npc/9000434.js 菜单 selection==8 跳转进入本脚本
// 原理：
//   1. 跨频道查询怪物存活数：channelServer.getMapFactory().getMap(mapId).countMonster(mobId)
//      该调用是进程内直接Java引用调用（World/Channel同进程，非RPC），可在不切换玩家频道的情况下查询任意频道任意地图的怪物数量
//   2. 切换玩家所在频道：cm.getPlayer().getClient().changeChannel(频道号)（Client.java:1556）
//      changeChannel默认不强制传送到固定地图，玩家停留在当前mapId，因此正确顺序是：
//      先 changeMap(目标BOSS地图) 把玩家"挪"到目标地图，再 changeChannel(目标频道号)
// 数据来源：BOSS_LIST 是通过扫描 wz-zh-CN/Mob.wz(boss=1标记) + wz-zh-CN/Map.wz(life节点type="m")
//          + wz-zh-CN/String.wz/Mob.img.xml(中文名) 自动生成的真实世界BOSS数据，
//          筛选规则：spotCount<=3(同一mobId作为怪物life节点出现的不同地图数量<=3，用于区分独立命名的世界BOSS和大量重复刷新的精英杂兵)，
//          并手动剔除墙/集装箱/系统机关等非战斗类boss=1标记对象；不是手填占位数据，如需增删按相同结构(name/mobId/mapId/x/y)直接改这个数组即可
// ================================================================

var BOSS_LIST = [
    { name: "蘑菇王", mobId: 6130101, mapId: 100000005, x: -649, y: 204 },
    { name: "地鼠王", mobId: 3501008, mapId: 101073300, x: 18, y: 222 },
    { name: "绿水灵王", mobId: 9300003, mapId: 103000804, x: 162, y: -451 },
    { name: "多尔", mobId: 6220000, mapId: 103030300, x: 117, y: 51 },
    { name: "摇滚之魂", mobId: 4300013, mapId: 103040430, x: -775, y: 86 },
    { name: "僵尸蘑菇王", mobId: 6300005, mapId: 105070002, x: 458, y: 395 },
    { name: "蝙蝠怪", mobId: 8130100, mapId: 105090900, x: 113, y: 83 },
    { name: "赫丽娜的分身", mobId: 9001002, mapId: 108010101, x: -227, y: 69 },
    { name: "汉斯的分身", mobId: 9001001, mapId: 108010201, x: -276, y: -3 },
    { name: "武术教练的分身", mobId: 9001000, mapId: 108010301, x: -246, y: 71 },
    { name: "达克鲁的分身", mobId: 9001003, mapId: 108010401, x: -253, y: 70 },
    { name: "凯琳的分身", mobId: 9001004, mapId: 108010501, x: 79, y: 7 },
    { name: "船长黑水灵", mobId: 3401011, mapId: 120041900, x: -149, y: -173 },
    { name: "盖奥勒克", mobId: 3502008, mapId: 141050300, x: 432, y: 111 },
    { name: "驮狼雪人", mobId: 8220001, mapId: 211040101, x: 356, y: 262 },
    { name: "第一座塔的阿尼", mobId: 8210010, mapId: 211060201, x: 615, y: -181 },
    { name: "第二座塔的阿尼", mobId: 8210011, mapId: 211060401, x: -1444, y: -149 },
    { name: "第三座塔的阿尼", mobId: 8210012, mapId: 211060601, x: 1554, y: -148 },
    { name: "战甲吹泡泡鱼", mobId: 4130103, mapId: 221020701, x: 148, y: 1464 },
    { name: "外星章鱼闪电棒", mobId: 5120100, mapId: 221030601, x: -2350, y: 763 },
    { name: "书生鬼", mobId: 6090003, mapId: 222010300, x: 1846, y: 136 },
    { name: "动物园的蝙蝠怪", mobId: 9500200, mapId: 230000003, x: -14, y: -1023 },
    { name: "动物园的白雪人", mobId: 9500201, mapId: 230000003, x: 15, y: -556 },
    { name: "动物园的猪猪", mobId: 9500203, mapId: 230000003, x: -50, y: -154 },
    { name: "动物园的漂漂猪", mobId: 9500204, mapId: 230000003, x: 56, y: -152 },
    { name: "皮亚奴斯", mobId: 8510000, mapId: 230040420, x: 568, y: 133 },
    { name: "皮亚奴斯", mobId: 8520000, mapId: 230040420, x: -459, y: 133 },
    { name: "蟾蜍领主", mobId: 6500011, mapId: 231050000, x: 665, y: 94 },
    { name: "天鹰", mobId: 8180001, mapId: 240020101, x: 0, y: 432 },
    { name: "火焰龙", mobId: 8180000, mapId: 240020401, x: -7, y: 444 },
    { name: "布索", mobId: 7120100, mapId: 240070010, x: 277, y: 379 },
    { name: "布索", mobId: 7120101, mapId: 240070020, x: 235, y: 351 },
    { name: "布索", mobId: 7120102, mapId: 240070030, x: 265, y: 367 },
    { name: "布索", mobId: 8120100, mapId: 240070040, x: 235, y: 351 },
    { name: "布索", mobId: 8120101, mapId: 240070050, x: 235, y: 351 },
    { name: "布索", mobId: 8140510, mapId: 240070060, x: 235, y: 351 },
    { name: "贝尔加莫特", mobId: 7220003, mapId: 240070203, x: 570, y: 392 },
    { name: "都纳斯", mobId: 8220010, mapId: 240070303, x: -119, y: 254 },
    { name: "欧啦啦", mobId: 8220011, mapId: 240070403, x: -2889, y: 653 },
    { name: "欧碧拉", mobId: 8220012, mapId: 240070503, x: 201, y: 234 },
    { name: "尼贝隆战舰", mobId: 8220013, mapId: 240070603, x: 270, y: -511 },
    { name: "幻龙", mobId: 8300006, mapId: 240080600, x: 1535, y: 146 },
    { name: "蜘蛛女王", mobId: 8800400, mapId: 240093300, x: 420, y: 56 },
    { name: "仙人玩偶", mobId: 5090001, mapId: 250020300, x: 1249, y: -485 },
    { name: "拉瓦那", mobId: 8800200, mapId: 252030100, x: 978, y: 507 },
    { name: "迪特和罗伊", mobId: 8090000, mapId: 261010102, x: 460, y: 210 },
    { name: "多多", mobId: 8220004, mapId: 270010500, x: 280, y: -938 },
    { name: "玄冰独角兽", mobId: 8220005, mapId: 270020500, x: 66, y: -921 },
    { name: "雷卡", mobId: 8220006, mapId: 270030500, x: -20, y: -584 },
    { name: "迷宫大眼怪2", mobId: 9300304, mapId: 272030400, x: -65, y: -195 },
    { name: "查乌", mobId: 5250004, mapId: 300010420, x: 409, y: 64 },
    { name: "艾菲尼娅", mobId: 5250007, mapId: 300030310, x: 43, y: 127 },
    { name: "塔高斯", mobId: 8240048, mapId: 350011900, x: 2017, y: -116 },
    { name: "黑魔女埃雷奥诺尔", mobId: 8240049, mapId: 350022600, x: 983, y: 17 },
    { name: "变身术士", mobId: 8240016, mapId: 350023200, x: -811, y: -151 },
    { name: "飞行变身术士", mobId: 8240018, mapId: 350023400, x: -746, y: -200 },
    { name: "黑色之翼飞船", mobId: 8240019, mapId: 350023600, x: -362, y: -150 },
    { name: "盖奥勒克", mobId: 8240067, mapId: 350042400, x: 2066, y: -140 },
    { name: "三头犬", mobId: 9400897, mapId: 510102100, x: 1071, y: 7 },
    { name: "三头犬", mobId: 9400904, mapId: 510102200, x: 1130, y: 10 },
    { name: "三头犬", mobId: 9400917, mapId: 510102300, x: 1113, y: 12 },
    { name: "三头犬", mobId: 9400919, mapId: 510102500, x: 1084, y: 19 },
    { name: "克劳德", mobId: 9400898, mapId: 510103700, x: 1089, y: 27 },
    { name: "舞狮怪物1", mobId: 9420602, mapId: 555001100, x: -172, y: -25 },
    { name: "舞狮怪物2", mobId: 9420603, mapId: 555001100, x: -1503, y: -23 },
    { name: "深红巴洛古的手下", mobId: 9400744, mapId: 674030300, x: 244, y: -960 },
    { name: "小巴洛古的手下", mobId: 9400745, mapId: 674030300, x: 281, y: -733 },
    { name: "肌肉之石的手下", mobId: 9400746, mapId: 674030300, x: 337, y: -442 },
    { name: "班的手下", mobId: 9400747, mapId: 674030300, x: 311, y: -210 },
    { name: "盖福克斯", mobId: 9400748, mapId: 674030300, x: 436, y: -1366 },
    { name: "大王蜈蚣", mobId: 9600009, mapId: 701010323, x: 2718, y: 815 },
    { name: "大王蜈蚣", mobId: 9600010, mapId: 701010324, x: 2718, y: 813 },
    { name: "武林妖僧", mobId: 9600025, mapId: 702060000, x: 351, y: 580 },
    { name: "钻机", mobId: 9600086, mapId: 703011000, x: 616, y: 217 },
    { name: "尖兵", mobId: 9600173, mapId: 706012110, x: 85, y: 40 },
    { name: "摇滚之魂", mobId: 9600162, mapId: 708000430, x: -775, y: 86 },
    { name: "兵马俑", mobId: 9410220, mapId: 745010100, x: 1494, y: 328 },
    { name: "毒牙蛇", mobId: 9410222, mapId: 745010300, x: 1519, y: 328 },
    { name: "皇帝", mobId: 9410224, mapId: 745010500, x: -10, y: 87 },
    { name: "蓝蘑菇王", mobId: 9400205, mapId: 800010100, x: 450, y: 73 },
    { name: "天球", mobId: 9400014, mapId: 800020130, x: 1366, y: 203 },
    { name: "老板", mobId: 9400120, mapId: 801030000, x: 1273, y: 306 },
    { name: "女老板", mobId: 9400121, mapId: 801040003, x: -37, y: 151 },
    { name: "男老板", mobId: 9400122, mapId: 801040004, x: 511, y: 147 },
    { name: "皇家护卫", mobId: 9400288, mapId: 802000801, x: 110, y: 328 },
    { name: "绯红猎蜥", mobId: 9400596, mapId: 803000400, x: 883, y: 191 },
    { name: "冰蓝猎蜥", mobId: 9400597, mapId: 803000400, x: 640, y: -27 },
    { name: "红蜗牛王", mobId: 2220000, mapId: 806000000, x: 258, y: 496 },
    { name: "树妖王", mobId: 3220000, mapId: 806010000, x: 578, y: 2180 },
    { name: "浮士德", mobId: 5220002, mapId: 806030000, x: 446, y: 303 },
    { name: "骷髅总司令官", mobId: 9400725, mapId: 806040000, x: -165, y: 209 },
    { name: "巨居蟹", mobId: 5220000, mapId: 806050000, x: -1047, y: 155 },
    { name: "精灵妈妈", mobId: 9400726, mapId: 806060000, x: -451, y: 7 },
    { name: "企鹅女王", mobId: 9400727, mapId: 806070000, x: 575, y: -160 },
    { name: "艾利杰", mobId: 8220000, mapId: 806080000, x: 378, y: 74 },
    { name: "木马黑骑士", mobId: 9400728, mapId: 806090000, x: 361, y: 126 },
    { name: "九尾狐", mobId: 7220001, mapId: 806100000, x: -247, y: 83 },
    { name: "提莫", mobId: 5220003, mapId: 806110000, x: 25, y: 978 },
    { name: "朱诺", mobId: 6220001, mapId: 806120000, x: -2656, y: 799 },
    { name: "大宇", mobId: 3220001, mapId: 806130000, x: 343, y: 264 },
    { name: "妖怪禅师", mobId: 7220002, mapId: 806160000, x: 249, y: 531 },
    { name: "蜈蚣妖怪", mobId: 9400729, mapId: 806170000, x: 684, y: 162 },
    { name: "大海兽", mobId: 8220003, mapId: 806240000, x: -34, y: 2469 },
    { name: "歇尔夫", mobId: 4220000, mapId: 806240100, x: 555, y: 506 },
    { name: "蓝蘑菇", mobId: 8220007, mapId: 806240400, x: 208, y: -701 },
    { name: "肯德熊", mobId: 7220000, mapId: 806240500, x: -229, y: 380 },
    { name: "青竹武士", mobId: 6090002, mapId: 806240800, x: -10, y: 117 },
    { name: "发财", mobId: 9500517, mapId: 910023100, x: -563, y: -6 },
    { name: "龙叔叔黑龙", mobId: 9500533, mapId: 910026100, x: 58, y: -407 },
    { name: "音乐室的幽灵", mobId: 9100034, mapId: 910027210, x: 392, y: 240 },
    { name: "蓝队", mobId: 9500578, mapId: 910027800, x: 5329, y: 82 },
    { name: "红队", mobId: 9500577, mapId: 910027800, x: 5315, y: -236 },
    { name: "终结者", mobId: 9500602, mapId: 910029100, x: 4645, y: 10 },
    { name: "猫的睡窝", mobId: 9300325, mapId: 910033100, x: 490, y: 226 },
    { name: "僵尸蘑菇王", mobId: 9300426, mapId: 910080020, x: 211, y: -643 },
    { name: "迷宫莱西", mobId: 9300309, mapId: 910510500, x: 375, y: 197 },
    { name: "不知底细的幼蝙蝠魔", mobId: 9300326, mapId: 910520000, x: 247, y: 172 },
    { name: "凯琳", mobId: 9300159, mapId: 912010000, x: 66, y: 131 },
    { name: "凯琳", mobId: 9300158, mapId: 912010100, x: 68, y: 135 },
    { name: "驮狼雪人", mobId: 9300287, mapId: 913010000, x: 50, y: 81 },
    { name: "资格之蝙蝠魔", mobId: 9300288, mapId: 913010100, x: 77, y: 76 },
    { name: "资格之多多", mobId: 9300289, mapId: 913010200, x: 31, y: 73 },
    { name: "资格之里里诺", mobId: 9300290, mapId: 913010300, x: 43, y: 73 },
    { name: "能力之火焰龙", mobId: 9300291, mapId: 913020000, x: 52, y: 77 },
    { name: "能力之天鹰", mobId: 9300292, mapId: 913020100, x: 47, y: 76 },
    { name: "能力之大海兽", mobId: 9300293, mapId: 913020200, x: 56, y: 66 },
    { name: "能力之皮亚奴斯", mobId: 9300294, mapId: 913020300, x: 130, y: 81 },
    { name: "入团考试教官肯德熊", mobId: 9001057, mapId: 913070800, x: -3163, y: 83 },
    { name: "暴躁的玛玛哈", mobId: 9001014, mapId: 914020000, x: -636, y: 73 },
    { name: "迷宫恶树", mobId: 9300295, mapId: 921140000, x: 1811, y: -152 },
    { name: "迷宫树荫", mobId: 9300296, mapId: 921140000, x: 1125, y: -159 },
    { name: "迷宫大眼怪6", mobId: 9300308, mapId: 922000030, x: -341, y: 16 },
    { name: "黑甲凶灵", mobId: 9300086, mapId: 922020100, x: -341, y: 16 },
    { name: "塔纳托斯", mobId: 9300100, mapId: 922020100, x: -584, y: 29 },
    { name: "大副", mobId: 9700035, mapId: 923020112, x: -174, y: 139 },
    { name: "海军下士幽灵", mobId: 9700034, mapId: 923020114, x: -1764, y: 149 },
    { name: "幽灵船船长", mobId: 9700037, mapId: 923020190, x: 4, y: 139 },
    { name: "坎特的歇尔夫", mobId: 9001026, mapId: 923030000, x: -1728, y: 543 },
    { name: "嗜血单眼怪", mobId: 9300448, mapId: 923040300, x: -620, y: 1222 },
    { name: "认识英雄的火焰龙", mobId: 9001043, mapId: 924000200, x: -7, y: 444 },
    { name: "认识英雄的天鹰", mobId: 9001044, mapId: 924000201, x: 0, y: 432 },
    { name: "大海兽", mobId: 9500382, mapId: 924900500, x: 42, y: 1626 },
    { name: "萧公", mobId: 9300269, mapId: 925020010, x: 218, y: 2 },
    { name: "武公的分身", mobId: 9300350, mapId: 925040001, x: 226, y: -5 },
    { name: "白毛公猴", mobId: 9100024, mapId: 925120000, x: -779, y: -241 },
    { name: "红石怪", mobId: 9300488, mapId: 931050402, x: 198, y: 162 },
    { name: "骷髅总司令官", mobId: 9300471, mapId: 931050410, x: 199, y: 196 },
    { name: "精灵妈妈", mobId: 9300472, mapId: 931050411, x: -448, y: -13 },
    { name: "木马黑骑士", mobId: 9300473, mapId: 931050412, x: 61, y: 114 },
    { name: "黑暗提莫", mobId: 9300475, mapId: 931050413, x: 25, y: 956 },
    { name: "黑暗朱诺", mobId: 9300476, mapId: 931050414, x: -2630, y: 789 },
    { name: "黑暗大宇", mobId: 9300477, mapId: 931050415, x: 433, y: 246 },
    { name: "蜈蚣妖怪", mobId: 9300478, mapId: 931050418, x: 542, y: 147 },
    { name: "侏儒怪酋长", mobId: 9300479, mapId: 931050419, x: 1821, y: 302 },
    { name: "哈维酋长", mobId: 9300480, mapId: 931050420, x: 288, y: 402 },
    { name: "绵羊酋长", mobId: 9300481, mapId: 931050421, x: -98, y: 395 },
    { name: "半人马王", mobId: 9300482, mapId: 931050422, x: 534, y: 403 },
    { name: "企鹅女王", mobId: 9300483, mapId: 931050423, x: 729, y: -156 },
    { name: "吉米拉", mobId: 9300510, mapId: 931050424, x: -777, y: -414 },
    { name: "火焰龙", mobId: 9300511, mapId: 931050425, x: -29, y: 436 },
    { name: "天鹰", mobId: 9300512, mapId: 931050426, x: -21, y: 441 },
    { name: "帕普拉图斯", mobId: 9300513, mapId: 931050427, x: -269, y: -395 },
    { name: "黑山老妖", mobId: 9300514, mapId: 931050428, x: 1444, y: 82 },
    { name: "皮亚奴斯", mobId: 9300515, mapId: 931050429, x: 577, y: 133 },
    { name: "大海兽", mobId: 9300516, mapId: 931050430, x: -103, y: 2478 },
    { name: "多多", mobId: 9300517, mapId: 931050431, x: -238, y: -582 },
    { name: "玄冰独角兽", mobId: 9300518, mapId: 931050432, x: 101, y: 172 },
    { name: "雷卡", mobId: 9300519, mapId: 931050433, x: -344, y: 177 },
    { name: "钢铁石头人", mobId: 9800003, mapId: 952000500, x: 368, y: -159 },
    { name: "摇滚之魂", mobId: 9800008, mapId: 952010400, x: -927, y: -358 },
    { name: "红石怪", mobId: 9800009, mapId: 952010500, x: 1025, y: 71 },
    { name: "雪山魔女", mobId: 9800016, mapId: 952020500, x: 745, y: 55 },
    { name: "巨居蟹", mobId: 9800022, mapId: 952030400, x: 771, y: -22 },
    { name: "歇尔夫", mobId: 9800024, mapId: 952030500, x: 1577, y: 376 },
    { name: "蝙蝠怪", mobId: 9800031, mapId: 952040500, x: 1552, y: -158 },
    { name: "大宇", mobId: 9800146, mapId: 952050300, x: 897, y: 255 },
    { name: "大宇", mobId: 9800149, mapId: 952050500, x: 388, y: 246 },
    { name: "九尾狐", mobId: 9800038, mapId: 953000400, x: 211, y: -346 },
    { name: "书生鬼", mobId: 9800037, mapId: 953000500, x: 797, y: 141 },
    { name: "外星章鱼闪电棒", mobId: 9800041, mapId: 953010200, x: -1691, y: 787 },
    { name: "朱诺", mobId: 9800044, mapId: 953010500, x: -2871, y: 764 },
    { name: "陆陆猫", mobId: 9800045, mapId: 953020200, x: 271, y: 153 },
    { name: "迪特和罗伊", mobId: 9800050, mapId: 953020500, x: 177, y: 143 },
    { name: "石头人", mobId: 9800058, mapId: 953030500, x: 1674, y: -44 },
    { name: "青竹武士", mobId: 9800060, mapId: 953040000, x: 668, y: -529 },
    { name: "肯德熊", mobId: 9800063, mapId: 953040300, x: 1251, y: -252 },
    { name: "妖怪禅师", mobId: 9800065, mapId: 953040400, x: 419, y: -330 },
    { name: "大王蜈蚣", mobId: 9800066, mapId: 953040500, x: 378, y: 29 },
    { name: "黑甲凶灵", mobId: 9800072, mapId: 953050500, x: -1578, y: -29 },
    { name: "海盗王巴尔博萨", mobId: 9800153, mapId: 953060300, x: 834, y: 153 },
    { name: "老海盗", mobId: 9800156, mapId: 953060500, x: -587, y: 66 },
    { name: "幽灵魔法师", mobId: 9800160, mapId: 953070300, x: 2809, y: -544 },
    { name: "幽灵死神", mobId: 9800163, mapId: 953070500, x: 5616, y: -1231 },
    { name: "哈维酋长", mobId: 9800166, mapId: 953080200, x: 618, y: -750 },
    { name: "半人马王", mobId: 9800170, mapId: 953080400, x: -43, y: -649 },
    { name: "天鹰", mobId: 9800171, mapId: 953080500, x: 851, y: -682 },
    { name: "白毛公猴", mobId: 9800175, mapId: 953090300, x: -846, y: -248 },
    { name: "加内什", mobId: 9800178, mapId: 953090500, x: 1240, y: 378 },
    { name: "贝尔加莫特", mobId: 9800077, mapId: 954000200, x: 808, y: -627 },
    { name: "尼贝隆战舰", mobId: 9800084, mapId: 954000500, x: 801, y: -755 },
    { name: "黑山老妖", mobId: 9800090, mapId: 954010400, x: 577, y: 3721 },
    { name: "艾里葛斯", mobId: 9800091, mapId: 954010500, x: 91, y: -255 },
    { name: "阿尼", mobId: 9800099, mapId: 954020500, x: 2014, y: -175 },
    { name: "大海兽", mobId: 9800105, mapId: 954030500, x: 60, y: 1112 },
    { name: "多多", mobId: 9800108, mapId: 954040100, x: 440, y: -656 },
    { name: "玄冰独角兽", mobId: 9800109, mapId: 954040200, x: 103, y: -959 },
    { name: "雷卡", mobId: 9800113, mapId: 954040500, x: -679, y: -938 },
    { name: "古代黑石头人", mobId: 9800184, mapId: 954060400, x: -387, y: -205 },
    { name: "变形树妖王", mobId: 9800186, mapId: 954060500, x: -303, y: -825 },
    { name: "保护者", mobId: 9306201, mapId: 957016000, x: 1006, y: -338 },
];

var Point = Java.type('java.awt.Point');
var LifeFactory = Java.type('org.gms.server.life.LifeFactory');
var PAGE_SIZE = 10; // 单页显示boss数量，避免NPC对话框文字过长
var SEL_PREV_PAGE = 9000;
var SEL_NEXT_PAGE = 9001;

// 按mobId取出怪物静态模板(LifeFactory带缓存,不依赖该boss是否已刷新存活)，拼出头像图片标签
// 用法/避坑参考: scripts-zh-CN/BeiDouSpecial/当前地图掉落_当前地图.js 的 getMobImage()
//   movetype: -1=未知类型(无站立动作，不能取图) 0=陆地 1=飞行，分别对应stand/0、fly/0两种动作帧
//   图片过大(宽>160且高>250)会导致客户端假死，需用占位图代替
function getBossIcon(mobId) {
    var mob = LifeFactory.getMonster(mobId);
    if (mob == null) {
        return "#fUI/UIWindow.img/Maker/randomRecipe#";
    }
    var stats = mob.getStats();
    var movetype = stats.getMovetype();
    if (movetype != 0 && movetype != 1) {
        return "#fUI/UIWindow.img/Maker/randomRecipe#";
    }
    if (stats.getImgwidth() > 160 && stats.getImgheight() > 250) {
        return "#fMap/Obj/Tdungeon.img/mushCatle/npc/0/0#";
    }
    var action = movetype == 1 ? "fly" : "stand";
    var idStr = "" + mobId;
    while (idStr.length < 7) {
        idStr = "0" + idStr;
    }
    return "#fMob/" + idStr + ".img/" + action + "/0#";
}

// 当前正在查看的频道号、页码
var viewChannel = -1;
var viewPage = 0;

function start() {
    status = -1;
    viewChannel = -1;
    viewPage = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (status >= 0 && mode == 0) {
        cm.sendOk("感谢你的光临！");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        // 默认显示玩家当前所在频道
        if (viewChannel == -1) {
            viewChannel = cm.getPlayer().getClient().getChannelServer().getId();
        }
        showBossList();
    } else if (status == 1) {
        var totalChannels = cm.getPlayer().getWorldServer().getChannelsSize();
        if (selection >= 0 && selection < totalChannels) {
            // 切换查看的频道(不是玩家真实换频道，只是切换查看视角)
            viewChannel = selection + 1;
            viewPage = 0;
            status = 0;
            showBossList();
            return;
        }
        if (selection == SEL_PREV_PAGE) {
            if (viewPage > 0) {
                viewPage--;
            }
            status = 0;
            showBossList();
            return;
        }
        if (selection == SEL_NEXT_PAGE) {
            var maxPage = Math.floor((BOSS_LIST.length - 1) / PAGE_SIZE);
            if (viewPage < maxPage) {
                viewPage++;
            }
            status = 0;
            showBossList();
            return;
        }
        if (selection >= 1000 && selection < 9000) {
            var idx = selection - 1000;
            if (idx >= 0 && idx < BOSS_LIST.length) {
                warpToBoss(BOSS_LIST[idx]);
                return;
            }
        }
        cm.dispose();
    }
}

// 渲染频道按钮 + 当前页BOSS存活状态列表
function showBossList() {
    var totalChannels = cm.getPlayer().getWorldServer().getChannelsSize();
    var targetChannelServer = cm.getPlayer().getClient().getWorldServer().getChannel(viewChannel);
    if (targetChannelServer == null) {
        // 目标频道不存在(可能服务器频道数变化)，回退到第1频道
        viewChannel = 1;
        targetChannelServer = cm.getPlayer().getClient().getWorldServer().getChannel(viewChannel);
    }

    var totalPages = Math.floor((BOSS_LIST.length - 1) / PAGE_SIZE) + 1;
    var text = "#eBOSS传送系统#n  (第" + (viewPage + 1) + "/" + totalPages + "页)\r\n";
    text += "当前所在频道: #b" + cm.getPlayer().getClient().getChannelServer().getId() + "#k    正在查看频道: #r" + viewChannel + "#k\r\n\r\n";

    // 频道切换按钮
    text += "#b切换查看频道：#k\r\n";
    for (var ch = 0; ch < totalChannels; ch++) {
        text += "#L" + ch + "#[频道" + (ch + 1) + "]#l ";
    }
    text += "\r\n\r\n";

    text += "#b名字为绿色代表已刷新，红色代表未刷新：#k\r\n";
    var start = viewPage * PAGE_SIZE;
    var end = Math.min(start + PAGE_SIZE, BOSS_LIST.length);
    for (var i = start; i < end; i++) {
        var boss = BOSS_LIST[i];
        var alive = targetChannelServer.getMapFactory().getMap(boss.mapId).countMonster(boss.mobId) > 0;
        var icon = getBossIcon(boss.mobId);
        if (alive) {
            text += icon + "#L" + (1000 + i) + "##g[" + boss.name + "]#k#l\r\n";
        } else {
            text += icon + "#L" + (1000 + i) + "##r[" + boss.name + "]#k#l\r\n";
        }
    }
    text += "\r\n";
    if (viewPage > 0) {
        text += "#L" + SEL_PREV_PAGE + "##b[上一页]#k#l  ";
    }
    if (viewPage < totalPages - 1) {
        text += "#L" + SEL_NEXT_PAGE + "##b[下一页]#k#l";
    }
    cm.sendSimple(text);
}

// 把玩家传送到目标频道目标BOSS所在地图坐标
function warpToBoss(boss) {
    cm.dispose();
    var targetChannelServer = cm.getPlayer().getClient().getWorldServer().getChannel(viewChannel);
    if (targetChannelServer == null) {
        cm.sendOk("目标频道不存在");
        return;
    }
    var targetMap = targetChannelServer.getMapFactory().getMap(boss.mapId);
    if (targetMap.countMonster(boss.mobId) <= 0) {
        cm.sendOk("该BOSS当前未刷新，暂时无法传送过去");
        return;
    }
    // 先把玩家所在地图切到目标BOSS地图(直接用扫描记录的boss刷新坐标，不依赖该地图是否存在0号传送点)，再切频道，保证changeChannel后落在正确地图
    cm.getPlayer().changeMap(targetMap, new Point(boss.x, boss.y));
    if (cm.getPlayer().getClient().getChannelServer().getId() != viewChannel) {
        cm.getPlayer().getClient().changeChannel(viewChannel);
    }
}
