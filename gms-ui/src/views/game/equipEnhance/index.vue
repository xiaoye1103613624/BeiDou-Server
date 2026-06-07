<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.equipEnhance')">
      <!-- 操作栏 -->
      <a-row>
        <a-col>
          <a-space>
            <a-button type="primary" status="success" @click="addConfigClick">
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
            :title="$t('equipEnhance.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('equipEnhance.column.itemPhoto')"
            align="center"
            :width="80"
          >
            <template #cell="{ record }">
              <img
                v-if="record.itemId"
                :src="getIconUrl('item', record.itemId)"
                style="width: 32px; height: 32px"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('equipEnhance.column.itemId')"
            data-index="itemId"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('equipEnhance.column.itemName')"
            data-index="itemName"
            :width="150"
            align="center"
          />
          <a-table-column
            :title="$t('equipEnhance.column.maxEnhance')"
            data-index="maxEnhance"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('equipEnhance.column.uniquePerChar')"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.uniquePerChar === 1" color="red">
                {{ $t('equipEnhance.yes') }}
              </a-tag>
              <a-tag v-else color="blue">
                {{ $t('equipEnhance.no') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('equipEnhance.column.enabled')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green">
                {{ $t('equipEnhance.yes') }}
              </a-tag>
              <a-tag v-else color="gray">
                {{ $t('equipEnhance.no') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('equipEnhance.column.levels')"
            align="center"
            :width="100"
          >
            <template #cell="{ record }">
              <span>{{ record.levels ? record.levels.length : 0 }} 个等级</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('equipEnhance.column.operation')"
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
                  {{ $t('equipEnhance.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('equipEnhance.delete.confirm')"
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
      :width="900"
      @ok="saveClick"
      @cancel="onCancel"
    >
      <a-form
        :model="form"
        layout="vertical"
        style="max-height: 65vh; overflow-y: auto"
      >
        <!-- 基础信息 -->
        <a-divider>{{ $t('equipEnhance.title.config') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="6">
            <a-form-item :label="$t('equipEnhance.column.itemId')">
              <a-input-number
                v-model="form.itemId"
                :placeholder="$t('equipEnhance.column.itemId')"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('equipEnhance.column.itemName')">
              <a-input
                v-model="form.itemName"
                :placeholder="$t('equipEnhance.column.itemName')"
              />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('equipEnhance.column.maxEnhance')">
              <a-input-number
                v-model="form.maxEnhance"
                :min="1"
                :max="30"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="3">
            <a-form-item :label="$t('equipEnhance.column.uniquePerChar')">
              <a-switch v-model="uniquePerCharBool" />
            </a-form-item>
          </a-col>
          <a-col :span="3">
            <a-form-item :label="$t('equipEnhance.column.enabled')">
              <a-switch v-model="enabledBool" />
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 等级配置 -->
        <a-divider>
          {{ $t('equipEnhance.title.levels') }}
          <a-button
            type="primary"
            size="mini"
            status="success"
            style="margin-left: 8px"
            @click="addLevel"
          >
            {{ $t('equipEnhance.button.addLevel') }}
          </a-button>
        </a-divider>

        <div
          v-for="(lv, li) in form.levels"
          :key="li"
          style="margin-bottom: 16px"
        >
          <a-card size="small" :title="`Lv.${lv.enhanceLevel || li + 1}`">
            <template #extra>
              <a-button
                type="text"
                size="mini"
                status="danger"
                @click="removeLevel(li)"
              >
                {{ $t('equipEnhance.button.removeLevel') }}
              </a-button>
            </template>

            <a-row :gutter="8">
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.enhanceLevel')">
                  <a-input-number
                    v-model="lv.enhanceLevel"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.successRate')">
                  <a-input-number
                    v-model="lv.successRate"
                    :min="0"
                    :max="100"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.destroyOnFail')">
                  <a-switch
                    :model-value="lv.destroyOnFail === 1"
                    @change="(v: boolean) => onDestroyChange(lv, v)"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="5">
                <a-form-item :label="$t('equipEnhance.level.mesoCost')">
                  <a-input-number
                    v-model="lv.mesoCost"
                    :min="0"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <!-- 属性加成 第1行 -->
            <a-row :gutter="8">
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.strAdd')">
                  <a-input-number
                    v-model="lv.strAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.dexAdd')">
                  <a-input-number
                    v-model="lv.dexAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.intAdd')">
                  <a-input-number
                    v-model="lv.intAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.lukAdd')">
                  <a-input-number
                    v-model="lv.lukAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.hpAdd')">
                  <a-input-number
                    v-model="lv.hpAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.mpAdd')">
                  <a-input-number
                    v-model="lv.mpAdd"
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
                <a-form-item :label="$t('equipEnhance.level.watkAdd')">
                  <a-input-number
                    v-model="lv.watkAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.matkAdd')">
                  <a-input-number
                    v-model="lv.matkAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.wdefAdd')">
                  <a-input-number
                    v-model="lv.wdefAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.mdefAdd')">
                  <a-input-number
                    v-model="lv.mdefAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.accAdd')">
                  <a-input-number
                    v-model="lv.accAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.avoidAdd')">
                  <a-input-number
                    v-model="lv.avoidAdd"
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
                <a-form-item :label="$t('equipEnhance.level.speedAdd')">
                  <a-input-number
                    v-model="lv.speedAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="4">
                <a-form-item :label="$t('equipEnhance.level.jumpAdd')">
                  <a-input-number
                    v-model="lv.jumpAdd"
                    :min="0"
                    style="width: 100%"
                    size="mini"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <!-- 消耗材料 -->
            <a-divider>
              {{ $t('equipEnhance.title.costs') }}
              <a-button
                type="primary"
                size="mini"
                status="success"
                style="margin-left: 8px"
                @click="addCost(lv)"
              >
                {{ $t('equipEnhance.button.addCost') }}
              </a-button>
            </a-divider>
            <div v-for="(co, ci) in lv.costs" :key="ci">
              <a-space style="margin-bottom: 4px">
                <a-input-number
                  v-model="co.itemId"
                  :placeholder="$t('equipEnhance.cost.itemId')"
                  size="mini"
                  style="width: 120px"
                />
                <a-input-number
                  v-model="co.count"
                  :placeholder="$t('equipEnhance.cost.count')"
                  :min="1"
                  size="mini"
                  style="width: 80px"
                />
                <a-button
                  type="text"
                  size="mini"
                  status="danger"
                  @click="removeCost(lv, ci)"
                >
                  {{ $t('equipEnhance.button.removeCost') }}
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
    deleteConfig,
    getConfig,
    getConfigList,
    saveConfig,
  } from '@/api/equipEnhance';
  import type {
    EquipEnhanceForm,
    EquipEnhanceLevelForm,
  } from '@/api/equipEnhance';
  import { Message } from '@arco-design/web-vue';
  import { getIconUrl } from '@/utils/mapleStoryAPI';

  const { t } = useI18n();

  const { loading, setLoading } = useLoading(false);

  // 列表数据
  const tableData = ref<EquipEnhanceForm[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getConfigList();
      tableData.value = data as unknown as EquipEnhanceForm[];
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
      return `${t('equipEnhance.button.edit')} - ${form.value.itemName}`;
    }
    return t('button.create');
  });

  // 表单数据
  const form = ref<EquipEnhanceForm>({
    itemId: undefined,
    itemName: '',
    uniquePerChar: 0,
    maxEnhance: 10,
    enabled: 1,
    levels: [],
  });

  const uniquePerCharBool = computed({
    get: () => form.value.uniquePerChar === 1,
    set: (v: boolean) => {
      form.value.uniquePerChar = v ? 1 : 0;
    },
  });
  const enabledBool = computed({
    get: () => form.value.enabled === 1,
    set: (v: boolean) => {
      form.value.enabled = v ? 1 : 0;
    },
  });

  const resetForm = () => {
    form.value = {
      itemId: undefined,
      itemName: '',
      uniquePerChar: 0,
      maxEnhance: 10,
      enabled: 1,
      levels: [],
    };
    editingId.value = null;
  };

  // 新增配置
  const addConfigClick = () => {
    resetForm();
    modalVisible.value = true;
  };

  // 编辑配置
  const editConfigClick = async ({ id }: EquipEnhanceForm) => {
    if (id == null) return;
    setLoading(true);
    try {
      const { data } = await getConfig(id);
      const d = data as unknown as EquipEnhanceForm;
      form.value = {
        id: d.id,
        itemId: d.itemId,
        itemName: d.itemName || '',
        uniquePerChar: d.uniquePerChar || 0,
        maxEnhance: d.maxEnhance || 10,
        enabled: d.enabled ?? 1,
        levels: d.levels || [],
      };
      editingId.value = d.id ?? null;
      modalVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  // 保存
  const saveClick = async () => {
    if (!form.value.itemId) {
      Message.warning('请输入物品ID');
      return;
    }
    setLoading(true);
    try {
      await saveConfig(form.value);
      Message.success('装备强化配置保存成功');
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
      await deleteConfig(id);
      Message.success('装备强化配置已删除');
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // 取消弹窗
  const onCancel = () => {
    modalVisible.value = false;
  };

  // 失败销毁开关变更
  const onDestroyChange = (lv: EquipEnhanceLevelForm, v: boolean) => {
    lv.destroyOnFail = v ? 1 : 0;
  };

  // 添加等级
  const addLevel = () => {
    if (!form.value.levels) form.value.levels = [];
    form.value.levels.push({
      enhanceLevel: form.value.levels.length + 1,
      successRate: 100,
      destroyOnFail: 0,
      mesoCost: 0,
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

  // 删除等级
  const removeLevel = (index: number) => {
    form.value.levels?.splice(index, 1);
  };

  // 添加消耗材料
  const addCost = (lv: EquipEnhanceLevelForm) => {
    if (!lv.costs) lv.costs = [];
    lv.costs.push({ itemId: undefined, count: 1 });
  };

  // 删除消耗材料
  const removeCost = (lv: EquipEnhanceLevelForm, index: number) => {
    lv.costs?.splice(index, 1);
  };
</script>

<script lang="ts">
  export default {
    name: 'EquipEnhance',
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
