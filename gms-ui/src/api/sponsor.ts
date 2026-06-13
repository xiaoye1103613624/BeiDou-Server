import axios from 'axios';

// ==================== 类型定义 ====================

/** 赞助奖励项 */
export interface SponsorRewardItem {
  type: 'item' | 'nx' | 'meso';
  id?: number;
  qty: number;
}

/** 赞助配置 */
export interface SponsorConfigForm {
  id?: number;
  name?: string;
  amount?: number;
  rewards?: SponsorRewardItem[];
  enabled?: number;
  comment?: string;
  createTime?: string;
}

/** 赞助记录 */
export interface SponsorRecord {
  id: number;
  playerId: number;
  playerName: string;
  accountId: number;
  accountName: string;
  totalSponsor: number;
  createTime: string;
  updateTime: string;
}

/** 赞助日志 */
export interface SponsorLog {
  id: number;
  playerId: number;
  playerName: string;
  accountId: number;
  type: number;       // 1=CDK兑换 2=管理员添加
  amount: number;
  detail: string;
  createTime: string;
}

// ==================== API 函数 ====================

/** 获取赞助配置列表 */
export function getSponsorConfigs() {
  return axios.get('/sponsor/v1/configs');
}

/** 保存赞助配置 */
export function saveSponsorConfig(data: SponsorConfigForm) {
  return axios.post('/sponsor/v1/saveConfig', data);
}

/** 删除赞助配置 */
export function deleteSponsorConfig(id: number) {
  return axios.delete(`/sponsor/v1/deleteConfig/${id}`);
}

/** 查询赞助记录 */
export function getSponsorRecords(params: { playerName?: string }) {
  return axios.get('/sponsor/v1/records', { params });
}

/** 查询赞助日志 */
export function getSponsorLogs(params: {
  playerName?: string;
  type?: number;
  startTime?: string;
  endTime?: string;
}) {
  return axios.get('/sponsor/v1/logs', { params });
}
