# Shoulder Slot in v83 (client kentakae)

> 来源：https://moguwuyu.com/d/291
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

如何在 MapleStory v83 中实现肩饰槽 (Shoulder Slot)
本教程将指导您如何在 MapleStory v83 服务端中添加肩饰槽。
服务端源码修改
首先，您需要修改服务端源码中的以下 Java 文件。
代码登录后可见
在定义中添加 SHOULDER 槽位：
代码登录后可见
代码登录后可见
添加 SHOULDER 类型及其 ID：
代码登录后可见
代码登录后可见
更新条件语句以包含肩饰物品 ID 范围 (1152000 到 1153000)：
代码登录后可见
客户端修改
现在，将以下 hook 和新文件添加到您的客户端注入器 (injector) 中。
代码登录后可见
声明 hook 函数并附加它们：
代码登录后可见
代码登录后可见
替换该槽位所需的字符串：
代码登录后可见
代码登录后可见
将新的 C++ 文件添加到您的注入器编译配置中：
代码登录后可见
接下来，在您的注入器文件夹中创建以下三个新文件：
代码登录后可见
此文件用于修复装备数据路径：
代码登录后可见
代码登录后可见
这确保客户端正确识别肩饰的身体部位 (body part)：
代码登录后可见
代码登录后可见
这将更新函数，以便客户端从物品中正确读取身体部位：
代码登录后可见
WZ 修改
准备好您的肩饰装备！将它们添加到 Accessory 文件夹中（ID 范围 1152000 --- 1153000）并配置对应的字符串 (strings)。
附加的客户端视觉修复
为了防止旧的怪物装备 (Mob Equip) 红色背景错位，我们不要移动真实的装备槽图表。相反，我们使用 hook 直接在客户端中纠正视觉表现。
此 hook 会将肩饰物品图标的绘制重定向到真实的肩饰位置，并确保鼠标悬停在视觉上的肩饰槽时，提示框 (tooltip) 会正确返回 BodyPart 20。在您的注入器文件夹中创建一个名为
代码登录后可见
的新文件：
代码登录后可见
代码登录后可见

---

**#2楼**

感谢分享，教程很详细。

---

**#3楼**

Update: It has been discovered that stats other than STR/DEX/INT/LUK do not work for the regular Shoulder, but they do work if the Shoulder is a Cash item!

---

**#4楼**

NEW Update for shoulder position, the older version showed a tooltip in the Mob Equip position.
on shoulderpos.cpp
寻找以下代码片段...
代码登录后可见
然后将其替换为以下内容：
代码登录后可见

---

**#5楼**

Fix for the shoulder to accept the remaining stats (ATT/MAGIC/DEF, etc.)
new file: shoulders.cpp
代码登录后可见
just this!

---

**#6楼**

its amazing tks !

---

**#7楼**

感谢分享！暂时还没到装备修改这一步 先回复收藏一下日后学习