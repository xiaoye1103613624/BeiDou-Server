import config from '@/views/game/config/locale/en-US';
import drop from '@/views/game/drop/locale/en-US';
import cashShop from '@/views/game/cashShop/locale/en-US';
import windowCashShop from '@/views/game/windowCashShop/locale/en-US';
import npcShop from '@/views/game/npcShop/locale/en-US';
import gachapon from '@/views/game/gachapon/locale/en-US';
import weather from '@/views/game/weather/locale/en-US';
import commandInfo from '@/views/game/commandInfo/locale/en-US';
import workplace from '@/views/dashboard/workplace/locale/en-US';
import informationSearch from '@/views/dashboard/informationSearch/locale/en-US';
import account from '@/views/account/locale/en-US';
import login from '@/views/login/locale/en-US';
import inventory from '@/views/game/inventory/locale/en-US';
import autoban from '@/views/game/autoban/locale/en-US';
import setItem from '@/views/game/setItem/locale/en-US';
import dailyCheckin from '@/views/game/dailyCheckin/locale/en-US';
import activity from '@/views/game/activity/locale/en-US';
import ranking from '@/views/game/ranking/locale/en-US';
import petGrowth from '@/views/game/petGrowth/locale/en-US';
import alchemyRecipe from '@/views/game/alchemyRecipe/locale/en-US';
import alchemistRecipe from '@/views/game/alchemistRecipe/locale/en-US';
import forgeRecipe from '@/views/game/forgeRecipe/locale/en-US';
import alchemyTier from '@/views/game/alchemyTier/locale/en-US';
import sysMenu from '@/views/game/sysMenu/locale/en-US';
import sysRole from '@/views/game/sysRole/locale/en-US';
import sidebarTool from '@/views/game/sidebarTool/locale/en-US';
import file from '@/views/game/file/locale/en-US';
import clientPath from '@/views/client/path/locale/en-US';
import clientWindowCashShopSync from '@/views/client/windowCashShopSync/locale/en-US';
import clientAssetHub from '@/views/client/assetHub/locale/en-US';
import clientSkillResources from '@/views/client/skillResources/locale/en-US';
import quest from '@/views/game/quest/locale/en-US';
import base from './en-US/base';

export default {
  'menu.dashboard': 'Dashboard',
  'menu.dashboard.workplace': 'Workplace',
  'menu.dashboard.informationSearch': 'Information Search',
  'menu.system': 'System',
  'menu.game': 'Game',
  'menu.activity': 'Activities',
  'menu.member': 'Players',
  'menu.enhance': 'Enhancement',
  'menu.gameplay': 'Gameplay',
  'menu.client': 'Client',
  'menu.client.path': 'Client Path',
  'menu.client.windowCashShopSync': 'New Cash Shop Client Sync',
  'menu.client.assetHub': 'Game Asset Hub',
  'menu.client.skillResources': 'Skill Resources',
  // Legacy group keys for residual DB / bookmarks
  'menu.daily': 'System',
  'menu.growth': 'Enhancement',
  'menu.game.config': 'Config',
  'menu.game.npcShop': 'NPC Shop',
  'menu.game.cashShop': 'Cash Shop',
  'menu.game.windowCashShop': 'New Cash Shop Data',
  'menu.game.sysMenu': 'Menu Management',
  'menu.game.sysRole': 'Role Permissions',
  'menu.game.drop': 'Mob Drop',
  'menu.game.drop.global': 'Global Drop',
  'menu.game.quest': 'Quest Management',
  'menu.game.inventory': 'Inventory',
  'menu.game.gachapon': 'Gachapon',
  'menu.game.weather': 'Weather',
  'menu.game.command': 'GM Commands',
  'menu.game.file': 'File Management',
  'menu.game.autoban': 'Autoban',
  'menu.game.setItem': 'Set Item',
  'menu.game.dailyCheckin': 'Daily Check-In',
  'menu.game.activity': 'Activity Management',
  'menu.game.petGrowth': 'Pet Growth',
  'menu.game.alchemyRecipe': 'Alchemy Recipe',
  'menu.game.alchemistRecipe': 'Alchemist Recipe',
  'menu.game.forgeRecipe': 'Forge Recipe',
  'menu.game.alchemyTier': 'Alchemy Tier',
  'menu.game.sidebarTool': 'Side Toolbar',
  'menu.account': 'Players',
  'menu.account.list': 'Account List',
  'menu.account.player': 'Characters',
  'menu.arco': 'UI Doc',
  'menu.beiDou': 'About BeiDou',
  'message.success': 'Success',
  'message.switch.success': 'Switch to English',
  'message.login.success': 'Welcome',
  'message.logout.success': 'Logout success',
  'settings.language': 'Language',
  'settings.switch.toDark': 'Click to use dark mode',
  'settings.switch.toLight': 'Click to use light mode',
  'settings.themeColor': 'Theme color',
  'settings.themeColor.group.recommended': 'Recommended',
  'settings.themeColor.group.more': 'More',
  'settings.themeColor.slateBlue': 'Slate blue',
  'settings.themeColor.teal': 'Teal',
  'settings.themeColor.indigo': 'Indigo',
  'settings.themeColor.mauve': 'Mauve',
  'settings.themeColor.steel': 'Steel',
  'settings.themeColor.ocean': 'Ocean',
  'settings.themeColor.forest': 'Forest',
  'settings.themeColor.amber': 'Amber',
  'settings.themeColor.rose': 'Rose',
  'settings.themeColor.charcoal': 'Charcoal',
  'settings.screen.toFull': 'Click to switch to full screen mode',
  'settings.screen.toExit': 'Click to exit the full screen mode',
  'settings.userCenter': 'User Center',
  'settings.userSettings': 'User Settings',
  'settings.logout': 'Logout',
  'settings.version': 'Version',
  'settings.title': 'Page Settings',
  'settings.close': 'Close',
  'settings.copy': 'Copy Settings',
  'settings.copy.success':
    'Copied. Paste into src/config/settings.json to persist.',
  'settings.content': 'Content Area',
  'settings.other': 'Other Settings',
  'settings.alert':
    'Layout settings are saved in this browser automatically. To set project defaults, click "Copy Settings" and replace settings.json.',
  'settings.navbar': 'Navbar',
  'settings.menu': 'Menu',
  'settings.topMenu': 'Top Menu',
  'settings.layout.toTop': 'Switch to top navigation (with dropdowns)',
  'settings.layout.toSide': 'Switch to side navigation',
  'settings.footer': 'Footer',
  'settings.tabBar': 'Tab Bar',
  'settings.menuFromServer': 'Menu From Server',
  'settings.menuWidth': 'Menu Width (px)',
  'settings.colorWeak': 'Color Weak Mode',
  'notFound.subtitle': 'Page not found',
  'notFound.back': 'Back to Workplace',
  ...base,
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
  ...quest,
};
