# 资源清单

| 资源 | 路径 | 状态 |
| --- | --- | --- |
| 坐骑模型 img | `E:\MXD\BeiDou-Client_S9\Data\Character\TamingMob\01902242.img` | 已有，140647 bytes |
| 坐骑 XML（服务端） | `gms-server/wz/Character.wz/TamingMob/01902242.img.xml` | 18623 bytes，与 V095 同源 |
| TamingMob 定义 | `gms-server/wz/TamingMob.wz/0010.img.xml` | 已存在（info.tamingMob=10） |
| 中文名 | `wz-zh-CN/String.wz/Eqp.img.xml` → `1902242` = `飞天坐骑1902242` | 已改 |
| 英文名 | `wz/String.wz/Eqp.img.xml` → `Sky Mount 1902242` | 已补 |
| 护肩对照 | String `1152206` = 星之国肩饰 | 仅对照，未改 |
| 插件 stamp | `MOUNT_BP20_OFF_ROW_PET_RED_20260916b` | 已进 Client_S9 DLL |

脚本：`fix_mount_string.py`（幂等改服务端 String）。
