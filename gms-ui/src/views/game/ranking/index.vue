<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.member.ranking')">
      <a-alert type="info" style="margin-bottom: 12px">
        {{ $t('ranking.hint') }}
      </a-alert>
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" :loading="refreshing" @click="onRefresh">
          <template #icon>
            <icon-refresh />
          </template>
          {{ $t('ranking.refresh') }}
        </a-button>
      </a-space>
      <a-tabs v-model:active-key="activeTab" @change="onTabChange">
        <a-tab-pane key="combat" :title="$t('ranking.tab.combat')">
          <a-form
            :model="combatForm"
            layout="inline"
            style="margin-bottom: 12px"
          >
            <a-form-item :label="$t('ranking.filter')">
              <a-select
                v-model="combatForm.filter"
                :options="jobOptions"
                :style="{ width: '220px' }"
                allow-search
                @change="loadCombat"
              />
            </a-form-item>
          </a-form>
          <a-table
            row-key="characterId"
            :loading="combatLoading"
            :data="combatRows"
            column-resizable
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('ranking.column.rank')"
                data-index="rank"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.name')"
                data-index="name"
                :width="140"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.world')"
                data-index="world"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.job')"
                data-index="jobName"
                :width="140"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.jobNiche')"
                data-index="jobNicheName"
                :width="120"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.level')"
                data-index="level"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.combatPower')"
                data-index="combatPower"
                :width="120"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.baseDamage')"
                data-index="baseDamage"
                :width="110"
                align="center"
              />
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="equip" :title="$t('ranking.tab.equip')">
          <a-form
            :model="equipForm"
            layout="inline"
            style="margin-bottom: 12px"
          >
            <a-form-item :label="$t('ranking.filter')">
              <a-select
                v-model="equipForm.filter"
                :options="slotOptions"
                :style="{ width: '220px' }"
                allow-search
                @change="loadEquip"
              />
            </a-form-item>
          </a-form>
          <a-table
            row-key="inventoryItemId"
            :loading="equipLoading"
            :data="equipRows"
            column-resizable
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('ranking.column.rank')"
                data-index="rank"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.item')"
                :width="260"
                align="left"
              >
                <template #cell="{ record }">
                  <a-popover position="right" trigger="hover">
                    <template #content>
                      <div class="equip-tooltip">
                        <div class="equip-tooltip__title">
                          {{ $t('ranking.tooltip.title') }}
                        </div>
                        <div>{{ record.itemName }} ({{ record.itemId }})</div>
                        <div>
                          {{ $t('ranking.tooltip.str') }} {{ record.attStr }} /
                          {{ $t('ranking.tooltip.dex') }} {{ record.attDex }} /
                          {{ $t('ranking.tooltip.int') }} {{ record.attInt }} /
                          {{ $t('ranking.tooltip.luk') }} {{ record.attLuk }}
                        </div>
                        <div>
                          {{ $t('ranking.tooltip.hp') }} {{ record.hp }} /
                          {{ $t('ranking.tooltip.mp') }} {{ record.mp }}
                        </div>
                        <div>
                          {{ $t('ranking.tooltip.pAtk') }} {{ record.pAtk }} /
                          {{ $t('ranking.tooltip.mAtk') }} {{ record.mAtk }}
                        </div>
                        <div>
                          {{ $t('ranking.tooltip.pDef') }} {{ record.pDef }} /
                          {{ $t('ranking.tooltip.mDef') }} {{ record.mDef }}
                        </div>
                        <div>
                          {{ $t('ranking.tooltip.acc') }} {{ record.acc }} /
                          {{ $t('ranking.tooltip.avoid') }} {{ record.avoid }}
                        </div>
                        <div>
                          {{ $t('ranking.tooltip.speed') }} {{ record.speed }} /
                          {{ $t('ranking.tooltip.jump') }} {{ record.jump }}
                        </div>
                        <div>
                          {{ $t('ranking.tooltip.upgradeSlots') }}
                          {{ record.upgradeSlots }} /
                          {{ $t('ranking.tooltip.level') }} {{ record.level }}
                        </div>
                        <div>
                          {{ $t('ranking.tooltip.vicious') }}
                          {{ record.vicious }} /
                          {{ $t('ranking.tooltip.itemLevel') }}
                          {{ record.itemLevel }}
                        </div>
                      </div>
                    </template>
                    <a-space>
                      <img
                        v-if="record.itemId"
                        class="equip-icon"
                        :src="getIconUrl('item', record.itemId)"
                        alt=""
                      />
                      <span>{{ record.itemName || record.itemId }}</span>
                    </a-space>
                  </a-popover>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('ranking.column.owner')"
                data-index="characterName"
                :width="120"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.slot')"
                data-index="slotCategoryName"
                :width="120"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.score')"
                data-index="score"
                :width="100"
                align="center"
              />
              <a-table-column
                :title="$t('ranking.column.equipped')"
                :width="90"
                align="center"
              >
                <template #cell="{ record }">
                  {{
                    record.equipped
                      ? $t('ranking.equipped.yes')
                      : $t('ranking.equipped.no')
                  }}
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('ranking.column.world')"
                data-index="world"
                :width="70"
                align="center"
              />
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { onMounted, reactive, ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import {
    CombatPowerRankItem,
    EquipScoreRankItem,
    RankingFilterOption,
    fetchCombatPowerRanking,
    fetchEquipScoreRanking,
    fetchJobNicheOptions,
    fetchSlotCategoryOptions,
    refreshRanking,
  } from '@/api/ranking';
  import { getIconUrl } from '@/utils/mapleStoryAPI';

  const { t } = useI18n();
  const activeTab = ref('combat');
  const refreshing = ref(false);
  const combatLoading = ref(false);
  const equipLoading = ref(false);
  const combatRows = ref<CombatPowerRankItem[]>([]);
  const equipRows = ref<EquipScoreRankItem[]>([]);
  const jobOptions = ref<{ label: string; value: number }[]>([]);
  const slotOptions = ref<{ label: string; value: number }[]>([]);
  const combatForm = reactive({ filter: -1 });
  const equipForm = reactive({ filter: 0 });

  const toOptions = (list: RankingFilterOption[]) =>
    (list || []).map((o) => ({ label: o.name, value: o.id }));

  const loadOptions = async () => {
    const [jobs, slots] = await Promise.all([
      fetchJobNicheOptions(),
      fetchSlotCategoryOptions(),
    ]);
    jobOptions.value = toOptions(jobs.data as RankingFilterOption[]);
    slotOptions.value = toOptions(slots.data as RankingFilterOption[]);
  };

  const loadCombat = async () => {
    combatLoading.value = true;
    try {
      const res = await fetchCombatPowerRanking({
        filter: combatForm.filter,
        limit: 20,
      });
      combatRows.value = (res.data as CombatPowerRankItem[]) || [];
    } finally {
      combatLoading.value = false;
    }
  };

  const loadEquip = async () => {
    equipLoading.value = true;
    try {
      const res = await fetchEquipScoreRanking({
        filter: equipForm.filter,
        limit: 20,
      });
      equipRows.value = (res.data as EquipScoreRankItem[]) || [];
    } finally {
      equipLoading.value = false;
    }
  };

  const onTabChange = (key: string | number) => {
    if (key === 'combat') {
      loadCombat();
    } else {
      loadEquip();
    }
  };

  const onRefresh = async () => {
    refreshing.value = true;
    try {
      await refreshRanking();
      Message.success(t('message.success'));
      if (activeTab.value === 'combat') {
        await loadCombat();
      } else {
        await loadEquip();
      }
    } finally {
      refreshing.value = false;
    }
  };

  onMounted(async () => {
    await loadOptions();
    await loadCombat();
  });
</script>

<style scoped>
  .equip-icon {
    width: 32px;
    height: 32px;
    object-fit: contain;
  }
  .equip-tooltip {
    max-width: 320px;
    font-size: 12px;
    line-height: 1.6;
  }
  .equip-tooltip__title {
    font-weight: 600;
    margin-bottom: 4px;
  }
</style>
