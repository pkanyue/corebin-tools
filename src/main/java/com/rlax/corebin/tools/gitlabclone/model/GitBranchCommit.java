package com.rlax.corebin.tools.gitlabclone.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Rlax
 * @date 2023/08/08
 */
@Data
public class GitBranchCommit {
    String id;
    @JsonProperty(value = "committed_date")
    Date committedDate;
}
