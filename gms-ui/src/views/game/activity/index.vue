<template>
  <div class="container">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.game.activity')">
      <a-alert type="info" style="margin-bottom: 12px">
        {{ $t('activity.hint') }}
      </a-alert>
      <a-space style="margin-bottom: 12px">
        <a-button :loading="loading" @click="refresh">
          {{ $t('activity.refresh') }}
        </a-button>
        <a-button type="outline" @click="scheduleVisible = true">
          {{ $t('activity.schedule') }}
        </a-button>
        <a-button type="outline" @click="openRewardModal()">
          {{ $t('activity.reward') }}
        </a-button>
      </a-space>
      <a-table
        row-key="code"
        :loading="loading"
        :data="rows"
        column-resizable
        :pagination="false"
        :scroll="{ x: 1400 }"
        :bordered="{ cell: true }"
      >
        <template #columns>
          <a-table-column :title="$t('activity.column.activity')" :width="260">
            <template #cell="{ record }">
              <div>
                <span>{{ record.nameZh || record.nameEn }}</span>
                <a-tag
                  v-if="record.teamEvent"
                  color="arcoblue"
                  size="small"
                  style="margin-left: 6px"
                >
                  {{ $t('activity.team') }}
                </a-tag>
              </div>
              <div style="color: var(--color-text-3); font-size: 12px">
                {{ record.code }} · {{ record.lobbyMapId }}
                <span v-if="record.category">
                  · {{ $t(`activity.category.${record.category}`) }}
                </span>
              </div>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('activity.column.enabled')"
            :width="90"
            align="center"
          >
            <template #cell="{ record }">
              <a-switch
                :model-value="!!record.enabled"
                @change="(v: boolean) => onToggleEnabled(record, v)"
              />
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('activity.column.status')"
            :width="110"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag :color="statusColor(record.status)">
                {{ $t(`activity.status.${record.status || 'IDLE'}`) }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column :title="$t('activity.column.players')" :width="160">
            <template #cell="{ record }">
              <div>
                {{
                  $t('activity.lobbyArena', [
                    record.lobbyCount ?? 0,
                    record.arenaCount ?? 0,
                  ])
                }}
              </div>
              <div style="color: var(--color-text-3); font-size: 12px">
                {{ $t('activity.registered', [record.registeredCount ?? 0]) }}
                /
                {{ record.maxPlayers ?? record.defaultMaxPlayers ?? '-' }}
              </div>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('activity.column.channel')"
            :width="90"
            align="center"
          >
            <template #cell="{ record }">
              {{ record.channelId ?? '-' }}
            </template>
          </a-table-column>
          <a-table-column :title="$t('activity.column.extra')" :width="180">
            <template #cell="{ record }">
              {{ record.plannedStartAt || record.extraInfo || '' }}
            </template>
          </a-table-column>
          <a-table-column :title="$t('activity.column.ops')" :width="520">
            <template #cell="{ record }">
              <a-space wrap>
                <a-input-number
                  v-model="limits[record.code]"
                  :min="1"
                  :max="200"
                  size="mini"
                  style="width: 72px"
                />
                <a-input-number
                  v-model="channels[record.code]"
                  :min="1"
                  :max="30"
                  size="mini"
                  style="width: 64px"
                  :placeholder="$t('activity.form.channel')"
                />
                <a-button
                  type="primary"
                  size="mini"
                  :disabled="!record.enabled || isActive(record)"
                  @click="onOpenReg(record)"
                >
                  {{ $t('activity.openReg') }}
                </a-button>
                <a-button
                  type="primary"
                  size="mini"
                  :disabled="!isActive(record)"
                  @click="runAction(closeRegistration, record)"
                >
                  {{ $t('activity.closeReg') }}
                </a-button>
                <a-button
                  status="success"
                  size="mini"
                  :disabled="!isActive(record) || record.status === 'RUNNING'"
                  @click="runAction(startActivity, record)"
                >
                  {{ $t('activity.start') }}
                </a-button>
                <a-button
                  status="danger"
                  size="mini"
                  :disabled="!isActive(record)"
                  @click="runAction(stopActivity, record)"
                >
                  {{ $t('activity.stop') }}
                </a-button>
                <a-button
                  status="danger"
                  size="mini"
                  :disabled="!isActive(record)"
                  @click="onStopClear(record)"
                >
                  {{ $t('activity.stopClear') }}
                </a-button>
                <a-button
                  status="danger"
                  size="mini"
                  type="text"
                  :disabled="!isActive(record)"
                  @click="runAction(warpAllOut, record)"
                >
                  {{ $t('activity.warpOut') }}
                </a-button>
                <a-button
                  size="mini"
                  type="outline"
                  :disabled="!record.sessionId"
                  @click="onSettle(record)"
                >
                  {{ $t('activity.reward.settle') }}
                </a-button>
                <a-button size="mini" @click="openRewardModal(record.code)">
                  {{ $t('activity.reward') }}
                </a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="openVisible"
      :title="$t('activity.openReg')"
      @ok="confirmOpen"
    >
      <a-form :model="openForm" layout="vertical">
        <a-form-item :label="$t('activity.form.world')">
          <a-input-number v-model="openForm.worldId" :min="0" />
        </a-form-item>
        <a-form-item :label="$t('activity.form.channel')">
          <a-input-number v-model="openForm.channelId" :min="1" />
        </a-form-item>
        <a-form-item :label="$t('activity.form.maxPlayers')">
          <a-input-number v-model="openForm.maxPlayers" :min="1" />
        </a-form-item>
        <a-form-item :label="$t('activity.form.plannedStart')">
          <a-input
            v-model="openForm.plannedStartAt"
            placeholder="yyyy-MM-dd HH:mm:ss"
            allow-clear
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:visible="scheduleVisible"
      :title="$t('activity.scheduleTitle')"
      :footer="false"
      width="920px"
      @open="loadSchedules"
    >
      <a-space style="margin-bottom: 12px">
        <a-button type="primary" size="small" @click="beginEditSchedule()">
          {{ $t('activity.schedule.add') }}
        </a-button>
        <a-button size="small" @click="loadSchedules">
          {{ $t('activity.refresh') }}
        </a-button>
      </a-space>
      <a-table
        row-key="id"
        :data="schedules"
        :pagination="false"
        :loading="scheduleLoading"
        size="small"
      >
        <template #columns>
          <a-table-column title="ID" data-index="id" :width="70" />
          <a-table-column
            :title="$t('activity.column.activity')"
            data-index="activityCode"
            :width="140"
          />
          <a-table-column
            :title="$t('activity.column.channel')"
            data-index="channelId"
            :width="70"
          />
          <a-table-column :title="$t('activity.schedule.type')" :width="90">
            <template #cell="{ record }">
              {{ $t(`activity.schedule.${record.scheduleType}`) }}
            </template>
          </a-table-column>
          <a-table-column :title="$t('activity.schedule.next')" :width="170">
            <template #cell="{ record }">
              {{ record.nextRunAt || record.startAt || record.cronTime || '' }}
            </template>
          </a-table-column>
          <a-table-column :title="$t('activity.column.ops')" :width="160">
            <template #cell="{ record }">
              <a-space>
                <a-button size="mini" @click="beginEditSchedule(record)">
                  {{ $t('activity.schedule.save') }}
                </a-button>
                <a-button
                  size="mini"
                  status="danger"
                  @click="onDeleteSchedule(record)"
                >
                  {{ $t('activity.schedule.delete') }}
                </a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>

      <a-divider />
      <a-form :model="scheduleForm" layout="vertical">
        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-item :label="$t('activity.column.activity')">
              <a-select v-model="scheduleForm.activityCode" allow-search>
                <a-option
                  v-for="r in rows"
                  :key="r.code"
                  :value="r.code"
                  :label="r.nameZh || r.nameEn"
                />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.form.channel')">
              <a-input-number v-model="scheduleForm.channelId" :min="1" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.schedule.type')">
              <a-select v-model="scheduleForm.scheduleType">
                <a-option value="ONCE">{{
                  $t('activity.schedule.ONCE')
                }}</a-option>
                <a-option value="DAILY">{{
                  $t('activity.schedule.DAILY')
                }}</a-option>
                <a-option value="WEEKLY">{{
                  $t('activity.schedule.WEEKLY')
                }}</a-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.form.maxPlayers')">
              <a-input-number v-model="scheduleForm.maxPlayers" :min="1" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.column.enabled')">
              <a-switch v-model="scheduleForm.enabled" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col v-if="scheduleForm.scheduleType === 'ONCE'" :span="8">
            <a-form-item :label="$t('activity.schedule.startAt')">
              <a-input
                v-model="scheduleForm.startAt"
                placeholder="yyyy-MM-dd HH:mm:ss"
              />
            </a-form-item>
          </a-col>
          <a-col v-else :span="8">
            <a-form-item :label="$t('activity.schedule.cronTime')">
              <a-input v-model="scheduleForm.cronTime" placeholder="20:00:00" />
            </a-form-item>
          </a-col>
          <a-col v-if="scheduleForm.scheduleType === 'WEEKLY'" :span="8">
            <a-form-item :label="$t('activity.schedule.days')">
              <a-input
                v-model="scheduleForm.daysOfWeek"
                placeholder="1,2,3,4,5"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-button
          type="primary"
          :loading="scheduleSaving"
          @click="onSaveSchedule"
        >
          {{ $t('activity.schedule.save') }}
        </a-button>
      </a-form>
    </a-modal>

    <a-modal
      v-model:visible="rewardVisible"
      :title="$t('activity.rewardTitle')"
      :footer="false"
      width="980px"
    >
      <a-alert type="info" style="margin-bottom: 12px">
        {{ $t('activity.reward.hint') }}
      </a-alert>
      <a-space style="margin-bottom: 12px">
        <a-select
          v-model="rewardFilterCode"
          allow-search
          style="width: 220px"
          @change="loadRewards"
        >
          <a-option
            v-for="r in rows"
            :key="r.code"
            :value="r.code"
            :label="r.nameZh || r.nameEn"
          />
        </a-select>
        <a-button type="primary" size="small" @click="beginEditReward()">
          {{ $t('activity.schedule.add') }}
        </a-button>
        <a-button size="small" @click="loadRewards">
          {{ $t('activity.refresh') }}
        </a-button>
      </a-space>
      <a-table
        row-key="id"
        :data="rewardTiers"
        :loading="rewardLoading"
        :pagination="false"
        size="small"
        :scroll="{ x: 900 }"
      >
        <template #columns>
          <a-table-column
            :title="$t('activity.reward.tierCode')"
            data-index="tierCode"
            :width="120"
          />
          <a-table-column
            :title="$t('activity.reward.tierName')"
            data-index="tierName"
            :width="120"
          />
          <a-table-column
            :title="$t('activity.reward.priority')"
            data-index="priority"
            :width="80"
          />
          <a-table-column
            :title="$t('activity.reward.grantMode')"
            data-index="grantMode"
            :width="110"
          />
          <a-table-column
            :title="$t('activity.reward.match')"
            data-index="matchJson"
            :width="220"
          />
          <a-table-column :title="$t('activity.column.ops')" :width="140">
            <template #cell="{ record }">
              <a-space>
                <a-button size="mini" @click="beginEditReward(record)">
                  {{ $t('activity.schedule.save') }}
                </a-button>
                <a-button
                  size="mini"
                  status="danger"
                  @click="onDeleteReward(record)"
                >
                  {{ $t('activity.schedule.delete') }}
                </a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
      <a-divider />
      <a-form :model="rewardForm" layout="vertical">
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item :label="$t('activity.reward.tierCode')">
              <a-input v-model="rewardForm.tierCode" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('activity.reward.tierName')">
              <a-input v-model="rewardForm.tierName" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.reward.priority')">
              <a-input-number v-model="rewardForm.priority" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.reward.group')">
              <a-input v-model="rewardForm.exclusiveGroup" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.reward.grantMode')">
              <a-select v-model="rewardForm.grantMode">
                <a-option value="AUTO_BAG">AUTO_BAG</a-option>
                <a-option value="AUTO_MAIL">AUTO_MAIL</a-option>
                <a-option value="CLAIM_NPC">CLAIM_NPC</a-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item :label="$t('activity.reward.match')">
          <a-input v-model="rewardForm.matchJson" />
        </a-form-item>
        <a-row :gutter="12">
          <a-col :span="4">
            <a-form-item :label="$t('activity.reward.mesos')">
              <a-input-number v-model="rewardForm.mesos" :min="0" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.reward.exp')">
              <a-input-number v-model="rewardForm.exp" :min="0" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.reward.item')">
              <a-input-number v-model="rewardForm.itemId" :min="0" />
            </a-form-item>
          </a-col>
          <a-col :span="3">
            <a-form-item label="qty">
              <a-input-number v-model="rewardForm.itemQty" :min="0" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.reward.announce')">
              <a-switch v-model="rewardForm.announceName" />
            </a-form-item>
          </a-col>
          <a-col :span="4">
            <a-form-item :label="$t('activity.column.enabled')">
              <a-switch v-model="rewardForm.enabled" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-button type="primary" :loading="rewardSaving" @click="onSaveReward">
          {{ $t('activity.schedule.save') }}
        </a-button>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { onMounted, onUnmounted, reactive, ref } from 'vue';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import {
    ActivityRewardTier,
    ActivitySchedule,
    ActivityStatus,
    closeRegistration,
    deleteRewardTier,
    deleteSchedule,
    listActivities,
    listRewardTiers,
    listSchedules,
    openRegistration,
    saveRewardTier,
    saveSchedule,
    setActivityEnabled,
    settleActivity,
    startActivity,
    stopActivity,
    stopAndClearActivity,
    warpAllOut,
  } from '@/api/activity';

  const { t } = useI18n();
  const loading = ref(false);
  const rows = ref<ActivityStatus[]>([]);
  const limits = reactive<Record<string, number>>({});
  const channels = reactive<Record<string, number>>({});
  let timer: number | undefined;

  const openVisible = ref(false);
  const openForm = reactive({
    code: '',
    worldId: 0,
    channelId: 1,
    maxPlayers: 30,
    plannedStartAt: '',
  });

  const scheduleVisible = ref(false);
  const scheduleLoading = ref(false);
  const scheduleSaving = ref(false);
  const schedules = ref<ActivitySchedule[]>([]);
  const scheduleForm = reactive<ActivitySchedule>({
    activityCode: '',
    worldId: 0,
    channelId: 1,
    scheduleType: 'ONCE',
    startAt: '',
    cronTime: '20:00:00',
    daysOfWeek: '1,2,3,4,5,6,7',
    maxPlayers: 30,
    notifyMinutes: 30,
    notifyIntervalSec: 60,
    prewarpMinutes: 5,
    enabled: true,
  });

  const isActive = (record: ActivityStatus) =>
    !!record.status && record.status !== 'IDLE' && record.status !== 'STOPPED';

  const statusColor = (status?: string) => {
    switch (status) {
      case 'RUNNING':
        return 'green';
      case 'REGISTERING':
      case 'PREWARP':
        return 'arcoblue';
      case 'NOTIFYING':
        return 'orangered';
      default:
        return 'gray';
    }
  };

  const refresh = async () => {
    loading.value = true;
    try {
      const { data } = await listActivities();
      const list = (data || []) as ActivityStatus[];
      rows.value = list;
      list.forEach((item) => {
        if (limits[item.code] == null) {
          limits[item.code] = item.defaultMaxPlayers || 30;
        }
        if (channels[item.code] == null) {
          channels[item.code] = item.channelId || 1;
        }
      });
    } finally {
      loading.value = false;
    }
  };

  const payloadOf = (record: ActivityStatus) => ({
    code: record.code,
    worldId: record.worldId ?? 0,
    channelId: record.channelId ?? channels[record.code] ?? 1,
    maxPlayers: limits[record.code] ?? record.defaultMaxPlayers ?? 30,
  });

  const runAction = async (
    fn: (data: ReturnType<typeof payloadOf>) => Promise<unknown>,
    record: ActivityStatus
  ) => {
    await fn(payloadOf(record));
    Message.success(t('message.success'));
    await refresh();
  };

  const onToggleEnabled = async (record: ActivityStatus, enabled: boolean) => {
    await setActivityEnabled({ code: record.code, enabled });
    Message.success(t('message.success'));
    await refresh();
  };

  const onOpenReg = (record: ActivityStatus) => {
    openForm.code = record.code;
    openForm.worldId = 0;
    openForm.channelId = channels[record.code] || 1;
    openForm.maxPlayers = limits[record.code] || record.defaultMaxPlayers || 30;
    openForm.plannedStartAt = '';
    openVisible.value = true;
  };

  const confirmOpen = async () => {
    await openRegistration({
      code: openForm.code,
      worldId: openForm.worldId,
      channelId: openForm.channelId,
      maxPlayers: openForm.maxPlayers,
      plannedStartAt: openForm.plannedStartAt || undefined,
    });
    openVisible.value = false;
    Message.success(t('message.success'));
    await refresh();
  };

  const onStopClear = (record: ActivityStatus) => {
    Modal.confirm({
      title: t('activity.stopClear'),
      content: t('activity.confirm.stopClear'),
      onOk: async () => {
        await stopAndClearActivity(payloadOf(record));
        Message.success(t('message.success'));
        await refresh();
      },
    });
  };

  const loadSchedules = async () => {
    scheduleLoading.value = true;
    try {
      const { data } = await listSchedules();
      schedules.value = (data || []) as ActivitySchedule[];
    } finally {
      scheduleLoading.value = false;
    }
  };

  const beginEditSchedule = (record?: ActivitySchedule) => {
    if (record) {
      Object.assign(scheduleForm, record);
    } else {
      Object.assign(scheduleForm, {
        id: undefined,
        activityCode: rows.value[0]?.code || '',
        worldId: 0,
        channelId: 1,
        scheduleType: 'ONCE',
        startAt: '',
        cronTime: '20:00:00',
        daysOfWeek: '1,2,3,4,5,6,7',
        maxPlayers: 30,
        notifyMinutes: 30,
        notifyIntervalSec: 60,
        prewarpMinutes: 5,
        enabled: true,
      });
    }
  };

  const onSaveSchedule = async () => {
    scheduleSaving.value = true;
    try {
      await saveSchedule({ ...scheduleForm });
      Message.success(t('message.success'));
      await loadSchedules();
    } finally {
      scheduleSaving.value = false;
    }
  };

  const onDeleteSchedule = async (record: ActivitySchedule) => {
    if (!record.id) return;
    await deleteSchedule({ id: record.id });
    Message.success(t('message.success'));
    await loadSchedules();
  };

  const rewardVisible = ref(false);
  const rewardLoading = ref(false);
  const rewardSaving = ref(false);
  const rewardFilterCode = ref('');
  const rewardTiers = ref<ActivityRewardTier[]>([]);
  const rewardForm = reactive<ActivityRewardTier>({
    activityCode: '',
    tierCode: '',
    tierName: '',
    priority: 100,
    exclusiveGroup: '',
    matchJson: '{"outcomes":["WIN"]}',
    grantMode: 'CLAIM_NPC',
    mesos: 0,
    exp: 0,
    itemId: 0,
    itemQty: 0,
    item2Id: 0,
    item2Qty: 0,
    announceName: false,
    enabled: true,
  });

  const openRewardModal = async (code?: string) => {
    rewardFilterCode.value = code || rows.value[0]?.code || '';
    rewardVisible.value = true;
    await loadRewards();
  };

  const loadRewards = async () => {
    rewardLoading.value = true;
    try {
      const { data } = await listRewardTiers(
        rewardFilterCode.value || undefined
      );
      rewardTiers.value = (data || []) as ActivityRewardTier[];
    } finally {
      rewardLoading.value = false;
    }
  };

  const beginEditReward = (record?: ActivityRewardTier) => {
    if (record) {
      Object.assign(rewardForm, record);
    } else {
      Object.assign(rewardForm, {
        id: undefined,
        activityCode: rewardFilterCode.value,
        tierCode: '',
        tierName: '',
        priority: 100,
        exclusiveGroup: '',
        matchJson: '{"outcomes":["PARTICIPATED","WIN","LOSE","COMPLETE"]}',
        grantMode: 'AUTO_BAG',
        mesos: 0,
        exp: 0,
        itemId: 0,
        itemQty: 0,
        item2Id: 0,
        item2Qty: 0,
        announceName: false,
        enabled: true,
      });
    }
  };

  const onSaveReward = async () => {
    rewardSaving.value = true;
    try {
      rewardForm.activityCode = rewardFilterCode.value;
      await saveRewardTier({ ...rewardForm });
      Message.success(t('message.success'));
      await loadRewards();
    } finally {
      rewardSaving.value = false;
    }
  };

  const onDeleteReward = async (record: ActivityRewardTier) => {
    if (!record.id) return;
    await deleteRewardTier({ id: record.id });
    Message.success(t('message.success'));
    await loadRewards();
  };

  const onSettle = async (record: ActivityStatus) => {
    if (!record.sessionId) return;
    await settleActivity({
      sessionId: record.sessionId,
      code: record.code,
      worldId: record.worldId,
      channelId: record.channelId,
    });
    Message.success(t('message.success'));
    await refresh();
  };

  onMounted(async () => {
    await refresh();
    timer = window.setInterval(refresh, 5000);
  });

  onUnmounted(() => {
    if (timer) window.clearInterval(timer);
  });
</script>

<script lang="ts">
  export default {
    name: 'GameActivity',
  };
</script>
