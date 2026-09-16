package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Data
public class FileTreeNodeDTO {
    private String title;
    private String key;
    /** 相对 user.dir 的路径，使用 / 分隔 */
    private String path;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FileTreeNodeDTO> children;
    @JsonProperty("isLeaf")
    private boolean leaf;

    public FileTreeNodeDTO(File file, String key, Path baseDir) {
        this.title = file.getName();
        this.key = key;
        Path absolute = file.toPath().toAbsolutePath().normalize();
        this.path = baseDir.relativize(absolute).toString().replace('\\', '/');
        this.children = file.isDirectory() ? Collections.emptyList() : null;
        this.leaf = !file.isDirectory();
    }
}
