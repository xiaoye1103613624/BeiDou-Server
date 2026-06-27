import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const LOG_MONITOR: AppRouteRecordRaw = {
  path: '/logMonitor',
  name: 'logMonitor',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.logMonitor',
    requiresAuth: true,
    icon: 'icon-history',
    order: 7,
  },
  children: [
    {
      path: 'cdk',
      name: 'CdkLogs',
      component: () => import('@/views/game/cdk/logView.vue'),
      meta: {
        locale: 'menu.logMonitor.cdk',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'sponsor',
      name: 'SponsorLogs',
      component: () => import('@/views/game/sponsor/log.vue'),
      meta: {
        locale: 'menu.logMonitor.sponsor',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default LOG_MONITOR;
