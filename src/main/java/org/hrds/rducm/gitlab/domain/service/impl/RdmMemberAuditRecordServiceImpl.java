package org.hrds.rducm.gitlab.domain.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Stopwatch;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberAuditRecordViewDTO;
import org.hrds.rducm.gitlab.app.assembler.RdmMemberAuditRecordAssembler;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabAdminApi;
import org.hrds.rducm.gitlab.infra.util.AssertExtensionUtils;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;
    @Autowired
    private RdmMemberRepository memberRepository;
    @Autowired
    private GitlabAdminApi gitlabAdminApi;
    @Autowired
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;
    @Autowired
    private IC7nBaseServiceService ic7nBaseServiceService;
    @Autowired
    private RdmMemberAuditRecordAssembler rdmMemberAuditRecordAssembler;
    @Autowired
    private IRdmMemberService iRdmMemberService;

    @Override
    public PageInfo<RdmMemberAuditRecordViewDTO> pageByOptions(Long organizationId, Long projectId, PageRequest pageRequest, Set<Long> repositoryIds) {
        Condition condition = Condition.builder(RdmMemberAuditRecord.class)
                .where(Sqls.custom()
                        .andEqualTo(RdmMemberAuditRecord.FIELD_SYNC_FLAG, false)
                        .andIn(RdmMemberAuditRecord.FIELD_ORGANIZATION_ID, Collections.singleton(organizationId))
                        .andIn(RdmMemberAuditRecord.FIELD_PROJECT_ID, Collections.singleton(projectId))
                        .andIn(RdmMemberAuditRecord.FIELD_REPOSITORY_ID, repositoryIds, true))
                .build();

        Page<RdmMemberAuditRecord> page = PageHelper.doPageAndSort(pageRequest, () -> rdmMemberAuditRecordRepository.selectByCondition(condition));

        return rdmMemberAuditRecordAssembler.pageToViewDTO(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RdmMemberAuditRecord> batchCompare(Long organizationId) {
        Stopwatch started = Stopwatch.createStarted();

        // <0> 删除原有数据
        rdmMemberAuditRecordRepository.delete(new RdmMemberAuditRecord().setOrganizationId(organizationId));

        // <1> 对比组织所有成员
        List<RdmMemberAuditRecord> list = compareMembersByOrganizationId(organizationId);

        // <2> 批量插入数据库 todo 可优化为批量插入
        rdmMemberAuditRecordRepository.batchInsertSelective(list);

        long elapsed = started.elapsed(TimeUnit.SECONDS);
        LOGGER.info("执行时长:{}", elapsed);

        return list;
    }

    private List<RdmMemberAuditRecord> compareMembersByOrganizationId(Long organizationId) {
        // <1> 获取组织下所有项目
        Set<Long> projectIds = ic7nBaseServiceService.listProjectIds(organizationId);

        List<RdmMemberAuditRecord> list = projectIds.stream()
                .map(projectId -> {
                    return compareMembersByProjectId(organizationId, projectId);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return list;
    }

    private List<RdmMemberAuditRecord> compareMembersByProjectId(Long organizationId,
                                                                 Long projectId) {
        // 获取项目下所有代码库id和Gitlab项目id
        Map<Long, Long> appServiceIdMap = ic7nDevOpsServiceService.listC7nAppServiceIdsMapOnProjectLevel(projectId);


        List<RdmMemberAuditRecord> list = appServiceIdMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .map((entry) -> {
                    Long repositoryId = entry.getKey();
                    Integer glProjectId = Math.toIntExact(entry.getValue());
                    return compareMembersByRepositoryId(organizationId, projectId, repositoryId, glProjectId);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return list;
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
                                                                    Integer glProjectId) {
        // 查询gitlab所有成员
        List<Member> members = gitlabAdminApi.getAllMembers(glProjectId);
        LOGGER.info("{}项目查询到成员数量为:{}", glProjectId, members.size());

        // 查询数据库所有成员
        List<RdmMember> dbMembers = memberRepository.select(new RdmMember().setGlProjectId(glProjectId));

        return compareMembersAndReturnAudit(organizationId, projectId, repositoryId, glProjectId, dbMembers, members);
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
                                                                    List<Member> glMembers) {
        Map<Integer, RdmMember> dbMemberMap = dbMembers.stream().collect(Collectors.toMap(RdmMember::getGlUserId, m -> m));

        // 比较是否有差异
        List<RdmMemberAuditRecord> memberAudits = new ArrayList<>();
        for (Member member : glMembers) {
            boolean isDifferent = false;

            // 查找数据库是否有此成员
            RdmMember dbMember = dbMemberMap.get(member.getId());

            // 移除
            dbMemberMap.remove(member.getId());
            if (dbMember == null) {
                // 数据库未找到该成员, 说明不一致
                isDifferent = true;
            } else {
                if (!Objects.equals(member.getAccessLevel().toValue(), dbMember.getGlAccessLevel())) {
                    // 如果AccessLevel不相等, 说明不一致
                    isDifferent = true;
                }

                if (!Objects.equals(member.getExpiresAt(), dbMember.getGlExpiresAt())) {
                    // 如果ExpiresAt不相等, 说明不一致
                    isDifferent = true;
                }
            }

            if (isDifferent) {
                memberAudits.add(buildMemberAudit(organizationId, projectId, repositoryId, glProjectId, member, dbMember));
            }
        }

        // 如果dbMemberMap还有数据, 说明不一致
        if (!dbMemberMap.isEmpty()) {
            dbMemberMap.forEach((k, v) -> {
                memberAudits.add(buildMemberAudit(organizationId, projectId, repositoryId, glProjectId, null, v));
            });
        }

        return memberAudits;
    }

    private RdmMemberAuditRecord buildMemberAudit(Long organizationId,
                                                  Long projectId,
                                                  Long repositoryId,
                                                  Integer glProjectId,
                                                  Member glMember,
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
        }

        if (dbMember != null) {
            memberAudit.setUserId(dbMember.getUserId())
                    .setAccessLevel(dbMember.getGlAccessLevel())
                    .setExpiresAt(dbMember.getGlExpiresAt());
        }

        return memberAudit;
    }
}
