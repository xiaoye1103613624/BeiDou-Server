import axios from 'axios';
import type { NewbieGiftState } from '@/store/modules/newbieGift/type';

/** 物品奖励表单 */
export interface NewbieGiftItemForm {
  id?: number;
  itemId?: number;
  quantity?: number;
}

/** 货币奖励表单 */
export interface NewbieGiftCurrencyForm {
  id?: number;
  currencyType?: string;
  amount?: number;
}

/** 新手礼包配置表单 */
export interface NewbieGiftForm {
  id?: number;
  giftName?: string;
  minLevel?: number;
  maxLevel?: number;
  enabled?: number;
  items?: NewbieGiftItemForm[];
  currencies?: NewbieGiftCurrencyForm[];
}

/** 获取所有新手礼包配置列表 */
export function getConfigList() {
  return axios.get<NewbieGiftState[]>('/newbieGift/v1/getConfigList');
}

/** 获取单个新手礼包配置 */
export function getConfig(id: number) {
  return axios.get<NewbieGiftState>(`/newbieGift/v1/getConfig/${id}`);
}

/** 保存新手礼包配置（新增或更新） */
export function saveConfig(data: NewbieGiftForm) {
  return axios.post<NewbieGiftState>('/newbieGift/v1/saveConfig', data);
}

/** 删除新手礼包配置 */
export function deleteConfig(id: number) {
  return axios.delete(`/newbieGift/v1/deleteConfig/${id}`);
}
