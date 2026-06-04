<!-- 装备增强配置管理页面 -->
<template>
  <!-- 页面主容器 -->
  <div class="container">
    <!-- 面包屑导航 -->
    <Breadcrumb />
    <!-- 主卡片容器 -->
    <a-card
      class="general-card"
      :title="$t('menu.game.equipEnhance')"
      style="overflow-x: auto"
    >
      <!-- 操作按钮区域 -->
      <a-row>
        <a-col>
          <a-space>
            <!-- 刷新数据按钮 -->
            <a-button type="primary" @click="loadData">
              {{ $t('button.search') }}
            </a-button>
            <!-- 新增配置按钮 -->
            <a-button type="primary" status="success" @click="insertClick">
              {{ $t('button.create') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <!-- 装备增强配置列表表格 -->
      <a-table
        row-key="id"
        :loading="loading"
        :data="tableData"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
        style="margin-top: 16px"
      >
        <template #columns>
          <!-- ID列 -->
          <a-table-column
            title="ID"
            data-index="id"
            :width="55"
            align="center"
          />
          <!-- 物品图标列 -->
          <a-table-column
            :title="$t('equipEnhance.list.column.icon')"
            :width="70"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 物品图标，带悬停放大效果 -->
              <a-popover>
                <img
                  :src="getIconUrl('item', record.itemId)"
                  alt=""
                  style="width: 40px; height: 40px"
                  @error="onImgError($event)"
                />
                <template #content>
                  <img :src="getIconUrl('item', record.itemId)" alt="" />
                </template>
              </a-popover>
            </template>
          </a-table-column>
          <!-- 物品ID列 -->
          <a-table-column
            :title="$t('equipEnhance.list.column.itemId')"
            data-index="itemId"
            :width="90"
            align="center"
          />
          <!-- 物品名称列 -->
          <a-table-column
            :title="$t('equipEnhance.list.column.itemName')"
            data-index="itemName"
            :width="130"
            align="center"
          />
          <!-- 最大增强等级列 -->
          <a-table-column
            :title="$t('equipEnhance.list.column.maxEnhance')"
            data-index="maxEnhance"
            :width="80"
            align="center"
          />
          <!-- 每角色唯一标识列 -->
          <a-table-column
            :title="$t('equipEnhance.list.column.uniquePerChar')"
            :width="80"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 根据uniquePerChar值显示不同颜色的标签 -->
              <a-tag
                v-if="record.uniquePerChar === 1"
                color="orange"
                size="small"
              >
                {{ $t('equipEnhance.unique.true') }}
              </a-tag>
              <a-tag v-else color="gray" size="small">
                {{ $t('equipEnhance.unique.false') }}
              </a-tag>
            </template>
          </a-table-column>
          <!-- 启用状态列 -->
          <a-table-column
            :title="$t('equipEnhance.list.column.enabled')"
            :width="60"
            align="center"
          >
            <template #cell="{ record }">
              <!-- 根据enabled值显示不同颜色的标签 -->
              <a-tag v-if="record.enabled === 1" color="green" size="small">
                {{ $t('equipEnhance.enabled.true') }}
              </a-tag>
              <a-tag v-else color="red" size="small">
                {{ $t('equipEnhance.enabled.false') }}
              </a-tag>
            </template> </a-table-column
          ><!-- 操作列 -->
          <a-table-column
            :title="$t('equipEnhance.list.column.operations')"
            :width="150"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <!-- 操作按钮组 -->
              <a-space>
                <!-- 编辑按钮 -->
                <a-button size="mini" type="text" @click="editClick(record)">
                  {{ $t('button.edit') }}
                </a-button>
                <!-- 删除确认弹窗 -->
                <a-popconfirm
                  type="error"
                  :content="$t('equipEnhance.message.deleteTips')"
                  position="left"
                  @ok="deleteClick(record)"
                >
                  <!-- 删除按钮 -->
                  <a-button size="mini" status="danger" type="text">
                    {{ $t('button.delete') }}
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 编辑抽屉：用于新增或编辑装备增强配置 -->
    <a-drawer
      v-model:visible="drawerVisible"
      :title="
        editId
          ? $t('equipEnhance.form.title.update')
          : $t('equipEnhance.form.title.create')
      "
      :width="780"
      @cancel="drawerVisible = false"
    >
      <!-- 表单：编辑装备增强配置 -->
      <a-form ref="formRef" :model="editConfig" layout="vertical">
        <!-- 基本信息部分 -->
        <a-row :gutter="16">
          <a-col :span="12">
            <!-- 物品ID输入框 -->
            <a-form-item :label="$t('equipEnhance.form.field.itemId')">
              <a-input-number
                v-model="editConfig.itemId"
                :min="1"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <!-- 物品名称输入框 -->
            <a-form-item :label="$t('equipEnhance.form.field.itemName')">
              <a-input v-model="editConfig.itemName" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <!-- 最大增强等级输入框 -->
            <a-form-item :label="$t('equipEnhance.form.field.maxEnhance')">
              <a-input-number
                v-model="editConfig.maxEnhance"
                :min="1"
                :max="30"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <!-- 每角色唯一开关 -->
            <a-form-item :label="$t('equipEnhance.form.field.uniquePerChar')">
              <a-switch
                :model-value="editConfig.uniquePerChar === 1"
                @change="(v: boolean) => (editConfig.uniquePerChar = v ? 1 : 0)"
              />
              <!-- 帮助提示 -->
              <a-tooltip
                :content="$t('equipEnhance.form.field.uniquePerCharHint')"
                style="margin-left: 8px"
              >
                <icon-question-circle-fill style="color: #999; cursor: help" />
              </a-tooltip>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <!-- 启用状态开关 -->
            <a-form-item :label="$t('equipEnhance.form.field.enabled')">
              <a-switch
                :model-value="editConfig.enabled === 1"
                @change="(v: boolean) => (editConfig.enabled = v ? 1 : 0)"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 分割线：等级配置区域 -->
        <a-divider>{{ $t('equipEnhance.form.field.levels') }}</a-divider>

        <!-- 逐级配置：每个增强等级的具体参数 -->
        <div
          v-for="(lv, idx) in editConfig.levels"
          :key="idx"
          class="level-card"
        >
          <!-- 等级头部：包含等级标题和删除按钮 -->
          <div class="level-header">
            <!-- 显示当前等级编号 -->
            <span class="level-title">Lv.{{ lv.enhanceLevel || idx + 1 }}</span>
            <!-- 删除当前等级按钮 -->
            <a-button
              size="mini"
              status="danger"
              type="text"
              @click="removeLevel(idx)"
            >
              {{ $t('equipEnhance.form.removeLevel') }}
            </a-button>
          </div>

          <!-- 核心参数：成功率、消耗金币、失败销毁 -->
          <a-row :gutter="12">
            <a-col :span="8">
              <!-- 成功率输入框 -->
              <a-form-item :label="$t('equipEnhance.form.field.successRate')">
                <a-input-number
                  v-model="lv.successRate"
                  :min="0"
                  :max="100"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <!-- 消耗金币输入框 -->
              <a-form-item :label="$t('equipEnhance.form.field.mesoCost')">
                <a-input-number
                  v-model="lv.mesoCost"
                  :min="0"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <!-- 失败销毁复选框 -->
              <a-form-item hide-label>
                <a-checkbox
                  :model-value="!!lv.destroyOnFail"
                  style="margin-top: 30px"
                  @change="(v: boolean) => (lv.destroyOnFail = v ? 1 : 0)"
                >
                  {{ $t('equipEnhance.form.field.destroyOnFail') }}
                </a-checkbox>
              </a-form-item>
            </a-col>
          </a-row>

          <!-- 属性加成：主要属性 -->
          <div class="stat-section">
            <span class="stat-group-label">{{
              $t('equipEnhance.form.field.statMain')
            }}</span>
            <a-space size="mini" wrap>
              <!-- 力量增加值输入框 -->
              <a-input-number
                v-model="lv.strAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.strAdd')"
                style="width: 78px"
                size="mini"
              />
              <!-- 敏捷增加值输入框 -->
              <a-input-number
                v-model="lv.dexAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.dexAdd')"
                style="width: 78px"
                size="mini"
              />
              <!-- 智力增加值输入框 -->
              <a-input-number
                v-model="lv.intAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.intAdd')"
                style="width: 78px"
                size="mini"
              />
              <!-- 运气增加值输入框 -->
              <a-input-number
                v-model="lv.lukAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.lukAdd')"
                style="width: 78px"
                size="mini"
              />
            </a-space>
          </div>
          <!-- 属性加成：攻击属性 -->
          <div class="stat-section">
            <span class="stat-group-label">{{
              $t('equipEnhance.form.field.statAttack')
            }}</span>
            <a-space size="mini" wrap>
              <!-- 物理攻击力增加值输入框 -->
              <a-input-number
                v-model="lv.watkAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.watkAdd')"
                style="width: 78px"
                size="mini"
              />
              <!-- 魔法攻击力增加值输入框 -->
              <a-input-number
                v-model="lv.matkAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.matkAdd')"
                style="width: 78px"
                size="mini"
              />
            </a-space>
          </div>
          <!-- 属性加成：防御属性 -->
          <div class="stat-section">
            <span class="stat-group-label">{{
              $t('equipEnhance.form.field.statDefense')
            }}</span>
            <a-space size="mini" wrap>
              <!-- 物理防御力增加值输入框 -->
              <a-input-number
                v-model="lv.wdefAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.wdefAdd')"
                style="width: 78px"
                size="mini"
              />
              <!-- 魔法防御力增加值输入框 -->
              <a-input-number
                v-model="lv.mdefAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.mdefAdd')"
                style="width: 78px"
                size="mini"
              />
            </a-space>
          </div>
          <!-- 属性加成：其他属性 -->
          <div class="stat-section">
            <span class="stat-group-label">{{
              $t('equipEnhance.form.field.statOther')
            }}</span>
            <a-space size="mini" wrap>
              <!-- 生命值增加值输入框 -->
              <a-input-number
                v-model="lv.hpAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.hpAdd')"
                style="width: 78px"
                size="mini"
              />
              <!-- 魔法值增加值输入框 -->
              <a-input-number
                v-model="lv.mpAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.mpAdd')"
                style="width: 78px"
                size="mini"
              />
              <!-- 命中率增加值输入框 -->
              <a-input-number
                v-model="lv.accAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.accAdd')"
                style="width: 78px"
                size="mini"
              />
              <!-- 回避率增加值输入框 -->
              <a-input-number
                v-model="lv.avoidAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.avoidAdd')"
                style="width: 78px"
                size="mini"
              />
              <!-- 速度增加值输入框 -->
              <a-input-number
                v-model="lv.speedAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.speedAdd')"
                style="width: 78px"
                size="mini"
              />
              <!-- 跳跃力增加值输入框 -->
              <a-input-number
                v-model="lv.jumpAdd"
                :min="0"
                :placeholder="$t('equipEnhance.form.field.jumpAdd')"
                style="width: 78px"
                size="mini"
              />
            </a-space>
          </div>

          <!-- 消耗道具：增强所需的材料 -->
          <div class="cost-section">
            <span class="stat-group-label">{{
              $t('equipEnhance.form.field.costs')
            }}</span>
            <a-space direction="vertical" :size="4" style="width: 100%">
              <!-- 循环渲染每个消耗道具 -->
              <div v-for="(co, ci) in lv.costs" :key="ci">
                <a-space size="mini">
                  <!-- 消耗道具ID输入框 -->
                  <a-input-number
                    v-model="co.itemId"
                    :min="1"
                    :placeholder="$t('equipEnhance.form.field.costItemId')"
                    style="width: 120px"
                    size="mini"
                  />
                  <span style="color: #999">×</span>
                  <!-- 消耗道具数量输入框 -->
                  <a-input-number
                    v-model="co.count"
                    :min="1"
                    :placeholder="$t('equipEnhance.form.field.costCount')"
                    style="width: 80px"
                    size="mini"
                  />
                  <!-- 删除当前消耗道具按钮 -->
                  <a-button
                    size="mini"
                    type="text"
                    status="danger"
                    @click="lv.costs.splice(ci, 1)"
                  >
                    {{ $t('equipEnhance.form.removeCost') }}
                  </a-button>
                </a-space>
              </div>
            </a-space>
            <!-- 添加新的消耗道具按钮 -->
            <a-button
              size="mini"
              type="outline"
              style="margin-top: 6px"
              @click="lv.costs.push({ itemId: 0, count: 1 })"
            >
              + {{ $t('equipEnhance.form.addCost') }}
            </a-button>
          </div>
        </div>

        <!-- 添加新的增强等级按钮 -->
        <a-button type="outline" long @click="addLevel">
          + {{ $t('equipEnhance.form.addLevel') }}
        </a-button>
      </a-form>

      <!-- 抽屉底部按钮区域 -->
      <template #footer>
        <a-space>
          <!-- 取消按钮 -->
          <a-button @click="drawerVisible = false">{{
            $t('button.cancel')
          }}</a-button>
          <!-- 保存按钮 -->
          <a-button type="primary" @click="submitForm">{{
            $t('button.save')
          }}</a-button>
        </a-space>
      </template>
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
  // 导入Vue 3响应式API
  import { ref } from 'vue';
  // 导入通用加载状态管理Hook
  import useLoading from '@/hooks/loading';
  // 导入装备增强配置相关的API和类型定义
  import {
    deleteEquipEnhanceConfig,
    EquipEnhanceConfig,
    EquipEnhanceLevel,
    getEquipEnhanceList,
    saveEquipEnhanceConfig,
  } from '@/api/equipEnhance';
  // 导入获取游戏图标URL的工具函数
  import { getIconUrl } from '@/utils/mapleStoryAPI';
  // 导入Arco Design的消息提示组件
  import { Message } from '@arco-design/web-vue';
  // 导入问号圆圈填充图标
  import { IconQuestionCircleFill } from '@arco-design/web-vue/es/icon';
  // 导入国际化相关功能
  import { useI18n } from 'vue-i18n';

  // 获取国际化函数
  const { t } = useI18n();
  // 创建加载状态和设置函数
  const { loading, setLoading } = useLoading(false);
  // 表格数据：存储装备增强配置列表
  const tableData = ref<EquipEnhanceConfig[]>([]);

  /**
   * 加载装备增强配置数据
   * 异步获取所有装备增强配置信息并更新表格数据
   */
  const loadData = async () => {
    setLoading(true);
    try {
      // 请求装备增强配置列表API
      const { data } = await getEquipEnhanceList();
      // 更新表格数据
      tableData.value = data;
    } finally {
      // 确保无论成功或失败都取消加载状态
      setLoading(false);
    }
  };
  // 组件初始化时加载数据
  loadData();

  // ========== 编辑抽屉 ==========
  // 控制编辑抽屉是否可见
  const drawerVisible = ref(false);
  // 当前编辑记录的ID（undefined表示新增）
  const editId = ref<number | undefined>();

  /**
   * 创建空的增强等级配置
   * 生成一个带有默认值的装备增强等级配置对象
   * @param lv - 等级编号
   * @returns 装备增强等级配置对象
   */
  const emptyLevel = (lv: number): EquipEnhanceLevel => ({
    enhanceLevel: lv, // 增强等级
    successRate: 100, // 成功率（百分比）
    destroyOnFail: 0, // 失败时销毁（0-否，1-是）
    mesoCost: 0, // 消耗金币
    strAdd: 0, // 力量增加值
    dexAdd: 0, // 敏捷增加值
    intAdd: 0, // 智力增加值
    lukAdd: 0, // 运气增加值
    hpAdd: 0, // 生命值增加值
    mpAdd: 0, // 魔法值增加值
    watkAdd: 0, // 物理攻击力增加值
    matkAdd: 0, // 魔法攻击力增加值
    wdefAdd: 0, // 物理防御力增加值
    mdefAdd: 0, // 魔法防御力增加值
    accAdd: 0, // 命中率增加值
    avoidAdd: 0, // 回避率增加值
    speedAdd: 0, // 速度增加值
    jumpAdd: 0, // 跳跃力增加值
    costs: [], // 增强消耗物品列表
  });

  const editConfig = ref<EquipEnhanceConfig>({
    itemId: 0,
    itemName: '',
    uniquePerChar: 0,
    maxEnhance: 10,
    enabled: 1,
    levels: [],
  });

  /**
   * 处理新增装备增强配置的点击事件
   * 初始化一个新的配置对象并打开编辑抽屉
   */
  /**
   * 处理新增装备增强配置的点击事件
   * 初始化一个新的配置对象并打开编辑抽屉
   */
  const insertClick = () => {
    editId.value = undefined; // 设置为undefined表示新增模式
    editConfig.value = {
      itemId: 0, // 物品ID
      itemName: '', // 物品名称
      uniquePerChar: 0, // 每角色唯一（0-否，1-是）
      maxEnhance: 10, // 最大增强等级
      enabled: 1, // 是否启用（0-否，1-是）
      levels: [emptyLevel(1)], // 包含一个默认等级的数组
    };
    drawerVisible.value = true; // 显示编辑抽屉
  };

  /**
   * 处理编辑装备增强配置的点击事件
   * 加载指定记录的数据到编辑表单并打开编辑抽屉
   * @param record - 要编辑的装备增强配置记录
   */
  /**
   * 处理编辑装备增强配置的点击事件
   * 加载指定记录的数据到编辑表单并打开编辑抽屉
   * @param record - 要编辑的装备增强配置记录
   */
  const editClick = (record: EquipEnhanceConfig) => {
    editId.value = record.id; // 设置当前编辑的记录ID
    // 深拷贝记录数据以避免直接修改原数据
    editConfig.value = JSON.parse(JSON.stringify(record));
    // 如果没有等级配置或配置为空，则添加一个默认等级
    if (!editConfig.value.levels || editConfig.value.levels.length === 0) {
      editConfig.value.levels = [emptyLevel(1)];
    }
    // 确保每个等级配置都有costs数组
    editConfig.value.levels.forEach((lv) => {
      if (!lv.costs) lv.costs = [];
    });
    drawerVisible.value = true; // 显示编辑抽屉
  };

  /**
   * 添加一个新的增强等级
   * 在当前等级配置数组末尾添加一个新等级
   */
  const addLevel = () => {
    // 计算下一个等级编号
    const next = editConfig.value.levels.length + 1;
    // 添加新的等级配置
    editConfig.value.levels.push(emptyLevel(next));
  };

  /**
   * 删除指定索引的增强等级
   * @param idx - 要删除的等级在数组中的索引
   */
  const removeLevel = (idx: number) => {
    // 从数组中移除指定索引的等级配置
    editConfig.value.levels.splice(idx, 1);
  };

  /**
   * 提交表单数据
   * 保存当前编辑的装备增强配置到服务器
   */
  const submitForm = async () => {
    try {
      // 修正等级序号，确保连续性
      editConfig.value.levels.forEach((_, i) => {
        editConfig.value.levels[i].enhanceLevel = i + 1;
      });
      // 保存装备增强配置到服务器
      await saveEquipEnhanceConfig(editConfig.value);
      // 显示保存成功消息
      Message.success(t('equipEnhance.message.saveSuccess'));
      // 关闭编辑抽屉
      drawerVisible.value = false;
      // 重新加载数据以更新表格
      loadData();
    } catch (e: any) {
      // 显示保存失败消息
      Message.error(e?.message || t('equipEnhance.message.saveFailed'));
    }
  };

  /**
   * 删除指定的装备增强配置
   * @param record - 要删除的装备增强配置记录
   */
  const deleteClick = async (record: EquipEnhanceConfig) => {
    try {
      // 检查记录ID是否为空
      if (record.id == null) {
        Message.error(t('equipEnhance.message.recordIdRequired'));
        return;
      }
      // 从服务器删除指定的装备增强配置
      await deleteEquipEnhanceConfig(record.id);
      // 显示删除成功消息
      Message.success(t('equipEnhance.message.deleteSuccess'));
      // 重新加载数据以更新表格
      loadData();
    } catch (e: any) {
      // 显示删除失败消息
      Message.error(e?.message || t('equipEnhance.message.deleteFailed'));
    }
  };

  /**
   * 处理图片加载失败事件
   * 当物品图标无法加载时隐藏图片元素
   * @param e - 图片加载错误事件
   */
  const onImgError = (e: Event) => {
    // 将目标元素（图片）的显示设置为隐藏
    (e.target as HTMLImageElement).style.display = 'none';
  };
</script>

<style scoped>
  .level-card {
    background: var(--color-fill-1);
    border-radius: 8px;
    padding: 12px 16px;
    margin-bottom: 12px;
  }

  .level-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
  }

  .level-title {
    font-weight: 600;
    font-size: 14px;
    color: rgb(var(--primary-6));
  }

  .stat-section {
    margin-bottom: 8px;
  }

  .stat-group-label {
    display: inline-block;
    width: 56px;
    font-size: 12px;
    color: var(--color-text-3);
    margin-right: 8px;
    vertical-align: middle;
    line-height: 24px;
  }

  .cost-section {
    margin-top: 8px;
    padding-top: 8px;
    border-top: 1px dashed var(--color-border-2);
  }
</style>
