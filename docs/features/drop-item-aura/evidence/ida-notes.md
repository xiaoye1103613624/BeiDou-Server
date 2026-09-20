# IDA 校准记录（v83 客户端）

> 工具：`ida-pro-mcp`（本地 IDA Professional 9.4，`127.0.0.1:13337`）
> 依据：IDA 实际查询结果，非记忆/推测

## 目标二进制

| 项 | 值 |
| --- | --- |
| IDB | `E:\download\v83.i64` |
| module | `` `Angel.exe `` |
| input path | `C:\Users\Admin\Desktop\v83 Server\`Angel.exe` |
| imagebase | `0x400000` |
| image size | `0xa95000` |
| arch | 32-bit |
| 函数总数 | 55015（named 3338） |

## 7 个硬编码地址核对结果

原 mod（`dropitemaura.cpp` / `itemaura.cpp`）中的地址**全部命中**当前 v83 客户端：

| 常量 | 地址 | IDA 解析 | 判定 |
| --- | --- | --- | --- |
| `kOnDropEnterField_jmp` | `0x00506385` | `?OnDropEnterField@CDropPool@@IAEXAAVCInPacket@@@Z`（函数起点 `0x505900`，size `0xB15`） | ✅ 落在函数内 |
| `kOnDropEnterField_ret` | `0x0050638A` | 同上 | ✅ `+5` = 一条 `CALL` 指令长度，自洽 |
| `kDecode1` | `0x004065F3` | `?Decode1@CInPacket@@QAEEXZ`（size `0x36`） | ✅ 精确入口 |
| `kLoadSingleLayer` | `0x0043EEFC` | `?LoadSingleLayer@CAnimationDisplayer@@SA?AV?$_com_ptr_t@...IWzGr2DLayer@@...`（size `0x405`） | ✅ 精确入口 |
| `kPutcolor` | `0x0045144A` | `?Putcolor@IWzGr2DLayer@@QAEXK@Z`（size `0x24`） | ✅ 精确入口 |
| `kCreateLayer` | `0x00426C7E` | `?CreateLayer@IWzGr2D@@QAE?AV?$_com_ptr_t@...IWzGr2DLayer@@...`（size `0x5D`） | ✅ 精确入口 |
| `kDROP_dtor` | `0x0050ACA8` | `sub_50ACA8`（size `0x44`，未命名） | ⚠️ 地址命中，但**语义未确认** |

### 已确认（2026-09-18 用 IDA 复核）

- **CInPacket 布局（A2 定稿）**：反编译 `CInPacket::Decode1`（`0x004065F3`）得到：
  - `m_pData = *(this + 0x08)`（数据缓冲）✅ 与 mod 一致
  - `m_nLen  = *(this + 0x0C)`（长度）⚠️ **mod 原无此用法；客户端 C4 取包尾 grade 必须用 `+0x0C`，而非推测的 `+0x04`**
  - `offset  = *(this + 0x14)`（读指针，Decode1 自增）
  - 客户端 `itemaura.cpp::ReadGradeFromTail` 已据此次确认改为 `len = *(int*)(pPacket + 0x0C)`。
- **DROP 对象成员（OnDropEnterField `0x505900` 反编译）**：`v115` 即 DROP 对象指针，封包解析写入成员：
  - `DROP[0x08]=objectId(Decode4)`、`DROP[0x09]=Decode4`、`DROP[0x0A]=Decode4`、`DROP[0x0B]=Decode1`、`DROP[0x0C]=Decode1`、`DROP[0x0D]=Decode4`、`DROP[0x1C]=type/mod(Decode1)`
  - 落点 `x=Decode2`、`y=Decode2` 经 foothold 校正存入 `v107(x)`/`v97(y)`，再由 `v116(=DROP)` 的虚函数（`vtab+180`）设位置：`10*(3000*v97 - v107)...`
  - 客户端原生解析的 x/y 为**世界坐标**（经 foothold 校正），与 mod 从封包 buf 直接读的静态 x/y 方向一致。

### 仍待确认（动态 / 实测）

- **A3 逐帧跟随是否必要**：取决于 `IWzGr2D::CreateLayer/LoadSingleLayer` 图层坐标系。若 WZ 图层用**世界坐标**（渲染时由相机变换），则 mod 传世界坐标已自动跟随镜头，**无需每帧减相机偏移**；若用屏幕坐标则需逐帧跟随。需 E2 实测镜头移动是否脱钩。
- **A4 真实 dropZ**：`CreateLayer` 的 z 参数语义（是否层序、是否与 `DROP.z(=y)` 同空间）静态分析无法定，需 E2 实测 front/back 是否夹住道具。当前 C3 暂以 `dropZ=0` 占位（`kAuraLayerZ`）。
- **pDrop 身份**：`OnDropEnterField_hook` 经 `push esi; push ecx` 传入，第二参（ecx）实为 `CDropPool*`（this），**非单个 DROP**。这影响 C2 逐帧读 DROP 实时坐标的可行性，需重新确认 hook 取参方式（C6）。
- `sub_50ACA8` 是否确为 `CDrop` 析构 + 候选偏移 `{pEntry, pEntry+0x10}` 仍待 xref 复核（C6）。

## 结论

协议插入点（包尾 grade）已定稿（A2 完成，并纠正 `len` 偏移为 `0x0C`）。坐标跟随 / z 取值（A3/A4）已取得静态证据（DROP 成员偏移、世界坐标方向），但 **C2 逐帧跟随必要性 与 C3 真实夹层 z 仍需 E2 实测**（因 WZ 图层 z 坐标系与 pDrop 身份无法纯静态定论）；原 7 个硬编码地址仍全部有效。
