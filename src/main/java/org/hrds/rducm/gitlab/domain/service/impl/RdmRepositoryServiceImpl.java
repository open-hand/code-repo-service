package org.hrds.rducm.gitlab.domain.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.domain.service.IRdmRepositoryService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabCommitApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabMergeRequestApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

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
    @Autowired
    private GitlabMergeRequestApi gitlabMergeRequestApi;
    @Autowired
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;

    @Override
    public PageInfo<RepositoryOverViewDTO> pageOverviewByOptions(Long projectId, PageRequest pageRequest, Set<Long> repositoryIds) {
        // <1> 查询
        // 调用devops服务查询
        PageInfo<C7nAppServiceVO> c7nRepositories = ic7nDevOpsServiceService.pageC7nAppServices(projectId, pageRequest, repositoryIds);

        PageInfo<RepositoryOverViewDTO> repositoryOverViewDTOPageInfo = ConvertUtils.convertPageInfo(c7nRepositories, s -> new RepositoryOverViewDTO()
                .setRepositoryId(s.getId())
                .setRepositoryName(s.getName())
                .setGlProjectId(Math.toIntExact(s.getGitlabProjectId())));

        // <2> 封装展示参数
        repositoryOverViewDTOPageInfo.getList().forEach(repo -> {
            Integer glProjectId = repo.getGlProjectId();

            // 查询Gitlab项目
            Project glProject = gitlabProjectApi.getProject(glProjectId);
            // 查询成员数量
            int memberCount = rdmMemberRepository.selectCountByRepositoryId(repo.getRepositoryId());

            // 查询合并分支
            int openedMergeRequestCount = gitlabMergeRequestApi.getMergeRequests(glProjectId, Constants.MergeRequestState.OPENED).size();

            // 查询最近一次提交
            Commit latestCommit = gitlabCommitApi.getLatestCommit(glProjectId);

            repo.setDeveloperCount(memberCount)
                    .setDefaultBranch(glProject.getDefaultBranch())
                    .setVisibility(glProject.getVisibility().toValue())
                    .setLastCommittedDate(latestCommit.getCommittedDate())
                    .setOpenedMergeRequestCount(openedMergeRequestCount)
                    .setRepositoryCreationDate(glProject.getCreatedAt());
        });

        return repositoryOverViewDTOPageInfo;
    }
}
