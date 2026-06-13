/** 新手礼包物品奖励 */
export interface NewbieGiftItemState {
  id?: number;
  itemId?: number;
  quantity?: number;
}

/** 新手礼包货币奖励 */
export interface NewbieGiftCurrencyState {
  id?: number;
  currencyType?: string;
  amount?: number;
}

/** 新手礼包配置 */
export interface NewbieGiftState {
  id?: number;
  giftName?: string;
  minLevel?: number;
  maxLevel?: number;
  enabled?: number;
  items?: NewbieGiftItemState[];
  currencies?: NewbieGiftCurrencyState[];
}
