package org.gms.model.dto;

import lombok.Data;

/**
 * 操作日志分页查询条件。
 */
@Data
public class OpLogSearchDTO {

    private Integer pageNo = 1;

    private Integer pageSize = 20;

    /**
     * 操作类型码(为空查全部)
     */
    private Integer opType;

    /**
     * 角色名(模糊)
     */
    private String characterName;

    /**
     * 账号ID
     */
    private Integer accountId;

    /**
     * 来源IP(模糊)
     */
    private String ip;

    /**
     * 起始时间 yyyy-MM-dd HH:mm:ss
     */
    private String startTime;

    /**
     * 结束时间 yyyy-MM-dd HH:mm:ss
     */
    private String endTime;

}