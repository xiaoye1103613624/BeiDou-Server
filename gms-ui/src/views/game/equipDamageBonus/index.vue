<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.equipment.equipDamageBonus')">
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
            :title="$t('equipDamageBonus.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('equipDamageBonus.column.itemPhoto')"
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
            :title="$t('equipDamageBonus.column.itemId')"
            data-index="itemId"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('equipDamageBonus.column.itemName')"
            data-index="itemName"
            :width="150"
            align="center"
          />
          <a-table-column
            :title="$t('equipDamageBonus.column.damagePct')"
            data-index="damagePct"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('equipDamageBonus.column.bossDamagePct')"
            data-index="bossDamagePct"
            :width="130"
            align="center"
          />
          <a-table-column
            :title="$t('equipDamageBonus.column.fixedDamage')"
            data-index="fixedDamage"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('equipDamageBonus.column.enabled')"
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
            :title="$t('equipDamageBonus.column.operation')"
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
                  {{ $t('equipDamageBonus.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('equipDamageBonus.delete.confirm')"
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
        <a-divider>{{ $t('equipDamageBonus.title.config') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('equipDamageBonus.column.itemId')">
              <a-input-number
                v-model="form.itemId"
                :placeholder="$t('equipDamageBonus.column.itemId')"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('equipDamageBonus.column.itemName')">
              <a-input
                v-model="form.itemName"
                :placeholder="$t('equipDamageBonus.column.itemName')"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('equipDamageBonus.column.damagePct')">
              <a-input-number
                v-model="form.damagePct"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('equipDamageBonus.column.bossDamagePct')">
              <a-input-number
                v-model="form.bossDamagePct"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('equipDamageBonus.column.fixedDamage')">
              <a-input-number
                v-model="form.fixedDamage"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('equipDamageBonus.column.enabled')">
              <a-switch v-model="enabledBool" />
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
    deleteConfig,
    getConfig,
    getConfigList,
    saveConfig,
    toggleEnabled,
  } from '@/api/equipDamageBonus';
  import type { EquipDamageBonusForm } from '@/api/equipDamageBonus';
  import { Message } from '@arco-design/web-vue';
  import { getIconUrl } from '@/utils/mapleStoryAPI';

  const { t } = useI18n();

  const { loading, setLoading } = useLoading(false);

  // 列表数据
  const tableData = ref<EquipDamageBonusForm[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getConfigList();
      tableData.value = data as unknown as EquipDamageBonusForm[];
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
      return `${t('equipDamageBonus.button.edit')} - ${form.value.itemName}`;
    }
    return t('button.create');
  });

  // 表单数据
  const form = ref<EquipDamageBonusForm>({
    itemId: undefined,
    itemName: '',
    damagePct: 0,
    bossDamagePct: 0,
    fixedDamage: 0,
    enabled: 1,
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
      damagePct: 0,
      bossDamagePct: 0,
      fixedDamage: 0,
      enabled: 1,
    };
    editingId.value = null;
  };

  // 新增配置
  const addConfigClick = () => {
    resetForm();
    modalVisible.value = true;
  };

  // 编辑配置
  const editConfigClick = async ({ id }: EquipDamageBonusForm) => {
    if (id == null) return;
    setLoading(true);
    try {
      const { data } = await getConfig(id);
      const d = data as unknown as EquipDamageBonusForm;
      form.value = {
        id: d.id,
        itemId: d.itemId,
        itemName: d.itemName || '',
        damagePct: d.damagePct || 0,
        bossDamagePct: d.bossDamagePct || 0,
        fixedDamage: d.fixedDamage || 0,
        enabled: d.enabled ?? 1,
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
      Message.success(t('equipDamageBonus.save.success'));
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
      Message.success(t('equipDamageBonus.delete.success'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // 快速切换启用/禁用状态
  const onToggleEnabled = async (record: EquipDamageBonusForm, v: boolean) => {
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
    name: 'EquipDamageBonus',
  };
</script>

<style lang="less" scoped></style>
