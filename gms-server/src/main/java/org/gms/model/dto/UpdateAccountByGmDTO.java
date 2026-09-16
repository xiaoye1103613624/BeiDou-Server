package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
public class UpdateAccountByGmDTO implements Serializable {
    private String newPwd;
    private String pin;
    private String pic;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    @Column("nxCredit")
    private Integer nxCredit;
    @Column("maplePoint")
    private Integer maplePoint;
    @Column("nxPrepaid")
    private Integer nxPrepaid;
    private Integer characterslots;
    private Integer gender;
    private Integer webadmin;
    /** 后台角色编码：admin / operator */
    private String roleCode;
    private String nick;
    private Integer mute;
    private String email;
    private Integer rewardpoints;
    private Integer votepoints;
    private Integer language;
}
