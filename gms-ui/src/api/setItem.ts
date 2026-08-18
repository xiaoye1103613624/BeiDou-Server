import axios from 'axios';

export interface SetItemRecord {
  id?: number;
  setId: number;
  setName?: string;
  completeCount?: number;
  itemIds?: string;
  enabled?: number;
  sortOrder?: number;
  remark?: string;
  tiersJson?: string;
}

export interface SetItemDetail extends SetItemRecord {
  source?: string;
  fromWz?: boolean;
  fromDb?: boolean;
  tierCount?: number;
  itemCount?: number;
}

export interface SetTierStats {
  str?: number;
  dex?: number;
  int?: number;
  luk?: number;
  pad?: number;
  mad?: number;
  pdd?: number;
  mdd?: number;
  acc?: number;
  eva?: number;
  mhp?: number;
  mmp?: number;
  allStat?: number;
  speed?: number;
  jump?: number;
}

export interface SetTierStatsPercent {
  strR?: number;
  dexR?: number;
  intR?: number;
  lukR?: number;
  mhpR?: number;
  mmpR?: number;
}

export interface SetTierCombatStats {
  damR?: number;
  bdR?: number;
  nbdR?: number;
  fdR?: number;
  ignoreMobpdpR?: number;
  ignoreMobmdR?: number;
  cr?: number;
  cd?: number;
}

export interface SetTier {
  count: number;
  enabled?: boolean;
  stats?: SetTierStats;
  statsPercent?: SetTierStatsPercent;
  combatStats?: SetTierCombatStats;
  damageSkin?: number;
  skills?: { id: number; level: number }[];
  activeSkills?: { skillId: number; level: number }[];
  skillMods?: {
    skillId: number;
    addAttackCount?: number;
    addLevel?: number;
    type?: string;
  }[];
}

export interface SetItemTiersV2 {
  schemaVersion: number;
  tiers: SetTier[];
}

export function getSetItemList() {
  return axios.get('/setItem/v1/list');
}

export function getSetItemMergedList() {
  return axios.get('/setItem/v1/merged/list');
}

export function getSetItemDetail(setId: number) {
  return axios.get(`/setItem/v1/detail/${setId}`);
}

export function getSetItemWzList() {
  return axios.get('/setItem/v1/wz/list');
}

export function importSetItemFromWz(setIds: number[], mode: string) {
  return axios.post('/setItem/v1/wz/import', { setIds, mode });
}

export function getSetItemStatFields() {
  return axios.get('/setItem/v1/meta/statFields');
}

export function getSetItemColors() {
  return axios.get('/setItem/v1/meta/colors');
}

export function previewSetItem(data: {
  setId: number;
  equippedCount: number;
  jobId?: number;
}) {
  return axios.post('/setItem/v1/preview', data);
}

export function saveSetItem(data: SetItemRecord) {
  return axios.post('/setItem/v1/save', data);
}

export function deleteSetItem(id: number) {
  return axios.post('/setItem/v1/delete', id);
}

export function reloadSetItem() {
  return axios.post('/setItem/v1/reload');
}

export function parseTiersJson(raw?: string): SetItemTiersV2 {
  if (!raw || !raw.trim()) {
    return { schemaVersion: 2, tiers: [] };
  }
  try {
    const parsed = JSON.parse(raw);
    if (Array.isArray(parsed)) {
      return { schemaVersion: 2, tiers: parsed };
    }
    return {
      schemaVersion: parsed.schemaVersion ?? 2,
      tiers: parsed.tiers ?? [],
    };
  } catch {
    return { schemaVersion: 2, tiers: [] };
  }
}

export function stringifyTiersJson(model: SetItemTiersV2): string {
  return JSON.stringify({ schemaVersion: 2, tiers: model.tiers });
}
