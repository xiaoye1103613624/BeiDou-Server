# Kinoko GBK/UTF-8 中文编码支持

> 来源：https://moguwuyu.com/d/167
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

ServerConstants.java 添加
代码登录后可见
后续想修改成UTF-8的话直接修改这里的参数就可以了
NioBufferInPacket.java 将两个decodeString 方法替换成
代码登录后可见
NioBufferOutPacket.java 将两个 encodeString 方法替换成
代码登录后可见
这样 Kinoko 的服务端就能正常处理带 GBK 字符的包了

---

**#2楼**

大神 韩版可以这样处理让它显示简体中文吗

---

**#3楼**

编码我有些疑惑，包括BeiDou里的。
客户端似乎并没有修改编码相关的代码。如果用GBK的话还能理解，客户端在简中环境下可能默认选择GBK。但是UTF-8的话，客户端应该是不支持吧

---

**#4楼**

其实就支持UTF-8就行了。编码接的秦皇了吧

---

**#5楼**

sigeer
各个服编码格式
한국(1, "EUC_KR"),
韩服测试服(1, "EUC_KR"),
日本(3, "Shift_JIS"),
中国(4, "GB18030"),
测试服(5, "GB18030"),
台港澳(6, "BIG5-HKSCS"),
SEA(7, "UTF-8"),
GLOBAL(8, "UTF-8"),
BRAZIL(9, "UTF-8");
GMS、SEA是支持的最好的。UFT-8编码

---

**#6楼**

ziqiming
gms095客户端是GBK编码的，如果服务端要用UTF-8去解析客户端的包，客户端要做额外的修改，很麻烦。还是改成GBK好。

---

**#7楼**

rayallens
应该不行，这个只是修改服务端发给客户端的字符编码，具体能不能显示还是看客户端是否支持

---

**#8楼**

sigeer
低版本我怀疑是根据本地系统语言的设置走的