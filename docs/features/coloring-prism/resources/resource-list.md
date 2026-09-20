# 资源清单

| 资源 | 路径 | 状态 |
| --- | --- | --- |
| UI 合并目标 | `E:\MXD\BeiDou-Client_S9\Data\UI\UIWindow.img` → `ColorPrism/*` | 已写入（含 LayerBt） |
| UI 备份 | `UIWindow.img.bak_before_colorprism_20260917` | 覆盖前 |
| UI 中间产物 | `UIWindow.img.patched_colorprism` | save_as 输出 |
| 独立 ColorPrism | `Data\UI\ColorPrism.img.unused_standalone` | 已迁出（窗口不读） |
| 道具 | `Data\Item\Cash\0578.img` / `05782000` | 已有 icon+cash |
| 中文 String | `Data\String\Cash.img` / `5782000` = 七彩棱镜 | 已对齐 |
| 英文 String | `EN\String\Cash.img` / `5782000` | 已创建/对齐 |
| String 备份 | `*.bak_before_5782000_20260917` | Data + EN |
| 服务端 EN XML | `gms-server/wz/String.wz/Cash.img.xml` 5782000 | 英文 |
| 服务端 ZH XML | `gms-server/wz-zh-CN/String.wz/Cash.img.xml` 5782000 | 中文 |
| 服务端 Item | `wz` + `wz-zh-CN` `Item.wz/Cash/0578.img.xml` | 存在 |
| 上游包 | `E:\MXD\扩展改动\装备染色\coloring-prism` | 源 |
| orange-wz 密钥 | `083-GMS` / IV `TSPHKw==` | 本机工具，勿论坛贴全文 userKey |
