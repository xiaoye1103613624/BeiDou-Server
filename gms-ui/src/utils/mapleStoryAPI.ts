/**
 * 小册子 / maplestory.io 默认版本（查询与同步）
 */
export const DEFAULT_BOOKLET_VERSION = '227';

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

/** 相对路径转到 API 源（开发态 gms-ui:8787 上没有 /item-icons） */
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

function normalizeIconType(category: string): string {
  const c = String(category || '')
    .trim()
    .toLowerCase();
  if (
    c === 'equip' ||
    c === 'consume' ||
    c === 'etc' ||
    c === 'cash' ||
    c === 'install' ||
    c === 'pet'
  ) {
    return 'item';
  }
  return c || 'item';
}

/**
 * 已持久化到库的图标地址（/drop/v1/icon/...，无需鉴权）
 */
export function getPersistedIconUrl(
  category: string,
  id: string | number
): string {
  if (!id || Number(id) <= 0) return '';
  return resolvePublicApiUrl(`/drop/v1/icon/${category}/${id}`);
}

/**
 * 共用图标缓存（懒加载）：缺失时服务端拉取 maplestory.io / 小册子并写入 xy_icon_cache。
 * 推荐各管理页统一使用本方法。
 */
export function getCachedIconUrl(
  category: string,
  id: string | number
): string {
  if (!id || Number(id) <= 0) return '';
  const type = normalizeIconType(category);
  return resolvePublicApiUrl(`/icon/v1/${type}/${id}`);
}

/**
 * CDN 图标（标准 GMS；自定义 ID 常 404）
 */
export function getCdnIconUrl(
  category: string,
  id: string | number,
  location = 'GMS',
  version = DEFAULT_BOOKLET_VERSION
): string {
  if (!id || Number(id) <= 0) return '';
  return `https://maplestory.io/api/${location}/${version}/${category}/${id}/icon`;
}

/**
 * 获取 icon 地址。
 * - 忽略已存的 maplestory.io CDN（改走本地）
 * - 优先 icon_url / 持久化相对路径（接到 API origin）
 * - 默认走共用缓存 /icon/v1（懒加载）
 */
export function getIconUrl(
  category: string,
  id: string | number,
  persistedUrl?: string | null
): string {
  if (!id || Number(id) <= 0) return '';
  if (persistedUrl && !isCdnIconUrl(persistedUrl)) {
    return resolvePublicApiUrl(persistedUrl);
  }
  return getCachedIconUrl(category, id);
}

/** <img @error>：缓存缺失时回退 CDN，再失败则隐藏。data-skip-cdn=1 时不打 CDN。 */
export function onItemIconError(event: Event): void {
  const img = event.target as HTMLImageElement | null;
  if (!img) return;
  if (img.dataset.skipCdn === '1' || img.dataset.fallback === '1') {
    img.style.visibility = 'hidden';
    img.removeAttribute('src');
    return;
  }
  const id =
    img.dataset.itemId ||
    img.src.match(/item-icons\/(\d+)/)?.[1] ||
    img.src.match(/\/icon\/v1\/item\/(\d+)/)?.[1] ||
    img.src.match(/\/drop\/v1\/icon\/item\/(\d+)/)?.[1];
  if (!id) {
    const mobMatch = img.src.match(
      /\/(?:icon\/v1|drop\/v1\/icon)\/(mob|npc)\/(\d+)/
    );
    if (mobMatch) {
      img.dataset.fallback = '1';
      img.src = getCdnIconUrl(mobMatch[1], mobMatch[2], 'GMS', '83');
      return;
    }
    img.style.visibility = 'hidden';
    return;
  }
  img.dataset.fallback = '1';
  img.src = getCdnIconUrl('item', id);
}

/** 怪物图标错误：持久化 → CDN → 隐藏 */
export function onMobIconError(event: Event): void {
  const img = event.target as HTMLImageElement | null;
  if (!img) return;
  if (img.dataset.fallback === '1') {
    img.style.visibility = 'hidden';
    img.removeAttribute('src');
    return;
  }
  const id =
    img.dataset.mobId ||
    img.src.match(/\/(?:icon\/v1|drop\/v1\/icon)\/mob\/(\d+)/)?.[1] ||
    img.src.match(/\/mob\/(\d+)\//)?.[1];
  if (!id) {
    img.style.visibility = 'hidden';
    return;
  }
  img.dataset.fallback = '1';
  img.src = getCdnIconUrl('mob', id, 'GMS', '83');
}

export function nothing() {
  return '占位用';
}
