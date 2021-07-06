package org.hrds.rducm.gitlab.app.service.impl;

import java.time.Duration;
import java.util.*;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.MemberAuditLogRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.AssertExtensionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.CollectionUtils;

/**
 * 成员权限审计应用服务默认实现
 * 包含审计相关的各个功能
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
@Service
public class RdmMemberAuditAppServiceImpl implements RdmMemberAuditAppService {
    private static final Logger logger = LoggerFactory.getLogger(RdmMemberAuditAppServiceImpl.class);

    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;
    @Autowired
    private IRdmMemberService iRdmMemberService;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private C7nDevOpsServiceFacade c7NDevOpsServiceFacade;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;
    @Autowired
    private GitlabProjectApi gitlabProjectApi;
    @Autowired
    private GitlabGroupApi gitlabGroupApi;
    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;
    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;
    @Autowired
    private MemberAuditLogRepository memberAuditLogRepository;
    @Autowired
    private IRdmMemberAuditRecordService iRdmMemberAuditRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public void syncByStrategy(Long id, int syncStrategy) {
        RdmMemberAuditRecord dbRecord = rdmMemberAuditRecordRepository.selectByPrimaryKey(id);
        AssertExtensionUtils.notNull(dbRecord, "该记录不存在");

        Long organizationId = dbRecord.getOrganizationId();
        Long projectId = dbRecord.getProjectId();
        Long repositoryId = dbRecord.getRepositoryId();

        // <1> 判断同步策略
        switch (syncStrategy) {
            case 1:
                // 本系统 -> Gitlab
                syncMemberToGitlabStrategy(organizationId,
                        projectId,
                        repositoryId,
                        dbRecord.getUserId(),
                        dbRecord.getGlProjectId(),
                        dbRecord.getGlUserId(),
                        dbRecord.getAccessLevel(),
                        dbRecord.getExpiresAt());
                break;
            case 2:
                // Gitlab -> 本系统
                syncMemberFromGitlabStrategy(organizationId,
                        projectId,
                        repositoryId,
                        dbRecord.getUserId(),
                        dbRecord.getGlProjectId(),
                        dbRecord.getGlUserId(),
                        dbRecord.getGlAccessLevel(),
                        dbRecord.getGlExpiresAt());
                break;
            default:
                break;
        }

        // <2> 同步结束, 设置审计记录同步标识为true
        rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAuditFix(Long organizationId, Long projectId, Set<Long> recordIds) {
        if (CollectionUtils.isEmpty(recordIds)) {
            return;
        }
        recordIds.forEach(recordId -> {
            auditFix(organizationId, projectId, 0L, recordId);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void projectAudit(Long organizationId, Long projectId) {
        // <1> 保存审计记录
        Date startDate = new Date();
        List<RdmMemberAuditRecord> records = iRdmMemberAuditRecordService.batchCompareProject(organizationId, projectId);
        Date endDate = new Date();

        //插入项目的审计日志
        String auditNo = UUID.randomUUID().toString();
        MemberAuditLog log = new MemberAuditLog();
        log.setOrganizationId(organizationId);
        log.setProjectId(projectId);
        log.setAuditNo(auditNo);
        log.setAuditCount(records == null ? 0 : records.size());
        log.setAuditStartDate(startDate);
        log.setAuditEndDate(endDate);
        log.setAuditDuration(Math.toIntExact(Duration.between(startDate.toInstant(), endDate.toInstant()).toMillis()));
        memberAuditLogRepository.insertSelective(log);
    }

    @Override
    public void auditFix(Long organizationId, Long projectId, Long repositoryId, Long id) {
        logger.debug(">>>>{}>>>{}>>>>{}>>>>{}>", organizationId, projectId, repositoryId, id);
        RdmMemberAuditRecord dbRecord = null;
        if (repositoryId.longValue() == 0l) {
            dbRecord = rdmMemberAuditRecordRepository.selectByPrimaryKey(id);
        } else {
            dbRecord = rdmMemberAuditRecordRepository.selectByUk(organizationId, projectId, repositoryId, id);
        }
        AssertExtensionUtils.notNull(dbRecord, "该记录不存在");

        Long userId = dbRecord.getUserId();
        //如果userId为null 猪齿鱼导入用户失败，导致猪齿鱼里没有这个用户
        if (Objects.isNull(userId)) {
            //如果userId不存在，这个数据就是异常的数据，那么就删除
            rdmMemberAuditRecordRepository.deleteByPrimaryKey(dbRecord.getId());
            return;
        }
        //首先判断项目是不是存在，不存在则不修,没有默认分支就认为它是空库
//        Project project = gitlabProjectFixApi.getProject(repositoryId.intValue());
//        if (Objects.isNull(project) && StringUtils.isEmpty(project.getDefaultBranch())) {
//            //同步这条记录
//            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
//            return;
//        }

        // 若glUserId为null, 获取glUserId
        Integer glUserId = dbRecord.getGlUserId() != null ? dbRecord.getGlUserId() : c7NBaseServiceFacade.userIdToGlUserId(userId);
        if (Objects.isNull(glUserId)) {
            rdmMemberAuditRecordRepository.deleteByPrimaryKey(dbRecord.getId());
            return;
        }
        Integer glProjectId = dbRecord.getGlProjectId();
        C7nDevopsProjectVO c7nDevopsProjectVO = c7NDevOpsServiceFacade.detailDevopsProjectById(projectId);
        Integer glGroupId = Math.toIntExact(c7nDevopsProjectVO.getGitlabGroupId());

        // 查询权限
        RdmMember dbMember = rdmMemberRepository.selectOneByUk(projectId, repositoryId, userId);
        // 查询用户在gitlab中project的权限
        Member projectGlMember = gitlabProjectFixApi.getMember(glProjectId, glUserId);
        if (Objects.nonNull(projectGlMember)) {
            logger.debug("Gl项目[{}]权限，ID为[{}],用户名[{}]的权限级别[{}]", glProjectId, projectGlMember.getId(), projectGlMember.getName(), projectGlMember.getAccessLevel());
        }
        // 查询用户在gitlab中group的权限
        Member groupGlMember = gitlabGroupFixApi.getMember(glGroupId, glUserId);
        if (Objects.nonNull(groupGlMember)) {
            logger.debug("Gl组[{}]权限，ID为[{}],用户名[{}]的权限级别[{}]", glGroupId, groupGlMember.getId(), groupGlMember.getName(), groupGlMember.getAccessLevel());
        }

        // 判断是否是组织管理员
        Boolean isOrgAdmin = c7NBaseServiceFacade.checkIsOrgAdmin(organizationId, userId);
        logger.debug("用户[{}]是否为组织[{}]管理员[{}]", userId, organizationId, isOrgAdmin);

        // 判断是否是项目团队成员
        C7nUserVO c7nUserVO = c7NBaseServiceFacade.detailC7nUserOnProjectLevel(projectId, userId);
        boolean isProjectMember = c7nUserVO != null;
        logger.debug("用户[{}]是否为项目[{}]的成员[{}]", userId, projectId, isProjectMember);

        // <> 按gitlab group和project两类情况讨论
        // 是否为组织管理员

        if (isOrgAdmin) {
            // 修复为group Owner权限
            logger.debug("修复用户[{}]为组织管理员权限", userId);
            updateGitlabGroupMemberWithOwner(groupGlMember, glGroupId, glUserId);
        } else {
            // 是否为团队成员
            if (isProjectMember) {
                // 如果是团队成员
                // 是否是项目管理员
                Boolean isProjectAdmin = c7nUserVO.isProjectAdmin();
                if (isProjectAdmin) {
                    // 如果是项目管理员 修复为group Owner权限
                    logger.debug("修复用户[{}]为项目管理员权限", userId);
                    if (Objects.isNull(dbMember) && !Objects.isNull(groupGlMember)) {
                        RdmMember rdmMember = new RdmMember();
                        rdmMember.setSyncGitlabFlag(true);
                        rdmMember.setGlAccessLevel(AccessLevel.OWNER.toValue());
                        rdmMember.setProjectId(projectId);
                        rdmMember.setUserId(userId);
                        rdmMember.setRepositoryId(repositoryId);
                        rdmMember.setOrganizationId(organizationId);
                        rdmMember.setGlProjectId(glProjectId);
                        rdmMember.setGlUserId(glUserId);
                        rdmMemberRepository.insert(rdmMember);
                    }
                    updateGitlabGroupMemberWithOwner(groupGlMember, glGroupId, glUserId);

                } else {
                    handProjectMember(userId, glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember, dbRecord);
                }
            } else {
                handNonProjectMember(userId, glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember, dbRecord);
            }
        }

        // <2> 修复结束, 设置审计记录同步标识为true
        rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
    }


    private void handNonProjectMember(Long userId, Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member projectGlMember, Member groupGlMember, RdmMemberAuditRecord dbRecord) {
        if (Objects.isNull(dbMember)) {
            // 如果不是团队成员,也不是赋予权限的项目外成员 移除gitlab权限
            logger.debug("用户[{}]是项目成员也不是外部成员，没有代码库权限，移除", userId);
            if (groupGlMember != null) {
                gitlabGroupFixApi.removeMember(glGroupId, glUserId);
            } else if (projectGlMember != null) {
                gitlabProjectFixApi.removeMember(glProjectId, glUserId);
            }
        } else {
            logger.debug("用户[{}]是外部成员，有代码库权限，修复GL权限", userId);
            updateGitLabPermission(glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember, dbRecord);
        }
    }

    private void handProjectMember(Long userId, Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member projectGlMember, Member groupGlMember, RdmMemberAuditRecord dbRecord) {
        //如果不是项目管理员，修复为该用户当前的代码库数据库的权限
        if (dbMember == null || !dbMember.getSyncGitlabFlag()) {
            // 如果dbMember为null 或者同步失败 移除gitlab权限
            logger.debug("用户[{}]为项目成员，但没有代码库权限，移除Gl权限", userId);
            removeGitlabMemberGP(glUserId, glProjectId, glGroupId, projectGlMember, groupGlMember);
        } else {
            // 修改gitlab权限
            logger.debug("用户[{}]为项目成员，有代码库权限，修复Gl权限", userId);
            updateGitLabPermission(glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember, dbRecord);
        }
    }

    private void updateGitLabPermission(Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member projectGlMember, Member groupGlMember, RdmMemberAuditRecord dbRecord) {
        if (groupGlMember != null) {
            //如果组的权限是owner，则不作处理， 组的权限是owner 他在项目的权限也是owner,这个时候需要按照gotlab的权限来修
            if (groupGlMember.getAccessLevel().value.intValue() == AccessLevel.OWNER.toValue().intValue()) {
                dbMember.setSyncGitlabFlag(Boolean.TRUE);
                dbMember.setGlAccessLevel(AccessLevel.OWNER.toValue().intValue());
                rdmMemberRepository.updateByPrimaryKey(dbMember);
                rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                return;
            }
            //如果组的权限存在，先移除组的权限（随之项目的权限也会被移除）
            //remove的时候注意  一个组至少存在一个owner, 如果删除返回403则不处理
            gitlabGroupFixApi.removeMember(glGroupId, glUserId);
            //然后如果同步成功，按照choerodon来修复，并且choerodon中为其赋予了权限并且同步成功了
            if (dbMember.getSyncGitlabFlag() && !Objects.isNull(dbMember.getGlAccessLevel())) {
                //上一步删除权限可以没有删掉 这里添加可能会报："should be higher than Owner inherited membership from group
                //如果上一步删除组的权限没有删掉，这里就不给
                gitlabProjectFixApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
                rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                return;
            } else {
                //如果同步失败， 直接删掉这条数据
                rdmMemberRepository.deleteByPrimaryKey(dbMember);
                //如果项目成员的角色存在也直接删掉
                if (!Objects.isNull(projectGlMember)) {
                    gitlabProjectFixApi.removeMember(glProjectId, glUserId);
                }
            }
            //如果gitlab组的权限存在，且是同步失败的
        } else {
            //如果gitlab组的权限为null,
            if (projectGlMember != null) {
                // gitlab项目的权限不为null
                // 2 如果同步失败的用户或者权限小于50,按照gitlab的权限来修复
                //这里为项目变更权限的时候需要注意，如果数据库的用户的权限是50，这里按照gitlab的权限来修复。
                //如果用户是同步失败了的， AccessLevel为null
                if (!dbMember.getSyncGitlabFlag() || Objects.isNull(dbMember.getGlAccessLevel())) {
                    dbMember.setGlAccessLevel(projectGlMember.getAccessLevel().value);
                    dbMember.setSyncGitlabFlag(Boolean.TRUE);
                    rdmMemberRepository.updateByPrimaryKey(dbMember);
                    rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                    return;
                }
                //同步成功的 组里面没有角色 gitlab的AccessLevel只可能小于50  就按照choerodon来修数据
                if (dbMember.getGlAccessLevel() < 50) {
                    gitlabProjectFixApi.updateMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
                } else {
                    dbMember.setGlAccessLevel(projectGlMember.getAccessLevel().value);
                    rdmMemberRepository.updateByPrimaryKey(dbMember);
                }

            } else {
                // 如果gitlab组的权限为null,gitlab项目的权限也为null
                // 如果在choerodon是同步成功的 权限小于50，则按照choerodon来修复权限
                if (dbMember.getSyncGitlabFlag() && dbMember.getGlAccessLevel() < 50) {
                    gitlabProjectFixApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
                    rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                    return;
                }
                //如果在gitlab 一个权限也没有，在choerodon又是同步失败的，则直接删除这种数据
                //如果如果gitlab组的权限为null,gitlab项目的权限也为null，同步成功，且权限>50，这种数据也删除
                else {
                    rdmMemberRepository.deleteByPrimaryKey(dbMember);
                }
            }
        }
    }

    private void removeGitlabMemberGP(Integer glUserId, Integer glProjectId, Integer glGroupId, Member projectGlMember, Member groupGlMember) {
        if (groupGlMember != null) {
            gitlabGroupFixApi.removeMember(glGroupId, glUserId);
        } else if (projectGlMember != null) {
            gitlabProjectFixApi.removeMember(glProjectId, glUserId);
        }
    }

    /**
     * 移除gitlab权限, 分两步
     * 1. 如果group有权限, 直接移除group的权限(移除group权限会将project权限也移除); 否则,进入2
     * 2. 如果project有权限, 移除project的权限
     */
//    private void removeGitlabMemberGP(Member groupGlMember, Member projectGlMember, Integer glGroupId, Integer glProjectId, Integer glUserId) {
//        if (groupGlMember != null) {
//            gitlabGroupApi.removeMember(glGroupId, glUserId);
//        } else if (projectGlMember != null) {
//            gitlabProjectApi.removeMember(glProjectId, glUserId);
//        }
//    }

    /**
     * 更新gitlab权限, 分两步
     * 1. 如果group有权限, 先移除group的权限, 再新增(移除group权限会将project权限也移除); 否则, 进入2
     * 2. 如果project有权限, 更新project的权限; 否则, 进入3
     * 3. 如果project无权限, 新增project的权限
     */
//    private void updateGitlabMemberP(RdmMember dbMember, Member groupGlMember, Member projectGlMember, Integer glGroupId, Integer glProjectId, Integer glUserId) {
//        // 1
//        if (groupGlMember != null) {
//            gitlabGroupApi.removeMember(glGroupId, glUserId);
//
//            gitlabProjectApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
//        } else {
//            if (projectGlMember != null) {
//                // 2
//                gitlabProjectApi.updateMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
//            } else {
//                // 3
//                gitlabProjectApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
//            }
//        }
//    }

    /**
     * 修改Gitlab group权限为Owner, 分2步
     * 如果group成员不存在, 新增Owner
     * 如果group成员存在, 更新为Owner
     *
     * @param groupGlMember
     * @param glGroupId
     * @param glUserId
     */
    private void updateGitlabGroupMemberWithOwner(Member groupGlMember, Integer glGroupId, Integer glUserId) {
        if (groupGlMember == null) {
            // 添加
            gitlabGroupFixApi.addMember(glGroupId, glUserId, AccessLevel.OWNER.toValue(), null);
        } else if (!groupGlMember.getAccessLevel().toValue().equals(AccessLevel.OWNER.toValue())) {
            // 更新
            gitlabGroupFixApi.updateMember(glGroupId, glUserId, AccessLevel.OWNER.toValue(), null);
        }
    }


    private void syncMemberToGitlabStrategy(Long organizationId,
                                            Long projectId,
                                            Long repositoryId,
                                            Long userId,
                                            Integer glProjectId,
                                            Integer glUserId,
                                            Integer accessLevel,
                                            Date expiresAt) {
        // 本系统 -> Gitlab
        if (userId == null) {
            // 移除Gitlab成员
            iRdmMemberService.tryRemoveMemberToGitlab(glProjectId, glUserId);
        } else {
            // 若glUserId为null, 获取glUserId
            glUserId = glUserId != null ? glUserId : c7NBaseServiceFacade.userIdToGlUserId(userId);

            // 更新Gitlab成员
            iRdmMemberService.tryRemoveAndAddMemberToGitlab(glProjectId, glUserId, accessLevel, expiresAt);
        }
    }

    private void syncMemberFromGitlabStrategy(Long organizationId,
                                              Long projectId,
                                              Long repositoryId,
                                              Long userId,
                                              Integer glProjectId,
                                              Integer glUserId,
                                              Integer accessLevel,
                                              Date expiresAt) {
        // Gitlab -> 本系统
        RdmMember dbMember = new RdmMember();
        if (userId == null) {
            // 同步并新增
            // 查询Gitlab用户对应的userId
            userId = c7NDevOpsServiceFacade.glUserIdToUserId(glUserId);

            dbMember.setOrganizationId(organizationId)
                    .setProjectId(projectId)
                    .setRepositoryId(repositoryId)
                    .setUserId(userId)
                    .setGlProjectId(glProjectId)
                    .setGlUserId(glUserId)
                    .setGlAccessLevel(accessLevel)
                    .setGlExpiresAt(expiresAt)
                    .setSyncGitlabFlag(Boolean.TRUE)
                    .setSyncGitlabDate(new Date());

            // 重新插入
            rdmMemberRepository.insertSelective(dbMember);
        } else {
            // 同步并修改
            dbMember = rdmMemberRepository.selectOneByUk(projectId, repositoryId, userId);

            if (glUserId == null) {
                rdmMemberRepository.deleteByPrimaryKey(dbMember.getId());
            } else {
                dbMember.setGlAccessLevel(accessLevel)
                        .setGlExpiresAt(expiresAt)
                        .setSyncGitlabFlag(Boolean.TRUE)
                        .setSyncGitlabDate(new Date());
                rdmMemberRepository.updateByPrimaryKey(dbMember);
            }
        }

        // <> 发送事件
        iRdmMemberService.publishMemberEvent(dbMember, MemberEvent.EventType.SYNC_MEMBER);
    }
}
