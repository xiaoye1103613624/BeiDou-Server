import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/**
 * 强化：套装、配方、宠物成长等养成强化
 * 预留：装备强化、潜能等玩法后台可挂此分组
 */
const ENHANCE: AppRouteRecordRaw = {
  path: '/enhance',
  name: 'enhance',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.enhance',
    requiresAuth: true,
    icon: 'icon-thunderbolt',
    order: 5,
  },
  children: [
    {
      path: 'setItem',
      name: 'GrowthSetItem',
      component: () => import('@/views/game/setItem/index.vue'),
      meta: {
        locale: 'menu.game.setItem',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'petGrowth',
      name: 'GrowthPetGrowth',
      component: () => import('@/views/game/petGrowth/index.vue'),
      meta: {
        locale: 'menu.game.petGrowth',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'alchemyRecipe',
      name: 'GrowthAlchemyRecipe',
      component: () => import('@/views/game/alchemyRecipe/index.vue'),
      meta: {
        locale: 'menu.game.alchemyRecipe',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'alchemistRecipe',
      name: 'GrowthAlchemistRecipe',
      component: () => import('@/views/game/alchemistRecipe/index.vue'),
      meta: {
        locale: 'menu.game.alchemistRecipe',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'forgeRecipe',
      name: 'GrowthForgeRecipe',
      component: () => import('@/views/game/forgeRecipe/index.vue'),
      meta: {
        locale: 'menu.game.forgeRecipe',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'alchemyTier',
      name: 'GrowthAlchemyTier',
      component: () => import('@/views/game/alchemyTier/index.vue'),
      meta: {
        locale: 'menu.game.alchemyTier',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
  ],
};

export default ENHANCE;
