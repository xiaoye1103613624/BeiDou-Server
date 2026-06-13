<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('cdk.title.config')">
      <!-- 搜索工具栏 -->
      <a-row style="margin-bottom: 16px">
        <a-col :span="24">
          <a-space wrap>
            <a-input
              v-model="searchKeyword"
              :placeholder="$t('cdk.column.code')"
              allow-clear
              style="width: 200px"
              @keydown.enter="loadData"
            />
            <a-select
              v-model="searchType"
              :placeholder="$t('cdk.column.type')"
              allow-clear
              style="width: 120px"
            >
              <a-option :value="1">{{ $t('cdk.column.type.1') }}</a-option>
              <a-option :value="2">{{ $t('cdk.column.type.2') }}</a-option>
            </a-select>
            <a-select
              v-model="searchEnabled"
              :placeholder="$t('cdk.column.enabled')"
              allow-clear
              style="width: 120px"
            >
              <a-option :value="1">启用</a-option>
              <a-option :value="0">禁用</a-option>
            </a-select>
            <a-button type="primary" @click="loadData">{{
              $t('cdk.button.search')
            }}</a-button>
            <a-button @click="resetSearch">{{
              $t('cdk.button.reset')
            }}</a-button>
            <a-button type="primary" status="success" @click="createClick">
              {{ $t('cdk.button.create') }}
            </a-button>
            <a-button type="primary" status="warning" @click="batchGenClick">
              {{ $t('cdk.button.batchGen') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>

      <!-- CDK列表表格 -->
      <a-table
        row-key="id"
        :loading="loading"
        :data="tableData"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
        :scroll="{ x: 1600 }"
      >
        <template #columns>
          <a-table-column
            title="ID"
            data-index="id"
            :width="70"
            align="center"
          />
          <a-table-column :title="$t('cdk.column.code')" :width="180">
            <template #cell="{ record }">
              <a-tooltip content="点击复制">
                <span class="code-cell" @click="copyCode(record.code)">
                  {{ record.code }}
                </span>
              </a-tooltip>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('cdk.column.type')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="record.type === 2 ? 'blue' : 'gray'">
                {{
                  record.type === 2
                    ? $t('cdk.column.type.2')
                    : $t('cdk.column.type.1')
                }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('cdk.column.nxCredit')"
            data-index="nxCredit"
            :width="80"
            align="right"
          >
            <template #cell="{ record }">{{ record.nxCredit || 0 }}</template>
          </a-table-column>
          <a-table-column
            :title="$t('cdk.column.nxPrepaid')"
            data-index="nxPrepaid"
            :width="80"
            align="right"
          >
            <template #cell="{ record }">{{ record.nxPrepaid || 0 }}</template>
          </a-table-column>
          <a-table-column
            :title="$t('cdk.column.meso')"
            data-index="meso"
            :width="80"
            align="right"
          >
            <template #cell="{ record }">{{ record.meso || 0 }}</template>
          </a-table-column>
          <a-table-column
            :title="$t('cdk.column.items')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag
                v-if="record.items && record.items.length > 0"
                color="green"
              >
                {{ record.items.length }}
              </a-tag>
              <span v-else>-</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('cdk.column.usedCount')"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              {{ record.usedCount || 0 }}/{{ record.maxUseCount || 1 }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('cdk.column.expireTime')"
            data-index="expireTime"
            :width="150"
          />
          <a-table-column
            :title="$t('cdk.column.enabled')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="record.enabled === 1 ? 'green' : 'red'">
                {{ record.enabled === 1 ? '启用' : '禁用' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('cdk.column.comment')"
            data-index="comment"
            :width="120"
            ellipsis
          />
          <a-table-column
            :title="$t('cdk.column.operation')"
            :width="150"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" size="mini" @click="detailClick(record)"
                  >详情</a-button
                >
                <a-button
                  type="text"
                  size="mini"
                  status="warning"
                  @click="editClick(record)"
                >
                  {{ $t('cdk.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('cdk.delete.confirm')"
                  @ok="deleteClick(record.id)"
                >
                  <a-button type="text" size="mini" status="danger">
                    {{ $t('cdk.button.delete') }}
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 详情抽屉（纯展示，无需确认/取消按钮） -->
    <a-drawer
      v-model:visible="drawerVisible"
      :title="$t('cdk.title.detail')"
      :width="500"
      :footer="false"
      @cancel="drawerVisible = false"
    >
      <template v-if="detailRecord">
        <a-descriptions :column="1" bordered size="small">
          <a-descriptions-item :label="$t('cdk.column.code')">
            <a-tag color="blue">{{ detailRecord.code }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.batchNo')">
            {{ detailRecord.batchNo || '-' }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.type')">
            {{
              detailRecord.type === 2
                ? $t('cdk.column.type.2')
                : $t('cdk.column.type.1')
            }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.nxCredit')">
            {{ detailRecord.nxCredit || 0 }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.nxPrepaid')">
            {{ detailRecord.nxPrepaid || 0 }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.meso')">
            {{ detailRecord.meso || 0 }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.sponsor')">
            {{ detailRecord.sponsor || 0 }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.maxUseCount')">
            {{ detailRecord.maxUseCount || 1 }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.usedCount')">
            {{ detailRecord.usedCount || 0 }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.expireTime')">
            {{ detailRecord.expireTime || '永不过期' }}
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.enabled')">
            <a-tag :color="detailRecord.enabled === 1 ? 'green' : 'red'">
              {{ detailRecord.enabled === 1 ? '启用' : '禁用' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item :label="$t('cdk.column.comment')">
            {{ detailRecord.comment || '-' }}
          </a-descriptions-item>
        </a-descriptions>

        <!-- 道具奖励列表 -->
        <div
          v-if="detailRecord.items && detailRecord.items.length > 0"
          style="margin-top: 20px"
        >
          <h4>{{ $t('cdk.title.items') }}</h4>
          <a-table
            :data="detailRecord.items"
            :pagination="false"
            :bordered="{ cell: true }"
            size="small"
          >
            <template #columns>
              <a-table-column
                :title="$t('cdk.column.itemId')"
                data-index="itemId"
                :width="80"
                align="center"
              />
              <a-table-column
                :title="$t('cdk.column.itemName')"
                data-index="itemName"
              />
              <a-table-column
                :title="$t('cdk.column.quantity')"
                data-index="quantity"
                :width="60"
                align="center"
              />
            </template>
          </a-table>
        </div>
        <div v-else style="margin-top: 20px; color: #999"> 无道具奖励 </div>
      </template>
    </a-drawer>

    <!-- 编辑/创建模态框 -->
    <CdkForm ref="formRef" @load-data="loadData" />

    <!-- 批量生成模态框 -->
    <CdkBatchForm ref="batchFormRef" />
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import useLoading from '@/hooks/loading';
  import { getCdkList, deleteCdkConfig, type CdkConfigForm } from '@/api/cdk';
  import CdkForm from './form.vue';
  import CdkBatchForm from './batchForm.vue';

  const { loading, setLoading } = useLoading(false);

  // 搜索条件
  const searchKeyword = ref('');
  const searchType = ref<number | undefined>();
  const searchEnabled = ref<number | undefined>();

  // 表格数据
  const tableData = ref<CdkConfigForm[]>([]);

  // 详情抽屉
  const drawerVisible = ref(false);
  const detailRecord = ref<CdkConfigForm | null>(null);

  // 表单/批量Ref
  const formRef = ref();
  const batchFormRef = ref();

  /** 加载列表 */
  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getCdkList({
        keyword: searchKeyword.value || undefined,
        type: searchType.value,
        enabled: searchEnabled.value,
      });
      tableData.value = data || [];
    } catch {
      // interceptor handles error
    } finally {
      setLoading(false);
    }
  };
  loadData();

  /** 重置搜索 */
  const resetSearch = () => {
    searchKeyword.value = '';
    searchType.value = undefined;
    searchEnabled.value = undefined;
    loadData();
  };

  /** 创建CDK */
  const createClick = () => {
    formRef.value?.initForm(null);
  };

  /** 编辑CDK */
  const editClick = (record: CdkConfigForm) => {
    formRef.value?.initForm(record);
  };

  /** 点击兑换码自动复制到剪贴板 */
  const copyCode = (code: string) => {
    navigator.clipboard.writeText(code).then(() => {
      Message.success(`兑换码已复制: ${code}`);
    });
  };

  /** 查看详情（抽屉） */
  const detailClick = (record: CdkConfigForm) => {
    detailRecord.value = record;
    drawerVisible.value = true;
  };

  /** 删除CDK */
  const deleteClick = async (id: number) => {
    try {
      await deleteCdkConfig(id);
      Message.success('CDK已删除');
      loadData();
    } catch {
      // interceptor handles error
    }
  };

  /** 批量生成 */
  const batchGenClick = () => {
    batchFormRef.value?.initForm();
  };
</script>

<script lang="ts">
  export default { name: 'CdkIndex' };
</script>

<style scoped>
  .code-cell {
    cursor: pointer;
    color: #165dff;
    font-family: monospace;
    transition: color 0.2s;
  }
  .code-cell:hover {
    color: #4080ff;
    text-decoration: underline;
  }
</style>
