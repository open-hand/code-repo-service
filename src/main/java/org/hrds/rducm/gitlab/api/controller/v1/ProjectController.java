package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import com.google.common.base.Joiner;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.*;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.hrds.rducm.gitlab.infra.enums.IamRoleCodeEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nRoleVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.mapper.RdmMemberMapper;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * C7N相关的项目层接口
 *
 * @author ying.xie
 * @date 2020/6/5
 */

@RestController("projectController.v1")
@RequestMapping("/v1/{organizationId}/projects/{projectId}")
public class ProjectController extends BaseController {
    private static final String GITLAB_ROLE_LABEL = "GITLAB_OWNER";

    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;
    @Autowired
    private RdmMemberMapper rdmMemberMapper;

    @ApiOperation(value = "查询项目成员, 排除'项目管理员'和'组织管理员'角色,并排除自己(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "name", value = "真实名称或登录名模糊搜索", paramType = "query"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/c7n/members/developers")
    public ResponseEntity<List<BaseC7nUserViewDTO>> listDeveloperProjectMembers(@PathVariable Long organizationId,
                                                                                @PathVariable Long projectId,
                                                                                @RequestParam(required = false) String name,
                                                                                @RequestParam(defaultValue = "project") String type) {
        if (StringUtils.equalsIgnoreCase(type, "project")) {
            //查询项目开发成员
            List<BaseC7nUserViewDTO> baseC7NUserViewDTOS = queryProjectUsers(organizationId, projectId, name);
            return Results.success(baseC7NUserViewDTOS);
        } else if (StringUtils.equalsIgnoreCase(type, "nonProject")) {
            //查询项目开发成员
            List<BaseC7nUserViewDTO> baseC7NUserViewDTOS = queryNonProjectUsers(projectId, name);
            return Results.success(baseC7NUserViewDTOS);
        } else {
            return Results.success(null);
        }
    }

    private List<BaseC7nUserViewDTO> queryNonProjectUsers(Long projectId, String name) {
        List<C7nUserVO> c7nUserVOS = Optional.ofNullable(c7NBaseServiceFacade.listDeveloperProjectMembers(projectId, name))
                .orElse(Collections.emptyList());
        if (org.springframework.util.StringUtils.isEmpty(name)) {
            return Collections.EMPTY_LIST;
        }

        // 过滤当前项目成员
        List<C7nUserVO> allC7nUserVOS = Optional.ofNullable(c7NBaseServiceFacade.listEnabledUsersByUserName(projectId, name))
                .orElse(Collections.emptyList());
        Set<Long> memberIds = c7nUserVOS.stream().map(C7nUserVO::getId).collect(Collectors.toSet());
        List<C7nUserVO> nonProjectMember = allC7nUserVOS.stream().filter(a -> !memberIds.contains(a.getId())).map(a -> a.setProjectMember(false)).collect(Collectors.toList());

        List<BaseC7nUserViewDTO> baseC7NUserViewDTOS = nonProjectMember.stream()
                .map(u -> {
                    BaseC7nUserViewDTO baseC7NUserViewDTO = new BaseC7nUserViewDTO();
                    baseC7NUserViewDTO.setUserId(u.getId())
                            .setLoginName(u.getLoginName())
                            .setEmail(u.getEmail())
                            .setEnabled(u.getEnabled())
                            .setOrganizationId(u.getOrganizationId())
                            .setRealName(u.getRealName())
                            .setImageUrl(u.getImageUrl())
                            .setProjectMember(u.getProjectMember())
                            .setLdap(u.getLdap());
                    return baseC7NUserViewDTO;
                }).collect(Collectors.toList());
        fillAccessLevel(baseC7NUserViewDTOS, projectId);
        return baseC7NUserViewDTOS;
    }

    private void fillAccessLevel(List<BaseC7nUserViewDTO> baseC7NUserViewDTOS, Long projectId) {
        if (!CollectionUtils.isEmpty(baseC7NUserViewDTOS)) {
            Set<Long> userIds = baseC7NUserViewDTOS.stream().map(BaseC7nUserViewDTO::getUserId).collect(Collectors.toSet());
            List<RdmMember> rdmMembers = rdmMemberMapper.selectUserGroupAccessLevel(userIds, projectId);
            if (!CollectionUtils.isEmpty(rdmMembers)) {
                Map<Long, Integer> longIntegerMap = rdmMembers.stream().collect(Collectors.toMap(RdmMember::getUserId, RdmMember::getGlAccessLevel));
                baseC7NUserViewDTOS.forEach(baseC7nUserViewDTO -> {
                    baseC7nUserViewDTO.setGroupAccessLevel(longIntegerMap.get(baseC7nUserViewDTO.getUserId()));
                });
            }
        }
    }

    private List<BaseC7nUserViewDTO> queryProjectUsers(Long organizationId, Long projectId, String name) {
        List<C7nUserVO> c7nUserVOS = Optional.ofNullable(c7NBaseServiceFacade.listDeveloperProjectMembers(projectId, name))
                .orElse(Collections.emptyList());
        c7nUserVOS = c7nUserVOS.stream().map(a -> a.setProjectMember(true)).collect(Collectors.toList());

        // 获取组织管理员
        List<C7nUserVO> orgAdministrators = Optional.ofNullable(c7NBaseServiceFacade.listOrgAdministrator(organizationId))
                .orElse(Collections.emptyList());
        Set<Long> orgAdmins = orgAdministrators.stream().map(C7nUserVO::getId).collect(Collectors.toSet());

        //获取自定义角色有gitlab owner标签的用户
        List<C7nUserVO> gitlabOwnerUser = Optional.ofNullable(c7NBaseServiceFacade.listCustomGitlabOwnerLableUser(projectId, GITLAB_ROLE_LABEL))
                .orElse(Collections.emptyList());
        Set<Long> gitlabUserIds = gitlabOwnerUser.stream().map(C7nUserVO::getId).collect(Collectors.toSet());

        List<BaseC7nUserViewDTO> baseC7NUserViewDTOS = c7nUserVOS.stream()
                // 过滤“项目所有者”角色的用户
                .filter(u -> u.getRoles().stream().noneMatch(r -> r.getCode().equals(IamRoleCodeEnum.PROJECT_OWNER.getCode())))
                // 过滤掉"组织管理员"角色的用户
                .filter(u -> !orgAdmins.contains(u.getId()))
                //过滤自定义项目层角色拥有gitlab owner的用户
                .filter(u -> !gitlabUserIds.contains(u.getId()))
                .map(u -> {
                    BaseC7nUserViewDTO baseC7NUserViewDTO = new BaseC7nUserViewDTO();
                    baseC7NUserViewDTO.setUserId(u.getId())
                            .setLoginName(u.getLoginName())
                            .setEmail(u.getEmail())
                            .setEnabled(u.getEnabled())
                            .setOrganizationId(u.getOrganizationId())
                            .setRealName(u.getRealName())
                            .setImageUrl(u.getImageUrl())
                            .setProjectMember(u.getProjectMember())
                            .setLdap(u.getLdap());

                    return baseC7NUserViewDTO;
                }).collect(Collectors.toList());

        fillAccessLevel(baseC7NUserViewDTOS, projectId);
        return baseC7NUserViewDTOS;
    }

    private boolean isGitlabOwnerLabel(C7nUserVO c7nUserVO) {
        List<C7nRoleVO> roles = c7nUserVO.getRoles();
        //根据roleCoe查询标签
        List<String> roleCodes = roles.stream().map(C7nRoleVO::getCode).collect(Collectors.toList());
        return true;
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
        List<C7nUserVO> allC7nUserVOS = Optional.ofNullable(c7NBaseServiceFacade.listEnabledUsersByUserName(projectId, name))
                .orElse(Collections.emptyList());
        Set<Long> memberIds = c7nUserVOS.stream().map(C7nUserVO::getId).collect(Collectors.toSet());
        List<C7nUserVO> nonProjectMember = allC7nUserVOS.stream().filter(a -> !memberIds.contains(a.getId())).map(a -> a.setProjectMember(false)).collect(Collectors.toList());

        List<BaseC7nUserViewDTO> baseC7NUserViewDTOS = nonProjectMember.stream()
                .map(u -> {
                    BaseC7nUserViewDTO baseC7NUserViewDTO = new BaseC7nUserViewDTO();
                    baseC7NUserViewDTO.setUserId(u.getId())
                            .setLoginName(u.getLoginName())
                            .setEmail(u.getEmail())
                            .setEnabled(u.getEnabled())
                            .setOrganizationId(u.getOrganizationId())
                            .setRealName(u.getRealName())
                            .setImageUrl(u.getImageUrl())
                            .setProjectMember(u.getProjectMember());
                    return baseC7NUserViewDTO;
                }).collect(Collectors.toList());
        return Results.success(baseC7NUserViewDTOS);
    }


}
