package org.hrds.rducm.gitlab.app.assembler;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmOperationLog;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
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
    public PageInfo<OperationLogViewDTO> pageToOperationLogViewDTO(Page<RdmOperationLog> page) {
        // 操作人用户id, 格式如: {projectId: [userId1, userId2]}
        Multimap<Long, Long> projectIdAndOpUserIds = HashMultimap.create();
        // 代码库id
        Set<Long> repositoryIds = new HashSet<>();

        page.getContent().forEach(v -> {
            projectIdAndOpUserIds.put(v.getProjectId(), v.getOpUserId());
            repositoryIds.add(v.getRepositoryId());
        });

        // 获取操作人用户信息
        Map<Long, C7nUserVO> c7nUserVOMap = new HashMap<>();

        projectIdAndOpUserIds.asMap().forEach((projectId, userIds) -> {
            Map<Long, C7nUserVO> tempMap = ic7nBaseServiceService.listC7nUserToMap(projectId, Sets.newHashSet(userIds));
            c7nUserVOMap.putAll(tempMap);
        });

        // 获取应用服务信息
        Map<Long, C7nAppServiceVO> c7nAppServiceVOMap = ic7nDevOpsServiceService.listC7nAppServiceToMap(repositoryIds);

        return PageConvertUtils.convert(ConvertUtils.convertPage(page, val -> {
            C7nAppServiceVO c7nAppServiceVO = Optional.ofNullable(c7nAppServiceVOMap.get(val.getRepositoryId())).orElse(new C7nAppServiceVO());
            C7nUserVO c7nUserVO = Optional.ofNullable(c7nUserVOMap.get(val.getOpUserId())).orElse(new C7nUserVO());

            OperationLogViewDTO operationLogViewDTO = ConvertUtils.convertObject(val, OperationLogViewDTO.class);
            operationLogViewDTO.setRepositoryName(c7nAppServiceVO.getName());
            operationLogViewDTO.setRepositoryImageUrl(c7nAppServiceVO.getImgUrl());
            operationLogViewDTO.setOpUserName(c7nUserVO.getRealName());
            operationLogViewDTO.setOpUserImageUrl(c7nUserVO.getImageUrl());
            return operationLogViewDTO;
        }));
    }
}