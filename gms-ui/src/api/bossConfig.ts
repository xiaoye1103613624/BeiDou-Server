import axios from 'axios';
import { PageState } from '@/store/page';

export interface BossConfigSearch {
  mobId?: number;
  bossName?: string;
  pageNo: number;
  pageSize: number;
}

export interface BossConfigItem {
  id?: number;
  mobId: number;
  bossName: string;
  hpMultiplier: number;
  expMultiplier: number;
  damageMultiplier: number;
  // 直接覆盖值（null=使用WZ默认）
  level?: number | null;
  hp?: number | null;
  mp?: number | null;
  exp?: number | null;
  pdd?: number | null;
  mdd?: number | null;
  acc?: number | null;
  eva?: number | null;
  // WZ基础属性（只读展示）
  wzLevel?: number;
  wzHp?: number;
  wzMp?: number;
  wzExp?: number;
  wzPdd?: number;
  wzMdd?: number;
  wzAcc?: number;
  wzEva?: number;
  wzPadamage?: number;
  wzMadamage?: number;
  wzBoss?: boolean;
  enabled: number;
  createTime?: string;
  updateTime?: string;
}

export function getBossConfigList(data: BossConfigSearch) {
  return axios.post<PageState<BossConfigItem>>(
    '/bossConfig/v1/getBossConfigList',
    data
  );
}

export function addBossConfig(data: BossConfigItem) {
  return axios.post('/bossConfig/v1/addBossConfig', data);
}

export function updateBossConfig(data: BossConfigItem) {
  return axios.post('/bossConfig/v1/updateBossConfig', data);
}

export function deleteBossConfig(id: number) {
  return axios.delete(`/bossConfig/v1/deleteBossConfig/${id}`);
}

export function deleteBossConfigList(ids: number[]) {
  return axios.post('/bossConfig/v1/deleteBossConfigList', ids);
}
