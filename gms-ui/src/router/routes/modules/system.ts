import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/**
 * 系统管理：后台自带能力（菜单、角色、文件、封禁、GM 指令等）
 */
const SYSTEM: AppRouteRecordRaw = {
  path: '/system',
  name: 'system',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.system',
    requiresAuth: true,
    icon: 'icon-settings',
    order: 1,
  },
  children: [
    {
      path: 'sysMenu',
      name: 'SysMenu',
      component: () => import('@/views/game/sysMenu/index.vue'),
      meta: {
        locale: 'menu.game.sysMenu',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'sysRole',
      name: 'SysRole',
      component: () => import('@/views/game/sysRole/index.vue'),
      meta: {
        locale: 'menu.game.sysRole',
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
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'autoban',
      name: 'DailyAutoban',
      component: () => import('@/views/game/autoban/index.vue'),
      meta: {
        locale: 'menu.game.autoban',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'commandInfo',
      name: 'DailyCommandInfo',
      component: () => import('@/views/game/commandInfo/index.vue'),
      meta: {
        locale: 'menu.game.command',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
  ],
};

export default SYSTEM;
