/**
 * 图标地址工具。
 * 优先本地 /game-assets/{type}/{id}.png（及遗留 /item-icons、/icons），
 * 缺失时回退 maplestory.io CDN，并在加载成功后异步缓存到服务端。
 */

import { cacheIcon } from '@/api/icon';

function apiPublicOrigin(): string {
  let base = String(import.meta.env.VITE_API_BASE_URL || '').trim();
  if (
    (base.startsWith("'") && base.endsWith("'")) ||
    (base.startsWith('"') && base.endsWith('"'))
  ) {
    base = base.slice(1, -1).trim();
  }
  return base.replace(/\/$/, '');
}

/** 相对路径转到 API 源（开发态 gms-ui:8787 上没有静态图标） */
export function resolvePublicApiUrl(path: string): string {
  if (!path) return '';
  if (/^https?:\/\//i.test(path)) return path;
  const origin = apiPublicOrigin();
  const p = path.startsWith('/') ? path : `/${path}`;
  return origin ? `${origin}${p}` : p;
}

export function isCdnIconUrl(url?: string | null): boolean {
  return !!url && /maplestory\.io/i.test(url);
}

export function normalizeIconCategory(category: string): string {
  const cat = (category || 'item').trim().toLowerCase();
  if (['cash', 'consume', 'eqp', 'etc', 'ins', 'pet', 'equip'].includes(cat)) {
    return 'item';
  }
  if (cat === 'monster' || cat === 'mobs') return 'mob';
  if (cat === 'npcs') return 'npc';
  if (cat === 'skills') return 'skill';
  if (cat === 'maps') return 'map';
  return cat;
}

/**
 * CDN 图标（标准 GMS；自定义 ID 常 404）
 */
export function getCdnIconUrl(
  category: string,
  id: string | number,
  location = 'GMS',
  version = '83'
): string {
  if (!id || Number(id) <= 0) return '';
  const cat = normalizeIconCategory(category);
  return `https://maplestory.io/api/${location}/${version}/${cat}/${id}/icon`;
}

/**
 * 固定默认人偶外观（皮肤 + 裸身发型/脸型 + 基础衣物）。
 * navelCenter：渲染图以肚脐为中心；GMS/83 sit 实测约 49×91，与 WZ 1:1 effect/stand 同像素单位。
 * 座椅预览唯一复用入口，见 posePreviewLayout.ts。
 */
export const DEFAULT_DOLL_SKIN_ID = 2000;
export const DEFAULT_DOLL_ITEM_IDS = [
  2000, 12000, 30000, 1040036, 1060026, 1070003,
] as const;

export type CharacterRenderPose = 'sit' | 'stand1' | 'walk1' | string;

/**
 * maplestory.io 角色渲染（GMS/83）。
 * 椅子/骑宠预览用 sit：CDN 无独立 ride 动作时，骑宠也用 sit 挂到 navel。
 */
export function getCharacterRenderUrl(options?: {
  pose?: CharacterRenderPose;
  frame?: number;
  skinId?: number;
  itemIds?: readonly number[] | number[];
  location?: string;
  version?: string;
  /** compact | center | navelCenter | feetCenter；锚点预览默认 navelCenter */
  align?: 'compact' | 'center' | 'navelCenter' | 'feetCenter';
}): string {
  const location = options?.location || 'GMS';
  const version = options?.version || '83';
  const skinId = options?.skinId ?? DEFAULT_DOLL_SKIN_ID;
  const items = (options?.itemIds || DEFAULT_DOLL_ITEM_IDS).join(',');
  const pose = options?.pose || 'sit';
  const frame = options?.frame ?? 0;
  const align = options?.align || 'navelCenter';
  return `https://maplestory.io/api/${location}/${version}/Character/${align}/${skinId}/${items}/${pose}/${frame}`;
}

/** 本地统一静态路径 /game-assets/{type}/{id}.png */
export function getLocalIconUrl(category: string, id: string | number): string {
  if (!id || Number(id) <= 0) return '';
  const cat = normalizeIconCategory(category);
  return resolvePublicApiUrl(`/game-assets/${cat}/${id}.png`);
}

/** 道具图标：优先本地 game-assets，兼容旧 /item-icons */
export function getItemIconUrl(id: string | number): string {
  return getLocalIconUrl('item', id);
}

/**
 * 获取 icon 地址。
 * - 第 3 参为已存非 CDN 路径时优先使用
 * - 兼容旧调用 (category, id, location?, version?)，location 如 'GMS'
 * - 默认先本地 game-assets，缺失由 ItemIcon / onItemIconError 回退 CDN
 */
export function getIconUrl(
  category: string,
  id: string | number,
  persistedUrlOrLocation?: string | null,
  version = '83'
): string {
  if (!id || Number(id) <= 0) return '';
  const third = persistedUrlOrLocation;
  if (third) {
    const looksLikeLocale =
      /^[A-Z]{2,4}$/.test(third) &&
      !third.includes('/') &&
      !third.includes('.');
    if (looksLikeLocale) {
      return getCdnIconUrl(category, id, third, version);
    }
    if (!isCdnIconUrl(third)) {
      return resolvePublicApiUrl(third);
    }
  }
  return getLocalIconUrl(category, id);
}

const pendingCache = new Set<string>();

/** 异步请求服务端把 CDN 图写入 static/game-assets（需已登录） */
export function scheduleIconCache(
  category: string,
  id: string | number,
  force = false
): void {
  const numId = Number(id);
  if (!numId || numId <= 0) return;
  const cat = normalizeIconCategory(category);
  const key = `${cat}:${numId}:${force ? 1 : 0}`;
  if (pendingCache.has(key)) return;
  pendingCache.add(key);
  cacheIcon({ category: cat, id: numId, force })
    .catch(() => {
      /* ignore: 未登录或 CDN 无图 */
    })
    .finally(() => {
      window.setTimeout(() => pendingCache.delete(key), 60_000);
    });
}

export function scheduleIconCacheBatch(
  items: Array<{ category: string; id: number | string }>,
  force = false
): void {
  const normalized = items
    .map((it) => ({
      category: normalizeIconCategory(it.category),
      id: Number(it.id),
    }))
    .filter((it) => it.id > 0);
  if (!normalized.length) return;
  const key = `batch:${normalized
    .map((i) => `${i.category}-${i.id}`)
    .join(',')}`;
  if (pendingCache.has(key)) return;
  pendingCache.add(key);
  cacheIcon({ items: normalized, force })
    .catch(() => {
      /* ignore */
    })
    .finally(() => {
      window.setTimeout(() => pendingCache.delete(key), 60_000);
    });
}

function extractIconRef(src: string): { category: string; id: string } | null {
  const gameAssets = src.match(/\/game-assets\/([a-zA-Z]+)\/(\d+)/);
  if (gameAssets) return { category: gameAssets[1], id: gameAssets[2] };
  const icons = src.match(/\/icons\/([a-zA-Z]+)\/(\d+)/);
  if (icons) return { category: icons[1], id: icons[2] };
  const itemIcons = src.match(/item-icons\/(\d+)/);
  if (itemIcons) return { category: 'item', id: itemIcons[1] };
  const iconApi = src.match(/\/icon\/v1\/(?:resolve\/)?([a-zA-Z]+)\/(\d+)/);
  if (iconApi) return { category: iconApi[1], id: iconApi[2] };
  const cdn = src.match(
    /maplestory\.io\/api\/[^/]+\/[^/]+\/([a-zA-Z]+)\/(\d+)/
  );
  if (cdn) return { category: cdn[1], id: cdn[2] };
  return null;
}

/** <img @error>：本地缺失时回退 CDN，并触发服务端缓存 */
export function onItemIconError(event: Event): void {
  const img = event.target as HTMLImageElement | null;
  if (!img) return;
  if (img.dataset.skipCdn === '1' || img.dataset.fallback === '1') {
    img.style.visibility = 'hidden';
    img.removeAttribute('src');
    return;
  }
  const fromData =
    img.dataset.itemId || img.dataset.iconId
      ? {
          category: img.dataset.category || 'item',
          id: img.dataset.itemId || img.dataset.iconId || '',
        }
      : null;
  const ref = fromData?.id ? fromData : extractIconRef(img.src);
  if (!ref?.id) {
    img.style.visibility = 'hidden';
    return;
  }
  img.dataset.fallback = '1';
  img.dataset.category = ref.category;
  img.dataset.iconId = ref.id;
  img.src = getCdnIconUrl(ref.category, ref.id);
  scheduleIconCache(ref.category, ref.id);
}

/** CDN 加载成功后同样触发缓存（本地已命中则服务端会跳过） */
export function onItemIconLoad(event: Event): void {
  const img = event.target as HTMLImageElement | null;
  if (!img || !isCdnIconUrl(img.src)) return;
  const category = img.dataset.category || 'item';
  const id = img.dataset.itemId || img.dataset.iconId;
  if (id) {
    scheduleIconCache(category, id);
  }
}

export function nothing() {
  return '占位用';
}
