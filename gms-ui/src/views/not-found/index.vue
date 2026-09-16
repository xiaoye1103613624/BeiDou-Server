<template>
  <div class="bd-not-found">
    <a-result
      class="bd-not-found__result"
      status="404"
      :subtitle="$t('notFound.subtitle')"
    />
    <div class="bd-not-found__actions">
      <a-button type="primary" @click="back">
        {{ $t('notFound.back') }}
      </a-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const back = () => {
    router.push({ name: 'Workplace' });
  };
</script>

<style scoped lang="less">
  .bd-not-found {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: calc(100vh - var(--bd-navbar-height) - 80px);
    padding: 40px 20px;
    text-align: center;
  }

  .bd-not-found__actions {
    margin-top: 8px;
  }

  :deep(.arco-result-subtitle) {
    color: var(--bd-ink-soft);
  }
</style>
