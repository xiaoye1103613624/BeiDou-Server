import axios from 'axios';

export interface GuildMember {
  id: number;
  name: string;
  level: number;
  job: number;
  guildrank: number;
}

export interface GuildInfo {
  guildid: number;
  name: string;
  leader: number;
  leaderName: string;
  gp: number;
  capacity: number;
  notice: string;
  logo: number;
  logoColor: number;
  logoBG: number;
  logoBGColor: number;
  rank1title: string;
  rank2title: string;
  rank3title: string;
  rank4title: string;
  rank5title: string;
  allianceId: number;
  allianceName: string;
  signature: number;
  memberCount: number;
}

export function getGuildList() {
  return axios.get<GuildInfo[]>('/guild/v1/list');
}

export function getGuildMembers(guildId: number) {
  return axios.get<GuildMember[]>(`/guild/v1/members/${guildId}`);
}

export function deleteGuild(guildId: number) {
  return axios.delete(`/guild/v1/delete/${guildId}`);
}
