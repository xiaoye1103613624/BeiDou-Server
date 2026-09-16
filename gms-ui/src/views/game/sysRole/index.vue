<template>
  <PageContainer :title="$t('menu.game.sysRole')">
    <ProCard>
      <a-alert
        class="bd-page-toolbar"
        type="info"
        :content="$t('sysRole.hint')"
      />
      <a-tabs v-model:active-key="activeTab" type="rounded">
        <a-tab-pane key="roles" :title="$t('sysRole.tab.roles')">
          <a-space class="bd-page-toolbar">
            <a-button @click="loadRoles">{{ $t('button.refresh') }}</a-button>
          </a-space>
          <a-table
            :loading="rolesLoading"
            :data="roles"
            row-key="id"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('sysRole.column.code')"
                data-index="code"
                :width="120"
              />
              <a-table-column
                :title="$t('sysRole.column.name')"
                data-index="name"
                :width="140"
              />
              <a-table-column
                :title="$t('sysRole.column.remark')"
                data-index="remark"
              />
              <a-table-column
                :title="$t('sysRole.column.enabled')"
                :width="90"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag :color="record.enabled === 1 ? 'green' : 'red'">
                    {{
                      record.enabled === 1
                        ? $t('account.list.updateForm.yes')
                        : $t('account.list.updateForm.no')
                    }}
                  </a-tag>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="menus" :title="$t('sysRole.tab.menus')">
          <a-space class="bd-page-toolbar" wrap>
            <span>{{ $t('sysRole.menus.selectRole') }}</span>
            <a-select
              v-model="bindRoleId"
              :placeholder="$t('sysRole.menus.selectRoleHint')"
              style="width: 220px"
              allow-clear
              @change="onBindRoleChange"
            >
              <a-option
                v-for="role in roles"
                :key="role.id"
                :value="role.id"
                :label="`${role.name} (${role.code})`"
              />
            </a-select>
            <a-button :disabled="!bindRoleId" @click="expandAllMenus">
              {{ $t('sysRole.menus.expandAll') }}
            </a-button>
            <a-button :disabled="!bindRoleId" @click="collapseAllMenus">
              {{ $t('sysRole.menus.collapseAll') }}
            </a-button>
            <a-button :disabled="!bindRoleId" @click="checkAllMenus">
              {{ $t('sysRole.menus.checkAll') }}
            </a-button>
            <a-button :disabled="!bindRoleId" @click="uncheckAllMenus">
              {{ $t('sysRole.menus.uncheckAll') }}
            </a-button>
            <a-button
              type="primary"
              :disabled="!bindRoleId"
              :loading="bindSaving"
              @click="saveMenuBinding"
            >
              {{ $t('sysRole.menus.save') }}
            </a-button>
          </a-space>
          <a-spin :loading="menusLoading" style="width: 100%">
            <a-tree
              v-if="bindRoleId"
              v-model:checked-keys="checkedMenuIds"
              v-model:expanded-keys="expandedMenuIds"
              :data="menuTreeData"
              :field-names="{
                key: 'key',
                title: 'title',
                children: 'children',
              }"
              checkable
              checked-strategy="all"
              block-node
            />
            <a-empty v-else :description="$t('sysRole.menus.selectRoleHint')" />
          </a-spin>
        </a-tab-pane>

        <a-tab-pane key="users" :title="$t('sysRole.tab.users')">
          <div class="assign-block bd-page-toolbar">
            <div class="assign-block__title">
              {{ $t('sysRole.users.assignTitle') }}
            </div>
            <a-form :model="assignForm" layout="inline">
              <a-form-item :label="$t('sysRole.users.accountId')">
                <a-input-number
                  v-model="assignForm.accountId"
                  :placeholder="$t('sysRole.users.accountIdPlaceholder')"
                  :min="1"
                  hide-button
                  style="width: 160px"
                />
              </a-form-item>
              <a-form-item :label="$t('sysRole.users.webadmin')">
                <a-switch
                  v-model="assignForm.webadmin"
                  :checked-value="1"
                  :unchecked-value="0"
                />
              </a-form-item>
              <a-form-item :label="$t('sysRole.users.roleCode')">
                <a-select
                  v-model="assignForm.roleCode"
                  style="width: 160px"
                  :disabled="assignForm.webadmin !== 1"
                >
                  <a-option
                    v-for="role in roles"
                    :key="role.code"
                    :value="role.code"
                    :label="roleLabel(role)"
                  />
                </a-select>
              </a-form-item>
              <a-form-item>
                <a-button
                  type="primary"
                  :loading="assignSaving"
                  @click="submitAssign"
                >
                  {{ $t('sysRole.users.assign') }}
                </a-button>
              </a-form-item>
            </a-form>
          </div>

          <a-space class="bd-page-toolbar">
            <a-button @click="loadAdminUsers">{{
              $t('button.refresh')
            }}</a-button>
          </a-space>
          <a-table
            :loading="usersLoading"
            :data="adminUsers"
            row-key="id"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('sysRole.column.accountId')"
                data-index="id"
                :width="100"
              />
              <a-table-column
                :title="$t('sysRole.column.accountName')"
                data-index="name"
                :width="160"
              />
              <a-table-column
                :title="$t('sysRole.column.nick')"
                data-index="nick"
                :width="140"
              />
              <a-table-column :title="$t('sysRole.column.role')" :width="180">
                <template #cell="{ record }">
                  <a-select
                    :model-value="record.roleCode || 'admin'"
                    size="small"
                    style="width: 150px"
                    @change="
                      (v: string | number | boolean | Record<string, any>) =>
                        changeUserRole(record, String(v))
                    "
                  >
                    <a-option
                      v-for="role in roles"
                      :key="role.code"
                      :value="role.code"
                      :label="roleLabel(role)"
                    />
                  </a-select>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('sysRole.column.operate')"
                :width="160"
                align="center"
              >
                <template #cell="{ record }">
                  <a-button
                    type="text"
                    status="danger"
                    size="mini"
                    @click="revokeUser(record)"
                  >
                    {{ $t('sysRole.users.revoke') }}
                  </a-button>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </ProCard>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { computed, onMounted, reactive, ref } from 'vue';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import {
    assignAccountRole,
    bindSysRoleMenus,
    listAdminUsers,
    listSysRoleMenus,
    listSysRoles,
    type AdminUserRecord,
    type SysRoleRecord,
  } from '@/api/sysRole';
  import { getSysMenuTree, type SysMenuRecord } from '@/api/sysMenu';
  import { useAppStore } from '@/store';

  const { t } = useI18n();
  const appStore = useAppStore();

  const activeTab = ref('roles');
  const rolesLoading = ref(false);
  const menusLoading = ref(false);
  const usersLoading = ref(false);
  const bindSaving = ref(false);
  const assignSaving = ref(false);

  const roles = ref<SysRoleRecord[]>([]);
  const adminUsers = ref<AdminUserRecord[]>([]);
  const menuTreeRaw = ref<SysMenuRecord[]>([]);
  const bindRoleId = ref<number | undefined>();
  const checkedMenuIds = ref<(string | number)[]>([]);
  const expandedMenuIds = ref<(string | number)[]>([]);

  const assignForm = reactive({
    accountId: undefined as number | undefined,
    webadmin: 1,
    roleCode: 'admin',
  });

  type MenuTreeNode = {
    key: number;
    title: string;
    children?: MenuTreeNode[];
  };

  const toTreeNodes = (nodes: SysMenuRecord[]): MenuTreeNode[] =>
    nodes.map((n) => ({
      key: n.id as number,
      title: n.localeKey ? t(n.localeKey) : n.name,
      children: n.children?.length ? toTreeNodes(n.children) : undefined,
    }));

  const menuTreeData = computed(() => toTreeNodes(menuTreeRaw.value));

  const collectMenuIds = (
    nodes: SysMenuRecord[],
    out: number[] = []
  ): number[] => {
    nodes.forEach((n) => {
      if (n.id != null) out.push(n.id);
      if (n.children?.length) collectMenuIds(n.children, out);
    });
    return out;
  };

  const roleLabel = (role: SysRoleRecord) => {
    const key = `sysRole.role.${role.code}`;
    const localized = t(key);
    return localized === key ? `${role.name} (${role.code})` : localized;
  };

  const loadRoles = async () => {
    rolesLoading.value = true;
    try {
      const { data } = await listSysRoles();
      roles.value = (data as SysRoleRecord[]) || [];
      if (!assignForm.roleCode && roles.value.length) {
        assignForm.roleCode = roles.value[0].code;
      }
    } finally {
      rolesLoading.value = false;
    }
  };

  const loadMenuTree = async () => {
    const { data } = await getSysMenuTree(true);
    menuTreeRaw.value = (data as SysMenuRecord[]) || [];
    expandedMenuIds.value = collectMenuIds(menuTreeRaw.value);
  };

  const loadRoleMenus = async (roleId: number) => {
    menusLoading.value = true;
    try {
      const { data } = await listSysRoleMenus(roleId);
      checkedMenuIds.value = ((data as number[]) || []).map(Number);
    } finally {
      menusLoading.value = false;
    }
  };

  const onBindRoleChange = async (
    value:
      | string
      | number
      | boolean
      | Record<string, any>
      | (string | number | boolean | Record<string, any>)[]
  ) => {
    if (value == null || value === '') {
      bindRoleId.value = undefined;
      checkedMenuIds.value = [];
      return;
    }
    const roleId = Number(value);
    if (Number.isNaN(roleId)) {
      bindRoleId.value = undefined;
      checkedMenuIds.value = [];
      return;
    }
    bindRoleId.value = roleId;
    await loadRoleMenus(roleId);
  };

  const expandAllMenus = () => {
    expandedMenuIds.value = collectMenuIds(menuTreeRaw.value);
  };

  const collapseAllMenus = () => {
    expandedMenuIds.value = [];
  };

  const checkAllMenus = () => {
    checkedMenuIds.value = collectMenuIds(menuTreeRaw.value);
  };

  const uncheckAllMenus = () => {
    checkedMenuIds.value = [];
  };

  const saveMenuBinding = async () => {
    if (!bindRoleId.value) return;
    bindSaving.value = true;
    try {
      await bindSysRoleMenus({
        roleId: bindRoleId.value,
        menuIds: checkedMenuIds.value.map(Number),
      });
      await appStore.fetchServerMenuConfig({ silent: true });
      Message.success(t('message.success'));
    } finally {
      bindSaving.value = false;
    }
  };

  const loadAdminUsers = async () => {
    usersLoading.value = true;
    try {
      const { data } = await listAdminUsers();
      adminUsers.value = (data as AdminUserRecord[]) || [];
    } finally {
      usersLoading.value = false;
    }
  };

  const submitAssign = async () => {
    if (!assignForm.accountId) {
      Message.warning(t('sysRole.accountIdRequired'));
      return;
    }
    assignSaving.value = true;
    try {
      await assignAccountRole({
        accountId: assignForm.accountId,
        webadmin: assignForm.webadmin,
        roleCode:
          assignForm.webadmin === 1
            ? assignForm.roleCode || 'admin'
            : undefined,
      });
      Message.success(t('message.success'));
      assignForm.accountId = undefined;
      await loadAdminUsers();
    } finally {
      assignSaving.value = false;
    }
  };

  const changeUserRole = async (record: AdminUserRecord, roleCode: string) => {
    await assignAccountRole({
      accountId: record.id,
      webadmin: 1,
      roleCode,
    });
    Message.success(t('message.success'));
    await loadAdminUsers();
  };

  const revokeUser = (record: AdminUserRecord) => {
    Modal.confirm({
      title: t('sysRole.users.revoke'),
      content: t('sysRole.users.revokeConfirm'),
      onOk: async () => {
        await assignAccountRole({
          accountId: record.id,
          webadmin: 0,
        });
        Message.success(t('message.success'));
        await loadAdminUsers();
      },
    });
  };

  onMounted(async () => {
    await Promise.all([loadRoles(), loadMenuTree(), loadAdminUsers()]);
  });
</script>

<script lang="ts">
  export default {
    name: 'SysRole',
  };
</script>

<style scoped lang="less">
  .assign-block {
    padding: 12px 16px;
    margin-bottom: 8px;
    border: 1px solid var(--color-border-2);
    border-radius: var(--border-radius-medium);
    background: var(--color-fill-1);

    &__title {
      margin-bottom: 12px;
      font-weight: 500;
      color: var(--color-text-1);
    }
  }
</style>
