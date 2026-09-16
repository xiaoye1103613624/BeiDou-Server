export default {
  'clientAssetHub.page.desc':
    '统一将图标补齐到服务端 static/game-assets。各业务页只展示本地资源，不再各自拉图。',
  'clientAssetHub.hint':
    '解析顺序：本地 game-assets → 客户端旁已有 PNG → maplestory.io → 小册子镜像。功能菜单里的图标同步已收敛到本页。',
  'clientAssetHub.info.root': '落盘目录',
  'clientAssetHub.info.clientPath': '客户端 Data',
  'clientAssetHub.info.clientPath.empty': '未配置（可到「客户端路径」设置）',
  'clientAssetHub.info.providers': '远程源',
  'clientAssetHub.off': '关',
  'clientAssetHub.refreshInfo': '刷新状态',
  'clientAssetHub.section.ensure': '补齐 / 刷新图标',
  'clientAssetHub.form.category': '分类',
  'clientAssetHub.form.idFrom': '起始 ID',
  'clientAssetHub.form.idTo': '结束 ID',
  'clientAssetHub.form.fromCatalog': '从新商城商品表取 ID（无区间时）',
  'clientAssetHub.ensureMissing': '补齐缺失',
  'clientAssetHub.ensureForce': '强制刷新',
  'clientAssetHub.ensureMissing.confirm':
    '仅为本地缺失的 ID 下载并写入 game-assets？',
  'clientAssetHub.ensureForce.confirm': '强制覆盖本地 PNG 并重新拉取？',
  'clientAssetHub.ensure.hint':
    '大范围补齐可能较慢；请先配置客户端路径（可选）并保持网络可用。',
  'clientAssetHub.msg.needTarget': '请勾选「从新商城商品表」或填写 ID 区间',
  'clientAssetHub.msg.done': '资源同步完成',
  'clientAssetHub.msg.fail': '资源同步失败',
  'clientAssetHub.summary':
    '请求 {requested} · 写入 {cached} · 跳过 {skipped} · 失败 {failed} · {seconds}s',
};
