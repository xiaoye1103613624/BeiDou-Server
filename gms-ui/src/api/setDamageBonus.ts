import axios from 'axios';

/** 套装伤害加成配置表单（套装ID + 件数档位） */
export interface SetDamageBonusForm {
  /** 配置ID（更新时必填） */
  id?: number;
  /** 套装ID(对应Etc.wz/SetItemInfo.img节点ID) */
  setItemId?: number;
  /** 套装名称 */
  setName?: string;
  /** 生效所需穿戴件数档位 */
  tierCount?: number;
  /** 普通伤害加成百分比（如10表示+10%） */
  damagePct?: number;
  /** Boss伤害加成百分比（仅对Boss类怪物生效） */
  bossDamagePct?: number;
  /** 是否启用（0=禁用 1=启用） */
  enabled?: number;
}

/** 获取所有套装伤害加成配置列表 */
export function getConfigList() {
  return axios.get<SetDamageBonusForm[]>('/setDamageBonus/v1/getConfigList');
}

/** 获取单个套装伤害加成配置 */
export function getConfig(id: number) {
  return axios.get<SetDamageBonusForm>(`/setDamageBonus/v1/getConfig/${id}`);
}

/** 保存套装伤害加成配置（新增或更新） */
export function saveConfig(data: SetDamageBonusForm) {
  return axios.post<SetDamageBonusForm>('/setDamageBonus/v1/saveConfig', data);
}

/** 切换套装伤害加成配置的启用/禁用状态 */
export function toggleEnabled(id: number) {
  return axios.post(`/setDamageBonus/v1/toggleEnabled/${id}`);
}

/** 删除套装伤害加成配置 */
export function deleteConfig(id: number) {
  return axios.delete(`/setDamageBonus/v1/deleteConfig/${id}`);
}
