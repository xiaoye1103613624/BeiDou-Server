# OrzRepacker 项目开源

> 来源：https://moguwuyu.com/d/274
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

OrzRepacker已经发布很久了，看起来已经逐步稳定，没有太多的bug。是时候把这个孩子交给社区自己成长了。
最开始用java完成这个作品是希望放在服务端上实现统一的wz解析接口的，你可以轻松的使用 new WzFIle或者new WzImage来加载文件，然后调用getChild、getChildren或者getValue来读取各种值
Github
https://github.com/leevccc/orange-wz
启动文件 OrangeWzApplication.java
Orz目前不支持 .ms 格式，不支持编码BC7（可以解析BC7），其他该支持的基本上都支持了。核心代码均是复刻自各类写轮眼以及WzCompare，只不过是在java上实现，理论上是支持跨平台的。
想要添加新功能的话
在右键菜单
gui/component/menu
里注册按钮
在
EditPane
里完成具体的代码
以上都可以参考原来功能是如何实现的
最最基础的功能demo
EditPane
里搜索
打开
void load
保存
void save
导出
void export
更多的功能实现可以自行查看提交记录
WzFile和WzImage对象在初始化完成后，需要在使用前调用parse方法，将内容解析进来，这个方法有防呆机制多次调用也没事。

---

**#2楼**

太牛了

---

**#3楼**

嗐得是Ling总！

---

**#4楼**

无敌的ling总

---

**#5楼**

太伟大了！
对了 OrzRepacker目前好像打不开同步服版本的韩服国服，是因为文中所说的不支持.ms格式吗？

---

**#6楼**

让Orz伟大！

---

**#7楼**

foggy
不清楚，不支持.ms指的是新版的以.ms结尾的文件，我没时间弄这一块了，要实现也是可以的
最新的 CMS 的 Packs目录里就有

---

**#8楼**

我等小白受益菲浅

---

**#9楼**

leevccc
懂了，ALL IN RECALL PARK!

---

**#10楼**

66666支持下，，

---

**#11楼**

ling总太酷了！

---

**#12楼**

:👍  支持

---

**#13楼**

支持！

---

**#14楼**

太酷辣

---

**#15楼**

作者伟大！
！Orz伟大！

---

**#16楼**

作者伟大！！Orz伟大！

---

**#17楼**

感谢大佬为社区付出

---

**#18楼**

还能说什么呢，Orz伟大！大佬🐂🍺！

---

**#19楼**

ling总牛逼！

---

**#20楼**

感谢分享！