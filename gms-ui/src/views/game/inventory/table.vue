<template>
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
        :title="$t('inventoryList.column.id')"
        data-index="id"
        align="center"
        :width="100"
      />
      <!--      <a-table-column-->
      <!--        :title="$t('inventoryList.column.characterId')"-->
      <!--        data-index="characterId"-->
      <!--        align="center"-->
      <!--      />-->
      <!--      <a-table-column-->
      <!--        :title="$t('inventoryList.column.online')"-->
      <!--        data-index="online"-->
      <!--        align="center"-->
      <!--      >-->
      <!--        <template #cell="{ record }">-->
      <!--          <a-tag v-if="record.online" color="green">{{-->
      <!--            $t('inventoryList.column.online')-->
      <!--          }}</a-tag>-->
      <!--          <a-tag v-else color="gray">{{-->
      <!--            $t('inventoryList.column.offline')-->
      <!--          }}</a-tag>-->
      <!--        </template>-->
      <!--      </a-table-column>-->
      <a-table-column
        :title="$t('inventoryList.column.itemId')"
        align="center"
        :width="260"
      >
        <template #cell="{ record }">
          <span>{{ record.itemId }}</span>
          <span v-if="displayItemName(record)" class="item-name">
            {{ displayItemName(record) }}
          </span>
        </template>
      </a-table-column>
      <a-table-column
        :title="$t('inventoryList.column.item')"
        align="center"
        :width="72"
      >
        <template #cell="{ record }">
          <a-popover placement="top">
            <template #content>
              <span>{{ displayItemName(record) || record.itemId }}</span>
            </template>
            <img
              v-if="record.itemId === 2430033"
              class="inv-item-icon"
              :src="beidouBook"
              alt="北斗卫星指导书"
            />
            <img
              v-else
              class="inv-item-icon"
              :src="getItemIconUrl(record.itemId)"
              :data-item-id="record.itemId"
              :alt="displayItemName(record) || String(record.itemId)"
              @error="onItemIconError"
              @load="onInventoryIconLoad"
            />
          </a-popover>
        </template>
      </a-table-column>
      <a-table-column
        :title="$t('inventoryList.column.itemType')"
        data-index="itemType"
        align="center"
      />
      <a-table-column
        :title="$t('inventoryList.column.position')"
        data-index="position"
        align="center"
      />
      <a-table-column
        :title="$t('inventoryList.column.quantity')"
        align="center"
        :width="160"
      >
        <template #cell="{ record }">
          <span v-if="editId !== record.id">
            {{ record.quantity }}
          </span>
          <a-input-number v-else v-model="record.quantity" />
        </template>
      </a-table-column>
      <a-table-column
        :title="$t('inventoryList.column.owner')"
        data-index="owner"
        align="center"
      />
      <a-table-column
        :title="$t('inventoryList.column.petId')"
        data-index="petId"
        align="center"
      />
      <a-table-column
        :title="$t('inventoryList.column.flag')"
        data-index="flag"
        align="center"
      />
      <a-table-column
        :title="$t('inventoryList.column.giftFrom')"
        data-index="giftFrom"
        align="center"
      />
      <a-table-column
        :title="$t('inventoryList.column.expiration')"
        align="center"
      >
        <template #cell="{ record }">
          <span v-if="editId !== record.id">
            {{ timestampToChineseTime(record.expiration) }}
          </span>
          <a-input-number v-else v-model="record.expiration" />
        </template>
      </a-table-column>
      <a-table-column
        :title="$t('inventoryList.column.operation')"
        :width="200"
      >
        <template #cell="{ record }">
          <a-button
            v-if="editId !== record.id"
            type="text"
            size="mini"
            @click="editClick(record)"
          >
            {{ $t('inventoryList.button.edit') }}
          </a-button>
          <a-button
            v-if="editId === record.id"
            type="text"
            size="mini"
            status="success"
            @click="saveClick(record)"
          >
            {{ $t('inventoryList.button.save') }}
          </a-button>
          <a-button
            v-if="editId === record.id"
            type="text"
            size="mini"
            @click="editId = undefined"
          >
            {{ $t('inventoryList.button.cancel') }}
          </a-button>
          <a-popconfirm
            v-if="editId !== record.id"
            type="error"
            :content="$t('inventoryList.confirm.delete')"
            @ok="deleteClick(record)"
          >
            <a-button type="text" size="mini" status="danger">
              {{ $t('inventoryList.button.delete') }}
            </a-button>
          </a-popconfirm>
        </template>
      </a-table-column>
    </template>
  </a-table>
  <inventory-equip-form ref="inventoryEquipFormRef" @load-data="loadData" />
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import {
    deleteInventory,
    getInventoryList,
    InventoryCondition,
    updateInventory,
  } from '@/api/inventory';
  import useLoading from '@/hooks/loading';
  import { InventoryState } from '@/store/modules/inventory/type';
  import { getItemIconUrl, onItemIconError } from '@/utils/mapleStoryAPI';
  import InventoryEquipForm from '@/views/game/inventory/inventoryEquipForm.vue';
  import { timestampToChineseTime } from '@/utils/stringUtils';
  import beidouBook from '@/assets/2430033.png';

  const { setLoading, loading } = useLoading(false);
  const tableData = ref<InventoryState[]>([]);

  const displayItemName = (record: InventoryState) => {
    if (record.itemId === 2430033) {
      return '北斗卫星指导书';
    }
    return record.itemName?.trim() || '';
  };

  /** CDN 在道具/NPC 同号时会返回 NPC 立绘（通常远大于 32px 道具图标） */
  const onInventoryIconLoad = (event: Event) => {
    const img = event.target as HTMLImageElement | null;
    if (!img) return;
    if (!/maplestory\.io/i.test(img.src)) return;
    if (img.naturalWidth > 48 || img.naturalHeight > 48) {
      img.style.visibility = 'hidden';
      img.removeAttribute('src');
    }
  };

  const props = defineProps<{
    currentType: string | number;
    characterId: number | undefined;
  }>();
  const editId = ref<number | undefined>(undefined);

  const loadData = async () => {
    editId.value = undefined;
    if (!props || !props.characterId) {
      return;
    }
    setLoading(true);
    try {
      const condition: InventoryCondition = {
        inventoryType: props.currentType as number,
        characterId: props.characterId as number,
        pageNo: 1,
        pageSize: 20,
      };
      const { data } = await getInventoryList(condition);
      tableData.value = data;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const saveClick = async (data: InventoryState) => {
    setLoading(true);
    try {
      await updateInventory(data);
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const deleteClick = async (data: InventoryState) => {
    setLoading(true);
    try {
      await deleteInventory(data);
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const inventoryEquipFormRef = ref();
  const editClick = (data: InventoryState) => {
    if (data.equipment) {
      inventoryEquipFormRef.value.initForm(data);
    } else {
      editId.value = data.id;
    }
  };
</script>

<script lang="ts">
  export default {
    name: 'InventoryList',
  };
</script>

<style lang="less" scoped>
  .item-name {
    margin-left: 8px;
    color: var(--color-text-2);
  }

  .inv-item-icon {
    max-width: 36px;
    max-height: 36px;
    object-fit: contain;
    vertical-align: middle;
  }
</style>
