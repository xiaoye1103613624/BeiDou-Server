<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.family.guild')">
      <a-row>
        <a-col>
          <a-space>
            <a-input
              v-model="condition.guildName"
              :placeholder="$t('family.guild.placeholder.name')"
              :style="{ width: '180px' }"
            />
            <a-button type="primary" @click="loadData">
              {{ $t('button.search') }}
            </a-button>
            <a-button @click="resetClick">
              {{ $t('button.reset') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <a-table
        row-key="guildid"
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
            data-index="guildid"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.column.name')"
            data-index="name"
            :width="150"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.column.leader')"
            data-index="leaderName"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.column.gp')"
            data-index="gp"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.column.memberCount')"
            data-index="memberCount"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.column.capacity')"
            data-index="capacity"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.column.alliance')"
            data-index="allianceName"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.column.notice')"
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
              <a-button size="mini" type="text" @click="viewMembers(record)">
                {{ $t('family.member.view') }}
              </a-button>
              <a-button size="mini" type="text" @click="editClick(record)">
                {{ $t('button.edit') }}
              </a-button>
              <a-popconfirm
                type="error"
                :content="$t('family.guild.message.disbandTips')"
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

    <!-- 成员弹窗 -->
    <a-modal
      v-model:visible="memberVisible"
      :title="$t('family.guild.form.title.detail')"
      :footer="false"
      width="800"
    >
      <a-table
        row-key="charId"
        :data="memberData"
        :pagination="false"
        :bordered="{ cell: true }"
        size="small"
      >
        <template #columns>
          <a-table-column
            :title="$t('family.guild.member.column.name')"
            data-index="name"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.member.column.level')"
            data-index="level"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.member.column.job')"
            data-index="jobName"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.member.column.rank')"
            data-index="rankTitle"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('family.guild.member.column.online')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="record.online ? 'green' : 'red'">
                {{ record.online ? $t('family.online') : $t('family.offline') }}
              </a-tag>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-modal>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:visible="editVisible"
      :title="$t('family.guild.form.title.edit')"
      width="500"
      @ok="submitEdit"
    >
      <a-form :model="editForm">
        <a-form-item :label="$t('family.guild.form.field.name')">
          <a-input v-model="editForm.name" />
        </a-form-item>
        <a-form-item :label="$t('family.guild.form.field.notice')">
          <a-input v-model="editForm.notice" />
        </a-form-item>
        <a-form-item :label="$t('family.guild.form.field.capacity')">
          <a-input-number v-model="editForm.capacity" :min="10" :max="100" />
        </a-form-item>
        <a-form-item :label="$t('family.guild.form.field.gp')">
          <a-input-number v-model="editForm.gp" :min="0" />
        </a-form-item>
        <a-form-item :label="$t('family.guild.form.field.rank1title')">
          <a-input v-model="editForm.rank1title" />
        </a-form-item>
        <a-form-item :label="$t('family.guild.form.field.rank2title')">
          <a-input v-model="editForm.rank2title" />
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
    GuildItem,
    GuildMemberItem,
    GuildSearch,
    getGuildList,
    getGuildMembers,
    updateGuild,
    disbandGuild,
  } from '@/api/family';

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<GuildItem[]>([]);
  const total = ref(0);
  const condition = ref<GuildSearch>({
    guildName: '',
    pageNo: 1,
    pageSize: 20,
  });

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
      const { data } = await getGuildList(condition.value);
      tableData.value = data.records;
      total.value = data.totalRow;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const resetClick = () => {
    condition.value = { guildName: '', pageNo: 1, pageSize: 20 };
    loadData();
  };

  // 成员查看
  const memberVisible = ref(false);
  const memberData = ref<GuildMemberItem[]>([]);
  const viewMembers = async (record: GuildItem) => {
    setLoading(true);
    try {
      const { data } = await getGuildMembers(record.guildid);
      memberData.value = data;
      memberVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  // 编辑
  const editVisible = ref(false);
  const editForm = ref<any>({});
  const editClick = (record: GuildItem) => {
    editForm.value = { ...record };
    editVisible.value = true;
  };
  const submitEdit = async () => {
    setLoading(true);
    try {
      await updateGuild(editForm.value);
      Message.success('family.guild.message.updateSuccess');
      editVisible.value = false;
      loadData();
    } finally {
      setLoading(false);
    }
  };

  // 解散
  const disbandClick = async (record: GuildItem) => {
    setLoading(true);
    try {
      await disbandGuild(record.guildid);
      Message.success('family.guild.message.disbandSuccess');
      loadData();
    } finally {
      setLoading(false);
    }
  };
</script>

<script lang="ts">
  export default { name: 'GuildList' };
</script>
