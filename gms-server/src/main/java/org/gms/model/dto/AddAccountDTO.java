package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * 新增账号请求参数
 * 封装账号创建时所需的用户名、密码、生日和语言设置
 */
@Data
public class AddAccountDTO implements Serializable {
    /** 账号名称 */
    private String name;
    /** 密码 */
    private String password;
    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    /** 语言设置 */
    private Integer language;
}