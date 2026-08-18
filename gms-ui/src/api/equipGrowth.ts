import axios from 'axios';

export interface EquipGrowthRecord {
  id?: number;
  itemId: number;
  itemName?: string;
  enabled?: number;
  maxLevel?: number;
  sortOrder?: number;
  remark?: string;
  levelsJson?: string;
  skillsJson?: string;
  source?: string;
  levelCount?: number;
  tipPreview?: string;
}

export interface GrowthLevelStats {
  str?: number;
  dex?: number;
  int?: number;
  luk?: number;
  pad?: number;
  mad?: number;
  mhp?: number;
  mmp?: number;
  pdd?: number;
  mdd?: number;
  acc?: number;
  eva?: number;
  speed?: number;
  jump?: number;
}

export interface GrowthLevel {
  level: number;
  enabled?: boolean;
  stats?: GrowthLevelStats;
  skills?: { id: number; level: number }[];
}

export interface GrowthLevelsV1 {
  schemaVersion: number;
  levels: GrowthLevel[];
}

export function getEquipGrowthList() {
  return axios.get('/equipGrowth/v1/list');
}

export function getEquipGrowthDetail(itemId: number) {
  return axios.get(`/equipGrowth/v1/detail/${itemId}`);
}

export function saveEquipGrowth(data: EquipGrowthRecord) {
  return axios.post('/equipGrowth/v1/save', data);
}

export function deleteEquipGrowth(id: number) {
  return axios.post('/equipGrowth/v1/delete', id);
}

export function reloadEquipGrowth() {
  return axios.post('/equipGrowth/v1/reload');
}

export function initEquipGrowthFromWz(payload?: {
  mode?: string;
  itemId?: number;
  itemIds?: number[];
}) {
  return axios.post('/equipGrowth/v1/init', payload || { mode: 'NEW_ONLY' });
}

export function previewEquipGrowthTip(itemId: number) {
  return axios.get(`/equipGrowth/v1/preview/${itemId}`);
}

export function parseLevelsJson(raw?: string): GrowthLevelsV1 {
  if (!raw || !raw.trim()) {
    return { schemaVersion: 1, levels: [] };
  }
  try {
    const parsed = JSON.parse(raw);
    return {
      schemaVersion: parsed.schemaVersion ?? 1,
      levels: parsed.levels ?? [],
    };
  } catch {
    return { schemaVersion: 1, levels: [] };
  }
}

export function stringifyLevelsJson(model: GrowthLevelsV1): string {
  return JSON.stringify({ schemaVersion: 1, levels: model.levels });
}
