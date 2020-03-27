package org.hrds.rducm.gitlab.app.assembler;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.*;
import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberCreateDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nRoleVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hrds.rducm.gitlab.infra.util.PageConvertUtils;
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
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;
    @Autowired
    private IC7nBaseServiceService ic7nBaseServiceService;

    /**
     * 将GitlabMemberBatchDTO转换为List<RdmMember>
     *
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
            Integer glProjectId = ic7nDevOpsServiceService.repositoryIdToGlProjectId(repositoryId);
            repositoryIdToGlProjectIdMap.put(repositoryId, glProjectId);
        });

        // 查询gitlab用户id
        Map<Long, Integer> userIdToGlUserIdMap = new HashMap<>();
        rdmMemberBatchDTO.getMembers().forEach(m -> {
            Integer glUserId = ic7nBaseServiceService.userIdToGlUserId(m.getUserId());
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
        Integer glProjectId = ic7nDevOpsServiceService.repositoryIdToGlProjectId(repositoryId);
        Integer glUserId = ic7nBaseServiceService.userIdToGlUserId(param.getUserId());

        param.setGlProjectId(glProjectId);
        param.setGlUserId(glUserId);

        param.setOrganizationId(organizationId);
        param.setProjectId(projectId);
        param.setRepositoryId(repositoryId);
        return param;
    }

    /**
     * 成员查询dto转换
     *
     * @param page
     * @return
     */
//    public PageInfo<RdmMemberViewDTO> pageToRdmMemberViewDTO(Long projectId, Page<RdmMember> page) {
//        Page<RdmMemberViewDTO> rdmMemberViewDTOS = ConvertUtils.convertPage(page, RdmMemberViewDTO.class);
//
//        // 获取用户id集合
//        Set<Long> userIds = Sets.newHashSet();
//        // 获取代码库id集合
//        Set<Long> repositoryIds = Sets.newHashSet();
//        rdmMemberViewDTOS.getContent().forEach(dto -> {
//            userIds.add(dto.getUserId());
//            userIds.add(dto.getCreatedBy());
//            repositoryIds.add(dto.getRepositoryId());
//        });
//
//        // 查询用户信息
//        Map<Long, C7nUserVO> userVOMap = ic7nBaseServiceService.listC7nUserToMap(projectId, userIds);
//
//        // 查询应用服务信息
//        Map<Long, C7nAppServiceVO> appServiceVOMap = ic7nDevOpsServiceService.listC7nAppServiceToMap(repositoryIds);
//
//        // 填充数据
//        for (RdmMemberViewDTO viewDTO : rdmMemberViewDTOS.getContent()) {
//            C7nUserVO c7nUserVO = Optional.ofNullable(userVOMap.get(viewDTO.getUserId())).orElse(new C7nUserVO().setRoles(Collections.emptyList()));
//            C7nUserVO c7nCreateUserVO = Optional.ofNullable(userVOMap.get(viewDTO.getCreatedBy())).orElse(new C7nUserVO());
//
//            C7nAppServiceVO c7nAppServiceVO = Optional.ofNullable(appServiceVOMap.get(viewDTO.getRepositoryId())).orElse(new C7nAppServiceVO());
//
//            viewDTO.setRealName(c7nUserVO.getRealName());
//            viewDTO.setLoginName(c7nUserVO.getLoginName());
//            viewDTO.setRoleNames(c7nUserVO.getRoles().stream().map(C7nRoleVO::getName).collect(Collectors.toList()));
//
//            viewDTO.setCreatedByName(c7nCreateUserVO.getRealName());
//
//            viewDTO.setAppServiceName(c7nAppServiceVO.getName());
//        }
//
//        return PageConvertUtils.convert(rdmMemberViewDTOS);
//    }

    /**
     * 成员查询dto转换(可复用)
     *
     * @param page
     * @return
     */
    public PageInfo<RdmMemberViewDTO> pageToRdmMemberViewDTO(Page<RdmMember> page) {
        Page<RdmMemberViewDTO> rdmMemberViewDTOS = ConvertUtils.convertPage(page, RdmMemberViewDTO.class);

        // 获取用户id集合, 格式如: {projectId: [userId1, userId2]}
        Multimap<Long, Long> projectIdAndUserIds = HashMultimap.create();
        // 获取代码库id集合
        Set<Long> repositoryIds = Sets.newHashSet();
        rdmMemberViewDTOS.getContent().forEach(dto -> {
            projectIdAndUserIds.put(dto.getProjectId(), dto.getUserId());
            projectIdAndUserIds.put(dto.getProjectId(), dto.getCreatedBy());
            repositoryIds.add(dto.getRepositoryId());
        });

        // 查询用户信息
        Map<Long, C7nUserVO> userVOMap = new HashMap<>();

        projectIdAndUserIds.asMap().forEach((projectId, userIds) -> {
            Map<Long, C7nUserVO> tempMap = ic7nBaseServiceService.listC7nUserToMapOnProjectLevel(projectId, Sets.newHashSet(userIds));
            userVOMap.putAll(tempMap);
        });

        // 查询应用服务信息
        Map<Long, C7nAppServiceVO> appServiceVOMap = ic7nDevOpsServiceService.listC7nAppServiceToMap(repositoryIds);

        // 填充数据
        for (RdmMemberViewDTO viewDTO : rdmMemberViewDTOS.getContent()) {
            C7nUserVO c7nUserVO = Optional.ofNullable(userVOMap.get(viewDTO.getUserId())).orElse(new C7nUserVO().setRoles(Collections.emptyList()));
            C7nUserVO c7nCreateUserVO = Optional.ofNullable(userVOMap.get(viewDTO.getCreatedBy())).orElse(new C7nUserVO());

            C7nAppServiceVO c7nAppServiceVO = Optional.ofNullable(appServiceVOMap.get(viewDTO.getRepositoryId())).orElse(new C7nAppServiceVO());

            viewDTO.setRealName(c7nUserVO.getRealName());
            viewDTO.setLoginName(c7nUserVO.getLoginName());
            viewDTO.setRoleNames(c7nUserVO.getRoles().stream().map(C7nRoleVO::getName).collect(Collectors.toList()));

            viewDTO.setCreatedByName(c7nCreateUserVO.getRealName());

            viewDTO.setAppServiceName(c7nAppServiceVO.getName());
        }

        return PageConvertUtils.convert(rdmMemberViewDTOS);
    }
}
