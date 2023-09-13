package com.rlax.corebin.tools.gitlabclone.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author Rlax
 * @date 2023/08/08
 */
@Data
@Builder
public class GitGroup {
    Long id;
    String name;
    String path;
    String description;
}
