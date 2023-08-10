package com.rlax.corebin.tools.gitlabclone.model;

import lombok.Data;

/**
 * @author Rlax
 * @date 2023/08/08
 */
@Data
public class GitBranch {
    String name;
    GitBranchCommit commit;
}
