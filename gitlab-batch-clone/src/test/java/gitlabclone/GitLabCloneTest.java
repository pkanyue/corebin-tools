package gitlabclone;

import cn.hutool.core.lang.Console;
import com.rlax.corebin.tools.gitlabclone.model.GitGroup;
import com.rlax.corebin.tools.gitlabclone.model.GitProject;
import com.rlax.corebin.tools.gitlabclone.service.GitlabProjectCloneService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Rlax
 * @date 2023/08/08
 */
public class GitLabCloneTest {

    private static GitlabProjectCloneService gitlabProjectCloneService;

    @Test
    void getAllProjects() {
        List<GitProject> projects = gitlabProjectCloneService.getAllProjects();
        Console.log(projects);
    }

    @Test
    void getGroups() {
        List<GitGroup> groups = gitlabProjectCloneService.getGroups();

        groups.forEach(gitGroup -> {
            List<GitGroup> subGroups = gitlabProjectCloneService.getSubGroups(gitGroup.getId());
            Console.log("subGroup parentGroupId: {} {}", gitGroup.getId(), subGroups);
        });

        Console.log("group {}", groups);
    }

    @Test
    void getGroupProjects() {
        List<GitGroup> groups = gitlabProjectCloneService.getGroups();

        groups.forEach(gitGroup -> {
            List<GitProject> projects = gitlabProjectCloneService.getProjectsByGroup(gitGroup.getId());
            Console.log("projects groupId: {} {}", gitGroup.getId(), projects);
        });

        Console.log("group {}", groups);
    }

    @BeforeAll
    static void init() {
        String gitlabUrl = "https://11.54.92.175";
        String privateToken = "H6R3R3NSNftKxYswPLpS";
        gitlabProjectCloneService = new GitlabProjectCloneService(gitlabUrl, privateToken);
    }

    @Test
    void cloneAndPull() {

    }

}
