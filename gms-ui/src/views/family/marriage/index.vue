<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.family.marriage')">
      <a-table
        row-key="marriageid"
        :loading="loading"
        :data="tableData"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column
            title="ID"
            data-index="marriageid"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('family.marriage.column.husband')"
            data-index="husbandName"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('family.marriage.column.husbandId')"
            data-index="husbandid"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('family.marriage.column.wife')"
            data-index="wifeName"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('family.marriage.column.wifeId')"
            data-index="wifeid"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('operation')"
            :width="100"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-popconfirm
                type="error"
                :content="$t('family.marriage.message.dissolveTips')"
                position="left"
                @ok="dissolveClick(record)"
              >
                <a-button size="mini" status="danger" type="text">
                  {{ $t('family.marriage.action.dissolve') }}
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
        :page-size-options="[10, 20, 40]"
        @change="pageChange"
        @page-size-change="pageSizeChange"
      />
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import useLoading from '@/hooks/loading';
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import {
    MarriageItem,
    getMarriageList,
    dissolveMarriage,
  } from '@/api/family';

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<MarriageItem[]>([]);
  const total = ref(0);
  const condition = ref({ pageNo: 1, pageSize: 20 });

  const pageChange = (d: number) => {
    condition.value.pageNo = d;
    loadData();
  };
  const pageSizeChange = (d: number) => {
    condition.value.pageNo = 1;
    condition.value.pageSize = d;
    loadData();
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getMarriageList(condition.value);
      tableData.value = data.records;
      total.value = data.totalRow;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const dissolveClick = async (record: MarriageItem) => {
    setLoading(true);
    try {
      await dissolveMarriage(record.marriageid);
      Message.success('family.marriage.message.dissolveSuccess');
      loadData();
    } finally {
      setLoading(false);
    }
  };
</script>

<script lang="ts">
  export default { name: 'MarriageList' };
</script>
