<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.forgeRecipe')">
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
            :title="$t('forgeRecipe.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('forgeRecipe.column.resultItemPhoto')"
            align="center"
            :width="80"
          >
            <template #cell="{ record }">
              <img
                v-if="record.resultItemId"
                :src="getIconUrl('item', record.resultItemId)"
                :data-item-id="record.resultItemId"
                style="width: 32px; height: 32px"
                @error="onItemIconError"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('forgeRecipe.column.name')"
            data-index="name"
            :width="140"
            align="center"
          />
          <a-table-column
            :title="$t('forgeRecipe.column.resultItemId')"
            data-index="resultItemId"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('forgeRecipe.column.tierRequired')"
            data-index="tierRequired"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('forgeRecipe.column.expGain')"
            data-index="expGain"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('forgeRecipe.column.mesoCost')"
            data-index="mesoCost"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('forgeRecipe.column.sortOrder')"
            data-index="sortOrder"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('forgeRecipe.column.enabled')"
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
            :title="$t('forgeRecipe.column.operation')"
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
                  {{ $t('forgeRecipe.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('forgeRecipe.delete.confirm')"
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
      :width="760"
      @ok="saveClick"
      @cancel="onCancel"
    >
      <a-form :model="form" layout="vertical">
        <a-divider>{{ $t('forgeRecipe.title.basic') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('forgeRecipe.column.name')">
              <a-input
                v-model="form.name"
                :placeholder="$t('forgeRecipe.column.name')"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('forgeRecipe.column.tierRequired')">
              <a-input-number
                v-model="form.tierRequired"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('forgeRecipe.column.resultItemId')">
              <a-input-number v-model="form.resultItemId" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('forgeRecipe.column.expGain')">
              <a-input-number
                v-model="form.expGain"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('forgeRecipe.column.mesoCost')">
              <a-input-number
                v-model="form.mesoCost"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('forgeRecipe.column.sortOrder')">
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
            <a-form-item :label="$t('forgeRecipe.column.enabled')">
              <a-switch v-model="enabledBool" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider>{{ $t('forgeRecipe.title.material') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('forgeRecipe.column.material1ItemId')">
              <a-input-number
                v-model="form.material1ItemId"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('forgeRecipe.column.material1Count')">
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
            <a-form-item :label="$t('forgeRecipe.column.material2ItemId')">
              <a-input-number
                v-model="form.material2ItemId"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('forgeRecipe.column.material2Count')">
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
            <a-form-item :label="$t('forgeRecipe.column.material3ItemId')">
              <a-input-number
                v-model="form.material3ItemId"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('forgeRecipe.column.material3Count')">
              <a-input-number
                v-model="form.material3Count"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider>{{ $t('forgeRecipe.title.stats') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.strMin')">
              <a-input-number
                v-model="form.strMin"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.strMax')">
              <a-input-number
                v-model="form.strMax"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.dexMin')">
              <a-input-number
                v-model="form.dexMin"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.dexMax')">
              <a-input-number
                v-model="form.dexMax"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.intMin')">
              <a-input-number
                v-model="form.intMin"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.intMax')">
              <a-input-number
                v-model="form.intMax"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.lukMin')">
              <a-input-number
                v-model="form.lukMin"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.lukMax')">
              <a-input-number
                v-model="form.lukMax"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.watkMin')">
              <a-input-number
                v-model="form.watkMin"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.watkMax')">
              <a-input-number
                v-model="form.watkMax"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.matkMin')">
              <a-input-number
                v-model="form.matkMin"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('forgeRecipe.column.matkMax')">
              <a-input-number
                v-model="form.matkMax"
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
  } from '@/api/forgeRecipe';
  import type { ForgeRecipeForm } from '@/api/forgeRecipe';
  import { Message } from '@arco-design/web-vue';
  import { getIconUrl, onItemIconError } from '@/utils/mapleStoryAPI';

  const { t } = useI18n();

  const { loading, setLoading } = useLoading(false);

  // 列表数据
  const tableData = ref<ForgeRecipeForm[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getRecipeList();
      tableData.value = data as unknown as ForgeRecipeForm[];
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
      return `${t('forgeRecipe.button.edit')} - ${form.value.name}`;
    }
    return t('button.create');
  });

  const emptyForm = (): ForgeRecipeForm => ({
    name: '',
    tierRequired: 0,
    resultItemId: undefined,
    expGain: 0,
    mesoCost: 0,
    material1ItemId: undefined,
    material1Count: 0,
    material2ItemId: undefined,
    material2Count: 0,
    material3ItemId: undefined,
    material3Count: 0,
    strMin: 0,
    strMax: 0,
    dexMin: 0,
    dexMax: 0,
    intMin: 0,
    intMax: 0,
    lukMin: 0,
    lukMax: 0,
    watkMin: 0,
    watkMax: 0,
    matkMin: 0,
    matkMax: 0,
    sortOrder: 0,
    enabled: 1,
  });

  // 表单数据
  const form = ref<ForgeRecipeForm>(emptyForm());

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
  const editRecipeClick = async ({ id }: ForgeRecipeForm) => {
    if (id == null) return;
    setLoading(true);
    try {
      const { data } = await getRecipe(id);
      form.value = { ...emptyForm(), ...(data as unknown as ForgeRecipeForm) };
      editingId.value = form.value.id ?? null;
      modalVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  // 保存
  const saveClick = async () => {
    if (!form.value.name) {
      Message.warning('请输入配方名称');
      return;
    }
    setLoading(true);
    try {
      await saveRecipe(form.value);
      Message.success(t('forgeRecipe.save.success'));
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
      Message.success(t('forgeRecipe.delete.success'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // 快速切换启用/禁用状态
  const onToggleEnabled = async (record: ForgeRecipeForm, v: boolean) => {
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
    name: 'ForgeRecipe',
  };
</script>

<style lang="less" scoped></style>
