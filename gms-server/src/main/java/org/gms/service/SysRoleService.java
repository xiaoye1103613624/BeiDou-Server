package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.gms.dao.entity.AccountsDO;
import org.gms.dao.entity.SysAdminAccountRoleDO;
import org.gms.dao.entity.SysAdminRoleDO;
import org.gms.dao.entity.SysAdminRoleMenuDO;
import org.gms.dao.mapper.AccountsMapper;
import org.gms.dao.mapper.SysAdminAccountRoleMapper;
import org.gms.dao.mapper.SysAdminRoleMapper;
import org.gms.dao.mapper.SysAdminRoleMenuMapper;
import org.gms.exception.BizException;
import org.gms.model.dto.AccountInfoDTO;
import org.gms.model.dto.AdminUserDTO;
import org.gms.model.dto.AssignAccountRoleDTO;
import org.gms.model.dto.SysRoleBindMenusDTO;
import org.gms.model.dto.SysRoleDTO;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleService {

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_OPERATOR = "operator";

    private final SysAdminRoleMapper roleMapper;
    private final SysAdminRoleMenuMapper roleMenuMapper;
    private final SysAdminAccountRoleMapper accountRoleMapper;
    private final AccountsMapper accountsMapper;

    public List<SysRoleDTO> listRoles() {
        List<SysAdminRoleDO> rows = roleMapper.selectListByQuery(
                QueryWrapper.create().eq("enabled", 1).orderBy("id", true));
        return rows.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<Long> listMenuIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return List.of();
        }
        return roleMenuMapper.selectListByQuery(QueryWrapper.create().eq("role_id", roleId))
                .stream()
                .map(SysAdminRoleMenuDO::getMenuId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Set<Long> listMenuIdsByRoleCodes(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return Set.of();
        }
        List<SysAdminRoleDO> roles = roleMapper.selectListByQuery(
                QueryWrapper.create().in("code", roleCodes).eq("enabled", 1));
        if (roles.isEmpty()) {
            return Set.of();
        }
        List<Long> roleIds = roles.stream().map(SysAdminRoleDO::getId).toList();
        return roleMenuMapper.selectListByQuery(QueryWrapper.create().in("role_id", roleIds))
                .stream()
                .map(SysAdminRoleMenuDO::getMenuId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public List<String> listRoleCodesByAccountId(Integer accountId) {
        if (accountId == null) {
            return List.of();
        }
        SysAdminAccountRoleDO link = accountRoleMapper.selectOneById(accountId);
        if (link == null || link.getRoleId() == null) {
            return List.of();
        }
        SysAdminRoleDO role = roleMapper.selectOneById(link.getRoleId());
        if (role == null || !Objects.equals(role.getEnabled(), 1) || !StringUtils.hasText(role.getCode())) {
            return List.of();
        }
        return List.of(role.getCode());
    }

    /**
     * 后台可登录账号的角色；webadmin 但未绑定时默认 admin（兼容旧数据）。
     */
    public List<String> resolveLoginRoles(AccountsDO account) {
        if (account == null || account.getWebadmin() == null || account.getWebadmin() != 1) {
            return List.of();
        }
        List<String> codes = listRoleCodesByAccountId(account.getId());
        if (codes.isEmpty()) {
            return List.of(ROLE_ADMIN);
        }
        return codes;
    }

    public AccountInfoDTO toAccountInfo(AccountsDO account) {
        List<String> roles = resolveLoginRoles(account);
        return AccountInfoDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .webadmin(account.getWebadmin())
                .nick(account.getNick())
                .email(account.getEmail())
                .language(account.getLanguage())
                .role(roles.isEmpty() ? "" : roles.get(0))
                .roles(new ArrayList<>(roles))
                .build();
    }

    public List<AdminUserDTO> listAdminUsers() {
        List<AccountsDO> accounts = accountsMapper.selectListByQuery(
                QueryWrapper.create().eq("webadmin", 1).orderBy("id", true));
        if (accounts.isEmpty()) {
            return List.of();
        }
        Map<Integer, Long> accountRoleMap = new HashMap<>();
        List<Integer> ids = accounts.stream().map(AccountsDO::getId).toList();
        accountRoleMapper.selectListByQuery(QueryWrapper.create().in("account_id", ids))
                .forEach(row -> accountRoleMap.put(row.getAccountId(), row.getRoleId()));

        Map<Long, SysAdminRoleDO> roleMap = roleMapper.selectAll().stream()
                .collect(Collectors.toMap(SysAdminRoleDO::getId, r -> r, (a, b) -> a));

        List<AdminUserDTO> result = new ArrayList<>();
        for (AccountsDO account : accounts) {
            Long roleId = accountRoleMap.get(account.getId());
            SysAdminRoleDO role = roleId == null ? null : roleMap.get(roleId);
            String code = role != null ? role.getCode() : ROLE_ADMIN;
            String name = role != null ? role.getName() : ROLE_ADMIN;
            result.add(AdminUserDTO.builder()
                    .id(account.getId())
                    .name(account.getName())
                    .webadmin(account.getWebadmin())
                    .nick(account.getNick())
                    .roleCode(code)
                    .roleName(name)
                    .build());
        }
        return result;
    }

    @Transactional
    public void bindMenus(SysRoleBindMenusDTO dto) {
        if (dto == null || dto.getRoleId() == null) {
            throw BizException.illegalArgument();
        }
        SysAdminRoleDO role = roleMapper.selectOneById(dto.getRoleId());
        if (role == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("SYS_ROLE.NOT_FOUND"));
        }
        roleMenuMapper.deleteByQuery(QueryWrapper.create().eq("role_id", dto.getRoleId()));
        List<Long> menuIds = dto.getMenuIds() == null ? List.of() : dto.getMenuIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (menuIds.isEmpty()) {
            return;
        }
        List<SysAdminRoleMenuDO> rows = menuIds.stream()
                .map(menuId -> SysAdminRoleMenuDO.builder().roleId(dto.getRoleId()).menuId(menuId).build())
                .toList();
        roleMenuMapper.insertBatch(rows);
    }

    @Transactional
    public void assignAccountRole(AssignAccountRoleDTO dto) {
        if (dto == null || dto.getAccountId() == null) {
            throw BizException.illegalArgument();
        }
        AccountsDO account = accountsMapper.selectOneById(dto.getAccountId());
        if (account == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("AccountService.id.NotExist"));
        }

        Integer webadmin = dto.getWebadmin();
        if (webadmin != null) {
            AccountsDO patch = new AccountsDO();
            patch.setId(account.getId());
            patch.setWebadmin(webadmin);
            accountsMapper.update(patch);
            account.setWebadmin(webadmin);
        }

        boolean canLogin = account.getWebadmin() != null && account.getWebadmin() == 1;
        if (!canLogin) {
            accountRoleMapper.deleteById(account.getId());
            return;
        }

        String roleCode = StringUtils.hasText(dto.getRoleCode()) ? dto.getRoleCode().trim() : ROLE_ADMIN;
        SysAdminRoleDO role = roleMapper.selectOneByQuery(
                QueryWrapper.create().eq("code", roleCode).eq("enabled", 1));
        if (role == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("SYS_ROLE.NOT_FOUND"));
        }

        SysAdminAccountRoleDO existing = accountRoleMapper.selectOneById(account.getId());
        if (existing == null) {
            accountRoleMapper.insert(SysAdminAccountRoleDO.builder()
                    .accountId(account.getId())
                    .roleId(role.getId())
                    .build());
        } else {
            existing.setRoleId(role.getId());
            accountRoleMapper.update(existing);
        }
    }

    /**
     * 账号更新时同步角色：开启 webadmin 默认 admin；关闭则清角色；显式传 roleCode 则覆盖。
     */
    @Transactional
    public void syncAccountRoleOnUpdate(Integer accountId, Integer webadmin, String roleCode) {
        AssignAccountRoleDTO dto = AssignAccountRoleDTO.builder()
                .accountId(accountId)
                .webadmin(webadmin)
                .roleCode(roleCode)
                .build();
        assignAccountRole(dto);
    }

    public String getAccountRoleCode(Integer accountId) {
        List<String> codes = listRoleCodesByAccountId(accountId);
        if (!codes.isEmpty()) {
            return codes.get(0);
        }
        AccountsDO account = accountsMapper.selectOneById(accountId);
        if (account != null && account.getWebadmin() != null && account.getWebadmin() == 1) {
            return ROLE_ADMIN;
        }
        return null;
    }

    private SysRoleDTO toDto(SysAdminRoleDO row) {
        return SysRoleDTO.builder()
                .id(row.getId())
                .code(row.getCode())
                .name(row.getName())
                .remark(row.getRemark())
                .enabled(row.getEnabled())
                .build();
    }
}
