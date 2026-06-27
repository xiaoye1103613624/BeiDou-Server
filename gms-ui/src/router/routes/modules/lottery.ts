import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const LOTTERY: AppRouteRecordRaw = {
  path: '/lottery',
  name: 'lottery',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.lottery',
    requiresAuth: true,
    icon: 'icon-gift',
    order: 8,
  },
  children: [
    {
      path: 'gachapon',
      name: 'gachapon',
      component: () => import('@/views/game/gachapon/index.vue'),
      meta: {
        locale: 'menu.lottery.gachapon',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default LOTTERY;
