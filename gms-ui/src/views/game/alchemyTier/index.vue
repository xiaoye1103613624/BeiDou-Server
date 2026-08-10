<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="t('menu.game.alchemyTier')">
      <a-alert
        :message="t('alchemyTier.tip')"
        type="info"
        :closable="false"
        style="margin-bottom: 16px"
      />

      <!-- 查询条件 -->
      <a-form layout="inline" :model="query">
        <a-form-item :label="$t('alchemyTier.column.type')">
          <a-radio-group v-model="query.type" @change="() => loadData()">
            <a-radio :value="1">{{ $t('alchemyTier.option.type1') }}</a-radio>
            <a-radio :value="2">{{ $t('alchemyTier.option.type2') }}</a-radio>
            <a-radio :value="3">{{ $t('alchemyTier.option.type3') }}</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>

      <!-- 操作栏 -->
      <a-row style="margin-top: 16px">
        <a-col>
          <a-space>
            <a-button type="primary" status="success" @click="addTierClick">
              {{ $t('button.create') }}
            </a-button>
            <a-button @click="loadData">
              {{ $t('button.search') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>

      <!-- 品级列表 -->
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
            :title="$t('alchemyTier.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyTier.column.type')"
            :width="90"
            align="center"
          >
            <template #cell="{ record }">
              {{ typeLabel(record.type) }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('alchemyTier.column.name')"
            data-index="name"
            :width="140"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyTier.column.expStart')"
            data-index="expStart"
            :width="140"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyTier.column.isMax')"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="record.isMax === 1 ? 'green' : 'gray'">
                {{ record.isMax === 1 ? '是' : '否' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('alchemyTier.column.sortOrder')"
            data-index="sortOrder"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyTier.column.enabled')"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              <a-switch
                :model-value="record.enabled === 1"
                @change="(v) => onToggleEnabled(record, v as boolean)"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('alchemyTier.column.operation')"
            :width="160"
            fixed="right"
            align="center"
          >
            <template #cell="{ record }">
              <a-space :size="0">
                <a-button
                  type="text"
                  size="mini"
                  @click="editTierClick(record)"
                >
                  {{ $t('alchemyTier.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('alchemyTier.delete.confirm')"
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
      :width="600"
      @ok="saveClick"
      @cancel="onCancel"
    >
      <a-form :model="form" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('alchemyTier.column.type')">
              <a-radio-group v-model="form.type">
                <a-radio :value="1">{{
                  $t('alchemyTier.option.type1')
                }}</a-radio>
                <a-radio :value="2">{{
                  $t('alchemyTier.option.type2')
                }}</a-radio>
                <a-radio :value="3">{{
                  $t('alchemyTier.option.type3')
                }}</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('alchemyTier.column.name')">
              <a-input
                v-model="form.name"
                :placeholder="$t('alchemyTier.column.name')"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('alchemyTier.column.expStart')">
              <a-input-number
                v-model="form.expStart"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('alchemyTier.column.sortOrder')">
              <a-input-number
                v-model="form.sortOrder"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('alchemyTier.column.isMax')">
              <a-switch v-model="isMaxBool" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('alchemyTier.column.enabled')">
              <a-switch v-model="enabledBool" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, reactive, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import {
    deleteTier,
    getTierList,
    saveTier,
    toggleEnabled,
  } from '@/api/alchemyTier';
  import type { AlchemyTierForm } from '@/api/alchemyTier';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();

  const { loading, setLoading } = useLoading(false);

  // 查询条件
  const query = reactive<{ type: number }>({ type: 1 });

  // 列表数据
  const tableData = ref<AlchemyTierForm[]>([]);

  const typeLabel = (type?: number) => {
    if (type === 2) return t('alchemyTier.option.type2');
    if (type === 3) return t('alchemyTier.option.type3');
    return t('alchemyTier.option.type1');
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getTierList(query.type);
      tableData.value = data as unknown as AlchemyTierForm[];
    } finally {
      setLoading(false);
    }
  };
  loadData();

  // 弹窗控制
  const modalVisible = ref(false);
  const editingId = ref<number | null>(null);

  const modalTitle = computed(() => {
    if (editingId.value) {
      return `${t('alchemyTier.button.edit')} - ${form.value.name}`;
    }
    return t('button.create');
  });

  const emptyForm = (): AlchemyTierForm => ({
    type: query.type,
    name: '',
    expStart: 0,
    isMax: 0,
    sortOrder: 0,
    enabled: 1,
  });

  // 表单数据
  const form = ref<AlchemyTierForm>(emptyForm());

  const isMaxBool = computed({
    get: () => form.value.isMax === 1,
    set: (v: boolean) => {
      form.value.isMax = v ? 1 : 0;
    },
  });

  const enabledBool = computed({
    get: () => form.value.enabled === 1,
    set: (v: boolean) => {
      form.value.enabled = v ? 1 : 0;
    },
  });

  const resetForm = () => {
    form.value = emptyForm();
    editingId.value = null;
  };

  // 新增品级
  const addTierClick = () => {
    resetForm();
    modalVisible.value = true;
  };

  // 编辑品级
  const editTierClick = (record: AlchemyTierForm) => {
    form.value = { ...emptyForm(), ...record };
    editingId.value = record.id ?? null;
    modalVisible.value = true;
  };

  // 保存
  const saveClick = async () => {
    if (!form.value.name) {
      Message.warning('请输入品级名称');
      return;
    }
    setLoading(true);
    try {
      await saveTier(form.value);
      Message.success(t('alchemyTier.save.success'));
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
      await deleteTier(id);
      Message.success(t('alchemyTier.delete.success'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // 快速切换启用状态
  const onToggleEnabled = async (record: AlchemyTierForm, v: boolean) => {
    try {
      await toggleEnabled(record.id!);
      record.enabled = v ? 1 : 0;
      Message.success(v ? '已启用' : '已禁用');
    } catch {
      Message.error('操作失败');
    }
  };

  // 取消弹窗
  const onCancel = () => {
    modalVisible.value = false;
  };
</script>

<script lang="ts">
  export default {
    name: 'AlchemyTier',
  };
</script>

<style lang="less" scoped></style>
