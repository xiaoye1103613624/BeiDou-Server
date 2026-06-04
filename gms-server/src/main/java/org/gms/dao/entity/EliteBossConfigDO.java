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
import java.util.Date;

/**
 * 【实体】EliteBossConfigDO（class），包 {@code org.gms.dao.entity}。
 *
 * 对应数据库表 elite_boss_config，存储精英BOSS（野外BOSS）配置信息。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_elite_boss_config")
public class EliteBossConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer mapId;

    private Integer bossId;

    private String bossName;

    private Integer companionBossId;

    private Integer bossTime;

    private String scriptName;

    private Integer enabled;

    private Date createTime;

    private Date updateTime;
}
