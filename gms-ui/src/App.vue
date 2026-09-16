<template>
  <a-config-provider :locale="locale">
    <router-view />
    <global-setting />
  </a-config-provider>
</template>

<script lang="ts" setup>
  import { computed, onMounted } from 'vue';
  import enUS from '@arco-design/web-vue/es/locale/lang/en-us';
  import zhCN from '@arco-design/web-vue/es/locale/lang/zh-cn';
  import GlobalSetting from '@/components/global-setting/index.vue';
  import useLocale from '@/hooks/locale';
  import { useAppStore } from '@/store';

  const { currentLocale } = useLocale();
  const appStore = useAppStore();
  const locale = computed(() => {
    switch (currentLocale.value) {
      case 'zh-CN':
        return zhCN;
      case 'en-US':
        return enUS;
      default:
        return enUS;
    }
  });

  // 恢复本机持久化的色弱模式
  onMounted(() => {
    if (appStore.colorWeak) {
      document.body.style.filter = 'invert(80%)';
    }
  });
</script>
