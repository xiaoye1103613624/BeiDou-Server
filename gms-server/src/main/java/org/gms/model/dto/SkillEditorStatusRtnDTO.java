package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillEditorStatusRtnDTO {
    private Boolean editorEnabled;
    private Boolean allowClientWrite;
    private Boolean clientDataConfigured;
    private String clientDataPath;
    private Boolean clientEnConfigured;
    private String clientEnPath;
    private Boolean patcherAvailable;
    private String patcherPath;
    private String configEditorWrite;
    private String configClientWrite;
}
