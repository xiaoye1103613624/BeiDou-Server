<template>
  <div class="item-id-cell">
    <ItemIcon
      v-if="numericId > 0"
      :id="numericId"
      :category="category"
      :size="iconSize"
    />
    <a-input-number
      v-if="editable"
      :model-value="numericId || undefined"
      :min="0"
      hide-button
      class="item-id-input"
      @update:model-value="onInput"
    />
    <span v-else class="item-id-text">{{ numericId || '—' }}</span>
  </div>
</template>

<script lang="ts" setup>
  import { computed } from 'vue';

  const props = withDefaults(
    defineProps<{
      modelValue?: number | string | null;
      category?: string;
      iconSize?: number;
      editable?: boolean;
    }>(),
    {
      modelValue: 0,
      category: 'item',
      iconSize: 28,
      editable: true,
    }
  );

  const emit = defineEmits<{
    (e: 'update:modelValue', value: number): void;
  }>();

  const numericId = computed(() => Number(props.modelValue) || 0);

  const onInput = (v: number | undefined) => {
    emit(
      'update:modelValue',
      v == null || Number.isNaN(Number(v)) ? 0 : Number(v)
    );
  };
</script>

<style lang="less" scoped>
  .item-id-cell {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    min-width: 0;
  }

  .item-id-input {
    width: 110px;
  }

  .item-id-text {
    font-variant-numeric: tabular-nums;
  }
</style>
