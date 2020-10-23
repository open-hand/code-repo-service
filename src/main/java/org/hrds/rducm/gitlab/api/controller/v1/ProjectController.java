package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.infra.enums.IamRoleCodeEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * C7N相关的项目层接口
 *
 * @author ying.xie
 * @date 2020/6/5
 */
//@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("projectController.v1")
@RequestMapping("/v1/{organizationId}/projects/{projectId}")
public class ProjectController extends BaseController {
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;

    @ApiOperation(value = "查询项目成员, 排除'项目管理员'和'组织管理员'角色,并排除自己(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "name", value = "真实名称或登录名模糊搜索", paramType = "query"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/c7n/members/developers")
    public ResponseEntity<List<BaseC7nUserViewDTO>> listDeveloperProjectMembers(@PathVariable Long organizationId,
                                                                                @PathVariable Long projectId,
                                                                                @RequestParam(required = false) String name) {
        //查询项目开发成员
        List<C7nUserVO> c7nUserVOS = Optional.ofNullable(c7NBaseServiceFacade.listDeveloperProjectMembers(projectId, name))
                .orElse(Collections.emptyList());
        c7nUserVOS = c7nUserVOS.stream().map(a -> a.setProjectMember(true)).collect(Collectors.toList());

        //过滤"项目所有者"角色的用户
        c7nUserVOS = c7nUserVOS.stream()
                .filter(u -> u.getRoles().stream().noneMatch(r -> r.getCode().equals(IamRoleCodeEnum.PROJECT_OWNER.getCode()))).collect(Collectors.toList());

        // 获取组织管理员
        List<C7nUserVO> orgAdministrators = Optional.ofNullable(c7NBaseServiceFacade.listOrgAdministrator(organizationId))
                .orElse(Collections.emptyList());
        Set<Long> orgAdmins = orgAdministrators.stream().map(C7nUserVO::getId).collect(Collectors.toSet());

        List<BaseC7nUserViewDTO> baseC7NUserViewDTOS = c7nUserVOS.stream()
                // 过滤“项目所有者”角色的用户

                // 过滤掉"组织管理员"角色的用户
                .filter(u -> !orgAdmins.contains(u.getId()))
                .map(u -> {
                    BaseC7nUserViewDTO baseC7NUserViewDTO = new BaseC7nUserViewDTO();
                    baseC7NUserViewDTO.setUserId(u.getId())
                            .setLoginName(u.getLoginName())
                            .setEmail(u.getEmail())
                            .setOrganizationId(u.getOrganizationId())
                            .setRealName(u.getRealName())
                            .setImageUrl(u.getImageUrl())
                            .setProjectMember(u.getProjectMember());
                    return baseC7NUserViewDTO;
                }).collect(Collectors.toList());
        return Results.success(baseC7NUserViewDTOS);

    }

    @ApiOperation(value = "查询非项目成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "name", value = "真实名称或登录名模糊搜索", paramType = "query"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/c7n/non-project-members")
    public ResponseEntity<List<BaseC7nUserViewDTO>> listNonProjectMembers(@PathVariable Long organizationId,
                                                                          @PathVariable Long projectId,
                                                                          @RequestParam String name) {
        //查询项目开发成员
        List<C7nUserVO> c7nUserVOS = Optional.ofNullable(c7NBaseServiceFacade.listDeveloperProjectMembers(projectId, name))
                .orElse(Collections.emptyList());

        // 过滤当前项目成员
        List<C7nUserVO> allC7nUserVOS = Optional.ofNullable(c7NBaseServiceFacade.listC7nUsersByNameOnSiteLevel(name, null))
                .orElse(Collections.emptyList());
        Set<Long> allC7nUserIds = allC7nUserVOS.stream().map(C7nUserVO::getId).collect(Collectors.toSet());
        List<C7nUserVO> nonProjectMember = allC7nUserVOS.stream().filter(a -> !allC7nUserIds.contains(a.getId())).map(a -> a.setProjectMember(false)).collect(Collectors.toList());

        List<BaseC7nUserViewDTO> baseC7NUserViewDTOS = nonProjectMember.stream()
                .map(u -> {
                    BaseC7nUserViewDTO baseC7NUserViewDTO = new BaseC7nUserViewDTO();
                    baseC7NUserViewDTO.setUserId(u.getId())
                            .setLoginName(u.getLoginName())
                            .setEmail(u.getEmail())
                            .setOrganizationId(u.getOrganizationId())
                            .setRealName(u.getRealName())
                            .setImageUrl(u.getImageUrl())
                            .setProjectMember(u.getProjectMember());
                    return baseC7NUserViewDTO;
                }).collect(Collectors.toList());
        return Results.success(baseC7NUserViewDTOS);
    }


}
