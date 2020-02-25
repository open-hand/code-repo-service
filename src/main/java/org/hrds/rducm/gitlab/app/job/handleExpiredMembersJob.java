//package org.hrds.rducm.gitlab.app.job;
//
//import org.hrds.rducm.gitlab.app.service.GitlabMemberService;
//import org.hzero.boot.scheduler.infra.annotation.JobHandler;
//import org.hzero.boot.scheduler.infra.enums.ReturnT;
//import org.hzero.boot.scheduler.infra.handler.IJobHandler;
//import org.hzero.boot.scheduler.infra.tool.SchedulerTool;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Map;
//
//@JobHandler("handleExpiredMembers")
//public class handleExpiredMembersJob implements IJobHandler {
//    @Autowired
//    private GitlabMemberService gitlabMemberService;
//
//    @Override
//    public ReturnT execute(Map<String, String> map, SchedulerTool tool) {
//        gitlabMemberService.handleExpiredMembers();
//
//        // 任务日志记录
//        tool.info("示例任务执行成功！");
//        return ReturnT.SUCCESS;
//    }
//}