<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.toyCollection')">
      <!-- 操作栏 -->
      <a-row>
        <a-col>
          <a-space>
            <a-button type="primary" status="success" @click="addClick">
              {{ $t('button.create') }}
            </a-button>
            <a-button @click="loadData">
              {{ $t('button.search') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>

      <!-- 分类列表 -->
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
          <a-table-column
            :title="$t('toyCollection.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('toyCollection.column.name')"
            data-index="name"
            :width="140"
            align="center"
          />
          <a-table-column
            :title="$t('toyCollection.column.icon')"
            data-index="icon"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('toyCollection.column.sortOrder')"
            data-index="sortOrder"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('toyCollection.column.items')"
            align="center"
            :width="80"
          >
            <template #cell="{ record }">
              <span>{{ record.items ? record.items.length : 0 }} 个物品</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('toyCollection.column.enabled')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green">
                {{ $t('toyCollection.yes') }}
              </a-tag>
              <a-tag v-else color="gray">
                {{ $t('toyCollection.no') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('toyCollection.column.operation')"
            :width="160"
            fixed="right"
            align="center"
          >
            <template #cell="{ record }">
              <a-space :size="0">
                <a-button type="text" size="mini" @click="editClick(record)">
                  {{ $t('toyCollection.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('toyCollection.delete.confirm')"
                  position="top"
                  @ok="deleteClick(record.id)"
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
    </a-card>

    <!-- 编辑/新增弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="modalTitle"
      :width="800"
      @ok="saveClick"
      @cancel="onCancel"
    >
      <a-form
        :model="form"
        layout="vertical"
        style="max-height: 65vh; overflow-y: auto"
      >
        <!-- 分类基础信息 -->
        <a-divider>{{ $t('toyCollection.title.category') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('toyCollection.column.name')">
              <a-input
                v-model="form.name"
                :placeholder="$t('toyCollection.column.name')"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('toyCollection.column.icon')">
              <a-input v-model="form.icon" placeholder="留空使用默认图标" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('toyCollection.column.sortOrder')">
              <a-input-number
                v-model="form.sortOrder"
                :min="0"
                :default-value="0"
              />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('toyCollection.column.enabled')">
              <a-switch v-model="formEnabled" />
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 收集物品列表 -->
        <a-divider>{{ $t('toyCollection.title.items') }}</a-divider>
        <div
          v-for="(item, idx) in form.items"
          :key="idx"
          style="margin-bottom: 8px"
        >
          <a-row :gutter="8" align="center">
            <a-col :span="4">
              <a-form-item label="物品ID">
                <a-input-number
                  v-model="item.itemId"
                  :min="0"
                  :placeholder="'物品ID'"
                />
              </a-form-item>
            </a-col>
            <a-col :span="3">
              <a-form-item label="需求数量">
                <a-input-number
                  v-model="item.requiredQuantity"
                  :min="1"
                  :default-value="1"
                />
              </a-form-item>
            </a-col>
            <a-col :span="3">
              <a-form-item label="奖励ID">
                <a-input-number
                  v-model="item.rewardItemId"
                  :min="0"
                  :placeholder="'0=无奖励'"
                />
              </a-form-item>
            </a-col>
            <a-col :span="3">
              <a-form-item label="奖励数量">
                <a-input-number
                  v-model="item.rewardQuantity"
                  :min="1"
                  :default-value="1"
                />
              </a-form-item>
            </a-col>
            <a-col :span="2">
              <a-form-item label="排序">
                <a-input-number
                  v-model="item.sortOrder"
                  :min="0"
                  :default-value="0"
                />
              </a-form-item>
            </a-col>
            <a-col :span="2">
              <a-form-item label="启用">
                <a-switch
                  size="small"
                  :model-value="item.enabled !== 0"
                  @change="(v: boolean) => item.enabled = v ? 1 : 0"
                />
              </a-form-item>
            </a-col>
            <a-col :span="2">
              <a-button
                type="text"
                status="danger"
                size="mini"
                @click="removeItem(idx)"
              >
                {{ $t('toyCollection.button.removeItem') }}
              </a-button>
            </a-col>
          </a-row>
        </div>
        <a-button type="dashed" long @click="addItem">
          {{ $t('toyCollection.button.addItem') }}
        </a-button>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue';
  import { useLoading } from '@/hooks/loading';
  import {
    deleteCategory,
    getCategory,
    getCategoryList,
    saveCategory,
    type ToyCollectionCategory,
    type ToyCollectionItem,
  } from '@/api/toyCollection';

  const { loading, setLoading } = useLoading();

  // ==================== 表格数据 ====================
  const tableData = ref<ToyCollectionCategory[]>([]);

  async function loadData() {
    setLoading(true);
    try {
      const { data } = await getCategoryList();
      tableData.value = data || [];
    } finally {
      setLoading(false);
    }
  }

  // ==================== 弹窗 ====================
  const modalVisible = ref(false);
  const editingId = ref<number | undefined>(undefined);
  const modalTitle = computed(() =>
    editingId.value ? `编辑分类 #${editingId.value}` : '创建分类'
  );

  const emptyItem = (): ToyCollectionItem => ({
    itemId: undefined,
    requiredQuantity: 1,
    rewardItemId: 0,
    rewardQuantity: 1,
    sortOrder: 0,
    enabled: 1,
  });

  const emptyForm = (): ToyCollectionCategory => ({
    name: '',
    icon: '',
    sortOrder: 0,
    enabled: 1,
    items: [],
  });

  const form = ref<ToyCollectionCategory>(emptyForm());

  const formEnabled = computed({
    get: () => form.value.enabled === 1,
    set: (v: boolean) => {
      form.value.enabled = v ? 1 : 0;
    },
  });

  function addClick() {
    editingId.value = undefined;
    form.value = { ...emptyForm(), items: [] };
    modalVisible.value = true;
  }

  async function editClick(record: ToyCollectionCategory) {
    setLoading(true);
    try {
      if (record.id == null) return;
      const { data } = await getCategory(record.id);
      if (data) {
        editingId.value = data.id;
        form.value = {
          id: data.id,
          name: data.name || '',
          icon: data.icon || '',
          sortOrder: data.sortOrder ?? 0,
          enabled: data.enabled ?? 1,
          items: (data.items || []).map((item) => ({
            id: item.id,
            categoryId: item.categoryId,
            itemId: item.itemId,
            requiredQuantity: item.requiredQuantity ?? 1,
            rewardItemId: item.rewardItemId ?? 0,
            rewardQuantity: item.rewardQuantity ?? 1,
            sortOrder: item.sortOrder ?? 0,
            enabled: item.enabled ?? 1,
          })),
        };
        modalVisible.value = true;
      }
    } finally {
      setLoading(false);
    }
  }

  async function saveClick() {
    if (!form.value.name || form.value.name.trim() === '') {
      return;
    }
    setLoading(true);
    try {
      await saveCategory(form.value);
      modalVisible.value = false;
      await loadData();
    } finally {
      setLoading(false);
    }
  }

  async function deleteClick(id: number) {
    setLoading(true);
    try {
      await deleteCategory(id);
      await loadData();
    } finally {
      setLoading(false);
    }
  }

  function onCancel() {
    modalVisible.value = false;
    form.value = emptyForm();
  }

  function addItem() {
    if (!form.value.items) form.value.items = [];
    form.value.items.push(emptyItem());
  }

  function removeItem(idx: number) {
    form.value.items?.splice(idx, 1);
  }

  // ==================== 初始化 ====================
  onMounted(() => {
    loadData();
  });
</script>

<style scoped>
  .container {
    padding: 0 20px 20px;
  }
</style>
