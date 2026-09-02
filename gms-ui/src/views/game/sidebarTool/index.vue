<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.sidebarTool')">
      <a-alert type="info" style="margin-bottom: 12px">
        {{ $t('sidebarTool.hint') }}
      </a-alert>
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" :loading="savingAll" @click="saveAllClick">
          {{ $t('sidebarTool.saveAll') }}
        </a-button>
        <a-button :loading="reloading" @click="reloadClick">
          {{ $t('sidebarTool.reload') }}
        </a-button>
      </a-space>
      <a-table
        row-key="toolIndex"
        :loading="loading"
        :data="rows"
        column-resizable
        :pagination="false"
        :scroll="{ x: 1280 }"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column
            :title="$t('sidebarTool.column.index')"
            data-index="toolIndex"
            :width="70"
            align="center"
          />
          <a-table-column :title="$t('sidebarTool.column.label')" :width="120">
            <template #cell="{ record }">
              <a-input v-model="record.label" allow-clear />
            </template>
          </a-table-column>
          <a-table-column :title="$t('sidebarTool.column.script')" :width="300">
            <template #cell="{ record }">
              <a-tree-select
                :model-value="record.scriptPath || undefined"
                :data="scriptTree"
                :field-names="{
                  key: 'key',
                  title: 'title',
                  children: 'children',
                  disabled: 'disabled',
                }"
                allow-clear
                allow-search
                :filter-tree-node="filterScriptNode"
                :placeholder="$t('sidebarTool.script.placeholder')"
                :loading="scriptTreeLoading"
                :dropdown-style="{ maxHeight: '360px', overflow: 'auto' }"
                style="width: 100%"
                @change="(v: string | undefined) => onScriptSelect(record, v)"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sidebarTool.column.tipTitle')"
            :width="140"
          >
            <template #cell="{ record }">
              <a-input v-model="record.tipTitle" allow-clear />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sidebarTool.column.tipDesc')"
            :width="240"
          >
            <template #cell="{ record }">
              <a-input v-model="record.tipDesc" allow-clear />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sidebarTool.column.enabled')"
            :width="90"
            align="center"
          >
            <template #cell="{ record }">
              <a-switch
                :model-value="!!record.enabled"
                :disabled="!record.scriptPath"
                @change="(v: boolean | string | number) => (record.enabled = v ? 1 : 0)"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sidebarTool.column.visible')"
            :width="90"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="isVisible(record) ? 'green' : 'red'">
                {{
                  isVisible(record)
                    ? $t('sidebarTool.visible.yes')
                    : $t('sidebarTool.visible.no')
                }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sidebarTool.column.operate')"
            :width="100"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-button
                type="text"
                size="small"
                :loading="savingIndex === record.toolIndex"
                @click="saveRow(record)"
              >
                {{ $t('sidebarTool.saveRow') }}
              </a-button>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import type { TreeNodeData } from '@arco-design/web-vue/es/tree/interface';
  import { useI18n } from 'vue-i18n';
  import {
    SidebarScriptTreeNode,
    SidebarToolConfig,
    getSidebarScriptTree,
    getSidebarToolList,
    reloadSidebarTool,
    saveAllSidebarTool,
    saveSidebarTool,
  } from '@/api/sidebarTool';

  const { t } = useI18n();
  const loading = ref(false);
  const savingAll = ref(false);
  const reloading = ref(false);
  const scriptTreeLoading = ref(false);
  const savingIndex = ref<number | null>(null);
  const rows = ref<SidebarToolConfig[]>([]);
  const scriptTree = ref<SidebarScriptTreeNode[]>([]);

  function isVisible(row: SidebarToolConfig) {
    return !!row.enabled && !!(row.scriptPath && row.scriptPath.trim());
  }

  function onScriptChange(row: SidebarToolConfig) {
    if (!row.scriptPath || !row.scriptPath.trim()) {
      row.enabled = 0;
      row.scriptPath = '';
    }
  }

  function onScriptSelect(row: SidebarToolConfig, value?: string) {
    // 目录节点 key 以 dir: 开头，不可选；若异常落到此则忽略
    if (value && String(value).startsWith('dir:')) {
      return;
    }
    row.scriptPath = value ? String(value) : '';
    onScriptChange(row);
  }

  /** 按节点标题或完整路径搜索，保留目录层级过滤体验 */
  function filterScriptNode(searchValue: string, nodeData: TreeNodeData) {
    const q = (searchValue || '').trim().toLowerCase();
    if (!q) {
      return true;
    }
    const title = String(nodeData.title ?? '').toLowerCase();
    const key = String(nodeData.key ?? '').toLowerCase();
    return title.includes(q) || key.includes(q);
  }

  async function loadScriptTree() {
    scriptTreeLoading.value = true;
    try {
      const res = await getSidebarScriptTree();
      scriptTree.value = (res.data || []) as SidebarScriptTreeNode[];
    } catch (e) {
      Message.error(t('sidebarTool.script.loadFailed'));
      scriptTree.value = [];
    } finally {
      scriptTreeLoading.value = false;
    }
  }

  async function loadRows() {
    loading.value = true;
    try {
      const res = await getSidebarToolList();
      rows.value = (res.data || []) as SidebarToolConfig[];
    } finally {
      loading.value = false;
    }
  }

  async function saveRow(row: SidebarToolConfig) {
    savingIndex.value = row.toolIndex;
    try {
      onScriptChange(row);
      await saveSidebarTool(row);
      Message.success(t('sidebarTool.save.success'));
      await Promise.all([loadRows(), loadScriptTree()]);
    } finally {
      savingIndex.value = null;
    }
  }

  async function saveAllClick() {
    savingAll.value = true;
    try {
      rows.value.forEach(onScriptChange);
      await saveAllSidebarTool(rows.value);
      Message.success(t('sidebarTool.save.success'));
      await Promise.all([loadRows(), loadScriptTree()]);
    } finally {
      savingAll.value = false;
    }
  }

  async function reloadClick() {
    reloading.value = true;
    try {
      await reloadSidebarTool();
      Message.success(t('sidebarTool.reload.success'));
      await Promise.all([loadRows(), loadScriptTree()]);
    } finally {
      reloading.value = false;
    }
  }

  onMounted(async () => {
    await Promise.all([loadRows(), loadScriptTree()]);
  });
</script>
