<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.boss.config')">
      <a-row>
        <a-col>
          <a-space>
            <a-input-number
              v-model="condition.mobId"
              :placeholder="$t('bossConfig.placeholder.mobId')"
              :min="1"
            />
            <a-input
              v-model="condition.bossName"
              :placeholder="$t('bossConfig.placeholder.bossName')"
              :style="{ width: '180px' }"
            />
            <a-button type="primary" @click="loadData">
              {{ $t('button.search') }}
            </a-button>
            <a-button @click="resetClick">
              {{ $t('button.reset') }}
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
            :title="$t('bossConfig.list.column.icon')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-popover>
                <img
                  :src="getIconUrl('mob', record.mobId)"
                  alt=""
                  style="width: 40px; height: 40px"
                  @error="onBossImgError($event, record.mobId)"
                />
                <template #content>
                  <img
                    :src="getIconUrl('mob', record.mobId)"
                    alt=""
                    @error="onBossImgError($event, record.mobId)"
                  />
                </template>
              </a-popover>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('bossConfig.list.column.mobId')"
            data-index="mobId"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('bossConfig.list.column.bossName')"
            data-index="bossName"
            :width="120"
            align="center"
          />
          <a-table-column
            :title="$t('bossConfig.list.column.level')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.level != null" color="orangered" size="small">
                {{ record.level }}
              </a-tag>
              <span v-else>{{ record.wzLevel ?? '-' }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('bossConfig.list.column.hp')"
            :width="90"
            align="right"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.hp != null" color="orangered" size="small">
                {{ formatNumber(record.hp) }}
              </a-tag>
              <span v-else>
                {{ formatNumber(computeEffectiveHp(record)) }}
              </span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('bossConfig.list.column.pdd')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.pdd != null" color="orangered" size="small">
                {{ record.pdd }}
              </a-tag>
              <span v-else>{{ record.wzPdd ?? '-' }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('bossConfig.list.column.mdd')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.mdd != null" color="orangered" size="small">
                {{ record.mdd }}
              </a-tag>
              <span v-else>{{ record.wzMdd ?? '-' }}</span>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('bossConfig.list.column.hpMultiplier')"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="record.hpMultiplier !== 1 ? 'orangered' : 'gray'">
                {{ record.hpMultiplier }}x
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('bossConfig.list.column.expMultiplier')"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="record.expMultiplier !== 1 ? 'orangered' : 'gray'">
                {{ record.expMultiplier }}x
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('bossConfig.list.column.damageMultiplier')"
            :width="100"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="record.damageMultiplier !== 1 ? 'red' : 'gray'">
                {{ record.damageMultiplier }}x
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('bossConfig.list.column.enabled')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green">
                {{ $t('bossConfig.enabled.true') }}
              </a-tag>
              <a-tag v-else color="red">
                {{ $t('bossConfig.enabled.false') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('bossConfig.list.column.updateTime')"
            data-index="updateTime"
            :width="160"
            align="center"
          />
          <a-table-column
            :title="$t('bossConfig.list.column.operations')"
            :width="120"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-button size="mini" type="text" @click="editClick(record)">
                {{ $t('button.edit') }}
              </a-button>
              <a-popconfirm
                type="error"
                :content="$t('bossConfig.message.deleteTips')"
                position="left"
                @ok="deleteClick(record)"
              >
                <a-button size="mini" status="danger" type="text">
                  {{ $t('button.delete') }}
                </a-button>
              </a-popconfirm>
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
        :page-size-options="[10, 20, 40, 60]"
        @change="pageChange"
        @page-size-change="pageSizeChange"
      />
    </a-card>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="formVisible"
      :title="
        formMode === 'add'
          ? $t('bossConfig.form.title.create')
          : $t('bossConfig.form.title.update')
      "
      @ok="submitForm"
      @cancel="formVisible = false"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules">
        <a-form-item field="mobId" :label="$t('bossConfig.form.field.mobId')">
          <a-input-number
            v-model="formData.mobId"
            :min="1"
            :disabled="formMode === 'edit'"
          />
        </a-form-item>
        <a-form-item
          field="bossName"
          :label="$t('bossConfig.form.field.bossName')"
        >
          <a-input v-model="formData.bossName" />
        </a-form-item>
        <a-form-item
          field="hpMultiplier"
          :label="$t('bossConfig.form.field.hpMultiplier')"
        >
          <a-input-number
            v-model="formData.hpMultiplier"
            :min="0.01"
            :step="0.1"
            :precision="2"
          />
        </a-form-item>
        <a-form-item
          field="expMultiplier"
          :label="$t('bossConfig.form.field.expMultiplier')"
        >
          <a-input-number
            v-model="formData.expMultiplier"
            :min="0.01"
            :step="0.1"
            :precision="2"
          />
        </a-form-item>
        <a-form-item
          field="damageMultiplier"
          :label="$t('bossConfig.form.field.damageMultiplier')"
        >
          <a-input-number
            v-model="formData.damageMultiplier"
            :min="0.01"
            :step="0.1"
            :precision="2"
          />
        </a-form-item>
        <a-form-item field="level" :label="$t('bossConfig.form.field.level')">
          <a-input-number
            v-model="formData.level"
            :min="1"
            :placeholder="String(formData.wzLevel ?? '')"
          />
        </a-form-item>
        <a-form-item field="hp" :label="$t('bossConfig.form.field.hp')">
          <a-input-number
            v-model="formData.hp"
            :min="1"
            :placeholder="String(formData.wzHp ?? '')"
          />
        </a-form-item>
        <a-form-item field="mp" :label="$t('bossConfig.form.field.mp')">
          <a-input-number
            v-model="formData.mp"
            :min="0"
            :placeholder="String(formData.wzMp ?? '')"
          />
        </a-form-item>
        <a-form-item field="exp" :label="$t('bossConfig.form.field.exp')">
          <a-input-number
            v-model="formData.exp"
            :min="0"
            :placeholder="String(formData.wzExp ?? '')"
          />
        </a-form-item>
        <a-form-item field="pdd" :label="$t('bossConfig.form.field.pdd')">
          <a-input-number
            v-model="formData.pdd"
            :min="0"
            :placeholder="String(formData.wzPdd ?? '')"
          />
        </a-form-item>
        <a-form-item field="mdd" :label="$t('bossConfig.form.field.mdd')">
          <a-input-number
            v-model="formData.mdd"
            :min="0"
            :placeholder="String(formData.wzMdd ?? '')"
          />
        </a-form-item>
        <a-form-item field="acc" :label="$t('bossConfig.form.field.acc')">
          <a-input-number
            v-model="formData.acc"
            :min="0"
            :placeholder="String(formData.wzAcc ?? '')"
          />
        </a-form-item>
        <a-form-item field="eva" :label="$t('bossConfig.form.field.eva')">
          <a-input-number
            v-model="formData.eva"
            :min="0"
            :placeholder="String(formData.wzEva ?? '')"
          />
        </a-form-item>
        <a-form-item
          field="enabled"
          :label="$t('bossConfig.form.field.enabled')"
        >
          <a-switch v-model="enabledSwitch" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import useLoading from '@/hooks/loading';
  import {
    BossConfigItem,
    BossConfigSearch,
    getBossConfigList,
    addBossConfig,
    updateBossConfig,
    deleteBossConfig,
  } from '@/api/bossConfig';
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import { getIconUrl } from '@/utils/mapleStoryAPI';

  // 进阶BOSS图标回退映射（无自身图标时使用普通版）
  const bossIconFallback: Record<number, number> = {
    8800102: 8800002, // 进阶扎昆 → 扎昆
    8810118: 8810018, // 进阶黑龙 → 黑龙
    8820101: 8820001, // 进阶品克缤 → 品克缤
  };

  function onBossImgError(e: Event, mobId: number) {
    const fallback = bossIconFallback[mobId];
    if (fallback) {
      const img = e.target as HTMLImageElement;
      if (!img.src.includes(String(fallback))) {
        img.src = getIconUrl('mob', fallback);
      }
    }
  }

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<BossConfigItem[]>([]);
  const total = ref<number>(0);
  const condition = ref<BossConfigSearch>({
    mobId: undefined,
    bossName: '',
    pageNo: 1,
    pageSize: 20,
  });

  const pageChange = (data: number) => {
    condition.value.pageNo = data;
    loadData();
  };
  const pageSizeChange = (data: number) => {
    condition.value.pageNo = 1;
    condition.value.pageSize = data;
    loadData();
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getBossConfigList(condition.value);
      tableData.value = data.records;
      total.value = data.totalRow;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const resetClick = () => {
    condition.value = {
      mobId: undefined,
      bossName: '',
      pageNo: 1,
      pageSize: 20,
    };
    loadData();
  };

  // ---------- 新增/编辑弹窗 ----------
  const formVisible = ref(false);
  const formMode = ref<'add' | 'edit'>('add');
  const enabledSwitch = ref(true);
  const formRef = ref();
  const formData = ref<BossConfigItem>({
    mobId: 0,
    bossName: '',
    hpMultiplier: 1.0,
    expMultiplier: 1.0,
    damageMultiplier: 1.0,
    enabled: 1,
  });

  const formRules = {
    mobId: [{ required: true, message: 'mobId required' }],
    bossName: [{ required: true, message: 'bossName required' }],
    hpMultiplier: [{ required: true, message: 'hpMultiplier required' }],
    expMultiplier: [{ required: true, message: 'expMultiplier required' }],
    damageMultiplier: [
      { required: true, message: 'damageMultiplier required' },
    ],
  };

  const insertClick = () => {
    formMode.value = 'add';
    formData.value = {
      mobId: 0,
      bossName: '',
      hpMultiplier: 1.0,
      expMultiplier: 1.0,
      damageMultiplier: 1.0,
      level: null,
      hp: null,
      mp: null,
      exp: null,
      pdd: null,
      mdd: null,
      acc: null,
      eva: null,
      enabled: 1,
    };
    enabledSwitch.value = true;
    formVisible.value = true;
  };

  const editClick = (record: BossConfigItem) => {
    formMode.value = 'edit';
    formData.value = { ...record };
    enabledSwitch.value = record.enabled === 1;
    formVisible.value = true;
  };

  const submitForm = async () => {
    const valid = await formRef.value?.validate();
    if (valid) return;

    formData.value.enabled = enabledSwitch.value ? 1 : 0;
    setLoading(true);
    try {
      if (formMode.value === 'add') {
        await addBossConfig(formData.value);
        Message.success('bossConfig.message.addSuccess');
      } else {
        await updateBossConfig(formData.value);
        Message.success('bossConfig.message.updateSuccess');
      }
      formVisible.value = false;
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  const deleteClick = async (record: BossConfigItem) => {
    setLoading(true);
    try {
      if (!record.id) return;
      await deleteBossConfig(record.id);
      Message.success('bossConfig.message.deleteSuccess');
      await loadData();
    } finally {
      setLoading(false);
    }
  };

  // ========== 工具函数 ==========
  const formatNumber = (num: number | null | undefined): string => {
    if (num == null) return '-';
    if (num >= 100000000) {
      return `${(num / 100000000).toFixed(1)}亿`;
    }
    if (num >= 10000) {
      return `${(num / 10000).toFixed(1)}万`;
    }
    return num.toLocaleString();
  };

  const computeEffectiveHp = (record: BossConfigItem): number | null => {
    const baseHp = record.wzHp;
    if (baseHp == null) return null;
    const mul = record.hpMultiplier ?? 1;
    return Math.round(baseHp * mul);
  };
</script>

<script lang="ts">
  export default {
    name: 'BossConfig',
  };
</script>

<style lang="less" scoped></style>
