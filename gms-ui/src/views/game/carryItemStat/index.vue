<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.carryItemStat')">
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" @click="openCreate">
          {{ $t('carryItemStat.add') }}
        </a-button>
        <a-button @click="reloadClick">
          {{ $t('carryItemStat.reload') }}
        </a-button>
      </a-space>
      <a-table
        :loading="loading"
        :data="rows"
        column-resizable
        :pagination="{ pageSize: 20 }"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column
            :title="$t('carryItemStat.column.itemId')"
            data-index="itemId"
            :width="100"
          />
          <a-table-column
            :title="$t('carryItemStat.column.itemName')"
            data-index="itemName"
            :width="160"
          />
          <a-table-column
            :title="$t('carryItemStat.column.requireEquipped')"
            :width="120"
          >
            <template #cell="{ record }">
              {{
                record.requireEquipped
                  ? $t('carryItemStat.require.yes')
                  : $t('carryItemStat.require.no')
              }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('carryItemStat.column.enabled')"
            :width="90"
          >
            <template #cell="{ record }">
              <a-tag :color="record.enabled ? 'green' : 'gray'">
                {{
                  record.enabled
                    ? $t('setItem.enabled.yes')
                    : $t('setItem.enabled.no')
                }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('carryItemStat.column.operate')"
            :width="140"
            align="center"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" size="mini" @click="openEdit(record)">
                  {{ $t('setItem.edit') }}
                </a-button>
                <a-button
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

    <a-drawer
      :visible="drawerVisible"
      :width="640"
      :title="$t('carryItemStat.detail')"
      unmount-on-close
      @cancel="drawerVisible = false"
    >
      <template #footer>
        <a-space>
          <a-button @click="drawerVisible = false">
            {{ $t('button.cancel') }}
          </a-button>
          <a-button type="primary" :loading="saving" @click="handleSave">
            {{ $t('button.save') }}
          </a-button>
        </a-space>
      </template>
      <a-form :model="form" layout="vertical">
        <a-row :gutter="12">
          <a-col :span="10">
            <a-form-item :label="$t('carryItemStat.column.itemId')">
              <a-input-number
                v-model="form.itemId"
                :min="1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="14">
            <a-form-item :label="$t('carryItemStat.column.itemName')">
              <a-input v-model="form.itemName" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item :label="$t('carryItemStat.column.requireEquipped')">
              <a-switch
                v-model="form.requireEquipped"
                :checked-value="1"
                :unchecked-value="0"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('carryItemStat.column.enabled')">
              <a-switch
                v-model="form.enabled"
                :checked-value="1"
                :unchecked-value="0"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('carryItemStat.column.statsJson')">
          <a-textarea
            v-model="form.statsJson"
            :auto-size="{ minRows: 8, maxRows: 16 }"
            :placeholder="statsPlaceholder"
          />
        </a-form-item>
        <a-form-item :label="$t('carryItemStat.column.remark')">
          <a-input v-model="form.remark" />
        </a-form-item>
      </a-form>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
  import { reactive, ref } from 'vue';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import {
    CarryItemStatRecord,
    deleteCarryItemStat,
    getCarryItemStatList,
    reloadCarryItemStat,
    saveCarryItemStat,
  } from '@/api/carryItemStat';

  const { t } = useI18n();
  const rows = ref<CarryItemStatRecord[]>([]);
  const drawerVisible = ref(false);
  const saving = ref(false);
  const { loading, setLoading } = useLoading(false);
  const form = reactive<CarryItemStatRecord>({
    itemId: 0,
    itemName: '',
    enabled: 1,
    requireEquipped: 0,
    statsJson: '{\n  "combatStats": { "damR": 5, "fdR": 3 }\n}',
    remark: '',
  });
  const statsPlaceholder = '{"combatStats":{"damR":5,"bdR":10,"fdR":5}}';

  const loadRows = async () => {
    setLoading(true);
    try {
      const { data } = await getCarryItemStatList();
      rows.value = data;
    } finally {
      setLoading(false);
    }
  };

  const openCreate = () => {
    Object.assign(form, {
      id: undefined,
      itemId: 0,
      itemName: '',
      enabled: 1,
      requireEquipped: 0,
      statsJson: '{\n  "combatStats": { "damR": 5, "fdR": 3 }\n}',
      remark: '',
    });
    drawerVisible.value = true;
  };

  const openEdit = (record: CarryItemStatRecord) => {
    Object.assign(form, { ...record });
    drawerVisible.value = true;
  };

  const handleSave = async () => {
    saving.value = true;
    try {
      await saveCarryItemStat(form);
      await reloadCarryItemStat();
      Message.success(t('message.success'));
      drawerVisible.value = false;
      await loadRows();
    } finally {
      saving.value = false;
    }
  };

  const deleteClick = (record: CarryItemStatRecord) => {
    if (!record.id) return;
    Modal.confirm({
      title: t('carryItemStat.delete.confirm'),
      onOk: async () => {
        await deleteCarryItemStat(record.id as number);
        await reloadCarryItemStat();
        Message.success(t('message.success'));
        await loadRows();
      },
    });
  };

  const reloadClick = async () => {
    await reloadCarryItemStat();
    Message.success(t('message.success'));
  };

  loadRows();
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px;
  }
</style>
