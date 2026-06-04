import axios from 'axios';
import { PageState } from '@/store/page';

// ==================== 公会管理 ====================

export interface GuildSearch {
  guildName?: string;
  pageNo: number;
  pageSize: number;
}

export interface GuildItem {
  guildid: number;
  name: string;
  leaderName: string;
  leaderId: number;
  gp: number;
  capacity: number;
  notice: string;
  allianceId: number;
  allianceName: string;
  memberCount: number;
}

export interface GuildMemberItem {
  charId: number;
  name: string;
  level: number;
  jobId: number;
  jobName: string;
  guildRank: number;
  rankTitle: string;
  online: boolean;
  allianceRank: number;
}

export function getGuildList(data: GuildSearch) {
  return axios.post<PageState<GuildItem>>('/guildManage/v1/getGuildList', data);
}

export function getGuildMembers(guildId: number) {
  return axios.get<GuildMemberItem[]>(
    `/guildManage/v1/getGuildMembers/${guildId}`
  );
}

export function updateGuild(data: any) {
  return axios.post('/guildManage/v1/updateGuild', data);
}

export function disbandGuild(guildId: number) {
  return axios.delete(`/guildManage/v1/disbandGuild/${guildId}`);
}

// ==================== 师徒家族 ====================

export interface FamilyItem {
  familyId: number;
  memberCount: number;
  totalReputation: number;
  leaderId: number;
  leaderName: string;
  precepts: string;
}

export interface FamilyMemberItem {
  cid: number;
  name: string;
  level: number;
  seniorid: number;
  reputation: number;
  totalreputation: number;
  todaysrep: number;
  reptosenior: number;
  precepts: string;
}

export function getFamilyList(data: { pageNo: number; pageSize: number }) {
  return axios.post<PageState<FamilyItem>>(
    '/familyManage/v1/getFamilyList',
    data
  );
}

export function getFamilyMembers(familyId: number) {
  return axios.get<FamilyMemberItem[]>(
    `/familyManage/v1/getFamilyMembers/${familyId}`
  );
}

export function removeFamilyMember(cid: number) {
  return axios.delete(`/familyManage/v1/removeFamilyMember/${cid}`);
}

// ==================== 联盟管理 ====================

export interface AllianceItem {
  id: number;
  name: string;
  capacity: number;
  notice: string;
  rank1: string;
  rank2: string;
  rank3: string;
  rank4: string;
  rank5: string;
  guildCount: number;
}

export function getAllianceList(data: { pageNo: number; pageSize: number }) {
  return axios.post<PageState<AllianceItem>>(
    '/allianceManage/v1/getAllianceList',
    data
  );
}

export function getAllianceDetail(allianceId: number) {
  return axios.get<any>(`/allianceManage/v1/getAllianceDetail/${allianceId}`);
}

export function updateAlliance(data: any) {
  return axios.post('/allianceManage/v1/updateAlliance', data);
}

export function disbandAlliance(allianceId: number) {
  return axios.delete(`/allianceManage/v1/disbandAlliance/${allianceId}`);
}

// ==================== 婚姻管理 ====================

export interface MarriageItem {
  marriageid: number;
  husbandid: number;
  husbandName: string;
  wifeid: number;
  wifeName: string;
}

export function getMarriageList(data: { pageNo: number; pageSize: number }) {
  return axios.post<PageState<MarriageItem>>(
    '/marriageManage/v1/getMarriageList',
    data
  );
}

export function dissolveMarriage(marriageId: number) {
  return axios.delete(`/marriageManage/v1/dissolveMarriage/${marriageId}`);
}
