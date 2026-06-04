import axios from 'axios';

export interface EliteBossConditionState {
  bossId?: number;
  bossName?: string;
  enabled?: number;
  pageNo?: number;
  pageSize?: number;
  onlyTotal?: boolean;
  notPage?: boolean;
}

export interface EliteBossConfigState {
  id?: number;
  mapId?: number;
  bossId?: number;
  bossName?: string;
  bossTime?: number;
  scriptName?: string;
  enabled?: number;
}

export interface EliteBossSpawnState {
  configId?: number;
  worldIds?: number[];
  channelIds?: number[];
  count?: number;
  spawnCompanion?: boolean;
}

export function getEliteBossList(data: EliteBossConditionState) {
  return axios.post('/eliteBoss/v1/getEliteBossList', data);
}

export function insertEliteBossConfig(data: EliteBossConfigState) {
  return axios.put('/eliteBoss/v1/addEliteBossConfig', data);
}

export function updateEliteBossConfig(data: EliteBossConfigState) {
  return axios.post('/eliteBoss/v1/updateEliteBossConfig', data);
}

export function deleteEliteBossConfig(id: number) {
  return axios.delete(`/eliteBoss/v1/deleteEliteBossConfig/${id}`);
}

export function spawnEliteBoss(data: EliteBossSpawnState) {
  return axios.post('/eliteBoss/v1/spawnEliteBoss', data);
}

export function killEliteBoss(data: EliteBossSpawnState) {
  return axios.post('/eliteBoss/v1/killEliteBoss', data);
}

export function getWorldChannels() {
  return axios.get('/eliteBoss/v1/worldChannels');
}
