import axios from 'axios';

/** 每日探索地图池 */
export interface DailyExploreMap {
  id?: number;
  mapId?: number;
  sortOrder?: number;
  enabled?: number;
  createTime?: string;
  updateTime?: string;
}

/** 每轮随机奖励 */
export interface DailyExploreReward {
  id?: number;
  itemId?: number;
  itemName?: string;
  minQuantity?: number;
  maxQuantity?: number;
  weight?: number;
  sortOrder?: number;
  enabled?: number;
}

/** 完成奖励 */
export interface DailyExploreFinalReward {
  id?: number;
  exploreCount?: number;
  rewardDesc?: string;
  itemId?: number;
  itemName?: string;
  quantity?: number;
  sortOrder?: number;
}

/** 游戏参数 */
export interface DailyExploreGameParams {
  dailyLimit?: number;
}

// ==================== 地图池 ====================

export function getMapList() {
  return axios.get<DailyExploreMap[]>('/dailyExplore/v1/getMapList');
}

export function getMap(id: number) {
  return axios.get<DailyExploreMap>(`/dailyExplore/v1/getMap/${id}`);
}

export function saveMap(data: DailyExploreMap) {
  return axios.post<DailyExploreMap>('/dailyExplore/v1/saveMap', data);
}

export function deleteMap(id: number) {
  return axios.delete(`/dailyExplore/v1/deleteMap/${id}`);
}

export function deleteMapBatch(ids: number[]) {
  return axios.post('/dailyExplore/v1/deleteMapBatch', ids);
}

// ==================== 每轮随机奖励 ====================

export function getRewardList() {
  return axios.get<DailyExploreReward[]>('/dailyExplore/v1/getRewardList');
}

export function saveReward(data: DailyExploreReward) {
  return axios.post<DailyExploreReward>('/dailyExplore/v1/saveReward', data);
}

export function deleteReward(id: number) {
  return axios.delete(`/dailyExplore/v1/deleteReward/${id}`);
}

// ==================== 完成奖励 ====================

export function getFinalRewardList() {
  return axios.get<DailyExploreFinalReward[]>(
    '/dailyExplore/v1/getFinalRewardList'
  );
}

export function saveFinalReward(data: DailyExploreFinalReward) {
  return axios.post<DailyExploreFinalReward>(
    '/dailyExplore/v1/saveFinalReward',
    data
  );
}

export function deleteFinalReward(id: number) {
  return axios.delete(`/dailyExplore/v1/deleteFinalReward/${id}`);
}

// ==================== 游戏参数 ====================

export function getGameParams() {
  return axios.get<DailyExploreGameParams>('/dailyExplore/v1/getGameParams');
}
