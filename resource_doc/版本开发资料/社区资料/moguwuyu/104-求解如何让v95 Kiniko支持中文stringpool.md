# 求解如何让v95 Kiniko支持中文stringpool

> 来源：https://moguwuyu.com/d/104
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

在Kinoko玩了一段时间，一切正常。
开始考虑做一些汉化，于是尝试用插件自带的 REPLACE_STRING(63, "船长"); 做出类似的字符串替换，然后编译运行。
但是客户端马上变得非常不稳定。即使我只替换了一个职业名，也会不定时立刻崩溃。
担心是 REPLACE_STRING 重写stringpool内存的方式引发的不稳定，于是按照北斗v83插件的方式，hook了StringPool::GetString，根据ID主动替换满足KeyValuePair的结果并返回。
但是问题依旧，客户端仍然是不稳定。
有没有之前汉化过或者参与过类似工作的大佬，可以指点一二，感激不尽！
目前用的VS项目统一用GBK保存文件和编译

---

**#2楼**

REPLACE_STRING 暂时没遇到过你说的问题

---

**#3楼**

REPLACE_STRING重写stringpool反而是最不容易产生内存泄漏的
StringPool::GetString的hook很容易写出内存泄漏，因为人家需要的是返回ZXString<char>，大部分开源的代码拼了个char*以为完事了，实际上会因为没释放造成内存泄漏。