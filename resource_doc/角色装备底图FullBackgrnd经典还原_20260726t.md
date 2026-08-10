# 角色装备底图实际写入 — 2026-07-26t

## 结论（先看）

| 项 | 结果 |
|----|------|
| 用户说「底图没有更换」 | 成立：此前只比过尺寸，**从未**对 PNG 做 MCP 写入 |
| `Equip/backgrnd` | **本来就是**经典窄栏参考图（与用户 PNG **字节级一致**，无需改） |
| 真正看得见的差异源 | `pendant2` flag=1 时客户端走 **`Equip/FullBackgrnd`**（原 208×291、双项链展开栏），与经典 `backgrnd` 重叠区约 **54% 像素不同** |
| 本轮实际写入 | **是** — `FullBackgrnd` ← 经典参考 PNG，`ARGB4444`，尺寸改为 **175×304** |
| ijl15 第二坠 | **未动** — 仍 `PENDANT2_SLOT7_20260726s` 坐标 `(38,101)` |

戳记文档：`EQUIP_BG_FULLBG_CLASSIC_20260726t`

---

## A. 像素证据（写入前）

参考 PNG：  
`…/assets/…/backgrnd-27fd9703-8f6c-4642-b9b8-ae902da6f05c.png`  
SHA256 `cc81528b6f87cb5a879587a1f6a11970b080dd99a10265d2c0d1b2aa99828e2a`，175×304

| 节点 | LIVE（写前） | V16 | vs 参考 |
|------|--------------|-----|---------|
| `Equip/backgrnd` | 175×304 ARGB8888，file SHA=`cc81528b…` | 同 LIVE | **像素差 0**（已是经典） |
| `Equip/FullBackgrnd` | **208×291** ARGB8888，px=`973bce02…` | 同 LIVE | **≠** 经典（双项链展开底图） |
| `Equip/pet` | 177×181 | 同 | — |

备份 `bak_pre_fullbg` / `bak_before_restore` 的 `FullBackgrnd` 与 LIVE **相同**（无更经典的 FullBackgrnd 源）。

---

## B. MCP 写入过程

1. orange-wz `mutate_nodes` `set_png`：  
   - root：`BeiDou-Client_1\Data\UI\UIWindow.img`  
   - node：`Equip/FullBackgrnd`  
   - 源：用户经典 `backgrnd` PNG  
   - `pngFormat`：**ARGB4444**
2. `save_node` 因文件被 MCP 进程占用，只落到同目录 **`UIWindow.img.bak`**（11877825 B，工具设计上的占用回退）。
3. 安全备份旧 LIVE → `UIWindow.img.bak_before_classic_fullbg_20260726t`。
4. 短暂停止 orange-wz（`:10002`）→ 将 MCP 写出的 `.bak` 提升为 LIVE → `ensure-mcp.ps1` 拉起。

**未** robocopy/Copy-Item 外来 `.img` 包体；未碰 `EN\`。

---

## C. 写入后核对

| 节点 | 写后 | 说明 |
|------|------|------|
| `Equip/backgrnd` | 175×304 ARGB8888，px==参考 **True**，diff=0 | 未改 |
| `Equip/FullBackgrnd` | **175×304 ARGB4444**，vs 参考 diff≈7420 | 布局/内容已是经典；差值为 4444 量化 |
| `UIWindow.img` | size **11877825**（原 11875071） | 已落盘 |

---

## D. 游戏内验证

1. **完全退出**所有 `BeiDou.exe` 后重开 Client_1（WZ 缓存）。  
2. 开「角色装备」：底图应为经典窄栏（单「项链」格、骑兽/鞍子底行），**不应**再是展开栏双项链 + 右下宠物区嵌进主底图。  
3. 对照参考 PNG / 红标布局图；第二坠仍在红标 **7** ≈ `(38,101)`（ijl15 戳记 `PENDANT2_SLOT7_20260726s`）。  
4. 若仍像展开栏：确认读的是 `BeiDou-Client_1`，且 `ijl15`/`pendant2_ui` 未切到别的客户端目录。

回滚：把 `UIWindow.img.bak_before_classic_fullbg_20260726t` 经 MCP `load`→`save_as` 写回（或停 MCP 后替换），勿裸拷不明来源包。

---

## E. 相关

- `装备栏第二坠残留修复_20260726r.md` — 曾误判「尺寸相同无需还原」  
- `装备栏第二坠移至红标7_20260726s.md` — slot7 坐标  
- 工作文件：`E:\pro\orange-wz\_boot_fix\equip_bg_compare\`
