import axios from 'axios';

export interface ChairPoseEditorStatus {
  editorEnabled?: boolean;
  allowClientWrite?: boolean;
  clientDataConfigured?: boolean;
  clientDataPath?: string;
  clientEnConfigured?: boolean;
  clientEnPath?: string;
  patcherAvailable?: boolean;
  patcherPath?: string;
  configEditorWrite?: string;
  configClientWrite?: string;
}

export interface ChairPoseEffect {
  layer?: string;
  originX?: number;
  originY?: number;
  pos?: number;
  z?: number;
  canvasWidth?: number;
  canvasHeight?: number;
  frameOriginX?: number;
  frameOriginY?: number;
  frameZ?: number;
}

export interface ChairPoseItem {
  itemId: number;
  name?: string;
  hasEffect?: boolean;
  hasEffect2?: boolean;
  iconUrl?: string;
  sourceFile?: string;
}

export interface ChairPoseListResult {
  total?: number;
  items?: ChairPoseItem[];
}

export interface ChairPoseDetail {
  itemId: number;
  name?: string;
  desc?: string;
  iconUrl?: string;
  sourceFile?: string;
  editorEnabled?: boolean;
  effects?: ChairPoseEffect[];
}

export interface ChairPosePreview {
  mode?: string;
  imageUrl?: string;
  iconUrl?: string;
  originX?: number;
  originY?: number;
  canvasWidth?: number;
  canvasHeight?: number;
  message?: string;
}

export interface TamingMobPoseItem {
  mobId: number;
  name?: string;
  hasNavel?: boolean;
  defaultAction?: string;
  iconUrl?: string;
  sourceFile?: string;
}

export interface TamingMobPoseListResult {
  total?: number;
  items?: TamingMobPoseItem[];
}

export interface TamingMobPoseFrame {
  action?: string;
  frameIndex?: number;
  navelX?: number;
  navelY?: number;
  originX?: number;
  originY?: number;
  z?: string;
  canvasWidth?: number;
  canvasHeight?: number;
}

export interface TamingMobPoseDetail {
  mobId: number;
  name?: string;
  desc?: string;
  iconUrl?: string;
  sourceFile?: string;
  defaultAction?: string;
  editorEnabled?: boolean;
  actions?: string[];
  frames?: TamingMobPoseFrame[];
}

export interface TamingMobPosePreview {
  mode?: string;
  imageUrl?: string;
  iconUrl?: string;
  action?: string;
  frameIndex?: number;
  navelX?: number;
  navelY?: number;
  originX?: number;
  originY?: number;
  canvasWidth?: number;
  canvasHeight?: number;
  message?: string;
}

export interface ChairPosePatchResult {
  dryRun?: boolean;
  applied?: boolean;
  patcherAvailable?: boolean;
  clientDataPath?: string;
  clientEnPath?: string;
  exportDir?: string;
  files?: string[];
  warnings?: string[];
  message?: string;
}

export function fetchChairPoseStatus() {
  return axios.post<any, { data: ChairPoseEditorStatus }>(
    '/clientChairPose/v1/status',
    {}
  );
}

export function fetchChairList(payload: {
  keyword?: string;
  effect2Only?: boolean;
  page?: number;
  pageSize?: number;
}) {
  return axios.post<any, { data: ChairPoseListResult }>(
    '/clientChairPose/v1/chairs/list',
    payload
  );
}

export function fetchChairDetail(itemId: number) {
  return axios.post<any, { data: ChairPoseDetail }>(
    '/clientChairPose/v1/chairs/detail',
    { itemId }
  );
}

export function writeChair(payload: {
  itemId: number;
  effects: ChairPoseEffect[];
}) {
  return axios.post<any, { data: { message?: string; warnings?: string[] } }>(
    '/clientChairPose/v1/chairs/write',
    payload
  );
}

export function previewChair(payload: { itemId: number; layer?: string }) {
  return axios.post<any, { data: ChairPosePreview }>(
    '/clientChairPose/v1/chairs/preview',
    payload
  );
}

export function fetchTamingMobList(payload: {
  keyword?: string;
  page?: number;
  pageSize?: number;
}) {
  return axios.post<any, { data: TamingMobPoseListResult }>(
    '/clientChairPose/v1/tamingMobs/list',
    payload
  );
}

export function fetchTamingMobDetail(mobId: number) {
  return axios.post<any, { data: TamingMobPoseDetail }>(
    '/clientChairPose/v1/tamingMobs/detail',
    { mobId }
  );
}

export function writeTamingMob(payload: {
  mobId: number;
  writeMode: string;
  action?: string;
  frameIndex?: number;
  navelX?: number;
  navelY?: number;
  originX?: number;
  originY?: number;
  z?: string;
}) {
  return axios.post<any, { data: { message?: string; warnings?: string[] } }>(
    '/clientChairPose/v1/tamingMobs/write',
    payload
  );
}

export function previewTamingMob(payload: {
  mobId: number;
  action?: string;
  frameIndex?: number;
}) {
  return axios.post<any, { data: TamingMobPosePreview }>(
    '/clientChairPose/v1/tamingMobs/preview',
    payload
  );
}

export function patchChairDryRun(payload?: {
  itemIds?: number[];
  mobIds?: number[];
}) {
  return axios.post<any, { data: ChairPosePatchResult }>(
    '/clientChairPose/v1/patch/dryRun',
    payload || {}
  );
}

export function patchChairApply(payload?: {
  itemIds?: number[];
  mobIds?: number[];
}) {
  return axios.post<any, { data: ChairPosePatchResult }>(
    '/clientChairPose/v1/patch/apply',
    payload || {}
  );
}
