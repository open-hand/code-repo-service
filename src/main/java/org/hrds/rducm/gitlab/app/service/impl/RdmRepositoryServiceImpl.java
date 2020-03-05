package org.hrds.rducm.gitlab.app.service.impl;

import org.hrds.rducm.gitlab.app.service.RdmRepositoryService;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用服务默认实现
 *
 * @author ying.xie@hand-china.com 2020-02-26 14:03:22
 */
@Service
public class RdmRepositoryServiceImpl implements RdmRepositoryService {
    @Autowired
    private RdmRepositoryRepository rdmRepositoryRepository;

    /**
     * 查询所有[已启用]的服务
     * todo 临时使用,需对接外围接口
     * @return
     */
    @Override
    public List<RdmRepository> listByActive(Long projectId) {
        List<RdmRepository> gitlabRepositories = rdmRepositoryRepository.selectAll();
        return gitlabRepositories;
    }
}
