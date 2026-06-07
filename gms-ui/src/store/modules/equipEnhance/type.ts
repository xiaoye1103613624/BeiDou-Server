/** 装备强化消耗物品 */
export interface EquipEnhanceCostState {
  /** 主键ID */
  id?: number;
  /** 关联的等级ID */
  levelId?: number;
  /** 消耗道具ID */
  itemId?: number;
  /** 消耗数量 */
  count?: number;
}

/** 装备强化等级配置（定义每级的属性加成和消耗） */
export interface EquipEnhanceLevelState {
  /** 主键ID */
  id?: number;
  /** 关联的配置ID */
  configId?: number;
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
  costs?: EquipEnhanceCostState[];
}

/** 装备强化配置（主表，关联装备ID与强化规则） */
export interface EquipEnhanceState {
  /** 主键ID */
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
  levels?: EquipEnhanceLevelState[];
}

/** 装备强化列表查询条件 */
export interface EquipEnhanceFilter {
  /** 按物品ID筛选 */
  itemId?: number;
  /** 按装备名称筛选 */
  itemName?: string;
}
