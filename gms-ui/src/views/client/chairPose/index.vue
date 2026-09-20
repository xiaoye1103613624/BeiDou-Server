<template>
  <PageContainer
    :title="$t('menu.client.chairPose')"
    :description="$t('clientChairPose.page.desc')"
  >
    <ProCard soft>
      <a-alert type="info" class="mb-alert">{{
        $t('clientChairPose.hint.switch')
      }}</a-alert>
      <a-space wrap class="bd-page-toolbar">
        <a-tag :color="status?.editorEnabled ? 'green' : 'gray'">
          {{
            status?.editorEnabled
              ? $t('clientChairPose.status.editorOn')
              : $t('clientChairPose.status.editorOff')
          }}
        </a-tag>
        <a-tag :color="status?.allowClientWrite ? 'green' : 'gray'">
          {{
            status?.allowClientWrite
              ? $t('clientChairPose.status.clientWriteOn')
              : $t('clientChairPose.status.clientWriteOff')
          }}
        </a-tag>
        <a-tag :color="status?.clientDataConfigured ? 'arcoblue' : 'orangered'">
          {{
            status?.clientDataConfigured
              ? $t('clientChairPose.status.dataOn')
              : $t('clientChairPose.status.dataOff')
          }}
        </a-tag>
        <a-tag :color="status?.patcherAvailable ? 'arcoblue' : 'orangered'">
          {{
            status?.patcherAvailable
              ? $t('clientChairPose.status.patcher')
              : $t('clientChairPose.status.noPatcher')
          }}
        </a-tag>
        <a-button size="small" :loading="statusLoading" @click="loadStatus">
          {{ $t('clientChairPose.refreshStatus') }}
        </a-button>
        <a-button size="small" @click="goClientPath">{{
          $t('menu.client.path')
        }}</a-button>
        <a-button size="small" :loading="patching" @click="runPatch(true)">
          {{ $t('clientChairPose.patchDryRun') }}
        </a-button>
        <a-button
          v-if="status?.allowClientWrite"
          size="small"
          status="warning"
          :loading="patching"
          @click="confirmApply"
        >
          {{ $t('clientChairPose.patchApply') }}
        </a-button>
      </a-space>
    </ProCard>

    <div class="pose-layout">
      <ProCard class="pose-sider">
        <a-tabs
          v-model:active-key="activeTab"
          size="small"
          @change="onTabChange"
        >
          <a-tab-pane key="chairs" :title="$t('clientChairPose.tab.chairs')" />
          <a-tab-pane
            key="tamingMobs"
            :title="$t('clientChairPose.tab.tamingMobs')"
          />
        </a-tabs>
        <a-space direction="vertical" fill class="search-bar">
          <a-input-search
            v-model="keyword"
            :placeholder="$t('clientChairPose.search.keyword')"
            allow-clear
            @search="onSearchCommit"
            @press-enter="onSearchCommit"
            @input="scheduleReloadList"
            @clear="onSearchCommit"
          />
          <a-checkbox
            v-if="activeTab === 'chairs'"
            v-model="effect2Only"
            @change="onSearchCommit"
          >
            {{ $t('clientChairPose.search.effect2Only') }}
          </a-checkbox>
        </a-space>
        <a-spin :loading="listLoading" style="width: 100%">
          <a-table
            v-if="listRows.length"
            :data="listRows"
            :pagination="false"
            row-key="id"
            size="small"
            :bordered="false"
            :scroll="{ y: 440 }"
            :row-class="rowClass"
            @row-click="onRowClick"
          >
            <template #columns>
              <a-table-column
                :title="$t('clientChairPose.col.id')"
                data-index="id"
                :width="90"
              />
              <a-table-column :title="$t('clientChairPose.col.name')">
                <template #cell="{ record }">
                  <a-space>
                    <ItemIcon
                      v-if="record.id"
                      :id="record.id"
                      category="item"
                      :size="24"
                      :persisted-url="record.iconUrl"
                      img-class="row-icon"
                      :alt="String(record.id)"
                    />
                    <span>{{ record.name || '-' }}</span>
                  </a-space>
                </template>
              </a-table-column>
              <a-table-column
                :title="$t('clientChairPose.col.flags')"
                :width="80"
              >
                <template #cell="{ record }">
                  <a-tag v-if="record.flag" size="small" color="arcoblue">
                    {{ record.flag }}
                  </a-tag>
                </template>
              </a-table-column>
            </template>
          </a-table>
          <a-empty v-else :description="$t('clientChairPose.list.empty')" />
          <a-pagination
            v-if="total > 0"
            class="list-pager"
            :total="total"
            :page-size="pageSize"
            :current="page"
            show-total
            show-page-size
            :page-size-options="[20, 50, 100, 200]"
            @change="onPageChange"
            @page-size-change="onPageSizeChange"
          />
        </a-spin>
      </ProCard>

      <ProCard class="pose-main">
        <a-spin :loading="detailLoading" style="width: 100%">
          <template v-if="listKind === 'chairs' && chairDetail">
            <div class="editor-grid">
              <div class="editor-form">
                <a-descriptions :column="1" size="small" bordered>
                  <a-descriptions-item :label="$t('clientChairPose.col.id')">
                    {{ chairDetail.itemId }}
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('clientChairPose.col.name')">
                    {{ chairDetail.name || '-' }}
                  </a-descriptions-item>
                  <a-descriptions-item
                    :label="$t('clientChairPose.form.source')"
                  >
                    <span class="mono">{{ chairDetail.sourceFile }}</span>
                  </a-descriptions-item>
                </a-descriptions>
                <a-divider />
                <a-form :model="chairForm" layout="vertical">
                  <a-form-item :label="$t('clientChairPose.form.layer')">
                    <a-radio-group
                      v-model="activeLayer"
                      type="button"
                      size="small"
                      @change="onChairLayerChange"
                    >
                      <a-radio
                        v-for="eff in chairDetail.effects || []"
                        :key="eff.layer"
                        :value="eff.layer"
                      >
                        {{ eff.layer }}
                      </a-radio>
                    </a-radio-group>
                  </a-form-item>
                  <a-row :gutter="12">
                    <a-col :span="12">
                      <a-form-item :label="$t('clientChairPose.form.originX')">
                        <a-input-number
                          v-model="chairForm.originX"
                          :disabled="!canEdit"
                          hide-button
                          @change="syncChairPreviewFromForm"
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item :label="$t('clientChairPose.form.originY')">
                        <a-input-number
                          v-model="chairForm.originY"
                          :disabled="!canEdit"
                          hide-button
                          @change="syncChairPreviewFromForm"
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item :label="$t('clientChairPose.form.pos')">
                        <a-input-number
                          v-model="chairForm.pos"
                          :disabled="!canEdit"
                          hide-button
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item :label="$t('clientChairPose.form.z')">
                        <a-input-number
                          v-model="chairForm.z"
                          :disabled="!canEdit"
                          hide-button
                        />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-button
                    type="primary"
                    :loading="saving"
                    :disabled="!canEdit"
                    @click="saveChair"
                  >
                    {{ $t('clientChairPose.save') }}
                  </a-button>
                </a-form>
              </div>
              <div class="preview-pane">
                <div class="preview-toolbar">
                  <div class="preview-title">
                    {{ $t('clientChairPose.preview.title') }}
                    <a-tag size="small">{{ chairPreview?.mode || '-' }}</a-tag>
                    <a-tag size="small" color="arcoblue">
                      {{ $t('clientChairPose.preview.poseSit') }}
                    </a-tag>
                  </div>
                  <a-space size="mini">
                    <a-button
                      size="mini"
                      :title="$t('clientChairPose.preview.zoomOut')"
                      @click="zoomOut"
                    >
                      <icon-minus />
                    </a-button>
                    <span class="zoom-label">{{ zoomPercent }}%</span>
                    <a-button
                      size="mini"
                      :title="$t('clientChairPose.preview.zoomIn')"
                      @click="zoomIn"
                    >
                      <icon-plus />
                    </a-button>
                    <a-button size="mini" @click="resetZoom">
                      {{ $t('clientChairPose.preview.zoomReset') }}
                    </a-button>
                  </a-space>
                </div>
                <DollStage
                  :item-image-url="chairPreviewImage"
                  :item-origin-x="Number(chairForm.originX || 0)"
                  :item-origin-y="Number(chairForm.originY || 0)"
                  :item-z-index="Number(chairForm.z) < 0 ? 1 : 3"
                  :item-id="chairDetail ? String(chairDetail.itemId) : ''"
                  :doll="doll"
                  anchor-mode="bodyOrigin"
                  :zoom="zoom"
                  :zoom-step="ZOOM_STEP"
                  :draggable="canEdit"
                  :anchor-label="stageReadout"
                  @zoom-step-change="applyZoomStep"
                  @drag-start="onChairDragStart"
                  @drag-move="onChairDragMove"
                  @drag-end="onDragEnd"
                  @stage-resize="onStageResize"
                  @item-error="onPreviewImgError"
                />
                <div class="preview-hint">
                  {{ $t('clientChairPose.preview.dragHint') }}
                </div>
                <div class="preview-hint">
                  {{ $t('clientChairPose.preview.zoomHint') }}
                </div>
                <div
                  v-if="chairPreview?.mode === 'ICON_FALLBACK'"
                  class="preview-msg"
                >
                  {{ $t('clientChairPose.preview.iconFallbackWarn') }}
                </div>
                <div v-if="chairPreview?.message" class="preview-msg">
                  {{ chairPreview.message }}
                </div>
              </div>
            </div>
          </template>

          <template v-else-if="listKind === 'tamingMobs' && mobDetail">
            <div class="editor-grid">
              <div class="editor-form">
                <a-descriptions :column="1" size="small" bordered>
                  <a-descriptions-item :label="$t('clientChairPose.col.id')">
                    {{ mobDetail.mobId }}
                  </a-descriptions-item>
                  <a-descriptions-item :label="$t('clientChairPose.col.name')">
                    {{ mobDetail.name || '-' }}
                  </a-descriptions-item>
                  <a-descriptions-item
                    :label="$t('clientChairPose.form.source')"
                  >
                    <span class="mono">{{ mobDetail.sourceFile }}</span>
                  </a-descriptions-item>
                </a-descriptions>
                <a-divider />
                <a-form :model="mobForm" layout="vertical">
                  <a-form-item :label="$t('clientChairPose.form.action')">
                    <a-select
                      v-model="mobForm.action"
                      :disabled="!canEdit"
                      @change="onMobActionChange"
                    >
                      <a-option
                        v-for="act in mobDetail.actions || []"
                        :key="act"
                        :value="act"
                      >
                        {{ act }}
                      </a-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item :label="$t('clientChairPose.form.frame')">
                    <a-select
                      v-model="mobForm.frameIndex"
                      :disabled="!canEdit"
                      @change="onMobFrameChange"
                    >
                      <a-option
                        v-for="fr in framesForAction"
                        :key="fr.frameIndex"
                        :value="fr.frameIndex"
                      >
                        #{{ fr.frameIndex }}
                      </a-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item :label="$t('clientChairPose.form.writeMode')">
                    <a-select v-model="mobForm.writeMode" :disabled="!canEdit">
                      <a-option value="currentFrame">{{
                        $t('clientChairPose.writeMode.currentFrame')
                      }}</a-option>
                      <a-option value="action">{{
                        $t('clientChairPose.writeMode.action')
                      }}</a-option>
                      <a-option value="allActions">{{
                        $t('clientChairPose.writeMode.allActions')
                      }}</a-option>
                    </a-select>
                  </a-form-item>
                  <a-row :gutter="12">
                    <a-col :span="12">
                      <a-form-item :label="$t('clientChairPose.form.navelX')">
                        <a-input-number
                          v-model="mobForm.navelX"
                          :disabled="!canEdit"
                          hide-button
                          @change="syncMobPreviewFromForm"
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item :label="$t('clientChairPose.form.navelY')">
                        <a-input-number
                          v-model="mobForm.navelY"
                          :disabled="!canEdit"
                          hide-button
                          @change="syncMobPreviewFromForm"
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item :label="$t('clientChairPose.form.originX')">
                        <a-input-number
                          v-model="mobForm.originX"
                          :disabled="!canEdit"
                          hide-button
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item :label="$t('clientChairPose.form.originY')">
                        <a-input-number
                          v-model="mobForm.originY"
                          :disabled="!canEdit"
                          hide-button
                        />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-button
                    type="primary"
                    :loading="saving"
                    :disabled="!canEdit"
                    @click="saveMob"
                  >
                    {{ $t('clientChairPose.save') }}
                  </a-button>
                </a-form>
              </div>
              <div class="preview-pane">
                <div class="preview-toolbar">
                  <div class="preview-title">
                    {{ $t('clientChairPose.preview.title') }}
                    <a-tag size="small">{{ mobPreview?.mode || '-' }}</a-tag>
                    <a-tag size="small" color="arcoblue">
                      {{ $t('clientChairPose.preview.poseRide') }}
                    </a-tag>
                  </div>
                  <a-space size="mini">
                    <a-button
                      size="mini"
                      :title="$t('clientChairPose.preview.zoomOut')"
                      @click="zoomOut"
                    >
                      <icon-minus />
                    </a-button>
                    <span class="zoom-label">{{ zoomPercent }}%</span>
                    <a-button
                      size="mini"
                      :title="$t('clientChairPose.preview.zoomIn')"
                      @click="zoomIn"
                    >
                      <icon-plus />
                    </a-button>
                    <a-button size="mini" @click="resetZoom">
                      {{ $t('clientChairPose.preview.zoomReset') }}
                    </a-button>
                  </a-space>
                </div>
                <DollStage
                  :item-image-url="mobPreviewImage"
                  :item-origin-x="Number(mobForm.originX || 0)"
                  :item-origin-y="Number(mobForm.originY || 0)"
                  :item-z-index="1"
                  :item-id="mobDetail ? String(mobDetail.mobId) : ''"
                  :doll="doll"
                  anchor-mode="navel"
                  :attach-x="Number(mobForm.navelX || 0)"
                  :attach-y="Number(mobForm.navelY || 0)"
                  :zoom="zoom"
                  :zoom-step="ZOOM_STEP"
                  :draggable="canEdit"
                  :anchor-label="stageReadout"
                  @zoom-step-change="applyZoomStep"
                  @drag-start="onMobDragStart"
                  @drag-move="onMobDragMove"
                  @drag-end="onDragEnd"
                  @stage-resize="onStageResize"
                  @item-error="onPreviewImgError"
                />
                <div class="preview-hint">
                  {{ $t('clientChairPose.preview.dragHint') }}
                </div>
                <div class="preview-hint">
                  {{ $t('clientChairPose.preview.zoomHint') }}
                </div>
                <div
                  v-if="mobPreview?.mode === 'ICON_FALLBACK'"
                  class="preview-msg"
                >
                  {{ $t('clientChairPose.preview.iconFallbackWarn') }}
                </div>
                <div v-if="mobPreview?.message" class="preview-msg">
                  {{ mobPreview.message }}
                </div>
              </div>
            </div>
          </template>

          <a-empty v-else :description="$t('clientChairPose.selectItem')" />
        </a-spin>
      </ProCard>
    </div>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useI18n } from 'vue-i18n';
  import { Message, Modal } from '@arco-design/web-vue';
  import type { TableData } from '@arco-design/web-vue';
  import ItemIcon from '@/components/item-icon/index.vue';
  import DollStage from '@/components/pose/DollStage.vue';
  import { onItemIconError, resolvePublicApiUrl } from '@/utils/mapleStoryAPI';
  import {
    renderDefaultCharacterDoll,
    type CharacterDollResult,
  } from '@/api/characterDoll';
  import type { StagePoint } from '@/utils/posePreviewLayout';
  import {
    fetchChairDetail,
    fetchChairList,
    fetchChairPoseStatus,
    fetchTamingMobDetail,
    fetchTamingMobList,
    patchChairApply,
    patchChairDryRun,
    previewChair,
    previewTamingMob,
    writeChair,
    writeTamingMob,
    type ChairPoseDetail,
    type ChairPoseEditorStatus,
    type ChairPoseEffect,
    type ChairPosePreview,
    type TamingMobPoseDetail,
    type TamingMobPoseFrame,
    type TamingMobPosePreview,
  } from '@/api/clientChairPose';

  const { t } = useI18n();
  const router = useRouter();

  const ZOOM_MIN = 0.5;
  const ZOOM_MAX = 4;
  const ZOOM_STEP = 0.25;
  const STAGE_FALLBACK = 480;
  const SEARCH_DEBOUNCE_MS = 280;

  const status = ref<ChairPoseEditorStatus | null>(null);
  const statusLoading = ref(false);
  const patching = ref(false);
  const listLoading = ref(false);
  const detailLoading = ref(false);
  const saving = ref(false);

  const activeTab = ref<'chairs' | 'tamingMobs'>('chairs');
  /**
   * 当前列表实际内容的种类。
   * 切 Tab 后列表异步刷新前可能仍显示旧行；点选必须按 listKind 调对应详情 API，
   * 不能用 activeTab（否则骑宠 ID 会误打进椅子详情 →「未找到椅子节点」）。
   */
  const listKind = ref<'chairs' | 'tamingMobs'>('chairs');
  const keyword = ref('');
  const effect2Only = ref(false);
  const page = ref(1);
  const pageSize = ref(50);
  const total = ref(0);
  const listRows = ref<
    { id: number; name?: string; iconUrl?: string; flag?: string }[]
  >([]);
  const selectedId = ref<number | null>(null);
  /** 丢弃过期列表响应；搜索防抖 */
  let listRequestSeq = 0;
  let searchDebounceTimer: ReturnType<typeof setTimeout> | null = null;

  const chairDetail = ref<ChairPoseDetail | null>(null);
  const chairPreview = ref<ChairPosePreview | null>(null);
  const activeLayer = ref('effect');
  const chairForm = reactive({
    originX: 0,
    originY: 0,
    pos: 0,
    z: 0,
  });
  const chairEffects = ref<ChairPoseEffect[]>([]);

  const mobDetail = ref<TamingMobPoseDetail | null>(null);
  const mobPreview = ref<TamingMobPosePreview | null>(null);
  const mobForm = reactive({
    action: '',
    frameIndex: 0,
    writeMode: 'currentFrame',
    navelX: 0,
    navelY: 0,
    originX: 0,
    originY: 0,
  });

  const dragging = ref(false);
  /** 拖拽起点：用指针位移改 origin/navel，避免绝对坐标与贴图 origin 语义打架 */
  const dragStart = reactive({
    pointerX: 0,
    pointerY: 0,
    originX: 0,
    originY: 0,
    navelX: 0,
    navelY: 0,
  });
  const zoom = ref(1);
  const stageSize = ref(STAGE_FALLBACK);
  /**
   * 本地合成人偶（Character.wz）：挂载点由真实 WZ 锚点给出，
   * 不再依赖 maplestory.io 整体渲染图（比例与偏移都不受控）。
   */
  const doll = ref<CharacterDollResult | null>(null);

  const loadDoll = async () => {
    try {
      const res = await renderDefaultCharacterDoll('sit');
      doll.value = res.data || null;
    } catch {
      doll.value = null;
    }
  };

  /** 预览图加载失败时回退 CDN（与 ItemIcon 一致） */
  const onPreviewImgError = onItemIconError;

  const canEdit = computed(() => !!status.value?.editorEnabled);

  const framesForAction = computed(() => {
    const frames = mobDetail.value?.frames || [];
    return frames.filter((f) => f.action === mobForm.action);
  });

  /** 开发态 8787 无静态资源，相对 /game-assets 须转到 API origin */
  const chairPreviewImage = computed(() =>
    resolvePublicApiUrl(
      chairPreview.value?.imageUrl || chairPreview.value?.iconUrl || ''
    )
  );
  const mobPreviewImage = computed(() =>
    resolvePublicApiUrl(
      mobPreview.value?.imageUrl || mobPreview.value?.iconUrl || ''
    )
  );

  const zoomPercent = computed(() => Math.round(zoom.value * 100));

  /** 舞台左下角读数：人偶尺寸与座椅场景锚点（身体原点） */
  const stageReadout = computed(() =>
    doll.value
      ? `WZ ${doll.value.width}×${doll.value.height} · origin(${doll.value.bodyOriginX}, ${doll.value.bodyOriginY})`
      : ''
  );

  const clampZoom = (v: number) =>
    Math.min(ZOOM_MAX, Math.max(ZOOM_MIN, Math.round(v * 100) / 100));

  const zoomIn = () => {
    zoom.value = clampZoom(zoom.value + ZOOM_STEP);
  };
  const zoomOut = () => {
    zoom.value = clampZoom(zoom.value - ZOOM_STEP);
  };
  const resetZoom = () => {
    zoom.value = 1;
  };

  /** 舞台内部滚轮缩放：只给增量，边界仍由本页掌控 */
  const applyZoomStep = (delta: number) => {
    zoom.value = clampZoom(zoom.value + delta);
  };

  /** 舞台尺寸由 DollStage 上报（用于拖拽时的合理取值区间） */
  const onStageResize = (size: number) => {
    stageSize.value = size;
  };

  const rowClass = (record: TableData) =>
    record.id === selectedId.value ? 'row-selected' : '';

  const loadStatus = async () => {
    statusLoading.value = true;
    try {
      const res = await fetchChairPoseStatus();
      status.value = res.data;
    } catch {
      // 拦截器已提示
    } finally {
      statusLoading.value = false;
    }
  };

  const reloadList = async () => {
    const kind = activeTab.value;
    listRequestSeq += 1;
    const seq = listRequestSeq;
    listLoading.value = true;
    try {
      if (kind === 'chairs') {
        const res = await fetchChairList({
          keyword: keyword.value,
          effect2Only: effect2Only.value,
          page: page.value,
          pageSize: pageSize.value,
        });
        // 若用户已切到另一 Tab / 发起了更新请求，丢弃过期响应
        if (seq !== listRequestSeq || activeTab.value !== kind) return;
        total.value = res.data?.total || 0;
        listRows.value = (res.data?.items || []).map((it) => ({
          id: it.itemId as number,
          name: it.name,
          iconUrl: it.iconUrl,
          flag: it.hasEffect2 ? t('clientChairPose.flag.effect2') : undefined,
        }));
      } else {
        const res = await fetchTamingMobList({
          keyword: keyword.value,
          page: page.value,
          pageSize: pageSize.value,
        });
        if (seq !== listRequestSeq || activeTab.value !== kind) return;
        total.value = res.data?.total || 0;
        listRows.value = (res.data?.items || []).map((it) => ({
          id: it.mobId as number,
          name: it.name,
          iconUrl: it.iconUrl,
          flag: it.hasNavel ? t('clientChairPose.flag.navel') : undefined,
        }));
      }
      listKind.value = kind;
    } catch {
      // 拦截器已提示；列表加载失败时清空，避免旧行误点进错误详情 API
      if (seq === listRequestSeq && activeTab.value === kind) {
        listRows.value = [];
        total.value = 0;
        listKind.value = kind;
      }
    } finally {
      if (seq === listRequestSeq) {
        listLoading.value = false;
      }
    }
  };

  const scheduleReloadList = () => {
    if (searchDebounceTimer != null) {
      clearTimeout(searchDebounceTimer);
    }
    searchDebounceTimer = setTimeout(() => {
      searchDebounceTimer = null;
      page.value = 1;
      reloadList();
    }, SEARCH_DEBOUNCE_MS);
  };

  const applyChairEffectToForm = (eff?: ChairPoseEffect) => {
    chairForm.originX = eff?.originX ?? 0;
    chairForm.originY = eff?.originY ?? 0;
    chairForm.pos = eff?.pos ?? 0;
    chairForm.z = eff?.z ?? 0;
  };

  const loadChairPreview = async (itemId: number, layer?: string) => {
    try {
      const res = await previewChair({ itemId, layer });
      chairPreview.value = res.data;
      if (res.data?.originX != null) chairForm.originX = res.data.originX;
      if (res.data?.originY != null) chairForm.originY = res.data.originY;
    } catch {
      chairPreview.value = null;
    }
  };

  const loadChairDetail = async (itemId: number) => {
    detailLoading.value = true;
    chairPreview.value = null;
    resetZoom();
    try {
      const res = await fetchChairDetail(itemId);
      chairDetail.value = res.data;
      chairEffects.value = [...(res.data.effects || [])];
      const first = chairEffects.value[0];
      activeLayer.value = first?.layer || 'effect';
      applyChairEffectToForm(first);
      await loadChairPreview(itemId, activeLayer.value);
    } catch {
      // 拦截器已弹出具体业务错误（如「未找到椅子节点」），不再叠「加载失败」
      chairDetail.value = null;
      chairEffects.value = [];
      chairPreview.value = null;
    } finally {
      detailLoading.value = false;
    }
  };

  const applyFrameToForm = (fr?: TamingMobPoseFrame) => {
    mobForm.navelX = fr?.navelX ?? 0;
    mobForm.navelY = fr?.navelY ?? 0;
    mobForm.originX = fr?.originX ?? 0;
    mobForm.originY = fr?.originY ?? 0;
    mobForm.frameIndex = fr?.frameIndex ?? 0;
  };

  const loadMobPreview = async (mobId: number) => {
    try {
      const res = await previewTamingMob({
        mobId,
        action: mobForm.action,
        frameIndex: mobForm.frameIndex,
      });
      mobPreview.value = res.data;
      if (res.data?.navelX != null) mobForm.navelX = res.data.navelX;
      if (res.data?.navelY != null) mobForm.navelY = res.data.navelY;
    } catch {
      mobPreview.value = null;
    }
  };

  const loadMobDetail = async (mobId: number) => {
    detailLoading.value = true;
    mobPreview.value = null;
    resetZoom();
    try {
      const res = await fetchTamingMobDetail(mobId);
      mobDetail.value = res.data;
      mobForm.action = res.data.defaultAction || res.data.actions?.[0] || '';
      const frames = (res.data.frames || []).filter(
        (f) => f.action === mobForm.action
      );
      applyFrameToForm(frames[0]);
      await loadMobPreview(mobId);
    } catch {
      mobDetail.value = null;
      mobPreview.value = null;
    } finally {
      detailLoading.value = false;
    }
  };

  const onRowClick = (record: TableData) => {
    const id = Number(record.id);
    if (!id) return;
    selectedId.value = id;
    // 必须按「列表实际种类」路由，不能用 activeTab（切 Tab 异步刷新窗口期）
    if (listKind.value === 'chairs') {
      mobDetail.value = null;
      mobPreview.value = null;
      loadChairDetail(id);
    } else {
      chairDetail.value = null;
      chairPreview.value = null;
      loadMobDetail(id);
    }
  };

  const onTabChange = () => {
    if (searchDebounceTimer != null) {
      clearTimeout(searchDebounceTimer);
      searchDebounceTimer = null;
    }
    page.value = 1;
    selectedId.value = null;
    chairDetail.value = null;
    chairPreview.value = null;
    mobDetail.value = null;
    mobPreview.value = null;
    resetZoom();
    // 立刻清空旧行，避免用新 Tab 的 activeTab 去点旧列表 ID
    listRows.value = [];
    total.value = 0;
    reloadList();
  };

  const onSearchCommit = () => {
    if (searchDebounceTimer != null) {
      clearTimeout(searchDebounceTimer);
      searchDebounceTimer = null;
    }
    page.value = 1;
    reloadList();
  };

  const onPageChange = (p: number) => {
    page.value = p;
    reloadList();
  };
  const onPageSizeChange = (ps: number) => {
    pageSize.value = ps;
    page.value = 1;
    reloadList();
  };

  const onChairLayerChange = () => {
    const eff = chairEffects.value.find((e) => e.layer === activeLayer.value);
    applyChairEffectToForm(eff);
    if (chairDetail.value) {
      loadChairPreview(chairDetail.value.itemId, activeLayer.value);
    }
  };

  const syncChairPreviewFromForm = () => {
    // local-only visual; values already bound
  };

  const syncMobPreviewFromForm = () => {
    // local-only visual
  };

  const persistChairFormIntoEffects = () => {
    chairEffects.value = chairEffects.value.map((e) => {
      if (e.layer !== activeLayer.value) return e;
      return {
        ...e,
        originX: chairForm.originX,
        originY: chairForm.originY,
        pos: chairForm.pos,
        z: chairForm.z,
      };
    });
  };

  const saveChair = async () => {
    if (!canEdit.value || !chairDetail.value) {
      Message.warning(t('clientChairPose.msg.editorRequired'));
      return;
    }
    persistChairFormIntoEffects();
    saving.value = true;
    try {
      const res = await writeChair({
        itemId: chairDetail.value.itemId,
        effects: chairEffects.value,
      });
      const warns = (res.data?.warnings || []).join('\n');
      Message.success(
        warns
          ? `${res.data?.message || t('clientChairPose.msg.saved')}\n${warns}`
          : res.data?.message || t('clientChairPose.msg.saved')
      );
      await loadChairDetail(chairDetail.value.itemId);
    } catch {
      // 拦截器已提示
    } finally {
      saving.value = false;
    }
  };

  const onMobActionChange = () => {
    const frames = framesForAction.value;
    applyFrameToForm(frames[0]);
    if (mobDetail.value) loadMobPreview(mobDetail.value.mobId);
  };

  const onMobFrameChange = () => {
    const fr = framesForAction.value.find(
      (f) => f.frameIndex === mobForm.frameIndex
    );
    applyFrameToForm(fr);
    if (mobDetail.value) loadMobPreview(mobDetail.value.mobId);
  };

  const saveMob = async () => {
    if (!canEdit.value || !mobDetail.value) {
      Message.warning(t('clientChairPose.msg.editorRequired'));
      return;
    }
    saving.value = true;
    try {
      const res = await writeTamingMob({
        mobId: mobDetail.value.mobId,
        writeMode: mobForm.writeMode,
        action: mobForm.action,
        frameIndex: mobForm.frameIndex,
        navelX: mobForm.navelX,
        navelY: mobForm.navelY,
        originX: mobForm.originX,
        originY: mobForm.originY,
      });
      const warns = (res.data?.warnings || []).join('\n');
      Message.success(
        warns
          ? `${res.data?.message || t('clientChairPose.msg.saved')}\n${warns}`
          : res.data?.message || t('clientChairPose.msg.saved')
      );
      await loadMobDetail(mobDetail.value.mobId);
    } catch {
      // 拦截器已提示
    } finally {
      saving.value = false;
    }
  };

  const clampAnchor = (v: number) => {
    const half = Math.max(8, Math.floor(stageSize.value / 2) - 4);
    return Math.min(half, Math.max(-half, v));
  };

  /**
   * 椅子拖拽：角色挂点固定在中心，拖动改变 origin → 椅子在挂点下平移。
   * 指针往右拖 = 椅子往右移 = originX 减小（与客户端 left = attach - origin 一致）。
   */
  const onChairDragStart = (point: StagePoint) => {
    if (!canEdit.value) return;
    dragging.value = true;
    dragStart.pointerX = point.x;
    dragStart.pointerY = point.y;
    dragStart.originX = Number(chairForm.originX || 0);
    dragStart.originY = Number(chairForm.originY || 0);
  };
  const onChairDragMove = (point: StagePoint) => {
    if (!dragging.value || !canEdit.value) return;
    const dx = point.x - dragStart.pointerX;
    const dy = point.y - dragStart.pointerY;
    chairForm.originX = clampAnchor(dragStart.originX - dx);
    chairForm.originY = clampAnchor(dragStart.originY - dy);
  };

  /**
   * 骑宠拖拽：贴图 origin 固定在中心，拖动改 map/navel（角色相对挂点偏移）。
   */
  const onMobDragStart = (point: StagePoint) => {
    if (!canEdit.value) return;
    dragging.value = true;
    dragStart.pointerX = point.x;
    dragStart.pointerY = point.y;
    dragStart.navelX = Number(mobForm.navelX || 0);
    dragStart.navelY = Number(mobForm.navelY || 0);
  };
  const onMobDragMove = (point: StagePoint) => {
    if (!dragging.value || !canEdit.value) return;
    const dx = point.x - dragStart.pointerX;
    const dy = point.y - dragStart.pointerY;
    mobForm.navelX = clampAnchor(dragStart.navelX + dx);
    mobForm.navelY = clampAnchor(dragStart.navelY + dy);
  };
  const onDragEnd = () => {
    dragging.value = false;
  };

  const runPatch = async (dryRun: boolean) => {
    if (!dryRun && !status.value?.allowClientWrite) {
      Message.warning(t('clientChairPose.msg.clientWriteRequired'));
      return;
    }
    patching.value = true;
    try {
      const res = dryRun ? await patchChairDryRun() : await patchChairApply();
      const msg = res.data?.message || t('clientChairPose.msg.patchDone');
      const warns = (res.data?.warnings || []).join('\n');
      Message.success(warns ? `${msg}\n${warns}` : msg);
    } catch {
      // 拦截器已提示
    } finally {
      patching.value = false;
    }
  };

  const confirmApply = () => {
    Modal.confirm({
      title: t('clientChairPose.patchApply'),
      content: t('clientChairPose.confirm.apply'),
      onOk: () => runPatch(false),
    });
  };

  const goClientPath = () => router.push({ name: 'ClientPath' });

  onMounted(async () => {
    await loadStatus();
    await loadDoll();
    await reloadList();
  });

  onBeforeUnmount(() => {
    if (searchDebounceTimer != null) {
      clearTimeout(searchDebounceTimer);
      searchDebounceTimer = null;
    }
    listRequestSeq += 1;
  });
</script>

<style scoped lang="less">
  .mb-alert {
    margin-bottom: 12px;
  }
  .pose-layout {
    display: flex;
    gap: 12px;
    margin-top: 12px;
    min-height: 640px;
  }
  .pose-sider {
    width: 360px;
    flex-shrink: 0;
  }
  .pose-main {
    flex: 1;
    min-width: 0;
  }
  .search-bar {
    margin: 8px 0 12px;
  }
  .list-pager {
    margin-top: 12px;
  }
  .row-icon {
    width: 24px;
    height: 24px;
    object-fit: contain;
  }
  .editor-grid {
    display: grid;
    grid-template-columns: minmax(280px, 380px) minmax(360px, 1fr);
    gap: 16px;
    align-items: start;
  }
  .editor-form {
    max-width: 380px;
    width: 100%;
  }
  .editor-form :deep(.arco-input-number),
  .editor-form :deep(.arco-select) {
    width: 100%;
  }
  .mono {
    word-break: break-all;
    font-size: 12px;
  }
  .preview-pane {
    border: 1px solid var(--color-border-2);
    border-radius: 4px;
    padding: 12px;
    min-width: 0;
    display: flex;
    flex-direction: column;
  }
  .preview-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
    flex-wrap: wrap;
    margin-bottom: 8px;
  }
  .preview-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
  }
  .zoom-label {
    min-width: 48px;
    text-align: center;
    font-size: 12px;
    color: var(--color-text-2);
    font-variant-numeric: tabular-nums;
  }
  .preview-hint,
  .preview-msg {
    margin-top: 8px;
    font-size: 12px;
    color: var(--color-text-3);
  }
  :deep(.row-selected) td {
    background: var(--color-fill-2) !important;
  }
  @media (max-width: 1100px) {
    .editor-grid {
      grid-template-columns: 1fr;
    }
    .editor-form {
      max-width: 400px;
    }
  }
</style>
