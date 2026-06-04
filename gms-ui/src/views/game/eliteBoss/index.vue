<template>
  <div class="container">
    <Breadcrumb />
    <a-card
      class="general-card"
      :title="$t('menu.boss.eliteBoss')"
      style="overflow-x: auto"
    >
      <a-row>
        <a-col>
          <a-space>
            <a-input-number
              v-model="condition.bossId"
              :placeholder="$t('eliteBoss.placeholder.bossId')"
              :min="1"
              allow-clear
            />
            <a-input
              v-model="condition.bossName"
              :placeholder="$t('eliteBoss.placeholder.bossName')"
              allow-clear
              :style="{ width: '160px' }"
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
            :title="$t('eliteBoss.list.column.icon')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <a-popover>
                <img
                  :src="getIconUrl('mob', record.bossId)"
                  alt=""
                  style="width: 40px; height: 40px"
                  @error="onBossImgError($event, record.bossId)"
                />
                <template #content>
                  <img
                    :src="getIconUrl('mob', record.bossId)"
                    alt=""
                    @error="onBossImgError($event, record.bossId)"
                  />
                </template>
              </a-popover>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('eliteBoss.list.column.bossId')"
            data-index="bossId"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('eliteBoss.list.column.bossName')"
            data-index="bossName"
            :width="110"
            align="center"
          />
          <a-table-column
            :title="$t('eliteBoss.list.column.level')"
            data-index="bossLevel"
            :width="55"
            align="center"
          />
          <a-table-column
            :title="$t('eliteBoss.list.column.hp')"
            :width="100"
            align="right"
          >
            <template #cell="{ record }">
              {{ formatNumber(record.bossMaxHp) }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('eliteBoss.list.column.exp')"
            :width="90"
            align="right"
          >
            <template #cell="{ record }">
              {{ formatNumber(record.bossExp) }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('eliteBoss.list.column.mapId')"
            data-index="mapId"
            :width="80"
            align="center"
          />
          <a-table-column
            :title="$t('eliteBoss.list.column.mapName')"
            data-index="mapName"
            :width="120"
            align="center"
            ellipsis
            tooltip
          />
          <a-table-column
            :title="$t('eliteBoss.list.column.bossTime')"
            :width="75"
            align="center"
          >
            <template #cell="{ record }">
              {{ record.bossTime }}{{ $t('eliteBoss.minute') }}
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('eliteBoss.list.column.aliveStatus')"
            :width="220"
            align="center"
          >
            <template #cell="{ record }">
              <a-space wrap size="mini">
                <a-tooltip
                  v-for="cs in record.channelStatuses"
                  :key="`${cs.worldId}-${cs.channelId}`"
                  :content="
                    cs.count > 0
                      ? $t('eliteBoss.alive') +
                        (cs.count > 1
                          ? '(' + cs.count + $t('eliteBoss.unit') + ')'
                          : '')
                      : $t('eliteBoss.dead')
                  "
                >
                  <a-tag :color="cs.count > 0 ? 'green' : 'red'" size="small">
                    {{
                      cs.count > 0
                        ? $t('eliteBoss.alive') +
                          (cs.count > 1
                            ? '(' + cs.count + $t('eliteBoss.unit') + ')'
                            : '')
                        : $t('eliteBoss.dead')
                    }}
                  </a-tag>
                </a-tooltip>
              </a-space>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('eliteBoss.list.column.enabled')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag v-if="record.enabled === 1" color="green" size="small">
                {{ $t('eliteBoss.enabled.true') }}
              </a-tag>
              <a-tag v-else color="red" size="small">
                {{ $t('eliteBoss.enabled.false') }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('eliteBoss.list.column.operations')"
            :width="240"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button
                  size="mini"
                  type="primary"
                  @click="spawnClick(record)"
                >
                  {{ $t('eliteBoss.button.spawn') }}
                </a-button>
                <a-button
                  size="mini"
                  status="danger"
                  @click="killClick(record)"
                >
                  {{ $t('eliteBoss.button.kill') }}
                </a-button>
                <a-button size="mini" type="text" @click="editClick(record)">
                  {{ $t('button.edit') }}
                </a-button>
                <a-popconfirm
                  type="error"
                  :content="$t('eliteBoss.message.deleteTips')"
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

    <!-- 召唤弹窗 -->
    <a-modal
      v-model:visible="spawnVisible"
      :title="$t('eliteBoss.form.title.spawn')"
      @ok="submitSpawn"
      @cancel="spawnVisible = false"
    >
      <a-form ref="spawnFormRef" :model="spawnForm">
        <a-form-item :label="$t('eliteBoss.form.field.bossName')">
          <a-input :model-value="spawnForm.bossName" disabled />
        </a-form-item>
        <a-form-item
          field="worldIds"
          :label="$t('eliteBoss.form.field.worldId')"
        >
          <a-select
            v-model="spawnForm.worldIds"
            multiple
            @change="onWorldChange"
          >
            <a-option :value="-1">{{
              $t('eliteBoss.placeholder.worldId')
            }}</a-option>
            <a-option
              v-for="w in worldList"
              :key="w.worldId"
              :value="w.worldId"
            >
              {{ $t('eliteBoss.worldLabel', { id: w.worldId }) }}
            </a-option>
          </a-select>
        </a-form-item>
        <a-form-item
          field="channelIds"
          :label="$t('eliteBoss.form.field.channelId')"
        >
          <a-select
            v-model="spawnForm.channelIds"
            multiple
            @change="onChannelChange"
          >
            <a-option :value="-1">{{
              $t('eliteBoss.placeholder.channelId')
            }}</a-option>
            <a-option v-for="ch in channelList" :key="ch.id" :value="ch.id">
              {{ $t('eliteBoss.channel', { id: ch.id }) }}
            </a-option>
          </a-select>
        </a-form-item>
        <a-form-item field="count" :label="$t('eliteBoss.form.field.count')">
          <a-input-number
            v-model="spawnForm.count"
            :min="1"
            :max="100"
            :default-value="1"
          />
        </a-form-item>
        <a-form-item
          v-if="spawnForm.companionBossId"
          field="spawnCompanion"
          :label="$t('eliteBoss.form.field.spawnCompanion')"
        >
          <a-switch v-model="spawnForm.spawnCompanion" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="formVisible"
      :title="
        formMode === 'add'
          ? $t('eliteBoss.form.title.create')
          : $t('eliteBoss.form.title.update')
      "
      @ok="submitForm"
      @cancel="formVisible = false"
    >
      <a-form ref="formRef" :model="formData">
        <a-form-item field="mapId" :label="$t('eliteBoss.form.field.mapId')">
          <a-input-number v-model="formData.mapId" :min="1" />
        </a-form-item>
        <a-form-item field="bossId" :label="$t('eliteBoss.form.field.bossId')">
          <a-input-number
            v-model="formData.bossId"
            :min="1"
            :disabled="formMode === 'edit'"
          />
        </a-form-item>
        <a-form-item
          field="bossName"
          :label="$t('eliteBoss.form.field.bossName')"
        >
          <a-input v-model="formData.bossName" />
        </a-form-item>
        <a-form-item
          field="bossTime"
          :label="$t('eliteBoss.form.field.bossTime')"
        >
          <a-input-number
            v-model="formData.bossTime"
            :min="1"
            :default-value="180"
          />
        </a-form-item>
        <a-form-item
          field="scriptName"
          :label="$t('eliteBoss.form.field.scriptName')"
        >
          <a-input v-model="formData.scriptName" />
        </a-form-item>
        <a-form-item
          field="enabled"
          :label="$t('eliteBoss.form.field.enabled')"
        >
          <a-switch v-model="enabledSwitch" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, computed, onMounted } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    EliteBossConditionState,
    EliteBossConfigState,
    EliteBossSpawnState,
    getEliteBossList,
    insertEliteBossConfig,
    updateEliteBossConfig,
    deleteEliteBossConfig,
    spawnEliteBoss,
    killEliteBoss,
    getWorldChannels,
  } from '@/api/eliteBoss';
  import { getIconUrl } from '@/utils/mapleStoryAPI';
  import { Message } from '@arco-design/web-vue';

  const bossIconFallback: Record<number, number> = {
    8800102: 8800002,
    8810118: 8810018,
    8820101: 8820001,
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
  const tableData = ref<any[]>([]);
  const total = ref<number>(0);
  const condition = ref<EliteBossConditionState>({
    bossId: undefined,
    bossName: '',
    pageNo: 1,
    pageSize: 20,
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getEliteBossList(condition.value);
      tableData.value = data.records;
      total.value = data.totalRow;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const pageChange = (data: number) => {
    condition.value.pageNo = data;
    loadData();
  };
  const pageSizeChange = (data: number) => {
    condition.value.pageNo = 1;
    condition.value.pageSize = data;
    loadData();
  };

  const resetClick = () => {
    condition.value.bossId = undefined;
    condition.value.bossName = '';
    condition.value.pageNo = 1;
    loadData();
  };

  // ========== 新增/编辑 ==========
  const formVisible = ref(false);
  const formMode = ref<'add' | 'edit'>('add');
  const formData = ref<EliteBossConfigState>({
    id: undefined,
    mapId: undefined,
    bossId: undefined,
    bossName: '',
    bossTime: 180,
    scriptName: '',
    enabled: 1,
  });

  const enabledSwitch = computed({
    get: () => formData.value.enabled === 1,
    set: (val: boolean) => {
      formData.value.enabled = val ? 1 : 0;
    },
  });

  const insertClick = () => {
    formMode.value = 'add';
    formData.value = {
      id: undefined,
      mapId: undefined,
      bossId: undefined,
      bossName: '',
      bossTime: 180,
      scriptName: '',
      enabled: 1,
    };
    formVisible.value = true;
  };

  const editClick = (record: any) => {
    formMode.value = 'edit';
    formData.value = {
      id: record.id,
      mapId: record.mapId,
      bossId: record.bossId,
      bossName: record.bossName,
      bossTime: record.bossTime,
      scriptName: record.scriptName,
      enabled: record.enabled,
    };
    formVisible.value = true;
  };

  const submitForm = async () => {
    try {
      if (formMode.value === 'add') {
        await insertEliteBossConfig(formData.value);
        Message.success('新增成功');
      } else {
        await updateEliteBossConfig(formData.value);
        Message.success('更新成功');
      }
      formVisible.value = false;
      loadData();
    } catch (e: any) {
      Message.error(e?.message || '操作失败');
    }
  };

  const deleteClick = async (record: any) => {
    try {
      await deleteEliteBossConfig(record.id);
      Message.success('删除成功');
      loadData();
    } catch (e: any) {
      Message.error(e?.message || '删除失败');
    }
  };

  // ========== 世界/频道列表 ==========
  const worldList = ref<any[]>([]);
  const channelList = ref<any[]>([]);

  async function fetchWorldChannels() {
    try {
      const { data } = await getWorldChannels();
      worldList.value = data;
      const channels: any[] = [];
      data.forEach((w: any) => {
        w.channels.forEach((ch: any) => channels.push(ch));
      });
      channelList.value = channels;
    } catch {
      // ignore
    }
  }

  // ========== 召唤/清除 ==========
  const spawnVisible = ref(false);
  const spawnForm = ref<
    EliteBossSpawnState & { bossName: string; companionBossId?: number }
  >({
    configId: undefined,
    worldIds: [-1],
    channelIds: [-1],
    count: 1,
    spawnCompanion: true,
    bossName: '',
    companionBossId: undefined,
  });

  function onWorldChange(values: number[]) {
    if (values.length === 0) {
      // 全部取消时回退到全部大区
      spawnForm.value.worldIds = [-1];
    } else if (values.includes(-1) && values.length > 1) {
      // 用户从"全部"切换到具体大区，移除-1只保留具体选择
      spawnForm.value.worldIds = values.filter((v) => v !== -1);
    } else if (values.includes(-1)) {
      // 选了"全部"，只保留-1
      spawnForm.value.worldIds = [-1];
    } else {
      // 选了具体大区，正常保留
      spawnForm.value.worldIds = values;
    }
  }

  function onChannelChange(values: number[]) {
    if (values.length === 0) {
      // 全部取消时回退到全部频道
      spawnForm.value.channelIds = [-1];
    } else if (values.includes(-1) && values.length > 1) {
      // 用户从"全部"切换到具体频道，移除-1只保留具体选择
      spawnForm.value.channelIds = values.filter((v) => v !== -1);
    } else if (values.includes(-1)) {
      // 选了"全部"，只保留-1
      spawnForm.value.channelIds = [-1];
    } else {
      // 选了具体频道，正常保留
      spawnForm.value.channelIds = values;
    }
  }

  const spawnClick = (record: any) => {
    spawnForm.value = {
      configId: record.id,
      worldIds: [-1],
      channelIds: [-1],
      count: 1,
      spawnCompanion: true,
      bossName: record.bossName,
      companionBossId: record.companionBossId,
    };
    spawnVisible.value = true;
  };

  const submitSpawn = async () => {
    try {
      const { data } = await spawnEliteBoss(spawnForm.value);
      Message.success(data || '召唤完成');
      spawnVisible.value = false;
      loadData();
    } catch (e: any) {
      Message.error(e?.message || '召唤失败');
    }
  };

  const killClick = async (record: any) => {
    try {
      const { data } = await killEliteBoss({
        configId: record.id,
        worldIds: [-1],
        channelIds: [-1],
        count: 1,
      });
      Message.success(data || '清除完成');
      loadData();
    } catch (e: any) {
      Message.error(e?.message || '清除失败');
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

  onMounted(() => {
    fetchWorldChannels();
  });
</script>
