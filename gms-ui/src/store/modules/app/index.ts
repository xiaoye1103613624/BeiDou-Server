import { defineStore } from 'pinia';
import { Notification } from '@arco-design/web-vue';
import type { NotificationReturn } from '@arco-design/web-vue/es/notification/interface';
import type { RouteRecordNormalized } from 'vue-router';
import defaultSettings from '@/config/settings.json';
import { getMenuList } from '@/api/user';
import { applyThemeColor } from '@/utils/theme';
import { AppState } from './types';

const LAYOUT_SETTINGS_KEY = 'bd-layout-settings';

/** 需要持久化到 localStorage 的布局相关字段 */
const PERSIST_KEYS: (keyof AppState)[] = [
  'navbar',
  'menu',
  'topMenu',
  'footer',
  'tabBar',
  'menuWidth',
  'menuCollapse',
  'colorWeak',
  'menuFromServer',
];

function loadPersistedSettings(): Partial<AppState> {
  try {
    const raw = localStorage.getItem(LAYOUT_SETTINGS_KEY);
    if (!raw) return {};
    const parsed = JSON.parse(raw) as Record<string, unknown>;
    const result: Partial<AppState> = {};
    PERSIST_KEYS.forEach((key) => {
      if (parsed[key as string] !== undefined) {
        (result as Record<string, unknown>)[key as string] =
          parsed[key as string];
      }
    });
    return result;
  } catch {
    return {};
  }
}

function savePersistedSettings(state: AppState) {
  const payload: Record<string, unknown> = {};
  PERSIST_KEYS.forEach((key) => {
    payload[key as string] = state[key];
  });
  localStorage.setItem(LAYOUT_SETTINGS_KEY, JSON.stringify(payload));
}

const useAppStore = defineStore('app', {
  state: (): AppState => {
    const savedColor = localStorage.getItem('bd-theme-color');
    return {
      ...defaultSettings,
      ...loadPersistedSettings(),
      themeColor: savedColor || defaultSettings.themeColor,
    };
  },

  getters: {
    appCurrentSetting(state: AppState): AppState {
      return { ...state };
    },
    appDevice(state: AppState) {
      return state.device;
    },
    appAsyncMenus(state: AppState): RouteRecordNormalized[] {
      return state.serverMenu as unknown as RouteRecordNormalized[];
    },
  },

  actions: {
    // Update app settings
    updateSettings(partial: Partial<AppState>) {
      // @ts-ignore-next-line
      this.$patch(partial);
      savePersistedSettings(this.$state);
    },

    /** 在顶部导航 / 左侧导航之间切换 */
    toggleNavLayout() {
      const nextTopMenu = !this.topMenu;
      this.updateSettings({
        topMenu: nextTopMenu,
        menu: true,
        menuCollapse: nextTopMenu ? false : this.menuCollapse,
      });
    },

    // Change theme color
    toggleTheme(dark: boolean) {
      if (dark) {
        this.theme = 'dark';
        document.body.setAttribute('arco-theme', 'dark');
        document.documentElement.setAttribute('data-theme', 'dark');
      } else {
        this.theme = 'light';
        document.body.removeAttribute('arco-theme');
        document.documentElement.setAttribute('data-theme', 'light');
      }
      applyThemeColor(this.themeColor || '#3B6EA5');
    },
    setThemeColor(color: string) {
      this.themeColor = color;
      localStorage.setItem('bd-theme-color', color);
      applyThemeColor(color);
    },
    toggleDevice(device: string) {
      this.device = device;
    },
    toggleMenu(value: boolean) {
      this.hideMenu = value;
    },
    async fetchServerMenuConfig(options?: { silent?: boolean }) {
      const silent = options?.silent === true;
      let notifyInstance: NotificationReturn | null = null;
      try {
        if (!silent) {
          notifyInstance = Notification.info({
            id: 'menuNotice',
            content: 'loading',
            closable: true,
          });
        }
        const { data } = await getMenuList();
        this.serverMenu = (data as unknown as RouteRecordNormalized[]) || [];
        if (!silent) {
          notifyInstance = Notification.success({
            id: 'menuNotice',
            content: 'success',
            closable: true,
          });
        }
      } catch (error) {
        // 服务端菜单拉取失败时保留本地路由侧栏，不阻断进入后台
        this.serverMenu = [];
        if (!silent) {
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          notifyInstance = Notification.error({
            id: 'menuNotice',
            content: 'error',
            closable: true,
          });
        }
      }
    },
    clearServerMenu() {
      this.serverMenu = [];
    },
  },
});

export default useAppStore;
