import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/**
 * 玩家：账户、角色、背包、排行
 * 预留：在线玩家监控、封号审计等可挂此分组
 */
const MEMBER: AppRouteRecordRaw = {
  path: '/member',
  name: 'member',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.member',
    requiresAuth: true,
    icon: 'icon-user-group',
    order: 4,
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
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'inventory',
      name: 'MemberInventory',
      component: () => import('@/views/game/inventory/index.vue'),
      meta: {
        locale: 'menu.game.inventory',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'ranking',
      name: 'MemberRanking',
      component: () => import('@/views/game/ranking/index.vue'),
      meta: {
        locale: 'menu.member.ranking',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    // 旧路径兼容
    {
      path: 'cashShop',
      redirect: '/gameplay/cashShop',
      meta: { hideInMenu: true, requiresAuth: true },
    } as AppRouteRecordRaw,
  ],
};

export default MEMBER;
