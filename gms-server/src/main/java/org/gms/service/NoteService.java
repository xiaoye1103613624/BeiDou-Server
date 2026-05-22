package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.dao.entity.NotesDO;
import org.gms.dao.mapper.NotesMapper;
import org.gms.net.packet.out.ShowNotesPacket;
import org.gms.net.server.Server;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.gms.dao.entity.table.NotesDOTableDef.NOTES_D_O;

/**
 * 【业务服务】NoteService：封装游戏内「便条/小纸条」的发送、展示与删除业务逻辑。
 *
 * <p>通过 {@link NotesMapper} 读写 {@code notes} 表，支持普通便条与带「人气 +1」标记的便条。
 * 收件人上线或打开便条列表时由 {@link #show(Character)} 查询未删除记录并下发 {@link ShowNotesPacket}。</p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class NoteService {
    private final NotesMapper notesMapper;

    /**
     * 发送普通便条：仅包含正文与收发件人，不带人气奖励标记。
     *
     * @param message      便条正文
     * @param senderName   发件人角色名
     * @param receiverName 收件人角色名
     */
    public void sendNormal(String message, String senderName, String receiverName) {
        notesMapper.insertSelective(NotesDO.builder()
                .message(message)
                .from(senderName)
                .to(receiverName)
                .timestamp(Server.getInstance().getCurrentTime())
                .build());
    }

    /**
     * 发送带人气奖励的便条：除正文外设置 fame 标记（客户端读取后为人气 +1）。
     *
     * @param message      便条正文
     * @param senderName   发件人角色名
     * @param receiverName 收件人角色名
     */
    public void sendWithFame(String message, String senderName, String receiverName) {
        notesMapper.insertSelective(NotesDO.builder()
                .message(message)
                .from(senderName)
                .to(receiverName)
                .timestamp(Server.getInstance().getCurrentTime())
                .fame(1)
                .build());
    }

    /**
     * 向客户端展示当前角色未删除的便条列表。
     *
     * <p>查询条件：{@code deleted = 0} 且收件人 {@code to} 等于角色名；有数据则通过 {@link ShowNotesPacket} 下发。</p>
     *
     * @param chr 收件人角色，不可为 null
     * @throws IllegalArgumentException 当 {@code chr} 为 null
     */
    public void show(Character chr) {
        if (chr == null) {
            throw new IllegalArgumentException("Unable to show notes - chr is null");
        }

        List<NotesDO> notesDOList = notesMapper.selectListByQuery(QueryWrapper.create()
                .from(NOTES_D_O)
                .where(NOTES_D_O.DELETED.eq(0))
                .and(NOTES_D_O.TO.eq(chr.getName())));
        if (!notesDOList.isEmpty()) {
            chr.sendPacket(new ShowNotesPacket(notesDOList));
        }
    }

    /**
     * 按主键物理删除一条便条，通常在玩家读完后丢弃时调用。
     *
     * @param noteId 便条数据库主键
     * @return 删除前查询到的实体；若查询或删除异常则返回 {@link Optional#empty()}
     */
    public Optional<NotesDO> delete(int noteId) {
        try {
            NotesDO notesDO = notesMapper.selectOneById(noteId);
            notesMapper.deleteById(noteId);
            return Optional.of(notesDO);
        } catch (Exception e) {
            log.error("Failed to discard note with id {}", noteId, e);
            return Optional.empty();
        }
    }

}
