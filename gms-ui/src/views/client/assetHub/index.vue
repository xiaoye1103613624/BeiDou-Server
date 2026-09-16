<template>
  <PageContainer
    :title="$t('menu.client.assetHub')"
    :description="$t('clientAssetHub.page.desc')"
  >
    <ProCard soft>
      <a-alert type="info" class="mb-alert">
        {{ $t('clientAssetHub.hint') }}
      </a-alert>
      <a-descriptions
        v-if="info"
        :column="1"
        size="small"
        class="mb-alert"
        bordered
      >
        <a-descriptions-item :label="$t('clientAssetHub.info.root')">
          {{ info.root || '—' }}
        </a-descriptions-item>
        <a-descriptions-item :label="$t('clientAssetHub.info.clientPath')">
          {{
            info.clientDataConfigured
              ? info.clientDataPath || '—'
              : $t('clientAssetHub.info.clientPath.empty')
          }}
        </a-descriptions-item>
        <a-descriptions-item :label="$t('clientAssetHub.info.providers')">
          <a-space wrap>
            <a-tag
              v-for="p in info.providers || []"
              :key="p.name"
              :color="p.enabled ? 'green' : 'gray'"
            >
              {{ p.name
              }}{{ p.enabled ? '' : ` (${$t('clientAssetHub.off')})` }}
            </a-tag>
          </a-space>
        </a-descriptions-item>
      </a-descriptions>
      <a-space wrap class="bd-page-toolbar">
        <a-button type="outline" @click="goClientPath">
          {{ $t('menu.client.path') }}
        </a-button>
        <a-button type="text" :loading="infoLoading" @click="reloadInfo">
          {{ $t('clientAssetHub.refreshInfo') }}
        </a-button>
      </a-space>
    </ProCard>

    <ProCard :title="$t('clientAssetHub.section.ensure')" class="mt-card">
      <a-form :model="form" layout="inline" class="bd-page-toolbar">
        <a-form-item :label="$t('clientAssetHub.form.category')">
          <a-select v-model="form.category" style="width: 140px">
            <a-option v-for="c in categories" :key="c" :value="c" :label="c" />
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('clientAssetHub.form.idFrom')">
          <a-input-number v-model="form.idFrom" :min="0" hide-button />
        </a-form-item>
        <a-form-item :label="$t('clientAssetHub.form.idTo')">
          <a-input-number v-model="form.idTo" :min="0" hide-button />
        </a-form-item>
        <a-form-item>
          <a-checkbox v-model="form.fromCatalog">
            {{ $t('clientAssetHub.form.fromCatalog') }}
          </a-checkbox>
        </a-form-item>
      </a-form>
      <a-space wrap class="bd-page-toolbar">
        <a-button
          type="primary"
          :loading="ensuringMissing"
          @click="runEnsure(false)"
        >
          {{ $t('clientAssetHub.ensureMissing') }}
        </a-button>
        <a-button :loading="ensuringForce" @click="runEnsure(true)">
          {{ $t('clientAssetHub.ensureForce') }}
        </a-button>
      </a-space>
      <a-alert v-if="lastSummary" type="success" class="bd-page-toolbar">
        {{ lastSummary }}
      </a-alert>
    </ProCard>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { onMounted, reactive, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import {
    ensureAssets,
    getAssetInfo,
    type AssetInfoResult,
  } from '@/api/asset';

  const { t } = useI18n();
  const router = useRouter();

  const categories = ['item', 'mob', 'npc', 'skill', 'map', 'quest'];
  const info = ref<AssetInfoResult | null>(null);
  const infoLoading = ref(false);
  const ensuringMissing = ref(false);
  const ensuringForce = ref(false);
  const lastSummary = ref('');

  const form = reactive({
    category: 'item',
    idFrom: undefined as number | undefined,
    idTo: undefined as number | undefined,
    fromCatalog: true,
  });

  const reloadInfo = async () => {
    infoLoading.value = true;
    try {
      const res = await getAssetInfo();
      info.value = res.data as unknown as AssetInfoResult;
    } catch {
      info.value = null;
    } finally {
      infoLoading.value = false;
    }
  };

  const goClientPath = () => {
    router.push({ name: 'ClientPath' });
  };

  const runEnsure = (force: boolean) => {
    const hasRange =
      form.idFrom != null &&
      form.idFrom > 0 &&
      form.idTo != null &&
      form.idTo > 0;
    if (!hasRange && !form.fromCatalog) {
      Message.warning(t('clientAssetHub.msg.needTarget'));
      return;
    }
    Modal.confirm({
      title: force
        ? t('clientAssetHub.ensureForce.confirm')
        : t('clientAssetHub.ensureMissing.confirm'),
      content: t('clientAssetHub.ensure.hint'),
      onOk: async () => {
        const loadingRef = force ? ensuringForce : ensuringMissing;
        loadingRef.value = true;
        lastSummary.value = '';
        try {
          const res = await ensureAssets({
            category: form.category,
            idFrom: hasRange ? form.idFrom : undefined,
            idTo: hasRange ? form.idTo : undefined,
            fromCatalog: !hasRange && form.fromCatalog,
            force,
          });
          const data = res.data as unknown as {
            requested: number;
            cached: number;
            skipped: number;
            failed: number;
            durationMs: number;
          };
          lastSummary.value = t('clientAssetHub.summary', {
            requested: data.requested ?? 0,
            cached: data.cached ?? 0,
            skipped: data.skipped ?? 0,
            failed: data.failed ?? 0,
            seconds: Math.round((data.durationMs ?? 0) / 1000),
          });
          Message.success(t('clientAssetHub.msg.done'));
          await reloadInfo();
        } catch {
          Message.error(t('clientAssetHub.msg.fail'));
        } finally {
          loadingRef.value = false;
        }
      },
    });
  };

  onMounted(() => {
    reloadInfo();
  });
</script>

<style lang="less" scoped>
  .mb-alert {
    margin-bottom: 12px;
  }

  .mt-card {
    margin-top: 12px;
  }
</style>
