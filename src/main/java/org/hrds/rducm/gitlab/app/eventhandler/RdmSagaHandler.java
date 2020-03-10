package org.hrds.rducm.gitlab.app.eventhandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTaskCodeConstants;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class RdmSagaHandler {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(RdmSagaHandler.class);

    /**
     * 调用gitlab添加/修改成员
     */
    @SagaTask(code = SagaTaskCodeConstants.RDUCM_BATCH_ADD_MEMBERS_TO_GITLAB,
            description = "Gitlab添加/修改成员",
            sagaCode = SagaTopicCodeConstants.RDUCM_BATCH_ADD_MEMBERS,
            maxRetryCount = 3,
            seq = 1)
    public String batchAddOrUpdateMembersToGitlab(String data) {
        try {
            System.out.println(data);
            LOGGER.info(data);

            RdmMember rdmMember = new RdmMember();
            rdmMember.setProjectId(-3L);
            rdmMember.setRepositoryId(-3L);
            rdmMember.setUserId(-100086L);
            rdmMember.setState(data);
            rdmMemberRepository.insertSelective(rdmMember);
            List<RdmMember> rdmMembers = objectMapper.readValue(data, new TypeReference<RdmMember>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        DevOpsAppServicePayload devOpsAppServicePayload = gson.fromJson(data, DevOpsAppServicePayload.class);

        // <2> 调用gitlab api添加成员 todo 事务一致性问题
//        rdmMemberRepository.batchAddOrUpdateMembersToGitlab(rdmMembers);

//        try {
//            appServiceService.operationApplication(devOpsAppServicePayload);
//        } catch (Exception e) {
//            appServiceService.setAppErrStatus(data, devOpsAppServicePayload.getIamProjectId());
//            throw e;
//        }
        return data;
    }
}
