import axios from 'axios';

export type AssetCategory = string;

export interface AssetProviderInfo {
  name: string;
  enabled: boolean;
}

export interface AssetInfoResult {
  root: string;
  legacyItemIconsDir?: string;
  clientDataConfigured: boolean;
  clientDataPath?: string;
  providers: AssetProviderInfo[];
}

export interface AssetEnsurePayload {
  category?: string;
  ids?: number[];
  idFrom?: number;
  idTo?: number;
  fromCatalog?: boolean;
  force?: boolean;
}

export interface AssetEnsureResult {
  category: string;
  force: boolean;
  requested: number;
  cached: number;
  skipped: number;
  failed: number;
  durationMs: number;
  root?: string;
  message?: string;
}

/** Resource hub info (auth not required for info/resolve). */
export function getAssetInfo() {
  return axios.get<AssetInfoResult>('/asset/v1/info');
}

/** Batch ensure icons into server game-assets. */
export function ensureAssets(payload: AssetEnsurePayload) {
  return axios.post<AssetEnsureResult>('/asset/v1/ensure', payload, {
    timeout: 600000,
  });
}

/** Fill only missing icons. */
export function ensureMissingAssets(payload: AssetEnsurePayload) {
  return axios.post<AssetEnsureResult>('/asset/v1/ensureMissing', payload, {
    timeout: 600000,
  });
}
