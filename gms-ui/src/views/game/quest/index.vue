<template>
  <PageContainer :title="$t('menu.game.quest')">
    <ProCard>
      <a-alert type="info" class="bd-page-toolbar" show-icon>
        {{ $t('quest.hint') }}
      </a-alert>

      <a-row :gutter="8" class="search-row">
        <a-col :xs="24" :sm="8" :md="4">
          <a-input-number
            v-model="condition.questId"
            :placeholder="$t('quest.search.questId')"
            allow-clear
            hide-button
            style="width: 100%"
          />
        </a-col>
        <a-col :xs="24" :sm="8" :md="4">
          <a-input
            v-model="condition.name"
            :placeholder="$t('quest.search.name')"
            allow-clear
            @keydown.enter="loadList"
          />
        </a-col>
        <a-col :xs="24" :sm="8" :md="4">
          <a-input-number
            v-model="condition.npcId"
            :placeholder="$t('quest.search.npcId')"
            allow-clear
            hide-button
            style="width: 100%"
          />
        </a-col>
        <a-col :xs="24" :sm="24" :md="12">
          <a-space wrap>
            <a-button type="primary" :loading="loading" @click="loadList">
              {{ $t('quest.search.query') }}
            </a-button>
            <a-button @click="resetSearch">{{
              $t('quest.search.reset')
            }}</a-button>
            <a-button type="primary" status="success" @click="openCreate">
              {{ $t('quest.action.add') }}
            </a-button>
            <a-button :loading="publishing" @click="onPublish">
              {{ $t('quest.action.publish') }}
            </a-button>
            <a-button :loading="syncing" @click="onSync(true)">
              {{ $t('quest.action.syncDry') }}
            </a-button>
            <a-button
              type="outline"
              status="warning"
              :loading="syncing"
              :disabled="!canSyncApply"
              @click="onSync(false)"
            >
              {{ $t('quest.action.syncApply') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>

      <a-spin :loading="loading" style="width: 100%">
        <div class="quest-card-grid">
          <div
            v-for="record in rows"
            :key="record.questId"
            class="quest-card"
            @click="openDetail(record.questId)"
          >
            <div class="quest-card-head">
              <span class="quest-card-id">#{{ record.questId }}</span>
              <a-tag
                v-if="record.hasStartScript || record.hasEndScript"
                size="small"
                color="arcoblue"
              >
                {{ $t('quest.card.script') }}
              </a-tag>
            </div>
            <div class="quest-card-title" :title="record.name">
              {{ record.name || '—' }}
            </div>
            <div v-if="record.parentName" class="quest-card-parent">
              {{ record.parentName }}
            </div>
            <div class="quest-card-npc">
              <ItemIcon
                v-if="record.startNpcId"
                :id="record.startNpcId"
                category="npc"
                :size="32"
                prefer-cdn
              />
              <div v-else class="quest-card-npc-placeholder" />
              <div class="quest-card-npc-text">
                <div class="quest-card-npc-name">
                  {{
                    record.startNpcId
                      ? record.startNpcName || record.startNpcId
                      : $t('quest.card.noNpc')
                  }}
                </div>
                <div v-if="record.startNpcId" class="quest-card-npc-id">
                  NPC {{ record.startNpcId }}
                </div>
              </div>
            </div>
            <div class="quest-card-meta">
              <span
                >{{ $t('quest.card.level') }} {{ record.minLevel ?? '—' }}</span
              >
              <span v-if="record.nextQuestId"
                >{{ $t('quest.card.next') }} #{{ record.nextQuestId }}</span
              >
            </div>
          </div>
        </div>
        <div v-if="!loading && rows.length === 0" class="quest-card-empty">
          —
        </div>
        <div class="quest-card-pager">
          <a-pagination
            :current="condition.pageNo"
            :page-size="condition.pageSize"
            :total="total"
            show-total
            show-page-size
            @change="onPageChange"
            @page-size-change="onPageSizeChange"
          />
        </div>
      </a-spin>
    </ProCard>

    <a-drawer
      :visible="drawerVisible"
      :width="960"
      unmount-on-close
      @cancel="drawerVisible = false"
    >
      <template #title>
        #{{ detail?.questId }} {{ detail?.name || '' }}
      </template>
      <a-spin :loading="detailLoading" style="width: 100%">
        <a-tabs
          v-if="detail"
          v-model:active-key="activeTab"
          @change="onTabChange"
        >
          <a-tab-pane key="overview" :title="$t('quest.tab.overview')">
            <a-alert
              v-for="(w, i) in detail.warnings || []"
              :key="i"
              type="warning"
              show-icon
              style="margin-bottom: 8px"
            >
              {{ w }}
            </a-alert>
            <a-descriptions :column="2" bordered size="small">
              <a-descriptions-item :label="$t('quest.field.parent')">
                {{ detail.parentName || '—' }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.field.area')">
                {{ detail.area ?? '—' }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.field.startNpc')">
                <div class="inline-entity">
                  <ItemIcon
                    v-if="detail.startNpcId"
                    :id="detail.startNpcId"
                    category="npc"
                    :size="28"
                    prefer-cdn
                  />
                  <span
                    >{{ detail.startNpcName || '—' }} ({{
                      detail.startNpcId ?? '—'
                    }})</span
                  >
                </div>
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.field.endNpc')">
                <div class="inline-entity">
                  <ItemIcon
                    v-if="detail.endNpcId"
                    :id="detail.endNpcId"
                    category="npc"
                    :size="28"
                    prefer-cdn
                  />
                  <span
                    >{{ detail.endNpcName || '—' }} ({{
                      detail.endNpcId ?? '—'
                    }})</span
                  >
                </div>
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.field.minLevel')">
                {{ detail.minLevel ?? '—' }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.field.maxLevel')">
                {{ detail.maxLevel ?? '—' }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.field.endExp')">
                {{ detail.endExp ?? '—' }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.field.endMeso')">
                {{ detail.endMeso ?? '—' }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.field.text0')" :span="2">
                {{ detail.text0 || '—' }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.field.text1')" :span="2">
                {{ detail.text1 || '—' }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.field.text2')" :span="2">
                {{ detail.text2 || '—' }}
              </a-descriptions-item>
            </a-descriptions>
            <a-divider>{{ $t('quest.items.end') }}</a-divider>
            <a-table
              :data="detail.endItems || []"
              :pagination="false"
              size="small"
              row-key="id"
            >
              <template #columns>
                <a-table-column :title="$t('quest.icon')" :width="56">
                  <template #cell="{ record }">
                    <ItemIcon
                      v-if="record.id"
                      :id="record.id"
                      category="item"
                      :size="28"
                      prefer-cdn
                    />
                  </template>
                </a-table-column>
                <a-table-column title="ID" data-index="id" :width="100" />
                <a-table-column
                  :title="$t('quest.column.name')"
                  data-index="name"
                />
                <a-table-column title="×" data-index="count" :width="80" />
              </template>
            </a-table>
            <a-divider>{{ $t('quest.mobs.end') }}</a-divider>
            <a-table
              :data="detail.endMobs || []"
              :pagination="false"
              size="small"
              row-key="id"
            >
              <template #columns>
                <a-table-column :title="$t('quest.icon')" :width="56">
                  <template #cell="{ record }">
                    <ItemIcon
                      v-if="record.id"
                      :id="record.id"
                      category="mob"
                      :size="28"
                      prefer-cdn
                    />
                  </template>
                </a-table-column>
                <a-table-column title="ID" data-index="id" :width="100" />
                <a-table-column
                  :title="$t('quest.column.name')"
                  data-index="name"
                />
                <a-table-column title="×" data-index="count" :width="80" />
              </template>
            </a-table>
            <a-divider>{{ $t('quest.rewards.end') }}</a-divider>
            <a-table
              :data="detail.endRewards || []"
              :pagination="false"
              size="small"
              row-key="id"
            >
              <template #columns>
                <a-table-column :title="$t('quest.icon')" :width="56">
                  <template #cell="{ record }">
                    <ItemIcon
                      v-if="record.id"
                      :id="record.id"
                      category="item"
                      :size="28"
                      prefer-cdn
                    />
                  </template>
                </a-table-column>
                <a-table-column title="ID" data-index="id" :width="100" />
                <a-table-column
                  :title="$t('quest.column.name')"
                  data-index="name"
                />
                <a-table-column title="×" data-index="count" :width="80" />
              </template>
            </a-table>
          </a-tab-pane>

          <a-tab-pane key="links" :title="$t('quest.tab.links')">
            <a-space wrap class="bd-page-toolbar">
              <a-button :loading="chainLoading" @click="loadChain">
                {{ $t('quest.links.chain') }}
              </a-button>
              <a-button @click="fitChainView">{{
                $t('quest.chain.fit')
              }}</a-button>
              <a-button @click="focusCurrentNode">{{
                $t('quest.chain.focusCurrent')
              }}</a-button>
            </a-space>
            <a-alert
              v-for="(w, i) in chainWarnings"
              :key="'cw-' + i"
              type="warning"
              show-icon
              style="margin-bottom: 8px"
            >
              {{ w }}
            </a-alert>
            <div class="quest-chain-canvas">
              <a-spin :loading="chainLoading" style="width: 100%; height: 100%">
                <VueFlow
                  v-if="chainNodes.length"
                  id="quest-chain"
                  v-model:nodes="chainNodes"
                  v-model:edges="chainEdges"
                  :node-types="chainNodeTypes"
                  fit-view-on-init
                  :default-viewport="{ zoom: 0.85 }"
                  :min-zoom="0.2"
                  :max-zoom="1.8"
                >
                  <Background />
                </VueFlow>
                <div v-else-if="!chainLoading" class="quest-chain-empty">—</div>
              </a-spin>
            </div>
            <a-collapse :default-active-key="[]" style="margin-top: 12px">
              <a-collapse-item
                key="assist"
                :header="$t('quest.links.listAssist')"
              >
                <a-divider>{{ $t('quest.links.upstream') }}</a-divider>
                <a-table
                  :data="detail.upstreamQuests || []"
                  :pagination="false"
                  size="small"
                  row-key="questId"
                >
                  <template #columns>
                    <a-table-column
                      title="ID"
                      data-index="questId"
                      :width="100"
                    />
                    <a-table-column
                      :title="$t('quest.column.name')"
                      data-index="name"
                    />
                    <a-table-column
                      title="state"
                      data-index="state"
                      :width="80"
                    />
                  </template>
                </a-table>
                <a-divider>{{ $t('quest.links.next') }}</a-divider>
                <div>{{ detail.nextQuestId ?? '—' }}</div>
                <a-divider>{{ $t('quest.links.maps') }}</a-divider>
                <a-table
                  :data="detail.maps || []"
                  :pagination="false"
                  size="small"
                  row-key="mapId"
                >
                  <template #columns>
                    <a-table-column
                      title="ID"
                      data-index="mapId"
                      :width="120"
                    />
                    <a-table-column
                      title="street"
                      data-index="streetName"
                      :width="160"
                    />
                    <a-table-column
                      :title="$t('quest.column.name')"
                      data-index="mapName"
                    />
                  </template>
                </a-table>
              </a-collapse-item>
            </a-collapse>
          </a-tab-pane>

          <a-tab-pane key="progress" :title="$t('quest.tab.progress')">
            <a-space wrap class="bd-page-toolbar">
              <a-select
                v-model="progressMode"
                :options="progressModeOptions"
                style="width: 200px"
              />
              <a-input
                v-model="progressName"
                :placeholder="$t('quest.progress.characterName')"
                allow-clear
                style="width: 160px"
              />
              <a-input-number
                v-model="forceCid"
                :placeholder="$t('quest.progress.characterId')"
                hide-button
                style="width: 140px"
              />
              <a-button
                type="primary"
                :loading="progressLoading"
                @click="loadProgress"
              >
                {{ $t('quest.progress.query') }}
              </a-button>
              <a-button @click="onForce('start')">{{
                $t('quest.progress.forceStart')
              }}</a-button>
              <a-button @click="onForce('complete')">{{
                $t('quest.progress.forceComplete')
              }}</a-button>
              <a-button status="danger" @click="onForce('reset')">{{
                $t('quest.progress.reset')
              }}</a-button>
            </a-space>
            <a-table
              row-key="questStatusId"
              :loading="progressLoading"
              :data="progressRows"
              :pagination="progressPagination"
              size="small"
              @page-change="onProgressPageChange"
            >
              <template #columns>
                <a-table-column
                  :title="$t('quest.progress.column.cid')"
                  data-index="characterId"
                  :width="100"
                />
                <a-table-column
                  :title="$t('quest.progress.column.cname')"
                  data-index="characterName"
                />
                <a-table-column
                  :title="$t('quest.progress.column.status')"
                  data-index="statusLabel"
                  :width="100"
                />
                <a-table-column
                  :title="$t('quest.progress.column.completed')"
                  data-index="completed"
                  :width="90"
                />
                <a-table-column
                  :title="$t('quest.progress.column.online')"
                  :width="80"
                >
                  <template #cell="{ record }">
                    {{ record.online ? $t('quest.yes') : $t('quest.no') }}
                  </template>
                </a-table-column>
              </template>
            </a-table>
          </a-tab-pane>

          <a-tab-pane key="edit" :title="$t('quest.tab.edit')">
            <a-form :model="editForm" layout="vertical">
              <a-row :gutter="12">
                <a-col :span="12">
                  <a-form-item :label="$t('quest.field.name')">
                    <a-input v-model="editForm.name" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item :label="$t('quest.field.parent')">
                    <a-input v-model="editForm.parentName" />
                  </a-form-item>
                </a-col>
                <a-col :span="8">
                  <a-form-item :label="$t('quest.field.startNpc')">
                    <a-input-number
                      v-model="editForm.startNpcId"
                      hide-button
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="8">
                  <a-form-item :label="$t('quest.field.endNpc')">
                    <a-input-number
                      v-model="editForm.endNpcId"
                      hide-button
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="8">
                  <a-form-item :label="$t('quest.field.nextQuest')">
                    <a-input-number
                      v-model="editForm.nextQuestId"
                      hide-button
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="8">
                  <a-form-item :label="$t('quest.field.minLevel')">
                    <a-input-number
                      v-model="editForm.minLevel"
                      hide-button
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="8">
                  <a-form-item :label="$t('quest.field.endExp')">
                    <a-input-number
                      v-model="editForm.endExp"
                      hide-button
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="8">
                  <a-form-item :label="$t('quest.field.endMeso')">
                    <a-input-number
                      v-model="editForm.endMeso"
                      hide-button
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="24">
                  <a-form-item :label="$t('quest.field.text0')">
                    <a-textarea
                      v-model="editForm.text0"
                      :auto-size="{ minRows: 2 }"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="24">
                  <a-form-item :label="$t('quest.field.text1')">
                    <a-textarea
                      v-model="editForm.text1"
                      :auto-size="{ minRows: 2 }"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="24">
                  <a-form-item :label="$t('quest.field.text2')">
                    <a-textarea
                      v-model="editForm.text2"
                      :auto-size="{ minRows: 2 }"
                    />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-space>
                <a-button type="primary" :loading="saving" @click="onSaveEdit">
                  {{ $t('quest.action.edit') }}
                </a-button>
                <a-button status="danger" @click="onDelete">
                  {{ $t('quest.action.delete') }}
                </a-button>
              </a-space>
            </a-form>
          </a-tab-pane>

          <a-tab-pane key="script" :title="$t('quest.tab.script')">
            <a-alert type="warning" show-icon style="margin-bottom: 8px">
              {{ $t('quest.script.warn') }}
            </a-alert>
            <a-space class="bd-page-toolbar">
              <a-checkbox v-model="scriptLocalized">{{
                $t('quest.script.localized')
              }}</a-checkbox>
              <a-button :loading="scriptLoading" @click="loadScript">{{
                $t('quest.script.load')
              }}</a-button>
              <a-button
                type="primary"
                :loading="scriptSaving"
                @click="saveScript"
                >{{ $t('quest.script.save') }}</a-button
              >
            </a-space>
            <div class="script-meta">
              {{ $t('quest.script.path') }}: {{ scriptPath || '—' }}
              <span v-if="scriptExists === false">
                · {{ $t('quest.script.missing') }}</span
              >
            </div>
            <vue-monaco-editor
              v-model:value="scriptContent"
              language="javascript"
              theme="vs-dark"
              height="360px"
              :options="{ automaticLayout: true, minimap: { enabled: false } }"
            />
          </a-tab-pane>

          <a-tab-pane key="sync" :title="$t('quest.tab.sync')">
            <a-space class="bd-page-toolbar">
              <a-button :loading="syncStatusLoading" @click="loadSyncStatus">{{
                $t('quest.sync.refresh')
              }}</a-button>
              <a-button :loading="syncing" @click="onSync(true)">{{
                $t('quest.action.syncDry')
              }}</a-button>
              <a-button
                type="outline"
                status="warning"
                :loading="syncing"
                :disabled="!canSyncApply"
                @click="onSync(false)"
              >
                {{ $t('quest.action.syncApply') }}
              </a-button>
            </a-space>
            <a-descriptions
              v-if="syncStatus"
              :column="1"
              bordered
              size="small"
              :title="$t('quest.sync.status')"
            >
              <a-descriptions-item :label="$t('quest.sync.dataPath')">
                {{ syncStatus.clientDataPath || '—' }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.sync.enPath')">
                {{ syncStatus.clientEnPath || '—' }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.sync.allowWrite')">
                {{ syncStatus.allowWrite ? $t('quest.yes') : $t('quest.no') }}
              </a-descriptions-item>
              <a-descriptions-item :label="$t('quest.sync.patcher')">
                {{
                  syncStatus.patcherAvailable
                    ? syncStatus.patcherPath
                    : $t('quest.no')
                }}
              </a-descriptions-item>
            </a-descriptions>
            <pre v-if="syncResultText" class="sync-result">{{
              syncResultText
            }}</pre>
          </a-tab-pane>
        </a-tabs>
      </a-spin>
    </a-drawer>

    <a-modal
      v-model:visible="createVisible"
      :title="$t('quest.msg.createTitle')"
      @ok="onCreate"
      @cancel="createVisible = false"
    >
      <a-form :model="createForm" layout="vertical">
        <a-form-item :label="$t('quest.search.questId')" required>
          <a-input-number
            v-model="createForm.questId"
            hide-button
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :label="$t('quest.field.name')">
          <a-input v-model="createForm.name" />
        </a-form-item>
      </a-form>
    </a-modal>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { computed, markRaw, nextTick, onMounted, reactive, ref } from 'vue';
  import { Message, Modal } from '@arco-design/web-vue';
  import { VueMonacoEditor } from '@guolao/vue-monaco-editor';
  import { Background } from '@vue-flow/background';
  import {
    VueFlow,
    useVueFlow,
    type Edge,
    type Node,
    type NodeTypesObject,
  } from '@vue-flow/core';
  import dagre from '@dagrejs/dagre';
  import '@vue-flow/core/dist/style.css';
  import '@vue-flow/core/dist/theme-default.css';
  import { useI18n } from 'vue-i18n';
  import {
    addQuest,
    deleteQuest,
    forceComplete,
    forceStart,
    getCompletions,
    getQuestChain,
    getQuestDetail,
    getQuestList,
    getSyncClientStatus,
    publishQuests,
    QuestDetail,
    QuestListRow,
    QuestProgressRow,
    QuestWriteReq,
    readQuestScript,
    resetQuest,
    syncClient,
    updateQuest,
    writeQuestScript,
  } from '@/api/quest';
  import ItemIcon from '@/components/item-icon/index.vue';
  import QuestChainNode from './QuestChainNode.vue';

  const { t } = useI18n();
  const { fitView: vfFitView, setCenter } = useVueFlow('quest-chain');
  const chainNodeTypes = {
    quest: markRaw(QuestChainNode),
  } as unknown as NodeTypesObject;

  const loading = ref(false);
  const publishing = ref(false);
  const syncing = ref(false);
  const rows = ref<QuestListRow[]>([]);
  const condition = reactive<{
    questId?: number;
    name?: string;
    npcId?: number;
    pageNo: number;
    pageSize: number;
  }>({
    pageNo: 1,
    pageSize: 20,
  });
  const total = ref(0);

  const drawerVisible = ref(false);
  const detailLoading = ref(false);
  const detail = ref<QuestDetail | null>(null);
  const activeTab = ref('overview');
  const editForm = reactive<QuestWriteReq>({});
  const saving = ref(false);

  const chainLoading = ref(false);
  const chainNodes = ref<Node[]>([]);
  const chainEdges = ref<Edge[]>([]);
  const chainWarnings = ref<string[]>([]);
  const chainLoadedFor = ref<number | null>(null);

  const progressMode = ref('completed');
  const progressName = ref('');
  const forceCid = ref<number | undefined>();
  const progressLoading = ref(false);
  const progressRows = ref<QuestProgressRow[]>([]);
  const progressPageNo = ref(1);
  const progressTotal = ref(0);
  const progressPagination = computed(() => ({
    current: progressPageNo.value,
    pageSize: 20,
    total: progressTotal.value,
    showTotal: true,
  }));
  const progressModeOptions = computed(() => [
    { label: t('quest.progress.mode.completed'), value: 'completed' },
    { label: t('quest.progress.mode.incomplete'), value: 'incomplete' },
    { label: t('quest.progress.mode.never'), value: 'never' },
    { label: t('quest.progress.mode.all'), value: 'all' },
  ]);

  const scriptLocalized = ref(true);
  const scriptLoading = ref(false);
  const scriptSaving = ref(false);
  const scriptContent = ref('');
  const scriptPath = ref('');
  const scriptExists = ref<boolean | null>(null);

  const syncStatusLoading = ref(false);
  const syncStatus = ref<Record<string, any> | null>(null);
  const syncResultText = ref('');
  const canSyncApply = computed(
    () =>
      !!syncStatus.value?.clientDataConfigured && !!syncStatus.value?.allowWrite
  );

  const createVisible = ref(false);
  const createForm = reactive<{ questId?: number; name?: string }>({});

  const layoutChain = (n: Node[], e: Edge[]) => {
    const g = new dagre.graphlib.Graph();
    g.setDefaultEdgeLabel(() => ({}));
    g.setGraph({ rankdir: 'LR', nodesep: 36, ranksep: 72 });
    n.forEach((node) => g.setNode(node.id, { width: 220, height: 72 }));
    e.forEach((edge) => g.setEdge(edge.source, edge.target));
    dagre.layout(g);
    return n.map((node) => {
      const pos = g.node(node.id);
      return {
        ...node,
        position: {
          x: (pos?.x || 0) - 110,
          y: (pos?.y || 0) - 36,
        },
      };
    });
  };

  async function loadList() {
    loading.value = true;
    try {
      const { data }: any = await getQuestList({ ...condition });
      rows.value = data?.records || [];
      total.value = data?.totalRow ?? rows.value.length;
    } finally {
      loading.value = false;
    }
  }

  function resetSearch() {
    condition.questId = undefined;
    condition.name = undefined;
    condition.npcId = undefined;
    condition.pageNo = 1;
    loadList();
  }

  function onPageChange(page: number) {
    condition.pageNo = page;
    loadList();
  }

  function onPageSizeChange(size: number) {
    condition.pageSize = size;
    condition.pageNo = 1;
    loadList();
  }

  async function openDetail(questId?: number, tab = 'overview') {
    if (!questId) return;
    drawerVisible.value = true;
    activeTab.value = tab;
    detailLoading.value = true;
    chainLoadedFor.value = null;
    try {
      const { data }: any = await getQuestDetail(questId);
      detail.value = data;
      Object.assign(editForm, {
        questId: detail.value?.questId,
        name: detail.value?.name,
        parentName: detail.value?.parentName,
        text0: detail.value?.text0,
        text1: detail.value?.text1,
        text2: detail.value?.text2,
        startNpcId: detail.value?.startNpcId,
        endNpcId: detail.value?.endNpcId,
        minLevel: detail.value?.minLevel,
        maxLevel: detail.value?.maxLevel,
        nextQuestId: detail.value?.nextQuestId,
        endExp: detail.value?.endExp,
        endMeso: detail.value?.endMeso,
        area: detail.value?.area,
        autoStart: detail.value?.autoStart,
        autoComplete: detail.value?.autoComplete,
        autoPreComplete: detail.value?.autoPreComplete,
      });
      progressPageNo.value = 1;
      await loadProgress();
      await loadSyncStatus();
      if (tab === 'links') {
        await loadChain();
      }
    } finally {
      detailLoading.value = false;
    }
  }

  function onTabChange(key: string | number) {
    if (key === 'links') {
      loadChain();
    }
  }

  async function loadChain() {
    const questId = detail.value?.questId;
    if (!questId) return;
    if (chainLoadedFor.value === questId && chainNodes.value.length) {
      await nextTick();
      fitChainView();
      return;
    }
    chainLoading.value = true;
    try {
      const { data }: any = await getQuestChain(questId);
      chainWarnings.value = data?.warnings || [];
      const onSelect = (id?: number) => {
        if (id && id !== detail.value?.questId) {
          openDetail(id, 'links');
        }
      };
      const onEdit = (id?: number) => {
        if (id) openDetail(id, 'edit');
      };
      const nextNodes: Node[] = (data?.nodes || []).map((n: any) => ({
        id: String(n.questId),
        type: 'quest',
        position: { x: 0, y: 0 },
        data: {
          questId: n.questId,
          name: n.name,
          parentName: n.parentName,
          startNpcId: n.startNpcId,
          current: !!n.current,
          onSelect,
          onEdit,
        },
      }));
      const nextEdges: Edge[] = (data?.edges || []).map(
        (e: any, idx: number) => ({
          id: `e-${e.type}-${e.from}-${e.to}-${idx}`,
          source: String(e.from),
          target: String(e.to),
          label:
            e.type === 'prereq'
              ? t('quest.chain.edgePrereq')
              : t('quest.chain.edgeNext'),
          animated: e.type === 'next',
          style:
            e.type === 'prereq'
              ? { stroke: 'var(--color-neutral-6)' }
              : undefined,
        })
      );
      // 保证边端点存在，避免 dagre 丢边
      const idSet = new Set(nextNodes.map((n) => n.id));
      (data?.edges || []).forEach((e: any) => {
        [e.from, e.to].forEach((qid: number) => {
          const sid = String(qid);
          if (qid && !idSet.has(sid)) {
            idSet.add(sid);
            nextNodes.push({
              id: sid,
              type: 'quest',
              position: { x: 0, y: 0 },
              data: { questId: qid, onSelect, onEdit },
            });
          }
        });
      });
      chainNodes.value = layoutChain(nextNodes, nextEdges);
      chainEdges.value = nextEdges;
      chainLoadedFor.value = questId;
      await nextTick();
      fitChainView();
    } finally {
      chainLoading.value = false;
    }
  }

  function fitChainView() {
    nextTick(() => {
      try {
        vfFitView({ padding: 0.2, duration: 200 });
      } catch {
        /* VueFlow 未挂载时忽略 */
      }
    });
  }

  function focusCurrentNode() {
    const cur = chainNodes.value.find((n) => n.data?.current);
    if (!cur) {
      fitChainView();
      return;
    }
    const x = cur.position.x + 110;
    const y = cur.position.y + 36;
    try {
      setCenter(x, y, { zoom: 1, duration: 200 });
    } catch {
      fitChainView();
    }
  }

  async function loadProgress() {
    if (!detail.value?.questId) return;
    progressLoading.value = true;
    try {
      const { data }: any = await getCompletions({
        questId: detail.value.questId,
        mode: progressMode.value,
        characterName: progressName.value || undefined,
        pageNo: progressPageNo.value,
        pageSize: 20,
      });
      progressRows.value = data?.records || [];
      progressTotal.value = data?.totalRow ?? progressRows.value.length;
    } finally {
      progressLoading.value = false;
    }
  }

  function onProgressPageChange(page: number) {
    progressPageNo.value = page;
    loadProgress();
  }

  async function onForce(op: 'start' | 'complete' | 'reset') {
    if (!detail.value?.questId || !forceCid.value) {
      Message.warning(t('quest.progress.characterId'));
      return;
    }
    const body = {
      questId: detail.value.questId,
      characterId: forceCid.value,
    };
    if (op === 'start') await forceStart(body);
    else if (op === 'complete') await forceComplete(body);
    else await resetQuest(body);
    Message.success(t('quest.msg.forceOk'));
    await loadProgress();
  }

  async function onSaveEdit() {
    if (!editForm.questId) return;
    saving.value = true;
    try {
      await updateQuest({ ...editForm });
      Message.success(t('quest.msg.saveOk'));
      await openDetail(editForm.questId, 'edit');
      await loadList();
    } finally {
      saving.value = false;
    }
  }

  function onDelete() {
    const questId = detail.value?.questId;
    if (!questId) return;
    Modal.warning({
      title: t('quest.action.delete'),
      content: t('quest.msg.deleteConfirm'),
      hideCancel: false,
      onOk: async () => {
        await deleteQuest(questId);
        Message.success(t('quest.msg.deleteOk'));
        drawerVisible.value = false;
        await loadList();
      },
    });
  }

  async function loadScript() {
    if (!detail.value?.questId) return;
    scriptLoading.value = true;
    try {
      const { data }: any = await readQuestScript({
        questId: detail.value.questId,
        localized: scriptLocalized.value,
      });
      scriptContent.value = data?.content || '';
      scriptPath.value = data?.path || '';
      scriptExists.value = !!data?.exists;
    } finally {
      scriptLoading.value = false;
    }
  }

  async function saveScript() {
    if (!detail.value?.questId) return;
    scriptSaving.value = true;
    try {
      await writeQuestScript({
        questId: detail.value.questId,
        localized: scriptLocalized.value,
        content: scriptContent.value,
      });
      Message.success(t('quest.msg.scriptOk'));
      await loadScript();
    } finally {
      scriptSaving.value = false;
    }
  }

  async function loadSyncStatus() {
    syncStatusLoading.value = true;
    try {
      const { data }: any = await getSyncClientStatus();
      syncStatus.value = data;
    } finally {
      syncStatusLoading.value = false;
    }
  }

  async function onPublish() {
    publishing.value = true;
    try {
      await publishQuests();
      Message.success(t('quest.msg.publishOk'));
      await loadList();
    } finally {
      publishing.value = false;
    }
  }

  async function onSync(dryRun: boolean) {
    syncing.value = true;
    try {
      const ids = detail.value?.questId ? [detail.value.questId] : [];
      const { data }: any = await syncClient({ dryRun, questIds: ids });
      syncResultText.value = JSON.stringify(data, null, 2);
      Message.success(data?.message || t('quest.msg.syncOk'));
      await loadSyncStatus();
    } finally {
      syncing.value = false;
    }
  }

  function openCreate() {
    createForm.questId = undefined;
    createForm.name = '';
    createVisible.value = true;
  }

  async function onCreate() {
    if (!createForm.questId) {
      Message.warning(t('quest.search.questId'));
      return;
    }
    await addQuest({
      questId: createForm.questId,
      name: createForm.name || `Quest ${createForm.questId}`,
    });
    createVisible.value = false;
    Message.success(t('quest.msg.saveOk'));
    await loadList();
    await openDetail(createForm.questId);
  }

  onMounted(() => {
    loadList();
    loadSyncStatus();
  });
</script>

<script lang="ts">
  export default {
    name: 'GameQuest',
    components: { VueMonacoEditor, ItemIcon },
  };
</script>

<style scoped>
  .search-row {
    margin-bottom: 12px;
  }
  .quest-card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 12px;
  }
  .quest-card {
    border: 1px solid var(--color-border-2);
    border-radius: 8px;
    padding: 12px;
    background: var(--color-bg-2);
    cursor: pointer;
    transition: border-color 0.15s ease;
  }
  .quest-card:hover {
    border-color: rgb(var(--primary-6));
  }
  .quest-card-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 4px;
  }
  .quest-card-id {
    font-size: 12px;
    opacity: 0.7;
  }
  .quest-card-title {
    font-size: 15px;
    font-weight: 600;
    line-height: 1.35;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .quest-card-parent {
    margin-top: 2px;
    font-size: 12px;
    opacity: 0.65;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .quest-card-npc {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 10px;
  }
  .quest-card-npc-placeholder {
    width: 32px;
    height: 32px;
    border-radius: 4px;
    background: var(--color-fill-2);
    flex-shrink: 0;
  }
  .quest-card-npc-text {
    min-width: 0;
  }
  .quest-card-npc-name {
    font-size: 13px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .quest-card-npc-id {
    font-size: 11px;
    opacity: 0.6;
  }
  .quest-card-meta {
    display: flex;
    justify-content: space-between;
    gap: 8px;
    margin-top: 10px;
    font-size: 12px;
    opacity: 0.75;
  }
  .quest-card-empty {
    padding: 24px;
    text-align: center;
    opacity: 0.5;
  }
  .quest-card-pager {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
  .inline-entity {
    display: inline-flex;
    align-items: center;
    gap: 8px;
  }
  .quest-chain-canvas {
    height: 420px;
    border: 1px solid var(--color-border-2);
    border-radius: 6px;
    overflow: hidden;
    background: var(--color-fill-1);
  }
  .quest-chain-empty {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 420px;
    opacity: 0.5;
  }
  .script-meta {
    margin-bottom: 8px;
    font-size: 12px;
    opacity: 0.8;
  }
  .sync-result {
    margin-top: 12px;
    max-height: 240px;
    overflow: auto;
    padding: 8px;
    background: var(--color-fill-2);
    font-size: 12px;
  }
</style>
