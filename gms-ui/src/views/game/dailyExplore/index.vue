<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.dailyExplore')">
      <a-tabs v-model:active-key="activeTab">
        <!-- Tab 1: 地图池管理 -->
        <a-tab-pane key="map" :title="$t('dailyExplore.tab.map')">
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space>
                <a-button type="primary" status="success" @click="addMapClick">
                  {{ $t('dailyExplore.map.add') }}
                </a-button>
                <a-button
                  v-if="mapSelectedKeys.length > 0"
                  status="danger"
                  @click="batchDeleteMapClick"
                >
                  {{ $t('button.delete') }}（{{ mapSelectedKeys.length }}）
                </a-button>
                <a-button @click="loadMapData">
                  {{ $t('button.search') }}
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :row-selection="{ type: 'checkbox', showCheckedAll: true }"
            v-model:selected-keys="mapSelectedKeys"
            :loading="mapLoading"
            :data="mapTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column
                :title="$t('dailyExplore.map.id')"
                data-index="id"
                :width="60"
                align="center"
              />
              <a-table-column
                :title="$t('dailyExplore.map.mapId')"
                data-index="mapId"
                :width="120"
                align="center"
              />
              <a-table-column
                :title="$t('dailyExplore.map.sortOrder')"
                data-index="sortOrder"
                :width="70"
                align="center"
              />
              <a-table-column
                :title="$t('dailyExplore.map.enabled')"
                :width="60"
                align="center"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{
                    $t('dailyExplore.yes')
                  }}</a-tag>
                  <a-tag v-else color="gray">{{ $t('dailyExplore.no') }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('dailyExplore.map.operation')"
                :width="140"
                fixed="right"
                align="center"
              >
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button type="text" size="mini" @click="editMapClick(record)">
                      {{ $t('dailyExplore.button.edit') }}
                    </a-button>
                    <a-popconfirm
                      :content="$t('dailyExplore.map.delete.confirm')"
                      position="top"
                      @ok="deleteMapClick(record.id)"
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
        </a-tab-pane>

        <!-- Tab 2: 每轮随机奖励 -->
        <a-tab-pane key="reward" :title="$t('dailyExplore.tab.reward')">
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space>
                <a-button type="primary" status="success" @click="addRewardClick">
                  {{ $t('dailyExplore.reward.add') }}
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :loading="rewardLoading"
            :data="rewardTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column :title="$t('dailyExplore.reward.id')" data-index="id" :width="60" align="center" />
              <a-table-column :title="$t('dailyExplore.reward.itemId')" data-index="itemId" :width="100" align="center" />
              <a-table-column :title="$t('dailyExplore.reward.itemName')" :width="180" align="center">
                <template #cell="{ record }">
                  <a-button v-if="record.itemId === 0" type="text" size="mini" status="warning">金币</a-button>
                  <a-popover v-else>
                    <a-button type="text" size="mini">{{ record.itemName }}</a-button>
                    <template #content>
                      <img :src="getIconUrl('item', record.itemId)" alt="" />
                    </template>
                  </a-popover>
                </template>
              </a-table-column>
              <a-table-column :title="$t('dailyExplore.reward.minQty')" data-index="minQuantity" :width="70" align="center" />
              <a-table-column :title="$t('dailyExplore.reward.maxQty')" data-index="maxQuantity" :width="70" align="center" />
              <a-table-column :title="$t('dailyExplore.reward.weight')" data-index="weight" :width="70" align="center" />
              <a-table-column :title="$t('dailyExplore.reward.sortOrder')" data-index="sortOrder" :width="70" align="center" />
              <a-table-column :title="$t('dailyExplore.reward.enabled')" :width="60" align="center">
                <template #cell="{ record }">
                  <a-tag v-if="record.enabled === 1" color="green">{{ $t('dailyExplore.yes') }}</a-tag>
                  <a-tag v-else color="gray">{{ $t('dailyExplore.no') }}</a-tag>
                </template>
              </a-table-column>
              <a-table-column :title="$t('dailyExplore.map.operation')" :width="140" fixed="right" align="center">
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button type="text" size="mini" @click="editRewardClick(record)">{{ $t('dailyExplore.button.edit') }}</a-button>
                    <a-popconfirm :content="$t('dailyExplore.reward.delete.confirm')" position="top" @ok="deleteRewardClick(record.id)">
                      <a-button type="text" size="mini" status="danger">{{ $t('button.delete') }}</a-button>
                    </a-popconfirm>
                  </a-space>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-tab-pane>

        <!-- Tab 3: 完成奖励 -->
        <a-tab-pane key="finalReward" :title="$t('dailyExplore.tab.finalReward')">
          <a-row style="margin-bottom: 16px">
            <a-col>
              <a-space>
                <a-button type="primary" status="success" @click="addFinalRewardClick">
                  {{ $t('dailyExplore.finalReward.add') }}
                </a-button>
              </a-space>
            </a-col>
          </a-row>

          <a-table
            row-key="id"
            :loading="finalRewardLoading"
            :data="finalRewardTableData"
            :pagination="false"
            :bordered="{ cell: true }"
          >
            <template #columns>
              <a-table-column :title="$t('dailyExplore.finalReward.id')" data-index="id" :width="60" align="center" />
              <a-table-column :title="$t('dailyExplore.finalReward.exploreCount')" data-index="exploreCount" :width="80" align="center" />
              <a-table-column :title="$t('dailyExplore.finalReward.desc')" data-index="rewardDesc" :width="150" align="center" />
              <a-table-column :title="$t('dailyExplore.finalReward.itemId')" data-index="itemId" :width="100" align="center" />
              <a-table-column :title="$t('dailyExplore.finalReward.itemName')" :width="200" align="center">
                <template #cell="{ record }">
                  <a-button v-if="record.itemId === 0" type="text" size="mini" status="warning">金币</a-button>
                  <a-popover v-else>
                    <a-button type="text" size="mini">{{ record.itemName }}</a-button>
                    <template #content>
                      <img :src="getIconUrl('item', record.itemId)" alt="" />
                    </template>
                  </a-popover>
                </template>
              </a-table-column>
              <a-table-column :title="$t('dailyExplore.finalReward.quantity')" data-index="quantity" :width="80" align="center" />
              <a-table-column :title="$t('dailyExplore.finalReward.sortOrder')" data-index="sortOrder" :width="70" align="center" />
              <a-table-column :title="$t('dailyExplore.map.operation')" :width="140" fixed="right" align="center">
                <template #cell="{ record }">
                  <a-space :size="0">
                    <a-button type="text" size="mini" @click="editFinalRewardClick(record)">{{ $t('dailyExplore.button.edit') }}</a-button>
                    <a-popconfirm :content="$t('dailyExplore.finalReward.delete.confirm')" position="top" @ok="deleteFinalRewardClick(record.id)">
                      <a-button type="text" size="mini" status="danger">{{ $t('button.delete') }}</a-button>
                    </a-popconfirm>
                  </a-space>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 地图编辑弹窗 -->
    <a-modal v-model:visible="mapModalVisible" :title="mapModalTitle" :width="400" @ok="saveMapClick" @cancel="onMapCancel">
      <a-form :model="mapForm" layout="vertical">
        <a-form-item :label="$t('dailyExplore.map.mapId')">
          <a-input-number v-model="mapForm.mapId" :min="1" :max="999999999" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.map.sortOrder')">
          <a-input-number v-model="mapForm.sortOrder" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.map.enabled')">
          <a-switch v-model="mapEnabledBool" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 每轮奖励编辑弹窗 -->
    <a-modal v-model:visible="rewardModalVisible" :title="rewardModalTitle" :width="400" @ok="saveRewardClick" @cancel="onRewardCancel">
      <a-form :model="rewardForm" layout="vertical">
        <a-form-item :label="$t('dailyExplore.reward.itemId')">
          <a-input-number v-model="rewardForm.itemId" :min="0" :max="5999999" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.reward.minQty')">
          <a-input-number v-model="rewardForm.minQuantity" :min="1" :max="99999" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.reward.maxQty')">
          <a-input-number v-model="rewardForm.maxQuantity" :min="1" :max="99999" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.reward.weight')">
          <a-input-number v-model="rewardForm.weight" :min="0" :max="999" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.reward.sortOrder')">
          <a-input-number v-model="rewardForm.sortOrder" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.reward.enabled')">
          <a-switch v-model="rewardEnabledBool" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 完成奖励编辑弹窗 -->
    <a-modal v-model:visible="finalRewardModalVisible" :title="finalRewardModalTitle" :width="400" @ok="saveFinalRewardClick" @cancel="onFinalRewardCancel">
      <a-form :model="finalRewardForm" layout="vertical">
        <a-form-item :label="$t('dailyExplore.finalReward.exploreCount')">
          <a-input-number v-model="finalRewardForm.exploreCount" :min="1" :max="999" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.finalReward.desc')">
          <a-input v-model="finalRewardForm.rewardDesc" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.finalReward.itemId')">
          <a-input-number v-model="finalRewardForm.itemId" :min="0" :max="5999999" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.finalReward.quantity')">
          <a-input-number v-model="finalRewardForm.quantity" :min="1" :max="99999" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('dailyExplore.finalReward.sortOrder')">
          <a-input-number v-model="finalRewardForm.sortOrder" :min="0" style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { useI18n } from 'vue-i18n';
  import useLoading from '@/hooks/loading';
  import type { DailyExploreMap, DailyExploreReward, DailyExploreFinalReward } from '@/api/dailyExplore';
  import {
    getMapList, getMap, saveMap, deleteMap, deleteMapBatch,
    getRewardList, saveReward, deleteReward,
    getFinalRewardList, saveFinalReward, deleteFinalReward,
  } from '@/api/dailyExplore';
  import { Message } from '@arco-design/web-vue';
  import { getIconUrl } from '@/utils/mapleStoryAPI';

  const { t } = useI18n();
  const activeTab = ref('map');

  // ==================== 地图池管理 ====================
  const { loading: mapLoading, setLoading: setMapLoading } = useLoading(false);
  const mapTableData = ref<DailyExploreMap[]>([]);
  const mapSelectedKeys = ref<number[]>([]);

  const loadMapData = async () => {
    setMapLoading(true);
    try {
      const { data } = await getMapList();
      mapTableData.value = (data as unknown as DailyExploreMap[]) || [];
    } finally {
      setMapLoading(false);
    }
  };

  const mapModalVisible = ref(false);
  const editingMapId = ref<number | null>(null);
  const mapModalTitle = computed(() =>
    editingMapId.value ? t('dailyExplore.button.edit') : t('dailyExplore.map.add')
  );

  const mapForm = ref<DailyExploreMap>({ mapId: undefined, sortOrder: 0, enabled: 1 });
  const mapEnabledBool = computed({
    get: () => mapForm.value.enabled === 1,
    set: (v: boolean) => { mapForm.value.enabled = v ? 1 : 0; },
  });

  const resetMapForm = () => {
    mapForm.value = { mapId: undefined, sortOrder: 0, enabled: 1 };
    editingMapId.value = null;
  };

  const addMapClick = () => { resetMapForm(); mapModalVisible.value = true; };

  const editMapClick = async (record: DailyExploreMap) => {
    if (record.id == null) return;
    setMapLoading(true);
    try {
      const { data } = await getMap(record.id);
      const d = data as unknown as DailyExploreMap;
      mapForm.value = { id: d.id, mapId: d.mapId, sortOrder: d.sortOrder, enabled: d.enabled ?? 1 };
      editingMapId.value = d.id ?? null;
      mapModalVisible.value = true;
    } finally {
      setMapLoading(false);
    }
  };

  const saveMapClick = async () => {
    if (!mapForm.value.mapId) { Message.warning(t('dailyExplore.validate.mapId')); return; }
    setMapLoading(true);
    try {
      await saveMap(mapForm.value);
      Message.success(t('message.success'));
      mapModalVisible.value = false;
      resetMapForm();
      await loadMapData();
    } finally {
      setMapLoading(false);
    }
  };

  const deleteMapClick = async (id: number) => {
    setMapLoading(true);
    try {
      await deleteMap(id);
      Message.success(t('message.success'));
      await loadMapData();
    } finally {
      setMapLoading(false);
    }
  };

  const batchDeleteMapClick = () => {
    if (mapSelectedKeys.value.length === 0) { Message.warning(t('dailyExplore.validate.selectFirst')); return; }
    if (!window.confirm(t('dailyExplore.map.deleteBatch.confirm'))) return;
    setMapLoading(true);
    deleteMapBatch(mapSelectedKeys.value as number[])
      .then(() => {
        Message.success(t('message.success'));
        mapSelectedKeys.value = [];
        return loadMapData();
      })
      .finally(() => setMapLoading(false));
  };

  const onMapCancel = () => { mapModalVisible.value = false; };

  // ==================== 每轮随机奖励 ====================
  const { loading: rewardLoading, setLoading: setRewardLoading } = useLoading(false);
  const rewardTableData = ref<DailyExploreReward[]>([]);

  const loadRewardData = async () => {
    setRewardLoading(true);
    try {
      const { data } = await getRewardList();
      rewardTableData.value = (data as unknown as DailyExploreReward[]) || [];
    } finally {
      setRewardLoading(false);
    }
  };

  const rewardModalVisible = ref(false);
  const editingRewardId = ref<number | null>(null);
  const rewardModalTitle = computed(() =>
    editingRewardId.value ? t('dailyExplore.button.edit') : t('dailyExplore.reward.add')
  );

  const rewardForm = ref<DailyExploreReward>({
    itemId: 0, minQuantity: 1, maxQuantity: 1, weight: 1, sortOrder: 0, enabled: 1,
  });
  const rewardEnabledBool = computed({
    get: () => rewardForm.value.enabled === 1,
    set: (v: boolean) => { rewardForm.value.enabled = v ? 1 : 0; },
  });

  const resetRewardForm = () => {
    rewardForm.value = { itemId: 0, minQuantity: 1, maxQuantity: 1, weight: 1, sortOrder: 0, enabled: 1 };
    editingRewardId.value = null;
  };

  const addRewardClick = () => { resetRewardForm(); rewardModalVisible.value = true; };

  const editRewardClick = (record: DailyExploreReward) => {
    rewardForm.value = { ...record };
    editingRewardId.value = record.id ?? null;
    rewardModalVisible.value = true;
  };

  const saveRewardClick = async () => {
    if (!rewardForm.value.itemId && rewardForm.value.itemId !== 0) { Message.warning(t('dailyExplore.validate.itemId')); return; }
    setRewardLoading(true);
    try {
      await saveReward(rewardForm.value);
      Message.success(t('message.success'));
      rewardModalVisible.value = false;
      resetRewardForm();
      await loadRewardData();
    } finally {
      setRewardLoading(false);
    }
  };

  const deleteRewardClick = async (id: number) => {
    setRewardLoading(true);
    try {
      await deleteReward(id);
      Message.success(t('message.success'));
      await loadRewardData();
    } finally {
      setRewardLoading(false);
    }
  };

  const onRewardCancel = () => { rewardModalVisible.value = false; };

  // ==================== 完成奖励 ====================
  const { loading: finalRewardLoading, setLoading: setFinalRewardLoading } = useLoading(false);
  const finalRewardTableData = ref<DailyExploreFinalReward[]>([]);

  const loadFinalRewardData = async () => {
    setFinalRewardLoading(true);
    try {
      const { data } = await getFinalRewardList();
      finalRewardTableData.value = (data as unknown as DailyExploreFinalReward[]) || [];
    } finally {
      setFinalRewardLoading(false);
    }
  };

  const finalRewardModalVisible = ref(false);
  const editingFinalRewardId = ref<number | null>(null);
  const finalRewardModalTitle = computed(() =>
    editingFinalRewardId.value ? t('dailyExplore.button.edit') : t('dailyExplore.finalReward.add')
  );

  const finalRewardForm = ref<DailyExploreFinalReward>({
    exploreCount: undefined, rewardDesc: '', itemId: 0, quantity: 1, sortOrder: 0,
  });

  const resetFinalRewardForm = () => {
    finalRewardForm.value = { exploreCount: undefined, rewardDesc: '', itemId: 0, quantity: 1, sortOrder: 0 };
    editingFinalRewardId.value = null;
  };

  const addFinalRewardClick = () => { resetFinalRewardForm(); finalRewardModalVisible.value = true; };

  const editFinalRewardClick = (record: DailyExploreFinalReward) => {
    finalRewardForm.value = { ...record };
    editingFinalRewardId.value = record.id ?? null;
    finalRewardModalVisible.value = true;
  };

  const saveFinalRewardClick = async () => {
    if (!finalRewardForm.value.exploreCount) { Message.warning(t('dailyExplore.validate.exploreCount')); return; }
    setFinalRewardLoading(true);
    try {
      await saveFinalReward(finalRewardForm.value);
      Message.success(t('message.success'));
      finalRewardModalVisible.value = false;
      resetFinalRewardForm();
      await loadFinalRewardData();
    } finally {
      setFinalRewardLoading(false);
    }
  };

  const deleteFinalRewardClick = async (id: number) => {
    setFinalRewardLoading(true);
    try {
      await deleteFinalReward(id);
      Message.success(t('message.success'));
      await loadFinalRewardData();
    } finally {
      setFinalRewardLoading(false);
    }
  };

  const onFinalRewardCancel = () => { finalRewardModalVisible.value = false; };

  loadMapData();
  loadRewardData();
  loadFinalRewardData();
</script>

<script lang="ts">
  export default { name: 'DailyExplore' };
</script>

<style lang="less" scoped>
  :deep(.arco-card-body, .arco-row) {
    width: 100%;
  }
</style>
