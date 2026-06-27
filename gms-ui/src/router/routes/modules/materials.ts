import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const MATERIALS: AppRouteRecordRaw = {
  path: '/materials',
  name: 'materials',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.materials',
    requiresAuth: true,
    icon: 'icon-storage',
    order: 5,
  },
  children: [
    {
      path: 'warehouse',
      name: 'Warehouse',
      component: () => import('@/views/game/warehouse/index.vue'),
      meta: {
        locale: 'menu.materials.warehouse',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'scrollDecompose',
      name: 'ScrollDecompose',
      component: () => import('@/views/game/scrollDecompose/index.vue'),
      meta: {
        locale: 'menu.materials.scrollDecompose',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default MATERIALS;
