package org.hrds.rducm.gitlab.app.service.impl;

import com.sun.tools.internal.xjc.reader.TypeUtil;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.gitlab4j.api.models.User;
import org.hrds.rducm.gitlab.api.controller.vo.GitlabUserVO;
import org.hrds.rducm.gitlab.app.service.GitlabUserService;
import org.hrds.rducm.gitlab.domain.entity.GitlabUser;
import org.hrds.rducm.gitlab.domain.repository.GitlabUserApiRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabUserRepository;
import org.hzero.core.util.AssertUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author xy
 */
@Service
public class GitlabUserServiceImpl implements GitlabUserService {
    private GitlabUserApiRepository gitlabUserApiRepository;

    private GitlabUserRepository gitlabUserRepository;

    @Override
    public GitlabUserVO queryUser(Long userId) {
        GitlabUser gitlabUser = new GitlabUser();
        gitlabUser.setUserId(userId);
        gitlabUser = gitlabUserRepository.selectOne(gitlabUser);
        return ConvertHelper.convert(gitlabUser, GitlabUserVO.class);
    }

    /**
     * todo 需要分布式事务
     * @param email
     * @param username
     * @param name
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUserWithRandomPassword(String email, String username, String name) {
        // 获取随机密码
        String randomPassword = RandomStringUtils.randomAlphanumeric(10);

        // <1> 往数据库添加用户
        GitlabUser gitlabUser = new GitlabUser();
        gitlabUser.setIamUserId(DetailsHelper.getUserDetails().getUserId());
        gitlabUser.setState("INIT");
        gitlabUserRepository.insertSelective(gitlabUser);

        // <2> 调用gitlab api创建用户
        User user = gitlabUserApiRepository.createUser(email, username, name, randomPassword);

        // <3> gitlab调用成功, 数据库修改用户数据
        gitlabUser.setUserId(Long.valueOf(user.getId()))
                .setUserName(user.getUsername())
                .setAdmin(user.getIsAdmin())
                .setInitPassword(randomPassword)
                .setState("SUCCESS");
        gitlabUserRepository.updateByPrimaryKey(gitlabUser);
    }

    /**
     *
     */
    @Override
    public void updatePasswordForUser(String password, String confirmPassword) {
        // 校验密码是否一致
        if (!Objects.equals(password, confirmPassword)) {
            throw new CommonException("error.todo");
        }

        // 校验用户是否同步
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        AssertUtils.notNull(userDetails.getUserId(), "error.");
        GitlabUser gitlabUser = new GitlabUser();
        gitlabUser.setIamUserId(userDetails.getUserId());
        gitlabUser = gitlabUserRepository.selectOne(gitlabUser);
        this.checkUserSync(gitlabUser, userDetails.getUserId());

        // 更新密码
        gitlabUserApiRepository.updateUserPassword(gitlabUser.getUserId(), password);
    }

    private void checkUserSync(GitlabUser gitlabUser, Long iamUserId) {
        if (gitlabUser == null || gitlabUser.getUserId() == null) {
            throw new CommonException("error.iam.user.sync.to.gitlab", iamUserId);
        }
    }
}
