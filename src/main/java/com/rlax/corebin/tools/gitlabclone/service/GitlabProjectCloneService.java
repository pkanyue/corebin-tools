package com.rlax.corebin.tools.gitlabclone.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.rlax.corebin.json.util.JsonUtil;
import com.rlax.corebin.tools.gitlabclone.model.GitBranch;
import com.rlax.corebin.tools.gitlabclone.model.GitGroup;
import com.rlax.corebin.tools.gitlabclone.model.GitProject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * @author Rlax
 * @date 2023/08/08
 */
@Slf4j
@AllArgsConstructor
public class GitlabProjectCloneService {

    private final String gitlabUrl;
    private final String privateToken;

    /**
     * 获取所有项目
     *
     */
    public List<GitProject> getAllProjects() {
        String url = gitlabUrl + "/api/v4/projects";
        HttpResponse response = HttpUtil.createGet(url)
                .form("per_page", "100")
                .form("private_token", privateToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .execute();

        if (!response.isOk()) {
            log.error(response.body());
            return Collections.emptyList();
        }

        return JsonUtil.parseArray(response.body(), GitProject.class);
    }

    /**
     * 获取子分组
     *
     */
    public List<GitGroup> getSubGroups(Long parentGroupId) {
        String url = StrUtil.format("{}/api/v4/groups/{}/subgroups", gitlabUrl, parentGroupId);
        HttpResponse response = HttpUtil.createGet(url)
                .form("per_page", "100")
                .form("private_token", privateToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .execute();

        if (!response.isOk()) {
            log.error(response.body());
            return Collections.emptyList();
        }

        return JsonUtil.parseArray(response.body(), GitGroup.class);
    }

    /**
     * 获取指定分组下的项目
     *
     */
    public List<GitProject> getProjectsByGroup(Long groupId) {
        String url = StrUtil.format("{}/api/v4/groups/{}/projects", gitlabUrl, groupId);
        HttpResponse response = HttpUtil.createGet(url)
                .form("per_page", "1000")
                .form("private_token", privateToken)
                .form("id", groupId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .execute();

        if (!response.isOk()) {
            log.error(response.body());
            return Collections.emptyList();
        }

        return JsonUtil.parseArray(response.body(), GitProject.class);
    }

    /**
     * 获取分组列表
     */
    public List<GitGroup> getGroups() {
        String url = gitlabUrl + "/api/v4/groups";
        HttpResponse response = HttpUtil.createGet(url)
                .form("private_token", privateToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .execute();

        if (!response.isOk()) {
            log.error(response.body());
            return Collections.emptyList();
        }

        return JsonUtil.parseArray(response.body(), GitGroup.class);
    }

    /**
     * 获取最近修改的分支名称
     *
     * @param projectId 项目ID
     */
    public String getLastActivityBranchName(Long projectId) {
        List<GitBranch> branches = getBranches(projectId);
        if (CollectionUtils.isEmpty(branches)) {
            return "";
        }
        GitBranch gitBranch = getLastActivityBranch(branches);
        return gitBranch.getName();
    }

    /**
     * 获取指定项目的分支列表
     * https://docs.gitlab.com/ee/api/branches.html#branches-api
     *
     * @param projectId 项目ID
     * @return
     */
    public List<GitBranch> getBranches(Long projectId) {
        String url = gitlabUrl + "/api/v3/projects/{projectId}/repository/branches?private_token={privateToken}";
        HttpResponse response = HttpUtil.createGet(url)
                .form("private_token", privateToken)
                .form("projectId", projectId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .execute();

        if (!response.isOk()) {
            log.error(response.body());
            return Collections.emptyList();
        }

        return JsonUtil.parseArray(response.body(), GitBranch.class);
    }

    /**
     * 获取最近修改的分支
     *
     * @param gitBranches 分支列表
     */
    public GitBranch getLastActivityBranch(List<GitBranch> gitBranches) {
        GitBranch lastActivityBranch = gitBranches.get(0);
        for (GitBranch gitBranch : gitBranches) {
            if (gitBranch.getCommit().getCommittedDate().getTime() > lastActivityBranch.getCommit().getCommittedDate().getTime()) {
                lastActivityBranch = gitBranch;
            }
        }
        return lastActivityBranch;
    }

    public void clone(String branchName, GitProject gitProject, File execDir) {
        String command = String.format("git clone -b %s %s %s", branchName, gitProject.getHttpUrlToRepo(), gitProject.getName());
        log.info("=== start exec command : {} ====", command);
        try {
            Process exec = Runtime.getRuntime().exec(command, null, execDir);
            exec.waitFor();
            String successResult = StreamUtils.copyToString(exec.getInputStream(), Charset.forName("UTF-8"));
            String errorResult = StreamUtils.copyToString(exec.getErrorStream(),Charset.forName("UTF-8"));
            log.info("successResult: " + successResult);
            log.info("errorResult: " + errorResult);
            log.info("================================");
        } catch (Exception e) {
            log.error("clone error: ", e);
            e.printStackTrace();
        }
    }
}
