package org.hrds.rducm.gitlab.app.assembler;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberCreateDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.DevOpsServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nRoleVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hrds.rducm.gitlab.infra.util.PageConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private RdmRepositoryRepository rdmRepositoryRepository;
    @Autowired
    private RdmUserRepository rdmUserRepository;
    @Autowired
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;
    @Autowired
    private IC7nBaseServiceService ic7nBaseServiceService;

    /**
     * 将GitlabMemberBatchDTO转换为List<RdmMember>
     *
     * @param projectId
     * @param rdmMemberBatchDTO
     * @return
     */
    public List<RdmMember> rdmMemberBatchDTOToRdmMembers(Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO) {
        // 查询gitlab项目id和用户id
        Map<Long, Integer> repositoryIdToGlProjectIdMap = new HashMap<>();
        rdmMemberBatchDTO.getRepositoryIds().forEach(repositoryId -> {
            // 获取gitlab项目id
            Integer glProjectId = ic7nDevOpsServiceService.repositoryIdToGlProjectId(projectId, repositoryId);
            repositoryIdToGlProjectIdMap.put(repositoryId, glProjectId);
        });

        // 查询gitlab用户id
        Map<Long, Integer> userIdToGlUserIdMap = new HashMap<>();
        rdmMemberBatchDTO.getMembers().forEach(m -> {
            Integer glUserId = ic7nBaseServiceService.userIdToGlUserId(projectId, m.getUserId());
            userIdToGlUserIdMap.put(m.getUserId(), glUserId);
        });

        // 转换为List<RdmMember>格式
        List<RdmMember> rdmMembers = new ArrayList<>();
        for (Long repositoryId : rdmMemberBatchDTO.getRepositoryIds()) {
            for (RdmMemberBatchDTO.GitlabMemberCreateDTO member : rdmMemberBatchDTO.getMembers()) {
                RdmMember rdmMember = ConvertUtils.convertObject(member, RdmMember.class);
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
     * @param projectId
     * @param repositoryId
     * @param rdmMemberCreateDTO
     * @return
     */
    public RdmMember rdmMemberCreateDTOToRdmMember(Long projectId, Long repositoryId, RdmMemberCreateDTO rdmMemberCreateDTO) {
        final RdmMember param = ConvertUtils.convertObject(rdmMemberCreateDTO, RdmMember.class);

        // 获取gitlab项目id和用户id todo 应从外部接口获取, 暂时从数据库获取
        Integer glProjectId;
        Integer glUserId;

        RdmRepository rdmRepository = rdmRepositoryRepository.selectOne(new RdmRepository().setRepositoryId(repositoryId));
        RdmUser rdmUser = rdmUserRepository.selectOne(new RdmUser().setUserId(param.getUserId()));

        glProjectId = rdmRepository.getGlProjectId();
        glUserId = rdmUser.getGlUserId();

        param.setGlProjectId(glProjectId);
        param.setGlUserId(glUserId);

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
    public PageInfo<RdmMemberViewDTO> pageToRdmMemberViewDTO(Long projectId, Page<RdmMember> page) {
        Page<RdmMemberViewDTO> rdmMemberViewDTOS = ConvertUtils.convertPage(page, RdmMemberViewDTO.class);

        // 获取用户id集合
        Set<Long> userIds = Sets.newHashSet();
        // 获取代码库id集合
        Set<Long> repositoryIds = Sets.newHashSet();
        rdmMemberViewDTOS.getContent().forEach(dto -> {
            userIds.add(dto.getUserId());
            userIds.add(dto.getCreatedBy());
            repositoryIds.add(dto.getRepositoryId());
        });

        // 查询用户信息
        Map<Long, C7nUserVO> userVOMap = ic7nBaseServiceService.listC7nUserToMap(projectId, userIds);

        // 查询应用服务信息
        Map<Long, C7nAppServiceVO> appServiceVOMap = ic7nDevOpsServiceService.listC7nAppServiceToMap(projectId, repositoryIds);

        // 填充数据
        for (RdmMemberViewDTO viewDTO : rdmMemberViewDTOS.getContent()) {
            C7nUserVO c7nUserVO = userVOMap.get(viewDTO.getUserId());
            C7nUserVO c7nCreateUserVO = userVOMap.get(viewDTO.getCreatedBy());

            C7nAppServiceVO c7nAppServiceVO = appServiceVOMap.get(viewDTO.getRepositoryId());

            viewDTO.setRealName(c7nUserVO.getRealName());
            viewDTO.setLoginName(c7nUserVO.getLoginName());
            viewDTO.setRoleNames(c7nUserVO.getRoles().stream().map(C7nRoleVO::getName).collect(Collectors.toList()));

            viewDTO.setCreatedByName(c7nCreateUserVO.getRealName());

            viewDTO.setAppServiceName(c7nAppServiceVO.getName());
        }

        return PageConvertUtils.convert(rdmMemberViewDTOS);
    }
}
