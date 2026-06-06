import axios from 'axios';

export interface ItemSearchResult {
  type: string;
  id: number;
  name: string;
  desc: string;
}

/**
 * 搜索物品 — 通过 /common/v1/informationSearch 接口（已验证可用）。
 * 查询所有物品类型：cash, consume, eqp, etc, ins, pet。
 */
export function itemSearch(keyword: string) {
  return axios.post('/common/v1/informationSearch', {
    types: ['cash', 'consume', 'eqp', 'etc', 'ins', 'pet'],
    filter: keyword,
  });
}
