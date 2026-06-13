/** 仓库物品配置（白名单） */
export interface WarehouseConfigState {
  /** 配置ID（更新时必填） */
  id?: number;
  /** 物品ID */
  itemId?: number;
  /** 物品名称（可为空） */
  itemName?: string;
  /** 物品栏类型(1=装备 2=消耗 3=设置 4=其他 5=现金) */
  inventoryType?: number;
  /** 物品掉落地图ID（0=未知，用于脚本传送） */
  dropMapId?: number;
  /** 是否启用(0=禁用 1=启用) */
  enabled?: number;
  /** 排序号（升序，越小越靠前，默认200） */
  sortOrder?: number;
}

/** 仓库物品信息 */
export interface WarehouseItemState {
  /** 仓库物品ID */
  id?: number;
  /** 账号ID */
  accountId?: number;
  /** 存入角色ID */
  characterId?: number;
  /** 物品ID */
  itemId?: number;
  /** 物品名称（WZ解析） */
  itemName?: string;
  /** 物品栏类型(1=装备 2=消耗 3=设置 4=其他 5=现金) */
  inventoryType?: number;
  /** 存放数量 */
  quantity?: number;
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
}

/** 仓库物品存取操作请求 */
export interface WarehouseOperateState {
  /** 账号ID */
  accountId?: number;
  /** 角色ID */
  characterId?: number;
  /** 物品ID */
  itemId?: number;
  /** 物品栏类型 */
  inventoryType?: number;
  /** 操作数量 */
  quantity?: number;
}

/** 仓库查询条件 */
export interface WarehouseQueryState {
  /** 账号ID */
  accountId?: number;
  /** 角色ID（非共享模式时必填） */
  characterId?: number;
  /** 物品栏类型（null=全部） */
  inventoryType?: number;
}

/** 仓库游戏参数 */
export interface WarehouseGameParamsState {
  /** 是否账号共享 */
  accountShared?: boolean;
  /** 最大堆叠数量 */
  maxStack?: number;
}
