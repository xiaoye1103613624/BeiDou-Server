# 【服务端】[GMS083] [事件脚本] 如何实现最简单的Boss召唤功能

> 来源：https://moguwuyu.com/d/32
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

使用事件脚本实现最简单的Boss召唤功能
本教程以
北斗服务端
(
https://github.com/BeiDouMS/BeiDou-Server
)为基础进行制作
北斗服务端自带有一个包含中文注释的事件模板，路径位于：
scripts-zh-CN/event/0_EXAMPLE.js
但是召唤Boss这些功能还需要另外构造代码，本贴简单讲解如何实现召唤。
本次例子以修复东方神州大王蜈蚣事件为例子（NPC使用NextLevel语法构造）：
事件与NPC脚本均有注释以方便理解
以下代码为事件自带的变量，可以根据需求自定义
代码登录后可见
以下为设置事件要求信息
代码登录后可见
以下为设置事件召唤Boss关键代码
代码登录后可见
以下为NPC开始事件代码
代码登录后可见
关键代码基本上就由这几部分组成，剩下的请参考完整代码里的注释
由于篇幅受限，完整代码改为附件形式。
1、将下方附件下载后重名为并保存到路径：scripts-zh-CN/event/WuGongPQ.js
WuGongPQ.js.chm
13kB
2、将下方附件下载后重名为并保存到路径：scripts-zh-CN/npc/9310006.js
9310006.js.chm
2kB
至此，完成了大王蜈蚣召唤事件。

---

**#2楼**

正在學習事件方面的，太感謝了。:loveliness: