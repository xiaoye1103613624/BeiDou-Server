package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.QuestStatus;
import org.gms.constants.id.NpcId;
import org.gms.dao.entity.CharactersDO;
import org.gms.dao.entity.QueststatusDO;
import org.gms.dao.mapper.CharactersMapper;
import org.gms.dao.mapper.QueststatusMapper;
import org.gms.exception.BizException;
import org.gms.manager.ServerManager;
import org.gms.model.dto.QuestChainEdgeDTO;
import org.gms.model.dto.QuestChainNodeDTO;
import org.gms.model.dto.QuestChainRtnDTO;
import org.gms.model.dto.QuestDetailRtnDTO;
import org.gms.model.dto.QuestForceReqDTO;
import org.gms.model.dto.QuestItemNodeDTO;
import org.gms.model.dto.QuestLinkNodeDTO;
import org.gms.model.dto.QuestListRtnDTO;
import org.gms.model.dto.QuestMapMetaDTO;
import org.gms.model.dto.QuestMobNodeDTO;
import org.gms.model.dto.QuestProgressReqDTO;
import org.gms.model.dto.QuestProgressRtnDTO;
import org.gms.model.dto.QuestScriptReqDTO;
import org.gms.model.dto.QuestSearchReqDTO;
import org.gms.model.dto.QuestWriteReqDTO;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.property.ServiceProperty;
import org.gms.server.ItemInformationProvider;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.server.maps.MapFactory;
import org.gms.server.quest.Quest;
import org.gms.service.quest.QuestWzXmlStore;
import org.gms.util.BasePageUtil;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import static org.gms.dao.entity.table.CharactersDOTableDef.CHARACTERS_D_O;
import static org.gms.dao.entity.table.QueststatusDOTableDef.QUESTSTATUS_D_O;

/**
 * 任务管理后台：WZ 定义 CRUD、完成态、强制态、脚本读写、发布热重载。
 */
@Slf4j
@Service
@AllArgsConstructor
public class QuestAdminService {
    private final QuestWzXmlStore wzXmlStore;
    private final QueststatusMapper queststatusMapper;
    private final CharactersMapper charactersMapper;
    private final CommandService commandService;

    /** 链路图节点上限，防止超大连通分量拖垮管理端。 */
    private static final int CHAIN_NODE_LIMIT = 80;

    public Page<QuestListRtnDTO> getQuestList(QuestSearchReqDTO req) {
        ensureQuestsLoaded();
        Document actDoc = wzXmlStore.loadDocument(wzXmlStore.preferredXml(QuestWzXmlStore.FILE_ACT));
        Map<Integer, Integer> nextByQuest = buildNextQuestIndex(actDoc);
        List<QuestListRtnDTO> rows = new ArrayList<>();
        for (Quest quest : Quest.getLoadedQuests()) {
            QuestListRtnDTO row = toListRow(quest, nextByQuest.get((int) quest.getId()));
            if (!matchSearch(row, quest, req)) {
                continue;
            }
            rows.add(row);
        }
        rows.sort((a, b) -> Integer.compare(
                a.getQuestId() == null ? 0 : a.getQuestId(),
                b.getQuestId() == null ? 0 : b.getQuestId()));
        return BasePageUtil.create(rows, req).page();
    }

    /**
     * 以种子任务为中心，按 nextQuest 与 Check 前置双向 BFS 展开完整连通链路。
     */
    public QuestChainRtnDTO getQuestChain(Integer seedQuestId) {
        RequireUtil.requireNotNull(seedQuestId, I18nUtil.getExceptionMessage("QuestAdminService.questId.required"));
        ensureQuestsLoaded();
        Quest seed = Quest.getInstance(seedQuestId);
        RequireUtil.requireNotNull(seed, I18nUtil.getExceptionMessage("QuestAdminService.quest.notExist", seedQuestId));

        Document actDoc = wzXmlStore.loadDocument(wzXmlStore.preferredXml(QuestWzXmlStore.FILE_ACT));
        Document checkDoc = wzXmlStore.loadDocument(wzXmlStore.preferredXml(QuestWzXmlStore.FILE_CHECK));
        Map<Integer, Integer> nextByQuest = buildNextQuestIndex(actDoc);
        Map<Integer, List<Integer>> upstreamByQuest = buildUpstreamIndex(checkDoc);
        Map<Integer, Integer> minLevelByQuest = buildMinLevelIndex(checkDoc);
        Map<Integer, List<Integer>> reverseNext = new HashMap<>();
        for (Map.Entry<Integer, Integer> e : nextByQuest.entrySet()) {
            reverseNext.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
        }
        Map<Integer, List<Integer>> dependentsByPrereq = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> e : upstreamByQuest.entrySet()) {
            for (Integer up : e.getValue()) {
                dependentsByPrereq.computeIfAbsent(up, k -> new ArrayList<>()).add(e.getKey());
            }
        }

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(seedQuestId);
        visited.add(seedQuestId);
        boolean truncated = false;
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            List<Integer> neighbors = new ArrayList<>();
            Integer next = nextByQuest.get(cur);
            if (next != null) {
                neighbors.add(next);
            }
            List<Integer> ups = upstreamByQuest.get(cur);
            if (ups != null) {
                neighbors.addAll(ups);
            }
            List<Integer> rev = reverseNext.get(cur);
            if (rev != null) {
                neighbors.addAll(rev);
            }
            List<Integer> deps = dependentsByPrereq.get(cur);
            if (deps != null) {
                neighbors.addAll(deps);
            }
            for (Integer n : neighbors) {
                if (n == null || n <= 0 || visited.contains(n)) {
                    continue;
                }
                if (visited.size() >= CHAIN_NODE_LIMIT) {
                    truncated = true;
                    break;
                }
                visited.add(n);
                queue.add(n);
            }
            if (truncated) {
                break;
            }
        }

        List<QuestChainEdgeDTO> edges = new ArrayList<>();
        Set<String> edgeKeys = new HashSet<>();
        for (Integer qid : visited) {
            Integer next = nextByQuest.get(qid);
            if (next != null && visited.contains(next)) {
                String key = "next:" + qid + "->" + next;
                if (edgeKeys.add(key)) {
                    edges.add(QuestChainEdgeDTO.builder().from(qid).to(next).type("next").build());
                }
            }
            List<Integer> ups = upstreamByQuest.get(qid);
            if (ups != null) {
                for (Integer up : ups) {
                    if (up == null || !visited.contains(up)) {
                        continue;
                    }
                    String key = "prereq:" + up + "->" + qid;
                    if (edgeKeys.add(key)) {
                        edges.add(QuestChainEdgeDTO.builder().from(up).to(qid).type("prereq").build());
                    }
                }
            }
        }

        List<QuestChainNodeDTO> nodes = new ArrayList<>();
        for (Integer qid : visited) {
            Quest q = Quest.getInstance(qid);
            int startNpc = q != null ? q.getNpcRequirement(false) : 0;
            nodes.add(QuestChainNodeDTO.builder()
                    .questId(qid)
                    .name(q != null ? q.getName() : null)
                    .parentName(q != null ? q.getParentName() : null)
                    .startNpcId(startNpc > 0 ? startNpc : null)
                    .startNpcName(startNpc > 0 ? npcName(startNpc) : null)
                    .minLevel(minLevelByQuest.get(qid))
                    .current(Objects.equals(qid, seedQuestId))
                    .build());
        }
        nodes.sort((a, b) -> Integer.compare(
                a.getQuestId() == null ? 0 : a.getQuestId(),
                b.getQuestId() == null ? 0 : b.getQuestId()));

        List<String> warnings = new ArrayList<>();
        if (truncated) {
            warnings.add(I18nUtil.getMessage("QuestAdminService.warning.chainTruncated", CHAIN_NODE_LIMIT));
        }
        return QuestChainRtnDTO.builder()
                .seedQuestId(seedQuestId)
                .nodes(nodes)
                .edges(edges)
                .warnings(warnings)
                .build();
    }

    public QuestDetailRtnDTO getQuestDetail(Integer questId) {
        RequireUtil.requireNotNull(questId, I18nUtil.getExceptionMessage("QuestAdminService.questId.required"));
        ensureQuestsLoaded();
        Quest quest = Quest.getInstance(questId);
        RequireUtil.requireNotNull(quest, I18nUtil.getExceptionMessage("QuestAdminService.quest.notExist", questId));

        Document infoDoc = wzXmlStore.loadDocument(wzXmlStore.preferredXml(QuestWzXmlStore.FILE_INFO));
        Document checkDoc = wzXmlStore.loadDocument(wzXmlStore.preferredXml(QuestWzXmlStore.FILE_CHECK));
        Document actDoc = wzXmlStore.loadDocument(wzXmlStore.preferredXml(QuestWzXmlStore.FILE_ACT));
        Element infoEl = wzXmlStore.findQuestElement(infoDoc, questId);
        Element checkEl = wzXmlStore.findQuestElement(checkDoc, questId);
        Element actEl = wzXmlStore.findQuestElement(actDoc, questId);

        Element startCheck = wzXmlStore.childByName(checkEl, "0");
        Element endCheck = wzXmlStore.childByName(checkEl, "1");
        Element startAct = wzXmlStore.childByName(actEl, "0");
        Element endAct = wzXmlStore.childByName(actEl, "1");

        Integer startNpcId = wzXmlStore.getIntAttr(startCheck, "npc");
        Integer endNpcId = wzXmlStore.getIntAttr(endCheck, "npc");
        List<QuestLinkNodeDTO> upstream = wzXmlStore.parseQuestLinks(startCheck);
        for (QuestLinkNodeDTO link : upstream) {
            Quest uq = Quest.getInstance(link.getQuestId());
            if (uq != null) {
                link.setName(uq.getName());
            }
        }

        List<QuestItemNodeDTO> startItems = enrichItems(wzXmlStore.parseItemDir(startCheck, "item"));
        List<QuestItemNodeDTO> endItems = enrichItems(wzXmlStore.parseItemDir(endCheck, "item"));
        List<QuestMobNodeDTO> startMobs = enrichMobs(wzXmlStore.parseMobDir(startCheck));
        List<QuestMobNodeDTO> endMobs = enrichMobs(wzXmlStore.parseMobDir(endCheck));
        List<QuestItemNodeDTO> startRewards = enrichItems(wzXmlStore.parseItemDir(startAct, "item"));
        List<QuestItemNodeDTO> endRewards = enrichItems(wzXmlStore.parseItemDir(endAct, "item"));

        Integer nextQuest = wzXmlStore.getIntAttr(endAct, "nextQuest");
        List<Integer> mapIds = new ArrayList<>();
        mapIds.addAll(wzXmlStore.parseFieldEnter(startCheck));
        mapIds.addAll(wzXmlStore.parseFieldEnter(endCheck));
        List<QuestMapMetaDTO> maps = new ArrayList<>();
        Set<Integer> seenMap = new HashSet<>();
        for (Integer mapId : mapIds) {
            if (mapId == null || !seenMap.add(mapId)) {
                continue;
            }
            maps.add(QuestMapMetaDTO.builder()
                    .mapId(mapId)
                    .mapName(MapFactory.loadPlaceName(mapId))
                    .streetName(MapFactory.loadStreetName(mapId))
                    .build());
        }

        List<String> warnings = new ArrayList<>();
        if (Files.isRegularFile(wzXmlStore.preferredXml(QuestWzXmlStore.FILE_SAY))
                && wzXmlStore.findQuestElement(wzXmlStore.loadDocument(wzXmlStore.preferredXml(QuestWzXmlStore.FILE_SAY)), questId) != null) {
            warnings.add(I18nUtil.getMessage("QuestAdminService.warning.sayNotStructured"));
        }
        if (hasUnstructuredAct(startAct) || hasUnstructuredAct(endAct)) {
            warnings.add(I18nUtil.getMessage("QuestAdminService.warning.actExtraNodes"));
        }

        return QuestDetailRtnDTO.builder()
                .questId(questId)
                .name(quest.getName())
                .parentName(quest.getParentName())
                .text0(wzXmlStore.getStringAttr(infoEl, "0"))
                .text1(wzXmlStore.getStringAttr(infoEl, "1"))
                .text2(wzXmlStore.getStringAttr(infoEl, "2"))
                .area(wzXmlStore.getIntAttr(infoEl, "area"))
                .order(wzXmlStore.getIntAttr(infoEl, "order"))
                .autoStart(Objects.equals(wzXmlStore.getIntAttr(infoEl, "autoStart"), 1))
                .autoPreComplete(Objects.equals(wzXmlStore.getIntAttr(infoEl, "autoPreComplete"), 1))
                .autoComplete(Objects.equals(wzXmlStore.getIntAttr(infoEl, "autoComplete"), 1))
                .startNpcId(startNpcId)
                .startNpcName(npcName(startNpcId))
                .endNpcId(endNpcId)
                .endNpcName(npcName(endNpcId))
                .minLevel(wzXmlStore.getIntAttr(startCheck, "lvmin"))
                .maxLevel(wzXmlStore.getIntAttr(startCheck, "lvmax"))
                .startScript(wzXmlStore.getStringAttr(startCheck, "startscript"))
                .endScript(wzXmlStore.getStringAttr(endCheck, "endscript"))
                .startItems(startItems)
                .endItems(endItems)
                .startMobs(startMobs)
                .endMobs(endMobs)
                .upstreamQuests(upstream)
                .nextQuestId(nextQuest)
                .startRewards(startRewards)
                .endRewards(endRewards)
                .startExp(wzXmlStore.getIntAttr(startAct, "exp"))
                .endExp(wzXmlStore.getIntAttr(endAct, "exp"))
                .startMeso(wzXmlStore.getIntAttr(startAct, "money"))
                .endMeso(wzXmlStore.getIntAttr(endAct, "money"))
                .maps(maps)
                .warnings(warnings)
                .build();
    }

    public Integer addQuest(QuestWriteReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        RequireUtil.requireNotNull(req.getQuestId(), I18nUtil.getExceptionMessage("QuestAdminService.questId.required"));
        if (wzXmlStore.questExistsInWz(req.getQuestId())) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.quest.exists", req.getQuestId()));
        }
        if (RequireUtil.isEmpty(req.getName())) {
            req.setName("Quest " + req.getQuestId());
        }
        wzXmlStore.upsertQuest(req, true);
        log.info(I18nUtil.getLogMessage("QuestAdminService.add.info"), req.getQuestId());
        return req.getQuestId();
    }

    public void updateQuest(QuestWriteReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        RequireUtil.requireNotNull(req.getQuestId(), I18nUtil.getExceptionMessage("QuestAdminService.questId.required"));
        if (!wzXmlStore.questExistsInWz(req.getQuestId())) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.quest.notExist", req.getQuestId()));
        }
        wzXmlStore.upsertQuest(req, false);
        log.info(I18nUtil.getLogMessage("QuestAdminService.update.info"), req.getQuestId());
    }

    /**
     * 删除 WZ 定义；不级联清理 queststatus/questprogress。
     */
    public void deleteQuest(Integer questId) {
        RequireUtil.requireNotNull(questId, I18nUtil.getExceptionMessage("QuestAdminService.questId.required"));
        if (!wzXmlStore.questExistsInWz(questId)) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.quest.notExist", questId));
        }
        wzXmlStore.deleteQuest(questId);
        Quest.clearCache(questId);
        log.info(I18nUtil.getLogMessage("QuestAdminService.delete.info"), questId);
    }

    public Map<String, Object> publish() {
        Quest.reloadFromWz();
        commandService.reloadQuestScriptsByGMCommand();
        Map<String, Object> result = new HashMap<>();
        result.put("questCount", Quest.getLoadedQuests().size());
        result.put("message", I18nUtil.getMessage("QuestAdminService.publish.ok"));
        log.info(I18nUtil.getLogMessage("QuestAdminService.publish.info"), Quest.getLoadedQuests().size());
        return result;
    }

    public Page<QuestProgressRtnDTO> getCompletions(QuestProgressReqDTO req) {
        RequireUtil.requireNotNull(req.getQuestId(), I18nUtil.getExceptionMessage("QuestAdminService.questId.required"));
        QueryWrapper qw = QueryWrapper.create().where(QUESTSTATUS_D_O.QUEST.eq(req.getQuestId()));
        applyStatusMode(qw, req);
        List<QueststatusDO> list = queststatusMapper.selectListByQuery(qw);
        List<QuestProgressRtnDTO> rows = mapProgressRows(list, req.getQuestId());
        if (!RequireUtil.isEmpty(req.getCharacterName())) {
            String needle = req.getCharacterName().toLowerCase();
            rows = rows.stream()
                    .filter(r -> r.getCharacterName() != null && r.getCharacterName().toLowerCase().contains(needle))
                    .toList();
        }
        return BasePageUtil.create(rows, req).page();
    }

    public Page<QuestProgressRtnDTO> getCharacterQuests(QuestProgressReqDTO req) {
        RequireUtil.requireNotNull(req.getCharacterId(), I18nUtil.getExceptionMessage("QuestAdminService.characterId.required"));
        QueryWrapper qw = QueryWrapper.create().where(QUESTSTATUS_D_O.CHARACTERID.eq(req.getCharacterId()));
        applyStatusMode(qw, req);
        List<QueststatusDO> list = queststatusMapper.selectListByQuery(qw);
        List<QuestProgressRtnDTO> rows = mapProgressRows(list, null);
        return BasePageUtil.create(rows, req).page();
    }

    public Map<String, Object> forceStart(QuestForceReqDTO req) {
        return forceOp(req, "start");
    }

    public Map<String, Object> forceComplete(QuestForceReqDTO req) {
        return forceOp(req, "complete");
    }

    public Map<String, Object> reset(QuestForceReqDTO req) {
        return forceOp(req, "reset");
    }

    public Map<String, Object> readScript(QuestScriptReqDTO req) {
        Path path = resolveScriptPath(req, false);
        Map<String, Object> result = new HashMap<>();
        result.put("path", path.toString());
        result.put("exists", Files.isRegularFile(path));
        if (Files.isRegularFile(path)) {
            try {
                result.put("content", Files.readString(path, StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.script.readFail", path.toString()));
            }
        } else {
            result.put("content", "");
        }
        return result;
    }

    public void writeScript(QuestScriptReqDTO req) {
        RequireUtil.requireNotNull(req.getContent(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "content"));
        Path path = resolveScriptPath(req, true);
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, req.getContent(), StandardCharsets.UTF_8);
            log.info(I18nUtil.getLogMessage("QuestAdminService.script.write.info"), path);
        } catch (Exception e) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.script.writeFail", path.toString()));
        }
    }

    private Map<String, Object> forceOp(QuestForceReqDTO req, String op) {
        RequireUtil.requireNotNull(req.getQuestId(), I18nUtil.getExceptionMessage("QuestAdminService.questId.required"));
        RequireUtil.requireNotNull(req.getCharacterId(), I18nUtil.getExceptionMessage("QuestAdminService.characterId.required"));
        ensureQuestsLoaded();
        Quest quest = Quest.getInstance(req.getQuestId());
        RequireUtil.requireNotNull(quest, I18nUtil.getExceptionMessage("QuestAdminService.quest.notExist", req.getQuestId()));

        Character online = findOnlineCharacter(req.getCharacterId());
        Map<String, Object> result = new HashMap<>();
        result.put("questId", req.getQuestId());
        result.put("characterId", req.getCharacterId());
        result.put("online", online != null);

        int npc = req.getNpcId() != null ? req.getNpcId()
                : (quest.getNpcRequirement(false) > 0 ? quest.getNpcRequirement(false) : NpcId.MAPLE_ADMINISTRATOR);

        if (online != null) {
            boolean ok;
            switch (op) {
                case "start" -> ok = quest.forceStart(online, npc);
                case "complete" -> ok = quest.forceComplete(online, npc);
                case "reset" -> {
                    quest.reset(online);
                    ok = true;
                }
                default -> ok = false;
            }
            result.put("success", ok);
            result.put("mode", "online");
            log.info(I18nUtil.getLogMessage("QuestAdminService.force.online.info"), op, req.getQuestId(), req.getCharacterId(), ok);
            return result;
        }

        // 离线：直接改 queststatus
        QueststatusDO existing = queststatusMapper.selectOneByQuery(QueryWrapper.create()
                .where(QUESTSTATUS_D_O.CHARACTERID.eq(req.getCharacterId()))
                .and(QUESTSTATUS_D_O.QUEST.eq(req.getQuestId())));
        int status = switch (op) {
            case "start" -> QuestStatus.Status.STARTED.getId();
            case "complete" -> QuestStatus.Status.COMPLETED.getId();
            case "reset" -> QuestStatus.Status.NOT_STARTED.getId();
            default -> -1;
        };
        if (status < 0) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "op"));
        }
        if ("reset".equals(op)) {
            if (existing != null) {
                queststatusMapper.deleteById(existing.getQueststatusid());
            }
        } else if (existing == null) {
            queststatusMapper.insert(QueststatusDO.builder()
                    .characterid(req.getCharacterId())
                    .quest(req.getQuestId())
                    .status(status)
                    .time((int) (System.currentTimeMillis() / 1000))
                    .expires(0L)
                    .forfeited(0)
                    .completed("complete".equals(op) ? 1 : 0)
                    .info(0)
                    .build());
        } else {
            existing.setStatus(status);
            existing.setTime((int) (System.currentTimeMillis() / 1000));
            if ("complete".equals(op)) {
                existing.setCompleted((existing.getCompleted() == null ? 0 : existing.getCompleted()) + 1);
            }
            queststatusMapper.update(existing);
        }
        result.put("success", true);
        result.put("mode", "offline");
        log.info(I18nUtil.getLogMessage("QuestAdminService.force.offline.info"), op, req.getQuestId(), req.getCharacterId());
        return result;
    }

    /**
     * 与 {@link org.gms.scripting.quest.QuestScriptManager} 对齐：文件名为 {@code {questId}.js}，
     * start/end 由脚本内函数区分，非 q{{id}}s/e 文件名。
     */
    private Path resolveScriptPath(QuestScriptReqDTO req, boolean forWrite) {
        RequireUtil.requireNotNull(req.getQuestId(), I18nUtil.getExceptionMessage("QuestAdminService.questId.required"));
        String fileName = req.getQuestId() + ".js";
        boolean localized = Boolean.TRUE.equals(req.getLocalized());
        String lang = ServerManager.getApplicationContext().getBean(ServiceProperty.class).getLanguage();
        if (forWrite) {
            Path dir = localized
                    ? Path.of("scripts-" + lang, "quest")
                    : Path.of("scripts", "quest");
            return dir.resolve(fileName);
        }
        Path langPath = Path.of("scripts-" + lang, "quest", fileName);
        if (Files.isRegularFile(langPath)) {
            return langPath;
        }
        return Path.of("scripts", "quest", fileName);
    }

    private void applyStatusMode(QueryWrapper qw, QuestProgressReqDTO req) {
        if (req.getStatus() != null) {
            qw.and(QUESTSTATUS_D_O.STATUS.eq(req.getStatus()));
            return;
        }
        String mode = req.getMode();
        if (RequireUtil.isEmpty(mode) || "all".equalsIgnoreCase(mode)) {
            return;
        }
        if ("never".equalsIgnoreCase(mode)) {
            // 从未接取：status=0（NOT_STARTED 行）或无行；有行时滤 0
            qw.and(QUESTSTATUS_D_O.STATUS.eq(QuestStatus.Status.NOT_STARTED.getId()));
        } else if ("incomplete".equalsIgnoreCase(mode)) {
            qw.and(QUESTSTATUS_D_O.STATUS.eq(QuestStatus.Status.STARTED.getId()));
        } else if ("completed".equalsIgnoreCase(mode)) {
            qw.and(QUESTSTATUS_D_O.STATUS.eq(QuestStatus.Status.COMPLETED.getId()));
        }
    }

    private List<QuestProgressRtnDTO> mapProgressRows(List<QueststatusDO> list, Integer fixedQuestId) {
        ensureQuestsLoaded();
        Set<Integer> charIds = new HashSet<>();
        for (QueststatusDO row : list) {
            if (row.getCharacterid() != null) {
                charIds.add(row.getCharacterid());
            }
        }
        Map<Integer, String> nameMap = new HashMap<>();
        if (!charIds.isEmpty()) {
            List<CharactersDO> chars = charactersMapper.selectListByQuery(
                    QueryWrapper.create().where(CHARACTERS_D_O.ID.in(charIds)));
            for (CharactersDO c : chars) {
                nameMap.put(c.getId(), c.getName());
            }
        }
        Set<Integer> onlineIds = onlineCharacterIds();
        List<QuestProgressRtnDTO> rows = new ArrayList<>();
        for (QueststatusDO row : list) {
            int qid = fixedQuestId != null ? fixedQuestId : row.getQuest();
            Quest q = Quest.getInstance(qid);
            rows.add(QuestProgressRtnDTO.builder()
                    .questStatusId(row.getQueststatusid())
                    .characterId(row.getCharacterid())
                    .characterName(nameMap.get(row.getCharacterid()))
                    .questId(qid)
                    .questName(q == null ? null : q.getName())
                    .status(row.getStatus())
                    .statusLabel(statusLabel(row.getStatus()))
                    .time(row.getTime())
                    .forfeited(row.getForfeited())
                    .completed(row.getCompleted())
                    .online(onlineIds.contains(row.getCharacterid()))
                    .build());
        }
        return rows;
    }

    private String statusLabel(Integer status) {
        if (status == null) {
            return "never";
        }
        return switch (status) {
            case 0 -> "never";
            case 1 -> "incomplete";
            case 2 -> "completed";
            default -> String.valueOf(status);
        };
    }

    private Set<Integer> onlineCharacterIds() {
        Set<Integer> ids = new HashSet<>();
        try {
            for (World world : Server.getInstance().getWorlds()) {
                for (Character chr : world.getPlayerStorage().getAllCharacters()) {
                    ids.add(chr.getId());
                }
            }
        } catch (Exception ignored) {
            // 游戏服未启动
        }
        return ids;
    }

    private Character findOnlineCharacter(int characterId) {
        try {
            for (World world : Server.getInstance().getWorlds()) {
                Character chr = world.getPlayerStorage().getCharacterById(characterId);
                if (chr != null) {
                    return chr;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void ensureQuestsLoaded() {
        if (Quest.getLoadedQuests().isEmpty()) {
            Quest.loadAllQuests();
        }
    }

    private QuestListRtnDTO toListRow(Quest quest, Integer nextQuestId) {
        int startNpc = quest.getNpcRequirement(false);
        int endNpc = quest.getNpcRequirement(true);
        return QuestListRtnDTO.builder()
                .questId((int) quest.getId())
                .name(quest.getName())
                .parentName(quest.getParentName())
                .startNpcId(startNpc > 0 ? startNpc : null)
                .startNpcName(startNpc > 0 ? npcName(startNpc) : null)
                .endNpcId(endNpc > 0 ? endNpc : null)
                .endNpcName(endNpc > 0 ? npcName(endNpc) : null)
                .minLevel(null)
                .hasStartScript(quest.hasScriptRequirement(false))
                .hasEndScript(quest.hasScriptRequirement(true))
                .nextQuestId(nextQuestId)
                .build();
    }

    /** 扫描 Act.img：questId → nextQuest。 */
    private Map<Integer, Integer> buildNextQuestIndex(Document actDoc) {
        Map<Integer, Integer> next = new HashMap<>();
        Element root = actDoc.getDocumentElement();
        for (Element questEl : wzXmlStore.namedChildren(root, null)) {
            Integer qid = parseElementQuestId(questEl);
            if (qid == null) {
                continue;
            }
            Element endAct = wzXmlStore.childByName(questEl, "1");
            Integer nq = wzXmlStore.getIntAttr(endAct, "nextQuest");
            if (nq != null && nq > 0) {
                next.put(qid, nq);
            }
        }
        return next;
    }

    /** 扫描 Check.img 接取条件：questId → 前置任务 ID 列表。 */
    private Map<Integer, List<Integer>> buildUpstreamIndex(Document checkDoc) {
        Map<Integer, List<Integer>> upstream = new HashMap<>();
        Element root = checkDoc.getDocumentElement();
        for (Element questEl : wzXmlStore.namedChildren(root, null)) {
            Integer qid = parseElementQuestId(questEl);
            if (qid == null) {
                continue;
            }
            Element startCheck = wzXmlStore.childByName(questEl, "0");
            List<Integer> ids = new ArrayList<>();
            for (QuestLinkNodeDTO link : wzXmlStore.parseQuestLinks(startCheck)) {
                if (link.getQuestId() != null && link.getQuestId() > 0) {
                    ids.add(link.getQuestId());
                }
            }
            if (!ids.isEmpty()) {
                upstream.put(qid, ids);
            }
        }
        return upstream;
    }

    private Map<Integer, Integer> buildMinLevelIndex(Document checkDoc) {
        Map<Integer, Integer> levels = new HashMap<>();
        Element root = checkDoc.getDocumentElement();
        for (Element questEl : wzXmlStore.namedChildren(root, null)) {
            Integer qid = parseElementQuestId(questEl);
            if (qid == null) {
                continue;
            }
            Element startCheck = wzXmlStore.childByName(questEl, "0");
            Integer lv = wzXmlStore.getIntAttr(startCheck, "lvmin");
            if (lv != null) {
                levels.put(qid, lv);
            }
        }
        return levels;
    }

    private Integer parseElementQuestId(Element questEl) {
        if (questEl == null) {
            return null;
        }
        String name = questEl.getAttribute("name");
        if (name == null || name.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(name.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean matchSearch(QuestListRtnDTO row, Quest quest, QuestSearchReqDTO req) {
        if (req.getQuestId() != null && !Objects.equals(req.getQuestId(), row.getQuestId())) {
            return false;
        }
        if (!RequireUtil.isEmpty(req.getName())) {
            String n = req.getName().toLowerCase();
            String name = row.getName() == null ? "" : row.getName().toLowerCase();
            String parent = row.getParentName() == null ? "" : row.getParentName().toLowerCase();
            if (!name.contains(n) && !parent.contains(n)) {
                return false;
            }
        }
        if (req.getNpcId() != null
                && !Objects.equals(req.getNpcId(), row.getStartNpcId())
                && !Objects.equals(req.getNpcId(), row.getEndNpcId())) {
            return false;
        }
        if (Boolean.TRUE.equals(req.getHasScript())
                && !Boolean.TRUE.equals(row.getHasStartScript())
                && !Boolean.TRUE.equals(row.getHasEndScript())) {
            return false;
        }
        if (Boolean.FALSE.equals(req.getHasScript())
                && (Boolean.TRUE.equals(row.getHasStartScript()) || Boolean.TRUE.equals(row.getHasEndScript()))) {
            return false;
        }
        if (req.getItemId() != null) {
            return quest.getStartItemAmountNeeded(req.getItemId()) > 0
                    || quest.getCompleteItemAmountNeeded(req.getItemId()) > 0;
        }
        return true;
    }

    private List<QuestItemNodeDTO> enrichItems(List<QuestItemNodeDTO> items) {
        for (QuestItemNodeDTO item : items) {
            if (item.getId() != null) {
                item.setName(ItemInformationProvider.getInstance().getName(item.getId()));
            }
        }
        return items;
    }

    private List<QuestMobNodeDTO> enrichMobs(List<QuestMobNodeDTO> mobs) {
        for (QuestMobNodeDTO mob : mobs) {
            if (mob.getId() != null) {
                mob.setName(MonsterInformationProvider.getInstance().getMobNameFromId(mob.getId()));
            }
        }
        return mobs;
    }

    private String npcName(Integer npcId) {
        if (npcId == null || npcId <= 0) {
            return null;
        }
        return LifeFactory.getNPCName(npcId);
    }

    private boolean hasUnstructuredAct(Element phase) {
        if (phase == null) {
            return false;
        }
        for (Element child : wzXmlStore.namedChildren(phase, null)) {
            String name = child.getAttribute("name");
            if ("item".equals(name) || "exp".equals(name) || "money".equals(name) || "nextQuest".equals(name)
                    || "fame".equals(name) || "skill".equals(name)) {
                continue;
            }
            // 对话 yes/no 等
            if ("0".equals(name) || "yes".equals(name) || "no".equals(name) || "stop".equals(name)) {
                return true;
            }
        }
        return false;
    }
}
