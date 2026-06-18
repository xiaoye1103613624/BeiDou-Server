# 求教客户端给服务端封包的RecvOpcode是根据什么来判断的？

> 来源：https://moguwuyu.com/d/71
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

如题：求教客户端给服务端封包的RecvOpcode是根据什么来判断的？
问题详细描述：在wz/Item.wz/Consume文件下比如0243.img.xml文件中，有的是触发NPC的逻辑，有的是直接走使用道具增益等效果
问题点：
1.客服端封包发送过来的属性值RecvOpcode是在客户端写死的某个ID就是某个类型？还是说根据物品img中的某个节点属性生成？
2.如果是根据某个节点属性生成的，为何同一个物品我只写一个spec节点，有的是走的ScriptedItemHandler有的走UseItemHandler？比如北斗书就是走的ScriptedItemHandler，我把北斗书的节点内容复制到其他物品下确实走的UseItemHandler
综上有没有办法控制这个物品走什么Handler逻辑？

---

**#2楼**

答案就在问题里，这个东西就是客户端控制的，改客户端或者利用已有的规则达到自己想要的效果。
实在不行就让新增道具统一走UseItemHandler，然后在这个handler里对道具做判断是否要转到其他方法里执行对应效果

---

**#3楼**

leevccc
谢了，那我知道了，目前新增逻辑都是在UseItemHandler里处理的