import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const BOSS: AppRouteRecordRaw = {
  path: '/boss',
  name: 'boss',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.boss',
    requiresAuth: true,
    icon: 'icon-relation',
    order: 3,
  },
  children: [
    {
      path: 'config',
      name: 'BossConfig',
      component: () => import('@/views/bossConfig/list/index.vue'),
      meta: {
        locale: 'menu.boss.config',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'eliteBoss',
      name: 'EliteBoss',
      component: () => import('@/views/game/eliteBoss/index.vue'),
      meta: {
        locale: 'menu.boss.eliteBoss',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default BOSS;
