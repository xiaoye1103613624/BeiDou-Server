import axios from 'axios';

export interface XyCollectionItem {
  id?: number;
  itemId: number;
  quantity: number;
  sortOrder?: number;
}

export interface XyCollectionStage {
  id?: number;
  stageName: string;
  sortOrder: number;
  rewardType: string;
  rewardAmount: number;
  items: XyCollectionItem[];
}

export interface XyCollectionType {
  id?: number;
  typeName: string;
  description?: string;
  sortOrder: number;
  enabled: number;
  rewardType: string;
  rewardAmount: number;
  stages: XyCollectionStage[];
}

export interface ItemSearchResult {
  itemId: number;
  itemName: string;
}

export function getXyCollectionList() {
  return axios.get('/xyCollection/v1/getConfigList');
}

export function getXyCollectionConfig(id: number) {
  return axios.get(`/xyCollection/v1/getConfig/${id}`);
}

export function saveXyCollectionConfig(data: XyCollectionType) {
  return axios.post('/xyCollection/v1/saveConfig', data);
}

export function deleteXyCollectionConfig(id: number) {
  return axios.delete(`/xyCollection/v1/deleteConfig/${id}`);
}

export function searchItems(keyword: string, limit = 20) {
  return axios.get('/item/v1/search', { params: { keyword, limit } });
}
