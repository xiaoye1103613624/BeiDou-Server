# 调研与实现笔记

## 关键结论（2026-09-15）

- Drop 管理是 **DB CRUD + clearDrops**，不写 WZ；Quest 只能复用其 REST/UI/i18n 形态。
- Quest 定义在 `Quest.wz`（Act / Check / QuestInfo / Say）；进度在 `queststatus` / `questprogress`。
- `Quest.clearCache` **不够**：须 `Quest.reloadFromWz()` 重载 static Data。
- 本仓无 `.claude/skills/wz-patch-java/`；客户端同步首版以降级导出为主，探测 patcher 路径若存在则调用。
- Flyway：`V1.11.54`（索引+菜单）、`V1.11.55`（客户端写开关/EN/patcher 配置）。

## 已拍板决策（实现口径）

| 项 | 值 |
| --- | --- |
| 主源 | A 纯 WZ |
| 客户端同步 | 同交付；默认 dry-run；实写需 `allow_quest_client_write` |
| 未做 | UI 可切 mode；never≈status=0（真·无行反查预留） |
| 删除 | 不级联 queststatus |
| 链路 | 表格上下游 |
| 脚本 | Monaco；文件 `{id}.js` |

## WZ 节点语义（简）

| 文件 | 用途 |
| --- | --- |
| Check.img | `0` 接取条件 / `1` 完成条件 |
| Act.img | `0`/`1` 动作：奖励、nextQuest… |
| QuestInfo.img | name、parent、文本、area、auto* |
| Say.img | 对话树；首期只读+警告 |

## 阻塞与降级

| 阻塞 | 降级 |
| --- | --- |
| 无 xml-img-patcher.exe | syncClient 导出到 `docs/features/quest-management/patches/`；实写时复制到客户端旁 `quest-management-patches/` |
| 未配置 ClientPath | dry-run 可；实写拒绝 |
| 生产 | 保持 `allow_quest_client_write=false` |

## 占用策略（客户端文件锁）

首遇占用询问用户 A 自行 / B AI 杀进程 / C 中止；会话内记住。本功能 notes 记录偏好位：尚未设定。

## 编译验证

- 本机 JDK：`C:\Users\11036\.jdks\ms-21.0.12.1`
- `mvn -DskipTests compile`（gms-server）已通过（2026-09-15）
