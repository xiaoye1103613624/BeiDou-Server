# 装备自定义属性系统

## 概述

装备自定义属性系统允许脚本通过服务端API为装备添加自定义词条信息，这些词条会被序列化发送给客户端，并在客户端插件中显示到装备tooltip的上方。

## 系统架构

### 三层架构
1. **数据库层** - `inventory_equipment.custom_properties` (LONGTEXT JSON格式)
2. **服务端层** - Character/InventoryService API和数据序列化
3. **客户端层** - 插件读取并显示自定义属性

## 脚本API

### 1. 设置自定义属性

```javascript
player.setEquipCustomAttr(position, key, value);
```

**参数说明：**
- `position` (short): 装备位置。负数表示穿戴位置(-11=武器, -12=盔甲等)，正数表示背包位置
- `key` (String): 属性键名，如"强化等级"、"锻造层数"
- `value` (Object): 属性值，可以是String、Number等任何序列化为JSON的对象

**示例：**
```javascript
// 给角色位置-11（武器位）添加强化等级属性
player.setEquipCustomAttr(-11, "强化等级", "10");

// 支持多个属性
player.setEquipCustomAttr(-11, "锻造层数", "5");
player.setEquipCustomAttr(-11, "品质等级", "传说");
```

### 2. 读取自定义属性

```javascript
var value = player.getEquipCustomAttr(position, key);
```

**返回值：** 属性值，不存在返回null

**示例：**
```javascript
var level = player.getEquipCustomAttr(-11, "强化等级");
if (level != null) {
    player.dropMessage(1, "当前强化等级：" + level);
}
```

### 3. 删除单个属性

```javascript
player.removeEquipCustomAttr(position, key);
```

### 4. 清空所有属性

```javascript
player.clearEquipCustomAttr(position);
```

## 应用场景示例

### 场景1：强化系统

```javascript
function action(mode, type, selection) {
    var player = rm.getPlayer();
    var equipPos = -11; // 武器位
    
    // 读取当前强化等级
    var currentLevel = player.getEquipCustomAttr(equipPos, "强化等级");
    currentLevel = currentLevel != null ? parseInt(currentLevel) : 0;
    
    // 强化逻辑...
    if (/* 强化成功 */) {
        currentLevel++;
        player.setEquipCustomAttr(equipPos, "强化等级", String(currentLevel));
        player.dropMessage(1, "强化成功！当前强化等级：" + currentLevel);
    }
}
```

### 场景2：锻造系统

```javascript
function action(mode, type, selection) {
    var player = rm.getPlayer();
    var equipPos = -11;
    
    // 设置多个锻造相关属性
    player.setEquipCustomAttr(equipPos, "锻造者", player.getName());
    player.setEquipCustomAttr(equipPos, "锻造时间", new Date().getTime());
    player.setEquipCustomAttr(equipPos, "锻造品质", "高级");
    
    player.dropMessage(1, "锻造完成！");
}
```

### 场景3：自定义品质系统

```javascript
function action(mode, type, selection) {
    var player = rm.getPlayer();
    var equipPos = -11;
    
    // 根据随机数设置品质等级
    var rarity = Math.floor(Math.random() * 5); // 0-4
    var qualityNames = ["普通", "优秀", "稀有", "传说", "神话"];
    var quality = qualityNames[rarity];
    
    player.setEquipCustomAttr(equipPos, "品质", quality);
    player.dropMessage(1, "装备品质：" + quality);
}
```

## 存储格式

自定义属性以JSON格式存储在数据库中：

```json
{
  "强化等级": "10",
  "锻造层数": "5",
  "品质等级": "传说"
}
```

- 当没有自定义属性时，该字段为NULL
- 属性键和值都存储为字符串或JSON基本类型
- 支持嵌套JSON对象（如需要更复杂的数据结构）

## 数据流向

```
脚本 API
  ↓
Character.setEquipCustomAttr()
  ↓
Equip.customProperties (内存)
  ↓
PacketCreator序列化 → 发送给客户端
  ↓
InventoryService保存 → 数据库

客户端插件
  ↓
读取Packet中的customProperties
  ↓
在tooltip上方显示
```

## 性能考虑

1. **存储空间** - JSON字符串存储在LONGTEXT中，支持任意大小
2. **序列化开销** - 轻微，仅在物品被发送给客户端时序列化
3. **查询性能** - 不影响，因为custom_properties是普通字段

## 兼容性

- ✅ 与现有装备系统完全兼容
- ✅ 不修改任何现有属性
- ✅ 向后兼容（没有custom_properties的装备继续工作）
- ✅ 可随时添加或移除自定义属性

## 客户端显示

在客户端ijl15插件中，自定义属性将显示在装备名称的上方。

示例展示：
```
【强化等级】10
【锻造层数】5
黑色羽毛衣  <- 装备名称
所有者：玩家名字  <- owner
```

## 常见问题

**Q: 可以添加多少个自定义属性？**
A: 理论上无限制，因为使用LONGTEXT字段。实际限制由JSON序列化库决定。

**Q: 自定义属性会被保存到数据库吗？**
A: 是的，会自动保存到`inventory_equipment.custom_properties`字段。

**Q: 装备丢弃/删除时自定义属性会怎样？**
A: 装备被删除时一起被删除。

**Q: 支持同步到同服其他角色吗？**
A: 不支持。自定义属性是per-equip的。如果需要跨角色共享，需要在脚本层面手动处理。

## 扩展建议

1. **内置品质系统** - 在客户端显示品质对应的颜色
2. **强化上限显示** - 显示强化是否已满
3. **过期时间** - 临时属性加上过期时间
4. **传承记录** - 保存装备的历史修改记录
