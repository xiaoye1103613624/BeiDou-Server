<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.growth.newbieGift')">
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

      <a-table
        row-key="id"
        :loading="loading"
        :data="tableData"
        :pagination="false"
        :bordered="{ cell: true }"
        style="margin-top: 16px"
      >
        <template #columns>
          <a-table-column
            :title="$t('newbieGift.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('newbieGift.column.giftName')"
            data-index="giftName"
            :width="150"
            align="center"
          />
          <a-table-column
            :title="$t('newbieGift.column.minLevel')"
            data-index="minLevel"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('newbieGift.column.maxLevel')"
            data-index="maxLevel"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('newbieGift.column.enabled')"
            data-index="enabled"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="record.enabled === 1 ? 'green' : 'red'">
                {{ record.enabled === 1 ? 'ON' : 'OFF' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('newbieGift.column.items')"
            :width="200"
            align="center"
          >
            <template #cell="{ record }">
              <span v-if="record.items && record.items.length">
                {{ record.items.length }} 种物品
              </span>
              <span v-else style="color: #999">-</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('newbieGift.column.currencies')"
            :width="200"
            align="center"
          >
            <template #cell="{ record }">
              <span v-if="record.currencies && record.currencies.length">
                <a-tag
                  v-for="c in record.currencies"
                  :key="c.id"
                  size="small"
                  style="margin: 2px"
                >
                  {{ $t('newbieGift.currency.' + c.currencyType) }}:
                  {{ c.amount }}
                </a-tag>
              </span>
              <span v-else style="color: #999">-</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('newbieGift.column.operation')"
            :width="160"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-button type="text" size="small" @click="editClick(record)">
                {{ $t('newbieGift.edit') }}
              </a-button>
              <a-popconfirm
                :content="$t('newbieGift.delete.confirm')"
                @ok="record.id && deleteClick(record.id)"
              >
                <a-button type="text" status="danger" size="small">
                  {{ $t('button.delete') }}
                </a-button>
              </a-popconfirm>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="dialogVisible"
      :title="isEdit ? $t('newbieGift.editTitle') : $t('newbieGift.addTitle')"
      :width="700"
      @ok="saveClick"
      @cancel="dialogVisible = false"
    >
      <a-form :model="form" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('newbieGift.field.giftName')" required>
              <a-input
                v-model="form.giftName"
                :placeholder="$t('newbieGift.field.giftName.placeholder')"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('newbieGift.field.minLevel')">
              <a-input-number
                v-model="form.minLevel"
                :min="1"
                :max="200"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('newbieGift.field.maxLevel')">
              <a-input-number
                v-model="form.maxLevel"
                :min="1"
                :max="200"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('newbieGift.field.enabled')">
          <a-switch v-model="enabledSwitch" />
        </a-form-item>

        <!-- 物品奖励 -->
        <a-divider>{{ $t('newbieGift.column.items') }}</a-divider>
        <div
          v-for="(item, idx) in form.items"
          :key="idx"
          style="margin-bottom: 8px"
        >
          <a-row :gutter="8" align="center">
            <a-col :span="10">
              <a-input
                v-model="item.itemId"
                :placeholder="$t('newbieGift.field.itemId.placeholder')"
                type="number"
              />
            </a-col>
            <a-col :span="6">
              <a-input-number
                v-model="item.quantity"
                :min="1"
                :placeholder="$t('newbieGift.field.quantity')"
                style="width: 100%"
              />
            </a-col>
            <a-col :span="4">
              <a-button type="text" status="danger" @click="removeItem(idx)">
                {{ $t('button.delete') }}
              </a-button>
            </a-col>
          </a-row>
        </div>
        <a-button type="dashed" long @click="addItem">
          + {{ $t('newbieGift.addItem') }}
        </a-button>

        <!-- 货币奖励 -->
        <a-divider>{{ $t('newbieGift.column.currencies') }}</a-divider>
        <div
          v-for="(cur, idx) in form.currencies"
          :key="idx"
          style="margin-bottom: 8px"
        >
          <a-row :gutter="8" align="center">
            <a-col :span="8">
              <a-select v-model="cur.currencyType" style="width: 100%">
                <a-option value="meso">{{
                  $t('newbieGift.currency.meso')
                }}</a-option>
                <a-option value="cash">{{
                  $t('newbieGift.currency.cash')
                }}</a-option>
                <a-option value="credit">{{
                  $t('newbieGift.currency.credit')
                }}</a-option>
              </a-select>
            </a-col>
            <a-col :span="6">
              <a-input-number
                v-model="cur.amount"
                :min="0"
                :placeholder="$t('newbieGift.field.amount.placeholder')"
                style="width: 100%"
              />
            </a-col>
            <a-col :span="4">
              <a-button
                type="text"
                status="danger"
                @click="removeCurrency(idx)"
              >
                {{ $t('button.delete') }}
              </a-button>
            </a-col>
          </a-row>
        </div>
        <a-button type="dashed" long @click="addCurrency">
          + {{ $t('newbieGift.addCurrency') }}
        </a-button>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive, computed } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import {
    getConfigList,
    saveConfig,
    deleteConfig,
    type NewbieGiftForm,
  } from '@/api/newbieGift';
  import type { NewbieGiftState } from '@/store/modules/newbieGift/type';
  import { useI18n } from 'vue-i18n';

  const { t } = useI18n();

  const loading = ref(false);
  const tableData = ref<NewbieGiftState[]>([]);
  const dialogVisible = ref(false);
  const isEdit = ref(false);

  const form = reactive<NewbieGiftForm>({
    giftName: '',
    minLevel: 1,
    maxLevel: 200,
    enabled: 1,
    items: [],
    currencies: [],
  });

  const enabledSwitch = computed({
    get: () => form.enabled === 1,
    set: (val: boolean) => {
      form.enabled = val ? 1 : 0;
    },
  });

  async function loadData() {
    loading.value = true;
    try {
      const res = await getConfigList();
      tableData.value = res.data as any;
    } finally {
      loading.value = false;
    }
  }

  function addClick() {
    isEdit.value = false;
    form.id = undefined;
    form.giftName = '';
    form.minLevel = 1;
    form.maxLevel = 200;
    form.enabled = 1;
    form.items = [];
    form.currencies = [];
    dialogVisible.value = true;
  }

  function editClick(record: NewbieGiftState) {
    isEdit.value = true;
    form.id = record.id;
    form.giftName = record.giftName || '';
    form.minLevel = record.minLevel || 1;
    form.maxLevel = record.maxLevel || 200;
    form.enabled = record.enabled || 1;
    form.items = (record.items || []).map((i) => ({ ...i }));
    form.currencies = (record.currencies || []).map((c) => ({ ...c }));
    dialogVisible.value = true;
  }

  function addItem() {
    if (form.items) {
      form.items.push({ itemId: undefined, quantity: 1 });
    }
  }

  function removeItem(idx: number) {
    if (form.items) {
      form.items.splice(idx, 1);
    }
  }

  function addCurrency() {
    if (form.currencies) {
      form.currencies.push({ currencyType: 'meso', amount: 0 });
    }
  }

  function removeCurrency(idx: number) {
    if (form.currencies) {
      form.currencies.splice(idx, 1);
    }
  }

  async function saveClick() {
    if (!form.giftName) {
      Message.warning(t('newbieGift.field.giftName.placeholder'));
      return;
    }
    try {
      await saveConfig({ ...form });
      Message.success(t('newbieGift.save.success'));
      dialogVisible.value = false;
      await loadData();
    } catch (e: any) {
      Message.error(e?.response?.data?.msg || e?.message || 'Error');
    }
  }

  async function deleteClick(id: number) {
    try {
      await deleteConfig(id);
      Message.success(t('newbieGift.delete.success'));
      await loadData();
    } catch (e: any) {
      Message.error(e?.response?.data?.msg || e?.message || 'Error');
    }
  }

  loadData();
</script>
