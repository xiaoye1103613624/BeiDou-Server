<script lang="ts" setup>
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    deleteXyCollectionConfig,
    getXyCollectionList,
    ItemSearchResult,
    saveXyCollectionConfig,
    searchItems,
    XyCollectionItem,
    XyCollectionStage,
    XyCollectionType,
  } from '@/api/xyCollection';
  import { getIconUrl } from '@/utils/mapleStoryAPI';
  import { Message } from '@arco-design/web-vue';

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<XyCollectionType[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getXyCollectionList();
      tableData.value = data;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  // ========== 编辑抽屉 ==========
  const drawerVisible = ref(false);
  const editId = ref<number | undefined>();

  const emptyStage = (): XyCollectionStage => ({
    stageName: '',
    sortOrder: 0,
    rewardType: '',
    rewardAmount: 0,
    items: [],
  });

  const emptyItem = (): XyCollectionItem => ({
    itemId: 0,
    quantity: 1,
    sortOrder: 0,
  });

  const editConfig = ref<XyCollectionType>({
    typeName: '',
    description: '',
    sortOrder: 0,
    enabled: 1,
    rewardType: '',
    rewardAmount: 0,
    stages: [],
  });
  const itemOptions = ref<ItemSearchResult[][][]>([]);
  const insertClick = () => {
    editId.value = undefined;
    editConfig.value = {
      typeName: '',
      description: '',
      sortOrder: 0,
      enabled: 1,
      rewardType: '',
      rewardAmount: 0,
      stages: [emptyStage()],
    };
    itemOptions.value = [];
    drawerVisible.value = true;
  };

  const editClick = (record: XyCollectionType) => {
    editId.value = record.id;
    editConfig.value = JSON.parse(JSON.stringify(record));
    if (!editConfig.value.stages || editConfig.value.stages.length === 0) {
      editConfig.value.stages = [emptyStage()];
    }
    editConfig.value.stages.forEach((stage) => {
      if (!stage.items) stage.items = [];
    });
    itemOptions.value = [];
    drawerVisible.value = true;
  };

  const addStage = () => {
    editConfig.value.stages.push(emptyStage());
  };

  const removeStage = (idx: number) => {
    editConfig.value.stages.splice(idx, 1);
  };

  const addItem = (stageIdx: number) => {
    editConfig.value.stages[stageIdx].items.push(emptyItem());
  };

  // ========== 物品搜索 ==========

  const handleItemSearch = async (keyword: string, si: number, ii: number) => {
    if (!keyword || keyword.length < 1) return;
    try {
      const { data } = await searchItems(keyword, 15);
      if (!itemOptions.value[si]) itemOptions.value[si] = [];
      itemOptions.value[si][ii] = data;
    } catch {
      // ignore search errors
    }
  };

  const onItemSelect = (itemId: number, si: number, ii: number) => {
    editConfig.value.stages[si].items[ii].itemId = itemId;
  };

  const onItemClear = (si: number, ii: number) => {
    editConfig.value.stages[si].items[ii].itemId = 0;
  };

  const submitForm = async () => {
    try {
      await saveXyCollectionConfig(editConfig.value);
      Message.success('保存成功');
      drawerVisible.value = false;
      loadData();
    } catch (e: any) {
      Message.error(e?.message || '保存失败');
    }
  };

  const deleteClick = async (record: XyCollectionType) => {
    try {
      if (!record.id) return;
      await deleteXyCollectionConfig(record.id);
      Message.success('删除成功');
      loadData();
    } catch (e: any) {
      Message.error(e?.message || '删除失败');
    }
  };

  const onImgError = (e: Event) => {
    (e.target as HTMLImageElement).style.display = 'none';
  };
</script>

<template>
  <div class="container">
    <Breadcrumb />
    <a-card
      class="general-card"
      :title="$t('menu.game.xyCollection')"
      style="overflow-x: auto"
    >
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
          <a-table-column
            :title="$t('xyCollection.list.column.typeName')"
            data-index="typeName"
            :width="150"
            align="center"
          />
          <a-table-column
            :title="$t('xyCollection.list.column.stages')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              {{ record.stages?.length || 0 }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('xyCollection.list.column.sortOrder')"
            data-index="sortOrder"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('xyCollection.list.column.enabled')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green" size="small">
                {{ $t('xyCollection.enabled.true') }}
              </a-tag>
              <a-tag v-else color="red" size="small">
                {{ $t('xyCollection.enabled.false') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('xyCollection.list.column.reward')"
            :width="150"
            align="center"
          >
            <template #cell="{ record }">
              <span v-if="record.rewardType">
                {{ $t('xyCollection.rewardType.' + record.rewardType) }} x{{
                  record.rewardAmount
                }}
              </span>
              <span v-else>-</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('xyCollection.list.column.operations')"
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
                  :content="$t('xyCollection.message.deleteTips')"
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

    <a-drawer
      v-model:visible="drawerVisible"
      :title="
        editId
          ? $t('xyCollection.form.title.update')
          : $t('xyCollection.form.title.create')
      "
      :width="800"
      @cancel="drawerVisible = false"
    >
      <a-form ref="formRef" :model="editConfig" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('xyCollection.form.field.typeName')">
              <a-input
                v-model="editConfig.typeName"
                :placeholder="$t('xyCollection.form.field.typeName')"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('xyCollection.form.field.description')">
              <a-input
                v-model="editConfig.description"
                :placeholder="$t('xyCollection.form.field.description')"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('xyCollection.form.field.sortOrder')">
              <a-input-number
                v-model="editConfig.sortOrder"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('xyCollection.form.field.enabled')">
              <a-switch
                :model-value="editConfig.enabled === 1"
                @change="(v: boolean) => editConfig.enabled = v ? 1 : 0"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8" />
        </a-row>

        <a-divider>{{ $t('xyCollection.form.field.typeReward') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item :label="$t('xyCollection.form.field.rewardType')">
              <a-select
                v-model="editConfig.rewardType"
                :placeholder="$t('xyCollection.form.field.rewardType')"
                allow-clear
              >
                <a-option value="CASH">{{
                  $t('xyCollection.rewardType.CASH')
                }}</a-option>
                <a-option value="MAPLE_POINT">{{
                  $t('xyCollection.rewardType.MAPLE_POINT')
                }}</a-option>
                <a-option value="MESO">{{
                  $t('xyCollection.rewardType.MESO')
                }}</a-option>
                <a-option value="AP">{{
                  $t('xyCollection.rewardType.AP')
                }}</a-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :label="$t('xyCollection.form.field.rewardAmount')">
              <a-input-number
                v-model="editConfig.rewardAmount"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider>{{ $t('xyCollection.form.field.stages') }}</a-divider>

        <div
          v-for="(stage, si) in editConfig.stages"
          :key="si"
          class="stage-card"
        >
          <a-row :gutter="16" align="center">
            <a-col :span="6">
              <a-form-item hide-label>
                <a-input
                  v-model="stage.stageName"
                  :placeholder="$t('xyCollection.form.field.stageName')"
                />
              </a-form-item>
            </a-col>
            <a-col :span="3">
              <a-form-item hide-label>
                <a-input-number
                  v-model="stage.sortOrder"
                  :min="0"
                  :placeholder="$t('xyCollection.form.field.stageSort')"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="5">
              <a-form-item hide-label>
                <a-select
                  v-model="stage.rewardType"
                  :placeholder="$t('xyCollection.form.field.rewardType')"
                  allow-clear
                >
                  <a-option value="CASH">{{
                    $t('xyCollection.rewardType.CASH')
                  }}</a-option>
                  <a-option value="MAPLE_POINT">{{
                    $t('xyCollection.rewardType.MAPLE_POINT')
                  }}</a-option>
                  <a-option value="MESO">{{
                    $t('xyCollection.rewardType.MESO')
                  }}</a-option>
                  <a-option value="AP">{{
                    $t('xyCollection.rewardType.AP')
                  }}</a-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="5">
              <a-form-item hide-label>
                <a-input-number
                  v-model="stage.rewardAmount"
                  :min="0"
                  :placeholder="$t('xyCollection.form.field.rewardAmount')"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="3">
              <span style="color: #999; font-size: 12px">{{
                $t('xyCollection.form.field.stageReward')
              }}</span>
            </a-col>
            <a-col :span="2">
              <a-button size="mini" status="danger" @click="removeStage(si)"
                >X</a-button
              >
            </a-col>
          </a-row>
          <!-- 阶段物品列表 -->
          <div style="margin-left: 20px; margin-bottom: 8px; margin-top: 4px">
            <div
              v-for="(item, ii) in stage.items"
              :key="ii"
              style="margin-bottom: 4px"
            >
              <a-space size="mini">
                <a-select
                  :model-value="item.itemId || undefined"
                  allow-search
                  allow-clear
                  :placeholder="$t('xyCollection.form.searchItemPlaceholder')"
                  style="width: 260px"
                  :filter-option="false"
                  @search="(val: string) => handleItemSearch(val, si, ii)"
                  @change="(val: number) => onItemSelect(val, si, ii)"
                  @clear="onItemClear(si, ii)"
                >
                  <a-option
                    v-for="opt in itemOptions[si]?.[ii] || []"
                    :key="opt.itemId"
                    :value="opt.itemId"
                    :label="`[${opt.itemId}] ${opt.itemName}`"
                  >
                    <div style="display: flex; align-items: center; gap: 6px">
                      <img
                        :src="getIconUrl('item', opt.itemId)"
                        style="width: 24px; height: 24px"
                        @error="onImgError($event)"
                      />
                      <span>[{{ opt.itemId }}] {{ opt.itemName }}</span>
                    </div>
                  </a-option>
                </a-select>
                <span>x</span>
                <a-input-number
                  v-model="item.quantity"
                  :min="1"
                  style="width: 80px"
                  size="mini"
                />
                <a-button
                  size="mini"
                  type="text"
                  status="danger"
                  @click="stage.items.splice(ii, 1)"
                  >X</a-button
                >
              </a-space>
            </div>
            <a-button size="mini" type="outline" @click="addItem(si)">
              + {{ $t('xyCollection.form.addItem') }}
            </a-button>
          </div>
        </div>
        <a-button type="outline" long @click="addStage">
          + {{ $t('xyCollection.form.addStage') }}
        </a-button>
      </a-form>
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
  </div>
</template>

<style scoped>
  .stage-card {
    background: var(--color-fill-1);
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 8px;
  }
</style>
