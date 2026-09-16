<template>
  <PageContainer :title="$t('menu.game.file')">
    <ProCard>
      <div class="file-toolbar">
        <a-space wrap>
          <a-button size="small" @click="refreshTree">
            {{ $t('file.toolbar.refreshTree') }}
          </a-button>
          <template v-if="showScriptReload">
            <a-divider direction="vertical" />
            <a-badge :count="dirtyPaths.size" :dot="false" :max-count="99">
              <a-button
                size="small"
                type="primary"
                :disabled="dirtyPaths.size === 0"
                :loading="reloading"
                @click="reloadModified"
              >
                {{ $t('file.toolbar.reloadModified') }}
              </a-button>
            </a-badge>
            <a-button
              size="small"
              :loading="reloading"
              @click="runReload('all')"
            >
              {{ $t('file.toolbar.reloadAllScripts') }}
            </a-button>
            <a-dropdown trigger="click">
              <a-button size="small">
                {{ $t('file.toolbar.reloadEvents') }}
                <IconDown />
              </a-button>
              <template #content>
                <a-doption @click="runReload('events')">{{
                  $t('file.toolbar.reloadEvents')
                }}</a-doption>
                <a-doption @click="runReload('portals')">{{
                  $t('file.toolbar.reloadPortals')
                }}</a-doption>
                <a-doption @click="runReload('mapScripts')">{{
                  $t('file.toolbar.reloadMapScripts')
                }}</a-doption>
                <a-doption @click="runReload('quest')">{{
                  $t('file.toolbar.reloadQuestScripts')
                }}</a-doption>
                <a-doption @click="runReload('npc')">{{
                  $t('file.toolbar.reloadNpcScripts')
                }}</a-doption>
                <a-doption @click="runReload('reactor')">{{
                  $t('file.toolbar.reloadReactorScripts')
                }}</a-doption>
              </template>
            </a-dropdown>
          </template>
        </a-space>
        <a-typography-text
          v-if="showScriptReload"
          type="secondary"
          class="file-hint"
        >
          {{ $t('file.toolbar.scriptHint') }}
        </a-typography-text>
      </div>

      <a-layout class="file-layout">
        <a-layout-sider class="file-sider">
          <a-tree
            ref="treeRef"
            theme="dark"
            size="mini"
            :block-node="true"
            :data="treeData"
            :draggable="true"
            :allow-drop="allowDrop"
            :load-more="onTreeSelectDirectory"
            :virtual-list-props="{ buffer: 100 }"
            @select="onTreeSelectFile"
            @drop="onTreeDrop"
          >
            <template #switcher-icon>
              <IconDown />
            </template>
            <template #title="nodeData">
              <div
                class="tree-title"
                @contextmenu.prevent="openContextMenu($event, nodeData)"
              >
                <span>{{ nodeData.title }}</span>
                <a-tag
                  v-if="nodeData.path && dirtyPaths.has(nodeData.path)"
                  size="small"
                  color="orangered"
                  class="dirty-tag"
                >
                  {{ $t('file.dirty.badge') }}
                </a-tag>
              </div>
            </template>
          </a-tree>
        </a-layout-sider>
        <a-layout-content>
          <vue-monaco-editor
            v-model:value="editorContent"
            :language="editorLanguage"
            default-language="javascript"
            theme="vs-dark"
            :options="editorOptions"
            @mount="onEditorMount"
            @change="onEditorTextChange"
          />
        </a-layout-content>
      </a-layout>
    </ProCard>

    <div
      v-if="ctx.visible"
      class="ctx-menu"
      :style="{ left: `${ctx.x}px`, top: `${ctx.y}px` }"
      @click.stop
    >
      <div
        v-if="ctx.canCreate"
        class="ctx-item"
        @click="onCtxAction('newFile')"
      >
        {{ $t('file.ctx.newFile') }}
      </div>
      <div
        v-if="ctx.canCreate"
        class="ctx-item"
        @click="onCtxAction('newFolder')"
      >
        {{ $t('file.ctx.newFolder') }}
      </div>
      <div v-if="ctx.canMutate" class="ctx-item" @click="onCtxAction('rename')">
        {{ $t('file.ctx.rename') }}
      </div>
      <div v-if="ctx.canMutate" class="ctx-item" @click="onCtxAction('copy')">
        {{ $t('file.ctx.copy') }}
      </div>
      <div
        v-if="ctx.canMutate"
        class="ctx-item danger"
        @click="onCtxAction('delete')"
      >
        {{ $t('file.ctx.delete') }}
      </div>
      <div
        v-if="ctx.canReloadScript"
        class="ctx-item"
        @click="onCtxAction('reload')"
      >
        {{ $t('file.ctx.reloadThis') }}
      </div>
      <div
        v-if="!ctx.node?.isLeaf"
        class="ctx-item"
        @click="onCtxAction('refresh')"
      >
        {{ $t('file.ctx.refresh') }}
      </div>
    </div>

    <a-modal
      v-model:visible="nameModal.visible"
      :title="nameModalTitle"
      :ok-text="$t('file.modal.ok')"
      :cancel-text="$t('file.modal.cancel')"
      unmount-on-close
      @ok="submitNameModal"
    >
      <a-form :model="nameModal" layout="vertical">
        <a-form-item :label="$t('file.modal.nameLabel')" required>
          <a-input
            v-model="nameModal.name"
            :placeholder="$t('file.modal.namePlaceholder')"
            allow-clear
            @press-enter="submitNameModal"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </PageContainer>
</template>

<script lang="ts" setup>
  import {
    computed,
    onMounted,
    onUnmounted,
    reactive,
    ref,
    shallowRef,
  } from 'vue';
  import {
    Editor,
    MonacoEditor,
    VueMonacoEditor,
  } from '@guolao/vue-monaco-editor';
  import {
    copyFileNode,
    createFileNode,
    deleteFileNode,
    moveFileNode,
    readFile,
    renameFileNode,
    treeFile,
    writeFile,
  } from '@/api/fileTree';
  import {
    reloadAllScriptsByGMCommand,
    reloadEventsByGMCommand,
    reloadMapScriptsByGMCommand,
    reloadNpcScriptsByGMCommand,
    reloadPortalsByGMCommand,
    reloadQuestScriptsByGMCommand,
    reloadReactorScriptsByGMCommand,
    reloadScriptsByPaths,
  } from '@/api/command';
  import { useDebounceFn } from '@vueuse/core';
  import type { TreeNodeData } from '@arco-design/web-vue/es/tree/interface';
  import { Message, Modal, Tree } from '@arco-design/web-vue';
  import { IconDown } from '@arco-design/web-vue/es/icon';
  import { useI18n } from 'vue-i18n';
  import localDts from './types/beidoums-scripts.d.ts.txt?raw';

  type FileNode = TreeNodeData & { path?: string };

  const { t } = useI18n();

  const treeData = ref<FileNode[]>([]);
  const treeRef = ref<InstanceType<typeof Tree>>();
  const treeEditingNode = ref<FileNode>();
  const selectedPath = ref('');
  const dirtyPaths = ref(new Set<string>());
  const reloading = ref(false);

  const editor = shallowRef<typeof Editor>();
  const editorContent = ref('');
  const editorCompletionProvider = shallowRef();
  const editorLanguage = ref('');
  const editorOptions = {
    automaticLayout: true,
    formatOnType: true,
    formatOnPaste: true,
  };

  const languageMap = {
    js: 'javascript',
    html: 'html',
    xml: 'xml',
    json: 'json',
    java: 'java',
    md: 'markdown',
    sh: 'shell',
    bat: 'bat',
    yml: 'yaml',
    yaml: 'yaml',
    properties: 'properties',
    sql: 'sql',
  };
  type LanguageMapKey = keyof typeof languageMap;

  const ctx = reactive<{
    visible: boolean;
    x: number;
    y: number;
    node: FileNode | null;
    canCreate: boolean;
    canMutate: boolean;
    canReloadScript: boolean;
  }>({
    visible: false,
    x: 0,
    y: 0,
    node: null,
    canCreate: false,
    canMutate: false,
    canReloadScript: false,
  });

  const nameModal = reactive<{
    visible: boolean;
    mode: 'newFile' | 'newFolder' | 'rename';
    name: string;
    parentKey: string;
    currentKey: string;
  }>({
    visible: false,
    mode: 'newFile',
    name: '',
    parentKey: '',
    currentKey: '',
  });

  const nameModalTitle = computed(() => {
    if (nameModal.mode === 'newFolder') return t('file.modal.newFolderTitle');
    if (nameModal.mode === 'rename') return t('file.modal.renameTitle');
    return t('file.modal.newFileTitle');
  });

  const showScriptReload = computed(() => {
    const p = selectedPath.value || treeEditingNode.value?.path || '';
    return isScriptsPath(p) || dirtyPaths.value.size > 0;
  });

  function isScriptsPath(path?: string) {
    if (!path) return false;
    return (
      path === 'scripts' ||
      path.startsWith('scripts/') ||
      path.startsWith('scripts-')
    );
  }

  function isWzPath(path?: string) {
    if (!path) return false;
    return path === 'wz' || path.startsWith('wz/') || path.startsWith('wz-');
  }

  function isProtectedRoot(path?: string) {
    if (!path || path.includes('/')) return false;
    return (
      path === 'scripts' ||
      path === 'wz' ||
      path.startsWith('scripts-') ||
      path.startsWith('wz-')
    );
  }

  function parentKeyOf(key: string) {
    const idx = key.lastIndexOf('-');
    return idx < 0 ? '' : key.slice(0, idx);
  }

  function closeContextMenu() {
    ctx.visible = false;
  }

  function openContextMenu(e: MouseEvent, nodeData: FileNode) {
    const path = nodeData.path || '';
    ctx.node = nodeData;
    ctx.x = e.clientX;
    ctx.y = e.clientY;
    // 文件夹下可新建；文件则在其父目录新建
    ctx.canCreate = !nodeData.isLeaf || !isProtectedRoot(path);
    ctx.canMutate = !isProtectedRoot(path);
    const leafJs =
      !!nodeData.isLeaf && !!path && path.toLowerCase().endsWith('.js');
    ctx.canReloadScript = leafJs && (isScriptsPath(path) || isWzPath(path));
    ctx.visible = true;
  }

  function onCtxAction(action: string) {
    const { node } = ctx;
    closeContextMenu();
    if (!node) return;
    switch (action) {
      case 'newFile':
        openNameModal('newFile', node);
        break;
      case 'newFolder':
        openNameModal('newFolder', node);
        break;
      case 'rename':
        openNameModal('rename', node);
        break;
      case 'copy':
        doCopy(node);
        break;
      case 'delete':
        doDelete(node);
        break;
      case 'reload':
        if (isWzPath(node.path)) {
          Message.warning(t('file.msg.wzNotSupported'));
        } else {
          reloadPaths([node.path || '']);
        }
        break;
      case 'refresh':
        refreshNode(node);
        break;
      default:
        break;
    }
  }

  function openNameModal(
    mode: 'newFile' | 'newFolder' | 'rename',
    node: FileNode
  ) {
    nameModal.mode = mode;
    nameModal.currentKey = String(node.key);
    if (mode === 'rename') {
      nameModal.parentKey = parentKeyOf(String(node.key));
      nameModal.name = node.title || '';
    } else {
      nameModal.parentKey = node.isLeaf
        ? parentKeyOf(String(node.key))
        : String(node.key);
      nameModal.name = mode === 'newFolder' ? 'new-folder' : 'new-script.js';
    }
    nameModal.visible = true;
  }

  function isValidName(name: string) {
    return (
      !!name && name !== '.' && name !== '..' && !/[\\/:*?"<>|\r\n]/.test(name)
    );
  }

  async function submitNameModal() {
    const name = nameModal.name.trim();
    if (!isValidName(name)) {
      Message.warning(t('file.msg.invalidName'));
      return;
    }
    try {
      if (nameModal.mode === 'rename') {
        await renameFileNode({
          currentKey: nameModal.currentKey,
          name,
        });
        Message.success(t('file.msg.renameOk'));
        if (treeEditingNode.value?.key === nameModal.currentKey) {
          treeEditingNode.value = undefined;
          editorContent.value = '';
        }
      } else {
        await createFileNode({
          currentKey: nameModal.parentKey,
          name,
          directory: nameModal.mode === 'newFolder',
        });
        Message.success(t('file.msg.createOk'));
      }
      nameModal.visible = false;
      await refreshTree();
    } catch (e) {
      console.error(e);
    }
  }

  async function doCopy(node: FileNode) {
    if (isProtectedRoot(node.path)) {
      Message.warning(t('file.msg.cannotModifyRoot'));
      return;
    }
    const base = node.title || 'copy';
    const dot = base.lastIndexOf('.');
    const copyName =
      dot > 0 ? `${base.slice(0, dot)}_copy${base.slice(dot)}` : `${base}_copy`;
    try {
      await copyFileNode({
        currentKey: String(node.key),
        targetParentKey: parentKeyOf(String(node.key)),
        name: copyName,
      });
      Message.success(t('file.msg.copyOk'));
      await refreshTree();
    } catch (e) {
      console.error(e);
    }
  }

  function doDelete(node: FileNode) {
    if (isProtectedRoot(node.path)) {
      Message.warning(t('file.msg.cannotModifyRoot'));
      return;
    }
    Modal.confirm({
      title: t('file.ctx.delete'),
      content: t('file.modal.deleteConfirm', { name: node.title }),
      okText: t('file.modal.ok'),
      cancelText: t('file.modal.cancel'),
      onOk: async () => {
        await deleteFileNode({ currentKey: String(node.key) });
        if (node.path) {
          const next = new Set(dirtyPaths.value);
          next.delete(node.path);
          dirtyPaths.value = next;
        }
        if (treeEditingNode.value?.key === node.key) {
          treeEditingNode.value = undefined;
          editorContent.value = '';
        }
        Message.success(t('file.msg.deleteOk'));
        await refreshTree();
      },
    });
  }

  function allowDrop(options: {
    dropNode: TreeNodeData;
    dropPosition: -1 | 0 | 1;
  }) {
    const drop = options.dropNode as FileNode;
    if (options.dropPosition === 0) {
      return !drop.isLeaf;
    }
    return !isProtectedRoot(drop.path);
  }

  async function onTreeDrop(data: {
    dragNode: TreeNodeData;
    dropNode: TreeNodeData;
    dropPosition: number;
  }) {
    const drag = data.dragNode as FileNode;
    const drop = data.dropNode as FileNode;
    if (isProtectedRoot(drag.path)) {
      Message.warning(t('file.msg.cannotModifyRoot'));
      return;
    }
    let targetParentKey = '';
    if (data.dropPosition === 0 && !drop.isLeaf) {
      targetParentKey = String(drop.key);
    } else {
      targetParentKey = parentKeyOf(String(drop.key));
    }
    if (String(drag.key) === targetParentKey) {
      Message.warning(t('file.msg.moveInvalid'));
      return;
    }
    // prevent move into own descendant
    if (String(targetParentKey).startsWith(`${String(drag.key)}-`)) {
      Message.warning(t('file.msg.moveInvalid'));
      return;
    }
    if (parentKeyOf(String(drag.key)) === targetParentKey) {
      return;
    }
    try {
      await moveFileNode({
        currentKey: String(drag.key),
        targetParentKey,
      });
      Message.success(t('file.msg.moveOk'));
      if (treeEditingNode.value?.key === drag.key) {
        treeEditingNode.value = undefined;
        editorContent.value = '';
      }
      await refreshTree();
    } catch (e) {
      console.error(e);
    }
  }

  onUnmounted(() => {
    if (editorCompletionProvider.value)
      editorCompletionProvider.value.dispose();
    if (editor.value) editor.value.dispose();
    window.removeEventListener('click', closeContextMenu);
  });

  onMounted(() => {
    window.addEventListener('click', closeContextMenu);
  });

  function onEditorMount(editorInstance: any, monacoInstance: MonacoEditor) {
    editor.value = editorInstance;
    registerCodeCompletion(monacoInstance);
  }

  async function registerCodeCompletion(monaco: MonacoEditor) {
    let usingDts = '';
    try {
      const response = await fetch(
        `https://cdn.jsdelivr.net/gh/shinobi9/beidoums-scripts-snippets/types/beidoums-scripts.d.ts`
      );
      usingDts = response.ok ? await response.text() : usingDts;
    } catch (e) {
      usingDts = localDts;
    }

    const tsLang = monaco.languages.typescript as unknown as {
      javascriptDefaults: {
        addExtraLib: (content: string, filePath?: string) => void;
        setCompilerOptions: (options: Record<string, unknown>) => void;
      };
      ScriptTarget: { ES6: number };
    };
    tsLang.javascriptDefaults.addExtraLib(usingDts, 'beidoums-scripts-dts');
    tsLang.javascriptDefaults.setCompilerOptions({
      allowJs: true,
      target: tsLang.ScriptTarget.ES6,
      allowNonTsExtensions: true,
      noNonAsciiIdentifier: false,
      noLib: true,
    });
  }

  async function onTreeSelectFile(
    newSelectedKeys: (string | number)[],
    event: any
  ) {
    const node = event.node as FileNode;
    const selectKey = String(newSelectedKeys[0]);
    selectedPath.value = node.path || '';
    if (!node.isLeaf) {
      const expanded = treeRef.value
        ?.getExpandedNodes()
        ?.some((it) => it != null && String(it.key) === selectKey);
      treeRef.value?.expandNode(String(node.key), !expanded);
      onTreeSelectDirectory(event.selectedNodes[0]);
      return;
    }
    treeEditingNode.value = node;
    const result = await readFile({
      currentKey: selectKey,
      title: node.title ?? '',
    });
    editorContent.value = result.data;
    const ext = node.title?.split('.')?.pop()?.toLowerCase();
    if (ext) editorLanguage.value = languageMap[ext as LanguageMapKey] ?? 'txt';
  }

  async function onTreeSelectDirectory(nodeData: TreeNodeData) {
    const result = await treeFile({ currentKey: String(nodeData.key) });
    nodeData.children = result.data;
  }

  async function refreshNode(node: FileNode) {
    if (node.isLeaf) return;
    await onTreeSelectDirectory(node);
  }

  async function refreshTree() {
    const result = await treeFile({ currentKey: '' });
    treeData.value = result.data;
  }

  const debounceSaveFile = useDebounceFn(
    async () => {
      if (!treeEditingNode.value) return;
      const node = treeEditingNode.value;
      await writeFile({
        currentKey: String(node.key),
        title: node.title ?? '',
        content: editorContent.value ?? '',
      });
      if (node.path) {
        const next = new Set(dirtyPaths.value);
        next.add(node.path);
        dirtyPaths.value = next;
      }
    },
    1000,
    { maxWait: 10_000 }
  );

  function onEditorTextChange() {
    if (treeEditingNode.value) debounceSaveFile();
  }

  async function reloadPaths(paths: string[]) {
    const unique = [...new Set(paths.filter(Boolean))];
    if (unique.length === 0) {
      Message.info(t('file.msg.noDirty'));
      return;
    }
    const wz = unique.filter((p) => isWzPath(p));
    const scripts = unique.filter((p) => isScriptsPath(p));
    if (wz.length) {
      Message.warning(t('file.msg.wzNotSupported'));
      const next = new Set(dirtyPaths.value);
      wz.forEach((p) => next.delete(p));
      dirtyPaths.value = next;
    }
    if (!scripts.length) {
      if (!wz.length) Message.info(t('file.msg.noDirty'));
      return;
    }
    reloading.value = true;
    try {
      const { data } = await reloadScriptsByPaths(scripts);
      const ok = data?.reloaded?.length ?? 0;
      const skip = data?.skipped?.length ?? 0;
      const next = new Set(dirtyPaths.value);
      (data?.reloaded || []).forEach((p: string) => next.delete(p));
      dirtyPaths.value = next;
      if (skip > 0) {
        Message.success(t('file.msg.reloadPartial', { ok, skip }));
      } else {
        Message.success(t('file.msg.reloadOk'));
      }
    } catch (e) {
      console.error(e);
    } finally {
      reloading.value = false;
    }
  }

  async function reloadModified() {
    await reloadPaths([...dirtyPaths.value]);
  }

  async function runReload(
    kind:
      | 'all'
      | 'events'
      | 'portals'
      | 'mapScripts'
      | 'quest'
      | 'npc'
      | 'reactor'
  ) {
    reloading.value = true;
    try {
      switch (kind) {
        case 'all':
          await reloadAllScriptsByGMCommand();
          dirtyPaths.value = new Set();
          break;
        case 'events':
          await reloadEventsByGMCommand();
          break;
        case 'portals':
          await reloadPortalsByGMCommand();
          break;
        case 'mapScripts':
          await reloadMapScriptsByGMCommand();
          break;
        case 'quest':
          await reloadQuestScriptsByGMCommand();
          break;
        case 'npc':
          await reloadNpcScriptsByGMCommand();
          break;
        case 'reactor':
          await reloadReactorScriptsByGMCommand();
          break;
        default:
          break;
      }
      Message.success(t('file.msg.reloadOk'));
    } catch (e) {
      console.error(e);
    } finally {
      reloading.value = false;
    }
  }

  refreshTree();
</script>

<script lang="ts">
  export default {
    name: 'ScriptFileManage',
    components: { VueMonacoEditor, IconDown },
  };
</script>

<style scoped>
  .file-toolbar {
    margin-bottom: 12px;
  }
  .file-hint {
    display: block;
    margin-top: 8px;
    font-size: 12px;
  }
  .file-layout {
    min-height: calc(100vh - 280px);
  }
  .file-sider {
    width: 360px !important;
  }
  .tree-title {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    max-width: 100%;
  }
  .dirty-tag {
    transform: scale(0.85);
  }
  .ctx-menu {
    position: fixed;
    z-index: 2000;
    min-width: 160px;
    padding: 4px 0;
    background: var(--color-bg-popup, #fff);
    border: 1px solid var(--color-border, #e5e6eb);
    border-radius: 4px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
  }
  .ctx-item {
    padding: 6px 12px;
    cursor: pointer;
    font-size: 13px;
    color: var(--color-text-1, #1d2129);
  }
  .ctx-item:hover {
    background: var(--color-fill-2, #f2f3f5);
  }
  .ctx-item.danger {
    color: rgb(var(--red-6));
  }
  :deep(.arco-layout-sider) {
    background-color: #2c2c2c !important;
    width: 360px;
  }
  :deep(.arco-virtual-list) {
    height: calc(100vh - 320px) !important;
    color: #d4d4d4;
    scrollbar-width: thin;
    scrollbar-color: #181818 #383838;
  }
  :deep(.arco-tree-node-title-text) {
    color: #b6b6b6;
  }
  :deep(.arco-tree-node-title):hover {
    background-color: #181818;
  }
  :deep(.arco-tree-node-plus-icon),
  :deep(.arco-tree-node-minus-icon) {
    background-color: #000;
  }
</style>
