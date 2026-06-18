# 分享心得：汉化高版本Stringpool记录，以GMS232为例

> 来源：https://moguwuyu.com/d/264
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

说明
之前对客户端的技术完全不懂，小弟正在学习客户端逆向研究等技术，觉得会改客户端的都很牛皮，琢磨从汉化开始感觉会比较简单一点，想尝试自己汉化一个StringPool的版本，分享一点心得体会。
0. 打开客户端IDA文件
我也不会拿大佬做好的.
1. 找到原版StringPool两个地址
StringPool__GetString_t ：读取String的方法
ZXString_char__Assign_t   分配内存写入char的方法
StringPool__GetString_t
Strings搜：UI/UIWindow4.img/coordiKing/avatarUI/bgPreVote
进入方法：
代码登录后可见
前面两个就是：
代码登录后可见
2529表示的就是StringPool的下标2529的值读出来。进到最里面就是
ZXString_char__Assign_t
字符串找：UI/UIWindowEvent.img/sundayMaple/icon 然后找到引用就是：
说明：以上两个方法是我自己经过实战，找出来最简单找到的方法，当然使用特征值去找也行，但是64位我用特征值没找到。低版本这样找不到XD
2. 打印原版客户端的值
按照站长的说法进行尝试：
原理：hook到GetString，然后就去循环打stringpool里面的值。
代码登录后可见
客户端dump出来的结果如下：
3. 翻译成中文
省略，直接丢给AI帮你翻译
4. Hook客户端的StringPool
重新hook你的客户端
代码登录后可见
代码说明：
先查你自己的翻译表
代码登录后可见
作用：
加读锁（线程安全）
判断这个 nIdx 有没有你自定义的翻译
如果有 → 直接替换字符串
代码登录后可见
关键点：
代码登录后可见
清掉原来的字符串（避免内存问题 or 强制重新赋值）
代码登录后可见
等价于：   给 ZXString 赋值字符串
参数说明：
result → this
szEntry.c_str() → 你的翻译字符串
-1 → 自动计算长度（常见写法）
结果：原本游戏返回的字符串，替换成：  translate[nIdx]
如果没有 → 调回原函数
return StringPool__GetString1(result, nIdx);
后记
AI虽然加速了我们的开发和分析的速度，但是个人对于技术的理解感觉越来越浅，还是要多进行实战加深理解XD。
感谢Koson提供的GMS232的StringPool以供研究
客户端相关逆向以及翻译都不是特别熟悉和擅长，如有别的方法欢迎讨论~
北斗群能不能通过一下，想进去学习XD
参考：
https://moguwuyu.com/d/13