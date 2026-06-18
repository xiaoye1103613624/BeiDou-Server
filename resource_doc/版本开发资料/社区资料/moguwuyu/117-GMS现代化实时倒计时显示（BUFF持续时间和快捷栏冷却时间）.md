# GMS现代化实时倒计时显示（BUFF持续时间和快捷栏冷却时间）

> 来源：https://moguwuyu.com/d/117
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

本教程为GMS低版本客户端添加了现代化的实时倒计时显示，包括：
右上角Buff图标持续时间倒计时（剩余buff时间）
快捷栏技能CD倒计时（剩余冷却时间）
自动在分钟/秒之间切换，倒计时在≥1分钟时在左下角显示分钟数，<1分钟时在正中间显示秒数
GMS在v233版本Destiny: Remastered版本（2022/6/15）中首次引入了居中的秒数倒计时显示，以下是对比图：
GMSv92现代化效果图：
GMS Reboot v233+对比参考图：
前置说明：
Buff图标持续时间倒计时是在kinoko(
https://github.com/iw2d/kinoko_client
)或者kaentake(
https://github.com/iw2d/kaentake
)的实现基础上进行修改的。并且下面的代码中有许多类定义、ztl、宏定义都来自于kinoko。因此，如果你没有在使用这两套client edit的话，你可能需要先花点时间引入相关的部分。
代码中的函数地址或类偏移均为v92版本的硬编码作为例子。如果你要迁移到其他版本，需要自行查找对应的地址。当然，如果你有相应版本idb作为参照的话，这不会是什么太难的事。
附件请解压出UIWindow99.img并放到UI.wz或者Data/UI下（取决于是wz客户端还是img客户端），里面是v233+版本的数字图片。
核心原理：
主要包含两个函数的hook：
CTemporaryStatView::TEMPORARY_STAT::UpdateShadowIndex，用于显示右上角Buff图标的倒计时阴影
CUIStatusBar::CQuickSlot:😃rawSkillCooltime，用于显示快捷栏技能图标的冷却倒计时阴影
在重写原函数或直接调用原函数后，通过调用客户端的draw_number_by_image方法实现数字绘制
其他细节：
起初在快捷栏绘制的数字会产生叠加，直到下一帧阴影才会重置。CUIStatusBar::CQuickSlot:😃rawSkillCooltime在v95的pdb中可以看到if ( v21 >= 0 && *v27 != v21 )，说明倒计时阴影只有当下标发生变化时才会copy canvas。因此额外添加了re:😛atchNop(0x0085136B, 6);跳过了阴影帧下标的检查，这样游戏每帧画面都能刷新技能图标的绘制。
每个技能的冷却时间实际上存放在CWvsContext::m_mSkillCooltimeOver这个哈希表中，通过ZMap<long, long, long>::GetAt方法可以查找哈希表，key是技能ID，value是冷却结束时间戳（以毫秒为单位）。这个时间戳其实是自电脑启动以来经过的毫秒数，即timeGetTime()，由于是DWORD实际上会在49天后loop back，如何修复（或者说缓解）这个漏洞就是另一回事了。
各个类的成员变量偏移可以自行对照v95的pdb寻找，值得注意的是FUNCKEY_MAPPED的nID实际上在0x1偏移处，而不是0x0。所以m_aFuncKeyMappedInfo的偏移是要在对应汇编的那行中额外减去1的。
接下来是代码部分：
CountdownDisplay.h
代码登录后可见
CountdownDisplay.cpp
代码登录后可见
CUIStatusBar.h
代码登录后可见
CUIStatusBar.cpp
代码登录后可见
CTemporaryStatView.h
代码登录后可见
CTemporaryStatView.cpp
代码登录后可见
Utility.h
代码登录后可见
zmap.h
代码登录后可见

---

**#2楼**

TETO这套东西是真的好用 用了之后减少好多代码量