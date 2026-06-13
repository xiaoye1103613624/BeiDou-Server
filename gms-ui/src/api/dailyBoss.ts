import axios from 'axios';

/** 里程碑奖励 */
export interface DailyBossReward {
  id?: number;
  completeCount?: number;
  rewardDesc?: string;
  itemId?: number;
  quantity?: number;
  sortOrder?: number;
}

/** 每日Boss配置 */
export interface DailyBossForm {
  id?: number;
  bossKey?: string;
  bossName?: string;
  bossMobId?: number;
  sweepItemId?: number;
  sweepItemCost?: number;
  maxSweep?: number;
  sortOrder?: number;
  enabled?: number;
  rewards?: DailyBossReward[];
}

export function getConfigList() {
  return axios.get<DailyBossForm[]>('/dailyBoss/v1/getConfigList');
}

export function getConfig(id: number) {
  return axios.get<DailyBossForm>(`/dailyBoss/v1/getConfig/${id}`);
}

export function saveConfig(data: DailyBossForm) {
  return axios.post<DailyBossForm>('/dailyBoss/v1/saveConfig', data);
}

export function deleteConfig(id: number) {
  return axios.delete(`/dailyBoss/v1/deleteConfig/${id}`);
}

/** 每日Boss环式系统游戏参数 */
export interface DailyBossGameParams {
  bossRingEnabled?: number;
  dailyLimit?: number;
  expBase?: number;
  mesoBase?: number;
  killMin?: number;
  killMax?: number;
  abandonFee?: number;
  finalItemId?: number;
  finalItemQty?: number;
  milestoneRewards?: string;
  randomRewards?: string;
}

export function getGameParams() {
  return axios.get<DailyBossGameParams>('/dailyBoss/v1/getGameParams');
}
