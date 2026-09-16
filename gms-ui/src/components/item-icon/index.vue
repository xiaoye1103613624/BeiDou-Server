<template>
  <img
    v-if="resolvedSrc"
    class="bd-game-icon"
    :class="imgClass"
    :src="resolvedSrc"
    :alt="altText"
    :width="size"
    :height="size"
    :data-category="normalizedCategory"
    :data-icon-id="String(numericId)"
    :data-item-id="
      normalizedCategory === 'item' ? String(numericId) : undefined
    "
    :style="imgStyle"
    @error="handleError"
    @load="handleLoad"
  />
</template>

<script lang="ts" setup>
  import { computed, ref, watch } from 'vue';
  import {
    getCdnIconUrl,
    getIconUrl,
    isCdnIconUrl,
    normalizeIconCategory,
    onItemIconLoad,
    scheduleIconCache,
  } from '@/utils/mapleStoryAPI';

  const props = withDefaults(
    defineProps<{
      category?: string;
      id?: number | string | null;
      /** Optional persisted non-CDN URL; prefer category+id → /game-assets */
      persistedUrl?: string | null;
      size?: number;
      alt?: string;
      imgClass?:
        | string
        | Record<string, boolean>
        | Array<string | Record<string, boolean>>;
      /** 加载后是否请求服务端缓存 CDN 图 */
      autoCache?: boolean;
      /** 优先走 CDN（本地 game-assets 未就绪时预览更稳） */
      preferCdn?: boolean;
    }>(),
    {
      category: 'item',
      id: 0,
      persistedUrl: null,
      size: 32,
      alt: '',
      imgClass: undefined,
      autoCache: true,
      preferCdn: false,
    }
  );

  const normalizedCategory = computed(() =>
    normalizeIconCategory(props.category || 'item')
  );
  const numericId = computed(() => Number(props.id) || 0);
  const altText = computed(
    () => props.alt || (numericId.value ? String(numericId.value) : '')
  );
  const imgStyle = computed(() => ({
    width: `${props.size}px`,
    height: `${props.size}px`,
    objectFit: 'contain' as const,
    imageRendering: 'pixelated' as const,
    verticalAlign: 'middle',
  }));

  const resolvedSrc = ref('');
  const usedFallback = ref(false);

  const resetSrc = () => {
    usedFallback.value = false;
    if (!numericId.value) {
      resolvedSrc.value = '';
      return;
    }
    if (props.preferCdn) {
      usedFallback.value = true;
      resolvedSrc.value = getCdnIconUrl(
        normalizedCategory.value,
        numericId.value
      );
      return;
    }
    resolvedSrc.value = getIconUrl(
      normalizedCategory.value,
      numericId.value,
      props.persistedUrl
    );
  };

  watch(
    () =>
      [props.category, props.id, props.persistedUrl, props.preferCdn] as const,
    () => resetSrc(),
    { immediate: true }
  );

  const handleError = () => {
    if (!numericId.value) {
      resolvedSrc.value = '';
      return;
    }
    if (!usedFallback.value) {
      usedFallback.value = true;
      const cdn = getCdnIconUrl(normalizedCategory.value, numericId.value);
      if (cdn) {
        resolvedSrc.value = cdn;
        if (props.autoCache) {
          scheduleIconCache(normalizedCategory.value, numericId.value);
        }
        return;
      }
    }
    // CDN 优先失败时再试本地
    if (props.preferCdn) {
      const local = getIconUrl(
        normalizedCategory.value,
        numericId.value,
        props.persistedUrl
      );
      if (local && local !== resolvedSrc.value) {
        usedFallback.value = true;
        resolvedSrc.value = local;
        return;
      }
    }
    resolvedSrc.value = '';
  };

  const handleLoad = (event: Event) => {
    if (!props.autoCache) return;
    const img = event.target as HTMLImageElement | null;
    if (img && isCdnIconUrl(img.src)) {
      onItemIconLoad(event);
    }
  };
</script>

<style scoped>
  .bd-game-icon {
    display: inline-block;
    flex-shrink: 0;
  }
</style>
