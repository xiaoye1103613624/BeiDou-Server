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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("activity_def")
public class ActivityDefDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.None)
    private String code;

    private String nameZh;

    private String nameEn;

    private String category;

    private Integer lobbyMapId;

    private Integer eventMapId;

    private String relatedMaps;

    private Integer teamEvent;

    private Integer supportsMapStart;

    private Integer enabled;

    private Integer sortOrder;

    private Integer defaultMaxPlayers;

    private String remark;
}
