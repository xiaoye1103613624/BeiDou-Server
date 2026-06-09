package org.gms.server.maps;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.packet.Packet;
import org.gms.util.PacketCreator;

import java.awt.*;

/**
 * 风筝
 * 玩家放风筝的地图对象，显示文字消息和物品外观
 */
public class Kite extends AbstractMapObject {
    /** 风筝位置 */
    private final Point pos;
    /** 风筝所属玩家 */
    private final Character owner;
    /** 风筝显示的文字 */
    private final String text;
    /** 风筝所在的站脚点 */
    private final int ft;
    /** 风筝关联的物品ID */
    private final int itemid;

    /**
     * 构造方法，记录玩家的位置、站脚点和文字
     *
     * @param owner  风筝所属玩家
     * @param text   风筝显示的文字
     * @param itemId 风筝关联的物品ID
     */
    public Kite(Character owner, String text, int itemId) {
        this.owner = owner;
        this.pos = owner.getPosition();
        this.ft = owner.getFh();
        this.text = text;
        this.itemid = itemId;
    }

    @Override
    public MapObjectType getType() {
        return MapObjectType.KITE;
    }

    @Override
    public Point getPosition() {
        return pos.getLocation();
    }

    /**
     * 获取风筝所属玩家
     */
    public Character getOwner() {
        return owner;
    }

    @Override
    public void setPosition(Point position) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(makeDestroyData());
    }

    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(makeSpawnData());
    }

    /**
     * 生成风筝生成数据包
     *
     * @return 风筝生成网络包
     */
    public final Packet makeSpawnData() {
        return PacketCreator.spawnKite(getObjectId(), itemid, owner.getName(), text, pos, ft);
    }

    /**
     * 生成风筝销毁数据包
     *
     * @return 风筝销毁网络包
     */
    public final Packet makeDestroyData() {
        return PacketCreator.removeKite(getObjectId(), 0);
    }
}