import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/** 服务端基础配置与 NPC 商店 */
const GAME: AppRouteRecordRaw = {
  path: '/game',
  name: 'game',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.game',
    requiresAuth: true,
    icon: 'icon-dice',
    order: 4,
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
        roles: ['admin'],
      },
    },
    {
      path: 'windowCashShop',
      name: 'windowCashShop',
      component: () => import('@/views/game/windowCashShop/index.vue'),
      meta: {
        locale: 'menu.game.windowCashShop',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'sysMenu',
      name: 'SysMenu',
      component: () => import('@/views/game/sysMenu/index.vue'),
      meta: {
        locale: 'menu.game.sysMenu',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    // 旧路径兼容（菜单已迁移至 daily / growth / member）
    {
      path: 'weather',
      redirect: '/daily/weather',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'commandInfo',
      redirect: '/daily/commandInfo',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'autoban',
      redirect: '/daily/autoban',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'file',
      redirect: '/daily/file',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'setItem',
      redirect: '/growth/setItem',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'drop',
      redirect: '/growth/drop',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'drop/global',
      redirect: '/growth/drop/global',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'gachapon',
      redirect: '/growth/gachapon',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
    {
      path: 'cashShop',
      redirect: '/member/cashShop',
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
