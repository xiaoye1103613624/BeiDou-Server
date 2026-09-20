import axios from 'axios';

export interface SkillLineage {
  code: string;
  nameKey: string;
  name?: string;
  enabled: boolean;
  remark?: string;
}

export interface SkillJobStage {
  branch: number;
  jobId: number;
  nameKey: string;
  name?: string;
}

export interface SkillJobLine {
  lineage: string;
  lineId: string;
  nameKey: string;
  name?: string;
  stages: SkillJobStage[];
}

export interface SkillNode {
  skillId: number;
  jobId: number;
  name: string;
  maxLevel: number;
  invisible?: boolean;
  masterLevel?: number;
  iconUrl?: string;
  req?: Record<number, number>;
}

export interface SkillEdge {
  fromSkillId: number;
  toSkillId: number;
  reqLevel: number;
  external?: boolean;
}

export interface SkillBook {
  jobId: number;
  skills: SkillNode[];
  edges: SkillEdge[];
}

export interface SkillLevel {
  level: number;
  attrs?: Record<string, string>;
}

export interface SkillDetail {
  skillId: number;
  jobId: number;
  name?: string;
  desc?: string;
  invisible?: boolean;
  masterLevel?: number;
  iconUrl?: string;
  req?: Record<number, number>;
  levels?: SkillLevel[];
  hs?: Record<string, string>;
  editorEnabled?: boolean;
}

export interface SkillEditorStatus {
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

export interface SkillPatchResult {
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

export function fetchSkillLineages() {
  return axios.post<any, { data: SkillLineage[] }>(
    '/clientSkill/v1/lineages',
    {}
  );
}

export function fetchSkillJobLines(lineage: string) {
  return axios.post<any, { data: SkillJobLine[] }>('/clientSkill/v1/jobLines', {
    lineage,
  });
}

export function fetchSkillBook(jobId: number) {
  return axios.post<any, { data: SkillBook }>('/clientSkill/v1/skillBook', {
    jobId,
  });
}

export function fetchSkillDetail(skillId: number) {
  return axios.post<any, { data: SkillDetail }>('/clientSkill/v1/skillDetail', {
    skillId,
  });
}

export interface SkillEnsureIconsResult {
  requested?: number;
  cached?: number;
  failed?: number;
  urls?: Record<string, string>;
  sources?: Record<string, string>;
}

export function ensureSkillIcons(skillIds: number[], force = false) {
  return axios.post<any, { data: SkillEnsureIconsResult }>(
    '/clientSkill/v1/ensureIcons',
    {
      skillIds,
      force,
    }
  );
}

export interface SkillEffectFrame {
  index?: number;
  nodePath?: string;
  layer?: string;
  originX?: number;
  originY?: number;
  delay?: number;
  width?: number;
  height?: number;
  imageUrl?: string;
}

export interface SkillEffectPreview {
  skillId?: number;
  hasEffect?: boolean;
  layer?: string;
  frames?: SkillEffectFrame[];
  dumped?: number;
  failed?: number;
  message?: string;
}

export function fetchSkillEffectPreview(skillId: number, refresh = false) {
  return axios.post<any, { data: SkillEffectPreview }>(
    '/clientSkill/v1/effectPreview',
    { skillId, refresh }
  );
}

export function fetchSkillEditorStatus() {
  return axios.post<any, { data: SkillEditorStatus }>(
    '/clientSkill/v1/editorStatus',
    {}
  );
}

export function writeSkill(payload: {
  skillId: number;
  masterLevel?: number | null;
  invisible?: boolean | null;
  req?: Record<number, number> | null;
  levels?: SkillLevel[] | null;
}) {
  return axios.post('/clientSkill/v1/skill/write', payload);
}

export function writeSkillString(payload: {
  skillId: number;
  name?: string | null;
  desc?: string | null;
  hs?: Record<string, string> | null;
}) {
  return axios.post('/clientSkill/v1/string/write', payload);
}

export function reloadSkills() {
  return axios.post<any, { data: { skillCount?: number; message?: string } }>(
    '/clientSkill/v1/reloadSkills',
    {}
  );
}

export function patchSkillDryRun(jobIds?: number[]) {
  return axios.post<any, { data: SkillPatchResult }>(
    '/clientSkill/v1/patch/dryRun',
    { jobIds }
  );
}

export function patchSkillApply(jobIds?: number[]) {
  return axios.post<any, { data: SkillPatchResult }>(
    '/clientSkill/v1/patch/apply',
    { jobIds }
  );
}
