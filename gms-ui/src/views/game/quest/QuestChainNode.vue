<template>
  <div
    class="quest-chain-node"
    :class="{ current: data.current }"
    @click="data.onSelect?.(data.questId)"
  >
    <Handle type="target" :position="Position.Left" />
    <ItemIcon
      v-if="data.startNpcId"
      :id="data.startNpcId"
      category="npc"
      :size="28"
      prefer-cdn
      img-class="quest-chain-npc"
    />
    <div v-else class="quest-chain-npc quest-chain-npc-placeholder" />
    <div class="quest-chain-text">
      <div class="quest-chain-id">#{{ data.questId }}</div>
      <div class="quest-chain-name" :title="displayName">{{ displayName }}</div>
      <div v-if="data.parentName" class="quest-chain-parent">
        {{ data.parentName }}
      </div>
    </div>
    <a-button
      type="text"
      size="mini"
      class="quest-chain-edit"
      @click.stop="data.onEdit?.(data.questId)"
    >
      {{ editLabel }}
    </a-button>
    <Handle type="source" :position="Position.Right" />
  </div>
</template>

<script lang="ts" setup>
  import { computed } from 'vue';
  import { Handle, Position } from '@vue-flow/core';
  import { useI18n } from 'vue-i18n';
  import ItemIcon from '@/components/item-icon/index.vue';

  const props = defineProps<{
    data: {
      questId?: number;
      name?: string;
      parentName?: string;
      startNpcId?: number;
      current?: boolean;
      onSelect?: (questId?: number) => void;
      onEdit?: (questId?: number) => void;
    };
  }>();

  const { t } = useI18n();
  const editLabel = computed(() => t('quest.chain.nodeEdit'));
  const displayName = computed(() => {
    const label = props.data.name?.trim();
    if (label) return label;
    return props.data.questId != null ? String(props.data.questId) : '';
  });
</script>

<style scoped>
  .quest-chain-node {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 10px;
    background: var(--color-bg-2);
    border: 1px solid var(--color-border-3);
    border-radius: 6px;
    min-width: 180px;
    max-width: 240px;
    cursor: pointer;
  }
  .quest-chain-node.current {
    border-color: rgb(var(--primary-6));
    background: var(--color-primary-light-1);
  }
  .quest-chain-npc {
    width: 28px;
    height: 28px;
    flex-shrink: 0;
    image-rendering: pixelated;
  }
  .quest-chain-npc-placeholder {
    background: var(--color-fill-2);
    border-radius: 4px;
  }
  .quest-chain-text {
    min-width: 0;
    flex: 1;
  }
  .quest-chain-id {
    font-size: 11px;
    opacity: 0.7;
    line-height: 1.2;
  }
  .quest-chain-name {
    font-size: 13px;
    font-weight: 600;
    line-height: 1.3;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .quest-chain-parent {
    font-size: 11px;
    opacity: 0.65;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .quest-chain-edit {
    flex-shrink: 0;
    padding: 0 4px;
  }
</style>
