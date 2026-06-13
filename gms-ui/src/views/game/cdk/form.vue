<template>
  <a-modal
    v-model:visible="visible"
    :title="editing ? '编辑CDK' : '创建CDK'"
    :ok-loading="submitLoading"
    @before-ok="handleSubmit"
    @cancel="handleCancel"
    :width="640"
  >
    <a-form ref="formRef" :model="formData" layout="vertical">
      <!-- 兑换码（创建时必填，编辑时只读） -->
      <a-form-item
        v-if="!editing"
        field="code"
        :label="$t('cdk.column.code')"
        :rules="[{ required: true, message: $t('cdk.validate.emptyCode') }]"
      >
        <a-input
          v-model="formData.code"
          placeholder="输入兑换码"
          :max-length="64"
        />
      </a-form-item>

      <!-- 奖励配置 -->
      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item :label="$t('cdk.column.nxCredit')">
            <a-input-number
              v-model="formData.nxCredit"
              :min="0"
              placeholder="0"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item :label="$t('cdk.column.nxPrepaid')">
            <a-input-number
              v-model="formData.nxPrepaid"
              :min="0"
              placeholder="0"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item :label="$t('cdk.column.meso')">
            <a-input-number
              v-model="formData.meso"
              :min="0"
              placeholder="0"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="8">
          <a-form-item :label="$t('cdk.column.maxUseCount')">
            <a-input-number
              v-model="formData.maxUseCount"
              :min="1"
              placeholder="1"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item :label="$t('cdk.column.type')">
            <a-select v-model="formData.type" style="width: 100%">
              <a-option :value="1">{{ $t('cdk.column.type.1') }}</a-option>
              <a-option :value="2">{{ $t('cdk.column.type.2') }}</a-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item :label="$t('cdk.column.enabled')">
            <a-switch
              v-model="formData.enabled"
              :checked-value="1"
              :unchecked-value="0"
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
            <a-input v-model="formData.comment" placeholder="备注说明" />
          </a-form-item>
        </a-col>
      </a-row>

      <!-- 道具奖励列表 -->
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
        <a-button type="primary" size="mini" @click="addItem">
          + {{ $t('cdk.button.addItem') }}
        </a-button>
      </div>

      <div
        v-for="(item, index) in formData.items"
        :key="index"
        style="margin-bottom: 8px"
      >
        <a-row :gutter="8" align="center">
          <a-col :span="8">
            <a-input-number
              v-model="item.itemId"
              :min="0"
              placeholder="物品ID"
              style="width: 100%"
            />
          </a-col>
          <a-col :span="10">
            <a-input
              :model-value="item.itemName || ''"
              placeholder="物品名称（自动）"
              disabled
              style="width: 100%"
            />
          </a-col>
          <a-col :span="4">
            <a-input-number
              v-model="item.quantity"
              :min="1"
              placeholder="数量"
              style="width: 100%"
            />
          </a-col>
          <a-col :span="2">
            <a-button
              type="text"
              size="mini"
              status="danger"
              @click="removeItem(index)"
            >
              ✕
            </a-button>
          </a-col>
        </a-row>
      </div>
      <div
        v-if="!formData.items || formData.items.length === 0"
        style="color: #999; font-size: 12px"
      >
        暂无道具奖励，点击上方按钮添加
      </div>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import {
    saveCdkConfig,
    type CdkConfigForm,
    type CdkItemForm,
  } from '@/api/cdk';

  const emit = defineEmits(['loadData']);

  const visible = ref(false);
  const editing = ref(false);
  const submitLoading = ref(false);
  const formRef = ref();
  const editingId = ref<number | undefined>();

  // 表单数据
  const formData = ref<CdkConfigForm>({
    code: '',
    type: 1,
    nxCredit: 0,
    nxPrepaid: 0,
    meso: 0,
    sponsor: 0,
    maxUseCount: 1,
    expireTime: '',
    enabled: 1,
    comment: '',
    items: [],
  });

  /** 初始化表单（外部调用） */
  const initForm = (record: CdkConfigForm | null) => {
    if (record) {
      editing.value = true;
      editingId.value = record.id;
      formData.value = {
        id: record.id,
        code: record.code,
        batchNo: record.batchNo,
        type: record.type ?? 1,
        nxCredit: record.nxCredit ?? 0,
        nxPrepaid: record.nxPrepaid ?? 0,
        meso: record.meso ?? 0,
        sponsor: record.sponsor ?? 0,
        maxUseCount: record.maxUseCount ?? 1,
        expireTime: record.expireTime || '',
        enabled: record.enabled ?? 1,
        comment: record.comment || '',
        items: record.items ? [...record.items] : [],
      };
    } else {
      editing.value = false;
      editingId.value = undefined;
      formData.value = {
        code: '',
        type: 1,
        nxCredit: 0,
        nxPrepaid: 0,
        meso: 0,
        sponsor: 0,
        maxUseCount: 1,
        expireTime: '',
        enabled: 1,
        comment: '',
        items: [],
      };
    }
    visible.value = true;
  };

  /** 添加道具行 */
  const addItem = () => {
    if (!formData.value.items) formData.value.items = [];
    formData.value.items.push({ itemId: undefined, quantity: 1 });
  };

  /** 删除道具行 */
  const removeItem = (index: number) => {
    formData.value.items?.splice(index, 1);
  };

  /** 提交保存 */
  const handleSubmit = async (): Promise<boolean> => {
    submitLoading.value = true;
    try {
      // 清理空道具
      if (formData.value.items) {
        formData.value.items = formData.value.items.filter(
          (i: CdkItemForm) => i.itemId && i.itemId > 0
        );
      }
      await saveCdkConfig(formData.value);
      Message.success('CDK保存成功');
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
  export default { name: 'CdkForm' };
</script>
