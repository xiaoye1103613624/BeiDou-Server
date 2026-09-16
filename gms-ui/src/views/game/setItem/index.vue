<template>
  <PageContainer :title="$t('menu.game.setItem')">
    <ProCard>
      <div class="bd-page-toolbar set-item-toolbar">
        <a-space>
          <a-button type="primary" @click="openCreate">{{
            $t('setItem.add')
          }}</a-button>
          <a-button @click="importWz">{{ $t('setItem.import.wz') }}</a-button>
          <a-button @click="reloadClick">{{ $t('setItem.reload') }}</a-button>
        </a-space>
        <a-input-search
          v-model="keyword"
          :placeholder="$t('setItem.search')"
          class="set-item-search"
          allow-clear
        />
      </div>
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
          <a-table-column :title="$t('setItem.column.setName')" :width="220">
            <template #cell="{ record }">
              <div class="set-name-cell">
                <div class="set-name-primary">{{ displayPrimary(record) }}</div>
                <div v-if="displaySecondary(record)" class="set-name-secondary">
                  {{ displaySecondary(record) }}
                </div>
                <div v-if="!record.setNameZh?.trim()" class="set-name-hint">
                  {{ $t('setItem.column.setNameZhMissing') }}
                </div>
              </div>
            </template>
          </a-table-column>
          <a-table-column :title="$t('setItem.column.source')" :width="110">
            <template #cell="{ record }">
              {{ formatSource(record.source) }}
            </template>
          </a-table-column>
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
    </ProCard>

    <SetItemDetailDrawer
      v-model:visible="drawerVisible"
      :record="editing"
      :all-records="rows"
      @saved="loadRows"
    />
  </PageContainer>
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

  const displayPrimary = (record: SetItemDetail) =>
    record.setNameZh?.trim() ||
    record.setName?.trim() ||
    record.setNameEn?.trim() ||
    '';

  const displaySecondary = (record: SetItemDetail) => {
    const zh = record.setNameZh?.trim();
    const en = record.setNameEn?.trim();
    if (zh && en && zh !== en) {
      return en;
    }
    if (!zh && en && record.setName?.trim() && record.setName.trim() !== en) {
      return en;
    }
    return '';
  };

  const formatSource = (source?: string) => {
    switch ((source || '').toUpperCase()) {
      case 'WZ':
        return t('setItem.source.wz');
      case 'DB':
        return t('setItem.source.db');
      case 'WZ+DB':
        return t('setItem.source.wzdb');
      case 'CUSTOM':
        return t('setItem.source.custom');
      default:
        return source || '';
    }
  };

  const filteredRows = computed(() => {
    const k = keyword.value.trim().toLowerCase();
    if (!k) return rows.value;
    return rows.value.filter(
      (r) =>
        String(r.setId).includes(k) ||
        (r.setName && r.setName.toLowerCase().includes(k)) ||
        (r.setNameZh && r.setNameZh.toLowerCase().includes(k)) ||
        (r.setNameEn && r.setNameEn.toLowerCase().includes(k))
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
      setNameZh: '',
      setNameEn: '',
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
        setNameZh: record.setNameZh,
        setNameEn: record.setNameEn,
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
    const zh = record.setNameZh?.trim();
    const en = record.setNameEn?.trim() || record.setName?.trim();
    await saveSetItem({
      setId: nextId,
      setNameZh: zh ? `${zh}_副本` : undefined,
      setNameEn: en ? `${en}_copy` : undefined,
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
      content: t('setItem.delete.confirm'),
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
      content: t('setItem.import.confirm'),
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
  .set-item-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    margin-bottom: 12px;
    flex-wrap: wrap;
  }

  .set-item-search {
    width: 240px;
  }

  .set-name-cell {
    line-height: 1.3;
  }

  .set-name-primary {
    font-weight: 500;
  }

  .set-name-secondary {
    margin-top: 2px;
    font-size: 12px;
    color: var(--color-text-3);
  }

  .set-name-hint {
    margin-top: 2px;
    font-size: 12px;
    color: rgb(var(--orange-6));
  }
</style>
