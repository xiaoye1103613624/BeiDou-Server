import axios from 'axios';

export interface SponsorConfigRecord {
  id?: number;
  name: string;
  amount: number;
  enabled?: number;
  sortOrder?: number;
  createTime?: string;
  updateTime?: string;
}

/** 与服务端 SponsorEquipStats / gainEquip 字段对齐 */
export interface SponsorEquipStats {
  str?: number | null;
  dex?: number | null;
  int?: number | null;
  luk?: number | null;
  hp?: number | null;
  mp?: number | null;
  pAtk?: number | null;
  mAtk?: number | null;
  pDef?: number | null;
  mDef?: number | null;
  acc?: number | null;
  avoid?: number | null;
  hands?: number | null;
  speed?: number | null;
  jump?: number | null;
  upgradeSlot?: number | null;
}

export interface SponsorRewardRecord {
  id?: number;
  configId: number;
  type: string;
  itemId?: number;
  qty: number;
  /** default | custom；非装备可省略 */
  statMode?: string;
  /** 自定义属性 JSON 字符串 */
  statsJson?: string | null;
  /** skill_group：ONE | MULTI | ALL */
  pickMode?: string | null;
  createTime?: string;
}

export interface SponsorSkillOptionRecord {
  id?: number;
  rewardId: number;
  skillId: number;
  /** 0 = 最大等级 */
  skillLevel?: number;
  /** 0 = 自动空闲键 */
  defaultKey?: number;
  sortOrder?: number;
  createTime?: string;
}

export interface SkillInfoRecord {
  skillId: number;
  name: string;
  maxLevel: number;
  exists: boolean;
}

export function getSponsorConfigList() {
  return axios.get<SponsorConfigRecord[]>('/sponsor/v1/getConfigList');
}

export function saveSponsorConfig(data: SponsorConfigRecord) {
  return axios.post<SponsorConfigRecord>('/sponsor/v1/saveConfig', data);
}

export function toggleSponsorEnabled(id: number) {
  return axios.post(`/sponsor/v1/toggleEnabled/${id}`);
}

export function deleteSponsorConfig(id: number) {
  return axios.delete(`/sponsor/v1/deleteConfig/${id}`);
}

export function getSponsorRewards(configId: number) {
  return axios.get<SponsorRewardRecord[]>(`/sponsor/v1/getRewards/${configId}`);
}

export function saveSponsorReward(data: SponsorRewardRecord) {
  return axios.post<SponsorRewardRecord>('/sponsor/v1/saveReward', data);
}

export function deleteSponsorReward(id: number) {
  return axios.delete(`/sponsor/v1/deleteReward/${id}`);
}

export function getSponsorSkillOptions(rewardId: number) {
  return axios.get<SponsorSkillOptionRecord[]>(
    `/sponsor/v1/getSkillOptions/${rewardId}`
  );
}

export function saveSponsorSkillOption(data: SponsorSkillOptionRecord) {
  return axios.post<SponsorSkillOptionRecord>('/sponsor/v1/saveSkillOption', data);
}

export function deleteSponsorSkillOption(id: number) {
  return axios.delete(`/sponsor/v1/deleteSkillOption/${id}`);
}

export function getSkillInfo(skillId: number) {
  return axios.get<SkillInfoRecord>(`/sponsor/v1/getSkillInfo/${skillId}`);
}

export function isEquipItemId(itemId?: number | null): boolean {
  return !!itemId && itemId > 0 && itemId < 2000000;
}

/** 与表单/服务端 absolute 字段一致 */
export const SPONSOR_STAT_KEYS: (keyof SponsorEquipStats)[] = [
  'str',
  'dex',
  'int',
  'luk',
  'hp',
  'mp',
  'pAtk',
  'mAtk',
  'pDef',
  'mDef',
  'acc',
  'avoid',
  'hands',
  'speed',
  'jump',
  'upgradeSlot',
];

export function parseStatsJson(json?: string | null): SponsorEquipStats {
  if (!json) return {};
  try {
    return JSON.parse(json) as SponsorEquipStats;
  } catch {
    return {};
  }
}

/** custom 绝对值：缺省/空 → 0 */
export function normalizeStatsAbsolute(
  stats?: SponsorEquipStats | null
): SponsorEquipStats {
  const src = stats || {};
  const out: SponsorEquipStats = {};
  SPONSOR_STAT_KEYS.forEach((k) => {
    const v = src[k];
    if (v === undefined || v === null || (v as unknown) === '') {
      out[k] = 0;
    } else {
      const n = Number(v);
      out[k] = Number.isNaN(n) ? 0 : n;
    }
  });
  return out;
}

/** 是否已有任意非空自定义字段（用于判断是否应用已存 JSON） */
export function hasAnyStatValue(stats?: SponsorEquipStats | null): boolean {
  if (!stats) return false;
  return SPONSOR_STAT_KEYS.some((k) => {
    const v = stats[k];
    return v !== undefined && v !== null && (v as unknown) !== '';
  });
}

/** 保存时空值一律写成 0（绝对值，非 omit） */
export function stringifyStats(stats: SponsorEquipStats): string {
  return JSON.stringify(normalizeStatsAbsolute(stats));
}

export function formatStatsPreview(stats: SponsorEquipStats): string {
  const labels: Record<string, string> = {
    str: '力量',
    dex: '敏捷',
    int: '智力',
    luk: '运气',
    hp: 'HP',
    mp: 'MP',
    pAtk: '物攻',
    mAtk: '魔攻',
    pDef: '物防',
    mDef: '魔防',
    acc: '命中',
    avoid: '回避',
    hands: '手技',
    speed: '速度',
    jump: '跳跃',
    upgradeSlot: '可升级',
  };
  const parts: string[] = [];
  Object.entries(labels).forEach(([k, label]) => {
    const v = (stats as Record<string, number | null | undefined>)[k];
    if (v != null && Number(v) !== 0) {
      parts.push(`${label}+${v}`);
    }
  });
  return parts.length ? parts.join(' ') : '（无展示属性）';
}
