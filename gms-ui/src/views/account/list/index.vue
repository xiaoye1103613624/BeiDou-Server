<template>
  <!-- 账户列表页面容器 -->
  <div class="container" :loading="true">
    <!-- 面包屑导航 -->
    <Breadcrumb />
    <!-- 主卡片容器 -->
    <a-card class="general-card" :title="$t('menu.account.list')">
      <!-- 筛选表单 -->
      <a-form :model="filterForm" class="a-from-keyword">
        <!-- ID筛选输入框 -->
        <a-form-item :label="$t('account.list.filter.id')">
          <a-input-number v-model="filterForm.id" />
        </a-form-item>
        <!-- 名称筛选输入框 -->
        <a-form-item :label="$t('account.list.filter.name')">
          <a-input v-model="filterForm.name" />
        </a-form-item>
        <!-- 最后登录时间起始日期选择器 -->
        <a-form-item :label="$t('account.list.filter.lastLoginStart')">
          <a-date-picker
            v-model="filterForm.lastLoginStart"
            style="width: 100%"
          />
        </a-form-item>
        <!-- 最后登录时间结束日期选择器 -->
        <a-form-item :label="$t('account.list.filter.lastLoginEnd')">
          <a-date-picker
            v-model="filterForm.lastLoginEnd"
            style="width: 100%"
          />
        </a-form-item>
        <!-- 创建时间起始日期选择器 -->
        <a-form-item :label="$t('account.list.filter.createdAtStart')">
          <a-date-picker
            v-model="filterForm.createdAtStart"
            style="width: 100%"
          />
        </a-form-item>
        <!-- 创建时间结束日期选择器 -->
        <a-form-item :label="$t('account.list.filter.createdAtEnd')">
          <a-date-picker
            v-model="filterForm.createdAtEnd"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
      <!-- 操作按钮组 -->
      <a-space class="a-space-btn">
        <!-- 加载数据按钮 -->
        <a-button type="primary" @click="loadData()">
          <template #icon>
            <icon-search />
          </template>
          {{ $t('button.load') }}
        </a-button>
        <!-- 重置筛选条件按钮 -->
        <a-button @click="resetClick">
          <template #icon>
            <icon-refresh />
          </template>
          {{ $t('button.reset') }}
        </a-button>
      </a-space>
      <a-divider />
      <!-- 添加按钮行 -->
      <a-row style="margin-bottom: 16px">
        <a-col>
          <a-space>
            <!-- 新增账户按钮 -->
            <a-button type="primary" @click="addClick">
              <template #icon>
                <icon-plus />
              </template>
              {{ $t('button.create') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <!-- 账户数据表格 -->
      <a-table
        row-key="id"
        :loading="loading"
        :data="tableData"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <!-- ID列 -->
          <a-table-column
            :title="$t('account.list.column.id')"
            data-index="id"
            :width="100"
          />
          <!-- 账户名称列 -->
          <a-table-column
            :title="$t('account.list.column.name')"
            data-index="name"
            :width="200"
          />
          <!-- 登录状态列 -->
          <a-table-column
            :title="$t('account.list.column.loggedin')"
            :width="120"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 登录状态标签 -->
              <a-tag v-if="record.loggedin" color="blue">
                {{ $t('account.list.column.loggedin.true') }}
              </a-tag>
              <a-tag v-else color="gray">
                {{ $t('account.list.column.loggedin.false') }}
              </a-tag>
            </template>
          </a-table-column>
          <!-- 封禁状态列 -->
          <a-table-column
            :title="$t('account.list.column.banned')"
            :width="120"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 如果账户被封禁，显示带工具提示的红色标签 -->
              <a-tooltip v-if="record.banned" :content="record.banreason">
                <a-tag color="red">
                  {{ $t('account.list.column.banned.true') }}
                </a-tag>
              </a-tooltip>
              <!-- 如果账户未被封禁，显示绿色标签 -->
              <a-tag v-else color="green">
                {{ $t('account.list.column.banned.false') }}
              </a-tag>
            </template>
          </a-table-column>
          <!-- 性别列 -->
          <a-table-column
            :title="$t('account.list.column.gender')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 根据性别值显示不同颜色的标签 -->
              <a-tag v-if="record.gender === 0" color="blue">
                {{ $t('account.list.column.gender.male') }}
              </a-tag>
              <a-tag v-else-if="record.gender === 1" color="red">
                {{ $t('account.list.column.gender.female') }}
              </a-tag>
              <a-tag v-else color="gray">
                {{ $t('account.list.column.gender.other') }}
              </a-tag>
            </template>
          </a-table-column>
          <!-- 最后登录时间列 -->
          <a-table-column
            :title="$t('account.list.column.lastLoginAt')"
            data-index="lastlogin"
            :width="120"
            align="center"
          />
          <!-- 注册时间列 -->
          <a-table-column
            :title="$t('account.list.column.registerAt')"
            data-index="createdat"
            :width="120"
            align="center"
          />
          <!-- 操作列 -->
          <a-table-column
            :title="$t('account.list.column.operate')"
            :width="150"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 编辑按钮（当账户在线时禁用） -->
              <a-button
                type="text"
                size="mini"
                :disabled="record.loggedin === 2"
                @click="editClick(record)"
              >
                {{ $t('button.edit') }}
              </a-button>
              <!-- 重置登录状态按钮 -->
              <a-button
                type="text"
                size="mini"
                @click="restLoggedInClick(record)"
              >
                {{ $t('account.list.column.operate.restLoggedIn') }}
              </a-button>
              <!-- 解封确认弹窗 -->
              <a-popconfirm
                type="warning"
                :content="$t('account.list.column.operate.unban.confirm')"
                @ok="unbanClick(record)"
              >
                <a-button
                  v-if="record.banned"
                  type="text"
                  size="mini"
                  status="warning"
                >
                  {{ $t('account.list.column.operate.unban') }}
                </a-button>
              </a-popconfirm>
              <!-- 封禁按钮（仅在账户未被封禁时显示） -->
              <a-button
                v-if="!record.banned"
                type="text"
                size="mini"
                status="danger"
                @click="banClick(record)"
              >
                {{ $t('account.list.column.operate.ban') }}
              </a-button>
              <!-- 删除确认弹窗 -->
              <a-popconfirm
                type="error"
                :content="$t('account.list.column.operate.delete.confirm')"
                @ok="deleteClick(record)"
              >
                <a-button type="text" size="mini" status="danger">
                  {{ $t('account.list.column.operate.delete') }}
                </a-button>
              </a-popconfirm>
            </template>
          </a-table-column>
        </template>
      </a-table>
      <!-- 分页组件 -->
      <a-pagination
        style="margin-top: 20px"
        :total="total"
        :page-size="size"
        :current="page"
        show-total
        show-jumper
        show-page-size
        :page-size-options="[10, 20, 50, 100]"
        @change="pageChange"
        @page-size-change="pageSizeChange"
      />
    </a-card>
    <account-add-form ref="accountAddFormRef" @reload="loadData" />
    <account-update-form ref="accountUpdateFormRef" @reload="loadData" />
    <a-modal
      v-model:visible="reasonVisible"
      :title="reasonTitle"
      :ok-loading="loading"
      :mask-closable="false"
      :esc-to-close="false"
      :ok-text="$t('account.list.column.operate.ban')"
      :on-before-ok="submitBanClick"
    >
      <a-form :model="{ reason }">
        <a-form-item
          :label="$t('account.list.column.operate.ban.reason')"
          validate-trigger="blur"
        >
          <a-input v-model="reason" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  // 导入加载状态钩子
  import useLoading from '@/hooks/loading';
  // 导入Vue响应式引用函数
  import { ref } from 'vue';
  // 导入账户状态类型定义
  import { AccountState } from '@/store/modules/account/types';
  // 导入账户管理相关API
  import {
    banAccount,
    deleteAccount,
    getAccountList,
    resetLoggedIn,
    unbanAccount,
  } from '@/api/account';
  // 导入新增账户表单组件
  import AccountAddForm from '@/views/account/list/addForm.vue';
  // 导入更新账户表单组件
  import AccountUpdateForm from '@/views/account/list/updateForm.vue';
  // 导入Arco Design消息提示组件
  import { Message } from '@arco-design/web-vue';
  // 导入国际化功能
  import { useI18n } from 'vue-i18n';

  // 获取国际化函数
  const { t } = useI18n();
  // 创建加载状态和设置函数
  const { loading, setLoading } = useLoading(false);
  // 账户数据表格
  const tableData = ref<AccountState[]>([]);
  // 总记录数
  const total = ref(0);
  // 当前页码
  const page = ref(1);
  // 每页大小
  const size = ref(14);
  // 筛选表单数据
  const filterForm = ref<{
    id?: number;
    name?: string;
    lastLoginStart?: string;
    lastLoginEnd?: string;
    createdAtStart?: string;
    createdAtEnd?: string;
  }>({
    id: undefined,
    name: undefined,
    lastLoginStart: undefined,
    lastLoginEnd: undefined,
    createdAtStart: undefined,
    createdAtEnd: undefined,
  });
  // 封禁原因弹窗是否可见
  const reasonVisible = ref(false);
  // 封禁原因弹窗标题
  const reasonTitle = ref('');
  // 封禁原因
  const reason = ref('');
  // 准备封禁的账户ID
  const banAccountIdReady = ref(0);

  /**
   * 加载账户列表数据
   * 根据当前页码、每页大小和筛选条件获取账户数据
   */
  const loadData = async () => {
    setLoading(true);
    try {
      // 调用API获取账户列表
      const { data } = await getAccountList(
        page.value,
        size.value,
        filterForm.value.id,
        filterForm.value.name,
        filterForm.value.lastLoginStart,
        filterForm.value.lastLoginEnd,
        filterForm.value.createdAtStart,
        filterForm.value.createdAtEnd
      );
      // 更新表格数据
      tableData.value = data.records;
      // 更新总记录数
      total.value = data.totalRow;
    } finally {
      // 取消加载状态
      setLoading(false);
    }
  };
  // 页面加载时自动调用
  loadData();

  /**
   * 页码变更处理函数
   * 当用户切换分页时更新当前页码并重新加载数据
   * @param data - 新的页码
   */
  const pageChange = (data: number) => {
    // 更新当前页码
    page.value = data;
    // 重新加载数据
    loadData();
  };

  /**
   * 每页大小变更处理函数
   * 当用户更改每页显示数量时更新页面大小并重新加载数据
   * @param data - 新的每页大小
   */
  const pageSizeChange = (data: number) => {
    // 重置为第一页
    page.value = 1;
    // 更新每页大小
    size.value = data;
    // 重新加载数据
    loadData();
  };

  /**
   * 重置筛选条件
   * 清空所有筛选条件并回到第一页，然后重新加载数据
   */
  const resetClick = () => {
    // 清空ID筛选条件
    filterForm.value.id = undefined;
    // 清空名称筛选条件
    filterForm.value.name = undefined;
    // 清空最后登录时间起始筛选条件
    filterForm.value.lastLoginStart = undefined;
    // 清空最后登录时间结束筛选条件
    filterForm.value.lastLoginEnd = undefined;
    // 清空创建时间起始筛选条件
    filterForm.value.createdAtStart = undefined;
    // 清空创建时间结束筛选条件
    filterForm.value.createdAtEnd = undefined;
    // 回到第一页
    page.value = 1;
    // 重新加载数据
    loadData();
  };

  // 新增账户表单引用
  const accountAddFormRef = ref();
  
  /**
   * 处理新增按钮点击事件
   * 初始化新增账户表单
   */
  const addClick = () => {
    // 调用新增表单的初始化方法
    accountAddFormRef.value.init();
  };

  // 更新账户表单引用
  const accountUpdateFormRef = ref();
  
  /**
   * 处理编辑按钮点击事件
   * 初始化更新账户表单并传入要编辑的数据
   * @param data - 要编辑的账户数据
   */
  const editClick = (data: AccountState) => {
    // 调用更新表单的初始化方法，传入账户数据
    accountUpdateFormRef.value.init(data);
  };

  /**
   * 重置账户登录状态
   * 将指定账户的登录状态重置为离线
   * @param data - 要重置登录状态的账户数据
   */
  const restLoggedInClick = async (data: AccountState) => {
    setLoading(true);
    try {
      // 调用API重置账户登录状态
      await resetLoggedIn(data.id);
      // 显示成功消息
      Message.success(t('message.success'));
      // 重新加载数据
      await loadData();
    } finally {
      // 取消加载状态
      setLoading(false);
    }
  };

  /**
   * 处理封禁账户按钮点击事件
   * 显示封禁原因输入弹窗
   * @param data - 要封禁的账户数据
   */
  const banClick = async (data: AccountState) => {
    // 设置弹窗标题
    reasonTitle.value = `${t(
      'account.list.column.operate.ban.reason.title'
    )} [${data.id}] ${data.name}`;
    // 设置准备封禁的账户ID
    banAccountIdReady.value = data.id;
    // 显示封禁原因弹窗
    reasonVisible.value = true;
    // 清空封禁原因
    reason.value = '';
  };

  /**
   * 解封账户
   * 移除指定账户的封禁状态
   * @param data - 要解封的账户数据
   */
  const unbanClick = async (data: AccountState) => {
    setLoading(true);
    try {
      // 调用API解封账户
      await unbanAccount(data.id);
      // 显示成功消息
      Message.success(t('message.success'));
      // 重新加载数据
      await loadData();
    } finally {
      // 取消加载状态
      setLoading(false);
    }
  };

  /**
   * 提交封禁操作
   * 执行账户封禁操作
   */
  const submitBanClick = async () => {
    setLoading(true);
    try {
      // 调用API执行封禁操作
      await banAccount(banAccountIdReady.value, reason.value);
      // 显示成功消息
      Message.success(t('message.success'));
      // 重新加载数据
      await loadData();
    } finally {
      // 取消加载状态
      setLoading(false);
    }
  };

  /**
   * 删除账户
   * 永久删除指定账户
   * @param data - 要删除的账户数据
   */
  const deleteClick = async (data: AccountState) => {
    setLoading(true);
    try {
      // 调用API删除账户
      await deleteAccount(data.id);
      // 显示成功消息
      Message.success(t('message.success'));
      // 重新加载数据
      await loadData();
    } finally {
      // 取消加载状态
      setLoading(false);
    }
  };
</script>

<script lang="ts">
  export default {
    name: 'AccountList',
  };
</script>

<style lang="less">
  .a-from-keyword {
    @media (min-width: @screen-sm) {
      display: flex;
      flex-direction: initial;
      flex-wrap: wrap;
      width: 100%;
      div {
        margin-right: 5px;
      }
      .arco-row {
        width: max-content;
        display: flex;
      }
      .arco-col {
        flex: max-content;
        width: 100%;
      }
      .arco-form-item-label-col,
      .arco-form-item-label {
        min-width: auto;
        text-align: right;
      }
    }
    @media (max-width: @screen-sm) {
      display: block;
      flex-direction: column;
      .arco-row {
        flex-flow: row wrap;
        width: 100%;
      }

      .arco-col {
        flex: 0 0 100%;
        width: 100%;
      }

      .arco-form-item-label-col {
        display: contents;
      }
    }
  }
</style>