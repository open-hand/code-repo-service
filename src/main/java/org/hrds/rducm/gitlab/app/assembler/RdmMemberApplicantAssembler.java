package org.hrds.rducm.gitlab.app.assembler;

import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberApplicantViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApplicant;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/20
 */
@Component
public class RdmMemberApplicantAssembler {
    @Autowired
    private C7nDevOpsServiceFacade c7NDevOpsServiceFacade;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;

    /**
     * 查询结果转换
     *
     * @param page
     * @return
     */
    public Page<RdmMemberApplicantViewDTO> pageToRdmMemberApplicantViewDTO(Page<RdmMemberApplicant> page) {
        // 用户id
        Set<Long> opUserIds = new HashSet<>();
        // 代码库id
        Set<Long> repositoryIds = new HashSet<>();

        page.getContent().forEach(v -> {
            opUserIds.add(v.getApplicantUserId());
            opUserIds.add(v.getApprovalUserId());
            repositoryIds.add(v.getRepositoryId());
        });

        // 获取操作人用户信息
        Map<Long, C7nUserVO> c7nUserVOMap = c7NBaseServiceFacade.listC7nUserToMap(opUserIds);
        Map<Long, C7nAppServiceVO> c7nAppServiceVOMap = c7NDevOpsServiceFacade.listC7nAppServiceToMap(repositoryIds);

        return ConvertUtils.convertPage(page, val -> {
            C7nAppServiceVO c7nAppServiceVO = Optional.ofNullable(c7nAppServiceVOMap.get(val.getRepositoryId())).orElse(new C7nAppServiceVO());
            C7nUserVO c7nApplicantUserVO = Optional.ofNullable(c7nUserVOMap.get(val.getApplicantUserId())).orElse(new C7nUserVO());
            C7nUserVO c7nApprovalUserVO = Optional.ofNullable(c7nUserVOMap.get(val.getApprovalUserId())).orElse(new C7nUserVO());

            RdmMemberApplicantViewDTO viewDTO = ConvertUtils.convertObject(val, RdmMemberApplicantViewDTO.class);
            viewDTO.setRepositoryName(c7nAppServiceVO.getName());
            // 申请人
            viewDTO.setApplicantUser(new BaseC7nUserViewDTO()
                    .setUserId(val.getApplicantUserId())
                    .setRealName(c7nApplicantUserVO.getRealName())
                    .setLoginName(c7nApplicantUserVO.getLoginName())
                    .setImageUrl(c7nApplicantUserVO.getImageUrl()));
            // 审批人
            viewDTO.setApprovalUser(new BaseC7nUserViewDTO()
                    .setUserId(val.getApprovalUserId())
                    .setRealName(c7nApprovalUserVO.getRealName())
                    .setLoginName(c7nApprovalUserVO.getLoginName())
                    .setImageUrl(c7nApprovalUserVO.getImageUrl()));
            return viewDTO;
        });
    }
}
