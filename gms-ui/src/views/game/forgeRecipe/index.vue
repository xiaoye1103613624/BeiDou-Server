<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.forgeRecipe')">
      <!-- 查询条件 -->
      <a-form layout="inline" :model="query">
        <a-form-item :label="$t('forgeRecipe.column.tierRequired')">
          <a-select
            v-model="query.tierRequired"
            placeholder="全部"
            allow-clear
            style="width: 140px"
          >
            <a-option :value="-1">全部</a-option>
            <a-option v-for="t in tierOptions" :key="t.value" :value="t.value">
              {{ t.label }}
            </a-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('forgeRecipe.column.enabled')">
          <a-select
            v-model="query.enabled"
            placeholder="全部"
            allow-clear
            style="width: 120px"
          >
            <a-option :value="-1">全部</a-option>
            <a-option :value="1">已启用</a-option>
            <a-option :value="0">已禁用</a-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('forgeRecipe.column.resultItemId')">
          <a-input-number
            v-model="query.resultItemId"
            :placeholder="$t('forgeRecipe.column.resultItemId')"
            :style="{ width: '160px' }"
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" status="success" @click="addRecipeClick">
              {{ $t('button.create') }}
            </a-button>
            <a-button @click="loadData">
              {{ $t('button.search') }}
            </a-button>
            <a-button @click="resetFilter">
              {{ $t('button.reset') }}
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <!-- 品级分组卡片 -->
      <a-collapse
        v-if="tierGroups.length"
        accordion
        :bordered="false"
        class="tier-collapse"
      >
        <a-collapse-item
          v-for="group in tierGroups"
          :key="group.tierRequired"
          :header="groupHeader(group)"
          :name="String(group.tierRequired)"
        >
          <a-table
            row-key="id"
            :loading="loading"
            :data="group.recipes"
            column-resizable
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('forgeRecipe.column.resultItemPhoto')"
                align="center"
                :width="70"
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
                :width="160"
                align="center"
                show-overflow-tooltip
              />
              <a-table-column
                :title="$t('forgeRecipe.column.resultItemId')"
                data-index="resultItemId"
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
                :width="110"
                align="center"
              />
              <a-table-column
                :title="$t('forgeRecipe.column.material1ItemId')"
                data-index="material1ItemId"
                :width="110"
                align="center"
              />
              <a-table-column
                :title="$t('forgeRecipe.column.material2ItemId')"
                data-index="material2ItemId"
                :width="110"
                align="center"
              />
              <a-table-column
                :title="$t('forgeRecipe.column.material3ItemId')"
                data-index="material3ItemId"
                :width="110"
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
        </a-collapse-item>
      </a-collapse>
      <a-empty v-else-if="!loading" description="暂无数据" />

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
                <a-input-number
                  v-model="form.resultItemId"
                  style="width: 100%"
                />
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
          <a-row v-for="n in 3" :key="n" :gutter="16">
            <a-col :span="12">
              <a-form-item :label="$t(`forgeRecipe.column.material${n}ItemId`)">
                <a-input-number
                  :model-value="getMaterial(n, 'ItemId', form)"
                  style="width: 100%"
                  @update:model-value="setMaterial(n, 'ItemId', $event, form)"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :label="$t(`forgeRecipe.column.material${n}Count`)">
                <a-input-number
                  :model-value="getMaterial(n, 'Count', form)"
                  :min="0"
                  style="width: 100%"
                  @update:model-value="setMaterial(n, 'Count', $event, form)"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <a-divider>{{ $t('forgeRecipe.title.stats') }}</a-divider>
          <a-row :gutter="16">
            <a-col v-for="stat in statFields" :key="stat.key" :span="6">
              <a-form-item :label="$t(`forgeRecipe.column.${stat.key}`)">
                <a-input-number
                  v-model="form[stat.key]"
                  :min="0"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </a-modal>
    </a-card>
  </div>
</template>

<script setup lang="ts">
  import { computed, reactive, ref } from 'vue';
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
  import { getTierList } from '@/api/alchemyTier';
  import { Message } from '@arco-design/web-vue';
  import { getIconUrl, onItemIconError } from '@/utils/mapleStoryAPI';

  const { t } = useI18n();

  const { loading, setLoading } = useLoading(false);

  const tierNames = ref<Record<number, string>>({});
  const getTierNames = async () => {
    try {
      const { data } = await getTierList(3);
      const map: Record<number, string> = {};
      (
        data as unknown as { id: number; sortOrder: number; name: string }[]
      ).forEach((d) => {
        map[d.sortOrder] = d.name;
      });
      tierNames.value = map;
    } catch {
      tierNames.value = {};
    }
  };
  getTierNames();

  const tierOptions = computed(() => {
    const names = tierNames.value;
    const def = ['入门', '普通', '职业', '大师', '宗师'];
    return def.map((name, i) => ({ value: i, label: names[i] ?? name }));
  });

  const allData = ref<ForgeRecipeForm[]>([]);
  const query = reactive<{
    tierRequired?: number;
    enabled?: number;
    resultItemId?: number;
  }>({
    tierRequired: -1,
    enabled: -1,
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getRecipeList();
      allData.value = data as unknown as ForgeRecipeForm[];
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const resetFilter = () => {
    query.tierRequired = -1;
    query.enabled = -1;
    query.resultItemId = undefined;
  };

  const filtered = computed(() =>
    allData.value.filter((r) => {
      if (
        query.tierRequired != null &&
        query.tierRequired !== -1 &&
        r.tierRequired !== query.tierRequired
      )
        return false;
      if (
        query.enabled != null &&
        query.enabled !== -1 &&
        (r.enabled ?? 1) !== query.enabled
      )
        return false;
      if (query.resultItemId != null && r.resultItemId !== query.resultItemId)
        return false;
      return true;
    })
  );

  const groupHeader = (group: {
    tierRequired: number;
    recipes: ForgeRecipeForm[];
  }) =>
    `${tierNames.value[group.tierRequired] ?? group.tierRequired}级打造 (${
      group.recipes.length
    })`;

  const tierGroups = computed(() =>
    tierNames.value
      ? tierOptions.value
          .map((o) => ({
            tierRequired: o.value,
            recipes: filtered.value.filter((r) => r.tierRequired === o.value),
          }))
          .filter((g) => g.recipes.length > 0)
      : []
  );

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

  type NumericFormKey =
    | 'strMin'
    | 'strMax'
    | 'dexMin'
    | 'dexMax'
    | 'intMin'
    | 'intMax'
    | 'lukMin'
    | 'lukMax'
    | 'watkMin'
    | 'watkMax'
    | 'matkMin'
    | 'matkMax';

  const statFields: { key: NumericFormKey }[] = [
    { key: 'strMin' },
    { key: 'strMax' },
    { key: 'dexMin' },
    { key: 'dexMax' },
    { key: 'intMin' },
    { key: 'intMax' },
    { key: 'lukMin' },
    { key: 'lukMax' },
    { key: 'watkMin' },
    { key: 'watkMax' },
    { key: 'matkMin' },
    { key: 'matkMax' },
  ];

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

  const getMaterial = (
    n: number,
    suffix: 'ItemId' | 'Count',
    f: ForgeRecipeForm
  ): number | undefined => {
    const itemIds = [f.material1ItemId, f.material2ItemId, f.material3ItemId];
    const counts = [f.material1Count, f.material2Count, f.material3Count];
    const idx = n - 1;
    return suffix === 'ItemId' ? itemIds[idx] : counts[idx];
  };

  const setMaterial = (
    n: number,
    suffix: 'ItemId' | 'Count',
    v: number | undefined,
    f: ForgeRecipeForm
  ) => {
    const itemIds = [f.material1ItemId, f.material2ItemId, f.material3ItemId];
    const counts = [f.material1Count, f.material2Count, f.material3Count];
    const idx = n - 1;
    if (suffix === 'ItemId') itemIds[idx] = v;
    else counts[idx] = v;
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

<style lang="less" scoped>
  .tier-collapse {
    margin-top: 16px;
  }
</style>
