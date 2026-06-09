package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * GM端修改账号请求参数
 * 封装GM后台修改账号信息的所有可修改字段
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
    /** NX信用点 */
    @Column("nxCredit")
    private Integer nxCredit;
    /** 冒险币 */
    @Column("maplePoint")
    private Integer maplePoint;
    /** NX预付点 */
    @Column("nxPrepaid")
    private Integer nxPrepaid;
    /** 角色槽位数量 */
    private Integer characterslots;
    /** 性别 */
    private Integer gender;
    /** 是否为Web管理员 */
    private Integer webadmin;
    /** 昵称 */
    private String nick;
    /** 禁言状态 */
    private Integer mute;
    /** 邮箱 */
    private String email;
    /** 奖励点数 */
    private Integer rewardpoints;
    /** 投票点数 */
    private Integer votepoints;
    private Integer language;
}