import axios from 'axios';

export interface AllianceGuild {
  guildId: string;
  guildName: string;
}

export interface AllianceInfo {
  id: number;
  name: string;
  capacity: number;
  notice: string;
  rank1: string;
  rank2: string;
  rank3: string;
  rank4: string;
  rank5: string;
  guilds: AllianceGuild[];
  guildCount: number;
}

export function getAllianceList() {
  return axios.get<AllianceInfo[]>('/alliance/v1/list');
}

export function deleteAlliance(id: number) {
  return axios.delete(`/alliance/v1/delete/${id}`);
}
