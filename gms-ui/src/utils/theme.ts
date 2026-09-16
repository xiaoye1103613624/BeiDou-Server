/** 将 hex 转为 RGB 三元组字符串，如 "59, 110, 165" */
export function hexToRgbTriplet(hex: string): string | null {
  const normalized = hex.replace('#', '').trim();
  if (!/^[0-9a-fA-F]{6}$/.test(normalized)) return null;
  const r = parseInt(normalized.slice(0, 2), 16);
  const g = parseInt(normalized.slice(2, 4), 16);
  const b = parseInt(normalized.slice(4, 6), 16);
  return `${r}, ${g}, ${b}`;
}

export type ThemePackId =
  | 'slateBlue'
  | 'teal'
  | 'indigo'
  | 'mauve'
  | 'steel'
  | 'ocean'
  | 'forest'
  | 'amber'
  | 'rose'
  | 'charcoal';

/** @deprecated 使用 ThemePackId */
export type PrimaryPresetId = ThemePackId;

export type ThemePackGroup = 'recommended' | 'more';

/** 命名主题包：主色阶 + 可选侧栏强调色 */
export interface ThemePack {
  id: ThemePackId;
  /** 浅色模式主色 */
  color: string;
  hover: string;
  active: string;
  light: string;
  /** 可选侧栏强调色 */
  sidebar?: string;
  group: ThemePackGroup;
}

/** @deprecated 使用 ThemePack */
export type PrimaryPreset = ThemePack;

/**
 * 品牌主题包预设（浅色模式基准色，饱和度偏克制，适合管理后台）
 * recommended = 原有 5 套；more = 扩展专业色板
 */
export const THEME_PACKS: ThemePack[] = [
  {
    id: 'slateBlue',
    color: '#3B6EA5',
    hover: '#5B8BC0',
    active: '#2A5580',
    light: '#E8F0F9',
    sidebar: '#3B6EA5',
    group: 'recommended',
  },
  {
    id: 'teal',
    color: '#3D7A6B',
    hover: '#4E8F7E',
    active: '#2F5F54',
    light: '#E8F3F0',
    sidebar: '#3D7A6B',
    group: 'recommended',
  },
  {
    id: 'indigo',
    color: '#5B6ABF',
    hover: '#7280CC',
    active: '#4A58A3',
    light: '#EDEFFA',
    sidebar: '#5B6ABF',
    group: 'recommended',
  },
  {
    id: 'mauve',
    color: '#8B5E83',
    hover: '#9E7396',
    active: '#6F4A68',
    light: '#F3EBF1',
    sidebar: '#8B5E83',
    group: 'recommended',
  },
  {
    id: 'steel',
    color: '#2C3E50',
    hover: '#3D5266',
    active: '#1F2D3A',
    light: '#E9ECF0',
    sidebar: '#2C3E50',
    group: 'recommended',
  },
  {
    id: 'ocean',
    color: '#0E7490',
    hover: '#148BA8',
    active: '#0A5C72',
    light: '#E6F5F8',
    sidebar: '#0E7490',
    group: 'more',
  },
  {
    id: 'forest',
    color: '#3F6F4E',
    hover: '#4F8760',
    active: '#2F543A',
    light: '#EAF2EC',
    sidebar: '#3F6F4E',
    group: 'more',
  },
  {
    id: 'amber',
    color: '#B45309',
    hover: '#C96A1F',
    active: '#8F4207',
    light: '#F8F0E6',
    sidebar: '#B45309',
    group: 'more',
  },
  {
    id: 'rose',
    color: '#A8556A',
    hover: '#B86B7D',
    active: '#8A4456',
    light: '#F6ECF0',
    sidebar: '#A8556A',
    group: 'more',
  },
  {
    id: 'charcoal',
    color: '#374151',
    hover: '#4B5563',
    active: '#1F2937',
    light: '#EEF0F3',
    sidebar: '#374151',
    group: 'more',
  },
];

/** 兼容旧引用名 */
export const PRIMARY_PRESETS = THEME_PACKS;

export function getThemePacksByGroup(group: ThemePackGroup): ThemePack[] {
  return THEME_PACKS.filter((p) => p.group === group);
}

export function getPresetById(id: ThemePackId): ThemePack {
  return THEME_PACKS.find((p) => p.id === id) || THEME_PACKS[0];
}

export function getPresetByColor(hex: string): ThemePack {
  const normalized = hex.toUpperCase();
  return (
    THEME_PACKS.find((p) => p.color.toUpperCase() === normalized) ||
    THEME_PACKS[0]
  );
}

/**
 * 基于主题包写入 CSS 变量（`--primary*` + Arco `--primary-*`）。
 * 深色模式下用 hover 作为行动色，保证对比度。
 */
export function applyThemeColor(hex: string) {
  const preset = getPresetByColor(hex);
  const isDark = document.body.getAttribute('arco-theme') === 'dark';
  const actionHex = isDark ? preset.hover : preset.color;
  const base = hexToRgbTriplet(actionHex);
  if (!base) return;

  const [r, g, b] = base.split(',').map((n) => Number(n.trim()));
  const mix = (t: number, toward: number) => Math.round(r + (toward - r) * t);

  // 深色色阶：浅端偏暗底，深端偏亮，便于暗色 UI 上的 hover/active
  const steps: Record<number, [number, number, number]> = isDark
    ? {
        1: [mix(0.88, 15), mix(0.88, 17), mix(0.88, 21)],
        2: [mix(0.72, 23), mix(0.72, 28), mix(0.72, 36)],
        3: [mix(0.5, 40), mix(0.5, 48), mix(0.5, 60)],
        4: [mix(0.28, 55), mix(0.28, 70), mix(0.28, 90)],
        5: [mix(0.12, 70), mix(0.12, 95), mix(0.12, 120)],
        6: [r, g, b],
        7: [mix(0.18, 255), mix(0.18, 255), mix(0.16, 255)],
        8: [mix(0.35, 255), mix(0.35, 255), mix(0.32, 255)],
        9: [mix(0.55, 255), mix(0.55, 255), mix(0.5, 255)],
        10: [mix(0.75, 255), mix(0.75, 255), mix(0.7, 255)],
      }
    : {
        1: [mix(0.92, 255), mix(0.92, 255), mix(0.9, 255)],
        2: [mix(0.78, 255), mix(0.78, 255), mix(0.74, 255)],
        3: [mix(0.58, 255), mix(0.58, 255), mix(0.52, 255)],
        4: [mix(0.35, 255), mix(0.35, 255), mix(0.3, 255)],
        5: [mix(0.15, 255), mix(0.15, 255), mix(0.12, 255)],
        6: [r, g, b],
        7: [mix(0.2, 0), mix(0.2, 0), mix(0.18, 0)],
        8: [mix(0.38, 0), mix(0.38, 0), mix(0.34, 0)],
        9: [mix(0.55, 0), mix(0.55, 0), mix(0.5, 0)],
        10: [mix(0.7, 0), mix(0.7, 0), mix(0.65, 0)],
      };

  const root = document.documentElement;
  root.style.setProperty('--primary', actionHex);
  root.style.setProperty(
    '--primary-hover',
    isDark ? preset.color : preset.hover
  );
  root.style.setProperty('--primary-active', preset.active);
  root.style.setProperty(
    '--primary-light',
    isDark ? `rgba(${hexToRgbTriplet(preset.color)}, 0.15)` : preset.light
  );

  const sidebarAccent = preset.sidebar || preset.color;
  const sidebarRgb = hexToRgbTriplet(sidebarAccent);
  root.style.setProperty('--bd-sidebar-accent', sidebarAccent);
  if (sidebarRgb) {
    root.style.setProperty(
      '--bd-sidebar-active',
      isDark ? `rgba(${sidebarRgb}, 0.18)` : preset.light
    );
  }

  document.body.style.setProperty('--bd-primary-rgb', base);
  Object.entries(steps).forEach(([level, rgb]) => {
    document.body.style.setProperty(
      `--primary-${level}`,
      `${rgb[0]}, ${rgb[1]}, ${rgb[2]}`
    );
  });
  document.body.style.setProperty('--link-6', `${r}, ${g}, ${b}`);
  document.body.style.setProperty(
    '--link-7',
    `${steps[7][0]}, ${steps[7][1]}, ${steps[7][2]}`
  );
}

export function initAppTheme(themeColor: string, isDark: boolean) {
  if (isDark) {
    document.body.setAttribute('arco-theme', 'dark');
    document.documentElement.setAttribute('data-theme', 'dark');
  } else {
    document.body.removeAttribute('arco-theme');
    document.documentElement.setAttribute('data-theme', 'light');
  }
  applyThemeColor(themeColor);
}
