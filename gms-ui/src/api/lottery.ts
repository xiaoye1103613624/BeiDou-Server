import axios from 'axios';

export interface LotteryMachine {
  id?: number;
  npcId?: number;
  name?: string;
  comment?: string;
  enabled?: number;
  multiDraws?: string;
  costType?: string;
  costItemId?: number | null;
  costAmount?: number;
  updatedAt?: string;
}

export interface LotteryItem {
  id?: number;
  npcId?: number;
  itemId?: number;
  quantity?: number;
  weight?: number;
  announce?: number;
  announceChannel?: number;
  announceBanner?: number;
  announceLabel?: string;
  randomStats?: number;
  untradeable?: number;
  accountBound?: number;
  uniqueEquip?: number;
  enabled?: number;
  fromComment?: number;
  itemValid?: number;
  itemType?: number;
  sortOrder?: number;
  itemName?: string;
}

export function getMachines() {
  return axios.get('/lottery/v1/getMachines');
}

export function saveMachine(data: LotteryMachine) {
  return axios.post('/lottery/v1/saveMachine', data);
}

export function deleteMachine(npcId: number) {
  return axios.delete(`/lottery/v1/deleteMachine/${npcId}`);
}

export function getItems(npcId: number) {
  return axios.get(`/lottery/v1/getItems/${npcId}`);
}

export function saveItem(data: LotteryItem) {
  return axios.post('/lottery/v1/saveItem', data);
}

export function deleteItem(id: number) {
  return axios.delete(`/lottery/v1/deleteItem/${id}`);
}

export function findNpcsByItem(itemId: number) {
  return axios.get(`/lottery/v1/findNpcsByItem/${itemId}`);
}

export function reloadAll() {
  return axios.post('/lottery/v1/reloadAll', {});
}

export function reloadNpc(npcId: number) {
  return axios.post(`/lottery/v1/reloadNpc/${npcId}`, {});
}

/** 导入奖池脚本，默认 9310022_123.js；可传 script / path */
export function importPool(data: {
  npcId?: number;
  replace?: boolean;
  script?: string;
  path?: string;
}) {
  return axios.post('/lottery/v1/importPool', data);
}

/** 金猪默认：导入 9310022_123.js（替换该 NPC 奖品） */
export function import123(data: {
  npcId?: number;
  replace?: boolean;
  path?: string;
}) {
  return axios.post('/lottery/v1/import123', {
    ...data,
    script: '9310022_123.js',
  });
}

/** @deprecated 使用 import123 / importPool */
export function import303(data: {
  npcId?: number;
  replace?: boolean;
  path?: string;
}) {
  return axios.post('/lottery/v1/import303', data);
}

export function detectItemType(itemId: number) {
  return axios.get(`/lottery/v1/detectItemType/${itemId}`);
}
