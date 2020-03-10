package org.hrds.rducm.gitlab.domain.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmBranchRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmBranchService;
import org.hrds.rducm.gitlab.domain.service.IRdmRepositoryService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabCommitApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabMergeRequestApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hrds.rducm.gitlab.infra.util.PageConvertUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
@Service
public class RdmBranchServiceImpl implements IRdmBranchService {
    @Autowired
    private RdmBranchRepository rdmBranchRepository;
    @Autowired
    private RdmRepositoryRepository repositoryRepository;

    @Override
    public List<BranchDTO> getBranchesWithExcludeProtected(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);

        // 获取分支
        List<Branch> branches = rdmBranchRepository.getBranchesFromGitlab(rdmRepository.getGlProjectId());

        // 排除保护分支
        branches.removeIf(Branch::getProtected);

        return ConvertUtils.convertList(branches, BranchDTO.class);
    }
}
