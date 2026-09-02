import axios from 'axios';

/** 炼药师配方表单 */
export interface AlchemistRecipeForm {
  /** 配方ID（更新时必填） */
  id?: number;
  /** 所需炼药品级（炼药师等级门槛） */
  tierRequired?: number;
  /** 产出物品ID */
  resultItemId?: number;
  /** 产出物品名称（后端解析，仅展示） */
  resultItemName?: string;
  /** 产出物品数量 */
  resultCount?: number;
  /** 炼药成功获得的经验 */
  expGain?: number;
  /** 消耗体力值 */
  staminaCost?: number;
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
  /** 材料4物品ID */
  material4ItemId?: number;
  /** 材料4数量 */
  material4Count?: number;
  /** 材料5物品ID */
  material5ItemId?: number;
  /** 材料5数量 */
  material5Count?: number;
  /** 列表显示顺序，越小越靠前 */
  sortOrder?: number;
  /** 是否启用（0=禁用 1=启用） */
  enabled?: number;
}

/** 获取所有炼药配方列表（含已禁用） */
export function getRecipeList() {
  return axios.get<AlchemistRecipeForm[]>('/alchemistRecipe/v1/getRecipeList');
}

/** 获取单个炼药配方详情 */
export function getRecipe(id: number) {
  return axios.get<AlchemistRecipeForm>(`/alchemistRecipe/v1/getRecipe/${id}`);
}

/** 保存炼药配方（新增或更新） */
export function saveRecipe(data: AlchemistRecipeForm) {
  return axios.post<AlchemistRecipeForm>(
    '/alchemistRecipe/v1/saveRecipe',
    data
  );
}

/** 切换炼药配方启用/禁用状态 */
export function toggleEnabled(id: number) {
  return axios.post(`/alchemistRecipe/v1/toggleEnabled/${id}`);
}

/** 删除炼药配方 */
export function deleteRecipe(id: number) {
  return axios.delete(`/alchemistRecipe/v1/deleteRecipe/${id}`);
}
