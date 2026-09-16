import axios from 'axios';

export interface XyCashShopCategoryDO {
  id?: number;
  name: string;
  parentId?: number | null;
  sort?: number;
  enabled?: number;
  clickType?: string;
  clickParam?: string;
  gateItemId?: number | null;
  isHot?: number;
  legacyTab?: number | null;
  legacyCategory?: number | null;
  remark?: string;
  updatedAt?: string;
}

export interface XyCashShopItemDO {
  itemId: number;
  price?: number;
  count?: number;
  period?: number;
  gender?: number;
  name?: string;
  iconUrl?: string;
  enabled?: number;
  remark?: string;
  updatedAt?: string;
}

export interface XyCashShopCategoryItemDO {
  id?: number;
  categoryId: number;
  itemId: number;
  sort?: number;
  enabled?: number;
  updatedAt?: string;
}

export interface LinkedItemRow {
  link: XyCashShopCategoryItemDO;
  item: XyCashShopItemDO;
}

export interface GroupedCategoryItems {
  category: XyCashShopCategoryDO;
  items: LinkedItemRow[];
}

export interface AssetCheckResult {
  serverOk: boolean;
  clientOk: boolean;
  clientSkipped: boolean;
  messages: string[];
}

export interface BrowseItemRow {
  itemId: number;
  name?: string;
}

export interface BrowseItemsQuery {
  minId?: number;
  maxId?: number;
  keyword?: string;
}

export interface ImportItemsBody {
  categoryId: number;
  itemIds: number[];
  price?: number;
  requireClient?: boolean;
}

export type {
  ClientDataPathInfo,
  DirectoryEntry,
  PathValidateResult,
} from '@/api/clientPath';

export {
  getClientDataPath,
  setClientDataPath,
  validateClientDataPath,
  listDirectories,
} from '@/api/clientPath';

export function getClickTypes() {
  return axios.get<any, { data: string[] }>('/windowCashShop/v1/clickTypes');
}

export function getCategories() {
  return axios.get<any, { data: XyCashShopCategoryDO[] }>(
    '/windowCashShop/v1/categories'
  );
}

export function saveCategory(data: XyCashShopCategoryDO) {
  return axios.post<any, { data: XyCashShopCategoryDO }>(
    '/windowCashShop/v1/category/save',
    data
  );
}

export function deleteCategory(id: number) {
  return axios.post('/windowCashShop/v1/category/delete', id);
}

export function getItems() {
  return axios.get<any, { data: XyCashShopItemDO[] }>(
    '/windowCashShop/v1/items'
  );
}

export function getItemsGrouped() {
  return axios.get<any, { data: GroupedCategoryItems[] }>(
    '/windowCashShop/v1/itemsGrouped'
  );
}

export function saveItem(data: XyCashShopItemDO, requireClient = false) {
  return axios.post<any, { data: XyCashShopItemDO }>(
    `/windowCashShop/v1/item/save?requireClient=${requireClient}`,
    data
  );
}

export function checkItemAsset(itemId: number) {
  return axios.get<any, { data: AssetCheckResult }>(
    `/windowCashShop/v1/item/checkAsset/${itemId}`
  );
}

export function linkItem(payload: {
  categoryId: number;
  itemId: number;
  sort?: number;
  enabled?: number;
}) {
  return axios.post('/windowCashShop/v1/link', payload);
}

export function unlinkItem(payload: { categoryId: number; itemId: number }) {
  return axios.post('/windowCashShop/v1/unlink', payload);
}

export function reloadWindowCashShop() {
  return axios.post<any, { data: Record<string, unknown> }>(
    '/windowCashShop/v1/reload'
  );
}

export function reloadCategory(categoryId: number) {
  return axios.post<any, { data: Record<string, unknown> }>(
    '/windowCashShop/v1/reloadCategory',
    categoryId
  );
}

export function importTsv(onlyIfEmpty = true) {
  return axios.post<any, { data: Record<string, unknown> }>(
    `/windowCashShop/v1/importTsv?onlyIfEmpty=${onlyIfEmpty}`
  );
}

/** Browse WZ/catalog items for batch import */
export function browseItems(query: BrowseItemsQuery) {
  return axios.post<any, { data: BrowseItemRow[] }>(
    '/windowCashShop/v1/browseItems',
    query
  );
}

/** Import selected item IDs into a category */
export function importItems(body: ImportItemsBody) {
  return axios.post('/windowCashShop/v1/importItems', body);
}

/** Reorder categories; data = category ids in display order */
export function reorderCategories(categoryIds: number[]) {
  return axios.post('/windowCashShop/v1/reorderCategories', categoryIds);
}

/** Seed default categories (hot / skin / XY play / mount) */
export function seedDefaults() {
  return axios.post('/windowCashShop/v1/seedDefaults');
}

/** Ensure mount tree + import all 190/191/226 into L2 buckets */
export function seedMountCatalog() {
  return axios.post('/windowCashShop/v1/seedMountCatalog');
}

export interface RefreshNamesResult {
  updated: number;
  skipped: number;
}

/** 批量将 DB 商品名刷成 WZ 中文名 */
export function refreshNamesFromWz() {
  return axios.post<any, { data: RefreshNamesResult }>(
    '/windowCashShop/v1/refreshNamesFromWz'
  );
}

export type IconSyncMode = 'fillEmpty' | 'force';

export interface IconSyncReq {
  mode: IconSyncMode;
  itemIds?: number[];
  categoryId?: number;
}

export interface IconSyncResult {
  mode?: string;
  iconDir?: string;
  requested?: number;
  updated?: number;
  skipped?: number;
  filesWritten?: number;
  failed?: number;
  message?: string;
}

export interface ClientSyncReq {
  fillIcons?: boolean;
  defaultPrice?: number;
  cashOnly?: boolean;
}

export interface ClientSyncResult {
  clientDataPath?: string;
  categoriesCreated?: number;
  categoriesUpdated?: number;
  itemsUpserted?: number;
  linksUpserted?: number;
  iconsFilled?: number;
  scanned?: number;
  skipped?: number;
  categoriesPruned?: number;
  linksMigrated?: number;
  catalogReloaded?: boolean;
  catalogSource?: string;
  catalogSize?: number;
  durationMs?: number;
  emptyReason?: string;
  message?: string;
}

/** @deprecated Prefer POST /asset/v1/ensure via Game Asset Hub. Kept for API compatibility. */
export function syncIcons(body: IconSyncReq) {
  return axios.post<any, { data: IconSyncResult }>(
    '/windowCashShop/v1/syncIcons',
    body
  );
}

/** 从已保存 ClientDataPath 同步分类与商品（可能较久，单独加长超时） */
export function syncFromClientData(body: ClientSyncReq = {}) {
  return axios.post<any, { data: ClientSyncResult }>(
    '/windowCashShop/v1/syncFromClientData',
    body,
    { timeout: 600_000 }
  );
}
