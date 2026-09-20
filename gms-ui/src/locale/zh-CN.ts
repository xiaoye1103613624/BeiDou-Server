import config from '@/views/game/config/locale/zh-CN';
import drop from '@/views/game/drop/locale/zh-CN';
import cashShop from '@/views/game/cashShop/locale/zh-CN';
import windowCashShop from '@/views/game/windowCashShop/locale/zh-CN';
import npcShop from '@/views/game/npcShop/locale/zh-CN';
import gachapon from '@/views/game/gachapon/locale/zh-CN';
import weather from '@/views/game/weather/locale/zh-CN';
import commandInfo from '@/views/game/commandInfo/locale/zh-CN';
import workplace from '@/views/dashboard/workplace/locale/zh-CN';
import informationSearch from '@/views/dashboard/informationSearch/locale/zh-CN';
import account from '@/views/account/locale/zh-CN';
import login from '@/views/login/locale/zh-CN';
import inventory from '@/views/game/inventory/locale/zh-CN';
import autoban from '@/views/game/autoban/locale/zh-CN';
import setItem from '@/views/game/setItem/locale/zh-CN';
import dailyCheckin from '@/views/game/dailyCheckin/locale/zh-CN';
import activity from '@/views/game/activity/locale/zh-CN';
import ranking from '@/views/game/ranking/locale/zh-CN';
import petGrowth from '@/views/game/petGrowth/locale/zh-CN';
import alchemyRecipe from '@/views/game/alchemyRecipe/locale/zh-CN';
import alchemistRecipe from '@/views/game/alchemistRecipe/locale/zh-CN';
import forgeRecipe from '@/views/game/forgeRecipe/locale/zh-CN';
import alchemyTier from '@/views/game/alchemyTier/locale/zh-CN';
import sysMenu from '@/views/game/sysMenu/locale/zh-CN';
import sysRole from '@/views/game/sysRole/locale/zh-CN';
import sidebarTool from '@/views/game/sidebarTool/locale/zh-CN';
import file from '@/views/game/file/locale/zh-CN';
import clientPath from '@/views/client/path/locale/zh-CN';
import clientWindowCashShopSync from '@/views/client/windowCashShopSync/locale/zh-CN';
import clientAssetHub from '@/views/client/assetHub/locale/zh-CN';
import clientSkillResources from '@/views/client/skillResources/locale/zh-CN';
import clientChairPose from '@/views/client/chairPose/locale/zh-CN';
import clientDoll from '@/views/client/doll/locale/zh-CN';
import quest from '@/views/game/quest/locale/zh-CN';
import baseConfig from './zh-CN/base';

export default {
  // 左侧菜单
  'menu.dashboard': '仪表盘',
  'menu.dashboard.workplace': '工作台',
  'menu.dashboard.informationSearch': '资料查询',
  'menu.system': '系统管理',
  'menu.game': '游戏管理',
  'menu.activity': '活动',
  'menu.member': '玩家',
  'menu.enhance': '强化',
  'menu.gameplay': '玩法',
  'menu.client': '客户端',
  'menu.client.path': '客户端路径',
  'menu.client.windowCashShopSync': '新商城客户端同步',
  'menu.client.assetHub': '游戏资源中心',
  'menu.client.skillResources': '技能资源',
  'menu.client.chairPose': '座椅设置',
  'menu.client.doll': '人偶 / 外观预览',
  // 旧分组 key 保留，避免历史 DB / 书签残留报缺文案
  'menu.daily': '系统管理',
  'menu.growth': '强化',
  'menu.game.config': '参数管理',
  'menu.game.npcShop': 'NPC商店',
  'menu.game.cashShop': '商城管理',
  'menu.game.windowCashShop': '新商城数据',
  'menu.game.sysMenu': '菜单管理',
  'menu.game.sysRole': '角色权限',
  'menu.game.drop': '怪物爆率',
  'menu.game.drop.global': '全局爆率',
  'menu.game.quest': '任务管理',
  'menu.game.inventory': '背包管理',
  'menu.game.gachapon': '百宝箱',
  'menu.game.weather': '天气切换',
  'menu.game.command': 'GM指令',
  'menu.game.file': '文件管理',
  'menu.game.autoban': '自动封禁',
  'menu.game.setItem': '套装管理',
  'menu.game.dailyCheckin': '每日签到',
  'menu.game.activity': '活动管理',
  'menu.game.petGrowth': '宠物成长进阶',
  'menu.game.alchemyRecipe': '炼金配方管理',
  'menu.game.alchemistRecipe': '炼药配方管理',
  'menu.game.forgeRecipe': '打造配方管理',
  'menu.game.alchemyTier': '炼金品级管理',
  'menu.game.sidebarTool': '右边栏菜单',
  'menu.account': '玩家',
  'menu.account.list': '账户列表',
  'menu.account.player': '角色管理',
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
  'settings.themeColor': '主题色',
  'settings.themeColor.group.recommended': '推荐',
  'settings.themeColor.group.more': '更多',
  'settings.themeColor.slateBlue': '灰蓝',
  'settings.themeColor.teal': '青绿',
  'settings.themeColor.indigo': '靛蓝',
  'settings.themeColor.mauve': '紫灰',
  'settings.themeColor.steel': '深灰蓝',
  'settings.themeColor.ocean': '海洋青',
  'settings.themeColor.forest': '森林绿',
  'settings.themeColor.amber': '琥珀',
  'settings.themeColor.rose': '玫瑰灰',
  'settings.themeColor.charcoal': '炭灰',
  'settings.screen.toFull': '点击切换全屏模式',
  'settings.screen.toExit': '点击退出全屏模式',
  'settings.userCenter': '用户中心',
  'settings.userSettings': '用户设置',
  'settings.logout': '退出登录',
  'settings.version': '版本',
  'settings.title': '页面配置',
  'settings.close': '关闭',
  'settings.copy': '复制配置',
  'settings.copy.success': '复制成功，请粘贴到 src/config/settings.json 文件中',
  'settings.content': '内容区域',
  'settings.other': '其他设置',
  'settings.alert':
    '布局相关配置会自动保存在本机浏览器。若要作为项目默认值，可点击「复制配置」并替换 settings.json。',
  'settings.navbar': '导航栏',
  'settings.menu': '菜单栏',
  'settings.topMenu': '顶部菜单',
  'settings.layout.toTop': '切换为顶部导航（含下拉子菜单）',
  'settings.layout.toSide': '切换为左侧导航',
  'settings.footer': '底部',
  'settings.tabBar': '多页签',
  'settings.menuFromServer': '菜单来自服务端',
  'settings.menuWidth': '菜单宽度 (px)',
  'settings.colorWeak': '色弱模式',
  'notFound.subtitle': '页面不存在',
  'notFound.back': '返回工作台',
  ...baseConfig, // 基本配置（示范如何外部导入）
  ...workplace,
  ...login,
  ...account,
  ...npcShop,
  ...cashShop,
  ...windowCashShop,
  ...drop,
  ...gachapon,
  ...weather,
  ...commandInfo,
  ...informationSearch,
  ...inventory,
  ...config,
  ...autoban,
  ...setItem,
  ...dailyCheckin,
  ...activity,
  ...ranking,
  ...petGrowth,
  ...alchemyRecipe,
  ...alchemistRecipe,
  ...forgeRecipe,
  ...alchemyTier,
  ...sysMenu,
  ...sysRole,
  ...sidebarTool,
  ...file,
  ...clientPath,
  ...clientWindowCashShopSync,
  ...clientAssetHub,
  ...clientSkillResources,
  ...clientChairPose,
  ...clientDoll,
  ...quest,
};
