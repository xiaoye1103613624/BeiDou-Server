import config from '@/views/game/config/locale/zh-CN';
import drop from '@/views/game/drop/locale/zh-CN';
import bossConfig from '@/views/bossConfig/locale/zh-CN';
import eliteBoss from '@/views/game/eliteBoss/locale/zh-CN';
import cashShop from '@/views/game/cashShop/locale/zh-CN';
import npcShop from '@/views/game/npcShop/locale/zh-CN';
import gachapon from '@/views/game/gachapon/locale/zh-CN';
import commandInfo from '@/views/game/commandInfo/locale/zh-CN';
import workplace from '@/views/dashboard/workplace/locale/zh-CN';
import informationSearch from '@/views/dashboard/informationSearch/locale/zh-CN';
import account from '@/views/account/locale/zh-CN';
import login from '@/views/login/locale/zh-CN';
import inventory from '@/views/game/inventory/locale/zh-CN';
import monsterInvasion from '@/views/game/monsterInvasion/locale/zh-CN';
import familyLocale from '@/views/family/locale/zh-CN';
import townConfig from '@/views/townConfig/locale/zh-CN';
import catchUpExpConfig from '@/views/catchUpExpConfig/locale/zh-CN';
import scriptManager from '@/views/game/scriptManager/locale/zh-CN';
import cardCollection from '@/views/game/cardCollection/locale/zh-CN';
import equipEnhance from '@/views/game/equipEnhance/locale/zh-CN';
import xyCollection from '@/views/game/xyCollection/locale/zh-CN';
import equipAdvance from '@/views/game/equipAdvance/locale/zh-CN';
import baseConfig from './zh-CN/base';

export default {
  // 左侧菜单
  'menu.dashboard': '仪表盘',
  'menu.dashboard.workplace': '工作台',
  'menu.dashboard.informationSearch': '资料查询',
  'menu.game': '游戏管理',
  'menu.game.config': '参数管理',
  'menu.game.npcShop': 'NPC商店',
  'menu.game.cashShop': '商城管理',
  'menu.game.drop': '怪物爆率',
  'menu.game.drop.global': '全局爆率',
  'menu.game.inventory': '背包管理',
  'menu.game.gachapon': '百宝箱',
  'menu.game.command': 'GM指令',
  'menu.game.file': '文件管理',
  'menu.game.monsterInvasion': '怪物攻城',
  'menu.game.townConfig': '城镇管理',
  'menu.game.catchUpExp': '追赶机制',
  'menu.game.cardCollection': '卡片收集',
  'menu.game.equipEnhance': '装备强化',
  'menu.game.xyCollection': 'XY收集',
  'menu.game.equipAdvance': '装备进阶',
  'menu.boss': 'BOSS管理',
  'menu.boss.config': 'BOSS配置',
  'menu.family': '家族管理',
  'menu.family.guild': '公会管理',
  'menu.family.family': '师徒家族',
  'menu.family.alliance': '联盟管理',
  'menu.family.marriage': '婚姻管理',
  'menu.account': '玩家管理',
  'menu.account.list': '账户列表',
  'menu.account.player': '玩家管理',
  'menu.arco': 'UI 开发文档',
  'menu.beiDou': '关于北斗',
  // 提示信息
  'message.success': '操作成功',
  'message.switch.success': '切换为中文',
  'message.login.success': '欢迎使用',
  'message.logout.success': '登出成功',
  // 设置
  'settings.language': '语言',
  'settings.switch.toDark': '点击切换为暗黑模式',
  'settings.switch.toLight': '点击切换为明亮模式',
  'settings.screen.toFull': '点击切换全屏模式',
  'settings.screen.toExit': '点击退出全屏模式',
  'settings.userCenter': '用户中心',
  'settings.userSettings': '用户设置',
  'settings.logout': '退出登录',
  'settings.version': '版本',
  ...baseConfig, // 基本配置（示范如何外部导入）
  ...workplace,
  ...login,
  ...account,
  ...npcShop,
  ...cashShop,
  ...drop,
  ...gachapon,
  ...commandInfo,
  ...informationSearch,
  ...inventory,
  ...config,
  ...bossConfig,
  ...eliteBoss,
  ...monsterInvasion,
  ...familyLocale,
  ...townConfig,
  ...catchUpExpConfig,
  ...scriptManager,
  ...cardCollection,
  ...equipEnhance,
  ...xyCollection,
  ...equipAdvance,
};
