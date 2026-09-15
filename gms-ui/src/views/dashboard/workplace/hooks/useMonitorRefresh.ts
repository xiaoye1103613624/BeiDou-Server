import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { Message } from '@arco-design/web-vue';
import { useI18n } from 'vue-i18n';
import {
  getMysqlMonitor,
  getServerMonitor,
  type MysqlMonitorInfo,
  type ServerMonitorInfo,
} from '@/api/monitor';

export type AutoRefreshSeconds = 0 | 5 | 10 | 30;

export interface MonitorTrendPoint {
  time: string;
  threadsConnected: number;
  poolActive: number;
  heapUsedMb: number;
  processCpuPercent: number;
}

const TREND_MAX = 48;

function formatClock(ts: number) {
  const date = new Date(ts);
  const hh = String(date.getHours()).padStart(2, '0');
  const mm = String(date.getMinutes()).padStart(2, '0');
  const ss = String(date.getSeconds()).padStart(2, '0');
  return `${hh}:${mm}:${ss}`;
}

/**
 * 工作台监控轮询与前端趋势采样缓冲。
 */
export default function useMonitorRefresh() {
  const { t } = useI18n();
  const loading = ref(false);
  const autoRefresh = ref<AutoRefreshSeconds>(0);
  const serverInfo = ref<ServerMonitorInfo | null>(null);
  const mysqlInfo = ref<MysqlMonitorInfo | null>(null);
  const trendPoints = ref<MonitorTrendPoint[]>([]);
  const lastError = ref<string | null>(null);

  let timer: ReturnType<typeof setInterval> | null = null;

  const pushTrend = (
    server: ServerMonitorInfo | null,
    mysql: MysqlMonitorInfo | null
  ) => {
    if (!server && !mysql) {
      return;
    }
    const sampledAt = mysql?.sampledAt || server?.sampledAt || Date.now();
    const heapUsed = server?.jvm?.heapUsed ?? 0;
    const cpu = server?.os?.processCpuLoad ?? -1;
    trendPoints.value = [
      ...trendPoints.value,
      {
        time: formatClock(sampledAt),
        threadsConnected: mysql?.threadsConnected ?? 0,
        poolActive: mysql?.pool?.activeCount ?? 0,
        heapUsedMb: Math.round((heapUsed / (1024 * 1024)) * 10) / 10,
        processCpuPercent: cpu < 0 ? 0 : Math.round(cpu * 1000) / 10,
      },
    ].slice(-TREND_MAX);
  };

  const refresh = async () => {
    loading.value = true;
    lastError.value = null;
    try {
      const [serverRes, mysqlRes] = await Promise.all([
        getServerMonitor(),
        getMysqlMonitor(),
      ]);
      serverInfo.value = serverRes.data;
      mysqlInfo.value = mysqlRes.data;
      pushTrend(serverRes.data, mysqlRes.data);
    } catch (err) {
      const message = err instanceof Error ? err.message : String(err);
      lastError.value = message;
      Message.error(t('workplace.monitor.refreshFailed'));
    } finally {
      loading.value = false;
    }
  };

  const clearTimer = () => {
    if (timer) {
      clearInterval(timer);
      timer = null;
    }
  };

  const setupTimer = (seconds: AutoRefreshSeconds) => {
    clearTimer();
    if (seconds > 0) {
      timer = setInterval(() => {
        refresh();
      }, seconds * 1000);
    }
  };

  watch(autoRefresh, (seconds) => {
    setupTimer(seconds);
  });

  onMounted(() => {
    refresh();
  });

  onUnmounted(() => {
    clearTimer();
  });

  const autoRefreshOptions = computed(
    () => [0, 5, 10, 30] as AutoRefreshSeconds[]
  );

  return {
    loading,
    autoRefresh,
    autoRefreshOptions,
    serverInfo,
    mysqlInfo,
    trendPoints,
    lastError,
    refresh,
  };
}
