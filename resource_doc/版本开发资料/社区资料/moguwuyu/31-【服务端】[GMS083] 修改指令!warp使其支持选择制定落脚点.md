# 【服务端】[GMS083] 修改指令!warp使其支持选择制定落脚点

> 来源：https://moguwuyu.com/d/31
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

如果你需要在某些情况下调试角色的传送落脚点，比如事件初始位置，那么可以参考这个方法
本教程以
北斗服务端
(
https://github.com/BeiDouMS/BeiDou-Server
)为基础进行制作
本次思路为服务端在接收到!warp指令时，会随机指定一个落脚点，因此我们需要将随机改为指定。
打开源码路径：
gms-server/src/main/java/org/gms/client/command/commands/gm2/WarpCommand.java
按下Ctrl+F搜索
代码登录后可见
将下方的
代码登录后可见
替换为
代码登录后可见
指令格式示例与解释：
指令：!warp 910000000 1
解释：指令          地图Id        落脚点索引
数组索引：                   0                  	1
指令：!warp 701010322 st07
解释：指令          地图Id        落脚点名称
数组索引：                   0                  	1
params[1] 指定分割的字符串索引，既落脚点字符串。

---

**#2楼**

很多任务地图的初始 portal 都用的相同 name ，用 name 可能会更好。
是否存在某个 ID 就是默认的初始 portal ？

---

**#3楼**

leevccc
默认落脚点应该都是int索引：0
似乎是第一个创建的落脚点