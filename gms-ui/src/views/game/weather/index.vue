<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.weather')">
      <a-space direction="vertical" fill :size="16">
        <a-alert type="info">
          {{ $t('weather.hint.axes') }}
        </a-alert>
        <a-alert type="warning">
          {{ $t('weather.hint.auto') }}
        </a-alert>

        <a-card :title="$t('weather.current')" :bordered="true" size="small">
          <a-descriptions :column="2" size="small" bordered>
            <a-descriptions-item :label="$t('weather.clock')">
              {{ status.clock || '-' }}
            </a-descriptions-item>
            <a-descriptions-item :label="$t('weather.wallClock')">
              {{ status.wallClock || '-' }}
            </a-descriptions-item>
            <a-descriptions-item :label="$t('weather.nightLevel')">
              {{ nightLevelText }}
            </a-descriptions-item>
            <a-descriptions-item :label="$t('weather.sky')">
              {{ skyText }}
            </a-descriptions-item>
            <a-descriptions-item :label="$t('weather.timeFrozen')">
              <a-tag :color="status.timeFrozen ? 'orangered' : 'green'">
                {{ status.timeFrozen ? $t('weather.yes') : $t('weather.no') }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item :label="$t('weather.skyForced')">
              <a-tag :color="status.skyForced ? 'orangered' : 'green'">
                {{ status.skyForced ? $t('weather.yes') : $t('weather.no') }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item :label="$t('weather.bareSky')">
              <a-tag :color="status.bareSky ? 'purple' : 'gray'">
                {{ status.bareSky ? $t('weather.yes') : $t('weather.no') }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item :label="$t('weather.equivCmd')">
              <a-typography-text code>
                {{ status.equivalentCommand || '-' }}
              </a-typography-text>
            </a-descriptions-item>
          </a-descriptions>
          <a-space style="margin-top: 12px">
            <a-button :loading="loading" @click="refresh">
              {{ $t('weather.refresh') }}
            </a-button>
            <a-button
              type="primary"
              status="warning"
              :loading="applying"
              @click="onAuto"
            >
              {{ $t('weather.auto') }}
            </a-button>
          </a-space>
        </a-card>

        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-card :title="$t('weather.timeSection')" size="small">
              <a-radio-group v-model="form.time" direction="vertical">
                <a-radio
                  v-for="opt in timeOptions"
                  :key="opt.value"
                  :value="opt.value"
                >
                  {{ opt.label }}
                </a-radio>
              </a-radio-group>
              <a-form-item
                v-if="form.time === 'clock'"
                :label="$t('weather.clockInput')"
                style="margin-top: 12px; margin-bottom: 0"
              >
                <a-input
                  v-model="form.clock"
                  :placeholder="$t('weather.clockPlaceholder')"
                  allow-clear
                  style="max-width: 160px"
                />
              </a-form-item>
            </a-card>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-card :title="$t('weather.skySection')" size="small">
              <a-radio-group v-model="form.sky" direction="vertical">
                <a-radio
                  v-for="opt in skyOptions"
                  :key="opt.value"
                  :value="opt.value"
                >
                  {{ opt.label }}
                </a-radio>
              </a-radio-group>
            </a-card>
          </a-col>
        </a-row>

        <a-card size="small">
          <a-space wrap>
            <a-checkbox v-model="form.snap">
              {{ $t('weather.snap') }}
            </a-checkbox>
            <a-typography-text type="secondary">
              {{ $t('weather.preview') }}：
              <a-typography-text code>{{ previewCommand }}</a-typography-text>
            </a-typography-text>
          </a-space>
          <div style="margin-top: 12px">
            <a-button
              type="primary"
              :loading="applying"
              :disabled="!canApply"
              @click="onApply"
            >
              {{ $t('weather.apply') }}
            </a-button>
          </div>
        </a-card>
      </a-space>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { computed, onMounted, reactive, ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import {
    applyWeather,
    getWeatherStatus,
    WeatherOption,
    WeatherStatus,
  } from '@/api/weather';

  const { t } = useI18n();

  const loading = ref(false);
  const applying = ref(false);
  const status = ref<WeatherStatus>({});
  const timeOptions = ref<WeatherOption[]>([]);
  const skyOptions = ref<WeatherOption[]>([]);

  const form = reactive({
    time: 'keep',
    sky: 'keep',
    clock: '21:30',
    // Admin apply defaults to snap: fade is easy to miss and matches map-entry packets.
    snap: true,
  });

  const nightLevelText = computed(() => {
    const n = status.value.nightLevel;
    if (n === undefined || n === null) return '-';
    return Number(n).toFixed(2);
  });

  const skyText = computed(() => {
    const zh = status.value.skyNameZh;
    const en = status.value.skyName;
    if (zh && en && zh !== en) return `${zh}（${en}）`;
    return zh || en || '-';
  });

  const canApply = computed(() => {
    if (form.time !== 'keep' && form.time !== 'clock') return true;
    if (form.time === 'clock' && form.clock.trim()) return true;
    return form.sky !== 'keep';
  });

  const previewCommand = computed(() => {
    const parts: string[] = ['!weather'];
    if (form.time === 'release' && form.sky === 'release') {
      return '!weather auto';
    }
    if (form.time === 'clock') {
      const c = form.clock.trim();
      if (c) parts.push(c);
    } else if (form.time !== 'keep' && form.time !== 'release') {
      parts.push(form.time);
    }
    if (form.sky !== 'keep' && form.sky !== 'release') {
      parts.push(form.sky);
    }
    if (form.time === 'release' && form.sky === 'keep') {
      return '解除时段冻结（保留天空强制）';
    }
    if (form.sky === 'release' && form.time === 'keep') {
      return '解除天空强制（保留时段冻结）';
    }
    if (
      form.time === 'release' &&
      form.sky !== 'keep' &&
      form.sky !== 'release'
    ) {
      parts.shift();
      return `解除时段 + 强制天空 ${form.sky}`;
    }
    if (
      form.sky === 'release' &&
      form.time !== 'keep' &&
      form.time !== 'release'
    ) {
      return `!weather ${
        form.time === 'clock' ? form.clock.trim() : form.time
      } + 解除天空`;
    }
    if (parts.length === 1) return '（请选择时段或天空）';
    return parts.join(' ');
  });

  async function refresh() {
    loading.value = true;
    try {
      const res: any = await getWeatherStatus();
      const data = (res.data || {}) as WeatherStatus;
      status.value = data;
      if (data.timeOptions?.length) timeOptions.value = data.timeOptions;
      if (data.skyOptions?.length) skyOptions.value = data.skyOptions;
    } catch (e: any) {
      Message.error(e?.message || t('message.error'));
    } finally {
      loading.value = false;
    }
  }

  async function onApply() {
    if (!canApply.value) return;
    applying.value = true;
    try {
      const res: any = await applyWeather({
        auto: false,
        time: form.time,
        clock: form.time === 'clock' ? form.clock.trim() : undefined,
        sky: form.sky,
        snap: form.snap,
      });
      status.value = (res.data || {}) as WeatherStatus;
      Message.success(t('weather.apply.success'));
    } catch (e: any) {
      Message.error(e?.message || t('message.error'));
    } finally {
      applying.value = false;
    }
  }

  async function onAuto() {
    applying.value = true;
    try {
      const res: any = await applyWeather({ auto: true });
      status.value = (res.data || {}) as WeatherStatus;
      form.time = 'keep';
      form.sky = 'keep';
      Message.success(t('weather.auto.success'));
    } catch (e: any) {
      Message.error(e?.message || t('message.error'));
    } finally {
      applying.value = false;
    }
  }

  onMounted(refresh);
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px;
  }
</style>
