import axios from 'axios';

export interface CarryItemStatRecord {
  id?: number;
  itemId: number;
  itemName?: string;
  statsJson?: string;
  requireEquipped?: number;
  enabled?: number;
  remark?: string;
}

export function getCarryItemStatList() {
  return axios.get('/carryItemStat/v1/list');
}

export function saveCarryItemStat(data: CarryItemStatRecord) {
  return axios.post('/carryItemStat/v1/save', data);
}

export function deleteCarryItemStat(id: number) {
  return axios.post('/carryItemStat/v1/delete', id);
}

export function reloadCarryItemStat() {
  return axios.post('/carryItemStat/v1/reload');
}
