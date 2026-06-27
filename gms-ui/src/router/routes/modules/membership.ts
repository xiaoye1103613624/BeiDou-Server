import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const MEMBERSHIP: AppRouteRecordRaw = {
  path: '/membership',
  name: 'membership',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.membership',
    requiresAuth: true,
    icon: 'icon-safe',
    order: 6,
  },
  children: [
    {
      path: 'cdk',
      name: 'CdkConfig',
      component: () => import('@/views/game/cdk/index.vue'),
      meta: {
        locale: 'menu.membership.cdk',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'sponsor',
      name: 'SponsorConfig',
      component: () => import('@/views/game/sponsor/index.vue'),
      meta: {
        locale: 'menu.membership.sponsor',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default MEMBERSHIP;
