import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const GAME: AppRouteRecordRaw = {
  path: '/game',
  name: 'game',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.game',
    requiresAuth: true,
    icon: 'icon-dice',
    order: 0,
  },
  children: [
    {
      path: 'config',
      name: 'Config',
      component: () => import('@/views/game/config/index.vue'),
      meta: {
        locale: 'menu.game.config',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'cashShop',
      name: 'CashShop',
      component: () => import('@/views/game/cashShop/index.vue'),
      meta: {
        locale: 'menu.game.cashShop',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'npcShop',
      name: 'NpcShop',
      component: () => import('@/views/game/npcShop/index.vue'),
      meta: {
        locale: 'menu.game.npcShop',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'drop',
      name: 'drop',
      component: () => import('@/views/game/drop/index.vue'),
      meta: {
        locale: 'menu.game.drop',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'drop/global',
      name: 'globalDrop',
      component: () => import('@/views/game/drop/global.vue'),
      meta: {
        locale: 'menu.game.drop.global',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'inventory',
      name: 'inventory',
      component: () => import('@/views/game/inventory/index.vue'),
      meta: {
        locale: 'menu.game.inventory',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'gachapon',
      name: 'gachapon',
      component: () => import('@/views/game/gachapon/index.vue'),
      meta: {
        locale: 'menu.game.gachapon',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'commandInfo',
      name: 'commandInfo',
      component: () => import('@/views/game/commandInfo/index.vue'),
      meta: {
        locale: 'menu.game.command',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'file',
      name: 'file',
      component: () => import('@/views/game/file/index.vue'),
      meta: {
        locale: 'menu.game.file',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'autoban',
      name: 'autoban',
      component: () => import('@/views/game/autoban/index.vue'),
      meta: {
        locale: 'menu.game.autoban',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'equipEnhance',
      name: 'EquipEnhance',
      component: () => import('@/views/game/equipEnhance/index.vue'),
      meta: {
        locale: 'menu.game.equipEnhance',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'equipAdvance',
      name: 'EquipAdvance',
      component: () => import('@/views/game/equipAdvance/index.vue'),
      meta: {
        locale: 'menu.game.equipAdvance',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'newbieGift',
      name: 'NewbieGift',
      component: () => import('@/views/game/newbieGift/index.vue'),
      meta: {
        locale: 'menu.game.newbieGift',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'levelReward',
      name: 'LevelReward',
      component: () => import('@/views/game/levelReward/index.vue'),
      meta: {
        locale: 'menu.game.levelReward',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'toyCollection',
      name: 'ToyCollection',
      component: () => import('@/views/game/toyCollection/index.vue'),
      meta: {
        locale: 'menu.game.toyCollection',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'dailyDungeon',
      name: 'DailyDungeon',
      component: () => import('@/views/game/dailyDungeon/index.vue'),
      meta: {
        locale: 'menu.game.dailyDungeon',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'dailyBoss',
      name: 'DailyBoss',
      component: () => import('@/views/game/dailyBoss/index.vue'),
      meta: {
        locale: 'menu.game.dailyBoss',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'guild',
      name: 'Guild',
      component: () => import('@/views/game/guild/index.vue'),
      meta: {
        locale: 'menu.game.guild',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'alliance',
      name: 'Alliance',
      component: () => import('@/views/game/alliance/index.vue'),
      meta: {
        locale: 'menu.game.alliance',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'warehouse',
      name: 'Warehouse',
      component: () => import('@/views/game/warehouse/index.vue'),
      meta: {
        locale: 'menu.game.warehouse',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'scrollDecompose',
      name: 'ScrollDecompose',
      component: () => import('@/views/game/scrollDecompose/index.vue'),
      meta: {
        locale: 'menu.game.scrollDecompose',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'paohuan',
      name: 'Paohuan',
      component: () => import('@/views/game/paohuan/index.vue'),
      meta: {
        locale: 'menu.game.paohuan',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'mentor',
      name: 'Mentor',
      component: () => import('@/views/game/mentor/index.vue'),
      meta: {
        locale: 'menu.game.mentor',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'dailyExplore',
      name: 'DailyExplore',
      component: () => import('@/views/game/dailyExplore/index.vue'),
      meta: {
        locale: 'menu.game.dailyExplore',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'cdk',
      name: 'CdkConfig',
      component: () => import('@/views/game/cdk/index.vue'),
      meta: {
        locale: 'menu.game.cdk',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'cdk/logs',
      name: 'CdkLogs',
      component: () => import('@/views/game/cdk/logView.vue'),
      meta: {
        locale: 'menu.game.cdk.logs',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'sponsor',
      name: 'SponsorConfig',
      component: () => import('@/views/game/sponsor/index.vue'),
      meta: {
        locale: 'menu.game.sponsor',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'sponsor/logs',
      name: 'SponsorLogs',
      component: () => import('@/views/game/sponsor/log.vue'),
      meta: {
        locale: 'menu.game.sponsor.logs',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default GAME;
