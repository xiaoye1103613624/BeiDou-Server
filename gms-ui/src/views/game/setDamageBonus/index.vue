<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.equipment.setDamageBonus')">
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
            :title="$t('setDamageBonus.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('setDamageBonus.column.setItemId')"
            data-index="setItemId"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('setDamageBonus.column.setName')"
            data-index="setName"
            :width="150"
            align="center"
          />
          <a-table-column
            :title="$t('setDamageBonus.column.tierCount')"
            data-index="tierCount"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('setDamageBonus.column.damagePct')"
            data-index="damagePct"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('setDamageBonus.column.bossDamagePct')"
            data-index="bossDamagePct"
            :width="130"
            align="center"
          />
          <a-table-column
            :title="$t('setDamageBonus.column.enabled')"
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
            :title="$t('setDamageBonus.column.operation')"
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
                  {{ $t('setDamageBonus.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('setDamageBonus.delete.confirm')"
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
        <a-divider>{{ $t('setDamageBonus.title.config') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('setDamageBonus.column.setItemId')">
              <a-input-number
                v-model="form.setItemId"
                :placeholder="$t('setDamageBonus.column.setItemId')"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('setDamageBonus.column.setName')">
              <a-input
                v-model="form.setName"
                :placeholder="$t('setDamageBonus.column.setName')"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('setDamageBonus.column.tierCount')">
              <a-input-number
                v-model="form.tierCount"
                :min="1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('setDamageBonus.column.damagePct')">
              <a-input-number
                v-model="form.damagePct"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('setDamageBonus.column.bossDamagePct')">
              <a-input-number
                v-model="form.bossDamagePct"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('setDamageBonus.column.enabled')">
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
  } from '@/api/setDamageBonus';
  import type { SetDamageBonusForm } from '@/api/setDamageBonus';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();

  const { loading, setLoading } = useLoading(false);

  // 列表数据
  const tableData = ref<SetDamageBonusForm[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getConfigList();
      tableData.value = data as unknown as SetDamageBonusForm[];
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
      return `${t('setDamageBonus.button.edit')} - ${form.value.setName}`;
    }
    return t('button.create');
  });

  // 表单数据
  const form = ref<SetDamageBonusForm>({
    setItemId: undefined,
    setName: '',
    tierCount: undefined,
    damagePct: 0,
    bossDamagePct: 0,
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
      setItemId: undefined,
      setName: '',
      tierCount: undefined,
      damagePct: 0,
      bossDamagePct: 0,
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
  const editConfigClick = async ({ id }: SetDamageBonusForm) => {
    if (id == null) return;
    setLoading(true);
    try {
      const { data } = await getConfig(id);
      const d = data as unknown as SetDamageBonusForm;
      form.value = {
        id: d.id,
        setItemId: d.setItemId,
        setName: d.setName || '',
        tierCount: d.tierCount,
        damagePct: d.damagePct || 0,
        bossDamagePct: d.bossDamagePct || 0,
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
    if (!form.value.setItemId) {
      Message.warning('请输入套装ID');
      return;
    }
    if (!form.value.tierCount) {
      Message.warning('请输入生效件数档位');
      return;
    }
    setLoading(true);
    try {
      await saveConfig(form.value);
      Message.success(t('setDamageBonus.save.success'));
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
      Message.success(t('setDamageBonus.delete.success'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // 快速切换启用/禁用状态
  const onToggleEnabled = async (record: SetDamageBonusForm, v: boolean) => {
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
    name: 'SetDamageBonus',
  };
</script>

<style lang="less" scoped></style>
