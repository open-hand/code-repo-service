package org.hrds.rducm.gitlab.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.*;
import org.hrds.rducm.gitlab.app.assembler.RdmMemberAssembler;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hrds.rducm.gitlab.infra.util.PageConvertUtils;
import org.hzero.core.base.AopProxy;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants.RDUCM_BATCH_ADD_MEMBERS;

@Service
public class RdmMemberAppServiceImpl implements RdmMemberAppService, AopProxy<RdmMemberAppServiceImpl> {
    private final RdmMemberRepository rdmMemberRepository;

    @Autowired
    private RdmRepositoryRepository rdmRepositoryRepository;

    @Autowired
    private RdmUserRepository rdmUserRepository;

    @Autowired
    private IRdmMemberService iRdmMemberService;

    @Autowired
    private RdmMemberAssembler rdmMemberAssembler;

    @Autowired
    private TransactionalProducer producer;

    public RdmMemberAppServiceImpl(RdmMemberRepository rdmMemberRepository) {
        this.rdmMemberRepository = rdmMemberRepository;
    }

    @Override
    public PageInfo<RdmMemberViewDTO> pageByOptions(Long projectId, PageRequest pageRequest, RdmMemberQueryDTO query) {
        // <1> 封装查询条件

        // 获取用户名对应的userId数组
        // 调用外部接口模糊查询的到userId todo
        List<Long> userIds = new ArrayList<>();
        // 获取应用服务对应的repositoryId数组
        // 调用外部接口模糊查询的到userId todo
        List<Long> repositoryIds = query.getRepositoryIds();

        Condition condition = Condition.builder(RdmMember.class)
                .where(Sqls.custom()
                        .andEqualTo(RdmMember.FIELD_PROJECT_ID, projectId)
                        .andIn(RdmMember.FIELD_USER_ID, userIds, true)
                        .andIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIds, true))
                .build();

        Page<RdmMember> page = PageHelper.doPageAndSort(pageRequest, () -> rdmMemberRepository.selectByCondition(condition));

//        Page<RdmMember> page = PageHelper.doPage(pageRequest, () -> rdmMemberRepository.select(query));
        Page<RdmMemberViewDTO> rdmMemberViewDTOS = ConvertUtils.convertPage(page, RdmMemberViewDTO.class);

        // todo 关联查询, 需从接口获取数据, 暂时造数据
        for (RdmMemberViewDTO viewDTO : rdmMemberViewDTOS.getContent()) {

            RdmUser dbUser = rdmUserRepository.selectByUk(viewDTO.getUserId());
            viewDTO.setRealName(dbUser.getGlUserName());
            viewDTO.setLoginName(dbUser.getGlUserName());

            RdmRepository dbRepository = rdmRepositoryRepository.selectByUk(viewDTO.getRepositoryId());
            viewDTO.setAppServiceName(dbRepository.getRepositoryName());

            viewDTO.setCreatedByName("张三");
            viewDTO.setProjectRoleName("项目成员");
        }

        return PageConvertUtils.convert(rdmMemberViewDTOS);
    }

    /**
     * 批量新增或修改成员
     *
     * @param projectId
     * @param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddOrUpdateMembers(Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO) {
        // <0> 校验入参 + 转换
        List<RdmMember> rdmMembers = rdmMemberAssembler.rdmMemberBatchDTOToRdmMembers(projectId, rdmMemberBatchDTO);

        // <1> 数据库添加成员, 已存在需要更新, 发起一个新事务
        self().batchAddOrUpdateMembersBeforeRequestsNew(rdmMembers);

        // <2> 调用gitlab api添加成员 todo 事务一致性问题
        iRdmMemberService.batchAddOrUpdateMembersToGitlab(rdmMembers);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(Long projectId, Long repositoryId, RdmMemberCreateDTO rdmMemberCreateDTO) {
        // <0> 转换
        final RdmMember param = rdmMemberAssembler.rdmMemberCreateDTOToRdmMember(projectId, repositoryId, rdmMemberCreateDTO);

        // <1> 数据库预更新成员, 发起新事务
        self().addMemberBeforeRequestsNew(param);

        // <2> 调用gitlab api更新成员 todo 事务一致性问题
        iRdmMemberService.addMemberToGitlab(param);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMember(Long memberId, RdmMemberUpdateDTO rdmMemberUpdateDTO) {
        // <0> 校验入参 todo + 转换
        final RdmMember rdmMember = ConvertUtils.convertObject(rdmMemberUpdateDTO, RdmMember.class);
        rdmMember.setId(memberId);

        // 获取gitlab项目id和用户id todo 应从外部接口获取, 暂时从数据库获取
        RdmMember dbMember = rdmMemberRepository.selectByPrimaryKey(memberId);

//        rdmMemberRepository.checkIsSyncGitlab(dbMember);
        rdmMember.setGlProjectId(dbMember.getGlProjectId());
        rdmMember.setGlUserId(dbMember.getGlUserId());
        rdmMember.setUserId(dbMember.getUserId());

        rdmMember.setProjectId(dbMember.getProjectId());
        rdmMember.setRepositoryId(dbMember.getRepositoryId());

        // 设置过期标识
        rdmMember.setExpiredFlag(dbMember.checkExpiredFlag());
        // 设置同步标识
        rdmMember.setSyncGitlabFlag(dbMember.getSyncGitlabFlag());

        // <1> 数据库预更新成员, 发起新事务
        self().updateMemberBeforeRequestsNew(rdmMember);

        // <2> 调用gitlab api更新成员 todo 事务一致性问题
        iRdmMemberService.updateMemberToGitlab(rdmMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long memberId) {
        RdmMember dbMember = rdmMemberRepository.selectByPrimaryKey(memberId);

        // <1> 数据库更新成员, 预删除, 发起新事务
        self().updateMemberBeforeRequestsNew(dbMember);

        // <2> 调用gitlab api删除成员 todo 事务一致性问题
        iRdmMemberService.removeMemberToGitlab(dbMember);
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

    @Override
    @Saga(code = RDUCM_BATCH_ADD_MEMBERS, description = "批量添加代码库成员")
    @Transactional(rollbackFor = Exception.class)
    public void batchAddMemberSagaDemo(Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO) {
        // <0> 校验入参 + 转换
        List<RdmMember> rdmMembers = rdmMemberAssembler.rdmMemberBatchDTOToRdmMembers(projectId, rdmMemberBatchDTO);

        // <1> 预更新, 数据库添加成员, 已存在需要更新
        iRdmMemberService.batchAddOrUpdateMembersBefore(rdmMembers);

        // 创建saga
        producer.apply(
                StartSagaBuilder.newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
//                        .withRefType("hrds-code-repo")
                        .withSagaCode(RDUCM_BATCH_ADD_MEMBERS)
                        .withPayloadAndSerialize(rdmMembers)
//                        .withRefId(null)
                        .withSourceId(projectId),
                builder -> {
                });
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