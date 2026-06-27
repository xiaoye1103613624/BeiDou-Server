<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.materials.scrollDecompose')">
      <a-tabs v-model:active-key="activeTab">
        <!-- Tab 1: 分解配置（白名单） -->
        <a-tab-pane
          key="decompose"
          :title="$t('scrollDecompose.tab.decompose')"
        >
          <!-- 查询条件 -->
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space wrap>
                <a-input-number
                  v-model="decomposeQuery.scrollId"
                  :placeholder="$t('scrollDecompose.decompose.scrollId')"
                  :min="2000000"
                  :max="2999999"
                  style="width: 140px"
                />
                <a-select
                  v-model="decomposeQuery.enabled"
                  :placeholder="$t('scrollDecompose.decompose.enabled')"
                  allow-clear
                  style="width: 110px"
                >
                  <a-option :value="1">{{
                    $t('scrollDecompose.yes')
                  }}</a-option>
                  <a-option :value="0">{{ $t('scrollDecompose.no') }}</a-option>
                </a-select>
                <a-button type="primary" @click="loadDecomposeData">
                  {{ $t('button.search') }}
                </a-button>
                <a-button
                  type="primary"
                  status="success"
                  @click="addDecomposeClick"
                >
                  {{ $t('scrollDecompose.decompose.addItem') }}
                </a-button>
                <a-button
                  v-if="decomposeSelectedKeys.length > 0"
                  status="danger"
                  @click="batchDeleteDecomposeClick"
                >
                  {{ $t('button.delete') }}（{{
                    decomposeSelectedKeys.length
                  }}）
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :row-selection="{ type: 'checkbox', showCheckedAll: true }"
            v-model:selected-keys="decomposeSelectedKeys"
            :loading="decomposeLoading"
            :data="decomposeTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('scrollDecompose.decompose.scrollId')"
                data-index="scrollId"
                :width="110"
                align="center"
              />
              <a-table-column
                :title="$t('scrollDecompose.decompose.scrollName')"
                data-index="scrollName"
                :width="200"
                ellipsis
              >
                <template #cell="{ record }">
                  <span>{{ record.scrollName || '-' }}</span>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('scrollDecompose.decompose.enabled')"
                :width="80"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{
                    $t('scrollDecompose.yes')
                  }}</a-tag>
                  <a-tag v-else color="gray">{{
                    $t('scrollDecompose.no')
                  }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('scrollDecompose.decompose.sortOrder')"
                data-index="sortOrder"
                :width="80"
                align="center"
              />
              <a-table-column
                :title="$t('scrollDecompose.decompose.operation')"
                :width="140"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button
                      type="text"
                      size="mini"
                      @click="editDecomposeClick(record)"
                    >
                      {{ $t('scrollDecompose.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('scrollDecompose.decompose.delete.confirm')"
                      position="top"
                      @ok="deleteDecomposeClick(record.id)"
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

        <!-- Tab 2: 兑换配置 -->
        <a-tab-pane key="exchange" :title="$t('scrollDecompose.tab.exchange')">
          <!-- 查询条件 -->
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space wrap>
                <a-input-number
                  v-model="exchangeQuery.scrollId"
                  :placeholder="$t('scrollDecompose.exchange.scrollId')"
                  :min="2000000"
                  :max="2999999"
                  style="width: 140px"
                />
                <a-select
                  v-model="exchangeQuery.enabled"
                  :placeholder="$t('scrollDecompose.exchange.enabled')"
                  allow-clear
                  style="width: 110px"
                >
                  <a-option :value="1">{{
                    $t('scrollDecompose.yes')
                  }}</a-option>
                  <a-option :value="0">{{ $t('scrollDecompose.no') }}</a-option>
                </a-select>
                <a-button type="primary" @click="loadExchangeData">
                  {{ $t('button.search') }}
                </a-button>
                <a-button
                  type="primary"
                  status="success"
                  @click="addExchangeClick"
                >
                  {{ $t('scrollDecompose.exchange.addItem') }}
                </a-button>
                <a-button
                  v-if="exchangeSelectedKeys.length > 0"
                  status="danger"
                  @click="batchDeleteExchangeClick"
                >
                  {{ $t('button.delete') }}（{{ exchangeSelectedKeys.length }}）
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :row-selection="{ type: 'checkbox', showCheckedAll: true }"
            v-model:selected-keys="exchangeSelectedKeys"
            :loading="exchangeLoading"
            :data="exchangeTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('scrollDecompose.exchange.scrollId')"
                data-index="scrollId"
                :width="110"
                align="center"
              />
              <a-table-column
                :title="$t('scrollDecompose.exchange.scrollName')"
                data-index="scrollName"
                :width="200"
                ellipsis
              >
                <template #cell="{ record }">
                  <span>{{ record.scrollName || '-' }}</span>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('scrollDecompose.exchange.cost')"
                data-index="cost"
                :width="100"
                align="center"
              >
                <template #cell="{ record }">
                  {{ (record.cost || 0).toLocaleString() }}
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('scrollDecompose.exchange.enabled')"
                :width="80"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{
                    $t('scrollDecompose.yes')
                  }}</a-tag>
                  <a-tag v-else color="gray">{{
                    $t('scrollDecompose.no')
                  }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('scrollDecompose.exchange.sortOrder')"
                data-index="sortOrder"
                :width="80"
                align="center"
              />
              <a-table-column
                :title="$t('scrollDecompose.exchange.operation')"
                :width="140"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button
                      type="text"
                      size="mini"
                      @click="editExchangeClick(record)"
                    >
                      {{ $t('scrollDecompose.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('scrollDecompose.exchange.delete.confirm')"
                      position="top"
                      @ok="deleteExchangeClick(record.id)"
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
      </a-tabs>
    </a-card>

    <!-- 分解配置编辑弹窗 -->
    <a-modal
      v-model:visible="decomposeModalVisible"
      :title="decomposeModalTitle"
      :width="450"
      @ok="saveDecomposeClick"
      @cancel="onDecomposeCancel"
    >
      <a-form :model="decomposeForm" layout="vertical">
        <a-form-item :label="$t('scrollDecompose.decompose.scrollId')">
          <a-input-number
            v-model="decomposeForm.scrollId"
            :min="2000000"
            :max="2999999"
            :placeholder="$t('scrollDecompose.decompose.scrollId')"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('scrollDecompose.decompose.scrollName')">
          <a-input
            v-model="decomposeForm.scrollName"
            placeholder="卷轴名称（可为空）"
            allow-clear
          />
        </a-form-item>
        <a-form-item :label="$t('scrollDecompose.decompose.sortOrder')">
          <a-input-number
            v-model="decomposeForm.sortOrder"
            :min="0"
            :max="9999"
            :placeholder="'默认200，数字越小越靠前'"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('scrollDecompose.decompose.enabled')">
          <a-switch v-model="decomposeEnabledBool" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 兑换配置编辑弹窗 -->
    <a-modal
      v-model:visible="exchangeModalVisible"
      :title="exchangeModalTitle"
      :width="450"
      @ok="saveExchangeClick"
      @cancel="onExchangeCancel"
    >
      <a-form :model="exchangeForm" layout="vertical">
        <a-form-item :label="$t('scrollDecompose.exchange.scrollId')">
          <a-input-number
            v-model="exchangeForm.scrollId"
            :min="2000000"
            :max="2999999"
            :placeholder="$t('scrollDecompose.exchange.scrollId')"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('scrollDecompose.exchange.scrollName')">
          <a-input
            v-model="exchangeForm.scrollName"
            placeholder="卷轴名称（可为空）"
            allow-clear
          />
        </a-form-item>
        <a-form-item :label="$t('scrollDecompose.exchange.cost')">
          <a-input-number
            v-model="exchangeForm.cost"
            :min="1"
            :max="99999"
            :placeholder="$t('scrollDecompose.exchange.cost')"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('scrollDecompose.exchange.sortOrder')">
          <a-input-number
            v-model="exchangeForm.sortOrder"
            :min="0"
            :max="9999"
            :placeholder="'默认200，数字越小越靠前'"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('scrollDecompose.exchange.enabled')">
          <a-switch v-model="exchangeEnabledBool" />
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
    ScrollDecomposeConfigState,
    ScrollExchangeConfigState,
  } from '@/store/modules/scrollDecompose/type';
  import {
    deleteDecomposeConfig,
    deleteDecomposeConfigBatch,
    deleteExchangeConfig,
    deleteExchangeConfigBatch,
    getDecomposeConfig,
    getDecomposeConfigList,
    getExchangeConfig,
    getExchangeConfigList,
    saveDecomposeConfig,
    saveExchangeConfig,
  } from '@/api/scrollDecompose';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();

  const activeTab = ref('decompose');

  // ==================== 分解配置 ====================

  const { loading: decomposeLoading, setLoading: setDecomposeLoading } =
    useLoading(false);
  const decomposeTableData = ref<ScrollDecomposeConfigState[]>([]);

  const decomposeQuery = reactive({
    scrollId: undefined as number | undefined,
    enabled: undefined as number | undefined,
  });

  const loadDecomposeData = async () => {
    setDecomposeLoading(true);
    try {
      const { data } = await getDecomposeConfigList({
        scrollId: decomposeQuery.scrollId,
        enabled: decomposeQuery.enabled,
      });
      decomposeTableData.value =
        (data as unknown as ScrollDecomposeConfigState[]) || [];
    } finally {
      setDecomposeLoading(false);
    }
  };

  // 分解配置弹窗
  const decomposeModalVisible = ref(false);
  const editingDecomposeId = ref<number | null>(null);

  const decomposeModalTitle = computed(() => {
    if (editingDecomposeId.value) {
      return `${t('scrollDecompose.button.edit')} - ID:${
        decomposeForm.value.scrollId
      }`;
    }
    return t('scrollDecompose.decompose.addItem');
  });

  const decomposeForm = ref<ScrollDecomposeConfigState>({
    scrollId: undefined,
    scrollName: '',
    enabled: 1,
    sortOrder: 200,
  });

  const decomposeEnabledBool = computed({
    get: () => decomposeForm.value.enabled === 1,
    set: (v: boolean) => {
      decomposeForm.value.enabled = v ? 1 : 0;
    },
  });

  const resetDecomposeForm = () => {
    decomposeForm.value = {
      scrollId: undefined,
      scrollName: '',
      enabled: 1,
      sortOrder: 200,
    };
    editingDecomposeId.value = null;
  };

  const addDecomposeClick = () => {
    resetDecomposeForm();
    decomposeModalVisible.value = true;
  };

  const editDecomposeClick = async (record: ScrollDecomposeConfigState) => {
    if (record.id == null) return;
    setDecomposeLoading(true);
    try {
      const { data } = await getDecomposeConfig(record.id);
      const d = data as unknown as ScrollDecomposeConfigState;
      decomposeForm.value = {
        id: d.id,
        scrollId: d.scrollId,
        scrollName: d.scrollName || '',
        enabled: d.enabled ?? 1,
        sortOrder: d.sortOrder ?? 200,
      };
      editingDecomposeId.value = d.id ?? null;
      decomposeModalVisible.value = true;
    } finally {
      setDecomposeLoading(false);
    }
  };

  const saveDecomposeClick = async () => {
    if (!decomposeForm.value.scrollId) {
      Message.warning(t('scrollDecompose.validate.scrollId'));
      return;
    }
    setDecomposeLoading(true);
    try {
      await saveDecomposeConfig(decomposeForm.value);
      Message.success(t('message.success'));
      decomposeModalVisible.value = false;
      resetDecomposeForm();
      await loadDecomposeData();
    } finally {
      setDecomposeLoading(false);
    }
  };

  const deleteDecomposeClick = async (id: number) => {
    setDecomposeLoading(true);
    try {
      await deleteDecomposeConfig(id);
      Message.success(t('message.success'));
      await loadDecomposeData();
    } finally {
      setDecomposeLoading(false);
    }
  };

  const decomposeSelectedKeys = ref<number[]>([]);

  const batchDeleteDecomposeClick = () => {
    if (decomposeSelectedKeys.value.length === 0) {
      Message.warning(t('scrollDecompose.validate.selectFirst'));
      return;
    }
    if (!window.confirm(t('scrollDecompose.decompose.deleteBatch.confirm')))
      return;
    setDecomposeLoading(true);
    deleteDecomposeConfigBatch(decomposeSelectedKeys.value as number[])
      .then(() => {
        Message.success(t('message.success'));
        decomposeSelectedKeys.value = [];
        return loadDecomposeData();
      })
      .finally(() => setDecomposeLoading(false));
  };

  const onDecomposeCancel = () => {
    decomposeModalVisible.value = false;
  };

  // ==================== 兑换配置 ====================

  const { loading: exchangeLoading, setLoading: setExchangeLoading } =
    useLoading(false);
  const exchangeTableData = ref<ScrollExchangeConfigState[]>([]);

  const exchangeQuery = reactive({
    scrollId: undefined as number | undefined,
    enabled: undefined as number | undefined,
  });

  const loadExchangeData = async () => {
    setExchangeLoading(true);
    try {
      const { data } = await getExchangeConfigList({
        scrollId: exchangeQuery.scrollId,
        enabled: exchangeQuery.enabled,
      });
      exchangeTableData.value =
        (data as unknown as ScrollExchangeConfigState[]) || [];
    } finally {
      setExchangeLoading(false);
    }
  };

  // 兑换配置弹窗
  const exchangeModalVisible = ref(false);
  const editingExchangeId = ref<number | null>(null);

  const exchangeModalTitle = computed(() => {
    if (editingExchangeId.value) {
      return `${t('scrollDecompose.button.edit')} - ID:${
        exchangeForm.value.scrollId
      }`;
    }
    return t('scrollDecompose.exchange.addItem');
  });

  const exchangeForm = ref<ScrollExchangeConfigState>({
    scrollId: undefined,
    scrollName: '',
    cost: 100,
    enabled: 1,
    sortOrder: 200,
  });

  const exchangeEnabledBool = computed({
    get: () => exchangeForm.value.enabled === 1,
    set: (v: boolean) => {
      exchangeForm.value.enabled = v ? 1 : 0;
    },
  });

  const resetExchangeForm = () => {
    exchangeForm.value = {
      scrollId: undefined,
      scrollName: '',
      cost: 100,
      enabled: 1,
      sortOrder: 200,
    };
    editingExchangeId.value = null;
  };

  const addExchangeClick = () => {
    resetExchangeForm();
    exchangeModalVisible.value = true;
  };

  const editExchangeClick = async (record: ScrollExchangeConfigState) => {
    if (record.id == null) return;
    setExchangeLoading(true);
    try {
      const { data } = await getExchangeConfig(record.id);
      const d = data as unknown as ScrollExchangeConfigState;
      exchangeForm.value = {
        id: d.id,
        scrollId: d.scrollId,
        scrollName: d.scrollName || '',
        cost: d.cost ?? 100,
        enabled: d.enabled ?? 1,
        sortOrder: d.sortOrder ?? 200,
      };
      editingExchangeId.value = d.id ?? null;
      exchangeModalVisible.value = true;
    } finally {
      setExchangeLoading(false);
    }
  };

  const saveExchangeClick = async () => {
    if (!exchangeForm.value.scrollId) {
      Message.warning(t('scrollDecompose.validate.scrollId'));
      return;
    }
    if (!exchangeForm.value.cost) {
      Message.warning(t('scrollDecompose.validate.cost'));
      return;
    }
    setExchangeLoading(true);
    try {
      await saveExchangeConfig(exchangeForm.value);
      Message.success(t('message.success'));
      exchangeModalVisible.value = false;
      resetExchangeForm();
      await loadExchangeData();
    } finally {
      setExchangeLoading(false);
    }
  };

  const deleteExchangeClick = async (id: number) => {
    setExchangeLoading(true);
    try {
      await deleteExchangeConfig(id);
      Message.success(t('message.success'));
      await loadExchangeData();
    } finally {
      setExchangeLoading(false);
    }
  };

  const exchangeSelectedKeys = ref<number[]>([]);

  const batchDeleteExchangeClick = () => {
    if (exchangeSelectedKeys.value.length === 0) {
      Message.warning(t('scrollDecompose.validate.selectFirst'));
      return;
    }
    if (!window.confirm(t('scrollDecompose.exchange.deleteBatch.confirm')))
      return;
    setExchangeLoading(true);
    deleteExchangeConfigBatch(exchangeSelectedKeys.value as number[])
      .then(() => {
        Message.success(t('message.success'));
        exchangeSelectedKeys.value = [];
        return loadExchangeData();
      })
      .finally(() => setExchangeLoading(false));
  };

  const onExchangeCancel = () => {
    exchangeModalVisible.value = false;
  };

  // 初始化加载分解配置
  loadDecomposeData();
</script>

<script lang="ts">
  export default {
    name: 'ScrollDecompose',
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
