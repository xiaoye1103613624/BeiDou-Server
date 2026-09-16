import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/** 旧 /growth 路径 → 强化 / 游戏管理 / 玩法 */
const GROWTH_LEGACY: AppRouteRecordRaw = {
  path: '/growth',
  name: 'growth',
  component: DEFAULT_LAYOUT,
  meta: {
    requiresAuth: true,
    hideInMenu: true,
  },
  children: [
    {
      path: '',
      redirect: '/enhance/setItem',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'setItem',
      redirect: '/enhance/setItem',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'drop',
      redirect: '/game/drop',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'drop/global',
      redirect: '/game/drop/global',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'gachapon',
      redirect: '/gameplay/gachapon',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'petGrowth',
      redirect: '/enhance/petGrowth',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'alchemyRecipe',
      redirect: '/enhance/alchemyRecipe',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'alchemistRecipe',
      redirect: '/enhance/alchemistRecipe',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'forgeRecipe',
      redirect: '/enhance/forgeRecipe',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'alchemyTier',
      redirect: '/enhance/alchemyTier',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
  ],
};

export default GROWTH_LEGACY;
