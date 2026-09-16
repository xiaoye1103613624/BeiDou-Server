<template>
  <a-layout-footer class="footer">
    <span class="footer-mark" />
    <span>{{ $t('title') }} Admin</span>
  </a-layout-footer>
</template>

<script lang="ts" setup></script>

<style lang="less" scoped>
  .footer {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    height: 40px;
    color: var(--bd-ink-soft);
    font-size: 12px;
    text-align: center;
    background: transparent;
  }

  .footer-mark {
    width: 6px;
    height: 6px;
    background: linear-gradient(135deg, #c9a84c, #1b6b93);
    border-radius: 50%;
    box-shadow: 0 0 0 3px rgba(27, 107, 147, 0.12);
  }
</style>
