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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_icon_cache")
public class GameIconDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;
    /** item / npc / mob */
    private String category;
    private Integer objectId;
    /** 来源版本号（小册子 / maplestory.io） */
    private Integer version;
    private String region;
    private byte[] iconData;
    private String contentType;
    /** maplestory.io / dvg / local / legacy */
    private String source;
    private Date updateTime;
}
