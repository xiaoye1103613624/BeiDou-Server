package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 【实体】FlywaySchemaHistoryDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 flyway_schema_history，存储Flyway数据库迁移历史数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("flyway_schema_history")
public class FlywaySchemaHistoryDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer installedRank;

    private String version;

    private String description;

    private String type;

    private String script;

    private Integer checksum;

    private String installedBy;

    private Timestamp installedOn;

    private Integer executionTime;

    private Boolean success;

}
