package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 单只宠物的自定义成长经验进度（按 pets.petid）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_pet_growth_progress")
public class PetGrowthProgressDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long petid;

    private Integer growthExp;

    private Date updateTime;
}
