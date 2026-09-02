import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/** 成长与养成：套装、爆率、百宝箱等 */
const GROWTH: AppRouteRecordRaw = {
  path: '/growth',
  name: 'growth',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.growth',
    requiresAuth: true,
    icon: 'icon-rise',
    order: 2,
  },
  children: [
    {
      path: 'setItem',
      name: 'GrowthSetItem',
      component: () => import('@/views/game/setItem/index.vue'),
      meta: {
        locale: 'menu.game.setItem',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'drop',
      name: 'GrowthDrop',
      component: () => import('@/views/game/drop/index.vue'),
      meta: {
        locale: 'menu.game.drop',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'drop/global',
      name: 'GrowthGlobalDrop',
      component: () => import('@/views/game/drop/global.vue'),
      meta: {
        locale: 'menu.game.drop.global',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'gachapon',
      name: 'GrowthGachapon',
      component: () => import('@/views/game/gachapon/index.vue'),
      meta: {
        locale: 'menu.game.gachapon',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'petGrowth',
      name: 'GrowthPetGrowth',
      component: () => import('@/views/game/petGrowth/index.vue'),
      meta: {
        locale: 'menu.game.petGrowth',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'alchemyRecipe',
      name: 'GrowthAlchemyRecipe',
      component: () => import('@/views/game/alchemyRecipe/index.vue'),
      meta: {
        locale: 'menu.game.alchemyRecipe',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'alchemistRecipe',
      name: 'GrowthAlchemistRecipe',
      component: () => import('@/views/game/alchemistRecipe/index.vue'),
      meta: {
        locale: 'menu.game.alchemistRecipe',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'forgeRecipe',
      name: 'GrowthForgeRecipe',
      component: () => import('@/views/game/forgeRecipe/index.vue'),
      meta: {
        locale: 'menu.game.forgeRecipe',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'alchemyTier',
      name: 'GrowthAlchemyTier',
      component: () => import('@/views/game/alchemyTier/index.vue'),
      meta: {
        locale: 'menu.game.alchemyTier',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default GROWTH;
