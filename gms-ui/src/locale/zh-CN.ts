import config from '@/views/game/config/locale/zh-CN';
import drop from '@/views/game/drop/locale/zh-CN';
import cashShop from '@/views/game/cashShop/locale/zh-CN';
import npcShop from '@/views/game/npcShop/locale/zh-CN';
import gachapon from '@/views/game/gachapon/locale/zh-CN';
import commandInfo from '@/views/game/commandInfo/locale/zh-CN';
import workplace from '@/views/dashboard/workplace/locale/zh-CN';
import informationSearch from '@/views/dashboard/informationSearch/locale/zh-CN';
import account from '@/views/account/locale/zh-CN';
import login from '@/views/login/locale/zh-CN';
import inventory from '@/views/game/inventory/locale/zh-CN';
import autoban from '@/views/game/autoban/locale/zh-CN';
import equipEnhance from '@/views/game/equipEnhance/locale/zh-CN';
import equipDamageBonus from '@/views/game/equipDamageBonus/locale/zh-CN';
import setDamageBonus from '@/views/game/setDamageBonus/locale/zh-CN';
import alchemyRecipe from '@/views/game/alchemyRecipe/locale/zh-CN';
import forgeRecipe from '@/views/game/forgeRecipe/locale/zh-CN';
import dailyActiveTask from '@/views/game/dailyActiveTask/locale/zh-CN';
import equipAdvance from '@/views/game/equipAdvance/locale/zh-CN';
import levelReward from '@/views/game/levelReward/locale/zh-CN';
import newbieGift from '@/views/game/newbieGift/locale/zh-CN';
import dailyDungeon from '@/views/game/dailyDungeon/locale/zh-CN';
import dailyBoss from '@/views/game/dailyBoss/locale/zh-CN';
import guild from '@/views/game/guild/locale/zh-CN';
import alliance from '@/views/game/alliance/locale/zh-CN';
import toyCollection from '@/views/game/toyCollection/locale/zh-CN';
import cdk from '@/views/game/cdk/locale/zh-CN';
import warehouse from '@/views/game/warehouse/locale/zh-CN';
import scrollDecompose from '@/views/game/scrollDecompose/locale/zh-CN';
import sponsor from '@/views/game/sponsor/locale/zh-CN';
import paohuan from '@/views/game/paohuan/locale/zh-CN';
import dailyExplore from '@/views/game/dailyExplore/locale/zh-CN';
import mentor from '@/views/game/mentor/locale/zh-CN';
import independentDrop from '@/views/customGameplay/independentDrop/locale/zh-CN';
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
  'menu.game.command': 'GM指令',
  'menu.game.file': '文件管理',
  'menu.game.autoban': '自动封禁',
  'menu.customGameplay': '定制玩法',
  'menu.customGameplay.overview': '玩法概览',
  'menu.customGameplay.independentDrop': '独立掉落',
  'menu.customGameplay.paohuan': '跑环管理',
  'menu.customGameplay.dailyActiveTask': '每日活跃任务管理',
  'menu.account': '玩家管理',
  'menu.account.list': '账户列表',
  'menu.account.player': '玩家管理',
  'menu.arco': 'UI 开发文档',
  'menu.growth': '成长管理',
  'menu.growth.newbieGift': '新手礼包',
  'menu.growth.levelReward': '等级奖励',
  'menu.growth.dailyDungeon': '每日副本',
  'menu.growth.dailyBoss': '每日Boss',
  'menu.growth.dailyExplore': '每日探索',
  'menu.growth.mentor': '师徒系统',
  'menu.growth.toyCollection': '玩具收集',
  'menu.growth.guild': '家族管理',
  'menu.growth.alliance': '联盟管理',
  'menu.equipment': '装备系统',
  'menu.equipment.equipEnhance': '装备强化',
  'menu.equipment.equipDamageBonus': '装备伤害加成',
  'menu.equipment.setDamageBonus': '套装伤害加成',
  'menu.equipment.equipAdvance': '装备进阶',
  'menu.equipment.alchemyRecipe': '炼金配方管理',
  'menu.equipment.forgeRecipe': '打造配方管理',
  'menu.materials': '材料系统',
  'menu.materials.warehouse': '仓库管理',
  'menu.materials.scrollDecompose': '卷轴分解',
  'menu.membership': '会员充值',
  'menu.membership.cdk': 'CDK兑换',
  'menu.membership.sponsor': '赞助配置',
  'menu.logMonitor': '日志监控',
  'menu.logMonitor.cdk': '兑换日志',
  'menu.logMonitor.sponsor': '赞助日志',
  'menu.lottery': '抽奖系统',
  'menu.lottery.gachapon': '百宝箱',
  // 定制玩法
  'customGameplay.placeholder': '定制玩法功能即将上线，敬请期待...',
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
  ...autoban,
  ...equipEnhance,
  ...equipDamageBonus,
  ...setDamageBonus,
  ...alchemyRecipe,
  ...forgeRecipe,
  ...dailyActiveTask,
  ...equipAdvance,
  ...levelReward,
  ...newbieGift,
  ...dailyDungeon,
  ...dailyBoss,
  ...guild,
  ...alliance,
  ...toyCollection,
  ...cdk,
  ...sponsor,
  ...warehouse,
  ...scrollDecompose,
  ...paohuan,
  ...dailyExplore,
  ...mentor,
  ...independentDrop,
};
