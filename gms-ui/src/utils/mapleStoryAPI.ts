/**
 * CDN 图标（标准 GMS 物品；自定义 ID 常 404）
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
 * 获取 icon 地址。物品优先本地 icon 库（含自定义），其它分类走 CDN。
 * @param category 分类： item / npc / mob 等
 * @param id 物品/NPC/怪物 id
 * @param location 地区：GMS（默认值）
 * @param version 版本：83（默认值）
 */
export function getIconUrl(
  category: string,
  id: string | number,
  location = 'GMS',
  version = '83'
): string {
  if (!id || Number(id) <= 0) return '';
  if (category === 'item') {
    return `/item-icons/${id}.png`;
  }
  return getCdnIconUrl(category, id, location, version);
}

/** <img @error>：本地物品图标缺失时回退 CDN，再失败则隐藏 */
export function onItemIconError(event: Event): void {
  const img = event.target as HTMLImageElement | null;
  if (!img) return;
  if (img.dataset.fallback === '1') {
    img.style.visibility = 'hidden';
    img.removeAttribute('src');
    return;
  }
  // 仅对本地 item-icons 回退；npc/mob 等 CDN 失败直接隐藏
  const fromLocal = /item-icons\/(\d+)/.test(img.src);
  const id = img.dataset.itemId || img.src.match(/item-icons\/(\d+)/)?.[1];
  if (!fromLocal || !id) {
    img.style.visibility = 'hidden';
    return;
  }
  img.dataset.fallback = '1';
  img.src = getCdnIconUrl('item', id);
}

export function nothing() {
  return '占位用';
}
