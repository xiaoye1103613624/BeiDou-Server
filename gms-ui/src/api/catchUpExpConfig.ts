import axios from 'axios';
import { PageState } from '@/store/page';

export interface CatchUpExpConfigSearch {
  levelMin?: number;
  levelMax?: number;
  pageNo: number;
  pageSize: number;
}

export interface CatchUpExpConfigItem {
  id?: number;
  levelMin: number;
  levelMax: number;
  expMultiplier: number;
  enabled: number;
  createTime?: string;
  updateTime?: string;
}

// POST /catchUpExpConfig/v1/getConfigList
export function getCatchUpExpConfigList(data: CatchUpExpConfigSearch) {
  return axios.post<PageState<CatchUpExpConfigItem>>(
    '/catchUpExpConfig/v1/getConfigList',
    data
  );
}

// POST /catchUpExpConfig/v1/addConfig
export function addCatchUpExpConfig(data: CatchUpExpConfigItem) {
  return axios.post('/catchUpExpConfig/v1/addConfig', data);
}

// POST /catchUpExpConfig/v1/updateConfig
export function updateCatchUpExpConfig(data: CatchUpExpConfigItem) {
  return axios.post('/catchUpExpConfig/v1/updateConfig', data);
}

// DELETE /catchUpExpConfig/v1/deleteConfig/{id}
export function deleteCatchUpExpConfig(id: number) {
  return axios.delete(`/catchUpExpConfig/v1/deleteConfig/${id}`);
}

// POST /catchUpExpConfig/v1/deleteConfigList
export function deleteCatchUpExpConfigList(ids: number[]) {
  return axios.post('/catchUpExpConfig/v1/deleteConfigList', ids);
}
