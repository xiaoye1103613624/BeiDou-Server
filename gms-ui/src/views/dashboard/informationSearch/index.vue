<template>
  <div class="container">
    <Breadcrumb />
    <a-card
      class="general-card"
      :title="$t('menu.dashboard.informationSearch')"
    >
      <a-row>
        <a-select
          v-model="condition.types"
          :placeholder="$t('informationSearch.placeholder.type')"
          :readonly="true"
          multiple
          :max-tag-count="3"
          allow-clear
          class="a-space-son"
        >
          <a-option value="cash">
            {{ $t('informationSearch.type.cash') }}
          </a-option>
          <a-option value="consume">
            {{ $t('informationSearch.type.consume') }}
          </a-option>
          <a-option value="eqp">
            {{ $t('informationSearch.type.eqp') }}
          </a-option>
          <a-option value="etc">
            {{ $t('informationSearch.type.etc') }}
          </a-option>
          <a-option value="ins">
            {{ $t('informationSearch.type.ins') }}
          </a-option>
          <a-option value="map">
            {{ $t('informationSearch.type.map') }}
          </a-option>
          <a-option value="mob">
            {{ $t('informationSearch.type.mob') }}
          </a-option>
          <a-option value="npc">
            {{ $t('informationSearch.type.npc') }}
          </a-option>
          <a-option value="pet">
            {{ $t('informationSearch.type.pet') }}
          </a-option>
          <a-option value="skill">
            {{ $t('informationSearch.type.skill') }}
          </a-option>
        </a-select>
        <a-input
          v-model="condition.filter"
          :placeholder="$t('informationSearch.placeholder.filter')"
          class="a-space-son"
          @keydown.enter="searchData"
        />
        <a-button type="primary" @click="searchData">
          {{ $t('button.search') }}
        </a-button>
        <a-button @click="resetSearch">
          {{ $t('button.reset') }}
        </a-button>
      </a-row>
      <a-table
        row-key="id"
        :loading="loading"
        :data="informationList"
        column-resizable
        :pagination="false"
        :bordered="{ wrapper: true, cell: true }"
      >
        <template #columns>
          <a-table-column
            :title="$t('informationSearch.column.type')"
            data-index="type"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag color="arcoblue">
                {{ getTag(record.type) }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('informationSearch.column.id')"
            data-index="id"
            align="center"
          />
          <a-table-column
            :title="$t('informationSearch.column.name')"
            data-index="name"
            align="center"
          >
            <template #cell="{ record }">
              <a-popover
                trigger="hover"
                :popup-visible="hoveredRecord?.id === record.id && hoveredRecord?.type === record.type"
                @popup-visible-change="(visible: boolean) => handlePopoverChange(visible, record)"
              >
                <a-button type="text" size="mini">
                  {{ record.name }}
                </a-button>
                <template #content>
                  <!-- 加载中 -->
                  <div class="item-tooltip-loading" v-if="tooltipLoading">
                    <a-spin dot />
                    加载中...
                  </div>
                  <!-- 加载失败或非物品类型 -->
                  <div class="item-tooltip-empty" v-else-if="!itemDetail">
                    暂无可查看的物品详情
                  </div>
                  <!-- 物品详情 -->
                  <div class="item-tooltip" v-else>
                    <!-- 物品基本信息 -->
                    <div class="item-tooltip-header">
                      <img
                        :src="getImg(record.type, record.id)"
                        alt=""
                        class="item-tooltip-icon"
                      />
                      <div class="item-tooltip-title">
                        <div class="item-name">{{ itemDetail.name || record.name }}</div>
                        <div class="item-id">ID: {{ record.id }}</div>
                        <div class="item-type">
                          <a-tag :color="getTypeColor(record.type)" size="small">
                            {{ getTag(record.type) }}
                          </a-tag>
                        </div>
                      </div>
                    </div>
                    <!-- 物品描述 -->
                    <div class="item-tooltip-desc" v-if="itemDetail.desc || record.desc">
                      {{ itemDetail.desc || record.desc }}
                    </div>
                    <!-- 通用属性 -->
                    <div class="item-tooltip-stats" v-if="hasGeneralInfo">
                      <div class="stat-row" v-if="itemDetail.wholePrice !== null && itemDetail.wholePrice > 0">
                        <span class="stat-label">售价：</span>
                        <span class="stat-value">{{ itemDetail.wholePrice.toLocaleString() }} 金币</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.slotMax !== null && itemDetail.slotMax > 0 && itemDetail.slotMax < 1000">
                        <span class="stat-label">最大堆叠：</span>
                        <span class="stat-value">{{ itemDetail.slotMax }}</span>
                      </div>
                    </div>
                    <!-- 装备属性 -->
                    <div class="item-tooltip-stats" v-if="hasEquipStats">
                      <div class="stat-section-title">装备属性</div>
                      <div class="stat-row" v-if="itemDetail.str">
                        <span class="stat-label">力量：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.str }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.dex">
                        <span class="stat-label">敏捷：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.dex }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.int">
                        <span class="stat-label">智力：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.int }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.luk">
                        <span class="stat-label">运气：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.luk }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.hp">
                        <span class="stat-label">HP：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.hp }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.mp">
                        <span class="stat-label">MP：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.mp }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.pAtk">
                        <span class="stat-label">攻击力：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.pAtk }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.mAtk">
                        <span class="stat-label">魔力：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.mAtk }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.pDef">
                        <span class="stat-label">防御力：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.pDef }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.mDef">
                        <span class="stat-label">魔防：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.mDef }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.acc">
                        <span class="stat-label">命中：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.acc }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.avoid">
                        <span class="stat-label">回避：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.avoid }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.speed">
                        <span class="stat-label">移速：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.speed }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.jump">
                        <span class="stat-label">跳跃：</span>
                        <span class="stat-value stat-plus">+{{ itemDetail.jump }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.upgradeSlots !== null && itemDetail.upgradeSlots > 0">
                        <span class="stat-label">升级次数：</span>
                        <span class="stat-value">{{ itemDetail.upgradeSlots }}</span>
                      </div>
                    </div>
                    <!-- 穿戴要求 -->
                    <div class="item-tooltip-stats" v-if="hasEquipReqs">
                      <div class="stat-section-title">穿戴要求</div>
                      <div class="stat-row" v-if="itemDetail.reqLevel">
                        <span class="stat-label">等级：</span>
                        <span class="stat-value">{{ itemDetail.reqLevel }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.reqStr">
                        <span class="stat-label">力量：</span>
                        <span class="stat-value">{{ itemDetail.reqStr }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.reqDex">
                        <span class="stat-label">敏捷：</span>
                        <span class="stat-value">{{ itemDetail.reqDex }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.reqInt">
                        <span class="stat-label">智力：</span>
                        <span class="stat-value">{{ itemDetail.reqInt }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.reqLuk">
                        <span class="stat-label">运气：</span>
                        <span class="stat-value">{{ itemDetail.reqLuk }}</span>
                      </div>
                      <div class="stat-row" v-if="itemDetail.reqJob">
                        <span class="stat-label">职业要求：</span>
                        <span class="stat-value">{{ getJobName(itemDetail.reqJob) }}</span>
                      </div>
                    </div>
                    <!-- 限制标记 -->
                    <div class="item-tooltip-flags" v-if="hasFlags">
                      <a-tag color="red" size="small" v-if="itemDetail.questItem">任务物品</a-tag>
                      <a-tag color="orange" size="small" v-if="itemDetail.untradeable">不可交易</a-tag>
                      <a-tag color="orangered" size="small" v-if="itemDetail.accountRestricted">账号绑定</a-tag>
                      <a-tag color="gray" size="small" v-if="itemDetail.dropRestricted">不可丢弃</a-tag>
                      <a-tag color="arcoblue" size="small" v-if="itemDetail.cashItem">现金物品</a-tag>
                      <a-tag color="green" size="small" v-if="itemDetail.upgradeable">可升级</a-tag>
                    </div>
                  </div>
                </template>
              </a-popover>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('informationSearch.column.desc')"
            data-index="desc"
            align="center"
            :width="400"
            :style="{ minWidth: '400px' }"
          />
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { Message } from '@arco-design/web-vue';
  import useLoading from '@/hooks/loading';
  import { getIconUrl } from '@/utils/mapleStoryAPI';
  import {
    InformationSearch,
    InformationResult,
    ItemDetailRtn,
    informationSearch,
    getItemDetail,
  } from '@/api/information';

  /** 物品类型列表（支持悬停查看详情的类型） */
  const ITEM_TYPES = ['cash', 'consume', 'eqp', 'etc', 'ins', 'pet'];

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);
  const informationList = ref<InformationResult[]>([]);
  const condition = ref<InformationSearch>({
    types: [],
    filter: '',
  });

  /** 当前悬停的记录 */
  const hoveredRecord = ref<{ id: number; type: string } | null>(null);
  /** 物品详情数据 */
  const itemDetail = ref<ItemDetailRtn | null>(null);
  /** 弹窗加载中 */
  const tooltipLoading = ref(false);

  /** 是否有通用信息可展示 */
  const hasGeneralInfo = computed(() => {
    if (!itemDetail.value) return false;
    const d = itemDetail.value;
    return (
      (d.wholePrice !== null && d.wholePrice > 0) ||
      (d.slotMax !== null && d.slotMax > 0 && d.slotMax < 1000)
    );
  });

  /** 是否有装备属性可展示 */
  const hasEquipStats = computed(() => {
    if (!itemDetail.value) return false;
    const d = itemDetail.value;
    return (
      d.str || d.dex || d.int || d.luk || d.hp || d.mp ||
      d.pAtk || d.mAtk || d.pDef || d.mDef || d.acc || d.avoid ||
      d.speed || d.jump || (d.upgradeSlots !== null && d.upgradeSlots > 0)
    );
  });

  /** 是否有穿戴要求可展示 */
  const hasEquipReqs = computed(() => {
    if (!itemDetail.value) return false;
    const d = itemDetail.value;
    return !!(
      d.reqLevel ||
      d.reqStr ||
      d.reqDex ||
      d.reqInt ||
      d.reqLuk ||
      d.reqJob
    );
  });

  /** 是否有标记可展示 */
  const hasFlags = computed(() => {
    if (!itemDetail.value) return false;
    const d = itemDetail.value;
    return !!(
      d.questItem ||
      d.untradeable ||
      d.accountRestricted ||
      d.dropRestricted ||
      d.cashItem ||
      d.upgradeable
    );
  });

  /**
   * 处理气泡弹窗显示/隐藏
   */
  const handlePopoverChange = async (visible: boolean, record: InformationResult) => {
    if (visible) {
      // 只有物品类型（非map/npc/mob/skill）才获取详情
      if (!ITEM_TYPES.includes(record.type)) {
        hoveredRecord.value = null;
        itemDetail.value = null;
        return;
      }
      hoveredRecord.value = { id: record.id, type: record.type };
      tooltipLoading.value = true;
      try {
        const { data } = await getItemDetail({ itemId: record.id, type: record.type });
        itemDetail.value = data;
      } catch {
        itemDetail.value = null;
      } finally {
        tooltipLoading.value = false;
      }
    } else {
      hoveredRecord.value = null;
      itemDetail.value = null;
      tooltipLoading.value = false;
    }
  };

  /**
   * 根据类型获取标签颜色
   */
  const getTypeColor = (type: string) => {
    const colorMap: Record<string, string> = {
      cash: 'red',
      consume: 'green',
      eqp: 'arcoblue',
      etc: 'gray',
      ins: 'purple',
      pet: 'orange',
    };
    return colorMap[type] || 'arcoblue';
  };

  /**
   * 根据职业位掩码获取职业名称
   */
  const getJobName = (reqJob: number) => {
    const jobMap: Record<number, string> = {
      0: '初心者',
      1: '剑客',
      2: '弓箭手',
      4: '法师',
      8: '飞侠',
      16: '海盗',
    };
    // 位掩码可能包含多个职业
    const matched: string[] = [];
    Object.entries(jobMap).forEach(([key, name]) => {
      if (reqJob & parseInt(key, 10)) {
        matched.push(name);
      }
    });
    return matched.length > 0 ? matched.join('/') : '无要求';
  };

  const getImg = (type: string, id: number) => {
    let imgType = type.toLowerCase();
    if (ITEM_TYPES.includes(type)) {
      imgType = 'item';
    }
    return getIconUrl(imgType, id);
  };

  const searchData = async () => {
    if (!condition.value.filter) {
      Message.error({
        content: t('informationSearch.check.filter'),
        duration: 3 * 1000,
      });
      return;
    }
    setLoading(true);
    try {
      const { data } = await informationSearch(condition.value);
      informationList.value = data;
    } finally {
      setLoading(false);
    }
  };

  const resetSearch = () => {
    condition.value.types = [];
    condition.value.filter = '';
  };

  const getTag = (type: string) => {
    let tag;
    switch (type) {
      case 'cash':
      case 'consume':
      case 'eqp':
      case 'etc':
      case 'ins':
      case 'map':
      case 'mob':
      case 'npc':
      case 'pet':
      case 'skill':
        tag = t(`informationSearch.type.${type}`);
        break;
      default:
        tag = type;
        break;
    }
    return tag;
  };
</script>

<script lang="ts">
  export default {
    name: 'InformationSearch',
  };
</script>

<style lang="less" scoped>
  .arco-card-body > .arco-row > {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
  }
  :deep(.a-space-son) {
    width: 400px;
    max-width: 100%;
  }
  :deep(.arco-table-th:nth-child(1)) {
    min-width: 70px;
  }
  :deep(.arco-table-th:nth-child(2)) {
    min-width: 100px;
  }
  :deep(.arco-table-th:nth-child(3)) {
    min-width: 50px;
    max-width: 150px;
  }
  :deep(.arco-table-th:nth-child(4)) {
    min-width: 400px;
  }

  /* 物品悬停提示样式 */
  .item-tooltip {
    max-width: 280px;
    min-width: 220px;
  }

  .item-tooltip-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;
  }

  .item-tooltip-icon {
    width: 48px;
    height: 48px;
    image-rendering: pixelated;
    flex-shrink: 0;
  }

  .item-tooltip-title {
    flex: 1;
    min-width: 0;
  }

  .item-name {
    font-weight: 600;
    font-size: 14px;
    color: var(--color-text-1);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .item-id {
    font-size: 11px;
    color: var(--color-text-3);
    margin-top: 2px;
  }

  .item-type {
    margin-top: 4px;
  }

  .item-tooltip-desc {
    font-size: 12px;
    color: var(--color-text-2);
    margin-bottom: 8px;
    padding: 6px 8px;
    background: var(--color-fill-1);
    border-radius: 4px;
    max-height: 60px;
    overflow-y: auto;
    word-break: break-all;
  }

  .item-tooltip-stats {
    margin-bottom: 8px;
  }

  .stat-section-title {
    font-size: 12px;
    font-weight: 600;
    color: var(--color-text-1);
    border-bottom: 1px solid var(--color-border-2);
    padding-bottom: 4px;
    margin-bottom: 4px;
  }

  .stat-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2px 0;
    font-size: 12px;
  }

  .stat-label {
    color: var(--color-text-2);
  }

  .stat-value {
    color: var(--color-text-1);
    font-weight: 500;
  }

  .stat-plus {
    color: #00b42a;
  }

  .item-tooltip-flags {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    margin-top: 6px;
  }

  .item-tooltip-loading {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 12px 0;
    font-size: 12px;
    color: var(--color-text-3);
  }

  .item-tooltip-empty {
    padding: 12px 8px;
    font-size: 12px;
    color: var(--color-text-3);
    text-align: center;
  }
</style>
