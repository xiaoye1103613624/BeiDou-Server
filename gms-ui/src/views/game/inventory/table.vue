<template>
  <!-- 物品库存表格 -->
  <a-table
    row-key="id"
    :loading="loading"
    :data="tableData"
    column-resizable
    :pagination="false"
    :bordered="{ cell: true }"
  >
    <template #columns>
      <!-- 物品ID列 -->
      <a-table-column
        :title="$t('inventoryList.column.id')"
        data-index="id"
        align="center"
        :width="100"
      />
      <!-- 注释掉的角色ID列和在线状态列 -->
      <!--      <a-table-column-->
      <!--        :title="$t('inventoryList.column.characterId')">-->
      <!--        data-index="characterId"-->
      <!--        align="center"-->
      <!--      />-->
      <!--      <a-table-column-->
      <!--        :title="$t('inventoryList.column.online')"-->
      <!--        data-index="online"-->
      <!--        align="center"-->
      <!--      >-->
      <!--        <template #cell="{ record }">-->
      <!--          <a-tag v-if="record.online" color="green">{{-->
      <!--            $t('inventoryList.column.online')-->
      <!--          }}</a-tag>-->
      <!--          <a-tag v-else color="gray">{{-->
      <!--            $t('inventoryList.column.offline')-->
      <!--          }}</a-tag>-->
      <!--        </template>-->
      <!--      </a-table-column>-->
      <!-- 物品ID列 -->
      <a-table-column
        :title="$t('inventoryList.column.itemId')"
        data-index="itemId"
        align="center"
        :width="130"
      />
      <!-- 物品名称列（带图片预览） -->
      <a-table-column :title="$t('inventoryList.column.item')" align="center">
        <template #cell="{ record }">
          <!-- 物品图片气泡弹窗，鼠标悬停显示物品名称 -->
          <a-popover placement="top">
            <template #content>
              <!-- 显示物品名称，如果是特殊物品则显示中文名称 -->
              <span>{{
                record.itemId === 2430033 ? '北斗卫星指导书' : record.itemName
              }}</span>
            </template>
            <!-- 根据物品ID显示对应的物品图片 -->
            <img
              v-if="record.itemId === 2430033"
              :src="beidouBook"
              alt="北斗卫星指导书"
            />
            <img v-else :src="getIconUrl('item', record.itemId)" />
          </a-popover>
        </template>
      </a-table-column>
      <!-- 物品类型列 -->
      <a-table-column
        :title="$t('inventoryList.column.itemType')"
        data-index="itemType"
        align="center"
      />
      <!-- 物品位置列 -->
      <a-table-column
        :title="$t('inventoryList.column.position')"
        data-index="position"
        align="center"
      />
      <!-- 物品数量列（可编辑） -->
      <a-table-column
        :title="$t('inventoryList.column.quantity')"
        align="center"
        :width="160"
      >
        <template #cell="{ record }">
          <!-- 非编辑状态下显示数量 -->
          <span v-if="editId !== record.id">
            {{ record.quantity }}
          </span>
          <!-- 编辑状态下显示数字输入框 -->
          <a-input-number v-else v-model="record.quantity" />
        </template>
      </a-table-column>
      <!-- 物品拥有者列 -->
      <a-table-column
        :title="$t('inventoryList.column.owner')"
        data-index="owner"
        align="center"
      />
      <!-- 宠物ID列 -->
      <a-table-column
        :title="$t('inventoryList.column.petId')"
        data-index="petId"
        align="center"
      />
      <!-- 物品标识列 -->
      <a-table-column
        :title="$t('inventoryList.column.flag')"
        data-index="flag"
        align="center"
      />
      <!-- 礼物来源列 -->
      <a-table-column
        :title="$t('inventoryList.column.giftFrom')"
        data-index="giftFrom"
        align="center"
      />
      <!-- 过期时间列（可编辑） -->
      <a-table-column
        :title="$t('inventoryList.column.expiration')"
        align="center"
      >
        <template #cell="{ record }">
          <!-- 非编辑状态下显示格式化的过期时间 -->
          <span v-if="editId !== record.id">
            {{ timestampToChineseTime(record.expiration) }}
          </span>
          <!-- 编辑状态下显示时间戳输入框 -->
          <a-input-number v-else v-model="record.expiration" />
        </template>
      </a-table-column>
      <!-- 操作列 -->
      <a-table-column
        :title="$t('inventoryList.column.operation')"
        :width="200"
      >
        <template #cell="{ record }">
          <!-- 编辑按钮（非编辑状态时显示） -->
          <a-button
            v-if="editId !== record.id"
            type="text"
            size="mini"
            @click="editClick(record)"
          >
            {{ $t('inventoryList.button.edit') }}
          </a-button>
          <!-- 保存按钮（编辑状态时显示） -->
          <a-button
            v-if="editId === record.id"
            type="text"
            size="mini"
            status="success"
            @click="saveClick(record)"
          >
            {{ $t('inventoryList.button.save') }}
          </a-button>
          <!-- 取消按钮（编辑状态时显示） -->
          <a-button
            v-if="editId === record.id"
            type="text"
            size="mini"
            @click="editId = undefined"
          >
            {{ $t('inventoryList.button.cancel') }}
          </a-button>
          <!-- 删除确认弹窗（非编辑状态时显示） -->
          <a-popconfirm
            v-if="editId !== record.id"
            type="error"
            :content="$t('inventoryList.confirm.delete')"
            @ok="deleteClick(record)"
          >
            <a-button type="text" size="mini" status="danger">
              {{ $t('inventoryList.button.delete') }}
            </a-button>
          </a-popconfirm>
        </template>
      </a-table-column>
    </template>
  </a-table>
  <!-- 装备表单组件（用于编辑装备属性） -->
  <inventory-equip-form ref="inventoryEquipFormRef" @load-data="loadData" />
</template>

<script lang="ts" setup>
  // 导入Vue响应式引用函数
  import { ref } from 'vue';
  // 导入物品管理相关API
  import {
    deleteInventory,
    getInventoryList,
    InventoryCondition,
    updateInventory,
  } from '@/api/inventory';
  // 导入加载状态钩子
  import useLoading from '@/hooks/loading';
  // 导入物品状态类型定义
  import { InventoryState } from '@/store/modules/inventory/type';
  // 导入枫story API工具函数（用于获取物品图标）
  import { getIconUrl } from '@/utils/mapleStoryAPI';
  // 导入装备表单组件
  import InventoryEquipForm from '@/views/game/inventory/inventoryEquipForm.vue';
  // 导入字符串工具函数（用于时间戳转换）
  import { timestampToChineseTime } from '@/utils/stringUtils';
  // 导入北斗卫星指导书图片资源
  import beidouBook from '@/assets/2430033.png';

  // 创建加载状态和设置函数
  const { setLoading, loading } = useLoading(false);
  // 物品数据表格
  const tableData = ref<InventoryState[]>([]);

  // 定义组件属性
  const props = defineProps<{
    // 当前物品类型
    currentType: string | number;
    // 角色ID
    characterId: number | undefined;
  }>();
  // 当前正在编辑的物品ID
  const editId = ref<number | undefined>(undefined);

  /**
   * 加载物品列表数据
   * 根据当前类型和角色ID获取物品数据
   */
  const loadData = async () => {
    // 重置编辑ID
    editId.value = undefined;
    // 如果没有传入角色ID则直接返回
    if (!props || !props.characterId) {
      return;
    }
    // 设置加载状态
    setLoading(true);
    try {
      // 构建查询条件
      const condition: InventoryCondition = {
        // 物品类型
        inventoryType: props.currentType as number,
        // 角色ID
        characterId: props.characterId as number,
        // 页码
        pageNo: 1,
        // 每页大小
        pageSize: 20,
      };
      // 获取物品列表数据
      const { data } = await getInventoryList(condition);
      // 更新表格数据
      tableData.value = data;
    } finally {
      // 取消加载状态
      setLoading(false);
    }
  };
  // 页面加载时自动调用
  loadData();

  /**
   * 保存物品修改
   * 更新物品信息并重新加载数据
   * @param data - 修改后的物品数据
   */
  const saveClick = async (data: InventoryState) => {
    setLoading(true);
    try {
      // 更新物品信息
      await updateInventory(data);
      // 重新加载数据
      await loadData();
    } finally {
      // 取消加载状态
      setLoading(false);
    }
  };

  /**
   * 删除物品
   * 从数据库中移除指定物品并重新加载数据
   * @param data - 要删除的物品数据
   */
  const deleteClick = async (data: InventoryState) => {
    setLoading(true);
    try {
      // 删除物品
      await deleteInventory(data);
      // 重新加载数据
      await loadData();
    } finally {
      // 取消加载状态
      setLoading(false);
    }
  };

  // 装备表单组件引用
  const inventoryEquipFormRef = ref();
  
  /**
   * 处理编辑按钮点击事件
   * 根据物品类型决定是打开装备编辑表单还是普通编辑模式
   * @param data - 要编辑的物品数据
   */
  const editClick = (data: InventoryState) => {
    // 如果是装备类型，则打开装备编辑表单
    if (data.equipment) {
      inventoryEquipFormRef.value.initForm(data);
    } else {
      // 否则进入普通编辑模式
      editId.value = data.id;
    }
  };
</script>

<script lang="ts">
  export default {
    name: 'InventoryList',
  };
</script>

<style lang="less" scoped></style>