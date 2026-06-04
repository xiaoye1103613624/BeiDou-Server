import axios from 'axios';

// 预设难度
export function getPresets() {
  return axios.get('/monsterInvasion/v1/presets');
}

// 城镇列表
export function getTowns() {
  return axios.get('/monsterInvasion/v1/towns');
}

// 线路列表
export function getChannels(worldId: number) {
  return axios.get(`/monsterInvasion/v1/channels/${worldId}`);
}

// 搜索怪物
export function searchMobs(keyword: string) {
  return axios.post('/monsterInvasion/v1/searchMobs', { keyword });
}

// 启动攻城
export function startInvasion(data: any) {
  return axios.post('/monsterInvasion/v1/start', data);
}

// 取消攻城
export function cancelInvasion(worldId: number) {
  return axios.post('/monsterInvasion/v1/cancel', { worldId });
}

// 获取攻城状态
export function getStatus(worldId: number) {
  return axios.get(`/monsterInvasion/v1/status/${worldId}`);
}
