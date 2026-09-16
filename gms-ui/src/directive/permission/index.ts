import { DirectiveBinding } from 'vue';
import { useUserStore } from '@/store';

function checkPermission(el: HTMLElement, binding: DirectiveBinding) {
  const { value } = binding;
  const userStore = useUserStore();
  let roles: string[] = [];
  if (userStore.roles?.length) {
    roles = userStore.roles;
  } else if (userStore.role) {
    roles = [userStore.role];
  }

  if (Array.isArray(value)) {
    if (value.length > 0) {
      const hasPermission = value.some((item) =>
        roles.map(String).includes(String(item))
      );
      if (!hasPermission && el.parentNode) {
        el.parentNode.removeChild(el);
      }
    }
  } else {
    throw new Error(`need roles! Like v-permission="['admin','user']"`);
  }
}

export default {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    checkPermission(el, binding);
  },
  updated(el: HTMLElement, binding: DirectiveBinding) {
    checkPermission(el, binding);
  },
};
