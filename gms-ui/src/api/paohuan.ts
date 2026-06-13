import axios from 'axios';

/** 里程碑奖励 */
export interface PaohuanReward {
  /** 主键ID（更新时必填） */
  id?: number;
  /** 完成第几环时触发 */
  ringCount?: number;
  /** 奖励描述 */
  rewardDesc?: string;
  /** 奖励道具ID(0=金币) */
  itemId?: number;
  /** 物品名称（服务端返回，展示用） */
  itemName?: string;
  /** 奖励数量 */
  quantity?: number;
  /** 同环内排序 */
  sortOrder?: number;
}

/** 跑环物品池配置 */
export interface PaohuanConfigForm {
  /** 配置ID（更新时必填） */
  id?: number;
  /** 物品ID */
  itemId?: number;
  /** 物品名称（服务端返回，展示用） */
  itemName?: string;
  /** 需求数量 */
  quantity?: number;
  /** 掉落地图ID（用于VIP传送，0=未知） */
  dropMapId?: number;
  /** 排序顺序 */
  sortOrder?: number;
  /** 是否启用 */
  enabled?: number;
  /** 里程碑奖励列表 */
  rewards?: PaohuanReward[];
}

/** 每环随机奖励 */
export interface PaohuanRingReward {
  id?: number;
  itemId?: number;
  /** 物品名称（服务端返回，展示用） */
  itemName?: string;
  minQuantity?: number;
  maxQuantity?: number;
  weight?: number;
  sortOrder?: number;
  enabled?: number;
}

/** 游戏参数 */
export interface PaohuanGameParams {
  dailyLimit?: number;
  expPerRing?: number;
  mesoPerRing?: number;
}

/** 获取跑环物品池配置列表 */
export function getConfigList() {
  return axios.get<PaohuanConfigForm[]>('/paohuan/v1/getConfigList');
}

/** 获取单个配置 */
export function getConfig(id: number) {
  return axios.get<PaohuanConfigForm>(`/paohuan/v1/getConfig/${id}`);
}

/** 保存配置 */
export function saveConfig(data: PaohuanConfigForm) {
  return axios.post<PaohuanConfigForm>('/paohuan/v1/saveConfig', data);
}

/** 删除配置 */
export function deleteConfig(id: number) {
  return axios.delete(`/paohuan/v1/deleteConfig/${id}`);
}

/** 批量删除配置 */
export function deleteConfigBatch(ids: number[]) {
  return axios.post('/paohuan/v1/deleteConfigBatch', ids);
}

/** 获取里程碑奖励列表 */
export function getRewardList() {
  return axios.get<PaohuanReward[]>('/paohuan/v1/getRewardList');
}

/** 保存单条里程碑奖励 */
export function saveReward(data: PaohuanReward) {
  return axios.post<PaohuanReward>('/paohuan/v1/saveReward', data);
}

/** 删除里程碑奖励 */
export function deleteReward(id: number) {
  return axios.delete(`/paohuan/v1/deleteReward/${id}`);
}

/** 获取每环随机奖励列表 */
export function getRingRewardList() {
  return axios.get<PaohuanRingReward[]>('/paohuan/v1/getRingRewardList');
}
export function saveRingReward(data: PaohuanRingReward) {
  return axios.post<PaohuanRingReward>('/paohuan/v1/saveRingReward', data);
}
export function deleteRingReward(id: number) {
  return axios.delete(`/paohuan/v1/deleteRingReward/${id}`);
}

/** 获取游戏参数 */
export function getGameParams() {
  return axios.get<PaohuanGameParams>('/paohuan/v1/getGameParams');
}
