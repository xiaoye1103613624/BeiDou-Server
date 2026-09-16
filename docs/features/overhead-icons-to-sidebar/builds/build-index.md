# 构建索引

## 插件

| 项 | 值 |
| --- | --- |
| 产物 | `E:\project\BeiDou-ijl15\ezorsia\out\Release\ijl15.dll` |
| 大小 | 1882112 |
| 时间 | 2026-09-16 10:18:00 |
| SHA256 | （见本机构建） |
| 同步目标 | `E:\MXD\BeiDou-Client_S9\ijl15.dll`（已覆盖，SYNC_OK） |
| 构建命令 | `MSBuild ezorsia.vcxproj /p:Configuration=Release /p:Platform=Win32` |

## 服务端

| 项 | 值 |
| --- | --- |
| 迁移 | `V1.11.58__overhead_icons_to_sidebar.sql` |
| 产物 | `gms-server/target/BeiDou.jar` |
| 构建 | `JAVA_HOME=.../ms-21.0.12.1 mvn -pl gms-server -DskipTests package`（2026-09-16 通过） |

## 脚本

- `scripts-zh-CN/BeiDouSpecial/xy/portal/任务提醒.js`
- `scripts/BeiDouSpecial/xy/portal/任务提醒.js`（英文桩）
- `北斗助手.js` 增加 id=10 任务提醒
