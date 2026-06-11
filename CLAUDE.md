# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BeiDou (北斗) is a MapleStory game server emulator forked from Cosmic, built on Java 21 Spring Boot 3.2 with a Vue 3 admin panel. It emulates the MapleStory MMORPG server, handling client connections via Netty, NPC/quest scripts via GraalJS, and game data via WZ file parsing.

Root POM (`pom.xml`) defines the Maven module: `gms-server`.

## Build & Run

### Backend (gms-server)

| Command | Description |
|---------|-------------|
| `mvn clean package -pl gms-server` | Build fat jar (`gms-server/target/BeiDou.jar`) |
| `java -jar gms-server/target/BeiDou.jar` | Run server (default port 8686) |
| `mvn test -pl gms-server` | Run all unit tests (JUnit 5 + Mockito) |
| `mvn test -pl gms-server -Dtest=SomeTest` | Run a single test class |

The jar must be run from the `gms-server/` directory (it reads `scripts-zh-CN/`, `wz-zh-CN/` relative paths). Database auto-creates via Flyway; just ensure MySQL 8+ is running with credentials matching `application.yml`.

### Frontend (gms-ui)

```shell
cd gms-ui
yarn install        # first time only
yarn dev            # development server (Vite)
yarn build          # production build
```

### Convenience Scripts

- `gms-server/launch.bat` — Windows launcher (requires `jdk-21.0.11+10-jre/` alongside jar)
- `gms-server/launch.sh` — Linux/macOS launcher

## Architecture

### Server Application Entry

`gms-server/src/main/java/org/gms/ServerApplication.java` — Spring Boot entry point. Before Spring starts, it manually parses `application.yml` to auto-create the MySQL database if missing. This avoids Flyway connection failures on first run.

### Dual Network Layer

The server runs **two distinct listening servers** on different ports:

1. **LoginServer** (`org.gms.net.netty.LoginServer`) — Handles client login, character list, character creation, PIN/PIC registration. Packets dispatched via `PacketProcessor` using opcode-to-handler mappings.
2. **ChannelServer** (`org.gms.net.netty.ChannelServer`) — Handles in-game gameplay. World/channel architecture: a World contains multiple Channels, each Channel is a separate Netty server instance.

Both are initialized in `ServerManager.run()` via `Server.init()`.

### Packet Handling Pipeline

`org.gms.net.PacketProcessor` — Central packet dispatcher. Maintains a `Map<RecvOpcode, PacketHandler>` for each server type (Login vs Channel). Channel handlers live in `org.gms.net.server.channel.handlers.*`, login handlers in `org.gms.net.server.handlers.login.*`.

- `net.encryption` — Client packet encryption/decryption
- `net.opcodes` — Opcode definitions (sent vs received)
- `net.packet` — `PacketWriter` utilities for building outgoing packets
- `net.server.channel` — Channel-specific services (guild, buff storage, world transfers)

### Scripting Engine

Game logic (NPCs, quests, portals, reactors, events, items) runs via **GraalJS** (GraalVM JavaScript engine):

- `org.gms.scripting.AbstractScriptManager` — Base class. Loads `.js` files from `scripts-{lang}/` (language-specific) or falls back to `scripts/`. Uses JSR-223 ScriptEngine API.
- `AbstractPlayerInteraction` — The bridge between JavaScript scripts and Java game world. Exposes methods scripts use: `cm.sendNext()`, `cm.openShop()`, `cm.gainItem()`, `cm.warp()`, etc.
- Script directories: `scripts-zh-CN/{npc,portal,quest,reactor,event,item,map}/`
- Language is configured via `gms.service.language` in `application.yml` (`zh-CN` or `en-US`)

### Data Layer

**WZ Files** (packaged game data — items, maps, skills, etc.):
- `org.gms.provider` — Generic WZ data reading interfaces (`Data`, `DataProvider`, `DataEntity`, etc.)
- `org.gms.provider.wz` — MapleStory-specific WZ format parser
- Key data providers: `ItemInformationProvider`, `SkillbookInformationProvider`

**Database** (MySQL 8+, via MyBatis-Flex):
- `org.gms.dao.entity` — Entity classes (generated from DB schema)
- `org.gms.dao.mapper` — MyBatis-Flex mapper interfaces (scan: `@MapperScan("org.gms.dao.mapper")`)
- Flyway migrations in `gms-server/src/main/resources/db/migration/` (versioned: V1.0.0 through V1.8.28+)
- Database connection pool: Alibaba Druid

**Flyway 迁移文件规范（数据库注释强制要求）**：
- **所有新建表必须包含表级 COMMENT 和字段级 COMMENT**，使用中文注释，简洁明了
- 表注释格式：`) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表的中文描述';`
- 字段注释格式：`column_name TYPE ... COMMENT '字段的中文描述'`
- **所有新增的 ALTER TABLE 语句也需对新增字段添加 COMMENT**
- 示例参考 `V1.8.23__create_equip_enhance_config.sql` 和 `V1.8.28__xy_level_reward.sql`
- 注释要说明字段的**业务含义**，而非重复字段名；枚举字段需说明各值含义（如 `0=禁用 1=启用`）

### REST API (Web Admin)

`org.gms.controller.*` — Spring MVC REST controllers (port 8686). JWT authentication via Spring Security:
- `AuthEntryPointJwt` / `AuthTokenFilter` — JWT filter chain
- Swagger UI available at `http://localhost:8686/swagger-ui/index.html` when enabled in config
- API versioning: `ApiConstant.LATEST` → current version; incompatible changes get pinned to explicit version constants

### Key Packages Reference

| Package | Purpose |
|---------|---------|
| `org.gms.client` | In-memory player state: `Character`, `Client`, skills, inventory, quests, buddies, family |
| `org.gms.server` | Game server logic: shops, trade, storage, cash shop, maps, life (NPCs/mobs), events, expeditions, quest engine |
| `org.gms.server.maps` | Map system: `MapleMap`, portals, footholds, reactors, spawns, mist effects |
| `org.gms.service` | Spring service layer — one service per domain (shop, inventory, character, etc.) |
| `org.gms.model.dto/pojo` | DTOs (API requests/responses) and POJOs (inner domain objects) |
| `org.gms.config` | Spring `@Configuration` classes and game config managers (equip enhance/advance, level rewards, etc.) |
| `org.gms.aop` | JWT auth filter chain and server-level request filter |
| `org.gms.constants` | Enums: job IDs, inventory types, skill IDs, server constants, opcodes |
| `org.gms.util` | Utilities: JWT, BCrypt, packet creation, i18n, randomization, rate limiting |
| `org.gms.property` | `ServiceProperty` — typed binding for `gms.service.*` config block |
| `org.gms.manager` | `ServerManager` — lifecycle bean (init game servers on start, shutdown on destroy) |

### Frontend (gms-ui)

- Vue 3 + TypeScript + Vite
- UI library: Arco Design Vue (`@arco-design/web-vue`)
- State: Pinia (`src/store/`)
- Routing: Vue Router (`src/router/`)
- i18n: vue-i18n (`src/locale/`)
- Charts: ECharts via vue-echarts
- API layer: Axios (`src/api/`)
- Code editor: Monaco Editor (for script editing in admin panel)

## Configuration

`gms-server/src/main/resources/application.yml`:
- `server.port: 8686` — REST API port
- `mybatis-flex.datasource.mysql.*` — DB connection (default: `beidou_dev_v1`, user `root`/`root`)
- `gms.service.language: zh-CN` — script/WZ directory suffix
- `gms.service.login-port: 8484` — game client login port
- `gms.service.rate-limit.*` — per-IP rate limiting for API
- `gms.service.wan-host/lan-host/localhost` — network interface binding
- `springdoc.api-docs.enabled` / `springdoc.swagger-ui.enabled` — Swagger toggle (disable in production)

## Internationalization (i18n)

Scripts and WZ data are language-versioned via directory naming:
- `scripts-zh-CN/` and `scripts-en-US/` (or plain `scripts/` as fallback)
- `wz-zh-CN/` and `wz-en-US/`
- `AbstractScriptManager.getInvocableScriptEngine()` resolves the correct path at runtime
- `I18nUtil` handles runtime message localization
