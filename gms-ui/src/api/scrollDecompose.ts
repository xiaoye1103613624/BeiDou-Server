import axios from 'axios';
import type {
  ScrollDecomposeConfigState,
  ScrollExchangeConfigState,
} from '@/store/modules/scrollDecompose/type';

// ==================== 分解配置 ====================

/** 获取卷轴分解配置列表（支持筛选） */
export function getDecomposeConfigList(params?: {
  scrollId?: number;
  enabled?: number;
}) {
  return axios.get<ScrollDecomposeConfigState[]>(
    '/scrollDecompose/v1/getDecomposeConfigList',
    { params }
  );
}

/** 获取单个卷轴分解配置 */
export function getDecomposeConfig(id: number) {
  return axios.get<ScrollDecomposeConfigState>(
    `/scrollDecompose/v1/getDecomposeConfig/${id}`
  );
}

/** 保存卷轴分解配置（新增或更新） */
export function saveDecomposeConfig(data: ScrollDecomposeConfigState) {
  return axios.post<ScrollDecomposeConfigState>(
    '/scrollDecompose/v1/saveDecomposeConfig',
    { data }
  );
}

/** 删除卷轴分解配置 */
export function deleteDecomposeConfig(id: number) {
  return axios.delete(`/scrollDecompose/v1/deleteDecomposeConfig/${id}`);
}

/** 批量删除卷轴分解配置 */
export function deleteDecomposeConfigBatch(ids: number[]) {
  return axios.post('/scrollDecompose/v1/deleteDecomposeConfigBatch', {
    data: ids,
  });
}

// ==================== 兑换配置 ====================

/** 获取卷轴兑换配置列表（支持筛选） */
export function getExchangeConfigList(params?: {
  scrollId?: number;
  enabled?: number;
}) {
  return axios.get<ScrollExchangeConfigState[]>(
    '/scrollDecompose/v1/getExchangeConfigList',
    { params }
  );
}

/** 获取单个卷轴兑换配置 */
export function getExchangeConfig(id: number) {
  return axios.get<ScrollExchangeConfigState>(
    `/scrollDecompose/v1/getExchangeConfig/${id}`
  );
}

/** 保存卷轴兑换配置（新增或更新） */
export function saveExchangeConfig(data: ScrollExchangeConfigState) {
  return axios.post<ScrollExchangeConfigState>(
    '/scrollDecompose/v1/saveExchangeConfig',
    { data }
  );
}

/** 删除卷轴兑换配置 */
export function deleteExchangeConfig(id: number) {
  return axios.delete(`/scrollDecompose/v1/deleteExchangeConfig/${id}`);
}

/** 批量删除卷轴兑换配置 */
export function deleteExchangeConfigBatch(ids: number[]) {
  return axios.post('/scrollDecompose/v1/deleteExchangeConfigBatch', {
    data: ids,
  });
}
