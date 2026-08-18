import axios from 'axios';

/** 副职业品级配置表单 */
export interface AlchemyTierForm {
  /** 品级ID（更新时必填） */
  id?: number;
  /** 副职业类型：1=炼金 2=炼药 3=锻造 */
  type?: number;
  /** 品级名称（如：入门、普通、职业、大师、宗师） */
  name?: string;
  /** 达到该品级所需的最低累计经验（经验阈值） */
  expStart?: number;
  /** 是否为最高品级（0=否 1=是，最高品级无上限） */
  isMax?: number;
  /** 品级显示顺序，越小品级越低 */
  sortOrder?: number;
  /** 是否启用：0=禁用 1=启用 */
  enabled?: number;
}

/** 获取某副职业所有品级列表（含已禁用） */
export function getTierList(type?: number) {
  return axios.get<AlchemyTierForm[]>('/alchemyTier/v1/getTierList', {
    params: { type },
  });
}

/** 保存品级（新增或更新） */
export function saveTier(data: AlchemyTierForm) {
  return axios.post<AlchemyTierForm>('/alchemyTier/v1/saveTier', data);
}

/** 切换品级启用/禁用状态 */
export function toggleEnabled(id: number) {
  return axios.post(`/alchemyTier/v1/toggleEnabled/${id}`);
}

/** 删除品级 */
export function deleteTier(id: number) {
  return axios.delete(`/alchemyTier/v1/deleteTier/${id}`);
}
