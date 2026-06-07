import axios from 'axios';
import type { EquipEnhanceState } from '@/store/modules/equipEnhance/type';

/** 装备强化消耗物品表单 */
export interface EquipEnhanceCostForm {
  /** 主键ID（更新时必填） */
  id?: number;
  /** 消耗道具ID */
  itemId?: number;
  /** 消耗数量 */
  count?: number;
}

/** 装备强化等级配置表单（定义每级的属性加成和消耗） */
export interface EquipEnhanceLevelForm {
  /** 主键ID（更新时必填） */
  id?: number;
  /** 强化等级（1~N） */
  enhanceLevel?: number;
  /** 成功率（0~100） */
  successRate?: number;
  /** 失败是否销毁装备（0=保留 1=销毁） */
  destroyOnFail?: number;
  /** 金币消耗 */
  mesoCost?: number;
  /** 力量加成 */
  strAdd?: number;
  /** 敏捷加成 */
  dexAdd?: number;
  /** 智力加成 */
  intAdd?: number;
  /** 运气加成 */
  lukAdd?: number;
  /** HP加成 */
  hpAdd?: number;
  /** MP加成 */
  mpAdd?: number;
  /** 物理攻击加成 */
  watkAdd?: number;
  /** 魔法攻击加成 */
  matkAdd?: number;
  /** 物理防御加成 */
  wdefAdd?: number;
  /** 魔法防御加成 */
  mdefAdd?: number;
  /** 命中加成 */
  accAdd?: number;
  /** 回避加成 */
  avoidAdd?: number;
  /** 速度加成 */
  speedAdd?: number;
  /** 跳跃加成 */
  jumpAdd?: number;
  /** 该等级的消耗物品列表 */
  costs?: EquipEnhanceCostForm[];
}

/** 装备强化配置表单（主表） */
export interface EquipEnhanceForm {
  /** 配置ID（更新时必填） */
  id?: number;
  /** 可强化的装备物品ID */
  itemId?: number;
  /** 装备名称 */
  itemName?: string;
  /** 每角色仅限强化一件（0=不限 1=限制） */
  uniquePerChar?: number;
  /** 最大强化等级 */
  maxEnhance?: number;
  /** 是否启用（0=禁用 1=启用） */
  enabled?: number;
  /** 各等级配置 */
  levels?: EquipEnhanceLevelForm[];
}

/** 获取所有装备强化配置列表 */
export function getConfigList() {
  return axios.get<EquipEnhanceState[]>('/equipEnhance/v1/getConfigList');
}

/** 获取单个装备强化配置 */
export function getConfig(id: number) {
  return axios.get<EquipEnhanceState>(`/equipEnhance/v1/getConfig/${id}`);
}

/** 保存装备强化配置（新增或更新） */
export function saveConfig(data: EquipEnhanceForm) {
  return axios.post<EquipEnhanceState>('/equipEnhance/v1/saveConfig', data);
}

/** 删除装备强化配置（级联删除关联的等级和消耗物品） */
export function deleteConfig(id: number) {
  return axios.delete(`/equipEnhance/v1/deleteConfig/${id}`);
}
