import axios from 'axios';

// ==================== 类型定义 ====================

/** CDK道具奖励 */
export interface CdkItemForm {
  id?: number;
  itemId?: number;
  itemName?: string; // 服务端填充，前端只读
  quantity?: number;
}

/** CDK配置 */
export interface CdkConfigForm {
  id?: number;
  code?: string;
  batchNo?: string;
  type?: number;
  nxCredit?: number;
  nxPrepaid?: number;
  meso?: number;
  sponsor?: number;
  maxUseCount?: number;
  usedCount?: number;
  expireTime?: string;
  enabled?: number;
  comment?: string;
  items?: CdkItemForm[];
}

/** CDK批量生成请求 */
export interface CdkBatchGenForm {
  count: number;
  length: number;
  prefix?: string;
  type?: number;
  nxCredit?: number;
  nxPrepaid?: number;
  meso?: number;
  sponsor?: number;
  maxUseCount?: number;
  expireTime?: string;
  enabled?: number;
  comment?: string;
  items?: CdkItemForm[];
}

/** CDK批量生成返回 */
export interface CdkBatchGenResult {
  batchNo: string;
  totalCount: number;
  codeList: string[];
}

/** 兑换日志 */
export interface CdkLogForm {
  id: number;
  cdkId?: number;
  code: string;
  playerName?: string;
  playerId?: number;
  accountName?: string;
  accountId?: number;
  ip?: string;
  result: number;
  resultMsg?: string;
  detail?: string;
  createTime: string;
}

/** 兑换请求 */
export interface CdkRedeemForm {
  code: string;
  playerId?: number;
}

/** 兑换结果 */
export interface CdkRedeemResult {
  success: boolean;
  message: string;
  detailJson?: string;
}

// ==================== API 函数 ====================

/** 获取CDK配置列表 */
export function getCdkList(params: {
  keyword?: string;
  type?: number;
  enabled?: number;
}) {
  return axios.get('/cdk/v1/list', { params });
}

/** 获取单个CDK配置 */
export function getCdkConfig(id: number) {
  return axios.get(`/cdk/v1/getConfig/${id}`);
}

/** 保存CDK配置（新增/更新） */
export function saveCdkConfig(data: CdkConfigForm) {
  return axios.post('/cdk/v1/saveConfig', data);
}

/** 删除CDK配置 */
export function deleteCdkConfig(id: number) {
  return axios.delete(`/cdk/v1/deleteConfig/${id}`);
}

/** 批量生成CDK */
export function batchGenerateCdk(data: CdkBatchGenForm) {
  return axios.post('/cdk/v1/batchGenerate', data);
}

/** 兑换CDK */
export function redeemCdk(data: CdkRedeemForm) {
  return axios.post('/cdk/v1/redeem', data);
}

/** 查询兑换日志 */
export function queryCdkLogs(params: {
  playerName?: string;
  code?: string;
  ip?: string;
  result?: number;
  startTime?: string;
  endTime?: string;
}) {
  return axios.get('/cdk/v1/logs', { params });
}
