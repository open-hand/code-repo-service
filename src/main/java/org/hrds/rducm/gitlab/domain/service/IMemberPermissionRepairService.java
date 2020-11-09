package org.hrds.rducm.gitlab.domain.service;

/**
 * description
 *
 * @author 14589 2020/10/26 16:17
 */
public interface IMemberPermissionRepairService {

    /**
     * 根据组织ID修复权限有问题的代码库成员
     * 1. 以代码库成员权限为准，修复问题数据
     * 2. 删除修复成功的审计数据
     * 3. 保留无法修复的审计数据
     *
     * @param organizationId
     */
    void repairMemberPermission(Long organizationId);

}
