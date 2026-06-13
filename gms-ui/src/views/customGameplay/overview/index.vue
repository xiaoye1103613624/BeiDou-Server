<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.customGameplay.overview')">
      <a-empty :description="$t('customGameplay.placeholder')" />
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import Breadcrumb from '@/components/breadcrumb/index.vue';
</script>

<style lang="less" scoped>
  .container {
    padding: 0 20px 20px 20px;
  }
</style>
