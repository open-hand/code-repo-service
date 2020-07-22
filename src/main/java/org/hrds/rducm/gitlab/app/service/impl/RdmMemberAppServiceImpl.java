package org.hrds.rducm.gitlab.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.api.controller.dto.*;
import org.hrds.rducm.gitlab.api.controller.dto.export.MemberExportDTO;
import org.hrds.rducm.gitlab.app.assembler.RdmMemberAssembler;
import org.hrds.rducm.gitlab.app.async.RdmMemberQueryHelper;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hrds.rducm.gitlab.infra.enums.RdmAccessLevel;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hzero.core.base.AopProxy;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;

import static org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants.RDUCM_BATCH_ADD_MEMBERS;

@Service
public class RdmMemberAppServiceImpl implements RdmMemberAppService, AopProxy<RdmMemberAppServiceImpl> {
    private static final Logger logger = LoggerFactory.getLogger(RdmMemberAppServiceImpl.class);

    private final RdmMemberRepository rdmMemberRepository;

    @Autowired
    private IRdmMemberService iRdmMemberService;

    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;

    @Autowired
    private C7nDevOpsServiceFacade c7NDevOpsServiceFacade;

    @Autowired
    private RdmMemberAssembler rdmMemberAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionalProducer producer;

    @Autowired
    private RdmMemberQueryHelper rdmMemberQueryHelper;

    public RdmMemberAppServiceImpl(RdmMemberRepository rdmMemberRepository) {
        this.rdmMemberRepository = rdmMemberRepository;
    }

    @Override
    public Page<RdmMemberViewDTO> pageByOptions(Long projectId, PageRequest pageRequest, RdmMemberQueryDTO query) {
        // <1> 封装查询条件
        String repositoryName = query.getRepositoryName();
        String realName = query.getRealName();
        String loginName = query.getLoginName();
        Set<Long> repositoryIds = query.getRepositoryIds();
        String params = query.getParams();

        Condition condition = Condition.builder(RdmMember.class)
                .where(Sqls.custom()
                        .andEqualTo(RdmMember.FIELD_PROJECT_ID, projectId)
                        .andIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIds, true))
                .build();
        // TODO 可使用多线程优化
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 调用外部接口模糊查询 用户名或登录名
        if (!StringUtils.isEmpty(realName) || !StringUtils.isEmpty(loginName)) {
            Set<Long> userIdsSet = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevel(projectId, realName, loginName);

            if (userIdsSet.isEmpty()) {
                return new Page<>();
            }

            condition.and().andIn(RdmMember.FIELD_USER_ID, userIdsSet);
        }

        // 调用外部接口模糊查询 应用服务
        if (!StringUtils.isEmpty(repositoryName)) {
            Set<Long> repositoryIdSet = c7NDevOpsServiceFacade.listC7nAppServiceIdsByNameOnProjectLevel(projectId, repositoryName);

            if (repositoryIdSet.isEmpty()) {
                return new Page<>();
            }

            condition.and().andIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIdSet);
        }

        // 根据params多条件查询
        if (!StringUtils.isEmpty(params)) {
            Set<Long> userIdsSet1 = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevel(projectId, params, null);
            Set<Long> userIdsSet2 = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevel(projectId, null, params);
            Set<Long> userIdsSet = new HashSet<>();
            userIdsSet.addAll(userIdsSet1);
            userIdsSet.addAll(userIdsSet2);

            Set<Long> repositoryIdSet = c7NDevOpsServiceFacade.listC7nAppServiceIdsByNameOnProjectLevel(projectId, params);

            boolean userIsEmpty = userIdsSet.isEmpty();
            boolean repositoryIsEmpty = repositoryIdSet.isEmpty();

            if (userIsEmpty && repositoryIsEmpty) {
                // 都为空, 查询结果为空
                return new Page<>();
            } else if (!userIsEmpty && !repositoryIsEmpty) {
                // 都不为空, or条件查询
                condition.and().andIn(RdmMember.FIELD_USER_ID, userIdsSet)
                        .orIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIdSet);
            } else if (!userIsEmpty) {
                // 用户查询不为空
                condition.and().andIn(RdmMember.FIELD_USER_ID, userIdsSet);
            } else {
                // 应用服务查询不为空
                condition.and().andIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIdSet);
            }
        }

        stopWatch.stop();
        logger.info(stopWatch.prettyPrint());

        Page<RdmMember> page = PageHelper.doPageAndSort(pageRequest, () -> rdmMemberRepository.selectByCondition(condition));

        return rdmMemberAssembler.pageToRdmMemberViewDTO(page, ResourceLevel.PROJECT);
    }

    @Override
    public Page<RdmMemberViewDTO> pageByOptionsOnOrg(Long organizationId, PageRequest pageRequest, RdmMemberQueryDTO query) {
        // <1> 封装查询条件
        String repositoryName = query.getRepositoryName();
        String realName = query.getRealName();
        String loginName = query.getLoginName();
        Set<Long> projectIds = query.getProjectIds();
        Set<Long> repositoryIds = query.getRepositoryIds();

        Condition condition = Condition.builder(RdmMember.class)
                .where(Sqls.custom()
                        .andEqualTo(RdmMember.FIELD_ORGANIZATION_ID, organizationId)
                        .andIn(RdmMember.FIELD_PROJECT_ID, projectIds, true)
                        .andIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIds, true))
                .build();

        // 调用外部接口模糊查询 用户名或登录名
        if (!StringUtils.isEmpty(realName) || !StringUtils.isEmpty(loginName)) {
            Set<Long> userIdsSet = c7NBaseServiceFacade.listProjectsC7nUserIdsByNameOnOrgLevel(organizationId, realName, loginName);

            if (userIdsSet.isEmpty()) {
                return new Page<>();
            }

            condition.and().andIn(RdmMember.FIELD_USER_ID, userIdsSet);
        }

        // 调用外部接口模糊查询 应用服务
        if (!StringUtils.isEmpty(repositoryName)) {
            Set<Long> repositoryIdSet = c7NDevOpsServiceFacade.listC7nAppServiceIdsByNameOnOrgLevel(organizationId, repositoryName);

            if (repositoryIdSet.isEmpty()) {
                return new Page<>();
            }

            condition.and().andIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIdSet);
        }

        Page<RdmMember> page = PageHelper.doPageAndSort(pageRequest, () -> rdmMemberRepository.selectByCondition(condition));

        return rdmMemberAssembler.pageToRdmMemberViewDTO(page, ResourceLevel.ORGANIZATION);
    }

    /**
     * 批量新增或修改成员
     *
     * @param projectId
     * @param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddOrUpdateMembers(Long organizationId, Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO) {
        // <0> 校验入参 + 转换
        List<RdmMember> rdmMembers = rdmMemberAssembler.rdmMemberBatchDTOToRdmMembers(organizationId, projectId, rdmMemberBatchDTO);

        // <1> 数据库添加成员, 已存在需要更新, 发起一个新事务
        // 开启新事务的目的是使这一步操作独立执行, 保证预操作成功
        self().batchAddOrUpdateMembersBeforeRequestsNew(rdmMembers);

        // <2> 调用gitlab api添加成员
        rdmMembers.forEach((m) -> {
            // <2.1> 判断新增或更新
            boolean isExists;
            if (m.get_status().equals(AuditDomain.RecordStatus.create)) {
                isExists = false;
            } else if (m.get_status().equals(AuditDomain.RecordStatus.update)) {
                isExists = true;
            } else {
                throw new IllegalArgumentException("record status is invalid");
            }

            // <2.2> 新增或更新成员至gitlab
            try {
                Member glMember = iRdmMemberService.tryRemoveAndAddMemberToGitlab(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());

                // <2.3> 回写数据库
                iRdmMemberService.updateMemberAfter(m, glMember);

                // <2.4> 发送事件
                if (isExists) {
                    iRdmMemberService.publishMemberEvent(m, MemberEvent.EventType.UPDATE_MEMBER);
                } else {
                    iRdmMemberService.publishMemberEvent(m, MemberEvent.EventType.ADD_MEMBER);
                }
            } catch (Exception e) {
                // 回写数据库错误消息
                logger.error(e.getMessage(), e);
                m.setSyncGitlabErrorMsg(e.getMessage());
                rdmMemberRepository.updateOptional(m, RdmMember.FIELD_SYNC_GITLAB_ERROR_MSG);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(Long organizationId, Long projectId, Long repositoryId, RdmMemberCreateDTO rdmMemberCreateDTO) {
        // <0> 转换
        final RdmMember param = rdmMemberAssembler.rdmMemberCreateDTOToRdmMember(organizationId, projectId, repositoryId, rdmMemberCreateDTO);

        // <1> 数据库预更新成员, 发起新事务
        self().addMemberBeforeRequestsNew(param);

        // <2> 调用gitlab api更新成员
        Member glMember = iRdmMemberService.tryRemoveAndAddMemberToGitlab(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());

        // <3> 回写数据库
        iRdmMemberService.updateMemberAfter(param, glMember);

        // <4> 发送事件
        iRdmMemberService.publishMemberEvent(param, MemberEvent.EventType.ADD_MEMBER);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMember(Long memberId, RdmMemberUpdateDTO rdmMemberUpdateDTO) {
        // <0> 转换
        final RdmMember param = ConvertUtils.convertObject(rdmMemberUpdateDTO, RdmMember.class);
        param.setId(memberId);

        // 获取数据库成员
        RdmMember dbMember = rdmMemberRepository.selectByPrimaryKey(memberId);

        param.setGlProjectId(dbMember.getGlProjectId());
        param.setGlUserId(dbMember.getGlUserId());
        param.setUserId(dbMember.getUserId());

        param.setOrganizationId(dbMember.getOrganizationId());
        param.setProjectId(dbMember.getProjectId());
        param.setRepositoryId(dbMember.getRepositoryId());

        // 设置过期标识
        param.setExpiredFlag(dbMember.checkExpiredFlag());
        // 设置同步标识
        param.setSyncGitlabFlag(dbMember.getSyncGitlabFlag());

        // <1> 数据库预更新成员, 发起新事务
        self().updateMemberBeforeRequestsNew(param);

        // <2> 调用gitlab api更新成员
        Member glMember = iRdmMemberService.tryRemoveAndAddMemberToGitlab(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());

        // <3> 回写数据库
        iRdmMemberService.updateMemberAfter(param, glMember);

        // <4> 发送事件
        iRdmMemberService.publishMemberEvent(param, MemberEvent.EventType.UPDATE_MEMBER);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long memberId) {
        RdmMember dbMember = rdmMemberRepository.selectByPrimaryKey(memberId);

        // <1> 数据库更新成员, 预删除, 发起新事务
        self().updateMemberBeforeRequestsNew(dbMember);

        // <2> 调用gitlab api删除成员
        iRdmMemberService.tryRemoveMemberToGitlab(dbMember.getGlProjectId(), dbMember.getGlUserId());

        // <3> 数据库删除成员
        rdmMemberRepository.deleteByPrimaryKey(dbMember.getId());

        // <4> 发送事件
        iRdmMemberService.publishMemberEvent(dbMember, MemberEvent.EventType.REMOVE_MEMBER);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncMember(Long memberId) {
        // <1> 查询数据库成员
        RdmMember dbMember = rdmMemberRepository.selectByPrimaryKey(memberId);

        // <2> 拉取Gitlab成员, 同步到db
        iRdmMemberService.syncMemberFromGitlab(dbMember);

        // <3> 发送事件
        iRdmMemberService.publishMemberEvent(dbMember, MemberEvent.EventType.SYNC_MEMBER);
    }

    @Override
    @ExcelExport(value = MemberExportDTO.class, groups = MemberExportDTO.GroupProject.class)
    public Page<MemberExportDTO> export(Long projectId, PageRequest pageRequest, RdmMemberQueryDTO query, ExportParam exportParam, HttpServletResponse response) {
        Page<RdmMemberViewDTO> page = this.pageByOptions(projectId, pageRequest, query);

        Page<MemberExportDTO> exportDTOPage = ConvertUtils.convertPage(page, dto -> {
            MemberExportDTO exportDTO = new MemberExportDTO();
            BeanUtils.copyProperties(dto, exportDTO);
            exportDTO.setRealName(dto.getUser().getRealName());
            exportDTO.setLoginName(dto.getUser().getLoginName());
            exportDTO.setCreatedByName(dto.getCreatedUser().getRealName());
            exportDTO.setGlAccessLevel(dto.getGlAccessLevel() == null ? null : RdmAccessLevel.forValue(dto.getGlAccessLevel()).toDesc());
            exportDTO.setSyncGitlabFlag(dto.getSyncGitlabFlag() ? "已同步" : "未同步"); // TODO 待hzero导出组件修复
            return exportDTO;
        });

        return exportDTOPage;
    }

    @Override
    @ExcelExport(value = MemberExportDTO.class, groups = MemberExportDTO.GroupOrg.class)
    public Page<MemberExportDTO> exportOnOrg(Long organizationId, PageRequest pageRequest, RdmMemberQueryDTO query, ExportParam exportParam, HttpServletResponse response) {
        Page<RdmMemberViewDTO> page = this.pageByOptionsOnOrg(organizationId, pageRequest, query);

        Page<MemberExportDTO> exportDTOPage = ConvertUtils.convertPage(page, dto -> {
            MemberExportDTO exportDTO = new MemberExportDTO();
            BeanUtils.copyProperties(dto, exportDTO);
            exportDTO.setRealName(dto.getUser().getRealName());
            exportDTO.setLoginName(dto.getUser().getLoginName());
            exportDTO.setCreatedByName(dto.getCreatedUser().getRealName());
            exportDTO.setProjectName(dto.getProject().getProjectName());
            exportDTO.setGlAccessLevel(dto.getGlAccessLevel() == null ? null : RdmAccessLevel.forValue(dto.getGlAccessLevel()).toDesc());
            exportDTO.setSyncGitlabFlag(dto.getSyncGitlabFlag() ? "已同步" : "未同步"); // TODO 待hzero导出组件修复
            return exportDTO;
        });

        return exportDTOPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleExpiredMembers() {
        // <1> 查询已过期的成员
        Condition condition = new Condition(RdmMember.class);
        condition.createCriteria().andLessThanOrEqualTo(RdmMember.FIELD_GL_EXPIRES_AT, new Date());
        List<RdmMember> expiredRdmMembers = rdmMemberRepository.selectByCondition(condition);

        // <2> 处理过期成员
        iRdmMemberService.batchExpireMembers(expiredRdmMembers);
    }

    /**
     * 批量预新增或修改, 使用一个新事务
     *
     * @param rdmMembers
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void batchAddOrUpdateMembersBeforeRequestsNew(List<RdmMember> rdmMembers) {
        // <1> 数据库添加成员, 已存在需要更新
        iRdmMemberService.batchAddOrUpdateMembersBefore(rdmMembers);
    }

    /**
     * 数据库预更新成员, 发起一个新事务
     *
     * @param rdmMember
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateMemberBeforeRequestsNew(RdmMember rdmMember) {
        iRdmMemberService.updateMemberBefore(rdmMember);
    }

    /**
     * 数据库预新增成员, 发起一个新事务
     *
     * @param rdmMember
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void addMemberBeforeRequestsNew(RdmMember rdmMember) {
        iRdmMemberService.insertMemberBefore(rdmMember);
    }


}
