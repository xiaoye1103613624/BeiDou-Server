<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('cdk.title.logs')">
      <!-- 筛选 -->
      <a-row style="margin-bottom: 16px">
        <a-col :span="24">
          <a-space wrap>
            <a-input
              v-model="filter.playerName"
              :placeholder="$t('cdk.column.playerName')"
              allow-clear
              style="width: 150px"
            />
            <a-input
              v-model="filter.code"
              :placeholder="$t('cdk.column.code')"
              allow-clear
              style="width: 180px"
            />
            <a-input
              v-model="filter.ip"
              :placeholder="$t('cdk.column.ip')"
              allow-clear
              style="width: 150px"
            />
            <a-select
              v-model="filter.result"
              :placeholder="$t('cdk.column.result')"
              allow-clear
              style="width: 130px"
            >
              <a-option :value="0">{{ $t('cdk.result.0') }}</a-option>
              <a-option :value="1">{{ $t('cdk.result.1') }}</a-option>
              <a-option :value="2">{{ $t('cdk.result.2') }}</a-option>
              <a-option :value="3">{{ $t('cdk.result.3') }}</a-option>
              <a-option :value="4">{{ $t('cdk.result.4') }}</a-option>
              <a-option :value="5">{{ $t('cdk.result.5') }}</a-option>
              <a-option :value="6">{{ $t('cdk.result.6') }}</a-option>
              <a-option :value="7">{{ $t('cdk.result.7') }}</a-option>
            </a-select>
            <a-range-picker
              v-model="filter.timeRange"
              show-time
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 360px"
            />
            <a-button type="primary" @click="loadData">{{
              $t('cdk.button.search')
            }}</a-button>
            <a-button @click="resetFilter">{{
              $t('cdk.button.reset')
            }}</a-button>
          </a-space>
        </a-col>
      </a-row>

      <!-- 日志表格 -->
      <a-table
        row-key="id"
        :loading="loading"
        :data="tableData"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
        :scroll="{ x: 1400 }"
      >
        <template #columns>
          <a-table-column
            title="ID"
            data-index="id"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('cdk.column.code')"
            data-index="code"
            :width="150"
          />
          <a-table-column
            :title="$t('cdk.column.playerName')"
            data-index="playerName"
            :width="120"
          />
          <a-table-column
            :title="$t('cdk.column.playerId')"
            data-index="playerId"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('cdk.column.accountName')"
            data-index="accountName"
            :width="120"
          />
          <a-table-column
            :title="$t('cdk.column.ip')"
            data-index="ip"
            :width="140"
          />
          <a-table-column
            :title="$t('cdk.column.result')"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="getResultColor(record.result)">
                {{ getResultText(record.result) }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('cdk.column.result') + '说明'"
            data-index="resultMsg"
            :width="180"
            ellipsis
          />
          <a-table-column
            :title="$t('cdk.column.detail')"
            data-index="detail"
            :width="200"
            ellipsis
          />
          <a-table-column
            :title="$t('cdk.column.createTime')"
            data-index="createTime"
            :width="160"
          />
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import { queryCdkLogs, type CdkLogForm } from '@/api/cdk';
  import { useI18n } from 'vue-i18n';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);
  const tableData = ref<CdkLogForm[]>([]);

  const filter = ref({
    playerName: '',
    code: '',
    ip: '',
    result: undefined as number | undefined,
    timeRange: [] as string[],
  });

  /** 结果颜色映射 */
  const getResultColor = (result: number) => {
    switch (result) {
      case 0:
        return 'green';
      case 1:
        return 'red';
      case 2:
        return 'orangered';
      case 3:
        return 'orange';
      case 4:
        return 'gray';
      case 5:
        return 'arcoblue';
      case 6:
        return 'red';
      case 7:
        return 'gray';
      default:
        return 'gray';
    }
  };

  /** 结果文本映射 */
  const getResultText = (result: number) => {
    const key = `cdk.result.${result}`;
    return t(key) || `未知(${result})`;
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await queryCdkLogs({
        playerName: filter.value.playerName || undefined,
        code: filter.value.code || undefined,
        ip: filter.value.ip || undefined,
        result: filter.value.result,
        startTime: filter.value.timeRange[0] || undefined,
        endTime: filter.value.timeRange[1] || undefined,
      });
      tableData.value = data || [];
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const resetFilter = () => {
    filter.value = {
      playerName: '',
      code: '',
      ip: '',
      result: undefined,
      timeRange: [],
    };
    loadData();
  };
</script>

<script lang="ts">
  export default { name: 'CdkLogView' };
</script>
