/**北斗刷道具
 * 支持可配置数量
 * 修复：装备类不可堆叠物品改为逐件发放，防止多数量时只给1件
 * 修复：校验道具ID是否存在，防止无效ID导致客户端闪退


---By hanmburger*/
var status;
var targetItemId = 0;

// 判断是否为装备类物品（不可堆叠）
// 冒险岛物品ID规则：1xxxxxx 为装备类
function isEquipment(itemId) {
    return itemId >= 1000000 && itemId < 2000000;
}

//Start
function start()
{
  status = -1;
  action(1, 0, 0);
}

function action(mode, type, selection)
{
	if (CheckStatus(mode))
	{
	    if (status == 0)
	    {
			// 第一层：输入道具ID
			cm.sendGetNumber("请输入道具ID", 0, 0, 99999999);
	    }
		else if (status == 1)
		{
			// 保存道具ID，进入数量输入
			targetItemId = selection;
			cm.sendGetNumber("请输入数量（默认1）", 1, 1, 9999);
		}
		else if (status == 2)
		{
			// 执行刷道具
			var qty = selection;
			// 先校验道具ID是否存在，防止客户端闪退
			if (!cm.itemExists(targetItemId)) {
				cm.sendOk("道具ID #b" + targetItemId + "#k 不存在，请确认后重新输入！");
				cm.dispose();
				return;
			}
			if (qty > 0)
			{
				if (isEquipment(targetItemId)) {
					// 装备类物品不可堆叠，需逐件发放
					for (var i = 0; i < qty; i++) {
						cm.gainItem(targetItemId, 1);
					}
				} else {
					// 可堆叠物品（消耗品/其他等）直接批量发放
					cm.gainItem(targetItemId, qty);
				}
				var text = "成功获得！" + "#i" + targetItemId + "# ×" + qty;
				cm.sendOk(text);
			}
			else
			{
				cm.sendOk("数量无效！");
			}
			cm.dispose();
		}
		else
		{
			//最后一层对话完继续循环至此，退出结束
			cm.dispose();
		}
	}

}

function CheckStatus(mode)
{
	if (mode == -1)
	{
		cm.dispose();//点击了取消，停止，结束
		return false;
	}

	if (mode == 1)
	{
		status++;
	}
	else
	{
		status--;
	}

	if (status == -1)
	{
		cm.dispose();//防止第一层对话带有上一项或者取消按钮而产生bug。
		return false;
	}
	return true;
}