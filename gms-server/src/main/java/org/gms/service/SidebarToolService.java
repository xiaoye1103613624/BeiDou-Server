package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.dao.entity.SidebarToolConfigDO;
import org.gms.dao.mapper.SidebarToolConfigMapper;
import org.gms.model.dto.SidebarScriptTreeNodeDTO;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.server.sidebar.SidebarTools;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 游戏右边栏 ServerTool — DB CRUD + 热重载 + 在线同步。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SidebarToolService {

    private final SidebarToolConfigMapper mapper;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            reload(false);
        } catch (Exception e) {
            try {
                log.error(I18nUtil.getLogMessage("SidebarTool.reloadFailed"), e);
            } catch (Exception ignored) {
                log.error("[SidebarTool] Failed to load configs on startup", e);
            }
        }
    }

    public List<SidebarToolConfigDO> listAll() {
        ensureRows();
        return mapper.selectListByQuery(QueryWrapper.create().orderBy("tool_index", true));
    }

    /**
     * 扫描 scripts-zh-CN/BeiDouSpecial（优先）与 scripts/BeiDouSpecial，
     * 返回可搜索 TreeSelect 用的目录树；叶子 key 为脚本相对路径（无 .js）。
     */
    public List<SidebarScriptTreeNodeDTO> listScriptTree() {
        Path base = resolveBeiDouSpecialRoot();
        List<SidebarScriptTreeNodeDTO> tree = new ArrayList<>();
        Set<String> knownPaths = new HashSet<>();
        if (base != null && Files.isDirectory(base)) {
            tree.addAll(buildScriptTree(base, "", knownPaths));
        }
        // 已配置但不在目录中的路径：挂到「其它」下，避免 TreeSelect 丢显示
        List<SidebarScriptTreeNodeDTO> orphans = new ArrayList<>();
        for (SidebarToolConfigDO row : listAll()) {
            String path = row.getScriptPath();
            if (path == null || path.isBlank()) {
                continue;
            }
            String normalized = path.trim().replace('\\', '/');
            if (knownPaths.contains(normalized)) {
                continue;
            }
            orphans.add(SidebarScriptTreeNodeDTO.builder()
                    .title(fileNameOf(normalized))
                    .key(normalized)
                    .leaf(true)
                    .disabled(false)
                    .build());
            knownPaths.add(normalized);
        }
        if (!orphans.isEmpty()) {
            orphans.sort(Comparator.comparing(SidebarScriptTreeNodeDTO::getTitle, String.CASE_INSENSITIVE_ORDER));
            String orphanTitle;
            try {
                orphanTitle = I18nUtil.getMessage("SidebarTool.scriptTree.orphanGroup");
            } catch (Exception ignored) {
                orphanTitle = "Other";
            }
            tree.add(SidebarScriptTreeNodeDTO.builder()
                    .title(orphanTitle)
                    .key("dir:__orphan__")
                    .leaf(false)
                    .disabled(true)
                    .children(orphans)
                    .build());
        }
        return tree;
    }

    private Path resolveBeiDouSpecialRoot() {
        Path cwd = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path zh = cwd.resolve("scripts-zh-CN").resolve("BeiDouSpecial");
        if (Files.isDirectory(zh)) {
            return zh;
        }
        Path en = cwd.resolve("scripts").resolve("BeiDouSpecial");
        if (Files.isDirectory(en)) {
            return en;
        }
        // IDE 可能以仓库根为 cwd
        Path zhAlt = cwd.resolve("gms-server").resolve("scripts-zh-CN").resolve("BeiDouSpecial");
        if (Files.isDirectory(zhAlt)) {
            return zhAlt;
        }
        Path enAlt = cwd.resolve("gms-server").resolve("scripts").resolve("BeiDouSpecial");
        if (Files.isDirectory(enAlt)) {
            return enAlt;
        }
        return null;
    }

    private List<SidebarScriptTreeNodeDTO> buildScriptTree(Path dir, String relativePrefix, Set<String> knownPaths) {
        List<SidebarScriptTreeNodeDTO> nodes = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            List<Path> entries = stream
                    .sorted(Comparator.comparing(p -> p.getFileName().toString(), String.CASE_INSENSITIVE_ORDER))
                    .toList();
            for (Path entry : entries) {
                String name = entry.getFileName().toString();
                if (name.startsWith(".")) {
                    continue;
                }
                if (Files.isDirectory(entry)) {
                    String childRel = relativePrefix.isEmpty() ? name : relativePrefix + "/" + name;
                    List<SidebarScriptTreeNodeDTO> children = buildScriptTree(entry, childRel, knownPaths);
                    if (children.isEmpty()) {
                        continue;
                    }
                    nodes.add(SidebarScriptTreeNodeDTO.builder()
                            .title(name)
                            .key("dir:" + childRel)
                            .leaf(false)
                            .disabled(true)
                            .children(children)
                            .build());
                } else if (name.toLowerCase(Locale.ROOT).endsWith(".js")) {
                    String scriptName = name.substring(0, name.length() - 3);
                    String scriptPath = relativePrefix.isEmpty() ? scriptName : relativePrefix + "/" + scriptName;
                    knownPaths.add(scriptPath);
                    nodes.add(SidebarScriptTreeNodeDTO.builder()
                            .title(scriptName)
                            .key(scriptPath)
                            .leaf(true)
                            .disabled(false)
                            .build());
                }
            }
        } catch (IOException e) {
            log.warn(I18nUtil.getLogMessage("SidebarTool.scriptTreeFailed"), dir, e);
        }
        return nodes;
    }

    private static String fileNameOf(String path) {
        int idx = path.lastIndexOf('/');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }

    @Transactional
    public void save(SidebarToolConfigDO row) {
        if (row == null || row.getToolIndex() == null) {
            throw new IllegalArgumentException("toolIndex required");
        }
        int idx = row.getToolIndex();
        if (idx < 0 || idx >= SidebarTools.TOOL_COUNT) {
            throw new IllegalArgumentException("toolIndex out of range");
        }
        normalize(row);
        SidebarToolConfigDO existing = mapper.selectOneById(idx);
        if (existing == null) {
            mapper.insert(row);
        } else {
            mapper.update(row);
        }
        reload(true);
    }

    @Transactional
    public void saveAll(List<SidebarToolConfigDO> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (SidebarToolConfigDO row : rows) {
            if (row == null || row.getToolIndex() == null) {
                continue;
            }
            int idx = row.getToolIndex();
            if (idx < 0 || idx >= SidebarTools.TOOL_COUNT) {
                continue;
            }
            normalize(row);
            SidebarToolConfigDO existing = mapper.selectOneById(idx);
            if (existing == null) {
                mapper.insert(row);
            } else {
                mapper.update(row);
            }
        }
        reload(true);
    }

    public void reload() {
        reload(true);
    }

    public void reload(boolean syncClients) {
        ensureRows();
        List<SidebarToolConfigDO> rows = mapper.selectListByQuery(
                QueryWrapper.create().orderBy("tool_index", true));
        SidebarTools.reload(rows);
        log.info(I18nUtil.getLogMessage("SidebarTool.reload"), rows.size());
        if (syncClients) {
            broadcastConfig();
        }
    }

    public void sendConfig(Character chr) {
        if (chr == null || chr.getClient() == null) {
            return;
        }
        chr.sendPacket(PacketCreator.sidebarConfigSync(SidebarTools.list()));
    }

    private void broadcastConfig() {
        try {
            Server server = Server.getInstance();
            if (server == null) {
                return;
            }
            var packet = PacketCreator.sidebarConfigSync(SidebarTools.list());
            for (World world : server.getWorlds()) {
                if (world == null) {
                    continue;
                }
                for (Character chr : world.getPlayerStorage().getAllCharacters()) {
                    if (chr != null) {
                        chr.sendPacket(packet);
                    }
                }
            }
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("SidebarTool.broadcastFailed"), e);
        }
    }

    private void ensureRows() {
        long count = mapper.selectCountByQuery(QueryWrapper.create());
        if (count >= SidebarTools.TOOL_COUNT) {
            return;
        }
        List<SidebarToolConfigDO> defaults = SidebarTools.defaultRows();
        List<SidebarToolConfigDO> toInsert = new ArrayList<>();
        for (SidebarToolConfigDO row : defaults) {
            if (mapper.selectOneById(row.getToolIndex()) == null) {
                toInsert.add(row);
            }
        }
        for (SidebarToolConfigDO row : toInsert) {
            mapper.insert(row);
        }
    }

    private static void normalize(SidebarToolConfigDO row) {
        if (row.getLabel() == null) {
            row.setLabel("");
        } else {
            row.setLabel(row.getLabel().trim());
        }
        if (row.getScriptPath() == null) {
            row.setScriptPath("");
        } else {
            row.setScriptPath(row.getScriptPath().trim());
        }
        if (row.getTipTitle() == null) {
            row.setTipTitle("");
        } else {
            row.setTipTitle(row.getTipTitle().trim());
        }
        if (row.getTipDesc() == null) {
            row.setTipDesc("");
        } else {
            row.setTipDesc(row.getTipDesc().trim());
        }
        if (row.getEnabled() == null) {
            row.setEnabled(1);
        }
        // 无脚本时强制视为关闭，便于 Web 一眼看出会隐藏
        if (row.getScriptPath().isEmpty()) {
            row.setEnabled(0);
        }
    }
}
