<!-- 装备进阶路线配置管理页面 -->
<template>
  <div class="container">
    <Breadcrumb />
    <a-card
      class="general-card"
      :title="$t('menu.game.equipAdvance')"
      style="overflow-x: auto"
    >
      <!-- 操作按钮区域 -->
      <a-row>
        <a-col>
          <a-space>
            <a-button type="primary" @click="loadData">
              {{ $t('button.search') }}
            </a-button>
            <a-button type="primary" status="success" @click="insertClick">
              {{ $t('button.create') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <!-- 进阶路线列表表格 -->
      <a-table
        row-key="id"
        :loading="loading"
        :data="tableData"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
        style="margin-top: 16px"
      >
        <template #columns>
          <a-table-column
            title="ID"
            data-index="id"
            :width="55"
            align="center"
          />
          <!-- 职业群列 -->
          <a-table-column
            :title="$t('equipAdvance.list.column.jobGroup')"
            data-index="jobGroup"
            :width="90"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag color="arcoblue" size="small">
                {{ $t('equipAdvance.jobGroup.' + record.jobGroup) }}
              </a-tag>
            </template>
          </a-table-column>
          <!-- 路线名称列 -->
          <a-table-column
            :title="$t('equipAdvance.list.column.routeName')"
            data-index="routeName"
            :width="130"
            align="center"
          />
          <!-- 阶段数列 -->
          <a-table-column
            :title="$t('equipAdvance.list.column.stageCount')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              {{ record.stages ? record.stages.length : 0 }}
            </template>
          </a-table-column>
          <!-- 启用状态列 -->
          <a-table-column
            :title="$t('equipAdvance.list.column.enabled')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green" size="small">
                {{ $t('equipAdvance.enabled.true') }}
              </a-tag>
              <a-tag v-else color="red" size="small">
                {{ $t('equipAdvance.enabled.false') }}
              </a-tag>
            </template>
          </a-table-column>
          <!-- 操作列 -->
          <a-table-column
            :title="$t('equipAdvance.list.column.operations')"
            :width="150"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button size="mini" type="text" @click="editClick(record)">
                  {{ $t('button.edit') }}
                </a-button>
                <a-popconfirm
                  type="error"
                  :content="$t('equipAdvance.message.deleteTips')"
                  position="left"
                  @ok="deleteClick(record)"
                >
                  <a-button size="mini" status="danger" type="text">
                    {{ $t('button.delete') }}
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 编辑抽屉 -->
    <a-drawer
      v-model:visible="drawerVisible"
      :title="
        editId
          ? $t('equipAdvance.form.title.update')
          : $t('equipAdvance.form.title.create')
      "
      :width="800"
      @cancel="drawerVisible = false"
    >
      <a-form ref="formRef" :model="editRoute" layout="vertical">
        <!-- 基本信息 -->
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('equipAdvance.form.field.jobGroup')">
              <a-select v-model="editRoute.jobGroup" style="width: 100%">
                <a-option v-for="job in jobGroups" :key="job" :value="job">
                  {{ $t('equipAdvance.jobGroup.' + job) }}
                </a-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="10">
            <a-form-item :label="$t('equipAdvance.form.field.routeName')">
              <a-input v-model="editRoute.routeName" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('equipAdvance.form.field.enabled')">
              <a-switch
                :model-value="editRoute.enabled === 1"
                @change="(v: boolean) => (editRoute.enabled = v ? 1 : 0)"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 阶段配置 -->
        <a-divider>{{ $t('equipAdvance.form.field.stages') }}</a-divider>

        <div
          v-for="(st, idx) in editRoute.stages"
          :key="idx"
          class="stage-card"
        >
          <!-- 阶段头部 -->
          <div class="stage-header">
            <span class="stage-title">{{
              st.stageOrder === 0
                ? $t('equipAdvance.stage.initial')
                : $t('equipAdvance.form.field.stageOrder') + ' ' + st.stageOrder
            }}</span>
            <a-button
              size="mini"
              status="danger"
              type="text"
              @click="removeStage(idx)"
            >
              {{ $t('equipAdvance.form.removeStage') }}
            </a-button>
          </div>

          <!-- 目标装备 -->
          <a-row :gutter="12">
            <a-col :span="8">
              <a-form-item :label="$t('equipAdvance.form.field.targetItemId')">
                <a-input-number
                  v-model="st.targetItemId"
                  :min="0"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="10">
              <a-form-item
                :label="$t('equipAdvance.form.field.targetItemName')"
              >
                <a-input v-model="st.targetItemName" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-button
                size="small"
                type="text"
                style="margin-top: 30px"
                @click="openItemSearch(idx, st)"
              >
                <template #icon><icon-search /></template>
                {{ $t('equipAdvance.form.searchItem') }}
              </a-button>
            </a-col>
          </a-row>

          <!-- 货币消耗（仅非初始阶段显示） -->
          <a-row v-if="st.stageOrder > 0" :gutter="12">
            <a-col :span="8">
              <a-form-item :label="$t('equipAdvance.form.field.mesoCost')">
                <a-input-number
                  v-model="st.mesoCost"
                  :min="0"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :label="$t('equipAdvance.form.field.cashCost')">
                <a-input-number
                  v-model="st.cashCost"
                  :min="0"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :label="$t('equipAdvance.form.field.creditCost')">
                <a-input-number
                  v-model="st.creditCost"
                  :min="0"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <!-- 属性加成（仅非初始阶段显示） -->
          <template v-if="st.stageOrder > 0">
            <div class="stat-section">
              <span class="stat-group-label">{{
                $t('equipAdvance.form.field.statMain')
              }}</span>
              <a-space size="mini" wrap>
                <a-tooltip :content="$t('equipAdvance.form.field.strAdd')">
                  <a-input-number
                    v-model="st.strAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.strAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
                <a-tooltip :content="$t('equipAdvance.form.field.dexAdd')">
                  <a-input-number
                    v-model="st.dexAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.dexAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
                <a-tooltip :content="$t('equipAdvance.form.field.intAdd')">
                  <a-input-number
                    v-model="st.intAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.intAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
                <a-tooltip :content="$t('equipAdvance.form.field.lukAdd')">
                  <a-input-number
                    v-model="st.lukAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.lukAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
              </a-space>
            </div>
            <div class="stat-section">
              <span class="stat-group-label">{{
                $t('equipAdvance.form.field.statAttack')
              }}</span>
              <a-space size="mini" wrap>
                <a-tooltip :content="$t('equipAdvance.form.field.watkAdd')">
                  <a-input-number
                    v-model="st.watkAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.watkAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
                <a-tooltip :content="$t('equipAdvance.form.field.matkAdd')">
                  <a-input-number
                    v-model="st.matkAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.matkAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
              </a-space>
            </div>
            <div class="stat-section">
              <span class="stat-group-label">{{
                $t('equipAdvance.form.field.statDefense')
              }}</span>
              <a-space size="mini" wrap>
                <a-tooltip :content="$t('equipAdvance.form.field.wdefAdd')">
                  <a-input-number
                    v-model="st.wdefAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.wdefAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
                <a-tooltip :content="$t('equipAdvance.form.field.mdefAdd')">
                  <a-input-number
                    v-model="st.mdefAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.mdefAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
              </a-space>
            </div>
            <div class="stat-section">
              <span class="stat-group-label">{{
                $t('equipAdvance.form.field.statOther')
              }}</span>
              <a-space size="mini" wrap>
                <a-tooltip :content="$t('equipAdvance.form.field.hpAdd')">
                  <a-input-number
                    v-model="st.hpAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.hpAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
                <a-tooltip :content="$t('equipAdvance.form.field.mpAdd')">
                  <a-input-number
                    v-model="st.mpAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.mpAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
                <a-tooltip :content="$t('equipAdvance.form.field.accAdd')">
                  <a-input-number
                    v-model="st.accAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.accAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
                <a-tooltip :content="$t('equipAdvance.form.field.avoidAdd')">
                  <a-input-number
                    v-model="st.avoidAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.avoidAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
                <a-tooltip :content="$t('equipAdvance.form.field.speedAdd')">
                  <a-input-number
                    v-model="st.speedAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.speedAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
                <a-tooltip :content="$t('equipAdvance.form.field.jumpAdd')">
                  <a-input-number
                    v-model="st.jumpAdd"
                    :min="0"
                    :placeholder="$t('equipAdvance.form.field.jumpAdd')"
                    style="width: 78px"
                    size="mini"
                  />
                </a-tooltip>
              </a-space>
            </div>
          </template>

          <!-- 消耗材料（仅非初始阶段显示） -->
          <div v-if="st.stageOrder > 0" class="cost-section">
            <span class="stat-group-label">{{
              $t('equipAdvance.form.field.costs')
            }}</span>
            <a-space direction="vertical" :size="4" style="width: 100%">
              <div v-for="(co, ci) in st.costs" :key="ci">
                <a-space size="mini">
                  <a-input-number
                    v-model="co.itemId"
                    :min="1"
                    :placeholder="$t('equipAdvance.form.field.costItemId')"
                    style="width: 120px"
                    size="mini"
                  />
                  <a-button
                    size="mini"
                    type="text"
                    @click="openCostItemSearch(idx, ci, st)"
                  >
                    <template #icon><icon-search /></template>
                  </a-button>
                  <span style="color: #999">×</span>
                  <a-input-number
                    v-model="co.count"
                    :min="1"
                    :placeholder="$t('equipAdvance.form.field.costCount')"
                    style="width: 80px"
                    size="mini"
                  />
                  <a-button
                    size="mini"
                    type="text"
                    status="danger"
                    @click="st.costs.splice(ci, 1)"
                  >
                    {{ $t('equipAdvance.form.removeCost') }}
                  </a-button>
                </a-space>
              </div>
            </a-space>
            <a-button
              size="mini"
              type="outline"
              style="margin-top: 6px"
              @click="st.costs.push({ itemId: 0, count: 1 })"
            >
              + {{ $t('equipAdvance.form.addCost') }}
            </a-button>
          </div>
        </div>

        <!-- 添加阶段按钮 -->
        <a-button type="outline" long @click="addStage">
          + {{ $t('equipAdvance.form.addStage') }}
        </a-button>
      </a-form>

      <!-- 抽屉底部按钮 -->
      <template #footer>
        <a-space>
          <a-button @click="drawerVisible = false">{{
            $t('button.cancel')
          }}</a-button>
          <a-button type="primary" @click="submitForm">{{
            $t('button.save')
          }}</a-button>
        </a-space>
      </template>
    </a-drawer>

    <!-- 物品搜索弹窗 -->
    <a-modal
      v-model:visible="itemSearchVisible"
      :title="$t('equipAdvance.form.searchItem')"
      :width="500"
      :footer="false"
      @cancel="itemSearchVisible = false"
    >
      <a-input-search
        v-model="itemSearchKeyword"
        :placeholder="$t('equipAdvance.form.searchItemPlaceholder')"
        search-button
        style="margin-bottom: 12px"
        @search="doItemSearch"
      />
      <a-table
        v-if="itemSearchResults.length > 0"
        :data="itemSearchResults"
        :pagination="false"
        :bordered="{ cell: true }"
        size="small"
        style="max-height: 360px; overflow-y: auto"
        row-key="itemId"
      >
        <template #columns>
          <a-table-column
            title="ID"
            data-index="itemId"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('equipAdvance.form.itemName')"
            data-index="itemName"
            align="center"
          >
            <template #cell="{ record }">
              <a-button type="text" size="mini" @click="selectItem(record)">
                {{ record.itemName }}
              </a-button>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    deleteEquipAdvanceRoute,
    EquipAdvanceRoute,
    EquipAdvanceStage,
    getEquipAdvanceList,
    saveEquipAdvanceRoute,
  } from '@/api/equipAdvance';
  import { Message } from '@arco-design/web-vue';
  import { IconSearch } from '@arco-design/web-vue/es/icon';
  import { useI18n } from 'vue-i18n';
  import { itemSearch, ItemSearchResult } from '@/api/item';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);
  const tableData = ref<EquipAdvanceRoute[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getEquipAdvanceList();
      tableData.value = data;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  // ========== 编辑抽屉 ==========
  const drawerVisible = ref(false);
  const editId = ref<number | undefined>();

  /** 职业群列表 */
  const jobGroups = ['warrior', 'archer', 'mage', 'thief', 'pirate'];

  /**
   * 创建空阶段配置
   * @param order - 阶段顺序（0=初始装备）
   */
  const emptyStage = (order: number): EquipAdvanceStage => ({
    stageOrder: order,
    targetItemId: 0,
    targetItemName: '',
    mesoCost: 0,
    cashCost: 0,
    creditCost: 0,
    strAdd: 0,
    dexAdd: 0,
    intAdd: 0,
    lukAdd: 0,
    hpAdd: 0,
    mpAdd: 0,
    watkAdd: 0,
    matkAdd: 0,
    wdefAdd: 0,
    mdefAdd: 0,
    accAdd: 0,
    avoidAdd: 0,
    speedAdd: 0,
    jumpAdd: 0,
    costs: [],
  });

  const editRoute = ref<EquipAdvanceRoute>({
    jobGroup: 'warrior',
    routeName: '',
    enabled: 1,
    stages: [],
  });

  const insertClick = () => {
    editId.value = undefined;
    editRoute.value = {
      jobGroup: 'warrior',
      routeName: '',
      enabled: 1,
      stages: [emptyStage(0)],
    };
    drawerVisible.value = true;
  };

  const editClick = (record: EquipAdvanceRoute) => {
    editId.value = record.id;
    editRoute.value = JSON.parse(JSON.stringify(record));
    if (!editRoute.value.stages || editRoute.value.stages.length === 0) {
      editRoute.value.stages = [emptyStage(0)];
    }
    editRoute.value.stages.forEach((st) => {
      if (!st.costs) st.costs = [];
    });
    drawerVisible.value = true;
  };

  const addStage = () => {
    const nextOrder =
      editRoute.value.stages.length > 0
        ? Math.max(...editRoute.value.stages.map((s) => s.stageOrder)) + 1
        : 1;
    editRoute.value.stages.push(emptyStage(nextOrder));
  };

  const removeStage = (idx: number) => {
    editRoute.value.stages.splice(idx, 1);
    // 重新编号阶段顺序
    editRoute.value.stages.forEach((st, i) => {
      st.stageOrder = i;
    });
  };

  const submitForm = async () => {
    try {
      // 确保初始阶段 stageOrder = 0，后续阶段从 1 开始连续编号
      editRoute.value.stages.forEach((st, i) => {
        st.stageOrder = i;
      });
      await saveEquipAdvanceRoute(editRoute.value);
      Message.success(t('equipAdvance.message.saveSuccess'));
      drawerVisible.value = false;
      loadData();
    } catch (e: any) {
      Message.error(e?.message || t('equipAdvance.message.saveFailed'));
    }
  };

  const deleteClick = async (record: EquipAdvanceRoute) => {
    try {
      if (record.id == null) {
        Message.error(t('equipAdvance.message.recordIdRequired'));
        return;
      }
      await deleteEquipAdvanceRoute(record.id);
      Message.success(t('equipAdvance.message.deleteSuccess'));
      loadData();
    } catch (e: any) {
      Message.error(e?.message || t('equipAdvance.message.deleteFailed'));
    }
  };

  // ========== 物品搜索 ==========
  const itemSearchVisible = ref(false);
  const itemSearchKeyword = ref('');
  const itemSearchResults = ref<ItemSearchResult[]>([]);
  // 当前编辑引用：可能是目标装备搜索或消耗材料搜索
  let currentItemRef: {
    stageIdx: number;
    costIdx?: number;
    stage: EquipAdvanceStage;
  } | null = null;

  /** 打开目标装备搜索 */
  const openItemSearch = (stageIdx: number, stage: EquipAdvanceStage) => {
    currentItemRef = { stageIdx, stage };
    itemSearchKeyword.value = '';
    itemSearchResults.value = [];
    itemSearchVisible.value = true;
  };

  /** 打开消耗材料物品搜索 */
  const openCostItemSearch = (
    stageIdx: number,
    costIdx: number,
    stage: EquipAdvanceStage
  ) => {
    currentItemRef = { stageIdx, costIdx, stage };
    itemSearchKeyword.value = '';
    itemSearchResults.value = [];
    itemSearchVisible.value = true;
  };

  const doItemSearch = async () => {
    if (!itemSearchKeyword.value) return;
    try {
      const { data } = await itemSearch(itemSearchKeyword.value, 20);
      itemSearchResults.value = data;
    } catch {
      itemSearchResults.value = [];
    }
  };

  /** 选择搜索结果中的物品 */
  const selectItem = (record: ItemSearchResult) => {
    if (currentItemRef) {
      if (currentItemRef.costIdx !== undefined) {
        // 回填到消耗材料
        currentItemRef.stage.costs[currentItemRef.costIdx].itemId =
          record.itemId;
      } else {
        // 回填到目标装备
        currentItemRef.stage.targetItemId = record.itemId;
        currentItemRef.stage.targetItemName = record.itemName;
      }
    }
    itemSearchVisible.value = false;
  };
</script>

<style scoped>
  .stage-card {
    background: var(--color-fill-1);
    border-radius: 8px;
    padding: 12px 16px;
    margin-bottom: 12px;
  }

  .stage-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
  }

  .stage-title {
    font-weight: 600;
    font-size: 14px;
    color: rgb(var(--primary-6));
  }

  .stat-section {
    margin-bottom: 8px;
  }

  .stat-group-label {
    display: inline-block;
    width: 56px;
    font-size: 12px;
    color: var(--color-text-3);
    margin-right: 8px;
    vertical-align: middle;
    line-height: 24px;
  }

  .cost-section {
    margin-top: 8px;
    padding-top: 8px;
    border-top: 1px dashed var(--color-border-2);
  }
</style>
