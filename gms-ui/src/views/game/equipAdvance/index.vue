<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.equipAdvance')">
      <!-- 操作栏 -->
      <a-row>
        <a-col>
          <a-space>
            <a-button type="primary" status="success" @click="addRouteClick">
              {{ $t('button.create') }}
            </a-button>
            <a-button @click="loadData">
              {{ $t('button.search') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>

      <!-- 路线列表 -->
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
            :title="$t('equipAdvance.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('equipAdvance.column.jobGroup')"
            :width="90"
            align="center"
          >
            <template #cell="{ record }">
              {{ $t(`equipAdvance.jobGroup.${record.jobGroup}`) }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('equipAdvance.column.routeName')"
            data-index="routeName"
            :width="160"
            align="center"
          />
          <a-table-column
            :title="$t('equipAdvance.column.enabled')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green">
                {{ $t('equipAdvance.yes') }}
              </a-tag>
              <a-tag v-else color="gray">
                {{ $t('equipAdvance.no') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('equipAdvance.column.stages')"
            align="center"
            :width="100"
          >
            <template #cell="{ record }">
              <span>{{ record.stages ? record.stages.length : 0 }} 个阶段</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('equipAdvance.column.operation')"
            :width="160"
            fixed="right"
            align="center"
          >
            <template #cell="{ record }">
              <a-space :size="0">
                <a-button
                  type="text"
                  size="mini"
                  @click="editRouteClick(record)"
                >
                  {{ $t('equipAdvance.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('equipAdvance.delete.confirm')"
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
      :width="920"
      @ok="saveClick"
      @cancel="onCancel"
    >
      <a-form
        :model="form"
        layout="vertical"
        style="max-height: 65vh; overflow-y: auto"
      >
        <!-- 路线基础信息 -->
        <a-divider>{{ $t('equipAdvance.title.route') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('equipAdvance.column.jobGroup')">
              <a-select
                v-model="form.jobGroup"
                :placeholder="$t('equipAdvance.column.jobGroup')"
                style="width: 100%"
              >
                <a-option value="warrior">{{
                  $t('equipAdvance.jobGroup.warrior')
                }}</a-option>
                <a-option value="archer">{{
                  $t('equipAdvance.jobGroup.archer')
                }}</a-option>
                <a-option value="mage">{{
                  $t('equipAdvance.jobGroup.mage')
                }}</a-option>
                <a-option value="thief">{{
                  $t('equipAdvance.jobGroup.thief')
                }}</a-option>
                <a-option value="pirate">{{
                  $t('equipAdvance.jobGroup.pirate')
                }}</a-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('equipAdvance.column.routeName')">
              <a-input
                v-model="form.routeName"
                :placeholder="$t('equipAdvance.column.routeName')"
              />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('equipAdvance.column.enabled')">
              <a-switch v-model="enabledBool" />
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 阶段配置 -->
        <a-divider>
          {{ $t('equipAdvance.title.stages') }}
          <a-button
            type="primary"
            size="mini"
            status="success"
            style="margin-left: 8px"
            @click="addStage"
          >
            {{ $t('equipAdvance.button.addStage') }}
          </a-button>
        </a-divider>

        <div
          v-for="(st, si) in form.stages"
          :key="si"
          style="margin-bottom: 16px"
        >
          <a-card
            size="small"
            :title="`${getStageLabel(st.stageOrder || si)} - ${
              st.targetItemName || '未命名'
            }`"
          >
            <template #extra>
              <a-button
                type="text"
                size="mini"
                status="danger"
                @click="removeStage(si)"
              >
                {{ $t('equipAdvance.button.removeStage') }}
              </a-button>
            </template>

            <a-row :gutter="8">
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.stageOrder')">
                  <a-input-number v-model="st.stageOrder" style="width: 100%" />
                </a-form-item>
              </a-col>
              <a-col :span="5">
                <a-form-item :label="$t('equipAdvance.stage.targetItemId')">
                  <a-input-number
                    v-model="st.targetItemId"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="5">
                <a-form-item :label="$t('equipAdvance.stage.targetItemName')">
                  <a-input
                    v-model="st.targetItemName"
                    :placeholder="$t('equipAdvance.stage.targetItemName')"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="3">
                <a-form-item :label="$t('equipAdvance.stage.mesoCost')">
                  <a-input-number
                    v-model="st.mesoCost"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="3">
                <a-form-item :label="$t('equipAdvance.stage.cashCost')">
                  <a-input-number
                    v-model="st.cashCost"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.creditCost')">
                  <a-input-number
                    v-model="st.creditCost"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <!-- 属性加成 第1行 -->
            <a-row :gutter="8">
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.strAdd')">
                  <a-input-number
                    v-model="st.strAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.dexAdd')">
                  <a-input-number
                    v-model="st.dexAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.intAdd')">
                  <a-input-number
                    v-model="st.intAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.lukAdd')">
                  <a-input-number
                    v-model="st.lukAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.hpAdd')">
                  <a-input-number
                    v-model="st.hpAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.mpAdd')">
                  <a-input-number
                    v-model="st.mpAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <!-- 属性加成 第2行 -->
            <a-row :gutter="8">
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.watkAdd')">
                  <a-input-number
                    v-model="st.watkAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.matkAdd')">
                  <a-input-number
                    v-model="st.matkAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.wdefAdd')">
                  <a-input-number
                    v-model="st.wdefAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.mdefAdd')">
                  <a-input-number
                    v-model="st.mdefAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.accAdd')">
                  <a-input-number
                    v-model="st.accAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.avoidAdd')">
                  <a-input-number
                    v-model="st.avoidAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <!-- 属性加成 第3行 -->
            <a-row :gutter="8">
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.speedAdd')">
                  <a-input-number
                    v-model="st.speedAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipAdvance.stage.jumpAdd')">
                  <a-input-number
                    v-model="st.jumpAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <!-- 消耗材料 -->
            <a-divider>
              {{ $t('equipAdvance.title.costs') }}
              <a-button
                type="primary"
                size="mini"
                status="success"
                style="margin-left: 8px"
                @click="addCost(st)"
              >
                {{ $t('equipAdvance.button.addCost') }}
              </a-button>
            </a-divider>
            <div v-for="(co, ci) in st.costs" :key="ci">
              <a-space style="margin-bottom: 4px">
                <a-input-number
                  v-model="co.itemId"
                  :placeholder="$t('equipAdvance.cost.itemId')"
                  size="mini"
                  style="width: 120px"
                />
                <a-input-number
                  v-model="co.count"
                  :placeholder="$t('equipAdvance.cost.count')"
                  :min="1"
                  size="mini"
                  style="width: 80px"
                />
                <a-button
                  type="text"
                  size="mini"
                  status="danger"
                  @click="removeCost(st, ci)"
                >
                  {{ $t('equipAdvance.button.removeCost') }}
                </a-button>
              </a-space>
            </div>
          </a-card>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import {
    deleteRoute,
    getRoute,
    getRouteList,
    saveRoute,
  } from '@/api/equipAdvance';
  import type {
    EquipAdvanceForm,
    EquipAdvanceStageForm,
  } from '@/api/equipAdvance';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);

  // 列表数据
  const tableData = ref<EquipAdvanceForm[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getRouteList();
      tableData.value = data as unknown as EquipAdvanceForm[];
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
      return `${t('equipAdvance.button.edit')} - ${form.value.routeName}`;
    }
    return t('button.create');
  });

  // 表单数据
  const form = ref<EquipAdvanceForm>({
    jobGroup: 'warrior',
    routeName: '',
    enabled: 1,
    stages: [],
  });

  const enabledBool = computed({
    get: () => form.value.enabled === 1,
    set: (v: boolean) => {
      form.value.enabled = v ? 1 : 0;
    },
  });

  const resetForm = () => {
    form.value = {
      jobGroup: 'warrior',
      routeName: '',
      enabled: 1,
      stages: [],
    };
    editingId.value = null;
  };

  // 新增路线
  const addRouteClick = () => {
    resetForm();
    modalVisible.value = true;
  };

  // 编辑路线
  const editRouteClick = async ({ id }: EquipAdvanceForm) => {
    if (id == null) return;
    setLoading(true);
    try {
      const { data } = await getRoute(id);
      const d = data as unknown as EquipAdvanceForm;
      form.value = {
        id: d.id,
        jobGroup: d.jobGroup || 'warrior',
        routeName: d.routeName || '',
        enabled: d.enabled ?? 1,
        stages: d.stages || [],
      };
      editingId.value = d.id ?? null;
      modalVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  // 保存
  const saveClick = async () => {
    if (!form.value.routeName) {
      Message.warning('请输入路线名称');
      return;
    }
    setLoading(true);
    try {
      await saveRoute(form.value);
      Message.success('装备进阶路线保存成功');
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
      await deleteRoute(id);
      Message.success('装备进阶路线已删除');
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const onCancel = () => {
    modalVisible.value = false;
  };

  // 阶段标签
  const getStageLabel = (order: number) => {
    if (order === 0) return '初始装备';
    return `${order}阶`;
  };

  // 添加阶段
  const addStage = () => {
    if (!form.value.stages) form.value.stages = [];
    form.value.stages.push({
      stageOrder: form.value.stages.length,
      targetItemId: undefined,
      targetItemName: '',
      mesoCost: 0,
      cashCost: 0,
      creditCost: 0,
      strAdd: 0,
      dexAdd: 0,
      intAdd: 0,
      lukAdd: 0,
      hpAdd: 0,
      mpAdd: 0,
      watkAdd: 0,
      matkAdd: 0,
      wdefAdd: 0,
      mdefAdd: 0,
      accAdd: 0,
      avoidAdd: 0,
      speedAdd: 0,
      jumpAdd: 0,
      costs: [],
    });
  };

  const removeStage = (index: number) => {
    form.value.stages?.splice(index, 1);
  };

  const addCost = (st: EquipAdvanceStageForm) => {
    if (!st.costs) st.costs = [];
    st.costs.push({ itemId: undefined, count: 1 });
  };

  const removeCost = (st: EquipAdvanceStageForm, index: number) => {
    st.costs?.splice(index, 1);
  };
</script>

<script lang="ts">
  export default {
    name: 'EquipAdvance',
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
