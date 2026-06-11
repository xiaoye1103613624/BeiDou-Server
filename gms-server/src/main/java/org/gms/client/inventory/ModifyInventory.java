package org.gms.client.inventory;

/**
 * 背包变更记录
 * 封装物品变更信息（增加/更新/移除），用于构建客户端同步包
 * 记录变更模式、物品深拷贝和旧位置，支持背包增量更新
 *
 * @author kevin
 */
public class ModifyInventory {

    /** 变更模式（0=增加，1=更新，2=移除） */
    private final int mode;
    /** 变更涉及的物品（深拷贝） */
    private Item item;
    /** 物品旧槽位 */
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

    /**
     * 清除物品引用（帮助GC回收）
     */
    public final void clear() {
        this.item = null;
    }
}