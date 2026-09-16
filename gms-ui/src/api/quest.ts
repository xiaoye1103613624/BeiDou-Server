import axios from 'axios';

export interface QuestSearchReq {
  questId?: number;
  name?: string;
  npcId?: number;
  itemId?: number;
  hasScript?: boolean;
  pageNo?: number;
  pageSize?: number;
}

export interface QuestListRow {
  questId?: number;
  name?: string;
  parentName?: string;
  startNpcId?: number;
  startNpcName?: string;
  endNpcId?: number;
  endNpcName?: string;
  minLevel?: number;
  hasStartScript?: boolean;
  hasEndScript?: boolean;
  nextQuestId?: number;
}

export interface QuestItemNode {
  itemId?: number;
  count?: number;
  name?: string;
}

export interface QuestMobNode {
  mobId?: number;
  count?: number;
  name?: string;
}

export interface QuestLinkNode {
  questId?: number;
  state?: number;
  name?: string;
}

export interface QuestMapMeta {
  mapId?: number;
  mapName?: string;
  streetName?: string;
}

export interface QuestDetail {
  questId?: number;
  name?: string;
  parentName?: string;
  text0?: string;
  text1?: string;
  text2?: string;
  area?: number;
  order?: number;
  autoStart?: boolean;
  autoPreComplete?: boolean;
  autoComplete?: boolean;
  startNpcId?: number;
  startNpcName?: string;
  endNpcId?: number;
  endNpcName?: string;
  minLevel?: number;
  maxLevel?: number;
  startScript?: string;
  endScript?: string;
  startItems?: QuestItemNode[];
  endItems?: QuestItemNode[];
  startMobs?: QuestMobNode[];
  endMobs?: QuestMobNode[];
  upstreamQuests?: QuestLinkNode[];
  nextQuestId?: number;
  startRewards?: QuestItemNode[];
  endRewards?: QuestItemNode[];
  startExp?: number;
  endExp?: number;
  startMeso?: number;
  endMeso?: number;
  maps?: QuestMapMeta[];
  warnings?: string[];
}

export interface QuestWriteReq {
  questId?: number;
  name?: string;
  parentName?: string;
  nameZh?: string;
  text0?: string;
  text1?: string;
  text2?: string;
  area?: number;
  order?: number;
  autoStart?: boolean;
  autoPreComplete?: boolean;
  autoComplete?: boolean;
  startNpcId?: number;
  endNpcId?: number;
  minLevel?: number;
  maxLevel?: number;
  startScript?: string;
  endScript?: string;
  nextQuestId?: number;
  endExp?: number;
  endMeso?: number;
  startItems?: QuestItemNode[];
  endItems?: QuestItemNode[];
  endMobs?: QuestMobNode[];
  upstreamQuests?: QuestLinkNode[];
  endRewards?: QuestItemNode[];
}

export interface QuestProgressReq {
  questId?: number;
  characterId?: number;
  characterName?: string;
  mode?: string;
  status?: number;
  pageNo?: number;
  pageSize?: number;
}

export interface QuestProgressRow {
  questStatusId?: number;
  characterId?: number;
  characterName?: string;
  questId?: number;
  questName?: string;
  status?: number;
  statusLabel?: string;
  time?: number;
  forfeited?: number;
  completed?: number;
  online?: boolean;
}

export interface QuestForceReq {
  questId?: number;
  characterId?: number;
  npcId?: number;
}

export interface QuestScriptReq {
  questId?: number;
  phase?: string;
  content?: string;
  localized?: boolean;
}

export interface QuestSyncReq {
  dryRun?: boolean;
  questIds?: number[];
}

export interface QuestChainNode {
  questId?: number;
  name?: string;
  parentName?: string;
  startNpcId?: number;
  startNpcName?: string;
  minLevel?: number;
  current?: boolean;
}

export interface QuestChainEdge {
  from?: number;
  to?: number;
  type?: string;
}

export interface QuestChainRtn {
  seedQuestId?: number;
  nodes?: QuestChainNode[];
  edges?: QuestChainEdge[];
  warnings?: string[];
}

export function getQuestList(data: QuestSearchReq) {
  return axios.post('/quest/v1/getQuestList', data);
}

export function getQuestDetail(questId: number) {
  return axios.post('/quest/v1/getQuestDetail', questId);
}

export function getQuestChain(questId: number) {
  return axios.post('/quest/v1/getQuestChain', questId);
}

export function addQuest(data: QuestWriteReq) {
  return axios.put('/quest/v1/addQuest', data);
}

export function updateQuest(data: QuestWriteReq) {
  return axios.post('/quest/v1/updateQuest', data);
}

export function deleteQuest(questId: number) {
  return axios.delete(`/quest/v1/deleteQuest/${questId}`);
}

export function publishQuests() {
  return axios.post('/quest/v1/publish');
}

export function getCompletions(data: QuestProgressReq) {
  return axios.post('/quest/v1/getCompletions', data);
}

export function getCharacterQuests(data: QuestProgressReq) {
  return axios.post('/quest/v1/getCharacterQuests', data);
}

export function forceStart(data: QuestForceReq) {
  return axios.post('/quest/v1/forceStart', data);
}

export function forceComplete(data: QuestForceReq) {
  return axios.post('/quest/v1/forceComplete', data);
}

export function resetQuest(data: QuestForceReq) {
  return axios.post('/quest/v1/reset', data);
}

export function readQuestScript(data: QuestScriptReq) {
  return axios.post('/quest/v1/script/read', data);
}

export function writeQuestScript(data: QuestScriptReq) {
  return axios.post('/quest/v1/script/write', data);
}

export function getSyncClientStatus() {
  return axios.get('/quest/v1/syncClient/status');
}

export function syncClient(data: QuestSyncReq) {
  return axios.post('/quest/v1/syncClient', data);
}
