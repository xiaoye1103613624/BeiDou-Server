<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.equipment.alchemyRecipe')">
      <!-- 操作栏 -->
      <a-row>
        <a-col>
          <a-space>
            <a-button type="primary" status="success" @click="addRecipeClick">
              {{ $t('button.create') }}
            </a-button>
            <a-button @click="loadData">
              {{ $t('button.search') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>

      <!-- 配方列表 -->
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
            :title="$t('alchemyRecipe.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyRecipe.column.resultItemPhoto')"
            align="center"
            :width="80"
          >
            <template #cell="{ record }">
              <img
                v-if="record.resultItemId"
                :src="getIconUrl('item', record.resultItemId)"
                style="width: 32px; height: 32px"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('alchemyRecipe.column.resultItemId')"
            data-index="resultItemId"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyRecipe.column.resultCount')"
            data-index="resultCount"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyRecipe.column.tierRequired')"
            data-index="tierRequired"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyRecipe.column.expGain')"
            data-index="expGain"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyRecipe.column.staminaCost')"
            data-index="staminaCost"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyRecipe.column.mesoCost')"
            data-index="mesoCost"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyRecipe.column.cashCost')"
            data-index="cashCost"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyRecipe.column.sortOrder')"
            data-index="sortOrder"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('alchemyRecipe.column.enabled')"
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
            :title="$t('alchemyRecipe.column.operation')"
            :width="160"
            fixed="right"
            align="center"
          >
            <template #cell="{ record }">
              <a-space :size="0">
                <a-button
                  type="text"
                  size="mini"
                  @click="editRecipeClick(record)"
                >
                  {{ $t('alchemyRecipe.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('alchemyRecipe.delete.confirm')"
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
      <a-form :model="form" layout="vertical">
        <a-divider>{{ $t('alchemyRecipe.title.basic') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('alchemyRecipe.column.tierRequired')">
              <a-input-number
                v-model="form.tierRequired"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('alchemyRecipe.column.resultItemId')">
              <a-input-number v-model="form.resultItemId" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('alchemyRecipe.column.resultCount')">
              <a-input-number
                v-model="form.resultCount"
                :min="1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('alchemyRecipe.column.expGain')">
              <a-input-number
                v-model="form.expGain"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('alchemyRecipe.column.staminaCost')">
              <a-input-number
                v-model="form.staminaCost"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('alchemyRecipe.column.sortOrder')">
              <a-input-number
                v-model="form.sortOrder"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('alchemyRecipe.column.mesoCost')">
              <a-input-number
                v-model="form.mesoCost"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('alchemyRecipe.column.cashCost')">
              <a-input-number
                v-model="form.cashCost"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('alchemyRecipe.column.enabled')">
              <a-switch v-model="enabledBool" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider>{{ $t('alchemyRecipe.title.material') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('alchemyRecipe.column.material1ItemId')">
              <a-input-number
                v-model="form.material1ItemId"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('alchemyRecipe.column.material1Count')">
              <a-input-number
                v-model="form.material1Count"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('alchemyRecipe.column.material2ItemId')">
              <a-input-number
                v-model="form.material2ItemId"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('alchemyRecipe.column.material2Count')">
              <a-input-number
                v-model="form.material2Count"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('alchemyRecipe.column.material3ItemId')">
              <a-input-number
                v-model="form.material3ItemId"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('alchemyRecipe.column.material3Count')">
              <a-input-number
                v-model="form.material3Count"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import {
    deleteRecipe,
    getRecipe,
    getRecipeList,
    saveRecipe,
    toggleEnabled,
  } from '@/api/alchemyRecipe';
  import type { AlchemyRecipeForm } from '@/api/alchemyRecipe';
  import { Message } from '@arco-design/web-vue';
  import { getIconUrl } from '@/utils/mapleStoryAPI';

  const { t } = useI18n();

  const { loading, setLoading } = useLoading(false);

  // 列表数据
  const tableData = ref<AlchemyRecipeForm[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getRecipeList();
      tableData.value = data as unknown as AlchemyRecipeForm[];
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
      return `${t('alchemyRecipe.button.edit')} - ${form.value.resultItemId}`;
    }
    return t('button.create');
  });

  const emptyForm = (): AlchemyRecipeForm => ({
    tierRequired: 0,
    resultItemId: undefined,
    resultCount: 1,
    expGain: 0,
    staminaCost: 0,
    mesoCost: 0,
    cashCost: 0,
    material1ItemId: undefined,
    material1Count: 0,
    material2ItemId: undefined,
    material2Count: 0,
    material3ItemId: undefined,
    material3Count: 0,
    sortOrder: 0,
    enabled: 1,
  });

  // 表单数据
  const form = ref<AlchemyRecipeForm>(emptyForm());

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

  // 新增配方
  const addRecipeClick = () => {
    resetForm();
    modalVisible.value = true;
  };

  // 编辑配方
  const editRecipeClick = async ({ id }: AlchemyRecipeForm) => {
    if (id == null) return;
    setLoading(true);
    try {
      const { data } = await getRecipe(id);
      form.value = {
        ...emptyForm(),
        ...(data as unknown as AlchemyRecipeForm),
      };
      editingId.value = form.value.id ?? null;
      modalVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  // 保存
  const saveClick = async () => {
    if (!form.value.resultItemId) {
      Message.warning('请输入产出物品ID');
      return;
    }
    setLoading(true);
    try {
      await saveRecipe(form.value);
      Message.success(t('alchemyRecipe.save.success'));
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
      await deleteRecipe(id);
      Message.success(t('alchemyRecipe.delete.success'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // 快速切换启用/禁用状态
  const onToggleEnabled = async (record: AlchemyRecipeForm, v: boolean) => {
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
    name: 'AlchemyRecipe',
  };
</script>

<style lang="less" scoped></style>
