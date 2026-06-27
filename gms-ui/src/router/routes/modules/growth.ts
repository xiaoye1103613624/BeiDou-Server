import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const GROWTH: AppRouteRecordRaw = {
  path: '/growth',
  name: 'growth',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.growth',
    requiresAuth: true,
    icon: 'icon-thumb-up',
    order: 3,
  },
  children: [
    {
      path: 'newbieGift',
      name: 'NewbieGift',
      component: () => import('@/views/game/newbieGift/index.vue'),
      meta: {
        locale: 'menu.growth.newbieGift',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'levelReward',
      name: 'LevelReward',
      component: () => import('@/views/game/levelReward/index.vue'),
      meta: {
        locale: 'menu.growth.levelReward',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'dailyDungeon',
      name: 'DailyDungeon',
      component: () => import('@/views/game/dailyDungeon/index.vue'),
      meta: {
        locale: 'menu.growth.dailyDungeon',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'dailyBoss',
      name: 'DailyBoss',
      component: () => import('@/views/game/dailyBoss/index.vue'),
      meta: {
        locale: 'menu.growth.dailyBoss',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'dailyExplore',
      name: 'DailyExplore',
      component: () => import('@/views/game/dailyExplore/index.vue'),
      meta: {
        locale: 'menu.growth.dailyExplore',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'mentor',
      name: 'Mentor',
      component: () => import('@/views/game/mentor/index.vue'),
      meta: {
        locale: 'menu.growth.mentor',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'toyCollection',
      name: 'ToyCollection',
      component: () => import('@/views/game/toyCollection/index.vue'),
      meta: {
        locale: 'menu.growth.toyCollection',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'guild',
      name: 'Guild',
      component: () => import('@/views/game/guild/index.vue'),
      meta: {
        locale: 'menu.growth.guild',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'alliance',
      name: 'Alliance',
      component: () => import('@/views/game/alliance/index.vue'),
      meta: {
        locale: 'menu.growth.alliance',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default GROWTH;
