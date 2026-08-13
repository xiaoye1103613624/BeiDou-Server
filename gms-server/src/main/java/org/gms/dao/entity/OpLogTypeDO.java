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
 * 操作日志类型样式绑定实体。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_op_log_type")
public class OpLogTypeDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 操作类型码
     */
    private Integer opType;

    /**
     * 类型名称
     */
    private String name;

    /**
     * 公告标签(广播前缀, 如:兑换系统)
     */
    private String noticeTag;

    /**
     * 聊天样式(serverNotice type)
     */
    private Integer chatType;

    /**
     * 是否全服聊天广播
     */
    private Boolean broadcast;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
