package org.hrds.rducm.gitlab.app.eventhandler;

import io.choerodon.asgard.saga.annotation.SagaTask;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTaskCodeConstants;
import org.springframework.stereotype.Component;

@Component
public class GitlabSagaHandler {
    /**
     * devops创建分支
     */
//    @SagaTask(code = SagaTaskCodeConstants.DEVOPS_CREATE_BRANCH,
//            description = "devops创建分支",
//            sagaCode = DEVOPS_CREATE_BRANCH,
//            maxRetryCount = 3,
//            seq = 1)
    public String devopsCreateBranch(String data) {
//        BranchSagaPayLoad branchSagaDTO = gson.fromJson(data, BranchSagaPayLoad.class);
//        devopsGitService.createBranchBySaga(branchSagaDTO);
        return data;
    }
}
