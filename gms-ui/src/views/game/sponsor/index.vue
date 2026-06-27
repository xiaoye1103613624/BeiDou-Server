<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('sponsor.title.config')">
      <!-- 工具栏 -->
      <a-row style="margin-bottom: 16px">
        <a-col :span="24">
          <a-space>
            <a-button type="primary" status="success" @click="createClick">
              {{ $t('sponsor.button.create') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>

      <!-- 配置表格 -->
      <a-table
        row-key="id"
        :loading="loading"
        :data="tableData"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
        :scroll="{ x: 1100 }"
      >
        <template #columns>
          <a-table-column
            title="ID"
            data-index="id"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('sponsor.column.name')"
            data-index="name"
            :width="140"
            ellipsis
          />
          <a-table-column
            :title="$t('sponsor.column.amount')"
            data-index="amount"
            :width="120"
            align="right"
          />
          <a-table-column :title="$t('sponsor.column.rewards')" :width="300">
            <template #cell="{ record }">
              <a-space v-if="record.rewards && record.rewards.length > 0">
                <a-tag
                  v-for="(r, i) in record.rewards"
                  :key="i"
                  :color="
                    r.type === 'nx'
                      ? 'orangered'
                      : r.type === 'meso'
                      ? 'gold'
                      : 'arcoblue'
                  "
                >
                  {{
                    r.type === 'nx'
                      ? '点券'
                      : r.type === 'meso'
                      ? '金币'
                      : `道具#${r.id}`
                  }}×{{ r.qty }}
                </a-tag>
              </a-space>
              <span v-else>-</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.enabled')"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="record.enabled === 1 ? 'green' : 'red'">
                {{ record.enabled === 1 ? '启用' : '禁用' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.comment')"
            data-index="comment"
            :width="150"
            ellipsis
          />
          <a-table-column
            :title="$t('sponsor.column.operation')"
            :width="120"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button
                  type="text"
                  size="mini"
                  status="warning"
                  @click="editClick(record)"
                >
                  {{ $t('sponsor.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('sponsor.delete.confirm')"
                  @ok="deleteClick(record.id)"
                >
                  <a-button type="text" size="mini" status="danger">
                    {{ $t('sponsor.button.delete') }}
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 编辑/创建模态框 -->
    <SponsorForm ref="formRef" @load-data="loadData" />
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import useLoading from '@/hooks/loading';
  import {
    getSponsorConfigs,
    saveSponsorConfig,
    deleteSponsorConfig,
    type SponsorConfigForm,
  } from '@/api/sponsor';
  import SponsorForm from './form.vue';

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<SponsorConfigForm[]>([]);
  const formRef = ref();

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getSponsorConfigs();
      tableData.value = data || [];
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const createClick = () => formRef.value?.initForm(null);
  const editClick = (record: SponsorConfigForm) =>
    formRef.value?.initForm(record);

  const deleteClick = async (id: number) => {
    try {
      await deleteSponsorConfig(id);
      Message.success('已删除');
      loadData();
    } catch {
      /* interceptor handles */
    }
  };
</script>

<script lang="ts">
  export default { name: 'SponsorIndex' };
</script>
