<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.townConfig')">
      <a-row>
        <a-col>
          <a-space>
            <a-input-number
              v-model="condition.mapId"
              :placeholder="$t('townConfig.placeholder.mapId')"
              :min="1"
            />
            <a-input
              v-model="condition.townName"
              :placeholder="$t('townConfig.placeholder.townName')"
              :style="{ width: '180px' }"
            />
            <a-button type="primary" @click="loadData">
              {{ $t('button.search') }}
            </a-button>
            <a-button @click="resetClick">
              {{ $t('button.reset') }}
            </a-button>
            <a-button type="primary" status="success" @click="insertClick">
              {{ $t('button.create') }}
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
            title="ID"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('townConfig.list.column.mapId')"
            data-index="mapId"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('townConfig.list.column.townName')"
            data-index="townName"
            :width="150"
            align="center"
          />
          <a-table-column
            :title="$t('townConfig.list.column.enabled')"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green">
                {{ $t('townConfig.enabled.true') }}
              </a-tag>
              <a-tag v-else color="red">
                {{ $t('townConfig.enabled.false') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('townConfig.list.column.updateTime')"
            data-index="updateTime"
            :width="160"
            align="center"
          />
          <a-table-column
            :title="$t('townConfig.list.column.operations')"
            :width="120"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-button size="mini" type="text" @click="editClick(record)">
                {{ $t('button.edit') }}
              </a-button>
              <a-popconfirm
                type="error"
                :content="$t('townConfig.message.deleteTips')"
                position="left"
                @ok="deleteClick(record)"
              >
                <a-button size="mini" status="danger" type="text">
                  {{ $t('button.delete') }}
                </a-button>
              </a-popconfirm>
            </template>
          </a-table-column>
        </template>
      </a-table>
      <a-pagination
        style="margin-top: 20px"
        :total="total"
        :page-size="condition.pageSize"
        :current="condition.pageNo"
        show-total
        show-jumper
        show-page-size
        :page-size-options="[10, 20, 40, 60]"
        @change="pageChange"
        @page-size-change="pageSizeChange"
      />
    </a-card>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="formVisible"
      :title="
        formMode === 'add'
          ? $t('townConfig.form.title.create')
          : $t('townConfig.form.title.update')
      "
      @ok="submitForm"
      @cancel="formVisible = false"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules">
        <a-form-item field="mapId" :label="$t('townConfig.form.field.mapId')">
          <a-input-number
            v-model="formData.mapId"
            :min="1"
            :disabled="formMode === 'edit'"
          />
        </a-form-item>
        <a-form-item
          field="townName"
          :label="$t('townConfig.form.field.townName')"
        >
          <a-input v-model="formData.townName" />
        </a-form-item>
        <a-form-item
          field="enabled"
          :label="$t('townConfig.form.field.enabled')"
        >
          <a-switch v-model="enabledSwitch" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import useLoading from '@/hooks/loading';
  import {
    TownConfigItem,
    TownConfigSearch,
    getTownConfigList,
    addTownConfig,
    updateTownConfig,
    deleteTownConfig,
  } from '@/api/townConfig';
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<TownConfigItem[]>([]);
  const total = ref<number>(0);
  const condition = ref<TownConfigSearch>({
    mapId: undefined,
    townName: '',
    pageNo: 1,
    pageSize: 20,
  });

  const pageChange = (data: number) => {
    condition.value.pageNo = data;
    loadData();
  };
  const pageSizeChange = (data: number) => {
    condition.value.pageNo = 1;
    condition.value.pageSize = data;
    loadData();
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getTownConfigList(condition.value);
      tableData.value = data.records;
      total.value = data.totalRow;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const resetClick = () => {
    condition.value = {
      mapId: undefined,
      townName: '',
      pageNo: 1,
      pageSize: 20,
    };
    loadData();
  };

  // ---------- 新增/编辑弹窗 ----------
  const formVisible = ref(false);
  const formMode = ref<'add' | 'edit'>('add');
  const enabledSwitch = ref(true);
  const formRef = ref();
  const formData = ref<TownConfigItem>({
    mapId: 0,
    townName: '',
    enabled: 1,
  });

  const formRules = {
    mapId: [{ required: true, message: 'mapId required' }],
    townName: [{ required: true, message: 'townName required' }],
  };

  const insertClick = () => {
    formMode.value = 'add';
    formData.value = {
      mapId: 0,
      townName: '',
      enabled: 1,
    };
    enabledSwitch.value = true;
    formVisible.value = true;
  };

  const editClick = (record: TownConfigItem) => {
    formMode.value = 'edit';
    formData.value = { ...record };
    enabledSwitch.value = record.enabled === 1;
    formVisible.value = true;
  };

  const submitForm = async () => {
    try {
      await formRef.value?.validate();
    } catch (error) {
      // 验证失败，直接返回，不执行后续操作
      return;
    }

    formData.value.enabled = enabledSwitch.value ? 1 : 0;
    setLoading(true);
    try {
      if (formMode.value === 'add') {
        await addTownConfig(formData.value);
        Message.success('townConfig.message.addSuccess');
      } else {
        await updateTownConfig(formData.value);
        Message.success('townConfig.message.updateSuccess');
      }
      formVisible.value = false;
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const deleteClick = async (record: TownConfigItem) => {
    setLoading(true);
    try {
      if (record.id == null) {
        Message.error('Record ID is required for deletion');
        return;
      }
      await deleteTownConfig(record.id);
      Message.success('townConfig.message.deleteSuccess');
      await loadData();
    } finally {
      setLoading(false);
    }
  };
</script>

<script lang="ts">
  export default {
    name: 'TownConfig',
  };
</script>

<style lang="less" scoped></style>
