import axios from 'axios';

/** 装备伤害加成配置表单（单件装备） */
export interface EquipDamageBonusForm {
  /** 配置ID（更新时必填） */
  id?: number;
  /** 装备物品ID */
  itemId?: number;
  /** 装备名称 */
  itemName?: string;
  /** 普通伤害加成百分比（如10表示+10%） */
  damagePct?: number;
  /** Boss伤害加成百分比（仅对Boss类怪物生效） */
  bossDamagePct?: number;
  /** 固定段伤（加法叠加的固定伤害值） */
  fixedDamage?: number;
  /** 是否启用（0=禁用 1=启用） */
  enabled?: number;
}

/** 获取所有装备伤害加成配置列表 */
export function getConfigList() {
  return axios.get<EquipDamageBonusForm[]>(
    '/equipDamageBonus/v1/getConfigList'
  );
}

/** 获取单个装备伤害加成配置 */
export function getConfig(id: number) {
  return axios.get<EquipDamageBonusForm>(
    `/equipDamageBonus/v1/getConfig/${id}`
  );
}

/** 保存装备伤害加成配置（新增或更新） */
export function saveConfig(data: EquipDamageBonusForm) {
  return axios.post<EquipDamageBonusForm>(
    '/equipDamageBonus/v1/saveConfig',
    data
  );
}

/** 切换装备伤害加成配置的启用/禁用状态 */
export function toggleEnabled(id: number) {
  return axios.post(`/equipDamageBonus/v1/toggleEnabled/${id}`);
}

/** 删除装备伤害加成配置 */
export function deleteConfig(id: number) {
  return axios.delete(`/equipDamageBonus/v1/deleteConfig/${id}`);
}
