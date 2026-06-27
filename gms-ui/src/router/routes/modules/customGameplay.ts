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
    {
      path: 'independentDrop',
      name: 'IndependentDrop',
      component: () =>
        import('@/views/customGameplay/independentDrop/index.vue'),
      meta: {
        locale: 'menu.customGameplay.independentDrop',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'paohuan',
      name: 'Paohuan',
      component: () => import('@/views/game/paohuan/index.vue'),
      meta: {
        locale: 'menu.customGameplay.paohuan',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'dailyActiveTask',
      name: 'DailyActiveTask',
      component: () => import('@/views/game/dailyActiveTask/index.vue'),
      meta: {
        locale: 'menu.customGameplay.dailyActiveTask',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default CUSTOM_GAMEPLAY;
