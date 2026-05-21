# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Identity

BeiDouMS (BeiDou-Server) is a MapleStory GMS v083 private server emulator, forked from Cosmic (which descends from HeavenMS/OdinMS). AGPL-3.0 licensed. The primary language for scripts and admin UI is Chinese (zh-CN).

## Build & Run

```bash
# Server (JDK 21, Maven)
mvn clean package                              # Build → gms-server/target/BeiDou.jar
java -jar gms-server/target/BeiDou.jar         # Run (API on :18686, game login on :8484)

# UI admin panel (Node 20, Yarn)
cd gms-ui && yarn install && yarn dev          # Dev server on :8787
cd gms-ui && yarn build                        # Production build → dist/
```

MySQL 8+ is required. The server auto-creates the database on first run. Flyway runs all migrations automatically. Default credentials: `root/root` on `localhost:13306/beidou`.

Swagger UI: `http://localhost:18686/swagger-ui/index.html`

## Architecture

**Dual-network design:**
- **Game network** — Netty-based TCP server (port 8484 login, then per-channel ports). Implements the MapleStory binary protocol with custom encryption (Shanda + AES). Opcode-based packet dispatching via `PacketProcessor`.
- **REST API** — Spring Boot/Undertow on port 18686. JWT-secured admin API consumed by the Vue frontend. Controllers use versioned paths (`ApiConstant.LATEST`, v1/v2).

**Server lifecycle:** `ServerApplication` → `ServerManager` (Spring `ApplicationRunner`) → `Server.getInstance().init()` starts `LoginServer` + `ChannelServer` instances. On shutdown, `ServerManager.destroy()` triggers graceful shutdown.

**Dynamic configuration:** `GameConfig` loads game parameters (exp/drop rates, server messages) from the database at runtime — no restart needed for tuning.

**Data layers:**
- `provider/` — WZ file data (maps, items, mobs, skills) loaded from XML
- `dao/` — Mybatis-Flex ORM with Druid connection pool
- `server/` — In-memory game state (maps, parties, expeditions, shops)

## Scripting System

All in-game logic (NPCs, quests, events, portals, items) runs as **JavaScript files** executed via the GraalVM JS engine. Scripts live in `gms-server/scripts-zh-CN/`.

**Script routing:** `cm.openNpc(9900001, "ScriptName")` resolves by checking `npc/` directory first, then `BeiDouSpecial/` as fallback (`NPCScriptManager.java:127-130`).

**Two script context objects:**
- NPC/quest/portal scripts use `cm` (NPCConversationManager / QuestActionManager)
- Item scripts use `im` (same underlying class, different variable name)

**Standard NPC script pattern:**
```javascript
var status = -1;
function start() { status = -1; action(1, 0, 0); }
function action(mode, type, selection) {
    if (mode === -1) { cm.dispose(); return; }
    if (mode === 1) status++; else status--;
    // status-based branching with cm.sendSimple/sendOk/sendNext/etc.
}
```

**Persistence from scripts:** `cm.getCharacterExtendValue(key)` / `cm.saveOrUpdateCharacterExtendValue(key, value)` — stores JSON strings in the `extend_value` table (type=21 for character data). No schema changes needed for new features.

**Key constraint — Item scripts:** Only items with IDs in the 243xxxx range trigger scripts (`ItemInformationProvider.java:1596`: `itemId / 10000 != 243`). Item IDs outside this range are silently ignored.

**`im.getNpc()`** returns the item's WZ `spec/npc` value — use this to pass context (e.g., card item → monster ID).

**Direction constants in scripts:** `im.getDirectionInfo()` values:
- `1` = Undefined
- `2` = Equipment (equip slot equip)
- `3` = Consume (consumable item use)
- `4` = Equip (unequip)

## WZ Data

WZ XML files live in `gms-server/wz/` (Mob.wz, Item.wz, Map.wz, Skill.wz, etc.). These are the canonical source for monster IDs, item stats, map metadata, and skill definitions. When writing scripts that reference game data, always verify against WZ XML rather than guessing IDs.

- **Monster IDs:** `gms-server/wz/Mob.wz/<id>.xml` — each file name is the monster ID
- **Item IDs:** `gms-server/wz/Item.wz/` — subdirectories like `Cash/`, `Consume/`, `Etc/`, `Install/`
- **Map IDs:** `gms-server/wz/Map.wz/Map/` — organized by region (Map0–Map9)

## Key Script API Methods

Available on both `cm` and `im` (all from `AbstractPlayerInteraction`):

| Method | Purpose |
|--------|---------|
| `warp(mapId, portal)` | Teleport player to map/portal |
| `gainItem(id, qty)` | Give items to player |
| `gainExp(amount)` | Give experience |
| `getPlayer().gainAp(n, silent)` | Add AP (silent=false shows animation) |
| `getPlayer().gainMeso(n)` | Add mesos |
| `openNpc(npcId, scriptName)` | Delegate to another script |
| `openShopNPC(shopId)` | Open a shop |
| `getCharacterExtendValue(key)` | Read persistent JSON data |
| `saveOrUpdateCharacterExtendValue(key, val)` | Write persistent JSON data |
| `getNpc()` | In item scripts: returns item's WZ spec/npc value |
| `dispose()` | End the conversation |

## Script Conventions — Two Routing Patterns

**1. Status-based (simple NPCs):** Uses a `status` variable incremented on each click. Best for linear, short conversations.

```javascript
var status = -1;
function start() { status = -1; action(1, 0, 0); }
function action(mode, type, selection) { status++; /* branch on status */ }
```

**2. sendSelectLevel (complex menus):** Each menu level routes to a named function `level<Name>(selection)`. The engine auto-dispatches based on the prefix string. Best for multi-page menus with many sub-options.

```javascript
function start() { action(1, 0, 0); }
function action(mode, type, selection) { cm.dispose(); }
// Auto-routed by the engine:
function levelStart() { cm.sendSelectLevel("Menu text with #L0#...#l options"); }
function levelRegion(selection) { /* handles selection from levelStart */ }
```
`sendNextSelectLevel("nextLevelName", text)` chains to another auto-routed level.

## Script Conventions (BeiDouSpecial/)

Custom features go in `gms-server/scripts-zh-CN/BeiDouSpecial/`. These are loaded via `cm.openNpc(9900001, "ScriptName")`. The `9900001.js` NPC serves as the main script hub with separate cases for each feature.

`#i<itemId>#` renders item icons in NPC dialogs. `#r` / `#k` / `#b` are color codes (red/black/blue).

## Package Overview

| Package | Purpose |
|---------|---------|
| `client/` | Player state: `Character`, `Client`, `Skill`, `Job`, inventory, buddies, in-game commands, keybinds |
| `config/` | Spring config: security (JWT), CORS, i18n, dynamic game config |
| `constants/` | Static constants organized by domain (game, net, skills, items, maps) |
| `controller/` | REST API controllers (Spring MVC) — admin panel backend |
| `dao/` | Mybatis-Flex entities and mappers |
| `net/` | Game network: Netty servers, packet codec, opcodes, handlers, encryption |
| `scripting/` | Script engine integration — `AbstractPlayerInteraction` is the script API surface |
| `server/` | Core game systems: maps, monsters, shops, expeditions, events, quests, loot |
| `service/` | Spring service beans (business logic for REST layer) |
| `provider/` | WZ data access layer |

## Important Conventions

- **API versioning:** Controllers use `ApiConstant.LATEST` for the current version. When breaking changes happen, old controllers pin to a specific version (e.g., `ApiConstant.V1`) and new ones use `LATEST`.
- **i18n paths:** WZ data and scripts are language-suffixed: `wz-zh-CN/`, `scripts-zh-CN/`. Config key `gms.language` controls this.
- **Commit style:** Chinese commit messages, format: `类型: 描述` (e.g., `feat: 1.增加注释;2.修改拍卖脚本内容`).
- **PR workflow:** PRs target `master`. Contributors fork and submit PRs. The project uses merge commits (not squash/rebase).
- **Database migrations:** All schema changes go through Flyway versioned SQL files in `db/migration/`. Never modify existing migrations — always add new ones.
- **Maven repository:** Uses Aliyun Maven mirror (`maven.aliyun.com/repository/public`) for faster downloads in China.


### 思考原则

1. **最小改动，已验证**  
   优先采用最简单、最可靠的修改方案，避免引入不必要的复杂逻辑。所有修改应先在小范围内验证通过。

2. **数据可溯源**  
   任何使用的数据（字段、值、配置等）必须明确其来源：  
   - 项目现有代码文件  
   - SQL 脚本中的真实数据  
   - 通过查询接口或资源匹配到的实际数据  
   禁止凭空捏造或猜测数据。

3. **代码规范 + 可维护性**  
   - 遵循《阿里巴巴Java开发手册》（或其他项目指定规范）  
   - 保持代码简洁，避免过度设计  
   - 为关键逻辑、复杂处理、非常规写法添加清晰注释  
   - 保证后续开发者能快速理解和二次修改
