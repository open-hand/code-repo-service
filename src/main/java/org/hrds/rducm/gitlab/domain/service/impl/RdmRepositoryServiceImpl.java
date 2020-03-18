package org.hrds.rducm.gitlab.domain.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.domain.service.IRdmRepositoryService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabCommitApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabMergeRequestApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.feign.DevOpsServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hrds.rducm.gitlab.infra.util.PageConvertUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private DevOpsServiceFeignClient devOpsServiceFeignClient;

    @Override
    public PageInfo<RepositoryOverViewDTO> pageOverviewByOptions(Long projectId, PageRequest pageRequest, List<Long> repositoryIds) {
        // <1> 查询
        // 这里加1是因为在controller被-1
        int page = pageRequest.getPage() + 1;
        int size = pageRequest.getSize();
        ResponseEntity<PageInfo<C7nAppServiceVO>> entity = devOpsServiceFeignClient.pageAppServiceByOptions(projectId, true, page, size);
        PageInfo<C7nAppServiceVO> c7nRepositories = Objects.requireNonNull(entity.getBody());

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
