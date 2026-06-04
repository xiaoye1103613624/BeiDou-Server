import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const FAMILY: AppRouteRecordRaw = {
  path: '/family',
  name: 'family',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.family',
    requiresAuth: true,
    icon: 'icon-user-group',
    order: 4,
  },
  children: [
    {
      path: 'guild',
      name: 'GuildList',
      component: () => import('@/views/family/guildList/index.vue'),
      meta: {
        locale: 'menu.family.guild',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'familyTree',
      name: 'FamilyTree',
      component: () => import('@/views/family/familyTree/index.vue'),
      meta: {
        locale: 'menu.family.family',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'alliance',
      name: 'AllianceList',
      component: () => import('@/views/family/alliance/index.vue'),
      meta: {
        locale: 'menu.family.alliance',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: 'marriage',
      name: 'MarriageList',
      component: () => import('@/views/family/marriage/index.vue'),
      meta: {
        locale: 'menu.family.marriage',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
};

export default FAMILY;
