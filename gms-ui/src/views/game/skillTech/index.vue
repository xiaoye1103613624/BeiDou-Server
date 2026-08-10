<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.skillTech')">
      <a-alert type="info" style="margin-bottom: 12px">
        {{ $t('skillTech.hint') }}
      </a-alert>
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" @click="openCreate">
          {{ $t('skillTech.add') }}
        </a-button>
        <a-button @click="reloadClick">{{ $t('skillTech.reload') }}</a-button>
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
            :title="$t('skillTech.column.skillId')"
            data-index="skillId"
            :width="110"
          />
          <a-table-column
            :title="$t('skillTech.column.skillName')"
            data-index="skillName"
            :width="140"
          />
          <a-table-column
            :title="$t('skillTech.column.spMaxLevel')"
            data-index="spMaxLevel"
            :width="100"
          />
          <a-table-column
            :title="$t('skillTech.column.effectMaxLevel')"
            data-index="effectMaxLevel"
            :width="100"
          />
          <a-table-column
            :title="$t('skillTech.column.enabled')"
            :width="80"
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
            :title="$t('skillTech.column.clientSynced')"
            :width="100"
          >
            <template #cell="{ record }">
              <a-tag :color="record.clientSynced ? 'green' : 'orange'">
                {{ record.clientSynced ? 'OK' : '待同步' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('skillTech.column.operate')"
            :width="220"
            align="center"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" size="mini" @click="openEdit(record)">
                  {{ $t('setItem.edit') }}
                </a-button>
                <a-button type="text" size="mini" @click="syncClick(record)">
                  {{ $t('skillTech.sync') }}
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
      :width="680"
      :title="$t('skillTech.detail')"
      unmount-on-close
      @cancel="drawerVisible = false"
    >
      <template #footer>
        <a-space>
          <a-button @click="drawerVisible = false">
            {{ $t('button.cancel') }}
          </a-button>
          <a-button @click="previewClick">{{ $t('skillTech.preview') }}</a-button>
          <a-button type="primary" :loading="saving" @click="handleSave">
            {{ $t('button.save') }}
          </a-button>
        </a-space>
      </template>
      <a-form :model="form" layout="vertical">
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item :label="$t('skillTech.column.skillId')">
              <a-input-number
                v-model="form.skillId"
                :min="1000"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('skillTech.column.skillName')">
              <a-input v-model="form.skillName" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-item :label="$t('skillTech.column.spMaxLevel')">
              <a-input-number v-model="form.spMaxLevel" :min="1" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('skillTech.column.effectMaxLevel')">
              <a-input-number v-model="form.effectMaxLevel" :min="1" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('skillTech.column.enabled')">
              <a-switch
                v-model="form.enabled"
                :checked-value="1"
                :unchecked-value="0"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('skillTech.column.levelsJson')">
          <a-textarea
            v-model="form.levelsJson"
            :auto-size="{ minRows: 10, maxRows: 20 }"
            :placeholder="$t('skillTech.levelsPlaceholder')"
          />
        </a-form-item>
        <a-form-item :label="$t('skillTech.column.remark')">
          <a-input v-model="form.remark" />
        </a-form-item>
        <a-typography-paragraph v-if="previewText" type="secondary">
          {{ previewText }}
        </a-typography-paragraph>
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
    SkillTechRecord,
    deleteSkillTech,
    getSkillTechList,
    previewSkillTech,
    reloadSkillTech,
    saveSkillTech,
    syncSkillTechClient,
  } from '@/api/skillTech';

  const { t } = useI18n();
  const rows = ref<SkillTechRecord[]>([]);
  const drawerVisible = ref(false);
  const saving = ref(false);
  const previewText = ref('');
  const { loading, setLoading } = useLoading(false);
  const form = reactive<SkillTechRecord>({
    skillId: 1121008,
    skillName: '',
    spMaxLevel: 30,
    effectMaxLevel: 35,
    levelsJson:
      '{\n  "31": { "damage": 155, "mpCon": 20 },\n  "35": { "damage": 175, "mpCon": 25 }\n}',
    enabled: 1,
    remark: '',
  });

  const loadRows = async () => {
    setLoading(true);
    try {
      const { data } = await getSkillTechList();
      rows.value = data;
    } finally {
      setLoading(false);
    }
  };

  const openCreate = () => {
    Object.assign(form, {
      id: undefined,
      skillId: 1121008,
      skillName: '',
      spMaxLevel: 30,
      effectMaxLevel: 35,
      levelsJson:
        '{\n  "31": { "damage": 155, "mpCon": 20 },\n  "35": { "damage": 175, "mpCon": 25 }\n}',
      enabled: 1,
      remark: '',
    });
    previewText.value = '';
    drawerVisible.value = true;
  };

  const openEdit = (record: SkillTechRecord) => {
    Object.assign(form, { ...record });
    previewText.value = '';
    drawerVisible.value = true;
  };

  const previewClick = async () => {
    if (!form.skillId) {
      Message.warning('skillId required');
      return;
    }
    const { data } = await previewSkillTech(form.skillId);
    previewText.value = JSON.stringify(data, null, 2);
    if (data?.name && !form.skillName) {
      form.skillName = data.name;
    }
    if (data?.spMaxLevel && !form.id) {
      form.spMaxLevel = data.spMaxLevel;
    }
  };

  const handleSave = async () => {
    saving.value = true;
    try {
      await saveSkillTech({ ...form });
      Message.success(t('message.success'));
      drawerVisible.value = false;
      await loadRows();
    } finally {
      saving.value = false;
    }
  };

  const deleteClick = (record: SkillTechRecord) => {
    Modal.confirm({
      title: t('skillTech.delete.confirm'),
      onOk: async () => {
        if (record.id != null) {
          await deleteSkillTech(record.id);
          Message.success(t('message.success'));
          await loadRows();
        }
      },
    });
  };

  const reloadClick = async () => {
    await reloadSkillTech();
    Message.success(t('message.success'));
    await loadRows();
  };

  const syncClick = async (record: SkillTechRecord) => {
    const { data } = await syncSkillTechClient(record.skillId);
    Message.info(data?.message || JSON.stringify(data));
    await loadRows();
  };

  loadRows();
</script>
