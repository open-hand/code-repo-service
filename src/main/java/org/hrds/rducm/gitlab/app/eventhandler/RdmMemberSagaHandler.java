//package org.hrds.rducm.gitlab.app.eventhandler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.choerodon.asgard.saga.annotation.SagaTask;
//import io.choerodon.mybatis.domain.AuditDomain;
//import org.gitlab4j.api.models.Member;
//import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTaskCodeConstants;
//import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants;
//import org.hrds.rducm.gitlab.domain.entity.RdmMember;
//import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
//import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class RdmMemberSagaHandler {
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private IRdmMemberService iRdmMemberService;
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(RdmMemberSagaHandler.class);
//
//    /**
//     * 调用gitlab添加/修改成员
//     */
//    @SagaTask(code = SagaTaskCodeConstants.RDUCM_BATCH_ADD_MEMBERS_TO_GITLAB,
//            description = "Gitlab添加/修改成员",
//            sagaCode = SagaTopicCodeConstants.RDUCM_BATCH_ADD_MEMBERS,
//            maxRetryCount = 3,
//            seq = 1)
//    public String batchAddOrUpdateMembersToGitlab(String data) {
//        List<RdmMember> rdmMembers = new ArrayList<>();
//
//        // <2> 调用gitlab api添加成员
//        rdmMembers.forEach((m) -> {
//            // <2.1> 判断新增或更新
//            boolean isExists;
//            if (m.get_status().equals(AuditDomain.RecordStatus.create)) {
//                isExists = false;
//            } else if (m.get_status().equals(AuditDomain.RecordStatus.update)) {
//                isExists = true;
//            } else {
//                throw new IllegalArgumentException("record status is invalid");
//            }
//
//            // <2.2> 新增或更新成员至gitlab
//            Member glMember = iRdmMemberService.tryRemoveAndAddMemberToGitlab(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
//
//            // <2.3> 回写数据库
//            iRdmMemberService.updateMemberAfter(m, glMember);
//
//            // <2.4> 发送事件
//            if (isExists) {
//                iRdmMemberService.publishMemberEvent(m, MemberEvent.EventType.UPDATE_MEMBER);
//            } else {
//                iRdmMemberService.publishMemberEvent(m, MemberEvent.EventType.ADD_MEMBER);
//            }
//        });
//
//        return data;
//    }
//
//    /**
//     * 回写数据库
//     */
////    @SagaTask(code = SagaTaskCodeConstants.RDUCM_BATCH_ADD_MEMBERS_TO_GITLAB,
////            description = "回写数据库",
////            sagaCode = SagaTopicCodeConstants.RDUCM_BATCH_ADD_MEMBERS,
////            maxRetryCount = 3,
////            seq = 1)
//    public void updateAfter(String data) {
////        List<RdmMember> rdmMembers = new ArrayList<>();
////
////        // <2> 调用gitlab api添加成员
////        rdmMembers.forEach((m) -> {
////            // <2.1> 判断新增或更新
////            boolean isExists;
////            if (m.get_status().equals(AuditDomain.RecordStatus.create)) {
////                isExists = false;
////            } else if (m.get_status().equals(AuditDomain.RecordStatus.update)) {
////                isExists = true;
////            } else {
////                throw new IllegalArgumentException("record status is invalid");
////            }
////
////            // <2.3> 回写数据库
////            iRdmMemberService.updateMemberAfter(m, glMember);
////
////            // <2.4> 发送事件
////            if (isExists) {
////                iRdmMemberService.publishMemberEvent(m, MemberEvent.EventType.UPDATE_MEMBER);
////            } else {
////                iRdmMemberService.publishMemberEvent(m, MemberEvent.EventType.ADD_MEMBER);
////            }
////        });
//    }
//}
