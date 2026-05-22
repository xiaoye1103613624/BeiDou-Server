package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * 新增账号请求DTO
 * <p>用于接收前端提交的创建新账号的请求参数</p>
 */
@Data
public class AddAccountDTO implements Serializable {
    /** 账号名称 */
    private String name;
    /** 账号密码 */
    private String password;
    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    /** 语言偏好 */
    private Integer language;
}
