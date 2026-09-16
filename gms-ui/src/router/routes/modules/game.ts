import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/**
 * 游戏管理：服务端参数、商店、爆率、天气、客户端右边栏等
 * 预留：地图/NPC/任务数据管理可挂此分组
 */
const GAME: AppRouteRecordRaw = {
  path: '/game',
  name: 'game',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.game',
    requiresAuth: true,
    icon: 'icon-dice',
    order: 2,
  },
  children: [
    {
      path: 'config',
      name: 'Config',
      component: () => import('@/views/game/config/index.vue'),
      meta: {
        locale: 'menu.game.config',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'npcShop',
      name: 'NpcShop',
      component: () => import('@/views/game/npcShop/index.vue'),
      meta: {
        locale: 'menu.game.npcShop',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'windowCashShop',
      name: 'windowCashShop',
      component: () => import('@/views/game/windowCashShop/index.vue'),
      meta: {
        locale: 'menu.game.windowCashShop',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    // 旧「客户端操作」入口：迁至 /client/windowCashShopSync
    {
      path: 'windowCashShop/clientSync',
      redirect: '/client/windowCashShopSync',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'weather',
      name: 'DailyWeather',
      component: () => import('@/views/game/weather/index.vue'),
      meta: {
        locale: 'menu.game.weather',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'drop',
      name: 'GrowthDrop',
      component: () => import('@/views/game/drop/index.vue'),
      meta: {
        locale: 'menu.game.drop',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'drop/global',
      name: 'GrowthGlobalDrop',
      component: () => import('@/views/game/drop/global.vue'),
      meta: {
        locale: 'menu.game.drop.global',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'quest',
      name: 'GameQuest',
      component: () => import('@/views/game/quest/index.vue'),
      meta: {
        locale: 'menu.game.quest',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'sidebarTool',
      name: 'DailySidebarTool',
      component: () => import('@/views/game/sidebarTool/index.vue'),
      meta: {
        locale: 'menu.game.sidebarTool',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    // 旧路径兼容（菜单已迁移至 system / activity / enhance / gameplay / member）
    {
      path: 'sysRole',
      redirect: '/system/sysRole',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'sysMenu',
      redirect: '/system/sysMenu',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'commandInfo',
      redirect: '/system/commandInfo',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'autoban',
      redirect: '/system/autoban',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'file',
      redirect: '/system/file',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'setItem',
      redirect: '/enhance/setItem',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'gachapon',
      redirect: '/gameplay/gachapon',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'cashShop',
      redirect: '/gameplay/cashShop',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'inventory',
      redirect: '/member/inventory',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
  ],
};

export default GAME;
