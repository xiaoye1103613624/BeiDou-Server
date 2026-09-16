import { RouteLocationNormalized, RouteRecordRaw } from 'vue-router';
import { useUserStore } from '@/store';
import type { RoleType } from '@/store/modules/user/types';

function userRoleList(userStore: ReturnType<typeof useUserStore>): string[] {
  if (userStore.roles?.length) {
    return userStore.roles.map(String);
  }
  return userStore.role ? [String(userStore.role)] : [];
}

export default function usePermission() {
  const userStore = useUserStore();
  return {
    accessRouter(route: RouteLocationNormalized | RouteRecordRaw) {
      if (
        !route.meta?.requiresAuth ||
        !route.meta?.roles ||
        route.meta?.roles?.includes('*')
      ) {
        return true;
      }
      const roles = userRoleList(userStore);
      return roles.some((role) => route.meta?.roles?.includes(role));
    },
    findFirstPermissionRoute(_routers: any, role: RoleType | string = 'admin') {
      const cloneRouters = [..._routers];
      const roleList = role ? [String(role)] : userRoleList(userStore);
      while (cloneRouters.length) {
        const firstElement = cloneRouters.shift();
        if (
          firstElement?.meta?.roles?.find((el: string) => {
            return el === '*' || roleList.includes(el);
          })
        )
          return { name: firstElement.name };
        if (firstElement?.children) {
          cloneRouters.push(...firstElement.children);
        }
      }
      return null;
    },
  };
}
