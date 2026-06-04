import axios from 'axios';

export interface EquipEnhanceCost {
  id?: number;
  itemId: number;
  count: number;
}

export interface EquipEnhanceLevel {
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
  costs: EquipEnhanceCost[];
}

export interface EquipEnhanceConfig {
  id?: number;
  itemId: number;
  itemName: string;
  uniquePerChar: number;
  maxEnhance: number;
  enabled: number;
  levels: EquipEnhanceLevel[];
}

export function getEquipEnhanceList() {
  return axios.get('/equipEnhance/v1/getConfigList');
}

export function getEquipEnhanceConfig(id: number) {
  return axios.get(`/equipEnhance/v1/getConfig/${id}`);
}

export function saveEquipEnhanceConfig(data: EquipEnhanceConfig) {
  return axios.post('/equipEnhance/v1/saveConfig', data);
}

export function deleteEquipEnhanceConfig(id: number) {
  return axios.delete(`/equipEnhance/v1/deleteConfig/${id}`);
}
