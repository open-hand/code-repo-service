package org.hrds.rducm.gitlab.app.assembler;

import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberCreateDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 将GitlabMemberBatchDTO转换为List<RdmMember>
     *
     * @param projectId
     * @param rdmMemberBatchDTO
     * @return
     */
    public List<RdmMember> rdmMemberBatchDTOToRdmMembers(Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO) {
        // 查询gitlab项目id和用户id todo 应从外部接口获取, 暂时从数据库获取
        Map<Long, Integer> repositoryIdToGlProjectIdMap = new HashMap<>();
        rdmMemberBatchDTO.getRepositoryIds().forEach(repositoryId -> {
            // 获取gitlab项目id
            RdmRepository rdmRepository = rdmRepositoryRepository.selectOne(new RdmRepository().setRepositoryId(repositoryId));
            repositoryIdToGlProjectIdMap.put(repositoryId, rdmRepository.getGlProjectId());
        });

        // 查询gitlab用户id todo 应从外部接口获取, 暂时从数据库获取
        Map<Long, Integer> userIdToGlUserIdMap = new HashMap<>();
        rdmMemberBatchDTO.getMembers().forEach(m -> {
            RdmUser rdmUser = rdmUserRepository.selectOne(new RdmUser().setUserId(m.getUserId()));
            userIdToGlUserIdMap.put(m.getUserId(), rdmUser.getGlUserId());
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
}
