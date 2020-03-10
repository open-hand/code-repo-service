package org.hrds.rducm.gitlab.domain.service.impl;

import io.choerodon.mybatis.domain.AuditDomain;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hrds.rducm.gitlab.infra.audit.event.OperationEventPublisherHelper;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
        rdmMemberRepository.checkIsSyncGitlab(param);

        RdmMember m = ConvertUtils.convertObject(param, RdmMember.class);
        m.setSyncGitlabFlag(false);
        m.setGlAccessLevel(null);
        m.setGlExpiresAt(null);
        rdmMemberRepository.updateOptional(m, RdmMember.FIELD_SYNC_GITLAB_FLAG, RdmMember.FIELD_GL_ACCESS_LEVEL, RdmMember.FIELD_GL_EXPIRES_AT);

        param.setId(m.getId());
        param.setObjectVersionNumber(m.getObjectVersionNumber());
    }

    @Override
    public void batchAddOrUpdateMembersToGitlab(List<RdmMember> rdmMembers) {
        rdmMembers.forEach((m) -> {
            // <1> 判断新增或更新
            boolean isExists;
            if (m.get_status().equals(AuditDomain.RecordStatus.create)) {
                isExists = false;
            } else if (m.get_status().equals(AuditDomain.RecordStatus.update)) {
                isExists = true;
            } else {
                throw new IllegalArgumentException("record status is invalid");
            }

            Member glMember;
            if (isExists) {
                // 如果过期, Gitlab会直接移除成员, 所以需改成添加成员
                if (m.getExpiredFlag()) {
                    // 调用gitlab api添加成员
                    glMember = gitlabProjectApi.addMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
                } else {
                    // 调用gitlab api更新成员
                    glMember = gitlabProjectApi.updateMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
                }
            } else {
                // 调用gitlab api添加成员
                glMember = gitlabProjectApi.addMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
            }

            // <2> 回写数据库
            this.updateMemberAfter(m, glMember);


            // <3> 发送事件
            MemberEvent.EventParam eventParam = buildEventParam(m.getProjectId(), m.getRepositoryId(), m.getUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
            if (isExists) {
                OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.UPDATE_MEMBER, eventParam));
            } else {
                OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.ADD_MEMBER, eventParam));
            }
        });
    }

    @Override
    public void updateMemberToGitlab(RdmMember param) {
        Member glMember;
        // 如果过期, Gitlab会直接移除成员, 所以需改成添加成员
        if (param.getExpiredFlag()) {
            glMember = gitlabProjectApi.addMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        } else {
            glMember = gitlabProjectApi.updateMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        }

        // <2> 回写数据库
        this.updateMemberAfter(param, glMember);

        // <3> 发送事件
        MemberEvent.EventParam eventParam = buildEventParam(param.getProjectId(), param.getRepositoryId(), param.getUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.UPDATE_MEMBER, eventParam));
    }

    @Override
    public void removeMemberToGitlab(RdmMember param) {
        gitlabProjectApi.removeMember(param.getGlProjectId(), param.getGlUserId());

        // <1> 数据库删除成员
        rdmMemberRepository.deleteByPrimaryKey(param.getId());

        // <3> 发送事件
        MemberEvent.EventParam eventParam = buildEventParam(param.getProjectId(), param.getRepositoryId(), param.getUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.REMOVE_MEMBER, eventParam));
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
            MemberEvent.EventParam eventParam = buildEventParam(m.getProjectId(), m.getRepositoryId(), m.getUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
            OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.REMOVE_EXPIRED_MEMBER, eventParam));
        });
    }

    /**
     * 构造审计所需报文参数
     *
     * @param targetUserId 目标用户id
     * @param accessLevel  访问权限等级
     * @param expiresAt    过期时间
     */
    private MemberEvent.EventParam buildEventParam(Long projectId, Long repositoryId, Long targetUserId, Integer accessLevel, Date expiresAt) {
        return new MemberEvent.EventParam(projectId, repositoryId, targetUserId, accessLevel, expiresAt);
    }
}
