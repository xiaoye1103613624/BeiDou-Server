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
                  <a-form-item :label="$t(f.labelKey)">
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
                  <a-form-item :label="$t(f.labelKey)">
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
                        <span>{{ $t(f.labelKey) }}</span>
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
          <div class="preview-hint">{{ $t('setItem.preview.tip') }}</div>
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
            />
          </div>
          <div class="set-tip">
            <div class="set-tip-title">{{ tipTitle }}</div>
            <div
              v-for="(row, idx) in tipItems"
              :key="`item-${idx}-${row.itemId}`"
              class="set-tip-item"
              :class="row.state"
            >
              <span class="set-tip-item-name">{{ row.name }}</span>
              <span v-if="row.slot" class="set-tip-item-slot"
                >({{ row.slot }})</span
              >
            </div>
            <template v-for="(tier, tidx) in tipTiers" :key="`tier-${tier.count}`">
              <div class="set-tip-sep" />
              <div
                class="set-tip-tier-header"
                :class="{ active: tier.active, inactive: !tier.active }"
              >
                {{ $t('setItem.preview.effect', { n: tier.count }) }}
              </div>
              <div
                v-for="(line, lidx) in tier.lines"
                :key="`tier-${tidx}-line-${lidx}`"
                class="set-tip-stat"
                :class="{
                  active: tier.active,
                  inactive: !tier.active,
                  accent: line.accent,
                }"
              >
                <span class="set-tip-stat-label">{{ line.label }} : </span>
                <span class="set-tip-stat-value">+{{ line.value }}{{ line.suffix }}</span>
              </div>
            </template>
          </div>
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
    parseTiersJson,
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
    { key: 'str', labelKey: 'setItem.stat.str' },
    { key: 'dex', labelKey: 'setItem.stat.dex' },
    { key: 'int', labelKey: 'setItem.stat.int' },
    { key: 'luk', labelKey: 'setItem.stat.luk' },
    { key: 'pad', labelKey: 'setItem.stat.pad' },
    { key: 'mad', labelKey: 'setItem.stat.mad' },
    { key: 'pdd', labelKey: 'setItem.stat.pdd' },
    { key: 'mdd', labelKey: 'setItem.stat.mdd' },
    { key: 'acc', labelKey: 'setItem.stat.acc' },
    { key: 'eva', labelKey: 'setItem.stat.eva' },
    { key: 'mhp', labelKey: 'setItem.stat.mhp' },
    { key: 'mmp', labelKey: 'setItem.stat.mmp' },
    { key: 'allStat', labelKey: 'setItem.stat.allStat' },
    { key: 'speed', labelKey: 'setItem.stat.speed' },
    { key: 'jump', labelKey: 'setItem.stat.jump' },
  ] as const;

  const percentFields = [
    { key: 'strR', labelKey: 'setItem.stat.strR' },
    { key: 'dexR', labelKey: 'setItem.stat.dexR' },
    { key: 'intR', labelKey: 'setItem.stat.intR' },
    { key: 'lukR', labelKey: 'setItem.stat.lukR' },
    { key: 'mhpR', labelKey: 'setItem.stat.mhpR' },
    { key: 'mmpR', labelKey: 'setItem.stat.mmpR' },
  ] as const;

  const combatFields = [
    {
      key: 'damR',
      labelKey: 'setItem.stat.damR',
      hint: 'setItem.hint.additive',
    },
    { key: 'bdR', labelKey: 'setItem.stat.bdR', hint: 'setItem.hint.boss' },
    {
      key: 'nbdR',
      labelKey: 'setItem.stat.nbdR',
      hint: 'setItem.hint.normal',
    },
    { key: 'fdR', labelKey: 'setItem.stat.fdR', hint: 'setItem.hint.multi' },
    {
      key: 'ignoreMobpdpR',
      labelKey: 'setItem.stat.ignoreMobpdpR',
      hint: 'setItem.hint.cap',
    },
    {
      key: 'ignoreMobmdR',
      labelKey: 'setItem.stat.ignoreMobmdR',
      hint: 'setItem.hint.cap',
    },
    { key: 'cr', labelKey: 'setItem.stat.cr', hint: 'setItem.hint.cap' },
    { key: 'cd', labelKey: 'setItem.stat.cd', hint: 'setItem.hint.additive' },
  ] as const;

  type TipStatLine = {
    label: string;
    value: number;
    suffix: string;
    accent?: boolean;
  };

  const currentTier = computed(() => tiersModel.tiers[selectedTierIndex.value]);
  const maxTierCount = computed(() =>
    tiersModel.tiers.reduce((m, tier) => Math.max(m, tier.count || 0), 0)
  );
  const previewMax = computed(() =>
    Math.max(form.completeCount || 0, maxTierCount.value, parsedItemIds.value.length, 1)
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

  const getEquipSortKey = (itemId: number) => {
    const cat = Math.floor(itemId / 10000);
    if ((cat >= 130 && cat <= 149) || cat === 170) {
      return 1000 + cat;
    }
    const order: Record<number, number> = {
      100: 10,
      101: 20,
      102: 30,
      103: 40,
      104: 50,
      105: 55,
      106: 60,
      107: 70,
      108: 80,
      109: 85,
      110: 90,
      113: 95,
      115: 96,
      112: 97,
      111: 98,
      114: 99,
    };
    return order[cat] ?? 200 + cat;
  };

  const getSlotLabel = (itemId: number) => {
    const cat = Math.floor(itemId / 10000);
    const key = `setItem.slot.${cat}`;
    const translated = t(key);
    if (translated !== key) {
      return translated;
    }
    if (itemId >= 1300000 && itemId < 1500000) {
      return t('setItem.slot.weapon');
    }
    if (cat === 170) {
      return t('setItem.slot.weapon');
    }
    return '';
  };

  const parsedItemIds = computed(() => {
    const raw = form.itemIds || '';
    const ids = raw
      .split(/[,，\s]+/)
      .map((s) => Number(s.trim()))
      .filter((n) => Number.isFinite(n) && n > 0);
    return [...new Set(ids)].sort(
      (a, b) => getEquipSortKey(a) - getEquipSortKey(b)
    );
  });

  const tipTitle = computed(
    () => form.setName?.trim() || t('setItem.detail.title')
  );

  const tipItems = computed(() => {
    const equipped = previewCount.value;
    return parsedItemIds.value.map((itemId, idx) => {
      const equippedRow = idx < equipped;
      return {
        itemId,
        name: String(itemId),
        slot: getSlotLabel(itemId),
        state: equippedRow ? 'equipped' : 'missing',
      };
    });
  });

  const pushStat = (
    lines: TipStatLine[],
    label: string,
    value: number | undefined,
    suffix = '',
    accent = false
  ) => {
    const v = Number(value) || 0;
    if (!v) return;
    lines.push({ label, value: v, suffix, accent });
  };

  const buildTierLines = (tier: SetTier): TipStatLine[] => {
    const lines: TipStatLine[] = [];
    const s = tier.stats || {};
    const p = tier.statsPercent || {};
    const c = tier.combatStats || {};

    const str = Number(s.str) || 0;
    const dex = Number(s.dex) || 0;
    const int_ = Number(s.int) || 0;
    const luk = Number(s.luk) || 0;
    if (str && str === dex && str === int_ && str === luk) {
      pushStat(lines, t('setItem.tip.allStat'), str);
    } else {
      pushStat(lines, t('setItem.stat.str'), str);
      pushStat(lines, t('setItem.stat.dex'), dex);
      pushStat(lines, t('setItem.stat.int'), int_);
      pushStat(lines, t('setItem.stat.luk'), luk);
    }

    const pad = Number(s.pad) || 0;
    const mad = Number(s.mad) || 0;
    if (pad && pad === mad) {
      pushStat(lines, t('setItem.tip.padMad'), pad, '', true);
    } else {
      pushStat(lines, t('setItem.stat.pad'), pad, '', true);
      pushStat(lines, t('setItem.stat.mad'), mad, '', true);
    }

    const mhp = Number(s.mhp) || 0;
    const mmp = Number(s.mmp) || 0;
    if (mhp && mhp === mmp) {
      pushStat(lines, t('setItem.tip.mhpMmp'), mhp);
    } else {
      pushStat(lines, t('setItem.stat.mhp'), mhp);
      pushStat(lines, t('setItem.stat.mmp'), mmp);
    }

    pushStat(lines, t('setItem.stat.pdd'), s.pdd);
    pushStat(lines, t('setItem.stat.mdd'), s.mdd);
    pushStat(lines, t('setItem.stat.acc'), s.acc);
    pushStat(lines, t('setItem.stat.eva'), s.eva);
    pushStat(lines, t('setItem.stat.allStat'), s.allStat);
    pushStat(lines, t('setItem.stat.speed'), s.speed);
    pushStat(lines, t('setItem.stat.jump'), s.jump);

    pushStat(lines, t('setItem.stat.str'), p.strR, '%');
    pushStat(lines, t('setItem.stat.dex'), p.dexR, '%');
    pushStat(lines, t('setItem.stat.int'), p.intR, '%');
    pushStat(lines, t('setItem.stat.luk'), p.lukR, '%');
    pushStat(lines, t('setItem.stat.mhp'), p.mhpR, '%');
    pushStat(lines, t('setItem.stat.mmp'), p.mmpR, '%');

    pushStat(lines, t('setItem.stat.damR').replace(/%$/, ''), c.damR, '%', true);
    pushStat(lines, t('setItem.stat.bdR').replace(/%$/, ''), c.bdR, '%', true);
    pushStat(lines, t('setItem.stat.nbdR').replace(/%$/, ''), c.nbdR, '%', true);
    pushStat(
      lines,
      t('setItem.stat.ignoreMobpdpR').replace(/%$/, ''),
      c.ignoreMobpdpR,
      '%',
      true
    );
    pushStat(
      lines,
      t('setItem.stat.ignoreMobmdR').replace(/%$/, ''),
      c.ignoreMobmdR,
      '%',
      true
    );
    pushStat(lines, t('setItem.stat.fdR').replace(/%$/, ''), c.fdR, '%', true);
    pushStat(lines, t('setItem.stat.cr').replace(/%$/, ''), c.cr, '%', true);
    pushStat(lines, t('setItem.stat.cd').replace(/%$/, ''), c.cd, '%', true);

    return lines;
  };

  const tipTiers = computed(() => {
    const count = previewCount.value;
    return [...tiersModel.tiers]
      .filter((tier) => tier.enabled !== false)
      .sort((a, b) => (a.count || 0) - (b.count || 0))
      .map((tier) => {
        const lines = buildTierLines(tier);
        return {
          count: tier.count || 0,
          active: count >= (tier.count || 0),
          lines,
        };
      })
      .filter((tier) => tier.count > 0 && tier.lines.length > 0);
  });

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
  };

  watch(
    () => props.record,
    (rec) => loadRecord(rec),
    { immediate: true }
  );

  watch(previewMax, (max) => {
    if (previewCount.value > max) {
      previewCount.value = max;
    }
  });

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
</script>

<style scoped>
  .preview-hint {
    margin-bottom: 8px;
    color: var(--color-text-3);
    font-size: 12px;
  }

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

  .set-tip {
    width: 280px;
    max-width: 100%;
    padding: 10px 12px 12px;
    background: rgba(8, 16, 40, 0.92);
    border: 1px solid rgba(220, 230, 255, 0.85);
    color: #fff;
    font-size: 13px;
    line-height: 1.35;
    font-family: 'Microsoft YaHei', 'SimSun', sans-serif;
    user-select: none;
  }

  .set-tip-title {
    text-align: center;
    color: #ccff00;
    font-weight: 700;
    margin-bottom: 10px;
  }

  .set-tip-item {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    margin: 2px 0;
  }

  .set-tip-item.equipped {
    color: #ffcc00;
  }

  .set-tip-item.missing {
    color: #9a9a9a;
  }

  .set-tip-item-name {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .set-tip-item-slot {
    flex-shrink: 0;
  }

  .set-tip-sep {
    height: 0;
    border-top: 1px solid rgba(255, 255, 255, 0.75);
    margin: 8px 0 6px;
  }

  .set-tip-tier-header {
    margin-bottom: 2px;
  }

  .set-tip-tier-header.active {
    color: #ffffff;
  }

  .set-tip-tier-header.inactive {
    color: #9a9a9a;
  }

  .set-tip-stat {
    padding-left: 1em;
    margin: 1px 0;
  }

  .set-tip-stat.active {
    color: #efefef;
  }

  .set-tip-stat.active.accent {
    color: #ffcc00;
  }

  .set-tip-stat.inactive {
    color: #9a9a9a;
  }
</style>
