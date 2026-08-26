import axios from 'axios';

export interface IconSyncReq {
  version?: number;
  region?: string;
  force?: boolean;
  categories?: string[];
  dropperId?: number;
  objectIds?: number[];
  fromLottery?: boolean;
  lotteryNpcId?: number;
}

export interface IconSyncRtn {
  version: number;
  region: string;
  requested: number;
  success: number;
  skipped: number;
  failed: number;
  message: string;
}

/** 批量同步到 xy_icon_cache（需登录） */
export function syncSharedIcons(data: IconSyncReq) {
  return axios.post<IconSyncRtn>('/icon/v1/sync', data);
}
