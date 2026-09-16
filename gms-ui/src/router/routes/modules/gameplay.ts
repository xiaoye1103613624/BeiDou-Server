import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/**
 * 玩法：商城、百宝箱等消费/抽奖向玩法
 * 预留：副本、排行玩法扩展等可挂此分组
 */
const GAMEPLAY: AppRouteRecordRaw = {
  path: '/gameplay',
  name: 'gameplay',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.gameplay',
    requiresAuth: true,
    icon: 'icon-trophy',
    order: 6,
  },
  children: [
    {
      path: 'cashShop',
      name: 'MemberCashShop',
      component: () => import('@/views/game/cashShop/index.vue'),
      meta: {
        locale: 'menu.game.cashShop',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'gachapon',
      name: 'GrowthGachapon',
      component: () => import('@/views/game/gachapon/index.vue'),
      meta: {
        locale: 'menu.game.gachapon',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
  ],
};

export default GAMEPLAY;
