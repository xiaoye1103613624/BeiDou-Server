<template>
  <!-- 登录表单包装器 -->
  <div class="login-form-wrapper">
    <!-- 登录标题 -->
    <div class="login-form-title">{{ $t('title') }}</div>
    <!-- 登录副标题 -->
    <div class="login-form-sub-title">{{ $t('form.login.title') }}</div>
    <!-- 错误消息显示区域 -->
    <div class="login-form-error-msg">{{ errorMessage }}</div>
    <!-- 登录表单 -->
    <a-form
      ref="loginForm"
      :model="userInfo"
      class="login-form"
      layout="vertical"
      @submit="handleSubmit"
    >
      <!-- 用户名输入项 -->
      <a-form-item
        field="username"
        :rules="[{ required: true, message: $t('form.login.user.empty') }]"
        :validate-trigger="['change', 'blur']"
        hide-label
      >
        <a-input
          v-model="userInfo.username"
          :placeholder="$t('form.login.user.placeholder')"
        >
          <!-- 用户名输入框前缀图标 -->
          <template #prefix>
            <icon-user />
          </template>
        </a-input>
      </a-form-item>
      <!-- 密码输入项 -->
      <a-form-item
        field="password"
        :rules="[{ required: true, message: $t('form.login.password.empty') }]"
        :validate-trigger="['change', 'blur']"
        hide-label
      >
        <a-input-password
          v-model="userInfo.password"
          :placeholder="$t('form.login.password.placeholder')"
          allow-clear
        >
          <!-- 密码输入框前缀图标 -->
          <template #prefix>
            <icon-lock />
          </template>
        </a-input-password>
      </a-form-item>
      <!-- 按钮和选项区域 -->
      <a-space :size="16" direction="vertical">
        <!-- 密码操作选项区域（记住密码、忘记密码等） -->
        <div class="login-form-password-actions">
          <a-checkbox
            checked="rememberPassword"
            :model-value="loginConfig.rememberPassword"
            @change="setRememberPassword as any"
          >
            {{ $t('form.login.rememberPassword') }}
          </a-checkbox>
          <a-link>
            {{ $t('form.login.forgetPassword') }}
          </a-link>
        </div>
        <!-- 登录按钮 -->
        <a-button type="primary" html-type="submit" long :loading="loading">
          {{ $t('form.login.login') }}
        </a-button>
        <!-- 注册按钮 -->
        <a-button type="text" long class="login-form-register-btn">
          {{ $t('form.login.register') }}
        </a-button>
      </a-space>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
  // 导入Vue基本功能
  import { reactive, ref } from 'vue';
  // 导入路由功能
  import { useRouter } from 'vue-router';
  // 导入Arco Design消息提示组件
  import { Message } from '@arco-design/web-vue';
  // 导入表单验证错误接口
  import { ValidatedError } from '@arco-design/web-vue/es/form/interface';
  // 导入本地存储功能
  import { useStorage } from '@vueuse/core';
  // 导入用户状态管理
  import { useUserStore } from '@/store';
  // 导入加载状态钩子
  import useLoading from '@/hooks/loading';
  // 导入登录数据类型
  import type { LoginData } from '@/api/user';
  // 导入国际化功能
  import { useI18n } from 'vue-i18n';

  // 获取路由器实例
  const router = useRouter();
  // 错误消息响应式变量
  const errorMessage = ref('');
  // 加载状态和设置函数
  const { loading, setLoading } = useLoading();
  // 用户状态管理器
  const userStore = useUserStore();
  // 国际化函数
  const { t } = useI18n();

  // 登录配置信息（存储在本地存储中）
  const loginConfig = useStorage('login-config', {
    // 是否记住密码
    rememberPassword: true,
    // 用户名（演示默认值）
    username: 'admin',
    // 密码（演示默认值）
    password: 'admin',
  });
  // 用户登录信息响应式对象
  const userInfo = reactive({
    // 用户名
    username: loginConfig.value.username,
    // 密码
    password: loginConfig.value.password,
  });

  /**
   * 处理登录表单提交
   * 验证表单数据并执行用户登录操作
   * @param errors - 表单验证错误对象
   * @param values - 表单提交的值
   */
  const handleSubmit = async ({
    errors,
    values,
  }: {
    errors: Record<string, ValidatedError> | undefined;
    values: Record<string, any>;
  }) => {
    // 如果正在加载则直接返回
    if (loading.value) return;
    // 如果没有验证错误则继续处理
    if (!errors) {
      // 设置加载状态
      setLoading(true);
      try {
        // 执行用户登录操作
        await userStore.login(values as LoginData);
        // 解析路由查询参数
        const { redirect, ...othersQuery } = router.currentRoute.value.query;
        // 跳转到指定页面或默认工作台
        router.push({
          name: (redirect as string) || 'Workplace',
          query: {
            ...othersQuery,
          },
        });
        // 显示登录成功消息
        Message.success(t('message.login.success'));
        // 获取记住密码设置和用户名密码值
        const { rememberPassword } = loginConfig.value;
        const { username, password } = values;
        // 实际生产环境需要进行加密存储。
        // The actual production environment requires encrypted storage.
        // 根据是否记住密码更新本地存储
        loginConfig.value.username = rememberPassword ? username : '';
        loginConfig.value.password = rememberPassword ? password : '';
      } catch (err) {
        // 设置错误消息
        errorMessage.value = (err as Error).message;
        // 特殊错误类型处理
        if ((err as Error).name === 'TypeError')
          errorMessage.value = '错误的请求';
      } finally {
        // 取消加载状态
        setLoading(false);
      }
    }
  };
  /**
   * 设置记住密码选项
   * 更新登录配置中的记住密码状态
   * @param value - 是否记住密码
   */
  const setRememberPassword = (value: boolean) => {
    // 更新记住密码配置
    loginConfig.value.rememberPassword = value;
  };
</script>

<style lang="less" scoped>
  .login-form {
    &-wrapper {
      width: 320px;
    }

    &-title {
      color: var(--color-text-1);
      font-weight: 500;
      font-size: 24px;
      line-height: 32px;
    }

    &-sub-title {
      color: var(--color-text-3);
      font-size: 16px;
      line-height: 24px;
    }

    &-error-msg {
      height: 32px;
      color: rgb(var(--red-6));
      line-height: 32px;
    }

    &-password-actions {
      display: flex;
      justify-content: space-between;
    }

    &-register-btn {
      color: var(--color-text-3) !important;
    }
  }
</style>
