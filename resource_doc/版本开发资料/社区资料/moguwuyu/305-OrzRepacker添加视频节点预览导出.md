# OrzRepacker添加视频节点预览导出

> 来源：https://moguwuyu.com/d/305
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

新版资源文件里面一些技能使用video格式，导致无法预览，为了查看一些技能特效添加预览
可以选择导出图片和视频，图片可自由选择指定帧
另外还加了一些功能比如 图片压缩（旧版客户端需要ARGB8888，DTX5转ARGB8888反而体积增大，第一次释放技能可能会卡顿，通过压缩后会大幅改善）
因为DTX5比起ARGB8888肯定是更小的，所以可能存在压缩率不高的情况
图片压缩目前知道比较好的应该是https://tinypng.com/
500kb可以压缩到180kb，可惜不是开源的，有限制
目前使用https://github.com/Lcry/java-png-compress-util
500kb大概压缩到350kb（保持65%画质以上的情况下）
动画预览超50个节点添加分页，按节点名字筛选，优化内存回收机制
内存足够的可以分16G内存，bat启动命令
start "" jre\bin\javaw -Xms512m -Xmx16g -jar OrzRepacker.jar
源码地址：
https://github.com/gujichu/orange-wz/tree/guji

---

**#2楼**

现在官方演都不演了？直接给看视频了是吧

---

**#3楼**

start "" jre\bin\javaw -Xms512m -Xmx16g -jar OrzRepacker.jar
可用内存大于16G才可以用这个
不然还是去掉-Xms512m -Xmx16g， 否则启动不了

---

**#4楼**

功能越来越强大了