import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const GAME: AppRouteRecordRaw = {
  path: '/game',
  name: 'game',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.game',
    requiresAuth: true,
    icon: 'icon-dice',
    order: 0,
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
      path: 'cashShop',
      name: 'CashShop',
      component: () => import('@/views/game/cashShop/index.vue'),
      meta: {
        locale: 'menu.game.cashShop',
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
      path: 'drop',
      name: 'drop',
      component: () => import('@/views/game/drop/index.vue'),
      meta: {
        locale: 'menu.game.drop',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'drop/global',
      name: 'globalDrop',
      component: () => import('@/views/game/drop/global.vue'),
      meta: {
        locale: 'menu.game.drop.global',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'inventory',
      name: 'inventory',
      component: () => import('@/views/game/inventory/index.vue'),
      meta: {
        locale: 'menu.game.inventory',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'gachapon',
      name: 'gachapon',
      component: () => import('@/views/game/gachapon/index.vue'),
      meta: {
        locale: 'menu.game.gachapon',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'commandInfo',
      name: 'commandInfo',
      component: () => import('@/views/game/commandInfo/index.vue'),
      meta: {
        locale: 'menu.game.command',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'file',
      name: 'file',
      component: () => import('@/views/game/file/index.vue'),
      meta: {
        locale: 'menu.game.file',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'monsterInvasion',
      name: 'monsterInvasion',
      component: () => import('@/views/game/monsterInvasion/index.vue'),
      meta: {
        locale: 'menu.game.monsterInvasion',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'townConfig',
      name: 'townConfig',
      component: () => import('@/views/townConfig/list/index.vue'),
      meta: {
        locale: 'menu.game.townConfig',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'catchUpExp',
      name: 'CatchUpExpConfig',
      component: () => import('@/views/catchUpExpConfig/list/index.vue'),
      meta: {
        locale: 'menu.game.catchUpExp',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'scriptManager',
      name: 'ScriptManager',
      component: () => import('@/views/game/scriptManager/index.vue'),
      meta: {
        locale: 'menu.game.scriptManager',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'cardCollection',
      name: 'cardCollection',
      component: () => import('@/views/game/cardCollection/index.vue'),
      meta: {
        locale: 'menu.game.cardCollection',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'equipEnhance',
      name: 'equipEnhance',
      component: () => import('@/views/game/equipEnhance/index.vue'),
      meta: {
        locale: 'menu.game.equipEnhance',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'medalEnhance',
      name: 'medalEnhance',
      component: () => import('@/views/game/medalEnhance/index.vue'),
      meta: {
        locale: 'menu.game.medalEnhance',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'xyCollection',
      name: 'xyCollection',
      component: () => import('@/views/game/xyCollection/index.vue'),
      meta: {
        locale: 'menu.game.xyCollection',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default GAME;
