import { ref } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import { useI18n } from 'vue-i18n';
import type { ClientDataPathInfo } from '@/api/clientPath';
import {
  syncFromClientData,
  type ClientSyncResult,
} from '@/api/windowCashShop';

/**
 * Shared client-Data sync for window cash shop (reads ClientDataPath via server).
 */
export default function useWindowCashShopClientSync(options?: {
  onDone?: (data: ClientSyncResult | undefined) => void | Promise<void>;
}) {
  const { t } = useI18n();
  const syncingClient = ref(false);

  const formatSummary = (data: ClientSyncResult | undefined) => {
    const summary = t('windowCashShop.syncSummary', {
      scanned: data?.scanned ?? 0,
      categoriesCreated: data?.categoriesCreated ?? 0,
      categoriesUpdated: data?.categoriesUpdated ?? 0,
      categoriesPruned: data?.categoriesPruned ?? 0,
      linksMigrated: data?.linksMigrated ?? 0,
      itemsUpserted: data?.itemsUpserted ?? 0,
      linksUpserted: data?.linksUpserted ?? 0,
      catalogSize: data?.catalogSize ?? 0,
      iconsFilled: data?.iconsFilled ?? 0,
      skipped: data?.skipped ?? 0,
    });
    const secs =
      data?.durationMs != null
        ? t('windowCashShop.syncSummary.duration', {
            seconds: (data.durationMs / 1000).toFixed(1),
          })
        : '';
    const hint = data?.emptyReason ? ` — ${data.emptyReason}` : '';
    return `${summary}${secs}${hint}`;
  };

  const runSync = async () => {
    syncingClient.value = true;
    const loadingMsg = Message.loading({
      content: t('windowCashShop.syncFromClient.loading'),
      duration: 0,
    });
    try {
      // fillIcons=true 仅绑本地已有 PNG，不会 CDN；图标补全请用「同步图标」
      const { data } = await syncFromClientData({
        fillIcons: true,
        cashOnly: true,
      });
      Message.success({
        content: `${t(
          'windowCashShop.msg.syncFromClientDone'
        )}: ${formatSummary(data)}`,
        duration: 12_000,
      });
      await options?.onDone?.(data);
    } catch (e: unknown) {
      let raw = '';
      if (e instanceof Error) {
        raw = e.message;
      } else if (typeof e === 'string') {
        raw = e;
      }
      const isTimeout =
        /timeout/i.test(raw) || /exceeded/i.test(raw) || raw === 'ECONNABORTED';
      if (isTimeout) {
        Message.error({
          content: t('windowCashShop.msg.syncFromClientTimeout'),
          duration: 10_000,
        });
      }
    } finally {
      loadingMsg.close();
      syncingClient.value = false;
    }
  };

  const syncFromClientClick = (skipConfirm = false) => {
    if (skipConfirm) {
      return runSync();
    }
    Modal.confirm({
      title: t('windowCashShop.syncFromClient.confirm'),
      content: t('windowCashShop.syncFromClient.confirm'),
      onOk: runSync,
    });
    return undefined;
  };

  const onPathSaved = (data: ClientDataPathInfo | null) => {
    if (data?.ok && data.resolved) {
      Modal.confirm({
        title: t('windowCashShop.syncFromClient.offer'),
        content: t('windowCashShop.syncFromClient.offer'),
        onOk: () => syncFromClientClick(true),
      });
    }
  };

  return {
    syncingClient,
    syncFromClientClick,
    onPathSaved,
  };
}
