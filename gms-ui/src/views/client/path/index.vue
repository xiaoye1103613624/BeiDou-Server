<template>
  <PageContainer
    :title="$t('menu.client.path')"
    :description="$t('clientPath.page.desc')"
  >
    <ProCard :title="$t('clientPath.section.path')">
      <ClientPathConfig />
    </ProCard>

    <ProCard soft :title="$t('clientPath.section.related')" class="mt-card">
      <a-space direction="vertical" fill>
        <div>{{ $t('clientPath.related.hint') }}</div>
        <a-space wrap>
          <a-button type="outline" @click="goAssetHub">
            {{ $t('menu.client.assetHub') }}
          </a-button>
          <a-button type="outline" @click="goSkillResources">
            {{ $t('menu.client.skillResources') }}
          </a-button>
          <a-button type="outline" @click="goClientSync">
            {{ $t('menu.client.windowCashShopSync') }}
          </a-button>
          <a-button type="text" @click="goWindowCashShop">
            {{ $t('menu.game.windowCashShop') }}
          </a-button>
        </a-space>
      </a-space>
    </ProCard>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { useRouter } from 'vue-router';
  import ClientPathConfig from '@/components/client-path-config/index.vue';

  const router = useRouter();

  const goClientSync = () => {
    router.push({ name: 'ClientWindowCashShopSync' });
  };

  const goAssetHub = () => {
    router.push({ name: 'ClientAssetHub' });
  };

  const goSkillResources = () => {
    router.push({ name: 'ClientSkillResources' });
  };

  const goWindowCashShop = () => {
    router.push({ name: 'windowCashShop' });
  };
</script>

<style lang="less" scoped>
  .mt-card {
    margin-top: 12px;
  }
</style>
