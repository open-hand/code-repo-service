package org.hrds.rducm.gitlab.domain.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmRepositoryService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabCommitApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabMergeRequestApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
@Service
public class RdmRepositoryServiceImpl implements IRdmRepositoryService {
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private GitlabProjectApi gitlabProjectApi;
    @Autowired
    private GitlabCommitApi gitlabCommitApi;
    @Autowired
    private GitlabMergeRequestApi gitlabMergeRequestApi;
    @Autowired
    private C7nDevOpsServiceFacade c7NDevOpsServiceFacade;

    @Override
    public Page<RepositoryOverViewDTO> pageOverviewByOptions(Long projectId, PageRequest pageRequest, Set<Long> repositoryIds) {
        // <1> 查询
        // 调用devops服务查询
        Page<C7nAppServiceVO> c7nRepositories = c7NDevOpsServiceFacade.pageC7nAppServices(projectId, pageRequest, repositoryIds);

        Page<RepositoryOverViewDTO> repositoryOverViewDTOPageInfo = ConvertUtils.convertPage(c7nRepositories, s -> new RepositoryOverViewDTO()
                .setRepositoryId(s.getId())
                .setRepositoryName(s.getName())
                .setGlProjectId(Math.toIntExact(s.getGitlabProjectId())));

        // <2> 封装展示参数
        repositoryOverViewDTOPageInfo.getContent().forEach(repo -> {
            Integer glProjectId = repo.getGlProjectId();

            // 查询Gitlab项目
            Project glProject = gitlabProjectApi.getProject(glProjectId);

            // Gitlab项目为空直接返回
            if (glProject == null) {
                return;
            }

            // 判断仓库是否初始化了, 暂根据是否有默认分支判定
            boolean repositoryEnabled = glProject.getDefaultBranch() != null;

            // 查询成员数量
            int memberCount = rdmMemberRepository.selectCountByRepositoryId(repo.getRepositoryId());

            // 查询权限大于Maintainer的成员数量
            int managerMemberCount = rdmMemberRepository.selectManagerCountByRepositoryId(repo.getRepositoryId());

            // 查询合并分支
            int openedMergeRequestCount = gitlabMergeRequestApi.getMergeRequests(glProjectId, Constants.MergeRequestState.OPENED).size();

            // 查询最近一次提交
            Commit latestCommit = null;
            if (repositoryEnabled) {
                latestCommit = gitlabCommitApi.getLatestCommit(glProjectId);
            }

            repo.setDeveloperCount(memberCount)
                    .setManagerCount(managerMemberCount)
                    .setDefaultBranch(glProject.getDefaultBranch())
                    .setVisibility(glProject.getVisibility().toValue())
                    .setLastCommittedDate(Optional.ofNullable(latestCommit).map(Commit::getCommittedDate).orElse(null))
                    .setOpenedMergeRequestCount(openedMergeRequestCount)
                    .setRepositoryCreationDate(glProject.getCreatedAt());
        });

        return repositoryOverViewDTOPageInfo;
    }
}
