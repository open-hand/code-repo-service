package org.hrds.rducm.gitlab.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberCreateDTO;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberUpdateDTO;

import java.util.List;

/**
 * @author xy
 */
public interface GitlabMemberService {

    Page<GitlabMemberCreateDTO> list(Long projectId, PageRequest pageRequest);

    void batchAddMembers(Long projectId, List<GitlabMemberCreateDTO> gitlabMembersDTO);

    void updateMember(Long projectId, Long repositoryId, Long memberId, GitlabMemberUpdateDTO gitlabMemberUpdateDTO);

    void removeMember(Long projectId, Long repositoryId, Long memberId);

    void handleExpiredMembers();
}
