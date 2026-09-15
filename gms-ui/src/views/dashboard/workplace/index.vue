<template>
  <PageContainer
    :title="$t('menu.dashboard.workplace')"
    :description="$t('workplace.page.description')"
  >
    <div class="workplace">
      <div class="workplace__hero">
        <div class="workplace__hero-copy">
          <div class="workplace__status-row">
            <span class="workplace__status-label">
              {{ $t('workplace.gameServer.status') }}
            </span>
            <a-tag
              v-if="serverStatus === 'running'"
              color="green"
              bordered
              size="large"
            >
              {{ $t('workplace.running') }}
            </a-tag>
            <a-tag v-else color="gray" bordered size="large">
              {{ $t('workplace.stopped') }}
            </a-tag>
          </div>
          <p class="workplace__hint">
            {{ $t('workplace.gameServer.currently') }}
            {{
              serverStatus === 'running'
                ? $t('workplace.running')
                : $t('workplace.stopped')
            }}
          </p>
        </div>
        <a-space>
          <a-select
            v-model="autoRefresh"
            :style="{ width: '140px' }"
            :options="autoRefreshSelectOptions"
          />
          <a-button
            type="outline"
            :loading="loading || monitorLoading"
            @click="handleRefreshAll"
          >
            <template #icon>
              <icon-refresh />
            </template>
            {{ $t('button.refresh') }}
          </a-button>
        </a-space>
      </div>

      <ServerMonitorPanel :loading="monitorLoading" :info="serverInfo" />

      <MysqlMonitorPanel
        :loading="monitorLoading"
        :info="mysqlInfo"
        :trend-points="trendPoints"
      />

      <a-row :gutter="16">
        <a-col :xs="24" :lg="14">
          <ProCard :title="$t('workplace.gameServer.serverControl')">
            <div class="action-grid">
              <button
                v-for="(btn, index) in serverControlButtons"
                :key="index"
                class="action-tile"
                :class="`action-tile--${btn.status}`"
                :disabled="btn.disabled(serverStatus) || loading"
                type="button"
                @click="handleButtonClick(btn.action)"
              >
                <span class="action-tile__icon">
                  <component :is="btn.icon" />
                </span>
                <span class="action-tile__label">
                  {{ $t(`workplace.button.${btn.label}`) }}
                </span>
              </button>
            </div>
          </ProCard>
        </a-col>
        <a-col :xs="24" :lg="10">
          <ProCard :title="$t('workplace.dataReload')">
            <p class="workplace__reload-hint">
              {{ $t('workplace.dataReload.hint') }}
            </p>
            <a-space direction="vertical" fill :size="12">
              <a-button
                v-for="(btn, index) in dataReloadButtons"
                :key="index + 'reload'"
                long
                type="secondary"
                :loading="loading"
                @click="handleButtonClick(btn.action)"
              >
                <template #icon>
                  <component :is="btn.icon" />
                </template>
                {{ $t(`workplace.button.${btn.label}`) }}
              </a-button>
            </a-space>
          </ProCard>
        </a-col>
      </a-row>

      <a-modal
        v-model:visible="shutdownConfirmVisible"
        class="arco-modal-auto"
        draggable
        @ok="handleShutdownConfirm"
        @cancel="handleShutdownCancel"
      >
        <template #title>
          {{ $t('workplace.button.shutdown') }}
        </template>
        <p>{{ $t('workplace.button.shutdown.confirm') }}</p>
      </a-modal>

      <a-modal
        v-model:visible="restartConfirmVisible"
        modal-class="arco-modal-auto"
        draggable
        @ok="handleRestartConfirm"
        @cancel="handleRestartCancel"
      >
        <template #title>
          {{ $t('workplace.button.restart') }}
        </template>
        <p>{{ $t('workplace.button.restart.confirm') }}</p>
      </a-modal>

      <a-modal
        v-model:visible="stopConfigVisible"
        modal-class="arco-modal-auto"
        draggable
        @ok="handleStopConfigOk"
        @cancel="handleStopConfigCancel"
      >
        <template #title>
          {{ $t('workplace.button.stop.config') }}
        </template>
        <a-form :model="stopConfigData" layout="vertical">
          <a-form-item :label="$t('workplace.stop.minutes')">
            <a-input-number
              v-model="stopConfigData.minutes"
              :min="0"
              style="width: 100%"
            >
              <template #suffix>
                {{ $t('workplace.unit.minutes') }}
              </template>
            </a-input-number>
          </a-form-item>
          <a-form-item>
            <template #label>
              <span>{{ $t('workplace.stop.shutdownMsg') }}</span>
              <a-tooltip :content="$t('workplace.stop.shutdownMsgDefault')">
                <icon-info-circle style="margin-left: 8px" />
              </a-tooltip>
            </template>
            <a-textarea v-model="stopConfigData.shutdownMsg" />
          </a-form-item>
          <a-form-item :label="$t('workplace.stop.messageTypes')">
            <a-space wrap>
              <a-checkbox v-model="stopConfigData.showServerMsg">
                {{ $t('workplace.stop.showServerMsg') }}
              </a-checkbox>
              <a-checkbox v-model="stopConfigData.showCenterMsg">
                {{ $t('workplace.stop.showCenterMsg') }}
              </a-checkbox>
              <a-checkbox v-model="stopConfigData.showChatMsg">
                {{ $t('workplace.stop.showChatMsg') }}
              </a-checkbox>
            </a-space>
          </a-form-item>
        </a-form>
      </a-modal>
    </div>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { computed, onMounted, reactive, ref } from 'vue';
  import {
    getServerStatus,
    restartServer,
    shutdown,
    startServer,
    stopServer,
  } from '@/api/dashboard';
  import { Message } from '@arco-design/web-vue';
  import useLoading from '@/hooks/loading';
  import {
    reloadAllScriptsByGMCommand,
    reloadDropsByGMCommand,
    reloadEventsByGMCommand,
    reloadMapScriptsByGMCommand,
    reloadMapsByGMCommand,
    reloadNpcScriptsByGMCommand,
    reloadPortalsByGMCommand,
    reloadQuestScriptsByGMCommand,
    reloadReactorScriptsByGMCommand,
    reloadShopsByGMCommand,
  } from '@/api/command';
  import { useI18n } from 'vue-i18n';
  import ServerMonitorPanel from './components/ServerMonitorPanel.vue';
  import MysqlMonitorPanel from './components/MysqlMonitorPanel.vue';
  import useMonitorRefresh from './hooks/useMonitorRefresh';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);
  const serverStatus = ref<'resting' | 'running'>('resting');
  const stopConfigVisible = ref(false);
  const shutdownConfirmVisible = ref(false);
  const restartConfirmVisible = ref(false);
  const stopConfigData = reactive({
    minutes: 0,
    shutdownMsg: '',
    showServerMsg: false,
    showCenterMsg: false,
    showChatMsg: false,
  });

  const {
    loading: monitorLoading,
    autoRefresh,
    serverInfo,
    mysqlInfo,
    trendPoints,
    refresh: refreshMonitor,
  } = useMonitorRefresh();

  const autoRefreshSelectOptions = computed(() => [
    { label: t('workplace.monitor.refresh.off'), value: 0 },
    { label: t('workplace.monitor.refresh.5s'), value: 5 },
    { label: t('workplace.monitor.refresh.10s'), value: 10 },
    { label: t('workplace.monitor.refresh.30s'), value: 30 },
  ]);

  const serverControlButtons = [
    {
      label: 'start',
      action: 'start',
      disabled: (status: 'resting' | 'running') => status === 'running',
      status: 'success' as const,
      icon: 'icon-play-arrow-fill',
    },
    {
      label: 'stop',
      action: 'stop',
      disabled: (status: 'resting' | 'running') => status === 'resting',
      status: 'danger' as const,
      icon: 'icon-stop',
    },
    {
      label: 'restart',
      action: 'restart',
      disabled: (status: 'resting' | 'running') => status === 'resting',
      status: 'warning' as const,
      icon: 'icon-refresh',
    },
    {
      label: 'shutdown',
      action: 'shutdown',
      disabled: () => false,
      status: 'danger' as const,
      icon: 'icon-poweroff',
    },
  ];

  const dataReloadButtons = [
    {
      label: 'dataReloadAllScripts',
      action: 'reloadAllScripts',
      icon: 'icon-sync',
    },
    { label: 'dataReloadEvents', action: 'reloadEvents', icon: 'icon-compass' },
    {
      label: 'dataReloadPortals',
      action: 'reloadPortals',
      icon: 'icon-common',
    },
    {
      label: 'dataReloadMapScripts',
      action: 'reloadMapScripts',
      icon: 'icon-code',
    },
    {
      label: 'dataReloadQuestScripts',
      action: 'reloadQuestScripts',
      icon: 'icon-book',
    },
    {
      label: 'dataReloadNpcScripts',
      action: 'reloadNpcScripts',
      icon: 'icon-user',
    },
    {
      label: 'dataReloadReactorScripts',
      action: 'reloadReactorScripts',
      icon: 'icon-storage',
    },
    {
      label: 'dataReloadMaps',
      action: 'reloadMaps',
      icon: 'icon-mind-mapping',
    },
    {
      label: 'dataReloadShops',
      action: 'reloadShops',
      icon: 'icon-gift',
    },
    {
      label: 'dataReloadDrops',
      action: 'reloadDrops',
      icon: 'icon-trophy',
    },
  ];

  const loadSeverStatus = async () => {
    setLoading(true);
    try {
      const { data } = await getServerStatus();
      serverStatus.value = data ? 'running' : 'resting';
    } finally {
      setLoading(false);
    }
  };

  const handleRefreshAll = async () => {
    await Promise.all([loadSeverStatus(), refreshMonitor()]);
  };

  onMounted(() => {
    loadSeverStatus();
  });

  const handleButtonClick = async (action: string) => {
    if (action === 'shutdown') {
      shutdownConfirmVisible.value = true;
      return;
    }
    if (action === 'restart') {
      restartConfirmVisible.value = true;
      return;
    }

    setLoading(true);
    try {
      switch (action) {
        case 'start':
          await startServer();
          break;
        case 'stop':
          stopConfigVisible.value = true;
          setLoading(false);
          return;
        case 'restart':
          await restartServer();
          break;
        case 'reloadEvents':
          await reloadEventsByGMCommand();
          break;
        case 'reloadMaps':
          await reloadMapsByGMCommand();
          break;
        case 'reloadPortals':
          await reloadPortalsByGMCommand();
          break;
        case 'reloadAllScripts':
          await reloadAllScriptsByGMCommand();
          break;
        case 'reloadMapScripts':
          await reloadMapScriptsByGMCommand();
          break;
        case 'reloadQuestScripts':
          await reloadQuestScriptsByGMCommand();
          break;
        case 'reloadNpcScripts':
          await reloadNpcScriptsByGMCommand();
          break;
        case 'reloadReactorScripts':
          await reloadReactorScriptsByGMCommand();
          break;
        case 'reloadShops':
          await reloadShopsByGMCommand();
          break;
        case 'reloadDrops':
          await reloadDropsByGMCommand();
          break;
        default:
          break;
      }

      Message.success(t('common.operationSuccess'));
    } catch (err) {
      console.error(err);
      Message.error(t('common.requestFailed'));
    } finally {
      await loadSeverStatus();
      setLoading(false);
    }
  };

  const handleShutdownConfirm = async () => {
    try {
      setLoading(true);
      await shutdown();
      Message.success(t('workplace.button.shutdown.success'));
      await loadSeverStatus();
    } catch (err) {
      console.error(err);
      Message.error(t('common.requestFailed'));
    } finally {
      shutdownConfirmVisible.value = false;
      setLoading(false);
    }
  };

  const handleShutdownCancel = () => {
    shutdownConfirmVisible.value = false;
  };

  const handleRestartConfirm = async () => {
    try {
      setLoading(true);
      await restartServer();
      Message.success(t('common.operationSuccess'));
    } catch (err) {
      console.error(err);
      Message.error(t('common.requestFailed'));
    } finally {
      restartConfirmVisible.value = false;
      setLoading(false);
    }
  };

  const handleRestartCancel = () => {
    restartConfirmVisible.value = false;
  };
  const handleStopConfigOk = async () => {
    try {
      setLoading(true);
      const stopConfigParams = {
        minutes: stopConfigData.minutes,
        shutdownMsg: stopConfigData.shutdownMsg,
        showServerMsg: stopConfigData.showServerMsg,
        showCenterMsg: stopConfigData.showCenterMsg,
        showChatMsg: stopConfigData.showChatMsg,
      };

      await stopServer(stopConfigParams);
      Message.success(t('workplace.stop.shutdownInProgress'));

      if (stopConfigData.minutes > 0) {
        setTimeout(async () => {
          await loadSeverStatus();
        }, stopConfigData.minutes * 60 * 1000);
      } else {
        await loadSeverStatus();
      }

      stopConfigVisible.value = false;
    } catch (err) {
      console.error(err);
      Message.error(t('common.requestFailed'));
    } finally {
      setLoading(false);
    }
  };

  const handleStopConfigCancel = () => {
    Object.assign(stopConfigData, {
      minutes: 0,
      shutdownMsg: '',
      showServerMsg: false,
      showCenterMsg: false,
      showChatMsg: false,
    });
    stopConfigVisible.value = false;
  };
</script>

<script lang="ts">
  export default {
    name: 'Dashboard',
  };
</script>

<style lang="less" scoped>
  .workplace {
    display: flex;
    flex-direction: column;
    gap: 16px;

    &__hero {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 16px;
      padding: 22px 24px;
      color: #e8eef5;
      background: linear-gradient(
        135deg,
        #0f2744 0%,
        #1b6b93 55%,
        #2a8fb5 100%
      );
      border-radius: var(--bd-radius-lg);
      box-shadow: var(--bd-shadow-md);
    }

    &__status-row {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    &__status-label {
      font-weight: 600;
      font-size: 16px;
      font-family: var(--bd-font-display);
    }

    &__hint {
      margin: 8px 0 0;
      color: rgba(232, 238, 245, 0.78);
      font-size: 13px;
    }

    &__reload-hint {
      margin: 0 0 12px;
      color: var(--color-text-3);
      font-size: 12px;
      line-height: 1.5;
    }
  }

  .workplace :deep(.arco-col) {
    margin-bottom: 16px;
  }

  .action-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }

  .action-tile {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
    min-height: 108px;
    padding: 16px;
    color: var(--bd-ink);
    text-align: left;
    background: var(--bd-surface-muted);
    border: 1px solid transparent;
    border-radius: var(--bd-radius-md);
    cursor: pointer;
    transition: transform 0.18s ease, box-shadow 0.18s ease,
      border-color 0.18s ease;

    &:hover:not(:disabled) {
      transform: translateY(-1px);
      border-color: rgba(var(--primary-6), 0.25);
      box-shadow: var(--bd-shadow-sm);
    }

    &:disabled {
      opacity: 0.45;
      cursor: not-allowed;
    }

    &__icon {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      color: #fff;
      font-size: 18px;
      border-radius: 10px;
    }

    &__label {
      font-weight: 600;
      font-size: 14px;
    }

    &--success &__icon {
      background: linear-gradient(135deg, #22c55e, #16a34a);
    }

    &--warning &__icon {
      background: linear-gradient(135deg, #f59e0b, #d97706);
    }

    &--danger &__icon {
      background: linear-gradient(135deg, #f43f5e, #e11d48);
    }
  }

  @media (max-width: 640px) {
    .workplace__hero {
      flex-direction: column;
      align-items: flex-start;
    }

    .action-grid {
      grid-template-columns: 1fr;
    }
  }

  body[arco-theme='dark'] {
    .action-tile {
      color: var(--color-text-1);
      background: var(--color-fill-2);
    }
  }
</style>
