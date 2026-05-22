package org.gms.client.inventory;

/**
 * 【类型】ModifyInventory（class），包 {@code org.gms.client.inventory}。
 *
 * 库存变更操作的数据载体，记录变更模式（新增/更新/移动/删除）、目标物品、旧位置等信息，
 * 用于组装客户端库存刷新包。
 *
 * @author kevin
 */
public class ModifyInventory {

    private final int mode;
    private Item item;
    private short oldPos;

    public ModifyInventory(final int mode, final Item item) {
        this.mode = mode;
        this.item = item.copy();
    }

    public ModifyInventory(final int mode, final Item item, final short oldPos) {
        this.mode = mode;
        this.item = item.copy();
        this.oldPos = oldPos;
    }

    public final int getMode() {
        return mode;
    }

    public final int getInventoryType() {
        return item.getInventoryType().getType();
    }

    public final short getPosition() {
        return item.getPosition();
    }

    public final short getOldPosition() {
        return oldPos;
    }

    public final short getQuantity() {
        return item.getQuantity();
    }

    public final Item getItem() {
        return item;
    }

    public final void clear() {
        this.item = null;
    }
}