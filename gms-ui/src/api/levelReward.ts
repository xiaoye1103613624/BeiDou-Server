import axios from 'axios';

/** 等级奖励道具表单 */
export interface LevelRewardItemForm {
  /** 主键ID（更新时必填） */
  id?: number;
  /** 道具ID */
  itemId?: number;
  /** 发放数量 */
  count?: number;
}

/** 等级奖励配置表单 */
export interface LevelRewardForm {
  /** 配置ID（更新时必填） */
  id?: number;
  /** 要求等级 */
  level?: number;
  /** 金币奖励 */
  meso?: number;
  /** 点卷（NX_CREDIT=1） */
  nxCredit?: number;
  /** 抵用券（MAPLE_POINT=2） */
  maplePoint?: number;
  /** 信用券（NX_PREPAID=4） */
  nxPrepaid?: number;
  /** 是否启用（0=禁用 1=启用） */
  enabled?: number;
  /** 道具奖励列表 */
  items?: LevelRewardItemForm[];
}

/** 获取所有等级奖励配置列表 */
export function getRewardList() {
  return axios.get<LevelRewardForm[]>('/levelReward/v1/getConfigList');
}

/** 获取单个等级奖励配置 */
export function getReward(id: number) {
  return axios.get<LevelRewardForm>(`/levelReward/v1/getConfig/${id}`);
}

/** 保存等级奖励配置（新增或更新） */
export function saveReward(data: LevelRewardForm) {
  return axios.post<LevelRewardForm>('/levelReward/v1/saveConfig', data);
}

/** 删除等级奖励配置（级联删除关联道具） */
export function deleteReward(id: number) {
  return axios.delete(`/levelReward/v1/deleteConfig/${id}`);
}
