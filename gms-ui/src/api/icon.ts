import axios from 'axios';

export type IconCategory = string;

export interface IconResolveResult {
  category: string;
  id: number;
  url: string;
  cdnUrl: string;
  local: boolean;
  source: 'local' | 'cdn' | 'none' | string;
}

export interface IconCacheItem {
  category: string;
  id: number;
}

export interface IconCacheResult {
  category: string;
  id: number;
  cached: boolean;
  url: string;
  source: string;
  message?: string;
}

/** Resolve preferred icon URL (local game-assets or CDN). No auth required. */
export function resolveIcon(category: string, id: number | string) {
  return axios.get<IconResolveResult>(
    `/icon/v1/resolve/${encodeURIComponent(category)}/${id}`
  );
}

/** Download remote icon into server static/game-assets (auth required). */
export function cacheIcon(payload: {
  category?: string;
  id?: number;
  items?: IconCacheItem[];
  force?: boolean;
}) {
  return axios.post<IconCacheResult[]>('/icon/v1/cache', payload);
}
