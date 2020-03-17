package org.hrds.rducm.gitlab.api.controller.v1;

import com.google.common.collect.Maps;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.c7n.C7nUserDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.UserDTO;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
            @ApiImplicitParam(name = "name", value = "真实名称或登录名模糊搜索", paramType = "query"),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/c7n/members/developers")
    public ResponseEntity<List<C7nUserDTO>> listProjectMembers(@PathVariable Long projectId,
                                                               @RequestParam(required = false) String name) {
        ResponseEntity<List<UserDTO>> responseEntity = baseServiceFeignClient.listProjectUsersByName(projectId, name);

        List<C7nUserDTO> c7nUserDTOS = Objects.requireNonNull(responseEntity.getBody()).stream().map(u -> {
            C7nUserDTO c7nUserDTO = new C7nUserDTO();
            c7nUserDTO.setUserId(u.getId())
                    .setLoginName(u.getLoginName())
                    .setEmail(u.getEmail())
                    .setOrganizationId(u.getOrganizationId())
                    .setRealName(u.getRealName())
                    .setImageUrl(u.getImageUrl());
            return c7nUserDTO;
        }).collect(Collectors.toList());
        return Results.success(c7nUserDTOS);

    }

    // todo delete
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
