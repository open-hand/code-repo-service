package org.hrds.rducm.gitlab.app.assembler;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.ResourceType;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nProjectViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmOperationLog;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hrds.rducm.gitlab.infra.util.PageConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/19
 */
@Component
public class RdmOperationLogAssembler {
    @Autowired
    private IC7nBaseServiceService ic7nBaseServiceService;
    @Autowired
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;

    /**
     * 操作日志查询结果转换
     *
     * @param projectId
     * @param page
     * @return
     */
//    public PageInfo<OperationLogViewDTO> pageToOperationLogViewDTO(Long projectId, Page<RdmOperationLog> page) {
//        // 操作人用户id
//        Set<Long> opUserIds = new HashSet<>();
//        // 代码库id
//        Set<Long> repositoryIds = new HashSet<>();
//
//        page.getContent().forEach(v -> {
//            opUserIds.add(v.getOpUserId());
//            repositoryIds.add(v.getRepositoryId());
//        });
//
//        // 获取操作人用户信息
//        Map<Long, C7nUserVO> c7nUserVOMap = ic7nBaseServiceService.listC7nUserToMap(projectId, opUserIds);
//        Map<Long, C7nAppServiceVO> c7nAppServiceVOMap = ic7nDevOpsServiceService.listC7nAppServiceToMap(repositoryIds);
//
//        return PageConvertUtils.convert(ConvertUtils.convertPage(page, val -> {
//            C7nAppServiceVO c7nAppServiceVO = Optional.ofNullable(c7nAppServiceVOMap.get(val.getRepositoryId())).orElse(new C7nAppServiceVO());
//            C7nUserVO c7nUserVO = Optional.ofNullable(c7nUserVOMap.get(val.getOpUserId())).orElse(new C7nUserVO());
//
//            OperationLogViewDTO operationLogViewDTO = ConvertUtils.convertObject(val, OperationLogViewDTO.class);
//            operationLogViewDTO.setRepositoryName(c7nAppServiceVO.getName());
//            operationLogViewDTO.setRepositoryImageUrl(c7nAppServiceVO.getImgUrl());
//            operationLogViewDTO.setOpUserName(c7nUserVO.getRealName());
//            operationLogViewDTO.setOpUserImageUrl(c7nUserVO.getImageUrl());
//            return operationLogViewDTO;
//        }));
//    }

    /**
     * 操作日志查询结果转换
     *
     * @param page
     * @return
     */
    public PageInfo<OperationLogViewDTO> pageToOperationLogViewDTO(Page<RdmOperationLog> page, ResourceType resourceType) {
        // 操作人用户id
        Set<Long> opUserIds = new HashSet<>();
        // 代码库id
        Set<Long> repositoryIds = new HashSet<>();
        // 获取项目id集合
        Set<Long> projectIds = Sets.newHashSet();
        page.getContent().forEach(v -> {
            opUserIds.add(v.getOpUserId());
            repositoryIds.add(v.getRepositoryId());
            projectIds.add(v.getProjectId());
        });

        // 获取操作人用户信息
        Map<Long, C7nUserVO> c7nUserVOMap = ic7nBaseServiceService.listC7nUserToMap(opUserIds);

        // 获取应用服务信息
        Map<Long, C7nAppServiceVO> c7nAppServiceVOMap = ic7nDevOpsServiceService.listC7nAppServiceToMap(repositoryIds);

        // 查询项目信息(组织层需要)
        Map<Long, C7nProjectVO> c7nProjectVOMap;
        if (ResourceType.ORGANIZATION.equals(resourceType)) {
            c7nProjectVOMap = ic7nBaseServiceService.listProjectsByIdsToMap(projectIds);
        } else {
            c7nProjectVOMap = Collections.emptyMap();
        }

        return PageConvertUtils.convert(ConvertUtils.convertPage(page, val -> {
            C7nAppServiceVO c7nAppServiceVO = Optional.ofNullable(c7nAppServiceVOMap.get(val.getRepositoryId())).orElse(new C7nAppServiceVO());
            C7nUserVO c7nUserVO = Optional.ofNullable(c7nUserVOMap.get(val.getOpUserId())).orElse(new C7nUserVO());

            OperationLogViewDTO operationLogViewDTO = ConvertUtils.convertObject(val, OperationLogViewDTO.class);
            operationLogViewDTO.setRepositoryName(c7nAppServiceVO.getName());
            operationLogViewDTO.setRepositoryImageUrl(c7nAppServiceVO.getImgUrl());
            operationLogViewDTO.setOpUserName(c7nUserVO.getRealName());
            operationLogViewDTO.setOpUserImageUrl(c7nUserVO.getImageUrl());

            // 组织层添加项目信息
            if (ResourceType.ORGANIZATION.equals(resourceType)) {
                C7nProjectVO c7nProjectVO = Optional.ofNullable(c7nProjectVOMap.get(operationLogViewDTO.getProjectId())).orElse(new C7nProjectVO());
                operationLogViewDTO.setProject(BaseC7nProjectViewDTO.convert(c7nProjectVO));
            }
            return operationLogViewDTO;
        }));
    }
}