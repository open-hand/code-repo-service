package org.hrds.rducm.migration.domain.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/12
 */
public interface Version023Service {
    void initAllPrivilegeOnSiteLevel();

    void orgLevel(Long organizationId);
}
