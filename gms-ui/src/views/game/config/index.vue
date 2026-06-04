<template>
  <!-- 配置管理页面容器 -->
  <div class="container">
    <!-- 面包屑导航 -->
    <Breadcrumb />
    <!-- 主卡片容器 -->
    <a-card class="general-card" :title="$t('menu.game.config')">
      <!-- 搜索和筛选区域 -->
      <a-space direction="vertical" align="start">
        <!-- 类型筛选单选组 -->
        <a-form-item :label="$t('config.search.type.label')">
          <a-radio-group
            v-for="type in types"
            :key="type"
            type="button"
            :model-value="condition.type"
            @change="typeChange"
          >
            <a-radio :value="type">
              <!-- 转换国际化类型显示 -->
              {{ transI18nType(type, false) }}
            </a-radio>
          </a-radio-group>
        </a-form-item>
        <!-- 子类型筛选单选组 -->
        <a-form-item :label="$t('config.search.subType.label')">
          <a-radio-group
            v-for="subType in subTypes"
            :key="subType"
            type="button"
            :model-value="condition.subType"
            @change="subTypeChange"
          >
            <a-radio :value="subType">
              <!-- 转换国际化子类型显示 -->
              {{ transI18nType(subType, true) }}
            </a-radio>
          </a-radio-group>
        </a-form-item>
        <!-- 搜索和操作按钮区域 -->
        <a-form-item :hide-label="true">
          <a-col :offset="0">
            <!-- 搜索过滤输入框 -->
            <a-input
              v-model="condition.filter"
              :placeholder="$t('config.placeholder.filter')"
            />
            <!-- 搜索按钮 -->
            <a-button type="primary" @click="searchData">
              {{ $t('button.search') }}
            </a-button>
            <!-- 重置按钮 -->
            <a-button @click="resetSearch">
              {{ $t('button.reset') }}
            </a-button>
            <!-- 新增按钮（只有未选择任何项时才启用） -->
            <a-button
              type="primary"
              status="success"
              :disabled="selectedKeys.length > 0"
              @click="addClick"
            >
              {{ $t('button.add') }}
            </a-button>
            <!-- 删除按钮（只有选择了至少一项时才启用） -->
            <a-button
              type="primary"
              status="danger"
              :disabled="selectedKeys.length === 0"
              @click="delClick"
            >
              {{ $t('button.delete') }}
            </a-button>
            <!-- 导入配置按钮 -->
            <a-button type="primary" @click="importClick">
              {{ $t('config.extra.import') }}
            </a-button>
            <!-- 导出配置按钮 -->
            <a-button type="primary" @click="exportClick">
              {{ $t('config.extra.export') }}
            </a-button>
          </a-col>
        </a-form-item>
      </a-space>
      <!-- 配置列表表格 -->
      <a-table
        v-model:selectedKeys="selectedKeys"
        row-key="id"
        :loading="loading"
        :data="configList"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
        :row-selection="{
          type: 'checkbox',
          showCheckedAll: true,
          onlyCurrent: false,
        }"
      >
        <template #columns>
          <!-- 配置类型列 -->
          <a-table-column
            :title="$t('config.column.type')"
            data-index="configType"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 配置类型的国际化标签 -->
              <a-tag color="orangered">
                {{ transI18nType(record.configType, false) }}
              </a-tag>
            </template>
          </a-table-column>
          <!-- 配置子类型列 -->
          <a-table-column
            :title="$t('config.column.subType')"
            data-index="configSubType"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 配置子类型的国际化标签 -->
              <a-tag color="purple">
                {{ transI18nType(record.configSubType, true) }}
              </a-tag>
            </template>
          </a-table-column>
          <!-- 配置类类型列 -->
          <a-table-column
            :title="$t('config.column.clazz')"
            data-index="configClazz"
            :width="120"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 配置类类型的国际化标签 -->
              <a-tag color="green">
                {{ transI18nClz(record.configClazz) }}
              </a-tag>
            </template>
          </a-table-column>
          <!-- 配置代码列 -->
          <a-table-column
            :title="$t('config.column.code')"
            data-index="configCode"
            :width="200"
            align="center"
          />
          <!-- 配置值列 -->
          <a-table-column
            :title="$t('config.column.value')"
            data-index="configValue"
            :width="100"
            align="center"
          />
          <!-- 配置描述列 -->
          <a-table-column
            :title="$t('config.column.desc')"
            data-index="configDesc"
            :width="400"
            align="center"
          />
          <!-- 操作列 -->
          <a-table-column
            :title="$t('config.column.operate')"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 编辑按钮 -->
              <a-button type="text" size="mini" @click="uptClick(record)">
                {{ $t('button.edit') }}
              </a-button>
            </template>
          </a-table-column>
        </template>
      </a-table>
      <a-pagination
        style="margin-top: 20px"
        :total="total"
        :page-size="condition.pageSize"
        :current="condition.pageNo"
        show-total
        show-jumper
        show-page-size
        :page-size-options="[10, 20, 40, 80, 100]"
        @change="pageChange"
        @page-size-change="pageSizeChange"
      />
      <a-modal
        v-model:visible="editVisible"
        :width="450"
        :title="editTitle"
        draggable
        :ok-text="$t('button.submit')"
        @ok="editOk"
      >
        <a-form :model="editData" :auto-label-width="true">
          <a-form-item
            field="configType"
            :label="$t('config.column.type')"
            :required="true"
            :disabled="editData.id != null && editData.id != 0"
          >
            <a-select v-model="editData.configType">
              <a-option v-for="type in oriTypes" :key="type" :value="type">
                {{ transI18nType(type, false) }}
              </a-option>
            </a-select>
          </a-form-item>
          <a-form-item
            field="configSubType"
            :label="$t('config.column.subType')"
            :required="true"
            :disabled="editData.id != null && editData.id != 0"
          >
            <a-select v-model="editData.configSubType">
              <a-option
                v-for="subType in oriSubTypes"
                :key="subType"
                :value="subType"
              >
                {{ transI18nType(subType, true) }}
              </a-option>
            </a-select>
          </a-form-item>
          <a-form-item
            field="configClazz"
            :label="$t('config.column.clazz')"
            :required="true"
            :disabled="editData.id != null && editData.id != 0"
          >
            <a-select v-model="editData.configClazz">
              <a-option
                v-for="clzType in editData.id != null && editData.id != 0
                  ? clzFull
                  : clzTypes"
                :key="clzType"
                :value="clzType"
              >
                {{ transI18nClz(clzType) }}
              </a-option>
            </a-select>
          </a-form-item>
          <a-form-item
            field="configCode"
            :label="$t('config.column.code')"
            :required="true"
            :disabled="editData.id != null && editData.id != 0"
          >
            <a-input v-model="editData.configCode" :max-length="64" />
          </a-form-item>
          <a-form-item
            field="configValue"
            :label="$t('config.column.value')"
            :required="true"
          >
            <!-- 小数也用字符串输入，避免进行小数点精确 -->
            <a-input
              v-if="getClzType(editData.configClazz) !== 'bool'"
              v-model="editData.configValue"
              :max-length="256"
            />
            <a-switch
              v-if="getClzType(editData.configClazz) === 'bool'"
              v-model="editData.configValue"
              checked-value="true"
              unchecked-value="false"
            />
          </a-form-item>
          <a-form-item field="configDesc" :label="$t('config.column.desc')">
            <a-textarea v-model="editData.configDesc" :max-length="500" />
          </a-form-item>
        </a-form>
      </a-modal>
      <a-modal
        v-model:visible="confirmVisible"
        :width="450"
        draggable
        @ok="confirmOk"
      >
        <template #title>
          {{ $t('button.delete') }}
        </template>
        <div>{{ $t('config.confirm.text') }}</div>
      </a-modal>
      <a-modal
        v-model:visible="importVisible"
        :width="450"
        draggable
        :ok-text="$t('button.upload')"
        @ok="importOk"
      >
        <template #title>
          {{ $t('config.extra.import') }}
        </template>
        <div>
          <p style="color: red; font-size: 16px">
            {{ $t('config.extra.import.warn') }}
          </p>
        </div>
        <a-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="1"
          action="/config/v1/importYml"
          :custom-request="customRequest"
          :file-list="fileList"
          @success="uploadSuccess"
        />
      </a-modal>
    </a-card>
  </div>
</template>

<script setup lang="ts">
  // 导入Vue响应式函数
  import { reactive, ref } from 'vue';
  // 导入配置管理相关API
  import {
    addConfig,
    ConfigResult,
    ConfigSearch,
    deleteConfigList,
    getConfigList,
    getConfigTypeList,
    updateConfig,
    importYml,
    exportYml,
  } from '@/api/config';
  // 导入国际化功能
  import { useI18n } from 'vue-i18n';
  // 导入加载状态钩子
  import useLoading from '@/hooks/loading';
  // 导入Arco Design上传组件相关类型
  import { FileItem, RequestOption } from '@arco-design/web-vue';

  // 获取国际化函数
  const { t } = useI18n();
  // 所有类型列表（包含'全部'选项）
  const types = ref<string[]>([]);
  // 原始类型列表（不含'全部'选项）
  const oriTypes = ref<string[]>([]);
  // 所有子类型列表（包含'全部'选项）
  const subTypes = ref<string[]>([]);
  // 原始子类型列表（不含'全部'选项）
  const oriSubTypes = ref<string[]>([]);
  // 搜索条件
  const condition = ref<ConfigSearch>({
    // 配置类型
    type: 'all',
    // 配置子类型
    subType: 'All',
    // 过滤关键词
    filter: '',
    // 页码
    pageNo: 1,
    // 每页大小
    pageSize: 20,
  });
  // 配置列表数据
  const configList = ref<ConfigResult[]>([]);
  // 总记录数
  const total = ref<number>(0);
  // 加载状态和设置函数
  const { loading, setLoading } = useLoading(false);
  // 已选择的配置项ID列表
  const selectedKeys = ref<number[]>([]);
  // 编辑模态框是否可见
  const editVisible = ref<boolean>(false);
  // 编辑模态框标题
  const editTitle = ref<string>('');
  // 编辑中的配置数据
  const editData = reactive<ConfigResult>({
    // 配置ID
    id: 0,
    // 配置类型
    configType: 'server',
    // 配置子类型
    configSubType: 'Game Mechanics',
    // 配置类类型
    configClazz: 'java.lang.String',
    // 配置代码
    configCode: '',
    // 配置值
    configValue: '',
    // 配置描述
    configDesc: '',
  });
  // 基础类类型列表
  const clzTypes = ref<string[]>([
    'java.lang.Integer',
    'java.lang.String',
    'java.lang.Float',
    'java.lang.Boolean',
  ]);
  // 完整类类型列表（包含高级类型）
  const clzFull = ref<string[]>([
    ...clzTypes.value,
    'java.lang.Long',
    'java.lang.Byte',
    'java.lang.Short',
    'java.lang.Double',
    'java.util.Map',
  ]);
  // 确认删除对话框是否可见
  const confirmVisible = ref<boolean>(false);
  // 导入配置对话框是否可见
  const importVisible = ref<boolean>(false);
  // 上传组件引用
  const uploadRef = ref();
  // 文件列表
  const fileList = ref<FileItem[]>([]);

  /**
   * 加载配置类型列表
   * 获取系统支持的所有配置类型和子类型
   */
  const loadTypes = async () => {
    // 获取配置类型列表
    const { data } = await getConfigTypeList();
    // 设置原始类型列表
    oriTypes.value = data.types;
    // 设置包含'全部'选项的类型列表
    types.value = ['all', ...data.types];
    // 设置原始子类型列表
    oriSubTypes.value = data.subTypes;
    // 设置包含'全部'选项的子类型列表
    subTypes.value = ['All', ...data.subTypes];
  };

  /**
   * 转换国际化类型显示
   * 将类型字符串转换为国际化显示文本
   * @param type - 类型字符串
   * @param isSub - 是否为子类型
   * @returns 国际化后的类型显示文本
   */
  const transI18nType = (type: string, isSub: boolean) => {
    // 移除类型字符串中的空格
    type = type.replace(' ', '');
    let i18nType;
    // 判断是否为子类型
    if (isSub) {
      // 如果是数字类型的世界配置
      if (Number.isFinite(Number(type))) {
        i18nType = t('config.type.world') + type;
      } else {
        // 其他子类型
        i18nType = t(`config.subType.${type}`);
      }
    } else {
      // 普通类型
      i18nType = t(`config.type.${type}`);
    }
    // 如果国际化结果为空则返回原类型
    return i18nType == null ? type : i18nType;
  };

  /**
   * 转换类类型国际化显示
   * 将类类型字符串转换为国际化显示文本
   * @param clz - 类类型字符串
   * @returns 国际化后的类类型显示文本
   */
  const transI18nClz = (clz: string) => {
    // 获取类类型
    const clzType = getClzType(clz);
    // 返回国际化显示文本
    return t(`config.clz.${clzType}`);
  };

  /**
   * 获取类类型
   * 根据完整类名获取简化的类型表示
   * @param clz - 完整类名
   * @returns 简化的类型表示
   */
  const getClzType = (clz: string) => {
    let clzType;
    // 根据类名判断类型
    switch (clz) {
      // 整数类型
      case 'java.lang.Integer':
      case 'java.lang.Long':
      case 'java.lang.Byte':
      case 'java.lang.Short':
        clzType = 'int';
        break;
      // 浮点类型
      case 'java.lang.Float':
      case 'java.lang.Double':
        clzType = 'float';
        break;
      // 布尔类型
      case 'java.lang.Boolean':
        clzType = 'bool';
        break;
      // 默认为字符串类型
      default:
        clzType = 'string';
        break;
    }
    return clzType;
  };

  /**
   * 加载配置列表数据
   * 根据当前筛选条件获取配置列表
   */
  const loadConfigs = async () => {
    setLoading(true);
    try {
      // 构建请求参数
      const param = {
        ...condition.value,
        // 如果类型为'全部'则传空字符串
        type: condition.value.type === 'all' ? '' : condition.value.type,
        // 如果子类型为'全部'则传空字符串
        subType:
          condition.value.subType === 'All' ? '' : condition.value.subType,
      };
      // 获取配置列表数据
      const { data } = await getConfigList(param);
      // 更新配置列表
      configList.value = data.records;
      // 更新总记录数
      total.value = data.totalRow;
      // 清空已选择项
      selectedKeys.value = [];
    } finally {
      setLoading(false);
    }
  };

  /**
   * 页码变更处理函数
   * 当用户切换分页时更新当前页码并重新加载数据
   * @param data - 新的页码
   */
  const pageChange = (data: number) => {
    // 更新当前页码
    condition.value.pageNo = data;
    // 重新加载数据
    loadConfigs();
  };

  /**
   * 每页大小变更处理函数
   * 当用户更改每页显示数量时更新页面大小并重新加载数据
   * @param data - 新的每页大小
   */
  const pageSizeChange = (data: number) => {
    // 重置为第一页
    condition.value.pageNo = 1;
    // 更新每页大小
    condition.value.pageSize = data;
    // 重新加载数据
    loadConfigs();
  };

  /**
   * 搜索数据
   * 根据当前筛选条件重新加载数据
   */
  const searchData = async () => {
    await loadConfigs();
  };

  /**
   * 重置搜索条件
   * 将所有搜索条件恢复为默认值并重新加载数据
   */
  const resetSearch = () => {
    // 重置类型为'全部'
    condition.value.type = 'all';
    // 重置子类型为'全部'
    condition.value.subType = 'All';
    // 清空过滤关键词
    condition.value.filter = '';
    // 重置为第一页
    condition.value.pageNo = 1;
    // 重置每页大小
    condition.value.pageSize = 20;
  };

  /**
   * 类型变更处理函数
   * 当用户更改配置类型时更新筛选条件并重新加载数据
   * @param value - 新的类型值
   */
  const typeChange = async (value: any) => {
    // 更新类型筛选条件
    condition.value.type = String(value);
    // 重新加载数据
    await loadConfigs();
  };

  /**
   * 子类型变更处理函数
   * 当用户更改配置子类型时更新筛选条件并重新加载数据
   * @param value - 新的子类型值
   */
  const subTypeChange = async (value: any) => {
    // 更新子类型筛选条件
    condition.value.subType = String(value);
    // 重新加载数据
    await loadConfigs();
  };

  /**
   * 处理新增按钮点击事件
   * 打开新增配置的编辑对话框
   */
  const addClick = () => {
    // 重置编辑数据
    resetEditData();
    // 显示编辑对话框
    editVisible.value = true;
    // 设置对话框标题
    editTitle.value = t('button.add');
  };

  /**
   * 处理删除按钮点击事件
   * 显示删除确认对话框
   */
  const delClick = async () => {
    // 显示删除确认对话框
    confirmVisible.value = true;
  };

  /**
   * 处理编辑按钮点击事件
   * 打开编辑配置的对话框并填充当前数据
   * @param record - 要编辑的配置记录
   */
  const uptClick = (record: ConfigResult) => {
    // 填充编辑数据
    editData.id = record.id;
    editData.configType = record.configType;
    editData.configSubType = record.configSubType;
    editData.configClazz = record.configClazz;
    editData.configCode = record.configCode;
    editData.configValue = record.configValue;
    editData.configDesc = record.configDesc;
    // 显示编辑对话框
    editVisible.value = true;
    // 设置对话框标题
    editTitle.value = t('button.edit');
  };

  /**
   * 处理编辑对话框确认事件
   * 保存新增或更新的配置数据
   */
  const editOk = async () => {
    // 根据是否有ID判断是新增还是更新
    if (editData.id) {
      // 更新配置
      await updateConfig(editData);
    } else {
      // 新增配置
      await addConfig(editData);
    }
    // 重置编辑数据
    resetEditData();
    // 隐藏编辑对话框
    editVisible.value = false;
    // 重新加载数据
    await loadConfigs();
  };

  /**
   * 重置编辑数据
   * 将编辑数据恢复为默认值
   */
  const resetEditData = () => {
    // 重置ID
    editData.id = 0;
    // 重置配置类型
    editData.configType = 'server';
    // 重置配置子类型
    editData.configSubType = 'Game Mechanics';
    // 重置配置类类型
    editData.configClazz = 'java.lang.String';
    // 重置配置代码
    editData.configCode = '';
    // 重置配置值
    editData.configValue = '';
    // 重置配置描述
    editData.configDesc = '';
  };

  /**
   * 处理删除确认事件
   * 执行批量删除操作并重新加载数据
   */
  const confirmOk = async () => {
    // 执行批量删除
    await deleteConfigList(selectedKeys.value);
    // 重新加载数据
    await loadConfigs();
  };

  /**
   * 处理导入按钮点击事件
   * 显示导入配置对话框
   */
  const importClick = async () => {
    // 显示导入对话框
    importVisible.value = true;
  };

  /**
   * 处理导入确认事件
   * 提交文件上传请求
   */
  const importOk = async () => {
    // 提交文件上传
    uploadRef.value.submit();
  };

  /**
   * 自定义上传请求处理函数
   * 处理YML文件导入请求
   * @param option - 上传选项
   */
  const customRequest = (option: RequestOption) => {
    setLoading(true);
    try {
      // 执行YML导入
      importYml(option);
    } finally {
      setLoading(false);
    }
    return undefined;
  };

  /**
   * 上传成功回调函数
   * 处理文件上传成功后的操作
   */
  const uploadSuccess = async () => {
    // 隐藏导入对话框
    importVisible.value = false;
    // 清空文件列表
    fileList.value = [];
    // 重新加载数据
    await loadConfigs();
  };

  /**
   * 处理导出按钮点击事件
   * 执行配置导出操作
   */
  const exportClick = async () => {
    setLoading(true);
    try {
      // 执行配置导出
      await exportYml();
    } finally {
      setLoading(false);
    }
  };

  // 页面加载时初始化类型列表
  loadTypes();
</script>

<script lang="ts">
  export default {
    name: 'Config',
  };
</script>

<style scoped lang="less">
  :deep(.arco-form-item-content-flex) {
    flex-wrap: wrap;
    align-items: center;
    justify-content: flex-start;
  }
  :deep(.arco-space-horizontal, .arco-col arco-col-24) {
    flex-wrap: wrap;
    align-items: center;
  }
  :deep(.arco-row-align-start > .arco-col) {
    flex-wrap: wrap;
  }
  :deep(.arco-card-body > .arco-space-vertical) {
    width: 100% !important;
  }
  :deep(.arco-space-item .arco-input-wrapper) {
    width: 100% !important;
    max-width: 400px !important;
  }
  :deep(.arco-space-item) {
    width: 100%;
  }
  :deep(.arco-form-item-content > div) {
    width: 100%;
  }
  :deep(.arco-form-item-content > div > *) {
    margin-right: 5px;
    margin-top: 5px;
  }
  :deep(.arco-table-th) {
    min-width: 30px;
  }
  :deep(.arco-table-th:nth-child(4)) {
    min-width: 70px;
  }
  :deep(.arco-table-th:nth-child(5)) {
    min-width: 100px;
  }
  :deep(.arco-table-th:nth-child(6)) {
    min-width: 100px;
  }
  :deep(.arco-table-th:nth-child(7)) {
    min-width: 250px;
  }
</style>