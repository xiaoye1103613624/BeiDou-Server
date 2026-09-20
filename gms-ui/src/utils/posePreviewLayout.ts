/**
 * 预览舞台的统一定位规则 —— 与游戏客户端完全一致：
 *
 *   精灵左上角 = 挂点 − origin
 *
 * 人偶的两个锚点由后端（CharacterDollService）按真实 WZ 坐标给出，
 * 因此这里不再有任何硬编码的坐姿偏移量（旧的 SIT_BODY_NAVEL_X/Y 已移除）：
 *   - bodyOrigin：身体原点（角色脚底），座椅 effect 的挂载点；
 *   - navel：肚脐，坐骑 Character.wz map/navel 的挂载点。
 *
 * 所有坐标都是 WZ 像素单位；缩放只通过舞台层的 CSS transform 整体进行，
 * 以免破坏各图层之间的像素级相对关系。
 */

export type SpriteStyle = Record<string, string | number>;

/** 舞台上的点（人偶空间像素） */
export interface StagePoint {
  x: number;
  y: number;
}

/**
 * 人偶几何（后端返回）：画布尺寸 + 锚点相对 PNG 左上角的坐标。
 */
export interface DollGeometry {
  width?: number;
  height?: number;
  bodyOriginX?: number;
  bodyOriginY?: number;
  navelX?: number;
  navelY?: number;
}

/** 物品贴图（椅子 effect / 坐骑帧）：origin 钉在挂点上 */
export function originSpriteStyle(
  point: StagePoint,
  originX: number,
  originY: number,
  zIndex: number
): SpriteStyle {
  return {
    left: `${point.x - originX}px`,
    top: `${point.y - originY}px`,
    transform: 'none',
    zIndex,
  };
}

/**
 * 人偶：把身体原点对齐到挂点（座椅场景）。
 *
 * 挂点 = 舞台中心（角色身体原点所在位置）。
 */
export function dollByBodyOrigin(
  point: StagePoint,
  doll: DollGeometry,
  zIndex: number
): SpriteStyle {
  return {
    left: `${point.x - Number(doll.bodyOriginX || 0)}px`,
    top: `${point.y - Number(doll.bodyOriginY || 0)}px`,
    transform: 'none',
    zIndex,
  };
}

/**
 * 人偶：把肚脐对齐到挂点（坐骑场景）。
 *
 * 挂点 = 坐骑贴图 origin 所在位置 + 坐骑 map/navel。
 */
export function dollByNavel(
  point: StagePoint,
  doll: DollGeometry,
  zIndex: number
): SpriteStyle {
  return {
    left: `${point.x - Number(doll.navelX || 0)}px`,
    top: `${point.y - Number(doll.navelY || 0)}px`,
    transform: 'none',
    zIndex,
  };
}

/** 缺人偶时的剪影：按锚点粗略框出坐姿躯干位置 */
export function dollSilhouetteStyle(
  point: StagePoint,
  bodyAnchorOffset: StagePoint,
  zIndex: number
): SpriteStyle {
  return {
    left: `${point.x + bodyAnchorOffset.x - 10}px`,
    top: `${point.y + bodyAnchorOffset.y - 28}px`,
    zIndex,
  };
}

/** 十字线（挂点指示）：自身 16×16，靠负 margin 对准中心 */
export function crosshairStyle(point: StagePoint, zIndex = 4): SpriteStyle {
  return {
    left: `${point.x}px`,
    top: `${point.y}px`,
    zIndex,
  };
}

/** 后端模式：携带 WZ 1:1 effect/stand 画布 URL */
export const EFFECT_PNG_MODE = 'EFFECT_PNG';

/** 只有 WZ 1:1 画布可以进舞台；背包图标与 effect 共享 origin 数值但画布不同，会破坏比例 */
export function resolveEffectStageUrl(
  mode?: string | null,
  imageUrl?: string | null
): string {
  if (mode !== EFFECT_PNG_MODE || !imageUrl) {
    return '';
  }
  return imageUrl;
}

/** 载入的 PNG 尺寸须等于 WZ canvas（effect/stand 帧），否则视为非 1:1 画布 */
export function isWzScaleImage(
  naturalWidth: number,
  naturalHeight: number,
  canvasWidth?: number | null,
  canvasHeight?: number | null
): boolean {
  if (!canvasWidth || !canvasHeight || canvasWidth <= 0 || canvasHeight <= 0) {
    return true;
  }
  return naturalWidth === canvasWidth && naturalHeight === canvasHeight;
}
