import axios from 'axios';

export interface DailyCheckinReward {
  day: number;
  iconItemId?: number;
  mesos?: number;
  itemId?: number;
  itemQty?: number;
  expireDays?: number;
  item2Id?: number;
  item2Qty?: number;
  item2Expire?: number;
  slotType?: number;
  slotCount?: number;
  remark?: string;
}

export function getDailyCheckinList() {
  return axios.get('/dailyCheckin/v1/list');
}

export function saveDailyCheckin(data: DailyCheckinReward) {
  return axios.post('/dailyCheckin/v1/save', data);
}

export function saveAllDailyCheckin(data: DailyCheckinReward[]) {
  return axios.post('/dailyCheckin/v1/saveAll', data);
}

export function reloadDailyCheckin() {
  return axios.post('/dailyCheckin/v1/reload');
}
