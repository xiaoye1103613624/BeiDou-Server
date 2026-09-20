<script lang="ts" setup>
  import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
  import type { CharacterDollResult } from '@/api/characterDoll';
  import {
    crosshairStyle,
    dollByBodyOrigin,
    dollByNavel,
    dollSilhouetteStyle,
    originSpriteStyle,
    type DollGeometry,
    type StagePoint,
  } from '@/utils/posePreviewLayout';

  /**
   * 舞台：WZ 1:1 影棚（透明棋盘格 + 锚点十字线），统一给座椅设置、坐骑设置、人偶试穿复用。
   *
   * 定位规则与客户端一致：精灵左上 = 挂点 − origin；挂点随场景不同：
   * - bodyOrigin：挂点在舞台中心，人偶身体原点与物品 origin 都钉在这里（座椅）；
   * - navel：挂点 = 中心 + 坐骑 map/navel，人偶肚脐钉在挂点（骑乘）。
   */
  const props = withDefaults(
    defineProps<{
      /** 物品贴图（椅子 effect / 坐骑帧） */
      itemImageUrl?: string;
      itemOriginX?: number;
      itemOriginY?: number;
      /** 负值 z 的特效画在人偶之后（与客户端层序近似） */
      itemZIndex?: number;
      itemCategory?: string;
      itemId?: string;
      /** 本地合成人偶结果；为空/非 DOLL 时回退剪影 */
      doll?: CharacterDollResult | null;
      anchorMode?: 'bodyOrigin' | 'navel';
      /** 挂点相对舞台中心的偏移（坐骑 map/navel） */
      attachX?: number;
      attachY?: number;
      zoom?: number;
      zoomStep?: number;
      zoomMax?: number;
      zoomMin?: number;
      draggable?: boolean;
      /** 左下角读数（WZ 坐标），省略则不显示 */
      anchorLabel?: string;
      showCrosshair?: boolean;
    }>(),
    {
      itemImageUrl: '',
      itemOriginX: 0,
      itemOriginY: 0,
      itemZIndex: 1,
      itemCategory: 'item',
      itemId: '',
      doll: null,
      anchorMode: 'bodyOrigin',
      attachX: 0,
      attachY: 0,
      zoom: 1,
      zoomStep: 0.25,
      zoomMax: 4,
      zoomMin: 0.5,
      draggable: false,
      anchorLabel: '',
      showCrosshair: true,
    }
  );

  const emit = defineEmits<{
    (e: 'zoomStepChange', delta: number): void;
    (e: 'dragStart', point: StagePoint): void;
    (e: 'dragMove', point: StagePoint): void;
    (e: 'dragEnd'): void;
    (e: 'stageResize', size: number): void;
    (e: 'itemError', event: Event): void;
  }>();

  const STAGE_FALLBACK = 480;

  const stageRef = ref<HTMLElement | null>(null);
  const stageSize = ref(STAGE_FALLBACK);
  const dragging = ref(false);
  let stageObserver: ResizeObserver | null = null;

  const zoomLayerStyle = computed(() => ({
    width: `${stageSize.value}px`,
    height: `${stageSize.value}px`,
    transform: `scale(${props.zoom})`,
  }));

  /** 物品贴图钉在原点的位置即为舞台中心 */
  const itemAnchor = computed<StagePoint>(() => ({
    x: stageSize.value / 2,
    y: stageSize.value / 2,
  }));

  /** 人偶挂点：座椅 = 中心；坐骑 = 中心 + map/navel */
  const dollAnchor = computed<StagePoint>(() => ({
    x: itemAnchor.value.x + Number(props.attachX || 0),
    y: itemAnchor.value.y + Number(props.attachY || 0),
  }));

  const dollReady = computed(
    () => props.doll?.mode === 'DOLL' && !!props.doll?.imageUrl
  );
  const dollImage = computed(() => props.doll?.imageUrl || '');
  const dollGeometry = computed<DollGeometry>(() => ({
    width: props.doll?.width,
    height: props.doll?.height,
    bodyOriginX: props.doll?.bodyOriginX,
    bodyOriginY: props.doll?.bodyOriginY,
    navelX: props.doll?.navelX,
    navelY: props.doll?.navelY,
  }));

  const itemStyle = computed(() =>
    originSpriteStyle(
      itemAnchor.value,
      Number(props.itemOriginX || 0),
      Number(props.itemOriginY || 0),
      props.itemZIndex
    )
  );

  /** 人偶层 z：永远压在物品「后层」之上、前层之下（物品 z 由 props 决定，这里固定 2） */
  const DOLL_Z = 2;
  const dollStyle = computed(() =>
    props.anchorMode === 'navel'
      ? dollByNavel(dollAnchor.value, dollGeometry.value, DOLL_Z)
      : dollByBodyOrigin(dollAnchor.value, dollGeometry.value, DOLL_Z)
  );

  /**
   * 剪影兜底（无人偶时）：画在挂点之上的躯干轮廓。
   * bodyOrigin 模式下挂点即身体原点，navel 模式下挂点是肚脐，二者都按同一点近似。
   */
  const silhouetteStyle = computed(() =>
    dollSilhouetteStyle(dollAnchor.value, { x: 0, y: 0 }, DOLL_Z)
  );
  const crosshair = computed(() => crosshairStyle(dollAnchor.value, 5));

  /** 指针相对舞台中心的偏移（已除以缩放），交给上层做拖拽语义 */
  const pointerOffset = (ev: PointerEvent): StagePoint => {
    const el = stageRef.value;
    if (!el) return { x: 0, y: 0 };
    const rect = el.getBoundingClientRect();
    const z = props.zoom || 1;
    return {
      x: Math.round((ev.clientX - (rect.left + rect.width / 2)) / z),
      y: Math.round((ev.clientY - (rect.top + rect.height / 2)) / z),
    };
  };

  const onPointerDown = (ev: PointerEvent) => {
    if (!props.draggable) return;
    dragging.value = true;
    (ev.currentTarget as HTMLElement).setPointerCapture?.(ev.pointerId);
    emit('dragStart', pointerOffset(ev));
  };
  const onPointerMove = (ev: PointerEvent) => {
    if (!dragging.value) return;
    emit('dragMove', pointerOffset(ev));
  };
  const onPointerUp = () => {
    if (!dragging.value) return;
    dragging.value = false;
    emit('dragEnd');
  };

  const onWheel = (ev: WheelEvent) => {
    emit('zoomStepChange', ev.deltaY > 0 ? -props.zoomStep : props.zoomStep);
  };

  const measureStage = () => {
    const el = stageRef.value;
    if (!el) return;
    const size = Math.min(el.clientWidth, el.clientHeight);
    if (size > 0 && size !== stageSize.value) {
      stageSize.value = size;
      emit('stageResize', size);
    }
  };

  const bindObserver = async () => {
    await nextTick();
    stageObserver?.disconnect();
    stageObserver = null;
    const el = stageRef.value;
    if (!el || typeof ResizeObserver === 'undefined') {
      measureStage();
      return;
    }
    stageObserver = new ResizeObserver(() => measureStage());
    stageObserver.observe(el);
    measureStage();
  };

  onMounted(bindObserver);
  onBeforeUnmount(() => {
    stageObserver?.disconnect();
    stageObserver = null;
  });

  defineExpose({ remeasure: bindObserver });
</script>

<template>
  <div
    ref="stageRef"
    class="doll-stage"
    :class="{ 'is-draggable': draggable }"
    @pointerdown="onPointerDown"
    @pointermove="onPointerMove"
    @pointerup="onPointerUp"
    @pointerleave="onPointerUp"
    @wheel.prevent="onWheel"
  >
    <div class="doll-zoom-layer" :style="zoomLayerStyle">
      <img
        v-if="itemImageUrl"
        :src="itemImageUrl"
        class="item-img"
        alt=""
        draggable="false"
        :style="itemStyle"
        :data-category="itemCategory"
        :data-item-id="itemId || undefined"
        @error="emit('itemError', $event)"
      />
      <img
        v-if="dollReady"
        :src="dollImage"
        class="doll-img"
        alt=""
        draggable="false"
        :style="dollStyle"
      />
      <div v-else class="doll-silhouette" :style="silhouetteStyle" />
      <div v-if="showCrosshair" class="doll-crosshair" :style="crosshair" />
    </div>
    <div v-if="anchorLabel" class="stage-readout">{{ anchorLabel }}</div>
  </div>
</template>

<style scoped lang="less">
  .doll-stage {
    position: relative;
    width: 100%;
    height: min(70vh, 560px);
    min-height: 420px;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    /* 透明通道影棚底：能直接看出人偶/特效图是否真的 1:1 */
    background-color: var(--color-fill-1);
    background-image: linear-gradient(
        45deg,
        var(--color-fill-2) 25%,
        transparent 25%
      ),
      linear-gradient(-45deg, var(--color-fill-2) 25%, transparent 25%),
      linear-gradient(45deg, transparent 75%, var(--color-fill-2) 75%),
      linear-gradient(-45deg, transparent 75%, var(--color-fill-2) 75%);
    background-size: 16px 16px;
    background-position: 0 0, 0 8px, 8px -8px, -8px 0;
    border: 1px solid var(--color-border-2);
    border-radius: 4px;
    touch-action: none;
    cursor: crosshair;
  }

  .doll-stage.is-draggable {
    cursor: grab;
  }

  .doll-zoom-layer {
    position: relative;
    flex-shrink: 0;
    transform-origin: center center;
    will-change: transform;
  }

  /* 像素单位对齐，禁止任何额外缩放/居中（max-width 会把 origin 拉漂） */
  .item-img,
  .doll-img {
    position: absolute;
    max-width: none;
    max-height: none;
    width: auto;
    height: auto;
    image-rendering: pixelated;
    pointer-events: none;
  }

  .item-img {
    user-select: none;
  }

  .doll-silhouette {
    position: absolute;
    width: 20px;
    height: 36px;
    border-radius: 8px 8px 4px 4px;
    background: rgba(22, 93, 255, 0.25);
    border: 1px solid rgba(22, 93, 255, 0.7);
    pointer-events: none;
  }

  /* 锚点十字线：描边配色保证在任何贴图上都可辨认 */
  .doll-crosshair {
    position: absolute;
    width: 18px;
    height: 18px;
    margin-left: -9px;
    margin-top: -9px;
    pointer-events: none;
  }

  .doll-crosshair::before,
  .doll-crosshair::after {
    content: '';
    position: absolute;
    background: #f53f3f;
    box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.55);
  }

  .doll-crosshair::before {
    left: 8px;
    top: 0;
    width: 2px;
    height: 18px;
  }

  .doll-crosshair::after {
    left: 0;
    top: 8px;
    width: 18px;
    height: 2px;
  }

  .stage-readout {
    position: absolute;
    left: 8px;
    bottom: 8px;
    padding: 2px 8px;
    border-radius: 2px;
    background: rgba(0, 0, 0, 0.55);
    color: #fff;
    font-size: 12px;
    font-variant-numeric: tabular-nums;
    pointer-events: none;
  }
</style>
