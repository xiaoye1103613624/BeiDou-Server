import axios from 'axios';

export interface MedalEnhanceCost {
  id?: number;
  itemId: number;
  count: number;
}

export interface MedalEnhanceLevel {
  id?: number;
  enhanceLevel: number;
  successRate: number;
  destroyOnFail: number;
  mesoCost: number;
  strAdd: number;
  dexAdd: number;
  intAdd: number;
  lukAdd: number;
  hpAdd: number;
  mpAdd: number;
  watkAdd: number;
  matkAdd: number;
  wdefAdd: number;
  mdefAdd: number;
  accAdd: number;
  avoidAdd: number;
  speedAdd: number;
  jumpAdd: number;
  costs: MedalEnhanceCost[];
}

export interface MedalEnhanceConfig {
  id?: number;
  maxEnhance: number;
  enabled: number;
  levels: MedalEnhanceLevel[];
}

export function getMedalEnhanceList() {
  return axios.get('/medalEnhance/v1/getConfigList');
}

export function getMedalEnhanceConfig(id: number) {
  return axios.get(`/medalEnhance/v1/getConfig/${id}`);
}

export function saveMedalEnhanceConfig(data: MedalEnhanceConfig) {
  return axios.post('/medalEnhance/v1/saveConfig', data);
}

export function deleteMedalEnhanceConfig(id: number) {
  return axios.delete(`/medalEnhance/v1/deleteConfig/${id}`);
}
