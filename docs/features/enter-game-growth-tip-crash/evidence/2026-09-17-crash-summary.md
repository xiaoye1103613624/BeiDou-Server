# 证据摘要 2026-09-17

## 崩溃块（client_boot / beidou-crash）

```
[10:37:22.453] *** CRASH code=0xC0000005 stage=set_stage END at CANVAS.DLL+0xED28
    Eip=0x5000ED28 Esp=0x001AD49C Ebp=0x001AD4A4 Eax=0x5000D5EA Ecx=0x00000000
    stack ... BeiDou.exe+0x4DE112  →  sub_8DDB30 @ 0x8DE112
```

## beidou-wz-last（同刻）

- `reason=VEH`
- RecentWZ 含 GrowthEnabled/Disabled（加载成功）
- `suspect_last=UI/StatusBar.img/base/quickSlot`
- `getobj_fail_hr=0x00000000`

## IDA / capstone

- `CANVAS.DLL+0xED28`：`mov esi,ecx; mov eax,[esi]` — this 为空时 AV
- `0x8DE0EC`：`push [ebp-14]` 后 `call [edx+80h]`（IWzCanvas::Copy）；源为 KeyConfig key glyph GetItem 结果
- Growth 字符串仅在 tip ctor 路径被 GetObject；与最终 EIP 无直接因果

## orange-wz

- Data/EN `UIWindow`：`ToolTip/Equip/GrowthEnabled|Disabled` 存在，canvas 有 PNG（ARGB8888）
- 服务端 XML Growth 子树无像素，**不可**用 XML 覆盖客户端
- EN StatusBar 原缺 `quickSlot32/08/26`；已从 Data 拷入并落盘（103347 字节）
