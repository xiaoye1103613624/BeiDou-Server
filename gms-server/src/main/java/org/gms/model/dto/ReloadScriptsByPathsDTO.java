package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReloadScriptsByPathsDTO {
    /** 相对 user.dir 的路径，如 scripts-zh-CN/npc/xxx.js */
    private List<String> paths;
}
