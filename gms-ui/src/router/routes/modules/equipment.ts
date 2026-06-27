import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const EQUIPMENT: AppRouteRecordRaw = {
  path: '/equipment',
  name: 'equipment',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.equipment',
    requiresAuth: true,
    icon: 'icon-skin',
    order: 4,
  },
  children: [
    {
      path: 'equipEnhance',
      name: 'EquipEnhance',
      component: () => import('@/views/game/equipEnhance/index.vue'),
      meta: {
        locale: 'menu.equipment.equipEnhance',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'equipDamageBonus',
      name: 'EquipDamageBonus',
      component: () => import('@/views/game/equipDamageBonus/index.vue'),
      meta: {
        locale: 'menu.equipment.equipDamageBonus',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'setDamageBonus',
      name: 'SetDamageBonus',
      component: () => import('@/views/game/setDamageBonus/index.vue'),
      meta: {
        locale: 'menu.equipment.setDamageBonus',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'equipAdvance',
      name: 'EquipAdvance',
      component: () => import('@/views/game/equipAdvance/index.vue'),
      meta: {
        locale: 'menu.equipment.equipAdvance',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'alchemyRecipe',
      name: 'AlchemyRecipe',
      component: () => import('@/views/game/alchemyRecipe/index.vue'),
      meta: {
        locale: 'menu.equipment.alchemyRecipe',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'forgeRecipe',
      name: 'ForgeRecipe',
      component: () => import('@/views/game/forgeRecipe/index.vue'),
      meta: {
        locale: 'menu.equipment.forgeRecipe',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default EQUIPMENT;
