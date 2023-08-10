package com.rlax.corebin.tools.gitlabclone.model;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Rlax
 * @date 2023/08/08
 */
@Data
@Builder
public class GitProject {
    Long id;
    String name;
    String description;
    @JsonProperty(value = "default_branch")
    String defaultBranch;
    @JsonProperty(value = "ssh_url_to_repo")
    String sshUrlToRepo;
    @JsonProperty(value = "http_url_to_repo")
    String httpUrlToRepo;
    @JsonProperty(value = "path_with_namespace")
    String pathWithNamespace;
    @JsonProperty(value = "created_at")
    @JsonFormat(pattern = DatePattern.UTC_MS_WITH_XXX_OFFSET_PATTERN, timezone = "GMT+8")
    LocalDateTime createdAt;
    @JsonProperty(value = "last_activity_at")
    @JsonFormat(pattern = DatePattern.UTC_MS_WITH_XXX_OFFSET_PATTERN, timezone = "GMT+8")
    LocalDateTime lastActivityAt;
}
