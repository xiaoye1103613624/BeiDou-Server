# kinoko 伤害统计

> 来源：https://moguwuyu.com/d/213
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

原文地址
https://discord.com/channels/350831332609359875/1479035973022126236
作者
@foggy
This is a custom UI implemented based on the Kinoko project by inheriting the client native structure CUIWnd.
To accommodate different preferences, I designed two UI styles based on the pre-bb and post-bb, which can be switched through the code.
Currently, the Create of UI is triggered by hooking the F12 hotkey. Under normal circumstances, this would require modifying CFuncKeyMapped and implementing corresponding changes on both the client and server sides. However, since this is not the main focus of this feature, that part has been left for users to implement themselves if needed.
The damage statistics data comes from the UserRemote.attack packet broadcast by the server when handling AttackHandler. I intercept this packet, parse it according to the original logic to obtain the relevant data, then restore the read offset and pass the packet back to the client. As a result, the required server-side modification is minimized to only a single line of change. The specific usage details for WZ and the client have also been added to the documentation.
One known remaining issue is that damage from Burn type effects is not broadcast via UserRemote, so it cannot currently be captured by this system. To support this, a custom packet needs to be added on the server side to trigger the corresponding update. Since server implementations vary across different codebases, this part is also left for users to implement according to their own server logic.
Special thanks to @teto for the Kinoko project and to everyone for their helpful guidance in this channel. As I continue learning, I increasingly appreciate the convenience and potential provided by the excellent architecture of the Kinoko project. Due to my limited technical skills and aesthetic sense, I would greatly appreciate it if you could share any bugs or shortcomings you encounter after trying it.
DamageStatistic.zip
54kB

---

**#2楼**

学习学习

---

**#3楼**

学习学习

---

**#4楼**

爱好者总是比官方牛逼

---

**#5楼**

太强了 ，前几天就在B站仰望大佬的创作
。

---

**#6楼**

太强了

---

**#7楼**

好厲害... 來學習研究看看~  !

---

**#8楼**

很强！已经实现了

---

**#9楼**

leevccc
我想放弃了，你太牛了，打不过。

---

**#10楼**

ziqiming
压缩包里写得很清楚了😳

---

**#11楼**

这个项目适用北斗插件吗？好像缺失了
这么多头文件。

---

**#12楼**

wj1yb1
不适用，这个是95的

---

**#13楼**

大佬都太牛了，一个想法就能实现。我看看就行

---

**#14楼**

楼主大佬，095是不是没有汉化好的呀

---

**#15楼**

果果
是的，95是底子好，但是不少功能没完成，没汉化

---

**#16楼**

蓝蜗牛
好的 谢谢，那再等等，直接改083的 实在改不动