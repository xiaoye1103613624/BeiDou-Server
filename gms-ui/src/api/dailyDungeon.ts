import axios from 'axios';

/** 里程碑奖励 */
export interface DailyDungeonReward {
  id?: number;
  completeCount?: number;
  rewardDesc?: string;
  itemId?: number;
  /** 物品名称（服务端返回，展示用） */
  itemName?: string;
  quantity?: number;
  sortOrder?: number;
}

/** 每日副本配置 */
export interface DailyDungeonForm {
  id?: number;
  dungeonKey?: string;
  dungeonName?: string;
  mapId?: number;
  /** 地图名称（服务端自动解析，展示用） */
  mapName?: string;
  /** 每日需完成次数 */
  completeCount?: number;
  sweepItemId?: number;
  sweepItemCost?: number;
  maxSweep?: number;
  sortOrder?: number;
  enabled?: number;
  rewards?: DailyDungeonReward[];
}

/** 每日完成奖励（所有副本完成后可领取） */
export interface DailyRewardForm {
  id?: number;
  itemId?: number;
  /** 物品名称（服务端返回，展示用） */
  itemName?: string;
  quantity?: number;
  rewardDesc?: string;
  sortOrder?: number;
}

/** VIP物品配置 */
export interface VipConfigForm {
  id?: number;
  itemId?: number;
  /** 物品名称（服务端返回，展示用） */
  itemName?: string;
  description?: string;
  enabled?: number;
  sortOrder?: number;
}

// ==================== 副本配置 API ====================

export function getConfigList() {
  return axios.get<DailyDungeonForm[]>('/dailyDungeon/v1/getConfigList');
}

export function getConfig(id: number) {
  return axios.get<DailyDungeonForm>(`/dailyDungeon/v1/getConfig/${id}`);
}

export function saveConfig(data: DailyDungeonForm) {
  return axios.post<DailyDungeonForm>('/dailyDungeon/v1/saveConfig', data);
}

export function deleteConfig(id: number) {
  return axios.delete(`/dailyDungeon/v1/deleteConfig/${id}`);
}

// ==================== 每日完成奖励 API ====================

export function getDailyRewardList() {
  return axios.get<DailyRewardForm[]>('/dailyDungeon/v1/getDailyRewardList');
}

export function saveDailyReward(data: DailyRewardForm) {
  return axios.post<DailyRewardForm>('/dailyDungeon/v1/saveDailyReward', data);
}

export function deleteDailyReward(id: number) {
  return axios.delete(`/dailyDungeon/v1/deleteDailyReward/${id}`);
}

// ==================== VIP物品配置 API ====================

export function getVipConfigList() {
  return axios.get<VipConfigForm[]>('/dailyDungeon/v1/getVipConfigList');
}

export function saveVipConfig(data: VipConfigForm) {
  return axios.post<VipConfigForm>('/dailyDungeon/v1/saveVipConfig', data);
}

export function deleteVipConfig(id: number) {
  return axios.delete(`/dailyDungeon/v1/deleteVipConfig/${id}`);
}
