<template>
  <a-modal
    v-model:visible="visible"
    :title="editing ? '编辑赞助配置' : '创建赞助配置'"
    :ok-loading="submitLoading"
    @before-ok="handleSubmit"
    @cancel="handleCancel"
    :width="600"
  >
    <a-form ref="formRef" :model="formData" layout="vertical">
      <!-- 配置名称 -->
      <a-form-item
        field="name"
        :label="$t('sponsor.column.name')"
        :rules="[{ required: true, message: '请输入配置名称' }]"
      >
        <a-input
          v-model="formData.name"
          placeholder="例如：初级赞助、高级赞助"
          style="width: 100%"
        />
      </a-form-item>

      <!-- 赞助金额 -->
      <a-form-item
        field="amount"
        :label="$t('sponsor.column.amount')"
        :rules="[{ required: true, message: '请输入赞助金额阈值' }]"
      >
        <a-input-number
          v-model="formData.amount"
          :min="0"
          placeholder="例如：99"
          style="width: 100%"
        />
      </a-form-item>

      <!-- 启用 -->
      <a-form-item :label="$t('sponsor.column.enabled')">
        <a-switch
          v-model="formData.enabled"
          :checked-value="1"
          :unchecked-value="0"
        />
      </a-form-item>

      <!-- 备注 -->
      <a-form-item :label="$t('sponsor.column.comment')">
        <a-input v-model="formData.comment" placeholder="备注说明" />
      </a-form-item>

      <!-- 奖励列表 -->
      <a-divider />
      <div
        style="
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
        "
      >
        <span style="font-weight: 600">{{ $t('sponsor.title.rewards') }}</span>
        <a-button type="primary" size="mini" @click="addReward"
          >+ 添加奖励</a-button
        >
      </div>

      <div
        v-for="(item, index) in formData.rewards"
        :key="index"
        style="margin-bottom: 8px"
      >
        <a-row :gutter="8" align="center">
          <a-col :span="6">
            <a-select
              v-model="item.type"
              style="width: 100%"
              placeholder="类型"
            >
              <a-option value="item">道具</a-option>
              <a-option value="nx">点券</a-option>
              <a-option value="meso">金币</a-option>
            </a-select>
          </a-col>
          <a-col :span="6">
            <a-input-number
              v-if="item.type === 'item'"
              v-model="item.id"
              :min="0"
              placeholder="物品ID"
              style="width: 100%"
            />
            <span v-else style="line-height: 28px; color: #999">—</span>
          </a-col>
          <a-col :span="6">
            <a-input-number
              v-model="item.qty"
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
              @click="removeReward(index)"
              >✕ 删除</a-button
            >
          </a-col>
        </a-row>
      </div>
      <div
        v-if="!formData.rewards || formData.rewards.length === 0"
        style="color: #999; font-size: 12px"
      >
        暂无奖励，点击上方按钮添加
      </div>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import {
    saveSponsorConfig,
    type SponsorConfigForm,
    type SponsorRewardItem,
  } from '@/api/sponsor';

  const emit = defineEmits(['loadData']);

  const visible = ref(false);
  const editing = ref(false);
  const submitLoading = ref(false);
  const formRef = ref();
  const editingId = ref<number | undefined>();

  const formData = ref<SponsorConfigForm>({
    name: '',
    amount: undefined,
    rewards: [],
    enabled: 1,
    comment: '',
  });

  const initForm = (record: SponsorConfigForm | null) => {
    if (record) {
      editing.value = true;
      editingId.value = record.id;
      formData.value = {
        id: record.id,
        name: record.name || '',
        amount: record.amount,
        rewards: record.rewards ? [...record.rewards] : [],
        enabled: record.enabled ?? 1,
        comment: record.comment || '',
      };
    } else {
      editing.value = false;
      editingId.value = undefined;
      formData.value = {
        name: '',
        amount: undefined,
        rewards: [],
        enabled: 1,
        comment: '',
      };
    }
    visible.value = true;
  };

  const addReward = () => {
    if (!formData.value.rewards) formData.value.rewards = [];
    formData.value.rewards.push({ type: 'item', id: undefined, qty: 1 });
  };

  const removeReward = (index: number) => {
    formData.value.rewards?.splice(index, 1);
  };

  const handleSubmit = async (): Promise<boolean> => {
    submitLoading.value = true;
    try {
      // 清理空奖励
      if (formData.value.rewards) {
        formData.value.rewards = formData.value.rewards.filter(
          (r: SponsorRewardItem) =>
            r.qty && r.qty > 0 && (r.type !== 'item' || (r.id && r.id > 0))
        );
      }
      await saveSponsorConfig(formData.value);
      Message.success('赞助配置保存成功');
      visible.value = false;
      emit('loadData');
      return true;
    } catch {
      return false;
    } finally {
      submitLoading.value = false;
    }
  };

  const handleCancel = () => {
    visible.value = false;
  };

  defineExpose({ initForm });
</script>

<script lang="ts">
  export default { name: 'SponsorForm' };
</script>
