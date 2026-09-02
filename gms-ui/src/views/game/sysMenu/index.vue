<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.sysMenu')">
      <a-alert
        style="margin-bottom: 12px"
        type="info"
        :content="$t('sysMenu.hint')"
      />
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" @click="openCreate()">{{
          $t('sysMenu.addRoot')
        }}</a-button>
        <a-button @click="loadTree">{{ $t('button.refresh') }}</a-button>
        <a-button @click="refreshSidebar">{{
          $t('sysMenu.refreshSidebar')
        }}</a-button>
      </a-space>
      <a-table
        :loading="loading"
        :data="treeData"
        row-key="id"
        :pagination="false"
        :bordered="{ cell: true }"
        default-expand-all-rows
      >
        <template #columns>
          <a-table-column :title="$t('sysMenu.column.title')" :width="200">
            <template #cell="{ record }">
              {{ record.localeKey ? $t(record.localeKey) : '-' }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sysMenu.column.name')"
            data-index="name"
            :width="160"
          />
          <a-table-column
            :title="$t('sysMenu.column.path')"
            data-index="path"
            :width="180"
          />
          <a-table-column
            :title="$t('sysMenu.column.locale')"
            data-index="localeKey"
            :width="200"
          />
          <a-table-column
            :title="$t('sysMenu.column.icon')"
            data-index="icon"
            :width="120"
          />
          <a-table-column :title="$t('sysMenu.column.type')" :width="90">
            <template #cell="{ record }">
              {{ $t(`sysMenu.type.${record.menuType ?? 1}`) }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sysMenu.column.sort')"
            data-index="sortOrder"
            :width="70"
          />
          <a-table-column :title="$t('sysMenu.column.enabled')" :width="90">
            <template #cell="{ record }">
              <a-switch
                :model-value="record.enabled"
                :checked-value="1"
                :unchecked-value="0"
                @change="(v: number) => toggleEnabled(record, v)"
              />
            </template>
          </a-table-column>
          <a-table-column :title="$t('sysMenu.column.hide')" :width="90">
            <template #cell="{ record }">
              <a-switch
                :model-value="record.hideInMenu"
                :checked-value="1"
                :unchecked-value="0"
                @change="(v: number) => toggleHide(record, v)"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('sysMenu.column.operate')"
            :width="280"
            align="center"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" size="mini" @click="openCreate(record)">
                  {{ $t('sysMenu.addChild') }}
                </a-button>
                <a-button type="text" size="mini" @click="openEdit(record)">
                  {{ $t('button.edit') }}
                </a-button>
                <a-button type="text" size="mini" @click="move(record, -1)">
                  {{ $t('sysMenu.moveUp') }}
                </a-button>
                <a-button type="text" size="mini" @click="move(record, 1)">
                  {{ $t('sysMenu.moveDown') }}
                </a-button>
                <a-button
                  type="text"
                  size="mini"
                  status="danger"
                  @click="onDelete(record)"
                >
                  {{ $t('button.delete') }}
                </a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="modalVisible"
      :title="editing?.id ? $t('sysMenu.edit') : $t('button.add')"
      :ok-loading="saving"
      unmount-on-close
      @before-ok="onSave"
    >
      <a-form :model="form" layout="vertical">
        <a-form-item :label="$t('sysMenu.form.parent')">
          <a-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            :field-names="{ key: 'id', title: 'label', children: 'children' }"
            allow-clear
            :placeholder="$t('sysMenu.form.parentRoot')"
          />
        </a-form-item>
        <a-form-item :label="$t('sysMenu.column.name')" required>
          <a-input v-model="form.name" />
        </a-form-item>
        <a-form-item :label="$t('sysMenu.column.path')">
          <a-input v-model="form.path" />
        </a-form-item>
        <a-form-item :label="$t('sysMenu.column.locale')">
          <a-input v-model="form.localeKey" />
        </a-form-item>
        <a-form-item :label="$t('sysMenu.column.icon')">
          <a-input v-model="form.icon" placeholder="icon-dashboard" />
        </a-form-item>
        <a-form-item :label="$t('sysMenu.column.type')">
          <a-select v-model="form.menuType">
            <a-option :value="0">{{ $t('sysMenu.type.0') }}</a-option>
            <a-option :value="1">{{ $t('sysMenu.type.1') }}</a-option>
            <a-option :value="2">{{ $t('sysMenu.type.2') }}</a-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('sysMenu.column.sort')">
          <a-input-number v-model="form.sortOrder" :min="0" />
        </a-form-item>
        <a-form-item :label="$t('sysMenu.column.roles')">
          <a-input v-model="form.roles" placeholder="admin" />
        </a-form-item>
        <a-form-item :label="$t('sysMenu.form.requiresAuth')">
          <a-switch
            v-model="form.requiresAuth"
            :checked-value="1"
            :unchecked-value="0"
          />
        </a-form-item>
        <a-form-item :label="$t('sysMenu.column.hide')">
          <a-switch
            v-model="form.hideInMenu"
            :checked-value="1"
            :unchecked-value="0"
          />
        </a-form-item>
        <a-form-item :label="$t('sysMenu.column.enabled')">
          <a-switch
            v-model="form.enabled"
            :checked-value="1"
            :unchecked-value="0"
          />
        </a-form-item>
        <a-form-item :label="$t('sysMenu.form.remark')">
          <a-input v-model="form.remark" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, reactive, ref } from 'vue';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import {
    deleteSysMenu,
    getSysMenuTree,
    reorderSysMenu,
    saveSysMenu,
    type SysMenuRecord,
  } from '@/api/sysMenu';
  import { useAppStore } from '@/store';

  const { t } = useI18n();
  const appStore = useAppStore();
  const loading = ref(false);
  const saving = ref(false);
  const treeData = ref<SysMenuRecord[]>([]);
  const modalVisible = ref(false);
  const editing = ref<SysMenuRecord | null>(null);

  const form = reactive<SysMenuRecord>({
    parentId: 0,
    name: '',
    path: '',
    localeKey: '',
    icon: '',
    sortOrder: 0,
    menuType: 1,
    roles: 'admin',
    requiresAuth: 1,
    hideInMenu: 0,
    enabled: 1,
    remark: '',
  });

  type ParentOption = {
    id: number;
    label: string;
    children?: ParentOption[];
    disabled?: boolean;
  };

  const parentOptions = computed(() => {
    const walk = (nodes: SysMenuRecord[]): ParentOption[] =>
      nodes.map((n) => ({
        id: n.id as number,
        label: n.localeKey ? t(n.localeKey) : n.name,
        disabled: editing.value?.id != null && n.id === editing.value.id,
        children: n.children?.length ? walk(n.children) : undefined,
      }));
    return walk(treeData.value);
  });

  const flatten = (nodes: SysMenuRecord[], out: SysMenuRecord[] = []) => {
    nodes.forEach((n) => {
      out.push(n);
      if (n.children?.length) flatten(n.children, out);
    });
    return out;
  };

  const findSiblings = (parentId: number) => {
    if (!parentId) {
      return treeData.value;
    }
    const all = flatten(treeData.value);
    const parent = all.find((x) => x.id === parentId);
    return parent?.children || [];
  };

  const loadTree = async () => {
    loading.value = true;
    try {
      const { data } = await getSysMenuTree(true);
      treeData.value = (data as SysMenuRecord[]) || [];
    } finally {
      loading.value = false;
    }
  };

  const refreshSidebar = async () => {
    await appStore.fetchServerMenuConfig({ silent: true });
    Message.success(t('message.success'));
  };

  const resetForm = (partial?: Partial<SysMenuRecord>) => {
    form.id = undefined;
    form.parentId = 0;
    form.name = '';
    form.path = '';
    form.localeKey = '';
    form.icon = '';
    form.sortOrder = 0;
    form.menuType = 1;
    form.roles = 'admin';
    form.requiresAuth = 1;
    form.hideInMenu = 0;
    form.enabled = 1;
    form.remark = '';
    if (partial) Object.assign(form, partial);
  };

  const openCreate = (parent?: SysMenuRecord) => {
    editing.value = null;
    const siblings = findSiblings(parent?.id || 0);
    const maxSort = siblings.reduce(
      (m, s) => Math.max(m, s.sortOrder ?? 0),
      -1
    );
    resetForm({
      parentId: parent?.id || 0,
      sortOrder: maxSort + 1,
      menuType: parent ? 1 : 0,
    });
    modalVisible.value = true;
  };

  const openEdit = (record: SysMenuRecord) => {
    editing.value = record;
    resetForm({
      id: record.id,
      parentId: record.parentId ?? 0,
      name: record.name,
      path: record.path || '',
      localeKey: record.localeKey || '',
      icon: record.icon || '',
      sortOrder: record.sortOrder ?? 0,
      menuType: record.menuType ?? 1,
      roles: record.roles || 'admin',
      requiresAuth: record.requiresAuth ?? 1,
      hideInMenu: record.hideInMenu ?? 0,
      enabled: record.enabled ?? 1,
      remark: record.remark || '',
    });
    modalVisible.value = true;
  };

  const onSave = async () => {
    if (!form.name?.trim()) {
      Message.warning(t('sysMenu.form.nameRequired'));
      return false;
    }
    saving.value = true;
    try {
      await saveSysMenu({
        ...form,
        parentId: form.parentId || 0,
      });
      Message.success(t('message.success'));
      modalVisible.value = false;
      await loadTree();
      await appStore.fetchServerMenuConfig({ silent: true });
      return true;
    } catch {
      return false;
    } finally {
      saving.value = false;
    }
  };

  const patchSave = async (
    record: SysMenuRecord,
    patch: Partial<SysMenuRecord>
  ) => {
    await saveSysMenu({
      id: record.id,
      parentId: record.parentId ?? 0,
      name: record.name,
      path: record.path || '',
      localeKey: record.localeKey || '',
      icon: record.icon || '',
      sortOrder: record.sortOrder ?? 0,
      menuType: record.menuType ?? 1,
      roles: record.roles || 'admin',
      requiresAuth: record.requiresAuth ?? 1,
      hideInMenu: record.hideInMenu ?? 0,
      enabled: record.enabled ?? 1,
      remark: record.remark || '',
      ...patch,
    });
    await loadTree();
    await appStore.fetchServerMenuConfig({ silent: true });
  };

  const toggleEnabled = async (record: SysMenuRecord, value: number) => {
    await patchSave(record, { enabled: value });
  };

  const toggleHide = async (record: SysMenuRecord, value: number) => {
    await patchSave(record, { hideInMenu: value });
  };

  const onDelete = (record: SysMenuRecord) => {
    Modal.confirm({
      title: t('button.delete'),
      content: t('sysMenu.deleteConfirm'),
      onOk: async () => {
        await deleteSysMenu(record.id as number);
        Message.success(t('message.success'));
        await loadTree();
        await appStore.fetchServerMenuConfig({ silent: true });
      },
    });
  };

  const move = async (record: SysMenuRecord, delta: number) => {
    const siblings = [...findSiblings(record.parentId ?? 0)].sort(
      (a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0)
    );
    const idx = siblings.findIndex((s) => s.id === record.id);
    const target = idx + delta;
    if (idx < 0 || target < 0 || target >= siblings.length) return;
    const a = siblings[idx];
    const b = siblings[target];
    await reorderSysMenu([
      {
        id: a.id as number,
        parentId: a.parentId ?? 0,
        sortOrder: b.sortOrder ?? 0,
      },
      {
        id: b.id as number,
        parentId: b.parentId ?? 0,
        sortOrder: a.sortOrder ?? 0,
      },
    ]);
    await loadTree();
    await appStore.fetchServerMenuConfig({ silent: true });
  };

  onMounted(loadTree);
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px;
  }
</style>
