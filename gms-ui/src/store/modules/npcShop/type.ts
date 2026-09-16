export interface NpcShopState {
  shopId?: number;
  npcId?: number;
  npcName?: string;
}

export interface NpcShopItemState {
  id?: number;
  shopId?: number;
  npcId?: number;
  npcName?: string;
  itemId?: number;
  price?: number;
  pitch?: number;
  position?: number;
  itemName?: string;
  itemDesc?: string;
  rowKey?: string | number;
}
