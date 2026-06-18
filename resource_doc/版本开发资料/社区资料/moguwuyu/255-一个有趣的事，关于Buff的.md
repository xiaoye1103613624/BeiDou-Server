# 一个有趣的事，关于Buff的

> 来源：https://moguwuyu.com/d/255
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

Note on this - the vanilla client implementation for handling the buff duration is by recording the remaining duration, and subtracting -30 each game tick, but this does not happen while inside cash shop, effectively freezing the buff durations if the user decides to go into the cash shop with an active buff.
In the kinoko server (and also confirmed in the BMS leak), the buff duration is handled by simply recording the expiry time, without any kind of special handling for going into the cash shop. This can cause a desync with the buff expiration times between the client and the server, where the server will expire the buff at the correct time, but the client will show the buff having plenty of time remaining before it disappears.
(Cosmic (v83) server handling does freeze the buff durations while in cash shop, hence this fix was not ported to kaentake)
简而言之就是说，基于BMS泄露版，MS 只会简单的记录BUFF的结束时间。不会因为玩家进入商城而延长BUFF的剩余时间。Kinoko 服务端也是这么处理的。
但是Cosmic会在玩家进入商城时保存buff状态，并在玩家退出商城时恢复buff，这属于魔改部分。我之前发布的
死亡、掉线保留buff
也是参考cosmic的这个功能实现的

---

**#2楼**

充分说明世界是一个巨大的草台班子

---

**#3楼**

商城不恢复其实挺好的，不然进商城留BUFF就会变成一种游戏策略，比如带着黑龙BUFF抢远征的时候进商城等。
在商城的时候不能聊天，看不到消息和喇叭，其实挺枯燥的。

---

**#4楼**

感觉这样可以增加判断，判断进入商城前角色所在地图如果是某些地图就不保留buff清空掉就好咯。