<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.enhanceRule')">
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" @click="openCreate">
          {{ $t('enhanceRule.add') }}
        </a-button>
        <a-button @click="reloadClick">{{ $t('enhanceRule.reload') }}</a-button>
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
            :title="$t('enhanceRule.column.ruleName')"
            data-index="ruleName"
            :width="160"
          />
          <a-table-column
            :title="$t('enhanceRule.column.equipType')"
            data-index="equipType"
            :width="100"
          />
          <a-table-column :title="$t('enhanceRule.column.level')" :width="100">
            <template #cell="{ record }">
              {{ record.minLevel }}-{{ record.maxLevel }}
            </template>
          </a-table-column>
          <a-table-column :title="$t('enhanceRule.column.enabled')" :width="90">
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
            :title="$t('enhanceRule.column.operate')"
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
      :title="$t('enhanceRule.detail')"
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
        <a-form-item :label="$t('enhanceRule.column.ruleName')">
          <a-input v-model="form.ruleName" />
        </a-form-item>
        <a-form-item :label="$t('enhanceRule.column.equipType')">
          <a-select v-model="form.equipType">
            <a-option value="ALL">ALL</a-option>
            <a-option value="WEAPON">WEAPON</a-option>
            <a-option value="ARMOR">ARMOR</a-option>
            <a-option value="ACCESSORY">ACCESSORY</a-option>
          </a-select>
        </a-form-item>
        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-item :label="$t('enhanceRule.column.minLevel')">
              <a-input-number v-model="form.minLevel" :min="0" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('enhanceRule.column.maxLevel')">
              <a-input-number v-model="form.maxLevel" :min="0" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('enhanceRule.column.enabled')">
              <a-switch
                v-model="form.enabled"
                :checked-value="1"
                :unchecked-value="0"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('enhanceRule.column.statsJson')">
          <a-textarea
            v-model="form.statsJson"
            :auto-size="{ minRows: 8, maxRows: 16 }"
            :placeholder="statsPlaceholder"
          />
        </a-form-item>
        <a-form-item :label="$t('enhanceRule.column.remark')">
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
    EnhanceRuleRecord,
    deleteEnhanceRule,
    getEnhanceRuleList,
    reloadEnhanceRule,
    saveEnhanceRule,
  } from '@/api/enhanceRule';

  const { t } = useI18n();
  const rows = ref<EnhanceRuleRecord[]>([]);
  const drawerVisible = ref(false);
  const saving = ref(false);
  const { loading, setLoading } = useLoading(false);
  const form = reactive<EnhanceRuleRecord>({
    ruleName: '',
    equipType: 'ALL',
    minLevel: 0,
    maxLevel: 25,
    enabled: 1,
    statsJson:
      '{\n  "perLevel": { "damR": 1 },\n  "milestones": { "15": { "bdR": 10 } }\n}',
    remark: '',
  });
  const statsPlaceholder =
    '{"perLevel":{"damR":1},"milestones":{"15":{"bdR":10,"fdR":5}}}';

  const loadRows = async () => {
    setLoading(true);
    try {
      const { data } = await getEnhanceRuleList();
      rows.value = data;
    } finally {
      setLoading(false);
    }
  };

  const openCreate = () => {
    Object.assign(form, {
      id: undefined,
      ruleName: '',
      equipType: 'ALL',
      minLevel: 0,
      maxLevel: 25,
      enabled: 1,
      sortOrder: 0,
      statsJson:
        '{\n  "perLevel": { "damR": 1 },\n  "milestones": { "15": { "bdR": 10 } }\n}',
      remark: '',
    });
    drawerVisible.value = true;
  };

  const openEdit = (record: EnhanceRuleRecord) => {
    Object.assign(form, { ...record });
    drawerVisible.value = true;
  };

  const handleSave = async () => {
    saving.value = true;
    try {
      await saveEnhanceRule(form);
      await reloadEnhanceRule();
      Message.success(t('message.success'));
      drawerVisible.value = false;
      await loadRows();
    } finally {
      saving.value = false;
    }
  };

  const deleteClick = (record: EnhanceRuleRecord) => {
    if (!record.id) return;
    Modal.confirm({
      title: t('enhanceRule.delete.confirm'),
      onOk: async () => {
        await deleteEnhanceRule(record.id as number);
        await reloadEnhanceRule();
        Message.success(t('message.success'));
        await loadRows();
      },
    });
  };

  const reloadClick = async () => {
    await reloadEnhanceRule();
    Message.success(t('message.success'));
  };

  loadRows();
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px;
  }
</style>
