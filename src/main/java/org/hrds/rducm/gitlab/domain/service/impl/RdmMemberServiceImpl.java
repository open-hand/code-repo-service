package org.hrds.rducm.gitlab.domain.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;
import org.hrds.rducm.gitlab.api.controller.dto.MemberAuthDetailViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseUserQueryDTO;
import org.hrds.rducm.gitlab.domain.aggregate.MemberAuthDetailAgg;
import org.hrds.rducm.gitlab.domain.component.QueryConditionHelper;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.facade.IC7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.domain.facade.IC7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hrds.rducm.gitlab.infra.audit.event.OperationEventPublisherHelper;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabAdminApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.hrds.rducm.gitlab.infra.enums.RdmAccessLevel;
import org.hrds.rducm.gitlab.infra.enums.RdmMemberStateEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nRoleVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 成员管理领域服务类
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/5
 */
@Service
public class RdmMemberServiceImpl implements IRdmMemberService {
    private static final Logger logger = LoggerFactory.getLogger(RdmMemberServiceImpl.class);
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private GitlabProjectApi gitlabProjectApi;
    @Autowired
    private GitlabAdminApi gitlabAdminApi;
    @Autowired
    private IC7nDevOpsServiceFacade ic7NDevOpsServiceFacade;
    @Autowired
    private IC7nBaseServiceFacade ic7NBaseServiceFacade;
    @Autowired
    private RdmUserRepository rdmUserRepository;

    @Override
    public Page<MemberAuthDetailViewDTO> pageMembersRepositoryAuthorized(Long organizationId, Long projectId, PageRequest pageRequest, BaseUserQueryDTO queryDTO) {
        // 封装查询条件
        String realName = queryDTO.getRealName();
        String loginName = queryDTO.getLoginName();

        // 调用外部接口模糊查询 用户名或登录名
        Set<Long> userIdsSet = QueryConditionHelper.queryByNameConditionOnProj(projectId, realName, loginName);
        if (userIdsSet != null && userIdsSet.isEmpty()) {
            return new Page<>();
        }

        Page<MemberAuthDetailAgg> page = PageHelper.doPageAndSort(pageRequest, () -> rdmMemberRepository.selectMembersRepositoryAuthorized(organizationId, projectId, userIdsSet));

        // 查询应用服务总数
        int allRepositoryCount = ic7NDevOpsServiceFacade.listC7nAppServiceOnProjectLevel(projectId).size();
        BigDecimal allRepositoryCountBigD = new BigDecimal(allRepositoryCount);

        // 用户id
        Set<Long> userIds = new HashSet<>();

        page.getContent().forEach(v -> {
            userIds.add(v.getUserId());
        });

        // 获取操作人用户信息
        Map<Long, C7nUserVO> c7nUserVOMap = ic7NBaseServiceFacade.listC7nUserToMapOnProjectLevel(projectId, userIds);


        Page<MemberAuthDetailViewDTO> pageReturn = ConvertUtils.convertPage(page, (v) -> {
            MemberAuthDetailViewDTO viewDTO = ConvertUtils.convertObject(v, MemberAuthDetailViewDTO.class);
            C7nUserVO c7nUserVO = Optional.ofNullable(c7nUserVOMap.get(v.getUserId()))
                    .orElse(new C7nUserVO().setRoles(Collections.emptyList()));

            viewDTO.setAllRepositoryCount(allRepositoryCount);
            viewDTO.setUser(BaseC7nUserViewDTO.convert(c7nUserVO));
            viewDTO.setRoleNames(c7nUserVO.getRoles().stream().map(C7nRoleVO::getName).collect(Collectors.toList()));

            BigDecimal authorizedRepositoryCountBigD = Optional.ofNullable(viewDTO.getAuthorizedRepositoryCount()).map(BigDecimal::new).orElse(BigDecimal.ZERO);
            viewDTO.setAuthorizedRepositoryPercent(authorizedRepositoryCountBigD.divide(allRepositoryCountBigD, 4, BigDecimal.ROUND_HALF_UP));

            return viewDTO;
        });

        return pageReturn;
    }

    @Override
    public Page<RdmMemberViewDTO> pageMemberPermissions(Long organizationId,
                                                        Long projectId,
                                                        Long userId,
                                                        PageRequest pageRequest) {
        RdmMember condition = new RdmMember();
        condition.setOrganizationId(organizationId);
        condition.setProjectId(projectId);
        condition.setUserId(userId);

        Page<RdmMember> page = PageHelper.doPageAndSort(pageRequest, () -> rdmMemberRepository.select(condition));

        // 代码库id
        Set<Long> repositoryIds = new HashSet<>();

        page.getContent().forEach(v -> {
            repositoryIds.add(v.getRepositoryId());
        });

        // 获取应用服务信息
        Map<Long, C7nAppServiceVO> c7nAppServiceVOMap = ic7NDevOpsServiceFacade.listC7nAppServiceToMap(repositoryIds);


        Page<RdmMemberViewDTO> pageReturn = ConvertUtils.convertPage(page, (v) -> {
            RdmMemberViewDTO viewDTO = ConvertUtils.convertObject(v, RdmMemberViewDTO.class);

            viewDTO.setRepositoryName(c7nAppServiceVOMap.get(v.getRepositoryId()).getName());
            return viewDTO;
        });

        return pageReturn;
    }

    @Override
    public void batchAddOrUpdateMembersBefore(List<RdmMember> rdmMembers) {
        rdmMembers.forEach(m -> {
            // 判断新增或修改
            RdmMember dbMember = rdmMemberRepository.selectOneByUk(m.getProjectId(), m.getRepositoryId(), m.getUserId());
            boolean isExists = dbMember != null;

            // 设置状态供后续判断
            m.set_status(isExists ? AuditDomain.RecordStatus.update : AuditDomain.RecordStatus.create);

            if (isExists) {
                // 设置过期标识
                m.setExpiredFlag(dbMember.checkExpiredFlag());
                // 设置同步标识
                m.setSyncGitlabFlag(dbMember.getSyncGitlabFlag());

                m.setId(dbMember.getId());
                m.setObjectVersionNumber(dbMember.getObjectVersionNumber());
                this.updateMemberBefore(m);
            } else {
                this.insertMemberBefore(m);
            }
        });
    }

    @Override
    public void insertMemberBefore(RdmMember param) {
        RdmMember m = ConvertUtils.convertObject(param, RdmMember.class);
        m.setSyncGitlabFlag(false);
        m.setGlAccessLevel(null);
        m.setGlExpiresAt(null);
        rdmMemberRepository.insertSelective(m);

        param.setId(m.getId());
        param.setObjectVersionNumber(m.getObjectVersionNumber());
    }

    @Override
    public void updateMemberBefore(RdmMember param) {
        // 校验是否已和gitlab保持同步, 保证一致性
        param.checkIsSyncGitlab();

        RdmMember m = ConvertUtils.convertObject(param, RdmMember.class);
        m.setSyncGitlabFlag(false);
        m.setGlAccessLevel(null);
        m.setGlExpiresAt(null);
        rdmMemberRepository.updateOptional(m, RdmMember.FIELD_SYNC_GITLAB_FLAG, RdmMember.FIELD_GL_ACCESS_LEVEL, RdmMember.FIELD_GL_EXPIRES_AT);

        param.setId(m.getId());
        param.setObjectVersionNumber(m.getObjectVersionNumber());
    }

    @Override
    @Deprecated
    public Member addOrUpdateMembersToGitlab(RdmMember param, boolean isExists) {
        // <1> 判断新增或更新
        Member glMember;
        if (isExists) {
            // 如果过期, Gitlab会直接移除成员, 所以需改成添加成员
            if (param.getExpiredFlag()) {
                // 调用gitlab api添加成员
                glMember = gitlabProjectApi.addMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
            } else {
                // 调用gitlab api更新成员
                glMember = gitlabProjectApi.updateMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
            }
        } else {
            // 调用gitlab api添加成员
            glMember = gitlabProjectApi.addMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        }

        return glMember;
    }

    @Override
    public Member addMemberToGitlab(Integer glProjectId, Integer glUserId, Integer accessLevel, Date expiresAt) {
        // 调用gitlab api添加成员
        return gitlabProjectApi.addMember(glProjectId, glUserId, accessLevel, expiresAt);
    }

    @Override
    @Deprecated
    public Member updateMemberToGitlab(RdmMember param) {
        Member glMember;
        // 如果过期, Gitlab会直接移除成员, 所以需改成添加成员
        if (param.getExpiredFlag()) {
            glMember = gitlabProjectApi.addMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        } else {
            glMember = gitlabProjectApi.updateMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        }

        return glMember;
    }

    @Override
    public void removeMemberToGitlab(Integer glProjectId, Integer glUserId) {
        gitlabProjectApi.removeMember(glProjectId, glUserId);
    }

    @Override
    public void updateMemberAfter(RdmMember m, Member member) {
        // <2> 回写数据库
        String[] fields = new String[]{
                RdmMember.FIELD_GL_PROJECT_ID,
                RdmMember.FIELD_GL_USER_ID,
                RdmMember.FIELD_GL_ACCESS_LEVEL,
                RdmMember.FIELD_GL_EXPIRES_AT,
                RdmMember.FIELD_SYNC_GITLAB_FLAG,
                RdmMember.FIELD_SYNC_GITLAB_DATE,
                RdmMember.FIELD_SYNC_GITLAB_ERROR_MSG
        };
        m.setGlAccessLevel(member.getAccessLevel().toValue());
        m.setGlExpiresAt(member.getExpiresAt());
        m.setSyncGitlabFlag(true);
        m.setSyncGitlabDate(new Date());
        m.setSyncGitlabErrorMsg(null);
        rdmMemberRepository.updateOptional(m, fields);
    }

    @Override
    public Member tryRemoveAndAddMemberToGitlab(Integer glProjectId, Integer glUserId, Integer accessLevel, Date expiresAt) {
        // 尝试移除成员
        this.tryRemoveMemberToGitlab(glProjectId, glUserId);
        // 添加新成员
        return this.addMemberToGitlab(glProjectId, glUserId, accessLevel, expiresAt);
    }

    @Override
    public void tryRemoveMemberToGitlab(Integer glProjectId, Integer glUserId) {
        // 先查询Gitlab用户
        Member glMember = gitlabProjectApi.getAllMember(glProjectId, glUserId);

        if (glMember != null) {
            if (glMember.getAccessLevel().toValue() >= RdmAccessLevel.OWNER.toValue()) {
                throw new CommonException("error.not.allow.remove.owner", glMember.getName());
            }

            try {
                this.removeMemberToGitlab(glProjectId, glUserId);
            } catch (GitlabClientException e) {
                throw new CommonException("error.member.not.allow.change", e, glMember.getName());
            }
        }
    }

    @Override
    public void batchExpireMembers(List<RdmMember> expiredRdmMembers) {
        expiredRdmMembers.forEach(m -> {
            // <1> 删除
            rdmMemberRepository.deleteByPrimaryKey(m);

            // <2> 发送事件
            this.publishMemberEvent(m, MemberEvent.EventType.REMOVE_EXPIRED_MEMBER);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncAllMembersFromGitlab(Long organizationId, Long projectId, Long repositoryId) {
        // <1> 获取Gitlab项目id
        Integer glProjectId = ic7NDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);

        if (glProjectId == null) {
            return 0;
        }

        // <2> 查询Gitlab成员
        // 判断一下gitlab是否存在该代码库, 避免404报错
        Project project = gitlabAdminApi.getProject(glProjectId);
        if (project == null) {
            return 0;
        }
        List<Member> glMembers = gitlabAdminApi.getAllMembers(glProjectId);

        // <3> 同步到数据库
        // 删除原成员
        RdmMember deleteMember = new RdmMember();
        deleteMember.setOrganizationId(organizationId);
        deleteMember.setProjectId(projectId);
        deleteMember.setRepositoryId(repositoryId);
        rdmMemberRepository.delete(deleteMember);

        AtomicInteger count = new AtomicInteger();
        // Gitlab成员权限分为group权限和project权限
        // group和project成员有可能重复, 并且权限不一样. 导致通过getAllMembers获取到的重复成员有2个,需要特殊处理
        // 当成员重复, 取权限较大的插入数据库
        glMembers.stream()
                // 过滤blocked的成员
                .filter(glMember -> !RdmMemberStateEnum.BLOCKED.getCode().equals(glMember.getState()))
                // 按id分组
                .collect(Collectors.groupingBy(Member::getId))
                .forEach((key, value) -> {
                    Member glMember;
                    if (value.size() > 1) {
                        // 如果有2个相同成员, 需要特殊处理; 表示该成员在gitlab的group和project都有权限, 这种情况取accessLevel中较大的那个权限
                        glMember = value.stream()
                                .max((o1, o2) -> o1.getAccessLevel().toValue() >= o2.getAccessLevel().toValue() ? 1 : -1)
                                .get();
                    } else {
                        glMember = value.get(0);
                    }

                    // 查询Gitlab用户对应的userId
                    Long userId = ic7NDevOpsServiceFacade.glUserIdToUserId(glMember.getId());

                    if (userId == null) {
                        logger.info("该Gitlab用户{}无对应的猪齿鱼用户", glMember.getUsername());
                    } else {
                        RdmMember rdmMember = new RdmMember();
                        rdmMember.setOrganizationId(organizationId)
                                .setProjectId(projectId)
                                .setRepositoryId(repositoryId)
                                .setUserId(userId)
                                .setGlProjectId(glProjectId)
                                .setGlUserId(glMember.getId())
                                .setGlAccessLevel(glMember.getAccessLevel().toValue())
                                .setGlExpiresAt(glMember.getExpiresAt())
                                .setSyncGitlabFlag(Boolean.TRUE)
                                .setSyncGitlabDate(new Date());

                        // 重新插入
                        rdmMemberRepository.insertSelective(rdmMember);
                        count.getAndIncrement();
                    }
                });
        return count.get();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncMemberFromGitlab(RdmMember param) {
        // <1> 获取Gitlab成员, 并更新数据库
        Integer glUserId = Objects.requireNonNull(param.getGlUserId());
        Member glMember = gitlabProjectApi.getAllMember(Objects.requireNonNull(param.getGlProjectId()), glUserId);
        // 理论上只会查询到一个成员
        if (glMember == null) {
            // 移除数据库成员
            rdmMemberRepository.deleteByPrimaryKey(param.getId());
        } else {
            // 更新数据库成员
            updateMemberAfter(param, glMember);
        }
    }

    @Override
    public void publishMemberEvent(RdmMember param, MemberEvent.EventType eventType) {
        // 发送事件
        MemberEvent.EventParam eventParam = buildEventParam(param.getOrganizationId(), param.getProjectId(), param.getRepositoryId(), param.getUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, eventType, eventParam));
    }

    /**
     * 构造审计所需报文参数
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @param targetUserId   目标用户id
     * @param accessLevel    访问权限等级
     * @param expiresAt      过期时间
     */
    private MemberEvent.EventParam buildEventParam(Long organizationId, Long projectId, Long repositoryId, Long targetUserId, Integer accessLevel, Date expiresAt) {
        return new MemberEvent.EventParam(organizationId, projectId, repositoryId, targetUserId, accessLevel, expiresAt);
    }
}
