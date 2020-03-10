package org.hrds.rducm.gitlab.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class RdmMemberRepositoryImpl extends BaseRepositoryImpl<RdmMember> implements RdmMemberRepository {
//    @Autowired
//    private GitlabProjectApi gitlabProjectApi;

    @Override
    public RdmMember selectOneByUk(Long projectId, Long repositoryId, Long userId) {
        RdmMember rdmMember = new RdmMember();
        rdmMember.setProjectId(projectId);
        rdmMember.setRepositoryId(repositoryId);
        rdmMember.setUserId(userId);
        return this.selectOne(rdmMember);
    }

    @Override
    public int selectCountByRepositoryId(Long repositoryId) {
        RdmMember rdmMember = new RdmMember();
        rdmMember.setRepositoryId(repositoryId);
        return this.selectCount(rdmMember);
    }

//    @Override
//    public List<Member> queryMembersFromGitlab(Integer glProjectId) {
//        return gitlabProjectApi.getMembers(glProjectId);
//    }

//    @Override
//    public void batchAddOrUpdateMembersToGitlab(List<RdmMember> rdmMembers) {
//        rdmMembers.forEach((m) -> {
//            // <1> 判断新增或更新
//            boolean isExists;
//            if (m.get_status().equals(AuditDomain.RecordStatus.create)) {
//                isExists = false;
//            } else if (m.get_status().equals(AuditDomain.RecordStatus.update)) {
//                isExists = true;
//            } else {
//                throw new IllegalArgumentException("record status is invalid");
//            }
//
//            Member glMember;
//            if (isExists) {
//                // 如果过期, Gitlab会直接移除成员, 所以需改成添加成员
//                if (m.getExpiredFlag()) {
//                    // 调用gitlab api添加成员
//                    glMember = gitlabProjectApi.addMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
//                } else {
//                    // 调用gitlab api更新成员
//                    glMember = gitlabProjectApi.updateMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
//                }
//            } else {
//                // 调用gitlab api添加成员
//                glMember = gitlabProjectApi.addMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
//            }
//
//            // <2> 回写数据库
//            this.updateMemberAfter(m, glMember);
//
//            // <3> 发送事件
//            MemberEvent.EventParam eventParam = buildEventParam(m.getProjectId(), m.getRepositoryId(), m.getUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
//            if (isExists) {
//                OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.UPDATE_MEMBER, eventParam));
//            } else {
//                OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.ADD_MEMBER, eventParam));
//            }
//
//        });
//    }


//    @Override
//    public void batchAddOrUpdateMembersBefore(List<RdmMember> rdmMembers) {
//        rdmMembers.forEach(m -> {
//            // 判断新增或修改
//            RdmMember dbMember = this.selectOneByUk(m.getProjectId(), m.getRepositoryId(), m.getUserId());
//            boolean isExists = dbMember != null;
//
//            // 设置状态供后续判断
//            m.set_status(isExists ? AuditDomain.RecordStatus.update : AuditDomain.RecordStatus.create);
//
//            if (isExists) {
//                this.checkIsSyncGitlab(dbMember);
//
//                // 设置过期标识
//                m.setExpiredFlag(dbMember.checkExpiredFlag());
//
//                m.setId(dbMember.getId());
//                m.setObjectVersionNumber(dbMember.getObjectVersionNumber());
//                this.updateMemberBefore(m);
//            } else {
//                this.insertMemberBefore(m);
//            }
//        });
//    }

//    @Override
//    public void insertMemberBefore(RdmMember param) {
//        RdmMember m = ConvertUtils.convertObject(param, RdmMember.class);
//        m.setSyncGitlabFlag(false);
//        m.setGlAccessLevel(null);
//        m.setGlExpiresAt(null);
//        this.insertSelective(m);
//
//        param.setId(m.getId());
//        param.setObjectVersionNumber(m.getObjectVersionNumber());
//    }
//
//    @Override
//    public void updateMemberBefore(RdmMember param) {
//        RdmMember m = ConvertUtils.convertObject(param, RdmMember.class);
//        m.setSyncGitlabFlag(false);
//        m.setGlAccessLevel(null);
//        m.setGlExpiresAt(null);
//        this.updateOptional(m, RdmMember.FIELD_SYNC_GITLAB_FLAG, RdmMember.FIELD_GL_ACCESS_LEVEL, RdmMember.FIELD_GL_EXPIRES_AT);
//
//        param.setId(m.getId());
//        param.setObjectVersionNumber(m.getObjectVersionNumber());
//    }
//
//    @Override
//    public void updateMemberAfter(RdmMember m, Member member) {
//        // <2> 回写数据库
//        String[] fields = new String[]{
//                RdmMember.FIELD_GL_PROJECT_ID,
//                RdmMember.FIELD_GL_USER_ID,
//                RdmMember.FIELD_GL_ACCESS_LEVEL,
//                RdmMember.FIELD_GL_EXPIRES_AT,
//                RdmMember.FIELD_SYNC_GITLAB_FLAG,
//                RdmMember.FIELD_SYNC_DATE_GITLAB
//        };
//        m.setGlAccessLevel(member.getAccessLevel().toValue());
//        m.setGlExpiresAt(member.getExpiresAt());
//        m.setSyncGitlabFlag(true);
//        m.setSyncDateGitlab(new Date());
//        this.updateOptional(m, fields);
//
//    }

//    @Override
//    public void updateMemberToGitlab(RdmMember param) {
//        Member glMember;
//        // 如果过期, Gitlab会直接移除成员, 所以需改成添加成员
//        if (param.getExpiredFlag()) {
//            glMember = gitlabProjectApi.addMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
//        } else {
//            glMember = gitlabProjectApi.updateMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
//        }
//
//        // <2> 回写数据库
//        updateMemberAfter(param, glMember);
//
//        // <3> 发送事件
//        MemberEvent.EventParam eventParam = buildEventParam(param.getProjectId(), param.getRepositoryId(), param.getUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
//        OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.UPDATE_MEMBER, eventParam));
//    }

//    @Override
//    public void removeMemberToGitlab(RdmMember param) {
//        gitlabProjectApi.removeMember(param.getGlProjectId(), param.getGlUserId());
//
//        // <1> 数据库删除成员
//        this.deleteByPrimaryKey(param.getId());
//
//        // <3> 发送事件
//        MemberEvent.EventParam eventParam = buildEventParam(param.getProjectId(), param.getRepositoryId(), param.getUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
//        OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, MemberEvent.EventType.REMOVE_MEMBER, eventParam));
//    }

    /**
     * 检查当前记录是否处于"预更新"状态
     *
     * @param m
     */
    @Override
    public void checkIsSyncGitlab(RdmMember m) {
        if (!m.getSyncGitlabFlag()) {
            // 当同步标记为false时, 表示上个事务还未结束
            throw new CommonException("error.sync.flag.false");
        }
    }

//    /**
//     * todo
//     * 成员权限审计
//     *
//     * @param
//     */
//    private void compareMembersWithGitlab(Long glProjectId, List<RdmMember> dbMembers, List<Member> glMembers) {
//        Map<String, GitlabMemberAudit> gitlabMemberAuditMap = new HashMap<>();
//
//        for (RdmMember dbMember : dbMembers) {
//            GitlabMemberAudit gitlabMemberAudit = new GitlabMemberAudit();
//            gitlabMemberAudit.setRepositoryId(dbMember.getRepositoryId())
//                    .setUserId(dbMember.getUserId())
//                    .setAccessLevel(dbMember.getGlAccessLevel())
//                    .setExpiresAt(dbMember.getGlExpiresAt());
//
//            gitlabMemberAuditMap.put(dbMember.getGlProjectId() + "-" + dbMember.getGlUserId(), gitlabMemberAudit);
//        }
//
//        for (Member glMember : glMembers) {
//            String key = glProjectId + "-" + glMember.getId();
//
//
//        }
//    }

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
