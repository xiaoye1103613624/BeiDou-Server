<template>
  <a-drawer
    :visible="visible"
    :width="920"
    unmount-on-close
    :footer="false"
    class="drop-drawer"
    @cancel="emit('update:visible', false)"
  >
    <template #title>
      <div class="drawer-title">
        <img
          v-if="dropperId"
          class="mob-icon"
          :src="getIconUrl('mob', dropperId)"
          alt=""
          @error="onImgError"
        />
        <div class="drawer-title-text">
          <div class="drawer-title-main">
            {{ dropperName || $t('drop.drawer.title') }}
            <span v-if="dropperId" class="drawer-title-id"
              >#{{ dropperId }}</span
            >
          </div>
          <div class="drawer-title-sub">
            {{ $t('drop.mob.column.count') }}: {{ dropRows.length }}
          </div>
        </div>
      </div>
    </template>

    <div v-if="!lockedDropper" class="drawer-mob-pick">
      <a-input-number
        v-model="localDropperId"
        :placeholder="$t('drop.search.mobId')"
        :min="1"
        style="width: 160px"
      />
      <a-button type="primary" :loading="loading" @click="confirmMob">
        {{ $t('drop.search.query') }}
      </a-button>
    </div>

    <a-space style="margin-bottom: 12px">
      <a-button
        type="primary"
        status="success"
        :disabled="!activeDropperId"
        @click="insertClick"
      >
        {{ $t('drop.drawer.add') }}
      </a-button>
      <a-button
        :disabled="!activeDropperId"
        :loading="loading"
        @click="loadDrops"
      >
        {{ $t('drop.drawer.refresh') }}
      </a-button>
      <a-typography-text type="secondary">
        {{ $t('drop.column.chanceHint') }}
      </a-typography-text>
    </a-space>

    <a-empty
      v-if="!loading && activeDropperId && dropRows.length === 0"
      :description="$t('drop.drawer.empty')"
    />

    <a-table
      v-else
      row-key="rowKey"
      :loading="loading"
      :data="dropRows"
      column-resizable
      :pagination="false"
      :bordered="{ cell: true }"
      :scroll="{ x: 860, y: 'calc(100vh - 220px)' }"
    >
      <template #columns>
        <a-table-column
          :title="$t('drop.column.itemId')"
          :width="120"
          align="center"
        >
          <template #cell="{ record }">
            <a-input-number
              v-if="editKey === record.rowKey"
              v-model="record.itemId"
              :min="0"
            />
            <span v-else>{{ record.itemId }}</span>
          </template>
        </a-table-column>
        <a-table-column
          :title="$t('drop.column.item')"
          :width="200"
          align="left"
        >
          <template #cell="{ record }">
            <div class="item-cell">
              <img
                v-if="record.itemId"
                class="item-icon"
                :src="getIconUrl('item', record.itemId)"
                alt=""
                @error="onImgError"
              />
              <a-tag v-if="record.itemId === 0" color="orangered" size="small">
                {{ $t('drop.item.meso') }}
              </a-tag>
              <span v-else>{{ record.itemName || '—' }}</span>
            </div>
          </template>
        </a-table-column>
        <a-table-column
          :title="$t('drop.column.min')"
          :width="90"
          align="center"
        >
          <template #cell="{ record }">
            <a-input-number
              v-if="editKey === record.rowKey"
              v-model="record.minimumQuantity"
              :min="1"
            />
            <span v-else>{{ record.minimumQuantity }}</span>
          </template>
        </a-table-column>
        <a-table-column
          :title="$t('drop.column.max')"
          :width="90"
          align="center"
        >
          <template #cell="{ record }">
            <a-input-number
              v-if="editKey === record.rowKey"
              v-model="record.maximumQuantity"
              :min="1"
            />
            <span v-else>{{ record.maximumQuantity }}</span>
          </template>
        </a-table-column>
        <a-table-column
          :title="$t('drop.column.chance')"
          :width="120"
          align="right"
        >
          <template #cell="{ record }">
            <a-input-number
              v-if="editKey === record.rowKey"
              v-model="record.chance"
              :min="1"
            />
            <div v-else class="chance-cell">
              <span class="chance-pct">{{ formatChance(record.chance) }}%</span>
              <span class="chance-bar-wrap">
                <span
                  class="chance-bar"
                  :style="{ width: chanceBarWidth(record.chance) }"
                />
              </span>
            </div>
          </template>
        </a-table-column>
        <a-table-column
          :title="$t('drop.column.questId')"
          :width="100"
          align="center"
        >
          <template #cell="{ record }">
            <a-input-number
              v-if="editKey === record.rowKey"
              v-model="record.questId"
              :min="0"
            />
            <span v-else>{{ record.questId || '—' }}</span>
          </template>
        </a-table-column>
        <a-table-column
          :title="$t('drop.column.quest')"
          :width="140"
          data-index="questName"
          align="center"
        />
        <a-table-column
          :title="$t('drop.column.operate')"
          :width="140"
          align="center"
          fixed="right"
        >
          <template #cell="{ record }">
            <a-space>
              <a-button
                v-if="editKey !== record.rowKey"
                type="text"
                size="mini"
                @click="editClick(record.rowKey)"
              >
                {{ $t('drop.action.edit') }}
              </a-button>
              <a-button
                v-if="editKey === record.rowKey"
                type="text"
                size="mini"
                @click="cancelEdit"
              >
                {{ $t('drop.action.cancel') }}
              </a-button>
              <a-button
                v-if="editKey === record.rowKey"
                type="text"
                size="mini"
                status="success"
                @click="saveClick(record)"
              >
                {{ $t('drop.action.save') }}
              </a-button>
              <a-popconfirm
                v-if="editKey === record.rowKey && record.id"
                :content="$t('drop.confirm.delete')"
                position="left"
                @ok="() => deleteClick(record)"
              >
                <a-button type="text" size="mini" status="danger">
                  {{ $t('drop.action.delete') }}
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </a-table-column>
      </template>
    </a-table>
  </a-drawer>
</template>

<script lang="ts" setup>
  import { computed, ref, watch } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import { deleteDrop, getDrop, insertDrop, updateDrop } from '@/api/drop';
  import { DropState } from '@/store/modules/drop/type';
  import { getIconUrl } from '@/utils/mapleStoryAPI';

  type DropRow = DropState & { rowKey: string };

  const props = defineProps<{
    visible: boolean;
    dropperId?: number;
    dropperName?: string;
    /** false = 可输入新怪物 ID */
    lockedDropper?: boolean;
  }>();

  const emit = defineEmits<{
    (e: 'update:visible', v: boolean): void;
    (e: 'changed'): void;
  }>();

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);
  const dropRows = ref<DropRow[]>([]);
  const editKey = ref('');
  const localDropperId = ref<number | undefined>();
  const activeDropperId = ref<number | undefined>();

  const lockedDropper = computed(() => props.lockedDropper !== false);

  const formatChance = (chance?: number) => {
    if (chance == null) return '—';
    return (chance / 10000).toFixed(4);
  };

  const chanceBarWidth = (chance?: number) => {
    if (!chance || chance <= 0) return '0%';
    const pct = Math.min(100, (chance / 10000) * 5);
    return `${pct}%`;
  };

  const onImgError = (e: Event) => {
    const img = e.target as HTMLImageElement;
    img.style.visibility = 'hidden';
  };

  const toRow = (r: DropState, idx: number): DropRow => ({
    ...r,
    rowKey: r.id != null && r.id > 0 ? `id-${r.id}` : `new-${idx}`,
  });

  const loadDrops = async () => {
    if (!activeDropperId.value) {
      dropRows.value = [];
      return;
    }
    editKey.value = '';
    setLoading(true);
    try {
      const { data } = await getDrop({
        dropperId: activeDropperId.value,
        pageNo: 1,
        pageSize: 1000,
      });
      dropRows.value = (data.records || []).map((r: DropState, i: number) =>
        toRow(r, i)
      );
    } finally {
      setLoading(false);
    }
  };

  const confirmMob = () => {
    if (!localDropperId.value) {
      Message.warning(t('drop.msg.needMob'));
      return;
    }
    activeDropperId.value = localDropperId.value;
    loadDrops();
  };

  watch(
    () => [props.visible, props.dropperId] as const,
    ([vis, id]) => {
      if (!vis) {
        dropRows.value = [];
        editKey.value = '';
        return;
      }
      localDropperId.value = id;
      activeDropperId.value = lockedDropper.value ? id : id;
      if (activeDropperId.value) {
        loadDrops();
      } else {
        dropRows.value = [];
      }
    }
  );

  const editClick = (key: string) => {
    editKey.value = key;
  };

  const cancelEdit = () => {
    editKey.value = '';
    loadDrops();
  };

  const insertClick = () => {
    if (!activeDropperId.value) {
      Message.warning(t('drop.msg.needMob'));
      return;
    }
    const row = toRow(
      {
        id: 0,
        dropperId: activeDropperId.value,
        itemId: undefined,
        minimumQuantity: 1,
        maximumQuantity: 1,
        questId: 0,
        chance: 10000,
      },
      Date.now()
    );
    dropRows.value.unshift(row);
    editKey.value = row.rowKey;
  };

  const saveClick = async (record: DropRow) => {
    if (record.itemId == null) {
      Message.warning(t('drop.msg.needItem'));
      return;
    }
    if (record.chance == null) {
      Message.warning(t('drop.msg.needChance'));
      return;
    }
    const payload: DropState = {
      ...record,
      dropperId: activeDropperId.value,
      id: record.id && record.id > 0 ? record.id : undefined,
    };
    setLoading(true);
    try {
      if (!payload.id) {
        await insertDrop(payload);
        Message.success(t('drop.msg.created'));
      } else {
        await updateDrop(payload);
        Message.success(t('drop.msg.updated'));
      }
      emit('changed');
      await loadDrops();
    } finally {
      setLoading(false);
    }
  };

  const deleteClick = async (record: DropRow) => {
    if (!record.id) return;
    setLoading(true);
    try {
      await deleteDrop(record);
      Message.success(t('drop.msg.deleted'));
      emit('changed');
      await loadDrops();
    } finally {
      setLoading(false);
    }
  };
</script>

<style lang="less" scoped>
  .drawer-title {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .mob-icon {
    width: 48px;
    height: 48px;
    object-fit: contain;
    background: var(--color-fill-2);
    border-radius: 8px;
  }
  .drawer-title-main {
    font-weight: 600;
    line-height: 1.3;
  }
  .drawer-title-id {
    margin-left: 6px;
    color: var(--color-text-3);
    font-weight: 400;
    font-size: 13px;
  }
  .drawer-title-sub {
    color: var(--color-text-3);
    font-size: 12px;
    margin-top: 2px;
  }
  .drawer-mob-pick {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;
  }
  .item-cell {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .item-icon {
    width: 28px;
    height: 28px;
    object-fit: contain;
    flex-shrink: 0;
  }
  .chance-cell {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 4px;
  }
  .chance-pct {
    font-variant-numeric: tabular-nums;
  }
  .chance-bar-wrap {
    width: 72px;
    height: 4px;
    background: var(--color-fill-3);
    border-radius: 2px;
    overflow: hidden;
  }
  .chance-bar {
    display: block;
    height: 100%;
    background: rgb(var(--primary-6));
    border-radius: 2px;
  }
</style>
