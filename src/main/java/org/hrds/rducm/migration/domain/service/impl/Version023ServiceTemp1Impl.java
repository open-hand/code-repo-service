package org.hrds.rducm.migration.domain.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nTenantVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.FeignUtils;
import org.hrds.rducm.migration.domain.facade.MigDevopsServiceFacade;
import org.hrds.rducm.migration.domain.service.Version023STemp1ervice;
import org.hrds.rducm.migration.domain.service.Version023Service;
import org.hrds.rducm.migration.infra.feign.MigDevOpsServiceFeignClient;
import org.hrds.rducm.migration.infra.feign.vo.DevopsUserPermissionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.hrds.rducm.migration.domain.service.impl.Version023TempFixServiceImpl.getDifferenceSetByGuava;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/12
 */
@Service
public class Version023ServiceTemp1Impl implements Version023STemp1ervice {
    private static final Logger logger = LoggerFactory.getLogger(Version023ServiceTemp1Impl.class);

    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() + 1;

    @Autowired
    private MigDevOpsServiceFeignClient migDevOpsServiceFeignClient;
    @Autowired
    private C7nDevOpsServiceFacade c7nDevOpsServiceFacade;
    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MigDevopsServiceFacade migDevopsServiceFacade;

    @Override
    public void initAllPrivilegeOnSiteLevel() {


        final ExecutorService pool = new ThreadPoolExecutor(THREAD_COUNT,
                THREAD_COUNT,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.AbortPolicy());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<C7nTenantVO> c7nTenantVOS = c7nBaseServiceFacade.listAllOrgs();

        // 需要导入的项目集合
        Map<Long, Set<Long>> orgProjects = new HashMap<>();
        // 项目总数
        AtomicInteger projectCount = new AtomicInteger();
        c7nTenantVOS.forEach(vo -> {
            Long organizationId = vo.getTenantId();

            // <1> 获取组织下所有项目
            Set<Long> projectIds = c7nBaseServiceFacade.listProjectIds(organizationId);
            logger.info("该组织{} 下的项目查询完毕", organizationId);

            orgProjects.put(organizationId, projectIds);
            projectCount.addAndGet(projectIds.size());
        });
//        Semaphore semaphore = new Semaphore(THREAD_COUNT);


        // 保证所有线程完成后, 再继续主线程
        CountDownLatch countDownLatch = new CountDownLatch(projectCount.get());

        // 记录导入失败的组织和项目
        Map<String, String> errorProjects = new HashMap<>(16);

        orgProjects.forEach((organizationId, projectIds) -> {
            logger.info("该组织{} 下的所有项目为{}", organizationId, projectIds);

            projectIds.forEach(projectId -> {
//                try {
//                    semaphore.acquire();
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    throw new RuntimeException(e);
//                }

                pool.execute(() -> {
                    try {
                        // 每个项目提交一个事务
                        projectLevel(organizationId, projectId);
                    } catch (Exception e) {
                        logger.error("导入失败的组织项目为:{}", organizationId);
                        errorProjects.put(organizationId + "-" + projectId, e.getMessage());
                        throw e;
                    } finally {
//                        semaphore.release();
                        countDownLatch.countDown();
                    }
                });

            });
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        stopWatch.stop();
        logger.info(stopWatch.prettyPrint());

        if (errorProjects.isEmpty()) {
            logger.info("导入成功");
        } else {
            logger.error("部分组织导入失败, 失败的组织为:{}", errorProjects);
        }

        pool.shutdown();
    }

    private void projectLevel(Long organizationId, Long projectId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // <> 获取项目下所有代码库id和Gitlab项目id
        List<RdmMember> projectList = new ArrayList<>();

        // 修复: 排除已导入的应用服务
        Map<Long, Long> allAppServiceIdMap = c7nDevOpsServiceFacade.listC7nAppServiceIdsMapOnProjectLevel(projectId);

        Map<Long, Long> appServiceIdMap = migDevopsServiceFacade.listC7nAppServiceIdsMapOnProjectLevel(projectId);

        Map<Long, Long> newMap = getDifferenceSetByGuava(allAppServiceIdMap, appServiceIdMap);


        Set<Long> deleteReps = new HashSet<>();

        newMap.forEach((repositoryId, glProjectId) -> {
            logger.info("组织id为{}, 项目id为{}, 代码库id为{}", organizationId, projectId, repositoryId);
            projectList.addAll(repositoryLevel(organizationId, projectId, repositoryId));

            deleteReps.add(repositoryId);
        });

        // 开启事务
        transactionTemplate.execute(status -> {
            // <> 删除权限, 只删除之前导入遗漏的
            deleteReps.forEach(val -> {
//                String[] split = val.split("-");
//                Long projectId = Long.valueOf(split[0]);
                Long repositoryId = val;

                RdmMember delete = new RdmMember();
                delete.setOrganizationId(organizationId);
                delete.setProjectId(projectId);
                delete.setRepositoryId(repositoryId);
                rdmMemberRepository.delete(delete);
            });

            // <> 批量插入
            if (!projectList.isEmpty()) {
                rdmMemberRepository.batchInsertCustom(projectList);
            }

            return null;
        });

        stopWatch.stop();
        logger.info(stopWatch.prettyPrint());
    }


    public List<RdmMember> repositoryLevel(Long organizationId, Long projectId, Long appServiceId) {
        // 查询项目成员权限
        ResponseEntity<Page<DevopsUserPermissionVO>> responseEntity = migDevOpsServiceFeignClient.pagePermissionUsers(projectId, appServiceId, 0, 0, "{}");
        Page<DevopsUserPermissionVO> devopsUserPermissionVOS = FeignUtils.handleResponseEntity(responseEntity);
        List<DevopsUserPermissionVO> list = devopsUserPermissionVOS.getContent();

        // 获取组织管理员
        List<C7nUserVO> orgAdmins = c7nBaseServiceFacade.listOrgAdministrator(organizationId);
        Map<Long, C7nUserVO> orgAdminsMap = orgAdmins.stream().collect(Collectors.toMap(C7nUserVO::getId, v -> v));

        // 如果项目成员也是组织管理员, 也需要设置成Owner
        // 获取需初始化的用户
        list.forEach(vo -> {
            // 是否是组织管理员
            boolean isOrgAdmin = orgAdminsMap.containsKey(vo.getIamUserId());
            if (isOrgAdmin) {
                vo.setGitlabProjectOwner(true);
            }
        });

        List<RdmMember> rdmMembers = transform(organizationId, projectId, appServiceId, list);

        return rdmMembers;
    }

    private List<RdmMember> transform(Long organizationId, Long projectId, Long appServiceId, List<DevopsUserPermissionVO> list) {
        // 查询glProjectId
        Integer glProjectId = c7nDevOpsServiceFacade.repositoryIdToGlProjectId(appServiceId);
        if (glProjectId == null) {
            logger.warn("该应用服务{}无对应glProjectId, 跳过", appServiceId);
            // 跳过
            return Collections.emptyList();
        }
        // 查询glUserId
        Set<Long> userIdSet = list.stream().map(DevopsUserPermissionVO::getIamUserId).collect(Collectors.toSet());
        Map<Long, C7nUserVO> C7nUserVOMap = Optional.ofNullable(c7nBaseServiceFacade.listC7nUserToMap(userIdSet)).orElse(Collections.emptyMap());

        return list.stream().map(v -> {
            Long glUserId = Optional.ofNullable(C7nUserVOMap.get(v.getIamUserId()))
                    .map(C7nUserVO::getGitlabUserId)
                    .orElse(null);

            if (glUserId == null) {
                logger.warn("跳过该用户{}", v.getIamUserId());
            } else {
                RdmMember m = new RdmMember();
                m.setOrganizationId(organizationId);
                m.setProjectId(projectId);
                m.setRepositoryId(appServiceId);
                m.setUserId(v.getIamUserId());
                m.setGlProjectId(glProjectId);
                m.setGlUserId(Math.toIntExact(glUserId));
                if (v.getGitlabProjectOwner()) {
                    // Owner
                    m.setGlAccessLevel(50);
                } else {
                    // Developer
                    m.setGlAccessLevel(30);
                }
                m.setSyncGitlabFlag(true);
                m.setSyncGitlabDate(new Date());
                return m;
            }

            return null;
        }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
