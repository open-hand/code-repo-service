package org.hrds.rducm.gitlab.domain.service;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/5
 */
public interface IRdmMemberSyncLogService {
    /**
     * 比对组织下的所有应用服务的成员权限
     *
     * @param organizationId
     */
    void batchCompare(Long organizationId);
}
