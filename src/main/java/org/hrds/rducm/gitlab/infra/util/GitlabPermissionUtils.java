package org.hrds.rducm.gitlab.infra.util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.hrds.rducm.gitlab.infra.mapper.RdmMemberMapper;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Created by wangxiang on 2022/1/14
 */
@Component
public class GitlabPermissionUtils {

    @Autowired
    private RdmMemberMapper rdmMemberMapper;


    /**
     * 用户在项目下生效的全局权限
     *
     * @param userId
     * @param projectId
     * @return
     */
    public Integer getUserGroupAccessLevel(Long userId, Long projectId) {
        RdmMember userGroupPermission = getUserGroupPermission(userId, projectId);
        return userGroupPermission.getGlAccessLevel();
    }

    public RdmMember getUserGroupPermission(Long userId, Long projectId) {
        RdmMember condition = new RdmMember();
        condition.setProjectId(projectId);
        condition.setUserId(userId);
        condition.setType(AuthorityTypeEnum.GROUP.getValue());
        condition.setSyncGitlabFlag(Boolean.TRUE);
        List<RdmMember> rdmMembers = rdmMemberMapper.select(condition);
        if (CollectionUtils.isEmpty(rdmMembers)) {
            return new RdmMember();
        }
        return rdmMembers.stream().sorted(Comparator.comparing(RdmMember::getGlAccessLevel).reversed()).collect(Collectors.toList()).get(0);
    }


    /**
     * 用户的仓库权限
     *
     * @param userId
     * @param projectId
     * @param repositoryId
     * @return
     */
    public Integer getUserRepositoryAccessLevel(Long userId, Long projectId, Long repositoryId) {
        Integer userGroupAccessLevel = getUserGroupAccessLevel(userId, projectId);
        RdmMember condition = new RdmMember();
        condition.setProjectId(projectId);
        condition.setUserId(userId);
        condition.setType(AuthorityTypeEnum.PROJECT.getValue());
        condition.setSyncGitlabFlag(Boolean.TRUE);
        condition.setRepositoryId(repositoryId);
        RdmMember rdmMember = rdmMemberMapper.selectOne(condition);
        if (rdmMember == null) {
            return userGroupAccessLevel;
        }
        if (userGroupAccessLevel == 0) {
            return rdmMember.getGlAccessLevel();
        } else {
            if (rdmMember.getGlAccessLevel() > userGroupAccessLevel) {
                return rdmMember.getGlAccessLevel();
            } else {
                return userGroupAccessLevel;
            }
        }
    }


}
