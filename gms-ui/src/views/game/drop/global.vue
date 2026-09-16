<template>
  <PageContainer :title="$t('menu.game.drop.global')">
    <ProCard>
      <a-row>
        <a-col>
          <a-input-number
            v-model="condition.continent"
            :placeholder="$t('drop.global.filter.continent')"
            allow-clear
            @keydown.enter="loadData"
          />
          <a-input-number
            v-model="condition.itemId"
            :placeholder="$t('drop.search.itemId')"
            allow-clear
            @keydown.enter="loadData"
          />
          <a-input
            v-model="condition.itemName"
            :placeholder="$t('drop.search.itemName')"
            allow-clear
            @keydown.enter="loadData"
          />
          <a-input-number
            v-model="condition.questId"
            :placeholder="$t('drop.search.questId')"
            allow-clear
            @keydown.enter="loadData"
          />
          <a-space>
            <a-button type="primary" @click="loadData">
              {{ $t('drop.search.query') }}
            </a-button>
            <a-button @click="resetClick">
              {{ $t('drop.search.reset') }}
            </a-button>
            <a-button type="primary" status="success" @click="insertClick">
              {{ $t('button.add') }}
            </a-button>
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
      >
        <template #columns>
          <a-table-column
            :title="$t('drop.global.column.id')"
            data-index="id"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('drop.global.column.continent')"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="editId === record.id"
                v-model="record.continent"
              />
              <span v-else>{{ record.continent }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('drop.column.itemId')"
            :width="150"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="editId === record.id"
                v-model="record.itemId"
              />
              <span v-else>{{ record.itemId }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('drop.column.item')"
            :width="230"
            align="center"
          >
            <template #cell="{ record }">
              <a-button
                v-if="record.itemId === 0"
                type="text"
                size="mini"
                status="warning"
                @click="filterItemClick(record.itemId, record.itemName)"
              >
                {{ $t('drop.item.meso') }}
              </a-button>
              <a-popover v-else>
                <a-button
                  type="text"
                  size="mini"
                  @click="filterItemClick(record.itemId, record.itemName)"
                >
                  {{ record.itemName }}
                </a-button>
                <template #content>
                  <ItemIcon :id="record.itemId" category="item" :size="48" />
                </template>
              </a-popover>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('drop.column.min')"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="editId === record.id"
                v-model="record.minimumQuantity"
              />
              <span v-else>{{ record.minimumQuantity }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('drop.column.max')"
            data-index="maximumQuantity"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="editId === record.id"
                v-model="record.maximumQuantity"
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
                v-if="editId === record.id"
                v-model="record.chance"
              />
              <span v-else>{{ (record.chance / 10000).toFixed(4) }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('drop.column.questId')"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="editId === record.id"
                v-model="record.questId"
              />
              <span v-else> {{ record.questId }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('drop.column.quest')"
            :width="200"
            data-index="questName"
            align="center"
          />
          <a-table-column
            :title="$t('drop.global.column.comments')"
            :width="250"
            data-index="comments"
            align="center"
          >
            <template #cell="{ record }">
              <a-input v-if="editId === record.id" v-model="record.comments" />
              <span v-else>{{ record.comments }}</span>
            </template>
          </a-table-column>
          <a-table-column :title="$t('drop.column.operate')" :width="80">
            <template #cell="{ record }">
              <a-button
                v-if="editId !== record.id"
                type="text"
                size="mini"
                @click="editClick(record.id)"
              >
                {{ $t('drop.action.edit') }}
              </a-button>
              <a-button
                v-if="editId === record.id"
                type="text"
                size="mini"
                @click="cancelEditClick"
              >
                {{ $t('drop.action.cancel') }}
              </a-button>
              <a-button
                v-if="editId === record.id"
                type="text"
                size="mini"
                status="success"
                @click="saveClick(record)"
              >
                {{ $t('drop.action.save') }}
              </a-button>
              <a-popconfirm
                v-if="editId === record.id"
                :content="$t('drop.global.confirm.delete')"
                position="left"
                @ok="() => deleteClick(record)"
              >
                <a-button type="text" size="mini" status="danger">
                  {{ $t('drop.action.delete') }}
                </a-button>
              </a-popconfirm>
            </template>
          </a-table-column>
        </template>
      </a-table>
      <a-pagination
        class="bd-page-pagination"
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
    </ProCard>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import {
    deleteGlobalDrop,
    DropConditionState,
    getGlobalDrop,
    insertGlobalDrop,
    updateGlobalDrop,
  } from '@/api/drop';
  import { DropState } from '@/store/modules/drop/type';
  import useLoading from '@/hooks/loading';
  import { Message } from '@arco-design/web-vue';

  const { t } = useI18n();
  const { setLoading, loading } = useLoading(false);
  const condition = ref<DropConditionState>({
    dropperId: undefined,
    continent: undefined,
    itemId: undefined,
    questId: undefined,
    pageNo: 1,
    pageSize: 20,
    onlyTotal: false,
    notPage: false,
  });
  const total = ref<number>(0);
  const pageChange = (data: number) => {
    condition.value.pageNo = data;
    loadData();
  };

  const pageSizeChange = (data: number) => {
    condition.value.pageNo = 1;
    condition.value.pageSize = data;
    loadData();
  };

  const editId = ref<number>(0);

  const tableData = ref<DropState[]>([]);
  const loadData = async () => {
    editId.value = 0;
    setLoading(true);
    try {
      const { data } = await getGlobalDrop(condition.value);
      tableData.value = data.records;
      total.value = data.totalRow;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const resetClick = () => {
    condition.value.continent = undefined;
    condition.value.itemId = undefined;
    condition.value.itemName = undefined;
    condition.value.questId = undefined;
    condition.value.pageNo = 1;
    loadData();
  };

  const filterItemClick = (itemId: number, itemName: string) => {
    condition.value.itemId = itemId;
    condition.value.pageNo = 1;
    if (itemId === 0) itemName = t('drop.item.meso');
    Message.success(t('drop.global.msg.filterItem', { itemName, itemId }));
    loadData();
  };

  const editClick = (id: number) => {
    editId.value = id;
  };

  const cancelEditClick = () => {
    editId.value = 0;
  };

  const saveClick = async (data: DropState) => {
    setLoading(true);
    try {
      if (data.id === 0) {
        await insertGlobalDrop(data);
        Message.success(t('drop.global.msg.created'));
      } else {
        await updateGlobalDrop(data);
        Message.success(t('drop.global.msg.updated'));
      }
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const deleteClick = async (data: DropState) => {
    setLoading(true);
    try {
      await deleteGlobalDrop(data);
      Message.success(t('drop.global.msg.deleted'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const insertClick = () => {
    editId.value = 0;
    tableData.value?.unshift({
      id: 0,
      dropperId: undefined,
      dropperName: undefined,
      continent: condition.value.continent || -1,
      itemId: condition.value.itemId,
      itemName: undefined,
      minimumQuantity: 1,
      maximumQuantity: 1,
      questId: condition.value.questId || 0,
      questName: undefined,
      chance: undefined,
      comments: undefined,
    });
  };
</script>

<script lang="ts">
  export default {
    name: 'GlobalDrop',
  };
</script>

<style lang="less" scoped>
  :deep(.arco-card-body, .arco-row) {
    width: 100%;
  }
  .arco-card-body > .arco-row > .arco-col > .arco-input-wrapper {
    margin-right: 0;
    margin-bottom: 5px;
    width: 100%;
  }
  @media (min-width: 500px) {
    .arco-card-body > .arco-row > .arco-col > .arco-input-wrapper {
      margin-right: 8px;
      width: 140px;
    }
  }
</style>
