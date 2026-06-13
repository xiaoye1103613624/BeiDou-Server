import axios from 'axios';

export interface InformationSearch {
  types: [];
  filter: string;
}

export interface InformationResult {
  type: string;
  id: number;
  name: string;
  desc: string;
}

export function informationSearch(condition: InformationSearch) {
  return axios.post('/common/v1/informationSearch', condition);
}

/** 物品详情请求 */
export interface ItemDetailReq {
  itemId: number;
  type: string;
}

/** 物品详情返回（包含属性、价格、穿戴要求等完整信息） */
export interface ItemDetailRtn {
  // 基本信息
  itemId: number;
  name: string;
  desc: string;
  type: string;
  // 通用属性
  unitPrice: number | null;
  wholePrice: number | null;
  slotMax: number | null;
  cashItem: boolean | null;
  // 限制标记
  questItem: boolean | null;
  untradeable: boolean | null;
  accountRestricted: boolean | null;
  dropRestricted: boolean | null;
  // 装备扩展属性
  str: number | null;
  dex: number | null;
  int: number | null;
  luk: number | null;
  hp: number | null;
  mp: number | null;
  pAtk: number | null;
  mAtk: number | null;
  pDef: number | null;
  mDef: number | null;
  acc: number | null;
  avoid: number | null;
  hands: number | null;
  speed: number | null;
  jump: number | null;
  upgradeSlots: number | null;
  // 装备穿戴要求
  reqLevel: number | null;
  reqStr: number | null;
  reqDex: number | null;
  reqInt: number | null;
  reqLuk: number | null;
  reqJob: number | null;
  // 装备额外属性
  equipCash: boolean | null;
  upgradeable: boolean | null;
}

/** 查询物品详细信息 */
export function getItemDetail(condition: ItemDetailReq) {
  return axios.post('/common/v1/getItemDetail', condition);
}
