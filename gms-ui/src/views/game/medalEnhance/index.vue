<template>
  <div class="container">
    <Breadcrumb />
    <a-card
      class="general-card"
      :title="$t('menu.game.medalEnhance')"
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
            :title="$t('medalEnhance.list.column.id')"
            data-index="id"
            :width="55"
            align="center"
          />
          <a-table-column
            :title="$t('medalEnhance.list.column.maxEnhance')"
            data-index="maxEnhance"
            :width="90"
            align="center"
          />
          <a-table-column
            :title="$t('medalEnhance.list.column.enabled')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green" size="small">
                {{ $t('medalEnhance.enabled.true') }}
              </a-tag>
              <a-tag v-else color="red" size="small">
                {{ $t('medalEnhance.enabled.false') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('medalEnhance.list.column.operations')"
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
                  :content="$t('medalEnhance.message.deleteTips')"
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
          ? $t('medalEnhance.form.title.update')
          : $t('medalEnhance.form.title.create')
      "
      :width="720"
      @cancel="drawerVisible = false"
    >
      <a-form ref="formRef" :model="editConfig" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item :label="$t('medalEnhance.form.field.maxEnhance')">
              <a-input-number
                v-model="editConfig.maxEnhance"
                :min="1"
                :max="30"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :label="$t('medalEnhance.form.field.enabled')">
              <a-switch
                :model-value="editConfig.enabled === 1"
                @change="(v: boolean) => (editConfig.enabled = v ? 1 : 0)"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider>{{ $t('medalEnhance.form.field.levels') }}</a-divider>

        <div
          v-for="(lv, idx) in editConfig.levels"
          :key="idx"
          class="level-card"
        >
          <a-row :gutter="8" align="center">
            <a-col :span="3">
              <span style="font-weight: bold"
                >Lv.{{ lv.enhanceLevel || idx + 1 }}</span
              >
            </a-col>
            <a-col :span="4">
              <a-input-number
                v-model="lv.successRate"
                :min="0"
                :max="100"
                :placeholder="$t('medalEnhance.form.field.successRate')"
                style="width: 100%"
              />
            </a-col>
            <a-col :span="4">
              <a-input-number
                v-model="lv.mesoCost"
                :min="0"
                :placeholder="$t('medalEnhance.form.field.mesoCost')"
                style="width: 100%"
              />
            </a-col>
            <a-col :span="3">
              <a-checkbox
                :model-value="!!lv.destroyOnFail"
                @change="
                  (v: boolean | string) => (lv.destroyOnFail = v ? 1 : 0)
                "
              >
                {{ $t('medalEnhance.form.field.destroyOnFail') }}
              </a-checkbox>
            </a-col>
            <a-col :span="8">
              <a-space size="mini" wrap>
                <a-input-number
                  v-model="lv.strAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.strAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.dexAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.dexAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.intAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.intAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.lukAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.lukAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.watkAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.watkAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.matkAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.matkAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.hpAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.hpAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.mpAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.mpAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.wdefAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.wdefAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.mdefAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.mdefAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.accAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.accAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.avoidAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.avoidAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.speedAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.speedAdd')"
                  style="width: 65px"
                  size="mini"
                />
                <a-input-number
                  v-model="lv.jumpAdd"
                  :min="0"
                  :placeholder="$t('medalEnhance.form.field.jumpAdd')"
                  style="width: 65px"
                  size="mini"
                />
              </a-space>
            </a-col>
            <a-col :span="2">
              <a-button size="mini" status="danger" @click="removeLevel(idx)"
                >X</a-button
              >
            </a-col>
          </a-row>
          <!-- 消耗道具 -->
          <div style="margin-left: 40px; margin-bottom: 8px">
            <a-space size="mini" wrap>
              <template v-for="(co, ci) in lv.costs" :key="ci">
                <a-input-number
                  v-model="co.itemId"
                  :min="1"
                  :placeholder="$t('medalEnhance.form.field.costItemId')"
                  style="width: 90px"
                  size="mini"
                />
                <span>x</span>
                <a-input-number
                  v-model="co.count"
                  :min="1"
                  :placeholder="$t('medalEnhance.form.field.costCount')"
                  style="width: 70px"
                  size="mini"
                />
                <a-button
                  size="mini"
                  type="text"
                  status="danger"
                  @click="lv.costs.splice(ci, 1)"
                  >X</a-button
                >
                <span v-if="ci < lv.costs.length - 1" style="margin: 0 4px"
                  >|</span
                >
              </template>
              <a-button
                size="mini"
                type="outline"
                @click="lv.costs.push({ itemId: 0, count: 1 })"
              >
                + {{ $t('medalEnhance.form.addCost') }}
              </a-button>
            </a-space>
          </div>
          <a-divider v-if="idx < editConfig.levels.length - 1" />
        </div>
        <a-button type="outline" long @click="addLevel">
          + {{ $t('medalEnhance.form.addLevel') }}
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

<script lang="ts" setup>
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    MedalEnhanceConfig,
    MedalEnhanceLevel,
    getMedalEnhanceList,
    saveMedalEnhanceConfig,
    deleteMedalEnhanceConfig,
  } from '@/api/medalEnhance';
  import { Message } from '@arco-design/web-vue';

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<MedalEnhanceConfig[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getMedalEnhanceList();
      tableData.value = data;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  // ========== 编辑抽屉 ==========
  const drawerVisible = ref(false);
  const editId = ref<number | undefined>();

  const emptyLevel = (lv: number): MedalEnhanceLevel => ({
    enhanceLevel: lv,
    successRate: 100,
    destroyOnFail: 0,
    mesoCost: 0,
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

  const editConfig = ref<MedalEnhanceConfig>({
    maxEnhance: 10,
    enabled: 1,
    levels: [],
  });

  const insertClick = () => {
    editId.value = undefined;
    editConfig.value = {
      maxEnhance: 10,
      enabled: 1,
      levels: [emptyLevel(1)],
    };
    drawerVisible.value = true;
  };

  const editClick = (record: MedalEnhanceConfig) => {
    editId.value = record.id;
    editConfig.value = JSON.parse(JSON.stringify(record));
    if (!editConfig.value.levels || editConfig.value.levels.length === 0) {
      editConfig.value.levels = [emptyLevel(1)];
    }
    editConfig.value.levels.forEach((lv) => {
      if (!lv.costs) lv.costs = [];
    });
    drawerVisible.value = true;
  };

  const addLevel = () => {
    const next = editConfig.value.levels.length + 1;
    editConfig.value.levels.push(emptyLevel(next));
  };

  const removeLevel = (idx: number) => {
    editConfig.value.levels.splice(idx, 1);
  };

  const submitForm = async () => {
    try {
      editConfig.value.levels.forEach((level, i) => {
        level.enhanceLevel = i + 1;
      });
      await saveMedalEnhanceConfig(editConfig.value);
      Message.success('保存成功');
      drawerVisible.value = false;
      loadData();
    } catch (e: any) {
      Message.error(e?.message || '保存失败');
    }
  };

  const deleteClick = async (record: MedalEnhanceConfig) => {
    try {
      if (!record.id) return;
      await deleteMedalEnhanceConfig(record.id);
      Message.success('删除成功');
      await loadData();
    } catch (e: any) {
      Message.error(e?.message || '删除失败');
    }
  };
</script>

<style scoped>
  .level-card {
    background: var(--color-fill-1);
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 8px;
  }
</style>
