<template>
  <div>
    <Breadcrumb :items="[$t('menu.game'), $t('menu.game.catchUpExp')]" />
    <a-card>
      <a-row style="margin-bottom: 16px">
        <a-col :span="4">
          <a-input-number
            v-model="condition.levelMin"
            :placeholder="$t('catchUpExpConfig.placeholder.levelMin')"
            style="width: 100%"
            allow-clear
            @press-enter="loadData()"
          />
        </a-col>
        <a-col :span="4" style="padding-left: 12px">
          <a-input-number
            v-model="condition.levelMax"
            :placeholder="$t('catchUpExpConfig.placeholder.levelMax')"
            style="width: 100%"
            allow-clear
            @press-enter="loadData()"
          />
        </a-col>
      </a-row>
      <a-row style="margin-bottom: 16px">
        <a-button type="primary" @click="loadData()">
          <template #icon><icon-search /></template>
          {{ $t('search') }}
        </a-button>
        <a-button style="margin-left: 6px" @click="resetClick()">
          <template #icon><icon-refresh /></template>
          {{ $t('reset') }}
        </a-button>
        <a-button
          type="primary"
          style="margin-left: auto"
          @click="insertClick()"
        >
          <template #icon><icon-plus /></template>
          {{ $t('add') }}
        </a-button>
      </a-row>
      <a-table
        :loading="loading"
        :data="tableData"
        :pagination="false"
        :bordered="true"
        size="medium"
        column-resizable
      >
        <template #columns>
          <a-table-column
            :title="$t('catchUpExpConfig.list.column.levelMin')"
            data-index="levelMin"
            :sortable="{ sortDirections: ['ascend', 'descend'] }"
            :width="120"
          />
          <a-table-column
            :title="$t('catchUpExpConfig.list.column.levelMax')"
            data-index="levelMax"
            :sortable="{ sortDirections: ['ascend', 'descend'] }"
            :width="120"
          />
          <a-table-column
            :title="$t('catchUpExpConfig.list.column.expMultiplier')"
            data-index="expMultiplier"
            :width="130"
          >
            <template #cell="{ record }">
              <a-tag
                :color="
                  record.expMultiplier > 1
                    ? 'green'
                    : record.expMultiplier < 1
                    ? 'red'
                    : 'blue'
                "
              >
                {{ record.expMultiplier }}x
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('catchUpExpConfig.list.column.enabled')"
            data-index="enabled"
            :width="90"
          >
            <template #cell="{ record }">
              <a-tag :color="record.enabled === 1 ? 'green' : 'red'">
                {{
                  record.enabled === 1
                    ? $t('catchUpExpConfig.enabled.true')
                    : $t('catchUpExpConfig.enabled.false')
                }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('catchUpExpConfig.list.column.createTime')"
            data-index="createTime"
            :sortable="{ sortDirections: ['ascend', 'descend'] }"
            :width="170"
          />
          <a-table-column
            :title="$t('catchUpExpConfig.list.column.updateTime')"
            data-index="updateTime"
            :sortable="{ sortDirections: ['ascend', 'descend'] }"
            :width="170"
          />
          <a-table-column
            :title="$t('catchUpExpConfig.list.column.operations')"
            data-index="operations"
            :width="160"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-button type="text" size="small" @click="editClick(record)">
                <template #icon><icon-edit /></template>
                {{ $t('edit') }}
              </a-button>
              <a-popconfirm
                type="warning"
                :content="$t('catchUpExpConfig.message.deleteTips')"
                @ok="deleteClick(record)"
              >
                <a-button type="text" size="small" status="danger">
                  <template #icon><icon-delete /></template>
                  {{ $t('delete') }}
                </a-button>
              </a-popconfirm>
            </template>
          </a-table-column>
        </template>
      </a-table>
      <a-pagination
        :current="pageNo"
        :total="total"
        :page-size="pageSize"
        :page-size-options="[10, 20, 40, 60]"
        show-total
        show-jumper
        show-page-size
        style="margin-top: 16px"
        @change="pageChange"
        @page-size-change="pageSizeChange"
      />
    </a-card>

    <a-modal
      v-model:visible="formVisible"
      :title="
        formMode === 'add'
          ? $t('catchUpExpConfig.form.title.create')
          : $t('catchUpExpConfig.form.title.update')
      "
      :width="500"
      @ok="submitForm"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        auto-label-width
      >
        <a-form-item
          field="levelMin"
          :label="$t('catchUpExpConfig.form.field.levelMin')"
        >
          <a-input-number
            v-model="formData.levelMin"
            :min="1"
            :max="250"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item
          field="levelMax"
          :label="$t('catchUpExpConfig.form.field.levelMax')"
        >
          <a-input-number
            v-model="formData.levelMax"
            :min="1"
            :max="250"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item
          field="expMultiplier"
          :label="$t('catchUpExpConfig.form.field.expMultiplier')"
        >
          <a-input-number
            v-model="formData.expMultiplier"
            :min="0.01"
            :step="0.1"
            :precision="2"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item
          field="enabled"
          :label="$t('catchUpExpConfig.form.field.enabled')"
        >
          <a-switch v-model="enabledSwitch" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import {
    CatchUpExpConfigItem,
    CatchUpExpConfigSearch,
    getCatchUpExpConfigList,
    addCatchUpExpConfig,
    updateCatchUpExpConfig,
    deleteCatchUpExpConfig,
  } from '@/api/catchUpExpConfig';
  import useLoading from '@/hooks/loading';
  import { ref, reactive } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading();

  const condition = reactive<CatchUpExpConfigSearch>({
    pageNo: 1,
    pageSize: 20,
    levelMin: undefined,
    levelMax: undefined,
  });

  const tableData = ref<CatchUpExpConfigItem[]>([]);
  const total = ref(0);
  const pageNo = ref(1);
  const pageSize = ref(20);

  const loadData = async () => {
    setLoading(true);
    try {
      condition.pageNo = pageNo.value;
      condition.pageSize = pageSize.value;
      const res = await getCatchUpExpConfigList(condition);
      tableData.value = res.data.records;
      total.value = res.data.totalRow;
    } finally {
      setLoading(false);
    }
  };

  const pageChange = (page: number) => {
    pageNo.value = page;
    loadData();
  };

  const pageSizeChange = (size: number) => {
    pageSize.value = size;
    pageNo.value = 1;
    loadData();
  };

  const resetClick = () => {
    condition.levelMin = undefined;
    condition.levelMax = undefined;
    pageNo.value = 1;
    loadData();
  };

  // ---- form ----
  const formVisible = ref(false);
  const formMode = ref<'add' | 'edit'>('add');
  const enabledSwitch = ref(true);
  const formRef = ref();

  const formData = reactive<CatchUpExpConfigItem>({
    levelMin: 1,
    levelMax: 250,
    expMultiplier: 1.0,
    enabled: 1,
  });

  const formRules = {
    levelMin: {
      required: true,
      message: t('catchUpExpConfig.form.rules.levelMin.required'),
    },
    levelMax: {
      required: true,
      message: t('catchUpExpConfig.form.rules.levelMax.required'),
    },
    expMultiplier: {
      required: true,
      message: t('catchUpExpConfig.form.rules.expMultiplier.required'),
    },
  };

  const insertClick = () => {
    formMode.value = 'add';
    formData.id = undefined;
    formData.levelMin = 1;
    formData.levelMax = 250;
    formData.expMultiplier = 1.0;
    enabledSwitch.value = true;
    formVisible.value = true;
  };

  const editClick = (record: CatchUpExpConfigItem) => {
    formMode.value = 'edit';
    formData.id = record.id;
    formData.levelMin = record.levelMin;
    formData.levelMax = record.levelMax;
    formData.expMultiplier = record.expMultiplier;
    enabledSwitch.value = record.enabled === 1;
    formVisible.value = true;
  };

  const submitForm = async () => {
    const valid = await formRef.value.validate();
    if (valid) return;
    if (formData.levelMax < formData.levelMin) {
      Message.warning(t('catchUpExpConfig.form.rules.range.invalid'));
      return;
    }
    formData.enabled = enabledSwitch.value ? 1 : 0;
    if (formMode.value === 'add') {
      await addCatchUpExpConfig(formData);
      Message.success(t('catchUpExpConfig.message.addSuccess'));
    } else {
      await updateCatchUpExpConfig(formData);
      Message.success(t('catchUpExpConfig.message.updateSuccess'));
    }
    formVisible.value = false;
    loadData();
  };

  const deleteClick = async (record: CatchUpExpConfigItem) => {
    if (record.id) {
      await deleteCatchUpExpConfig(record.id);
      Message.success(t('catchUpExpConfig.message.deleteSuccess'));
      loadData();
    }
  };

  loadData();
</script>

<script lang="ts">
  export default {
    name: 'CatchUpExpConfig',
  };
</script>

<style scoped lang="less"></style>
