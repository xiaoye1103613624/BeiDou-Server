<template>
  <a-modal
    v-model:visible="visible"
    :title="$t('cdk.title.batchGen')"
    :ok-loading="submitLoading"
    :width="640"
    @before-ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-form ref="batchFormRef" :model="formData" layout="vertical">
      <!-- 生成参数 -->
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item label="生成数量" :rules="[{ required: true }]">
            <a-input-number
              v-model="formData.count"
              :min="1"
              :max="1000"
              placeholder="10"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="码长度">
            <a-input-number
              v-model="formData.length"
              :min="6"
              :max="16"
              placeholder="10"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="前缀（可选）">
            <a-input
              v-model="formData.prefix"
              placeholder="VIP"
              :max-length="8"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <!-- 奖励配置 -->
      <a-row :gutter="16">
        <a-col :span="6">
          <a-form-item :label="$t('cdk.column.nxCredit')">
            <a-input-number
              v-model="formData.nxCredit"
              :min="0"
              placeholder="0"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item :label="$t('cdk.column.nxPrepaid')">
            <a-input-number
              v-model="formData.nxPrepaid"
              :min="0"
              placeholder="0"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item :label="$t('cdk.column.meso')">
            <a-input-number
              v-model="formData.meso"
              :min="0"
              placeholder="0"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item :label="$t('cdk.column.maxUseCount')">
            <a-input-number
              v-model="formData.maxUseCount"
              :min="1"
              placeholder="1"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item :label="$t('cdk.column.expireTime')">
            <a-date-picker
              v-model="formData.expireTime"
              show-time
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="永不过期"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item :label="$t('cdk.column.comment')">
            <a-input v-model="formData.comment" placeholder="备注" />
          </a-form-item>
        </a-col>
      </a-row>

      <!-- 道具 -->
      <a-divider />
      <div
        style="
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
        "
      >
        <span style="font-weight: 600">{{ $t('cdk.title.items') }}</span>
        <a-button type="primary" size="mini" @click="addItem"
          >+ {{ $t('cdk.button.addItem') }}</a-button
        >
      </div>
      <div
        v-for="(item, index) in formData.items"
        :key="index"
        style="margin-bottom: 8px"
      >
        <a-row :gutter="8" align="center">
          <a-col :span="10">
            <a-input-number
              v-model="item.itemId"
              :min="0"
              placeholder="物品ID"
              style="width: 100%"
            />
          </a-col>
          <a-col :span="8">
            <a-input-number
              v-model="item.quantity"
              :min="1"
              placeholder="数量"
              style="width: 100%"
            />
          </a-col>
          <a-col :span="6">
            <a-button
              type="text"
              size="mini"
              status="danger"
              @click="removeItem(index)"
              >✕</a-button
            >
          </a-col>
        </a-row>
      </div>
    </a-form>

    <!-- 批量生成结果 -->
    <div v-if="batchResult" style="margin-top: 16px">
      <a-alert type="success">
        {{ $t('cdk.batchGen.totalCount', { count: batchResult.totalCount }) }}
      </a-alert>
      <div
        style="
          max-height: 200px;
          overflow-y: auto;
          margin-top: 8px;
          background: #f5f5f5;
          padding: 12px;
          border-radius: 4px;
        "
      >
        <div
          v-for="(code, i) in batchResult.codeList"
          :key="i"
          style="font-family: monospace; padding: 2px 0"
        >
          {{ code }}
        </div>
      </div>
      <a-button
        type="outline"
        size="small"
        style="margin-top: 8px"
        @click="copyCodes"
      >
        {{ $t('cdk.button.copyCode') }}
      </a-button>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import {
    batchGenerateCdk,
    type CdkBatchGenForm,
    type CdkItemForm,
    type CdkBatchGenResult,
  } from '@/api/cdk';

  const visible = ref(false);
  const submitLoading = ref(false);
  const batchFormRef = ref();
  const batchResult = ref<CdkBatchGenResult | null>(null);

  const formData = ref<CdkBatchGenForm>({
    count: 10,
    length: 10,
    prefix: '',
    type: 2,
    nxCredit: 0,
    nxPrepaid: 0,
    meso: 0,
    maxUseCount: 1,
    expireTime: '',
    enabled: 1,
    comment: '',
    items: [],
  });

  /** 初始化 */
  const initForm = () => {
    formData.value = {
      count: 10,
      length: 10,
      prefix: '',
      type: 2,
      nxCredit: 0,
      nxPrepaid: 0,
      meso: 0,
      maxUseCount: 1,
      expireTime: '',
      enabled: 1,
      comment: '',
      items: [],
    };
    batchResult.value = null;
    visible.value = true;
  };

  const addItem = () => {
    if (!formData.value.items) formData.value.items = [];
    formData.value.items.push({ itemId: undefined, quantity: 1 });
  };

  const removeItem = (index: number) => {
    formData.value.items?.splice(index, 1);
  };

  const handleSubmit = async (): Promise<boolean> => {
    if (batchResult.value) {
      visible.value = false;
      return true;
    }

    submitLoading.value = true;
    try {
      if (formData.value.items) {
        formData.value.items = formData.value.items.filter(
          (i: CdkItemForm) => i.itemId && i.itemId > 0
        );
      }

      // 校验至少一项奖励
      const hasReward =
        (formData.value.nxCredit || 0) > 0 ||
        (formData.value.nxPrepaid || 0) > 0 ||
        (formData.value.meso || 0) > 0 ||
        (formData.value.items && formData.value.items.length > 0);

      if (!hasReward) {
        Message.warning('请至少配置一项奖励');
        return false;
      }

      const { data } = await batchGenerateCdk(formData.value);
      batchResult.value = data;
      Message.success('批量生成完成');
      return false; // 不关闭弹窗，让用户复制码
    } catch {
      return false;
    } finally {
      submitLoading.value = false;
    }
  };

  const copyCodes = () => {
    if (batchResult.value) {
      const text = batchResult.value.codeList.join('\n');
      navigator.clipboard.writeText(text).then(() => {
        Message.success('已复制到剪贴板');
      });
    }
  };

  const handleCancel = () => {
    visible.value = false;
    batchResult.value = null;
  };

  defineExpose({ initForm });
</script>

<script lang="ts">
  export default { name: 'CdkBatchForm' };
</script>
