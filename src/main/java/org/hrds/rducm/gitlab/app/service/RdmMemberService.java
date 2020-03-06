package org.hrds.rducm.gitlab.app.service;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberUpdateDTO;
import org.springframework.transaction.annotation.Transactional;

import static org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants.RDUCM_BATCH_ADD_MEMBERS;

/**
 * @author xy
 */
public interface RdmMemberService {

    Page<RdmMemberViewDTO> list(Long projectId, PageRequest pageRequest, RdmMemberQueryDTO query);

    void batchAddOrUpdateMembers(Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO);

    void updateMember(Long memberId, RdmMemberUpdateDTO rdmMemberUpdateDTO);

    void removeMember(Long memberId);

    void handleExpiredMembers();

    /**
     * saga测试demo
     * @param projectId
     * @param rdmMemberBatchDTO
     */
    void batchAddMemberSagaDemo(Long projectId, RdmMemberBatchDTO rdmMemberBatchDTO);
}
