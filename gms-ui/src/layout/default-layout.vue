<template>
  <a-layout class="layout" :class="{ mobile: appStore.hideMenu }">
    <div v-if="navbar" class="layout-navbar">
      <NavBar />
    </div>
    <a-layout>
      <a-layout>
        <a-layout-sider
          v-if="renderMenu"
          v-show="!hideMenu"
          class="layout-sider"
          breakpoint="xl"
          :collapsed="collapsed"
          :collapsible="true"
          :width="menuWidth"
          :style="{ paddingTop: navbar ? 'var(--bd-navbar-height)' : '' }"
          :hide-trigger="true"
          @collapse="setCollapsed"
        >
          <div class="menu-wrapper">
            <Menu />
          </div>
        </a-layout-sider>
        <a-drawer
          v-if="hideMenu"
          :visible="drawerVisible"
          placement="left"
          :footer="false"
          mask-closable
          :closable="false"
          @cancel="drawerCancel"
        >
          <Menu />
        </a-drawer>
        <a-layout class="layout-content" :style="paddingStyle">
          <TabBar v-if="appStore.tabBar" />
          <a-layout-content class="layout-content-main">
            <PageLayout />
          </a-layout-content>
          <Footer v-if="footer" />
        </a-layout>
      </a-layout>
    </a-layout>
  </a-layout>
</template>

<script lang="ts" setup>
  import { ref, computed, watch, provide, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { useAppStore, useUserStore } from '@/store';
  import NavBar from '@/components/navbar/index.vue';
  import Menu from '@/components/menu/index.vue';
  import Footer from '@/components/footer/index.vue';
  import TabBar from '@/components/tab-bar/index.vue';
  import usePermission from '@/hooks/permission';
  import useResponsive from '@/hooks/responsive';
  import PageLayout from './page-layout.vue';

  const isInit = ref(false);
  const appStore = useAppStore();
  const userStore = useUserStore();
  const router = useRouter();
  const route = useRoute();
  const permission = usePermission();
  useResponsive(true);
  const navbarHeight = `var(--bd-navbar-height)`;
  const navbar = computed(() => appStore.navbar);
  const renderMenu = computed(() => appStore.menu && !appStore.topMenu);
  const hideMenu = computed(() => appStore.hideMenu);
  const footer = computed(() => appStore.footer);
  const menuWidth = computed(() => {
    return appStore.menuCollapse ? 48 : appStore.menuWidth;
  });
  const collapsed = computed(() => {
    return appStore.menuCollapse;
  });
  const paddingStyle = computed(() => {
    const paddingLeft =
      renderMenu.value && !hideMenu.value
        ? { paddingLeft: `${menuWidth.value}px` }
        : {};
    const paddingTop = navbar.value ? { paddingTop: navbarHeight } : {};
    return { ...paddingLeft, ...paddingTop };
  });
  const setCollapsed = (val: boolean) => {
    if (!isInit.value) return;
    appStore.updateSettings({ menuCollapse: val });
  };
  watch(
    () => userStore.role,
    (roleValue) => {
      if (roleValue && !permission.accessRouter(route))
        router.push({ name: 'notFound' });
    }
  );
  const drawerVisible = ref(false);
  const drawerCancel = () => {
    drawerVisible.value = false;
  };
  provide('toggleDrawerMenu', () => {
    drawerVisible.value = !drawerVisible.value;
  });
  onMounted(() => {
    isInit.value = true;
    appStore.fetchServerMenuConfig({ silent: true });
  });
</script>

<style scoped lang="less">
  .layout {
    width: 100%;
    height: 100%;
  }

  .layout-navbar {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 100;
    width: 100%;
    height: var(--bd-navbar-height);
  }

  .layout-sider {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 99;
    height: 100%;
    background: var(--bd-sidebar) !important;
    border-right: 1px solid var(--border);
    box-shadow: none;
    transition: all 0.2s cubic-bezier(0.34, 0.69, 0.1, 1);

    &::after {
      display: none;
    }

    > :deep(.arco-layout-sider-children) {
      overflow-y: hidden;
      background: transparent;
    }

    :deep(.arco-menu) {
      background: transparent;
    }

    :deep(.arco-menu-item),
    :deep(.arco-menu-inline-header),
    :deep(.arco-menu-pop-header) {
      color: var(--text-secondary);
      border-radius: 8px;
      margin: 2px 8px;
      width: auto;
    }

    :deep(.arco-menu-item:hover),
    :deep(.arco-menu-inline-header:hover) {
      color: var(--text-primary);
      background: var(--bg-hover);
    }

    :deep(.arco-menu-selected) {
      color: var(--primary) !important;
      background: var(--primary-light) !important;
      font-weight: 600;
      box-shadow: inset 3px 0 0 var(--primary);
    }

    :deep(.arco-menu-selected::before) {
      display: none;
    }

    :deep(.arco-menu-inline-header.arco-menu-selected) {
      color: var(--primary);
    }

    :deep(.arco-menu-collapse-button) {
      color: var(--text-secondary);
      background: var(--bg-hover);
      border: none;
    }
  }

  .menu-wrapper {
    height: 100%;
    overflow: auto;
    overflow-x: hidden;
    padding: 8px 0 16px;

    :deep(.arco-menu) {
      ::-webkit-scrollbar {
        width: 8px;
        height: 4px;
      }

      ::-webkit-scrollbar-thumb {
        border: 2px solid transparent;
        background-clip: padding-box;
        border-radius: 7px;
        background-color: var(--border-strong);
      }
    }
  }

  .layout-content {
    min-height: 100vh;
    overflow-y: hidden;
    background: var(--bd-surface);
    transition: padding 0.2s cubic-bezier(0.34, 0.69, 0.1, 1);
  }

  .layout-content-main {
    min-height: calc(100vh - var(--bd-navbar-height) - 40px);
  }
</style>
