package org.hrds.rducm.gitlab.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.*;
import org.hrds.rducm.gitlab.api.controller.dto.export.MemberExportDTO;
import org.hzero.export.vo.ExportParam;

import javax.servlet.http.HttpServletResponse;

/**
 * @author xy
 */
public interface RdmMemberAppService {
    /**
     * 分页查询成员
     *
     * @param projectId   项目id
     * @param pageRequest 分页参数
     * @param query       查询参数
     * @return
     */
    PageInfo<RdmMemberViewDTO> pageByOptions(Long projectId, PageRequest pageRequest, RdmMemberQueryDTO query);

    /**
     * 组织层
     * 分页查询成员
     *
     * @param organizationId
     * @param pageRequest
     * @param query
     * @return
     */
    PageInfo<RdmMemberViewDTO> pageByOptionsOnOrg(Long organizationId, PageRequest pageRequest, RdmMemberQueryDTO query);

    /**
     * 批量新增或修改成员
     *
     * @param organizationId    组织id
     * @param projectId         项目id
     * @param rdmMemberBatchDTO 参数
     */
    void batchAddOrUpdateMembers(Long organizationId, Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO);

    /**
     * 新增成员
     *
     * @param organizationId
     * @param projectId          项目id
     * @param repositoryId       代码库id
     * @param rdmMemberCreateDTO 参数
     */
    void addMember(Long organizationId, Long projectId, Long repositoryId, RdmMemberCreateDTO rdmMemberCreateDTO);

    /**
     * 修改成员
     *
     * @param memberId           成员id, 主键
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
     * 从Gitlab同步成员
     *
     * @param memberId
     */
    void syncMember(Long memberId);

    /**
     * 成员导出
     *
     * @param projectId
     * @param pageRequest
     * @param query
     * @param exportParam
     * @param response
     * @return
     */
    Page<MemberExportDTO> export(Long projectId, PageRequest pageRequest, RdmMemberQueryDTO query, ExportParam exportParam, HttpServletResponse response);

    /**
     * 组织层
     * 成员导出
     *
     * @param organizationId
     * @param pageRequest
     * @param query
     * @param exportParam
     * @param response
     * @return
     */
    Page<MemberExportDTO> exportOnOrg(Long organizationId, PageRequest pageRequest, RdmMemberQueryDTO query, ExportParam exportParam, HttpServletResponse response);

    /**
     * 处理过期的成员(定时任务调用)
     */
    void handleExpiredMembers();

    /**
     * saga测试demo
     *
     * @param organizationId
     * @param projectId
     * @param rdmMemberBatchDTO
     */
    void batchAddMemberSagaDemo(Long organizationId, Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO);
}
