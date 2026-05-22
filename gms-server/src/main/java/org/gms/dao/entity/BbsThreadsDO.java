package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 【实体】BbsThreadsDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 bbs_threads，存储论坛主题数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("bbs_threads")
public class BbsThreadsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long threadid;

    private Long postercid;

    private String name;

    private BigInteger timestamp;

    private Integer icon;

    private Integer replycount;

    private String startpost;

    private Long guildid;

    private Long localthreadid;

}
