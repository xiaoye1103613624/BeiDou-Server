/** 卷轴分解配置（白名单） */
export interface ScrollDecomposeConfigState {
  /** 配置ID（更新时必填） */
  id?: number;
  /** 卷轴物品ID */
  scrollId?: number;
  /** 卷轴名称（可为空，WZ自动识别） */
  scrollName?: string;
  /** 是否启用(0=禁用 1=启用) */
  enabled?: number;
  /** 排序号（升序，越小越靠前，默认200） */
  sortOrder?: number;
}

/** 卷轴兑换配置（碎片→卷轴） */
export interface ScrollExchangeConfigState {
  /** 配置ID（更新时必填） */
  id?: number;
  /** 卷轴物品ID */
  scrollId?: number;
  /** 卷轴名称（可为空，WZ自动识别） */
  scrollName?: string;
  /** 兑换所需碎片数量 */
  cost?: number;
  /** 是否启用(0=禁用 1=启用) */
  enabled?: number;
  /** 排序号（升序，越小越靠前，默认200） */
  sortOrder?: number;
}
