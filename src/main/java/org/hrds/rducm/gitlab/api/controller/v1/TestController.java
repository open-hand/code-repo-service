//package org.hrds.rducm.gitlab.api.controller.v1;
//
//import io.choerodon.core.annotation.Permission;
//import io.choerodon.core.convertor.ApplicationContextHelper;
//import io.choerodon.core.enums.ResourceType;
//import io.choerodon.core.oauth.DetailsHelper;
//import io.swagger.annotations.ApiOperation;
//import org.apache.rocketmq.client.producer.SendCallback;
//import org.apache.rocketmq.client.producer.SendResult;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
//import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
//import org.hrds.rducm.test.RocketMQProducer;
//import org.hzero.core.base.BaseController;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//
///**
// * todo 需删除
// */
//@RestController
//@RequestMapping("/v1/gitlab/test")
//public class TestController extends BaseController {
//    @Autowired
//    private Gitlab4jClientWrapper gitlab4jClientWrapper;
////
////    @Autowired
////    private RdmMemberAuditRecordServiceImpl securityAudit;
////
////    @Autowired
////    private BaseServiceFeignClient baseServiceFeignClient;
//
//    static Logger logger = LoggerFactory.getLogger(TestController.class);
//
//    @ApiOperation(value = "查询")
//    @Permission(type = ResourceType.SITE, permissionLogin = true)
//    @PostMapping("/users")
//    public ResponseEntity<List<C7nUserVO>> queryUser() {
//        Long userId = DetailsHelper.getUserDetails().getUserId();
//
//        logger.warn("-------------------- getUserDetails:{}", DetailsHelper.getUserDetails());
//        logger.warn("-------------------- userId:{}", userId);
////        return baseServiceFeignClient.listUsersByIds(ids, null);
//        return null;
//    }
//
//    @ApiOperation(value = "查询")
//    @Permission(type = ResourceType.SITE, permissionPublic = true)
//    @PostMapping("/test")
//    public ResponseEntity<List<C7nUserVO>> test() {
//        RocketMQTemplate rocketMQTemplate = ApplicationContextHelper.getContext().getBean(RocketMQTemplate.class);
//        // 同步发送消息
//        rocketMQTemplate.convertAndSend("test-topic-1", "Hello world");
//        //
//        rocketMQTemplate.send("test-topic-1", MessageBuilder.withPayload("hello world").build());
//        //
//        rocketMQTemplate.asyncSend("test-topic-2", new RocketMQProducer.OrderPaidEvent("T_001", new BigDecimal(100)), new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//                System.out.printf("async on Success sendResult=%s %n", sendResult);
//            }
//
//            @Override
//            public void onException(Throwable throwable) {
//                System.out.printf("async onException throwable=%s %n", throwable);
//            }
//        });
//        //
//        rocketMQTemplate.syncSendOrderly("orderly_topic", MessageBuilder.withPayload("hello world").build(), "hashKey");
//        return ResponseEntity.ok(null);
//    }
//
//
//}
