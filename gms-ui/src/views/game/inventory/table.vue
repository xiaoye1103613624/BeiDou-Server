<template>
  <div>
    <a-space style="margin-bottom: 12px">
      <a-popconfirm
        type="error"
        :content="$t('inventoryList.confirm.batchDelete')"
        :disabled="selectedKeys.length === 0"
        @ok="batchDeleteClick"
      >
        <a-button
          type="primary"
          status="danger"
          :disabled="selectedKeys.length === 0"
        >
          {{ $t('inventoryList.button.batchDelete') }}
          <template v-if="selectedKeys.length">
            ({{ selectedKeys.length }})
          </template>
        </a-button>
      </a-popconfirm>
    </a-space>
    <a-table
      v-model:selectedKeys="selectedKeys"
      row-key="inventoryRowKey"
      :loading="loading"
      :data="tableData"
      column-resizable
      :pagination="false"
      :bordered="{ cell: true }"
      :row-selection="{
        type: 'checkbox',
        showCheckedAll: true,
        onlyCurrent: false,
      }"
    >
      <template #columns>
        <a-table-column
          :title="$t('inventoryList.column.id')"
          data-index="id"
          align="center"
          :width="100"
        />
        <a-table-column
          :title="$t('inventoryList.column.itemId')"
          data-index="itemId"
          align="center"
          :width="130"
        />
        <a-table-column :title="$t('inventoryList.column.item')" align="center">
          <template #cell="{ record }">
            <a-popover placement="top">
              <template #content>
                <span>{{
                  record.itemId === 2430033 ? '北斗卫星指导书' : record.itemName
                }}</span>
              </template>
              <img
                v-if="record.itemId === 2430033"
                :src="beidouBook"
                alt="北斗卫星指导书"
              />
              <img
                v-else
                :src="getIconUrl('item', record.itemId)"
                :data-item-id="record.itemId"
                alt=""
                @error="onItemIconError"
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
  </div>
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
  import { getIconUrl, onItemIconError } from '@/utils/mapleStoryAPI';
  import InventoryEquipForm from '@/views/game/inventory/inventoryEquipForm.vue';
  import { timestampToChineseTime } from '@/utils/stringUtils';
  import beidouBook from '@/assets/2430033.png';
  import { Message } from '@arco-design/web-vue';

  const { setLoading, loading } = useLoading(false);
  type InventoryRow = InventoryState & { inventoryRowKey: string | number };
  const tableData = ref<InventoryRow[]>([]);
  const selectedKeys = ref<(string | number)[]>([]);

  const props = defineProps<{
    currentType: string | number;
    characterId: number | undefined;
  }>();
  const editId = ref<number | undefined>(undefined);

  const loadData = async () => {
    editId.value = undefined;
    selectedKeys.value = [];
    if (!props || !props.characterId) {
      return;
    }
    setLoading(true);
    try {
      const condition: InventoryCondition = {
        inventoryType: props.currentType as number,
        characterId: props.characterId as number,
        pageNo: 1,
        pageSize: 9999,
      };
      const { data } = await getInventoryList(condition);
      // Arco Table 的 row-key 只能是字段名（不是函数），在线物品 id 全是 -1，需自建唯一键
      tableData.value = (data as InventoryState[]).map((row, index) => ({
        ...row,
        inventoryRowKey:
          row.id != null && row.id > 0
            ? row.id
            : `online-${row.position}-${row.itemId}-${index}`,
      }));
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

  const batchDeleteClick = async () => {
    if (selectedKeys.value.length === 0) {
      return;
    }
    const keySet = new Set(selectedKeys.value.map(String));
    const targets = tableData.value.filter((row) =>
      keySet.has(String(row.inventoryRowKey))
    );
    if (targets.length === 0) {
      return;
    }
    setLoading(true);
    try {
      await Promise.all(targets.map((row) => deleteInventory(row)));
      Message.success(`已删除 ${targets.length} 条`);
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

<style lang="less" scoped></style>
