<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.windowCashShop')">
      <a-space wrap style="margin-bottom: 12px">
        <span>{{ $t('windowCashShop.path') }}</span>
        <a-input
          v-model="clientPath"
          :placeholder="$t('windowCashShop.path.placeholder')"
          style="width: 360px"
          allow-clear
        />
        <a-button @click="openBrowse"
          >{{ $t('windowCashShop.path.browse') }}
        </a-button>
        <a-button :loading="validating" @click="validatePath"
          >{{ $t('windowCashShop.path.validate') }}
        </a-button>
        <a-button type="primary" :loading="savingPath" @click="savePath"
          >{{ $t('windowCashShop.path.save') }}
        </a-button>
        <a-button @click="clearPath"
          >{{ $t('windowCashShop.path.clear') }}
        </a-button>
        <a-button
          type="outline"
          :loading="syncingClient"
          @click="syncFromClientClick"
          >{{ $t('windowCashShop.syncFromClient') }}
        </a-button>
        <a-button :loading="reloading" @click="reloadAll"
          >{{ $t('windowCashShop.reload') }}
        </a-button>
        <a-button @click="importTsvClick"
          >{{ $t('windowCashShop.importTsv') }}
        </a-button>
        <a-button :loading="refreshingNames" @click="refreshNamesClick"
          >{{ $t('windowCashShop.refreshNames') }}
        </a-button>
        <a-button :loading="syncingIconsEmpty" @click="syncIconsEmptyClick"
          >{{ $t('windowCashShop.syncIconsEmpty') }}
        </a-button>
        <a-button :loading="syncingIconsForce" @click="syncIconsForceClick"
          >{{ $t('windowCashShop.syncIconsForce') }}
        </a-button>
        <a-button @click="seedDefaultsClick"
          >{{ $t('windowCashShop.seedDefaults') }}
        </a-button>
      </a-space>
      <a-alert
        v-if="pathInfo"
        style="margin-bottom: 12px"
        :type="pathAlertType"
        :title="$t('windowCashShop.path.status')"
      >
        {{ pathStatusText }}
      </a-alert>
      <a-alert type="info" style="margin-bottom: 12px">
        {{ $t('windowCashShop.path.hint') }}
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
                v-for="cat in categories"
                :key="cat.id"
                class="cat-row"
                :class="{ active: selectedCategoryId === cat.id }"
                @click="selectCategory(cat.id!)"
              >
                <div class="cat-main">
                  <div class="cat-name">
                    <a-tag v-if="cat.isHot" color="orangered" size="small"
                      >HOT
                    </a-tag>
                    <span>{{ cat.name }}</span>
                    <a-tag size="small" color="arcoblue"
                      >{{ cat.clickType || 'SHOW_ITEMS' }}
                    </a-tag>
                  </div>
                  <div class="cat-meta">
                    #{{ cat.id }} · sort {{ cat.sort ?? 0 }}
                  </div>
                </div>
                <div class="cat-actions" @click.stop>
                  <a-switch
                    v-model="cat.enabled"
                    :checked-value="1"
                    :unchecked-value="0"
                    size="small"
                    @change="() => quickSaveCategory(cat)"
                  />
                  <a-button
                    type="text"
                    size="mini"
                    @click="moveCategory(cat, -1)"
                  >
                    {{ $t('windowCashShop.category.moveUp') }}
                  </a-button>
                  <a-button
                    type="text"
                    size="mini"
                    @click="moveCategory(cat, 1)"
                  >
                    {{ $t('windowCashShop.category.moveDown') }}
                  </a-button>
                  <a-button
                    type="text"
                    size="mini"
                    @click="openCategoryEdit(cat)"
                  >
                    {{ $t('button.edit') }}
                  </a-button>
                  <a-button
                    type="text"
                    size="mini"
                    status="danger"
                    @click="deleteCategoryClick(cat)"
                  >
                    {{ $t('button.delete') }}
                  </a-button>
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
                    <img
                      v-if="record.item?.itemId"
                      class="shop-item-icon"
                      :src="shopItemIconUrl(record)"
                      :alt="String(record.item.itemId)"
                      :data-item-id="record.item.itemId"
                      :data-skip-cdn="shopIconSkipCdn(record) ? '1' : undefined"
                      @error="onItemIconError"
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
                  :width="120"
                  align="center"
                >
                  <template #cell="{ record }">
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
    </a-card>

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
      <a-form :model="categoryForm" layout="vertical">
        <a-form-item :label="$t('windowCashShop.column.name')" required>
          <a-input v-model="categoryForm.name" />
        </a-form-item>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.parentId')">
              <a-input-number
                v-model="categoryForm.parentId"
                :min="0"
                style="width: 100%"
                allow-clear
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
        </a-form-item>
        <a-form-item :label="$t('windowCashShop.column.clickParam')">
          <a-input v-model="categoryForm.clickParam" />
        </a-form-item>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.gateItemId')">
              <a-input-number
                v-model="categoryForm.gateItemId"
                :min="0"
                style="width: 100%"
                allow-clear
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
                v-model="categoryForm.legacyTab"
                style="width: 100%"
                allow-clear
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('windowCashShop.column.legacyCategory')">
              <a-input-number
                v-model="categoryForm.legacyCategory"
                style="width: 100%"
                allow-clear
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('windowCashShop.column.remark')">
          <a-input v-model="categoryForm.remark" />
        </a-form-item>
      </a-form>
    </a-drawer>

    <!-- Add item drawer -->
    <a-drawer
      :visible="itemDrawerVisible"
      :width="420"
      :title="$t('windowCashShop.item.add')"
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
      <a-form :model="itemForm" layout="vertical">
        <a-form-item :label="$t('windowCashShop.column.itemId')" required>
          <a-input-number
            v-model="itemForm.itemId"
            :min="1"
            style="width: 100%"
            @change="onItemIdChange"
          />
        </a-form-item>
        <a-alert
          v-if="assetHint"
          style="margin-bottom: 12px"
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
        <a-form-item :label="$t('windowCashShop.item.requireClient')">
          <a-switch v-model="requireClient" />
        </a-form-item>
        <a-form-item :label="$t('windowCashShop.column.remark')">
          <a-input v-model="itemForm.remark" />
        </a-form-item>
      </a-form>
    </a-drawer>

    <!-- Directory browse modal -->
    <a-modal
      v-model:visible="browseVisible"
      :title="$t('windowCashShop.browse.title')"
      :width="560"
      unmount-on-close
    >
      <a-space direction="vertical" fill style="width: 100%">
        <a-input-search
          v-model="browsePath"
          :placeholder="$t('windowCashShop.browse.current')"
          search-button
          @search="loadBrowseDirs"
        />
        <a-space>
          <a-button size="small" @click="browseGoParent">
            {{ $t('windowCashShop.browse.parent') }}
          </a-button>
          <a-button size="small" type="outline" @click="useBrowsePath">
            {{ $t('windowCashShop.browse.use') }}
          </a-button>
        </a-space>
        <a-spin :loading="browseLoading" style="width: 100%">
          <a-list
            v-if="browseDirs.length"
            size="small"
            :bordered="true"
            style="max-height: 320px; overflow: auto"
          >
            <a-list-item
              v-for="d in browseDirs"
              :key="d.path"
              class="dir-item"
              @click="enterDir(d.path)"
            >
              {{ d.name }}
            </a-list-item>
          </a-list>
          <a-empty v-else :description="$t('windowCashShop.browse.empty')" />
        </a-spin>
      </a-space>
      <template #footer>
        <a-space>
          <a-button @click="browseVisible = false">
            {{ $t('button.cancel') }}
          </a-button>
          <a-button type="primary" @click="useBrowsePath">
            {{ $t('windowCashShop.browse.use') }}
          </a-button>
        </a-space>
      </template>
    </a-modal>

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
      <a-form layout="inline" style="margin-bottom: 12px">
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
  </div>
</template>

<script setup lang="ts">
  import { computed, reactive, ref } from 'vue';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import {
    getIconUrl,
    isCdnIconUrl,
    onItemIconError,
  } from '@/utils/mapleStoryAPI';
  import {
    BrowseItemRow,
    ClientDataPathInfo,
    DirectoryEntry,
    LinkedItemRow,
    XyCashShopCategoryDO,
    XyCashShopItemDO,
    browseItems,
    checkItemAsset,
    deleteCategory,
    getCategories,
    getClickTypes,
    getClientDataPath,
    getItemsGrouped,
    importItems,
    importTsv,
    linkItem,
    listDirectories,
    reloadCategory,
    reloadWindowCashShop,
    refreshNamesFromWz,
    reorderCategories,
    saveCategory,
    saveItem,
    seedDefaults,
    setClientDataPath,
    syncFromClientData,
    syncIcons,
    unlinkItem,
    validateClientDataPath,
  } from '@/api/windowCashShop';

  interface TableRow extends LinkedItemRow {
    rowKey: string;
  }

  const { t } = useI18n();

  const clientPath = ref('');
  const pathInfo = ref<ClientDataPathInfo | null>(null);
  const validating = ref(false);
  const savingPath = ref(false);
  const reloading = ref(false);
  const refreshingNames = ref(false);
  const reloadingCat = ref(false);
  const syncingClient = ref(false);
  const syncingIconsEmpty = ref(false);
  const syncingIconsForce = ref(false);
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

  const browseVisible = ref(false);
  const browsePath = ref('');
  const browseDirs = ref<DirectoryEntry[]>([]);
  const browseLoading = ref(false);

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

  const pathAlertType = computed(() => {
    if (!pathInfo.value) return 'info';
    if (pathInfo.value.skipped) return 'warning';
    if (pathInfo.value.ok) return 'success';
    return 'error';
  });

  const pathStatusText = computed(() => {
    if (!pathInfo.value) return '';
    const parts = [
      pathInfo.value.message,
      pathInfo.value.resolved ? `resolved=${pathInfo.value.resolved}` : '',
      pathInfo.value.configured
        ? `configured=${pathInfo.value.configured}`
        : '',
    ].filter(Boolean);
    return parts.join(' · ');
  });

  const loadPathInfo = async () => {
    const { data } = await getClientDataPath();
    pathInfo.value = data;
    clientPath.value = data.configured || data.resolved || '';
  };

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

  const validatePath = async () => {
    validating.value = true;
    try {
      const { data } = await validateClientDataPath(clientPath.value || '');
      Message.info(
        `${t('windowCashShop.msg.pathValidated')}: ${data.message || ''}`
      );
    } finally {
      validating.value = false;
    }
  };

  const savePath = async () => {
    savingPath.value = true;
    try {
      const { data } = await setClientDataPath(clientPath.value || '');
      pathInfo.value = data;
      Message.success(t('windowCashShop.msg.pathSaved'));
      if (data?.ok && clientPath.value) {
        Modal.confirm({
          title: t('windowCashShop.syncFromClient.offer'),
          onOk: () => syncFromClientClick(true),
        });
      }
    } finally {
      savingPath.value = false;
    }
  };

  const clearPath = async () => {
    clientPath.value = '';
    savingPath.value = true;
    try {
      const { data } = await setClientDataPath(' ');
      pathInfo.value = data;
      Message.success(t('windowCashShop.msg.pathSaved'));
    } finally {
      savingPath.value = false;
    }
  };

  const syncFromClientClick = (skipConfirm = false) => {
    const run = async () => {
      syncingClient.value = true;
      const loadingMsg = Message.loading({
        content: t('windowCashShop.syncFromClient.loading'),
        duration: 0,
      });
      try {
        // fillIcons=true 仅绑本地已有 PNG，不会 CDN；图标补全请用「同步图标」
        const { data } = await syncFromClientData({
          fillIcons: true,
          cashOnly: true,
        });
        const secs =
          data?.durationMs != null
            ? ` · ${(data.durationMs / 1000).toFixed(1)}s`
            : '';
        const summary = [
          `扫描 ${data?.scanned ?? 0}`,
          `分类+${data?.categoriesCreated ?? 0}/~${
            data?.categoriesUpdated ?? 0
          }`,
          `清理空分类 ${data?.categoriesPruned ?? 0}`,
          `迁移关联 ${data?.linksMigrated ?? 0}`,
          `商品 ${data?.itemsUpserted ?? 0}`,
          `关联 ${data?.linksUpserted ?? 0}`,
          `热重载 ${data?.catalogSize ?? 0}`,
          `图标 ${data?.iconsFilled ?? 0}`,
          `跳过 ${data?.skipped ?? 0}`,
        ].join(' · ');
        const hint = data?.emptyReason ? ` — ${data.emptyReason}` : '';
        Message.success({
          content: `${t(
            'windowCashShop.msg.syncFromClientDone'
          )}: ${summary}${secs}${hint}`,
          duration: 12_000,
        });
        await loadCategories();
        await loadLinkedItems();
      } catch (e: unknown) {
        let raw = '';
        if (e instanceof Error) {
          raw = e.message;
        } else if (typeof e === 'string') {
          raw = e;
        }
        const isTimeout =
          /timeout/i.test(raw) ||
          /exceeded/i.test(raw) ||
          raw === 'ECONNABORTED';
        // interceptor already toasts most errors; clarify timeout in Chinese
        if (isTimeout) {
          Message.error({
            content: `${t(
              'windowCashShop.msg.syncFromClientFail'
            )}: 请求超时。请确认已保存正确 Data 路径后重试；服务端日志见 syncFromClientData。`,
            duration: 10_000,
          });
        }
      } finally {
        loadingMsg.close();
        syncingClient.value = false;
      }
    };
    if (skipConfirm) {
      return run();
    }
    Modal.confirm({
      title: t('windowCashShop.syncFromClient.confirm'),
      onOk: run,
    });
    return undefined;
  };

  const selectedItemIds = (): number[] => {
    const keys = new Set(itemSelectedKeys.value.map(String));
    return linkedItems.value
      .filter((row) => keys.has(row.rowKey))
      .map((row) => row.item?.itemId)
      .filter((id): id is number => typeof id === 'number' && id > 0);
  };

  const shopItemIconUrl = (record: TableRow) => {
    const id = record.item?.itemId;
    if (!id) return '';
    return getIconUrl('item', id, record.item?.iconUrl);
  };

  const shopIconSkipCdn = (record: TableRow) => {
    const url = record.item?.iconUrl;
    return !!url && !isCdnIconUrl(url);
  };

  const syncIconsEmptyClick = () => {
    Modal.confirm({
      title: t('windowCashShop.syncIconsEmpty.confirm'),
      onOk: async () => {
        syncingIconsEmpty.value = true;
        try {
          const payload: {
            mode: 'fillEmpty';
            itemIds?: number[];
          } = { mode: 'fillEmpty' };
          const ids = selectedItemIds();
          if (ids.length) {
            payload.itemIds = ids;
          }
          const { data } = await syncIcons(payload);
          Message.success(
            `${t('windowCashShop.msg.syncIconsDone')}: ${
              data?.message || JSON.stringify(data)
            }`
          );
          await loadLinkedItems();
        } finally {
          syncingIconsEmpty.value = false;
        }
      },
    });
  };

  const syncIconsForceClick = () => {
    Modal.confirm({
      title: t('windowCashShop.syncIconsForce.confirm'),
      onOk: async () => {
        syncingIconsForce.value = true;
        try {
          const payload: {
            mode: 'force';
            itemIds?: number[];
          } = { mode: 'force' };
          const ids = selectedItemIds();
          if (ids.length) {
            payload.itemIds = ids;
          }
          const { data } = await syncIcons(payload);
          Message.success(
            `${t('windowCashShop.msg.syncIconsDone')}: ${
              data?.message || JSON.stringify(data)
            }`
          );
          await loadLinkedItems();
        } finally {
          syncingIconsForce.value = false;
        }
      },
    });
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
    const list = categories.value.slice();
    const idx = list.findIndex((c) => c.id === cat.id);
    const target = idx + delta;
    if (idx < 0 || target < 0 || target >= list.length) return;
    const tmp = list[idx];
    list[idx] = list[target];
    list[target] = tmp;
    const withSort = list.map((c, i) => ({ ...c, sort: i }));
    categories.value = withSort;
    const ids = withSort
      .map((c) => c.id)
      .filter((id): id is number => id != null);
    try {
      await reorderCategories(ids);
    } catch {
      await Promise.all(withSort.map((c) => saveCategory(c)));
    }
    Message.success(t('windowCashShop.msg.reordered'));
    await loadCategories();
  };

  const openItemAdd = () => {
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
        enabled: 1,
      });
      Message.success(t('windowCashShop.msg.linked'));
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

  const openBrowse = () => {
    browsePath.value = clientPath.value || 'F:\\MXD_dev\\BeiDou-Client' || '';
    browseVisible.value = true;
    loadBrowseDirs();
  };

  const loadBrowseDirs = async () => {
    if (!browsePath.value?.trim()) return;
    browseLoading.value = true;
    try {
      const { data } = await listDirectories(browsePath.value.trim());
      browseDirs.value = data || [];
    } catch {
      browseDirs.value = [];
    } finally {
      browseLoading.value = false;
    }
  };

  const enterDir = (path: string) => {
    browsePath.value = path;
    loadBrowseDirs();
  };

  const browseGoParent = () => {
    const p = browsePath.value.replace(/[\\/]+$/, '');
    const idx = Math.max(p.lastIndexOf('\\'), p.lastIndexOf('/'));
    if (idx > 2) {
      browsePath.value = p.slice(0, idx);
      loadBrowseDirs();
    }
  };

  const useBrowsePath = () => {
    clientPath.value = browsePath.value;
    browseVisible.value = false;
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
    await Promise.all([loadPathInfo(), loadClickTypes(), loadCategories()]);
  };

  init();
</script>

<script lang="ts">
  export default {
    name: 'WindowCashShop',
  };
</script>

<style lang="less" scoped>
  .cat-row {
    display: flex;
    flex-direction: column;
    gap: 4px;
    padding: 8px 10px;
    margin-bottom: 6px;
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
