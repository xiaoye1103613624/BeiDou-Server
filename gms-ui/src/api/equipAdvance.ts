import axios from 'axios';
import type { EquipAdvanceState } from '@/store/modules/equipAdvance/type';

/** 装备进阶消耗材料表单 */
export interface EquipAdvanceCostForm {
  /** 主键ID（更新时必填） */
  id?: number;
  /** 消耗道具ID */
  itemId?: number;
  /** 消耗数量 */
  count?: number;
}

/** 装备进阶阶段配置表单（定义每个阶段的目标装备和属性加成） */
export interface EquipAdvanceStageForm {
  /** 主键ID（更新时必填） */
  id?: number;
  /** 阶段顺序（0=初始装备，1=一阶，2=二阶...） */
  stageOrder?: number;
  /** 该阶段目标装备ID */
  targetItemId?: number;
  /** 目标装备名称 */
  targetItemName?: string;
  /** 金币消耗 */
  mesoCost?: number;
  /** 点卷消耗 */
  cashCost?: number;
  /** 抵用券消耗 */
  creditCost?: number;
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
  /** 该阶段的消耗材料列表 */
  costs?: EquipAdvanceCostForm[];
}

/** 装备进阶路线配置表单（主表，按职业群划分） */
export interface EquipAdvanceForm {
  /** 路线ID（更新时必填） */
  id?: number;
  /** 职业群（warrior/archer/mage/thief/pirate） */
  jobGroup?: string;
  /** 路线名称 */
  routeName?: string;
  /** 是否启用（0=禁用 1=启用） */
  enabled?: number;
  /** 各阶段配置 */
  stages?: EquipAdvanceStageForm[];
}

const BASE = '/equipAdvance/v1';

/** 获取所有装备进阶路线列表 */
export function getRouteList() {
  return axios.get<EquipAdvanceState[]>(`${BASE}/getRouteList`);
}

/** 获取单个装备进阶路线 */
export function getRoute(id: number) {
  return axios.get<EquipAdvanceState>(`${BASE}/getRoute/${id}`);
}

/** 保存装备进阶路线（新增或更新） */
export function saveRoute(data: EquipAdvanceForm) {
  return axios.post<EquipAdvanceState>(`${BASE}/saveRoute`, data);
}

/** 删除装备进阶路线（级联删除关联的阶段和消耗材料） */
export function deleteRoute(id: number) {
  return axios.delete(`${BASE}/deleteRoute/${id}`);
}
