package org.hrds.rducm.gitlab.infra.feign.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class C7nAppServiceVO {
    @ApiModelProperty("应用服务id")
    private Long id;

    @ApiModelProperty("应用服务名称")
    private String name;

    @ApiModelProperty("应用服务code")
    private String code;

    @ApiModelProperty("应用服务的类型")
    private String type;

    @ApiModelProperty("应用服务所属项目id")
    private Long projectId;

    @ApiModelProperty("应用服务对应gitlab项目的id")
    private Long gitlabProjectId;

    @ApiModelProperty("应用服务对应的gitlab仓库地址")
    private String repoUrl;

//    @ApiModelProperty("应用服务对应的gitlab的仓库的ssh协议克隆地址")
//    private String sshRepositoryUrl;

//    @ApiModelProperty("应用服务是否同步完成，false表示正在处理中")
//    private Boolean synchro;

    @ApiModelProperty("应用服务是否启用")
    private Boolean active;

//    private String publishLevel;
//    private String contributor;

    @ApiModelProperty("应用服务描述")
    private String description;

//    @ApiModelProperty("sonarqube地址")
//    private String sonarUrl;

    @ApiModelProperty("应用服务是否失败，如果已同步且这个值为true说明应用服务创建失败")
    private Boolean fail;


    @ApiModelProperty("应用服务数据库纪录的版本号")
    private Long objectVersionNumber;

//    @ApiModelProperty("应用服务对应的harbor配置信息")
//    private DevopsConfigVO harbor;

//    @ApiModelProperty("应用服务对应的chart配置信息")
//    private DevopsConfigVO chart;

    @ApiModelProperty("应用服务图标url")
    private String imgUrl;

    @ApiModelProperty("应用创建时间")
    private Date creationDate;

    @ApiModelProperty("应用服务最近更新时间")
    private Date lastUpdateDate;

    @ApiModelProperty("创建者用户名")
    private String createUserName;

    @ApiModelProperty("创建者登录名")
    private String createLoginName;

    @ApiModelProperty("最近更新者用户名")
    private String updateUserName;

    @ApiModelProperty("最近更新者登录名")
    private String updateLoginName;


    @Encrypt
    @ApiModelProperty("外部仓库配置id")
    private Long externalConfigId;

//    @ApiModelProperty("此应用服务是够跳过权限检查，true表示允许项目下所有的项目成员及项目所有者访问")
//    private Boolean skipCheckPermission;

//    @ApiModelProperty("是否是空仓库(是否没有分支)")
//    private Boolean emptyRepository;

//    @ApiModelProperty("应用服务类型")
//    private String serviceType;

//    public DevopsConfigVO getHarbor() {
//        return harbor;
//    }
//
//    public void setHarbor(DevopsConfigVO harbor) {
//        this.harbor = harbor;
//    }
//
//    public DevopsConfigVO getChart() {
//        return chart;
//    }
//
//    public void setChart(DevopsConfigVO chart) {
//        this.chart = chart;
//    }


    public Long getExternalConfigId() {
        return externalConfigId;
    }

    public void setExternalConfigId(Long externalConfigId) {
        this.externalConfigId = externalConfigId;
    }

    public Long getId() {
        return id;
    }

    public C7nAppServiceVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public C7nAppServiceVO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public C7nAppServiceVO setCode(String code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public C7nAppServiceVO setType(String type) {
        this.type = type;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public C7nAppServiceVO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getGitlabProjectId() {
        return gitlabProjectId;
    }

    public C7nAppServiceVO setGitlabProjectId(Long gitlabProjectId) {
        this.gitlabProjectId = gitlabProjectId;
        return this;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public C7nAppServiceVO setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
        return this;
    }

    public Boolean getActive() {
        return active;
    }

    public C7nAppServiceVO setActive(Boolean active) {
        this.active = active;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public C7nAppServiceVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public C7nAppServiceVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public C7nAppServiceVO setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public C7nAppServiceVO setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public C7nAppServiceVO setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
        return this;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public C7nAppServiceVO setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
        return this;
    }

    public String getCreateLoginName() {
        return createLoginName;
    }

    public C7nAppServiceVO setCreateLoginName(String createLoginName) {
        this.createLoginName = createLoginName;
        return this;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public C7nAppServiceVO setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
        return this;
    }

    public String getUpdateLoginName() {
        return updateLoginName;
    }

    public C7nAppServiceVO setUpdateLoginName(String updateLoginName) {
        this.updateLoginName = updateLoginName;
        return this;
    }

    public Boolean getFail() {
        return fail;
    }

    public void setFail(Boolean fail) {
        this.fail = fail;
    }

    @Override
    public String toString() {
        return "C7nAppServiceVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", projectId=" + projectId +
                ", gitlabProjectId=" + gitlabProjectId +
                ", repoUrl='" + repoUrl + '\'' +
                ", active=" + active +
                ", description='" + description + '\'' +
                ", objectVersionNumber=" + objectVersionNumber +
                ", imgUrl='" + imgUrl + '\'' +
                ", creationDate=" + creationDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", createUserName='" + createUserName + '\'' +
                ", createLoginName='" + createLoginName + '\'' +
                ", updateUserName='" + updateUserName + '\'' +
                ", updateLoginName='" + updateLoginName + '\'' +
                '}';
    }
}