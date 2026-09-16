<template>
  <div class="block">
    <h5 class="title">{{ title }}</h5>
    <div v-for="option in options" :key="option.name" class="switch-wrapper">
      <span>{{ $t(option.name) }}</span>
      <form-wrapper
        :type="option.type || 'switch'"
        :name="option.key"
        :default-value="option.defaultVal"
        @input-change="handleChange"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { PropType } from 'vue';
  import { useAppStore } from '@/store';
  import FormWrapper from './form-wrapper.vue';

  interface OptionsProps {
    name: string;
    key: string;
    type?: string;
    defaultVal?: boolean | string | number;
  }
  defineProps({
    title: {
      type: String,
      default: '',
    },
    options: {
      type: Array as PropType<OptionsProps[]>,
      default() {
        return [];
      },
    },
  });
  const appStore = useAppStore();
  const handleChange = async ({
    key,
    value,
  }: {
    key: string;
    value: unknown;
  }) => {
    if (key === 'colorWeak') {
      document.body.style.filter = value ? 'invert(80%)' : 'none';
    }
    if (key === 'menuFromServer' && value) {
      await appStore.fetchServerMenuConfig();
    }
    if (key === 'topMenu') {
      appStore.updateSettings({
        menuCollapse: false,
        menu: true,
        topMenu: Boolean(value),
      });
      return;
    }
    appStore.updateSettings({ [key]: value as never });
  };
</script>

<style scoped lang="less">
  .block {
    margin-bottom: 24px;
  }

  .title {
    margin: 0 0 12px;
    padding: 0;
    color: var(--bd-ink);
    font-weight: 600;
    font-size: 14px;
    font-family: var(--bd-font-display);
  }

  .switch-wrapper {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    min-height: 36px;
    color: var(--bd-ink-soft);
    font-size: 13px;
  }

  body[arco-theme='dark'] {
    .title {
      color: var(--color-text-1);
    }

    .switch-wrapper {
      color: var(--color-text-2);
    }
  }
</style>
