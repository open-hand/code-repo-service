package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.UserDTO;
import org.hzero.core.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("projectController.v1")
@RequestMapping("/v1/projects/{projectId}")
public class ProjectController extends BaseController {
    @Autowired
    private RdmUserRepository rdmUserRepository;
    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;

    @ApiOperation(value = "查询项目开发成员, 并排除自己(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "param", value = "登录名或真实名称模糊搜索", paramType = "query"),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/c7n/members")
    public ResponseEntity<List<UserDTO>> listProjectMembers(@PathVariable Long projectId,
                                                            @RequestParam(required = false) String param) {
        ResponseEntity<List<UserDTO>> responseEntity = baseServiceFeignClient.listProjectUsersByName(projectId, param);
        return responseEntity;

        // todo 临时使用, 后续需替换为 外部接口
//        List<RdmUser> rdmUsers = rdmUserRepository.selectAll();
//        List<Map<String, Object>> collect = rdmUsers.stream().map(u -> {
//            Map<String, Object> m = Maps.newHashMap();
//            m.put("userId", u.getUserId());
//            m.put("realName", u.getGlUserName());
//            return m;
//        }).collect(Collectors.toList());
//        return Results.success(collect);
    }


}
