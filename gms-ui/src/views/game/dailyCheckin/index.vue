<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.dailyCheckin')">
      <a-alert type="info" style="margin-bottom: 12px">
        {{ $t('dailyCheckin.hint') }}
      </a-alert>
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" :loading="savingAll" @click="saveAllClick">
          {{ $t('dailyCheckin.saveAll') }}
        </a-button>
        <a-button :loading="reloading" @click="reloadClick">
          {{ $t('dailyCheckin.reload') }}
        </a-button>
      </a-space>
      <a-table
        row-key="day"
        :loading="loading"
        :data="rows"
        column-resizable
        :pagination="false"
        :scroll="{ x: 1600 }"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column
            :title="$t('dailyCheckin.column.day')"
            data-index="day"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('dailyCheckin.column.icon')"
            :width="64"
            align="center"
          >
            <template #cell="{ record }">
              <img
                v-if="record.iconItemId"
                :src="getIconUrl('item', record.iconItemId)"
                alt=""
                style="width: 32px; height: 32px"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.iconItemId')"
            :width="120"
          >
            <template #cell="{ record }">
              <a-input-number
                v-model="record.iconItemId"
                :min="0"
                hide-button
                style="width: 100%"
              />
            </template>
          </a-table-column>
          <a-table-column :title="$t('dailyCheckin.column.mesos')" :width="110">
            <template #cell="{ record }">
              <a-input-number
                v-model="record.mesos"
                :min="0"
                hide-button
                style="width: 100%"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.itemId')"
            :width="120"
          >
            <template #cell="{ record }">
              <a-input-number
                v-model="record.itemId"
                :min="0"
                hide-button
                style="width: 100%"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.itemQty')"
            :width="90"
          >
            <template #cell="{ record }">
              <a-input-number
                v-model="record.itemQty"
                :min="0"
                hide-button
                style="width: 100%"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.expireDays')"
            :width="90"
          >
            <template #cell="{ record }">
              <a-input-number
                v-model="record.expireDays"
                :min="0"
                hide-button
                style="width: 100%"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.item2Id')"
            :width="120"
          >
            <template #cell="{ record }">
              <a-input-number
                v-model="record.item2Id"
                :min="0"
                hide-button
                style="width: 100%"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.item2Qty')"
            :width="90"
          >
            <template #cell="{ record }">
              <a-input-number
                v-model="record.item2Qty"
                :min="0"
                hide-button
                style="width: 100%"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.item2Expire')"
            :width="90"
          >
            <template #cell="{ record }">
              <a-input-number
                v-model="record.item2Expire"
                :min="0"
                hide-button
                style="width: 100%"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.slotType')"
            :width="110"
          >
            <template #cell="{ record }">
              <a-select
                v-model="record.slotType"
                :options="slotOptions"
                style="width: 100%"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.slotCount')"
            :width="90"
          >
            <template #cell="{ record }">
              <a-input-number
                v-model="record.slotCount"
                :min="0"
                hide-button
                style="width: 100%"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.remark')"
            :width="140"
          >
            <template #cell="{ record }">
              <a-input v-model="record.remark" allow-clear />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('dailyCheckin.column.operate')"
            :width="90"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-button
                type="text"
                size="mini"
                :loading="savingDay === record.day"
                @click="saveRow(record)"
              >
                {{ $t('dailyCheckin.saveRow') }}
              </a-button>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import {
    DailyCheckinReward,
    getDailyCheckinList,
    reloadDailyCheckin,
    saveAllDailyCheckin,
    saveDailyCheckin,
  } from '@/api/dailyCheckin';
  import { getIconUrl } from '@/utils/mapleStoryAPI';

  const { t } = useI18n();
  const loading = ref(false);
  const savingAll = ref(false);
  const reloading = ref(false);
  const savingDay = ref<number | null>(null);
  const rows = ref<DailyCheckinReward[]>([]);

  const slotOptions = computed(() => [
    { value: 0, label: t('dailyCheckin.slot.none') },
    { value: 1, label: t('dailyCheckin.slot.equip') },
    { value: 2, label: t('dailyCheckin.slot.use') },
    { value: 3, label: t('dailyCheckin.slot.setup') },
    { value: 4, label: t('dailyCheckin.slot.etc') },
    { value: 5, label: t('dailyCheckin.slot.cash') },
  ]);

  async function loadRows() {
    loading.value = true;
    try {
      const res = await getDailyCheckinList();
      rows.value = (res.data || []) as DailyCheckinReward[];
    } finally {
      loading.value = false;
    }
  }

  async function saveRow(record: DailyCheckinReward) {
    savingDay.value = record.day;
    try {
      await saveDailyCheckin(record);
      Message.success(t('dailyCheckin.save.success'));
      await loadRows();
    } finally {
      savingDay.value = null;
    }
  }

  async function saveAllClick() {
    savingAll.value = true;
    try {
      await saveAllDailyCheckin(rows.value);
      Message.success(t('dailyCheckin.save.success'));
      await loadRows();
    } finally {
      savingAll.value = false;
    }
  }

  async function reloadClick() {
    reloading.value = true;
    try {
      await reloadDailyCheckin();
      Message.success(t('dailyCheckin.reload.success'));
      await loadRows();
    } finally {
      reloading.value = false;
    }
  }

  onMounted(loadRows);
</script>
