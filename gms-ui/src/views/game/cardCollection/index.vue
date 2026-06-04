<template>
  <div class="container">
    <Breadcrumb />
    <a-card
      class="general-card"
      :title="$t('menu.game.cardCollection')"
      style="overflow-x: auto"
    >
      <a-row>
        <a-col>
          <a-space>
            <a-button type="primary" @click="loadData">
              {{ $t('button.search') }}
            </a-button>
            <a-button type="primary" status="success" @click="insertClick">
              {{ $t('button.create') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
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
          <a-table-column
            title="ID"
            data-index="id"
            :width="55"
            align="center"
          />
          <a-table-column
            :title="$t('cardCollection.list.column.regionName')"
            data-index="regionName"
            :width="140"
            align="center"
          />
          <a-table-column
            :title="$t('cardCollection.list.column.sortOrder')"
            data-index="sortOrder"
            :width="60"
            align="center"
          />
          <a-table-column
            :title="$t('cardCollection.list.column.monsterId')"
            :width="90"
            align="center"
          >
            <template #cell="{ record }">
              <a-space size="mini">
                <a-popover>
                  <img
                    :src="getIconUrl('mob', record.monsterId)"
                    alt=""
                    style="width: 32px; height: 32px"
                    @error="onImgError($event)"
                  />
                  <template #content>
                    <img
                      :src="getIconUrl('mob', record.monsterId)"
                      alt=""
                      @error="onImgError($event)"
                    />
                  </template>
                </a-popover>
                {{ record.monsterId }}
              </a-space>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('cardCollection.list.column.cardItemId')"
            :width="110"
            align="center"
          >
            <template #cell="{ record }">
              <a-space size="mini">
                <img
                  :src="getIconUrl('item', record.cardItemId)"
                  alt=""
                  style="width: 32px; height: 32px"
                  @error="onImgError($event)"
                />
                {{ record.cardItemId }}
              </a-space>
            </template>
          </a-table-column>
          <a-table-column
            :title="$t('cardCollection.list.column.operations')"
            :width="150"
            align="center"
            fixed="right"
          >
            <template #cell="{ record }">
              <a-space>
                <a-button size="mini" type="text" @click="editClick(record)">
                  {{ $t('button.edit') }}
                </a-button>
                <a-popconfirm
                  type="error"
                  :content="$t('cardCollection.message.deleteTips')"
                  position="left"
                  @ok="deleteClick(record)"
                >
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

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="formVisible"
      :title="
        formMode === 'add'
          ? $t('cardCollection.form.title.create')
          : $t('cardCollection.form.title.update')
      "
      @ok="submitForm"
      @cancel="formVisible = false"
    >
      <a-form ref="formRef" :model="formData">
        <a-form-item
          field="regionName"
          :label="$t('cardCollection.form.field.regionName')"
        >
          <a-input
            v-model="formData.regionName"
            :placeholder="$t('cardCollection.placeholder.regionName')"
          />
        </a-form-item>
        <a-form-item
          field="sortOrder"
          :label="$t('cardCollection.form.field.sortOrder')"
        >
          <a-input-number
            v-model="formData.sortOrder"
            :min="0"
            :default-value="0"
          />
        </a-form-item>
        <a-form-item
          field="monsterId"
          :label="$t('cardCollection.form.field.monsterId')"
        >
          <a-input-number
            v-model="formData.monsterId"
            :min="1"
            :placeholder="$t('cardCollection.placeholder.monsterId')"
          />
        </a-form-item>
        <a-form-item
          field="cardItemId"
          :label="$t('cardCollection.form.field.cardItemId')"
        >
          <a-input-number
            v-model="formData.cardItemId"
            :min="1"
            :placeholder="$t('cardCollection.placeholder.cardItemId')"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    addCardCollection,
    CardCollectionItem,
    deleteCardCollection,
    getCardCollectionList,
    updateCardCollection,
  } from '@/api/cardCollection';
  import { getIconUrl } from '@/utils/mapleStoryAPI';
  import { Message } from '@arco-design/web-vue';

  const { loading, setLoading } = useLoading(false);
  const tableData = ref<CardCollectionItem[]>([]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getCardCollectionList({ notPage: true });
      tableData.value = data.records;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  // ========== 新增/编辑 ==========
  const formVisible = ref(false);
  const formMode = ref<'add' | 'edit'>('add');
  const formData = ref<CardCollectionItem>({
    regionName: '',
    sortOrder: 0,
    monsterId: 0,
    cardItemId: 0,
  });

  const insertClick = () => {
    formMode.value = 'add';
    formData.value = {
      regionName: '',
      sortOrder: 0,
      monsterId: 0,
      cardItemId: 0,
    };
    formVisible.value = true;
  };

  const editClick = (record: CardCollectionItem) => {
    formMode.value = 'edit';
    formData.value = { ...record };
    formVisible.value = true;
  };

  const submitForm = async () => {
    try {
      if (formMode.value === 'add') {
        await addCardCollection(formData.value);
        Message.success('新增成功');
      } else {
        await updateCardCollection(formData.value);
        Message.success('更新成功');
      }
      formVisible.value = false;
      loadData();
    } catch (e: any) {
      Message.error(e?.message || '操作失败');
    }
  };

  const deleteClick = async (record: CardCollectionItem) => {
    try {
      await deleteCardCollection(record.id!);
      Message.success('删除成功');
      loadData();
    } catch (e: any) {
      Message.error(e?.message || '删除失败');
    }
  };

  const onImgError = (e: Event) => {
    (e.target as HTMLImageElement).style.display = 'none';
  };
</script>
