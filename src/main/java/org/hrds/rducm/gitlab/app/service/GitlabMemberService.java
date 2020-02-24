package org.hrds.rducm.gitlab.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberDTO;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberUpdateDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xy
 */
public interface GitlabMemberService {

    Page<GitlabMemberDTO> list(Long projectId, PageRequest pageRequest);

    void batchAddMembers(Long projectId, List<GitlabMemberDTO> gitlabMembersDTO);

    void updateMember(Long projectId, Long repositoryId, Long memberId, GitlabMemberUpdateDTO gitlabMemberUpdateDTO);

    void removeMember(Long id, Integer glProjectId, Integer glUserId);

    void handleExpiredMembers();
}
