//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.choerodon.core.oauth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * todo 测试专用,后续务必删除
 */
public class CustomUserDetails extends User implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);
	private static final long serialVersionUID = -3762281463683847665L;
	private Long userId;
	private String realName;
	private String email;
	private String timeZone;
	private String language;
	private String userType;
	private Long roleId;
	/** @deprecated */
	@Deprecated
	private String roleAssignLevel;
	/** @deprecated */
	@Deprecated
	private Long roleAssignValue;
	private List<Long> roleIds;
	private List<Long> siteRoleIds;
	private List<Long> tenantRoleIds;
	private Boolean roleMergeFlag;
	private Long tenantId;
	private String tenantNum;
	private List<Long> tenantIds;
	private String imageUrl;
	private Long organizationId;
	private Boolean isAdmin;
	private Long clientId;
	private String clientName;
	private Set<String> clientAuthorizedGrantTypes;
	private Set<String> clientResourceIds;
	private Set<String> clientScope;
	private Set<String> clientRegisteredRedirectUri;
	private Integer clientAccessTokenValiditySeconds;
	private Integer clientRefreshTokenValiditySeconds;
	private Set<String> clientAutoApproveScopes;
	private Map<String, Object> additionInfo;
	private Map<String, String> additionInfoMeaning;

	public CustomUserDetails(String username, String userType, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.userType = userType;
	}

	public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		this(username, "P", password, authorities);
	}

	public CustomUserDetails(@JsonProperty("username") String username, @JsonProperty("password") String password) {
		this(username, "P", password, Collections.emptyList());
	}

	@JsonCreator
	public CustomUserDetails(@JsonProperty("username") String username, @JsonProperty("userType") String userType, @JsonProperty("password") String password) {
		this(username, userType, password, Collections.emptyList());
	}

	public List<Long> roleMergeIds() {
		if(this.isRoleMergeFlag()) {
			if(!CollectionUtils.isEmpty(this.siteRoleIds) && this.siteRoleIds.contains(this.roleId)) {
				return this.siteRoleIds;
			}

			if(!CollectionUtils.isEmpty(this.tenantRoleIds) && this.tenantRoleIds.contains(this.roleId)) {
				return this.tenantRoleIds;
			}

			logger.error("The current role is not in any of the optional role lists");
		}

		return Collections.singletonList(this.roleId);
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getUserType() {
		return this.userType;
	}

	public Long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleAssignLevel() {
		return this.roleAssignLevel;
	}

	public CustomUserDetails setRoleAssignLevel(String roleAssignLevel) {
		this.roleAssignLevel = roleAssignLevel;
		return this;
	}

	public Long getRoleAssignValue() {
		return this.roleAssignValue;
	}

	public CustomUserDetails setRoleAssignValue(Long roleAssignValue) {
		this.roleAssignValue = roleAssignValue;
		return this;
	}

	public List<Long> getRoleIds() {
		return this.roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	public List<Long> getSiteRoleIds() {
		return this.siteRoleIds;
	}

	public CustomUserDetails setSiteRoleIds(List<Long> siteRoleIds) {
		this.siteRoleIds = siteRoleIds;
		return this;
	}

	public List<Long> getTenantRoleIds() {
		return this.tenantRoleIds;
	}

	public CustomUserDetails setTenantRoleIds(List<Long> tenantRoleIds) {
		this.tenantRoleIds = tenantRoleIds;
		return this;
	}

	public boolean isRoleMergeFlag() {
		return Boolean.TRUE.equals(this.roleMergeFlag);
	}

	public CustomUserDetails setRoleMergeFlag(Boolean roleMergeFlag) {
		this.roleMergeFlag = roleMergeFlag;
		return this;
	}

	public Long getTenantId() {
		return this.tenantId;
	}

	public CustomUserDetails setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

	public String getTenantNum() {
		return this.tenantNum;
	}

	public CustomUserDetails setTenantNum(String tenantNum) {
		this.tenantNum = tenantNum;
		return this;
	}

	public List<Long> getTenantIds() {
		return this.tenantIds;
	}

	public CustomUserDetails setTenantIds(List<Long> tenantIds) {
		this.tenantIds = tenantIds;
		return this;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public CustomUserDetails setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRealName() {
		return this.realName;
	}

	public CustomUserDetails setRealName(String realName) {
		this.realName = realName;
		return this;
	}

	public Long getOrganizationId() {
		return this.organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getTimeZone() {
		return this.timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public Map<String, Object> getAdditionInfo() {
		return this.additionInfo;
	}

	public void setAdditionInfo(Map<String, Object> additionInfo) {
		this.additionInfo = additionInfo;
	}

	public CustomUserDetails addAdditionInfo(String key, Object value) {
		if(this.additionInfo == null) {
			this.additionInfo = new HashMap();
		}

		this.additionInfo.put(key, value);
		return this;
	}

	public Map<String, String> getAdditionInfoMeaning() {
		return this.additionInfoMeaning;
	}

	public Object readAdditionInfo(String key) {
		return this.additionInfo == null?null:this.additionInfo.get(key);
	}

	public CustomUserDetails addAdditionMeaning(String key, String meaning) {
		if(this.additionInfoMeaning == null) {
			this.additionInfoMeaning = new HashMap();
		}

		this.additionInfoMeaning.put(key, meaning);
		return this;
	}

	public CustomUserDetails setAdditionInfoMeaning(Map<String, String> additionInfoMeaning) {
		this.additionInfoMeaning = additionInfoMeaning;
		return this;
	}

	public String readAdditionInfoMeaning(String key) {
		return this.additionInfoMeaning == null?null:(String)this.additionInfoMeaning.get(key);
	}

	public CustomUserDetails removeAdditionInfo(String key) {
		if(this.additionInfo != null) {
			this.additionInfo.remove(key);
		}

		if(this.additionInfoMeaning != null) {
			this.additionInfoMeaning.remove(key);
		}

		return this;
	}

	public CustomUserDetails removeAdditionInfos(Collection<String> keys) {
		if(!CollectionUtils.isEmpty(keys)) {
			keys.forEach(this::removeAdditionInfo);
		}

		return this;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getAdmin() {
		return this.isAdmin;
	}

	public void setAdmin(Boolean admin) {
		this.isAdmin = admin;
	}

	public Long getClientId() {
		return this.clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return this.clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Set<String> getClientAuthorizedGrantTypes() {
		return this.clientAuthorizedGrantTypes;
	}

	public void setClientAuthorizedGrantTypes(Collection<String> clientAuthorizedGrantTypes) {
		this.clientAuthorizedGrantTypes = (Set)(clientAuthorizedGrantTypes == null?Collections.emptySet():new LinkedHashSet(clientAuthorizedGrantTypes));
	}

	public Set<String> getClientResourceIds() {
		return this.clientResourceIds;
	}

	public void setClientResourceIds(Collection<String> clientResourceIds) {
		this.clientResourceIds = (Set)(clientResourceIds == null?Collections.emptySet():new LinkedHashSet(clientResourceIds));
	}

	public Set<String> getClientScope() {
		return this.clientScope;
	}

	public void setClientScope(Collection<String> clientScope) {
		this.clientScope = (Set)(clientScope == null?Collections.emptySet():new LinkedHashSet(clientScope));
	}

	public Set<String> getClientRegisteredRedirectUri() {
		return this.clientRegisteredRedirectUri;
	}

	public void setClientRegisteredRedirectUri(Collection<String> clientRegisteredRedirectUri) {
		this.clientRegisteredRedirectUri = clientRegisteredRedirectUri == null?null:new LinkedHashSet(clientRegisteredRedirectUri);
	}

	public Integer getClientAccessTokenValiditySeconds() {
		return this.clientAccessTokenValiditySeconds;
	}

	public void setClientAccessTokenValiditySeconds(Integer clientAccessTokenValiditySeconds) {
		this.clientAccessTokenValiditySeconds = clientAccessTokenValiditySeconds;
	}

	public Integer getClientRefreshTokenValiditySeconds() {
		return this.clientRefreshTokenValiditySeconds;
	}

	public void setClientRefreshTokenValiditySeconds(Integer clientRefreshTokenValiditySeconds) {
		this.clientRefreshTokenValiditySeconds = clientRefreshTokenValiditySeconds;
	}

	public Set<String> getClientAutoApproveScopes() {
		return this.clientAutoApproveScopes;
	}

	public void setClientAutoApproveScopes(Collection<String> clientAutoApproveScopes) {
		this.clientAutoApproveScopes = clientAutoApproveScopes == null?null:new LinkedHashSet(clientAutoApproveScopes);
	}

	public boolean equals(Object o) {
		if(this == o) {
			return true;
		} else if(o != null && this.getClass() == o.getClass()) {
			if(!super.equals(o)) {
				return false;
			} else {
				CustomUserDetails that = (CustomUserDetails)o;
				return Objects.equals(this.userId, that.userId) && Objects.equals(this.realName, that.realName) && Objects.equals(this.email, that.email) && Objects.equals(this.timeZone, that.timeZone) && Objects.equals(this.language, that.language) && Objects.equals(this.roleId, that.roleId) && Objects.equals(this.roleAssignLevel, that.roleAssignLevel) && Objects.equals(this.roleAssignValue, that.roleAssignValue) && Objects.equals(this.roleIds, that.roleIds) && Objects.equals(this.siteRoleIds, that.siteRoleIds) && Objects.equals(this.tenantRoleIds, that.tenantRoleIds) && Objects.equals(this.roleMergeFlag, that.roleMergeFlag) && Objects.equals(this.tenantId, that.tenantId) && Objects.equals(this.tenantNum, that.tenantNum) && Objects.equals(this.tenantIds, that.tenantIds) && Objects.equals(this.imageUrl, that.imageUrl) && Objects.equals(this.organizationId, that.organizationId) && Objects.equals(this.isAdmin, that.isAdmin) && Objects.equals(this.clientId, that.clientId) && Objects.equals(this.clientName, that.clientName) && Objects.equals(this.clientAuthorizedGrantTypes, that.clientAuthorizedGrantTypes) && Objects.equals(this.clientResourceIds, that.clientResourceIds) && Objects.equals(this.clientScope, that.clientScope) && Objects.equals(this.clientRegisteredRedirectUri, that.clientRegisteredRedirectUri) && Objects.equals(this.clientAccessTokenValiditySeconds, that.clientAccessTokenValiditySeconds) && Objects.equals(this.clientRefreshTokenValiditySeconds, that.clientRefreshTokenValiditySeconds) && Objects.equals(this.clientAutoApproveScopes, that.clientAutoApproveScopes) && Objects.equals(this.additionInfo, that.additionInfo);
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{Integer.valueOf(super.hashCode()), this.userId, this.realName, this.email, this.timeZone, this.language, this.roleId, this.roleAssignLevel, this.roleAssignValue, this.roleIds, this.siteRoleIds, this.tenantRoleIds, this.roleMergeFlag, this.tenantId, this.tenantNum, this.tenantIds, this.imageUrl, this.organizationId, this.isAdmin, this.clientId, this.clientName, this.clientAuthorizedGrantTypes, this.clientResourceIds, this.clientScope, this.clientRegisteredRedirectUri, this.clientAccessTokenValiditySeconds, this.clientRefreshTokenValiditySeconds, this.clientAutoApproveScopes, this.additionInfo, this.additionInfoMeaning});
	}

	public String toString() {
		return "CustomUserDetails{userId=" + this.userId + ", realName=\'" + this.realName + '\'' + ", email=\'" + this.email + '\'' + ", timeZone=\'" + this.timeZone + '\'' + ", language=\'" + this.language + '\'' + ", roleId=" + this.roleId + ", roleAssignLevel=\'" + this.roleAssignLevel + '\'' + ", roleAssignValue=" + this.roleAssignValue + ", roleIds=" + this.roleIds + ", siteRoleIds=" + this.siteRoleIds + ", tenantRoleIds=" + this.tenantRoleIds + ", roleMergeFlag=" + this.roleMergeFlag + ", tenantId=" + this.tenantId + ", tenantNum=\'" + this.tenantNum + '\'' + ", tenantIds=" + this.tenantIds + ", imageUrl=\'" + this.imageUrl + '\'' + ", organizationId=" + this.organizationId + ", isAdmin=" + this.isAdmin + ", clientId=" + this.clientId + ", clientName=\'" + this.clientName + '\'' + ", clientAuthorizedGrantTypes=" + this.clientAuthorizedGrantTypes + ", clientResourceIds=" + this.clientResourceIds + ", clientScope=" + this.clientScope + ", clientRegisteredRedirectUri=" + this.clientRegisteredRedirectUri + ", clientAccessTokenValiditySeconds=" + this.clientAccessTokenValiditySeconds + ", clientRefreshTokenValiditySeconds=" + this.clientRefreshTokenValiditySeconds + ", clientAutoApproveScopes=" + this.clientAutoApproveScopes + ", additionInfo=" + this.additionInfo + '}';
	}

	public String toJSONString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
