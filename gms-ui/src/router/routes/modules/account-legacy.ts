import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/** 旧 /account 路径 → 会员中心 */
const ACCOUNT_LEGACY: AppRouteRecordRaw = {
  path: '/account',
  name: 'accountLegacy',
  component: DEFAULT_LAYOUT,
  meta: {
    requiresAuth: true,
    hideInMenu: true,
  },
  children: [
    {
      path: '',
      redirect: '/member/list',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'list',
      redirect: '/member/list',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
    {
      path: 'player',
      redirect: '/member/player',
      meta: { requiresAuth: true, hideInMenu: true },
    } as AppRouteRecordRaw,
  ],
};

export default ACCOUNT_LEGACY;
