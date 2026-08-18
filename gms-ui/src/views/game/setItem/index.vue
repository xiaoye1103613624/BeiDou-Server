<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.setItem')">
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" @click="openCreate">{{
          $t('setItem.add')
        }}</a-button>
        <a-button @click="importWz">{{ $t('setItem.import.wz') }}</a-button>
        <a-button @click="reloadClick">{{ $t('setItem.reload') }}</a-button>
        <a-input-search
          v-model="keyword"
          :placeholder="$t('setItem.search')"
          style="width: 220px"
          allow-clear
        />
      </a-space>
      <a-table
        :loading="loading"
        :data="filteredRows"
        column-resizable
        :pagination="{ pageSize: 20 }"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column
            :title="$t('setItem.column.setId')"
            data-index="setId"
            :width="90"
          />
          <a-table-column
            :title="$t('setItem.column.setName')"
            data-index="setName"
            :width="180"
          />
          <a-table-column
            :title="$t('setItem.column.source')"
            data-index="source"
            :width="90"
          />
          <a-table-column
            :title="$t('setItem.column.tierCount')"
            data-index="tierCount"
            :width="80"
          />
          <a-table-column
            :title="$t('setItem.column.itemCount')"
            data-index="itemCount"
            :width="80"
          />
          <a-table-column :title="$t('setItem.column.enabled')" :width="100">
            <template #cell="{ record }">
              <a-switch
                v-model="record.enabled"
                :checked-value="1"
                :unchecked-value="0"
                :disabled="togglingSetId === record.setId"
                @change="() => toggleEnabled(record)"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('setItem.column.operate')"
            :width="200"
            align="center"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" size="mini" @click="openEdit(record)">
                  {{ $t('setItem.edit') }}
                </a-button>
                <a-button type="text" size="mini" @click="copyClick(record)">
                  {{ $t('setItem.copy') }}
                </a-button>
                <a-button
                  v-if="record.id"
                  type="text"
                  size="mini"
                  status="danger"
                  @click="deleteClick(record)"
                >
                  {{ $t('button.delete') }}
                </a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <SetItemDetailDrawer
      v-model:visible="drawerVisible"
      :record="editing"
      @saved="loadRows"
    />
  </div>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import {
    SetItemDetail,
    deleteSetItem,
    getSetItemMergedList,
    importSetItemFromWz,
    reloadSetItem,
    saveSetItem,
  } from '@/api/setItem';
  import SetItemDetailDrawer from './detail.vue';

  const { t } = useI18n();
  const rows = ref<SetItemDetail[]>([]);
  const keyword = ref('');
  const drawerVisible = ref(false);
  const editing = ref<SetItemDetail | null>(null);
  const togglingSetId = ref<number | null>(null);
  const { loading, setLoading } = useLoading(false);

  const filteredRows = computed(() => {
    const k = keyword.value.trim().toLowerCase();
    if (!k) return rows.value;
    return rows.value.filter(
      (r) =>
        String(r.setId).includes(k) ||
        (r.setName && r.setName.toLowerCase().includes(k))
    );
  });

  const loadRows = async () => {
    setLoading(true);
    try {
      const { data } = await getSetItemMergedList();
      rows.value = data;
    } finally {
      setLoading(false);
    }
  };

  const openCreate = () => {
    editing.value = {
      setId: 9001,
      setName: '',
      enabled: 1,
      completeCount: 0,
      itemIds: '',
    };
    drawerVisible.value = true;
  };

  const openEdit = (record: SetItemDetail) => {
    editing.value = { ...record };
    drawerVisible.value = true;
  };

  const toggleEnabled = async (record: SetItemDetail) => {
    const prev = record.enabled === 1 ? 0 : 1;
    togglingSetId.value = record.setId;
    try {
      await saveSetItem({
        id: record.id,
        setId: record.setId,
        setName: record.setName,
        completeCount: record.completeCount,
        itemIds: record.itemIds,
        enabled: record.enabled,
        sortOrder: record.sortOrder,
        remark: record.remark,
        tiersJson: record.tiersJson,
      });
      await reloadSetItem();
      Message.success(t('message.success'));
      if (!record.id) {
        await loadRows();
      }
    } catch {
      record.enabled = prev;
      Message.error(t('message.error'));
    } finally {
      togglingSetId.value = null;
    }
  };

  const copyClick = async (record: SetItemDetail) => {
    const used = new Set(rows.value.map((r) => r.setId));
    let nextId = Math.max(9000, ...used) + 1;
    while (used.has(nextId)) {
      nextId += 1;
    }
    await saveSetItem({
      setId: nextId,
      setName: `${record.setName || 'set'}_copy`,
      completeCount: record.completeCount,
      itemIds: record.itemIds,
      enabled: record.enabled ?? 1,
      sortOrder: record.sortOrder ?? 0,
      remark: record.remark,
      tiersJson: record.tiersJson,
    });
    await reloadSetItem();
    Message.success(t('setItem.copy.success'));
    await loadRows();
  };

  const deleteClick = async (record: SetItemDetail) => {
    const recordId = record.id;
    if (!recordId) return;
    Modal.confirm({
      title: t('setItem.delete.confirm'),
      onOk: async () => {
        await deleteSetItem(recordId);
        Message.success(t('message.success'));
        await loadRows();
      },
    });
  };

  const importWz = async () => {
    Modal.confirm({
      title: t('setItem.import.confirm'),
      onOk: async () => {
        await importSetItemFromWz([], 'NEW_ONLY');
        Message.success(t('setItem.import.success'));
        await loadRows();
      },
    });
  };

  const reloadClick = async () => {
    await reloadSetItem();
    Message.success(t('setItem.reload.success'));
  };

  loadRows();
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px;
  }
</style>
