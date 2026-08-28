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

export interface ClientDataPathInfo {
  configured?: string;
  resolved?: string;
  jvmProperty?: string;
  configCode?: string;
  ok?: boolean;
  skipped?: boolean;
  warning?: boolean;
  message?: string;
}

export interface PathValidateResult {
  ok?: boolean;
  skipped?: boolean;
  warning?: boolean;
  path?: string;
  message?: string;
}

export interface DirectoryEntry {
  name: string;
  path: string;
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

/** POST body is wrapped by axios interceptor as `{ data }`. Empty string clears path. */
export function getClientDataPath() {
  return axios.get<any, { data: ClientDataPathInfo }>(
    '/windowCashShop/v1/clientDataPath'
  );
}

export function setClientDataPath(path: string) {
  // interceptor skips falsy body; whitespace-only clears on server (hasText)
  return axios.post<any, { data: ClientDataPathInfo }>(
    '/windowCashShop/v1/clientDataPath',
    path === '' ? ' ' : path
  );
}

export function validateClientDataPath(path: string) {
  return axios.post<any, { data: PathValidateResult }>(
    '/windowCashShop/v1/clientDataPath/validate',
    path === '' ? ' ' : path
  );
}

export function listDirectories(absolutePath: string) {
  return axios.post<any, { data: DirectoryEntry[] }>(
    '/windowCashShop/v1/listDirectories',
    absolutePath
  );
}

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

/** Stub — server will add soon */
export function browseItems(query: BrowseItemsQuery) {
  return axios.post<any, { data: BrowseItemRow[] }>(
    '/windowCashShop/v1/browseItems',
    query
  );
}

/** Stub — server will add soon */
export function importItems(body: ImportItemsBody) {
  return axios.post('/windowCashShop/v1/importItems', body);
}

/** Stub — server will add soon; data = category ids in display order */
export function reorderCategories(categoryIds: number[]) {
  return axios.post('/windowCashShop/v1/reorderCategories', categoryIds);
}

/** Stub — server will add soon */
export function seedDefaults() {
  return axios.post('/windowCashShop/v1/seedDefaults');
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

/** fillEmpty=仅空 icon_url；force=覆盖。可选 itemIds / categoryId */
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
