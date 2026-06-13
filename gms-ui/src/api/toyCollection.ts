import axios from 'axios';

/** 玩具收集物品 */
export interface ToyCollectionItem {
  /** 主键ID（更新时必填） */
  id?: number;
  /** 所属分类ID */
  categoryId?: number;
  /** 收集物品ID */
  itemId?: number;
  /** 物品名称（服务端解析） */
  itemName?: string;
  /** 需要收集的数量 */
  requiredQuantity?: number;
  /** 奖励物品ID（0=无奖励） */
  rewardItemId?: number;
  /** 奖励物品名称（服务端解析） */
  rewardItemName?: string;
  /** 奖励物品数量 */
  rewardQuantity?: number;
  /** 排序序号 */
  sortOrder?: number;
  /** 是否启用（0=禁用 1=启用） */
  enabled?: number;
}

/** 玩具收集分类 */
export interface ToyCollectionCategory {
  /** 主键ID（更新时必填） */
  id?: number;
  /** 分类名称 */
  name?: string;
  /** 图标标识 */
  icon?: string;
  /** 排序序号 */
  sortOrder?: number;
  /** 是否启用（0=禁用 1=启用） */
  enabled?: number;
  /** 该分类下的收集物品列表 */
  items?: ToyCollectionItem[];
}

/** 玩具收集进度 */
export interface ToyCollectionProgress {
  /** 主键ID */
  id?: number;
  /** 角色ID */
  characterId?: number;
  /** 角色名称 */
  characterName?: string;
  /** 关联物品配置ID */
  itemConfigId?: number;
  /** 收集物品ID */
  itemId?: number;
  /** 需要收集的数量 */
  requiredQuantity?: number;
  /** 已提交数量 */
  submittedQuantity?: number;
  /** 奖励是否已领取 */
  rewardClaimed?: number;
}

// ==================== 分类接口 ====================

/** 获取所有分类列表（含物品列表） */
export function getCategoryList() {
  return axios.get<ToyCollectionCategory[]>('/toyCollection/v1/getCategoryList');
}

/** 获取单个分类配置 */
export function getCategory(id: number) {
  return axios.get<ToyCollectionCategory>(`/toyCollection/v1/getCategory/${id}`);
}

/** 保存分类配置（新增或更新，含物品列表） */
export function saveCategory(data: ToyCollectionCategory) {
  return axios.post<ToyCollectionCategory>('/toyCollection/v1/saveCategory', data);
}

/** 删除分类配置 */
export function deleteCategory(id: number) {
  return axios.delete(`/toyCollection/v1/deleteCategory/${id}`);
}

// ==================== 物品接口 ====================

/** 获取指定分类的物品列表 */
export function getItemList(categoryId: number) {
  return axios.get<ToyCollectionItem[]>('/toyCollection/v1/getItemList', {
    params: { categoryId },
  });
}

/** 保存单个物品配置 */
export function saveItem(data: ToyCollectionItem) {
  return axios.post<ToyCollectionItem>('/toyCollection/v1/saveItem', data);
}

/** 删除物品配置 */
export function deleteItem(id: number) {
  return axios.delete(`/toyCollection/v1/deleteItem/${id}`);
}

// ==================== 进度接口 ====================

/** 获取角色收集进度 */
export function getProgress(characterId: number, categoryId?: number) {
  return axios.get<ToyCollectionProgress[]>('/toyCollection/v1/getProgress', {
    params: { characterId, categoryId },
  });
}
