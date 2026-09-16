import { defineStore } from 'pinia';
import {
  login as userLogin,
  logout as userLogout,
  getUserInfo,
  LoginData,
} from '@/api/user';
import { setToken, clearToken } from '@/utils/auth';
import { removeRouteListener } from '@/utils/route-listener';
import { RoleType, UserState } from './types';
import useAppStore from '../app';

function isWebAdmin(webadmin: UserState['webadmin']): boolean {
  return webadmin === true || webadmin === 1;
}

function normalizeRoles(partial: Partial<UserState>): {
  role: RoleType;
  roles: RoleType[];
} {
  const fromList = (partial.roles || [])
    .map((r) => String(r || '').trim())
    .filter(Boolean) as RoleType[];
  const fromPrimary = partial.role
    ? ([String(partial.role).trim()] as RoleType[])
    : [];
  const merged = (fromList.length ? fromList : fromPrimary).filter(Boolean);
  if (merged.length) {
    return { role: merged[0], roles: merged };
  }
  if (isWebAdmin(partial.webadmin)) {
    return { role: 'admin', roles: ['admin'] };
  }
  return { role: 'user', roles: [] };
}

const useUserStore = defineStore('user', {
  state: (): UserState => ({
    id: undefined,
    name: undefined,
    pin: undefined,
    pic: undefined,
    loggedin: undefined,
    lastlogin: undefined,
    createdat: undefined,
    birthday: undefined,
    banned: undefined,
    banreason: undefined,
    macs: undefined,
    nxCredit: undefined,
    maplePoint: undefined,
    nxPrepaid: undefined,
    characterslots: undefined,
    gender: undefined,
    tempban: undefined,
    greason: undefined,
    tos: undefined,
    sitelogged: undefined,
    webadmin: undefined,
    nick: undefined,
    mute: undefined,
    email: undefined,
    ip: undefined,
    rewardpoints: undefined,
    votepoints: undefined,
    hwid: undefined,
    language: undefined,
    role: '',
    roles: [],
    avatar: undefined,
  }),

  getters: {
    userInfo(state: UserState): UserState {
      return { ...state };
    },
  },

  actions: {
    switchRoles() {
      return new Promise((resolve) => {
        this.role = this.role === 'user' ? 'admin' : 'user';
        this.roles = this.role ? [this.role] : [];
        resolve(this.role);
      });
    },
    // Set user's information（优先 AccountInfoDTO.role / roles）
    setInfo(partial: Partial<UserState>) {
      const { role, roles } = normalizeRoles(partial);
      this.$patch({
        ...partial,
        role,
        roles,
      });
    },

    // Reset user's information
    resetInfo() {
      this.$reset();
    },

    // Get user's information
    async info() {
      const res = await getUserInfo();

      this.setInfo(res.data);
    },

    // Login
    async login(loginForm: LoginData) {
      try {
        const res = await userLogin(loginForm);
        setToken(res.data.token);
      } catch (err) {
        clearToken();
        throw err;
      }
    },
    logoutCallBack() {
      const appStore = useAppStore();
      this.resetInfo();
      clearToken();
      removeRouteListener();
      appStore.clearServerMenu();
    },
    // Logout
    async logout() {
      try {
        await userLogout();
      } finally {
        this.logoutCallBack();
      }
    },
  },
});

export default useUserStore;
