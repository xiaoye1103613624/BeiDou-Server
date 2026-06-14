import axios from 'axios';

/** 出师奖励道具 */
export interface GraduationRewardItem {
  id?: number;
  itemId?: number;
  quantity?: number;
}

/** 出师奖励 */
export interface GraduationReward {
  id?: number;
  rewardType?: number; // 0=师父奖励 1=徒弟奖励
  meso?: number;
  nxCredit?: number;
  maplePoint?: number;
  nxPrepaid?: number;
  enabled?: number;
  items?: GraduationRewardItem[];
}

/** 师徒系统配置 */
export interface MentorConfig {
  id?: number;
  configKey?: string;
  configValue?: string;
  description?: string;
  enabled?: number;
}

// ==================== 系统配置 ====================

export function getConfigList() {
  return axios.get<MentorConfig[]>('/mentor/v1/getConfigList');
}

export function getConfig(id: number) {
  return axios.get<MentorConfig>(`/mentor/v1/getConfig/${id}`);
}

export function saveConfig(data: MentorConfig) {
  return axios.post<MentorConfig>('/mentor/v1/saveConfig', data);
}

export function deleteConfig(id: number) {
  return axios.delete(`/mentor/v1/deleteConfig/${id}`);
}

// ==================== 毕业奖励 ====================

export function getRewardList() {
  return axios.get<GraduationReward[]>('/mentor/v1/getRewardList');
}

export function getReward(id: number) {
  return axios.get<GraduationReward>(`/mentor/v1/getReward/${id}`);
}

export function saveReward(data: GraduationReward) {
  return axios.post<GraduationReward>('/mentor/v1/saveReward', data);
}

export function deleteReward(id: number) {
  return axios.delete(`/mentor/v1/deleteReward/${id}`);
}
