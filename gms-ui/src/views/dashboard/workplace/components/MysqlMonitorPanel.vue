<template>
  <ProCard :title="t('workplace.monitor.mysql.title')">
    <a-spin :loading="loading" style="width: 100%">
      <template v-if="info">
        <a-row :gutter="12" class="monitor-summary">
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.version') }}
              </div>
              <div class="stat-item__value">{{ info.version || '-' }}</div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.database') }}
              </div>
              <div class="stat-item__value">
                {{ info.databaseName || '-' }}
              </div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.uptime') }}
              </div>
              <div class="stat-item__value">{{ uptimeText }}</div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.dbSize') }}
              </div>
              <div class="stat-item__value">
                {{ formatBytes(info.databaseSizeBytes) }}
              </div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.connections') }}
              </div>
              <div class="stat-item__value">
                {{ info.threadsConnected }} /
                {{ info.maxConnections || '-' }}
              </div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.threadsRunning') }}
              </div>
              <div class="stat-item__value">{{ info.threadsRunning }}</div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.questions') }}
              </div>
              <div class="stat-item__value">
                {{ formatNumber(info.questions) }}
              </div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.slowQueries') }}
              </div>
              <div class="stat-item__value">{{ info.slowQueries }}</div>
            </div>
          </a-col>
        </a-row>

        <div class="section-title">
          {{ t('workplace.monitor.mysql.poolTitle') }}
        </div>
        <a-alert
          v-if="!info.pool?.available"
          type="warning"
          :content="t('workplace.monitor.mysql.poolUnavailable')"
          style="margin-bottom: 12px"
        />
        <a-row v-else :gutter="12" class="monitor-summary">
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.poolActive') }}
              </div>
              <div class="stat-item__value">
                {{ info.pool.activeCount }} / {{ info.pool.maxActive }}
              </div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.poolIdle') }}
              </div>
              <div class="stat-item__value">{{ info.pool.poolingCount }}</div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.poolWait') }}
              </div>
              <div class="stat-item__value">
                {{ info.pool.waitThreadCount }}
              </div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.poolConnectError') }}
              </div>
              <div class="stat-item__value">
                {{ info.pool.connectErrorCount }}
              </div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.poolConnectCount') }}
              </div>
              <div class="stat-item__value">
                {{ formatNumber(info.pool.connectCount) }}
              </div>
            </div>
          </a-col>
          <a-col :xs="12" :sm="8" :md="6">
            <div class="stat-item">
              <div class="stat-item__label">
                {{ t('workplace.monitor.mysql.poolCreateDestroy') }}
              </div>
              <div class="stat-item__value">
                {{ formatNumber(info.pool.createCount) }} /
                {{ formatNumber(info.pool.destroyCount) }}
              </div>
            </div>
          </a-col>
        </a-row>

        <div class="section-title">
          {{ t('workplace.monitor.mysql.trendTitle') }}
        </div>
        <div class="chart-box">
          <Chart :options="trendOption" height="280px" />
        </div>
      </template>
      <a-empty v-else :description="t('workplace.monitor.empty')" />
    </a-spin>
  </ProCard>
</template>

<script lang="ts" setup>
  import { computed } from 'vue';
  import { useI18n } from 'vue-i18n';
  import type { MysqlMonitorInfo } from '@/api/monitor';
  import useChartOption from '@/hooks/chart-option';
  import type { MonitorTrendPoint } from '../hooks/useMonitorRefresh';

  const props = defineProps<{
    loading: boolean;
    info: MysqlMonitorInfo | null;
    trendPoints: MonitorTrendPoint[];
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

  const formatNumber = (value: number) => {
    if (value == null) {
      return '0';
    }
    return value.toLocaleString();
  };

  const formatDuration = (totalSec: number) => {
    const sec = Math.max(0, Math.floor(totalSec));
    const days = Math.floor(sec / 86400);
    const hours = Math.floor((sec % 86400) / 3600);
    const minutes = Math.floor((sec % 3600) / 60);
    if (days > 0) {
      return `${days}d ${hours}h ${minutes}m`;
    }
    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    }
    return `${minutes}m ${sec % 60}s`;
  };

  const uptimeText = computed(() =>
    props.info ? formatDuration(props.info.uptimeSeconds) : '-'
  );

  const { chartOption: trendOption } = useChartOption((isDark) => {
    const points = props.trendPoints || [];
    const axisColor = isDark ? '#ccc' : '#4e5969';
    return {
      tooltip: { trigger: 'axis' },
      legend: {
        data: [
          t('workplace.monitor.mysql.trend.connections'),
          t('workplace.monitor.mysql.trend.poolActive'),
          t('workplace.monitor.mysql.trend.heap'),
          t('workplace.monitor.mysql.trend.cpu'),
        ],
        textStyle: { color: axisColor },
      },
      grid: { left: 48, right: 48, top: 40, bottom: 36 },
      xAxis: {
        type: 'category',
        data: points.map((p) => p.time),
        axisLabel: { color: axisColor },
      },
      yAxis: [
        {
          type: 'value',
          name: t('workplace.monitor.mysql.trend.leftAxis'),
          minInterval: 1,
          axisLabel: { color: axisColor },
        },
        {
          type: 'value',
          name: t('workplace.monitor.mysql.trend.rightAxis'),
          axisLabel: { color: axisColor },
        },
      ],
      series: [
        {
          name: t('workplace.monitor.mysql.trend.connections'),
          type: 'line',
          smooth: true,
          data: points.map((p) => p.threadsConnected),
        },
        {
          name: t('workplace.monitor.mysql.trend.poolActive'),
          type: 'line',
          smooth: true,
          data: points.map((p) => p.poolActive),
        },
        {
          name: t('workplace.monitor.mysql.trend.heap'),
          type: 'line',
          smooth: true,
          yAxisIndex: 1,
          data: points.map((p) => p.heapUsedMb),
        },
        {
          name: t('workplace.monitor.mysql.trend.cpu'),
          type: 'line',
          smooth: true,
          yAxisIndex: 1,
          data: points.map((p) => p.processCpuPercent),
        },
      ],
    };
  });
</script>

<style lang="less" scoped>
  .monitor-summary {
    margin-bottom: 4px;
  }

  .section-title {
    margin: 8px 0 12px;
    color: var(--color-text-1);
    font-weight: 600;
    font-size: 14px;
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
      font-size: 15px;
      word-break: break-all;
    }
  }

  .chart-box {
    padding: 12px;
    background: var(--bd-surface-muted, var(--color-fill-2));
    border-radius: var(--bd-radius-md, 8px);
  }
</style>
