package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色天赋等级持久化（xy_character_talent）。
 */
@Table("xy_character_talent")
public class CharacterTalentDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer characterId;
    private Integer talentId;
    private Integer level;

    public CharacterTalentDO() {
    }

    public CharacterTalentDO(Integer characterId, Integer talentId, Integer level) {
        this.characterId = characterId;
        this.talentId = talentId;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Integer characterId) {
        this.characterId = characterId;
    }

    public Integer getTalentId() {
        return talentId;
    }

    public void setTalentId(Integer talentId) {
        this.talentId = talentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
