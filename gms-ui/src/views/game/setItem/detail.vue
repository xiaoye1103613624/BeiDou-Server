<template>
  <a-drawer
    :visible="visible"
    :width="920"
    :title="form.setName || $t('setItem.detail.title')"
    unmount-on-close
    @cancel="handleClose"
  >
    <template #footer>
      <a-space>
        <a-button @click="handleClose">{{ $t('button.cancel') }}</a-button>
        <a-button type="primary" :loading="saving" @click="handleSave">
          {{ $t('button.save') }}
        </a-button>
      </a-space>
    </template>

    <a-form :model="form" layout="vertical">
      <a-row :gutter="12">
        <a-col :span="6">
          <a-form-item :label="$t('setItem.column.setId')">
            <a-input-number v-model="form.setId" :min="1" style="width: 100%" />
          </a-form-item>
        </a-col>
        <a-col :span="10">
          <a-form-item :label="$t('setItem.column.setName')">
            <a-input v-model="form.setName" />
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-form-item :label="$t('setItem.column.completeCount')">
            <a-input-number
              v-model="form.completeCount"
              :min="0"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-form-item :label="$t('setItem.column.enabled')">
            <a-switch
              v-model="form.enabled"
              :checked-value="1"
              :unchecked-value="0"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-tabs default-active-key="equip">
        <a-tab-pane key="equip" :title="$t('setItem.tab.equip')">
          <a-textarea
            v-model="form.itemIds"
            :placeholder="$t('setItem.equip.placeholder')"
            :auto-size="{ minRows: 4, maxRows: 8 }"
          />
        </a-tab-pane>

        <a-tab-pane key="tiers" :title="$t('setItem.tab.tiers')">
          <a-space style="margin-bottom: 12px">
            <a-button type="primary" size="small" @click="addTier">
              {{ $t('setItem.tier.add') }}
            </a-button>
            <a-button size="small" :disabled="!currentTier" @click="copyTier">
              {{ $t('setItem.tier.copy') }}
            </a-button>
          </a-space>
          <a-row :gutter="12">
            <a-col :span="6">
              <a-menu
                :selected-keys="[String(selectedTierIndex)]"
                @menu-item-click="onTierSelect"
              >
                <a-menu-item
                  v-for="(tier, idx) in tiersModel.tiers"
                  :key="String(idx)"
                >
                  <a-space>
                    <span>{{ tier.count }} {{ $t('setItem.tier.piece') }}</span>
                    <a-switch
                      v-model="tier.enabled"
                      size="small"
                      :checked-value="true"
                      :unchecked-value="false"
                      @click.stop
                    />
                  </a-space>
                </a-menu-item>
              </a-menu>
            </a-col>
            <a-col v-if="currentTier" :span="18">
              <a-form-item :label="$t('setItem.tier.count')">
                <a-input-number v-model="currentTier.count" :min="1" />
              </a-form-item>
              <a-divider>{{ $t('setItem.tier.basic') }}</a-divider>
              <a-row :gutter="8">
                <a-col v-for="f in basicFields" :key="f.key" :span="6">
                  <a-form-item :label="f.label">
                    <a-input-number
                      v-model="currentTier.stats![f.key]"
                      :min="0"
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-divider>{{ $t('setItem.tier.percent') }}</a-divider>
              <a-row :gutter="8">
                <a-col v-for="f in percentFields" :key="f.key" :span="6">
                  <a-form-item :label="f.label">
                    <a-input-number
                      v-model="currentTier.statsPercent![f.key]"
                      :min="0"
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-divider>{{ $t('setItem.tier.combat') }}</a-divider>
              <a-row :gutter="8">
                <a-col v-for="f in combatFields" :key="f.key" :span="6">
                  <a-form-item>
                    <template #label>
                      <a-tooltip :content="$t(f.hint)">
                        <span>{{ f.label }}</span>
                      </a-tooltip>
                    </template>
                    <a-input-number
                      v-model="currentTier.combatStats![f.key]"
                      :min="0"
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-divider>{{ $t('setItem.tier.skills') }}</a-divider>
              <a-space direction="vertical" fill>
                <div v-for="(sk, sidx) in currentTier.skills" :key="sidx">
                  <a-space>
                    <a-input-number
                      v-model="sk.id"
                      :placeholder="$t('setItem.skill.id')"
                    />
                    <a-input-number
                      v-model="sk.level"
                      :placeholder="$t('setItem.skill.level')"
                    />
                    <a-button
                      size="mini"
                      status="danger"
                      @click="removeSkill(sidx)"
                    >
                      ×
                    </a-button>
                  </a-space>
                </div>
                <a-button size="small" @click="addSkill">
                  {{ $t('setItem.skill.add') }}
                </a-button>
              </a-space>
              <a-divider>{{ $t('setItem.tier.activeSkills') }}</a-divider>
              <a-space direction="vertical" fill>
                <div
                  v-for="(ask, aidx) in currentTier.activeSkills"
                  :key="aidx"
                >
                  <a-space>
                    <a-input-number
                      v-model="ask.skillId"
                      :placeholder="$t('setItem.skill.id')"
                    />
                    <a-input-number
                      v-model="ask.level"
                      :placeholder="$t('setItem.skill.level')"
                    />
                    <a-button
                      size="mini"
                      status="danger"
                      @click="removeActiveSkill(aidx)"
                    >
                      ×
                    </a-button>
                  </a-space>
                </div>
                <a-button size="small" @click="addActiveSkill">
                  {{ $t('setItem.activeSkill.add') }}
                </a-button>
              </a-space>
              <a-divider>{{ $t('setItem.tier.skillMods') }}</a-divider>
              <a-space direction="vertical" fill>
                <div v-for="(mod, midx) in currentTier.skillMods" :key="midx">
                  <a-space wrap>
                    <a-input-number
                      v-model="mod.skillId"
                      :placeholder="$t('setItem.skill.id')"
                    />
                    <a-input-number
                      v-model="mod.addAttackCount"
                      :placeholder="$t('setItem.skillMod.attackCount')"
                      :min="0"
                    />
                    <a-input-number
                      v-model="mod.addLevel"
                      :placeholder="$t('setItem.skillMod.addLevel')"
                      :min="0"
                    />
                    <a-button
                      size="mini"
                      status="danger"
                      @click="removeSkillMod(midx)"
                    >
                      ×
                    </a-button>
                  </a-space>
                </div>
                <a-button size="small" @click="addSkillMod">
                  {{ $t('setItem.skillMod.add') }}
                </a-button>
              </a-space>
            </a-col>
          </a-row>
        </a-tab-pane>

        <a-tab-pane key="preview" :title="$t('setItem.tab.preview')">
          <a-form-item :label="$t('setItem.color')">
            <a-select v-model="tooltipColor" style="width: 200px">
              <a-option
                v-for="(item, key) in colorOptions"
                :key="String(key)"
                :value="String(key)"
              >
                {{ item.label }} ({{ item.code }})
              </a-option>
            </a-select>
          </a-form-item>
          <div class="preview-toolbar">
            <span class="preview-label">{{ $t('setItem.preview.count') }}</span>
            <a-slider
              v-model="previewCount"
              class="preview-slider"
              :min="0"
              :max="previewMax"
              :marks="previewMarks"
              show-ticks
              :format-tooltip="formatPreviewTooltip"
              @change="runPreview"
            />
            <a-button size="small" @click="runPreview">{{
              $t('setItem.preview.refresh')
            }}</a-button>
          </div>
          <a-typography-paragraph>
            <pre class="preview-box">{{ previewText }}</pre>
          </a-typography-paragraph>
        </a-tab-pane>
      </a-tabs>

      <a-form-item :label="$t('setItem.column.remark')">
        <a-input v-model="form.remark" />
      </a-form-item>
    </a-form>
  </a-drawer>
</template>

<script setup lang="ts">
  import { computed, reactive, ref, watch } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import {
    SetItemDetail,
    SetItemTiersV2,
    SetTier,
    getSetItemColors,
    parseTiersJson,
    previewSetItem,
    reloadSetItem,
    saveSetItem,
    stringifyTiersJson,
  } from '@/api/setItem';

  const props = defineProps<{
    visible: boolean;
    record: SetItemDetail | null;
  }>();
  const emit = defineEmits<{
    (e: 'update:visible', v: boolean): void;
    (e: 'saved'): void;
  }>();

  const { t } = useI18n();
  const saving = ref(false);
  const selectedTierIndex = ref(0);
  const previewCount = ref(0);
  const previewText = ref('');
  const tooltipColor = ref('SET_BONUS');
  const colorOptions = ref<Record<string, { code: string; label: string }>>({});

  const form = reactive<SetItemDetail>({
    setId: 0,
    setName: '',
    completeCount: 0,
    itemIds: '',
    enabled: 1,
    remark: '',
    tiersJson: '',
  });

  const tiersModel = reactive<SetItemTiersV2>({ schemaVersion: 2, tiers: [] });

  const basicFields = [
    { key: 'str', label: 'STR' },
    { key: 'dex', label: 'DEX' },
    { key: 'int', label: 'INT' },
    { key: 'luk', label: 'LUK' },
    { key: 'pad', label: 'PAD' },
    { key: 'mad', label: 'MAD' },
    { key: 'pdd', label: 'PDD' },
    { key: 'mdd', label: 'MDD' },
    { key: 'acc', label: 'ACC' },
    { key: 'eva', label: 'EVA' },
    { key: 'mhp', label: 'HP' },
    { key: 'mmp', label: 'MP' },
    { key: 'allStat', label: '全属性' },
    { key: 'speed', label: 'Speed' },
    { key: 'jump', label: 'Jump' },
  ] as const;

  const percentFields = [
    { key: 'strR', label: 'STR%' },
    { key: 'dexR', label: 'DEX%' },
    { key: 'intR', label: 'INT%' },
    { key: 'lukR', label: 'LUK%' },
    { key: 'mhpR', label: 'HP%' },
    { key: 'mmpR', label: 'MP%' },
  ] as const;

  const combatFields = [
    { key: 'damR', label: '伤害%', hint: 'setItem.hint.additive' },
    { key: 'bdR', label: 'Boss%', hint: 'setItem.hint.boss' },
    { key: 'nbdR', label: '普通怪%', hint: 'setItem.hint.normal' },
    { key: 'fdR', label: '最终伤害%', hint: 'setItem.hint.multi' },
    { key: 'ignoreMobpdpR', label: '无视物防%', hint: 'setItem.hint.cap' },
    { key: 'ignoreMobmdR', label: '无视魔防%', hint: 'setItem.hint.cap' },
    { key: 'cr', label: '暴击率%', hint: 'setItem.hint.cap' },
    { key: 'cd', label: '暴击伤害%', hint: 'setItem.hint.additive' },
  ] as const;

  const currentTier = computed(() => tiersModel.tiers[selectedTierIndex.value]);
  const maxTierCount = computed(() =>
    tiersModel.tiers.reduce((m, tier) => Math.max(m, tier.count || 0), 0)
  );
  const previewMax = computed(() =>
    Math.max(form.completeCount || 0, maxTierCount.value, 1)
  );
  const previewMarks = computed(() => {
    const marks: Record<number, string> = { 0: '0' };
    const counts = [
      ...new Set(
        tiersModel.tiers
          .map((tier) => tier.count)
          .filter((count) => count > 0 && count <= previewMax.value)
      ),
    ].sort((a, b) => a - b);
    counts.forEach((count) => {
      marks[count] = String(count);
    });
    return marks;
  });

  const formatPreviewTooltip = (value: number) =>
    `${value} ${t('setItem.tier.piece')}`;

  const ensureTierShape = (tier: SetTier) => {
    tier.enabled = tier.enabled !== false;
    tier.stats = tier.stats ?? {};
    tier.statsPercent = tier.statsPercent ?? {};
    tier.combatStats = tier.combatStats ?? {};
    tier.skills = tier.skills ?? [];
    tier.activeSkills = tier.activeSkills ?? [];
    tier.skillMods = tier.skillMods ?? [];
  };

  const loadRecord = (rec: SetItemDetail | null) => {
    Object.assign(form, {
      id: rec?.id,
      setId: rec?.setId ?? 0,
      setName: rec?.setName ?? '',
      completeCount: rec?.completeCount ?? 0,
      itemIds: rec?.itemIds ?? '',
      enabled: rec?.enabled ?? 1,
      remark: rec?.remark ?? '',
      tiersJson: rec?.tiersJson ?? '',
    });
    const parsed = parseTiersJson(rec?.tiersJson);
    tiersModel.tiers = parsed.tiers.map((tier) => {
      ensureTierShape(tier);
      return tier;
    });
    selectedTierIndex.value = 0;
    const tierCounts = parsed.tiers
      .map((tier) => tier.count || 0)
      .filter((count) => count > 0);
    previewCount.value = tierCounts.length ? Math.min(...tierCounts) : 0;
    previewText.value = '';
  };

  watch(
    () => props.record,
    (rec) => loadRecord(rec),
    { immediate: true }
  );

  const onTierSelect = (key: string) => {
    selectedTierIndex.value = Number(key);
  };

  const addTier = () => {
    const next = Math.max(0, ...tiersModel.tiers.map((item) => item.count)) + 1;
    const newTier: SetTier = {
      count: next,
      enabled: true,
      stats: {},
      statsPercent: {},
      combatStats: {},
      skills: [],
      activeSkills: [],
      skillMods: [],
    };
    tiersModel.tiers.push(newTier);
    tiersModel.tiers.sort((a, b) => a.count - b.count);
    selectedTierIndex.value = tiersModel.tiers.findIndex(
      (item) => item.count === next
    );
  };

  const copyTier = () => {
    const src = currentTier.value;
    if (!src) return;
    const next =
      Math.max(0, ...tiersModel.tiers.map((item) => item.count || 0)) + 1;
    const cloned: SetTier = JSON.parse(JSON.stringify(src));
    cloned.count = next;
    cloned.enabled = true;
    ensureTierShape(cloned);
    tiersModel.tiers.push(cloned);
    tiersModel.tiers.sort((a, b) => a.count - b.count);
    selectedTierIndex.value = tiersModel.tiers.findIndex(
      (item) => item.count === next
    );
  };

  const addSkill = () => {
    const tier = currentTier.value;
    if (!tier) return;
    if (!tier.skills) {
      tier.skills = [];
    }
    tier.skills.push({ id: 0, level: 1 });
  };

  const removeSkill = (idx: number) => {
    currentTier.value?.skills?.splice(idx, 1);
  };

  const addActiveSkill = () => {
    const tier = currentTier.value;
    if (!tier) return;
    if (!tier.activeSkills) {
      tier.activeSkills = [];
    }
    tier.activeSkills.push({ skillId: 0, level: 1 });
  };

  const removeActiveSkill = (idx: number) => {
    currentTier.value?.activeSkills?.splice(idx, 1);
  };

  const addSkillMod = () => {
    const tier = currentTier.value;
    if (!tier) return;
    if (!tier.skillMods) {
      tier.skillMods = [];
    }
    tier.skillMods.push({
      skillId: 0,
      addAttackCount: 1,
      type: 'attackCount',
    });
  };

  const removeSkillMod = (idx: number) => {
    currentTier.value?.skillMods?.splice(idx, 1);
  };

  const runPreview = async () => {
    if (!form.setId) return;
    form.tiersJson = stringifyTiersJson(tiersModel);
    await saveSetItem(form);
    const { data } = await previewSetItem({
      setId: form.setId,
      equippedCount: previewCount.value,
    });
    previewText.value = data?.tooltipText ?? '';
  };

  const handleSave = async () => {
    saving.value = true;
    try {
      form.tiersJson = stringifyTiersJson(tiersModel);
      await saveSetItem(form);
      await reloadSetItem();
      Message.success(t('setItem.save.success'));
      emit('saved');
      emit('update:visible', false);
    } finally {
      saving.value = false;
    }
  };

  const handleClose = () => emit('update:visible', false);

  const loadColors = async () => {
    try {
      const { data } = await getSetItemColors();
      colorOptions.value = data || {};
    } catch {
      colorOptions.value = {
        SET_BONUS: { code: '#g', label: '绿色' },
      };
    }
  };

  loadColors();
</script>

<style scoped>
  .preview-toolbar {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
  }

  .preview-label {
    flex-shrink: 0;
  }

  .preview-slider {
    flex: 1;
    min-width: 320px;
    padding: 0 8px 20px;
  }

  .preview-box {
    white-space: pre-wrap;
    background: var(--color-fill-2);
    padding: 12px;
    border-radius: 4px;
    min-height: 120px;
  }
</style>
