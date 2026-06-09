package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * 用户端修改账号请求参数
 * 封装用户自行修改密码、个人信息等操作
 */
@Data
public class UpdateAccountByUserDTO implements Serializable {
    /** 旧密码 */
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
    /** 邮箱 */
    private String email;
    /** 语言设置 */
    private Integer language;
}