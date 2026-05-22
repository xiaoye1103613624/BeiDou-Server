package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * 用户自助修改账号信息请求DTO
 * <p>用于玩家自助修改个人账号属性</p>
 */
@Data
public class UpdateAccountByUserDTO implements Serializable {
    /** 原密码（用于验证身份） */
    private String oldPwd;
    /** 新密码 */
    private String newPwd;
    /** PIN码 */
    private String pin;
    /** PIC码 */
    private String pic;
    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    /** 昵称 */
    private String nick;
    /** 邮箱地址 */
    private String email;
    /** 语言偏好 */
    private Integer language;
}
