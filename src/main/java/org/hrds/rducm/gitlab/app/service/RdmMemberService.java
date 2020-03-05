package org.hrds.rducm.gitlab.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberUpdateDTO;

/**
 * @author xy
 */
public interface RdmMemberService {

    Page<RdmMemberViewDTO> list(Long projectId, PageRequest pageRequest, RdmMemberQueryDTO query);

    void batchAddOrUpdateMembers(Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO);

    void updateMember(Long memberId, RdmMemberUpdateDTO rdmMemberUpdateDTO);

    void removeMember(Long memberId);

    void handleExpiredMembers();
}
