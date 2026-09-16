<template>
  <PageContainer :title="$t('menu.game.windowCashShop')">
    <ProCard soft class="wcs-client-link">
      <a-alert type="info">
        <template #title>{{
          $t('windowCashShop.section.clientLink.title')
        }}</template>
        {{ $t('windowCashShop.section.clientLink.hint') }}
        <template #action>
          <a-space>
            <a-button type="primary" size="small" @click="goClientSync">
              {{ $t('menu.client.windowCashShopSync') }}
            </a-button>
            <a-button size="small" @click="goAssetHub">
              {{ $t('menu.client.assetHub') }}
            </a-button>
          </a-space>
        </template>
      </a-alert>
    </ProCard>

    <ProCard :title="$t('windowCashShop.section.server')" class="wcs-server">
      <a-space wrap class="bd-page-toolbar">
        <a-button :loading="reloading" @click="reloadAll">
          {{ $t('windowCashShop.reload') }}
        </a-button>
        <a-button @click="importTsvClick">
          {{ $t('windowCashShop.importTsv') }}
        </a-button>
        <a-button :loading="refreshingNames" @click="refreshNamesClick">
          {{ $t('windowCashShop.refreshNames') }}
        </a-button>
        <a-button @click="seedDefaultsClick">
          {{ $t('windowCashShop.seedDefaults') }}
        </a-button>
        <a-button :loading="seedingMount" @click="seedMountClick">
          {{ $t('windowCashShop.seedMount') }}
        </a-button>
      </a-space>
      <a-alert type="info" class="bd-page-toolbar">
        {{ $t('windowCashShop.section.server.hint') }}
      </a-alert>

      <a-row :gutter="12">
        <a-col :span="8">
          <a-card :title="$t('windowCashShop.category')" size="small">
            <template #extra>
              <a-space>
                <a-button type="text" size="mini" @click="openCategoryCreate">
                  {{ $t('windowCashShop.category.add') }}
                </a-button>
              </a-space>
            </template>
            <a-spin :loading="catLoading" style="width: 100%">
              <a-empty
                v-if="!categories.length"
                :description="$t('windowCashShop.category.empty')"
              />
              <div
                v-for="group in categoryGroups"
                :key="group.key"
                class="cat-group"
              >
                <div class="cat-group-title">
                  {{ group.label }}
                  <span class="cat-group-meta">tab {{ group.tab ?? '—' }}</span>
                </div>
                <div
                  v-for="row in group.rows"
                  :key="row.cat.id"
                  class="cat-row"
                  :class="{
                    'active': selectedCategoryId === row.cat.id,
                    'cat-row--child': row.depth > 0,
                  }"
                  :style="{ paddingLeft: `${12 + row.depth * 18}px` }"
                  @click="selectCategory(row.cat.id!)"
                >
                  <div class="cat-main">
                    <div class="cat-name">
                      <a-tag v-if="row.cat.isHot" color="orangered" size="small"
                        >HOT
                      </a-tag>
                      <a-tag
                        v-if="row.depth === 0 && row.hasChildren"
                        size="small"
                        color="arcoblue"
                      >
                        {{ $t('windowCashShop.category.root') }}
                      </a-tag>
                      <a-tag
                        v-else-if="row.depth > 0"
                        size="small"
                        color="green"
                      >
                        {{ $t('windowCashShop.category.child') }}
                      </a-tag>
                      <span>{{ row.cat.name }}</span>
                      <a-tag size="small" color="gray">
                        {{ row.cat.legacyTab ?? '?' }}:{{
                          row.cat.legacyCategory ?? '?'
                        }}
                      </a-tag>
                    </div>
                    <div class="cat-meta">
                      #{{ row.cat.id }} · sort {{ row.cat.sort ?? 0 }} ·
                      {{ row.cat.clickType || 'SHOW_ITEMS' }}
                      <template v-if="row.cat.parentId">
                        · parent #{{ row.cat.parentId }}
                      </template>
                    </div>
                  </div>
                  <div class="cat-actions" @click.stop>
                    <a-switch
                      v-model="row.cat.enabled"
                      :checked-value="1"
                      :unchecked-value="0"
                      size="small"
                      @change="() => quickSaveCategory(row.cat)"
                    />
                    <a-button
                      type="text"
                      size="mini"
                      @click="moveCategory(row.cat, -1)"
                    >
                      {{ $t('windowCashShop.category.moveUp') }}
                    </a-button>
                    <a-button
                      type="text"
                      size="mini"
                      @click="moveCategory(row.cat, 1)"
                    >
                      {{ $t('windowCashShop.category.moveDown') }}
                    </a-button>
                    <a-button
                      type="text"
                      size="mini"
                      @click="openCategoryEdit(row.cat)"
                    >
                      {{ $t('button.edit') }}
                    </a-button>
                    <a-button
                      type="text"
                      size="mini"
                      status="danger"
                      @click="deleteCategoryClick(row.cat)"
                    >
                      {{ $t('button.delete') }}
                    </a-button>
                  </div>
                </div>
              </div>
            </a-spin>
          </a-card>
        </a-col>

        <a-col :span="16">
          <a-card size="small">
            <template #title>
              <span v-if="selectedCategory">
                {{ selectedCategory.name }}
              </span>
              <span v-else>{{ $t('windowCashShop.category.select') }}</span>
            </template>
            <template #extra>
              <a-space v-if="selectedCategory">
                <a-button type="primary" size="small" @click="openItemAdd">
                  {{ $t('windowCashShop.item.add') }}
                </a-button>
                <a-button size="small" @click="openBatchImport">
                  {{ $t('windowCashShop.item.batch') }}
                </a-button>
                <a-button
                  size="small"
                  :loading="reloadingCat"
                  @click="reloadCategoryClick"
                >
                  {{ $t('windowCashShop.reloadCategory') }}
                </a-button>
              </a-space>
            </template>

            <a-table
              v-model:selectedKeys="itemSelectedKeys"
              :loading="itemLoading"
              :data="linkedItems"
              column-resizable
              :pagination="itemTablePagination"
              :bordered="{ cell: true }"
              row-key="rowKey"
              :row-selection="{ type: 'checkbox', showCheckedAll: true }"
            >
              <template #columns>
                <a-table-column
                  :title="$t('windowCashShop.column.iconUrl')"
                  :width="56"
                  align="center"
                >
                  <template #cell="{ record }">
                    <ItemIcon
                      v-if="record.item?.itemId"
                      :id="record.item.itemId"
                      category="item"
                      :size="32"
                      img-class="shop-item-icon"
                      :alt="String(record.item.itemId)"
                    />
                  </template>
                </a-table-column>
                <a-table-column
                  :title="$t('windowCashShop.column.itemId')"
                  :width="100"
                >
                  <template #cell="{ record }">
                    {{ record.item?.itemId }}
                  </template>
                </a-table-column>
                <a-table-column
                  :title="$t('windowCashShop.column.name')"
                  :width="160"
                >
                  <template #cell="{ record }">
                    {{ record.item?.name }}
                  </template>
                </a-table-column>
                <a-table-column
                  :title="$t('windowCashShop.column.price')"
                  :width="80"
                >
                  <template #cell="{ record }">
                    {{ record.item?.price }}
                  </template>
                </a-table-column>
                <a-table-column
                  :title="$t('windowCashShop.column.count')"
                  :width="70"
                >
                  <template #cell="{ record }">
                    {{ record.item?.count }}
                  </template>
                </a-table-column>
                <a-table-column
                  :title="$t('windowCashShop.column.period')"
                  :width="90"
                >
                  <template #cell="{ record }">
                    {{ record.item?.period }}
                  </template>
                </a-table-column>
                <a-table-column
                  :title="$t('windowCashShop.column.linkSort')"
                  :width="90"
                >
                  <template #cell="{ record }">
                    {{ record.link?.sort }}
                  </template>
                </a-table-column>
                <a-table-column
                  :title="$t('windowCashShop.column.enabled')"
                  :width="90"
                >
                  <template #cell="{ record }">
                    <a-switch
                      v-model="record.link.enabled"
                      :checked-value="1"
                      :unchecked-value="0"
                      size="small"
                      @change="() => toggleLinkEnabled(record)"
                    />
                  </template>
                </a-table-column>
                <a-table-column
                  :title="$t('windowCashShop.column.operate')"
                  :width="160"
                  align="center"
                >
                  <template #cell="{ record }">
                    <a-button
                      type="text"
                      size="mini"
                      @click="openItemEdit(record)"
                    >
                      {{ $t('button.edit') }}
                    </a-button>
                    <a-button
                      type="text"
                      size="mini"
                      status="danger"
                      @click="unlinkClick(record)"
                    >
                      {{ $t('windowCashShop.item.unlink') }}
                    </a-button>
                  </template>
                </a-table-column>
              </template>
            </a-table>
          </a-card>
        </a-col>
      </a-row>
    </ProCard>

    <!-- Category drawer -->
    <a-drawer
      :visible="catDrawerVisible"
      :width="480"
      :title="
        categoryForm.id
          ? $t('windowCashShop.category.edit')
          : $t('windowCashShop.category.add')
      "
      unmount-on-close
      @cancel="catDrawerVisible = false"
    >
      <template #footer>
        <a-space>
          <a-button @click="catDrawerVisible = false">
            {{ $t('button.cancel') }}
          </a-button>
          <a-button
            type="primary"
            :loading="savingCat"
            @click="saveCategoryClick"
          >
            {{ $t('button.save') }}
          </a-button>
        </a-space>
      </template>
      <a-form class="bd-overlay-form" :model="categoryForm" layout="vertical">
        <a-form-item :label="$t('windowCashShop.column.name')" required>
          <a-input v-model="categoryForm.name" />
        </a-form-item>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.parentId')">
              <a-input-number
                :model-value="categoryForm.parentId ?? undefined"
                :min="0"
                style="width: 100%"
                allow-clear
                @update:model-value="
                  (v: number | undefined) => {
                    categoryForm.parentId = v ?? null;
                  }
                "
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.sort')">
              <a-input-number
                v-model="categoryForm.sort"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('windowCashShop.column.clickType')">
          <a-select v-model="categoryForm.clickType" allow-search>
            <a-option v-for="ct in clickTypes" :key="ct" :value="ct">
              {{ ct }}
            </a-option>
          </a-select>
          <template #extra>
            {{ $t('windowCashShop.clickType.hint') }}
          </template>
        </a-form-item>
        <a-form-item :label="$t('windowCashShop.column.clickParam')">
          <a-input v-model="categoryForm.clickParam" />
        </a-form-item>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.gateItemId')">
              <ItemIdCell
                :model-value="categoryForm.gateItemId ?? 0"
                @update:model-value="
                  (v: number) => {
                    categoryForm.gateItemId = v > 0 ? v : null;
                  }
                "
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('windowCashShop.column.enabled')">
              <a-switch
                v-model="categoryForm.enabled"
                :checked-value="1"
                :unchecked-value="0"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('windowCashShop.column.isHot')">
              <a-switch
                v-model="categoryForm.isHot"
                :checked-value="1"
                :unchecked-value="0"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.legacyTab')">
              <a-input-number
                :model-value="categoryForm.legacyTab ?? undefined"
                style="width: 100%"
                allow-clear
                @update:model-value="
                  (v: number | undefined) => {
                    categoryForm.legacyTab = v ?? null;
                  }
                "
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.legacyCategory')">
              <a-input-number
                :model-value="categoryForm.legacyCategory ?? undefined"
                style="width: 100%"
                allow-clear
                @update:model-value="
                  (v: number | undefined) => {
                    categoryForm.legacyCategory = v ?? null;
                  }
                "
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('windowCashShop.column.remark')">
          <a-input v-model="categoryForm.remark" />
        </a-form-item>
      </a-form>
    </a-drawer>

    <!-- Add / edit item drawer -->
    <a-drawer
      :visible="itemDrawerVisible"
      :width="420"
      :title="
        itemEditMode
          ? $t('windowCashShop.item.edit')
          : $t('windowCashShop.item.add')
      "
      unmount-on-close
      @cancel="itemDrawerVisible = false"
    >
      <template #footer>
        <a-space>
          <a-button @click="itemDrawerVisible = false">
            {{ $t('button.cancel') }}
          </a-button>
          <a-button type="primary" :loading="savingItem" @click="saveItemClick">
            {{ $t('button.save') }}
          </a-button>
        </a-space>
      </template>
      <a-form class="bd-overlay-form" :model="itemForm" layout="vertical">
        <a-form-item :label="$t('windowCashShop.column.itemId')" required>
          <ItemIdCell
            :model-value="itemForm.itemId ?? 0"
            :editable="!itemEditMode"
            @update:model-value="
              (v: number) => {
                itemForm.itemId = v;
                onItemIdChange(v);
              }
            "
          />
        </a-form-item>
        <a-alert
          v-if="assetHint"
          class="bd-page-toolbar"
          :type="assetOk ? 'success' : 'warning'"
        >
          {{ assetHint }}
        </a-alert>
        <a-form-item :label="$t('windowCashShop.column.name')">
          <a-input
            v-model="itemForm.name"
            :placeholder="$t('windowCashShop.item.nameAuto')"
          />
        </a-form-item>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.price')">
              <a-input-number
                v-model="itemForm.price"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.count')">
              <a-input-number
                v-model="itemForm.count"
                :min="1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.period')">
              <a-input-number
                v-model="itemForm.period"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.gender')">
              <a-input-number
                v-model="itemForm.gender"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('windowCashShop.column.linkSort')">
          <a-input-number v-model="linkSort" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('windowCashShop.column.enabled')">
          <a-switch
            v-model="itemForm.enabled"
            :checked-value="1"
            :unchecked-value="0"
          />
        </a-form-item>
        <a-form-item :label="$t('windowCashShop.item.requireClient')">
          <a-switch v-model="requireClient" />
        </a-form-item>
        <a-form-item :label="$t('windowCashShop.column.remark')">
          <a-input v-model="itemForm.remark" />
        </a-form-item>
      </a-form>
    </a-drawer>

    <!-- Batch import drawer -->
    <a-drawer
      :visible="batchVisible"
      :width="560"
      :title="$t('windowCashShop.batch.title')"
      unmount-on-close
      @cancel="batchVisible = false"
    >
      <template #footer>
        <a-space>
          <a-button @click="batchVisible = false">
            {{ $t('button.cancel') }}
          </a-button>
          <a-button
            type="primary"
            :loading="batchImporting"
            :disabled="!batchSelected.length"
            @click="batchImportClick"
          >
            {{ $t('windowCashShop.batch.import') }}
          </a-button>
        </a-space>
      </template>
      <a-form :model="batchQuery" layout="inline" class="bd-page-toolbar">
        <a-form-item :label="$t('windowCashShop.batch.minId')">
          <a-input-number v-model="batchQuery.minId" :min="0" />
        </a-form-item>
        <a-form-item :label="$t('windowCashShop.batch.maxId')">
          <a-input-number v-model="batchQuery.maxId" :min="0" />
        </a-form-item>
        <a-form-item :label="$t('windowCashShop.batch.keyword')">
          <a-input v-model="batchQuery.keyword" style="width: 140px" />
        </a-form-item>
        <a-form-item>
          <a-button
            type="primary"
            :loading="batchSearching"
            @click="batchSearch"
          >
            {{ $t('windowCashShop.batch.search') }}
          </a-button>
        </a-form-item>
      </a-form>
      <a-form-item :label="$t('windowCashShop.batch.defaultPrice')">
        <a-input-number v-model="batchPrice" :min="0" style="width: 160px" />
      </a-form-item>
      <a-table
        v-model:selectedKeys="batchSelected"
        :data="batchRows"
        :loading="batchSearching"
        row-key="itemId"
        :pagination="batchTablePagination"
        :row-selection="{ type: 'checkbox', showCheckedAll: true }"
      >
        <template #columns>
          <a-table-column
            :title="$t('windowCashShop.column.itemId')"
            data-index="itemId"
            :width="100"
          />
          <a-table-column
            :title="$t('windowCashShop.column.name')"
            data-index="name"
          />
        </template>
      </a-table>
    </a-drawer>
  </PageContainer>
</template>

<script setup lang="ts">
  import { computed, reactive, ref } from 'vue';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import {
    BrowseItemRow,
    LinkedItemRow,
    XyCashShopCategoryDO,
    XyCashShopItemDO,
    browseItems,
    checkItemAsset,
    deleteCategory,
    getCategories,
    getClickTypes,
    getItemsGrouped,
    importItems,
    importTsv,
    linkItem,
    reloadCategory,
    reloadWindowCashShop,
    refreshNamesFromWz,
    saveCategory,
    saveItem,
    seedDefaults,
    seedMountCatalog,
    unlinkItem,
  } from '@/api/windowCashShop';
  import { useRouter } from 'vue-router';

  interface TableRow extends LinkedItemRow {
    rowKey: string;
  }

  const { t } = useI18n();
  const router = useRouter();

  const goClientSync = () => {
    router.push({ name: 'ClientWindowCashShopSync' });
  };

  const goAssetHub = () => {
    router.push({ name: 'ClientAssetHub' });
  };

  const reloading = ref(false);
  const refreshingNames = ref(false);
  const reloadingCat = ref(false);
  const itemSelectedKeys = ref<(string | number)[]>([]);

  const categories = ref<XyCashShopCategoryDO[]>([]);
  const clickTypes = ref<string[]>([]);
  const selectedCategoryId = ref<number | null>(null);
  const linkedItems = ref<TableRow[]>([]);
  const { loading: catLoading, setLoading: setCatLoading } = useLoading(false);
  const { loading: itemLoading, setLoading: setItemLoading } =
    useLoading(false);

  const catDrawerVisible = ref(false);
  const savingCat = ref(false);
  const categoryForm = reactive<XyCashShopCategoryDO>({
    name: '',
    sort: 0,
    enabled: 1,
    clickType: 'SHOW_ITEMS',
    isHot: 0,
  });

  const itemDrawerVisible = ref(false);
  const itemEditMode = ref(false);
  const savingItem = ref(false);
  const requireClient = ref(false);
  const linkSort = ref(0);
  const assetHint = ref('');
  const assetOk = ref(false);
  const itemForm = reactive<XyCashShopItemDO>({
    itemId: 0,
    price: 0,
    count: 1,
    period: 0,
    gender: 0,
    name: '',
    enabled: 1,
    remark: '',
  });

  const batchVisible = ref(false);
  const batchSearching = ref(false);
  const batchImporting = ref(false);
  const batchRows = ref<BrowseItemRow[]>([]);
  const batchSelected = ref<(string | number)[]>([]);
  const batchPrice = ref(0);
  const batchQuery = reactive({
    minId: undefined as number | undefined,
    maxId: undefined as number | undefined,
    keyword: '',
  });

  const tablePageSizeOptions = [20, 50, 100, 200];
  const itemTablePagination = {
    pageSize: 50,
    pageSizeOptions: tablePageSizeOptions,
    showPageSize: true,
    showTotal: true,
    showJumper: true,
  };
  const batchTablePagination = {
    pageSize: 50,
    pageSizeOptions: tablePageSizeOptions,
    showPageSize: true,
    showTotal: true,
    showJumper: true,
  };

  const selectedCategory = computed(() =>
    categories.value.find((c) => c.id === selectedCategoryId.value)
  );

  /** 与客户端 kTabs 对齐的一级 Tab 名 */
  const TAB_LABELS: Record<number, string> = {
    2: '装备',
    3: '消耗',
    5: '设置',
    6: '宠物',
    7: '礼包',
    8: '热门',
    9: '皮肤',
    10: 'XY玩法',
    11: '坐骑',
  };

  type CatTreeRow = {
    cat: XyCashShopCategoryDO;
    depth: number;
    hasChildren: boolean;
  };

  /** 按 parentId 展平成树行：根在前，子缩进；无父或父不在本组则当根。 */
  const flattenByParent = (cats: XyCashShopCategoryDO[]): CatTreeRow[] => {
    const idSet = new Set(
      cats.map((c) => c.id).filter((id): id is number => id != null)
    );
    const children = new Map<number | null, XyCashShopCategoryDO[]>();
    cats.forEach((cat) => {
      const pid =
        cat.parentId != null && idSet.has(cat.parentId) ? cat.parentId : null;
      const list = children.get(pid) ?? [];
      list.push(cat);
      children.set(pid, list);
    });
    const sortPeers = (list: XyCashShopCategoryDO[]) =>
      [...list].sort(
        (a, b) =>
          (a.legacyCategory ?? 0) - (b.legacyCategory ?? 0) ||
          (a.sort ?? 0) - (b.sort ?? 0) ||
          (a.id ?? 0) - (b.id ?? 0)
      );
    const out: CatTreeRow[] = [];
    const walk = (parentKey: number | null, depth: number) => {
      const peers = sortPeers(children.get(parentKey) ?? []);
      peers.forEach((cat) => {
        const kids = children.get(cat.id ?? -1) ?? [];
        out.push({ cat, depth, hasChildren: kids.length > 0 });
        if (cat.id != null) {
          walk(cat.id, depth + 1);
        }
      });
    };
    walk(null, 0);
    return out;
  };

  const categoryGroups = computed(() => {
    const order = [2, 3, 5, 6, 7, 8, 9, 10, 11];
    const byTab = new Map<number | 'other', XyCashShopCategoryDO[]>();
    categories.value.forEach((cat) => {
      const tab =
        cat.legacyTab == null || Number.isNaN(cat.legacyTab)
          ? 'other'
          : cat.legacyTab;
      const list = byTab.get(tab) ?? [];
      list.push(cat);
      byTab.set(tab, list);
    });

    const orderedGroups = order
      .filter((tab) => (byTab.get(tab)?.length ?? 0) > 0)
      .map((tab) => {
        const cats = byTab.get(tab) ?? [];
        byTab.delete(tab);
        return {
          key: `tab-${tab}`,
          tab,
          label: TAB_LABELS[tab] ?? `Tab ${tab}`,
          rows: flattenByParent(cats),
        };
      });

    const restGroups = Array.from(byTab.entries()).map(([tab, cats]) => ({
      key: `tab-${tab}`,
      tab: typeof tab === 'number' ? tab : null,
      label:
        typeof tab === 'number'
          ? TAB_LABELS[tab] ?? `Tab ${tab}`
          : t('windowCashShop.category.ungrouped'),
      rows: flattenByParent(cats),
    }));

    return [...orderedGroups, ...restGroups];
  });

  const loadClickTypes = async () => {
    const { data } = await getClickTypes();
    clickTypes.value = data || [];
  };

  const loadCategories = async () => {
    setCatLoading(true);
    try {
      const { data } = await getCategories();
      categories.value = (data || []).slice().sort((a, b) => {
        const sa = a.sort ?? 0;
        const sb = b.sort ?? 0;
        if (sa !== sb) return sa - sb;
        return (a.id ?? 0) - (b.id ?? 0);
      });
      if (
        selectedCategoryId.value != null &&
        !categories.value.some((c) => c.id === selectedCategoryId.value)
      ) {
        selectedCategoryId.value = null;
        linkedItems.value = [];
      }
    } finally {
      setCatLoading(false);
    }
  };

  const loadLinkedItems = async () => {
    if (selectedCategoryId.value == null) {
      linkedItems.value = [];
      return;
    }
    setItemLoading(true);
    try {
      const { data } = await getItemsGrouped();
      const group = (data || []).find(
        (g) => g.category?.id === selectedCategoryId.value
      );
      linkedItems.value = (group?.items || []).map((row) => ({
        ...row,
        rowKey: `${row.link?.categoryId}_${row.item?.itemId}`,
      }));
    } finally {
      setItemLoading(false);
    }
  };

  const selectCategory = (id: number) => {
    selectedCategoryId.value = id;
    itemSelectedKeys.value = [];
    loadLinkedItems();
  };

  const reloadAll = async () => {
    reloading.value = true;
    try {
      const { data } = await reloadWindowCashShop();
      Message.success(
        `${t('windowCashShop.msg.reloaded')}: source=${data?.source}, size=${
          data?.size
        }`
      );
    } finally {
      reloading.value = false;
    }
  };

  const reloadCategoryClick = async () => {
    if (selectedCategoryId.value == null) return;
    reloadingCat.value = true;
    try {
      await reloadCategory(selectedCategoryId.value);
      Message.success(t('windowCashShop.msg.reloaded'));
    } finally {
      reloadingCat.value = false;
    }
  };

  const importTsvClick = () => {
    Modal.confirm({
      title: t('windowCashShop.importTsv.confirm'),
      content: t('windowCashShop.importTsv.confirm'),
      onOk: async () => {
        const { data } = await importTsv(true);
        Message.success(
          `${t('windowCashShop.msg.importDone')}: ${JSON.stringify(data)}`
        );
        await loadCategories();
        await loadLinkedItems();
      },
    });
  };

  const refreshNamesClick = () => {
    Modal.confirm({
      title: t('windowCashShop.refreshNames.confirm'),
      content: t('windowCashShop.refreshNames.confirm'),
      onOk: async () => {
        refreshingNames.value = true;
        try {
          const { data } = await refreshNamesFromWz();
          Message.success(
            `${t('windowCashShop.msg.refreshNamesDone')}: updated=${
              data?.updated
            }, skipped=${data?.skipped}`
          );
          await loadLinkedItems();
        } finally {
          refreshingNames.value = false;
        }
      },
    });
  };

  const seedDefaultsClick = async () => {
    await seedDefaults();
    Message.success(t('windowCashShop.msg.seedDone'));
    await loadCategories();
  };

  const seedingMount = ref(false);
  const seedMountClick = async () => {
    seedingMount.value = true;
    try {
      const { data } = await seedMountCatalog();
      const mounts = (data as any)?.mountIds ?? '?';
      const saddles = (data as any)?.saddleIds ?? '?';
      const use = (data as any)?.useIds ?? '?';
      Message.success(
        t('windowCashShop.msg.seedMountDone', { mounts, saddles, use })
      );
      await loadCategories();
      if (selectedCategoryId.value != null) {
        await loadLinkedItems();
      }
    } finally {
      seedingMount.value = false;
    }
  };

  const emptyCategoryForm = (): XyCashShopCategoryDO => ({
    name: '',
    parentId: undefined,
    sort: categories.value.length,
    enabled: 1,
    clickType: 'SHOW_ITEMS',
    clickParam: '',
    gateItemId: undefined,
    isHot: 0,
    legacyTab: undefined,
    legacyCategory: undefined,
    remark: '',
  });

  const openCategoryCreate = () => {
    Object.assign(categoryForm, emptyCategoryForm(), { id: undefined });
    catDrawerVisible.value = true;
  };

  const openCategoryEdit = (cat: XyCashShopCategoryDO) => {
    Object.assign(categoryForm, { ...cat });
    catDrawerVisible.value = true;
  };

  const saveCategoryClick = async () => {
    if (!categoryForm.name?.trim()) {
      Message.warning(t('windowCashShop.column.name'));
      return;
    }
    savingCat.value = true;
    try {
      await saveCategory({ ...categoryForm });
      Message.success(t('windowCashShop.msg.saved'));
      catDrawerVisible.value = false;
      await loadCategories();
    } finally {
      savingCat.value = false;
    }
  };

  const quickSaveCategory = async (cat: XyCashShopCategoryDO) => {
    await saveCategory({ ...cat });
    Message.success(t('windowCashShop.msg.saved'));
  };

  const deleteCategoryClick = (cat: XyCashShopCategoryDO) => {
    const categoryId = cat.id;
    if (categoryId == null) return;
    Modal.confirm({
      title: t('windowCashShop.category.deleteConfirm'),
      content: t('windowCashShop.category.deleteConfirm'),
      onOk: async () => {
        await deleteCategory(categoryId);
        if (selectedCategoryId.value === categoryId) {
          selectedCategoryId.value = null;
          linkedItems.value = [];
        }
        Message.success(t('message.success'));
        await loadCategories();
      },
    });
  };

  const moveCategory = async (cat: XyCashShopCategoryDO, delta: number) => {
    const peers = categories.value
      .filter(
        (c) =>
          (c.legacyTab ?? null) === (cat.legacyTab ?? null) &&
          (c.parentId ?? null) === (cat.parentId ?? null)
      )
      .slice()
      .sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0));
    const idx = peers.findIndex((c) => c.id === cat.id);
    const target = idx + delta;
    if (idx < 0 || target < 0 || target >= peers.length) return;
    const reordered = peers.slice();
    const [moved] = reordered.splice(idx, 1);
    reordered.splice(target, 0, moved);
    const updates = reordered.map((c, i) => ({ ...c, sort: i }));
    categories.value = categories.value.map((c) => {
      const u = updates.find((x) => x.id === c.id);
      return u ?? c;
    });
    await Promise.all(updates.map((c) => saveCategory(c)));
    Message.success(t('windowCashShop.msg.reordered'));
    await loadCategories();
  };

  const openItemAdd = () => {
    itemEditMode.value = false;
    Object.assign(itemForm, {
      itemId: undefined,
      price: 0,
      count: 1,
      period: 0,
      gender: 0,
      name: '',
      enabled: 1,
      remark: '',
      iconUrl: '',
    });
    linkSort.value = linkedItems.value.length;
    requireClient.value = false;
    assetHint.value = '';
    assetOk.value = false;
    itemDrawerVisible.value = true;
  };

  const openItemEdit = (row: TableRow) => {
    itemEditMode.value = true;
    Object.assign(itemForm, {
      itemId: row.item.itemId,
      price: row.item.price ?? 0,
      count: row.item.count ?? 1,
      period: row.item.period ?? 0,
      gender: row.item.gender ?? 0,
      name: row.item.name ?? '',
      enabled: row.item.enabled ?? 1,
      remark: row.item.remark ?? '',
      iconUrl: row.item.iconUrl ?? '',
    });
    linkSort.value = row.link.sort ?? 0;
    requireClient.value = false;
    assetHint.value = '';
    assetOk.value = true;
    itemDrawerVisible.value = true;
    onItemIdChange(row.item.itemId);
  };

  const onItemIdChange = async (val: number | undefined) => {
    assetHint.value = '';
    if (!val) return;
    try {
      const { data } = await checkItemAsset(val);
      assetOk.value = !!data?.serverOk;
      assetHint.value =
        (data?.messages || []).join('; ') ||
        (assetOk.value
          ? t('windowCashShop.item.assetOk')
          : t('windowCashShop.item.assetFail'));
    } catch {
      assetOk.value = false;
      assetHint.value = t('windowCashShop.item.assetFail');
    }
  };

  const saveItemClick = async () => {
    if (!itemForm.itemId || selectedCategoryId.value == null) {
      Message.warning(t('windowCashShop.column.itemId'));
      return;
    }
    savingItem.value = true;
    try {
      const payload: XyCashShopItemDO = {
        ...itemForm,
        name: itemForm.name?.trim() || undefined,
      };
      const { data } = await saveItem(payload, requireClient.value);
      if (data?.name) {
        itemForm.name = data.name;
      }
      await linkItem({
        categoryId: selectedCategoryId.value,
        itemId: data.itemId ?? itemForm.itemId,
        sort: linkSort.value,
        enabled: itemForm.enabled ?? 1,
      });
      Message.success(
        itemEditMode.value
          ? t('windowCashShop.msg.saved')
          : t('windowCashShop.msg.linked')
      );
      itemDrawerVisible.value = false;
      await loadLinkedItems();
    } finally {
      savingItem.value = false;
    }
  };

  const toggleLinkEnabled = async (row: TableRow) => {
    if (selectedCategoryId.value == null) return;
    await linkItem({
      categoryId: selectedCategoryId.value,
      itemId: row.item.itemId,
      sort: row.link.sort,
      enabled: row.link.enabled,
    });
    Message.success(t('windowCashShop.msg.saved'));
  };

  const unlinkClick = (row: TableRow) => {
    const categoryId = selectedCategoryId.value;
    if (categoryId == null) return;
    Modal.confirm({
      title: t('windowCashShop.item.unlinkConfirm'),
      content: t('windowCashShop.item.unlinkConfirm'),
      onOk: async () => {
        await unlinkItem({
          categoryId,
          itemId: row.item.itemId,
        });
        Message.success(t('windowCashShop.msg.unlinked'));
        await loadLinkedItems();
      },
    });
  };

  const openBatchImport = () => {
    batchRows.value = [];
    batchSelected.value = [];
    batchVisible.value = true;
  };

  const batchSearch = async () => {
    batchSearching.value = true;
    try {
      const { data } = await browseItems({
        minId: batchQuery.minId,
        maxId: batchQuery.maxId,
        keyword: batchQuery.keyword || undefined,
      });
      batchRows.value = data || [];
    } catch {
      batchRows.value = [];
    } finally {
      batchSearching.value = false;
    }
  };

  const batchImportClick = async () => {
    if (selectedCategoryId.value == null || !batchSelected.value.length) return;
    batchImporting.value = true;
    try {
      await importItems({
        categoryId: selectedCategoryId.value,
        itemIds: batchSelected.value.map(Number),
        price: batchPrice.value,
        requireClient: requireClient.value,
      });
      Message.success(t('windowCashShop.msg.importDone'));
      batchVisible.value = false;
      await loadLinkedItems();
    } finally {
      batchImporting.value = false;
    }
  };

  const init = async () => {
    await Promise.all([loadClickTypes(), loadCategories()]);
  };

  init();
</script>

<script lang="ts">
  export default {
    name: 'WindowCashShop',
  };
</script>

<style lang="less" scoped>
  .wcs-client-link {
    margin-bottom: 0;
  }

  .wcs-server {
    margin-top: 12px;
  }

  .cat-group {
    margin-bottom: 10px;
  }

  .cat-group-title {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin: 4px 0 6px;
    padding: 0 2px;
    font-size: 13px;
    font-weight: 600;
    color: var(--color-text-1);
  }

  .cat-group-meta {
    font-size: 12px;
    font-weight: 400;
    color: var(--color-text-3);
  }

  .cat-row {
    display: flex;
    flex-direction: column;
    gap: 4px;
    padding: 8px 10px;
    margin-bottom: 6px;
    margin-left: 6px;
    border: 1px solid var(--color-border-2);
    border-radius: 4px;
    cursor: pointer;
    transition: background 0.15s;

    &:hover {
      background: var(--color-fill-2);
    }

    &.active {
      border-color: rgb(var(--primary-6));
      background: var(--color-primary-light-1);
    }

    &.cat-row--child {
      border-left: 3px solid rgb(var(--primary-6));
      background: var(--color-fill-1);
    }
  }

  .cat-main {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .cat-name {
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 500;
  }

  .cat-meta {
    font-size: 12px;
    color: var(--color-text-3);
  }

  .cat-actions {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 2px;
  }

  .shop-item-icon {
    width: 32px;
    height: 32px;
    object-fit: contain;
    image-rendering: pixelated;
  }

  .dir-item {
    cursor: pointer;

    &:hover {
      background: var(--color-fill-2);
    }
  }
</style>
