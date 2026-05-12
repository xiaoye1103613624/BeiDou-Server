package org.gms.model.dto;

import lombok.*;

import java.util.List;

/**
 * 【类型】ChrOnlineListRtnDTO（class），包 `org.gms.model.dto`。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChrOnlineListRtnDTO {
    private int world;
    private int id;
    private String name;
    private int map;
    private int job;
    private String jobName;
    private int level;
    private int gm;

}
