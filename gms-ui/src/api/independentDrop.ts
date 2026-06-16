import axios from 'axios';

/** 独立掉落怪物配置 */
export interface IndependentDropConfig {
  id?: number;
  mobId?: number;
  mobName?: string;
  enabled?: number;
}

export function getConfigList() {
  return axios.get<IndependentDropConfig[]>('/independentDrop/v1/getConfigList');
}

export function getConfig(id: number) {
  return axios.get<IndependentDropConfig>(`/independentDrop/v1/getConfig/${id}`);
}

export function saveConfig(data: IndependentDropConfig) {
  return axios.post<IndependentDropConfig>('/independentDrop/v1/saveConfig', data);
}

export function deleteConfig(id: number) {
  return axios.delete(`/independentDrop/v1/deleteConfig/${id}`);
}
