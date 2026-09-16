import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

/**
 * 客户端菜单约定（menu.client）：
 * - 凡读写/同步/校验/浏览游戏客户端 Data（或其它客户端文件）的管理端功能，路由与侧栏必须挂在本模块。
 * - 路径配置共用 ClientPathConfig / ClientPathService；纯服务端 DB CRUD、wz/scripts FileTree、图标 CDN→服务端 static 等不放这里。
 * - 新增同类能力时跟此约定，勿再塞进 game/system 等分组。
 */
const CLIENT: AppRouteRecordRaw = {
  path: '/client',
  name: 'client',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.client',
    requiresAuth: true,
    icon: 'icon-storage',
    order: 7,
  },
  children: [
    {
      path: 'path',
      name: 'ClientPath',
      component: () => import('@/views/client/path/index.vue'),
      meta: {
        locale: 'menu.client.path',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'windowCashShopSync',
      name: 'ClientWindowCashShopSync',
      component: () => import('@/views/client/windowCashShopSync/index.vue'),
      meta: {
        locale: 'menu.client.windowCashShopSync',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'assetHub',
      name: 'ClientAssetHub',
      component: () => import('@/views/client/assetHub/index.vue'),
      meta: {
        locale: 'menu.client.assetHub',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
    {
      path: 'skillResources',
      name: 'ClientSkillResources',
      component: () => import('@/views/client/skillResources/index.vue'),
      meta: {
        locale: 'menu.client.skillResources',
        requiresAuth: true,
        roles: ['admin', 'operator'],
      },
    },
  ],
};

export default CLIENT;
