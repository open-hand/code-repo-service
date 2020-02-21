package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.api.controller.vo.GitlabUserVO;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hzero.core.base.AopProxy;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xy
 */
public interface GitlabMemberService {

    List<GitlabMember> list(Long projectId);

    void batchAddMembers(List<GitlabMember> gitlabMembers);

    void updateMember(GitlabMember gitlabMember);

    void removeMember(Long id, Integer glProjectId, Integer glUserId);

    @Transactional(rollbackFor = Exception.class)
    void handleExpiredMembers();
}
