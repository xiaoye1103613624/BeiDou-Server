# Kinoko 角色名/家族名增加中文检测

> 来源：https://moguwuyu.com/d/168
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

GameConstants.java 替换 isValidCharacterName 方法
代码登录后可见
这样这个方法就具备了对中文字符的检测
ServerConstants.CHARSET 见
https://moguwuyu.com/d/167
角色名检测
LoginHandler.java 将
代码登录后可见
修改为
代码登录后可见
家族名检测
GuildPacket.java 添加
代码登录后可见
GuildHandler.java 在
代码登录后可见
下面添加
代码登录后可见