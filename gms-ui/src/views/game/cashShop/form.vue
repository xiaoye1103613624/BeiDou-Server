<template>
  <a-modal
    v-model:visible="visible"
    :width="560"
    :ok-loading="loading"
    :on-before-ok="handleBeforeOk"
    unmount-on-close
    @cancel="handleCancel"
  >
    <template #title>{{ $t('cashShop.form.title') }}</template>
    <div class="bd-overlay-form">
      <a-form :model="formData" auto-label-width>
        <a-form-item label="sn">
          {{ formData.sn }}
        </a-form-item>
        <a-form-item :label="$t('cashShop.form.item')">
          <a-space>
            {{ formData.itemId }}
            <ItemIcon :id="formData.itemId" category="item" :size="32" />
          </a-space>
        </a-form-item>
        <a-form-item :label="$t('cashShop.form.count')">
          <a-input-number v-model="formData.count" />
          <template v-if="tempData.defaultCount" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultCount,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item :label="$t('cashShop.form.price')">
          <a-input-number v-model="formData.price" />
          <template v-if="tempData.defaultPrice" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultPrice,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item :label="$t('cashShop.form.priority')">
          <a-input-number v-model="formData.priority" />
          <template v-if="tempData.defaultPriority" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultPriority,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item :label="$t('cashShop.form.period')">
          <a-input-number v-model="formData.period" />
          <template v-if="tempData.defaultPeriod" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultPeriod,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item :label="$t('cashShop.form.status')">
          <a-switch
            v-model="formData.onSale"
            type="round"
            :checked-value="1"
            :unchecked-value="0"
          >
            <template #checked>
              {{ $t('cashShop.filter.onSale') }}
            </template>
            <template #unchecked>
              {{ $t('cashShop.filter.offSale') }}
            </template>
          </a-switch>
          <template #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultOnSale
                  ? $t('cashShop.filter.onSale')
                  : $t('cashShop.filter.offSale'),
              })
            }}
          </template>
        </a-form-item>
        <a-form-item label="Bonus">
          <a-input-number v-model="formData.bonus" />
          <template v-if="tempData.defaultBonus" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultBonus,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item :label="$t('cashShop.form.maplePoint')">
          <a-input-number v-model="formData.maplePoint" />
          <template v-if="tempData.defaultMaplePoint" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultMaplePoint,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item :label="$t('cashShop.form.meso')">
          <a-input-number v-model="formData.meso" />
          <template v-if="tempData.defaultMeso" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultMeso,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item label="PremiumUser">
          <a-input-number v-model="formData.forPremiumUser" />
          <template v-if="tempData.defaultForPremiumUser" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultForPremiumUser,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item :label="$t('cashShop.form.gender')">
          <a-select v-model="formData.commodityGender">
            <a-option :value="0">
              {{ $t('cashShop.gender.male') }}
            </a-option>
            <a-option :value="1">
              {{ $t('cashShop.gender.female') }}
            </a-option>
            <a-option :value="2">
              {{ $t('cashShop.gender.both') }}
            </a-option>
          </a-select>
          <template #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: genderDefaultLabel,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item :label="$t('cashShop.form.clz')">
          <a-select v-model="formData.clz" allow-clear>
            <a-option :value="0">NEW</a-option>
            <a-option :value="1">SALE</a-option>
            <a-option :value="2">HOT</a-option>
            <a-option :value="3">EVENT</a-option>
          </a-select>
          <template v-if="tempData.defaultClz" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: clzDefaultLabel,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item label="Limit">
          <a-input-number v-model="formData.limit" />
          <template v-if="tempData.defaultLimit" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultLimit,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item label="pbCash">
          <a-input-number v-model="formData.pbCash" />
          <template v-if="tempData.defaultPBCash" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultPBCash,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item label="pbPoint">
          <a-input-number v-model="formData.pbPoint" />
          <template v-if="tempData.defaultPBPoint" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultPBPoint,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item label="pbGift">
          <a-input-number v-model="formData.pbGift" />
          <template v-if="tempData.defaultPBGift" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultPBGift,
              })
            }}
          </template>
        </a-form-item>
        <a-form-item label="packageSn">
          <a-input-number v-model="formData.packageSn" />
          <template v-if="tempData.defaultPackageSn" #extra>
            {{
              $t('cashShop.form.wzDefault', {
                value: tempData.defaultPackageSn,
              })
            }}
          </template>
        </a-form-item>
      </a-form>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { cashShopState } from '@/store/modules/cashShop/type';
  import { cashShopFormState, offSale, onSale } from '@/api/cashShop';
  import useLoading from '@/hooks/loading';
  import { Message } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';

  const { t } = useI18n();
  const { setLoading, loading } = useLoading(false);
  const visible = ref<boolean>(false);
  const formData = ref<cashShopFormState>({ sn: -1, itemId: -1 });
  const tempData = ref<cashShopState>({ sn: -1, itemId: -1 });

  const genderDefaultLabel = computed(() => {
    if (tempData.value.defaultGender === 0) {
      return t('cashShop.gender.male');
    }
    if (tempData.value.defaultGender === 1) {
      return t('cashShop.gender.female');
    }
    if (tempData.value.defaultGender === 2) {
      return t('cashShop.gender.both');
    }
    return '';
  });

  const clzDefaultLabel = computed(() => {
    if (tempData.value.defaultClz === 0) return 'NEW';
    if (tempData.value.defaultClz === 1) return 'SALE';
    if (tempData.value.defaultClz === 2) return 'HOT';
    if (tempData.value.defaultClz === 3) return 'EVENT';
    return '';
  });

  const emit = defineEmits(['loadData']);
  const handleBeforeOk = async () => {
    setLoading(true);
    try {
      if (formData.value.onSale) await onSale(formData.value);
      else await offSale(formData.value);
      visible.value = false;
      Message.success(t('cashShop.msg.updateSuccess'));
      emit('loadData');
    } finally {
      setLoading(false);
    }
  };
  const handleCancel = () => {
    visible.value = false;
  };

  const initForm = (data: cashShopState) => {
    tempData.value = data;
    formData.value = {
      sn: data.sn,
      itemId: data.itemId,
      count: data.count,
      price: data.price,
      bonus: data.bonus,
      priority: data.priority,
      period: data.period,
      maplePoint: data.maplePoint,
      meso: data.meso,
      forPremiumUser: data.forPremiumUser,
      commodityGender: data.gender,
      onSale: data.onSale ? 1 : 0,
      clz: data.clz,
      limit: data.limit,
      pbCash: data.pbCash,
      pbPoint: data.pbPoint,
      pbGift: data.pbGift,
      packageSn: data.packageSn,
    };
    visible.value = true;
  };
  defineExpose({ initForm });
</script>

<script lang="ts">
  export default {
    name: 'CashShopForm',
  };
</script>

<style lang="less" scoped>
  .bd-overlay-form {
    :deep(.arco-form-item-label-col) {
      padding-right: 12px;
    }

    img {
      width: 32px;
      height: 32px;
      object-fit: contain;
      border-radius: var(--bd-radius-sm);
      background: var(--bd-surface-muted);
    }
  }
</style>
