import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/** 旧 /daily 路径 → 系统管理 / 活动 / 游戏管理 */
const DAILY_LEGACY: AppRouteRecordRaw = {
  path: '/daily',
  name: 'daily',
  component: DEFAULT_LAYOUT,
  meta: {
    requiresAuth: true,
    hideInMenu: true,
  },
  children: [
    {
      path: '',
      redirect: '/system/file',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'weather',
      redirect: '/game/weather',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'commandInfo',
      redirect: '/system/commandInfo',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'autoban',
      redirect: '/system/autoban',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'file',
      redirect: '/system/file',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'dailyCheckin',
      redirect: '/activity/dailyCheckin',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'activity',
      redirect: '/activity/activity',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'sidebarTool',
      redirect: '/game/sidebarTool',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
  ],
};

export default DAILY_LEGACY;
