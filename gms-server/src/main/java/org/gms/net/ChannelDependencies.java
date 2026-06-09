package org.gms.net;

import org.gms.client.processor.npc.FredrickProcessor;
import org.gms.service.NoteService;

import java.util.Objects;

/**
 * 频道依赖关系记录
 * 封装频道处理器所需的依赖服务，包括便签服务和Fredrick处理器
 *
 * @param noteService         便签服务
 * @param fredrickProcessor   Fredrick NPC处理器
 */
public record ChannelDependencies(NoteService noteService, FredrickProcessor fredrickProcessor) {

    public ChannelDependencies {
        Objects.requireNonNull(noteService);
        Objects.requireNonNull(fredrickProcessor);
    }
}