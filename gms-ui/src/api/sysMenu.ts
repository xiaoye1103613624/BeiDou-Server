import axios from 'axios';

export interface SysMenuRecord {
  id?: number;
  parentId?: number;
  name: string;
  path?: string;
  localeKey?: string;
  icon?: string;
  sortOrder?: number;
  /** 0目录 1菜单 2外链 */
  menuType?: number;
  roles?: string;
  requiresAuth?: number;
  hideInMenu?: number;
  enabled?: number;
  remark?: string;
  children?: SysMenuRecord[];
}

export interface SysMenuReorderItem {
  id: number;
  parentId?: number;
  sortOrder?: number;
}

export function getSysMenuTree(includeDisabled = true) {
  return axios.get<SysMenuRecord[]>('/sysMenu/v1/tree', {
    params: { includeDisabled },
  });
}

export function getSysMenuSidebar() {
  return axios.get('/sysMenu/v1/sidebar');
}

export function saveSysMenu(data: SysMenuRecord) {
  return axios.post('/sysMenu/v1/save', data);
}

export function deleteSysMenu(id: number) {
  return axios.post('/sysMenu/v1/delete', id);
}

export function reorderSysMenu(items: SysMenuReorderItem[]) {
  return axios.post('/sysMenu/v1/reorder', { items });
}
