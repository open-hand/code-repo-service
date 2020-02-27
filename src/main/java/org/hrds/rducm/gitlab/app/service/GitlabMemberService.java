package org.hrds.rducm.gitlab.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberUpdateDTO;

/**
 * @author xy
 */
public interface GitlabMemberService {

    Page<GitlabMemberViewDTO> list(Long projectId, PageRequest pageRequest, GitlabMemberQueryDTO query);

    void batchAddOrUpdateMembers(Long projectId, GitlabMemberBatchDTO gitlabMemberBatchDTO);

    void updateMember(Long projectId, Long repositoryId, Long memberId, GitlabMemberUpdateDTO gitlabMemberUpdateDTO);

    void removeMember(Long projectId, Long repositoryId, Long memberId);

    void handleExpiredMembers();
}
