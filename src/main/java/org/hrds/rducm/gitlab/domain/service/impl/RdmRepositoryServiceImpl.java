package org.hrds.rducm.gitlab.domain.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmRepositoryService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabCommitApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hrds.rducm.gitlab.infra.util.PageConvertUtils;
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
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private GitlabProjectApi gitlabProjectApi;
    @Autowired
    private GitlabCommitApi gitlabCommitApi;

    @Override
    public PageInfo<RepositoryOverViewDTO> pageOverviewByOptions(Long projectId, PageRequest pageRequest, List<Long> repositoryIds) {
        // <1> 查询
        Condition condition = Condition.builder(RdmRepository.class)
                .where(Sqls.custom()
                        .andEqualTo(RdmRepository.FIELD_PROJECT_ID, projectId)
                        .andIn(RdmRepository.FIELD_REPOSITORY_ID, repositoryIds, true))
                .build();

        Page<RdmRepository> rdmRepositories = PageHelper.doPageAndSort(pageRequest, () -> rdmRepositoryRepository.selectByCondition(condition));

        // <2> 封装展示参数
        List<RepositoryOverViewDTO> repositoryOverViewDTOS = new ArrayList<>();
        rdmRepositories.forEach(repo -> {
            RepositoryOverViewDTO repositoryOverViewDTO = new RepositoryOverViewDTO();

            // 查询Gitlab项目
            Project glProject = gitlabProjectApi.getProject(repo.getGlProjectId());
            // 查询成员数量
            int memberCount = rdmMemberRepository.selectCountByRepositoryId(repo.getRepositoryId());

            // 查询合并分支 todo

            // 查询最近一次提交
            Commit latestCommit = gitlabCommitApi.getLatestCommit(repo.getGlProjectId());

            repositoryOverViewDTO.setRepositoryId(repo.getRepositoryId())
                    .setRepositoryName(repo.getRepositoryName())
                    .setDeveloperCount(memberCount)
                    .setDefaultBranch(glProject.getDefaultBranch())
                    .setVisibility(glProject.getVisibility().toValue())
                    .setLastCommittedDate(latestCommit.getCommittedDate())
                    .setOpenedMergeRequestCount(glProject.getApprovalsBeforeMerge()) // todo
                    .setRepositoryCreationDate(glProject.getCreatedAt());

            repositoryOverViewDTOS.add(repositoryOverViewDTO);
        });

        return PageConvertUtils.convert(new Page<>(repositoryOverViewDTOS, new io.choerodon.core.domain.PageInfo(rdmRepositories.getNumber(), rdmRepositories.getSize()), rdmRepositories.getTotalElements()));
    }
}
