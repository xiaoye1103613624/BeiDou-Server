package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * 【类型】UpdateAccountByUserDTO（class），包 `org.gms.model.dto`。
 */
@Data
public class UpdateAccountByUserDTO implements Serializable {
    private String oldPwd;
    private String newPwd;
    private String pin;
    private String pic;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private String nick;
    private String email;
    private Integer language;
}
