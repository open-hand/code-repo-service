package org.hrds.rducm.gitlab.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.api.controller.dto.*;
import org.hrds.rducm.gitlab.api.controller.dto.export.MemberExportDTO;
import org.hrds.rducm.gitlab.app.assembler.RdmMemberAssembler;
import org.hrds.rducm.gitlab.app.async.RdmMemberQueryHelper;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.payload.GroupMemberPayload;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.hrds.rducm.gitlab.infra.enums.RdmAccessLevel;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.hrds.rducm.gitlab.infra.mapper.RdmMemberMapper;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.AssertUtils;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    private TransactionalProducer producer;

    @Autowired
    private GitlabGroupApi gitlabGroupApi;

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;


    public RdmMemberAppServiceImpl(RdmMemberRepository rdmMemberRepository) {
        this.rdmMemberRepository = rdmMemberRepository;
    }

    @Override
    public Page<RdmMemberViewDTO> pageByOptions(Long projectId, PageRequest pageRequest, RdmMemberQueryDTO query) {
        Page<RdmMember> page = PageHelper.doPageAndSort(pageRequest, () -> listRdmMemberByOptions(projectId, query));
        return rdmMemberAssembler.pageToRdmMemberViewDTO(page, ResourceLevel.PROJECT, projectId);
    }

    @Override
    public List<RdmMemberViewDTO> listByOptions(Long projectId, RdmMemberQueryDTO query) {
        List<RdmMember> list = listRdmMemberByOptions(projectId, query);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        Page<RdmMember> rdmMemberPage = new Page<>();
        rdmMemberPage.setContent(list);
        Page<RdmMemberViewDTO> page = rdmMemberAssembler.pageToRdmMemberViewDTO(rdmMemberPage, ResourceLevel.PROJECT, projectId);
        return page.getContent();
    }

    public List<RdmMember> listRdmMemberByOptions(Long projectId, RdmMemberQueryDTO query) {
        // <1> 封装查询条件
        String repositoryName = query.getRepositoryName();
        String realName = query.getRealName();
        String loginName = query.getLoginName();
        Set<Long> repositoryIds = query.getRepositoryIds();
        String params = query.getParams();
        Boolean enabled = query.getEnabled();
        Boolean syncGitlabFlag = query.getSyncGitlabFlag();
        Boolean glExpiresFlag = query.getGlExpiresFlag();

        Condition condition = Condition.builder(RdmMember.class)
                .where(Sqls.custom()
                        .andEqualTo(RdmMember.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(RdmMember.FIELD_SYNC_GITLAB_FLAG, syncGitlabFlag, true))
                .orderByDesc(RdmMember.FIELD_LAST_UPDATE_DATE)
                .build();

        // TODO 可使用多线程优化
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (Objects.nonNull(glExpiresFlag)) {
            Date now = new Date();
            if (glExpiresFlag) {
                condition.and().andIsNotNull(RdmMember.FIELD_GL_EXPIRES_AT).andLessThan(RdmMember.FIELD_GL_EXPIRES_AT, now);
            } else {
                condition.and().andIsNull(RdmMember.FIELD_GL_EXPIRES_AT).orGreaterThanOrEqualTo(RdmMember.FIELD_GL_EXPIRES_AT, now);
            }
        }

        // 调用外部接口模糊查询 用户名或登录名
        if (!StringUtils.isEmpty(realName) || !StringUtils.isEmpty(loginName) || Objects.nonNull(enabled)) {
            Set<Long> userIdsSet = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevelAndEnabled(projectId, realName, loginName, enabled);

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

            condition.and().orIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIdSet).orIsNull(RdmMember.FIELD_REPOSITORY_ID);
        }

        // 根据params多条件查询
        if (!StringUtils.isEmpty(params)) {
            Set<Long> userIdsSet1 = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevelAndEnabled(projectId, params, null, enabled);
            Set<Long> userIdsSet2 = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevelAndEnabled(projectId, null, params, enabled);
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
                condition.and().andIn(RdmMember.FIELD_USER_ID, userIdsSet);
                condition.and().orIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIdSet).orIsNull(RdmMember.FIELD_REPOSITORY_ID);
            } else if (!userIsEmpty) {
                // 用户查询不为空
                condition.and().andIn(RdmMember.FIELD_USER_ID, userIdsSet);
            } else {
                // 应用服务查询不为空
                condition.and().orIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIdSet).orIsNull(RdmMember.FIELD_REPOSITORY_ID);
            }
        }
        if (!CollectionUtils.isEmpty(repositoryIds)) {
            condition.and().orIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIds).orIsNull(RdmMember.FIELD_REPOSITORY_ID);
        }
        stopWatch.stop();
        logger.info(stopWatch.prettyPrint());

        return rdmMemberRepository.selectByCondition(condition);
    }

    @Override
    public Page<RdmMemberViewDTO> pageByOptionsOnOrg(Long organizationId, PageRequest pageRequest, RdmMemberQueryDTO query) {
        // <1> 封装查询条件
        String repositoryName = query.getRepositoryName();
        String realName = query.getRealName();
        String loginName = query.getLoginName();
        String params = query.getParams();
        Set<Long> projectIds = query.getProjectIds();
        Set<Long> repositoryIds = query.getRepositoryIds();

        Condition condition = Condition.builder(RdmMember.class)
                .where(Sqls.custom()
                        .andEqualTo(RdmMember.FIELD_ORGANIZATION_ID, organizationId)
                        .andIn(RdmMember.FIELD_PROJECT_ID, projectIds, true)
                        .andIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIds, true))
                .build();

        Set<Long> userIdsSet = new HashSet<>();

        // 根据params多条件查询
        if (!StringUtils.isEmpty(params)) {
            Set<Long> userIdsSet1 = c7NBaseServiceFacade.listC7nUserIdsByNameOnOrgLevel(organizationId, params, null);
            Set<Long> userIdsSet2 = c7NBaseServiceFacade.listC7nUserIdsByNameOnOrgLevel(organizationId, null, params);
            userIdsSet.addAll(userIdsSet1);
            userIdsSet.addAll(userIdsSet2);
        }

        // 调用外部接口模糊查询 用户名或登录名
        if (!StringUtils.isEmpty(realName) || !StringUtils.isEmpty(loginName)) {
            Set<Long> userIdsSet3 = c7NBaseServiceFacade.listProjectsC7nUserIdsByNameOnOrgLevel(organizationId, realName, loginName);
            userIdsSet.addAll(userIdsSet3);
            if (userIdsSet.isEmpty()) {
                return new Page<>();
            }

        }
        if (!CollectionUtils.isEmpty(userIdsSet)) {
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

        return rdmMemberAssembler.pageToRdmMemberViewDTO(page, ResourceLevel.ORGANIZATION, null);
    }

    /**
     * 批量新增或修改成员
     *
     * @param projectId
     * @param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = SagaTopicCodeConstants.BATCH_ADD_GITLAB_MEMBER,
            description = "批量添加用户的应用服务权限", inputSchema = "{}")
    public void batchAddOrUpdateMembers(Long organizationId, Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO) {
        // <0> 校验入参 + 转换
        List<RdmMember> rdmMembers = rdmMemberAssembler.rdmMemberBatchDTOToRdmMembers(organizationId, projectId, rdmMemberBatchDTO);

        // <1> 数据库添加成员, 已存在需要更新, 发起一个新事务
        // 开启新事务的目的是使这一步操作独立执行, 保证预操作成功
        self().batchAddOrUpdateMembersBeforeRequestsNew(rdmMembers);

        // <2> 调用gitlab api添加成员
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("batchAddOrUpdateMembers")
                        .withSagaCode(SagaTopicCodeConstants.BATCH_ADD_GITLAB_MEMBER),
                builder -> {
                    builder
                            .withPayloadAndSerialize(rdmMembers)
                            .withRefId(String.valueOf(projectId))
                            .withSourceId(projectId);
                    return rdmMembers;
                });


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveMembers(Long organizationId, Long projectId, Set<Long> memberIds) {
        if (CollectionUtils.isEmpty(memberIds)) {
            return;
        }
        List<RdmMember> rdmMembers = rdmMemberRepository.selectByCondition(Condition.builder(RdmMember.class)
                .andWhere(Sqls.custom().andIn(RdmMember.FIELD_ID, memberIds))
                .build());
        // <1> 数据库更新成员, 预删除, 发起新事务
        self().batchUpdateMemberBeforeRequestsNew(rdmMembers);
        rdmMembers.forEach(m -> {
            // <2> 调用gitlab api删除成员
            iRdmMemberService.tryRemoveMemberToGitlab(m.getGlProjectId(), m.getGlUserId());

            // <3> 数据库删除成员
            rdmMemberRepository.deleteByPrimaryKey(m.getId());

            // <4> 发送事件
            iRdmMemberService.publishMemberEvent(m, MemberEvent.EventType.REMOVE_MEMBER);
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

        // <1.1> 停用应用服务的过期成员权限不做删除
        Set<Long> repositoryIds = expiredRdmMembers.stream().map(RdmMember::getRepositoryId).collect(Collectors.toSet());
        List<C7nAppServiceVO> c7nAppServices = c7NDevOpsServiceFacade.listAppServiceByIds(repositoryIds);
        Set<Long> activeRepositoryIds = c7nAppServices.stream().filter(C7nAppServiceVO::getActive).map(C7nAppServiceVO::getId).collect(Collectors.toSet());
        expiredRdmMembers = expiredRdmMembers.stream().filter(a -> activeRepositoryIds.contains(a.getRepositoryId())).collect(Collectors.toList());

        // <2> 处理过期成员
        iRdmMemberService.batchExpireMembers(expiredRdmMembers);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RdmMember> batchInvalidMember(Long organizationId, Long projectId, Long repositoryId) {
        C7nAppServiceVO c7nAppServiceVO = c7NDevOpsServiceFacade.detailC7nAppServiceById(projectId, repositoryId);
        //应用服务为空，或应用服务启用状态则直接返回
        if (Objects.isNull(c7nAppServiceVO) || c7nAppServiceVO.getActive()) {
            return Collections.emptyList();
        }
        Condition condition = Condition.builder(RdmMember.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RdmMember.FIELD_ORGANIZATION_ID, organizationId)
                        .andEqualTo(RdmMember.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(RdmMember.FIELD_REPOSITORY_ID, repositoryId))
                .build();
        List<RdmMember> rdmMembers = rdmMemberRepository.selectByCondition(condition);
        // 过滤项目所有者和组织管理员角色的用户
        List<RdmMember> rdmMemberList = rdmMembers.stream().filter(a -> RdmAccessLevel.OWNER.value > a.getGlAccessLevel()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rdmMemberList)) {
            return Collections.emptyList();
        }
        rdmMemberList = rdmMemberList.stream().map(a -> a.setGlExpiresAt(new Date())).collect(Collectors.toList());
        //已同步到Gitlab的成员
        List<RdmMember> syncMembers = rdmMemberList.stream().filter(RdmMember::getSyncGitlabFlag).collect(Collectors.toList());
        //未同步到Gitlab的成员
        List<RdmMember> unSyncMembers = rdmMemberList.stream().filter(a -> !syncMembers.contains(a)).collect(Collectors.toList());

        for (RdmMember member : rdmMemberList) {
            // <2> 调用gitlab api删除成员
            iRdmMemberService.tryRemoveMemberToGitlab(member.getGlProjectId(), member.getGlUserId());
        }
        rdmMemberRepository.batchUpdateByPrimaryKeySelective(rdmMemberList);
        return rdmMemberList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RdmMember> batchValidMember(Long organizationId, Long projectId, Long repositoryId) {
        C7nAppServiceVO c7nAppServiceVO = c7NDevOpsServiceFacade.detailC7nAppServiceById(projectId, repositoryId);
        //应用服务为空，或应用服务停用状态则直接返回
        if (Objects.isNull(c7nAppServiceVO) || !c7nAppServiceVO.getActive()) {
            return Collections.emptyList();
        }
        Condition condition = Condition.builder(RdmMember.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RdmMember.FIELD_ORGANIZATION_ID, organizationId)
                        .andEqualTo(RdmMember.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(RdmMember.FIELD_REPOSITORY_ID, repositoryId))
                .build();
        List<RdmMember> rdmMembers = rdmMemberRepository.selectByCondition(condition);
        List<RdmMember> rdmMemberList = rdmMembers.stream().filter(a -> RdmAccessLevel.OWNER.value > a.getGlAccessLevel()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rdmMemberList)) {
            return Collections.emptyList();
        }
        rdmMemberList = rdmMemberList.stream().map(a -> a.setGlExpiresAt(null)).collect(Collectors.toList());

        for (RdmMember member : rdmMemberList) {
            // <2> 调用gitlab api删除成员
            iRdmMemberService.tryRemoveAndAddMemberToGitlab(member.getGlProjectId(), member.getGlUserId(), member.getGlAccessLevel(), null);
            member.setSyncGitlabFlag(true);
            member.setSyncGitlabDate(new Date());
        }
        rdmMemberRepository.batchUpdateByPrimaryKey(rdmMemberList);
        return rdmMemberList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncBatchMember(List<Long> memberIds) {
        if (CollectionUtils.isEmpty(memberIds)) {
            return;
        }
        memberIds.forEach(memberId -> {
            RdmMember dbMember = rdmMemberRepository.selectByPrimaryKey(memberId);
            if (Objects.isNull(dbMember)) {
                return;
            }
            if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(dbMember.getType(), AuthorityTypeEnum.PROJECT.getValue())) {
                syncMember(memberId);
            } else {
                syncGroupMember(memberId);
            }
        });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allSync(Long organizationId, Long projectId) {
        //查询项目下所有未同步的用户
        RdmMember rdmMember = new RdmMember();
        rdmMember.setProjectId(projectId);
        rdmMember.setSyncGitlabFlag(Boolean.FALSE);
        List<RdmMember> rdmMembers = rdmMemberRepository.select(rdmMember);
        if (CollectionUtils.isEmpty(rdmMembers)) {
            return;
        }
        rdmMembers.forEach(member -> {
            if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(member.getType(), AuthorityTypeEnum.PROJECT.getValue())) {
                syncMember(member.getId());
            } else {
                syncGroupMember(member.getId());
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = SagaTopicCodeConstants.BATCH_ADD_GROUP_MEMBER, description = "批量添加项目下gitlab group成员", inputSchemaClass = GroupMemberPayload.class, inputSchema = "{}")
    public void batchAddGroupMembers(Long organizationId, Long projectId, List<RdmMemberBatchDTO.GitlabMemberCreateDTO> gitlabMemberCreateDTOS) {
        //查询项目下的group
        C7nDevopsProjectVO c7nDevopsProjectVO = c7NDevOpsServiceFacade.detailDevopsProjectById(projectId);
        checkIamProject(c7nDevopsProjectVO);
        //校验是否已经存在
        checkGroupPermissionExist(projectId, gitlabMemberCreateDTOS);

        //检验这个gitlab group id 能不能查到group, 因为有些人手动删了group，自己又新建了group
        Group group = gitlabGroupApi.getGroup(Integer.valueOf(String.valueOf(c7nDevopsProjectVO.getGitlabGroupId())));
        checkProjectGroup(c7nDevopsProjectVO, group);
        //持久化到数据库
        persistenceMemberToDB(gitlabMemberCreateDTOS, organizationId, projectId, group.getId());
        //发送saga
        sendAddGroupMemberSaga(projectId, gitlabMemberCreateDTOS, c7nDevopsProjectVO);
    }

    private void checkGroupPermissionExist(Long projectId, List<RdmMemberBatchDTO.GitlabMemberCreateDTO> gitlabMemberCreateDTOS) {
        if (CollectionUtils.isEmpty(gitlabMemberCreateDTOS)) {
            throw new CommonException("error.group.member.is.null");
        }
        List<Long> userIds = gitlabMemberCreateDTOS.stream().map(RdmMemberBatchDTO.GitlabMemberCreateDTO::getUserId).collect(Collectors.toList());
        long count = userIds.stream().distinct().count();
        if (userIds.size() != count) {
            throw new CommonException("error.group.member.exist");
        }
        List<RdmMember> rdmMembers = rdmMemberRepository.groupMemberByUserId(projectId, userIds);
        if (!CollectionUtils.isEmpty(rdmMembers)) {
            throw new CommonException("error.group.member.exist");
        }

    }

    private void checkProjectGroup(C7nDevopsProjectVO c7nDevopsProjectVO, Group group) {
        AssertUtils.notNull(group, "error.gitlab.group.not.exist", c7nDevopsProjectVO.getGitlabGroupId());
    }

    private void checkIamProject(C7nDevopsProjectVO c7nDevopsProjectVO) {
        AssertUtils.notNull(c7nDevopsProjectVO, "error.devops.gitlab.project.not.exist");
        AssertUtils.notNull(c7nDevopsProjectVO.getGitlabGroupId(), "error.devops.group.id.is.null");
    }

    private void sendAddGroupMemberSaga(Long projectId, List<RdmMemberBatchDTO.GitlabMemberCreateDTO> gitlabMemberCreateDTOS, C7nDevopsProjectVO c7nDevopsProjectVO) {
        GroupMemberPayload groupMemberPayload = new GroupMemberPayload();
        groupMemberPayload.setgGroupId(Integer.valueOf(String.valueOf(c7nDevopsProjectVO.getGitlabGroupId())));
        groupMemberPayload.setGitlabMemberCreateDTOS(gitlabMemberCreateDTOS);

        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("batchAddGroupMembers")
                        .withSagaCode(SagaTopicCodeConstants.BATCH_ADD_GROUP_MEMBER),
                builder -> {
                    builder
                            .withPayloadAndSerialize(groupMemberPayload)
                            .withRefId(String.valueOf(projectId))
                            .withSourceId(projectId);
                    return groupMemberPayload;
                });
    }

    private void persistenceMemberToDB(List<RdmMemberBatchDTO.GitlabMemberCreateDTO> gitlabMemberCreateDTOS, Long organizationId, Long projectId, Integer groupId) {
        List<RdmMember> rdmMembers = new ArrayList<>();
        //将用户id转换为Gitlab用户Id
        gitlabMemberCreateDTOS.forEach(gitlabMemberCreateDTO -> {
            Integer glUserId = c7NBaseServiceFacade.userIdToGlUserId(gitlabMemberCreateDTO.getUserId());
            if (Objects.isNull(glUserId)) {
                return;
            }
            gitlabMemberCreateDTO.setgUserId(glUserId);

            //插入数据库
            RdmMember rdmMember = new RdmMember();
            rdmMember.setOrganizationId(organizationId);
            rdmMember.setProjectId(projectId);
            rdmMember.setType(AuthorityTypeEnum.GROUP.getValue());
            // 设置gitlab项目id和用户id
            rdmMember.setGlUserId(glUserId);
            rdmMember.setgGroupId(groupId);
            rdmMember.setUserId(gitlabMemberCreateDTO.getUserId());
            rdmMember.setSyncGitlabFlag(false);
            rdmMember.setGlAccessLevel(null);
            rdmMember.setGlExpiresAt(null);
            rdmMembers.add(rdmMember);
        });
        //批量插入数据库
        rdmMemberRepository.batchInsert(rdmMembers);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGroupMember(Long organizationId, Long projectId, RdmMemberBatchDTO.GitlabMemberCreateDTO gitlabMemberCreateDTO, Long rducmGitlabMemberId) {
        RdmMember rdmMember = rdmMemberRepository.selectByPrimaryKey(rducmGitlabMemberId);
        AssertUtils.notNull(rdmMember, "error.rdmMember.is.not.exist");
        AssertUtils.notNull(rdmMember.getgGroupId(), "error.group.id.is.null");
        if (!rdmMember.getSyncGitlabFlag()) {
            throw new CommonException("error.sync.flag.false");
        }
        //查询group
        Group group = gitlabGroupApi.getGroup(rdmMember.getgGroupId());
        AssertUtils.notNull(group, "error.gitlab.group.not.exist", gitlabMemberCreateDTO.getgGroupId());

        //删除添加
        iRdmMemberService.tryRemoveAndAddGroupMemberToGitlab(group.getId(), rdmMember.getGlUserId(), gitlabMemberCreateDTO.getGlAccessLevel(), gitlabMemberCreateDTO.getGlExpiresAt());

        //跟新数据库
        rdmMember.setSyncGitlabFlag(true);
        rdmMember.setSyncGitlabDate(new Date());
        rdmMember.setGlAccessLevel(gitlabMemberCreateDTO.getGlAccessLevel());
        rdmMember.setGlExpiresAt(gitlabMemberCreateDTO.getGlExpiresAt());
        rdmMemberRepository.updateByPrimaryKey(rdmMember);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroupMember(Long organizationId, Long projectId, Long rducmGitlabMemberId) {
        RdmMember rdmMember = rdmMemberRepository.selectByPrimaryKey(rducmGitlabMemberId);
        AssertUtils.notNull(rdmMember, "error.rdmMember.is.null");
        if (!rdmMember.getSyncGitlabFlag()) {
            throw new CommonException("error.sync.flag.false");
        }
        //删除group的用户
        gitlabGroupFixApi.removeMember(rdmMember.getgGroupId(), rdmMember.getGlUserId());
        //删除数据库
        rdmMemberRepository.deleteByPrimaryKey(rducmGitlabMemberId);
        //删除组的权限的时候会把用户在组下所有应用服务的权限删除
        RdmMember record = new RdmMember();
        record.setUserId(rdmMember.getUserId());
        record.setProjectId(projectId);
        record.setType(AuthorityTypeEnum.PROJECT.getValue());
        rdmMemberRepository.delete(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncGroupMember(Long rducmGitlabMemberId) {
        // <1> 查询数据库成员
        RdmMember dbMember = rdmMemberRepository.selectByPrimaryKey(rducmGitlabMemberId);

        // <2> 拉取Gitlab成员, 同步到db
        iRdmMemberService.syncGroupMemberFromGitlab(dbMember);

        // <3> 发送事件
//        iRdmMemberService.publishMemberEvent(dbMember, MemberEvent.EventType.SYNC_MEMBER);
    }

    @Override
    public RdmMember getGroupMember(Long organizationId, Long projectId, Long userId) {
        RdmMember rdmMember = new RdmMember();
        rdmMember.setUserId(userId);
        rdmMember.setType(AuthorityTypeEnum.GROUP.getValue());
        rdmMember.setProjectId(projectId);
        return rdmMemberRepository.selectOne(rdmMember);
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
     * 数据库批量预更新成员, 发起一个新事务
     *
     * @param rdmMembers
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void batchUpdateMemberBeforeRequestsNew(List<RdmMember> rdmMembers) {
        rdmMembers.forEach(m -> iRdmMemberService.updateMemberBefore(m));
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
