# 角色能力详情面板（Stat / backgrnd4）

> 状态：已闭环  
> 适用客户端/协议版本：BeiDou v83 / GMS 协议  
> 项目名称：BeiDou-Server_s9 + BeiDou-ijl15  
> 日期：2026-09-16  
> 产物目录：docs/features/stat-detail-panel/

## 1. 背景与目标

- 能力详情改为始终使用 `UI/UIWindow.img/Stat/backgrnd4`（237×239），并绘制经典副属性 + 战斗属性。
- 开合保留原生滑入动画；修复插件在关闭动画期间误判为仍打开并 `EnsureDetailLayout` 打断收缩的问题。
- 不做：关闭瞬间 Destroy（方案 A）；不改滑入 X 偏移 `0xAA`（该值是主窗宽度近似，不是详情面板宽）。

## 2. 影响范围

| 层级 | 路径/模块 | 变更类型 |
| --- | --- | --- |
| 插件 | `BeiDou-ijl15/ezorsia/statdetail/statdetail.cpp` | 开合状态机 + CreateWnd/关闭钮补丁 |
| 客户端 | `BeiDou-Client_S9/ijl15.dll` | 构建同步 |
| WZ | `Data/UI/UIWindow.img` → `Stat/backgrnd4` | 已有资源，本次未改 |

## 3. 最终实现逻辑

### 3.1 开合状态机

原生 `CUIStat::ToggleDetail`（`0x008C545D`）：

- 打开：创建 `CUIStatDetail`，`CUIStat+0x5A4 = 1`，再 `sub_8C5531` 滑到 `StatLeft+0xAA`。
- 关闭：先清 `+0x5A4`，滑向 `StatLeft+0`，若干帧后 `Destroy`。**关闭动画期间详情单例 `*(0x00BF10C8)` 仍在。**

插件 `StatBtn_Hook`（详情按钮 id 2006）：

1. 调用 orig **前**记录是否已开（优先读 `CUIStat+0x5A4`，`CUIStat*` @ `0x00BF10C4`）。
2. 调用后：
   - **打开**：`MarkDetailOpened` → 重置 layout → `SetBackgrnd(backgrnd4)` + Invalidate。
   - **关闭**（flag 已清、单例可能仍在）：`MarkDetailClosed`，**禁止** `g_layoutReady=false` / `EnsureDetailLayout`。
3. `DetailBtn_Hook`（关闭钮 id 1000）：只清插件 open 标志，不在关闭路径重布局。
4. `Draw_Hook`：滑入收尾期仍可叠绘；不因关闭清 layout。

### 3.2 尺寸与关闭钮

`AttachHooks` 时内存补丁（`push imm32` 立即数）：

| 地址 | 含义 | 新值 |
| --- | --- | --- |
| `0x8C510A+1` | CreateWnd 宽 | 237 |
| `0x8C5105+1` | CreateWnd 高 | 239 |
| `0x8C2754+1` | 关闭钮 X | 215（右缘边距与原生一致） |
| `0x8C274F+1` | 关闭钮 Y | 218 |

开合/拖拽共用的放置偏移已按新尺寸补丁（否则会盖掉 ctor 微调）：

| 地址 | 含义 | 原值 | 新值 |
| --- | --- | --- | --- |
| `0x8C54A9+1` / `0x8C57BF+1` | Y = StatTop + off | 144 | 109（348−239，底对齐） |
| `0x8C5760+1` / `0x8C575B+1` | 打开 X 偏移 / 关闭掩码 | +170 / −170 | +176 / −176（贴主窗右缘） |

### 3.3 数值绘制

- 左列：PAD/PDD/MAD/MDD/ACC/EVA/Craft/Speed/Jump + 暴击率/暴击伤害。
- 右列：`SetItemData::g_combatPanel` 战斗属性（伤害、最终伤害、BOSS、无视、异常抗性等）。

## 4. 资源与同步

- `backgrnd4` 已在客户端 `Data/UI/UIWindow.img`。
- 插件改完经 `build_once.bat` PostBuild 覆盖 `E:\MXD\BeiDou-Client_S9\ijl15.dll`。

## 5. 编码与 i18n

- 插件日志为 ASCII/`std::cout` 英文节点日志；游戏内数值为数字，无额外文案。

## 6. 产物清单

| 类型 | 路径 | 说明 |
| --- | --- | --- |
| 文档 | README.md | 本文件 |
| 证据 | evidence/ | 开合问题录屏帧与原视频副本 |

## 7. 验证记录

- 构建：`BeiDou-ijl15/build_once.bat` → `out/Release/ijl15.dll`（已部署客户端）。
- IDA：ToggleDetail / OnCreate close@(155,182) / CreateWnd 177×203 / 滑入 `0xAA`。
- 客户端验收（需进游戏）：能力 → 详情贴合无缝且底对齐 → 拖拽主窗详情跟随不错位 → 再点详情滑入关闭 → 「←」可关。

## 8. 预留、风险与回滚

- 关闭滑入数帧内，宽面板相对主窗可能短暂露边（方案 B）。
- 对齐依赖 `CUIStat` CreateWnd 尺寸 176×348；若以后改主窗尺寸，需同步改 `kPlaceOffsetX/Y`。
- 回滚：恢复旧 `ijl15.dll` 或回退 `statdetail.cpp` 补丁。
