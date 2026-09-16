# 选频道闪退定位（0x80030002）

- 时间：2026-09-15
- 客户端：`E:\MXD\BeiDou-Client_S9`
- 错误：`error code : -2147287038 (Unknown error 0x80030002)` → `STG_E_FILENOTFOUND`
- 日志：`beidou-wz-last.log`
  - 已加载：`Character/Cap/01009925.img`（阿尔泰）
  - 失败：`getobj_inflight=Character/00000000.img`
- 根因：`PacketCreator.addCharLook` 在阿尔泰过滤时写 `face=0` / `hair=0`
- 修复：改为始终写真实 face/hair；装备仍只发 Cap
