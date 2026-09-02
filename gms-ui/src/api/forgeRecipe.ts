import axios from 'axios';

/** 打造配方表单 */
export interface ForgeRecipeForm {
  /** 配方ID（更新时必填） */
  id?: number;
  /** 配方名称 */
  name?: string;
  /** 所需锻造品级（锻造师等级门槛） */
  tierRequired?: number;
  /** 产出装备物品ID */
  resultItemId?: number;
  /** 打造成功获得的经验 */
  expGain?: number;
  /** 消耗金币 */
  mesoCost?: number;
  /** 材料1物品ID */
  material1ItemId?: number;
  /** 材料1数量 */
  material1Count?: number;
  /** 材料2物品ID */
  material2ItemId?: number;
  /** 材料2数量 */
  material2Count?: number;
  /** 材料3物品ID */
  material3ItemId?: number;
  /** 材料3数量 */
  material3Count?: number;
  /** 产出装备力量下限 */
  strMin?: number;
  /** 产出装备力量上限 */
  strMax?: number;
  /** 产出装备敏捷下限 */
  dexMin?: number;
  /** 产出装备敏捷上限 */
  dexMax?: number;
  /** 产出装备智力下限 */
  intMin?: number;
  /** 产出装备智力上限 */
  intMax?: number;
  /** 产出装备运气下限 */
  lukMin?: number;
  /** 产出装备运气上限 */
  lukMax?: number;
  /** 产出装备物攻下限 */
  watkMin?: number;
  /** 产出装备物攻上限 */
  watkMax?: number;
  /** 产出装备魔攻下限 */
  matkMin?: number;
  /** 产出装备魔攻上限 */
  matkMax?: number;
  /** 列表显示顺序，越小越靠前 */
  sortOrder?: number;
  /** 是否启用（0=禁用 1=启用） */
  enabled?: number;
}

/** 获取所有打造配方列表（含已禁用） */
export function getRecipeList() {
  return axios.get<ForgeRecipeForm[]>('/forgeRecipe/v1/getRecipeList');
}

/** 获取单个打造配方详情 */
export function getRecipe(id: number) {
  return axios.get<ForgeRecipeForm>(`/forgeRecipe/v1/getRecipe/${id}`);
}

/** 保存打造配方（新增或更新） */
export function saveRecipe(data: ForgeRecipeForm) {
  return axios.post<ForgeRecipeForm>('/forgeRecipe/v1/saveRecipe', data);
}

/** 切换打造配方启用/禁用状态 */
export function toggleEnabled(id: number) {
  return axios.post(`/forgeRecipe/v1/toggleEnabled/${id}`);
}

/** 删除打造配方 */
export function deleteRecipe(id: number) {
  return axios.delete(`/forgeRecipe/v1/deleteRecipe/${id}`);
}
