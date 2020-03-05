package org.hrds.rducm.gitlab.api.controller.v1;

import com.google.common.collect.Maps;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberService;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("projectController.v1")
@RequestMapping("/v1/projects/{projectId}")
public class ProjectController extends BaseController {
    private final RdmMemberService rdmMemberService;

    @Autowired
    private RdmUserRepository rdmUserRepository;

    public ProjectController(RdmMemberService rdmMemberService) {
        this.rdmMemberService = rdmMemberService;
    }

    @ApiOperation(value = "查询代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", dataType = "Long", required = true),
//            @ApiImplicitParam(name = "query", value = "查询参数", paramType = "query", dataType = "GitlabMemberQueryDTO"),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/gitlab/members")
    public ResponseEntity<Page<GitlabMemberViewDTO>> pageByOptions(@PathVariable Long projectId,
                                                                   PageRequest pageRequest,
                                                                   GitlabMemberQueryDTO query) {
        return Results.success(rdmMemberService.list(projectId, pageRequest, query));
    }

    @ApiOperation(value = "批量新增代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "gitlabMemberBatchDTO", value = "body参数", dataType = "GitlabMemberBatchDTO", required = true),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/gitlab/members/batch-add")
    public ResponseEntity<Object> batchAddMembers(@PathVariable Long projectId,
                                                  @RequestBody GitlabMemberBatchDTO gitlabMemberBatchDTO) {
        validObject(gitlabMemberBatchDTO);
        rdmMemberService.batchAddOrUpdateMembers(projectId, gitlabMemberBatchDTO);
        return Results.created(null);
    }

    @ApiOperation(value = "查询项目开发成员, 并排除自己(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/c7n/members")
    public ResponseEntity<List<Map<String, Object>>> listProjectMembers(@PathVariable Long projectId) {
        // todo 临时使用, 后续需替换为 外部接口
        List<RdmUser> rdmUsers = rdmUserRepository.selectAll();
        List<Map<String, Object>> collect = rdmUsers.stream().map(u -> {
            Map<String, Object> m = Maps.newHashMap();
            m.put("userId", u.getUserId());
            m.put("realName", u.getGlUserName());
            return m;
        }).collect(Collectors.toList());
        return Results.success(collect);
    }
}
