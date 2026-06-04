<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.family.family')">
      <a-table
        row-key="familyId"
        :loading="loading"
        :data="tableData"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column
            title="Family ID"
            data-index="familyId"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('family.family.column.leader')"
            data-index="leaderName"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('family.family.column.memberCount')"
            data-index="memberCount"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('family.family.column.totalReputation')"
            data-index="totalReputation"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('family.family.column.precepts')"
            data-index="precepts"
            :width="200"
            align="center"
          />
          <a-table-column
            :title="$t('operation')"
            :width="120"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-button size="mini" type="text" @click="viewMembers(record)">
                {{ $t('family.member.view') }}
              </a-button>
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

    <a-modal
      v-model:visible="memberVisible"
      :title="$t('family.family.form.title.members')"
      :footer="false"
      width="800"
    >
      <a-table
        row-key="cid"
        :data="memberData"
        :pagination="false"
        :bordered="{ cell: true }"
        size="small"
      >
        <template #columns>
          <a-table-column
            :title="$t('family.member.column.name')"
            data-index="name"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('family.member.column.level')"
            data-index="level"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('family.member.column.seniorid')"
            data-index="seniorid"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('family.member.column.reputation')"
            data-index="reputation"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('family.member.column.totalreputation')"
            data-index="totalreputation"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('family.member.column.todaysrep')"
            data-index="todaysrep"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('operation')"
            :width="80"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-popconfirm
                type="error"
                :content="$t('family.family.message.removeTips')"
                position="left"
                @ok="removeMember(record)"
              >
                <a-button size="mini" status="danger" type="text">
                  {{ $t('button.delete') }}
                </a-button>
              </a-popconfirm>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import useLoading from '@/hooks/loading';
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import {
    FamilyItem,
    FamilyMemberItem,
    getFamilyList,
    getFamilyMembers,
    removeFamilyMember,
  } from '@/api/family';

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<FamilyItem[]>([]);
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
      const { data } = await getFamilyList(condition.value);
      tableData.value = data.records;
      total.value = data.totalRow;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const memberVisible = ref(false);
  const memberData = ref<FamilyMemberItem[]>([]);
  const viewMembers = async (record: FamilyItem) => {
    setLoading(true);
    try {
      const { data } = await getFamilyMembers(record.familyId);
      memberData.value = data;
      memberVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  const removeMember = async (record: FamilyMemberItem) => {
    setLoading(true);
    try {
      await removeFamilyMember(record.cid);
      Message.success('family.family.message.removeSuccess');
      loadData();
    } finally {
      setLoading(false);
    }
  };
</script>

<script lang="ts">
  export default { name: 'FamilyTree' };
</script>
