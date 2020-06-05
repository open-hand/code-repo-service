package org.hrds.rducm.gitlab.domain.aggregate;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/4/7
 */
public class MemberAuthDetailAgg {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 已授权服务数
     */
    private Integer authorizedRepositoryCount;

    public Long getUserId() {
        return userId;
    }

    public MemberAuthDetailAgg setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Integer getAuthorizedRepositoryCount() {
        return authorizedRepositoryCount;
    }

    public MemberAuthDetailAgg setAuthorizedRepositoryCount(Integer authorizedRepositoryCount) {
        this.authorizedRepositoryCount = authorizedRepositoryCount;
        return this;
    }
}
