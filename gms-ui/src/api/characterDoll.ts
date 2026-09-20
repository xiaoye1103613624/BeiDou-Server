import axios from 'axios';

/**
 * 本地人偶接口（Character.wz 合成）。
 *
 * 人偶给出 bodyOrigin / navel 两个锚点，座椅与坐骑预览都按它挂载，
 * 不再使用任何硬编码坐姿偏移量。
 */
export interface CharacterDollRequest {
  /** 皮肤：决定 body 基础件 0000{skin}.img 与头底 0001{skin}.img */
  skinId?: number;
  /** 脸型 itemId（Character/Face） */
  faceId?: number;
  /** 发型 itemId（Character/Hair） */
  hairId?: number;
  /** 装备 itemId 列表（Cap/Coat/Longcoat/Pants/Shoes/Glove/Weapon...） */
  equipIds?: number[];
  /** 姿势：sit | stand1 | walk1 ... */
  pose?: string;
  /** 帧序号 */
  frame?: number;
  /** 忽略缓存重新合成 */
  refresh?: boolean;
}

export interface CharacterDollResult {
  /** DOLL | NONE */
  mode?: string;
  imageUrl?: string;
  width?: number;
  height?: number;
  /** 身体原点（角色脚底）相对 PNG 左上角：座椅 effect 挂载点 */
  bodyOriginX?: number;
  bodyOriginY?: number;
  /** 肚脐相对 PNG 左上角：坐骑 map/navel 挂载点 */
  navelX?: number;
  navelY?: number;
  zOrder?: string[];
  missingParts?: string[];
  lookKey?: string;
  message?: string;
}

/**
 * 默认外观人偶（服务端座椅/坐骑预览共用）。
 * 与后端 {@code CharacterWzPartStore} 默认常量一致：皮肤 2000 / 发型 30000 / 脸型 20000。
 */
export const DEFAULT_DOLL_LOOK = {
  skinId: 2000,
  faceId: 20000,
  hairId: 30000,
  equipIds: [1040036, 1060026, 1070003],
};

export function renderCharacterDoll(payload: CharacterDollRequest) {
  return axios.post<any, { data: CharacterDollResult }>(
    '/characterDoll/v1/render',
    payload
  );
}

export function renderDefaultCharacterDoll(pose = 'sit') {
  return renderCharacterDoll({ ...DEFAULT_DOLL_LOOK, pose, frame: 0 });
}
