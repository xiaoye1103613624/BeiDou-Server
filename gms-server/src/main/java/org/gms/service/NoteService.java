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
 * 便签服务类，提供便签的发送、查看和删除功能。
 */
@Service
@AllArgsConstructor
@Slf4j
public class NoteService {

    /**
     * 便签数据访问对象
     */
    private final NotesMapper notesMapper;

    /**
     * 发送普通便签（从一个角色发送给另一个角色）。
     *
     * @param message      便签内容
     * @param senderName   发送者名称
     * @param receiverName 接收者名称
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
     * 发送带声望的便签（接收者声望+1）。
     *
     * @param message      便签内容
     * @param senderName   发送者名称
     * @param receiverName 接收者名称
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
     * 向角色展示未读便签。
     *
     * @param chr 便签接收者角色
     * @throws IllegalArgumentException 如果角色为null
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
     * 删除已读便签。
     *
     * @param noteId 要删除的便签ID
     * @return 被删除的便签对象，如果删除失败则返回空的Optional
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