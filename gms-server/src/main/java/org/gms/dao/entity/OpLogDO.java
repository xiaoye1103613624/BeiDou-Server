package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_op_log")
public class OpLogDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 操作类型
     */
    private Integer opType;

    /**
     * 操作类型名称快照
     */
    private String opTypeName;

    /**
     * 角色ID
     */
    private Integer characterId;

    /**
     * 角色名
     */
    private String characterName;

    /**
     * 账号ID
     */
    private Integer accountId;

    /**
     * 摘要(聊天广播内容)
     */
    private String summary;

    /**
     * 完整详情(审计用)
     */
    private String detail;

    /**
     * 聊天样式快照(serverNotice type)
     */
    private Integer chatType;

    /**
     * 是否已广播
     */
    private Boolean broadcast;

    /**
     * 来源IP
     */
    private String ip;

    /**
     * 世界-频道
     */
    private String worldChannel;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
