import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/**
 * 活动：运营活动与签到
 * 预留：限时活动、运营日历等可继续挂在此分组下
 */
const ACTIVITY: AppRouteRecordRaw = {
  path: '/activity',
  name: 'activity',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.activity',
    requiresAuth: true,
    icon: 'icon-gift',
    order: 3,
  },
  children: [
    {
      path: 'activity',
      name: 'DailyActivity',
      component: () => import('@/views/game/activity/index.vue'),
      meta: {
        locale: 'menu.game.activity',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'dailyCheckin',
      name: 'DailyCheckin',
      component: () => import('@/views/game/dailyCheckin/index.vue'),
      meta: {
        locale: 'menu.game.dailyCheckin',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
  ],
};

export default ACTIVITY;
