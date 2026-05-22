package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * GM修改账号信息请求DTO
 * <p>用于GM后台修改玩家账号属性</p>
 */
@Data
public class UpdateAccountByGmDTO implements Serializable {
    /** 新密码 */
    private String newPwd;
    /** PIN码 */
    private String pin;
    /** PIC码 */
    private String pic;
    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    /** NX点券余额 */
    @Column("nxCredit")
    private Integer nxCredit;
    /** 枫币点数 */
    @Column("maplePoint")
    private Integer maplePoint;
    /** NX预付点数 */
    @Column("nxPrepaid")
    private Integer nxPrepaid;
    /** 角色槽位数 */
    private Integer characterslots;
    /** 性别 */
    private Integer gender;
    /** 是否为网站管理员 */
    private Integer webadmin;
    /** 昵称 */
    private String nick;
    /** 是否禁言 */
    private Integer mute;
    /** 邮箱地址 */
    private String email;
    /** 奖励点数 */
    private Integer rewardpoints;
    /** 投票点数 */
    private Integer votepoints;
    /** 语言偏好 */
    private Integer language;
}
