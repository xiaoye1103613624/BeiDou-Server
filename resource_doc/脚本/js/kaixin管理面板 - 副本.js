var 礼包物品 = "#v1302000#",
  x1 = "1302000,+1",
  x2,
  x3,
  x4,
  爱心 = "#fEffect/CharacterEff/1022223/4/0#",
  红色箭头 = "#fUI/UIWindow/Quest/icon6/7#",
  蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#",
  星星 = "#fEffect/CharacterEff/1003393/0/0#",
  hx = "#fEffect/CharacterEff/1022223/4/0#",
  警报灯 = "#fUI/StatusBar/BtClaim/normal/0#",
  正方形 = "#fUI/UIWindow/Quest/icon3/6#",
  小雪花 = "#fEffect/CharacterEff/1003393/0/0#",
  音乐 = "#fEffect/CharacterEff/1003249/0/0#",
  表情高兴 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#",
  彩虹 = "#fEffect/ItemEff/1071085/effect/walk1/2#";
hx = "#fEffect/CharacterEff/1112960/3/0#";
蓝色角点 = "#fEffect/CharacterEff/1082565/4/0#";
var 邪恶小兔 = "#fEffect/CharacterEff/1112960/3/0#",
  红枫叶 = "#fMap/MapHelper/weather/maple/1#",
  银杏叶 = "#fMap/MapHelper/weather/maple/3#",
  点券 = "#fUI/CashShop/CashItem/0#",
  M7 = "#fEffect/CharacterEff/1051296/1/0#",
  M14 = "#fEffect/CharacterEff/1112925/0/0#",
  H1 = "#fEffect/CharacterEff/1050334/0/1#",
  FG = "#fEffect/CharacterEff/1051366/1/0#",
  热点推荐 = "#fUI/CashShop/CSChar/BtCoordination/mouseOver/0#",
  LJ = "#fEffect/CharacterEff/1082565/2/0#",
  xmml2 = 0;
function start() {
  status = -1;
  action(1, 0, 0);
}
function action(a, d, b) {
  if (-1 == a) cm.dispose();
  else if (0 <= status && 0 == a) cm.dispose();
  else if ((1 == a ? status++ : status--, 0 == status)) {
    a = "";
    for (i = 0; 10 > i; i++) a += "";
    a =
      a +
      "\r\n                #L18##r" +
      (蓝色角点 +
        "充值余额" +
        蓝色角点 +
        "#l\r\n\r\n                #L10##d" +
        蓝色角点 +
        "充值抵用" +
        蓝色角点 +
        "#l \r\n\r\n");
    a +=
      "                #L9##d" +
      蓝色角点 +
      "充值点卷" +
      蓝色角点 +
      "#l\r\n\r\n             #L2##d" +
      蓝色角点 +
      "获取金币(给自己)" +
      蓝色角点 +
      "#l\r\n";
    cm.sendSimple(a);
  } else
    1 == status
      ? ((xmml = b),
        0 != b &&
          (2 == b
            ? ((xmml = 2),
              cm.sendGetNumber(
                "请输入需要的金币",
                0,
                0,
                2e9
              ))
            : 3 == b
            ? ((xmml = 3),
              cm.sendYesNo(
                "尊敬的管理员是否确定全服在线抽奖？"
              ))
            : 4 == b
            ? ((xmml = 4),
              cm.sendGetText(
                "请输入需要充值的账户："
              ))
            : 7 == b
            ? cm.openNpc(9900004, 10107)
            : 9 == b
            ? ((xmml = 9),
              cm.sendGetText(
                "请输入需要充值#r点卷#k的账户："
              ))
            : 10 == b
            ? ((xmml = 10),
              cm.sendGetText(
                "请输入需要充值#r抵用#k的账户："
              ))
            : 11 == b
            ? cm.openNpc(9900004, "赞助装备")
            : 12 == b
            ? cm.sendGetNumber(
                "请输入你需要的经验值：",
                1,
                1,
                2e9
              )
            : 13 == b
            ? cm.sendGetText(
                "请输入需要充值#r充值币#k的账户："
              )
            : 14 == b
            ? (cm.maxAllSkills(), cm.sendOk("成功！"), cm.dispose())
            : 15 == b
            ? cm.openNpc(9900004, 1001)
            : 16 == b
            ? cm.openNpc(9900004, 1003)
            : 17 == b
            ? cm.sendGetText(
                "请输入需要充值#r交易币#k的账户："
              )
            : 18 == b
            ? cm.sendGetText(
                "请输入需要充值#r元宝#k的账户(无累计)："
              )
            : 20 == b &&
              cm.openNpc(9900004, "永恒箱子随机")))
      : 2 == status
      ? ((xmml2 = b),
        0 != xmml &&
          (1 == xmml
            ? (cm.setshdj(b),
              cm.playerMessage(
                5,
                "伤害等级修改为:" + b
              ),
              cm.dispose())
            : 2 == xmml
            ? (cm.gainMeso(b),
              cm.playerMessage(5, "获得金币:" + b),
              cm.dispose())
            : 3 == xmml
            ? (全服在线抽奖(), cm.dispose())
            : 4 == xmml
            ? ((账号 = cm.getText()),
              cm.sendGetNumber(
                "请确定充值账号？\r\n#b" +
                  账号 +
                  "\r\n请输入充值金额",
                0,
                0,
                1e5
              ))
            : 5 == xmml
            ? ((wjmc1 = cm.getText()),
              角色是否存在(wjmc1)
                ? cm.sendOk(
                    "当前玩家：" +
                      wjmc1 +
                      "\r\n请选择您的操作：\r\n #L1#屏蔽伤害#l  #L2#解除屏蔽#l"
                  )
                : (cm.playerMessage(
                    5,
                    "输入的角色不存在,请重新检查！"
                  ),
                  cm.dispose()))
            : 9 == xmml
            ? ((账号 = cm.getText()),
              cm.sendGetNumber(
                "请确定充值账号？\r\n#b" +
                  账号 +
                  "\r\n请输入充值#r点卷",
                0,
                0,
                1e8
              ))
            : 10 == xmml
            ? ((账号 = cm.getText()),
              cm.sendGetNumber(
                "请确定充值账号？\r\n#b" +
                  账号 +
                  "\r\n请输入充值#r抵用",
                0,
                0,
                1e6
              ))
            : 12 == xmml
            ? (cm.gainExp(+xmml2), cm.dispose())
            : 13 == xmml
            ? ((账号 = cm.getText()),
              cm.sendGetNumber(
                "请确定充值账号？\r\n#b" +
                  账号 +
                  "\r\n#e请输入充值#r充值币",
                0,
                0,
                1e6
              ))
            : 17 == xmml
            ? ((账号 = cm.getText()),
              getjianchajiaoyibizhanghao(账号)
                ? cm.sendGetNumber(
                    "请确定充值账号？\r\n#b" +
                      账号 +
                      "\r\n#e请输入充值#r交易币",
                    0,
                    0,
                    1e6
                  )
                : (cm.sendOk(
                    "金币交易行交易账号不存在!请让玩家打开交易行一次！"
                  ),
                  (status = -1)))
            : 18 == xmml &&
              ((账号 = cm.getText()),
              cm.sendGetNumber(
                "请确定充值账号？\r\n#b" +
                  账号 +
                  "\r\n#e请输入充值#r元宝",
                0,
                0,
                1e6
              ))))
      : 3 == status
      ? ((充值金额 = b),
        0 != xmml &&
          (4 == xmml
            ? cm.sendYesNo(
                "请再次确定充值信息：\r\n#b账号" +
                  账号 +
                  "\r\n#r金额：" +
                  充值金额
              )
            : 5 == xmml
            ? (1 == b
                ? ((cssz = -1), (mse = "成功屏蔽伤害"))
                : ((cssz = 0), (mse = "解除屏蔽伤害")),
              cm.playerMessage(
                5,
                "" + pingbiwanjia(wjmc1, cssz) + "  状态：" + mse
              ),
              cm.dispose())
            : 9 == xmml
            ? ((取服务器时间 =
                Packages.tools.FileoutputUtil.CurrentReadable_Time()),
              (充值信息 =
                取服务器时间 +
                "充值信息：" +
                账号 +
                "  点卷：" +
                充值金额 +
                "  状态：" +
                充值点卷(账号, 充值金额)),
              cm.playerMessage(5, 充值信息),
              cm.dispose())
            : 10 == xmml
            ? ((取服务器时间 =
                Packages.tools.FileoutputUtil.CurrentReadable_Time()),
              (充值信息 =
                取服务器时间 +
                "充值信息：" +
                账号 +
                "  抵用：" +
                充值金额 +
                "  状态：" +
                充值抵用(账号, 充值金额)),
              cm.playerMessage(5, 充值信息),
              cm.dispose())
            : 12 == xmml
            ? (cm.gainExp(+xmml2), cm.dispose())
            : 13 == xmml
            ? cm.sendYesNo(
                "请再次确定#e#r充值币#n#k信息：\r\n#b账号" +
                  账号 +
                  "\r\n#r充值：" +
                  充值金额
              )
            : 17 == xmml
            ? cm.sendYesNo(
                "请再次确定#e#r交易币#n#k信息：\r\n#b账号" +
                  账号 +
                  "\r\n#r充值：" +
                  充值金额
              )
            : 18 == xmml &&
              cm.sendYesNo(
                "请再次确定#e#r元宝#n#k信息：\r\n#b账号" +
                  账号 +
                  "\r\n#r充值：" +
                  充值金额
              )))
      : 4 == status &&
        0 != xmml &&
        (4 == xmml
          ? ((取服务器时间 =
              Packages.tools.FileoutputUtil.CurrentReadable_Time()),
            (充值信息 =
              取服务器时间 +
              "充值信息：" +
              账号 +
              "  金额：" +
              充值金额 +
              "  状态：" +
              充值余额(账号, 充值金额)),
            cm.playerMessage(5, 充值信息),
            cm.dispose())
          : 13 == xmml
          ? ((取服务器时间 =
              Packages.tools.FileoutputUtil.CurrentReadable_Time()),
            (充值信息 =
              取服务器时间 +
              "充值信息：" +
              账号 +
              "  充值币：" +
              充值金额 +
              "  状态：" +
              充值充值币(账号, 充值金额)),
            cm.playerMessage(5, 充值信息),
            cm.dispose())
          : 17 == xmml
          ? ((取服务器时间 =
              Packages.tools.FileoutputUtil.CurrentReadable_Time()),
            (充值信息 =
              取服务器时间 +
              "充值信息：" +
              账号 +
              "  交易币：" +
              充值金额 +
              "  状态：" +
              chongzhijiaoyibi(账号, 充值金额)),
            cm.playerMessage(5, 充值信息),
            cm.sendOk(充值信息),
            cm.dispose())
          : 18 == xmml &&
            ((取服务器时间 =
              Packages.tools.FileoutputUtil.CurrentReadable_Time()),
            (充值信息 =
              取服务器时间 +
              "充值信息：" +
              账号 +
              "  余额：" +
              充值金额 +
              "  状态：" +
              chongzhiyuanbao(账号, 充值金额)),
            cm.playerMessage(5, 充值信息),
            cm.sendOk(充值信息),
            cm.dispose()));
}
function szwj(a) {
  for (
    var d = "设置失败",
      b = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
    b.hasNext();

  )
    for (
      var c = b.next().getPlayerStorage().getAllCharacters().iterator();
      c.hasNext();

    )
      c.next().getName() == a && (d = "设置成功");
  cm.playerMessage(5, "状态:" + d);
}
function 充值(a, d) {
  a = Packages.database.DatabaseConnection.getConnection().prepareStatement(
    "UPDATE hypay SET pay = pay+" + d + " WHERE accname = '" + a + "' ;"
  );
  a.executeUpdate();
  return (fhz = a.close());
}
function 充值余额(a, d) {
  var b = "",
    c = !1;
  if (检测账号是否存在(a)) {
    for (
      var e =
        Packages.handling.channel.ChannelServer.getAllInstances().iterator();
      e.hasNext();

    )
      for (
        var f = e.next().getPlayerStorage().getAllCharacters().iterator();
        f.hasNext();

      ) {
        var g = f.next();
        g.getClient().getAccountName() == a &&
          (g.setmoney(+d),
          (b = "充值成功[在线]"),
          (c = !0));
      }
    c ||
      ((a = cm
        .getConnection()
        .prepareStatement(
          "UPDATE accounts SET money = money+" +
            d +
            " WHERE name = '" +
            a +
            "' ;"
        )),
      a.executeUpdate(),
      a.close(),
      (b = "充值成功[离线]"));
  } else b = "充值失败 账号错误";
  return b;
}
function 充值充值币(a, d) {
  var b = "充值失败 账号输入错误",
    c = cm.getConnection();
  c = c.prepareStatement("SELECT * FROM accounts WHERE name = '" + a + "' ;");
  var e = c.executeQuery(),
    f = e.next();
  e.close();
  c.close();
  f &&
    ((c = cm.getConnection()),
    (c = c.prepareStatement(
      "UPDATE hypay SET xmcz = xmcz+" + d + " WHERE accname = '" + a + "' ;"
    )),
    c.executeUpdate(),
    c.close(),
    (b = "充值成功"),
    在线提醒(
      a,
      "GM为您充值了：" + d + " 充值币"
    ));
  return b;
}
function 充值点卷(a, d) {
  var b = "",
    c = !1;
  if (检测账号是否存在(a)) {
    for (
      var e =
        Packages.handling.channel.ChannelServer.getAllInstances().iterator();
      e.hasNext();

    )
      for (
        var f = e.next().getPlayerStorage().getAllCharacters().iterator();
        f.hasNext();

      ) {
        var g = f.next();
        g.getClient().getAccountName() == a &&
          (g.modifyCSPoints(1, +d, !0),
          (b = "充值成功[在线]"),
          (c = !0));
      }
    c ||
      ((a = cm
        .getConnection()
        .prepareStatement(
          "UPDATE accounts SET ACash = ACash+" +
            d +
            " WHERE name = '" +
            a +
            "' ;"
        )),
      a.executeUpdate(),
      a.close(),
      (b = "充值成功[离线]"));
  } else b = "充值失败 账号错误";
  return b;
}
function 充值抵用(a, d) {
  var b = "",
    c = !1;
  if (检测账号是否存在(a)) {
    for (
      var e =
        Packages.handling.channel.ChannelServer.getAllInstances().iterator();
      e.hasNext();

    )
      for (
        var f = e.next().getPlayerStorage().getAllCharacters().iterator();
        f.hasNext();

      ) {
        var g = f.next();
        g.getClient().getAccountName() == a &&
          (g.modifyCSPoints(2, +d, !0),
          (b = "充值成功[在线]"),
          (c = !0));
      }
    c ||
      ((a = cm
        .getConnection()
        .prepareStatement(
          "UPDATE accounts SET mPoints = mPoints+" +
            d +
            " WHERE name = '" +
            a +
            "' ;"
        )),
      a.executeUpdate().close(),
      a.close(),
      (b = "充值成功[离线]"));
  } else b = "充值失败 账号错误";
  return b;
}
function 写入记录(a, d, b) {
  取服务器时间 = Packages.tools.FileoutputUtil.CurrentReadable_Time();
  jlczz = "" + cm.getPlayer().getClient().getAccountName();
  var c = cm
    .getConnection()
    .prepareStatement(
      "insert into xm充值记录 (充值账号,充值金额,操作IP,备注) values (?,?,?,?);"
    );
  c.setString(1, a);
  c.setString(2, d);
  c.setString(3, jlczz);
  c.setString(4, b);
  c.executeUpdate();
  c.close();
}
function 在线提醒(a, d) {
  for (
    var b =
      Packages.handling.channel.ChannelServer.getAllInstances().iterator();
    b.hasNext();

  )
    for (
      var c = b.next().getPlayerStorage().getAllCharacters().iterator();
      c.hasNext();

    ) {
      var e = c.next();
      if (e.getClient().getAccountName() == a) {
        e.dropMessage(5, d);
        break;
      }
    }
}
function 设置屏蔽(a, d) {
  a = Packages.database.DatabaseConnection.getConnection().prepareStatement(
    "UPDATE characters SET shdj = " + a + " WHERE name = '" + d + "' ;"
  );
  a.executeUpdate().close();
  a.close();
}
function 更新数据库(a, d, b, c, e) {
  a = Packages.database.DatabaseConnection.getConnection().prepareStatement(
    "UPDATE " +
      表 +
      " SET " +
      d +
      " = " +
      b +
      " WHERE " +
      c +
      " = '" +
      e +
      "' ;"
  );
  a.executeUpdate();
  a.close();
}
function 检测账号是否存在(a) {
  a = cm
    .getConnection()
    .prepareStatement("SELECT * FROM accounts WHERE name = '" + a + "' ;");
  var d = a.executeQuery(),
    b = d.next();
  d.close();
  a.close();
  return b;
}
function 角色是否存在(a) {
  a = Packages.database.DatabaseConnection.getConnection().prepareStatement(
    "SELECT * FROM characters WHERE name = '" + a + "' ;"
  );
  var d = a.executeQuery(),
    b = d.next();
  d.close();
  a.close();
  return b;
}
function 查看伤害玩家() {
  for (
    var a =
        Packages.database.DatabaseConnection.getConnection().prepareStatement(
          "SELECT * FROM characters WHERE shdj = -1;"
        ),
      d = a.executeQuery(),
      b = "",
      c = "",
      e = 0;
    d.next();

  )
    (e += 1),
      (c += "ID:" + d.getString("id") + "  " + d.getString("name") + "  \r\n");
  b +=
    "当前被屏蔽玩家总计：" +
    e +
    " 个\r\n" +
    c;
  d.close();
  a.close();
  return b;
}
function 全服在线抽奖() {
  for (
    var a = 0,
      d = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
    d.hasNext();

  )
    for (
      var b = d.next().getPlayerStorage().getAllCharacters().iterator();
      b.hasNext();

    ) {
      var c = b.next();
      !c.isGM() &&
        20 > a &&
        ((Fameup = c.getFame()),
        (gailv = Fameup + 100),
        Math.floor(500 * Math.random()) <= gailv &&
          ((a += 1),
          (sjzz = Math.floor(50 * Math.random() + 50)),
          c.gainHyPay(sjzz),
          (c =
            "玩家★" +
            c.getName() +
            "★[人气值:" +
            Fameup +
            "]成为幸运之星！获得:" +
            sjzz +
            "元赞助！！"),
          Packages.handling.world.World.Broadcast.broadcastMessage(
            Packages.tools.MaplePacketCreator.serverNotice(
              12,
              cm.getClient().getChannel(),
              "【幸运大抽奖】 : " + c
            )
          )));
    }
  cm.getPlayer().dropMessage(
    5,
    "全服在线抽奖！中奖玩家:" + a
  );
  cm.dispose();
}
function pingbiwanjia(a, d) {
  for (
    var b =
        "屏蔽失败！可能名字错误！",
      c = !1,
      e = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
    e.hasNext();

  )
    for (
      var f = e.next().getPlayerStorage().getAllCharacters().iterator();
      f.hasNext();

    ) {
      var g = f.next();
      if (g.getName() == a) {
        g.setshdj(d);
        b = "操作屏蔽功能成功[在线]";
        c = !0;
        g.saveToDB(!1, !1);
        break;
      }
    }
  c ||
    ((a = cm
      .getConnection()
      .prepareStatement(
        "UPDATE characters SET shdj = " + d + " WHERE name = '" + a + "' ;"
      )),
    a.executeUpdate().close(),
    a.close(),
    (b = "操作屏蔽功能成功[离线]"));
  return b;
}
function getjianchajiaoyibizhanghao(a) {
  a = cm
    .getConnection()
    .prepareStatement(
      "SELECT * FROM xmjinbijiaoyi_geren WHERE name = '" + a + "' ;"
    );
  var d = a.executeQuery(),
    b = d.next();
  d.close();
  a.close();
  return b;
}
function chongzhijiaoyibi(a, d) {
  var b =
      "充值失败 账号输入错误!或者账号不存在！",
    c = cm.getConnection();
  c = c.prepareStatement(
    "SELECT * FROM xmjinbijiaoyi_geren WHERE name = '" + a + "' ;"
  );
  var e = c.executeQuery(),
    f = e.next();
  e.close();
  c.close();
  f &&
    ((c = cm.getConnection()),
    (c = c.prepareStatement(
      "UPDATE xmjinbijiaoyi_geren SET rmb = rmb+" +
        d +
        " WHERE name = '" +
        a +
        "' ;"
    )),
    c.executeUpdate(),
    c.close(),
    (b = "充值成功"),
    在线提醒(
      a,
      "GM为您充值了：" + d + " 交易币"
    ));
  return b;
}
function chongzhiyuanbao(a, d) {
    if (检测账号是否存在(a)) {
        var b = cm.getConnection().prepareStatement(
            "UPDATE accounts SET money = money + " + d + " WHERE name = '" + a + "';"
        );
        b.executeUpdate();
        b.close();

        // 在线提醒玩家
        在线提醒(a, "GM为您充值了：" + d + " 余额");

        // 记录每日赞助
        for (
            var e = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
            e.hasNext();

        ) {
            var f = e.next().getPlayerStorage().getAllCharacters().iterator();
            while (f.hasNext()) {
                var g = f.next();
                if (g.getClient().getAccountName() == a) {
                    g.setBossLog("每日赞助", 0, d); // 添加每日赞助记录
                    break; // 找到玩家后退出循环
                }
            }
        }

        return "充值余额成功";
    } else {
        return "充值失败 账号错误";
    }
}
