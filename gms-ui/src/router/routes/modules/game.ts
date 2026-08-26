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
      path: 'lottery',
      name: 'lottery',
      component: () => import('@/views/game/lottery/index.vue'),
      meta: {
        locale: 'menu.game.lottery',
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
      path: 'weather',
      name: 'weather',
      component: () => import('@/views/game/weather/index.vue'),
      meta: {
        locale: 'menu.game.weather',
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
      path: 'autoban',
      name: 'autoban',
      component: () => import('@/views/game/autoban/index.vue'),
      meta: {
        locale: 'menu.game.autoban',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'setItem',
      name: 'setItem',
      component: () => import('@/views/game/setItem/index.vue'),
      meta: {
        locale: 'menu.game.setItem',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'equipGrowth',
      name: 'equipGrowth',
      component: () => import('@/views/game/equipGrowth/index.vue'),
      meta: {
        locale: 'menu.game.equipGrowth',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'enhanceRule',
      name: 'enhanceRule',
      component: () => import('@/views/game/enhanceRule/index.vue'),
      meta: {
        locale: 'menu.game.enhanceRule',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'carryItemStat',
      name: 'carryItemStat',
      component: () => import('@/views/game/carryItemStat/index.vue'),
      meta: {
        locale: 'menu.game.carryItemStat',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'alchemyRecipe',
      name: 'alchemyRecipe',
      component: () => import('@/views/game/alchemyRecipe/index.vue'),
      meta: {
        locale: 'menu.game.alchemyRecipe',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'alchemistRecipe',
      name: 'alchemistRecipe',
      component: () => import('@/views/game/alchemistRecipe/index.vue'),
      meta: {
        locale: 'menu.game.alchemistRecipe',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'forgeRecipe',
      name: 'forgeRecipe',
      component: () => import('@/views/game/forgeRecipe/index.vue'),
      meta: {
        locale: 'menu.game.forgeRecipe',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'alchemyTier',
      name: 'alchemyTier',
      component: () => import('@/views/game/alchemyTier/index.vue'),
      meta: {
        locale: 'menu.game.alchemyTier',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'petGrowth',
      name: 'petGrowth',
      component: () => import('@/views/game/petGrowth/index.vue'),
      meta: {
        locale: 'menu.game.petGrowth',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'sponsor',
      name: 'sponsor',
      component: () => import('@/views/game/sponsor/index.vue'),
      meta: {
        locale: 'menu.game.sponsor',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'skillTech',
      name: 'skillTech',
      component: () => import('@/views/game/skillTech/index.vue'),
      meta: {
        locale: 'menu.game.skillTech',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'opLog',
      name: 'opLog',
      component: () => import('@/views/game/opLog/index.vue'),
      meta: {
        locale: 'menu.game.opLog',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default GAME;
