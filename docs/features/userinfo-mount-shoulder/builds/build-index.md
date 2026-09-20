# 构建索引

| 产物 | 路径 | 备注 |
| --- | --- | --- |
| ijl15.dll | `E:\project\BeiDou-ijl15\out\Release\ijl15.dll` | Release\|x86 |
| 已部署 | `E:\MXD\BeiDou-Client_S9\ijl15.dll` | PostBuild xcopy；2026-09-17 ~13:25 |
| Stamp | `USERINFO_MOUNT_BP20_PREVIEW_20260917` | DLL 内 ASCII 可搜 |
| 源码 | `ezorsia/userinfomount/userinfomount.cpp` | DeferredBoot 调用 `UserInfoMount::ApplyPatches()` |

构建命令：`BeiDou-ijl15\build_once.bat`（MSBuild Release x86）。
