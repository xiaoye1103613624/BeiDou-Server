<template>
  <template v-for="item in items" :key="String(item.name)">
    <a-doption
      v-if="!hasChildren(item)"
      :value="String(item.name)"
      :class="{ 'is-active': isActive(item) }"
      @click="emit('navigate', item)"
    >
      {{ labelOf(item) }}
    </a-doption>
    <a-dsubmenu v-else :value="String(item.name)" trigger="hover" position="rt">
      <span :class="{ 'is-active': isActive(item) }">{{ labelOf(item) }}</span>
      <template #content>
        <!-- Vue 3.2.34+ 由文件名推断组件名，支持递归且无需自导入 -->
        <TopMenuPanel :items="item.children || []" @navigate="onNavigate" />
      </template>
    </a-dsubmenu>
  </template>
</template>

<script lang="ts" setup>
  import { useRoute } from 'vue-router';
  import type { RouteRecordRaw } from 'vue-router';
  import { useI18n } from 'vue-i18n';

  defineProps<{
    items: RouteRecordRaw[];
  }>();

  const emit = defineEmits<{
    (e: 'navigate', item: RouteRecordRaw): void;
  }>();

  const { t } = useI18n();
  const route = useRoute();

  const hasChildren = (item: RouteRecordRaw) =>
    Array.isArray(item.children) && item.children.length > 0;

  const containsRoute = (item: RouteRecordRaw, name: string): boolean => {
    if (item.name === name) return true;
    return (item.children || []).some((child) => containsRoute(child, name));
  };

  const isActive = (item: RouteRecordRaw) => {
    const name = String(route.name || '');
    if (!name) return false;
    return containsRoute(item, name);
  };

  const labelOf = (item: RouteRecordRaw) => t(item.meta?.locale || '');

  const onNavigate = (item: RouteRecordRaw) => {
    emit('navigate', item);
  };
</script>

<style scoped lang="less">
  .is-active {
    color: var(--primary);
    font-weight: 600;
  }
</style>
