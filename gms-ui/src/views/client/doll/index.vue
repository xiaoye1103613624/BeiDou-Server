<template>
  <PageContainer
    :title="$t('menu.client.doll')"
    :description="$t('clientDoll.page.desc')"
  >
    <a-alert type="info" class="mb-alert">{{ $t('clientDoll.page.hint') }}</a-alert>

    <div class="doll-layout">
      <ProCard class="doll-panel">
        <template #title>{{ $t('clientDoll.appearance.title') }}</template>
        <a-form :model="form" layout="vertical">
          <div class="look-grid">
            <a-form-item :label="$t('clientDoll.form.skin')">
              <a-select v-model="form.skinId">
                <a-option
                  v-for="skin in SKIN_OPTIONS"
                  :key="skin"
                  :value="skin"
                  :label="String(skin)"
                />
              </a-select>
            </a-form-item>
            <a-form-item :label="$t('clientDoll.form.pose')">
              <a-select v-model="form.pose">
                <a-option
                  v-for="pose in POSE_OPTIONS"
                  :key="pose"
                  :value="pose"
                  :label="pose"
                />
              </a-select>
            </a-form-item>
            <a-form-item :label="$t('clientDoll.form.frame')">
              <a-input-number v-model="form.frame" :min="0" :max="30" />
            </a-form-item>
          </div>

          <div class="look-grid">
            <a-form-item :label="$t('clientDoll.form.hair')">
              <a-input-number
                v-model="form.hairId"
                :min="0"
                allow-clear
                class="id-input"
              >
                <template #prefix>
                  <ItemIcon :id="form.hairId" category="item" :size="22" />
                </template>
              </a-input-number>
            </a-form-item>
            <a-form-item :label="$t('clientDoll.form.face')">
              <a-input-number
                v-model="form.faceId"
                :min="0"
                allow-clear
                class="id-input"
              >
                <template #prefix>
                  <ItemIcon :id="form.faceId" category="item" :size="22" />
                </template>
              </a-input-number>
            </a-form-item>
          </div>

          <a-form-item :label="$t('clientDoll.form.equips')">
            <div class="equip-add">
              <a-input-number
                v-model="newEquipId"
                :min="0"
                :placeholder="$t('clientDoll.form.equipPlaceholder')"
                @press-enter="addEquip"
              />
              <a-button type="primary" size="small" @click="addEquip">
                {{ $t('clientDoll.form.add') }}
              </a-button>
            </div>
            <div v-if="equipIds.length" class="equip-list">
              <div v-for="id in equipIds" :key="id" class="equip-chip">
                <ItemIcon :id="id" category="item" :size="24" />
                <span class="equip-id">{{ id }}</span>
                <icon-delete class="equip-del" @click="removeEquip(id)" />
              </div>
            </div>
            <div v-else class="equip-empty">
              {{ $t('clientDoll.form.noEquip') }}
            </div>
          </a-form-item>

          <a-space wrap>
            <a-button type="primary" :loading="loading" @click="renderDoll(false)">
              {{ $t('clientDoll.action.render') }}
            </a-button>
            <a-button :loading="loading" @click="renderDoll(true)">
              {{ $t('clientDoll.action.rebuild') }}
            </a-button>
            <a-button @click="resetLook">{{ $t('clientDoll.action.reset') }}</a-button>
          </a-space>
        </a-form>
      </ProCard>

      <div class="doll-stage-col">
        <ProCard>
          <div class="preview-toolbar">
            <span class="preview-title">
              {{ $t('clientDoll.stage.title') }}
              <a-tag size="small">{{ doll?.mode || '-' }}</a-tag>
            </span>
            <a-space size="mini">
              <a-button size="mini" :title="$t('clientDoll.stage.zoomOut')" @click="zoomBy(-0.25)">
                <icon-minus />
              </a-button>
              <span class="zoom-label">{{ zoomPercent }}%</span>
              <a-button size="mini" :title="$t('clientDoll.stage.zoomIn')" @click="zoomBy(0.25)">
                <icon-plus />
              </a-button>
              <a-button size="mini" @click="zoom = 1">
                {{ $t('clientDoll.stage.zoomReset') }}
              </a-button>
            </a-space>
          </div>
          <DollStage
            :doll="doll"
            anchor-mode="bodyOrigin"
            :zoom="zoom"
            :zoom-step="0.25"
            :anchor-label="anchorLabel"
            @zoom-step-change="zoomBy"
          />
          <div class="preview-hint">{{ $t('clientDoll.stage.hint') }}</div>
        </ProCard>

        <ProCard v-if="doll" class="info-card">
          <template #title>{{ $t('clientDoll.info.title') }}</template>
          <a-descriptions :column="2" size="small" bordered>
            <a-descriptions-item :label="$t('clientDoll.info.size')">
              {{ doll.width }} × {{ doll.height }}
            </a-descriptions-item>
            <a-descriptions-item :label="$t('clientDoll.info.lookKey')">
              {{ doll.lookKey || '-' }}
            </a-descriptions-item>
            <a-descriptions-item :label="$t('clientDoll.info.bodyOrigin')">
              {{ doll.bodyOriginX }}, {{ doll.bodyOriginY }}
            </a-descriptions-item>
            <a-descriptions-item :label="$t('clientDoll.info.navel')">
              {{ doll.navelX ?? '-' }}, {{ doll.navelY ?? '-' }}
            </a-descriptions-item>
          </a-descriptions>
          <div v-if="doll.message" class="preview-msg">{{ doll.message }}</div>
          <a-collapse v-if="doll.missingParts?.length" class="info-collapse">
            <a-collapse-item
              :header="$t('clientDoll.info.missing', { n: doll.missingParts.length })"
              :key="'missing'"
            >
              <div v-for="p in doll.missingParts" :key="p" class="info-line">
                {{ p }}
              </div>
            </a-collapse-item>
          </a-collapse>
          <a-collapse v-if="doll.zOrder?.length" class="info-collapse">
            <a-collapse-item :header="$t('clientDoll.info.zOrder')" :key="'z'">
              <div class="info-z">
                <a-tag v-for="z in doll.zOrder" :key="z" size="small">{{ z }}</a-tag>
              </div>
            </a-collapse-item>
          </a-collapse>
        </ProCard>
      </div>
    </div>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { computed, onMounted, reactive, ref, watch } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import ItemIcon from '@/components/item-icon/index.vue';
  import DollStage from '@/components/pose/DollStage.vue';
  import {
    renderCharacterDoll,
    type CharacterDollResult,
  } from '@/api/characterDoll';

  const { t } = useI18n();

  /** 皮肤即 Character/0000{skin}.img；这套与仓库现有基础件一一对应 */
  const SKIN_OPTIONS = [2000, 2001, 2002, 2003, 2004, 2005, 2009, 2010, 2011];
  const POSE_OPTIONS = ['sit', 'stand1', 'stand2', 'walk1', 'ladder', 'rope'];
  const DEFAULT_LOOK = {
    skinId: 2000,
    faceId: 20000,
    hairId: 30000,
    equipIds: [1040036, 1060026, 1070003],
  };

  const ZOOM_MIN = 0.5;
  const ZOOM_MAX = 4;

  const form = reactive({
    skinId: DEFAULT_LOOK.skinId,
    faceId: DEFAULT_LOOK.faceId,
    hairId: DEFAULT_LOOK.hairId,
    pose: 'sit',
    frame: 0,
  });
  const equipIds = ref<number[]>([...DEFAULT_LOOK.equipIds]);
  const newEquipId = ref<number | undefined>(undefined);
  const doll = ref<CharacterDollResult | null>(null);
  const loading = ref(false);
  const zoom = ref(1);

  const zoomPercent = computed(() => Math.round(zoom.value * 100));
  const anchorLabel = computed(() =>
    doll.value
      ? `WZ ${doll.value.width}×${doll.value.height} · origin(${doll.value.bodyOriginX}, ${doll.value.bodyOriginY})`
      : ''
  );

  const clampZoom = (v: number) =>
    Math.min(ZOOM_MAX, Math.max(ZOOM_MIN, Math.round(v * 100) / 100));

  const zoomBy = (delta: number) => {
    zoom.value = clampZoom(zoom.value + delta);
  };

  const addEquip = () => {
    const id = Number(newEquipId.value || 0);
    if (id <= 0) {
      Message.warning(t('clientDoll.msg.invalidEquip'));
      return;
    }
    if (!equipIds.value.includes(id)) {
      equipIds.value = [...equipIds.value, id];
    }
    newEquipId.value = undefined;
  };

  const removeEquip = (id: number) => {
    equipIds.value = equipIds.value.filter((v) => v !== id);
  };

  const resetLook = () => {
    form.skinId = DEFAULT_LOOK.skinId;
    form.faceId = DEFAULT_LOOK.faceId;
    form.hairId = DEFAULT_LOOK.hairId;
    equipIds.value = [...DEFAULT_LOOK.equipIds];
  };

  const renderDoll = async (refresh = false) => {
    loading.value = true;
    try {
      const res = await renderCharacterDoll({
        skinId: form.skinId,
        faceId: form.faceId,
        hairId: form.hairId,
        equipIds: equipIds.value,
        pose: form.pose,
        frame: form.frame,
        refresh,
      });
      doll.value = res.data || null;
    } catch {
      doll.value = null;
    } finally {
      loading.value = false;
    }
  };

  let debounceTimer: ReturnType<typeof setTimeout> | null = null;
  watch(
    [() => ({ ...form }), equipIds],
    () => {
      if (debounceTimer) clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => renderDoll(false), 400);
    },
    { deep: true }
  );

  onMounted(() => renderDoll(false));
</script>

<style scoped lang="less">
  .mb-alert {
    margin-bottom: 12px;
  }

  .doll-layout {
    display: grid;
    grid-template-columns: minmax(300px, 380px) 1fr;
    gap: 12px;
    align-items: start;
  }

  .doll-stage-col {
    display: flex;
    flex-direction: column;
    gap: 12px;
    min-width: 0;
  }

  .look-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 0 12px;
  }

  .id-input {
    width: 100%;
  }

  .equip-add {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-bottom: 8px;
  }

  .equip-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .equip-chip {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 2px 8px;
    border: 1px solid var(--color-border-2);
    border-radius: 4px;
    background: var(--color-fill-1);
  }

  .equip-id {
    font-size: 12px;
    font-variant-numeric: tabular-nums;
  }

  .equip-del {
    cursor: pointer;
    color: var(--color-text-3);

    &:hover {
      color: rgb(var(--red-6));
    }
  }

  .equip-empty {
    font-size: 12px;
    color: var(--color-text-3);
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

  .info-card {
    :deep(.arco-descriptions-item-label) {
      width: 120px;
    }
  }

  .info-collapse {
    margin-top: 8px;
  }

  .info-line {
    font-size: 12px;
    color: var(--color-text-3);
    word-break: break-all;
  }

  .info-z {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
  }

  @media (max-width: 1100px) {
    .doll-layout {
      grid-template-columns: 1fr;
    }
  }
</style>
