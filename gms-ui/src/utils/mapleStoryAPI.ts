/**
 * 图标地址工具。
 * S9 未接入 /icon/v1 共用缓存时，默认走 maplestory.io CDN；
 * 已存的非 CDN 相对路径会接到 VITE_API_BASE_URL。
 */

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
  return `https://maplestory.io/api/${location}/${version}/${category}/${id}/icon`;
}

/**
 * 获取 icon 地址。
 * - 第 3 参为已存非 CDN 路径时优先使用
 * - 兼容旧调用 (category, id, location?, version?)，location 如 'GMS'
 * - 默认 maplestory.io CDN
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
  return getCdnIconUrl(category, id);
}

/** 道具图标：优先本地 /item-icons，缺失时由 onItemIconError 回退 CDN */
export function getItemIconUrl(id: string | number): string {
  if (!id || Number(id) <= 0) return '';
  return resolvePublicApiUrl(`/item-icons/${id}.png`);
}

/** <img @error>：本地缺失时回退 CDN，再失败则隐藏 */
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
    img.src.match(/\/item\/(\d+)\//)?.[1] ||
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

export function nothing() {
  return '占位用';
}
