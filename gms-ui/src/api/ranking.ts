import axios from 'axios';

export interface RankingQuery {
  filter?: number;
  limit?: number;
}

export interface RankingFilterOption {
  id: number;
  name: string;
}

export interface CombatPowerRankItem {
  rank: number;
  characterId: number;
  name: string;
  world: number;
  job: number;
  jobName: string;
  jobNiche: number;
  jobNicheName: string;
  level: number;
  combatPower: number;
  baseDamage: number;
}

export interface EquipScoreRankItem {
  rank: number;
  inventoryItemId: number;
  characterId: number;
  characterName: string;
  world: number;
  itemId: number;
  itemName: string;
  position: number;
  slotCategory: number;
  slotCategoryName: string;
  equipped: boolean;
  score: number;
  attStr: number;
  attDex: number;
  attInt: number;
  attLuk: number;
  hp: number;
  mp: number;
  pAtk: number;
  mAtk: number;
  pDef: number;
  mDef: number;
  acc: number;
  avoid: number;
  hands: number;
  speed: number;
  jump: number;
  upgradeSlots: number;
  level: number;
  vicious: number;
  itemLevel: number;
  itemExp: number;
}

export function fetchCombatPowerRanking(data: RankingQuery) {
  return axios.post('/ranking/v1/combatPower', data);
}

export function fetchEquipScoreRanking(data: RankingQuery) {
  return axios.post('/ranking/v1/equipScore', data);
}

export function fetchJobNicheOptions() {
  return axios.get('/ranking/v1/jobNicheOptions');
}

export function fetchSlotCategoryOptions() {
  return axios.get('/ranking/v1/slotCategoryOptions');
}

export function refreshRanking() {
  return axios.post('/ranking/v1/refresh', {});
}
