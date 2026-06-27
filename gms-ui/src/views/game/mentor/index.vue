<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.growth.mentor')">
      <a-tabs v-model:active-key="activeTab">
        <!-- Tab 1: 系统配置 -->
        <a-tab-pane key="config" :title="$t('mentor.tab.config')">
          <a-row>
            <a-col>
              <a-space>
                <a-button
                  type="primary"
                  status="success"
                  @click="addConfigClick"
                >
                  {{ $t('mentor.button.addConfig') }}
                </a-button>
                <a-button @click="loadConfigs">{{
                  $t('mentor.button.refresh')
                }}</a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :loading="configLoading"
            :data="configTableData"
            column-resizable
            :pagination="false"
            :bordered="{ cell: true }"
            style="margin-top: 16px"
          >
            <template #columns>
              <a-table-column
                :title="$t('mentor.column.id')"
                data-index="id"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('mentor.column.configKey')"
                data-index="configKey"
                :width="180"
              />
              <a-table-column
                :title="$t('mentor.column.configValue')"
                data-index="configValue"
                :width="100"
                align="center"
              />
              <a-table-column
                :title="$t('mentor.column.description')"
                data-index="description"
                :width="200"
              />
              <a-table-column
                :title="$t('mentor.column.enabled')"
                :width="70"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{
                    $t('mentor.yes')
                  }}</a-tag>
                  <a-tag v-else color="gray">{{ $t('mentor.no') }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('mentor.column.operation')"
                :width="160"
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
                      {{ $t('mentor.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('mentor.delete.config.confirm')"
                      @ok="deleteConfigClick(record.id!)"
                    >
                      <a-button type="text" size="mini" status="danger">{{
                        $t('button.delete')
                      }}</a-button>
                    </a-popconfirm>
                  </a-space>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-tab-pane>

        <!-- Tab 2: 毕业奖励 -->
        <a-tab-pane key="reward" :title="$t('mentor.tab.reward')">
          <a-row>
            <a-col>
              <a-space>
                <a-button
                  type="primary"
                  status="success"
                  @click="addRewardClick"
                >
                  {{ $t('mentor.button.addReward') }}
                </a-button>
                <a-button @click="loadRewards">{{
                  $t('mentor.button.refresh')
                }}</a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :loading="rewardLoading"
            :data="rewardTableData"
            column-resizable
            :pagination="false"
            :bordered="{ cell: true }"
            style="margin-top: 16px"
          >
            <template #columns>
              <a-table-column
                :title="$t('mentor.column.id')"
                data-index="id"
                :width="50"
                align="center"
              />
              <a-table-column
                :title="$t('mentor.column.rewardType')"
                :width="100"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.rewardType === 0" color="orange">{{
                    $t('mentor.rewardType.master')
                  }}</a-tag>
                  <a-tag v-else color="blue">{{
                    $t('mentor.rewardType.disciple')
                  }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('mentor.column.meso')"
                data-index="meso"
                :width="100"
                align="right"
              />
              <a-table-column
                :title="$t('mentor.column.nxCredit')"
                data-index="nxCredit"
                :width="80"
                align="right"
              />
              <a-table-column
                :title="$t('mentor.column.maplePoint')"
                data-index="maplePoint"
                :width="80"
                align="right"
              />
              <a-table-column
                :title="$t('mentor.column.nxPrepaid')"
                data-index="nxPrepaid"
                :width="80"
                align="right"
              />
              <a-table-column
                :title="$t('mentor.column.itemCount')"
                :width="70"
                align="center"
              >
                <template #cell="{ record }">
                  {{ record.items ? record.items.length : 0 }}
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('mentor.column.enabled')"
                :width="60"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{
                    $t('mentor.yes')
                  }}</a-tag>
                  <a-tag v-else color="gray">{{ $t('mentor.no') }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('mentor.column.operation')"
                :width="160"
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
                      {{ $t('mentor.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('mentor.delete.reward.confirm')"
                      @ok="deleteRewardClick(record.id!)"
                    >
                      <a-button type="text" size="mini" status="danger">{{
                        $t('button.delete')
                      }}</a-button>
                    </a-popconfirm>
                  </a-space>
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
      :width="550"
      @ok="saveConfigClick"
      @cancel="resetConfigForm"
    >
      <a-form :model="configForm" layout="vertical">
        <a-divider>{{ $t('mentor.title.config') }}</a-divider>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item :label="$t('mentor.column.configKey')">
              <a-input
                v-model="configForm.configKey"
                placeholder="如：create_master_level"
                :disabled="!!editingConfigId"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('mentor.column.configValue')">
              <a-input
                v-model="configForm.configValue"
                placeholder="请输入配置值"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="18">
            <a-form-item :label="$t('mentor.column.description')">
              <a-input
                v-model="configForm.description"
                placeholder="配置说明"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('mentor.column.enabled')">
              <a-switch v-model="configEnabledBool" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <!-- 毕业奖励编辑弹窗 -->
    <a-modal
      v-model:visible="rewardModalVisible"
      :title="rewardModalTitle"
      :width="650"
      @ok="saveRewardClick"
      @cancel="resetRewardForm"
    >
      <a-form
        :model="rewardForm"
        layout="vertical"
        style="max-height: 60vh; overflow-y: auto"
      >
        <a-divider>{{ $t('mentor.title.reward') }}</a-divider>
        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-item :label="$t('mentor.column.rewardType')">
              <a-select v-model="rewardForm.rewardType">
                <a-option :value="0">{{
                  $t('mentor.rewardType.master')
                }}</a-option>
                <a-option :value="1">{{
                  $t('mentor.rewardType.disciple')
                }}</a-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('mentor.column.enabled')">
              <a-switch v-model="rewardEnabledBool" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item :label="$t('mentor.column.meso')">
              <a-input-number
                v-model="rewardForm.meso"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('mentor.column.nxCredit')">
              <a-input-number
                v-model="rewardForm.nxCredit"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('mentor.column.maplePoint')">
              <a-input-number
                v-model="rewardForm.maplePoint"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('mentor.column.nxPrepaid')">
              <a-input-number
                v-model="rewardForm.nxPrepaid"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider>
          {{ $t('mentor.title.items') }}
          <a-button
            type="primary"
            size="mini"
            status="success"
            style="margin-left: 8px"
            @click="addRewardItem"
          >
            {{ $t('mentor.button.addItem') }}
          </a-button>
        </a-divider>
        <div v-for="(item, idx) in rewardForm.items" :key="idx">
          <a-space style="margin-bottom: 6px">
            <a-input-number
              v-model="item.itemId"
              :placeholder="$t('mentor.reward.itemId')"
              style="width: 120px"
              :min="1"
            />
            <span>×</span>
            <a-input-number
              v-model="item.quantity"
              :min="1"
              :placeholder="$t('mentor.reward.quantity')"
              style="width: 80px"
            />
            <a-button
              type="text"
              size="mini"
              status="danger"
              @click="removeRewardItem(idx)"
            >
              {{ $t('mentor.button.removeItem') }}
            </a-button>
          </a-space>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import type { GraduationReward, MentorConfig } from '@/api/mentor';
  import {
    deleteConfig,
    deleteReward,
    getConfigList,
    getRewardList,
    saveConfig,
    saveReward,
  } from '@/api/mentor';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();

  // ==================== Tab 状态 ====================
  const activeTab = ref('config');

  // ==================== 系统配置 ====================
  const { loading: configLoading, setLoading: setConfigLoading } =
    useLoading(false);
  const configTableData = ref<MentorConfig[]>([]);
  const configModalVisible = ref(false);
  const editingConfigId = ref<number | null>(null);

  const configForm = ref<MentorConfig>({
    configKey: '',
    configValue: '',
    description: '',
    enabled: 1,
  });

  const configEnabledBool = computed({
    get: () => configForm.value.enabled === 1,
    set: (v) => {
      configForm.value.enabled = v ? 1 : 0;
    },
  });

  const configModalTitle = computed(() =>
    editingConfigId.value
      ? t('mentor.button.edit')
      : t('mentor.button.addConfig')
  );

  const loadConfigs = async () => {
    setConfigLoading(true);
    try {
      const { data } = await getConfigList();
      configTableData.value = data;
    } finally {
      setConfigLoading(false);
    }
  };

  const resetConfigForm = () => {
    configForm.value = {
      configKey: '',
      configValue: '',
      description: '',
      enabled: 1,
    };
    editingConfigId.value = null;
    configModalVisible.value = false;
  };

  const addConfigClick = () => {
    resetConfigForm();
    configModalVisible.value = true;
  };

  const editConfigClick = (record: MentorConfig) => {
    configForm.value = { ...record };
    editingConfigId.value = record.id ?? null;
    configModalVisible.value = true;
  };

  const saveConfigClick = async () => {
    try {
      await saveConfig({
        id: editingConfigId.value ?? undefined,
        configKey: configForm.value.configKey,
        configValue: configForm.value.configValue,
        description: configForm.value.description,
        enabled: configForm.value.enabled,
      });
      Message.success(t('mentor.save.config.success'));
      resetConfigForm();
      await loadConfigs();
    } catch (e) {
      // 错误已在 http 拦截器中处理
    }
  };

  const deleteConfigClick = async (id: number) => {
    try {
      await deleteConfig(id);
      Message.success(t('mentor.delete.config.success'));
      await loadConfigs();
    } catch (e) {
      // 错误已在 http 拦截器中处理
    }
  };

  // ==================== 毕业奖励 ====================
  const { loading: rewardLoading, setLoading: setRewardLoading } =
    useLoading(false);
  const rewardTableData = ref<GraduationReward[]>([]);
  const rewardModalVisible = ref(false);
  const editingRewardId = ref<number | null>(null);

  const rewardForm = ref<GraduationReward>({
    rewardType: 0,
    meso: 0,
    nxCredit: 0,
    maplePoint: 0,
    nxPrepaid: 0,
    enabled: 1,
    items: [],
  });

  const rewardEnabledBool = computed({
    get: () => rewardForm.value.enabled === 1,
    set: (v) => {
      rewardForm.value.enabled = v ? 1 : 0;
    },
  });

  const rewardModalTitle = computed(() =>
    editingRewardId.value
      ? t('mentor.button.edit')
      : t('mentor.button.addReward')
  );

  const loadRewards = async () => {
    setRewardLoading(true);
    try {
      const { data } = await getRewardList();
      rewardTableData.value = data;
    } finally {
      setRewardLoading(false);
    }
  };

  const resetRewardForm = () => {
    rewardForm.value = {
      rewardType: 0,
      meso: 0,
      nxCredit: 0,
      maplePoint: 0,
      nxPrepaid: 0,
      enabled: 1,
      items: [],
    };
    editingRewardId.value = null;
    rewardModalVisible.value = false;
  };

  const addRewardClick = () => {
    resetRewardForm();
    rewardModalVisible.value = true;
  };

  const editRewardClick = (record: GraduationReward) => {
    rewardForm.value = {
      id: record.id,
      rewardType: record.rewardType,
      meso: record.meso ?? 0,
      nxCredit: record.nxCredit ?? 0,
      maplePoint: record.maplePoint ?? 0,
      nxPrepaid: record.nxPrepaid ?? 0,
      enabled: record.enabled ?? 1,
      items: record.items ? [...record.items] : [],
    };
    editingRewardId.value = record.id ?? null;
    rewardModalVisible.value = true;
  };

  const saveRewardClick = async () => {
    try {
      await saveReward({
        id: editingRewardId.value ?? undefined,
        rewardType: rewardForm.value.rewardType,
        meso: rewardForm.value.meso,
        nxCredit: rewardForm.value.nxCredit,
        maplePoint: rewardForm.value.maplePoint,
        nxPrepaid: rewardForm.value.nxPrepaid,
        enabled: rewardForm.value.enabled,
        items: rewardForm.value.items,
      });
      Message.success(t('mentor.save.reward.success'));
      resetRewardForm();
      await loadRewards();
    } catch (e) {
      // 错误已在 http 拦截器中处理
    }
  };

  const deleteRewardClick = async (id: number) => {
    try {
      await deleteReward(id);
      Message.success(t('mentor.delete.reward.success'));
      await loadRewards();
    } catch (e) {
      // 错误已在 http 拦截器中处理
    }
  };

  const addRewardItem = () => {
    if (!rewardForm.value.items) rewardForm.value.items = [];
    rewardForm.value.items.push({ itemId: 0, quantity: 1 });
  };

  const removeRewardItem = (index: number) => {
    rewardForm.value.items?.splice(index, 1);
  };

  // ==================== 初始化 ====================
  loadConfigs();
  loadRewards();
</script>

<script lang="ts">
  export default { name: 'Mentor' };
</script>

<style lang="less" scoped>
  .container {
    padding: 24px;
  }
</style>
