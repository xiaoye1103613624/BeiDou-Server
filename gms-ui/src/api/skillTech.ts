import axios from 'axios';

export interface SkillTechRecord {
  id?: number;
  skillId: number;
  skillName?: string;
  spMaxLevel: number;
  effectMaxLevel: number;
  levelsJson?: string;
  enabled?: number;
  clientSynced?: number;
  remark?: string;
}

export function getSkillTechList() {
  return axios.get('/skillTech/v1/list');
}

export function previewSkillTech(skillId: number) {
  return axios.get('/skillTech/v1/preview', { params: { skillId } });
}

export function saveSkillTech(data: SkillTechRecord) {
  return axios.post('/skillTech/v1/save', data);
}

export function deleteSkillTech(id: number) {
  return axios.post('/skillTech/v1/delete', id);
}

export function reloadSkillTech() {
  return axios.post('/skillTech/v1/reload');
}

export function syncSkillTechClient(skillId: number) {
  return axios.post('/skillTech/v1/syncClient', skillId);
}
