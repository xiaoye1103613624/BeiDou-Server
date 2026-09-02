import { defineStore } from 'pinia';
import { Notification } from '@arco-design/web-vue';
import type { NotificationReturn } from '@arco-design/web-vue/es/notification/interface';
import type { RouteRecordNormalized } from 'vue-router';
import defaultSettings from '@/config/settings.json';
import { getMenuList } from '@/api/user';
import { AppState } from './types';

const useAppStore = defineStore('app', {
  state: (): AppState => ({ ...defaultSettings }),

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
    },

    // Change theme color
    toggleTheme(dark: boolean) {
      if (dark) {
        this.theme = 'dark';
        document.body.setAttribute('arco-theme', 'dark');
      } else {
        this.theme = 'light';
        document.body.removeAttribute('arco-theme');
      }
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
