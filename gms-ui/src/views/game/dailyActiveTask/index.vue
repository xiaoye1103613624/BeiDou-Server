<template>
  <div class="container">
    <Breadcrumb />
    <a-card
      class="general-card"
      :title="$t('menu.customGameplay.dailyActiveTask')"
    >
      <!-- 操作栏 -->
      <a-row>
        <a-col>
          <a-space>
            <a-button type="primary" status="success" @click="addTaskClick">
              {{ $t('button.create') }}
            </a-button>
            <a-button @click="loadData">
              {{ $t('button.search') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>

      <!-- 任务列表 -->
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
            :title="$t('dailyActiveTask.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('dailyActiveTask.column.taskKey')"
            data-index="taskKey"
            :width="160"
            align="center"
          />
          <a-table-column
            :title="$t('dailyActiveTask.column.taskName')"
            data-index="taskName"
            :width="150"
            align="center"
          />
          <a-table-column
            :title="$t('dailyActiveTask.column.targetCount')"
            data-index="targetCount"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('dailyActiveTask.column.rewardMeso')"
            data-index="rewardMeso"
            :width="110"
            align="center"
          />
          <a-table-column
            :title="$t('dailyActiveTask.column.rewardItemId')"
            data-index="rewardItemId"
            :width="110"
            align="center"
          />
          <a-table-column
            :title="$t('dailyActiveTask.column.rewardItemCount')"
            data-index="rewardItemCount"
            :width="110"
            align="center"
          />
          <a-table-column
            :title="$t('dailyActiveTask.column.sortOrder')"
            data-index="sortOrder"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('dailyActiveTask.column.enabled')"
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
            :title="$t('dailyActiveTask.column.operation')"
            :width="160"
            fixed="right"
            align="center"
          >
            <template #cell="{ record }">
              <a-space :size="0">
                <a-button
                  type="text"
                  size="mini"
                  @click="editTaskClick(record)"
                >
                  {{ $t('dailyActiveTask.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('dailyActiveTask.delete.confirm')"
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
      :width="640"
      @ok="saveClick"
      @cancel="onCancel"
    >
      <a-form :model="form" layout="vertical">
        <a-divider>{{ $t('dailyActiveTask.title.basic') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('dailyActiveTask.column.taskKey')">
              <a-input
                v-model="form.taskKey"
                :placeholder="$t('dailyActiveTask.column.taskKey')"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('dailyActiveTask.column.taskName')">
              <a-input
                v-model="form.taskName"
                :placeholder="$t('dailyActiveTask.column.taskName')"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('dailyActiveTask.column.targetCount')">
              <a-input-number
                v-model="form.targetCount"
                :min="1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('dailyActiveTask.column.sortOrder')">
              <a-input-number
                v-model="form.sortOrder"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('dailyActiveTask.column.enabled')">
              <a-switch v-model="enabledBool" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider>{{ $t('dailyActiveTask.title.reward') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('dailyActiveTask.column.rewardMeso')">
              <a-input-number
                v-model="form.rewardMeso"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('dailyActiveTask.column.rewardItemId')">
              <a-input-number
                v-model="form.rewardItemId"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('dailyActiveTask.column.rewardItemCount')">
              <a-input-number
                v-model="form.rewardItemCount"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item :label="$t('dailyActiveTask.column.extraConfig')">
              <a-textarea
                v-model="form.extraConfig"
                :placeholder="$t('dailyActiveTask.column.extraConfig')"
                :auto-size="{ minRows: 2, maxRows: 4 }"
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
    deleteTask,
    getTask,
    getTaskList,
    saveTask,
    toggleEnabled,
  } from '@/api/dailyActiveTask';
  import type { DailyActiveTaskForm } from '@/api/dailyActiveTask';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();

  const { loading, setLoading } = useLoading(false);

  // 列表数据
  const tableData = ref<DailyActiveTaskForm[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getTaskList();
      tableData.value = data as unknown as DailyActiveTaskForm[];
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
      return `${t('dailyActiveTask.button.edit')} - ${form.value.taskName}`;
    }
    return t('button.create');
  });

  const emptyForm = (): DailyActiveTaskForm => ({
    taskKey: '',
    taskName: '',
    targetCount: 1,
    rewardMeso: 0,
    rewardItemId: 0,
    rewardItemCount: 0,
    extraConfig: '',
    sortOrder: 0,
    enabled: 1,
  });

  // 表单数据
  const form = ref<DailyActiveTaskForm>(emptyForm());

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

  // 新增任务
  const addTaskClick = () => {
    resetForm();
    modalVisible.value = true;
  };

  // 编辑任务
  const editTaskClick = async ({ id }: DailyActiveTaskForm) => {
    if (id == null) return;
    setLoading(true);
    try {
      const { data } = await getTask(id);
      form.value = {
        ...emptyForm(),
        ...(data as unknown as DailyActiveTaskForm),
      };
      editingId.value = form.value.id ?? null;
      modalVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  // 保存
  const saveClick = async () => {
    if (!form.value.taskKey || !form.value.taskName) {
      Message.warning('请输入任务标识和任务名称');
      return;
    }
    setLoading(true);
    try {
      await saveTask(form.value);
      Message.success(t('dailyActiveTask.save.success'));
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
      await deleteTask(id);
      Message.success(t('dailyActiveTask.delete.success'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // 快速切换启用/禁用状态
  const onToggleEnabled = async (record: DailyActiveTaskForm, v: boolean) => {
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
    name: 'DailyActiveTask',
  };
</script>

<style lang="less" scoped></style>
