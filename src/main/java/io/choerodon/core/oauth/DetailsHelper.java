package io.choerodon.core.oauth;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import org.hzero.core.base.BaseConstants;

/**
 * 根据请求解析出userDetail和clientDetail对象
 *
 * todo 测试专用,后续务必删除
 * fixme
 *
 * @author wuguokai
 */
public class DetailsHelper {

    private DetailsHelper() {
    }

    private static final CustomUserDetails ANONYMOUS;

    static {
        ANONYMOUS = new CustomUserDetails(BaseConstants.ANONYMOUS_USER_NAME, "unknown", Collections.emptyList());
        ANONYMOUS.setUserId(BaseConstants.ANONYMOUS_USER_ID);
        ANONYMOUS.setOrganizationId(BaseConstants.DEFAULT_TENANT_ID);
        ANONYMOUS.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        ANONYMOUS.setTenantIds(Collections.singletonList(BaseConstants.DEFAULT_TENANT_ID));
        ANONYMOUS.setLanguage(BaseConstants.DEFAULT_LOCALE_STR);
        ANONYMOUS.setTimeZone(BaseConstants.DEFAULT_TIME_ZONE);
    }

    public static final CustomUserDetails U10001;
    static {
        U10001 = new CustomUserDetails("cxh", "unknown", Collections.emptyList());
        U10001.setUserId(10001L);
        U10001.setOrganizationId(BaseConstants.DEFAULT_TENANT_ID);
        U10001.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        U10001.setTenantIds(Collections.singletonList(BaseConstants.DEFAULT_TENANT_ID));
        U10001.setLanguage(BaseConstants.DEFAULT_LOCALE_STR);
        U10001.setTimeZone(BaseConstants.DEFAULT_TIME_ZONE);
    }

    /**
     * 返回匿名用户
     */
    public static CustomUserDetails getAnonymousDetails() {
        return ANONYMOUS;
    }

    /**
     * {@inheritDoc}
     * 获取访问用户的userDetail对象
     *
     * @return CustomUserDetails
     */
    public static CustomUserDetails getUserDetails() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null) {
            return ANONYMOUS;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof OAuth2AuthenticationDetails) {
            Object decodedDetails = ((OAuth2AuthenticationDetails) details).getDecodedDetails();
            if (decodedDetails instanceof CustomUserDetails) {
                return (CustomUserDetails) decodedDetails;
            }
        }
        return U10001;
    }

    /**
     * {@inheritDoc}
     * 获取访问的clientDetail对象
     *
     * @return CustomClientDetails
     */
    public static CustomClientDetails getClientDetails() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomClientDetails) {
            return (CustomClientDetails) principal;
        }
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof OAuth2AuthenticationDetails) {
            Object decodedDetails = ((OAuth2AuthenticationDetails) details).getDecodedDetails();
            if (decodedDetails instanceof CustomClientDetails) {
                return (CustomClientDetails) decodedDetails;
            }
        }
        return null;
    }

    public static void setCustomUserDetails(CustomUserDetails customUserDetails) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            return;
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword());
        securityContext.setAuthentication(usernamePasswordAuthenticationToken);
    }

    public static void setCustomUserDetails(Long userId, String language) {
        CustomUserDetails customUserDetails = new CustomUserDetails("default", "default");
        customUserDetails.setUserId(userId);
        customUserDetails.setLanguage(language);
        setCustomUserDetails(customUserDetails);
    }
}
