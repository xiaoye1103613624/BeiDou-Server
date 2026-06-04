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
@Table("xy_collection_progress")
public class XyCollectionProgressDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer characterId;

    private Long typeId;

    private Long stageId;

    private Integer itemId;

    private Integer collectedCount;

    private Integer stageCompleted;

    private Integer typeCompleted;

    private Date createTime;

    private Date updateTime;
}
