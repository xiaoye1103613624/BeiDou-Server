package org.gms.net;

import org.gms.client.processor.npc.FredrickProcessor;
import org.gms.service.NoteService;

import java.util.Objects;

/**
 * 网络层类型「ChannelDependencies」。
 * 位于 `org.gms.net`，参与客户端会话、封包路由或服务器间协作。
 */
public record ChannelDependencies(NoteService noteService, FredrickProcessor fredrickProcessor) {

    public ChannelDependencies {
        Objects.requireNonNull(noteService);
        Objects.requireNonNull(fredrickProcessor);
    }
}
