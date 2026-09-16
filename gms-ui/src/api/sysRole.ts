import axios from 'axios';

export interface SysRoleRecord {
  id: number;
  code: string;
  name: string;
  remark?: string;
  enabled?: number;
}

export interface AdminUserRecord {
  id: number;
  name: string;
  webadmin?: number;
  nick?: string;
  roleCode?: string;
  roleName?: string;
}

export interface SysRoleBindMenusPayload {
  roleId: number;
  menuIds: number[];
}

export interface AssignAccountRolePayload {
  accountId: number;
  /** 是否允许登录后台；null 表示不改 */
  webadmin?: number | null;
  /** 角色编码；webadmin=0 时清空角色 */
  roleCode?: string;
}

export function listSysRoles() {
  return axios.get<SysRoleRecord[]>('/sysRole/v1/list');
}

export function listSysRoleMenus(roleId: number) {
  return axios.get<number[]>('/sysRole/v1/menus', {
    params: { roleId },
  });
}

export function bindSysRoleMenus(data: SysRoleBindMenusPayload) {
  return axios.post('/sysRole/v1/bindMenus', data);
}

export function listAdminUsers() {
  return axios.get<AdminUserRecord[]>('/sysRole/v1/adminUsers');
}

export function assignAccountRole(data: AssignAccountRolePayload) {
  return axios.post('/sysRole/v1/assign', data);
}
