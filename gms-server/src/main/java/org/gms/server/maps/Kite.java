package org.gms.server.maps;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.packet.Packet;
import org.gms.util.PacketCreator;

import java.awt.*;

/**
 * 【类型】Kite（class），包 `org.gms.server.maps`。
 * 
 * <p>风筝类，表示游戏中由玩家投放的风筝对象。
 * 风筝是一种特殊的地图对象，用于展示文本信息或其他效果，
 * 通常由特定道具或技能创建。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>表示地图上的风筝对象</li>
 *   <li>存储风筝的相关信息（所有者、文本、道具ID等）</li>
 *   <li>处理风筝的生成和销毁</li>
 * </ul>
 */
public class Kite extends AbstractMapObject {
    /** 风筝的位置 */
    private final Point pos;
    /** 风筝的所有者角色 */
    private final Character owner;
    /** 风筝显示的文本 */
    private final String text;
    /** 风筝的 foothold（立足点） */
    private final int ft;
    /** 创建风筝的道具ID */
    private final int itemid;

    /**
     * 构造函数：创建风筝实例
     * 
     * @param owner 风筝的所有者角色
     * @param text 风筝显示的文本
     * @param itemId 创建风筝的道具ID
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
     * 获取风筝的所有者
     * 
     * @return 风筝的所有者角色
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
     * 创建生成数据包
     * 
     * @return 风筝生成数据包
     */
    public final Packet makeSpawnData() {
        return PacketCreator.spawnKite(getObjectId(), itemid, owner.getName(), text, pos, ft);
    }

    /**
     * 创建销毁数据包
     * 
     * @return 风筝销毁数据包
     */
    public final Packet makeDestroyData() {
        return PacketCreator.removeKite(getObjectId(), 0);
    }
}