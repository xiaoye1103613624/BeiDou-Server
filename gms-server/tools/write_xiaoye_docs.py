# -*- coding: utf-8 -*-
from pathlib import Path

BASE = Path("E:/\u8d44\u6599/xiaoye/mxd\u5b66\u4e60/\u8096\u9097\u5192\u9669\u5c9b")
REPO = Path("E:/pro/BeiDou-Server_xy")
DOCS = REPO / "docs"
TOOLS = REPO / "gms-server/tools/xiaoye_docs"

HEADER_DS = """# \u4f24\u5bb3\u76ae\u80a4 \u2014 \u5b8c\u6574\u5b9e\u73b0\u6307\u5357\uff08Case C\uff09

> **\u9002\u7528**\uff1aGMS v083 \u5317\u6597\u670d\u52a1\u7aef + BeiDou-ijl15 \u63d2\u4ef6
> **\u9a8c\u8bc1**\uff1a2026-07-09 \u77e9\u9635\u4e8c\u5206 Case C PASS

---

"""

CASE_C_TABLE = """
## 8. Case C \u5b89\u5168\u90e8\u7f72\u6e05\u5355

| \u6587\u4ef6 | \u76ee\u6807\u5b57\u8282\u6570 | \u8bf4\u660e |
|------|-----------|------|
| `ijl15.dll` | **523,776** | Release Win32 lazy init |
| `Data/Effect/BasicEff.img` | **3,992,551** | \u4fdd\u6301\u539f\u7248\uff0c\u52ff\u66ff\u6362 |
| `Data/Effect/DamageSkin.img` | **70,058,716** | \u72ec\u7acb IMG |
| `Data/UI/UIWindow.img` | **12,160,040** | \u542b DamageSkin UI |
| `Data/String/Cash.img` | **287,095** | \u542b 5910000 |
| `Data/Item/Cash/0591.img` | **1,193** | 5910000 \u9053\u5177 |
| `Data/manifest.json` | **\u4e0d\u5b58\u5728** | \u52ff\u90e8\u7f72 |

---

## 9. \u63d2\u4ef6\u5173\u952e\u6587\u4ef6\uff08E:\\\\pro\\\\BeiDou-ijl15\\\\ezorsia\uff09

| \u6587\u4ef6 | \u804c\u8d23 |
|------|------|
| `damageskin/damageskin.cpp` | Effect_HP/Miss splice\u3001LoadDamageSkin\u3001CMob::OnHit |
| `damageskin/damageskinpicker.cpp` | \u9009\u62e9\u5668 UI\u30015910000 \u53cc\u51fb hook |
| `damageskin/DamageSkinBridge.cpp` | 0x170-0x173 \u6536\u5305\u3001EnsureHooks |
| `damagerank/DamageRankStage.cpp` | set_stage \u2192 EnsureHooks |
| `compat/LazyCompatInit.cpp` | \u9996\u6b21 CField \u5ef6\u8fdf\u521d\u59cb\u5316 |
| `compat/PacketDispatcher.cpp` | ProcessPacket\u30010x178 \u541e\u5305 + 0x170-173 \u4fa7\u6548 |
| `compat/wvs/util.h` | get_screen_height \u4f7f\u7528 Client::m_nGameHeight |

### PacketDispatcher \u89c4\u5219

- **0x0178**\uff1a\u5fc5\u987b\u541e\u5305\uff08vanilla \u65e0 handler\uff09
- **0x0170-0x0173**\uff1aDamageSkinBridge \u4fa7\u6548\u540e return 0\uff08\u542b **0x172**\uff09
- \u5176\u4f59 opcode\uff1a\u5fc5\u987b\u8f6c\u53d1\u539f\u751f ProcessPacket

---

"""

OVERVIEW = """# \u5317\u6597 GMS083 \u81ea\u5b9a\u4e49\u529f\u80fd\u603b\u89c8

> **\u9a8c\u8bc1\u57fa\u7ebf**\uff1a2026-07-09 Case C

## \u529f\u80fd\u7d22\u5f15

| \u529f\u80fd | \u6587\u6863 |
|------|------|
| \u4f24\u5bb3\u76ae\u80a4 | [\u4f24\u5bb3\u76ae\u80a4-\u5b8c\u6574\u5b9e\u73b0\u6307\u5357.md](./\u4f24\u5bb3\u76ae\u80a4-\u5b8c\u6574\u5b9e\u73b0\u6307\u5357.md) |
| \u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1 (DPT) | [\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1-DPT-\u5b9e\u73b0\u6307\u5357.md](./\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1-DPT-\u5b9e\u73b0\u6307\u5357.md) |
| \u4e16\u754c\u5730\u56fe MapInfo | [\u4e16\u754c\u5730\u56feMapInfo-\u5b9e\u73b0\u6307\u5357.md](./\u4e16\u754c\u5730\u56feMapInfo-\u5b9e\u73b0\u6307\u5357.md) |
| Kaentake \u517c\u5bb9\u5c42 | [kaentake\u517c\u5bb9\u5c42\u8bf4\u660e.md](./kaentake\u517c\u5bb9\u5c42\u8bf4\u660e.md) |

## \u5de5\u7a0b\u8def\u5f84

| \u89d2\u8272 | \u8def\u5f84 |
|------|------|
| \u670d\u52a1\u7aef | `E:\\\\pro\\\\BeiDou-Server_xy\\\\gms-server` |
| \u63d2\u4ef6 | `E:\\\\pro\\\\BeiDou-ijl15\\\\ezorsia` |
| \u5ba2\u6237\u7aef Data | `E:\\\\mxd_soft\\\\2.\u5ba2\u6237\u7aef\\\\083\\\\beidou_client_xiaoye\\\\BeiDou-Client_1\\\\Data` |
| WZ \u53c2\u8003\u5305 | `E:\\\\\u8d44\u6599\\\\xiaoye\\\\mxd\u5b66\u4e60\\\\\u4f24\u5bb3\u76ae\u80a4\\\\WZ needed.zip` |
| 079 DamageSkin | `E:\\\\mxd_soft\\\\2.\u5ba2\u6237\u7aef\\\\083\\\\20\u5927\u9646_079\u6574\u5408\u7248\\\\20dalu\\\\wz\\\\Effect.wz\\\\DamageSkin.img.xml` |
| DPT Tengutake | `E:\\\\\u8d44\u6599\\\\xiaoye\\\\mxd\u5b66\u4e60\\\\\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1\\\\\u7edf\u8ba11\\\\Tengutake\\\\` |
| UI_custom2.wz | `E:\\\\\u8d44\u6599\\\\xiaoye\\\\mxd\u5b66\u4e60\\\\DamageStatistic\\\\` \uff08\u89c1 README\uff09 |

## Case C \u90e8\u7f72\u5b57\u8282\u6570

| \u6587\u4ef6 | \u5b57\u8282\u6570 |
|------|--------|
| ijl15.dll | 523776 |
| BasicEff.img | 3992551 |
| DamageSkin.img | 70058716 |
| UIWindow.img | 12160040 |
| Cash.img | 287095 |
| 0591.img | 1193 |

```mermaid
flowchart LR
    DS[DamageSkin.img 70MB] --> Client
    BE[BasicEff.img 4MB \u539f\u7248] --> Client
    UI[UIWindow + DamageSkin UI] --> Client
    DLL[ijl15.dll lazy] --> Client
```

## \u5371\u9669\u7ec4\u5408

- 78MB BasicEff + 70MB DamageSkin \u540c\u65f6\u5b58\u5728 \u2192 DEAD
- Debug dll 1.35MB \u2192 E_FAIL
- \u6839\u76ee\u5f55 WZ \u8865\u4e01 \u2192 \u65e0\u6548/\u51b2\u7a81
"""

README = """# \u8096\u9097\u5192\u9669\u5c9b \u2014 GMS083 \u81ea\u5b9a\u4e49\u529f\u80fd\u6587\u6863

\u5165\u53e3\uff1a[\u5317\u6597GMS083\u81ea\u5b9a\u4e49\u529f\u80fd\u603b\u89c8.md](./\u5317\u6597GMS083\u81ea\u5b9a\u4e49\u529f\u80fd\u603b\u89c8.md)

## \u6587\u6863\u5217\u8868

- [\u4f24\u5bb3\u76ae\u80a4-\u5b8c\u6574\u5b9e\u73b0\u6307\u5357.md](./\u4f24\u5bb3\u76ae\u80a4-\u5b8c\u6574\u5b9e\u73b0\u6307\u5357.md)
- [\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1-DPT-\u5b9e\u73b0\u6307\u5357.md](./\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1-DPT-\u5b9e\u73b0\u6307\u5357.md)
- [\u4e16\u754c\u5730\u56feMapInfo-\u5b9e\u73b0\u6307\u5357.md](./\u4e16\u754c\u5730\u56feMapInfo-\u5b9e\u73b0\u6307\u5357.md)
- [kaentake\u517c\u5bb9\u5c42\u8bf4\u660e.md](./kaentake\u517c\u5bb9\u5c42\u8bf4\u660e.md)

## \u5b50\u76ee\u5f55

- [\u4e16\u754c\u5730\u56feMapInfo/](./\u4e16\u754c\u5730\u56feMapInfo/) \u2014 \u53c2\u8003\u6e90\u7801
- [\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1/](./\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1/) \u2014 \u65e9\u671f\u6587\u6863
"""


def read_doc(name):
    p = DOCS / name
    if p.exists():
        return p.read_text(encoding="utf-8")
    return ""


def build_damage_skin():
    src = read_doc("damage-skin-adaptation-zh.md")
    extra = """
## \u8865\u5145\uff1aCase C \u6b63\u786e\u4fee\u6539\u903b\u8f91\uff082026-07-09 \u4f1a\u8bdd\u9a8c\u8bc1\uff09

### \u6838\u5fc3\u539f\u5219

1. **\u5ba2\u6237\u7aef**\uff1a\u72ec\u7acb\u90e8\u7f72 `Data/Effect/DamageSkin.img` (~70MB)\uff0c`BasicEff.img` \u4fdd\u6301\u539f\u7248 (~4MB)
2. **\u670d\u52a1\u7aef**\uff1a\u5408\u5e76 `damageSkin` \u5230 `gms-server/wz/Effect.wz/BasicEff.img.xml`\uff08\u4ec5 catalog \u626b\u63cf\uff09
3. **\u63d2\u4ef6**\uff1a`ResolveDamageSkinRootPath()` \u4f18\u5148\u8bfb `Effect/DamageSkin.img/<id>`
4. **\u521d\u59cb\u5316**\uff1aLazyCompatInit + DamageRankStage \u9996\u6b21\u8fdb\u56fe\u624d `EnsureHooks()`

```mermaid
flowchart TD
    A[\u5173\u5ba2\u6237\u7aef] --> B[\u90e8\u7f72 DamageSkin.img \u6574\u6587\u4ef6]
    B --> C[BasicEff.img \u4e0d\u52a8]
    C --> D[UIWindow APPEND DamageSkin \u8282\u70b9]
    D --> E[String 5910000 + Item 0591]
    E --> F[\u670d\u52a1\u7aef\u5408\u5e76 BasicEff.xml]
    F --> G[Release ijl15.dll 523776B]
    G --> H[\u8fdb\u56fe\u9a8c\u8bc1]
```

### WzImg MCP \u5408\u5e76\u8981\u70b9

- \u5bf9 **IMG \u8282\u70b9** \u64cd\u4f5c\uff08`copy_nodes` / `paste_nodes` / `save_node`\uff09\uff0c\u975e\u6839\u76ee\u5f55 WZ \u8865\u4e01
- `UIWindow.img`\uff1a\u7b56\u7565 **APPEND** `DamageSkin/*`
- `DamageSkin.img`\uff1a\u6574\u6587\u4ef6\u590d\u5236\uff08\u6765\u81ea 079 \u6216 bisect \u5907\u4efd\uff09
- **\u52ff**\u5c06 damageSkin \u5408\u5e76\u8fdb\u5ba2\u6237\u7aef BasicEff.img\uff08Case C \u7981\u6b62\uff09

### wz-zh-CN String \u4fee\u590d

\u540c\u6b65 `gms-server/wz-zh-CN/String.wz/Cash.img.xml` \u7684 `5910000` \u4e2d\u6587\u540d\u79f0\u8282\u70b9\u3002

"""
    # strip duplicate Case C section from source if present and prepend header
    body = src
    marker = "### Case C \u5b89\u5168\u90e8\u7f72\u6e05\u5355"
    if marker in body:
        body = body.split(marker)[0].rstrip() + "\n\n---\n\n" + body.split(marker)[1]
        body = HEADER_DS + extra + "\n---\n\n## \u6765\u81ea\u670d\u52a1\u7aef\u4ed3\u5e93\u7684\u5b8c\u6574\u8bf4\u660e\n\n" + body.split("---", 1)[-1] if "---" in body else HEADER_DS + extra + body
    else:
        body = HEADER_DS + extra + body
    if CASE_C_TABLE not in body:
        body += "\n" + CASE_C_TABLE
    return body


def build_dpt():
    src = read_doc("\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1-DPT.md")
    existing = BASE / "\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1" / "\u5b8c\u6574\u5b9e\u73b0\u6587\u6863.md"
    body = existing.read_text(encoding="utf-8") if existing.exists() else src
    header = """# \u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1 (DPT) \u2014 \u5b9e\u73b0\u6307\u5357

> Tengutake \u53c2\u8003 + BeiDou \u9002\u914d

"""
    tengutake = """
## Tengutake WZ \u5408\u5e76\u8865\u5145

| \u8d44\u6e90 | \u8def\u5f84 |
|------|------|
| Tengutake \u6e90\u7801 | `E:\\\\\u8d44\u6599\\\\xiaoye\\\\mxd\u5b66\u4e60\\\\\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1\\\\\u7edf\u8ba11\\\\Tengutake\\\\` |
| UI_custom2.wz | `E:\\\\\u8d44\u6599\\\\xiaoye\\\\mxd\u5b66\u4e60\\\\DamageStatistic\\\\` |
| \u5408\u5e76\u76ee\u6807 | `Data/UI/UIWindow.img` \u2192 `DamageRank/*` |

\u4e0e\u4f24\u5bb3\u76ae\u80a4\u5171\u5b58\uff1aCase C \u7684 UIWindow.img (12160040B) \u5e94\u540c\u65f6\u542b `DamageRank` \u4e0e `DamageSkin`\u3002

```mermaid
flowchart LR
    T[UI_custom2.wz] -->|APPEND DamageRank| UI[UIWindow.img]
    DS[UI.wz DamageSkin] -->|APPEND| UI
```

"""
    if "Tengutake WZ" not in body:
        # insert after WZ section
        if "## 3. WZ" in body:
            parts = body.split("## 3. WZ", 1)
            body = parts[0] + tengutake + "## 3. WZ" + parts[1]
        else:
            body = header + tengutake + body
    return header + body if not body.startswith("# ") else body


def build_worldmap():
    existing = BASE / "\u4e16\u754c\u5730\u56feMapInfo" / "\u5b8c\u6574\u5b9e\u73b0\u6587\u6863.md"
    body = existing.read_text(encoding="utf-8") if existing.exists() else ""
    header = """# \u4e16\u754c\u5730\u56fe MapInfo \u2014 \u5b9e\u73b0\u6307\u5357

> \u6839\u76ee\u5f55\u7248\uff1b\u5b50\u76ee\u5f55\u53c2\u8003\u6e90\u7801\u4ecd\u5728 [\u4e16\u754c\u5730\u56feMapInfo/](./\u4e16\u754c\u5730\u56feMapInfo/)

"""
    add = """
## \u63d2\u4ef6\u6587\u4ef6\uff08E:\\\\pro\\\\BeiDou-ijl15\\\\ezorsia\\\\worldmap\\\\\uff09

| \u6587\u4ef6 | \u804c\u8d23 |
|------|------|
| worldmapinfo.cpp | Hook + \u6e32\u67d3 |
| getwzinfo.cpp | WZ \u8def\u5f84 |
| iconprovider.cpp | \u56fe\u6807 |
| WorldMapInfoBridge.cpp | 0x115/0x178\uff0c0x178 \u541e\u5305 |

\u670d\u52a1\u7aef\uff1a`WorldMapPlayersHandler.java` \u2014 \u89c1 `./\u4e16\u754c\u5730\u56feMapInfo/server/`

"""
    if not body:
        body = header + add + read_doc("kaentake-compat-layer.md")
    else:
        body = header + add + body
    return body


def build_compat():
    src = read_doc("kaentake-compat-layer.md")
    header = """# Kaentake \u517c\u5bb9\u5c42\u8bf4\u660e

> \u5de5\u7a0b\uff1a`E:\\\\pro\\\\BeiDou-ijl15\\\\ezorsia\\\\compat\\\\`

"""
    extra = """
## LazyCompatInit\uff082026-07-09 \u65b0\u589e\uff09

```mermaid
flowchart TD
    DllMain --> Lazy[LazyCompatInit::InstallBootstrapHook]
    Lazy --> CField[Hook CField @ 0x528DBC]
    CField --> Init[ModRegistry::Initialize]
    Init --> PD[PacketDispatcher::InstallHook]
    CField --> EH[DamageSkin::EnsureHooks]
    Stage[set_stage CField] --> EH
```

- `DamageSkin::AttachHooks()` \u5728 ModRegistry \u4e2d\u4e3a\u7a7a\uff0c\u5b9e\u9645 hook \u5728 `EnsureHooks()`
- `DamageRankStage` \u8fdb CField \u65f6\u8c03\u7528 `EnsureHooks()`\uff0c\u907f\u514d\u91cd\u590d detour set_stage

## PacketDispatcher \u4fee\u6b63\uff08opcode skip fix\uff09

| Opcode | \u5904\u7406 |
|--------|------|
| 0x0178 | \u5fc5\u987b\u541e\u5305 |
| 0x0170-0x0173 | \u4fa7\u6548\u540e return 0\uff08\u5df2\u6ce8\u518c handler\uff09 |
| 0x3714 | DPT \u4fa7\u6548 |
| \u5176\u4f59 | \u8f6c\u53d1\u539f\u751f ProcessPacket |

\u6587\u4ef6\uff1a`compat/PacketDispatcher.cpp`\u3001`damageskin/DamageSkinBridge.cpp`

"""
    return header + extra + "\n---\n\n" + src


def main():
    BASE.mkdir(parents=True, exist_ok=True)
    mapping = {
        "\u5317\u6597GMS083\u81ea\u5b9a\u4e49\u529f\u80fd\u603b\u89c8.md": OVERVIEW,
        "README.md": README,
        "\u4f24\u5bb3\u76ae\u80a4-\u5b8c\u6574\u5b9e\u73b0\u6307\u5357.md": build_damage_skin(),
        "\u4e2a\u4eba\u4f24\u5bb3\u7edf\u8ba1-DPT-\u5b9e\u73b0\u6307\u5357.md": build_dpt(),
        "\u4e16\u754c\u5730\u56feMapInfo-\u5b9e\u73b0\u6307\u5357.md": build_worldmap(),
        "kaentake\u517c\u5bb9\u5c42\u8bf4\u660e.md": build_compat(),
    }
    for name, content in mapping.items():
        path = BASE / name
        path.write_text(content, encoding="utf-8")
        print(f"Wrote {name}: {path.stat().st_size} bytes")

if __name__ == "__main__":
    main()
