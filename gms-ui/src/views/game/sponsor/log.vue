<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('sponsor.title.logs')">
      <!-- 筛选 -->
      <a-row style="margin-bottom: 16px">
        <a-col :span="24">
          <a-space wrap>
            <a-input
              v-model="filter.playerName"
              :placeholder="$t('sponsor.column.playerName')"
              allow-clear
              style="width: 160px"
            />
            <a-select
              v-model="filter.type"
              :placeholder="$t('sponsor.column.type')"
              allow-clear
              style="width: 140px"
            >
              <a-option :value="1">{{ $t('sponsor.type.1') }}</a-option>
              <a-option :value="2">{{ $t('sponsor.type.2') }}</a-option>
            </a-select>
            <a-range-picker
              v-model="filter.timeRange"
              show-time
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 360px"
            />
            <a-button type="primary" @click="loadData">搜索</a-button>
            <a-button @click="resetFilter">重置</a-button>
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
        :scroll="{ x: 1000 }"
      >
        <template #columns>
          <a-table-column title="ID" data-index="id" :width="70" align="center" />
          <a-table-column :title="$t('sponsor.column.playerName')" data-index="playerName" :width="120" />
          <a-table-column :title="$t('sponsor.column.type')" :width="100" align="center">
            <template #cell="{ record }">
              <a-tag :color="record.type === 1 ? 'green' : 'blue'">
                {{ record.type === 1 ? $t('sponsor.type.1') : $t('sponsor.type.2') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column :title="$t('sponsor.column.amount2')" data-index="amount" :width="100" align="right" />
          <a-table-column :title="$t('sponsor.column.detail')" data-index="detail" :width="200" ellipsis />
          <a-table-column :title="$t('sponsor.column.createTime')" data-index="createTime" :width="160" />
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import { getSponsorLogs, type SponsorLog } from '@/api/sponsor';

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<SponsorLog[]>([]);

  const filter = ref({
    playerName: '',
    type: undefined as number | undefined,
    timeRange: [] as string[],
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getSponsorLogs({
        playerName: filter.value.playerName || undefined,
        type: filter.value.type,
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
    filter.value = { playerName: '', type: undefined, timeRange: [] };
    loadData();
  };
</script>

<script lang="ts">
  export default { name: 'SponsorLog' };
</script>
