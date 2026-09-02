import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/** 日常运营：天气、指令、封禁、文件等 */
const DAILY: AppRouteRecordRaw = {
  path: '/daily',
  name: 'daily',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.daily',
    requiresAuth: true,
    icon: 'icon-calendar',
    order: 1,
  },
  children: [
    {
      path: 'weather',
      name: 'DailyWeather',
      component: () => import('@/views/game/weather/index.vue'),
      meta: {
        locale: 'menu.game.weather',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'commandInfo',
      name: 'DailyCommandInfo',
      component: () => import('@/views/game/commandInfo/index.vue'),
      meta: {
        locale: 'menu.game.command',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'autoban',
      name: 'DailyAutoban',
      component: () => import('@/views/game/autoban/index.vue'),
      meta: {
        locale: 'menu.game.autoban',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'file',
      name: 'DailyFile',
      component: () => import('@/views/game/file/index.vue'),
      meta: {
        locale: 'menu.game.file',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'dailyCheckin',
      name: 'DailyCheckin',
      component: () => import('@/views/game/dailyCheckin/index.vue'),
      meta: {
        locale: 'menu.game.dailyCheckin',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'activity',
      name: 'DailyActivity',
      component: () => import('@/views/game/activity/index.vue'),
      meta: {
        locale: 'menu.game.activity',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'sidebarTool',
      name: 'DailySidebarTool',
      component: () => import('@/views/game/sidebarTool/index.vue'),
      meta: {
        locale: 'menu.game.sidebarTool',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default DAILY;
