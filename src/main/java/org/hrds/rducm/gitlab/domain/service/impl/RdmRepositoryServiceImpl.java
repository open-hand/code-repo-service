package org.hrds.rducm.gitlab.domain.service.impl;

import org.gitlab4j.api.models.Project;
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmRepositoryService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
@Service
public class RdmRepositoryServiceImpl implements IRdmRepositoryService {
    @Autowired
    private RdmRepositoryRepository rdmRepositoryRepository;

    @Autowired
    private GitlabProjectApi gitlabProjectApi;

    @Override
    public List<RepositoryOverViewDTO> pageByOptions(Long projectId) {
        // <1> 查询
        Condition condition = Condition.builder(RdmRepository.class)
                .where(Sqls.custom().andEqualTo(RdmRepository.FIELD_PROJECT_ID, projectId))
                .build();

        List<RdmRepository> rdmRepositories = rdmRepositoryRepository.selectByCondition(condition);

        // <2> 封装展示参数
        List<RepositoryOverViewDTO> repositoryOverViewDTOS = new ArrayList<>();
        rdmRepositories.forEach(repo -> {
            RepositoryOverViewDTO repositoryOverViewDTO = new RepositoryOverViewDTO();

            // 查询Gitlab项目
            Project glProject = gitlabProjectApi.getProject(repo.getGlProjectId());
            // 查询成员 todo

            // 查询合并分支 todo

            repositoryOverViewDTO.setRepositoryId(repo.getRepositoryId())
                    .setRepositoryName(repo.getRepositoryName())
                    .setDeveloperCount(-1) // todo
                    .setDefaultBranch(glProject.getDefaultBranch())
                    .setVisibility(glProject.getVisibility().toValue())
                    .setLastCommittedDate(glProject.getLastActivityAt()) // todo
                    .setOpenedMergeRequestCount(glProject.getApprovalsBeforeMerge()) // todo
                    .setRepositoryCreationDate(glProject.getCreatedAt());

            repositoryOverViewDTOS.add(repositoryOverViewDTO);
        });

        return repositoryOverViewDTOS;
    }
}
