package org.hrds.rducm.gitlab.app.service.impl;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants;
import org.hrds.rducm.gitlab.app.service.GitlabMemberService;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hrds.rducm.gitlab.domain.repository.GitlabMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabProjectApiRepository;
import org.hrds.rducm.gitlab.infra.constant.Constants;
import org.hzero.core.base.AopProxy;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.common.query.WhereField;
import org.hzero.mybatis.domian.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants.RDUCM_ADD_MEMBERS;

@Service
public class GitlabMemberServiceImpl implements GitlabMemberService, AopProxy<GitlabMemberServiceImpl> {
    private final GitlabProjectApiRepository gitlabProjectApiRepository;

    private final GitlabMemberRepository gitlabMemberRepository;

    @Autowired
    private TransactionalProducer producer;

    public GitlabMemberServiceImpl(GitlabProjectApiRepository gitlabProjectApiRepository, GitlabMemberRepository gitlabMemberRepository) {
        this.gitlabProjectApiRepository = gitlabProjectApiRepository;
        this.gitlabMemberRepository = gitlabMemberRepository;
    }

    @Override
    public List<GitlabMember> list(Long projectId) {
        GitlabMember query = new GitlabMember();
        query.setProjectId(projectId);
        return gitlabMemberRepository.select(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddMembers(List<GitlabMember> gitlabMembers) {
        // <0> 校验入参

        // <1> 数据库添加成员
        gitlabMemberRepository.batchInsertSelective(gitlabMembers);

        // <2> 调用gitlab api添加成员 todo 事务一致性问题
        batchAddMemberToGitlab(gitlabMembers);
    }

    private void batchAddMemberToGitlab(List<GitlabMember> gitlabMembers) {
        gitlabMembers.forEach((m) -> {
            // <1> 调用gitlab api添加成员
            Member glMember = gitlabProjectApiRepository.addMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
            // <2> 回写数据库
            m = gitlabMemberRepository.selectByPrimaryKey(m.getId());

            m.setGlMemberId(glMember.getId());
            m.setGlProjectId(m.getGlProjectId());
            m.setGlUserId(m.getGlUserId());
            m.setGlAccessLevel(glMember.getAccessLevel().toValue());
            m.setGlExpiresAt(glMember.getExpiresAt());
            gitlabMemberRepository.batchUpdateByPrimaryKeySelective(gitlabMembers);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMember(GitlabMember gitlabMember) {
        // <1> 数据库更新成员
        gitlabMemberRepository.updateByPrimaryKeySelective(gitlabMember);

        // <2> 调用gitlab api更新成员 todo 事务一致性问题
        Member glMember = gitlabProjectApiRepository.updateMember(gitlabMember.getGlProjectId(), gitlabMember.getGlUserId(), gitlabMember.getGlAccessLevel(), gitlabMember.getGlExpiresAt());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long id, Integer glProjectId, Integer glUserId) {
        // <1> 数据库删除成员
        gitlabMemberRepository.deleteByPrimaryKey(id);

        // <2> 调用gitlab api删除成员 todo 事务一致性问题
        gitlabProjectApiRepository.removeMember(glProjectId, glUserId);
    }

    /**
     * 将成员设为过期
     */
    private void batchExpireMembers(List<GitlabMember> expiredGitlabMembers) {
        // <2> 设置过期成员的状态
        expiredGitlabMembers.forEach(m -> {
            m.setState(Constants.MemberState.EXPIRED);
        });

        // <3> 批量更新
        gitlabMemberRepository.batchUpdateByPrimaryKeySelective(expiredGitlabMembers);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleExpiredMembers() {
        // <1> 查询已过期的成员
        Condition condition = new Condition(GitlabMember.class);
        condition.createCriteria().andLessThanOrEqualTo("glExpiresAt", new Date());
        List<GitlabMember> expiredGitlabMembers = gitlabMemberRepository.selectByCondition(condition);

        // <2> 设置过期成员的状态
        batchExpireMembers(expiredGitlabMembers);
    }

    @Saga(code = RDUCM_ADD_MEMBERS, description = "批量添加代码库成员")
    @Transactional(rollbackFor = Exception.class)
    public void batchAddMemberSagaDemo(List<GitlabMember> gitlabMembers) {
        // <1> 数据库添加成员
        gitlabMemberRepository.batchInsertSelective(gitlabMembers);

        // 创建saga
        producer.apply(
                StartSagaBuilder.newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("app-service")
                        .withSagaCode(RDUCM_ADD_MEMBERS)
                        .withPayloadAndSerialize(null)
                        .withRefId(null)
                        .withSourceId(null),
                builder -> {});
    }

    @SagaTask(code = "", sagaCode = RDUCM_ADD_MEMBERS, description = "调用gitlab api添加成员并回写", seq = 1)
    public void batchAddMemberToGitlabSagaDemo(List<GitlabMember> gitlabMembers) {
        // <2> 调用gitlab api添加成员
        batchAddMemberToGitlab(gitlabMembers);
    }
}
