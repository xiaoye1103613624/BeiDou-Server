import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const CUSTOM_GAMEPLAY: AppRouteRecordRaw = {
  path: '/customGameplay',
  name: 'customGameplay',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.customGameplay',
    requiresAuth: true,
    icon: 'icon-experiment',
    order: 2,
  },
  children: [
    {
      path: 'overview',
      name: 'CustomGameplayOverview',
      component: () => import('@/views/customGameplay/overview/index.vue'),
      meta: {
        locale: 'menu.customGameplay.overview',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default CUSTOM_GAMEPLAY;
