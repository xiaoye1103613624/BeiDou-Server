import axios from 'axios';

export interface TownConfigSearch {
  mapId?: number;
  townName?: string;
  pageNo: number;
  pageSize: number;
}

export interface TownConfigItem {
  id?: number;
  mapId?: number;
  townName: string;
  enabled: number;
  createTime?: string;
  updateTime?: string;
}

export function getTownConfigList(data: TownConfigSearch) {
  return axios.post('/townConfig/v1/getTownConfigList', data);
}

export function addTownConfig(data: TownConfigItem) {
  return axios.post('/townConfig/v1/addTownConfig', data);
}

export function updateTownConfig(data: TownConfigItem) {
  return axios.post('/townConfig/v1/updateTownConfig', data);
}

export function deleteTownConfig(id: number) {
  return axios.delete(`/townConfig/v1/deleteTownConfig/${id}`);
}

export function deleteTownConfigList(ids: number[]) {
  return axios.post('/townConfig/v1/deleteTownConfigList', ids);
}
