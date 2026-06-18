# GMS083 NPC 商店多货币支持

> 来源：https://moguwuyu.com/d/48
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

原帖地址
https://discord.com/channels/350831332609359875/1363796913811361892/1363796913811361892
支持不同的NPC商店使用不同的货币，原版最多只支持一种音符货币
代码：
https://gist.github.com/sirdanielot/a669ca79b51e675fb39787de6bbd68c2
这不一定是完美的实现，此功能仅由原作者测试过，并非在公共服务器环境中进行测试，因此可能存在问题，数据库 + 源代码实现适用于 Cosmic。
WZ 编辑需要将“iconShop”字段添加到 Item.wz/Etc/0431.img/<itemId>/
不同商店支持不同货币，但是和高版本的不同，083 依然是每个商店只能使用一种货币类型。