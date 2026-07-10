"""
北斗 GMS083 高版本现金外观装备导入 — 默认路径配置

用户需从高版本客户端用 HaRepacker 导出 XML，填入 SOURCE_DIR / REFERENCE_DIR。
"""
from pathlib import Path

# 北斗工程根目录（gms-server 上一级）
BEIDOU_ROOT = Path(__file__).resolve().parents[2]
GMS_SERVER = BEIDOU_ROOT

# 服务端已有装备（跳过重复导入）
SKIP_DIR = GMS_SERVER / "wz" / "Character.wz"

# 脚本输出目录（处理结果，确认后再 --deploy）
OUTPUT_DIR = Path(__file__).resolve().parent / "output"

# String 双端
STRINGS_TARGET_EN = GMS_SERVER / "wz" / "String.wz" / "Eqp.img.xml"
STRINGS_TARGET_ZH = GMS_SERVER / "wz-zh-CN" / "String.wz" / "Eqp.img.xml"

# 高版本 String 源（名称/描述）；用户可替换为自己的导出
STRINGS_SOURCE_EN = Path(r"E:\资料\xiaoye\mxd学习\导入高版本现金物品\SOURCE_Eqp.img.xml")
# 无独立中文源时，脚本会用英文或占位中文
STRINGS_SOURCE_ZH = STRINGS_SOURCE_EN

# ---------------------------------------------------------------------------
# 用户必须提供的高版本导出路径（按装备类型分子目录）
# ---------------------------------------------------------------------------
# SOURCE_DIR: 高版本 Character.wz/{Type}/*.img.xml（含坐标、动作帧；可有 _outlink）
# 推荐：079 整合包已导出的 XML
SOURCE_DIR_DEFAULT = Path(r"E:\mxd_soft\079\wz\Character.wz")

# REFERENCE_DIR: 同目录或 _Canvas 子目录，含 canvas bytedata
# 若 SOURCE 已内嵌 bytedata，可与 SOURCE_DIR 相同
REFERENCE_DIR_DEFAULT = SOURCE_DIR_DEFAULT

# 客户端散 img 热更目录（Case C：仅 APPEND，不写 PKG1）
CLIENT_DATA_ROOT = Path(
    r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data"
)

# 079 wz 目录通常只有结构 XML（无 bytedata）；二进制 .img 在 079 客户端 Data 里
# 按优先级查找已含 icon/sprite 的二进制 .img 复制到 BeiDou-Client_1
CLIENT_IMG_FALLBACK_ROOTS = [
    Path(r"E:\mxd_soft\2.客户端\083\079整合data"),
    Path(r"E:\mxd_soft\2.客户端\083\20大陆_079整合版\Data_fresh"),
    Path(r"E:\mxd_soft\2.客户端\083\BeiDou-Client\Data"),
    Path(r"E:\mxd_soft\2.客户端\083\BeiDou-ClientV16.1\BeiDou-Client\Data"),
    Path(r"E:\mxd_soft\2.客户端\083\20大陆_079整合版\20dalu\Data_fresh"),
]

# orange-wz 单文件 Xml→Img
ORANGE_WZ_MYXML2IMG = Path(r"E:\pro\orange-wz\target\classes")

# v083 不兼容节点（导入时删除）
V083_STRIP_NODE_NAMES = frozenset({
    "potential",
    "starForce",
    "setEffect",
    "equipTradeBlock",
    "setItemID",
    "exGradeOption",
    "charmEXP",
    "bossReward",
    "exItem",
    "durability",
    "exchanger",
    "jokerToSetItem",
    "jokerToSetItemID",
})

# 装备类型 → String.wz Eqp 子分类名
ITEM_TYPE_TO_STRING_CATEGORY = {
    "Cap": "Cap",
    "Cape": "Cape",
    "Coat": "Coat",
    "Longcoat": "Longcoat",
    "Pants": "Pants",
    "Shoes": "Shoes",
    "Glove": "Glove",
    "Shield": "Shield",
    "Ring": "Ring",
    "Accessory": "Accessory",
    "Face": "Face",
    "Hair": "Hair",
    "Weapon": "Weapon",
}
