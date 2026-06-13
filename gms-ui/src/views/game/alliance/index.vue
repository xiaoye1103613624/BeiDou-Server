<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.alliance')">
      <a-row>
        <a-col>
          <a-space>
            <a-button @click="loadData">{{ $t('button.search') }}</a-button>
          </a-space>
        </a-col>
      </a-row>

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
          <a-table-column :title="$t('alliance.column.id')" data-index="id" :width="50" align="center" />
          <a-table-column :title="$t('alliance.column.name')" data-index="name" :width="140" />
          <a-table-column :title="$t('alliance.column.guildCount')" :width="80" align="center">
            <template #cell="{ record }">
              <a-link @click="showGuilds(record)">{{ record.guildCount || 0 }}</a-link>
            </template>
          </a-table-column>
          <a-table-column :title="$t('alliance.column.capacity')" data-index="capacity" :width="70" align="center" />
          <a-table-column :title="$t('alliance.column.notice')" data-index="notice" :width="180" ellipsis tooltip />
          <a-table-column :title="$t('alliance.column.ranks')" :width="160">
            <template #cell="{ record }">
              {{ record.rank1 }} / {{ record.rank2 }} / {{ record.rank3 }}
            </template>
          </a-table-column>
          <a-table-column :title="$t('alliance.column.operation')" :width="100" fixed="right" align="center">
            <template #cell="{ record }">
              <a-popconfirm :content="$t('alliance.delete.confirm')" @ok="deleteClick(record.id)">
                <a-button type="text" size="mini" status="danger">{{ $t('button.delete') }}</a-button>
              </a-popconfirm>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 下属公会弹窗 -->
    <a-modal v-model:visible="guildVisible" :title="$t('alliance.guilds.title')" :footer="false" :width="400">
      <a-table
        row-key="guildId"
        :data="guildData"
        :pagination="false"
        :bordered="{ cell: true }"
        size="small"
      >
        <template #columns>
          <a-table-column :title="$t('alliance.guilds.id')" data-index="guildId" :width="80" align="center" />
          <a-table-column :title="$t('alliance.guilds.name')" data-index="guildName" />
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import useLoading from '@/hooks/loading';
import type { AllianceInfo, AllianceGuild } from '@/api/alliance';
import { getAllianceList, deleteAlliance } from '@/api/alliance';
import { Message } from '@arco-design/web-vue';

const { t } = useI18n();
const { loading, setLoading } = useLoading(false);
const tableData = ref<AllianceInfo[]>([]);

const loadData = async () => {
  setLoading(true);
  try {
    const { data } = await getAllianceList();
    tableData.value = (data as unknown as AllianceInfo[]) || [];
  } finally { setLoading(false); }
};
loadData();

const deleteClick = async (id: number) => {
  setLoading(true);
  try {
    await deleteAlliance(id);
    Message.success(t('alliance.delete.success'));
    await loadData();
  } finally { setLoading(false); }
};

// 下属公会弹窗
const guildVisible = ref(false);
const guildData = ref<AllianceGuild[]>([]);
const currentAllianceName = ref('');

const showGuilds = (record: AllianceInfo) => {
  currentAllianceName.value = record.name;
  guildData.value = record.guilds || [];
  guildVisible.value = true;
};
</script>

<script lang="ts">
export default { name: 'Alliance' };
</script>

<style lang="less" scoped>
:deep(.arco-card-body) { width: 100%; }
</style>
