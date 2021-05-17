package org.hrds.rducm.gitlab.app.assembler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;

import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberCreateDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nAppServiceViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nProjectViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nRoleVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/10
 */
@Component
public class RdmMemberAssembler {
    @Autowired
    private C7nDevOpsServiceFacade c7NDevOpsServiceFacade;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;

    /**
     * 将GitlabMemberBatchDTO转换为List<RdmMember>
     *
     * @param organizationId
     * @param projectId
     * @param rdmMemberBatchDTO
     * @return
     */
    public List<RdmMember> rdmMemberBatchDTOToRdmMembers(Long organizationId, Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO) {
        // 查询gitlab项目id和用户id
        Map<Long, Integer> repositoryIdToGlProjectIdMap = new HashMap<>();
        rdmMemberBatchDTO.getRepositoryIds().forEach(repositoryId -> {
            // 获取gitlab项目id
            Integer glProjectId = c7NDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);
            repositoryIdToGlProjectIdMap.put(repositoryId, glProjectId);
        });

        // 查询gitlab用户id
        Map<Long, Integer> userIdToGlUserIdMap = new HashMap<>();
        rdmMemberBatchDTO.getMembers().forEach(m -> {
            Integer glUserId = c7NBaseServiceFacade.userIdToGlUserId(m.getUserId());
            if (Objects.isNull(glUserId)) {
                throw new CommonException("error.gitlab.user.id.null", m.getUserId());
            }
            userIdToGlUserIdMap.put(m.getUserId(), glUserId);
        });

        // 转换为List<RdmMember>格式
        List<RdmMember> rdmMembers = new ArrayList<>();
        for (Long repositoryId : rdmMemberBatchDTO.getRepositoryIds()) {
            for (RdmMemberBatchDTO.GitlabMemberCreateDTO member : rdmMemberBatchDTO.getMembers()) {
                RdmMember rdmMember = ConvertUtils.convertObject(member, RdmMember.class);
                rdmMember.setOrganizationId(organizationId);
                rdmMember.setProjectId(projectId);
                rdmMember.setRepositoryId(repositoryId);

                // 设置gitlab项目id和用户id
                rdmMember.setGlProjectId(repositoryIdToGlProjectIdMap.get(repositoryId));
                rdmMember.setGlUserId(userIdToGlUserIdMap.get(member.getUserId()));

                rdmMembers.add(rdmMember);
            }
        }

        return rdmMembers;
    }

    /**
     * 转换新增成员所需参数
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @param rdmMemberCreateDTO
     * @return
     */
    public RdmMember rdmMemberCreateDTOToRdmMember(Long organizationId, Long projectId, Long repositoryId, RdmMemberCreateDTO rdmMemberCreateDTO) {
        final RdmMember param = ConvertUtils.convertObject(rdmMemberCreateDTO, RdmMember.class);

        // 获取gitlab项目id和用户id
        Integer glProjectId = c7NDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);
        Integer glUserId = c7NBaseServiceFacade.userIdToGlUserId(param.getUserId());

        param.setGlProjectId(glProjectId);
        param.setGlUserId(glUserId);

        param.setOrganizationId(organizationId);
        param.setProjectId(projectId);
        param.setRepositoryId(repositoryId);
        return param;
    }

    /**
     * 成员查询dto转换(可复用)
     *
     * @param page
     * @param resourceLevel
     * @return
     */
    public Page<RdmMemberViewDTO> pageToRdmMemberViewDTO(Page<RdmMember> page, ResourceLevel resourceLevel) {
        Page<RdmMemberViewDTO> rdmMemberViewDTOS = ConvertUtils.convertPage(page, RdmMemberViewDTO.class);

        // 获取用户id集合, 格式如: {projectId: [userId1, userId2]}, 用于查询项目角色
        Multimap<Long, Long> projectIdAndUserIds = HashMultimap.create();
        // 获取用户id集合
        Set<Long> userIds = Sets.newHashSet();
        // 获取代码库id集合
        Set<Long> repositoryIds = Sets.newHashSet();
        // 获取项目id集合
        Set<Long> projectIds = Sets.newHashSet();
        rdmMemberViewDTOS.getContent().forEach(dto -> {
            projectIdAndUserIds.put(dto.getProjectId(), dto.getUserId());
            userIds.add(dto.getUserId());
            userIds.add(dto.getCreatedBy());
            repositoryIds.add(dto.getRepositoryId());
            projectIds.add(dto.getProjectId());
        });

        // 查询用户信息, 带角色信息
        Map<Long, C7nUserVO> userWithRolesVOMap = new HashMap<>();

        projectIdAndUserIds.asMap().forEach((projectId, uIds) -> {
            Map<Long, C7nUserVO> tempMap = c7NBaseServiceFacade.listC7nUserToMapOnProjectLevel(projectId, Sets.newHashSet(uIds));
            userWithRolesVOMap.putAll(tempMap);
        });

        // 查询用户信息
        Map<Long, C7nUserVO> userVOMap = c7NBaseServiceFacade.listC7nUserToMap(Sets.newHashSet(userIds));

        // 查询应用服务信息
        Map<Long, C7nAppServiceVO> appServiceVOMap = c7NDevOpsServiceFacade.listC7nAppServiceToMap(repositoryIds);

        // 查询项目信息(组织层需要)
        Map<Long, C7nProjectVO> c7nProjectVOMap = Collections.emptyMap();
        if (ResourceLevel.ORGANIZATION.equals(resourceLevel)) {
            c7nProjectVOMap = c7NBaseServiceFacade.listProjectsByIdsToMap(projectIds);
        }

        // 填充数据
        for (RdmMemberViewDTO viewDTO : rdmMemberViewDTOS.getContent()) {
            C7nUserVO c7nUserVO = Optional.ofNullable(userVOMap.get(viewDTO.getUserId())).orElse(new C7nUserVO());
            C7nUserVO c7nCreateUserVO = Optional.ofNullable(userVOMap.get(viewDTO.getCreatedBy())).orElse(new C7nUserVO());

            C7nUserVO c7nUserWithRolesVO = Optional.ofNullable(userWithRolesVOMap.get(viewDTO.getUserId())).orElse(new C7nUserVO().setRoles(Collections.emptyList()));


            C7nAppServiceVO c7nAppServiceVO = Optional.ofNullable(appServiceVOMap.get(viewDTO.getRepositoryId())).orElse(new C7nAppServiceVO());

            // 组织层添加项目信息
            if (ResourceLevel.ORGANIZATION.equals(resourceLevel)) {
                C7nProjectVO c7nProjectVO = Optional.ofNullable(c7nProjectVOMap.get(viewDTO.getProjectId())).orElse(new C7nProjectVO());
                viewDTO.setProject(BaseC7nProjectViewDTO.convert(c7nProjectVO));
            }

            viewDTO.setUser(BaseC7nUserViewDTO.convert(c7nUserVO));
            viewDTO.setRoleNames(c7nUserWithRolesVO.getRoles().stream().map(C7nRoleVO::getName).collect(Collectors.toList()));

            viewDTO.setCreatedUser(BaseC7nUserViewDTO.convert(c7nCreateUserVO));

            viewDTO.setRepositoryName(c7nAppServiceVO.getName());
        }

        //按照跟新时间倒序排序表
        if (!Objects.isNull(rdmMemberViewDTOS.getContent())) {
            rdmMemberViewDTOS.setContent(rdmMemberViewDTOS.getContent().stream().sorted(Comparator.comparing(RdmMemberViewDTO::getLastUpdateDate).reversed()).collect(Collectors.toList()));
        }
        return rdmMemberViewDTOS;
    }

    /**
     * ExpiredMembersJob定时任务站内信模板使用
     * 将RdmMember里的id字段查询对应的name
     *
     * @param rdmMembers
     * @return
     */
    public void conversionForExpireMembersJob(List<RdmMember> rdmMembers) {
        // 获取用户id集合
        Set<Long> userIds = Sets.newHashSet();
        // 获取代码库id集合
        Set<Long> repositoryIds = Sets.newHashSet();
        // 获取项目id集合
        Set<Long> projectIds = Sets.newHashSet();

        rdmMembers.forEach(dto -> {
            userIds.add(dto.getUserId());
            repositoryIds.add(dto.getRepositoryId());
            projectIds.add(dto.getProjectId());
        });

        // 查询用户信息
        Map<Long, C7nUserVO> userVOMap = c7NBaseServiceFacade.listC7nUserToMap(Sets.newHashSet(userIds));
        // 查询应用服务信息
        Map<Long, C7nAppServiceVO> appServiceVOMap = c7NDevOpsServiceFacade.listC7nAppServiceToMap(repositoryIds);
        // 查询项目信息
        Map<Long, C7nProjectVO> c7nProjectVOMap = c7NBaseServiceFacade.listProjectsByIdsToMap(projectIds);

        // 填充数据
        for (RdmMember rdmMember : rdmMembers) {
            C7nUserVO c7nUserVO = Optional.ofNullable(userVOMap.get(rdmMember.getUserId())).orElse(new C7nUserVO());
            C7nAppServiceVO c7nAppServiceVO = Optional.ofNullable(appServiceVOMap.get(rdmMember.getRepositoryId())).orElse(new C7nAppServiceVO());
            C7nProjectVO c7nProjectVO = Optional.ofNullable(c7nProjectVOMap.get(rdmMember.getProjectId())).orElse(new C7nProjectVO());

            rdmMember.setProject(BaseC7nProjectViewDTO.convert(c7nProjectVO));
            rdmMember.setUser(BaseC7nUserViewDTO.convert(c7nUserVO));
            rdmMember.setRepository(BaseC7nAppServiceViewDTO.convert(c7nAppServiceVO));
        }
    }
}
