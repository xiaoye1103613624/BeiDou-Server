import axios from 'axios';

export interface EnhanceRuleRecord {
  id?: number;
  ruleName: string;
  equipType?: string;
  minLevel?: number;
  maxLevel?: number;
  statsJson?: string;
  enabled?: number;
  sortOrder?: number;
  remark?: string;
}

export function getEnhanceRuleList() {
  return axios.get('/enhanceRule/v1/list');
}

export function saveEnhanceRule(data: EnhanceRuleRecord) {
  return axios.post('/enhanceRule/v1/save', data);
}

export function deleteEnhanceRule(id: number) {
  return axios.post('/enhanceRule/v1/delete', id);
}

export function reloadEnhanceRule() {
  return axios.post('/enhanceRule/v1/reload');
}
