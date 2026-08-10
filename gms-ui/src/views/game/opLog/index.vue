<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.opLog')">
      <a-alert type="info" style="margin-bottom: 12px">
        {{ $t('opLog.tip.logs') }}
      </a-alert>
      <a-tabs v-model:active-key="activeTab">
        <a-tab-pane :key="1" :title="$t('opLog.tab.logs')">
          <a-row>
            <a-col>
              <a-space wrap>
                <a-select
                  v-model="search.opType"
                  :placeholder="$t('opLog.placeholder.opType')"
                  style="width: 160px"
                  allow-clear
                >
                  <a-option
                    v-for="t in typeOptions"
                    :key="t.value"
                    :value="t.value"
                    :label="t.label"
                  />
                </a-select>
                <a-input
                  v-model="search.characterName"
                  :placeholder="$t('opLog.placeholder.characterName')"
                  style="width: 140px"
                  allow-clear
                  @press-enter="onSearch"
                />
                <a-input-number
                  v-model="search.accountId"
                  :placeholder="$t('opLog.placeholder.accountId')"
                  style="width: 140px"
                  @press-enter="onSearch"
                />
                <a-input
                  v-model="search.ip"
                  :placeholder="$t('opLog.placeholder.ip')"
                  style="width: 140px"
                  allow-clear
                  @press-enter="onSearch"
                />
                <a-range-picker
                  v-model="timeRange"
                  :show-time="true"
                  :format="'YYYY-MM-DD HH:mm:ss'"
                  style="width: 320px"
                  @change="onRangeChange"
                />
                <a-button type="primary" @click="onSearch">
                  {{ $t('opLog.button.search') }}
                </a-button>
                <a-button @click="onReset">{{
                  $t('opLog.button.reset')
                }}</a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :loading="loading"
            :data="logTableData"
            column-resizable
            :pagination="pagination"
            :bordered="{ cell: true }"
            style="margin-top: 16px"
            @page-change="onPageChange"
            @page-size-change="onPageSizeChange"
          >
            <template #columns>
              <a-table-column
                :title="$t('opLog.column.createTime')"
                data-index="createTime"
                :width="170"
                align="center"
              />
              <a-table-column
                :title="$t('opLog.column.opType')"
                :width="90"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag>{{ record.opTypeName || record.opType }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('opLog.column.characterName')"
                data-index="characterName"
                :width="110"
                align="center"
              />
              <a-table-column
                :title="$t('opLog.column.accountId')"
                data-index="accountId"
                :width="90"
                align="center"
              />
              <a-table-column
                :title="$t('opLog.column.summary')"
                data-index="summary"
                align="left"
              />
              <a-table-column
                :title="$t('opLog.column.worldChannel')"
                data-index="worldChannel"
                :width="90"
                align="center"
              />
              <a-table-column
                :title="$t('opLog.column.ip')"
                data-index="ip"
                :width="130"
                align="center"
              />
              <a-table-column
                :title="$t('opLog.column.operation')"
                :width="90"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-button type="text" size="mini" @click="viewDetail(record)">
                    {{ $t('opLog.button.viewDetail') }}
                  </a-button>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane :key="2" :title="$t('opLog.tab.types')">
          <a-row>
            <a-col>
              <a-space>
                <a-button type="primary" status="success" @click="addType">
                  {{ $t('opLog.button.create') }}
                </a-button>
                <a-button type="outline" @click="onReloadTypes">
                  {{ $t('opLog.button.reload') }}
                </a-button>
              </a-space>
            </a-col>
          </a-row>
          <a-alert type="warning" style="margin: 12px 0">
            {{ $t('opLog.tip.style') }}
          </a-alert>

          <a-table
            row-key="id"
            :loading="typeLoading"
            :data="typeTableData"
            column-resizable
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('opLog.column.opTypeCode')"
                data-index="opType"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('opLog.column.name')"
                data-index="name"
                :width="110"
                align="center"
              />
              <a-table-column
                :title="$t('opLog.column.noticeTag')"
                data-index="noticeTag"
                :width="130"
                align="center"
              />
              <a-table-column
                :title="$t('opLog.column.chatType')"
                :width="150"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag color="arcoblue">
                    {{ chatStyleLabel(record.chatType) }}
                  </a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('opLog.column.broadcast')"
                :width="90"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag :color="record.broadcast ? 'green' : 'gray'">
                    {{
                      record.broadcast
                        ? $t('opLog.broadcast.yes')
                        : $t('opLog.broadcast.no')
                    }}
                  </a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('opLog.column.enabled')"
                :width="80"
                align="center"
              >
                <template #cell="{ record }">
                  <a-switch
                    :model-value="record.enabled"
                    @change="(v) => onToggleEnabled(record, v as boolean)"
                  />
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('opLog.column.sortOrder')"
                data-index="sortOrder"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('opLog.column.remark')"
                data-index="remark"
                align="left"
              />
              <a-table-column
                :title="$t('opLog.column.operation')"
                :width="150"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button type="text" size="mini" @click="editType(record)">
                      {{ $t('opLog.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('opLog.delete.confirm')"
                      @ok="removeType(record)"
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
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <a-modal
      v-model:visible="detailVisible"
      :title="$t('opLog.detail.title')"
      :width="640"
      :footer="false"
    >
      <a-descriptions v-if="currentLog" :column="2" bordered size="small">
        <a-descriptions-item :label="$t('opLog.column.opType')">
          {{ currentLog.opTypeName || currentLog.opType }}
        </a-descriptions-item>
        <a-descriptions-item :label="$t('opLog.column.createTime')">
          {{ currentLog.createTime }}
        </a-descriptions-item>
        <a-descriptions-item :label="$t('opLog.column.characterName')">
          {{ currentLog.characterName }}
        </a-descriptions-item>
        <a-descriptions-item :label="$t('opLog.column.accountId')">
          {{ currentLog.accountId }}
        </a-descriptions-item>
        <a-descriptions-item :label="$t('opLog.column.summary')" :span="2">
          {{ currentLog.summary }}
        </a-descriptions-item>
        <a-descriptions-item :label="$t('opLog.column.detail')" :span="2">
          <span style="white-space: pre-wrap">{{ currentLog.detail }}</span>
        </a-descriptions-item>
        <a-descriptions-item :label="$t('opLog.column.worldChannel')">
          {{ currentLog.worldChannel }}
        </a-descriptions-item>
        <a-descriptions-item :label="$t('opLog.column.ip')">
          {{ currentLog.ip }}
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>

    <a-modal
      v-model:visible="typeModalVisible"
      :title="
        editingType
          ? `${$t('opLog.button.edit')} - ${editingType.name}`
          : $t('opLog.button.create')
      "
      :width="560"
      @ok="saveTypeClick"
    >
      <a-form :model="typeForm" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('opLog.column.opTypeCode')">
              <a-input-number
                v-model="typeForm.opType"
                :min="0"
                style="width: 100%"
                :disabled="!!editingType"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('opLog.column.name')">
              <a-input v-model="typeForm.name" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('opLog.column.noticeTag')">
              <a-input
                v-model="typeForm.noticeTag"
                :placeholder="$t('opLog.placeholder.noticeTag')"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('opLog.column.chatType')">
              <a-select
                v-model="typeForm.chatType"
                :options="chatStyleOptions"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('opLog.column.broadcast')">
              <a-switch v-model="typeForm.broadcast" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('opLog.column.enabled')">
              <a-switch v-model="typeForm.enabled" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('opLog.column.sortOrder')">
              <a-input-number
                v-model="typeForm.sortOrder"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('opLog.column.remark')">
          <a-input v-model="typeForm.remark" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { computed, reactive, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { Message } from '@arco-design/web-vue';
  import useLoading from '@/hooks/loading';
  import {
    OpLogDO,
    OpLogSearchDTO,
    OpLogTypeDO,
    Page,
    deleteType,
    getChatStyles,
    getTypeList,
    pageOpLogs,
    reloadTypes,
    saveType,
  } from '@/api/opLog';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);

  const activeTab = ref(1);
  const logTableData = ref<OpLogDO[]>([]);
  const typeTableData = ref<OpLogTypeDO[]>([]);
  const typeLoading = ref(false);
  const chatStyles = ref<Record<number, string>>({});
  const timeRange = ref<string[]>([]);

  const detailVisible = ref(false);
  const currentLog = ref<OpLogDO | null>(null);

  const typeModalVisible = ref(false);
  const editingType = ref<OpLogTypeDO | null>(null);

  function emptyType(): OpLogTypeDO {
    return {
      opType: 1,
      name: '',
      noticeTag: '',
      chatType: 6,
      broadcast: true,
      enabled: true,
      sortOrder: 0,
      remark: '',
    };
  }

  const search = reactive<OpLogSearchDTO>({
    pageNo: 1,
    pageSize: 20,
    opType: undefined,
    characterName: '',
    accountId: undefined,
    ip: '',
    startTime: undefined,
    endTime: undefined,
  });

  const pagination = reactive({ current: 1, pageSize: 20, total: 0 });

  const typeOptions = computed(() =>
    typeTableData.value.map((t) => ({
      value: t.opType,
      label: `${t.opType} ${t.name}`,
    }))
  );

  const chatStyleOptions = computed(() =>
    Object.entries(chatStyles.value).map(([k, v]) => ({
      value: Number(k),
      label: `${k}: ${v}`,
    }))
  );

  const chatStyleLabel = (type: number) => chatStyles.value[type] || `${type}`;

  const typeForm = ref<OpLogTypeDO>(emptyType());

  const loadTypes = async () => {
    typeLoading.value = true;
    try {
      const { data } = await getTypeList();
      typeTableData.value = data as unknown as OpLogTypeDO[];
      const { data: styles } = await getChatStyles();
      chatStyles.value = styles as unknown as Record<number, string>;
    } finally {
      typeLoading.value = false;
    }
  };

  const loadLogs = async () => {
    setLoading(true);
    try {
      const body: OpLogSearchDTO = { ...search, pageNo: pagination.current };
      const { data } = await pageOpLogs(body);
      const page = data as unknown as Page<OpLogDO>;
      logTableData.value = page.records;
      pagination.total = page.totalRow;
    } finally {
      setLoading(false);
    }
  };

  const onSearch = () => {
    pagination.current = 1;
    loadLogs();
  };

  const onReset = () => {
    search.opType = undefined;
    search.characterName = '';
    search.accountId = undefined;
    search.ip = '';
    search.startTime = undefined;
    search.endTime = undefined;
    timeRange.value = [];
    pagination.current = 1;
    loadLogs();
  };

  const onRangeChange = (
    value: unknown,
    date: unknown,
    dateString: (string | undefined)[] | undefined
  ) => {
    search.startTime = dateString?.[0];
    search.endTime = dateString?.[1];
  };

  const onPageChange = (page: number) => {
    pagination.current = page;
    loadLogs();
  };

  const onPageSizeChange = (size: number) => {
    pagination.pageSize = size;
    pagination.current = 1;
    loadLogs();
  };

  const viewDetail = (record: OpLogDO) => {
    currentLog.value = record;
    detailVisible.value = true;
  };

  const addType = () => {
    editingType.value = null;
    typeForm.value = emptyType();
    typeModalVisible.value = true;
  };

  const editType = (record: OpLogTypeDO) => {
    editingType.value = { ...record };
    typeForm.value = { ...emptyType(), ...record };
    typeModalVisible.value = true;
  };

  const saveTypeClick = async () => {
    if (typeForm.value.opType == null || !typeForm.value.name) {
      Message.warning('类型码与名称不能为空');
      return;
    }
    typeLoading.value = true;
    try {
      await saveType(typeForm.value);
      Message.success(t('opLog.save.success'));
      typeModalVisible.value = false;
      await loadTypes();
    } finally {
      typeLoading.value = false;
    }
  };

  const removeType = async (record: OpLogTypeDO) => {
    if (record.id == null) return;
    try {
      await deleteType(record.id);
      Message.success(t('opLog.delete.success'));
      await loadTypes();
    } catch {
      Message.error(t('message.error'));
    }
  };

  const onToggleEnabled = async (record: OpLogTypeDO, v: boolean) => {
    try {
      await saveType({ ...record, enabled: v });
      record.enabled = v;
      Message.success(v ? t('opLog.enabled.yes') : t('opLog.enabled.no'));
    } catch {
      Message.error(t('message.error'));
    }
  };

  const onReloadTypes = async () => {
    await reloadTypes();
    Message.success(t('opLog.reload.success'));
    await loadTypes();
  };

  loadTypes();
  loadLogs();
</script>

<script lang="ts">
  export default {
    name: 'OpLog',
  };
</script>

<style scoped></style>
