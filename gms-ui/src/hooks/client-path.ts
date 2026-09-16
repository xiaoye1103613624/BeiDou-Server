import { computed, ref } from 'vue';
import { Message } from '@arco-design/web-vue';
import { useI18n } from 'vue-i18n';
import {
  ClientDataPathInfo,
  DirectoryEntry,
  getClientDataPath,
  listDirectories,
  setClientDataPath,
  validateClientDataPath,
} from '@/api/clientPath';

/**
 * Shared client Data path state + browse helpers.
 * Used by Client Path / Client Assets pages (and any feature needing the shared path).
 */
export default function useClientPath() {
  const { t } = useI18n();

  const clientPath = ref('');
  const pathInfo = ref<ClientDataPathInfo | null>(null);
  const validating = ref(false);
  const savingPath = ref(false);

  const browseVisible = ref(false);
  const browsePath = ref('');
  const browseDirs = ref<DirectoryEntry[]>([]);
  const browseLoading = ref(false);

  const pathAlertType = computed(() => {
    if (!pathInfo.value) return 'info';
    if (!pathInfo.value.ok) return 'error';
    if (pathInfo.value.warning) return 'warning';
    if (pathInfo.value.skipped) return 'info';
    return 'success';
  });

  const pathStatusText = computed(() => {
    if (!pathInfo.value) return '';
    const parts = [
      pathInfo.value.message,
      pathInfo.value.resolved ? `resolved=${pathInfo.value.resolved}` : '',
      pathInfo.value.configured
        ? `configured=${pathInfo.value.configured}`
        : '',
    ].filter(Boolean);
    return parts.join(' · ');
  });

  const loadPathInfo = async () => {
    const { data } = await getClientDataPath();
    pathInfo.value = data;
    const raw = data.configured || data.resolved || '';
    // strip " (JVM -D...)" suffix for editable input when JVM overrides
    clientPath.value = raw.replace(/\s*\(JVM -D[^)]+\)\s*$/, '').trim();
  };

  const validatePath = async () => {
    validating.value = true;
    try {
      const { data } = await validateClientDataPath(clientPath.value || '');
      Message.info(`${t('clientPath.msg.validated')}: ${data.message || ''}`);
    } finally {
      validating.value = false;
    }
  };

  const savePath = async (): Promise<ClientDataPathInfo | null> => {
    savingPath.value = true;
    try {
      const { data } = await setClientDataPath(clientPath.value || '');
      pathInfo.value = data;
      Message.success(t('clientPath.msg.saved'));
      return data;
    } finally {
      savingPath.value = false;
    }
  };

  const clearPath = async () => {
    clientPath.value = '';
    savingPath.value = true;
    try {
      const { data } = await setClientDataPath(' ');
      pathInfo.value = data;
      Message.success(t('clientPath.msg.saved'));
    } finally {
      savingPath.value = false;
    }
  };

  const loadBrowseDirs = async () => {
    if (!browsePath.value?.trim()) return;
    browseLoading.value = true;
    try {
      const { data } = await listDirectories(browsePath.value.trim());
      browseDirs.value = data || [];
    } catch {
      browseDirs.value = [];
    } finally {
      browseLoading.value = false;
    }
  };

  const openBrowse = (fallback = '') => {
    browsePath.value = clientPath.value || fallback || '';
    browseVisible.value = true;
    loadBrowseDirs();
  };

  const enterDir = (path: string) => {
    browsePath.value = path;
    loadBrowseDirs();
  };

  const browseGoParent = () => {
    const p = browsePath.value.replace(/[\\/]+$/, '');
    const idx = Math.max(p.lastIndexOf('\\'), p.lastIndexOf('/'));
    if (idx > 2) {
      browsePath.value = p.slice(0, idx);
      loadBrowseDirs();
    }
  };

  const useBrowsePath = () => {
    clientPath.value = browsePath.value;
    browseVisible.value = false;
  };

  return {
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
  };
}
