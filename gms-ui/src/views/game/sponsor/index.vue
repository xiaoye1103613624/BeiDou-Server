<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.sponsor')">
      <a-alert style="margin-bottom: 12px" type="info" :show-icon="true">
        {{ $t('sponsor.tip') }}
      </a-alert>
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" @click="openCreate">
          {{ $t('sponsor.add') }}
        </a-button>
        <a-button @click="loadRows">{{ $t('button.search') }}</a-button>
      </a-space>
      <a-table
        row-key="id"
        :loading="loading"
        :data="rows"
        column-resizable
        :pagination="{ pageSize: 20 }"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column
            title="ID"
            data-index="id"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('sponsor.column.name')"
            data-index="name"
            :width="180"
          />
          <a-table-column
            :title="$t('sponsor.column.amount')"
            data-index="amount"
            :width="110"
            align="center"
          />
          <a-table-column
            :title="$t('sponsor.column.sortOrder')"
            data-index="sortOrder"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('sponsor.column.enabled')"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-switch
                :model-value="record.enabled === 1"
                @change="() => toggleClick(record)"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.operate')"
            :width="200"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" size="mini" @click="openEdit(record)">
                  {{ $t('button.edit') }}
                </a-button>
                <a-button type="text" size="mini" @click="openRewards(record)">
                  {{ $t('sponsor.rewards') }}
                </a-button>
                <a-popconfirm
                  type="error"
                  :content="$t('sponsor.delete.confirm')"
                  @ok="deleteClick(record)"
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

    <a-modal
      v-model:visible="formVisible"
      :title="
        form.id
          ? $t('sponsor.form.title.edit')
          : $t('sponsor.form.title.create')
      "
      :ok-loading="saving"
      :on-before-ok="handleSave"
      unmount-on-close
    >
      <a-form :model="form" layout="vertical">
        <a-form-item :label="$t('sponsor.column.name')">
          <a-input v-model="form.name" placeholder="赞助满1888" />
        </a-form-item>
        <a-form-item :label="$t('sponsor.column.amount')">
          <a-input-number v-model="form.amount" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('sponsor.column.sortOrder')">
          <a-input-number
            v-model="form.sortOrder"
            :min="0"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('sponsor.column.enabled')">
          <a-switch
            v-model="form.enabled"
            :checked-value="1"
            :unchecked-value="0"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:visible="rewardVisible"
      :title="rewardTitle"
      :width="1180"
      :footer="false"
      unmount-on-close
    >
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" @click="insertReward">
          {{ $t('sponsor.reward.add') }}
        </a-button>
      </a-space>
      <a-table
        row-key="id"
        :loading="rewardLoading"
        :data="rewardRows"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column
            title="ID"
            data-index="id"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('sponsor.column.type')"
            :width="130"
            align="center"
          >
            <template #cell="{ record }">
              <a-select
                v-if="editRewardId === record.id"
                v-model="record.type"
                style="width: 120px"
                @change="() => onRewardTypeOrItemChange(record)"
              >
                <a-option value="item">{{ $t('sponsor.type.item') }}</a-option>
                <a-option value="nx">{{ $t('sponsor.type.nx') }}</a-option>
                <a-option value="maple">{{
                  $t('sponsor.type.maple')
                }}</a-option>
                <a-option value="meso">{{ $t('sponsor.type.meso') }}</a-option>
                <a-option value="skill_group">{{
                  $t('sponsor.type.skill_group')
                }}</a-option>
              </a-select>
              <span v-else>{{ typeLabel(record.type) }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.pickMode')"
            :width="120"
            align="center"
          >
            <template #cell="{ record }">
              <template v-if="record.type === 'skill_group'">
                <a-select
                  v-if="editRewardId === record.id"
                  v-model="record.pickMode"
                  style="width: 110px"
                  @change="() => onPickModeChange(record)"
                >
                  <a-option value="ONE">{{
                    $t('sponsor.pickMode.ONE')
                  }}</a-option>
                  <a-option value="MULTI">{{
                    $t('sponsor.pickMode.MULTI')
                  }}</a-option>
                  <a-option value="ALL">{{
                    $t('sponsor.pickMode.ALL')
                  }}</a-option>
                </a-select>
                <span v-else>{{ pickModeLabel(record.pickMode) }}</span>
              </template>
              <span v-else>-</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.itemId')"
            :width="120"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="editRewardId === record.id && record.type === 'item'"
                v-model="record.itemId"
                :min="0"
                @change="() => onRewardTypeOrItemChange(record)"
              />
              <span v-else>{{
                record.type === 'item' ? record.itemId : '-'
              }}</span>
            </template>
          </a-table-column>
          <a-table-column title="图标" :width="70" align="center">
            <template #cell="{ record }">
              <img
                v-if="record.type === 'item' && record.itemId"
                :src="getIconUrl('item', record.itemId)"
                alt=""
                @error="onItemIconError"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.qty')"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="
                  editRewardId === record.id &&
                  (record.type !== 'skill_group' ||
                    record.pickMode === 'MULTI')
                "
                v-model="record.qty"
                :min="1"
              />
              <span v-else-if="record.type === 'skill_group'">
                <template v-if="record.pickMode === 'MULTI'">{{
                  record.qty
                }}</template>
                <template v-else>-</template>
              </span>
              <span v-else>{{ record.qty }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.statMode')"
            :width="180"
            align="center"
          >
            <template #cell="{ record }">
              <template
                v-if="
                  record.type === 'item' && isEquipItemId(record.itemId || 0)
                "
              >
                <a-radio-group
                  v-if="editRewardId === record.id"
                  v-model="record.statMode"
                  type="button"
                  size="mini"
                  @change="() => onStatModeChange(record)"
                >
                  <a-radio value="default">{{
                    $t('sponsor.statMode.default')
                  }}</a-radio>
                  <a-radio value="custom">{{
                    $t('sponsor.statMode.custom')
                  }}</a-radio>
                </a-radio-group>
                <span v-else>{{
                  record.statMode === 'custom'
                    ? $t('sponsor.statMode.custom')
                    : $t('sponsor.statMode.default')
                }}</span>
              </template>
              <span v-else>-</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.operate')"
            :width="220"
            align="center"
          >
            <template #cell="{ record }">
              <template v-if="editRewardId !== record.id">
                <a-button
                  type="text"
                  size="mini"
                  @click="beginEditReward(record)"
                >
                  {{ $t('button.edit') }}
                </a-button>
                <a-button
                  v-if="record.type === 'skill_group' && record.id"
                  type="text"
                  size="mini"
                  @click="openSkillOptions(record)"
                >
                  {{ $t('sponsor.skill.options') }}
                </a-button>
                <a-popconfirm
                  type="error"
                  :content="$t('sponsor.reward.delete.confirm')"
                  @ok="deleteRewardClick(record)"
                >
                  <a-button type="text" size="mini" status="danger">
                    {{ $t('button.delete') }}
                  </a-button>
                </a-popconfirm>
              </template>
              <template v-else>
                <a-button
                  type="text"
                  size="mini"
                  @click="saveRewardClick(record)"
                >
                  {{ $t('button.save') }}
                </a-button>
                <a-button type="text" size="mini" @click="cancelRewardEdit">
                  {{ $t('button.cancel') }}
                </a-button>
              </template>
            </template>
          </a-table-column>
        </template>
      </a-table>

      <a-alert
        v-if="
          editingSkillGroupRecord &&
          editingSkillGroupRecord.pickMode === 'MULTI' &&
          editRewardId === editingSkillGroupRecord.id
        "
        style="margin-top: 12px"
        type="info"
        :show-icon="true"
      >
        {{ $t('sponsor.skill.multiQtyTip') }}
      </a-alert>

      <a-card
        v-if="
          editingEquipRecord &&
          editingEquipRecord.statMode === 'custom' &&
          editRewardId === editingEquipRecord.id
        "
        style="margin-top: 16px"
        :title="$t('sponsor.statMode.custom')"
        size="small"
      >
        <a-alert type="info" style="margin-bottom: 12px" :show-icon="true">
          {{ $t('sponsor.stat.tip') }}
        </a-alert>
        <a-space style="margin-bottom: 12px">
          <a-button
            size="small"
            :loading="loadingTemplate"
            @click="loadWzTemplate"
          >
            {{ $t('sponsor.stat.loadTemplate') }}
          </a-button>
          <span style="color: var(--color-text-3)">
            {{ $t('sponsor.stat.preview') }}：{{
              formatStatsPreview(editStats)
            }}
          </span>
        </a-space>
        <a-row :gutter="12">
          <a-col
            v-for="field in STAT_FIELDS"
            :key="field.key"
            :span="6"
            style="margin-bottom: 8px"
          >
            <div style="font-size: 12px; margin-bottom: 4px">
              {{ $t(field.labelKey) }}
            </div>
            <a-input-number
              v-model="editStats[field.key]"
              :min="0"
              :max="32767"
              style="width: 100%"
              allow-clear
            />
          </a-col>
        </a-row>
      </a-card>
    </a-modal>

    <a-modal
      v-model:visible="skillOptVisible"
      :title="skillOptTitle"
      :width="900"
      :footer="false"
      unmount-on-close
    >
      <a-alert style="margin-bottom: 12px" type="info" :show-icon="true">
        {{ $t('sponsor.skill.levelTip') }}；{{ $t('sponsor.skill.keyTip') }}
      </a-alert>
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" @click="insertSkillOption">
          {{ $t('sponsor.skill.add') }}
        </a-button>
      </a-space>
      <a-table
        row-key="id"
        :loading="skillOptLoading"
        :data="skillOptRows"
        :pagination="false"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column title="ID" data-index="id" :width="70" align="center" />
          <a-table-column
            :title="$t('sponsor.column.skillId')"
            :width="120"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="editSkillOptId === record.id"
                v-model="record.skillId"
                :min="1"
              />
              <span v-else>{{ record.skillId }}</span>
            </template>
          </a-table-column>
          <a-table-column title="名称" :width="160">
            <template #cell="{ record }">
              <span>{{ skillNameCache[record.skillId] || '-' }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.skillLevel')"
            :width="110"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="editSkillOptId === record.id"
                v-model="record.skillLevel"
                :min="0"
              />
              <span v-else>{{
                record.skillLevel === 0 ? '最大' : record.skillLevel
              }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.defaultKey')"
            :width="110"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="editSkillOptId === record.id"
                v-model="record.defaultKey"
                :min="0"
              />
              <span v-else>{{
                record.defaultKey === 0 ? '自动' : record.defaultKey
              }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.sortOrder')"
            :width="90"
            align="center"
          >
            <template #cell="{ record }">
              <a-input-number
                v-if="editSkillOptId === record.id"
                v-model="record.sortOrder"
                :min="0"
              />
              <span v-else>{{ record.sortOrder }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sponsor.column.operate')"
            :width="200"
            align="center"
          >
            <template #cell="{ record }">
              <template v-if="editSkillOptId !== record.id">
                <a-button
                  type="text"
                  size="mini"
                  @click="beginEditSkillOpt(record)"
                >
                  {{ $t('button.edit') }}
                </a-button>
                <a-button
                  type="text"
                  size="mini"
                  @click="lookupSkillName(record.skillId)"
                >
                  {{ $t('sponsor.skill.lookup') }}
                </a-button>
                <a-popconfirm
                  type="error"
                  content="确认删除该技能选项？"
                  @ok="deleteSkillOptClick(record)"
                >
                  <a-button type="text" size="mini" status="danger">
                    {{ $t('button.delete') }}
                  </a-button>
                </a-popconfirm>
              </template>
              <template v-else>
                <a-button
                  type="text"
                  size="mini"
                  @click="saveSkillOptClick(record)"
                >
                  {{ $t('button.save') }}
                </a-button>
                <a-button type="text" size="mini" @click="cancelSkillOptEdit">
                  {{ $t('button.cancel') }}
                </a-button>
              </template>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, reactive, ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import { getIconUrl, onItemIconError } from '@/utils/mapleStoryAPI';
  import { getEquInitialInfo } from '@/api/player';
  import {
    SponsorConfigRecord,
    SponsorEquipStats,
    SponsorRewardRecord,
    SponsorSkillOptionRecord,
    deleteSponsorConfig,
    deleteSponsorReward,
    deleteSponsorSkillOption,
    formatStatsPreview,
    getSkillInfo,
    getSponsorConfigList,
    getSponsorRewards,
    getSponsorSkillOptions,
    hasAnyStatValue,
    isEquipItemId,
    normalizeStatsAbsolute,
    parseStatsJson,
    saveSponsorConfig,
    saveSponsorReward,
    saveSponsorSkillOption,
    stringifyStats,
    toggleSponsorEnabled,
  } from '@/api/sponsor';

  const { t } = useI18n();
  const rows = ref<SponsorConfigRecord[]>([]);
  const { loading, setLoading } = useLoading(false);
  const formVisible = ref(false);
  const saving = ref(false);
  const form = reactive<SponsorConfigRecord>({
    name: '',
    amount: 188,
    enabled: 1,
    sortOrder: 188,
  });

  const rewardVisible = ref(false);
  const rewardLoading = ref(false);
  const rewardTitle = ref('');
  const rewardRows = ref<SponsorRewardRecord[]>([]);
  const editRewardId = ref<number | undefined>(-1);
  const currentConfigId = ref<number>(0);
  const editStats = reactive<SponsorEquipStats>({});
  const loadingTemplate = ref(false);

  const skillOptVisible = ref(false);
  const skillOptLoading = ref(false);
  const skillOptTitle = ref('');
  const skillOptRows = ref<SponsorSkillOptionRecord[]>([]);
  const editSkillOptId = ref<number | undefined>(-1);
  const currentSkillRewardId = ref<number>(0);
  const skillNameCache = reactive<Record<number, string>>({});

  const STAT_FIELDS: { key: keyof SponsorEquipStats; labelKey: string }[] = [
    { key: 'str', labelKey: 'sponsor.stat.str' },
    { key: 'dex', labelKey: 'sponsor.stat.dex' },
    { key: 'int', labelKey: 'sponsor.stat.int' },
    { key: 'luk', labelKey: 'sponsor.stat.luk' },
    { key: 'hp', labelKey: 'sponsor.stat.hp' },
    { key: 'mp', labelKey: 'sponsor.stat.mp' },
    { key: 'pAtk', labelKey: 'sponsor.stat.pAtk' },
    { key: 'mAtk', labelKey: 'sponsor.stat.mAtk' },
    { key: 'pDef', labelKey: 'sponsor.stat.pDef' },
    { key: 'mDef', labelKey: 'sponsor.stat.mDef' },
    { key: 'acc', labelKey: 'sponsor.stat.acc' },
    { key: 'avoid', labelKey: 'sponsor.stat.avoid' },
    { key: 'hands', labelKey: 'sponsor.stat.hands' },
    { key: 'speed', labelKey: 'sponsor.stat.speed' },
    { key: 'jump', labelKey: 'sponsor.stat.jump' },
    { key: 'upgradeSlot', labelKey: 'sponsor.stat.upgradeSlot' },
  ];

  const editingEquipRecord = computed(() => {
    if (editRewardId.value === -1) return null;
    return (
      rewardRows.value.find((r) => r.id === editRewardId.value) ||
      (editRewardId.value === undefined
        ? rewardRows.value.find((r) => r.id === undefined)
        : null)
    );
  });

  const editingSkillGroupRecord = computed(() => {
    const rec = editingEquipRecord.value;
    return rec && rec.type === 'skill_group' ? rec : null;
  });

  const typeLabel = (type: string) => {
    if (type === 'nx') return t('sponsor.type.nx');
    if (type === 'maple') return t('sponsor.type.maple');
    if (type === 'meso') return t('sponsor.type.meso');
    if (type === 'skill_group') return t('sponsor.type.skill_group');
    return t('sponsor.type.item');
  };

  const pickModeLabel = (mode?: string | null) => {
    if (mode === 'ONE') return t('sponsor.pickMode.ONE');
    if (mode === 'MULTI') return t('sponsor.pickMode.MULTI');
    if (mode === 'ALL') return t('sponsor.pickMode.ALL');
    return mode || '-';
  };

  const clearEditStats = () => {
    STAT_FIELDS.forEach(({ key }) => {
      editStats[key] = undefined;
    });
  };

  const fillEditStats = (stats: SponsorEquipStats) => {
    clearEditStats();
    Object.assign(editStats, normalizeStatsAbsolute(stats));
  };

  const loadRows = async () => {
    setLoading(true);
    try {
      const { data } = await getSponsorConfigList();
      rows.value = data;
    } finally {
      setLoading(false);
    }
  };
  loadRows();

  const openCreate = () => {
    Object.assign(form, {
      id: undefined,
      name: '',
      amount: 188,
      enabled: 1,
      sortOrder: 188,
    });
    formVisible.value = true;
  };

  const openEdit = (record: SponsorConfigRecord) => {
    Object.assign(form, { ...record });
    formVisible.value = true;
  };

  const handleSave = async () => {
    if (!form.amount || form.amount <= 0) {
      Message.error('达标金额必须大于0');
      return false;
    }
    saving.value = true;
    try {
      await saveSponsorConfig({
        id: form.id,
        name: form.name || `赞助满${form.amount}`,
        amount: form.amount,
        enabled: form.enabled ?? 1,
        sortOrder: form.sortOrder ?? form.amount,
      });
      Message.success(t('message.success'));
      formVisible.value = false;
      await loadRows();
      return true;
    } catch {
      return false;
    } finally {
      saving.value = false;
    }
  };

  const toggleClick = async (record: SponsorConfigRecord) => {
    if (!record.id) return;
    await toggleSponsorEnabled(record.id);
    Message.success(t('message.success'));
    await loadRows();
  };

  const deleteClick = async (record: SponsorConfigRecord) => {
    if (!record.id) return;
    await deleteSponsorConfig(record.id);
    Message.success(t('message.success'));
    await loadRows();
  };

  const openRewards = async (record: SponsorConfigRecord) => {
    if (!record.id) return;
    currentConfigId.value = record.id;
    rewardTitle.value = `${t('sponsor.reward.title')} — ${
      record.name || record.amount
    }`;
    rewardVisible.value = true;
    await loadRewards();
  };

  const loadRewards = async () => {
    rewardLoading.value = true;
    editRewardId.value = -1;
    clearEditStats();
    try {
      const { data } = await getSponsorRewards(currentConfigId.value);
      rewardRows.value = data.map((r) => ({
        ...r,
        statMode: r.statMode || 'default',
        pickMode: r.pickMode || (r.type === 'skill_group' ? 'ALL' : null),
      }));
    } finally {
      rewardLoading.value = false;
    }
  };

  const beginEditReward = async (record: SponsorRewardRecord) => {
    editRewardId.value = record.id;
    if (
      record.type === 'item' &&
      isEquipItemId(record.itemId || 0) &&
      record.statMode === 'custom'
    ) {
      const parsed = parseStatsJson(record.statsJson);
      if (hasAnyStatValue(parsed)) {
        fillEditStats(parsed);
      } else {
        await loadWzTemplate(false);
      }
    } else {
      clearEditStats();
    }
  };

  const onStatModeChange = async (record: SponsorRewardRecord) => {
    if (
      record.statMode === 'custom' &&
      record.type === 'item' &&
      isEquipItemId(record.itemId || 0)
    ) {
      const parsed = parseStatsJson(record.statsJson);
      if (hasAnyStatValue(parsed)) {
        fillEditStats(parsed);
      } else {
        await loadWzTemplate(false);
      }
    } else {
      clearEditStats();
    }
  };

  const onPickModeChange = (record: SponsorRewardRecord) => {
    if (record.pickMode === 'ONE') {
      record.qty = 1;
    } else if (record.pickMode === 'MULTI' && (!record.qty || record.qty < 1)) {
      record.qty = 1;
    } else if (record.pickMode === 'ALL') {
      record.qty = 0;
    }
  };

  const onRewardTypeOrItemChange = async (record: SponsorRewardRecord) => {
    if (record.type === 'skill_group') {
      record.itemId = 0;
      record.statMode = 'default';
      record.statsJson = null;
      if (!record.pickMode) record.pickMode = 'ALL';
      onPickModeChange(record);
      clearEditStats();
      return;
    }
    record.pickMode = null;
    if (record.type !== 'item' || !isEquipItemId(record.itemId || 0)) {
      record.statMode = 'default';
      record.statsJson = null;
      clearEditStats();
      return;
    }
    if (!record.statMode) {
      record.statMode = 'default';
    }
    if (record.statMode === 'custom' && editRewardId.value === record.id) {
      record.statsJson = null;
      await loadWzTemplate(false);
    }
  };

  const insertReward = () => {
    rewardRows.value.unshift({
      configId: currentConfigId.value,
      type: 'item',
      itemId: 0,
      qty: 1,
      statMode: 'default',
      statsJson: null,
      pickMode: null,
    });
    editRewardId.value = undefined;
    clearEditStats();
  };

  /** @param showToast 手动点击按钮时提示成功；自动预填时静默 */
  const loadWzTemplate = async (showToast = true) => {
    const record = editingEquipRecord.value;
    if (!record?.itemId) {
      if (showToast) Message.warning('请先填写装备道具 ID');
      return;
    }
    loadingTemplate.value = true;
    try {
      const { data } = await getEquInitialInfo(record.itemId);
      fillEditStats({
        str: data.str,
        dex: data.dex,
        int: data.int,
        luk: data.luk,
        hp: data.hp,
        mp: data.mp,
        pAtk: data.pAtk,
        mAtk: data.mAtk,
        pDef: data.pDef,
        mDef: data.mDef,
        acc: data.acc,
        avoid: data.avoid,
        hands: data.hands,
        speed: data.speed,
        jump: data.jump,
        upgradeSlot: data.upgradeSlot,
      });
      if (showToast) Message.success(t('message.success'));
    } catch {
      Message.error('载入模板失败，请确认道具 ID 为有效装备');
    } finally {
      loadingTemplate.value = false;
    }
  };

  const saveRewardClick = async (record: SponsorRewardRecord) => {
    if (record.type === 'skill_group') {
      const pickMode = record.pickMode || 'ALL';
      let qty = record.qty || 0;
      if (pickMode === 'ONE') qty = 1;
      if (pickMode === 'MULTI' && qty < 1) {
        Message.error('多选多须设置选取数量 ≥ 1');
        return;
      }
      await saveSponsorReward({
        id: record.id,
        configId: currentConfigId.value,
        type: 'skill_group',
        itemId: 0,
        qty,
        pickMode,
        statMode: 'default',
        statsJson: null,
      });
      Message.success(t('message.success'));
      await loadRewards();
      return;
    }

    const isEquip = record.type === 'item' && isEquipItemId(record.itemId || 0);
    const mode = isEquip ? record.statMode || 'default' : 'default';
    let statsJson: string | null = null;
    if (isEquip && mode === 'custom') {
      statsJson = stringifyStats(editStats);
    }
    await saveSponsorReward({
      id: record.id,
      configId: currentConfigId.value,
      type: record.type,
      itemId: record.itemId || 0,
      qty: record.qty,
      statMode: mode,
      statsJson,
      pickMode: null,
    });
    Message.success(t('message.success'));
    await loadRewards();
  };

  const cancelRewardEdit = async () => {
    await loadRewards();
  };

  const deleteRewardClick = async (record: SponsorRewardRecord) => {
    if (!record.id) {
      await loadRewards();
      return;
    }
    await deleteSponsorReward(record.id);
    Message.success(t('message.success'));
    await loadRewards();
  };

  // ---------- 技能选项 ----------

  const openSkillOptions = async (record: SponsorRewardRecord) => {
    if (!record.id) {
      Message.warning('请先保存技能组奖励行');
      return;
    }
    currentSkillRewardId.value = record.id;
    skillOptTitle.value = `${t('sponsor.skill.options')} — reward #${record.id}`;
    skillOptVisible.value = true;
    await loadSkillOptions();
  };

  const loadSkillOptions = async () => {
    skillOptLoading.value = true;
    editSkillOptId.value = -1;
    try {
      const { data } = await getSponsorSkillOptions(currentSkillRewardId.value);
      skillOptRows.value = data.map((r) => ({
        ...r,
        skillLevel: r.skillLevel ?? 0,
        defaultKey: r.defaultKey ?? 0,
        sortOrder: r.sortOrder ?? 0,
      }));
      for (const row of skillOptRows.value) {
        if (row.skillId) {
          await lookupSkillName(row.skillId, false);
        }
      }
    } finally {
      skillOptLoading.value = false;
    }
  };

  const lookupSkillName = async (skillId: number, toast = true) => {
    if (!skillId) return;
    try {
      const { data } = await getSkillInfo(skillId);
      skillNameCache[skillId] = data.exists
        ? data.name || `技能${skillId}`
        : `未知(${skillId})`;
      if (toast) {
        Message.info(
          data.exists
            ? `${data.name || skillId}（最大Lv${data.maxLevel}）`
            : `技能 ${skillId} 不存在`
        );
      }
    } catch {
      skillNameCache[skillId] = `查询失败(${skillId})`;
    }
  };

  const insertSkillOption = () => {
    skillOptRows.value.unshift({
      rewardId: currentSkillRewardId.value,
      skillId: 0,
      skillLevel: 0,
      defaultKey: 0,
      sortOrder: 0,
    });
    editSkillOptId.value = undefined;
  };

  const beginEditSkillOpt = (record: SponsorSkillOptionRecord) => {
    editSkillOptId.value = record.id;
  };

  const saveSkillOptClick = async (record: SponsorSkillOptionRecord) => {
    if (!record.skillId || record.skillId <= 0) {
      Message.error('请填写有效技能 ID');
      return;
    }
    await saveSponsorSkillOption({
      id: record.id,
      rewardId: currentSkillRewardId.value,
      skillId: record.skillId,
      skillLevel: record.skillLevel ?? 0,
      defaultKey: record.defaultKey ?? 0,
      sortOrder: record.sortOrder ?? 0,
    });
    Message.success(t('message.success'));
    await loadSkillOptions();
  };

  const cancelSkillOptEdit = async () => {
    await loadSkillOptions();
  };

  const deleteSkillOptClick = async (record: SponsorSkillOptionRecord) => {
    if (!record.id) {
      await loadSkillOptions();
      return;
    }
    await deleteSponsorSkillOption(record.id);
    Message.success(t('message.success'));
    await loadSkillOptions();
  };
</script>

<script lang="ts">
  export default {
    name: 'Sponsor',
  };
</script>

<style lang="less" scoped></style>
