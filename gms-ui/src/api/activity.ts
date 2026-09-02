import axios from 'axios';

export interface ActivityStatus {
  code: string;
  nameZh?: string;
  nameEn?: string;
  category?: string;
  lobbyMapId?: number;
  eventMapId?: number;
  teamEvent?: boolean;
  supportsMapStart?: boolean;
  enabled?: boolean;
  defaultMaxPlayers?: number;
  sortOrder?: number;
  status?: string;
  sessionId?: number;
  worldId?: number;
  channelId?: number;
  maxPlayers?: number;
  registeredCount?: number;
  lobbyCount?: number;
  arenaCount?: number;
  plannedStartAt?: string;
  extraInfo?: string;
}

export interface ActivityActionPayload {
  code: string;
  worldId?: number;
  channelId?: number;
  maxPlayers?: number;
  plannedStartAt?: string;
  enabled?: boolean;
}

export interface ActivitySchedule {
  id?: number;
  activityCode: string;
  worldId?: number;
  channelId: number;
  scheduleType: 'ONCE' | 'DAILY' | 'WEEKLY' | string;
  startAt?: string;
  cronTime?: string;
  daysOfWeek?: string;
  maxPlayers?: number;
  notifyMinutes?: number;
  notifyIntervalSec?: number;
  prewarpMinutes?: number;
  enabled?: boolean;
  nextRunAt?: string;
}

export function listActivities() {
  return axios.get('/activity/v1/list');
}

export function setActivityEnabled(data: ActivityActionPayload) {
  return axios.post('/activity/v1/setEnabled', data);
}

export function openRegistration(data: ActivityActionPayload) {
  return axios.post('/activity/v1/openRegistration', data);
}

export function closeRegistration(data: ActivityActionPayload) {
  return axios.post('/activity/v1/closeRegistration', data);
}

export function startActivity(data: ActivityActionPayload) {
  return axios.post('/activity/v1/start', data);
}

export function stopActivity(data: ActivityActionPayload) {
  return axios.post('/activity/v1/stop', data);
}

export function stopAndClearActivity(data: ActivityActionPayload) {
  return axios.post('/activity/v1/stopAndClear', data);
}

export function warpAllOut(data: ActivityActionPayload) {
  return axios.post('/activity/v1/warpAllOut', data);
}

export function listSchedules() {
  return axios.get('/activity/v1/schedules');
}

export function saveSchedule(data: ActivitySchedule) {
  return axios.post('/activity/v1/saveSchedule', data);
}

export function deleteSchedule(data: { id: number }) {
  return axios.post('/activity/v1/deleteSchedule', data);
}

export interface ActivityRewardTier {
  id?: number;
  activityCode: string;
  tierCode: string;
  tierName?: string;
  priority?: number;
  exclusiveGroup?: string;
  matchJson?: string;
  grantMode?: string;
  mesos?: number;
  exp?: number;
  itemId?: number;
  itemQty?: number;
  item2Id?: number;
  item2Qty?: number;
  announceName?: boolean;
  announceTpl?: string;
  enabled?: boolean;
  remark?: string;
}

export interface ActivitySettlePayload {
  sessionId?: number;
  code?: string;
  worldId?: number;
  channelId?: number;
  results?: Array<{
    characterId: number;
    characterName?: string;
    teamId?: number;
    rankNo?: number;
    score?: number;
    finishTimeMs?: number;
    outcome: string;
    tags?: string;
  }>;
}

export function listRewardTiers(activityCode?: string) {
  return axios.get('/activity/v1/rewardTiers', {
    params: activityCode ? { activityCode } : {},
  });
}

export function saveRewardTier(data: ActivityRewardTier) {
  return axios.post('/activity/v1/saveRewardTier', data);
}

export function deleteRewardTier(data: { id: number }) {
  return axios.post('/activity/v1/deleteRewardTier', data);
}

export function settleActivity(data: ActivitySettlePayload) {
  return axios.post('/activity/v1/settle', data);
}

export function listSessionClaims(sessionId: number) {
  return axios.get('/activity/v1/sessionClaims', { params: { sessionId } });
}
