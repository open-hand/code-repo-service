package org.hrds.rducm.gitlab.app.assembler;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberAuditRecordViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
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
public class RdmMemberAuditRecordAssembler {
    @Autowired
    private IC7nBaseServiceService ic7nBaseServiceService;
    @Autowired
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;

    /**
     * 查询结果转换
     *
     * @param page
     * @return
     */
    public PageInfo<RdmMemberAuditRecordViewDTO> pageToViewDTO(Page<RdmMemberAuditRecord> page) {
        // 用户id
        Set<Long> userIds = new HashSet<>();
        // 代码库id
        Set<Long> repositoryIds = new HashSet<>();
        // Gitlab用户id
        Set<Integer> glUserIds = new HashSet<>();

        page.getContent().forEach(v -> {
            userIds.add(v.getUserId());
            repositoryIds.add(v.getRepositoryId());

            if (v.getUserId() == null) {
                glUserIds.add(v.getGlUserId());
            }
        });

        // 获取Gitlab用户id对应的用户id
        Map<Integer, Long> glToUserIds = ic7nDevOpsServiceService.mapGlUserIdsToUserIds(glUserIds);

        // Gitlab用户查询的userId添加到集合里
        userIds.addAll(glToUserIds.values());

        // 获取用户信息
        Map<Long, C7nUserVO> c7nUserVOMap = ic7nBaseServiceService.listC7nUserToMap(userIds);

        // 获取应用服务信息
        Map<Long, C7nAppServiceVO> c7nAppServiceVOMap = ic7nDevOpsServiceService.listC7nAppServiceToMap(repositoryIds);



        return PageConvertUtils.convert(ConvertUtils.convertPage(page, val -> {
            // 若无userId, 先根据glUserId获取userId
            if (val.getUserId() == null) {
                val.setUserId(glToUserIds.get(val.getGlUserId()));
            }

            C7nAppServiceVO c7nAppServiceVO = Optional.ofNullable(c7nAppServiceVOMap.get(val.getRepositoryId())).orElse(new C7nAppServiceVO());
            C7nUserVO c7nUserVO = Optional.ofNullable(c7nUserVOMap.get(val.getUserId())).orElse(new C7nUserVO());

            RdmMemberAuditRecordViewDTO viewDTO = ConvertUtils.convertObject(val, RdmMemberAuditRecordViewDTO.class);
            viewDTO.setUser(BaseC7nUserViewDTO.convert(c7nUserVO));
            viewDTO.setRepositoryName(c7nAppServiceVO.getName());
            return viewDTO;
        }));
    }

//    private Set<Long> glUserIdsToUserIds(Set<Integer> glUserIds) {
//        // 查询Gitlab用户id对应的猪齿鱼用户id
//        // todo
//        Map<Integer, Long> glToUserIds = ic7nDevOpsServiceService.mapGlUserIdsToUserIds(glUserIds);
//
//        return Collections.emptySet();
//    }
}