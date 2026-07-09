# merge_beauty_img

Case C 美容院 WZ -> 客户端 Data/*.img 的 MapleLib 合并小工具。

## 构建

dotnet build merge_beauty_img/MergeBeautyImg.csproj -c Release

依赖 WzImg MCP 同路径的 MapleLib.csproj。

## 用法

MergeBeautyImg copy-file <src> <dst>
MergeBeautyImg merge-child <srcImg> <dstImg> <childName>
MergeBeautyImg merge-sounds <srcUI.img> <dstUI.img> <soundLeaf> ...

也可使用 merge_beauty_img.py 包装 dotnet run。
