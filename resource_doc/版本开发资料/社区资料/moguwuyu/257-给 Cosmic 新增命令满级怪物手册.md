# 给 Cosmic 新增命令满级怪物手册

> 来源：https://moguwuyu.com/d/257
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

这个命令用于一键集齐怪物手册里的所有卡片
创建命令文件 src/main/java/client/command/commands/gm2/MaxMonsterBookCommand.java
package client.command.commands.gm2;

import client.Client;
import client.command.Command;

public class MaxMonsterBookCommand extends Command {
    {
        setDescription("Max Monster Book.");
    }

    @Override
    public void execute(Client c, String[] params) {

        for (int i = 2380000; i <= 2380019; i++) {
            for (int y = 1; y < 6; y++) {
                c.getPlayer().getMonsterBook().addCard(c, i);
            }
        }
        for (int i = 2381000; i <= 2381083; i++) {
            for (int y = 1; y < 6; y++) {
                c.getPlayer().getMonsterBook().addCard(c, i);
            }
        }
        for (int i = 2382000; i <= 2382096; i++) {
            for (int y = 1; y < 6; y++) {
                c.getPlayer().getMonsterBook().addCard(c, i);
            }
        }
        for (int i = 2383000; i <= 2383059; i++) {
            for (int y = 1; y < 6; y++) {
                c.getPlayer().getMonsterBook().addCard(c, i);
            }
        }
        for (int i = 2384000; i <= 2384040; i++) {
            for (int y = 1; y < 6; y++) {
                c.getPlayer().getMonsterBook().addCard(c, i);
            }
        }
        for (int i = 2385000; i <= 2385025; i++) {
            for (int y = 1; y < 6; y++) {
                c.getPlayer().getMonsterBook().addCard(c, i);
            }
        }
        for (int i = 2386000; i <= 2386024; i++) {
            for (int y = 1; y < 6; y++) {
                c.getPlayer().getMonsterBook().addCard(c, i);
            }
        }
        for (int i = 2387000; i <= 2387013; i++) {
            for (int y = 1; y < 6; y++) {
                c.getPlayer().getMonsterBook().addCard(c, i);
            }
        }
        for (int i = 2388000; i <= 2388070; i++) {
            for (int y = 1; y < 6; y++) {
                c.getPlayer().getMonsterBook().addCard(c, i);
            }
        }
    }
}
注册命令
修改 CommandsExecutor.java，在
private void registerLv2Commands() {
里面添加
addCommand("maxmonsterbook", MaxMonsterBookCommand.class);
客户端使用GM2命令即可
!maxmonsterbook
会产生很多动画，导致卡顿一小会。
我最近在研究v95自己绘制怪物手册，发现一个内存泄漏的问题。
于是我回到v83写了这个命令，用于测试，结果表明v83即使是使用img也能轻易复现。
具体表现为，打开满级的怪物手册，每张卡片都点一下（最好是从最下面的标签往上点）
大概是把最下面3张标签页都点一遍，客户端就会炸掉
v95新增了一些体积较大的BOSS会更快的炸掉
从这里发现一个有可能用于缓解内存危机的点

---

**#2楼**

哦耶，等大佬的95版怪物手册！

---

**#3楼**

foggy
估计还得一阵子，太懒了，后端还没写呢

---

**#4楼**

感谢分享

---

**#5楼**

学习一下，感谢分享。