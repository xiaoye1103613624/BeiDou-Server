<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.growth.levelReward')">
      <!-- 操作栏 -->
      <a-row>
        <a-col>
          <a-space>
            <a-button type="primary" status="success" @click="addClick">
              {{ $t('button.create') }}
            </a-button>
            <a-button @click="loadData">
              {{ $t('button.search') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>

      <!-- 配置列表 -->
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
            :title="$t('levelReward.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('levelReward.column.level')"
            data-index="level"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('levelReward.column.meso')"
            data-index="meso"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              {{ (record.meso || 0).toLocaleString() }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('levelReward.column.nxCredit')"
            data-index="nxCredit"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              {{ (record.nxCredit || 0).toLocaleString() }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('levelReward.column.maplePoint')"
            data-index="maplePoint"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              {{ (record.maplePoint || 0).toLocaleString() }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('levelReward.column.nxPrepaid')"
            data-index="nxPrepaid"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              {{ (record.nxPrepaid || 0).toLocaleString() }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('levelReward.column.items')"
            align="center"
            :width="80"
          >
            <template #cell="{ record }">
              <span>{{ record.items ? record.items.length : 0 }} 种道具</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('levelReward.column.enabled')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green">
                {{ $t('levelReward.yes') }}
              </a-tag>
              <a-tag v-else color="gray">
                {{ $t('levelReward.no') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('levelReward.column.operation')"
            :width="160"
            fixed="right"
            align="center"
          >
            <template #cell="{ record }">
              <a-space :size="0">
                <a-button type="text" size="mini" @click="editClick(record)">
                  {{ $t('levelReward.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('levelReward.delete.confirm')"
                  position="top"
                  @ok="deleteClick(record.id)"
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
    </a-card>

    <!-- 编辑/新增弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="modalTitle"
      :width="700"
      @ok="saveClick"
      @cancel="onCancel"
    >
      <a-form
        :model="form"
        layout="vertical"
        style="max-height: 65vh; overflow-y: auto"
      >
        <!-- 基础信息 -->
        <a-divider>{{ $t('levelReward.title.config') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="6">
            <a-form-item :label="$t('levelReward.column.level')">
              <a-input-number
                v-model="form.level"
                :min="1"
                :max="300"
                :placeholder="$t('levelReward.column.level')"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('levelReward.column.meso')">
              <a-input-number
                v-model="form.meso"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('levelReward.column.nxCredit')">
              <a-input-number
                v-model="form.nxCredit"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('levelReward.column.maplePoint')">
              <a-input-number
                v-model="form.maplePoint"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('levelReward.column.nxPrepaid')">
              <a-input-number
                v-model="form.nxPrepaid"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="4">
            <a-form-item :label="$t('levelReward.column.enabled')">
              <a-switch v-model="enabledBool" />
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 道具奖励 -->
        <a-divider>
          {{ $t('levelReward.title.items') }}
          <a-button
            type="primary"
            size="mini"
            status="success"
            style="margin-left: 8px"
            @click="addItem"
          >
            {{ $t('levelReward.button.addItem') }}
          </a-button>
        </a-divider>

        <div v-for="(item, idx) in form.items" :key="idx">
          <a-space style="margin-bottom: 8px">
            <a-input-number
              v-model="item.itemId"
              :placeholder="$t('levelReward.item.itemId')"
              style="width: 140px"
            />
            <span style="margin: 0 4px">×</span>
            <a-input-number
              v-model="item.count"
              :min="1"
              :placeholder="$t('levelReward.item.count')"
              style="width: 100px"
            />
            <a-button
              type="text"
              size="mini"
              status="danger"
              @click="removeItem(idx)"
            >
              {{ $t('levelReward.button.removeItem') }}
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
  import type { LevelRewardForm } from '@/api/levelReward';
  import {
    deleteReward,
    getReward,
    getRewardList,
    saveReward,
  } from '@/api/levelReward';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();

  const { loading, setLoading } = useLoading(false);

  // 列表数据
  const tableData = ref<LevelRewardForm[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getRewardList();
      tableData.value = data as unknown as LevelRewardForm[];
    } finally {
      setLoading(false);
    }
  };
  loadData();

  // 弹窗控制
  const modalVisible = ref(false);
  const editingId = ref<number | null>(null);

  // 弹窗标题
  const modalTitle = computed(() => {
    if (editingId.value) {
      return `${t('levelReward.button.edit')} - Lv.${form.value.level}`;
    }
    return t('button.create');
  });

  // 表单数据
  const form = ref<LevelRewardForm>({
    level: undefined,
    meso: 0,
    nxCredit: 0,
    maplePoint: 0,
    nxPrepaid: 0,
    enabled: 1,
    items: [],
  });

  const enabledBool = computed({
    get: () => form.value.enabled === 1,
    set: (v: boolean) => {
      form.value.enabled = v ? 1 : 0;
    },
  });

  const resetForm = () => {
    form.value = {
      level: undefined,
      meso: 0,
      nxCredit: 0,
      maplePoint: 0,
      nxPrepaid: 0,
      enabled: 1,
      items: [],
    };
    editingId.value = null;
  };

  // 新增配置
  const addClick = () => {
    resetForm();
    modalVisible.value = true;
  };

  // 编辑配置
  const editClick = async (record: LevelRewardForm) => {
    if (record.id == null) return;
    setLoading(true);
    try {
      const { data } = await getReward(record.id);
      const d = data as unknown as LevelRewardForm;
      form.value = {
        id: d.id,
        level: d.level,
        meso: d.meso ?? 0,
        nxCredit: d.nxCredit ?? 0,
        maplePoint: d.maplePoint ?? 0,
        nxPrepaid: d.nxPrepaid ?? 0,
        enabled: d.enabled ?? 1,
        items: d.items || [],
      };
      editingId.value = d.id ?? null;
      modalVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  // 保存
  const saveClick = async () => {
    if (!form.value.level) {
      Message.warning('请输入等级');
      return;
    }
    setLoading(true);
    try {
      await saveReward(form.value);
      Message.success(t('levelReward.save.success'));
      modalVisible.value = false;
      resetForm();
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // 删除
  const deleteClick = async (id: number) => {
    setLoading(true);
    try {
      await deleteReward(id);
      Message.success(t('levelReward.delete.success'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // 取消弹窗
  const onCancel = () => {
    modalVisible.value = false;
  };

  // 添加道具
  const addItem = () => {
    if (!form.value.items) form.value.items = [];
    form.value.items.push({ itemId: undefined, count: 1 });
  };

  // 删除道具
  const removeItem = (index: number) => {
    form.value.items?.splice(index, 1);
  };
</script>

<script lang="ts">
  export default {
    name: 'LevelReward',
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
