export type RoleType = '' | '*' | 'admin' | 'operator' | 'user';
export interface UserState {
  id?: number;
  name?: string;
  pin?: string;
  pic?: string;
  loggedin?: number;
  lastlogin?: string;
  createdat?: string;
  birthday?: string;
  banned?: boolean;
  banreason?: string;
  macs?: string;
  nxCredit?: number;
  maplePoint?: number;
  nxPrepaid?: number;
  characterslots?: number;
  gender?: number;
  tempban?: string;
  greason?: string;
  tos?: boolean;
  sitelogged?: string;
  /** 后端可能返回 0/1 或 boolean */
  webadmin?: boolean | number;
  nick?: string;
  mute?: boolean;
  email?: string;
  ip?: string;
  rewardpoints?: number;
  votepoints?: number;
  hwid?: string;
  language?: number;
  /** 主角色编码（AccountInfoDTO.role） */
  role: RoleType;
  /** 角色列表（AccountInfoDTO.roles） */
  roles: RoleType[];
  avatar: undefined;
}
