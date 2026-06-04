<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.scriptManager')">
      <a-alert
        v-if="overrideActive"
        type="warning"
        show-icon
        closable
        style="margin-bottom: 12px"
      >
        {{ $t('scriptManager.overrideBanner', { path: overridePath }) }}
      </a-alert>
      <a-layout>
        <a-layout-sider @contextmenu.prevent="onTreeContextMenu">
          <div class="tree-toolbar">
            <a-button-group type="primary" size="mini">
              <a-button
                :title="$t('scriptManager.toolbar.newFile')"
                @click="handleCreateFile"
              >
                <template #icon><IconFile /></template>
              </a-button>
              <a-button
                :title="$t('scriptManager.toolbar.newFolder')"
                @click="handleCreateFolder"
              >
                <template #icon><IconFolderAdd /></template>
              </a-button>
              <a-button
                :title="$t('scriptManager.toolbar.refresh')"
                @click="handleRefresh"
              >
                <template #icon><IconRefresh /></template>
              </a-button>
            </a-button-group>
          </div>
          <a-tree
            ref="treeRef"
            theme="dark"
            size="mini"
            :block-node="true"
            :data="treeData"
            :load-more="onTreeLoadDirectory"
            :virtual-list-props="{ buffer: 100 }"
            @select="onTreeSelect"
          >
            <template #switcher-icon><IconDown /></template>
          </a-tree>
        </a-layout-sider>
        <a-layout-content>
          <vue-monaco-editor
            v-if="selectedFile"
            v-model:value="editorContent"
            :language="editorLanguage"
            default-language="javascript"
            theme="vs-dark"
            :options="editorOptions"
            @mount="onEditorMount"
            @change="debounceSaveFile"
          />
          <div v-else class="empty-state">
            {{ $t('scriptManager.emptyState') }}
          </div>
        </a-layout-content>
      </a-layout>
    </a-card>

    <!-- 创建/重命名 弹窗 -->
    <a-modal
      v-model:visible="dialogVisible"
      :title="
        dialogAction === 'rename'
          ? $t('scriptManager.dialog.rename')
          : dialogAction === 'createFolder'
          ? $t('scriptManager.dialog.createFolder')
          : $t('scriptManager.dialog.createFile')
      "
      @ok="onDialogConfirm"
      @cancel="dialogVisible = false"
    >
      <a-input
        v-model="dialogName"
        :placeholder="$t('scriptManager.dialog.namePlaceholder')"
      />
    </a-modal>

    <!-- 右键菜单 -->
    <div
      v-if="contextMenu.visible"
      class="tree-context-menu"
      :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
      @click.stop
    >
      <div class="context-menu-item" @click="onContextMenuRename">重命名</div>
      <div class="context-menu-item danger" @click="onContextMenuDelete"
        >删除</div
      >
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, shallowRef, reactive, onMounted, onUnmounted } from 'vue';
  import {
    Editor,
    MonacoEditor,
    VueMonacoEditor,
  } from '@guolao/vue-monaco-editor';
  import {
    treeScript,
    readScript,
    writeScript,
    createScript,
    deleteScript,
    renameScript,
    getOverrideStatus,
    ScriptTreeNode,
  } from '@/api/scriptFile';
  import { useDebounceFn } from '@vueuse/core';
  import type { TreeNodeData } from '@arco-design/web-vue/es/tree/interface';
  import { Message, Modal } from '@arco-design/web-vue';
  import {
    IconDown,
    IconFile,
    IconFolderAdd,
    IconRefresh,
  } from '@arco-design/web-vue/es/icon';
  import localDts from '@/views/game/file/types/beidoums-scripts.d.ts.txt?raw';

  // ========== 编辑器 ==========
  const editor = shallowRef<typeof Editor>();
  const editorContent = ref('');
  const editorLanguage = ref('javascript');
  const editorOptions = {
    automaticLayout: true,
    formatOnType: true,
    formatOnPaste: true,
  };

  const languageMap: Record<string, string> = {
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
    txt: 'plaintext',
  };

  function onEditorMount(editorInstance: any, monacoInstance: MonacoEditor) {
    editor.value = editorInstance;
    registerCodeCompletion(monacoInstance);
  }

  async function registerCodeCompletion(monaco: MonacoEditor) {
    let usingDts = '';
    try {
      const response = await fetch(
        'https://cdn.jsdelivr.net/gh/shinobi9/beidoums-scripts-snippets/types/beidoums-scripts.d.ts'
      );
      usingDts = response.ok ? await response.text() : usingDts;
    } catch {
      usingDts = localDts;
    }
    if (!usingDts) usingDts = localDts;

    monaco.languages.typescript.javascriptDefaults.addExtraLib(
      usingDts,
      'beidoums-scripts-dts'
    );
    monaco.languages.typescript.javascriptDefaults.setCompilerOptions({
      allowJs: true,
      target: monaco.languages.typescript.ScriptTarget.ES6,
      allowNonTsExtensions: true,
      noNonAsciiIdentifier: false,
      noLib: true,
    });
  }

  // ========== 树 ==========
  const treeData = ref<ScriptTreeNode[]>([]);
  const treeRef = ref();
  const selectedFile = ref('');
  const editingFile = ref('');

  // ========== 覆盖状态 ==========
  const overrideActive = ref(false);
  const overridePath = ref<string | null>(null);

  async function loadOverrideStatus() {
    try {
      const { data } = await getOverrideStatus();
      overrideActive.value = data.active;
      overridePath.value = data.path;
    } catch {
      // ignore
    }
  }

  // ========== 树操作 ==========
  async function loadTreeRoot() {
    const result = await treeScript({ path: '' });
    treeData.value = result.data;
  }

  async function onTreeLoadDirectory(nodeData: TreeNodeData) {
    const result = await treeScript({ path: String(nodeData.key) });
    nodeData.children = result.data;
  }

  function onTreeSelect(newSelectedKeys: (string | number)[], event: any) {
    const node = event.node as TreeNodeData;
    if (node.isLeaf) {
      loadFile(String(node.key), node.title as string);
    } else {
      // 点击文件夹：展开/收起
      const selectKey = String(newSelectedKeys[0]);
      const expanded = treeRef.value
        ?.getExpandedNodes()
        ?.some((it: TreeNodeData) => String(it.key) === selectKey);
      treeRef.value?.expandNode(String(node.key), !expanded);
      onTreeLoadDirectory(event.selectedNodes[0]);
    }
  }

  async function loadFile(path: string, name: string) {
    const result = await readScript({ path });
    selectedFile.value = path;
    editingFile.value = path;
    editorContent.value = result.data;
    const ext = name.split('.').pop()?.toLowerCase();
    if (ext) editorLanguage.value = languageMap[ext] ?? 'javascript';
  }

  async function refreshDirectory(key: string) {
    const result = await treeScript({ path: key });
    if (key) {
      // 查找父节点并更新children
      const findAndUpdate = (nodes: any[]): boolean => {
        return nodes.some((n) => {
          if (String(n.key) === key) {
            n.children = result.data;
            return true;
          }
          return n.children && findAndUpdate(n.children);
        });
      };
      findAndUpdate(treeData.value);
    } else {
      treeData.value = result.data;
    }
  }

  function handleRefresh() {
    loadTreeRoot();
  }

  // ========== 保存（防抖） ==========
  const doSave = useDebounceFn(
    async () => {
      if (!editingFile.value) return;
      try {
        await writeScript({
          path: editingFile.value,
          content: editorContent.value ?? '',
        });
      } catch (e: any) {
        Message.error(e?.message || '保存失败');
      }
    },
    1000,
    { maxWait: 10_000 }
  );

  function debounceSaveFile() {
    if (editingFile.value) doSave();
  }

  // ========== 新建/重命名/删除 ==========
  const dialogVisible = ref(false);
  const dialogName = ref('');
  const dialogAction = ref<'createFile' | 'createFolder' | 'rename'>(
    'createFile'
  );
  let renameTargetPath = '';
  let createParentPath = '';

  // 右键菜单
  const contextMenu = reactive({
    visible: false,
    x: 0,
    y: 0,
    nodePath: '',
    nodeTitle: '',
  });

  function onTreeContextMenu(e: MouseEvent) {
    const nodeEl = (e.target as HTMLElement).closest(
      '[data-arco-tree-node-key]'
    );
    if (!nodeEl) return;
    const key = nodeEl.getAttribute('data-arco-tree-node-key');
    const titleEl = nodeEl.querySelector('.arco-tree-node-title-text');
    const title = titleEl?.textContent ?? '';
    if (!key) return;
    contextMenu.visible = true;
    contextMenu.x = e.clientX;
    contextMenu.y = e.clientY;
    contextMenu.nodePath = key;
    contextMenu.nodeTitle = title;
  }

  function closeContextMenu() {
    contextMenu.visible = false;
  }

  function onContextMenuRename() {
    const { nodePath, nodeTitle } = contextMenu;
    closeContextMenu();
    handleRename(nodePath, nodeTitle);
  }

  function onContextMenuDelete() {
    const { nodePath, nodeTitle } = contextMenu;
    closeContextMenu();
    handleDelete(nodePath, nodeTitle);
  }

  function handleCreateFile() {
    dialogAction.value = 'createFile';
    createParentPath = getCurrentDirectoryPath();
    dialogName.value = '';
    dialogVisible.value = true;
  }

  function handleCreateFolder() {
    dialogAction.value = 'createFolder';
    createParentPath = getCurrentDirectoryPath();
    dialogName.value = '';
    dialogVisible.value = true;
  }

  function getCurrentDirectoryPath(): string {
    if (selectedFile.value) {
      const parts = selectedFile.value.split('/');
      parts.pop();
      return parts.join('/');
    }
    return '';
  }

  function handleRename(nodePath: string, nodeTitle: string) {
    dialogAction.value = 'rename';
    renameTargetPath = nodePath;
    dialogName.value = nodeTitle;
    dialogVisible.value = true;
  }

  function handleDelete(nodePath: string, nodeTitle: string) {
    Modal.confirm({
      title: '删除',
      content: `确定删除 "${nodeTitle}" 吗？此操作不可恢复。`,
      okButtonProps: { status: 'danger' },
      onOk: async () => {
        try {
          await deleteScript({ path: nodePath });
          Message.success('删除成功');
          const parts = nodePath.split('/');
          parts.pop();
          const parentPath = parts.join('/');
          await refreshDirectory(parentPath);
          if (selectedFile.value === nodePath) {
            selectedFile.value = '';
            editingFile.value = '';
            editorContent.value = '';
          }
        } catch (e: any) {
          Message.error(e?.message || '删除失败');
        }
      },
    });
  }

  async function onDialogConfirm() {
    const name = dialogName.value.trim();
    if (!name) {
      Message.warning('名称不能为空');
      return;
    }
    try {
      if (dialogAction.value === 'rename') {
        const parts = renameTargetPath.split('/');
        parts.pop();
        const parentPath = parts.join('/');
        const newPath = parentPath ? `${parentPath}/${name}` : name;
        await renameScript({
          oldPath: renameTargetPath,
          newPath,
        });
        Message.success('重命名成功');
        await refreshDirectory(parentPath);
        if (selectedFile.value === renameTargetPath) {
          selectedFile.value = newPath;
          editingFile.value = newPath;
        }
      } else {
        const fullPath = createParentPath
          ? `${createParentPath}/${name}`
          : name;
        await createScript({
          path: fullPath,
          directory: dialogAction.value === 'createFolder',
        });
        Message.success('创建成功');
        await refreshDirectory(createParentPath);
      }
      dialogVisible.value = false;
    } catch (e: any) {
      Message.error(e?.message || '操作失败');
    }
  }

  // ========== 初始化 ==========
  onMounted(() => {
    loadTreeRoot();
    loadOverrideStatus();
    document.addEventListener('click', closeContextMenu);
  });

  onUnmounted(() => {
    document.removeEventListener('click', closeContextMenu);
  });
</script>

<script lang="ts">
  export default {
    name: 'ScriptManager',
    components: { VueMonacoEditor },
  };
</script>

<style scoped>
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

  .tree-toolbar {
    padding: 6px 8px;
    background-color: #1e1e1e;
    border-bottom: 1px solid #383838;
  }

  .empty-state {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #888;
    font-size: 14px;
    background-color: #1e1e1e;
  }

  .tree-context-menu {
    position: fixed;
    z-index: 9999;
    background: #2c2c2c;
    border: 1px solid #454545;
    border-radius: 4px;
    padding: 4px 0;
    min-width: 100px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  }

  .context-menu-item {
    padding: 6px 14px;
    color: #ccc;
    font-size: 13px;
    cursor: pointer;
    user-select: none;
  }

  .context-menu-item:hover {
    background: #094771;
    color: #fff;
  }

  .context-menu-item.danger:hover {
    background: #a82121;
  }
</style>
