<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.guild')">
      <a-row>
        <a-col>
          <a-space>
            <a-button @click="loadData">{{ $t('button.search') }}</a-button>
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
            :title="$t('guild.column.id')"
            data-index="guildid"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('guild.column.name')"
            data-index="name"
            :width="130"
          />
          <a-table-column
            :title="$t('guild.column.leader')"
            data-index="leaderName"
            :width="110"
          />
          <a-table-column
            :title="$t('guild.column.gp')"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">{{
              (record.gp || 0).toLocaleString()
            }}</template>
          </a-table-column>
          <a-table-column
            :title="$t('guild.column.members')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <a-link @click="showMembers(record)">{{
                record.memberCount || 0
              }}</a-link>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('guild.column.capacity')"
            data-index="capacity"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('guild.column.alliance')"
            data-index="allianceName"
            :width="130"
          />
          <a-table-column
            :title="$t('guild.column.notice')"
            data-index="notice"
            :width="180"
            ellipsis
            tooltip
          />
          <a-table-column
            :title="$t('guild.column.operation')"
            :width="100"
            fixed="right"
            align="center"
          >
            <template #cell="{ record }">
              <a-popconfirm
                :content="$t('guild.delete.confirm')"
                @ok="deleteClick(record.guildid)"
              >
                <a-button type="text" size="mini" status="danger">{{
                  $t('button.delete')
                }}</a-button>
              </a-popconfirm>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 成员列表弹窗 -->
    <a-modal
      v-model:visible="memberVisible"
      :title="$t('guild.members.title')"
      :footer="false"
      :width="550"
    >
      <a-table
        row-key="id"
        :data="memberData"
        :pagination="false"
        :bordered="{ cell: true }"
        size="small"
      >
        <template #columns>
          <a-table-column
            :title="$t('guild.members.id')"
            data-index="id"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('guild.members.name')"
            data-index="name"
            :width="120"
          />
          <a-table-column
            :title="$t('guild.members.level')"
            data-index="level"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('guild.members.rank')"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="rankColor(record.guildrank)">{{
                rankName(record.guildrank)
              }}</a-tag>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import type { GuildInfo, GuildMember } from '@/api/guild';
  import { deleteGuild, getGuildList, getGuildMembers } from '@/api/guild';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);
  const tableData = ref<GuildInfo[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getGuildList();
      tableData.value = (data as unknown as GuildInfo[]) || [];
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const deleteClick = async (id: number) => {
    setLoading(true);
    try {
      await deleteGuild(id);
      Message.success(t('guild.delete.success'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // 成员弹窗
  const memberVisible = ref(false);
  const memberData = ref<GuildMember[]>([]);
  const currentGuildName = ref('');

  const showMembers = async (record: GuildInfo) => {
    currentGuildName.value = record.name;
    try {
      const { data } = await getGuildMembers(record.guildid);
      memberData.value = (data as unknown as GuildMember[]) || [];
      memberVisible.value = true;
    } catch {
      /* ignore */
    }
  };

  const rankName = (rank: number) => {
    const map: Record<number, string> = {
      1: t('guild.rank.master'),
      2: t('guild.rank.jrmaster'),
      3: t('guild.rank.member3'),
      4: t('guild.rank.member4'),
      5: t('guild.rank.member5'),
    };
    return map[rank] || `Rank${rank}`;
  };
  const rankColor = (rank: number) => {
    const map: Record<number, string> = {
      1: 'red',
      2: 'orangered',
      3: 'blue',
      4: 'green',
      5: 'gray',
    };
    return map[rank] || 'gray';
  };
</script>

<script lang="ts">
  export default { name: 'Guild' };
</script>

<style lang="less" scoped>
  :deep(.arco-card-body) {
    width: 100%;
  }
</style>
