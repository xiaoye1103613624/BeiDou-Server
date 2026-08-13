import axios from 'axios';

/** 操作日志查询条件 */
export interface OpLogSearchDTO {
  pageNo: number;
  pageSize: number;
  opType?: number;
  characterName?: string;
  accountId?: number;
  ip?: string;
  startTime?: string;
  endTime?: string;
}

/** 操作类型样式绑定 */
export interface OpLogTypeDO {
  id?: number;
  opType: number;
  name: string;
  noticeTag: string;
  chatType: number;
  broadcast: boolean;
  enabled: boolean;
  sortOrder: number;
  remark: string;
  createTime?: string;
  updateTime?: string;
}

/** 操作日志记录 */
export interface OpLogDO {
  id: number;
  opType: number;
  opTypeName: string;
  characterId: number;
  characterName: string;
  accountId: number;
  summary: string;
  detail: string;
  chatType: number;
  broadcast: boolean;
  ip: string;
  worldChannel: string;
  createTime: string;
}

export interface Page<T> {
  records: T[];
  pageNumber: number;
  pageSize: number;
  totalRow: number;
}

export function pageOpLogs(data: OpLogSearchDTO) {
  return axios.post<Page<OpLogDO>>('/opLog/v1/page', data);
}

export function getTypeList() {
  return axios.get<OpLogTypeDO[]>('/opLog/v1/typeList');
}

export function getChatStyles() {
  return axios.get<Record<number, string>>('/opLog/v1/chatStyles');
}

export function saveType(data: OpLogTypeDO) {
  return axios.post('/opLog/v1/saveType', data);
}

export function deleteType(id: number) {
  return axios.delete(`/opLog/v1/deleteType/${id}`);
}

export function reloadTypes() {
  return axios.post('/opLog/v1/reload');
}