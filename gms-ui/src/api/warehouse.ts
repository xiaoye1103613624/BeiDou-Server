import axios from 'axios';
import type {
  WarehouseConfigState,
  WarehouseItemState,
  WarehouseOperateState,
  WarehouseQueryState,
  WarehouseGameParamsState,
} from '@/store/modules/warehouse/type';

/** 获取仓库物品白名单配置列表（支持筛选） */
export function getConfigList(params?: {
  itemId?: number;
  inventoryType?: number;
  enabled?: number;
}) {
  return axios.get<WarehouseConfigState[]>('/warehouse/v1/getConfigList', {
    params,
  });
}

/** 获取单个仓库物品白名单配置 */
export function getConfig(id: number) {
  return axios.get<WarehouseConfigState>(`/warehouse/v1/getConfig/${id}`);
}

/** 保存仓库物品白名单配置（新增或更新） */
export function saveConfig(data: WarehouseConfigState) {
  return axios.post<WarehouseConfigState>('/warehouse/v1/saveConfig', data);
}

/** 删除仓库物品白名单配置 */
export function deleteConfig(id: number) {
  return axios.delete(`/warehouse/v1/deleteConfig/${id}`);
}

/** 批量删除仓库物品白名单配置 */
export function deleteConfigBatch(ids: number[]) {
  return axios.post('/warehouse/v1/deleteConfigBatch', ids);
}

/** 查询仓库物品列表 */
export function getWarehouseItems(params: WarehouseQueryState) {
  return axios.get<WarehouseItemState[]>('/warehouse/v1/getWarehouseItems', {
    params,
  });
}

/** 存入物品到仓库 */
export function depositItem(data: WarehouseOperateState) {
  return axios.post<boolean>('/warehouse/v1/deposit', data);
}

/** 从仓库取出物品 */
export function withdrawItem(data: WarehouseOperateState) {
  return axios.post<number>('/warehouse/v1/withdraw', data);
}

/** 删除仓库物品记录 */
export function deleteWarehouseItem(id: number) {
  return axios.delete(`/warehouse/v1/deleteWarehouseItem/${id}`);
}

/** 批量删除仓库物品记录 */
export function deleteWarehouseItemBatch(ids: number[]) {
  return axios.post('/warehouse/v1/deleteWarehouseItemBatch', ids);
}

/** 获取有仓库数据的账号ID列表 */
export function getAccountList() {
  return axios.get<number[]>('/warehouse/v1/getAccountList');
}

/** 获取某账号下有仓库数据的角色ID列表 */
export function getCharacterList(accountId: number) {
  return axios.get<number[]>('/warehouse/v1/getCharacterList', {
    params: { accountId },
  });
}

/** 获取仓库游戏参数 */
export function getGameParams() {
  return axios.get<WarehouseGameParamsState>('/warehouse/v1/getGameParams');
}

/** 一键根据WZ数据更新仓库白名单配置的物品名称 */
export function refreshItemNames() {
  return axios.post<number>('/warehouse/v1/refreshItemNames');
}
