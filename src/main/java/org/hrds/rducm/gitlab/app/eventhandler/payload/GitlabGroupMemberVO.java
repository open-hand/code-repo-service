package org.hrds.rducm.gitlab.app.eventhandler.payload;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/8
 */
public class GitlabGroupMemberVO {
    /**
     * 被更改角色的用户的用户名
     */
    private String username;

    /**
     * 项目Id
     */
    private Long resourceId;

    /**
     * 层级  site/organization/project
     */
    private String resourceType;

    /**
     * 权限列表
     */
    private List<String> roleLabels;

    /**
     * 被更改角色的用户的id
     */
    private Long userId;

    private String uuid;

    @ApiModelProperty("已经删除角色的标签")
    private Set<String> deleteRoleLabels;

    @ApiModelProperty("之前的角色")
    private Set<String> previousRoleLabels;

    public Set<String> getPreviousRoleLabels() {
        return previousRoleLabels;
    }

    public void setPreviousRoleLabels(Set<String> previousRoleLabels) {
        this.previousRoleLabels = previousRoleLabels;
    }

    public Set<String> getDeleteRoleLabels() {
        return deleteRoleLabels;
    }

    public void setDeleteRoleLabels(Set<String> deleteRoleLabels) {
        this.deleteRoleLabels = deleteRoleLabels;
    }

    public String getUsername() {
        return username;
    }

    public GitlabGroupMemberVO setUsername(String username) {
        this.username = username;
        return this;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public GitlabGroupMemberVO setResourceId(Long resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public String getResourceType() {
        return resourceType;
    }

    public GitlabGroupMemberVO setResourceType(String resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public List<String> getRoleLabels() {
        return roleLabels;
    }

    public GitlabGroupMemberVO setRoleLabels(List<String> roleLabels) {
        this.roleLabels = roleLabels;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public GitlabGroupMemberVO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public GitlabGroupMemberVO setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }
}
