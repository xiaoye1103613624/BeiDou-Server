<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.family.alliance')">
      <a-table
        row-key="id"
        :loading="loading"
        :data="tableData"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column
            title="ID"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('family.alliance.column.name')"
            data-index="name"
            :width="150"
            align="center"
          />
          <a-table-column
            :title="$t('family.alliance.column.guildCount')"
            data-index="guildCount"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('family.alliance.column.capacity')"
            data-index="capacity"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('family.alliance.column.notice')"
            data-index="notice"
            :width="200"
            align="center"
          />
          <a-table-column
            :title="$t('operation')"
            :width="200"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-button size="mini" type="text" @click="viewDetail(record)">
                {{ $t('family.member.view') }}
              </a-button>
              <a-button size="mini" type="text" @click="editClick(record)">
                {{ $t('button.edit') }}
              </a-button>
              <a-popconfirm
                type="error"
                :content="$t('family.alliance.message.disbandTips')"
                position="left"
                @ok="disbandClick(record)"
              >
                <a-button size="mini" status="danger" type="text">
                  {{ $t('family.guild.action.disband') }}
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

    <!-- 详情弹窗 -->
    <a-modal
      v-model:visible="detailVisible"
      :title="$t('family.alliance.form.title.detail')"
      :footer="false"
      width="700"
    >
      <a-descriptions
        v-if="detailData.alliance"
        :column="2"
        bordered
        size="small"
      >
        <a-descriptions-item :label="$t('family.alliance.column.name')">{{
          detailData.alliance.name
        }}</a-descriptions-item>
        <a-descriptions-item :label="$t('family.alliance.column.capacity')">{{
          detailData.alliance.capacity
        }}</a-descriptions-item>
        <a-descriptions-item
          :label="$t('family.alliance.column.notice')"
          :span="2"
          >{{ detailData.alliance.notice }}</a-descriptions-item
        >
        <a-descriptions-item :label="$t('family.alliance.column.rank1')">{{
          detailData.alliance.rank1
        }}</a-descriptions-item>
        <a-descriptions-item :label="$t('family.alliance.column.rank2')">{{
          detailData.alliance.rank2
        }}</a-descriptions-item>
        <a-descriptions-item :label="$t('family.alliance.column.rank3')">{{
          detailData.alliance.rank3
        }}</a-descriptions-item>
        <a-descriptions-item :label="$t('family.alliance.column.rank4')">{{
          detailData.alliance.rank4
        }}</a-descriptions-item>
        <a-descriptions-item
          :label="$t('family.alliance.column.rank5')"
          :span="2"
          >{{ detailData.alliance.rank5 }}</a-descriptions-item
        >
      </a-descriptions>
      <div
        v-if="detailData.guilds && detailData.guilds.length"
        style="margin-top: 16px"
      >
        <strong>{{ $t('family.alliance.memberGuilds') }}:</strong>
        <a-table
          :data="detailData.guilds"
          :pagination="false"
          size="small"
          style="margin-top: 8px"
        >
          <template #columns>
            <a-table-column
              :title="$t('family.guild.column.name')"
              data-index="guildName"
            />
          </template>
        </a-table>
      </div>
    </a-modal>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:visible="editVisible"
      :title="$t('family.alliance.form.title.edit')"
      width="500"
      @ok="submitEdit"
    >
      <a-form :model="editForm">
        <a-form-item :label="$t('family.alliance.column.name')">
          <a-input v-model="editForm.name" />
        </a-form-item>
        <a-form-item :label="$t('family.alliance.column.notice')">
          <a-input v-model="editForm.notice" />
        </a-form-item>
        <a-form-item :label="$t('family.alliance.column.capacity')">
          <a-input-number v-model="editForm.capacity" :min="2" :max="5" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import useLoading from '@/hooks/loading';
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import {
    AllianceItem,
    getAllianceList,
    getAllianceDetail,
    updateAlliance,
    disbandAlliance,
  } from '@/api/family';

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<AllianceItem[]>([]);
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
      const { data } = await getAllianceList(condition.value);
      tableData.value = data.records;
      total.value = data.totalRow;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  // 详情
  const detailVisible = ref(false);
  const detailData = ref<any>({});
  const viewDetail = async (record: AllianceItem) => {
    setLoading(true);
    try {
      const { data } = await getAllianceDetail(record.id);
      detailData.value = data;
      detailVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  // 编辑
  const editVisible = ref(false);
  const editForm = ref<any>({});
  const editClick = (record: AllianceItem) => {
    editForm.value = { ...record };
    editVisible.value = true;
  };
  const submitEdit = async () => {
    setLoading(true);
    try {
      await updateAlliance(editForm.value);
      Message.success('family.alliance.message.updateSuccess');
      editVisible.value = false;
      loadData();
    } finally {
      setLoading(false);
    }
  };

  const disbandClick = async (record: AllianceItem) => {
    setLoading(true);
    try {
      await disbandAlliance(record.id);
      Message.success('family.alliance.message.disbandSuccess');
      loadData();
    } finally {
      setLoading(false);
    }
  };
</script>

<script lang="ts">
  export default { name: 'AllianceList' };
</script>
