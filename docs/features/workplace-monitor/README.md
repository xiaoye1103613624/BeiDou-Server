# 工作台服务器/MySQL 监控图表

> 状态：已闭环  
> 适用客户端/协议版本：无关（仅管理后台 + Spring REST）  
> 项目名称：BeiDou-Server_s9  
> 作者/日期：2026-09-15  
> 相关提交：（见本功能原子 commit，提交后回填）  
> 产物目录：docs/features/workplace-monitor/

## 1. 背景与目标

- 工作台原先仅有游戏服启停/热重载，缺少 JVM、系统、在线人数与 MySQL 可视化。
- 目标：同页增加服务器信息 ECharts 图表，以及独立的 MySQL/Druid 信息区；支持手动与可选自动刷新；趋势仅前端采样缓存，不落库。

明确不做：

- 不启用 Spring Actuator / Druid StatView
- 不新增菜单路由
- 不改游戏玩法、存档、协议、WZ/插件

## 2. 影响范围

| 层级 | 路径/模块 | 变更类型 |
| --- | --- | --- |
| 服务端 | `MonitorController` / `MonitorService` / DTO / i18n | 新增只读监控 API |
| 管理端/前端 | `workplace` 页、`api/monitor.ts`、监控面板组件 | 新增图表与刷新 |
| 脚本 / WZ / 插件 / DB 迁移 | — | 无 |

## 3. 最终实现逻辑

### 3.1 主流程

1. 登录后台进入 `/dashboard/workplace`
2. `onMounted` 拉取游戏服 online 状态，并并行请求：
   - `GET /monitor/v1/serverInfo`
   - `GET /monitor/v1/mysqlInfo`
3. 服务器区展示 JVM/OS/GC/大区在线/频道占用图表；摘要含系统物理内存（已用/总量）
4. MySQL 区展示版本、连接、库大小、Druid 池，以及前端 ring buffer 趋势折线
5. 可选自动刷新 5/10/30s；离开页面清理 timer；刷新失败 `Message.error` 提示

### 3.2 数据语义

**serverInfo**

- JVM：堆/非堆 used/max、线程、uptime、各 GC 次数与耗时
- OS：名称、架构、逻辑核、进程/系统 CPU（0~1，不可用为 -1）、物理内存
- Game：online、版本、各 world/channel 在线人数（服未启动返回空列表）

**mysqlInfo**

- `SHOW GLOBAL STATUS/VARIABLES`：Uptime、Threads_connected、Threads_running、Questions、Slow_queries、max_connections
- `information_schema.TABLES` 汇总当前库大小
- Druid：active/pooling/wait/maxActive/connect*；非 Druid 则 `pool.available=false`

### 3.3 模块与扩展点

- 后端入口：`org.gms.controller.MonitorController`
- 采集：`org.gms.service.MonitorService`
- 前端 API：`gms-ui/src/api/monitor.ts`
- 轮询：`workplace/hooks/useMonitorRefresh.ts`
- 面板：`ServerMonitorPanel.vue` / `MysqlMonitorPanel.vue`

## 4. 资源与同步

- 无 WZ/IMG/插件/客户端资源变更

## 5. 编码与 i18n

- 后端日志/异常：`log_*.properties`、`exception_*.properties` 的 `MonitorService.*`
- 前端：`workplace/locale/zh-CN.ts`、`en-US.ts` 的 `workplace.monitor.*`
- Web/源码 UTF-8；无游戏封包改动

## 6. 产物清单

| 类型 | 路径 | 说明 |
| --- | --- | --- |
| 文档 | README.md | 本文件 |

## 7. 验证记录

- 后端：`mvn -pl gms-server -DskipTests compile`（JDK 21）——2026-09-15 **BUILD SUCCESS**
- 前端：`cd gms-ui && yarn type:check`——2026-09-15 **通过**（vue-tsc --noEmit --skipLibCheck）
- 手工建议：登录工作台查看两块监控区与系统内存摘要；故意断后端观察失败提示；切换自动刷新观察趋势点增长；停服后在线柱图为空（本机未强制要求跑通手工项）

## 8. 预留、风险与回滚

- 风险：`DataSource` 若被包装导致非 `DruidDataSource`，池指标会降级为空（有 WARN）
- 回滚：删除 `/monitor` 接口与前端监控组件/接入即可，无 DB 迁移

## 9. 对外交流摘要

工作台新增只读监控：JVM/OS/GC/在线与独立 MySQL+Druid 面板，ECharts 展示，前端采样趋势，JWT 鉴权下的 `/monitor/v1/*`。
