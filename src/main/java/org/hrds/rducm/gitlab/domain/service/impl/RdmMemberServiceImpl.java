package org.hrds.rducm.gitlab.domain.service.impl;

import io.choerodon.mybatis.domain.AuditDomain;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hrds.rducm.gitlab.infra.audit.event.OperationEventPublisherHelper;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 成员管理领域服务类
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/5
 */
@Service
public class RdmMemberServiceImpl implements IRdmMemberService {
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private GitlabProjectApi gitlabProjectApi;
    @Autowired
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;
    @Autowired
    private RdmUserRepository rdmUserRepository;

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
    public Member addOrUpdateMembersToGitlab(RdmMember param, boolean isExists) {
        // <1> 判断新增或更新
//        boolean isExists;
//        if (param.get_status().equals(AuditDomain.RecordStatus.create)) {
//            isExists = false;
//        } else if (param.get_status().equals(AuditDomain.RecordStatus.update)) {
//            isExists = true;
//        } else {
//            throw new IllegalArgumentException("record status is invalid");
//        }

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
    public Member addMemberToGitlab(RdmMember param) {
        // 调用gitlab api添加成员
        Member glMember = gitlabProjectApi.addMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());

        return glMember;

        // <2> 回写数据库
//        this.updateMemberAfter(param, glMember);

        // <3> 发送事件
//        publishMemberEvent(param, MemberEvent.EventType.ADD_MEMBER);
//        MemberEvent.EventParam eventParam = buildEventParam(param.getProjectId(), param.getRepositoryId(), param.getUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
//        OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.ADD_MEMBER, eventParam));
    }

    @Override
    public Member updateMemberToGitlab(RdmMember param) {
        Member glMember;
        // 如果过期, Gitlab会直接移除成员, 所以需改成添加成员
        if (param.getExpiredFlag()) {
            glMember = gitlabProjectApi.addMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        } else {
            glMember = gitlabProjectApi.updateMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        }

        return glMember;

//        // <2> 回写数据库
//        this.updateMemberAfter(param, glMember);
//
//        // <3> 发送事件
//        MemberEvent.EventParam eventParam = buildEventParam(param.getProjectId(), param.getRepositoryId(), param.getUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
//        OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.UPDATE_MEMBER, eventParam));
    }

    @Override
    public void removeMemberToGitlab(RdmMember param) {
        gitlabProjectApi.removeMember(param.getGlProjectId(), param.getGlUserId());

//        // <1> 数据库删除成员
//        rdmMemberRepository.deleteByPrimaryKey(param.getId());
//
//        // <3> 发送事件
//        MemberEvent.EventParam eventParam = buildEventParam(param.getProjectId(), param.getRepositoryId(), param.getUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
//        OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.REMOVE_MEMBER, eventParam));
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
                RdmMember.FIELD_SYNC_DATE_GITLAB
        };
        m.setGlAccessLevel(member.getAccessLevel().toValue());
        m.setGlExpiresAt(member.getExpiresAt());
        m.setSyncGitlabFlag(true);
        m.setSyncDateGitlab(new Date());
        rdmMemberRepository.updateOptional(m, fields);
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
    public int syncAllMembersFromGitlab(Long projectId, Long repositoryId) {
        // <1> 获取Gitlab项目id
        Integer glProjectId = ic7nDevOpsServiceService.repositoryIdToGlProjectId(projectId, repositoryId);

        // <2> 查询Gitlab成员
        List<Member> glMembers = gitlabProjectApi.getAllMembers(glProjectId);

        // <3> 同步到数据库
        // 删除原成员
        RdmMember deleteMember = new RdmMember();
        deleteMember.setProjectId(projectId);
        deleteMember.setRepositoryId(repositoryId);
        rdmMemberRepository.delete(deleteMember);
        glMembers.forEach(glMember -> {
            // 查询Gitlab用户对应的userId todo 从数据库取还是猪齿鱼取
            RdmUser dbUser = rdmUserRepository.selectByUk(glMember.getId());
            Long userId = dbUser.getUserId();

            RdmMember rdmMember = new RdmMember();
            rdmMember.setProjectId(projectId)
                    .setRepositoryId(repositoryId)
                    .setUserId(userId)
                    .setGlProjectId(glProjectId)
                    .setGlUserId(glMember.getId())
                    .setGlAccessLevel(glMember.getAccessLevel().toValue())
                    .setGlExpiresAt(glMember.getExpiresAt())
                    .setSyncGitlabFlag(Boolean.TRUE)
                    .setSyncDateGitlab(new Date());

            // 重新插入
            rdmMemberRepository.insertSelective(rdmMember);
        });
        return glMembers.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncMemberFromGitlab(RdmMember param) {
        // <1> 获取Gitlab成员, 并更新数据库
        Member glMember = gitlabProjectApi.getMember(Objects.requireNonNull(param.getGlProjectId()), Objects.requireNonNull(param.getGlUserId()));
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
     * @param targetUserId 目标用户id
     * @param accessLevel  访问权限等级
     * @param expiresAt    过期时间
     */
    private MemberEvent.EventParam buildEventParam(Long organizationId, Long projectId, Long repositoryId, Long targetUserId, Integer accessLevel, Date expiresAt) {
        return new MemberEvent.EventParam(organizationId, projectId, repositoryId, targetUserId, accessLevel, expiresAt);
    }
}
