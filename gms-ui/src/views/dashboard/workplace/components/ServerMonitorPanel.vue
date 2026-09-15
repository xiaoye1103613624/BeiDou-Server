<template>
  <ProCard :title="t('workplace.monitor.server.title')">
    <a-spin :loading="loading" style="width: 100%">
      <template v-if="info">
        <a-row :gutter="12" class="monitor-summary">
          <a-col :xs="12" :sm="8" :md="6" :lg="4">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.server.uptime') }}
              </div>
              <div class="stat-item__value">{{ uptimeText }}</div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6" :lg="4">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.server.threads') }}
              </div>
              <div class="stat-item__value">
                {{ info.jvm.threadCount }}
                <span class="stat-item__sub">
                  ({{ info.jvm.daemonThreadCount }})
                </span>
              </div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6" :lg="4">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.server.processCpu') }}
              </div>
              <div class="stat-item__value">{{ processCpuText }}</div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6" :lg="4">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.server.systemCpu') }}
              </div>
              <div class="stat-item__value">{{ systemCpuText }}</div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6" :lg="4">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.server.heapUsage') }}
              </div>
              <div class="stat-item__value">{{ heapUsageText }}</div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6" :lg="4">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.server.systemMemory') }}
              </div>
              <div class="stat-item__value">{{ systemMemoryText }}</div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6" :lg="4">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.server.version') }}
              </div>
              <div class="stat-item__value">{{ info.game.version || '-' }}</div>
            </div>
          </a-col>
        </a-row>

        <a-row :gutter="16" class="monitor-charts">
          <a-col :xs="24" :lg="8">
            <div class="chart-box">
              <div class="chart-box__title">
                {{ t('workplace.monitor.server.chart.memory') }}
              </div>
              <Chart :options="memoryOption" height="240px" />
            </div>
          </a-col>
          <a-col :xs="24" :lg="8">
            <div class="chart-box">
              <div class="chart-box__title">
                {{ t('workplace.monitor.server.chart.cpu') }}
              </div>
              <Chart :options="cpuOption" height="240px" />
            </div>
          </a-col>
          <a-col :xs="24" :lg="8">
            <div class="chart-box">
              <div class="chart-box__title">
                {{ t('workplace.monitor.server.chart.gc') }}
              </div>
              <Chart :options="gcOption" height="240px" />
            </div>
          </a-col>
          <a-col :xs="24" :lg="12">
            <div class="chart-box">
              <div class="chart-box__title">
                {{ t('workplace.monitor.server.chart.worldOnline') }}
              </div>
              <Chart :options="worldOption" height="240px" />
            </div>
          </a-col>
          <a-col :xs="24" :lg="12">
            <div class="chart-box">
              <div class="chart-box__title">
                {{ t('workplace.monitor.server.chart.channelOnline') }}
              </div>
              <Chart :options="channelOption" height="240px" />
            </div>
          </a-col>
        </a-row>
      </template>
      <a-empty v-else :description="t('workplace.monitor.empty')" />
    </a-spin>
  </ProCard>
</template>

<script lang="ts" setup>
  import { computed } from 'vue';
  import { useI18n } from 'vue-i18n';
  import type { ServerMonitorInfo } from '@/api/monitor';
  import useChartOption from '@/hooks/chart-option';

  const props = defineProps<{
    loading: boolean;
    info: ServerMonitorInfo | null;
  }>();

  const { t } = useI18n();

  const formatBytes = (bytes: number) => {
    if (!bytes || bytes < 0) {
      return '0 B';
    }
    const units = ['B', 'KB', 'MB', 'GB', 'TB'];
    let value = bytes;
    let idx = 0;
    while (value >= 1024 && idx < units.length - 1) {
      value /= 1024;
      idx += 1;
    }
    return `${value.toFixed(value >= 10 || idx === 0 ? 0 : 1)} ${units[idx]}`;
  };

  const formatDuration = (ms: number) => {
    const totalSec = Math.max(0, Math.floor(ms / 1000));
    const days = Math.floor(totalSec / 86400);
    const hours = Math.floor((totalSec % 86400) / 3600);
    const minutes = Math.floor((totalSec % 3600) / 60);
    const seconds = totalSec % 60;
    if (days > 0) {
      return `${days}d ${hours}h ${minutes}m`;
    }
    if (hours > 0) {
      return `${hours}h ${minutes}m ${seconds}s`;
    }
    if (minutes > 0) {
      return `${minutes}m ${seconds}s`;
    }
    return `${seconds}s`;
  };

  const formatCpu = (load: number) => {
    if (load == null || load < 0) {
      return t('workplace.monitor.na');
    }
    return `${(load * 100).toFixed(1)}%`;
  };

  const uptimeText = computed(() =>
    props.info ? formatDuration(props.info.jvm.uptimeMs) : '-'
  );
  const processCpuText = computed(() =>
    props.info ? formatCpu(props.info.os.processCpuLoad) : '-'
  );
  const systemCpuText = computed(() =>
    props.info ? formatCpu(props.info.os.systemCpuLoad) : '-'
  );
  const heapUsageText = computed(() => {
    if (!props.info) {
      return '-';
    }
    const { heapUsed, heapMax } = props.info.jvm;
    if (!heapMax) {
      return formatBytes(heapUsed);
    }
    return `${((heapUsed / heapMax) * 100).toFixed(1)}%`;
  });

  /** OS 物理内存：已用 / 总量（不可用时显示 N/A） */
  const systemMemoryText = computed(() => {
    if (!props.info) {
      return '-';
    }
    const { totalPhysicalMemory, freePhysicalMemory } = props.info.os;
    if (!totalPhysicalMemory || totalPhysicalMemory <= 0) {
      return t('workplace.monitor.na');
    }
    const used = Math.max(totalPhysicalMemory - Math.max(freePhysicalMemory, 0), 0);
    return `${formatBytes(used)} / ${formatBytes(totalPhysicalMemory)}`;
  });

  const { chartOption: memoryOption } = useChartOption((isDark) => {
    const jvm = props.info?.jvm;
    const heapUsed = jvm?.heapUsed ?? 0;
    const heapFree = Math.max((jvm?.heapMax ?? 0) - heapUsed, 0);
    const nonHeapUsed = jvm?.nonHeapUsed ?? 0;
    const nonHeapFree = Math.max((jvm?.nonHeapMax ?? 0) - nonHeapUsed, 0);
    return {
      tooltip: {
        trigger: 'item',
        formatter: (params: unknown) => {
          const item = params as {
            name?: string;
            value?: number;
            percent?: number;
          };
          return `${item.name}<br/>${formatBytes(Number(item.value) || 0)} (${
            item.percent ?? 0
          }%)`;
        },
      },
      legend: {
        bottom: 0,
        textStyle: { color: isDark ? '#ccc' : '#4e5969' },
      },
      series: [
        {
          type: 'pie',
          radius: ['42%', '68%'],
          center: ['50%', '45%'],
          label: { show: false },
          data: [
            {
              name: t('workplace.monitor.server.heapUsed'),
              value: heapUsed,
            },
            {
              name: t('workplace.monitor.server.heapFree'),
              value: heapFree,
            },
            {
              name: t('workplace.monitor.server.nonHeapUsed'),
              value: nonHeapUsed,
            },
            {
              name: t('workplace.monitor.server.nonHeapFree'),
              value: nonHeapFree,
            },
          ],
        },
      ],
    };
  });

  const { chartOption: cpuOption } = useChartOption((isDark) => {
    const process =
      props.info && props.info.os.processCpuLoad >= 0
        ? Math.round(props.info.os.processCpuLoad * 1000) / 10
        : 0;
    const system =
      props.info && props.info.os.systemCpuLoad >= 0
        ? Math.round(props.info.os.systemCpuLoad * 1000) / 10
        : 0;
    return {
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 16, top: 24, bottom: 32 },
      xAxis: {
        type: 'category',
        data: [
          t('workplace.monitor.server.processCpu'),
          t('workplace.monitor.server.systemCpu'),
        ],
        axisLabel: { color: isDark ? '#ccc' : '#4e5969' },
      },
      yAxis: {
        type: 'value',
        max: 100,
        axisLabel: {
          formatter: '{value}%',
          color: isDark ? '#ccc' : '#4e5969',
        },
      },
      series: [
        {
          type: 'bar',
          barWidth: 36,
          data: [process, system],
          itemStyle: {
            color: '#2a8fb5',
            borderRadius: [6, 6, 0, 0],
          },
        },
      ],
    };
  });

  const { chartOption: gcOption } = useChartOption((isDark) => {
    const gc = props.info?.jvm?.gc ?? [];
    return {
      tooltip: { trigger: 'axis' },
      legend: {
        data: [
          t('workplace.monitor.server.gcCount'),
          t('workplace.monitor.server.gcTime'),
        ],
        textStyle: { color: isDark ? '#ccc' : '#4e5969' },
      },
      grid: { left: 48, right: 48, top: 36, bottom: 48 },
      xAxis: {
        type: 'category',
        data: gc.map((item) => item.name),
        axisLabel: {
          color: isDark ? '#ccc' : '#4e5969',
          rotate: gc.length > 2 ? 20 : 0,
        },
      },
      yAxis: [
        {
          type: 'value',
          name: t('workplace.monitor.server.gcCount'),
          axisLabel: { color: isDark ? '#ccc' : '#4e5969' },
        },
        {
          type: 'value',
          name: t('workplace.monitor.server.gcTime'),
          axisLabel: { color: isDark ? '#ccc' : '#4e5969' },
        },
      ],
      series: [
        {
          name: t('workplace.monitor.server.gcCount'),
          type: 'bar',
          data: gc.map((item) => item.collectionCount),
          itemStyle: { color: '#22c55e' },
        },
        {
          name: t('workplace.monitor.server.gcTime'),
          type: 'bar',
          yAxisIndex: 1,
          data: gc.map((item) => item.collectionTimeMs),
          itemStyle: { color: '#f59e0b' },
        },
      ],
    };
  });

  const { chartOption: worldOption } = useChartOption((isDark) => {
    const worlds = props.info?.game?.worlds ?? [];
    return {
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 16, top: 24, bottom: 32 },
      xAxis: {
        type: 'category',
        data: worlds.map((w) => `W${w.id}`),
        axisLabel: { color: isDark ? '#ccc' : '#4e5969' },
      },
      yAxis: {
        type: 'value',
        minInterval: 1,
        axisLabel: { color: isDark ? '#ccc' : '#4e5969' },
      },
      series: [
        {
          type: 'bar',
          data: worlds.map((w) => w.playerCount),
          itemStyle: { color: '#6366f1', borderRadius: [6, 6, 0, 0] },
        },
      ],
    };
  });

  const { chartOption: channelOption } = useChartOption((isDark) => {
    const channels =
      props.info?.game?.worlds?.flatMap((world) =>
        (world.channels || []).map((channel) => ({
          label: `W${world.id}-C${channel.id}`,
          count: channel.playerCount,
        }))
      ) ?? [];
    return {
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 16, top: 24, bottom: 48 },
      xAxis: {
        type: 'category',
        data: channels.map((c) => c.label),
        axisLabel: {
          color: isDark ? '#ccc' : '#4e5969',
          rotate: channels.length > 8 ? 30 : 0,
        },
      },
      yAxis: {
        type: 'value',
        minInterval: 1,
        axisLabel: { color: isDark ? '#ccc' : '#4e5969' },
      },
      series: [
        {
          type: 'bar',
          data: channels.map((c) => c.count),
          itemStyle: { color: '#0ea5e9', borderRadius: [6, 6, 0, 0] },
        },
      ],
    };
  });
</script>

<style lang="less" scoped>
  .monitor-summary {
    margin-bottom: 8px;
  }

  .stat-item {
    padding: 10px 12px;
    margin-bottom: 12px;
    background: var(--bd-surface-muted, var(--color-fill-2));
    border-radius: var(--bd-radius-md, 8px);

    &__label {
      color: var(--color-text-3);
      font-size: 12px;
    }

    &__value {
      margin-top: 4px;
      color: var(--color-text-1);
      font-weight: 600;
      font-size: 16px;
      word-break: break-all;
    }

    &__sub {
      margin-left: 4px;
      color: var(--color-text-3);
      font-weight: 400;
      font-size: 12px;
    }
  }

  .chart-box {
    margin-bottom: 12px;
    padding: 12px;
    background: var(--bd-surface-muted, var(--color-fill-2));
    border-radius: var(--bd-radius-md, 8px);

    &__title {
      margin-bottom: 8px;
      color: var(--color-text-2);
      font-weight: 600;
      font-size: 13px;
    }
  }
</style>
