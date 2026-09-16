<template>
  <div class="skill-node" :class="{ muted: data.invisible }">
    <Handle type="target" :position="Position.Top" />
    <img
      v-if="data.iconUrl && !iconFailed"
      class="skill-icon"
      :src="data.iconUrl"
      alt=""
      @error="onIconError"
    />
    <div v-else class="skill-icon skill-icon-placeholder" aria-hidden="true" />
    <div class="skill-node-text">
      <div class="skill-node-name">{{ displayName }}</div>
      <div class="skill-node-id">{{ data.skillId }}</div>
    </div>
    <Handle type="source" :position="Position.Bottom" />
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref, watch } from 'vue';
  import { Handle, Position } from '@vue-flow/core';

  const props = defineProps<{
    data: {
      label?: string;
      skillId?: number;
      iconUrl?: string;
      invisible?: boolean;
    };
  }>();

  const iconFailed = ref(false);

  watch(
    () => props.data.iconUrl,
    () => {
      iconFailed.value = false;
    }
  );

  const displayName = computed(() => {
    const label = props.data.label?.trim();
    if (label) return label;
    return props.data.skillId != null ? String(props.data.skillId) : '';
  });

  const onIconError = () => {
    iconFailed.value = true;
  };
</script>

<style scoped>
  .skill-node {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 10px;
    background: var(--color-bg-2);
    border: 1px solid var(--color-border-3);
    border-radius: 6px;
    min-width: 140px;
  }
  .skill-node.muted {
    opacity: 0.55;
  }
  .skill-icon {
    width: 32px;
    height: 32px;
    flex-shrink: 0;
    image-rendering: pixelated;
  }
  .skill-icon-placeholder {
    background: var(--color-fill-2);
    border-radius: 4px;
  }
  .skill-node-text {
    min-width: 0;
  }
  .skill-node-name {
    font-size: 12px;
    font-weight: 600;
    line-height: 1.3;
    word-break: break-word;
  }
  .skill-node-id {
    font-size: 10px;
    color: var(--color-text-3);
    margin-top: 2px;
  }
</style>
