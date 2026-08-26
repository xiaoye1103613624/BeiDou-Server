<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" title="抽奖管理">
      <a-space wrap style="margin-bottom: 12px">
        <a-button type="primary" status="success" @click="createMachine">
          新建抽奖机
        </a-button>
        <a-button @click="loadMachines">刷新</a-button>
        <a-button status="warning" @click="onReloadAll">热重载全部</a-button>
        <a-button status="danger" @click="onImport123">导入123奖池</a-button>
        <a-button :loading="syncingIcons" @click="onSyncLotteryIcons">
          同步奖池图标
        </a-button>
        <a-input-number
          v-model="lookupItemId"
          placeholder="物品反查"
          :style="{ width: '120px' }"
        />
        <a-button @click="onLookupItem">反查NPC</a-button>
      </a-space>
      <a-table
        row-key="npcId"
        :loading="loading"
        :data="machines"
        :pagination="false"
        column-resizable
        :bordered="{ cell: true }"
        :row-class="rowClass"
        @row-click="onSelectMachine"
      >
        <template #columns>
          <a-table-column title="NPC" data-index="npcId" :width="90" />
          <a-table-column title="名称" data-index="name" :width="140" />
          <a-table-column title="消耗" :width="140">
            <template #cell="{ record }">
              {{ record.costType }} x{{ record.costAmount }}
            </template>
          </a-table-column>
          <a-table-column title="连抽" data-index="multiDraws" :width="120" />
          <a-table-column title="启用" :width="70">
            <template #cell="{ record }">
              <a-tag :color="record.enabled === 1 ? 'green' : 'red'">
                {{ record.enabled === 1 ? '是' : '否' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="操作" :width="220" fixed="right">
            <template #cell="{ record }">
              <a-button
                size="mini"
                type="text"
                @click.stop="openItemDrawer(record)"
              >
                奖品
              </a-button>
              <a-button
                size="mini"
                type="text"
                @click.stop="editMachine(record)"
              >
                编辑
              </a-button>
              <a-button
                size="mini"
                type="text"
                @click.stop="onReloadNpc(record.npcId)"
              >
                重载
              </a-button>
              <a-popconfirm
                content="删除该抽奖机及全部奖品？"
                @ok="onDeleteMachine(record.npcId)"
              >
                <a-button size="mini" status="danger" type="text" @click.stop>
                  删
                </a-button>
              </a-popconfirm>
            </template>
          </a-table-column>
        </template>
      </a-table>
      <a-alert v-if="lookupResult.length" style="margin-top: 12px" type="info">
        物品 {{ lookupItemId }} 绑定：
        <span v-for="r in lookupResult" :key="r.npcId">
          NPC{{ r.npcId }}({{ r.machineName }})x{{ r.count }};
        </span>
      </a-alert>
      <a-empty
        v-if="!machines.length && !loading"
        description="暂无抽奖机，请新建或导入"
        style="margin-top: 24px"
      />
    </a-card>

    <a-drawer
      :visible="itemDrawerVisible"
      :width="980"
      unmount-on-close
      @cancel="closeItemDrawer"
    >
      <template #title>
        奖品 · NPC {{ selectedNpcId }}
        <span v-if="selectedMachineName" style="margin-left: 8px; opacity: 0.7">
          （{{ selectedMachineName }}）
        </span>
      </template>
      <template #footer>
        <a-space>
          <a-button @click="closeItemDrawer">关闭</a-button>
          <a-button type="primary" @click="addItem">新增奖品</a-button>
        </a-space>
      </template>
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" @click="addItem">新增奖品</a-button>
        <a-button :loading="itemLoading" @click="loadItems">刷新奖品</a-button>
        <span style="opacity: 0.65">共 {{ items.length }} 条</span>
      </a-space>
      <a-table
        row-key="id"
        :loading="itemLoading"
        :data="items"
        :pagination="false"
        column-resizable
        :bordered="{ cell: true }"
        :scroll="{ y: 'calc(100vh - 220px)', x: 1400 }"
      >
        <template #columns>
          <a-table-column title="图" :width="56">
            <template #cell="{ record }">
              <img
                v-if="record.itemId"
                :src="getCachedIconUrl('item', record.itemId)"
                :data-item-id="String(record.itemId)"
                style="width: 32px; height: 32px"
                @error="onItemIconError"
              />
            </template>
          </a-table-column>
          <a-table-column title="物品ID" data-index="itemId" :width="90" />
          <a-table-column
            title="名称"
            data-index="itemName"
            :width="120"
            show-overflow-tooltip
          />
          <a-table-column title="类型" :width="100">
            <template #cell="{ record }">
              <a-select
                v-model="record.itemType"
                size="mini"
                @change="saveItemRow(record)"
              >
                <a-option :value="1">特殊</a-option>
                <a-option :value="2">装备</a-option>
                <a-option :value="3">消耗</a-option>
                <a-option :value="4">其它</a-option>
              </a-select>
            </template>
          </a-table-column>
          <a-table-column title="排序" :width="90">
            <template #cell="{ record }">
              <a-input-number
                v-model="record.sortOrder"
                size="mini"
                :style="{ width: '70px' }"
                @change="saveItemRow(record)"
              />
            </template>
          </a-table-column>
          <a-table-column title="数量" :width="80">
            <template #cell="{ record }">
              <a-input-number
                v-model="record.quantity"
                size="mini"
                :style="{ width: '60px' }"
                @change="saveItemRow(record)"
              />
            </template>
          </a-table-column>
          <a-table-column title="权重" :width="90">
            <template #cell="{ record }">
              <a-input-number
                v-model="record.weight"
                size="mini"
                :style="{ width: '70px' }"
                @change="saveItemRow(record)"
              />
            </template>
          </a-table-column>
          <a-table-column title="万分" :width="70">
            <template #cell="{ record }">
              {{ weightPermille(record) }}
            </template>
          </a-table-column>
          <a-table-column title="波动" :width="60">
            <template #cell="{ record }">
              <a-switch
                :model-value="record.randomStats === 1"
                size="small"
                @change="
                  (v: boolean) => {
                    record.randomStats = v ? 1 : 0;
                    saveItemRow(record);
                  }
                "
              />
            </template>
          </a-table-column>
          <a-table-column title="不可交易" :width="70">
            <template #cell="{ record }">
              <a-switch
                :model-value="record.untradeable === 1"
                size="small"
                @change="
                  (v: boolean) => {
                    record.untradeable = v ? 1 : 0;
                    saveItemRow(record);
                  }
                "
              />
            </template>
          </a-table-column>
          <a-table-column title="固有道具" :width="70">
            <template #cell="{ record }">
              <a-switch
                :model-value="record.accountBound === 1"
                size="small"
                @change="
                  (v: boolean) => {
                    record.accountBound = v ? 1 : 0;
                    saveItemRow(record);
                  }
                "
              />
            </template>
          </a-table-column>
          <a-table-column title="固有装备" :width="70">
            <template #cell="{ record }">
              <a-switch
                :model-value="record.uniqueEquip === 1"
                size="small"
                @change="
                  (v: boolean) => {
                    record.uniqueEquip = v ? 1 : 0;
                    saveItemRow(record);
                  }
                "
              />
            </template>
          </a-table-column>
          <a-table-column title="广播" :width="60">
            <template #cell="{ record }">
              <a-switch
                :model-value="record.announce === 1"
                size="small"
                @change="
                  (v: boolean) => {
                    record.announce = v ? 1 : 0;
                    saveItemRow(record);
                  }
                "
              />
            </template>
          </a-table-column>
          <a-table-column title="启用" :width="60">
            <template #cell="{ record }">
              <a-switch
                :model-value="record.enabled === 1"
                size="small"
                @change="
                  (v: boolean) => {
                    record.enabled = v ? 1 : 0;
                    saveItemRow(record);
                  }
                "
              />
            </template>
          </a-table-column>
          <a-table-column title="操作" :width="70" fixed="right">
            <template #cell="{ record }">
              <a-popconfirm
                content="删除该奖品？"
                @ok="onDeleteItem(record.id)"
              >
                <a-button size="mini" status="danger" type="text">删</a-button>
              </a-popconfirm>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-drawer>

    <a-modal
      v-model:visible="machineVisible"
      :title="machineForm.id ? '编辑抽奖机' : '新建抽奖机'"
      :ok-loading="loading"
      @ok="saveMachineSubmit"
    >
      <a-form :model="machineForm" layout="vertical">
        <a-form-item label="NPC ID" required>
          <a-input-number
            v-model="machineForm.npcId"
            :disabled="!!machineForm.id"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="名称">
          <a-input v-model="machineForm.name" />
        </a-form-item>
        <a-form-item label="几连抽（逗号分隔，如 1,10,20）">
          <a-input v-model="multiDrawsText" placeholder="1,10" />
        </a-form-item>
        <a-form-item label="消耗类型（仅一种）">
          <a-select v-model="machineForm.costType">
            <a-option value="MESO">金币</a-option>
            <a-option value="ITEM">道具</a-option>
            <a-option value="NX">点卷</a-option>
            <a-option value="MAPLE_POINT">抵用卷</a-option>
          </a-select>
        </a-form-item>
        <a-form-item v-if="machineForm.costType === 'ITEM'" label="消耗道具ID">
          <a-input-number
            v-model="machineForm.costItemId"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="一连消耗量">
          <a-input-number
            v-model="machineForm.costAmount"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="启用">
          <a-switch
            :model-value="machineForm.enabled === 1"
            @change="
              (v: boolean) => {
                machineForm.enabled = v ? 1 : 0;
              }
            "
          />
        </a-form-item>
        <a-form-item label="备注">
          <a-input v-model="machineForm.comment" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:visible="itemVisible"
      title="新增奖品"
      :ok-loading="itemLoading"
      @ok="saveNewItem"
    >
      <a-form :model="itemForm" layout="vertical">
        <a-form-item label="物品ID" required>
          <a-input-number
            v-model="itemForm.itemId"
            style="width: 100%"
            @change="onItemIdChange"
          />
        </a-form-item>
        <a-form-item label="类型">
          <a-select v-model="itemForm.itemType">
            <a-option :value="1">特殊</a-option>
            <a-option :value="2">装备</a-option>
            <a-option :value="3">消耗</a-option>
            <a-option :value="4">其它</a-option>
          </a-select>
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model="itemForm.sortOrder" style="width: 100%" />
        </a-form-item>
        <a-form-item label="数量">
          <a-input-number v-model="itemForm.quantity" style="width: 100%" />
        </a-form-item>
        <a-form-item label="权重">
          <a-input-number v-model="itemForm.weight" style="width: 100%" />
        </a-form-item>
        <a-form-item label="属性波动（装备）">
          <a-switch
            :model-value="itemForm.randomStats === 1"
            @change="
              (v: boolean) => {
                itemForm.randomStats = v ? 1 : 0;
              }
            "
          />
        </a-form-item>
        <a-form-item label="广播">
          <a-switch
            :model-value="itemForm.announce === 1"
            @change="
              (v: boolean) => {
                itemForm.announce = v ? 1 : 0;
              }
            "
          />
        </a-form-item>
        <a-form-item label="广播频道 type">
          <a-input-number
            v-model="itemForm.announceChannel"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="横幅广播">
          <a-switch
            :model-value="itemForm.announceBanner === 1"
            @change="
              (v: boolean) => {
                itemForm.announceBanner = v ? 1 : 0;
              }
            "
          />
        </a-form-item>
        <a-form-item label="广播前缀">
          <a-input v-model="itemForm.announceLabel" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import { Message } from '@arco-design/web-vue';
  import { getCachedIconUrl, onItemIconError } from '@/utils/mapleStoryAPI';
  import { syncSharedIcons } from '@/api/icon';
  import {
    detectItemType,
    deleteItem,
    deleteMachine,
    getItems,
    getMachines,
    import123,
    LotteryItem,
    LotteryMachine,
    findNpcsByItem,
    reloadAll,
    reloadNpc,
    saveItem,
    saveMachine,
  } from '@/api/lottery';

  const { loading, setLoading } = useLoading(false);
  const itemLoading = ref(false);
  const syncingIcons = ref(false);
  const machines = ref<LotteryMachine[]>([]);
  const items = ref<LotteryItem[]>([]);
  const selectedNpcId = ref<number | undefined>();
  const selectedMachineName = ref('');
  const itemDrawerVisible = ref(false);
  const machineVisible = ref(false);
  const itemVisible = ref(false);
  const machineForm = ref<LotteryMachine>({});
  const itemForm = ref<LotteryItem>({});
  const multiDrawsText = ref('1,10');
  const lookupItemId = ref<number | undefined>();
  const lookupResult = ref<
    { npcId: number; machineName?: string; count: number }[]
  >([]);

  const totalWeight = computed(() =>
    items.value.reduce((s, i) => s + (i.weight || 0), 0)
  );

  const weightPermille = (record: LotteryItem) => {
    if (!totalWeight.value) return 0;
    return Math.round(((record.weight || 0) * 10000) / totalWeight.value);
  };

  const rowClass = (record: LotteryMachine) =>
    record.npcId === selectedNpcId.value ? 'row-selected' : '';

  const loadMachines = async () => {
    setLoading(true);
    try {
      const { data } = await getMachines();
      machines.value = data || [];
    } finally {
      setLoading(false);
    }
  };

  const loadItems = async () => {
    if (!selectedNpcId.value) return;
    itemLoading.value = true;
    try {
      const { data } = await getItems(selectedNpcId.value);
      items.value = data || [];
    } finally {
      itemLoading.value = false;
    }
  };

  const openItemDrawer = (record: LotteryMachine) => {
    selectedNpcId.value = record.npcId;
    selectedMachineName.value = record.name || '';
    itemDrawerVisible.value = true;
    loadItems();
  };

  const onSelectMachine = (record: LotteryMachine) => {
    openItemDrawer(record);
  };

  const closeItemDrawer = () => {
    itemDrawerVisible.value = false;
  };

  const createMachine = () => {
    machineForm.value = {
      npcId: undefined,
      name: '',
      enabled: 1,
      multiDraws: '[1,10]',
      costType: 'NX',
      costAmount: 10000,
      comment: '',
    };
    multiDrawsText.value = '1,10';
    machineVisible.value = true;
  };

  const editMachine = (record: LotteryMachine) => {
    machineForm.value = { ...record };
    try {
      const arr = JSON.parse(record.multiDraws || '[1,10]');
      multiDrawsText.value = (arr as number[]).join(',');
    } catch {
      multiDrawsText.value = '1,10';
    }
    machineVisible.value = true;
  };

  const saveMachineSubmit = async () => {
    const parts = multiDrawsText.value
      .split(/[,，\s]+/)
      .map((s) => parseInt(s, 10))
      .filter((n) => !Number.isNaN(n) && n > 0);
    machineForm.value.multiDraws = JSON.stringify(
      parts.length ? parts : [1, 10]
    );
    if (machineForm.value.costType !== 'ITEM') {
      machineForm.value.costItemId = null;
    }
    setLoading(true);
    try {
      await saveMachine(machineForm.value);
      Message.success('抽奖机已保存');
      machineVisible.value = false;
      await loadMachines();
    } finally {
      setLoading(false);
    }
  };

  const onDeleteMachine = async (npcId?: number) => {
    if (!npcId) return;
    await deleteMachine(npcId);
    Message.success('已删除');
    if (selectedNpcId.value === npcId) {
      selectedNpcId.value = undefined;
      selectedMachineName.value = '';
      items.value = [];
      itemDrawerVisible.value = false;
    }
    await loadMachines();
  };

  const onReloadAll = async () => {
    await reloadAll();
    Message.success('已热重载全部');
  };

  const onReloadNpc = async (npcId?: number) => {
    if (!npcId) return;
    await reloadNpc(npcId);
    Message.success(`已重载 NPC ${npcId}`);
  };

  const onImport123 = async () => {
    const npcId = selectedNpcId.value || 9310022;
    setLoading(true);
    try {
      const { data } = await import123({ npcId, replace: true });
      Message.success(
        `导入完成：${data.inserted} 条，无效 ${data.invalid}，注释 ${data.fromComment}`
      );
      selectedNpcId.value = npcId;
      await loadMachines();
      const m = machines.value.find((x) => x.npcId === npcId);
      selectedMachineName.value = m?.name || '';
      itemDrawerVisible.value = true;
      await loadItems();
    } finally {
      setLoading(false);
    }
  };

  const onSyncLotteryIcons = async () => {
    syncingIcons.value = true;
    try {
      const { data } = await syncSharedIcons({
        categories: ['item'],
        fromLottery: true,
        lotteryNpcId: selectedNpcId.value,
        force: false,
      });
      Message.success(
        `图标同步：请求 ${data.requested}，成功 ${data.success}，跳过 ${data.skipped}，失败 ${data.failed}`
      );
      if (itemDrawerVisible.value) {
        await loadItems();
      }
    } catch {
      Message.error('图标同步失败');
    } finally {
      syncingIcons.value = false;
    }
  };

  const onLookupItem = async () => {
    if (!lookupItemId.value) {
      Message.warning('请输入物品ID');
      return;
    }
    const { data } = await findNpcsByItem(lookupItemId.value);
    lookupResult.value = data || [];
    if (!lookupResult.value.length) {
      Message.info('未绑定任何抽奖机');
    }
  };

  const addItem = () => {
    itemForm.value = {
      npcId: selectedNpcId.value,
      itemId: undefined,
      quantity: 1,
      weight: 100,
      itemType: 4,
      sortOrder: 0,
      announce: 0,
      announceChannel: 6,
      announceBanner: 0,
      announceLabel: '',
      randomStats: 0,
      untradeable: 0,
      accountBound: 0,
      uniqueEquip: 0,
      enabled: 1,
    };
    itemVisible.value = true;
  };

  const onItemIdChange = async () => {
    if (!itemForm.value.itemId) return;
    try {
      const { data } = await detectItemType(itemForm.value.itemId);
      itemForm.value.itemType = data.itemType;
    } catch {
      /* ignore */
    }
  };

  const saveNewItem = async () => {
    if (!itemForm.value.itemId || !itemForm.value.npcId) {
      Message.warning('请填写物品ID');
      return;
    }
    itemLoading.value = true;
    try {
      await saveItem(itemForm.value);
      Message.success('已新增');
      itemVisible.value = false;
      await loadItems();
    } finally {
      itemLoading.value = false;
    }
  };

  const saveItemRow = async (record: LotteryItem) => {
    try {
      await saveItem(record);
    } catch {
      Message.error('保存失败');
      await loadItems();
    }
  };

  const onDeleteItem = async (id?: number) => {
    if (!id) return;
    await deleteItem(id);
    Message.success('已删除');
    await loadItems();
  };

  loadMachines();
</script>

<script lang="ts">
  export default { name: 'Lottery' };
</script>

<style lang="less" scoped>
  :deep(.row-selected) {
    background: var(--color-primary-light-1);
  }
</style>
