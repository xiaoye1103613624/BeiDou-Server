<template>
  <div class="container">
    <Breadcrumb />
    <a-card
      class="general-card"
      :title="$t('menu.customGameplay.independentDrop')"
    >
      <a-row>
        <a-col>
          <a-space>
            <a-button type="primary" status="success" @click="addClick">
              {{ $t('independentDrop.button.add') }}
            </a-button>
            <a-button @click="loadData">
              {{ $t('independentDrop.button.refresh') }}
            </a-button>
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
            :title="$t('independentDrop.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('independentDrop.column.mobId')"
            data-index="mobId"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('independentDrop.column.mobName')"
            data-index="mobName"
            :width="200"
          />
          <a-table-column
            :title="$t('independentDrop.column.enabled')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green">{{$t('independentDrop.yes')}}</a-tag>
              <a-tag v-else color="gray">{{ $t('independentDrop.no') }}</a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('independentDrop.column.operation')"
            :width="160"
            fixed="right"
            align="center"
          >
            <template #cell="{ record }">
              <a-space :size="0">
                <a-button type="text" size="mini" @click="editClick(record)">
                  {{ $t('independentDrop.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('independentDrop.delete.confirm')"
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
    </a-card>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="modalTitle"
      :width="480"
      @ok="saveClick"
      @cancel="resetForm"
    >
      <a-form :model="form" layout="vertical">
        <a-form-item :label="$t('independentDrop.column.mobId')">
          <a-input-number
            v-model="form.mobId"
            :min="1"
            :placeholder="$t('independentDrop.placeholder.mobId')"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('independentDrop.column.mobName')">
          <a-input
            v-model="form.mobName"
            :placeholder="$t('independentDrop.placeholder.mobName')"
          />
        </a-form-item>
        <a-form-item :label="$t('independentDrop.column.enabled')">
          <a-switch v-model="enabledBool" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import Breadcrumb from '@/components/breadcrumb/index.vue';
  import type { IndependentDropConfig } from '@/api/independentDrop';
  import {
    getConfigList,
    saveConfig,
    deleteConfig,
  } from '@/api/independentDrop';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);
  const tableData = ref<IndependentDropConfig[]>([]);
  const modalVisible = ref(false);
  const editingId = ref<number | null>(null);

  const form = ref<IndependentDropConfig>({
    mobId: undefined,
    mobName: '',
    enabled: 1,
  });

  const enabledBool = computed({
    get: () => form.value.enabled === 1,
    set: (v) => {
      form.value.enabled = v ? 1 : 0;
    },
  });

  const modalTitle = computed(() =>
    editingId.value
      ? t('independentDrop.button.edit')
      : t('independentDrop.button.add')
  );

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getConfigList();
      tableData.value = data;
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    form.value = { mobId: undefined, mobName: '', enabled: 1 };
    editingId.value = null;
    modalVisible.value = false;
  };

  const addClick = () => {
    resetForm();
    modalVisible.value = true;
  };

  const editClick = (record: IndependentDropConfig) => {
    form.value = {
      id: record.id,
      mobId: record.mobId,
      mobName: record.mobName,
      enabled: record.enabled,
    };
    editingId.value = record.id ?? null;
    modalVisible.value = true;
  };

  const saveClick = async () => {
    try {
      await saveConfig({
        id: editingId.value ?? undefined,
        mobId: form.value.mobId,
        mobName: form.value.mobName,
        enabled: form.value.enabled,
      });
      Message.success(t('independentDrop.save.success'));
      resetForm();
      await loadData();
    } catch (e) {
      // 错误已在 http 拦截器中处理
    }
  };

  const deleteClick = async (id: number) => {
    try {
      await deleteConfig(id);
      Message.success(t('independentDrop.delete.success'));
      await loadData();
    } catch (e) {
      // 错误已在 http 拦截器中处理
    }
  };

  loadData();
</script>

<script lang="ts">
  export default { name: 'IndependentDrop' };
</script>

<style lang="less" scoped>
  .container {
    padding: 24px;
  }
</style>
