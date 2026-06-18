# GMS083 拆分椅子img为多个小img的例子

> 来源：https://moguwuyu.com/d/53
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

原帖地址
这个例子是将原本的椅子数据从0301.img拆分为0301x.img 并让客户端正确加载，可以尽情的举一反三推导出其他的使用场景，我就不举例了。
客户端部分
代码登录后可见
另外要修改 StringPool 中ID为2352的值
由
代码登录后可见
改为
代码登录后可见
因为img的文件名多了一个
然后你就可以把0301.img里的内容拆分到0301x.img中去了
服务端要修改的部分，应该是HeavenMS的端
修改src\server\MapleItemInformationProvider.java里的两个方法
代码登录后可见

---

**#2楼**

沙发！顶起来支持！

---

**#3楼**

感谢