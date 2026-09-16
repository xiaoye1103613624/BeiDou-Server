<template>
  <div class="navbar">
    <div class="left-side">
      <a-space :size="12">
        <div class="brand">
          <img
            class="logo"
            alt="logo"
            src="/src/assets/logo.png"
            width="36"
            height="36"
          />
          <div class="brand-copy">
            <a-typography-title class="brand-title" :heading="6">
              {{ $t('title') }}
            </a-typography-title>
            <span class="brand-sub">Admin</span>
          </div>
        </div>
        <icon-menu-fold
          v-if="!topMenu && appStore.device === 'mobile'"
          class="menu-trigger"
          @click="toggleDrawerMenu"
        />
      </a-space>
    </div>
    <div class="center-side">
      <TopMenu v-if="topMenu" />
    </div>
    <ul class="right-side">
      <li>
        <a-tag class="version-tag" size="small">
          {{ $t('settings.version') }} {{ version }}
        </a-tag>
      </li>
      <li>
        <a-tooltip :content="$t('settings.language')">
          <a-button
            class="nav-btn"
            type="text"
            shape="circle"
            @click="setDropDownVisible"
          >
            <template #icon>
              <icon-language />
            </template>
          </a-button>
        </a-tooltip>
        <a-dropdown trigger="click" @select="changeLocale as any">
          <div ref="triggerBtn" class="trigger-btn"></div>
          <template #content>
            <a-doption
              v-for="item in locales"
              :key="item.value"
              :value="item.value"
            >
              <template #icon>
                <icon-check v-show="item.value === currentLocale" />
              </template>
              {{ item.label }}
            </a-doption>
          </template>
        </a-dropdown>
      </li>
      <li>
        <a-tooltip :content="$t('settings.themeColor')">
          <a-dropdown trigger="click" @select="handleSelectThemeColor as any">
            <a-button class="nav-btn" type="text" shape="circle">
              <template #icon>
                <icon-palette />
              </template>
            </a-button>
            <template #content>
              <a-dgroup :title="$t('settings.themeColor.group.recommended')">
                <a-doption
                  v-for="preset in recommendedPresets"
                  :key="preset.id"
                  :value="preset.color"
                >
                  <a-space>
                    <span
                      class="theme-swatch"
                      :style="{ background: preset.color }"
                    />
                    <span>{{ $t(`settings.themeColor.${preset.id}`) }}</span>
                    <icon-check v-show="themeColor === preset.color" />
                  </a-space>
                </a-doption>
              </a-dgroup>
              <a-dgroup :title="$t('settings.themeColor.group.more')">
                <a-doption
                  v-for="preset in morePresets"
                  :key="preset.id"
                  :value="preset.color"
                >
                  <a-space>
                    <span
                      class="theme-swatch"
                      :style="{ background: preset.color }"
                    />
                    <span>{{ $t(`settings.themeColor.${preset.id}`) }}</span>
                    <icon-check v-show="themeColor === preset.color" />
                  </a-space>
                </a-doption>
              </a-dgroup>
            </template>
          </a-dropdown>
        </a-tooltip>
      </li>
      <li>
        <a-tooltip
          :content="
            theme === 'light'
              ? $t('settings.switch.toDark')
              : $t('settings.switch.toLight')
          "
        >
          <a-button
            class="nav-btn"
            type="text"
            shape="circle"
            @click="handleToggleTheme"
          >
            <template #icon>
              <icon-moon-fill v-if="theme === 'dark'" />
              <icon-sun-fill v-else />
            </template>
          </a-button>
        </a-tooltip>
      </li>
      <li>
        <a-tooltip
          :content="
            isFullscreen
              ? $t('settings.screen.toExit')
              : $t('settings.screen.toFull')
          "
        >
          <a-button
            class="nav-btn"
            type="text"
            shape="circle"
            @click="toggleFullScreen"
          >
            <template #icon>
              <icon-fullscreen-exit v-if="isFullscreen" />
              <icon-fullscreen v-else />
            </template>
          </a-button>
        </a-tooltip>
      </li>
      <li>
        <a-tooltip
          :content="
            appStore.topMenu
              ? $t('settings.layout.toSide')
              : $t('settings.layout.toTop')
          "
        >
          <a-button
            class="nav-btn"
            type="text"
            shape="circle"
            @click="handleToggleNavLayout"
          >
            <template #icon>
              <icon-menu v-if="appStore.topMenu" />
              <icon-apps v-else />
            </template>
          </a-button>
        </a-tooltip>
      </li>
      <li>
        <a-tooltip :content="$t('settings.title')">
          <a-button
            class="nav-btn"
            type="text"
            shape="circle"
            @click="handleOpenSettings"
          >
            <template #icon>
              <icon-settings />
            </template>
          </a-button>
        </a-tooltip>
      </li>
      <li>
        <a-dropdown trigger="click">
          <div class="user-entry">
            <a-avatar :size="32" :src="avatar" />
            <span class="user-name">{{ displayName }}</span>
          </div>
          <template #content>
            <a-doption>
              <a-space @click="handleLogout">
                <icon-export />
                <span>{{ $t('settings.logout') }}</span>
              </a-space>
            </a-doption>
          </template>
        </a-dropdown>
      </li>
    </ul>
  </div>
</template>

<script lang="ts" setup>
  import { computed, inject, ref } from 'vue';
  import { useDark, useToggle, useFullscreen } from '@vueuse/core';
  import { useAppStore, useUserStore } from '@/store';
  import useUser from '@/hooks/user';
  import TopMenu from '@/components/navbar/top-menu.vue';
  import useLocale from '@/hooks/locale';
  import { LOCALE_OPTIONS } from '@/locale';
  import { getVersion } from '@/api/dashboard';
  import useLoading from '@/hooks/loading';
  import { getThemePacksByGroup } from '@/utils/theme';

  const { changeLocale, currentLocale } = useLocale();
  const locales = [...LOCALE_OPTIONS];
  const recommendedPresets = getThemePacksByGroup('recommended');
  const morePresets = getThemePacksByGroup('more');
  const appStore = useAppStore();
  const userStore = useUserStore();
  const { logout } = useUser();
  const { isFullscreen, toggle: toggleFullScreen } = useFullscreen();
  const avatar = computed(() => userStore.avatar);
  const displayName = computed(
    () => userStore.name || userStore.nick || 'Admin'
  );
  const theme = computed(() => appStore.theme);
  const themeColor = computed(() => appStore.themeColor);
  const topMenu = computed(() => appStore.topMenu && appStore.menu);
  const isDark = useDark({
    selector: 'body',
    attribute: 'arco-theme',
    valueDark: 'dark',
    valueLight: 'light',
    storageKey: 'arco-theme',
    onChanged(dark: boolean) {
      appStore.toggleTheme(dark);
    },
  });
  const toggleTheme = useToggle(isDark);
  const handleToggleTheme = () => {
    toggleTheme();
  };
  const handleSelectThemeColor = (color: string) => {
    appStore.setThemeColor(color);
  };
  const handleLogout = () => {
    logout();
  };
  const handleToggleNavLayout = () => {
    appStore.toggleNavLayout();
  };
  const handleOpenSettings = () => {
    appStore.updateSettings({ globalSettings: true });
  };
  const toggleDrawerMenu = inject('toggleDrawerMenu') as () => void;

  const triggerBtn = ref();
  const setDropDownVisible = () => {
    const event = new MouseEvent('click', {
      view: window,
      bubbles: true,
      cancelable: true,
    });
    triggerBtn.value.dispatchEvent(event);
  };

  const version = ref<string>('');
  const { setLoading } = useLoading(false);
  const loadVersion = async () => {
    setLoading(true);
    try {
      const { data } = await getVersion();
      version.value = data;
    } finally {
      setLoading(false);
    }
  };
  loadVersion();
</script>

<style scoped lang="less">
  .navbar {
    display: flex;
    justify-content: space-between;
    height: 100%;
    padding: 0 8px 0 0;
    background: var(--bg-card);
    border-bottom: 1px solid var(--border);
  }

  .left-side {
    display: flex;
    align-items: center;
    padding-left: 16px;
  }

  .brand {
    display: inline-flex;
    align-items: center;
    gap: 10px;
  }

  .brand-copy {
    display: flex;
    flex-direction: column;
    line-height: 1.15;
  }

  .brand-title {
    margin: 0 !important;
    color: var(--text-primary) !important;
    font-size: 16px !important;
    font-weight: 700 !important;
    font-family: var(--bd-font-display);
    letter-spacing: -0.02em;
  }

  .brand-sub {
    color: var(--text-secondary);
    font-size: 11px;
    letter-spacing: 0.08em;
    text-transform: uppercase;
  }

  .menu-trigger {
    font-size: 22px;
    cursor: pointer;
  }

  .center-side {
    flex: 1;
    min-width: 0;
    overflow: hidden;
    align-self: stretch;
  }

  .right-side {
    display: flex;
    padding-right: 12px;
    list-style: none;

    li {
      display: flex;
      align-items: center;
      padding: 0 6px;
    }

    .nav-btn {
      color: var(--text-secondary);
      font-size: 16px;

      &:hover {
        color: var(--primary);
        background: var(--primary-light);
      }
    }

    .theme-swatch {
      display: inline-block;
      width: 14px;
      height: 14px;
      border-radius: 4px;
      border: 1px solid var(--border-strong);
    }

    .version-tag {
      color: var(--primary);
      background: var(--primary-light);
      border: none;
      border-radius: 999px;
    }

    .user-entry {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 4px 8px 4px 4px;
      border-radius: 999px;
      cursor: pointer;
      transition: background 0.2s ease;

      &:hover {
        background: var(--primary-light);
      }
    }

    .user-name {
      max-width: 96px;
      overflow: hidden;
      color: var(--text-primary);
      font-size: 13px;
      font-weight: 500;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .trigger-btn {
      position: absolute;
      bottom: 14px;
      margin-left: 14px;
    }
  }

  body[arco-theme='dark'] .navbar {
    background: var(--bg-card);
    border-bottom-color: var(--border);
  }

  body[arco-theme='dark'] .brand-title,
  body[arco-theme='dark'] .user-name {
    color: var(--text-primary) !important;
  }

  body[arco-theme='dark'] .brand-sub,
  body[arco-theme='dark'] .nav-btn {
    color: var(--text-secondary);
  }
</style>

<style lang="less">
  .logo {
    filter: none;
  }

  [arco-theme='dark'] .logo {
    filter: invert(100%);
  }
</style>
