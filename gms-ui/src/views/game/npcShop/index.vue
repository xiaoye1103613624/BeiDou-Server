<template>
  <PageContainer :title="$t('menu.game.npcShop')">
    <ProCard>
      <a-tabs v-model:active-key="activeTab" type="rounded">
        <a-tab-pane key="byNpc" :title="$t('npcShop.tab.byNpc')">
          <a-space wrap class="bd-page-toolbar">
            <a-input-number
              v-model="shopQuery.shopId"
              :placeholder="$t('npcShop.filter.shopId')"
              hide-button
              @keydown.enter="searchShops"
            />
            <a-input-number
              v-model="shopQuery.npcId"
              :placeholder="$t('npcShop.filter.npcId')"
              hide-button
              @keydown.enter="searchShops"
            />
            <a-input
              v-model="shopQuery.npcName"
              :placeholder="$t('npcShop.filter.npcName')"
              allow-clear
              @keydown.enter="searchShops"
            />
            <a-input-number
              v-model="shopQuery.itemId"
              :placeholder="$t('npcShop.filter.itemId')"
              hide-button
              @keydown.enter="searchShops"
            />
            <a-input
              v-model="shopQuery.itemName"
              :placeholder="$t('npcShop.filter.itemName')"
              allow-clear
              @keydown.enter="searchShops"
            />
            <a-button type="primary" status="success" @click="searchShops">
              {{ $t('button.search') }}
            </a-button>
            <a-button @click="resetShops">
              {{ $t('button.reset') }}
            </a-button>
            <a-button type="primary" @click="openShopCreate">
              {{ $t('npcShop.shop.add') }}
            </a-button>
          </a-space>

          <a-row :gutter="12">
            <a-col :xs="24" :md="8">
              <a-card :title="$t('npcShop.shop.list')" size="small">
                <a-table
                  row-key="shopId"
                  :loading="shopLoading"
                  :data="shopList"
                  :pagination="false"
                  :bordered="{ cell: true }"
                  :row-class="shopRowClass"
                  @row-click="onShopRowClick"
                >
                  <template #columns>
                    <a-table-column
                      :title="$t('npcShop.column.npcImage')"
                      :width="56"
                      align="center"
                    >
                      <template #cell="{ record }">
                        <ItemIcon
                          :id="record.npcId"
                          category="npc"
                          :size="32"
                        />
                      </template>
                    </a-table-column>
                    <a-table-column
                      :title="$t('npcShop.column.npcName')"
                      data-index="npcName"
                    />
                    <a-table-column
                      :title="$t('npcShop.column.shopId')"
                      data-index="shopId"
                      :width="80"
                      align="center"
                    />
                    <a-table-column
                      :title="$t('npcShop.column.operate')"
                      :width="120"
                      align="center"
                    >
                      <template #cell="{ record }">
                        <a-space :size="0" @click.stop>
                          <a-button
                            type="text"
                            size="mini"
                            @click="openShopEdit(record)"
                          >
                            {{ $t('button.edit') }}
                          </a-button>
                          <a-popconfirm
                            :content="$t('npcShop.confirm.deleteShop')"
                            @ok="deleteShopClick(record.shopId)"
                          >
                            <a-button type="text" size="mini" status="danger">
                              {{ $t('button.delete') }}
                            </a-button>
                          </a-popconfirm>
                        </a-space>
                      </template>
                    </a-table-column>
                  </template>
                </a-table>
                <a-pagination
                  class="bd-page-pagination"
                  :total="shopTotal"
                  :page-size="shopQuery.pageSize"
                  :current="shopQuery.pageNo"
                  show-total
                  show-jumper
                  show-page-size
                  :page-size-options="[10, 20, 50]"
                  @change="onShopPageChange"
                  @page-size-change="onShopPageSizeChange"
                />
              </a-card>
            </a-col>
            <a-col :xs="24" :md="16">
              <a-card size="small">
                <template #title>
                  <span v-if="selectedShop">
                    {{ selectedShop.npcName }} · #{{ selectedShop.shopId }}
                  </span>
                  <span v-else>{{ $t('npcShop.shop.empty') }}</span>
                </template>
                <template #extra>
                  <a-button
                    v-if="selectedShopId"
                    type="primary"
                    size="small"
                    @click="insertItemClick"
                  >
                    {{ $t('button.add') }}
                  </a-button>
                </template>
                <a-table
                  v-if="selectedShopId"
                  row-key="rowKey"
                  :loading="itemLoading"
                  :data="shopItemList"
                  column-resizable
                  :pagination="false"
                  :bordered="{ cell: true }"
                >
                  <template #columns>
                    <a-table-column
                      :title="$t('npcShop.column.itemImage')"
                      :width="56"
                      align="center"
                    >
                      <template #cell="{ record }">
                        <ItemIcon
                          v-if="record.itemId"
                          :id="record.itemId"
                          category="item"
                          :size="32"
                        />
                      </template>
                    </a-table-column>
                    <a-table-column
                      :title="$t('npcShop.column.itemId')"
                      :width="110"
                      align="center"
                    >
                      <template #cell="{ record }">
                        <a-input-number
                          v-if="isItemEditing(record)"
                          v-model="record.itemId"
                          hide-button
                        />
                        <span v-else>{{ record.itemId }}</span>
                      </template>
                    </a-table-column>
                    <a-table-column
                      :title="$t('npcShop.column.item')"
                      data-index="itemName"
                      :width="140"
                    />
                    <a-table-column
                      :title="$t('npcShop.column.price')"
                      :width="110"
                      align="center"
                    >
                      <template #cell="{ record }">
                        <a-input-number
                          v-if="isItemEditing(record)"
                          v-model="record.price"
                          hide-button
                        />
                        <span v-else>{{ record.price }}</span>
                      </template>
                    </a-table-column>
                    <a-table-column
                      :title="$t('npcShop.column.pitch')"
                      :width="90"
                      align="center"
                    >
                      <template #cell="{ record }">
                        <a-input-number
                          v-if="isItemEditing(record)"
                          v-model="record.pitch"
                          hide-button
                        />
                        <span v-else>{{ record.pitch }}</span>
                      </template>
                    </a-table-column>
                    <a-table-column
                      :title="$t('npcShop.column.position')"
                      :width="90"
                      align="center"
                    >
                      <template #cell="{ record }">
                        <a-input-number
                          v-if="isItemEditing(record)"
                          v-model="record.position"
                          hide-button
                        />
                        <span v-else>{{ record.position }}</span>
                      </template>
                    </a-table-column>
                    <a-table-column
                      :title="$t('npcShop.column.itemDesc')"
                      data-index="itemDesc"
                    />
                    <a-table-column
                      :title="$t('npcShop.column.operate')"
                      :width="140"
                      fixed="right"
                    >
                      <template #cell="{ record }">
                        <a-space :size="0">
                          <a-button
                            v-if="!isItemEditing(record)"
                            type="text"
                            size="mini"
                            @click="editMode = record.id"
                          >
                            {{ $t('button.edit') }}
                          </a-button>
                          <a-popconfirm
                            v-if="!isItemEditing(record) && record.id"
                            :content="$t('npcShop.confirm.delete')"
                            @ok="deleteItemClick(record.id)"
                          >
                            <a-button type="text" size="mini" status="danger">
                              {{ $t('button.delete') }}
                            </a-button>
                          </a-popconfirm>
                          <a-button
                            v-if="isItemEditing(record)"
                            type="text"
                            size="mini"
                            status="success"
                            @click="saveItemClick(record)"
                          >
                            {{ $t('button.save') }}
                          </a-button>
                          <a-button
                            v-if="isItemEditing(record)"
                            type="text"
                            size="mini"
                            @click="rollbackItemClick(record)"
                          >
                            {{ $t('npcShop.action.back') }}
                          </a-button>
                        </a-space>
                      </template>
                    </a-table-column>
                  </template>
                </a-table>
                <a-pagination
                  v-if="selectedShopId"
                  class="bd-page-pagination"
                  :total="itemTotal"
                  :page-size="itemQuery.pageSize"
                  :current="itemQuery.pageNo"
                  show-total
                  show-jumper
                  show-page-size
                  :page-size-options="[10, 20, 50]"
                  @change="onItemPageChange"
                  @page-size-change="onItemPageSizeChange"
                />
                <a-empty v-else :description="$t('npcShop.shop.empty')" />
              </a-card>
            </a-col>
          </a-row>
        </a-tab-pane>

        <a-tab-pane key="byItem" :title="$t('npcShop.tab.byItem')">
          <a-space wrap class="bd-page-toolbar">
            <a-input-number
              v-model="itemSearch.itemId"
              :placeholder="$t('npcShop.filter.itemId')"
              hide-button
              @keydown.enter="searchByItem"
            />
            <a-input
              v-model="itemSearch.itemName"
              :placeholder="$t('npcShop.filter.itemName')"
              allow-clear
              @keydown.enter="searchByItem"
            />
            <a-button type="primary" status="success" @click="searchByItem">
              {{ $t('button.search') }}
            </a-button>
            <a-button @click="resetItemSearch">
              {{ $t('button.reset') }}
            </a-button>
          </a-space>
          <a-table
            row-key="rowKey"
            :loading="reverseLoading"
            :data="reverseList"
            column-resizable
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #empty>
              <a-empty :description="$t('npcShop.item.emptyHint')" />
            </template>
            <template #columns>
              <a-table-column
                :title="$t('npcShop.column.itemImage')"
                :width="56"
                align="center"
              >
                <template #cell="{ record }">
                  <ItemIcon
                    v-if="record.itemId"
                    :id="record.itemId"
                    category="item"
                    :size="32"
                  />
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('npcShop.column.item')"
                data-index="itemName"
                :width="140"
              />
              <a-table-column
                :title="$t('npcShop.column.itemId')"
                data-index="itemId"
                :width="100"
                align="center"
              />
              <a-table-column
                :title="$t('npcShop.column.npcImage')"
                :width="56"
                align="center"
              >
                <template #cell="{ record }">
                  <ItemIcon
                    v-if="record.npcId"
                    :id="record.npcId"
                    category="npc"
                    :size="32"
                  />
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('npcShop.column.npcName')"
                data-index="npcName"
                :width="140"
              />
              <a-table-column
                :title="$t('npcShop.column.shopId')"
                data-index="shopId"
                :width="90"
                align="center"
              />
              <a-table-column
                :title="$t('npcShop.column.price')"
                :width="110"
                align="center"
              >
                <template #cell="{ record }">
                  <a-input-number
                    v-if="isItemEditing(record)"
                    v-model="record.price"
                    hide-button
                  />
                  <span v-else>{{ record.price }}</span>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('npcShop.column.pitch')"
                :width="90"
                align="center"
              >
                <template #cell="{ record }">
                  <a-input-number
                    v-if="isItemEditing(record)"
                    v-model="record.pitch"
                    hide-button
                  />
                  <span v-else>{{ record.pitch }}</span>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('npcShop.column.position')"
                :width="90"
                align="center"
              >
                <template #cell="{ record }">
                  <a-input-number
                    v-if="isItemEditing(record)"
                    v-model="record.position"
                    hide-button
                  />
                  <span v-else>{{ record.position }}</span>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('npcShop.column.operate')"
                :width="180"
                fixed="right"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button
                      v-if="!isItemEditing(record)"
                      type="text"
                      size="mini"
                      @click="jumpToShop(record.shopId)"
                    >
                      {{ $t('npcShop.action.jump') }}
                    </a-button>
                    <a-button
                      v-if="!isItemEditing(record)"
                      type="text"
                      size="mini"
                      @click="editMode = record.id"
                    >
                      {{ $t('button.edit') }}
                    </a-button>
                    <a-popconfirm
                      v-if="!isItemEditing(record) && record.id"
                      :content="$t('npcShop.confirm.delete')"
                      @ok="deleteReverseItemClick(record.id)"
                    >
                      <a-button type="text" size="mini" status="danger">
                        {{ $t('button.delete') }}
                      </a-button>
                    </a-popconfirm>
                    <a-button
                      v-if="isItemEditing(record)"
                      type="text"
                      size="mini"
                      status="success"
                      @click="saveReverseItemClick(record)"
                    >
                      {{ $t('button.save') }}
                    </a-button>
                    <a-button
                      v-if="isItemEditing(record)"
                      type="text"
                      size="mini"
                      @click="editMode = undefined"
                    >
                      {{ $t('npcShop.action.back') }}
                    </a-button>
                  </a-space>
                </template>
              </a-table-column>
            </template>
          </a-table>
          <a-pagination
            v-if="reverseQueried"
            class="bd-page-pagination"
            :total="reverseTotal"
            :page-size="itemSearch.pageSize"
            :current="itemSearch.pageNo"
            show-total
            show-jumper
            show-page-size
            :page-size-options="[10, 20, 50]"
            @change="onReversePageChange"
            @page-size-change="onReversePageSizeChange"
          />
        </a-tab-pane>
      </a-tabs>
    </ProCard>

    <a-modal
      v-model:visible="shopModalVisible"
      :title="shopModalEdit ? $t('npcShop.shop.edit') : $t('npcShop.shop.add')"
      :ok-loading="shopSaving"
      @before-ok="saveShopClick"
    >
      <a-form :model="shopForm" layout="vertical">
        <a-form-item
          :label="$t('npcShop.column.shopId')"
          :extra="$t('npcShop.shop.modal.shopIdHint')"
        >
          <a-input-number
            v-model="shopForm.shopId"
            :disabled="shopModalEdit"
            hide-button
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item
          :label="$t('npcShop.column.npcId')"
          :extra="$t('npcShop.shop.modal.npcHint')"
        >
          <a-input-number
            v-model="shopForm.npcId"
            hide-button
            style="width: 100%"
          />
        </a-form-item>
        <ItemIcon
          v-if="shopForm.npcId"
          :id="shopForm.npcId"
          category="npc"
          :size="48"
        />
      </a-form>
    </a-modal>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { computed, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { Message } from '@arco-design/web-vue';
  import type { TableData } from '@arco-design/web-vue';
  import {
    addShop,
    addShopItem,
    deleteShop,
    deleteShopItem,
    getItemShopList,
    getShopFilter,
    getShopItemList,
    getShopList,
    updateShop,
    updateShopItem,
  } from '@/api/npcShop';
  import { NpcShopItemState, NpcShopState } from '@/store/modules/npcShop/type';

  const { t } = useI18n();

  const emptyFilter = (): getShopFilter => ({
    pageNo: 1,
    pageSize: 20,
    onlyTotal: false,
    notPage: false,
    shopId: undefined,
    npcId: undefined,
    npcName: undefined,
    itemId: undefined,
    itemName: undefined,
  });

  const activeTab = ref<'byNpc' | 'byItem'>('byNpc');
  const shopQuery = ref<getShopFilter>(emptyFilter());
  const itemQuery = ref<getShopFilter>(emptyFilter());
  const itemSearch = ref<getShopFilter>(emptyFilter());

  const shopList = ref<NpcShopState[]>([]);
  const shopTotal = ref(0);
  const shopLoading = ref(false);
  const selectedShopId = ref<number>();

  const shopItemList = ref<NpcShopItemState[]>([]);
  const itemTotal = ref(0);
  const itemLoading = ref(false);
  const editMode = ref<number>();

  const reverseList = ref<NpcShopItemState[]>([]);
  const reverseTotal = ref(0);
  const reverseLoading = ref(false);
  const reverseQueried = ref(false);

  const shopModalVisible = ref(false);
  const shopModalEdit = ref(false);
  const shopSaving = ref(false);
  const shopForm = ref<NpcShopState>({});

  const selectedShop = computed(() =>
    shopList.value.find((s) => s.shopId === selectedShopId.value)
  );

  const withRowKey = (
    item: NpcShopItemState,
    index: number
  ): NpcShopItemState => ({
    ...item,
    rowKey: item.id ?? `new-${index}`,
  });

  const isItemEditing = (record: NpcShopItemState) =>
    record.id === undefined || editMode.value === record.id;

  const shopRowClass = (record: TableData) =>
    record.shopId === selectedShopId.value ? 'shop-row-active' : '';

  const loadShopList = async (keepSelection = true) => {
    shopLoading.value = true;
    try {
      const { data } = await getShopList(shopQuery.value);
      shopList.value = data.records || [];
      shopTotal.value = data.totalRow || 0;
      if (!keepSelection) {
        selectedShopId.value = undefined;
        shopItemList.value = [];
        return;
      }
      if (
        selectedShopId.value &&
        !shopList.value.some((s) => s.shopId === selectedShopId.value)
      ) {
        selectedShopId.value = undefined;
        shopItemList.value = [];
      }
    } finally {
      shopLoading.value = false;
    }
  };

  const loadShopItemList = async () => {
    if (!selectedShopId.value) {
      shopItemList.value = [];
      itemTotal.value = 0;
      return;
    }
    itemLoading.value = true;
    try {
      const { data } = await getShopItemList({
        ...itemQuery.value,
        shopId: selectedShopId.value,
      });
      shopItemList.value = (data.records || []).map(withRowKey);
      itemTotal.value = data.totalRow || 0;
      editMode.value = undefined;
    } finally {
      itemLoading.value = false;
    }
  };

  const selectShop = (shopId?: number) => {
    selectedShopId.value = shopId;
    itemQuery.value.pageNo = 1;
    editMode.value = undefined;
    loadShopItemList();
  };

  const onShopRowClick = (record: TableData) => {
    const shop = record as NpcShopState;
    if (shop.shopId) {
      selectShop(shop.shopId);
    }
  };

  const searchShops = () => {
    shopQuery.value.pageNo = 1;
    loadShopList(true);
  };

  const resetShops = () => {
    shopQuery.value = emptyFilter();
    loadShopList(false);
  };

  const onShopPageChange = (page: number) => {
    shopQuery.value.pageNo = page;
    loadShopList(true);
  };

  const onShopPageSizeChange = (size: number) => {
    shopQuery.value.pageNo = 1;
    shopQuery.value.pageSize = size;
    loadShopList(true);
  };

  const onItemPageChange = (page: number) => {
    itemQuery.value.pageNo = page;
    loadShopItemList();
  };

  const onItemPageSizeChange = (size: number) => {
    itemQuery.value.pageNo = 1;
    itemQuery.value.pageSize = size;
    loadShopItemList();
  };

  const openShopCreate = () => {
    shopModalEdit.value = false;
    shopForm.value = {};
    shopModalVisible.value = true;
  };

  const openShopEdit = (record: NpcShopState) => {
    shopModalEdit.value = true;
    shopForm.value = {
      shopId: record.shopId,
      npcId: record.npcId,
    };
    shopModalVisible.value = true;
  };

  const saveShopClick = async () => {
    shopSaving.value = true;
    try {
      if (shopModalEdit.value) {
        await updateShop(shopForm.value);
        Message.success(t('npcShop.msg.shopUpdated'));
      } else {
        const { data } = await addShop(shopForm.value);
        Message.success(t('npcShop.msg.shopCreated'));
        shopQuery.value = emptyFilter();
        if (data) {
          shopQuery.value.shopId = data;
          selectedShopId.value = data;
        }
      }
      await loadShopList(true);
      if (selectedShopId.value) {
        await loadShopItemList();
      }
      return true;
    } catch {
      return false;
    } finally {
      shopSaving.value = false;
    }
  };

  const deleteShopClick = async (shopId?: number) => {
    if (!shopId) {
      return;
    }
    await deleteShop(shopId);
    Message.success(t('npcShop.msg.shopDeleted'));
    if (selectedShopId.value === shopId) {
      selectedShopId.value = undefined;
      shopItemList.value = [];
    }
    await loadShopList(true);
  };

  const insertItemClick = () => {
    if (!selectedShopId.value) {
      return;
    }
    shopItemList.value.unshift({
      shopId: selectedShopId.value,
      itemId: undefined,
      price: 0,
      pitch: 0,
      position: 1,
      rowKey: `new-${Date.now()}`,
    });
    editMode.value = undefined;
  };

  const saveItemClick = async (data: NpcShopItemState) => {
    itemLoading.value = true;
    try {
      if (data.id === undefined) {
        await addShopItem(data);
        Message.success(t('npcShop.msg.itemCreated'));
      } else {
        await updateShopItem(data);
        Message.success(t('npcShop.msg.itemUpdated'));
      }
      await loadShopItemList();
    } finally {
      itemLoading.value = false;
    }
  };

  const deleteItemClick = async (id?: number) => {
    if (!id) {
      return;
    }
    await deleteShopItem(id);
    Message.success(t('npcShop.msg.itemDeleted'));
    await loadShopItemList();
  };

  const rollbackItemClick = (record: NpcShopItemState) => {
    if (record.id === undefined) {
      const index = shopItemList.value.findIndex((item) => item === record);
      if (index > -1) {
        shopItemList.value.splice(index, 1);
      }
    }
    editMode.value = undefined;
  };

  const loadReverseList = async () => {
    reverseLoading.value = true;
    try {
      const { data } = await getItemShopList(itemSearch.value);
      reverseList.value = (data.records || []).map(withRowKey);
      reverseTotal.value = data.totalRow || 0;
      reverseQueried.value = true;
      editMode.value = undefined;
    } finally {
      reverseLoading.value = false;
    }
  };

  const searchByItem = () => {
    if (!itemSearch.value.itemId && !itemSearch.value.itemName) {
      Message.error(t('npcShop.item.emptyHint'));
      return;
    }
    itemSearch.value.pageNo = 1;
    loadReverseList();
  };

  const resetItemSearch = () => {
    itemSearch.value = emptyFilter();
    reverseList.value = [];
    reverseTotal.value = 0;
    reverseQueried.value = false;
    editMode.value = undefined;
  };

  const onReversePageChange = (page: number) => {
    itemSearch.value.pageNo = page;
    loadReverseList();
  };

  const onReversePageSizeChange = (size: number) => {
    itemSearch.value.pageNo = 1;
    itemSearch.value.pageSize = size;
    loadReverseList();
  };

  const saveReverseItemClick = async (data: NpcShopItemState) => {
    reverseLoading.value = true;
    try {
      await updateShopItem(data);
      Message.success(t('npcShop.msg.itemUpdated'));
      await loadReverseList();
    } finally {
      reverseLoading.value = false;
    }
  };

  const deleteReverseItemClick = async (id?: number) => {
    if (!id) {
      return;
    }
    await deleteShopItem(id);
    Message.success(t('npcShop.msg.itemDeleted'));
    await loadReverseList();
  };

  const jumpToShop = async (shopId?: number) => {
    if (!shopId) {
      return;
    }
    activeTab.value = 'byNpc';
    shopQuery.value = emptyFilter();
    shopQuery.value.shopId = shopId;
    await loadShopList(true);
    selectShop(shopId);
  };

  loadShopList(false);
</script>

<script lang="ts">
  export default {
    name: 'NpcShop',
  };
</script>

<style lang="less" scoped>
  :deep(.arco-card-body) {
    width: 100%;
  }
  :deep(.shop-row-active) td {
    background: var(--color-fill-2);
  }
  .arco-input-wrapper,
  :deep(.arco-input-number) {
    width: 140px;
  }
</style>
