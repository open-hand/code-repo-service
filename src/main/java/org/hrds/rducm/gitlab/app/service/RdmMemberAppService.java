package org.hrds.rducm.gitlab.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hrds.rducm.gitlab.api.controller.dto.*;
import org.hrds.rducm.gitlab.api.controller.dto.export.MemberExportDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hzero.export.vo.ExportParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

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
    Page<RdmMemberViewDTO> pageByOptions(Long projectId, PageRequest pageRequest, RdmMemberQueryDTO query);

    /**
     * 列表查询成员
     *
     * @param projectId 项目id
     * @param query     查询参数
     * @return
     */
    List<RdmMemberViewDTO> listByOptions(Long projectId, RdmMemberQueryDTO query);

    /**
     * 组织层
     * 分页查询成员
     *
     * @param organizationId
     * @param pageRequest
     * @param query
     * @return
     */
    Page<RdmMemberViewDTO> pageByOptionsOnOrg(Long organizationId, PageRequest pageRequest, RdmMemberQueryDTO query);

    /**
     * 批量新增或修改成员
     *
     * @param organizationId    组织id
     * @param projectId         项目id
     * @param rdmMemberBatchDTO 参数
     */
    void batchAddOrUpdateMembers(Long organizationId, Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO);

    /**
     * 批量移除成员
     *
     * @param organizationId
     * @param projectId
     * @param memberIds
     */
    void batchRemoveMembers(Long organizationId, Long projectId, Set<Long> memberIds);

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
     * 批量失效应用服务的所有成员权限
     * <p>
     * 禁用应用服务时将代码库具有该应用服务权限的成员失效
     * 失效方式：将成员权限的gitlab过期时间设为当前日期，并将Gitlab的权限清除
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     */
    List<RdmMember> batchInvalidMember(Long organizationId, Long projectId, Long repositoryId);

    /**
     * 批量生效应用服务的所有成员权限
     * <p>
     * 启用应用服务时将代码库具有该应用服务权限的成员生效
     * 生效方式：将成员权限的gitlab过期时间清除，并添加Gitlab权限
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     */
    List<RdmMember> batchValidMember(Long organizationId, Long projectId, Long repositoryId);

    void syncBatchMember(List<Long> memberIds);

    void allSync(Long organizationId, Long projectId);

    void batchAddGroupMembers(Long organizationId, Long projectId, List<RdmMemberBatchDTO.GitlabMemberCreateDTO> gitlabMemberCreateDTOS);

    void updateGroupMember(Long organizationId, Long projectId, RdmMemberBatchDTO.GitlabMemberCreateDTO gitlabMemberCreateDTO, Long rducmGitlabMemberId);

    void deleteGroupMember(Long organizationId, Long projectId, Long rducmGitlabMemberId);

    void syncGroupMember(Long rducmGitlabMemberId);

    RdmMember getGroupMember(Long organizationId, Long projectId, Long userId);

    void insertGroupMember(RdmMemberAuditRecord rdmMemberAuditRecord);
}
