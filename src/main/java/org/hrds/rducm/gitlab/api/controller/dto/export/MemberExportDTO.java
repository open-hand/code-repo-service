package org.hrds.rducm.gitlab.api.controller.dto.export;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.core.base.BaseConstants;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.hzero.export.render.ValueRenderer;

import java.util.Date;
import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/23
 */
@ExcelSheet(title = "成员权限")
public class MemberExportDTO {
    @ExcelColumn(title = "用户名")
    private String realName;

    @ExcelColumn(title = "登录名")
    private String loginName;

    @ExcelColumn(title = "应用服务名")
    private String appServiceName;

    @ExcelColumn(title = "项目角色")
    private List<String> roleNames;

    @ExcelColumn(title = "权限")
    private String glAccessLevel;

    @ExcelColumn(title = "过期时间", pattern = BaseConstants.Pattern.DATE)
    private Date glExpiresAt;

    @ExcelColumn(title = "创建人")
    private String createdByName;

    @ExcelColumn(title = "添加时间", pattern = BaseConstants.Pattern.DATETIME)
    private Date creationDate;

    @ExcelColumn(title = "Gitlab同步标识", renderers = SyncGitlabFlagValueRenderer.class)
    private Boolean syncGitlabFlag;

    @ExcelColumn(title = "Gitlab同步时间", pattern = BaseConstants.Pattern.DATETIME)
    private Date syncDateGitlab;

    public static class SyncGitlabFlagValueRenderer implements ValueRenderer {
        @Override
        public Object render(Object value, Object data) {
            System.out.println("------------------------" + value);
            System.out.println(data);

            return (Boolean) value ? "已同步" : "未同步";
        }
    }


    public String getRealName() {
        return realName;
    }

    public MemberExportDTO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public MemberExportDTO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getAppServiceName() {
        return appServiceName;
    }

    public MemberExportDTO setAppServiceName(String appServiceName) {
        this.appServiceName = appServiceName;
        return this;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public MemberExportDTO setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
        return this;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public MemberExportDTO setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
        return this;
    }

    public String getGlAccessLevel() {
        return glAccessLevel;
    }

    public MemberExportDTO setGlAccessLevel(String glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public MemberExportDTO setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }

    public Boolean getSyncGitlabFlag() {
        return syncGitlabFlag;
    }

    public MemberExportDTO setSyncGitlabFlag(Boolean syncGitlabFlag) {
        this.syncGitlabFlag = syncGitlabFlag;
        return this;
    }

    public Date getSyncDateGitlab() {
        return syncDateGitlab;
    }

    public MemberExportDTO setSyncDateGitlab(Date syncDateGitlab) {
        this.syncDateGitlab = syncDateGitlab;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public MemberExportDTO setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}
