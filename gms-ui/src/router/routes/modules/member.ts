import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/** 会员与玩家：账户、角色、商城、背包等 */
const MEMBER: AppRouteRecordRaw = {
  path: '/member',
  name: 'member',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.member',
    requiresAuth: true,
    icon: 'icon-user-group',
    order: 3,
  },
  children: [
    {
      path: 'list',
      name: 'MemberAccountList',
      component: () => import('@/views/account/list/index.vue'),
      meta: {
        locale: 'menu.account.list',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'player',
      name: 'MemberPlayerList',
      component: () => import('@/views/account/player/index.vue'),
      meta: {
        locale: 'menu.account.player',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'cashShop',
      name: 'MemberCashShop',
      component: () => import('@/views/game/cashShop/index.vue'),
      meta: {
        locale: 'menu.game.cashShop',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'inventory',
      name: 'MemberInventory',
      component: () => import('@/views/game/inventory/index.vue'),
      meta: {
        locale: 'menu.game.inventory',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default MEMBER;
