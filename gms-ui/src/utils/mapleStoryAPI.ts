/**
 * 小册子 / maplestory.io 默认版本（查询与同步）
 */
export const DEFAULT_BOOKLET_VERSION = '227';

/**
 * 已持久化到库的图标地址（/drop/v1/icon/...，无需鉴权）
 */
export function getPersistedIconUrl(
  category: string,
  id: string | number
): string {
  if (!id || Number(id) <= 0) return '';
  return `/drop/v1/icon/${category}/${id}`;
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
 * - 优先使用已持久化 URL（若传入 persistedUrl）
 * - item：本地 item-icons → 持久化 → CDN
 * - mob/npc：持久化路径 → CDN
 */
export function getIconUrl(
  category: string,
  id: string | number,
  persistedUrl?: string | null
): string {
  if (!id || Number(id) <= 0) return '';
  if (persistedUrl) return persistedUrl;
  if (category === 'item') {
    return `/item-icons/${id}.png`;
  }
  // mob/npc：先尝试库内持久化地址，缺失时由 @error 回退 CDN
  return getPersistedIconUrl(category, id);
}

/** <img @error>：本地/持久化缺失时回退 CDN，再失败则隐藏 */
export function onItemIconError(event: Event): void {
  const img = event.target as HTMLImageElement | null;
  if (!img) return;
  if (img.dataset.fallback === '1') {
    img.style.visibility = 'hidden';
    img.removeAttribute('src');
    return;
  }
  const fromLocal = /item-icons\/(\d+)/.test(img.src);
  const fromPersisted = /\/drop\/v1\/icon\/item\/(\d+)/.test(img.src);
  const id =
    img.dataset.itemId ||
    img.src.match(/item-icons\/(\d+)/)?.[1] ||
    img.src.match(/\/drop\/v1\/icon\/item\/(\d+)/)?.[1];
  if ((!fromLocal && !fromPersisted) || !id) {
    // 非物品路径（例如 mob 持久化 404）→ 尝试 CDN
    const mobMatch = img.src.match(/\/drop\/v1\/icon\/(mob|npc)\/(\d+)/);
    if (mobMatch) {
      img.dataset.fallback = '1';
      img.src = getCdnIconUrl(mobMatch[1], mobMatch[2]);
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
    img.src.match(/\/drop\/v1\/icon\/mob\/(\d+)/)?.[1] ||
    img.src.match(/\/mob\/(\d+)\//)?.[1];
  if (!id) {
    img.style.visibility = 'hidden';
    return;
  }
  img.dataset.fallback = '1';
  img.src = getCdnIconUrl('mob', id);
}

export function nothing() {
  return '占位用';
}
