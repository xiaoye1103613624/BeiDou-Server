<template>
  <PageContainer
    :title="$t('menu.client.windowCashShopSync')"
    :description="$t('clientWindowCashShopSync.page.desc')"
  >
    <ProCard soft :title="$t('clientWindowCashShopSync.section.path')">
      <a-alert type="info" class="mb-alert">
        {{ $t('clientWindowCashShopSync.path.hint') }}
      </a-alert>
      <ClientPathConfig :show-hint="false" @saved="onPathSaved">
        <template #actions>
          <a-button
            type="primary"
            :loading="syncingClient"
            @click="() => syncFromClientClick()"
          >
            {{ $t('windowCashShop.syncFromClient') }}
          </a-button>
        </template>
      </ClientPathConfig>
    </ProCard>

    <ProCard
      soft
      :title="$t('clientWindowCashShopSync.section.ops')"
      class="mt-card"
    >
      <a-space direction="vertical" fill>
        <div>{{ $t('clientWindowCashShopSync.ops.hint') }}</div>
        <a-space wrap>
          <a-button type="outline" @click="goServerAdmin">
            {{ $t('clientWindowCashShopSync.link.serverAdmin') }}
          </a-button>
          <a-button type="outline" @click="goAssetHub">
            {{ $t('clientWindowCashShopSync.link.assetHub') }}
          </a-button>
          <a-button type="text" @click="goClientPath">
            {{ $t('menu.client.path') }}
          </a-button>
        </a-space>
      </a-space>
    </ProCard>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { useRouter } from 'vue-router';
  import ClientPathConfig from '@/components/client-path-config/index.vue';
  import useWindowCashShopClientSync from '@/hooks/window-cash-shop-client-sync';

  const router = useRouter();
  const { syncingClient, syncFromClientClick, onPathSaved } =
    useWindowCashShopClientSync();

  const goServerAdmin = () => {
    router.push({ name: 'windowCashShop' });
  };

  const goAssetHub = () => {
    router.push({ name: 'ClientAssetHub' });
  };

  const goClientPath = () => {
    router.push({ name: 'ClientPath' });
  };
</script>

<style lang="less" scoped>
  .mb-alert {
    margin-bottom: 12px;
  }

  .mt-card {
    margin-top: 12px;
  }
</style>
