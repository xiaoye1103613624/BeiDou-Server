<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.customGameplay.paohuan')">
      <a-tabs v-model:active-key="activeTab">
        <!-- Tab 1: 物品池管理 -->
        <a-tab-pane key="config" :title="$t('paohuan.tab.config')">
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space>
                <a-button
                  type="primary"
                  status="success"
                  @click="addConfigClick"
                >
                  {{ $t('paohuan.config.addItem') }}
                </a-button>
                <a-button
                  v-if="configSelectedKeys.length > 0"
                  status="danger"
                  @click="batchDeleteConfigClick"
                >
                  {{ $t('button.delete') }}（{{ configSelectedKeys.length }}）
                </a-button>
                <a-input-number
                  v-model="configFilter.itemId"
                  :placeholder="$t('paohuan.ringReward.filter.itemId')"
                  :min="0"
                  :max="5999999"
                  style="width: 140px"
                  allow-clear
                  @keydown.enter="searchConfigData"
                />
                <a-input
                  v-model="configFilter.itemName"
                  :placeholder="$t('paohuan.ringReward.filter.itemName')"
                  style="width: 160px"
                  allow-clear
                  @keydown.enter="searchConfigData"
                />
                <a-button @click="searchConfigData">
                  {{ $t('button.search') }}
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
                :title="$t('paohuan.config.id')"
                data-index="id"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.config.itemId')"
                :width="110"
                align="center"
              >
                <template #cell="{ record }">
                  <a-input-number
                    :model-value="record.itemId"
                    :min="0"
                    :max="5999999"
                    size="mini"
                    style="width: 90px"
                    @change="(v: number) => inlineUpdate(record, 'itemId', v)"
                  />
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('paohuan.config.itemName')"
                :width="180"
                align="center"
              >
                <template #cell="{ record }">
                  <a-popover>
                    <a-button type="text" size="mini">
                      {{ record.itemName }}
                    </a-button>
                    <template #content>
                      <img :src="getIconUrl('item', record.itemId)" alt="" />
                    </template>
                  </a-popover>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('paohuan.config.quantity')"
                :width="90"
                align="center"
              >
                <template #cell="{ record }">
                  <a-input-number
                    :model-value="record.quantity"
                    :min="1"
                    :max="99999"
                    size="mini"
                    style="width: 70px"
                    @change="(v: number) => inlineUpdate(record, 'quantity', v)"
                  />
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('paohuan.config.dropMapId')"
                :width="90"
                align="center"
              >
                <template #cell="{ record }">
                  <a-input-number
                    :model-value="record.dropMapId"
                    :min="0"
                    size="mini"
                    style="width: 70px"
                    @change="(v: number) => inlineUpdate(record, 'dropMapId', v)"
                  />
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('paohuan.config.sortOrder')"
                :width="80"
                align="center"
              >
                <template #cell="{ record }">
                  <a-input-number
                    :model-value="record.sortOrder"
                    :min="0"
                    size="mini"
                    style="width: 60px"
                    @change="(v: number) => inlineUpdate(record, 'sortOrder', v)"
                  />
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('paohuan.config.enabled')"
                :width="65"
                align="center"
              >
                <template #cell="{ record }">
                  <a-switch
                    :model-value="record.enabled === 1"
                    size="mini"
                    @change="(v: boolean) => inlineUpdate(record, 'enabled', v ? 1 : 0)"
                  />
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('paohuan.config.operation')"
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
                      {{ $t('paohuan.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('paohuan.config.delete.confirm')"
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

        <!-- Tab 2: 每环随机奖励 -->
        <a-tab-pane key="ringReward" :title="$t('paohuan.tab.ringReward')">
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space>
                <a-button
                  type="primary"
                  status="success"
                  @click="addRingRewardClick"
                >
                  {{ $t('paohuan.ringReward.add') }}
                </a-button>
                <a-input-number
                  v-model="ringRewardFilter.itemId"
                  :placeholder="$t('paohuan.ringReward.filter.itemId')"
                  :min="0"
                  :max="5999999"
                  style="width: 140px"
                  allow-clear
                  @keydown.enter="searchRingRewardData"
                />
                <a-input
                  v-model="ringRewardFilter.itemName"
                  :placeholder="$t('paohuan.ringReward.filter.itemName')"
                  style="width: 160px"
                  allow-clear
                  @keydown.enter="searchRingRewardData"
                />
                <a-button @click="searchRingRewardData">
                  {{ $t('button.search') }}
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :loading="ringRewardLoading"
            :data="ringRewardTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('paohuan.ringReward.id')"
                data-index="id"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.ringReward.itemId')"
                data-index="itemId"
                :width="100"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.ringReward.itemName')"
                :width="200"
                align="center"
              >
                <template #cell="{ record }">
                  <a-button
                    v-if="record.itemId === 0"
                    type="text"
                    size="mini"
                    status="warning"
                  >
                    金币
                  </a-button>
                  <a-popover v-else>
                    <a-button type="text" size="mini">
                      {{ record.itemName }}
                    </a-button>
                    <template #content>
                      <img :src="getIconUrl('item', record.itemId)" alt="" />
                    </template>
                  </a-popover>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('paohuan.ringReward.minQty')"
                data-index="minQuantity"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.ringReward.maxQty')"
                data-index="maxQuantity"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.ringReward.weight')"
                data-index="weight"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.ringReward.sortOrder')"
                data-index="sortOrder"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.ringReward.enabled')"
                :width="60"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{
                    $t('paohuan.yes')
                  }}</a-tag>
                  <a-tag v-else color="gray">{{ $t('paohuan.no') }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('paohuan.config.operation')"
                :width="140"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button
                      type="text"
                      size="mini"
                      @click="editRingRewardClick(record)"
                    >
                      {{ $t('paohuan.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('paohuan.ringReward.delete.confirm')"
                      position="top"
                      @ok="deleteRingRewardClick(record.id)"
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

        <!-- Tab 3: 里程碑奖励 -->
        <a-tab-pane key="reward" :title="$t('paohuan.tab.reward')">
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space>
                <a-button
                  type="primary"
                  status="success"
                  @click="addRewardClick"
                >
                  {{ $t('paohuan.reward.addReward') }}
                </a-button>
                <a-input-number
                  v-model="rewardFilter.itemId"
                  :placeholder="$t('paohuan.ringReward.filter.itemId')"
                  :min="0"
                  :max="5999999"
                  style="width: 140px"
                  allow-clear
                  @keydown.enter="searchRewardData"
                />
                <a-input
                  v-model="rewardFilter.itemName"
                  :placeholder="$t('paohuan.ringReward.filter.itemName')"
                  style="width: 160px"
                  allow-clear
                  @keydown.enter="searchRewardData"
                />
                <a-button @click="searchRewardData">
                  {{ $t('button.search') }}
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :loading="rewardLoading"
            :data="rewardTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('paohuan.reward.id')"
                data-index="id"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.reward.ringCount')"
                data-index="ringCount"
                :width="80"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.reward.desc')"
                data-index="rewardDesc"
                :width="150"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.reward.itemId')"
                data-index="itemId"
                :width="100"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.reward.itemName')"
                :width="200"
                align="center"
              >
                <template #cell="{ record }">
                  <a-button
                    v-if="record.itemId === 0"
                    type="text"
                    size="mini"
                    status="warning"
                  >
                    金币
                  </a-button>
                  <a-popover v-else>
                    <a-button type="text" size="mini">
                      {{ record.itemName }}
                    </a-button>
                    <template #content>
                      <img :src="getIconUrl('item', record.itemId)" alt="" />
                    </template>
                  </a-popover>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('paohuan.reward.quantity')"
                data-index="quantity"
                :width="80"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.reward.sortOrder')"
                data-index="sortOrder"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('paohuan.config.operation')"
                :width="140"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button
                      type="text"
                      size="mini"
                      @click="editRewardClick(record)"
                    >
                      {{ $t('paohuan.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('paohuan.reward.delete.confirm')"
                      position="top"
                      @ok="deleteRewardClick(record.id)"
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

    <!-- 物品池编辑弹窗 -->
    <a-modal
      v-model:visible="configModalVisible"
      :title="configModalTitle"
      :width="400"
      @ok="saveConfigClick"
      @cancel="onConfigCancel"
    >
      <a-form :model="configForm" layout="vertical">
        <a-form-item :label="$t('paohuan.config.itemId')">
          <a-input-number
            v-model="configForm.itemId"
            :min="1000000"
            :max="5999999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.config.quantity')">
          <a-input-number
            v-model="configForm.quantity"
            :min="1"
            :max="99999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.config.dropMapId')">
          <a-input-number
            v-model="configForm.dropMapId"
            :min="0"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.config.sortOrder')">
          <a-input-number
            v-model="configForm.sortOrder"
            :min="0"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.config.enabled')">
          <a-switch v-model="configEnabledBool" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 奖励编辑弹窗 -->
    <a-modal
      v-model:visible="rewardModalVisible"
      :title="rewardModalTitle"
      :width="400"
      @ok="saveRewardClick"
      @cancel="onRewardCancel"
    >
      <a-form :model="rewardForm" layout="vertical">
        <a-form-item :label="$t('paohuan.reward.ringCount')">
          <a-input-number
            v-model="rewardForm.ringCount"
            :min="1"
            :max="999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.reward.desc')">
          <a-input v-model="rewardForm.rewardDesc" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('paohuan.reward.itemId')">
          <a-input-number
            v-model="rewardForm.itemId"
            :min="0"
            :max="5999999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.reward.quantity')">
          <a-input-number
            v-model="rewardForm.quantity"
            :min="1"
            :max="99999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.reward.sortOrder')">
          <a-input-number
            v-model="rewardForm.sortOrder"
            :min="0"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 每环随机奖励弹窗 -->
    <a-modal
      v-model:visible="ringRewardModalVisible"
      :title="ringRewardModalTitle"
      :width="400"
      @ok="saveRingRewardClick"
      @cancel="onRingRewardCancel"
    >
      <a-form :model="ringRewardForm" layout="vertical">
        <a-form-item :label="$t('paohuan.ringReward.itemId')">
          <a-input-number
            v-model="ringRewardForm.itemId"
            :min="0"
            :max="5999999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.ringReward.minQty')">
          <a-input-number
            v-model="ringRewardForm.minQuantity"
            :min="1"
            :max="99999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.ringReward.maxQty')">
          <a-input-number
            v-model="ringRewardForm.maxQuantity"
            :min="1"
            :max="99999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.ringReward.weight')">
          <a-input-number
            v-model="ringRewardForm.weight"
            :min="0"
            :max="999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.ringReward.sortOrder')">
          <a-input-number
            v-model="ringRewardForm.sortOrder"
            :min="0"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('paohuan.ringReward.enabled')">
          <a-switch v-model="ringRewardEnabledBool" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import type {
    PaohuanConfigForm,
    PaohuanReward,
    PaohuanRingReward,
  } from '@/api/paohuan';
  import {
    deleteConfig,
    deleteConfigBatch,
    deleteReward,
    deleteRingReward,
    getConfig,
    getConfigList,
    getRewardList,
    getRingRewardList,
    saveConfig,
    saveReward,
    saveRingReward,
  } from '@/api/paohuan';
  import { Message } from '@arco-design/web-vue';
  import { getIconUrl } from '@/utils/mapleStoryAPI';

  const { t } = useI18n();
  const activeTab = ref('config');

  // ==================== 物品池管理 ====================
  const { loading: configLoading, setLoading: setConfigLoading } =
    useLoading(false);
  const configTableData = ref<PaohuanConfigForm[]>([]);
  /** 物品池全量数据（未筛选） */
  const configAllData = ref<PaohuanConfigForm[]>([]);
  /** 物品池搜索筛选条件 */
  const configFilter = ref({
    itemId: undefined as number | undefined,
    itemName: '',
  });
  /** 物品池表格多选 key 列表 */
  const configSelectedKeys = ref<number[]>([]);

  const loadConfigData = async () => {
    setConfigLoading(true);
    try {
      const { data } = await getConfigList();
      configAllData.value = (data as unknown as PaohuanConfigForm[]) || [];
      configTableData.value = configAllData.value;
    } finally {
      setConfigLoading(false);
    }
  };

  /** 根据筛选条件过滤物品池数据 */
  const searchConfigData = () => {
    const filterItemId = configFilter.value.itemId;
    const filterName = configFilter.value.itemName?.trim().toLowerCase();
    if (!filterItemId && !filterName) {
      configTableData.value = configAllData.value;
      return;
    }
    configTableData.value = configAllData.value.filter((item) => {
      if (filterItemId && item.itemId !== filterItemId) return false;
      if (
        filterName &&
        !(item.itemName || '').toLowerCase().includes(filterName)
      )
        return false;
      return true;
    });
  };

  const configModalVisible = ref(false);
  const editingConfigId = ref<number | null>(null);
  const configModalTitle = computed(() => {
    return editingConfigId.value
      ? t('paohuan.button.edit')
      : t('paohuan.config.addItem');
  });

  const configForm = ref<PaohuanConfigForm>({
    itemId: undefined,
    quantity: 1,
    dropMapId: 0,
    sortOrder: 0,
    enabled: 1,
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
      quantity: 1,
      dropMapId: 0,
      sortOrder: 0,
      enabled: 1,
    };
    editingConfigId.value = null;
  };

  const addConfigClick = () => {
    resetConfigForm();
    configModalVisible.value = true;
  };

  const editConfigClick = async (record: PaohuanConfigForm) => {
    if (record.id == null) return;
    setConfigLoading(true);
    try {
      const { data } = await getConfig(record.id);
      const d = data as unknown as PaohuanConfigForm;
      configForm.value = {
        id: d.id,
        itemId: d.itemId,
        quantity: d.quantity,
        dropMapId: d.dropMapId ?? 0,
        sortOrder: d.sortOrder,
        enabled: d.enabled ?? 1,
      };
      editingConfigId.value = d.id ?? null;
      configModalVisible.value = true;
    } finally {
      setConfigLoading(false);
    }
  };

  /** 行内编辑保存 */
  const inlineSaving = ref(false);
  const inlineUpdate = async (
    record: PaohuanConfigForm,
    field: string,
    value: number
  ) => {
    if (inlineSaving.value) return;
    inlineSaving.value = true;
    try {
      const payload = {
        id: record.id,
        itemId: record.itemId,
        quantity: record.quantity,
        dropMapId: record.dropMapId,
        sortOrder: record.sortOrder,
        enabled: record.enabled,
        [field]: value,
      };
      await saveConfig(payload as PaohuanConfigForm);
      // 更新本地数据
      (record as Record<string, unknown>)[field] = value;
      if (field === 'itemId') {
        // itemId 变更后需要刷新以获取新的 itemName
        await loadConfigData();
      }
    } catch {
      // 保存失败不更新 UI
    } finally {
      inlineSaving.value = false;
    }
  };

  const saveConfigClick = async () => {
    if (!configForm.value.itemId) {
      Message.warning(t('paohuan.validate.itemId'));
      return;
    }
    if (!configForm.value.quantity) {
      Message.warning(t('paohuan.validate.quantity'));
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

  /** 批量删除物品池配置 */
  const batchDeleteConfigClick = () => {
    if (configSelectedKeys.value.length === 0) {
      Message.warning(t('paohuan.validate.selectFirst'));
      return;
    }
    if (!window.confirm(t('paohuan.config.deleteBatch.confirm'))) return;
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

  // ==================== 里程碑奖励 ====================
  const { loading: rewardLoading, setLoading: setRewardLoading } =
    useLoading(false);
  const rewardTableData = ref<PaohuanReward[]>([]);
  /** 里程碑奖励全量数据（未筛选） */
  const rewardAllData = ref<PaohuanReward[]>([]);
  /** 里程碑奖励搜索筛选条件 */
  const rewardFilter = ref({
    itemId: undefined as number | undefined,
    itemName: '',
  });

  const loadRewardData = async () => {
    setRewardLoading(true);
    try {
      const { data } = await getRewardList();
      rewardAllData.value = (data as unknown as PaohuanReward[]) || [];
      rewardTableData.value = rewardAllData.value;
    } finally {
      setRewardLoading(false);
    }
  };

  /** 根据筛选条件过滤里程碑奖励数据 */
  const searchRewardData = () => {
    const filterItemId = rewardFilter.value.itemId;
    const filterName = rewardFilter.value.itemName?.trim().toLowerCase();
    if (!filterItemId && !filterName) {
      rewardTableData.value = rewardAllData.value;
      return;
    }
    rewardTableData.value = rewardAllData.value.filter((item) => {
      if (filterItemId && item.itemId !== filterItemId) return false;
      if (
        filterName &&
        !(item.itemName || '').toLowerCase().includes(filterName)
      )
        return false;
      return true;
    });
  };

  const rewardModalVisible = ref(false);
  const editingRewardId = ref<number | null>(null);
  const rewardModalTitle = computed(() => {
    return editingRewardId.value
      ? t('paohuan.button.edit')
      : t('paohuan.reward.addReward');
  });

  const rewardForm = ref<PaohuanReward>({
    ringCount: undefined,
    rewardDesc: '',
    itemId: 0,
    quantity: 1,
    sortOrder: 0,
  });
  const resetRewardForm = () => {
    rewardForm.value = {
      ringCount: undefined,
      rewardDesc: '',
      itemId: 0,
      quantity: 1,
      sortOrder: 0,
    };
    editingRewardId.value = null;
  };

  const addRewardClick = () => {
    resetRewardForm();
    rewardModalVisible.value = true;
  };

  const editRewardClick = (record: PaohuanReward) => {
    rewardForm.value = { ...record };
    editingRewardId.value = record.id ?? null;
    rewardModalVisible.value = true;
  };

  const saveRewardClick = async () => {
    if (!rewardForm.value.ringCount) {
      Message.warning(t('paohuan.validate.ringCount'));
      return;
    }
    setRewardLoading(true);
    try {
      await saveReward(rewardForm.value);
      Message.success(t('message.success'));
      rewardModalVisible.value = false;
      resetRewardForm();
      await loadRewardData();
    } finally {
      setRewardLoading(false);
    }
  };

  const deleteRewardClick = async (id: number) => {
    setRewardLoading(true);
    try {
      await deleteReward(id);
      Message.success(t('message.success'));
      await loadRewardData();
    } finally {
      setRewardLoading(false);
    }
  };

  const onRewardCancel = () => {
    rewardModalVisible.value = false;
  };

  // ==================== 每环随机奖励 ====================
  const { loading: ringRewardLoading, setLoading: setRingRewardLoading } =
    useLoading(false);
  const ringRewardTableData = ref<PaohuanRingReward[]>([]);
  /** 完整数据（未筛选） */
  const ringRewardAllData = ref<PaohuanRingReward[]>([]);
  /** 搜索筛选条件 */
  const ringRewardFilter = ref({
    itemId: undefined as number | undefined,
    itemName: '',
  });

  /** 加载每环随机奖励（全量数据） */
  const loadRingRewardData = async () => {
    setRingRewardLoading(true);
    try {
      const { data } = await getRingRewardList();
      ringRewardAllData.value = (data as unknown as PaohuanRingReward[]) || [];
      ringRewardTableData.value = ringRewardAllData.value;
    } finally {
      setRingRewardLoading(false);
    }
  };

  /** 根据筛选条件过滤每环随机奖励 */
  const searchRingRewardData = () => {
    const filterItemId = ringRewardFilter.value.itemId;
    const filterName = ringRewardFilter.value.itemName?.trim().toLowerCase();
    if (!filterItemId && !filterName) {
      ringRewardTableData.value = ringRewardAllData.value;
      return;
    }
    ringRewardTableData.value = ringRewardAllData.value.filter((item) => {
      // 按道具ID精确匹配
      if (filterItemId && item.itemId !== filterItemId) return false;
      // 按道具名称模糊匹配
      if (
        filterName &&
        !(item.itemName || '').toLowerCase().includes(filterName)
      )
        return false;
      return true;
    });
  };

  const ringRewardModalVisible = ref(false);
  const editingRingRewardId = ref<number | null>(null);
  const ringRewardModalTitle = computed(() =>
    editingRingRewardId.value
      ? t('paohuan.button.edit')
      : t('paohuan.ringReward.add')
  );

  const ringRewardForm = ref<PaohuanRingReward>({
    itemId: 0,
    minQuantity: 1,
    maxQuantity: 1,
    weight: 1,
    sortOrder: 0,
    enabled: 1,
  });
  const ringRewardEnabledBool = computed({
    get: () => ringRewardForm.value.enabled === 1,
    set: (v: boolean) => {
      ringRewardForm.value.enabled = v ? 1 : 0;
    },
  });

  const resetRingRewardForm = () => {
    ringRewardForm.value = {
      itemId: 0,
      minQuantity: 1,
      maxQuantity: 1,
      weight: 1,
      sortOrder: 0,
      enabled: 1,
    };
    editingRingRewardId.value = null;
  };

  const addRingRewardClick = () => {
    resetRingRewardForm();
    ringRewardModalVisible.value = true;
  };

  const editRingRewardClick = (record: PaohuanRingReward) => {
    ringRewardForm.value = { ...record };
    editingRingRewardId.value = record.id ?? null;
    ringRewardModalVisible.value = true;
  };

  const saveRingRewardClick = async () => {
    if (!ringRewardForm.value.itemId && ringRewardForm.value.itemId !== 0) {
      Message.warning(t('paohuan.validate.itemId'));
      return;
    }
    setRingRewardLoading(true);
    try {
      await saveRingReward(ringRewardForm.value);
      Message.success(t('message.success'));
      ringRewardModalVisible.value = false;
      resetRingRewardForm();
      await loadRingRewardData();
    } finally {
      setRingRewardLoading(false);
    }
  };

  const deleteRingRewardClick = async (id: number) => {
    setRingRewardLoading(true);
    try {
      await deleteRingReward(id);
      Message.success(t('message.success'));
      await loadRingRewardData();
    } finally {
      setRingRewardLoading(false);
    }
  };

  const onRingRewardCancel = () => {
    ringRewardModalVisible.value = false;
  };

  loadConfigData();
  loadRewardData();
  loadRingRewardData();
</script>

<script lang="ts">
  export default { name: 'Paohuan' };
</script>

<style lang="less" scoped>
  :deep(.arco-card-body, .arco-row) {
    width: 100%;
  }
</style>
