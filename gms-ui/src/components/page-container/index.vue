<template>
  <div class="bd-page">
    <div v-if="showHeader" class="bd-page__header">
      <div class="bd-page__header-main">
        <Breadcrumb v-if="breadcrumb" />
        <div v-if="title || $slots.title" class="bd-page__title-row">
          <h1 class="bd-page__title">
            <slot name="title">{{ title }}</slot>
          </h1>
          <p v-if="description || $slots.description" class="bd-page__desc">
            <slot name="description">{{ description }}</slot>
          </p>
        </div>
      </div>
      <div v-if="$slots.extra" class="bd-page__extra">
        <slot name="extra" />
      </div>
    </div>
    <div class="bd-page__body" :class="{ 'bd-page__body--flush': flush }">
      <slot />
    </div>
  </div>
</template>

<script lang="ts" setup>
  withDefaults(
    defineProps<{
      title?: string;
      description?: string;
      breadcrumb?: boolean;
      showHeader?: boolean;
      flush?: boolean;
    }>(),
    {
      title: '',
      description: '',
      breadcrumb: true,
      showHeader: true,
      flush: false,
    }
  );
</script>

<style lang="less" scoped>
  .bd-page {
    display: flex;
    flex-direction: column;
    gap: var(--bd-content-gap);
    min-height: 100%;
    padding: 16px 20px 24px;

    &__header {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 16px;
    }

    &__title-row {
      margin-top: 4px;
    }

    &__title {
      margin: 0;
      color: var(--bd-ink);
      font-weight: 600;
      font-size: 20px;
      font-family: var(--bd-font-display);
      line-height: 1.35;
      letter-spacing: -0.02em;
    }

    &__desc {
      margin: 6px 0 0;
      color: var(--bd-ink-soft);
      font-size: 13px;
      line-height: 1.5;
    }

    &__extra {
      display: flex;
      flex-shrink: 0;
      align-items: center;
      gap: 8px;
    }

    &__body {
      flex: 1;

      &--flush {
        margin: 0 -4px;
      }
    }
  }

  body[arco-theme='dark'] {
    .bd-page__title {
      color: var(--color-text-1);
    }

    .bd-page__desc {
      color: var(--color-text-3);
    }
  }
</style>
