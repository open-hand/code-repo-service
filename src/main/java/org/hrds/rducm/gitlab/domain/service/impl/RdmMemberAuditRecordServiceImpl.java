package org.hrds.rducm.gitlab.domain.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.function.Function;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;
import org.hrds.rducm.gitlab.api.controller.dto.MemberAuditRecordQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberAuditRecordViewDTO;
import org.hrds.rducm.gitlab.app.assembler.RdmMemberAuditRecordAssembler;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.admin.GitlabAdminApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.GitlabMember;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 安全审计
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/3
 */
@Service
public class RdmMemberAuditRecordServiceImpl implements IRdmMemberAuditRecordService {
    public static final Logger LOGGER = LoggerFactory.getLogger(RdmMemberAuditRecordServiceImpl.class);

    /**
     * 用于存储组织的组织管理员数据, 便于后续逻辑处理
     */
    private final ThreadLocal<List<C7nUserVO>> threadLocal = new ThreadLocal<>();
    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;
    @Autowired
    private RdmMemberRepository memberRepository;
    @Autowired
    private GitlabAdminApi gitlabAdminApi;
    @Autowired
    private C7nDevOpsServiceFacade c7NDevOpsServiceFacade;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;
    @Autowired
    private RdmMemberAuditRecordAssembler rdmMemberAuditRecordAssembler;

    @Override
    public Page<RdmMemberAuditRecordViewDTO> pageByOptions(Long organizationId,
                                                           Set<Long> projectIds,
                                                           Set<Long> repositoryIds,
                                                           PageRequest pageRequest,
                                                           MemberAuditRecordQueryDTO queryDTO, ResourceLevel resourceLevel) {
        String repositoryName = queryDTO.getRepositoryName();

        Condition condition = Condition.builder(RdmMemberAuditRecord.class)
                .where(Sqls.custom()
                        .andEqualTo(RdmMemberAuditRecord.FIELD_SYNC_FLAG, false)
                        .andIn(RdmMemberAuditRecord.FIELD_ORGANIZATION_ID, Collections.singleton(organizationId))
                        .andIn(RdmMemberAuditRecord.FIELD_PROJECT_ID, projectIds, true)
                        .andIn(RdmMemberAuditRecord.FIELD_REPOSITORY_ID, repositoryIds, true))
                .build();


        switch (resourceLevel) {
            case ORGANIZATION: {
                // 调用外部接口模糊查询 应用服务
                if (!StringUtils.isEmpty(repositoryName)) {
                    Set<Long> repositoryIdSet = c7NDevOpsServiceFacade.listC7nAppServiceIdsByNameOnOrgLevel(organizationId, repositoryName);

                    if (repositoryIdSet.isEmpty()) {
                        return new Page<>();
                    }

                    condition.and().andIn(RdmMemberAuditRecord.FIELD_REPOSITORY_ID, repositoryIdSet);
                }
                break;
            }
            case PROJECT: {
                // 调用外部接口模糊查询 应用服务
                if (!StringUtils.isEmpty(repositoryName)) {
                    Set<Long> repositoryIdSet = c7NDevOpsServiceFacade.listC7nAppServiceIdsByNameOnProjectLevel(projectIds.iterator().next(), repositoryName);

                    if (repositoryIdSet.isEmpty()) {
                        return new Page<>();
                    }

                    condition.and().andIn(RdmMemberAuditRecord.FIELD_REPOSITORY_ID, repositoryIdSet);
                }
                break;
            }
            default:
                break;
        }

        Page<RdmMemberAuditRecord> page = PageHelper.doPageAndSort(pageRequest, () -> rdmMemberAuditRecordRepository.selectByCondition(condition));

        return rdmMemberAuditRecordAssembler.pageToViewDTO(page, resourceLevel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RdmMemberAuditRecord> batchCompare(Long organizationId) {
        StopWatch stopWatch = new StopWatch();

        // <0> 删除原有数据
        rdmMemberAuditRecordRepository.delete(new RdmMemberAuditRecord().setOrganizationId(organizationId));

        // <1> 对比组织所有成员
        stopWatch.start("任务1");
        List<RdmMemberAuditRecord> list = compareMembersByOrganizationId(organizationId);
        stopWatch.stop();

        // <2> 批量插入数据库
        // 优化, 每次最多一次性插入10000条
        stopWatch.start("任务2");
        int maxInsert = 10000;
        int curIndex = 0;
        while (curIndex < list.size()) {
            rdmMemberAuditRecordRepository.batchInsertCustom(list.subList(curIndex, Math.min(curIndex + maxInsert, list.size())));
            curIndex += maxInsert;
        }
        stopWatch.stop();

        LOGGER.info("执行时长:{}, 执行详情\n{}", stopWatch.getTotalTimeMillis(), stopWatch.prettyPrint());

        return list;
    }

    @Override
    public List<RdmMemberAuditRecord> batchCompareProject(Long organizationId, Long projectId) {
        // <0> 删除原有数据
        rdmMemberAuditRecordRepository.delete(new RdmMemberAuditRecord().setProjectId(projectId));

        // <1> 对比项目所有成员
        List<RdmMemberAuditRecord> list = batchCompareMembersByProjectId(organizationId, projectId);

        // <2> 批量插入数据库
        // 优化, 每次最多一次性插入10000条

        int maxInsert = 10000;
        int curIndex = 0;
        while (curIndex < list.size()) {
            rdmMemberAuditRecordRepository.batchInsertCustom(list.subList(curIndex, Math.min(curIndex + maxInsert, list.size())));
            curIndex += maxInsert;
        }
        return list;
    }

    private List<RdmMemberAuditRecord> batchCompareMembersByProjectId(Long organizationId, Long projectId) {
        // 获取组织管理员, 存入ThreadLocal备用
        List<C7nUserVO> orgAdministrators = c7NBaseServiceFacade.listOrgAdministrator(organizationId);
        threadLocal.set(orgAdministrators);
        List<RdmMemberAuditRecord> rdmMemberAuditRecords = compareMembersByProjectId(organizationId, projectId);
        threadLocal.remove();
        return rdmMemberAuditRecords;

    }

    private List<RdmMemberAuditRecord> compareMembersByOrganizationId(Long organizationId) {
        // 获取组织管理员, 存入ThreadLocal备用
        List<C7nUserVO> orgAdministrators = c7NBaseServiceFacade.listOrgAdministrator(organizationId);
        threadLocal.set(orgAdministrators);

        // <1> 获取组织下所有项目
        Set<Long> projectIds = c7NBaseServiceFacade.listProjectIds(organizationId);

        List<RdmMemberAuditRecord> list = projectIds.stream()
                .map(projectId -> {
                    return compareMembersByProjectId(organizationId, projectId);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        threadLocal.remove();

        return list;
    }

    private List<RdmMemberAuditRecord> compareMembersByProjectId(Long organizationId,
                                                                 Long projectId) {

        List<RdmMemberAuditRecord> rdmMemberAuditRecords = new ArrayList<>();
        //查询项目下有gitlab  owner标签的人
        List<C7nUserVO> gitlabOwners = c7NBaseServiceFacade.listCustomGitlabOwnerLableUser(projectId, "GITLAB_OWNER");
        // 获取项目下应用服务组的id
        Integer appGroupId = getAppServiceGroupId(projectId);

        //审计组的权限
        List<RdmMemberAuditRecord> rdmGroupMemberAuditRecords = compareGitlabGroupMembers(organizationId, projectId, gitlabOwners, appGroupId);
        //审计项目下应用服务的权限
        List<RdmMemberAuditRecord> rdmProjectMemberAuditRecords = compareGitlabProjectMembers(organizationId, projectId, gitlabOwners, appGroupId);


        rdmMemberAuditRecords.addAll(rdmGroupMemberAuditRecords);
        rdmMemberAuditRecords.addAll(rdmProjectMemberAuditRecords);
        return rdmMemberAuditRecords;
    }

    private List<RdmMemberAuditRecord> compareGitlabProjectMembers(Long organizationId, Long projectId, List<C7nUserVO> gitlabOwners, Integer appGroupId) {
        // 获取项目下所有代码库id和Gitlab项目id  导入失败的项目有代码库id(应用服务id) 没有gProjectId
        Map<Long, Long> appServiceIdMap = c7NDevOpsServiceFacade.listActiveC7nAppServiceIdsMapOnProjectLevel(projectId);
        //审计应用服务的权限
        List<RdmMemberAuditRecord> list = appServiceIdMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .map((entry) -> {
                    Long repositoryId = entry.getKey();
                    Integer glProjectId = Math.toIntExact(entry.getValue());
                    return compareMembersByRepositoryId(organizationId, projectId, repositoryId, glProjectId, appGroupId, gitlabOwners);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return list;
    }

    private Integer getAppServiceGroupId(Long projectId) {
        //查询组是否存在,有些人将组删掉然后自己又创建了一个组，这样他的组id在devops中就对不上了
        Long appGroupIdByProjectId = c7NDevOpsServiceFacade.getAppGroupIdByProjectId(projectId);
        if (appGroupIdByProjectId == null || appGroupIdByProjectId == 0 || Objects.isNull(gitlabAdminApi.getGroup(appGroupIdByProjectId.intValue()))) {
            LOGGER.info("There is no app service group in the project:" + projectId);
            return BaseConstants.Digital.ZERO;
        }
        return appGroupIdByProjectId.intValue();
    }

    private List<RdmMemberAuditRecord> compareGitlabGroupMembers(Long organizationId, Long projectId, List<C7nUserVO> c7nUserVOS, Integer appGroupId) {
        List<GitlabMember> gitlabMembers = getGitlabGroupMembers(appGroupId);
        //查询数据库的权限
        RdmMember rdmMember = new RdmMember();
        rdmMember.setgGroupId(appGroupId);
        rdmMember.setType("group");
        rdmMember.setProjectId(projectId);
        List<RdmMember> dbMembers = memberRepository.select(rdmMember);
        //对比两边的权限
        List<RdmMemberAuditRecord> rdmMemberAuditRecords = compareMembersAndReturnAudit(organizationId, projectId, null, null, dbMembers, gitlabMembers, c7nUserVOS);
        return rdmMemberAuditRecords;
    }

    private List<GitlabMember> getGitlabGroupMembers(Integer appGroupId) {
        //查询所有的group成员
        List<Member> members = gitlabAdminApi.getAllGroupMember(appGroupId);
        List<GitlabMember> gitlabMembers = ConvertUtils.convertList(members, GitlabMember.class);
        // 确定member来自group还是project
        if (!CollectionUtils.isEmpty(gitlabMembers)) {
            gitlabMembers.forEach(member -> {
                //两条member 可能权限相同id 相同，过期时间相同，但是分别来自project和group,所以必须确定来自哪个
                member.setType("group");
            });
        }
        return gitlabMembers;
    }

    /**
     * 审计一个代码库的成员权限
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @param glProjectId
     * @return
     */
    private List<RdmMemberAuditRecord> compareMembersByRepositoryId(Long organizationId,
                                                                    Long projectId,
                                                                    Long repositoryId,
                                                                    Integer glProjectId,
                                                                    Integer appGroupId,
                                                                    List<C7nUserVO> c7nUserVOS) {
        // 判断一下gitlab是否存在该仓库, 避免报错
        Project project = gitlabAdminApi.getProject(glProjectId);
        if (project == null) {
            return Collections.emptyList();
        }

        // 查询gitlab所有成员 这里的查询所有的成员 能将group有角色，而project没有角色的用户查询出来，如果project也有角色，将返回两条数据
        List<Member> members = gitlabAdminApi.getAllMembers(glProjectId);
        List<GitlabMember> gitlabMembers = ConvertUtils.convertList(members, GitlabMember.class);
        List<GitlabMember> gitlabMemberList = new ArrayList<>();
        List<String> gitlabOwnerLoginNames = c7nUserVOS.stream().map(C7nUserVO::getLoginName).collect(Collectors.toList());
        // 查询属于gProject下的成员
        RdmMember rdmMember = new RdmMember();
        rdmMember.setGlProjectId(glProjectId);
        rdmMember.setType("project");
        List<RdmMember> dbMembers = memberRepository.select(rdmMember);
        List<RdmMember> rdmMembers = new ArrayList<>();

        //筛选出项目的权限
        if (!CollectionUtils.isEmpty(gitlabMembers)) {
            gitlabMembers.forEach(member -> {
                //两条member 可能权限相同id 相同，过期时间相同，但是分别来自project和group,所以必须确定来自哪个
                Member groupMember = gitlabAdminApi.getGroupMember(appGroupId, member.getId());
                GitlabMember gitlabMember = ConvertUtils.convertObject(groupMember, GitlabMember.class);
                if (!member.equals(gitlabMember)) {
                    member.setType("project");
                    gitlabMemberList.add(member);
                } else {
                    //如果在gitlab上是owner
                    if (!Objects.isNull(gitlabMember) && gitlabMember.getAccessLevel().value == 50) {
                        //剔除该用户在db里面权限等于50的project类型的记录
                        List<RdmMember> memberList = dbMembers.stream().filter(rdmMember1 -> {
                            return !(gitlabOwnerLoginNames.contains(rdmMember1.getUserId()) && rdmMember1.getType().equals("project") && rdmMember1.getGlProjectId().equals(glProjectId));
                        }).collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(memberList)) {
                            rdmMembers.addAll(memberList);
                        }
                    }
                }
            });
        }


        LOGGER.info("{}项目查询到Gitlab成员数量为:{}", glProjectId, gitlabMembers.size());


        return compareMembersAndReturnAudit(organizationId, projectId, repositoryId, glProjectId, rdmMembers, gitlabMemberList, c7nUserVOS);
    }

    /**
     * 比较数据库成员和Gitlab成员差异, 并返回审计结果
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @param glProjectId
     * @param dbMembers
     * @param glMembers
     * @return 差异数据的列表
     */
    private List<RdmMemberAuditRecord> compareMembersAndReturnAudit(Long organizationId,
                                                                    Long projectId,
                                                                    Long repositoryId,
                                                                    Integer glProjectId,
                                                                    List<RdmMember> dbMembers,
                                                                    List<GitlabMember> glMembers,
                                                                    List<C7nUserVO> c7nUserVOS) {
        //获取项目下有gitlab Owner标签的用户
        List<String> gitlabOwnerLoginNames = c7nUserVOS.stream().map(C7nUserVO::getLoginName).collect(Collectors.toList());
        // 比较是否有差异
        List<RdmMemberAuditRecord> memberAudits = new ArrayList<>();
        dbMembers = comparisonMemberGitlabPermission(organizationId, projectId, repositoryId, glProjectId, dbMembers, glMembers, gitlabOwnerLoginNames, memberAudits);

        // 如果dbMemberMap还有数据, 说明不一致
        if (!dbMembers.isEmpty()) {
            dbMembers.forEach(rdmMember -> {
                memberAudits.add(buildMemberAudit(organizationId, projectId, repositoryId, glProjectId, null, rdmMember));
            });
        }

        // 填补userId
        List<RdmMemberAuditRecord> memberAuditsWrapper = fill(memberAudits);

        // 排除组织管理员且Gitlab权限为Owner的用户(是组织管理员且Gitlab权限正常的不进行审计)
        List<RdmMemberAuditRecord> result = excludeOrgAdmin(memberAuditsWrapper);

        return result;
    }

    private List<RdmMember> comparisonMemberGitlabPermission(Long organizationId, Long projectId, Long repositoryId,
                                                             Integer glProjectId, List<RdmMember> dbMembers,
                                                             List<GitlabMember> glMembers, List<String> loginNames,
                                                             List<RdmMemberAuditRecord> memberAudits) {
        if (CollectionUtils.isEmpty(glMembers)) {
            return dbMembers;
        }
        for (GitlabMember member : glMembers) {
            boolean isDifferent = false;
            RdmMember rdmMember = null;
            //看看这个用户是不是有 gitlabOwner标签
            if (loginNames.contains(member.getUsername())) {
                //如果有
                if (member.getAccessLevel().value == 50) {
                    //移除 这个用户下项目下应用服务的owner权限
                    //找出这个group owner 对应的项目下的所有权限为owner记录，将其剔除
                    dbMembers = dbMembers.stream().filter(rdmMember1 -> !isEqualProjectAccess(rdmMember1, member)).collect(Collectors.toList());
                } else {
                    //有gitlab标签，但是小于50，则全部有问题
                    queryAndCheckPermission(member, isDifferent, dbMembers);
                }
            } else {
                //没有owner标签就是项目成员或者外部成员
                //如果能查到db权限，并且与gitlab的相等，则通过校验，不能 则也是有问题的权限
                queryAndCheckPermission(member, isDifferent, dbMembers);
                if (isDifferent) {
                    memberAudits.add(buildMemberAudit(organizationId, projectId, repositoryId, glProjectId, member, rdmMember));
                    //记录之后移除
                    dbMembers = dbMembers.stream().filter(rdmMember1 -> !isEqualGitlabAccess(rdmMember1, member)).collect(Collectors.toList());
                }
            }
        }
        return dbMembers;
    }

    private void queryAndCheckPermission(GitlabMember member, boolean isDifferent, List<RdmMember> dbMembers) {
        RdmMember rdmMember = getRdmMember(member);
        if (!Objects.isNull(rdmMember)) {
            List<RdmMember> rdmMembers = dbMembers.stream().filter(rdmMember1 -> isEqualGitlabAccess(rdmMember1, member)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(rdmMembers)) {
                RdmMember rdmMember1 = rdmMembers.get(0);
                if (!Objects.equals(member.getAccessLevel().toValue(), rdmMember1.getGlAccessLevel())) {
                    // 如果AccessLevel不相等, 说明不一致
                    isDifferent = true;
                }
                //如果两边的level都为null 则不插入
                if (Objects.isNull(rdmMember1.getGlAccessLevel()) && Objects.isNull(member.getAccessLevel().value)) {
                    isDifferent = false;
                }

                if (!Objects.equals(member.getExpiresAt(), rdmMember1.getGlExpiresAt())) {
                    // 如果ExpiresAt不相等, 说明不一致
                    isDifferent = true;
                }
                if (!Objects.equals(member.getType(), rdmMember1.getType())) {
                    isDifferent = true;
                }
                //剔除
                dbMembers = dbMembers.stream().filter(member1 -> !isEqualGitlabAccess(member1, member)).collect(Collectors.toList());
            }
        } else {
            isDifferent = true;
        }

    }

    private boolean isEqualGitlabAccess(RdmMember rdmMember, GitlabMember gitlabMember) {
        return rdmMember.getGlUserId().intValue() == gitlabMember.getId().intValue()
                && rdmMember.getGlAccessLevel().intValue() == gitlabMember.getAccessLevel().value
                && org.apache.commons.lang3.StringUtils.equalsIgnoreCase(rdmMember.getType(), gitlabMember.getType());
    }

    private RdmMember getRdmMember(GitlabMember member) {
        RdmMember record = new RdmMember();
        record.setType(member.getType());
        record.setGlUserId(member.getId());
        record.setgGroupId(member.getAppGroupId());
        return memberRepository.selectOne(record);
    }

    private boolean isEqualProjectAccess(RdmMember rdmMember, GitlabMember gitlabMember) {
        return rdmMember.getGlUserId().intValue() == gitlabMember.getId().intValue()
                && rdmMember.getGlAccessLevel().intValue() == gitlabMember.getAccessLevel().value
                && "project".equals(rdmMember.getType());
    }

    /**
     * 查询并填补为空的userId
     * 举例:
     * 补全前: userId -> null  glUserId -> 10001
     * 补全后: userId -> 20000 glUserId -> 10001
     *
     * @param memberAudits
     */
    private List<RdmMemberAuditRecord> fill(List<RdmMemberAuditRecord> memberAudits) {
        // Gitlab用户id
        Set<Integer> glUserIds;
        glUserIds = memberAudits.stream().filter(m -> m.getUserId() == null)
                .map(RdmMemberAuditRecord::getGlUserId)
                .collect(Collectors.toSet());
        // 获取Gitlab用户id对应的用户id
        Map<Integer, Long> glToUserIds = c7NDevOpsServiceFacade.mapGlUserIdsToUserIds(glUserIds);

        memberAudits.stream()
                .filter(m -> m.getUserId() == null)
                .forEach(m -> {
                    m.setUserId(glToUserIds.get(m.getGlUserId()));
                });

        return memberAudits;
    }

    private List<RdmMemberAuditRecord> excludeOrgAdmin(List<RdmMemberAuditRecord> memberAudits) {
        // 获取组织管理员
        List<C7nUserVO> orgAdministrators = threadLocal.get();
        Set<Long> orgAdminUserIds = orgAdministrators.stream().map(C7nUserVO::getId).collect(Collectors.toSet());

        return memberAudits.stream()
                .filter(m -> !(orgAdminUserIds.contains(m.getUserId()) && AccessLevel.OWNER.toValue().equals(m.getGlAccessLevel())))
                .collect(Collectors.toList());
    }

    private RdmMemberAuditRecord buildMemberAudit(Long organizationId,
                                                  Long projectId,
                                                  Long repositoryId,
                                                  Integer glProjectId,
                                                  GitlabMember glMember,
                                                  RdmMember dbMember) {
        RdmMemberAuditRecord memberAudit = new RdmMemberAuditRecord()
                .setOrganizationId(organizationId)
                .setProjectId(projectId)
                .setRepositoryId(repositoryId)
                .setGlProjectId(glProjectId);


        if (glMember != null) {
            memberAudit.setGlUserId(glMember.getId())
                    .setGlAccessLevel(glMember.getAccessLevel().toValue())
                    .setGlExpiresAt(glMember.getExpiresAt());
            memberAudit.setType(glMember.getType());
            memberAudit.setgGroupId(glMember.getAppGroupId());
        }

        if (dbMember != null) {
            memberAudit.setUserId(dbMember.getUserId())
                    .setAccessLevel(dbMember.getGlAccessLevel())
                    .setExpiresAt(dbMember.getGlExpiresAt());
        }

        return memberAudit;
    }
}
