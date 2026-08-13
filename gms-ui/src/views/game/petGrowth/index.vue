<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.petGrowth')">
      <a-alert type="info" style="margin-bottom: 12px">
        {{ $t('petGrowth.tip.safe') }}
      </a-alert>
      <a-row>
        <a-col>
          <a-space>
            <a-button type="primary" status="success" @click="addClick">
              {{ $t('button.create') }}
            </a-button>
            <a-button @click="loadData">{{ $t('button.search') }}</a-button>
            <a-button type="outline" @click="openPreview">
              {{ $t('petGrowth.button.preview') }}
            </a-button>
            <a-button type="outline" @click="onReload">
              {{ $t('petGrowth.button.reload') }}
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
            :title="$t('petGrowth.column.id')"
            data-index="id"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('petGrowth.column.icon')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <img
                v-if="record.petId"
                :src="getIconUrl('item', record.petId)"
                style="width: 32px; height: 32px"
                @error="onItemIconError"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('petGrowth.column.chainCode')"
            data-index="chainCode"
            :width="130"
            align="center"
          />
          <a-table-column
            :title="$t('petGrowth.column.stage')"
            data-index="stage"
            :width="70"
            align="center"
          />
          <a-table-column
            :title="$t('petGrowth.column.name')"
            data-index="name"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('petGrowth.column.petId')"
            data-index="petId"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('petGrowth.column.nextPetId')"
            data-index="nextPetId"
            :width="110"
            align="center"
          />
          <a-table-column
            :title="$t('petGrowth.column.needExp')"
            data-index="needExp"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('petGrowth.column.expRate')"
            data-index="expRate"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('petGrowth.column.dropRate')"
            data-index="dropRate"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('petGrowth.column.mesoRate')"
            data-index="mesoRate"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('petGrowth.column.wz')"
            :width="90"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="record.petExists ? 'green' : 'red'">
                {{
                  record.petExists
                    ? $t('petGrowth.wz.ok')
                    : $t('petGrowth.wz.missing')
                }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('petGrowth.column.enabled')"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              <a-switch
                :model-value="record.enabled === 1"
                @change="(v) => onToggleEnabled(record, v as boolean)"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('petGrowth.column.operation')"
            :width="160"
            fixed="right"
            align="center"
          >
            <template #cell="{ record }">
              <a-space :size="0">
                <a-button type="text" size="mini" @click="editClick(record)">
                  {{ $t('petGrowth.button.edit') }}
                </a-button>
                <a-popconfirm
                  :content="$t('petGrowth.delete.confirm')"
                  @ok="deleteClick(record.id)"
                >
                  <a-button type="text" size="mini" status="danger">
                    {{ $t('button.delete') }}
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="modalVisible"
      :title="modalTitle"
      :width="720"
      @ok="saveClick"
      @cancel="onCancel"
    >
      <a-form :model="form" layout="vertical">
        <a-divider>{{ $t('petGrowth.title.basic') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.chainCode')">
              <a-input v-model="form.chainCode" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.stage')">
              <a-input-number
                v-model="form.stage"
                :min="1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.name')">
              <a-input v-model="form.name" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.petId')">
              <a-input-number v-model="form.petId" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.nextPetId')">
              <a-input-number v-model="form.nextPetId" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.sortOrder')">
              <a-input-number
                v-model="form.sortOrder"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.needExp')">
              <a-input-number
                v-model="form.needExp"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.expPerFeed')">
              <a-input-number
                v-model="form.expPerFeed"
                :min="1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.enabled')">
              <a-switch v-model="enabledBool" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('petGrowth.column.feedItemIds')">
          <a-input
            v-model="form.feedItemIds"
            :placeholder="$t('petGrowth.tip.feed')"
          />
        </a-form-item>
        <a-divider>{{ $t('petGrowth.title.rates') }}</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.expRate')">
              <a-input-number
                v-model="form.expRate"
                :min="0.1"
                :step="0.01"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.dropRate')">
              <a-input-number
                v-model="form.dropRate"
                :min="0.1"
                :step="0.01"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('petGrowth.column.mesoRate')">
              <a-input-number
                v-model="form.mesoRate"
                :min="0.1"
                :step="0.01"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <a-modal
      v-model:visible="previewVisible"
      :title="$t('petGrowth.preview.title')"
      :width="900"
      :footer="false"
    >
      <a-spin :loading="previewLoading" style="width: 100%">
        <div v-for="chain in previewData" :key="chain.chainCode" class="chain">
          <a-space style="margin-bottom: 8px">
            <a-tag :color="chain.safe ? 'green' : 'orangered'">
              {{ chain.chainCode }}
            </a-tag>
            <span v-if="chain.warning" style="color: #cf1322">{{
              chain.warning
            }}</span>
          </a-space>
          <a-space wrap>
            <template v-for="(st, idx) in chain.stages" :key="st.id">
              <a-card size="small" style="width: 180px; text-align: center">
                <img
                  v-if="st.petId"
                  :src="getIconUrl('item', st.petId)"
                  style="width: 40px; height: 40px"
                  @error="onItemIconError"
                />
                <div>{{ st.name }} (L{{ st.stage }})</div>
                <div style="font-size: 12px; color: #86909c">
                  {{ st.petId }}
                  →
                  {{ st.nextPetId || '-' }}
                </div>
                <div style="font-size: 12px">
                  EXP {{ st.expRate }} / Drop {{ st.dropRate }} / Meso
                  {{ st.mesoRate }}
                </div>
              </a-card>
              <span
                v-if="idx < chain.stages.length - 1"
                style="font-size: 20px; padding: 0 4px"
                >→</span
              >
            </template>
          </a-space>
          <a-divider />
        </div>
      </a-spin>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { Message } from '@arco-design/web-vue';
  import useLoading from '@/hooks/loading';
  import { getIconUrl, onItemIconError } from '@/utils/mapleStoryAPI';
  import {
    deleteStage,
    getPreview,
    getStage,
    getStageList,
    PetGrowthPreview,
    PetGrowthStageForm,
    reloadCache,
    saveStage,
    toggleEnabled,
  } from '@/api/petGrowth';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);
  const tableData = ref<PetGrowthStageForm[]>([]);
  const modalVisible = ref(false);
  const editingId = ref<number | null>(null);
  const previewVisible = ref(false);
  const previewLoading = ref(false);
  const previewData = ref<PetGrowthPreview[]>([]);

  const emptyForm = (): PetGrowthStageForm => ({
    chainCode: '',
    stage: 1,
    name: '',
    petId: undefined,
    nextPetId: null,
    needExp: 100,
    expPerFeed: 10,
    feedItemIds: null,
    expRate: 1.0,
    dropRate: 1.0,
    mesoRate: 1.0,
    sortOrder: 0,
    enabled: 1,
  });

  const form = ref<PetGrowthStageForm>(emptyForm());
  const enabledBool = computed({
    get: () => form.value.enabled === 1,
    set: (v: boolean) => {
      form.value.enabled = v ? 1 : 0;
    },
  });
  const modalTitle = computed(() =>
    editingId.value
      ? `${t('petGrowth.button.edit')} - ${form.value.name}`
      : t('button.create')
  );

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getStageList();
      tableData.value = data as unknown as PetGrowthStageForm[];
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const resetForm = () => {
    form.value = emptyForm();
    editingId.value = null;
  };

  const addClick = () => {
    resetForm();
    modalVisible.value = true;
  };

  const editClick = async ({ id }: PetGrowthStageForm) => {
    if (id == null) return;
    setLoading(true);
    try {
      const { data } = await getStage(id);
      form.value = {
        ...emptyForm(),
        ...(data as unknown as PetGrowthStageForm),
      };
      editingId.value = form.value.id ?? null;
      modalVisible.value = true;
    } finally {
      setLoading(false);
    }
  };

  const saveClick = async () => {
    if (!form.value.name || !form.value.chainCode || !form.value.petId) {
      Message.warning('请填写进阶链、名称和宠物ID');
      return;
    }
    setLoading(true);
    try {
      await saveStage(form.value);
      Message.success(t('petGrowth.save.success'));
      modalVisible.value = false;
      resetForm();
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const deleteClick = async (id: number) => {
    setLoading(true);
    try {
      await deleteStage(id);
      Message.success(t('petGrowth.delete.success'));
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const onToggleEnabled = async (record: PetGrowthStageForm, v: boolean) => {
    try {
      await toggleEnabled(record.id!);
      record.enabled = v ? 1 : 0;
      Message.success(v ? '已启用' : '已禁用');
    } catch {
      Message.error('操作失败');
    }
  };

  const openPreview = async () => {
    previewVisible.value = true;
    previewLoading.value = true;
    try {
      const { data } = await getPreview();
      previewData.value = data as unknown as PetGrowthPreview[];
    } finally {
      previewLoading.value = false;
    }
  };

  const onReload = async () => {
    await reloadCache();
    Message.success(t('petGrowth.reload.success'));
    await loadData();
  };

  const onCancel = () => {
    modalVisible.value = false;
  };
</script>

<script lang="ts">
  export default {
    name: 'PetGrowth',
  };
</script>

<style scoped>
  .chain {
    margin-bottom: 8px;
  }
</style>
