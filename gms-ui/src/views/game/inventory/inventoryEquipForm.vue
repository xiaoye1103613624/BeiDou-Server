<template>
  <a-modal
    v-model:visible="visible"
    :width="560"
    :ok-loading="loading"
    :on-before-ok="handleBeforeOk"
    unmount-on-close
    @cancel="handleCancel"
  >
    <template #title>{{ $t('inventoryEquip.form.title') }}</template>
    <div class="bd-overlay-form">
      <a-form :model="formData" auto-label-width>
        <a-form-item :label="$t('inventoryEquip.form.equipId')">
          {{ formData.inventoryEquipment.id }}
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.itemTableId')">
          {{ formData.inventoryEquipment.inventoryItemId }}
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.upgradeSlots')">
          <a-input-number v-model="formData.inventoryEquipment.upgradeSlots" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.level')">
          <a-input-number v-model="formData.inventoryEquipment.level" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.str')">
          <a-input-number v-model="formData.inventoryEquipment.attStr" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.dex')">
          <a-input-number v-model="formData.inventoryEquipment.attDex" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.int')">
          <a-input-number v-model="formData.inventoryEquipment.attInt" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.luk')">
          <a-input-number v-model="formData.inventoryEquipment.attLuk" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.hp')">
          <a-input-number v-model="formData.inventoryEquipment.hp" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.mp')">
          <a-input-number v-model="formData.inventoryEquipment.mp" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.patk')">
          <a-input-number v-model="formData.inventoryEquipment.patk" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.matk')">
          <a-input-number v-model="formData.inventoryEquipment.matk" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.pdef')">
          <a-input-number v-model="formData.inventoryEquipment.pdef" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.mdef')">
          <a-input-number v-model="formData.inventoryEquipment.mdef" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.acc')">
          <a-input-number v-model="formData.inventoryEquipment.acc" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.avoid')">
          <a-input-number v-model="formData.inventoryEquipment.avoid" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.hands')">
          <a-input-number v-model="formData.inventoryEquipment.hands" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.speed')">
          <a-input-number v-model="formData.inventoryEquipment.speed" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.jump')">
          <a-input-number v-model="formData.inventoryEquipment.jump" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.locked')">
          <a-input-number v-model="formData.inventoryEquipment.locked" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.vicious')">
          <a-input-number v-model="formData.inventoryEquipment.vicious" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.itemLevel')">
          <a-input-number v-model="formData.inventoryEquipment.itemLevel" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.itemExp')">
          <a-input-number v-model="formData.inventoryEquipment.itemExp" />
        </a-form-item>
        <a-form-item :label="$t('inventoryEquip.form.ringId')">
          <a-input-number v-model="formData.inventoryEquipment.ringId" />
        </a-form-item>
      </a-form>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import { Message } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import { InventoryState } from '@/store/modules/inventory/type';
  import { updateInventory } from '@/api/inventory';

  const { t } = useI18n();
  const { setLoading, loading } = useLoading(false);
  const visible = ref<boolean>(false);
  const formData = ref<InventoryState>({
    id: undefined,
    characterId: undefined,
    itemId: undefined,
    itemType: undefined,
    inventoryType: undefined,
    position: undefined,
    quantity: undefined,
    owner: undefined,
    petId: undefined,
    flag: undefined,
    expiration: undefined,
    giftFrom: undefined,
    online: undefined,
    equipment: undefined,
    inventoryEquipment: {
      id: -1,
      inventoryItemId: 0,
      upgradeSlots: 0,
      level: 0,
      attStr: 0,
      attDex: 0,
      attInt: 0,
      attLuk: 0,
      hp: 0,
      mp: 0,
      patk: 0,
      matk: 0,
      pdef: 0,
      mdef: 0,
      acc: 0,
      avoid: 0,
      hands: 0,
      speed: 0,
      jump: 0,
      locked: 0,
      vicious: 0,
      itemLevel: 0,
      itemExp: 0,
      ringId: 0,
    },
  });

  const emit = defineEmits(['loadData']);
  const handleBeforeOk = async () => {
    setLoading(true);
    try {
      await updateInventory(formData.value);
      visible.value = false;
      Message.success(t('inventoryEquip.msg.updateSuccess'));
      emit('loadData');
    } finally {
      setLoading(false);
    }
  };
  const handleCancel = () => {
    visible.value = false;
  };

  const initForm = (data: InventoryState) => {
    formData.value = data;
    visible.value = true;
  };
  defineExpose({ initForm });
</script>

<script lang="ts">
  export default {
    name: 'InventoryEquipForm',
  };
</script>
