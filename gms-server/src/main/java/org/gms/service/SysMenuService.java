package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.gms.dao.entity.SysAdminMenuDO;
import org.gms.dao.mapper.SysAdminMenuMapper;
import org.gms.exception.BizException;
import org.gms.model.dto.SysMenuDTO;
import org.gms.model.dto.SysMenuReorderDTO;
import org.gms.model.dto.SysMenuRouteDTO;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysMenuService {

    private final SysAdminMenuMapper sysAdminMenuMapper;

    public List<SysMenuDTO> listTree(boolean includeDisabled) {
        List<SysAdminMenuDO> rows = sysAdminMenuMapper.selectListByQuery(
                QueryWrapper.create().orderBy("sort_order", true).orderBy("id", true));
        if (!includeDisabled) {
            rows = rows.stream().filter(r -> Objects.equals(r.getEnabled(), 1)).toList();
        }
        return buildDtoTree(rows);
    }

    public List<SysMenuRouteDTO> listSidebarRoutes() {
        List<SysAdminMenuDO> rows = sysAdminMenuMapper.selectListByQuery(
                QueryWrapper.create()
                        .eq("enabled", 1)
                        .eq("hide_in_menu", 0)
                        .orderBy("sort_order", true)
                        .orderBy("id", true));
        return buildRouteTree(rows);
    }

    @Transactional
    public SysMenuDTO save(SysMenuDTO dto) {
        validateSave(dto);
        SysAdminMenuDO entity = toEntity(dto);
        if (dto.getId() == null) {
            sysAdminMenuMapper.insert(entity);
        } else {
            SysAdminMenuDO existing = sysAdminMenuMapper.selectOneById(dto.getId());
            if (existing == null) {
                throw BizException.illegalArgument(I18nUtil.getExceptionMessage("SYS_MENU.NOT_FOUND"));
            }
            entity.setId(dto.getId());
            sysAdminMenuMapper.update(entity);
        }
        SysAdminMenuDO saved = sysAdminMenuMapper.selectOneById(entity.getId());
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw BizException.illegalArgument();
        }
        SysAdminMenuDO existing = sysAdminMenuMapper.selectOneById(id);
        if (existing == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("SYS_MENU.NOT_FOUND"));
        }
        List<Long> toDelete = new ArrayList<>();
        collectSubtreeIds(id, toDelete);
        for (Long deleteId : toDelete) {
            sysAdminMenuMapper.deleteById(deleteId);
        }
    }

    @Transactional
    public void reorder(SysMenuReorderDTO request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw BizException.illegalArgument();
        }
        for (SysMenuReorderDTO.Item item : request.getItems()) {
            if (item.getId() == null) {
                continue;
            }
            SysAdminMenuDO row = sysAdminMenuMapper.selectOneById(item.getId());
            if (row == null) {
                continue;
            }
            if (item.getParentId() != null) {
                row.setParentId(item.getParentId());
            }
            if (item.getSortOrder() != null) {
                row.setSortOrder(item.getSortOrder());
            }
            sysAdminMenuMapper.update(row);
        }
    }

    private void validateSave(SysMenuDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getName())) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("SYS_MENU.NAME_REQUIRED"));
        }
        long parentId = dto.getParentId() == null ? 0L : dto.getParentId();
        if (parentId > 0) {
            SysAdminMenuDO parent = sysAdminMenuMapper.selectOneById(parentId);
            if (parent == null) {
                throw BizException.illegalArgument(I18nUtil.getExceptionMessage("SYS_MENU.PARENT_NOT_FOUND"));
            }
            if (dto.getId() != null && Objects.equals(dto.getId(), parentId)) {
                throw BizException.illegalArgument(I18nUtil.getExceptionMessage("SYS_MENU.INVALID_PARENT"));
            }
            if (dto.getId() != null && isDescendant(dto.getId(), parentId)) {
                throw BizException.illegalArgument(I18nUtil.getExceptionMessage("SYS_MENU.INVALID_PARENT"));
            }
        }
        QueryWrapper nameQuery = QueryWrapper.create().eq("name", dto.getName().trim());
        if (dto.getId() != null) {
            nameQuery.ne("id", dto.getId());
        }
        if (sysAdminMenuMapper.selectCountByQuery(nameQuery) > 0) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("SYS_MENU.NAME_EXISTS"));
        }
    }

    private boolean isDescendant(Long ancestorId, Long candidateId) {
        Set<Long> visited = new HashSet<>();
        Long current = candidateId;
        while (current != null && current > 0 && visited.add(current)) {
            if (Objects.equals(current, ancestorId)) {
                return true;
            }
            SysAdminMenuDO row = sysAdminMenuMapper.selectOneById(current);
            if (row == null) {
                return false;
            }
            current = row.getParentId();
        }
        return false;
    }

    private void collectSubtreeIds(Long rootId, List<Long> out) {
        out.add(rootId);
        List<SysAdminMenuDO> children = sysAdminMenuMapper.selectListByQuery(
                QueryWrapper.create().eq("parent_id", rootId));
        for (SysAdminMenuDO child : children) {
            collectSubtreeIds(child.getId(), out);
        }
    }

    private List<SysMenuDTO> buildDtoTree(List<SysAdminMenuDO> rows) {
        Map<Long, SysMenuDTO> map = new HashMap<>();
        for (SysAdminMenuDO row : rows) {
            map.put(row.getId(), toDto(row));
        }
        List<SysMenuDTO> roots = new ArrayList<>();
        for (SysAdminMenuDO row : rows) {
            SysMenuDTO node = map.get(row.getId());
            long parentId = row.getParentId() == null ? 0L : row.getParentId();
            if (parentId <= 0 || !map.containsKey(parentId)) {
                roots.add(node);
            } else {
                map.get(parentId).getChildren().add(node);
            }
        }
        sortDtoTree(roots);
        return roots;
    }

    private void sortDtoTree(List<SysMenuDTO> nodes) {
        nodes.sort(Comparator
                .comparing((SysMenuDTO n) -> n.getSortOrder() == null ? 0 : n.getSortOrder())
                .thenComparing(n -> n.getId() == null ? 0L : n.getId()));
        for (SysMenuDTO node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortDtoTree(node.getChildren());
            }
        }
    }

    private List<SysMenuRouteDTO> buildRouteTree(List<SysAdminMenuDO> rows) {
        Map<Long, SysMenuRouteDTO> map = new HashMap<>();
        Map<Long, Long> parentMap = new HashMap<>();
        for (SysAdminMenuDO row : rows) {
            map.put(row.getId(), toRoute(row));
            parentMap.put(row.getId(), row.getParentId() == null ? 0L : row.getParentId());
        }
        List<SysMenuRouteDTO> roots = new ArrayList<>();
        for (SysAdminMenuDO row : rows) {
            SysMenuRouteDTO node = map.get(row.getId());
            long parentId = parentMap.getOrDefault(row.getId(), 0L);
            if (parentId <= 0 || !map.containsKey(parentId)) {
                roots.add(node);
            } else {
                SysMenuRouteDTO parent = map.get(parentId);
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(node);
            }
        }
        return roots;
    }

    private SysMenuRouteDTO toRoute(SysAdminMenuDO row) {
        List<String> roles = parseRoles(row.getRoles());
        return SysMenuRouteDTO.builder()
                .path(row.getPath())
                .name(row.getName())
                .meta(SysMenuRouteDTO.SysMenuRouteMetaDTO.builder()
                        .locale(row.getLocaleKey())
                        .requiresAuth(!Objects.equals(row.getRequiresAuth(), 0))
                        .icon(row.getIcon())
                        .order(row.getSortOrder())
                        .roles(roles)
                        .hideInMenu(Objects.equals(row.getHideInMenu(), 1))
                        .build())
                // 叶子不要空 children，否则前端 use-menu-tree 会把无子节点的根菜单滤掉
                .children(null)
                .build();
    }

    private List<String> parseRoles(String roles) {
        if (!StringUtils.hasText(roles)) {
            return List.of("admin");
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    private SysMenuDTO toDto(SysAdminMenuDO row) {
        return SysMenuDTO.builder()
                .id(row.getId())
                .parentId(row.getParentId() == null ? 0L : row.getParentId())
                .name(row.getName())
                .path(row.getPath())
                .localeKey(row.getLocaleKey())
                .icon(row.getIcon())
                .sortOrder(row.getSortOrder())
                .menuType(row.getMenuType())
                .roles(row.getRoles())
                .requiresAuth(row.getRequiresAuth())
                .hideInMenu(row.getHideInMenu())
                .enabled(row.getEnabled())
                .remark(row.getRemark())
                .children(new ArrayList<>())
                .build();
    }

    private SysAdminMenuDO toEntity(SysMenuDTO dto) {
        return SysAdminMenuDO.builder()
                .id(dto.getId())
                .parentId(dto.getParentId() == null ? 0L : dto.getParentId())
                .name(dto.getName().trim())
                .path(dto.getPath() == null ? "" : dto.getPath().trim())
                .localeKey(dto.getLocaleKey() == null ? "" : dto.getLocaleKey().trim())
                .icon(StringUtils.hasText(dto.getIcon()) ? dto.getIcon().trim() : null)
                .sortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder())
                .menuType(dto.getMenuType() == null ? 1 : dto.getMenuType())
                .roles(StringUtils.hasText(dto.getRoles()) ? dto.getRoles().trim() : "admin")
                .requiresAuth(dto.getRequiresAuth() == null ? 1 : dto.getRequiresAuth())
                .hideInMenu(dto.getHideInMenu() == null ? 0 : dto.getHideInMenu())
                .enabled(dto.getEnabled() == null ? 1 : dto.getEnabled())
                .remark(dto.getRemark())
                .build();
    }
}
