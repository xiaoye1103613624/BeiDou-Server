package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 【实体】GuildsDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 guilds，存储公会信息数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("guilds")
public class GuildsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long guildid;

    private Long leader;

    private Long gp;

    private Long logo;

    @Column("logoColor")
    private Integer logoColor;

    private String name;

    private String rank1title;

    private String rank2title;

    private String rank3title;

    private String rank4title;

    private String rank5title;

    private Long capacity;

    @Column("logoBG")
    private Long logoBG;

    @Column("logoBGColor")
    private Integer logoBGColor;

    private String notice;

    private Integer signature;

    @Column("allianceId")
    private Long allianceId;

}
