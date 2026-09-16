<template>
  <nav class="top-menu">
    <template v-for="item in menuTree" :key="String(item.name)">
      <button
        v-if="!hasChildren(item)"
        type="button"
        class="top-menu-item"
        :class="{ active: isActive(item) }"
        @click="goto(item)"
      >
        {{ labelOf(item) }}
      </button>
      <a-dropdown v-else trigger="hover" position="bl" :popup-max-height="320">
        <button
          type="button"
          class="top-menu-item"
          :class="{ active: isActive(item) }"
        >
          <span>{{ labelOf(item) }}</span>
          <icon-down class="top-menu-arrow" />
        </button>
        <template #content>
          <TopMenuPanel :items="item.children || []" @navigate="goto" />
        </template>
      </a-dropdown>
    </template>
  </nav>
</template>

<script lang="ts" setup>
  import { useRoute, useRouter } from 'vue-router';
  import type { RouteRecordRaw } from 'vue-router';
  import { useI18n } from 'vue-i18n';
  import useMenuTree from '@/components/menu/use-menu-tree';
  import { openWindow, regexUrl } from '@/utils';
  import TopMenuPanel from './top-menu-panel.vue';

  const { t } = useI18n();
  const route = useRoute();
  const router = useRouter();
  const { menuTree } = useMenuTree();

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

  const goto = (item: RouteRecordRaw) => {
    if (!item?.name) return;
    if (regexUrl.test(item.path)) {
      openWindow(item.path);
      return;
    }
    if (route.name === item.name) return;
    router.push({ name: item.name });
  };
</script>

<style scoped lang="less">
  .top-menu {
    display: flex;
    align-items: stretch;
    height: 100%;
    overflow-x: auto;
    overflow-y: hidden;
    scrollbar-width: none;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .top-menu-item {
    display: inline-flex;
    flex: 0 0 auto;
    align-items: center;
    gap: 4px;
    height: 100%;
    padding: 0 16px;
    color: var(--text-secondary);
    font-weight: 500;
    font-size: 14px;
    line-height: 1;
    white-space: nowrap;
    text-align: left;
    background: transparent;
    border: none;
    border-bottom: 2px solid transparent;
    cursor: pointer;
    transition: color 0.2s ease, border-color 0.2s ease, background 0.2s ease;

    &:hover {
      color: var(--primary);
      background: var(--primary-light);
    }

    &.active {
      color: var(--primary);
      font-weight: 600;
      border-bottom-color: var(--primary);
    }
  }

  .top-menu-arrow {
    font-size: 12px;
    opacity: 0.65;
  }
</style>

<style lang="less">
  /* Ant Design 风格顶栏下拉：白底、轻阴影、选中主色 */
  .arco-dropdown-list-wrapper {
    .arco-dropdown-option:hover,
    .arco-dropdown-option-active {
      color: var(--primary);
      background-color: var(--primary-light);
    }
  }
</style>
