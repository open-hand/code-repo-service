package org.hrds.rducm.gitlab.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberUpdateDTO;

/**
 * @author xy
 */
public interface RdmMemberService {
    /**
     * 分页查询成员
     * @param projectId 项目id
     * @param pageRequest 分页参数
     * @param query 查询参数
     * @return
     */
    PageInfo<RdmMemberViewDTO> pageByOptions(Long projectId, PageRequest pageRequest, RdmMemberQueryDTO query);

    /**
     * 批量新增或修改成员
     *
     * @param projectId 项目id
     * @param rdmMemberBatchDTO 参数
     */
    void batchAddOrUpdateMembers(Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO);

    /**
     * 修改成员
     *
     * @param memberId 成员id, 主键
     * @param rdmMemberUpdateDTO 参数
     */
    void updateMember(Long memberId, RdmMemberUpdateDTO rdmMemberUpdateDTO);

    /**
     * 移除成员
     *
     * @param memberId 成员id, 主键
     */
    void removeMember(Long memberId);

    /**
     * 处理过期的成员(定时任务调用)
     */
    void handleExpiredMembers();

    /**
     * saga测试demo
     * @param projectId
     * @param rdmMemberBatchDTO
     */
    void batchAddMemberSagaDemo(Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO);
}
