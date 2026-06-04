<template>
  <!-- 工作台容器 -->
  <div class="container" :loading="loading">
    <!-- 面包屑导航 -->
    <Breadcrumb />
    <!-- 主卡片容器 -->
    <a-card class="general-card" :title="$t('menu.dashboard.workplace')">
      <!-- 服务器状态卡片 -->
      <a-card
        class="status-card"
        :title="$t('workplace.gameServer.status')"
        :bordered="false"
      >
        <a-row>
          <a-col>
            <!-- 显示当前服务器状态 -->
            {{ $t('workplace.gameServer.currently') }}
            <!-- 运行状态标签 -->
            <a-tag v-if="serverStatus === 'running'" color="green" bordered>
              {{ $t('workplace.running') }}
            </a-tag>
            <!-- 停止状态标签 -->
            <a-tag v-else color="gray" bordered>
              {{ $t('workplace.stopped') }}
            </a-tag>
          </a-col>
        </a-row>
      </a-card>

      <!-- 服务器控制卡片 -->
      <a-card
        class="control-card"
        :title="$t('workplace.gameServer.serverControl')"
        :bordered="false"
      >
        <!-- 控制按钮组 -->
        <a-space class="button-group" :size="16">
          <!-- 遍历渲染服务器控制按钮 -->
          <a-button
            v-for="(btn, index) in serverControlButtons"
            :key="index"
            :loading="loading && btn.action !== 'stop'"
            type="primary"
            :disabled="btn.disabled(serverStatus)"
            :status="btn.status"
            @click="handleButtonClick(btn.action)"
          >
            <!-- 按钮图标 -->
            <template #icon>
              <component :is="btn.icon" />
            </template>
            <!-- 按钮文字 -->
            {{ $t(`workplace.button.${btn.label}`) }}
          </a-button>
        </a-space>
      </a-card>

      <!-- 数据重载卡片 -->
      <a-card
        class="reload-card"
        :title="$t('workplace.dataReload')"
        :bordered="false"
      >
        <!-- 重载按钮组 -->
        <a-space class="button-group" :size="16">
          <!-- 遍历渲染数据重载按钮 -->
          <a-button
            v-for="(btn, index) in dataReloadButtons"
            :key="index + 'reload'"
            :loading="loading"
            type="primary"
            @click="handleButtonClick(btn.action)"
          >
            <!-- 按钮图标 -->
            <template #icon>
              <component :is="btn.icon" />
            </template>
            <!-- 按钮文字 -->
            {{ $t(`workplace.button.${btn.label}`) }}
          </a-button>
        </a-space>
      </a-card>

      <!-- 完全停服并退出BAT的确认框 -->
      <a-modal
        v-model:visible="shutdownConfirmVisible"
        class="arco-modal-auto"
        draggable
        @ok="handleShutdownConfirm"
        @cancel="handleShutdownCancel"
      >
        <!-- 关机确认对话框标题 -->
        <template #title>
          {{ $t('workplace.button.shutdown') }}
        </template>
        <!-- 关机确认对话框内容 -->
        <p>{{ $t('workplace.button.shutdown.confirm') }}</p>
      </a-modal>

      <!-- 重启服务端的确认框 -->
      <a-modal
        v-model:visible="restartConfirmVisible"
        modal-class="arco-modal-auto"
        draggable
        @ok="handleRestartConfirm"
        @cancel="handleRestartCancel"
      >
        <!-- 重启确认对话框标题 -->
        <template #title>
          {{ $t('workplace.button.restart') }}
        </template>
        <!-- 重启确认对话框内容 -->
        <p>{{ $t('workplace.button.restart.confirm') }}</p>
      </a-modal>

      <!-- 停服倒计时配置框 -->
      <a-modal
        v-model:visible="stopConfigVisible"
        modal-class="arco-modal-auto"
        draggable
        @ok="handleStopConfigOk"
        @cancel="handleStopConfigCancel"
      >
        <!-- 停服配置对话框标题 -->
        <template #title>
          {{ $t('workplace.button.stop.config') }}
        </template>
        <!-- 停服配置表单 -->
        <a-form :model="stopConfigData" layout="vertical">
          <!-- 停服倒计时分钟数设置 -->
          <a-card
            :title="$t('workplace.stop.minutes')"
            :bordered="false"
            style="margin-bottom: 16px"
          >
            <a-row :gutter="[16, 16]">
              <a-col :span="18">
                <!-- 分钟输入控件 -->
                <a-input-number v-model="stopConfigData.minutes" :min="0" />
              </a-col>
              <a-col :span="6">
                <!-- 单位标签 -->
                <span style="line-height: 32px; text-align: right">{{
                  $t('workplace.unit.minutes')
                }}</span>
              </a-col>
            </a-row>
          </a-card>

          <!-- 停服消息设置 -->
          <a-card :bordered="false" style="margin-bottom: 16px">
            <template #title>
              <div style="display: flex; align-items: center">
                <!-- 停服消息标题及帮助提示 -->
                <span>{{ $t('workplace.stop.shutdownMsg') }}</span>
                <a-tooltip :content="$t('workplace.stop.shutdownMsgDefault')">
                  <icon-info-circle style="margin-left: 8px" />
                </a-tooltip>
              </div>
            </template>

            <a-row :gutter="[16, 16]">
              <a-col :span="24">
                <!-- 消息文本域 -->
                <a-textarea v-model="stopConfigData.shutdownMsg" />
              </a-col>
            </a-row>
          </a-card>

          <!-- 消息类型选择 -->
          <a-card :title="$t('workplace.stop.messageTypes')" :bordered="false">
            <a-space class="button-group" :size="16">
              <!-- 显示服务器消息复选框 -->
              <a-checkbox v-model="stopConfigData.showServerMsg">
                {{ $t('workplace.stop.showServerMsg') }}
              </a-checkbox>
              <!-- 显示中央消息复选框 -->
              <a-checkbox v-model="stopConfigData.showCenterMsg">
                {{ $t('workplace.stop.showCenterMsg') }}
              </a-checkbox>
              <!-- 显示聊天消息复选框 -->
              <a-checkbox v-model="stopConfigData.showChatMsg">
                {{ $t('workplace.stop.showChatMsg') }}
              </a-checkbox>
            </a-space>
          </a-card>
        </a-form>
      </a-modal>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  // 导入Vue 3组合式API相关功能
  import { onMounted, reactive, ref } from 'vue';
  // 导入服务器状态管理API
  import {
    getServerStatus,
    restartServer,
    shutdown,
    startServer,
    stopServer,
  } from '@/api/dashboard';
  // 导入Arco Design的消息提示组件
  import { Message } from '@arco-design/web-vue';
  // 导入自定义加载状态钩子
  import useLoading from '@/hooks/loading';

  // 导入游戏命令相关API（用于重载数据）
  import {
    reloadEventsByGMCommand,
    reloadMapsByGMCommand,
    reloadPortalsByGMCommand,
  } from '@/api/command';
  // 导入国际化相关功能
  import { useI18n } from 'vue-i18n';

  // 获取国际化函数
  const { t } = useI18n();
  // 创建加载状态和设置函数
  const { loading, setLoading } = useLoading(false);
  // 服务器当前状态（休息中或运行中）
  const serverStatus = ref<'resting' | 'running'>('resting');
  // 停服配置对话框是否可见
  const stopConfigVisible = ref(false);
  // 关机确认对话框是否可见
  const shutdownConfirmVisible = ref(false);
  // 重启确认对话框是否可见
  const restartConfirmVisible = ref(false);

  // 停服配置数据对象
  const stopConfigData = reactive({
    // 停服倒计时分钟数
    minutes: 0,
    // 停服时显示的消息
    shutdownMsg: '',
    // 是否显示服务器消息
    showServerMsg: false,
    // 是否显示中央消息
    showCenterMsg: false,
    // 是否显示聊天消息
    showChatMsg: false,
  });

  // 服务器控制按钮配置数组
  const serverControlButtons = [
    {
      // 按钮标签
      label: 'start',
      // 按钮执行的动作
      action: 'start',
      // 根据服务器状态判断按钮是否禁用的函数
      disabled: (status: 'resting' | 'running') => status === 'running',
      // 按钮状态样式
      status: 'success' as const,
      // 按钮图标
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

  // 数据重载按钮配置数组
  const dataReloadButtons = [
    {
      // 重载事件数据按钮
      label: 'dataReloadEvents',
      action: 'reloadEvents',
      icon: 'icon-compass',
    },
    {
      // 重载地图数据按钮
      label: 'dataReloadMaps',
      action: 'reloadMaps',
      icon: 'icon-mind-mapping',
    },
    {
      // 重载传送点数据按钮
      label: 'dataReloadPortals',
      action: 'reloadPortals',
      icon: 'icon-common',
    },
  ];

  /**
   * 加载服务器状态信息
   * 异步获取当前服务器运行状态，并更新本地状态变量
   */
  const loadSeverStatus = async () => {
    setLoading(true);
    try {
      // 请求服务器状态API
      const { data } = await getServerStatus();
      // 根据返回的数据更新服务器状态
      serverStatus.value = data ? 'running' : 'resting';
    } finally {
      // 确保无论成功或失败都取消加载状态
      setLoading(false);
    }
  };

  // 组件挂载完成后自动加载服务器状态
  onMounted(() => {
    loadSeverStatus();
  });

  /**
   * 处理按钮点击事件
   * 根据不同的操作类型执行相应的服务器管理功能
   * @param action - 要执行的操作类型
   */
  const handleButtonClick = async (action: string) => {
    // 如果是关机操作，显示确认对话框
    if (action === 'shutdown') {
      shutdownConfirmVisible.value = true;
      return;
    }
    // 如果是重启操作，显示确认对话框
    if (action === 'restart') {
      restartConfirmVisible.value = true;
      return;
    }

    // 设置加载状态
    setLoading(true);
    try {
      // 根据操作类型执行相应功能
      switch (action) {
        case 'start':
          // 启动服务器
          await startServer();
          break;
        case 'stop':
          // 打开停服配置对话框
          stopConfigVisible.value = true;
          setLoading(false);
          return;
        case 'restart':
          // 重启服务器
          await restartServer();
          break;
        case 'reloadEvents':
          // 重载事件数据
          await reloadEventsByGMCommand();
          break;
        case 'reloadMaps':
          // 重载地图数据
          await reloadMapsByGMCommand();
          break;
        case 'reloadPortals':
          // 重载传送点数据
          await reloadPortalsByGMCommand();
          break;
        default:
          // 默认情况什么都不做
          break;
      }

      // 操作成功提示
      Message.success(t('common.operationSuccess'));
    } catch (err) {
      // 操作失败提示
      Message.error(t('common.requestFailed'));
    } finally {
      // 重新加载服务器状态
      await loadSeverStatus();
      // 取消加载状态
      setLoading(false);
    }
  };

  /**
   * 处理关机确认操作
   * 当用户在确认对话框中点击确定时执行关机操作
   */
  const handleShutdownConfirm = async () => {
    try {
      setLoading(true);
      // 执行服务器关机操作
      await shutdown();
      // 显示关机成功消息
      Message.success(t('workplace.button.shutdown.success'));
      // 立即尝试更新服务器状态
      await loadSeverStatus();
    } catch (err) {
      // 显示请求失败消息
      Message.error(t('common.requestFailed'));
    } finally {
      // 隐藏关机确认对话框
      shutdownConfirmVisible.value = false;
      // 取消加载状态
      setLoading(false);
    }
  };

  /**
   * 处理关机取消操作
   * 当用户在确认对话框中点击取消时隐藏对话框
   */
  const handleShutdownCancel = () => {
    // 隐藏关机确认对话框
    shutdownConfirmVisible.value = false;
  };

  /**
   * 处理重启确认操作
   * 当用户在确认对话框中点击确定时执行重启操作
   */
  const handleRestartConfirm = async () => {
    try {
      setLoading(true);
      // 执行服务器重启操作
      await restartServer();
      // 显示操作成功消息
      Message.success(t('common.operationSuccess'));
    } catch (err) {
      // 显示请求失败消息
      Message.error(t('common.requestFailed'));
    } finally {
      // 隐藏重启确认对话框
      restartConfirmVisible.value = false;
      // 取消加载状态
      setLoading(false);
    }
  };

  /**
   * 处理重启取消操作
   * 当用户在确认对话框中点击取消时隐藏对话框
   */
  const handleRestartCancel = () => {
    // 隐藏重启确认对话框
    restartConfirmVisible.value = false;
  };

  /**
   * 处理停服配置确认操作
   * 当用户在配置对话框中点击确定时执行停服操作
   */
  const handleStopConfigOk = async () => {
    try {
      setLoading(true);
      // 构建停服配置参数
      const stopConfigParams = {
        minutes: stopConfigData.minutes,
        shutdownMsg: stopConfigData.shutdownMsg,
        showServerMsg: stopConfigData.showServerMsg,
        showCenterMsg: stopConfigData.showCenterMsg,
        showChatMsg: stopConfigData.showChatMsg,
      };

      // 执行停服操作
      await stopServer(stopConfigParams);
      // 显示停服进行中的消息
      Message.success(t('workplace.stop.shutdownInProgress'));

      // 如果设置了延迟时间，则启动一个定时器，在延迟时间结束后更新服务器状态
      if (stopConfigData.minutes > 0) {
        setTimeout(async () => {
          await loadSeverStatus();
        }, stopConfigData.minutes * 60 * 1000);
      } else {
        // 如果没有设置延迟时间，立即更新服务器状态
        await loadSeverStatus();
      }

      // 隐藏停服配置对话框
      stopConfigVisible.value = false;
    } catch (err) {
      // 显示请求失败消息
      Message.error(t('common.requestFailed'));
    } finally {
      // 取消加载状态
      setLoading(false);
    }
  };

  /**
   * 处理停服配置取消操作
   * 当用户在配置对话框中点击取消时重置配置并隐藏对话框
   */
  const handleStopConfigCancel = () => {
    // 将停服配置数据重置为默认值
    Object.assign(stopConfigData, {
      minutes: 0,
      shutdownMsg: '',
      showServerMsg: false,
      showCenterMsg: false,
      showChatMsg: false,
    });
    // 隐藏停服配置对话框
    stopConfigVisible.value = false;
  };
</script>

<script lang="ts">
  export default {
    name: 'Dashboard',
  };
</script>

<style lang="less" scoped>
  .button-group {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
  }
</style>
