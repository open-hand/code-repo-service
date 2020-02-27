package org.hrds.rducm.gitlab.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Gitlab用户表
 *
 * @author ying.xie@hand-china.com 2020-02-27 16:13:38
 */
@ApiModel("Gitlab用户表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rducm_gitlab_user")
public class GitlabUser extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_INIT_PASSWORD = "initPassword";
    public static final String FIELD_RESET_PASSWORD_FLAG = "resetPasswordFlag";
    public static final String FIELD_GL_USER_ID = "glUserId";
    public static final String FIELD_GL_USER_NAME = "glUserName";
    public static final String FIELD_GL_IS_ADMIN = "glIsAdmin";
    public static final String FIELD_GL_IMPERSONATION_TOKEN = "glImpersonationToken";
    public static final String FIELD_SYNC_GITLAB_FLAG = "syncGitlabFlag";
    public static final String FIELD_SYNC_DATE_GITLAB = "syncDateGitlab";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("")
    @Id
    @GeneratedValue
    private Long id;
    @ApiModelProperty(value = "用户id", required = true)
    @NotNull
    private Long userId;
    @ApiModelProperty(value = "默认密码")
    private String initPassword;
    @ApiModelProperty(value = "是否已重置密码", required = true)
    @NotNull
    private Boolean resetPasswordFlag;
    @ApiModelProperty(value = "Gitlab用户id")
    private Integer glUserId;
    @ApiModelProperty(value = "Gitlab用户名")
    private String glUserName;
    @ApiModelProperty(value = "是否是gitlab的admin")
    private Boolean glIsAdmin;
    @ApiModelProperty(value = "impersonationToken")
    private String glImpersonationToken;
    @ApiModelProperty(value = "gitlab同步标识", required = true)
    @NotNull
    private Boolean syncGitlabFlag;
    @ApiModelProperty(value = "gitlab同步时间")
    private Date syncDateGitlab;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


	public Long getId() {
		return id;
	}

	public GitlabUser setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public GitlabUser setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public String getInitPassword() {
		return initPassword;
	}

	public GitlabUser setInitPassword(String initPassword) {
		this.initPassword = initPassword;
		return this;
	}

	public Boolean getResetPasswordFlag() {
		return resetPasswordFlag;
	}

	public GitlabUser setResetPasswordFlag(Boolean resetPasswordFlag) {
		this.resetPasswordFlag = resetPasswordFlag;
		return this;
	}

	public Integer getGlUserId() {
		return glUserId;
	}

	public GitlabUser setGlUserId(Integer glUserId) {
		this.glUserId = glUserId;
		return this;
	}

	public String getGlUserName() {
		return glUserName;
	}

	public GitlabUser setGlUserName(String glUserName) {
		this.glUserName = glUserName;
		return this;
	}

	public Boolean getGlIsAdmin() {
		return glIsAdmin;
	}

	public GitlabUser setGlIsAdmin(Boolean glIsAdmin) {
		this.glIsAdmin = glIsAdmin;
		return this;
	}

	public String getGlImpersonationToken() {
		return glImpersonationToken;
	}

	public GitlabUser setGlImpersonationToken(String glImpersonationToken) {
		this.glImpersonationToken = glImpersonationToken;
		return this;
	}

	public Boolean getSyncGitlabFlag() {
		return syncGitlabFlag;
	}

	public GitlabUser setSyncGitlabFlag(Boolean syncGitlabFlag) {
		this.syncGitlabFlag = syncGitlabFlag;
		return this;
	}

	public Date getSyncDateGitlab() {
		return syncDateGitlab;
	}

	public GitlabUser setSyncDateGitlab(Date syncDateGitlab) {
		this.syncDateGitlab = syncDateGitlab;
		return this;
	}
}
