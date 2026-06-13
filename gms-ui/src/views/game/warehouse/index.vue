<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.warehouse')">
      <a-tabs v-model:active-key="activeTab" @change="onTabChange">
        <!-- Tab 1: 仓库配置（白名单） -->
        <a-tab-pane key="config" :title="$t('warehouse.tab.config')">
          <!-- 查询条件 -->
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space wrap>
                <a-input-number
                  v-model="configQuery.itemId"
                  :placeholder="$t('warehouse.config.itemId')"
                  :min="1000000"
                  :max="5999999"
                  style="width: 140px"
                />
                <a-select
                  v-model="configQuery.inventoryType"
                  :placeholder="$t('warehouse.items.inventoryType')"
                  allow-clear
                  style="width: 120px"
                >
                  <a-option :value="1">{{
                    $t('warehouse.type.equip')
                  }}</a-option>
                  <a-option :value="2">{{ $t('warehouse.type.use') }}</a-option>
                  <a-option :value="3">{{
                    $t('warehouse.type.setup')
                  }}</a-option>
                  <a-option :value="4">{{ $t('warehouse.type.etc') }}</a-option>
                  <a-option :value="5">{{
                    $t('warehouse.type.cash')
                  }}</a-option>
                </a-select>
                <a-select
                  v-model="configQuery.enabled"
                  :placeholder="$t('warehouse.config.enabled')"
                  allow-clear
                  style="width: 110px"
                >
                  <a-option :value="1">{{ $t('warehouse.yes') }}</a-option>
                  <a-option :value="0">{{ $t('warehouse.no') }}</a-option>
                </a-select>
                <a-button type="primary" @click="loadConfigData">
                  {{ $t('button.search') }}
                </a-button>
                <a-button
                  type="primary"
                  status="success"
                  @click="addConfigClick"
                >
                  {{ $t('warehouse.config.addItem') }}
                </a-button>
                <a-button
                  v-if="configSelectedKeys.length > 0"
                  status="danger"
                  @click="batchDeleteConfigClick"
                >
                  {{ $t('button.delete') }}（{{ configSelectedKeys.length }}）
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :row-selection="{ type: 'checkbox', showCheckedAll: true }"
            v-model:selected-keys="configSelectedKeys"
            :loading="configLoading"
            :data="configTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('warehouse.config.id')"
                data-index="id"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('warehouse.config.itemId')"
                data-index="itemId"
                :width="100"
                align="center"
              />
              <a-table-column
                :title="$t('warehouse.config.itemName')"
                data-index="itemName"
                :width="140"
                ellipsis
              >
                <template #cell="{ record }">
                  <span>{{ record.itemName || '-' }}</span>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('warehouse.config.inventoryType')"
                :width="80"
                align="center"
              >
                <template #cell="{ record }">
                  {{ getInventoryTypeName(record.inventoryType) }}
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('warehouse.config.enabled')"
                :width="70"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{
                    $t('warehouse.yes')
                  }}</a-tag>
                  <a-tag v-else color="gray">{{ $t('warehouse.no') }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('warehouse.config.sortOrder')"
                data-index="sortOrder"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('warehouse.config.operation')"
                :width="140"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button
                      type="text"
                      size="mini"
                      @click="editConfigClick(record)"
                    >
                      {{ $t('warehouse.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('warehouse.config.delete.confirm')"
                      position="top"
                      @ok="deleteConfigClick(record.id)"
                    >
                      <a-button type="text" size="mini" status="danger">
                        {{ $t('button.delete') }}
                      </a-button>
                    </a-popconfirm>
                  </a-space>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-tab-pane>

        <!-- Tab 2: 仓库物品管理 -->
        <a-tab-pane key="items" :title="$t('warehouse.tab.items')">
          <!-- 查询条件 -->
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space wrap>
                <a-input-number
                  v-model="query.characterId"
                  :placeholder="$t('warehouse.items.characterId')"
                  :min="1"
                  style="width: 140px"
                  allow-clear
                />
                <a-input-number
                  v-model="query.accountId"
                  :placeholder="$t('warehouse.items.accountId')"
                  :min="1"
                  style="width: 140px"
                  allow-clear
                />
                <a-select
                  v-model="query.inventoryType"
                  :placeholder="$t('warehouse.items.inventoryType')"
                  allow-clear
                  style="width: 120px"
                >
                  <a-option :value="1">{{
                    $t('warehouse.type.equip')
                  }}</a-option>
                  <a-option :value="2">{{ $t('warehouse.type.use') }}</a-option>
                  <a-option :value="3">{{
                    $t('warehouse.type.setup')
                  }}</a-option>
                  <a-option :value="4">{{ $t('warehouse.type.etc') }}</a-option>
                  <a-option :value="5">{{
                    $t('warehouse.type.cash')
                  }}</a-option>
                </a-select>
                <a-button type="primary" @click="loadItemsData">
                  {{ $t('button.search') }}
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <!-- 游戏参数 -->
          <a-row style="margin-bottom: 8px">
            <a-col>
              <a-space>
                <a-tag color="arcoblue">
                  {{ $t('warehouse.gameParams.accountShared') }}:
                  {{
                    gameParams.accountShared
                      ? $t('warehouse.yes')
                      : $t('warehouse.no')
                  }}
                </a-tag>
                <a-tag color="arcoblue">
                  {{ $t('warehouse.gameParams.maxStack') }}:
                  {{ gameParams.maxStack?.toLocaleString() }}
                </a-tag>
              </a-space>
            </a-col>
          </a-row>

          <!-- 物品列表 -->
          <a-table
            row-key="id"
            :loading="itemsLoading"
            :data="itemsTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('warehouse.items.id')"
                data-index="id"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('warehouse.items.itemId')"
                data-index="itemId"
                :width="100"
                align="center"
              />
              <a-table-column
                :title="$t('warehouse.items.itemName')"
                data-index="itemName"
                :width="160"
                ellipsis
              >
                <template #cell="{ record }">
                  <a-tooltip :content="record.itemName || '-'">
                    <span>{{ record.itemName || '-' }}</span>
                  </a-tooltip>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('warehouse.items.inventoryType')"
                :width="80"
                align="center"
              >
                <template #cell="{ record }">
                  {{ getInventoryTypeName(record.inventoryType) }}
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('warehouse.items.quantity')"
                data-index="quantity"
                :width="100"
                align="center"
              >
                <template #cell="{ record }">
                  {{ (record.quantity || 0).toLocaleString() }}
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('warehouse.items.characterId')"
                data-index="characterId"
                :width="100"
                align="center"
              />
              <a-table-column
                :title="$t('warehouse.items.createTime')"
                data-index="createTime"
                :width="160"
                align="center"
              >
                <template #cell="{ record }">
                  {{ formatTime(record.createTime) }}
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('warehouse.items.operation')"
                :width="80"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-popconfirm
                    :content="$t('warehouse.items.delete.confirm')"
                    position="top"
                    @ok="deleteItemClick(record.id)"
                  >
                    <a-button type="text" size="mini" status="danger">
                      {{ $t('button.delete') }}
                    </a-button>
                  </a-popconfirm>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 配置编辑弹窗 -->
    <a-modal
      v-model:visible="configModalVisible"
      :title="configModalTitle"
      :width="450"
      @ok="saveConfigClick"
      @cancel="onConfigCancel"
    >
      <a-form :model="configForm" layout="vertical">
        <a-form-item :label="$t('warehouse.config.itemId')">
          <a-input-number
            v-model="configForm.itemId"
            :min="1000000"
            :max="5999999"
            :placeholder="$t('warehouse.config.itemId')"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('warehouse.config.itemName')">
          <a-input
            v-model="configForm.itemName"
            placeholder="物品名称（可为空）"
            allow-clear
          />
        </a-form-item>
        <a-form-item :label="$t('warehouse.config.inventoryType')">
          <a-select v-model="configForm.inventoryType" style="width: 100%">
            <a-option :value="1">{{ $t('warehouse.type.equip') }}</a-option>
            <a-option :value="2">{{ $t('warehouse.type.use') }}</a-option>
            <a-option :value="3">{{ $t('warehouse.type.setup') }}</a-option>
            <a-option :value="4">{{ $t('warehouse.type.etc') }}</a-option>
            <a-option :value="5">{{ $t('warehouse.type.cash') }}</a-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('warehouse.config.sortOrder')">
          <a-input-number
            v-model="configForm.sortOrder"
            :min="0"
            :max="9999"
            :placeholder="'默认200，数字越小越靠前'"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('warehouse.config.enabled')">
          <a-switch v-model="configEnabledBool" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, reactive, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import type {
    WarehouseConfigState,
    WarehouseItemState,
    WarehouseGameParamsState,
  } from '@/store/modules/warehouse/type';
  import {
    getConfigList,
    getConfig,
    saveConfig,
    deleteConfig,
    deleteConfigBatch,
    getWarehouseItems,
    deleteWarehouseItem,
    getGameParams,
  } from '@/api/warehouse';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();

  // Tab 切换
  const activeTab = ref('config');

  // ==================== 配置管理 ====================

  const { loading: configLoading, setLoading: setConfigLoading } =
    useLoading(false);
  const configTableData = ref<WarehouseConfigState[]>([]);

  // 配置搜索条件
  const configQuery = reactive({
    itemId: undefined as number | undefined,
    inventoryType: undefined as number | undefined,
    enabled: undefined as number | undefined,
  });

  const loadConfigData = async () => {
    setConfigLoading(true);
    try {
      const { data } = await getConfigList({
        itemId: configQuery.itemId,
        inventoryType: configQuery.inventoryType,
        enabled: configQuery.enabled,
      });
      configTableData.value = (data as unknown as WarehouseConfigState[]) || [];
    } finally {
      setConfigLoading(false);
    }
  };

  // 配置弹窗
  const configModalVisible = ref(false);
  const editingConfigId = ref<number | null>(null);

  const configModalTitle = computed(() => {
    if (editingConfigId.value) {
      return `${t('warehouse.button.edit')} - ID:${configForm.value.itemId}`;
    }
    return t('warehouse.config.addItem');
  });

  const configForm = ref<WarehouseConfigState>({
    itemId: undefined,
    itemName: '',
    inventoryType: undefined,
    enabled: 1,
    sortOrder: 200,
  });

  const configEnabledBool = computed({
    get: () => configForm.value.enabled === 1,
    set: (v: boolean) => {
      configForm.value.enabled = v ? 1 : 0;
    },
  });

  const resetConfigForm = () => {
    configForm.value = {
      itemId: undefined,
      itemName: '',
      inventoryType: undefined,
      enabled: 1,
      sortOrder: 200,
    };
    editingConfigId.value = null;
  };

  const addConfigClick = () => {
    resetConfigForm();
    configModalVisible.value = true;
  };

  const editConfigClick = async (record: WarehouseConfigState) => {
    if (record.id == null) return;
    setConfigLoading(true);
    try {
      const { data } = await getConfig(record.id);
      const d = data as unknown as WarehouseConfigState;
      configForm.value = {
        id: d.id,
        itemId: d.itemId,
        itemName: d.itemName || '',
        inventoryType: d.inventoryType,
        enabled: d.enabled ?? 1,
        sortOrder: d.sortOrder ?? 200,
      };
      editingConfigId.value = d.id ?? null;
      configModalVisible.value = true;
    } finally {
      setConfigLoading(false);
    }
  };

  const saveConfigClick = async () => {
    if (!configForm.value.itemId) {
      Message.warning(t('warehouse.validate.itemId'));
      return;
    }
    if (!configForm.value.inventoryType) {
      Message.warning(t('warehouse.validate.inventoryType'));
      return;
    }
    setConfigLoading(true);
    try {
      await saveConfig(configForm.value);
      Message.success(t('message.success'));
      configModalVisible.value = false;
      resetConfigForm();
      await loadConfigData();
    } finally {
      setConfigLoading(false);
    }
  };

  const deleteConfigClick = async (id: number) => {
    setConfigLoading(true);
    try {
      await deleteConfig(id);
      Message.success(t('message.success'));
      await loadConfigData();
    } finally {
      setConfigLoading(false);
    }
  };

  /** 配置表格多选 key 列表 */
  const configSelectedKeys = ref<number[]>([]);

  /** 批量删除配置 */
  const batchDeleteConfigClick = () => {
    if (configSelectedKeys.value.length === 0) {
      Message.warning(t('warehouse.validate.selectFirst'));
      return;
    }
    if (!window.confirm(t('warehouse.config.deleteBatch.confirm'))) return;
    setConfigLoading(true);
    deleteConfigBatch(configSelectedKeys.value as number[])
      .then(() => {
        Message.success(t('message.success'));
        configSelectedKeys.value = [];
        return loadConfigData();
      })
      .finally(() => setConfigLoading(false));
  };

  const onConfigCancel = () => {
    configModalVisible.value = false;
  };

  // ==================== 物品管理 ====================

  const { loading: itemsLoading, setLoading: setItemsLoading } =
    useLoading(false);
  const itemsTableData = ref<WarehouseItemState[]>([]);
  const gameParams = reactive<WarehouseGameParamsState>({
    accountShared: false,
    maxStack: 30000,
  });

  const query = reactive({
    accountId: undefined as number | undefined,
    characterId: undefined as number | undefined,
    inventoryType: undefined as number | undefined,
  });

  const loadGameParams = async () => {
    try {
      const { data } = await getGameParams();
      const d = data as unknown as WarehouseGameParamsState;
      gameParams.accountShared = d.accountShared ?? false;
      gameParams.maxStack = d.maxStack ?? 30000;
    } catch {
      // ignore
    }
  };

  const loadItemsData = async () => {
    if (!query.characterId && !query.accountId) {
      Message.warning(t('warehouse.validate.accountId'));
      return;
    }
    setItemsLoading(true);
    try {
      const { data } = await getWarehouseItems({
        accountId: query.accountId,
        characterId: query.characterId,
        inventoryType: query.inventoryType,
      });
      itemsTableData.value = (data as unknown as WarehouseItemState[]) || [];
    } finally {
      setItemsLoading(false);
    }
  };

  const deleteItemClick = async (id: number) => {
    setItemsLoading(true);
    try {
      await deleteWarehouseItem(id);
      Message.success(t('message.success'));
      await loadItemsData();
    } finally {
      setItemsLoading(false);
    }
  };

  const onTabChange = (key: string | number) => {
    if (key === 'items') {
      loadGameParams();
    }
  };

  // ==================== 工具函数 ====================

  const getInventoryTypeName = (type: number | undefined) => {
    const map: Record<number, string> = {
      1: t('warehouse.type.equip'),
      2: t('warehouse.type.use'),
      3: t('warehouse.type.setup'),
      4: t('warehouse.type.etc'),
      5: t('warehouse.type.cash'),
    };
    return type ? map[type] || type : '-';
  };

  const formatTime = (time: string | undefined) => {
    if (!time) return '-';
    try {
      const d = new Date(time);
      if (Number.isNaN(d.getTime())) return time;
      return d.toLocaleString();
    } catch {
      return time;
    }
  };

  // 初始化加载配置
  loadConfigData();
</script>

<script lang="ts">
  export default {
    name: 'Warehouse',
  };
</script>

<style lang="less" scoped>
  :deep(.arco-card-body, .arco-row) {
    width: 100%;
  }
  .arco-input-wrapper {
    margin-right: 0;
    margin-bottom: 5px;
    width: 100%;
  }
  @media (min-width: 500px) {
    .arco-input-wrapper {
      margin-right: 8px;
      width: 140px;
    }
  }
</style>
