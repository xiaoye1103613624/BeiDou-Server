# 资源清单

| 资源 | 路径 | 状态 |
| --- | --- | --- |
| 客户端 String | `E:\MXD\BeiDou-Client_S9\Data\String\Eqp.img` | `Eqp/Taming/1902242/name` = 小浣猪（已校验） |
| 备份 | `...\Eqp.img.bak_before_xiaohuanzhu_20260917` | 覆盖前副本 |
| 中间产物 | `resources/Eqp.img.patched_xiaohuanzhu` / `Eqp_patched.img` | save_as 输出 |
| 服务端 zh | `gms-server/wz-zh-CN/String.wz/Eqp.img.xml` | 1902242 → 小浣猪 |
| 服务端 en | `gms-server/wz/String.wz/Eqp.img.xml` | 1902242 → Hog Mount |
| 脚本 | `fix_mount_string_xiaohuanzhu.py` | 幂等改服务端 XML |
| 密钥 | orange-wz `083-GMS` iv=`TSPHKw==` | 仅本机工具用，勿提交明文到论坛 |

对照：经典坐骑 `1902000` 名称本就是「小浣猪」；1902242 与之 `tamingMob=10` 同族。
