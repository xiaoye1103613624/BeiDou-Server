<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('mi.title')">
      <a-tabs v-model:active-key="activeTab" @change="onTabChange">
        <!-- ==================== 快速攻城 ==================== -->
        <a-tab-pane key="quick" :title="$t('mi.tab.quick')">
          <a-form :model="quickForm" layout="vertical" style="max-width: 600px">
            <a-form-item :label="$t('mi.difficulty')">
              <a-select
                v-model="quickForm.presetIndex"
                :placeholder="$t('mi.difficulty')"
              >
                <a-option
                  v-for="(p, i) in presets"
                  :key="i"
                  :value="i"
                  :label="p.name"
                >
                  {{ p.name }} - {{ p.desc }}
                </a-option>
              </a-select>
            </a-form-item>
            <a-form-item :label="$t('mi.town')">
              <a-select v-model="quickForm.mapId" :placeholder="$t('mi.town')">
                <a-option
                  v-for="t in towns"
                  :key="t.mapId"
                  :value="t.mapId"
                  :label="t.name"
                />
              </a-select>
            </a-form-item>
            <a-form-item :label="$t('mi.channel')">
              <a-select
                v-model="quickForm.channelId"
                :placeholder="$t('mi.channel')"
              >
                <a-option
                  v-for="ch in channels"
                  :key="ch.id"
                  :value="ch.id"
                  :label="`频道${ch.id}`"
                >
                  {{ `频道${ch.id}` }} —
                  {{ $t('mi.label.online', { n: ch.online }) }}
                </a-option>
              </a-select>
            </a-form-item>
            <a-form-item :label="$t('mi.duration')">
              <a-input-number
                v-model="quickForm.duration"
                :min="1"
                :max="120"
              />
            </a-form-item>
            <a-form-item>
              <a-button
                type="primary"
                :loading="quickLoading"
                @click="doQuickStart"
              >
                {{ $t('mi.btn.start') }}
              </a-button>
            </a-form-item>
          </a-form>
          <!-- 预览 -->
          <div v-if="selectedPreset" style="margin-top: 16px">
            <a-divider>{{ $t('mi.waves') }}</a-divider>
            <div
              v-for="(w, i) in selectedPreset.waves"
              :key="i"
              class="wave-preview"
            >
              <strong>{{ $t('mi.wave', { n: i + 1 }) }}</strong>
              ({{ $t('mi.wave.delay') }}: {{ w.delay }}s):
              {{
                w.mobs
                  .map((m: any) =>
                    $t('mi.label.mobCount', { name: m.mobId, count: m.count })
                  )
                  .join(', ')
              }}
            </div>
            <a-divider>{{ $t('mi.reward') }}</a-divider>
            <div class="reward-preview">
              <span v-if="selectedPreset.expRate > 1"
                >EXP {{ selectedPreset.expRate }}x
                {{ selectedPreset.expDur }}min
              </span>
              <span v-if="selectedPreset.dropRate > 1"
                >Drop {{ selectedPreset.dropRate }}x
                {{ selectedPreset.dropDur }}min
              </span>
              <span v-if="selectedPreset.mesoRate > 1"
                >Meso {{ selectedPreset.mesoRate }}x
                {{ selectedPreset.mesoDur }}min
              </span>
              <span v-if="selectedPreset.cash > 0"
                >Cash {{ selectedPreset.cash }}
              </span>
              <span v-if="selectedPreset.meso > 0"
                >Meso {{ selectedPreset.meso.toLocaleString() }}</span
              >
            </div>
          </div>
        </a-tab-pane>

        <!-- ==================== 自定义攻城 ==================== -->
        <a-tab-pane key="custom" :title="$t('mi.tab.custom')">
          <!-- 步骤条 -->
          <a-steps :current="customStep" style="margin-bottom: 20px">
            <a-step :description="$t('mi.town') + ' & ' + $t('mi.channel')" />
            <a-step :description="$t('mi.waves')" />
            <a-step :description="$t('mi.reward')" />
            <a-step :description="$t('mi.confirm.title')" />
          </a-steps>

          <!-- Step 0: 选择城镇/线路/时长 -->
          <div v-if="customStep === 0" style="max-width: 500px">
            <a-form layout="vertical">
              <a-form-item :label="$t('mi.town')">
                <a-select
                  v-model="customForm.mapId"
                  :placeholder="$t('mi.town')"
                >
                  <a-option
                    v-for="t in towns"
                    :key="t.mapId"
                    :value="t.mapId"
                    :label="t.name"
                  />
                </a-select>
              </a-form-item>
              <a-form-item :label="$t('mi.channel')">
                <a-select
                  v-model="customForm.channelId"
                  :placeholder="$t('mi.channel')"
                >
                  <a-option
                    v-for="ch in channels"
                    :key="ch.id"
                    :value="ch.id"
                    :label="`频道${ch.id} — ${$t('mi.label.online', {
                      n: ch.online,
                    })}`"
                  />
                </a-select>
              </a-form-item>
              <a-form-item :label="$t('mi.duration')">
                <a-input-number
                  v-model="customForm.duration"
                  :min="1"
                  :max="120"
                />
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="customStep = 1">
                  {{ $t('mi.btn.next') }}
                </a-button>
              </a-form-item>
            </a-form>
          </div>

          <!-- Step 1: 配置波次 -->
          <div v-if="customStep === 1">
            <div v-for="(wave, wi) in customWaves" :key="wi" class="wave-card">
              <a-card
                :title="$t('mi.wave', { n: wi + 1 })"
                size="small"
                closable
                @close="() => customWaves.splice(wi, 1)"
              >
                <a-form layout="inline" style="margin-bottom: 8px">
                  <a-form-item :label="$t('mi.wave.delay')">
                    <a-input-number
                      v-model="wave.delaySeconds"
                      :min="0"
                      :max="600"
                      style="width: 120px"
                    />
                  </a-form-item>
                </a-form>
                <div v-for="(mob, mi) in wave.mobs" :key="mi" class="mob-row">
                  <a-tag closable @close="() => wave.mobs.splice(mi, 1)">
                    ID: {{ mob.mobId }} x{{ mob.count }}
                  </a-tag>
                </div>
                <div style="margin-top: 8px">
                  <a-space>
                    <a-input
                      v-model="mobSearchKeyword"
                      :placeholder="$t('mi.mob.keyword')"
                      style="width: 160px"
                      @keyup.enter="doSearchMobs(wi)"
                    />
                    <a-button size="small" @click="doSearchMobs(wi)">
                      {{ $t('mi.btn.search') }}
                    </a-button>
                  </a-space>
                  <div
                    v-if="mobSearchResults.length > 0 && currentWaveIdx === wi"
                    class="mob-search-results"
                  >
                    <div
                      v-for="r in mobSearchResults"
                      :key="r.mobId"
                      class="mob-search-item"
                    >
                      <span>{{ r.name }} (ID: {{ r.mobId }})</span>
                      <a-space>
                        <a-input-number
                          v-model="mobAddCount"
                          :min="1"
                          :max="200"
                          style="width: 80px"
                          size="mini"
                        />
                        <a-button
                          size="mini"
                          type="primary"
                          @click="addMobToWave(wi, r)"
                        >
                          {{ $t('mi.wave.addMob') }}
                        </a-button>
                      </a-space>
                    </div>
                  </div>
                </div>
              </a-card>
            </div>
            <a-button
              type="outline"
              :disabled="customWaves.length >= 5"
              style="margin-top: 12px"
              @click="addWave"
            >
              {{ $t('mi.wave.addWave') }} ({{ customWaves.length }}/5)
            </a-button>
            <div style="margin-top: 16px">
              <a-space>
                <a-button @click="customStep = 0">{{
                  $t('mi.btn.back')
                }}</a-button>
                <a-button
                  type="primary"
                  :disabled="customWaves.length === 0"
                  @click="customStep = 2"
                >
                  {{ $t('mi.btn.next') }}
                </a-button>
              </a-space>
            </div>
          </div>

          <!-- Step 2: 配置奖励 -->
          <div v-if="customStep === 2" style="max-width: 500px">
            <a-form layout="vertical">
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item :label="$t('mi.reward.expRate')">
                    <a-input-number
                      v-model="rewardForm.expRate"
                      :min="1"
                      :max="10"
                      :step="0.5"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item :label="$t('mi.reward.expDur')">
                    <a-input-number
                      v-model="rewardForm.expDur"
                      :min="0"
                      :max="480"
                    />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item :label="$t('mi.reward.dropRate')">
                    <a-input-number
                      v-model="rewardForm.dropRate"
                      :min="0"
                      :max="10"
                      :step="0.5"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item :label="$t('mi.reward.dropDur')">
                    <a-input-number
                      v-model="rewardForm.dropDur"
                      :min="0"
                      :max="480"
                    />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item :label="$t('mi.reward.mesoRate')">
                    <a-input-number
                      v-model="rewardForm.mesoRate"
                      :min="0"
                      :max="10"
                      :step="0.5"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item :label="$t('mi.reward.mesoDur')">
                    <a-input-number
                      v-model="rewardForm.mesoDur"
                      :min="0"
                      :max="480"
                    />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-form-item :label="$t('mi.reward.cash')">
                <a-input-number
                  v-model="rewardForm.cash"
                  :min="0"
                  :max="100000"
                />
              </a-form-item>
              <a-form-item :label="$t('mi.reward.meso')">
                <a-input-number
                  v-model="rewardForm.meso"
                  :min="0"
                  :max="2000000000"
                />
              </a-form-item>
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item :label="$t('mi.reward.itemId')">
                    <a-input-number v-model="rewardForm.itemId" :min="0" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item :label="$t('mi.reward.itemCount')">
                    <a-input-number
                      v-model="rewardForm.itemCount"
                      :min="1"
                      :max="200"
                      :disabled="rewardForm.itemId === 0"
                    />
                  </a-form-item>
                </a-col>
              </a-row>
            </a-form>
            <a-space>
              <a-button @click="customStep = 1">{{
                $t('mi.btn.back')
              }}</a-button>
              <a-button type="primary" @click="customStep = 3">{{
                $t('mi.btn.next')
              }}</a-button>
            </a-space>
          </div>

          <!-- Step 3: 确认 -->
          <div v-if="customStep === 3">
            <h4>{{ $t('mi.confirm.title') }}</h4>
            <a-descriptions bordered :column="2" size="small">
              <a-descriptions-item :label="$t('mi.town')">
                {{
                  towns.find((t: any) => t.mapId === customForm.mapId)?.name ||
                  customForm.mapId
                }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('mi.channel')">{{
                customForm.channelId
              }}</a-descriptions-item>
              <a-descriptions-item :label="$t('mi.duration')"
                >{{ customForm.duration }} min</a-descriptions-item
              >
              <a-descriptions-item :label="$t('mi.waves')">{{
                customWaves.length
              }}</a-descriptions-item>
            </a-descriptions>
            <div style="margin-top: 12px">
              <a-space>
                <a-button @click="customStep = 2">{{
                  $t('mi.btn.back')
                }}</a-button>
                <a-button
                  type="primary"
                  :loading="customLoading"
                  @click="doCustomStart"
                >
                  {{ $t('mi.btn.start') }}
                </a-button>
              </a-space>
            </div>
          </div>
        </a-tab-pane>

        <!-- ==================== 攻城状态 ==================== -->
        <a-tab-pane key="status" :title="$t('mi.tab.status')">
          <div v-if="!invasionStatus || !invasionStatus.active">
            <a-empty :description="$t('mi.tab.status.empty')" />
            <a-button
              type="primary"
              style="margin-top: 12px"
              @click="doRefreshStatus"
            >
              {{ $t('mi.btn.refresh') }}
            </a-button>
          </div>
          <div v-else>
            <a-descriptions bordered :column="2" size="small">
              <a-descriptions-item :label="$t('mi.status.mapId')">{{
                invasionStatus.mapId
              }}</a-descriptions-item>
              <a-descriptions-item :label="$t('mi.status.channel')">{{
                invasionStatus.channelId
              }}</a-descriptions-item>
              <a-descriptions-item :label="$t('mi.status.alive')">{{
                invasionStatus.monstersAlive
              }}</a-descriptions-item>
              <a-descriptions-item :label="$t('mi.status.total')">{{
                invasionStatus.totalMonsters
              }}</a-descriptions-item>
              <a-descriptions-item :label="$t('mi.status.elapsed')"
                >{{
                  Math.floor(invasionStatus.elapsedSec / 60)
                }}
                min</a-descriptions-item
              >
              <a-descriptions-item :label="$t('mi.status.remaining')"
                >{{
                  Math.max(0, Math.floor(invasionStatus.remainingSec / 60))
                }}
                min</a-descriptions-item
              >
              <a-descriptions-item :label="$t('mi.status.players')">{{
                invasionStatus.participants
              }}</a-descriptions-item>
            </a-descriptions>
            <a-space style="margin-top: 16px">
              <a-button @click="doRefreshStatus">{{
                $t('mi.btn.refresh')
              }}</a-button>
              <a-popconfirm
                :content="$t('mi.msg.cancelConfirm')"
                @ok="doCancel"
              >
                <a-button type="primary" status="danger">{{
                  $t('mi.btn.cancel')
                }}</a-button>
              </a-popconfirm>
            </a-space>
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script lang="ts"></script>

<script lang="ts" setup>
  import { ref, reactive, computed, onMounted } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import useLoading from '@/hooks/loading';
  import {
    getPresets,
    getTowns,
    getChannels,
    searchMobs,
    startInvasion,
    cancelInvasion,
    getStatus,
  } from '@/api/monsterInvasion';

  const { loading: quickLoading, setLoading: setQuickLoading } = useLoading();
  const { loading: customLoading, setLoading: setCustomLoading } = useLoading();

  const activeTab = ref('quick');

  // ==================== 静态数据 ====================
  const presets = ref<any[]>([]);
  const towns = ref<any[]>([]);
  const channels = ref<any[]>([]);

  // ==================== 快速攻城 ====================
  const quickForm = reactive({
    presetIndex: 0,
    mapId: 100000000,
    channelId: 1,
    duration: 10,
  });

  const selectedPreset = computed(() => {
    return presets.value[quickForm.presetIndex] || null;
  });

  async function doQuickStart() {
    setQuickLoading(true);
    try {
      const p = selectedPreset.value;
      if (!p) return;
      const res = await startInvasion({
        worldId: 0,
        channelId: quickForm.channelId,
        mapId: quickForm.mapId,
        durationSeconds: quickForm.duration * 60,
        waves: p.waves,
        expRate: p.expRate,
        expDurationMin: p.expDur,
        dropRate: p.dropRate,
        dropDurationMin: p.dropDur,
        mesoRate: p.mesoRate,
        mesoDurationMin: p.mesoDur,
        cashReward: p.cash,
        mesoReward: p.meso,
        rewardItemId: p.itemId || 0,
        rewardItemCount: p.itemCount || 0,
      });
      const { data } = res.data;
      if (data.success) {
        Message.success('攻城已启动！');
      } else {
        Message.error('该世界已有进行中的攻城！');
      }
    } finally {
      setQuickLoading(false);
    }
  }

  // ==================== 自定义攻城 ====================
  const customStep = ref(0);

  const customForm = reactive({
    mapId: 100000000,
    channelId: 1,
    duration: 10,
  });

  interface MobEntry {
    mobId: number;
    count: number;
  }

  interface WaveConfig {
    delaySeconds: number;
    mobs: MobEntry[];
  }

  const customWaves = ref<WaveConfig[]>([{ delaySeconds: 0, mobs: [] }]);

  const mobSearchKeyword = ref('');
  const mobSearchResults = ref<any[]>([]);
  const mobAddCount = ref(10);
  const currentWaveIdx = ref(-1);

  const rewardForm = reactive({
    expRate: 2,
    expDur: 30,
    dropRate: 1,
    dropDur: 0,
    mesoRate: 1,
    mesoDur: 0,
    cash: 0,
    meso: 0,
    itemId: 0,
    itemCount: 1,
  });

  function addWave() {
    if (customWaves.value.length >= 5) return;
    customWaves.value.push({ delaySeconds: 0, mobs: [] });
  }

  async function doSearchMobs(waveIdx: number) {
    currentWaveIdx.value = waveIdx;
    const kw = mobSearchKeyword.value.trim();
    if (!kw) return;
    try {
      const res = await searchMobs(kw);
      mobSearchResults.value = res.data.data || [];
      if (mobSearchResults.value.length === 0) {
        Message.info('未找到匹配的怪物。');
      }
    } catch {
      mobSearchResults.value = [];
    }
  }

  function addMobToWave(waveIdx: number, mob: any) {
    const wave = customWaves.value[waveIdx];
    if (!wave) return;
    const existing = wave.mobs.find((m: MobEntry) => m.mobId === mob.mobId);
    if (existing) {
      existing.count += mobAddCount.value;
    } else {
      wave.mobs.push({ mobId: mob.mobId, count: mobAddCount.value });
    }
    mobSearchResults.value = [];
    mobSearchKeyword.value = '';
    currentWaveIdx.value = -1;
  }

  async function doCustomStart() {
    // 校验
    if (
      customWaves.value.length === 0 ||
      customWaves.value.every((w: WaveConfig) => w.mobs.length === 0)
    ) {
      Message.warning('请至少添加一个波次。');
      return;
    }
    setCustomLoading(true);
    try {
      const res = await startInvasion({
        worldId: 0,
        channelId: customForm.channelId,
        mapId: customForm.mapId,
        durationSeconds: customForm.duration * 60,
        waves: customWaves.value,
        expRate: rewardForm.expRate,
        expDurationMin: rewardForm.expDur,
        dropRate: rewardForm.dropRate,
        dropDurationMin: rewardForm.dropDur,
        mesoRate: rewardForm.mesoRate,
        mesoDurationMin: rewardForm.mesoDur,
        cashReward: rewardForm.cash,
        mesoReward: rewardForm.meso,
        rewardItemId: rewardForm.itemId,
        rewardItemCount: rewardForm.itemCount,
      });
      const { data } = res.data;
      if (data.success) {
        Message.success('攻城已启动！');
        activeTab.value = 'status';
        customStep.value = 0;
      } else {
        Message.error('该世界已有进行中的攻城！');
      }
    } finally {
      setCustomLoading(false);
    }
  }

  // ==================== 攻城状态 ====================
  const invasionStatus = ref<any>(null);

  async function doRefreshStatus() {
    try {
      const res = await getStatus(0);
      invasionStatus.value = res.data.data;
    } catch {
      invasionStatus.value = null;
    }
  }

  async function doCancel() {
    try {
      const res = await cancelInvasion(0);
      if (res.data.data.success) {
        Message.success('攻城已取消。');
        invasionStatus.value = null;
      }
    } catch {
      Message.error('Failed');
    }
  }

  function onTabChange(key: string) {
    if (key === 'status') {
      doRefreshStatus();
    }
  }

  // ==================== 初始化 ====================
  onMounted(async () => {
    try {
      const [presetRes, townRes, channelRes] = await Promise.all([
        getPresets(),
        getTowns(),
        getChannels(0),
      ]);
      presets.value = presetRes.data.data || [];
      towns.value = townRes.data.data || [];
      channels.value = channelRes.data.data || [];
      if (towns.value.length > 0) {
        quickForm.mapId = towns.value[0].mapId;
        customForm.mapId = towns.value[0].mapId;
      }
      if (channels.value.length > 0) {
        quickForm.channelId = channels.value[0].id;
        customForm.channelId = channels.value[0].id;
      }
    } catch (e) {
      console.error('Failed to load initial data', e);
    }
  });
</script>

<script lang="ts">
  export default { name: 'MonsterInvasion' };
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px;
  }
  .wave-preview {
    padding: 4px 0;
  }
  .reward-preview {
    color: var(--color-text-2);
  }
  .wave-card {
    margin-bottom: 12px;
  }
  .mob-row {
    display: inline-block;
    margin-right: 6px;
    margin-bottom: 4px;
  }
  .mob-search-results {
    margin-top: 8px;
    border: 1px solid var(--color-border-2);
    border-radius: 4px;
    padding: 8px;
    max-height: 200px;
    overflow-y: auto;
  }
  .mob-search-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 0;
    border-bottom: 1px solid var(--color-border-1);
  }
</style>
