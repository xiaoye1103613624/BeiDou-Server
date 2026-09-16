<template>
  <a-card
    class="bd-pro-card"
    :class="{ 'bd-pro-card--bordered': bordered, 'bd-pro-card--soft': soft }"
    :bordered="false"
    :title="title"
  >
    <template v-if="$slots.title" #title>
      <slot name="title" />
    </template>
    <template v-if="$slots.extra" #extra>
      <slot name="extra" />
    </template>
    <slot />
  </a-card>
</template>

<script lang="ts" setup>
  withDefaults(
    defineProps<{
      title?: string;
      bordered?: boolean;
      soft?: boolean;
    }>(),
    {
      title: undefined,
      bordered: false,
      soft: false,
    }
  );
</script>

<style lang="less" scoped>
  .bd-pro-card {
    overflow: hidden;
    background: var(--bd-surface-elevated);
    border-radius: var(--bd-radius-lg);
    box-shadow: var(--bd-shadow-sm);

    :deep(.arco-card-header) {
      height: auto;
      min-height: 52px;
      padding: 16px 20px;
      border-bottom: 1px solid var(--bd-border);
    }

    :deep(.arco-card-header-title) {
      color: var(--bd-ink);
      font-weight: 600;
      font-size: 15px;
      font-family: var(--bd-font-display);
    }

    :deep(.arco-card-body) {
      padding: 20px;
    }

    &--bordered {
      border: 1px solid var(--bd-border);
      box-shadow: none;
    }

    &--soft {
      background: var(--bd-surface-muted);
      box-shadow: none;
    }
  }

  body[arco-theme='dark'] {
    .bd-pro-card {
      background: var(--color-bg-2);

      :deep(.arco-card-header) {
        border-bottom-color: var(--color-border);
      }

      :deep(.arco-card-header-title) {
        color: var(--color-text-1);
      }

      &--soft {
        background: var(--color-fill-2);
      }
    }
  }
</style>
