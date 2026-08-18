<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.equipGrowth')">
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" @click="openCreate">{{
          $t('equipGrowth.add')
        }}</a-button>
        <a-button @click="importWz">{{ $t('equipGrowth.import.wz') }}</a-button>
        <a-button @click="reloadClick">{{ $t('equipGrowth.reload') }}</a-button>
        <a-input-search
          v-model="keyword"
          :placeholder="$t('equipGrowth.search')"
          style="width: 220px"
          allow-clear
        />
      </a-space>
      <a-alert type="info" style="margin-bottom: 12px">
        {{ $t('equipGrowth.hint.maxVsSeg') }}
      </a-alert>
      <a-table
        :loading="loading"
        :data="filteredRows"
        column-resizable
        :pagination="{ pageSize: 20 }"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column
            :title="$t('equipGrowth.column.itemId')"
            data-index="itemId"
            :width="100"
          />
          <a-table-column
            :title="$t('equipGrowth.column.itemName')"
            data-index="itemName"
            :width="160"
          />
          <a-table-column
            :title="$t('equipGrowth.column.maxLevel')"
            data-index="maxLevel"
            :width="100"
          />
          <a-table-column
            :title="$t('equipGrowth.column.levelCount')"
            data-index="levelCount"
            :width="90"
          />
          <a-table-column
            :title="$t('equipGrowth.column.source')"
            data-index="source"
            :width="90"
          />
          <a-table-column :title="$t('equipGrowth.column.enabled')" :width="90">
            <template #cell="{ record }">
              <a-switch
                v-model="record.enabled"
                :checked-value="1"
                :unchecked-value="0"
                @change="() => toggleEnabled(record)"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('equipGrowth.column.operate')"
            :width="180"
            align="center"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" size="mini" @click="openEdit(record)">
                  {{ $t('equipGrowth.edit') }}
                </a-button>
                <a-button
                  v-if="record.id"
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
      :width="780"
      unmount-on-close
      :ok-text="$t('button.save')"
      @cancel="drawerVisible = false"
      @ok="saveClick"
    >
      <template #title>{{ $t('equipGrowth.detail.title') }}</template>
      <a-form :model="editing" layout="vertical">
        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-item :label="$t('equipGrowth.column.itemId')" required>
              <a-input-number
                v-model="editing.itemId"
                :disabled="!!editing.id"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('equipGrowth.column.itemName')">
              <a-input v-model="editing.itemName" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('equipGrowth.column.maxLevel')">
              <a-input-number
                v-model="editing.maxLevel"
                :disabled="true"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-alert type="info" style="margin-bottom: 12px">
          {{ $t('equipGrowth.hint.skillStack') }}
        </a-alert>

        <a-tabs>
          <a-tab-pane key="levels" :title="$t('equipGrowth.tab.levels')">
            <a-space style="margin-bottom: 8px">
              <a-button size="mini" @click="addLevel">{{
                $t('equipGrowth.level.add')
              }}</a-button>
            </a-space>
            <div
              v-for="(lv, idx) in levelsModel.levels"
              :key="idx"
              class="lv-block"
            >
              <a-space>
                <span>{{ lv.level }}{{ $t('equipGrowth.level.n') }}</span>
                <a-switch v-model="lv.enabled" />
                <a-button size="mini" status="danger" @click="removeLevel(idx)">
                  {{ $t('button.delete') }}
                </a-button>
              </a-space>
              <a-row :gutter="8" style="margin-top: 8px">
                <a-col v-for="f in statFields" :key="f" :span="6">
                  <a-form-item :label="f">
                    <a-input-number
                      v-model="(lv.stats as any)[f]"
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
              </a-row>
              <div class="skill-block">
                <a-space style="margin-bottom: 6px">
                  <span>{{ $t('equipGrowth.level.skills') }}</span>
                  <a-button size="mini" @click="addSkill(lv)">{{
                    $t('equipGrowth.skill.add')
                  }}</a-button>
                </a-space>
                <div
                  v-for="(sk, sidx) in lv.skills || []"
                  :key="sidx"
                  class="skill-row"
                >
                  <a-space>
                    <a-input-number
                      v-model="sk.id"
                      :placeholder="$t('equipGrowth.skill.id')"
                      style="width: 140px"
                    />
                    <a-input-number
                      v-model="sk.level"
                      :placeholder="$t('equipGrowth.skill.level')"
                      style="width: 100px"
                    />
                    <a-button
                      size="mini"
                      status="danger"
                      @click="removeSkill(lv, sidx)"
                    >
                      {{ $t('button.delete') }}
                    </a-button>
                  </a-space>
                </div>
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="preview" :title="$t('equipGrowth.tab.preview')">
            <a-button
              size="mini"
              style="margin-bottom: 8px"
              @click="previewClick"
            >
              {{ $t('equipGrowth.tab.preview') }}
            </a-button>
            <pre class="tip-preview">{{ tipPreview }}</pre>
          </a-tab-pane>
        </a-tabs>
      </a-form>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
  import { computed, reactive, ref, watch } from 'vue';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import {
    deleteEquipGrowth,
    getEquipGrowthList,
    initEquipGrowthFromWz,
    parseLevelsJson,
    previewEquipGrowthTip,
    reloadEquipGrowth,
    saveEquipGrowth,
    stringifyLevelsJson,
    type EquipGrowthRecord,
    type GrowthLevel,
    type GrowthLevelsV1,
  } from '@/api/equipGrowth';

  const { t } = useI18n();
  const loading = ref(false);
  const rows = ref<EquipGrowthRecord[]>([]);
  const keyword = ref('');
  const drawerVisible = ref(false);
  const editing = reactive<EquipGrowthRecord>({ itemId: 0, enabled: 1 });
  const levelsModel = reactive<GrowthLevelsV1>({
    schemaVersion: 1,
    levels: [],
  });
  const tipPreview = ref('');
  const statFields = [
    'str',
    'dex',
    'int',
    'luk',
    'pad',
    'mad',
    'mhp',
    'mmp',
    'pdd',
    'mdd',
    'acc',
    'eva',
    'speed',
    'jump',
  ];

  const filteredRows = computed(() => {
    const k = keyword.value.trim().toLowerCase();
    if (!k) return rows.value;
    return rows.value.filter(
      (r) =>
        String(r.itemId).includes(k) ||
        (r.itemName || '').toLowerCase().includes(k)
    );
  });

  function syncMaxLevelFromSegments() {
    const maxSeg = levelsModel.levels.reduce(
      (m, l) => Math.max(m, l.level || 0),
      0
    );
    editing.maxLevel = maxSeg > 0 ? maxSeg + 1 : 0;
  }

  watch(
    () => levelsModel.levels.map((l) => l.level),
    () => syncMaxLevelFromSegments(),
    { deep: true }
  );

  async function loadRows() {
    loading.value = true;
    try {
      const res: any = await getEquipGrowthList();
      rows.value = res.data || res || [];
    } finally {
      loading.value = false;
    }
  }

  function ensureSkills(lv: GrowthLevel) {
    if (!lv.skills) lv.skills = [];
  }

  function openCreate() {
    Object.assign(editing, {
      id: undefined,
      itemId: 0,
      itemName: '',
      enabled: 1,
      maxLevel: 0,
      sortOrder: 0,
      remark: '',
      levelsJson: '{"schemaVersion":1,"levels":[]}',
      skillsJson: '[]',
      source: 'DB',
    });
    Object.assign(levelsModel, parseLevelsJson(editing.levelsJson));
    tipPreview.value = '';
    drawerVisible.value = true;
  }

  function openEdit(record: EquipGrowthRecord) {
    Object.assign(editing, { ...record });
    const parsed = parseLevelsJson(record.levelsJson);
    parsed.levels.forEach(ensureSkills);
    Object.assign(levelsModel, parsed);
    syncMaxLevelFromSegments();
    tipPreview.value = record.tipPreview || '';
    drawerVisible.value = true;
  }

  function addLevel() {
    const next =
      (levelsModel.levels.reduce((m, l) => Math.max(m, l.level || 0), 0) || 0) +
      1;
    levelsModel.levels.push({
      level: next,
      enabled: true,
      stats: {},
      skills: [],
    });
    syncMaxLevelFromSegments();
  }

  function removeLevel(idx: number) {
    levelsModel.levels.splice(idx, 1);
    syncMaxLevelFromSegments();
  }

  function addSkill(lv: GrowthLevel) {
    ensureSkills(lv);
    const { skills } = lv;
    if (skills) {
      skills.push({ id: 0, level: 1 });
    }
  }

  function removeSkill(lv: GrowthLevel, sidx: number) {
    ensureSkills(lv);
    const { skills } = lv;
    if (skills) {
      skills.splice(sidx, 1);
    }
  }

  async function saveClick() {
    levelsModel.levels.forEach(ensureSkills);
    syncMaxLevelFromSegments();
    editing.levelsJson = stringifyLevelsJson(levelsModel);
    editing.skillsJson = '[]';
    await saveEquipGrowth({ ...editing });
    Message.success(t('equipGrowth.save.success'));
    drawerVisible.value = false;
    await loadRows();
  }

  async function toggleEnabled(record: EquipGrowthRecord) {
    await saveEquipGrowth({ ...record });
    Message.success(t('equipGrowth.save.success'));
  }

  function deleteClick(record: EquipGrowthRecord) {
    Modal.confirm({
      title: t('equipGrowth.delete.confirm'),
      onOk: async () => {
        if (record.id) await deleteEquipGrowth(record.id);
        await loadRows();
      },
    });
  }

  async function reloadClick() {
    await reloadEquipGrowth();
    Message.success(t('equipGrowth.reload.success'));
    await loadRows();
  }

  const importWz = async () => {
    Modal.confirm({
      title: t('equipGrowth.import.confirm'),
      onOk: async () => {
        const res: any = await initEquipGrowthFromWz({ mode: 'NEW_ONLY' });
        const imported = res?.data?.imported ?? res?.imported ?? 0;
        const skipped = res?.data?.skipped ?? res?.skipped ?? 0;
        Message.success(t('equipGrowth.import.success', { imported, skipped }));
        await loadRows();
      },
    });
  };

  async function previewClick() {
    if (!editing.itemId) return;
    levelsModel.levels.forEach(ensureSkills);
    syncMaxLevelFromSegments();
    editing.levelsJson = stringifyLevelsJson(levelsModel);
    editing.skillsJson = '[]';
    await saveEquipGrowth({ ...editing });
    const res: any = await previewEquipGrowthTip(editing.itemId);
    tipPreview.value = res?.data?.text || res?.text || '';
  }

  loadRows();
</script>

<style scoped>
  .lv-block {
    border: 1px solid var(--color-border);
    border-radius: 4px;
    padding: 8px;
    margin-bottom: 8px;
  }
  .skill-block {
    margin-top: 8px;
    padding-top: 8px;
    border-top: 1px dashed var(--color-border);
  }
  .skill-row {
    margin-bottom: 6px;
  }
  .tip-preview {
    white-space: pre-wrap;
    background: var(--color-fill-2);
    padding: 12px;
    min-height: 160px;
  }
</style>
