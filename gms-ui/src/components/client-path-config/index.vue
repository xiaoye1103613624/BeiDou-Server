<template>
  <div class="client-path-config">
    <a-space wrap class="client-path-config__toolbar">
      <span class="client-path-config__label">{{
        $t('clientPath.label')
      }}</span>
      <a-input
        v-model="clientPath"
        :placeholder="$t('clientPath.placeholder')"
        style="width: 360px"
        allow-clear
      />
      <a-button @click="() => openBrowse(browseFallback)">
        {{ $t('clientPath.browse') }}
      </a-button>
      <a-button :loading="validating" @click="validatePath">
        {{ $t('clientPath.validate') }}
      </a-button>
      <a-button type="primary" :loading="savingPath" @click="onSave">
        {{ $t('clientPath.save') }}
      </a-button>
      <a-button @click="clearPath">{{ $t('clientPath.clear') }}</a-button>
      <slot name="actions" />
    </a-space>

    <a-alert
      v-if="pathInfo"
      class="client-path-config__status"
      :type="pathAlertType"
      :title="$t('clientPath.status')"
    >
      {{ pathStatusText }}
    </a-alert>
    <a-alert v-if="showHint" type="info" class="client-path-config__hint">
      {{ $t('clientPath.hint') }}
    </a-alert>

    <a-modal
      v-model:visible="browseVisible"
      :title="$t('clientPath.browse.title')"
      :width="560"
      unmount-on-close
    >
      <a-space direction="vertical" fill style="width: 100%">
        <a-input-search
          v-model="browsePath"
          :placeholder="$t('clientPath.browse.current')"
          search-button
          @search="loadBrowseDirs"
        />
        <a-space>
          <a-button size="small" @click="browseGoParent">
            {{ $t('clientPath.browse.parent') }}
          </a-button>
          <a-button size="small" type="outline" @click="useBrowsePath">
            {{ $t('clientPath.browse.use') }}
          </a-button>
        </a-space>
        <a-spin :loading="browseLoading" style="width: 100%">
          <a-list
            v-if="browseDirs.length"
            size="small"
            :bordered="true"
            style="max-height: 320px; overflow: auto"
          >
            <a-list-item
              v-for="d in browseDirs"
              :key="d.path"
              class="client-path-config__dir"
              @click="enterDir(d.path)"
            >
              {{ d.name }}
            </a-list-item>
          </a-list>
          <a-empty v-else :description="$t('clientPath.browse.empty')" />
        </a-spin>
      </a-space>
      <template #footer>
        <a-space>
          <a-button @click="browseVisible = false">
            {{ $t('button.cancel') }}
          </a-button>
          <a-button type="primary" @click="useBrowsePath">
            {{ $t('clientPath.browse.use') }}
          </a-button>
        </a-space>
      </template>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { onMounted, toRef } from 'vue';
  import type { ClientDataPathInfo } from '@/api/clientPath';
  import useClientPath from '@/hooks/client-path';

  const props = withDefaults(
    defineProps<{
      /** When true, load path on mount. */
      autoLoad?: boolean;
      showHint?: boolean;
      browseFallback?: string;
    }>(),
    {
      autoLoad: true,
      showHint: true,
      browseFallback: '',
    }
  );

  const emit = defineEmits<{
    (e: 'saved', info: ClientDataPathInfo | null): void;
    (e: 'loaded', info: ClientDataPathInfo | null): void;
  }>();

  const pathApi = useClientPath();
  const {
    clientPath,
    pathInfo,
    validating,
    savingPath,
    browseVisible,
    browsePath,
    browseDirs,
    browseLoading,
    pathAlertType,
    pathStatusText,
    loadPathInfo,
    validatePath,
    savePath,
    clearPath,
    openBrowse,
    loadBrowseDirs,
    enterDir,
    browseGoParent,
    useBrowsePath,
  } = pathApi;

  const onSave = async () => {
    const data = await savePath();
    emit('saved', data);
  };

  onMounted(async () => {
    if (props.autoLoad) {
      await loadPathInfo();
      emit('loaded', pathInfo.value);
    }
  });

  defineExpose({
    clientPath: toRef(pathApi, 'clientPath'),
    pathInfo: toRef(pathApi, 'pathInfo'),
    loadPathInfo,
    savePath,
    clearPath,
  });
</script>

<style lang="less" scoped>
  .client-path-config {
    display: flex;
    flex-direction: column;
    gap: 12px;

    &__label {
      color: var(--color-text-2);
      font-size: 13px;
    }

    &__dir {
      cursor: pointer;

      &:hover {
        background: var(--color-fill-2);
      }
    }
  }
</style>
