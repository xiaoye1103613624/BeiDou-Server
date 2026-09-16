<template>
  <div class="login-form-wrapper">
    <div class="login-form-title">{{ $t('form.login.title') }}</div>
    <div class="login-form-sub-title">{{ $t('form.login.subtitle') }}</div>
    <div class="login-form-error-msg">{{ errorMessage }}</div>
    <a-form
      ref="loginForm"
      :model="userInfo"
      class="login-form"
      layout="vertical"
      @submit="handleSubmit"
    >
      <a-form-item
        field="username"
        :rules="[{ required: true, message: $t('form.login.user.empty') }]"
        :validate-trigger="['change', 'blur']"
        hide-label
      >
        <a-input
          v-model="userInfo.username"
          size="large"
          :placeholder="$t('form.login.user.placeholder')"
          @keydown.enter="focusToPassword"
        >
          <template #prefix>
            <icon-user />
          </template>
        </a-input>
      </a-form-item>
      <a-form-item
        field="password"
        :rules="[{ required: true, message: $t('form.login.password.empty') }]"
        :validate-trigger="['change', 'blur']"
        hide-label
      >
        <a-input-password
          ref="passwordInputRef"
          v-model="userInfo.password"
          size="large"
          :placeholder="$t('form.login.password.placeholder')"
          allow-clear
          @keydown.enter.prevent="
            () => handleSubmit({ errors: undefined, values: userInfo })
          "
        >
          <template #prefix>
            <icon-lock />
          </template>
        </a-input-password>
      </a-form-item>
      <a-space :size="16" direction="vertical" fill>
        <div class="login-form-password-actions">
          <a-checkbox
            :model-value="loginConfig.rememberPassword"
            @change="setRememberPassword as any"
          >
            {{ $t('form.login.rememberPassword') }}
          </a-checkbox>
        </div>
        <a-button
          type="primary"
          html-type="submit"
          long
          size="large"
          :loading="loading"
        >
          {{ $t('form.login.login') }}
        </a-button>
      </a-space>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
  import { ref, reactive, nextTick } from 'vue';
  import { useRouter } from 'vue-router';
  import { Message } from '@arco-design/web-vue';
  import { ValidatedError } from '@arco-design/web-vue/es/form/interface';
  import { useStorage } from '@vueuse/core';
  import { useUserStore } from '@/store';
  import useLoading from '@/hooks/loading';
  import type { LoginData } from '@/api/user';
  import { useI18n } from 'vue-i18n';

  const router = useRouter();
  const errorMessage = ref('');
  const { loading, setLoading } = useLoading();
  const userStore = useUserStore();
  const { t } = useI18n();

  const loginConfig = useStorage('login-config', {
    rememberPassword: true,
    username: 'admin',
    password: 'admin',
  });
  const userInfo = reactive({
    username: loginConfig.value.username,
    password: loginConfig.value.password,
  });

  const passwordInputRef = ref();

  const focusToPassword = () => {
    nextTick(() => {
      passwordInputRef.value?.focus?.();
    });
  };

  const handleSubmit = async ({
    errors,
    values,
  }: {
    errors: Record<string, ValidatedError> | undefined;
    values: Record<string, any>;
  }) => {
    if (loading.value) return;
    if (!errors) {
      setLoading(true);
      try {
        await userStore.login(values as LoginData);
        const { redirect, ...othersQuery } = router.currentRoute.value.query;
        router.push({
          name: (redirect as string) || 'Workplace',
          query: {
            ...othersQuery,
          },
        });
        Message.success(t('message.login.success'));
        const { rememberPassword } = loginConfig.value;
        const { username, password } = values;
        loginConfig.value.username = rememberPassword ? username : '';
        loginConfig.value.password = rememberPassword ? password : '';
      } catch (err) {
        errorMessage.value = (err as Error).message;
        if ((err as Error).name === 'TypeError') {
          errorMessage.value = t('form.login.requestError');
        }
      } finally {
        setLoading(false);
      }
    }
  };
  const setRememberPassword = (value: boolean) => {
    loginConfig.value.rememberPassword = value;
  };
</script>

<style lang="less" scoped>
  .login-form {
    &-wrapper {
      width: 100%;
    }

    &-title {
      color: var(--bd-ink);
      font-weight: 700;
      font-size: 26px;
      font-family: var(--bd-font-display);
      line-height: 1.3;
      letter-spacing: -0.03em;
    }

    &-sub-title {
      margin-top: 8px;
      color: var(--bd-ink-soft);
      font-size: 14px;
      line-height: 1.5;
    }

    &-error-msg {
      min-height: 28px;
      margin-top: 8px;
      color: rgb(var(--red-6));
      font-size: 13px;
      line-height: 28px;
    }

    &-password-actions {
      display: flex;
      justify-content: space-between;
      width: 100%;
    }

    :deep(.arco-input-wrapper),
    :deep(.arco-input-password) {
      border-radius: var(--bd-radius-md);
    }

    :deep(.arco-btn-primary) {
      height: 42px;
      border-radius: var(--bd-radius-md);
      font-weight: 600;
    }
  }

  body[arco-theme='dark'] {
    .login-form-title {
      color: var(--color-text-1);
    }

    .login-form-sub-title {
      color: var(--color-text-3);
    }
  }
</style>
