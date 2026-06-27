<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.growth.dailyDungeon')">
      <a-tabs v-model:active-key="activeTab">
        <!-- Tab 1: 副本配置 -->
        <a-tab-pane key="config" :title="$t('dailyDungeon.tab.config')">
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space>
                <a-button
                  type="primary"
                  status="success"
                  @click="addConfigClick"
                >
                  {{ $t('button.create') }}
                </a-button>
                <a-input
                  v-model="configFilter.name"
                  :placeholder="$t('dailyDungeon.filter.name')"
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
            :loading="configLoading"
            :data="configTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('dailyDungeon.column.id')"
                data-index="id"
                :width="50"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.column.name')"
                data-index="dungeonName"
                :width="120"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.column.mapId')"
                data-index="mapId"
                :width="90"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.column.mapName')"
                data-index="mapName"
                :width="140"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.column.completeCount')"
                data-index="completeCount"
                :width="80"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.column.sortOrder')"
                data-index="sortOrder"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.column.enabled')"
                :width="55"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{
                    $t('dailyDungeon.yes')
                  }}</a-tag>
                  <a-tag v-else color="gray">{{ $t('dailyDungeon.no') }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('dailyDungeon.column.operation')"
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
                      {{ $t('dailyDungeon.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('dailyDungeon.delete.confirm')"
                      @ok="deleteConfigClick(record.id!)"
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

        <!-- Tab 2: 每日奖励 -->
        <a-tab-pane
          key="dailyReward"
          :title="$t('dailyDungeon.tab.dailyReward')"
        >
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space>
                <a-button
                  type="primary"
                  status="success"
                  @click="addDailyRewardClick"
                >
                  {{ $t('dailyDungeon.dailyReward.add') }}
                </a-button>
                <a-input-number
                  v-model="dailyRewardFilter.itemId"
                  :placeholder="$t('dailyDungeon.filter.itemId')"
                  :min="0"
                  :max="5999999"
                  style="width: 140px"
                  allow-clear
                  @keydown.enter="searchDailyRewardData"
                />
                <a-input
                  v-model="dailyRewardFilter.itemName"
                  :placeholder="$t('dailyDungeon.filter.itemName')"
                  style="width: 160px"
                  allow-clear
                  @keydown.enter="searchDailyRewardData"
                />
                <a-button @click="searchDailyRewardData">
                  {{ $t('button.search') }}
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :loading="dailyRewardLoading"
            :data="dailyRewardTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('dailyDungeon.column.id')"
                data-index="id"
                :width="50"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.dailyReward.itemId')"
                data-index="itemId"
                :width="100"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.dailyReward.itemName')"
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
                :title="$t('dailyDungeon.dailyReward.quantity')"
                data-index="quantity"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.dailyReward.desc')"
                data-index="rewardDesc"
                :width="150"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.dailyReward.sortOrder')"
                data-index="sortOrder"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.column.operation')"
                :width="140"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button
                      type="text"
                      size="mini"
                      @click="editDailyRewardClick(record)"
                    >
                      {{ $t('dailyDungeon.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('dailyDungeon.dailyReward.delete.confirm')"
                      @ok="deleteDailyRewardClick(record.id!)"
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

        <!-- Tab 3: VIP配置 -->
        <a-tab-pane key="vip" :title="$t('dailyDungeon.tab.vip')">
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space>
                <a-button
                  type="primary"
                  status="success"
                  @click="addVipConfigClick"
                >
                  {{ $t('dailyDungeon.vip.add') }}
                </a-button>
                <a-input-number
                  v-model="vipFilter.itemId"
                  :placeholder="$t('dailyDungeon.filter.itemId')"
                  :min="0"
                  :max="5999999"
                  style="width: 140px"
                  allow-clear
                  @keydown.enter="searchVipData"
                />
                <a-input
                  v-model="vipFilter.itemName"
                  :placeholder="$t('dailyDungeon.filter.itemName')"
                  style="width: 160px"
                  allow-clear
                  @keydown.enter="searchVipData"
                />
                <a-button @click="searchVipData">
                  {{ $t('button.search') }}
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :loading="vipLoading"
            :data="vipTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('dailyDungeon.column.id')"
                data-index="id"
                :width="50"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.vip.itemId')"
                data-index="itemId"
                :width="100"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.vip.itemName')"
                :width="200"
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
                :title="$t('dailyDungeon.vip.desc')"
                data-index="description"
                :width="160"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.column.enabled')"
                :width="55"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{
                    $t('dailyDungeon.yes')
                  }}</a-tag>
                  <a-tag v-else color="gray">{{ $t('dailyDungeon.no') }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('dailyDungeon.vip.sortOrder')"
                data-index="sortOrder"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('dailyDungeon.column.operation')"
                :width="140"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button
                      type="text"
                      size="mini"
                      @click="editVipConfigClick(record)"
                    >
                      {{ $t('dailyDungeon.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('dailyDungeon.vip.delete.confirm')"
                      @ok="deleteVipConfigClick(record.id!)"
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

    <!-- 副本配置编辑弹窗 -->
    <a-modal
      v-model:visible="configModalVisible"
      :title="configModalTitle"
      :width="750"
      @ok="saveConfigClick"
      @cancel="onConfigCancel"
    >
      <a-form
        :model="configForm"
        layout="vertical"
        style="max-height: 65vh; overflow-y: auto"
      >
        <a-divider>{{ $t('dailyDungeon.title.config') }}</a-divider>
        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-item :label="$t('dailyDungeon.column.name')">
              <a-input
                v-model="configForm.dungeonName"
                placeholder="副本显示名称"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('dailyDungeon.column.mapId')">
              <a-input-number
                v-model="configForm.mapId"
                :min="1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('dailyDungeon.column.completeCount')">
              <a-input-number
                v-model="configForm.completeCount"
                :min="1"
                :max="999"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-item :label="$t('dailyDungeon.column.sortOrder')">
              <a-input-number
                v-model="configForm.sortOrder"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('dailyDungeon.column.enabled')">
              <a-switch v-model="configEnabledBool" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider>
          {{ $t('dailyDungeon.title.rewards') }}
          <a-button
            type="primary"
            size="mini"
            status="success"
            style="margin-left: 8px"
            @click="addReward"
          >
            {{ $t('dailyDungeon.button.addReward') }}
          </a-button>
        </a-divider>
        <div v-for="(r, idx) in configForm.rewards" :key="idx">
          <a-space style="margin-bottom: 6px">
            <a-input-number
              v-model="r.completeCount"
              :placeholder="$t('dailyDungeon.reward.completeCount')"
              style="width: 80px"
              :min="1"
            />
            <a-input
              v-model="r.rewardDesc"
              :placeholder="$t('dailyDungeon.reward.desc')"
              style="width: 120px"
            />
            <a-input-number
              v-model="r.itemId"
              :placeholder="$t('dailyDungeon.reward.itemId')"
              style="width: 100px"
            />
            <span>×</span>
            <a-input-number
              v-model="r.quantity"
              :min="1"
              :placeholder="$t('dailyDungeon.reward.quantity')"
              style="width: 70px"
            />
            <a-input-number
              v-model="r.sortOrder"
              :min="0"
              :placeholder="$t('dailyDungeon.reward.sortOrder')"
              style="width: 60px"
            />
            <a-button
              type="text"
              size="mini"
              status="danger"
              @click="removeReward(idx)"
            >
              {{ $t('dailyDungeon.button.removeReward') }}
            </a-button>
          </a-space>
        </div>
      </a-form>
    </a-modal>

    <!-- 每日奖励编辑弹窗 -->
    <a-modal
      v-model:visible="dailyRewardModalVisible"
      :title="dailyRewardModalTitle"
      :width="400"
      @ok="saveDailyRewardClick"
      @cancel="onDailyRewardCancel"
    >
      <a-form :model="dailyRewardForm" layout="vertical">
        <a-form-item :label="$t('dailyDungeon.dailyReward.itemId')">
          <a-input-number
            v-model="dailyRewardForm.itemId"
            :min="0"
            :max="5999999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('dailyDungeon.dailyReward.quantity')">
          <a-input-number
            v-model="dailyRewardForm.quantity"
            :min="1"
            :max="99999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('dailyDungeon.dailyReward.desc')">
          <a-input v-model="dailyRewardForm.rewardDesc" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyDungeon.dailyReward.sortOrder')">
          <a-input-number
            v-model="dailyRewardForm.sortOrder"
            :min="0"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- VIP配置编辑弹窗 -->
    <a-modal
      v-model:visible="vipModalVisible"
      :title="vipModalTitle"
      :width="400"
      @ok="saveVipConfigClick"
      @cancel="onVipCancel"
    >
      <a-form :model="vipForm" layout="vertical">
        <a-form-item :label="$t('dailyDungeon.vip.itemId')">
          <a-input-number
            v-model="vipForm.itemId"
            :min="1"
            :max="5999999"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('dailyDungeon.vip.desc')">
          <a-input v-model="vipForm.description" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyDungeon.column.enabled')">
          <a-switch v-model="vipEnabledBool" />
        </a-form-item>
        <a-form-item :label="$t('dailyDungeon.vip.sortOrder')">
          <a-input-number
            v-model="vipForm.sortOrder"
            :min="0"
            style="width: 100%"
          />
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
    DailyDungeonForm,
    DailyRewardForm,
    VipConfigForm,
  } from '@/api/dailyDungeon';
  import {
    deleteConfig,
    deleteDailyReward,
    deleteVipConfig,
    getConfig,
    getConfigList,
    getDailyRewardList,
    getVipConfigList,
    saveConfig,
    saveDailyReward,
    saveVipConfig,
  } from '@/api/dailyDungeon';
  import { Message } from '@arco-design/web-vue';
  import { getIconUrl } from '@/utils/mapleStoryAPI';

  const { t } = useI18n();
  const activeTab = ref('config');

  // ==================== Tab 1: 副本配置 ====================
  const { loading: configLoading, setLoading: setConfigLoading } =
    useLoading(false);
  const configTableData = ref<DailyDungeonForm[]>([]);
  /** 全量数据 */
  const configAllData = ref<DailyDungeonForm[]>([]);
  /** 搜索筛选 */
  const configFilter = ref({ name: '' });

  const loadConfigData = async () => {
    setConfigLoading(true);
    try {
      const { data } = await getConfigList();
      configAllData.value = (data as unknown as DailyDungeonForm[]) || [];
      configTableData.value = configAllData.value;
    } finally {
      setConfigLoading(false);
    }
  };

  const searchConfigData = () => {
    const filterName = configFilter.value.name?.trim().toLowerCase();
    if (!filterName) {
      configTableData.value = configAllData.value;
      return;
    }
    configTableData.value = configAllData.value.filter((item) =>
      (item.dungeonName || '').toLowerCase().includes(filterName)
    );
  };

  const configModalVisible = ref(false);
  const editingConfigId = ref<number | null>(null);
  const configModalTitle = computed(() =>
    editingConfigId.value
      ? `${t('dailyDungeon.button.edit')} - ${configForm.value.dungeonName}`
      : t('button.create')
  );

  const configForm = ref<DailyDungeonForm>({
    dungeonKey: '',
    dungeonName: '',
    mapId: undefined,
    completeCount: 3,
    sweepItemId: 0,
    sweepItemCost: 1,
    maxSweep: 0,
    sortOrder: 0,
    enabled: 1,
    rewards: [],
  });
  const configEnabledBool = computed({
    get: () => configForm.value.enabled === 1,
    set: (v: boolean) => {
      configForm.value.enabled = v ? 1 : 0;
    },
  });

  const resetConfigForm = () => {
    configForm.value = {
      dungeonKey: '',
      dungeonName: '',
      mapId: undefined,
      completeCount: 3,
      sweepItemId: 0,
      sweepItemCost: 1,
      maxSweep: 0,
      sortOrder: 0,
      enabled: 1,
      rewards: [],
    };
    editingConfigId.value = null;
  };

  const addConfigClick = () => {
    resetConfigForm();
    configModalVisible.value = true;
  };

  const editConfigClick = async (record: DailyDungeonForm) => {
    if (!record.id) return;
    setConfigLoading(true);
    try {
      const { data } = await getConfig(record.id);
      const d = data as unknown as DailyDungeonForm;
      configForm.value = {
        id: d.id,
        dungeonKey: d.dungeonKey,
        dungeonName: d.dungeonName,
        mapId: d.mapId,
        mapName: d.mapName,
        completeCount: d.completeCount ?? 3,
        sweepItemId: d.sweepItemId ?? 0,
        sweepItemCost: d.sweepItemCost ?? 1,
        maxSweep: d.maxSweep ?? 0,
        sortOrder: d.sortOrder ?? 0,
        enabled: d.enabled ?? 1,
        rewards: d.rewards || [],
      };
      editingConfigId.value = d.id!;
      configModalVisible.value = true;
    } finally {
      setConfigLoading(false);
    }
  };

  const saveConfigClick = async () => {
    if (!configForm.value.dungeonName) {
      Message.warning(t('dailyDungeon.validate.name'));
      return;
    }
    if (!configForm.value.mapId) {
      Message.warning(t('dailyDungeon.validate.mapId'));
      return;
    }
    // 自动生成 dungeonKey
    if (!configForm.value.dungeonKey) {
      configForm.value.dungeonKey = `每日_${configForm.value.dungeonName}`;
    }
    setConfigLoading(true);
    try {
      await saveConfig(configForm.value);
      Message.success(t('dailyDungeon.save.success'));
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
      Message.success(t('dailyDungeon.delete.success'));
      await loadConfigData();
    } finally {
      setConfigLoading(false);
    }
  };

  const onConfigCancel = () => {
    configModalVisible.value = false;
  };

  const addReward = () => {
    if (!configForm.value.rewards) configForm.value.rewards = [];
    configForm.value.rewards.push({
      completeCount: 1,
      rewardDesc: '',
      itemId: undefined,
      quantity: 1,
      sortOrder: 0,
    });
  };

  const removeReward = (i: number) => {
    configForm.value.rewards?.splice(i, 1);
  };

  // ==================== Tab 2: 每日奖励 ====================
  const { loading: dailyRewardLoading, setLoading: setDailyRewardLoading } =
    useLoading(false);
  const dailyRewardTableData = ref<DailyRewardForm[]>([]);
  const dailyRewardAllData = ref<DailyRewardForm[]>([]);
  const dailyRewardFilter = ref({
    itemId: undefined as number | undefined,
    itemName: '',
  });

  const loadDailyRewardData = async () => {
    setDailyRewardLoading(true);
    try {
      const { data } = await getDailyRewardList();
      dailyRewardAllData.value = (data as unknown as DailyRewardForm[]) || [];
      dailyRewardTableData.value = dailyRewardAllData.value;
    } finally {
      setDailyRewardLoading(false);
    }
  };

  const searchDailyRewardData = () => {
    const filterItemId = dailyRewardFilter.value.itemId;
    const filterName = dailyRewardFilter.value.itemName?.trim().toLowerCase();
    if (!filterItemId && !filterName) {
      dailyRewardTableData.value = dailyRewardAllData.value;
      return;
    }
    dailyRewardTableData.value = dailyRewardAllData.value.filter((item) => {
      if (filterItemId && item.itemId !== filterItemId) return false;
      if (
        filterName &&
        !(item.itemName || '').toLowerCase().includes(filterName)
      )
        return false;
      return true;
    });
  };

  const dailyRewardModalVisible = ref(false);
  const editingDailyRewardId = ref<number | null>(null);
  const dailyRewardModalTitle = computed(() =>
    editingDailyRewardId.value
      ? t('dailyDungeon.button.edit')
      : t('dailyDungeon.dailyReward.add')
  );

  const dailyRewardForm = ref<DailyRewardForm>({
    itemId: undefined,
    quantity: 1,
    rewardDesc: '',
    sortOrder: 0,
  });

  const resetDailyRewardForm = () => {
    dailyRewardForm.value = {
      itemId: undefined,
      quantity: 1,
      rewardDesc: '',
      sortOrder: 0,
    };
    editingDailyRewardId.value = null;
  };

  const addDailyRewardClick = () => {
    resetDailyRewardForm();
    dailyRewardModalVisible.value = true;
  };

  const editDailyRewardClick = (record: DailyRewardForm) => {
    dailyRewardForm.value = { ...record };
    editingDailyRewardId.value = record.id ?? null;
    dailyRewardModalVisible.value = true;
  };

  const saveDailyRewardClick = async () => {
    if (!dailyRewardForm.value.itemId && dailyRewardForm.value.itemId !== 0) {
      Message.warning(t('dailyDungeon.validate.itemId'));
      return;
    }
    setDailyRewardLoading(true);
    try {
      await saveDailyReward(dailyRewardForm.value);
      Message.success(t('message.success'));
      dailyRewardModalVisible.value = false;
      resetDailyRewardForm();
      await loadDailyRewardData();
    } finally {
      setDailyRewardLoading(false);
    }
  };

  const deleteDailyRewardClick = async (id: number) => {
    setDailyRewardLoading(true);
    try {
      await deleteDailyReward(id);
      Message.success(t('message.success'));
      await loadDailyRewardData();
    } finally {
      setDailyRewardLoading(false);
    }
  };

  const onDailyRewardCancel = () => {
    dailyRewardModalVisible.value = false;
  };

  // ==================== Tab 3: VIP配置 ====================
  const { loading: vipLoading, setLoading: setVipLoading } = useLoading(false);
  const vipTableData = ref<VipConfigForm[]>([]);
  const vipAllData = ref<VipConfigForm[]>([]);
  const vipFilter = ref({
    itemId: undefined as number | undefined,
    itemName: '',
  });

  const loadVipData = async () => {
    setVipLoading(true);
    try {
      const { data } = await getVipConfigList();
      vipAllData.value = (data as unknown as VipConfigForm[]) || [];
      vipTableData.value = vipAllData.value;
    } finally {
      setVipLoading(false);
    }
  };

  const searchVipData = () => {
    const filterItemId = vipFilter.value.itemId;
    const filterName = vipFilter.value.itemName?.trim().toLowerCase();
    if (!filterItemId && !filterName) {
      vipTableData.value = vipAllData.value;
      return;
    }
    vipTableData.value = vipAllData.value.filter((item) => {
      if (filterItemId && item.itemId !== filterItemId) return false;
      if (
        filterName &&
        !(item.itemName || '').toLowerCase().includes(filterName)
      )
        return false;
      return true;
    });
  };

  const vipModalVisible = ref(false);
  const editingVipId = ref<number | null>(null);
  const vipModalTitle = computed(() =>
    editingVipId.value
      ? t('dailyDungeon.button.edit')
      : t('dailyDungeon.vip.add')
  );

  const vipForm = ref<VipConfigForm>({
    itemId: undefined,
    description: '',
    enabled: 1,
    sortOrder: 0,
  });
  const vipEnabledBool = computed({
    get: () => vipForm.value.enabled === 1,
    set: (v: boolean) => {
      vipForm.value.enabled = v ? 1 : 0;
    },
  });

  const resetVipForm = () => {
    vipForm.value = {
      itemId: undefined,
      description: '',
      enabled: 1,
      sortOrder: 0,
    };
    editingVipId.value = null;
  };

  const addVipConfigClick = () => {
    resetVipForm();
    vipModalVisible.value = true;
  };

  const editVipConfigClick = (record: VipConfigForm) => {
    vipForm.value = { ...record };
    editingVipId.value = record.id ?? null;
    vipModalVisible.value = true;
  };

  const saveVipConfigClick = async () => {
    if (!vipForm.value.itemId) {
      Message.warning(t('dailyDungeon.validate.itemId'));
      return;
    }
    setVipLoading(true);
    try {
      await saveVipConfig(vipForm.value);
      Message.success(t('message.success'));
      vipModalVisible.value = false;
      resetVipForm();
      await loadVipData();
    } finally {
      setVipLoading(false);
    }
  };

  const deleteVipConfigClick = async (id: number) => {
    setVipLoading(true);
    try {
      await deleteVipConfig(id);
      Message.success(t('message.success'));
      await loadVipData();
    } finally {
      setVipLoading(false);
    }
  };

  const onVipCancel = () => {
    vipModalVisible.value = false;
  };

  // 页面初始化
  loadConfigData();
  loadDailyRewardData();
  loadVipData();
</script>

<script lang="ts">
  export default { name: 'DailyDungeon' };
</script>

<style lang="less" scoped>
  :deep(.arco-card-body, .arco-row) {
    width: 100%;
  }
</style>
