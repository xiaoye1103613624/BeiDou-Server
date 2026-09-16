package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 完成态查询。mode: never(默认未接取占位说明) / incomplete / completed / all
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuestProgressReqDTO extends BasePageDTO {
    private Integer questId;
    private Integer characterId;
    private String characterName;
    /**
     * never | incomplete | completed | all；默认 never 仅用于前端语义，
     * 列表仍查 queststatus，never 表示过滤 status=0 或不存在由前端对照。
     */
    private String mode;
    private Integer status;
}
