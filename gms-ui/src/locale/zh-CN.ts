import config from '@/views/game/config/locale/zh-CN';
import drop from '@/views/game/drop/locale/zh-CN';
import cashShop from '@/views/game/cashShop/locale/zh-CN';
import windowCashShop from '@/views/game/windowCashShop/locale/zh-CN';
import npcShop from '@/views/game/npcShop/locale/zh-CN';
import gachapon from '@/views/game/gachapon/locale/zh-CN';
import commandInfo from '@/views/game/commandInfo/locale/zh-CN';
import workplace from '@/views/dashboard/workplace/locale/zh-CN';
import informationSearch from '@/views/dashboard/informationSearch/locale/zh-CN';
import account from '@/views/account/locale/zh-CN';
import login from '@/views/login/locale/zh-CN';
import inventory from '@/views/game/inventory/locale/zh-CN';
import autoban from '@/views/game/autoban/locale/zh-CN';
import setItem from '@/views/game/setItem/locale/zh-CN';
import equipGrowth from '@/views/game/equipGrowth/locale/zh-CN';
import enhanceRule from '@/views/game/enhanceRule/locale/zh-CN';
import carryItemStat from '@/views/game/carryItemStat/locale/zh-CN';
import alchemyRecipe from '@/views/game/alchemyRecipe/locale/zh-CN';
import alchemistRecipe from '@/views/game/alchemistRecipe/locale/zh-CN';
import forgeRecipe from '@/views/game/forgeRecipe/locale/zh-CN';
import alchemyTier from '@/views/game/alchemyTier/locale/zh-CN';
import petGrowth from '@/views/game/petGrowth/locale/zh-CN';
import sponsor from '@/views/game/sponsor/locale/zh-CN';
import skillTech from '@/views/game/skillTech/locale/zh-CN';
import opLog from '@/views/game/opLog/locale/zh-CN';
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
  'menu.game.windowCashShop': '窗口商城',
  'menu.game.drop': '怪物爆率',
  'menu.game.drop.global': '全局爆率',
  'menu.game.inventory': '背包管理',
  'menu.game.gachapon': '百宝箱',
  'menu.game.command': 'GM指令',
  'menu.game.file': '文件管理',
  'menu.game.autoban': '自动封禁',
  'menu.game.setItem': '套装管理',
  'menu.game.equipGrowth': '装备成长',
  'menu.game.enhanceRule': '强化规则',
  'menu.game.carryItemStat': '携带物属性',
  'menu.game.alchemyRecipe': '炼金配方管理',
  'menu.game.alchemistRecipe': '炼药配方管理',
  'menu.game.forgeRecipe': '打造配方管理',
  'menu.game.alchemyTier': '炼金品级管理',
  'menu.game.petGrowth': '宠物成长进阶',
  'menu.game.sponsor': '赞助档位',
  'menu.game.skillTech': '技能技改',
  'menu.game.opLog': '操作日志',
  'menu.account': '玩家管理',
  'menu.account.list': '账户列表',
  'menu.account.player': '玩家管理',
  'menu.arco': 'UI 开发文档',
  'menu.beiDou': '关于北斗',
  // 提示信息
  'message.success': '操作成功',
  'message.error': '操作失败',
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
  ...windowCashShop,
  ...drop,
  ...gachapon,
  ...commandInfo,
  ...informationSearch,
  ...inventory,
  ...config,
  ...autoban,
  ...setItem,
  ...equipGrowth,
  ...enhanceRule,
  ...carryItemStat,
  ...alchemyRecipe,
  ...alchemistRecipe,
  ...forgeRecipe,
  ...alchemyTier,
  ...petGrowth,
  ...sponsor,
  ...skillTech,
  ...opLog,
};
