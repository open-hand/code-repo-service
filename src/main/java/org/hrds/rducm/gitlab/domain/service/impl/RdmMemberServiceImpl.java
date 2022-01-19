package org.hrds.rducm.gitlab.domain.service.impl;

import com.google.common.collect.Sets;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;
import org.hrds.rducm.gitlab.api.controller.dto.MemberAuthDetailViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.MemberPrivilegeViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RepositoryPrivilegeViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseUserQueryDTO;
import org.hrds.rducm.gitlab.domain.aggregate.MemberAuthDetailAgg;
import org.hrds.rducm.gitlab.domain.component.QueryConditionHelper;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hrds.rducm.gitlab.infra.audit.event.OperationEventPublisherHelper;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.admin.GitlabAdminApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.hrds.rducm.gitlab.infra.enums.RdmAccessLevel;
import org.hrds.rducm.gitlab.infra.enums.RdmMemberStateEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nRoleVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.mapper.RdmMemberMapper;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hrds.rducm.gitlab.infra.util.GitlabPermissionUtils;
import org.hrds.rducm.gitlab.infra.util.PageInfoUtil;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    private C7nDevOpsServiceFacade c7NDevOpsServiceFacade;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;
    @Autowired
    private GitlabGroupApi gitlabGroupApi;
    @Autowired
    private RdmMemberMapper rdmMemberMapper;
    @Autowired
    private GitlabPermissionUtils gitlabPermissionUtils;

    @Override
    public Page<MemberAuthDetailViewDTO> pageMembersRepositoryAuthorized(Long organizationId, Long projectId, PageRequest pageRequest, BaseUserQueryDTO queryDTO) {
        // 封装查询条件
        String realName = queryDTO.getRealName();
        String loginName = queryDTO.getLoginName();
        String params = queryDTO.getParams();

        // 调用外部接口模糊查询 用户名或登录名
        Set<Long> userIdsSet = QueryConditionHelper.queryByNameConditionOnProj(projectId, realName, loginName);

        // 根据params多条件查询
        if (!StringUtils.isEmpty(queryDTO.getParams())) {
            Set<Long> userIdsSet1 = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevelAndEnabled(projectId, params, null, null);
            Set<Long> userIdsSet2 = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevelAndEnabled(projectId, null, params, null);
            if (Objects.isNull(userIdsSet)) {
                userIdsSet = new HashSet<>();
            }
            userIdsSet.addAll(userIdsSet1);
            userIdsSet.addAll(userIdsSet2);
        }

        if (userIdsSet != null && userIdsSet.isEmpty()) {
            return new Page<>();
        }

        Set<Long> finalUserIdsSet = userIdsSet;
        Page<MemberAuthDetailAgg> page = PageHelper.doPageAndSort(pageRequest, () -> rdmMemberRepository.selectMembersRepositoryAuthorized(organizationId, projectId, finalUserIdsSet));

        // 查询应用服务总数
        int allRepositoryCount = c7NDevOpsServiceFacade.listC7nAppServiceOnProjectLevel(projectId).size();
        BigDecimal allRepositoryCountBigD = new BigDecimal(allRepositoryCount);

        // 用户id
        Set<Long> userIds = new HashSet<>();

        page.getContent().forEach(v -> {
            userIds.add(v.getUserId());
        });

        // 获取操作人用户信息, 带有项目角色信息
        Map<Long, C7nUserVO> userWithRolesVOMap = c7NBaseServiceFacade.listC7nUserToMapOnProjectLevel(projectId, userIds);
        // 查询用户信息
        Map<Long, C7nUserVO> userVOMap = c7NBaseServiceFacade.listC7nUserToMap(Sets.newHashSet(userIds));


        Page<MemberAuthDetailViewDTO> pageReturn = ConvertUtils.convertPage(page, (v) -> {
            MemberAuthDetailViewDTO viewDTO = ConvertUtils.convertObject(v, MemberAuthDetailViewDTO.class);
            C7nUserVO c7nUserVO = Optional.ofNullable(userVOMap.get(v.getUserId()))
                    .orElse(new C7nUserVO());
            C7nUserVO c7nUserWithRolesVO = Optional.ofNullable(userWithRolesVOMap.get(v.getUserId()))
                    .orElse(new C7nUserVO().setRoles(Collections.emptyList()));

            viewDTO.setAllRepositoryCount(allRepositoryCount);
            viewDTO.setUser(BaseC7nUserViewDTO.convert(c7nUserVO));
            viewDTO.setRoleNames(c7nUserWithRolesVO.getRoles().stream().map(C7nRoleVO::getName).collect(Collectors.toList()));

            BigDecimal authorizedRepositoryCountBigD = Optional.ofNullable(viewDTO.getAuthorizedRepositoryCount()).map(BigDecimal::new).orElse(BigDecimal.ZERO);
            //根据角色来判断授权的服务的百分比
            C7nUserVO userVO = c7NBaseServiceFacade.detailC7nUserOnProjectLevel(projectId, v.getUserId());
            if (userVO != null && userVO.isProjectAdmin()) {
                viewDTO.setAuthorizedRepositoryCount(viewDTO.getAllRepositoryCount());
                authorizedRepositoryCountBigD = new BigDecimal(viewDTO.getAuthorizedRepositoryCount());
            } else {
                //如果拥有group层级的权限
                RdmMember rdmMember = new RdmMember();
                rdmMember.setProjectId(projectId);
                rdmMember.setUserId(v.getUserId());
                rdmMember.setType(AuthorityTypeEnum.GROUP.getValue());
                rdmMember.setSyncGitlabFlag(Boolean.TRUE);
                List<RdmMember> rdmMembers = rdmMemberMapper.select(rdmMember);
                if (!CollectionUtils.isEmpty(rdmMembers)) {
                    viewDTO.setAuthorizedRepositoryCount(viewDTO.getAllRepositoryCount());
                    authorizedRepositoryCountBigD = new BigDecimal(viewDTO.getAuthorizedRepositoryCount());
                }
            }
            if (allRepositoryCountBigD.compareTo(BigDecimal.ZERO) != 0) {
                viewDTO.setAuthorizedRepositoryPercent(authorizedRepositoryCountBigD.divide(allRepositoryCountBigD, 4, BigDecimal.ROUND_HALF_UP));
            }

            return viewDTO;
        });

        return pageReturn;
    }

    @Override
    public Page<RdmMemberViewDTO> pageMemberPermissions(Long organizationId,
                                                        Long projectId,
                                                        Long userId,
                                                        PageRequest pageRequest) {


        // 代码库id
        Set<Long> repositoryIds = new HashSet<>();
        RdmMember userGroupPermission = gitlabPermissionUtils.getUserGroupPermission(userId, projectId);
        if (userGroupPermission.getGlAccessLevel() == null) {
            return getRdmMemberViewDTOS(organizationId, projectId, userId, pageRequest, repositoryIds);

        } else {
            Page<RdmMember> page = getRdmMembers(organizationId, projectId, userId, pageRequest);
            //查询项目下所有的应用服务
            if (!CollectionUtils.isEmpty(page.getContent())) {
                return getRdmMemberViewDTOS(projectId, userId, repositoryIds, page);
            } else {
                return getRdmMemberViewDTOS(projectId, pageRequest, userGroupPermission);
            }
        }

    }

    private Page<RdmMemberViewDTO> getRdmMemberViewDTOS(Long projectId, PageRequest pageRequest, RdmMember userGroupPermission) {
        List<C7nAppServiceVO> c7nAppServiceVOS = c7NDevOpsServiceFacade.listC7nAppServiceOnProjectLevel(projectId);
        Page<C7nAppServiceVO> pageFromList = PageInfoUtil.createPageFromList(c7nAppServiceVOS, pageRequest);
        Page<RdmMemberViewDTO> pageReturn = ConvertUtils.convertPage(pageFromList, (v) -> {
            RdmMemberViewDTO rdmMemberViewDTO = new RdmMemberViewDTO();
            rdmMemberViewDTO.setGlAccessLevel(userGroupPermission.getGlAccessLevel());
            rdmMemberViewDTO.setRepositoryName(v.getName());
            rdmMemberViewDTO.setLastUpdateDate(userGroupPermission.getLastUpdateDate());
            rdmMemberViewDTO.setCreationDate(userGroupPermission.getCreationDate());
            return rdmMemberViewDTO;
        });
        return pageReturn;
    }

    private Page<RdmMemberViewDTO> getRdmMemberViewDTOS(Long projectId, Long userId, Set<Long> repositoryIds, Page<RdmMember> page) {
        page.getContent().forEach(rdmMember -> {
            if (rdmMember.getRepositoryId() != null) {
                repositoryIds.add(rdmMember.getRepositoryId());
            }
        });

        List<C7nAppServiceVO> c7nAppServiceVOS = c7NDevOpsServiceFacade.listC7nAppServiceOnProjectLevel(projectId);
        Map<Long, C7nAppServiceVO> c7nAppServiceVOMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(c7nAppServiceVOS)) {
            // 获取应用服务信息
            c7nAppServiceVOMap = c7NDevOpsServiceFacade.listC7nAppServiceToMap(repositoryIds);
        }
        Map<Long, C7nAppServiceVO> finalC7nAppServiceVOMap = c7nAppServiceVOMap;
        Page<RdmMemberViewDTO> pageReturn = ConvertUtils.convertPage(page, (v) -> {
            RdmMemberViewDTO viewDTO = ConvertUtils.convertObject(v, RdmMemberViewDTO.class);
            C7nAppServiceVO c7nAppServiceVO = finalC7nAppServiceVOMap.get(v.getRepositoryId());
            if (c7nAppServiceVO != null) {
                viewDTO.setRepositoryName(c7nAppServiceVO.getName());
                viewDTO.setGlAccessLevel(gitlabPermissionUtils.getUserRepositoryAccessLevel(userId, projectId, c7nAppServiceVO.getId()));
            }
            return viewDTO;
        });
        return pageReturn;
    }

    private Page<RdmMemberViewDTO> getRdmMemberViewDTOS(Long organizationId, Long projectId, Long userId, PageRequest pageRequest, Set<Long> repositoryIds) {
        Page<RdmMember> page = getRdmMembers(organizationId, projectId, userId, pageRequest);
        if (CollectionUtils.isEmpty(page.getContent())) {
            return new Page<>();
        }
        page.getContent().forEach(v -> {
            if (v.getRepositoryId() != null) {
                repositoryIds.add(v.getRepositoryId());
            }
        });
        Map<Long, C7nAppServiceVO> longC7nAppServiceVOMap = c7NDevOpsServiceFacade.listC7nAppServiceToMap(repositoryIds);
        Page<RdmMemberViewDTO> pageReturn = ConvertUtils.convertPage(page, (v) -> {
            RdmMemberViewDTO viewDTO = ConvertUtils.convertObject(v, RdmMemberViewDTO.class);
            C7nAppServiceVO c7nAppServiceVO = longC7nAppServiceVOMap.get(v.getRepositoryId());
            if (c7nAppServiceVO != null) {
                viewDTO.setRepositoryName(c7nAppServiceVO.getName());
            }
            return viewDTO;
        });
        return pageReturn;
    }

    private Page<RdmMember> getRdmMembers(Long organizationId, Long projectId, Long userId, PageRequest pageRequest) {
        RdmMember condition = new RdmMember();
        condition.setOrganizationId(organizationId);
        condition.setProjectId(projectId);
        condition.setUserId(userId);
        condition.setType(AuthorityTypeEnum.PROJECT.getValue());
        condition.setSyncGitlabFlag(Boolean.TRUE);

        return PageHelper.doPageAndSort(pageRequest, () -> rdmMemberRepository.select(condition));
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

    private void removeGroupMemberToGitLab(Integer gGroupId, Integer glUserId) {
        gitlabGroupApi.removeMember(gGroupId, glUserId);
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
        Integer glProjectId = c7NDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);

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
                    Long userId = c7NDevOpsServiceFacade.glUserIdToUserId(glMember.getId());

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
        if (param.getGlProjectId() == null && param.getGlUserId() == null) {
            rdmMemberRepository.deleteByPrimaryKey(param.getId());
        }
        Member glMember = gitlabProjectApi.getMember(param.getGlProjectId(), param.getGlUserId());
        // 理论上只会查询到一个成员
        if (glMember == null) {
            // 移除数据库成员
            rdmMemberRepository.deleteByPrimaryKey(param.getId());
        } else {
            //TODO 此处逻辑待确认
            //当添加了一个项目层自定义角色{GITLAB_OWNER}的团队成员用户A，此时gitlab中用户A已经是OWNER了
            //且在代码库分配权限时（Maintainer，developer， reporter，guest）时，rducm_gitlab_member会提示无法移除owner权限，
            //因此调用同步方法时，会判断gitlab中的用户A权限级别为50>=owner，走此处的删除方法，直接在代码库中删除了这个成员
            if (glMember.getAccessLevel().toValue() >= AccessLevel.OWNER.toValue()) {
                // 移除数据库成员
                rdmMemberRepository.deleteByPrimaryKey(param.getId());
            } else {
                // 更新数据库成员
                updateMemberAfter(param, glMember);
            }
        }
    }

    @Override
    public List<MemberPrivilegeViewDTO> selfPrivilege(Long organizationId,
                                                      Long projectId,
                                                      Set<Long> repositoryIds) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        RdmMember groupMember = new RdmMember();
        groupMember.setType(AuthorityTypeEnum.GROUP.getValue());
        groupMember.setUserId(userId);
        groupMember.setProjectId(projectId);
        RdmMember rdmMember = rdmMemberMapper.selectOne(groupMember);
        //如果全局层的权限为null,就查找项目层的
        if (rdmMember == null) {
            return repositoryIds.stream().map(repositoryId -> {
                RdmMember dbMember = Optional.ofNullable(rdmMemberRepository.selectOneByUk(projectId, repositoryId, userId))
                        .orElse(new RdmMember());
                MemberPrivilegeViewDTO viewDTO = new MemberPrivilegeViewDTO();
                viewDTO.setRepositoryId(repositoryId)
                        .setAccessLevel(dbMember.getGlAccessLevel());
                return viewDTO;
            }).collect(Collectors.toList());
        }
        // 综合返回全局和仓库的权限
        return repositoryIds.stream().map(repositoryId -> {
            RdmMember dbMember = Optional.ofNullable(rdmMemberRepository.selectOneByUk(projectId, repositoryId, userId))
                    .orElse(new RdmMember());
            MemberPrivilegeViewDTO viewDTO = new MemberPrivilegeViewDTO();
            viewDTO.setRepositoryId(repositoryId);
            if (dbMember.getGlAccessLevel() == null) {
                viewDTO.setAccessLevel(rdmMember.getGlAccessLevel());
            } else {
                if (dbMember.getGlAccessLevel() > rdmMember.getGlAccessLevel()) {
                    viewDTO.setAccessLevel(dbMember.getGlAccessLevel());
                } else {
                    viewDTO.setAccessLevel(rdmMember.getGlAccessLevel());
                }
            }
            return viewDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RepositoryPrivilegeViewDTO> selectRepositoriesByPrivilege(Long organizationId, Long projectId, Set<Long> userIds) {
        //先判断有没有组的权限
        Condition condition = Condition.builder(RdmMember.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RdmMember.FIELD_ORGANIZATION_ID, organizationId)
                        .andEqualTo(RdmMember.FIELD_PROJECT_ID, projectId)
                        .andIn(RdmMember.FIELD_USER_ID, userIds)
                        .andEqualTo(RdmMember.FIELD_TYPE, "group")
                        // 同步状态需为true
                        .andEqualTo(RdmMember.FIELD_SYNC_GITLAB_FLAG, Boolean.TRUE))
                .build();
        List<RdmMember> rdmMembers = rdmMemberRepository.selectByCondition(condition);
        List<RepositoryPrivilegeViewDTO> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(rdmMembers)) {
            rdmMembers.forEach(rdmMember -> {
                RepositoryPrivilegeViewDTO repositoryPrivilegeViewDTO = new RepositoryPrivilegeViewDTO();
                //查询项目下应用服务的
                List<C7nAppServiceVO> c7nAppServiceVOS = c7NDevOpsServiceFacade.listC7nAppServiceOnProjectLevel(projectId);
                repositoryPrivilegeViewDTO.setUserId(rdmMember.getUserId());
                if (!CollectionUtils.isEmpty(c7nAppServiceVOS)) {
                    repositoryPrivilegeViewDTO.setAppServiceIds(c7nAppServiceVOS.stream().map(C7nAppServiceVO::getId).collect(Collectors.toSet()));
                }
                result.add(repositoryPrivilegeViewDTO);
            });
        } else {
            Condition projectCondition = Condition.builder(RdmMember.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(RdmMember.FIELD_ORGANIZATION_ID, organizationId)
                            .andEqualTo(RdmMember.FIELD_PROJECT_ID, projectId)
                            .andIn(RdmMember.FIELD_USER_ID, userIds)
                            .andEqualTo(RdmMember.FIELD_TYPE, "project")
                            // 同步状态需为true
                            .andEqualTo(RdmMember.FIELD_SYNC_GITLAB_FLAG, Boolean.TRUE))
                    .build();
            List<RdmMember> projectRdmMembers = rdmMemberRepository.selectByCondition(projectCondition);
            Map<Long, List<RdmMember>> group = projectRdmMembers.stream().collect(Collectors.groupingBy(RdmMember::getUserId));
            group.forEach((k, v) -> {
                RepositoryPrivilegeViewDTO viewDTO = new RepositoryPrivilegeViewDTO();
                viewDTO.setUserId(k);
                viewDTO.setAppServiceIds(v.stream().map(RdmMember::getRepositoryId).collect(Collectors.toSet()));
                result.add(viewDTO);
            });
        }
        return result;
    }

    @Override
    public RepositoryPrivilegeViewDTO selectOrgRepositoriesByDeveloper(Long organizationId, Long userId) {
        Condition condition = Condition.builder(RdmMember.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RdmMember.FIELD_ORGANIZATION_ID, organizationId)
                        .andEqualTo(RdmMember.FIELD_USER_ID, userId)
                        // 同步状态需为true
                        .andEqualTo(RdmMember.FIELD_SYNC_GITLAB_FLAG, Boolean.TRUE)
                        .andGreaterThanOrEqualTo(RdmMember.FIELD_GL_ACCESS_LEVEL, RdmAccessLevel.DEVELOPER.toValue()))
                .build();
        List<RdmMember> rdmMembers = rdmMemberRepository.selectByCondition(condition);
        //处理rdmMembers，包含project层和group层的
        RepositoryPrivilegeViewDTO result = new RepositoryPrivilegeViewDTO();
        result.setUserId(userId);
        //如果是group层的，则查询devops
        if (CollectionUtils.isEmpty(rdmMembers)) {
            result.setAppServiceIds(Collections.EMPTY_SET);
            return result;
        }
        Set<Long> appServiceIds = new HashSet<>();
        List<Long> projectIds = rdmMembers.stream().filter(rdmMember -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(rdmMember.getType(), AuthorityTypeEnum.GROUP.getValue())).map(RdmMember::getProjectId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(projectIds)) {
            List<C7nAppServiceVO> c7nAppServiceVOS = c7NDevOpsServiceFacade.queryAppByProjectIds(0L, projectIds);
            if (!CollectionUtils.isEmpty(c7nAppServiceVOS)) {
                Set<Long> groupAppServiceIds = c7nAppServiceVOS.stream().map(C7nAppServiceVO::getId).collect(Collectors.toSet());
                appServiceIds.addAll(groupAppServiceIds);
            }
        }
        Set<Long> projectAppServiceIds = rdmMembers.stream().filter(rdmMember -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(rdmMember.getType(), AuthorityTypeEnum.PROJECT.getValue())).map(RdmMember::getRepositoryId).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(projectAppServiceIds)) {
            appServiceIds.addAll(projectAppServiceIds);
        }
        result.setAppServiceIds(appServiceIds);
        return result;
    }

    @Override
    public void publishMemberEvent(RdmMember param, MemberEvent.EventType eventType) {
        // 发送事件
        MemberEvent.EventParam eventParam = buildEventParam(param.getOrganizationId(), param.getProjectId(), param.getRepositoryId(), param.getUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());
        OperationEventPublisherHelper.publishMemberEvent(new MemberEvent(this, eventType, eventParam));
    }


    @Override
    public List<RepositoryPrivilegeViewDTO> listMemberRepositoriesByAccesses(Long organizationId, Long projectId, Set<Long> userIds, Integer accessLevel, Long appId) {
        //查询全局层的权限
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.EMPTY_LIST;
        }
        List<RepositoryPrivilegeViewDTO> repositoryPrivilegeViewDTOS = new ArrayList<>();
        userIds.forEach(userId -> {
            RepositoryPrivilegeViewDTO repositoryPrivilegeViewDTO = new RepositoryPrivilegeViewDTO();
            repositoryPrivilegeViewDTO.setUserId(userId);
            //先查项目层的权限
            RdmMember groupMember = new RdmMember();
            groupMember.setType(AuthorityTypeEnum.GROUP.getValue());
            groupMember.setUserId(userId);
            groupMember.setProjectId(projectId);
            RdmMember rdmMember = rdmMemberMapper.selectOne(groupMember);
            if (rdmMember != null && rdmMember.getGlAccessLevel() >= accessLevel.intValue()) {
                HashSet<Long> appIds = new HashSet<>();
                appIds.add(appId);
                repositoryPrivilegeViewDTO.setAppServiceIds(appIds);
            } else {
                //查询项目层的权限
                RdmMember dbMember = Optional.ofNullable(rdmMemberRepository.selectOneByUk(projectId, appId, userId))
                        .orElse(new RdmMember());
                if (dbMember != null && dbMember.getGlAccessLevel() != null && dbMember.getGlAccessLevel() >= accessLevel.intValue()) {
                    HashSet<Long> appIds = new HashSet<>();
                    appIds.add(appId);
                    repositoryPrivilegeViewDTO.setAppServiceIds(appIds);
                }
            }
            repositoryPrivilegeViewDTOS.add(repositoryPrivilegeViewDTO);
        });

        return repositoryPrivilegeViewDTOS;
    }

    @Override
    public void syncGroupMemberFromGitlab(RdmMember dbMember) {
        // <1> 获取Gitlab成员, 并更新数据库
        if (dbMember.getGlUserId() == null && dbMember.getgGroupId() == null) {
            rdmMemberRepository.deleteByPrimaryKey(dbMember.getId());
        }
        Member glMember = gitlabGroupApi.getMember(dbMember.getgGroupId(), dbMember.getGlUserId());
        // 理论上只会查询到一个成员
        if (glMember == null) {
            // 移除数据库成员
            rdmMemberRepository.deleteByPrimaryKey(dbMember.getId());
        } else {
            if (glMember.getAccessLevel().toValue() >= AccessLevel.OWNER.toValue()) {
                // 移除数据库成员
                rdmMemberRepository.deleteByPrimaryKey(dbMember.getId());
            } else {
                // 更新数据库成员
                updateMemberAfter(dbMember, glMember);
            }
        }
    }

    @Override
    public Member tryRemoveAndAddGroupMemberToGitlab(Integer gGroupId, Integer glUserId, Integer accessLevel, Date expiresAt) {
        // 尝试移除成员
        this.tryRemoveGroupMemberToGitlab(gGroupId, glUserId);
        // 添加新成员
        return this.addGroupMemberToGitlab(gGroupId, glUserId, accessLevel, expiresAt);
    }

    @Override
    public void updateGroupMemberToGitLab(Integer gGroupId, Integer glUserId, Integer accessLevel, Date expiresAt) {
        Member glMember = gitlabGroupApi.getMember(gGroupId, glUserId);
        if (glMember != null) {
            if (glMember.getAccessLevel().toValue() >= RdmAccessLevel.OWNER.toValue()) {
                throw new CommonException("error.not.allow.remove.owner", glMember.getName());
            }
            try {
                gitlabGroupApi.updateMember(gGroupId, glUserId, accessLevel, expiresAt);
            } catch (GitlabClientException e) {
                throw new CommonException("error.member.not.allow.change", e, glMember.getName());
            }
        } else {
            if (glMember.getAccessLevel().toValue() >= RdmAccessLevel.OWNER.toValue()) {
                throw new CommonException("error.not.allow.remove.owner", glMember.getName());
            }
            try {
                this.addGroupMemberToGitlab(gGroupId, glUserId, accessLevel, expiresAt);
            } catch (GitlabClientException e) {
                throw new CommonException("error.member.not.allow.change", e, glMember.getName());
            }
        }
    }

    private Member addGroupMemberToGitlab(Integer gGroupId, Integer glUserId, Integer accessLevel, Date expiresAt) {
        // 调用gitlab api添加成员
        return gitlabGroupApi.addMember(gGroupId, glUserId, accessLevel, expiresAt);
    }

    private void tryRemoveGroupMemberToGitlab(Integer gGroupId, Integer glUserId) {
        // 先查询Gitlab用户
        Member glMember = gitlabGroupApi.getMember(gGroupId, glUserId);

        if (glMember != null) {
            if (glMember.getAccessLevel().toValue() >= RdmAccessLevel.OWNER.toValue()) {
                throw new CommonException("error.not.allow.remove.owner", glMember.getName());
            }

            try {
                this.removeGroupMemberToGitLab(gGroupId, glUserId);
            } catch (GitlabClientException e) {
                throw new CommonException("error.member.not.allow.change", e, glMember.getName());
            }
        }
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
