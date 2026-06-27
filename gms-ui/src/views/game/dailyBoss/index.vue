<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.growth.dailyBoss')">
      <a-tabs v-model:active-key="activeTab">
        <!-- Tab 1: Boss池管理 -->
        <a-tab-pane key="pool" :title="$t('dailyBoss.tab.pool')">
          <a-row>
            <a-col>
              <a-space>
                <a-button type="primary" status="success" @click="addClick">
                  {{ $t('button.create') }}
                </a-button>
                <a-button @click="loadData">{{ $t('button.search') }}</a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :loading="loading"
            :data="tableData"
            column-resizable
            :pagination="false"
            :bordered="{ cell: true }"
            style="margin-top: 16px"
          >
            <template #columns>
              <a-table-column
                :title="$t('dailyBoss.column.id')"
                data-index="id"
                :width="50"
                align="center"
              />
              <a-table-column
                :title="$t('dailyBoss.column.key')"
                data-index="bossKey"
                :width="130"
              />
              <a-table-column
                :title="$t('dailyBoss.column.name')"
                data-index="bossName"
                :width="100"
              />
              <a-table-column
                :title="$t('dailyBoss.column.mobId')"
                data-index="bossMobId"
                :width="80"
                align="center"
              />
              <a-table-column
                :title="$t('dailyBoss.column.rewards')"
                align="center"
                :width="60"
              >
                <template #cell="{ record }">
                  {{ record.rewards ? record.rewards.length : 0 }}
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('dailyBoss.column.sortOrder')"
                data-index="sortOrder"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('dailyBoss.column.enabled')"
                :width="55"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{
                    $t('dailyBoss.yes')
                  }}</a-tag>
                  <a-tag v-else color="gray">{{ $t('dailyBoss.no') }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('dailyBoss.column.operation')"
                :width="160"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button
                      type="text"
                      size="mini"
                      @click="editClick(record)"
                    >
                      {{ $t('dailyBoss.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('dailyBoss.delete.confirm')"
                      @ok="deleteClick(record.id!)"
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

        <!-- Tab 2: 环参数 -->
        <a-tab-pane key="params" :title="$t('dailyBoss.tab.params')">
          <a-table
            row-key="key"
            :loading="paramsLoading"
            :data="paramsTableData"
            :pagination="false"
            :bordered="{ cell: true }"
            style="margin-top: 16px"
          >
            <template #columns>
              <a-table-column title="参数名" data-index="key" :width="200" />
              <a-table-column title="值" data-index="value" />
              <a-table-column title="说明" data-index="desc" />
            </template>
          </a-table>
          <a-row style="margin-top: 12px">
            <a-col>
              <a-button @click="loadParams">{{ $t('button.search') }}</a-button>
            </a-col>
          </a-row>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="modalTitle"
      :width="750"
      @ok="saveClick"
      @cancel="onCancel"
    >
      <a-form
        :model="form"
        layout="vertical"
        style="max-height: 65vh; overflow-y: auto"
      >
        <a-divider>{{ $t('dailyBoss.title.config') }}</a-divider>
        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-item :label="$t('dailyBoss.column.key')">
              <a-input v-model="form.bossKey" placeholder="如：每日_扎昆" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('dailyBoss.column.name')">
              <a-input v-model="form.bossName" placeholder="Boss显示名称" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('dailyBoss.column.mobId')">
              <a-input-number
                v-model="form.bossMobId"
                :min="1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="4">
            <a-form-item :label="$t('dailyBoss.column.sortOrder')">
              <a-input-number
                v-model="form.sortOrder"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('dailyBoss.column.enabled')">
              <a-switch v-model="enabledBool" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider>
          {{ $t('dailyBoss.title.rewards') }}
          <a-button
            type="primary"
            size="mini"
            status="success"
            style="margin-left: 8px"
            @click="addReward"
          >
            {{ $t('dailyBoss.button.addReward') }}
          </a-button>
        </a-divider>
        <div v-for="(r, idx) in form.rewards" :key="idx">
          <a-space style="margin-bottom: 6px">
            <a-input-number
              v-model="r.completeCount"
              :placeholder="$t('dailyBoss.reward.completeCount')"
              style="width: 80px"
              :min="1"
            />
            <a-input
              v-model="r.rewardDesc"
              :placeholder="$t('dailyBoss.reward.desc')"
              style="width: 120px"
            />
            <a-input-number
              v-model="r.itemId"
              :placeholder="$t('dailyBoss.reward.itemId')"
              style="width: 100px"
            />
            <span>×</span>
            <a-input-number
              v-model="r.quantity"
              :min="1"
              :placeholder="$t('dailyBoss.reward.quantity')"
              style="width: 70px"
            />
            <a-input-number
              v-model="r.sortOrder"
              :min="0"
              :placeholder="$t('dailyBoss.reward.sortOrder')"
              style="width: 60px"
            />
            <a-button
              type="text"
              size="mini"
              status="danger"
              @click="removeReward(idx)"
            >
              {{ $t('dailyBoss.button.removeReward') }}
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
  import type { DailyBossForm, DailyBossGameParams } from '@/api/dailyBoss';
  import {
    deleteConfig,
    getConfig,
    getConfigList,
    getGameParams,
    saveConfig,
  } from '@/api/dailyBoss';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);
  const tableData = ref<DailyBossForm[]>([]);

  // Tab 状态
  const activeTab = ref('pool');

  // 环参数
  const paramsLoading = ref(false);
  const paramsTableData = ref<{ key: string; value: string; desc: string }[]>(
    []
  );

  const paramDescMap: Record<string, string> = {
    bossRingEnabled: '环式系统开关（0=旧系统, 1=新系统）',
    dailyLimit: '每日总环数',
    expBase: '每环基础经验',
    mesoBase: '每环基础金币',
    killMin: '随机最少击杀数',
    killMax: '随机最多击杀数',
    abandonFee: '放弃任务手续费',
    finalItemId: '最终完成奖励物品ID',
    finalItemQty: '最终完成奖励数量',
    milestoneRewards: '里程碑奖励（JSON）',
    randomRewards: '每环随机奖励池（JSON）',
  };

  const loadParams = async () => {
    paramsLoading.value = true;
    try {
      const { data } = await getGameParams();
      const params = data as unknown as DailyBossGameParams;
      paramsTableData.value = Object.entries(params).map(([key, value]) => ({
        key,
        value: value != null ? String(value) : '',
        desc: paramDescMap[key] || '',
      }));
    } finally {
      paramsLoading.value = false;
    }
  };
  loadParams();

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getConfigList();
      tableData.value = (data as unknown as DailyBossForm[]) || [];
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const modalVisible = ref(false);
  const editingId = ref<number | null>(null);

  const modalTitle = computed(() => {
    if (editingId.value)
      return `${t('dailyBoss.button.edit')} - ${form.value.bossName}`;
    return t('button.create');
  });

  const form = ref<DailyBossForm>({
    bossKey: '',
    bossName: '',
    bossMobId: undefined,
    sortOrder: 0,
    enabled: 1,
    rewards: [],
  });

  const enabledBool = computed({
    get: () => form.value.enabled === 1,
    set: (v: boolean) => {
      form.value.enabled = v ? 1 : 0;
    },
  });

  const resetForm = () => {
    form.value = {
      bossKey: '',
      bossName: '',
      bossMobId: undefined,
      sortOrder: 0,
      enabled: 1,
      rewards: [],
    };
    editingId.value = null;
  };

  const addClick = () => {
    resetForm();
    modalVisible.value = true;
  };

  const editClick = async (record: DailyBossForm) => {
    if (!record.id) return;
    setLoading(true);
    try {
      const { data } = await getConfig(record.id);
      const d = data as unknown as DailyBossForm;
      form.value = {
        id: d.id,
        bossKey: d.bossKey,
        bossName: d.bossName,
        bossMobId: d.bossMobId,
        sortOrder: d.sortOrder ?? 0,
        enabled: d.enabled ?? 1,
        rewards: d.rewards || [],
      };
      editingId.value = d.id!;
      modalVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  const saveClick = async () => {
    if (!form.value.bossKey || !form.value.bossName) {
      Message.warning('请填写Boss标识和名称');
      return;
    }
    setLoading(true);
    try {
      await saveConfig(form.value);
      Message.success(t('dailyBoss.save.success'));
      modalVisible.value = false;
      resetForm();
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const deleteClick = async (id: number) => {
    setLoading(true);
    try {
      await deleteConfig(id);
      Message.success(t('dailyBoss.delete.success'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const onCancel = () => {
    modalVisible.value = false;
  };

  const addReward = () => {
    if (!form.value.rewards) form.value.rewards = [];
    form.value.rewards.push({
      completeCount: 1,
      rewardDesc: '',
      itemId: undefined,
      quantity: 1,
      sortOrder: 0,
    });
  };

  const removeReward = (i: number) => {
    form.value.rewards?.splice(i, 1);
  };
</script>

<script lang="ts">
  export default { name: 'DailyBoss' };
</script>

<style lang="less" scoped>
  :deep(.arco-card-body, .arco-row) {
    width: 100%;
  }
</style>
