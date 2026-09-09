<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.drop')">
      <a-row :gutter="8" class="search-row">
        <a-col :xs="24" :sm="8" :md="4">
          <a-input-number
            v-model="condition.dropperId"
            :placeholder="$t('drop.search.mobId')"
            allow-clear
            hide-button
            style="width: 100%"
          />
        </a-col>
        <a-col :xs="24" :sm="8" :md="4">
          <a-input
            v-model="condition.dropperName"
            :placeholder="$t('drop.search.mobName')"
            allow-clear
            @keydown.enter="loadMobs"
          />
        </a-col>
        <a-col :xs="24" :sm="8" :md="4">
          <a-input-number
            v-model="condition.itemId"
            :placeholder="$t('drop.search.itemId')"
            allow-clear
            hide-button
            style="width: 100%"
            @keydown.enter="loadMobs"
          />
        </a-col>
        <a-col :xs="24" :sm="8" :md="4">
          <a-input
            v-model="condition.itemName"
            :placeholder="$t('drop.search.itemName')"
            allow-clear
            @keydown.enter="loadMobs"
          />
        </a-col>
        <a-col :xs="24" :sm="8" :md="4">
          <a-input-number
            v-model="condition.questId"
            :placeholder="$t('drop.search.questId')"
            allow-clear
            hide-button
            style="width: 100%"
            @keydown.enter="loadMobs"
          />
        </a-col>
        <a-col :xs="24" :sm="16" :md="4">
          <a-space wrap>
            <a-button type="primary" @click="loadMobs">
              {{ $t('drop.search.query') }}
            </a-button>
            <a-button @click="resetClick">{{
              $t('drop.search.reset')
            }}</a-button>
            <a-button type="primary" status="success" @click="openNewMob">
              {{ $t('drop.mob.add') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>

      <a-table
        row-key="dropperId"
        class="mob-table"
        :loading="loading"
        :data="mobRows"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
        :row-class="rowClass"
        @row-click="onRowClick"
      >
        <template #columns>
          <a-table-column
            :title="$t('drop.mob.column.icon')"
            :width="72"
            align="center"
          >
            <template #cell="{ record }">
              <img
                class="mob-icon"
                :src="getIconUrl('mob', record.dropperId)"
                alt=""
                @error="onImgError"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('drop.mob.column.id')"
            data-index="dropperId"
            :width="110"
            align="center"
          />
          <a-table-column
            :title="$t('drop.mob.column.name')"
            data-index="dropperName"
            :width="220"
          >
            <template #cell="{ record }">
              <span class="mob-name">{{ record.dropperName || '—' }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('drop.mob.column.count')"
            data-index="dropCount"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag color="arcoblue" size="small">{{
                record.dropCount ?? 0
              }}</a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('drop.mob.column.operate')"
            :width="120"
            align="center"
          >
            <template #cell="{ record }">
              <a-button type="text" size="mini" @click.stop="openMob(record)">
                {{ $t('drop.mob.manage') }}
              </a-button>
            </template>
          </a-table-column>
        </template>
      </a-table>

      <a-pagination
        style="margin-top: 16px"
        :total="total"
        :page-size="condition.pageSize"
        :current="condition.pageNo"
        show-total
        show-jumper
        show-page-size
        :page-size-options="[20, 40, 60, 100]"
        @change="pageChange"
        @page-size-change="pageSizeChange"
      />
    </a-card>

    <MobDropDrawer
      v-model:visible="drawerVisible"
      :dropper-id="editingMob?.dropperId"
      :dropper-name="editingMob?.dropperName"
      :locked-dropper="drawerLocked"
      @changed="loadMobs"
    />
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import type { TableData } from '@arco-design/web-vue';
  import useLoading from '@/hooks/loading';
  import { DropConditionState, DropMobState, getDropMobList } from '@/api/drop';
  import { getIconUrl } from '@/utils/mapleStoryAPI';
  import MobDropDrawer from './MobDropDrawer.vue';

  const { setLoading, loading } = useLoading(false);
  const condition = ref<DropConditionState>({
    dropperId: undefined,
    dropperName: undefined,
    itemId: undefined,
    itemName: undefined,
    questId: undefined,
    pageNo: 1,
    pageSize: 20,
  });
  const total = ref(0);
  const mobRows = ref<DropMobState[]>([]);
  const drawerVisible = ref(false);
  const drawerLocked = ref(true);
  const editingMob = ref<DropMobState | null>(null);

  const loadMobs = async () => {
    setLoading(true);
    try {
      const { data } = await getDropMobList(condition.value);
      mobRows.value = data.records || [];
      total.value = data.totalRow || 0;
    } finally {
      setLoading(false);
    }
  };
  loadMobs();

  const pageChange = (page: number) => {
    condition.value.pageNo = page;
    loadMobs();
  };

  const pageSizeChange = (size: number) => {
    condition.value.pageNo = 1;
    condition.value.pageSize = size;
    loadMobs();
  };

  const resetClick = () => {
    condition.value = {
      ...condition.value,
      dropperId: undefined,
      dropperName: undefined,
      itemId: undefined,
      itemName: undefined,
      questId: undefined,
      pageNo: 1,
    };
    loadMobs();
  };

  const openMob = (record: DropMobState) => {
    editingMob.value = record;
    drawerLocked.value = true;
    drawerVisible.value = true;
  };

  const openNewMob = () => {
    editingMob.value = { dropperId: undefined, dropperName: undefined };
    drawerLocked.value = false;
    drawerVisible.value = true;
  };

  const onRowClick = (record: TableData) => {
    openMob(record as DropMobState);
  };

  const rowClass = () => 'mob-row-clickable';

  const onImgError = (e: Event) => {
    const img = e.target as HTMLImageElement;
    img.style.visibility = 'hidden';
  };
</script>

<script lang="ts">
  export default {
    name: 'Drop',
  };
</script>

<style lang="less" scoped>
  .search-row {
    margin-bottom: 16px;
  }
  .search-row :deep(.arco-col) {
    margin-bottom: 8px;
  }
  .mob-icon {
    width: 40px;
    height: 40px;
    object-fit: contain;
    vertical-align: middle;
  }
  .mob-name {
    font-weight: 500;
  }
  :deep(.mob-row-clickable) {
    cursor: pointer;
  }
  :deep(.mob-row-clickable:hover) td {
    background: var(--color-fill-1);
  }
</style>
