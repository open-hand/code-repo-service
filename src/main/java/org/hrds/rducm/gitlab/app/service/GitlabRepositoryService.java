package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.domain.entity.GitlabRepository;

import java.util.List;

/**
 * 应用服务
 *
 * @author ying.xie@hand-china.com 2020-02-26 14:03:22
 */
public interface GitlabRepositoryService {

    List<GitlabRepository> listByActive(Long projectId);
}
