<template>
  <PageContainer
    :title="$t('menu.client.skillResources')"
    :description="$t('clientSkill.page.desc')"
  >
    <ProCard soft>
      <a-alert type="info" class="mb-alert">{{
        $t('clientSkill.hint.switch')
      }}</a-alert>
      <a-space wrap class="bd-page-toolbar">
        <a-tag :color="status?.editorEnabled ? 'green' : 'gray'">
          {{
            status?.editorEnabled
              ? $t('clientSkill.status.editorOn')
              : $t('clientSkill.status.editorOff')
          }}
        </a-tag>
        <a-tag :color="status?.allowClientWrite ? 'green' : 'gray'">
          {{
            status?.allowClientWrite
              ? $t('clientSkill.status.clientWriteOn')
              : $t('clientSkill.status.clientWriteOff')
          }}
        </a-tag>
        <a-tag :color="status?.patcherAvailable ? 'arcoblue' : 'orangered'">
          {{
            status?.patcherAvailable
              ? $t('clientSkill.status.patcher')
              : $t('clientSkill.status.noPatcher')
          }}
        </a-tag>
        <a-button size="small" :loading="statusLoading" @click="loadStatus">
          {{ $t('clientSkill.refreshStatus') }}
        </a-button>
        <a-button size="small" @click="goClientPath">{{
          $t('menu.client.path')
        }}</a-button>
        <a-button size="small" @click="goAssetHub">{{
          $t('menu.client.assetHub')
        }}</a-button>
      </a-space>
    </ProCard>

    <div class="skill-layout">
      <ProCard class="skill-sider" :title="$t('menu.client.skillResources')">
        <a-tabs
          v-model:active-key="lineage"
          size="small"
          @change="onLineageChange"
        >
          <a-tab-pane
            v-for="l in lineages"
            :key="l.code"
            :title="lineageTitle(l)"
            :disabled="!l.enabled"
          />
        </a-tabs>
        <a-spin :loading="linesLoading" style="width: 100%">
          <a-menu
            v-if="jobLines.length"
            :selected-keys="selectedLineId ? [selectedLineId] : []"
            @menu-item-click="onSelectLine"
          >
            <a-menu-item v-for="line in jobLines" :key="line.lineId">
              {{ lineDisplayName(line) }}
            </a-menu-item>
          </a-menu>
          <a-empty v-else />
        </a-spin>
      </ProCard>

      <ProCard class="skill-main">
        <template v-if="currentLine">
          <div class="bd-page-toolbar skill-toolbar">
            <a-radio-group
              v-model="activeJobId"
              type="button"
              size="small"
              @change="loadBook"
            >
              <a-radio
                v-for="st in currentLine.stages"
                :key="st.jobId"
                :value="st.jobId"
              >
                {{ stageLabel(st) }}
              </a-radio>
            </a-radio-group>
            <a-space wrap>
              <a-button size="small" @click="fitView">{{
                $t('clientSkill.fitView')
              }}</a-button>
              <a-button
                size="small"
                :loading="iconLoading"
                @click="runEnsureIcons"
              >
                {{ $t('clientSkill.ensureIcons') }}
              </a-button>
              <a-button
                v-if="status?.editorEnabled"
                size="small"
                :loading="reloading"
                @click="runReload"
              >
                {{ $t('clientSkill.reload') }}
              </a-button>
              <a-button
                size="small"
                :loading="patching"
                @click="runPatch(true)"
              >
                {{ $t('clientSkill.patchDryRun') }}
              </a-button>
              <a-button
                v-if="status?.allowClientWrite"
                size="small"
                status="warning"
                :loading="patching"
                @click="confirmApply"
              >
                {{ $t('clientSkill.patchApply') }}
              </a-button>
            </a-space>
          </div>

          <div class="skill-canvas-wrap">
            <a-spin :loading="bookLoading" style="width: 100%; height: 100%">
              <VueFlow
                v-model:nodes="nodes"
                v-model:edges="edges"
                :node-types="nodeTypes"
                fit-view-on-init
                :min-zoom="0.2"
                :max-zoom="2"
                @node-click="onNodeClick"
              >
                <Background />
              </VueFlow>
            </a-spin>
          </div>
        </template>
        <a-empty v-else :description="$t('clientSkill.selectLine')" />
      </ProCard>
    </div>

    <a-drawer
      :visible="drawerVisible"
      :width="480"
      unmount-on-close
      @cancel="drawerVisible = false"
    >
      <template #title>{{ $t('clientSkill.detail.title') }}</template>
      <a-spin :loading="detailLoading">
        <template v-if="detail">
          <a-form :model="editForm" layout="vertical">
            <a-form-item :label="$t('clientSkill.detail.id')">
              <a-input :model-value="String(detail.skillId)" disabled />
            </a-form-item>
            <a-form-item :label="$t('clientSkill.detail.job')">
              <a-input :model-value="String(detail.jobId)" disabled />
            </a-form-item>
            <a-form-item :label="$t('clientSkill.detail.name')">
              <a-input v-model="editForm.name" :disabled="!canEdit" />
            </a-form-item>
            <a-form-item :label="$t('clientSkill.detail.desc')">
              <a-textarea
                v-model="editForm.desc"
                :auto-size="{ minRows: 2, maxRows: 6 }"
                :disabled="!canEdit"
              />
            </a-form-item>
            <a-form-item :label="$t('clientSkill.detail.masterLevel')">
              <a-input-number
                v-model="editForm.masterLevel"
                :disabled="!canEdit"
                hide-button
              />
            </a-form-item>
            <a-form-item :label="$t('clientSkill.detail.invisible')">
              <a-switch v-model="editForm.invisible" :disabled="!canEdit" />
            </a-form-item>
            <a-form-item :label="$t('clientSkill.detail.req')">
              <a-textarea
                v-model="editForm.reqJson"
                :auto-size="{ minRows: 2, maxRows: 6 }"
                :disabled="!canEdit"
              />
            </a-form-item>
            <a-form-item :label="$t('clientSkill.detail.levels')">
              <a-textarea
                v-model="editForm.levelsJson"
                :auto-size="{ minRows: 4, maxRows: 12 }"
                :disabled="!canEdit"
              />
            </a-form-item>
            <a-form-item :label="$t('clientSkill.detail.hs')">
              <a-textarea
                v-model="editForm.hsJson"
                :auto-size="{ minRows: 2, maxRows: 8 }"
                :disabled="!canEdit"
              />
            </a-form-item>
          </a-form>
          <a-space v-if="canEdit" wrap>
            <a-button type="primary" :loading="saving" @click="saveSkillData">
              {{ $t('clientSkill.saveSkill') }}
            </a-button>
            <a-button :loading="saving" @click="saveStringData">
              {{ $t('clientSkill.saveString') }}
            </a-button>
          </a-space>
        </template>
      </a-spin>
    </a-drawer>
  </PageContainer>
</template>

<script lang="ts" setup>
  import { computed, markRaw, nextTick, onMounted, reactive, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useI18n } from 'vue-i18n';
  import { Message, Modal } from '@arco-design/web-vue';
  import { Background } from '@vue-flow/background';
  import {
    VueFlow,
    useVueFlow,
    type Edge,
    type Node,
    type NodeTypesObject,
  } from '@vue-flow/core';
  import dagre from '@dagrejs/dagre';
  import '@vue-flow/core/dist/style.css';
  import '@vue-flow/core/dist/theme-default.css';
  import {
    ensureSkillIcons,
    fetchSkillBook,
    fetchSkillDetail,
    fetchSkillEditorStatus,
    fetchSkillJobLines,
    fetchSkillLineages,
    patchSkillApply,
    patchSkillDryRun,
    reloadSkills,
    writeSkill,
    writeSkillString,
    type SkillDetail,
    type SkillEditorStatus,
    type SkillJobLine,
    type SkillJobStage,
    type SkillLineage,
  } from '@/api/clientSkill';
  import SkillNode from './SkillNode.vue';

  const { t, te } = useI18n();
  const router = useRouter();
  const { fitView: vfFitView } = useVueFlow();
  const nodeTypes = { skill: markRaw(SkillNode) } as unknown as NodeTypesObject;

  const lineages = ref<SkillLineage[]>([]);
  const lineage = ref('explorer');
  const jobLines = ref<SkillJobLine[]>([]);
  const selectedLineId = ref('');
  const activeJobId = ref<number | undefined>();
  const linesLoading = ref(false);
  const bookLoading = ref(false);
  const statusLoading = ref(false);
  const iconLoading = ref(false);
  const reloading = ref(false);
  const patching = ref(false);
  const detailLoading = ref(false);
  const saving = ref(false);
  const status = ref<SkillEditorStatus | null>(null);
  const nodes = ref<Node[]>([]);
  const edges = ref<Edge[]>([]);
  const drawerVisible = ref(false);
  const detail = ref<SkillDetail | null>(null);

  const editForm = reactive({
    name: '',
    desc: '',
    masterLevel: undefined as number | undefined,
    invisible: false,
    reqJson: '{}',
    levelsJson: '[]',
    hsJson: '{}',
  });

  const currentLine = computed(() =>
    jobLines.value.find((l) => l.lineId === selectedLineId.value)
  );
  const canEdit = computed(() => !!status.value?.editorEnabled);

  const lineageTitle = (l: SkillLineage) => {
    const name = l.name || (te(l.nameKey) ? t(l.nameKey) : l.code);
    return l.enabled ? name : `${name} ${t('clientSkill.lineage.reserved')}`;
  };

  const lineDisplayName = (line: SkillJobLine) =>
    line.name || (te(line.nameKey) ? t(line.nameKey) : line.nameKey);

  const stageLabel = (st: SkillJobStage) => {
    const branchKey = `clientSkill.stage.branch${st.branch}`;
    const branch = te(branchKey) ? t(branchKey) : String(st.branch);
    const jobName =
      st.name || (te(st.nameKey) ? t(st.nameKey) : String(st.jobId));
    return `${branch} · ${jobName}`;
  };

  const layoutGraph = (n: Node[], e: Edge[]) => {
    const g = new dagre.graphlib.Graph();
    g.setDefaultEdgeLabel(() => ({}));
    g.setGraph({ rankdir: 'TB', nodesep: 40, ranksep: 60 });
    n.forEach((node) => g.setNode(node.id, { width: 160, height: 56 }));
    e.forEach((edge) => g.setEdge(edge.source, edge.target));
    dagre.layout(g);
    return n.map((node) => {
      const pos = g.node(node.id);
      return {
        ...node,
        position: {
          x: (pos?.x || 0) - 80,
          y: (pos?.y || 0) - 28,
        },
      };
    });
  };

  const loadStatus = async () => {
    statusLoading.value = true;
    try {
      const res = await fetchSkillEditorStatus();
      status.value = res.data;
    } catch {
      Message.error(t('clientSkill.msg.loadFail'));
    } finally {
      statusLoading.value = false;
    }
  };

  const loadJobLines = async () => {
    linesLoading.value = true;
    selectedLineId.value = '';
    activeJobId.value = undefined;
    nodes.value = [];
    edges.value = [];
    try {
      const res = await fetchSkillJobLines(lineage.value);
      jobLines.value = res.data || [];
    } catch {
      jobLines.value = [];
      Message.error(t('clientSkill.msg.loadFail'));
    } finally {
      linesLoading.value = false;
    }
  };

  const loadLineages = async () => {
    try {
      const res = await fetchSkillLineages();
      lineages.value = res.data || [];
      const first = lineages.value.find((l) => l.enabled);
      if (first) {
        lineage.value = first.code;
        await loadJobLines();
      }
    } catch {
      Message.error(t('clientSkill.msg.loadFail'));
    }
  };

  const onLineageChange = () => {
    loadJobLines();
  };

  const silentEnsureIcons = async (graphNodes: Node[]) => {
    const ids = graphNodes
      .map((n) => Number(n.data?.skillId || n.id))
      .filter((id) => id > 0);
    if (!ids.length) return;
    try {
      const res = await ensureSkillIcons(ids, false);
      const urls = res.data?.urls;
      if (!urls) return;
      nodes.value = nodes.value.map((n) => {
        const sid = Number(n.data?.skillId || n.id);
        const url = urls[String(sid)] || urls[sid as unknown as string];
        if (!url) return n;
        return {
          ...n,
          data: { ...n.data, iconUrl: url },
        };
      });
    } catch {
      // ignore: keep CDN/empty; SkillNode handles @error
    }
  };

  const loadBook = async () => {
    if (activeJobId.value == null) return;
    bookLoading.value = true;
    try {
      const res = await fetchSkillBook(activeJobId.value);
      const book = res.data;
      const nextNodes: Node[] = (book.skills || []).map((s) => ({
        id: String(s.skillId),
        type: 'skill',
        position: { x: 0, y: 0 },
        data: {
          label: s.name,
          skillId: s.skillId,
          iconUrl: s.iconUrl,
          invisible: s.invisible,
        },
      }));
      const nextEdges: Edge[] = (book.edges || []).map((e, idx) => ({
        id: `e-${e.fromSkillId}-${e.toSkillId}-${idx}`,
        source: String(e.fromSkillId),
        target: String(e.toSkillId),
        label: `Lv.${e.reqLevel}`,
        animated: !!e.external,
        style: e.external ? { strokeDasharray: '4 2' } : undefined,
      }));
      // ensure edge endpoints exist as nodes for dagre
      const idSet = new Set(nextNodes.map((n) => n.id));
      (book.edges || []).forEach((e) => {
        const from = String(e.fromSkillId);
        if (!idSet.has(from)) {
          idSet.add(from);
          nextNodes.push({
            id: from,
            type: 'skill',
            position: { x: 0, y: 0 },
            data: {
              label: from,
              skillId: e.fromSkillId,
              invisible: true,
            },
          });
        }
      });
      nodes.value = layoutGraph(nextNodes, nextEdges);
      edges.value = nextEdges;
      await nextTick();
      vfFitView({ padding: 0.2 });
      // 静默补图标：失败不弹窗，破图由节点占位处理
      silentEnsureIcons(nextNodes).catch(() => undefined);
    } catch {
      Message.error(t('clientSkill.msg.loadFail'));
    } finally {
      bookLoading.value = false;
    }
  };

  const onSelectLine = (key: string) => {
    selectedLineId.value = key;
    const line = jobLines.value.find((l) => l.lineId === key);
    if (line?.stages?.length) {
      activeJobId.value = line.stages[line.stages.length - 1].jobId;
      loadBook();
    }
  };

  const fitView = () => vfFitView({ padding: 0.2 });

  const onNodeClick = async ({ node }: { node: Node }) => {
    const skillId = Number(node.data?.skillId || node.id);
    if (!skillId) return;
    drawerVisible.value = true;
    detailLoading.value = true;
    try {
      const res = await fetchSkillDetail(skillId);
      detail.value = res.data;
      editForm.name = res.data.name || '';
      editForm.desc = res.data.desc || '';
      editForm.masterLevel = res.data.masterLevel;
      editForm.invisible = !!res.data.invisible;
      editForm.reqJson = JSON.stringify(res.data.req || {}, null, 2);
      editForm.levelsJson = JSON.stringify(res.data.levels || [], null, 2);
      editForm.hsJson = JSON.stringify(res.data.hs || {}, null, 2);
    } catch {
      Message.error(t('clientSkill.msg.loadFail'));
    } finally {
      detailLoading.value = false;
    }
  };

  const saveSkillData = async () => {
    if (!detail.value || !canEdit.value) {
      Message.warning(t('clientSkill.msg.editorRequired'));
      return;
    }
    saving.value = true;
    try {
      const req = JSON.parse(editForm.reqJson || '{}');
      const levels = JSON.parse(editForm.levelsJson || '[]');
      await writeSkill({
        skillId: detail.value.skillId,
        masterLevel: editForm.masterLevel,
        invisible: editForm.invisible,
        req,
        levels,
      });
      // 与保存联动：写盘后热重载内存，使游戏侧尽快读到新数值
      try {
        await reloadSkills();
        Message.success(t('clientSkill.msg.savedAndReloaded'));
      } catch {
        Message.success(t('clientSkill.msg.saved'));
        Message.warning(t('clientSkill.msg.reloadAfterSaveFail'));
      }
      await loadBook();
    } catch (e: any) {
      Message.error(e?.message || t('clientSkill.msg.loadFail'));
    } finally {
      saving.value = false;
    }
  };

  const saveStringData = async () => {
    if (!detail.value || !canEdit.value) {
      Message.warning(t('clientSkill.msg.editorRequired'));
      return;
    }
    saving.value = true;
    try {
      const hs = JSON.parse(editForm.hsJson || '{}');
      await writeSkillString({
        skillId: detail.value.skillId,
        name: editForm.name,
        desc: editForm.desc,
        hs,
      });
      Message.success(t('clientSkill.msg.saved'));
      await loadBook();
    } catch (e: any) {
      Message.error(e?.message || t('clientSkill.msg.loadFail'));
    } finally {
      saving.value = false;
    }
  };

  const runEnsureIcons = async () => {
    const ids = nodes.value
      .map((n) => Number(n.data?.skillId || n.id))
      .filter((id) => id > 0);
    if (!ids.length) return;
    iconLoading.value = true;
    try {
      await ensureSkillIcons(ids, false);
      await loadBook();
      Message.success(t('clientSkill.msg.saved'));
    } catch {
      Message.error(t('clientSkill.msg.loadFail'));
    } finally {
      iconLoading.value = false;
    }
  };

  const runReload = async () => {
    if (!status.value?.editorEnabled) {
      Message.warning(t('clientSkill.msg.editorRequired'));
      return;
    }
    reloading.value = true;
    try {
      const res = await reloadSkills();
      Message.success(res.data?.message || t('clientSkill.msg.reloaded'));
    } catch {
      Message.error(t('clientSkill.msg.loadFail'));
    } finally {
      reloading.value = false;
    }
  };

  const runPatch = async (dryRun: boolean) => {
    if (!dryRun && !status.value?.allowClientWrite) {
      Message.warning(t('clientSkill.msg.clientWriteRequired'));
      return;
    }
    patching.value = true;
    try {
      const jobIds = currentLine.value?.stages?.map((s) => s.jobId);
      const res = dryRun
        ? await patchSkillDryRun(jobIds)
        : await patchSkillApply(jobIds);
      const msg = res.data?.message || t('clientSkill.msg.patchDone');
      const warns = (res.data?.warnings || []).join('\n');
      Message.success(warns ? `${msg}\n${warns}` : msg);
    } catch {
      Message.error(t('clientSkill.msg.loadFail'));
    } finally {
      patching.value = false;
    }
  };

  const confirmApply = () => {
    Modal.confirm({
      title: t('clientSkill.patchApply'),
      content: t('clientSkill.confirm.apply'),
      onOk: () => runPatch(false),
    });
  };

  const goClientPath = () => router.push({ name: 'ClientPath' });
  const goAssetHub = () => router.push({ name: 'ClientAssetHub' });

  onMounted(async () => {
    await loadStatus();
    await loadLineages();
  });
</script>

<style scoped lang="less">
  .mb-alert {
    margin-bottom: 12px;
  }
  .skill-layout {
    display: flex;
    gap: 12px;
    margin-top: 12px;
    min-height: 640px;
  }
  .skill-sider {
    width: 260px;
    flex-shrink: 0;
  }
  .skill-main {
    flex: 1;
    min-width: 0;
  }
  .skill-toolbar {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    gap: 8px;
    margin-bottom: 8px;
  }
  .skill-canvas-wrap {
    height: 560px;
    border: 1px solid var(--color-border-2);
    border-radius: 4px;
    overflow: hidden;
  }
</style>
