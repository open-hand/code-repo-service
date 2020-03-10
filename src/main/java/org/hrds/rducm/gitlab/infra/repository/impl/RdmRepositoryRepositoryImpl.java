package org.hrds.rducm.gitlab.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 资源库实现
 *
 * @author ying.xie@hand-china.com 2020-02-26 14:03:22
 */
@Component
public class RdmRepositoryRepositoryImpl extends BaseRepositoryImpl<RdmRepository> implements RdmRepositoryRepository {

    @Override
    public RdmRepository selectByUk(Long repositoryId) {
        RdmRepository repository = new RdmRepository();
        repository.setRepositoryId(repositoryId);
        return Optional.ofNullable(this.selectOne(repository))
                .orElseThrow(() -> new CommonException("数据库不存在该数据, 请检查参数repositoryId " + repositoryId));
    }

}
