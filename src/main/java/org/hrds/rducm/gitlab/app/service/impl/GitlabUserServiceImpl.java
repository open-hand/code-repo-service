package org.hrds.rducm.gitlab.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.RandomStringUtils;
import org.gitlab4j.api.models.User;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabUserViewDTO;
import org.hrds.rducm.gitlab.app.service.GitlabUserService;
import org.hrds.rducm.gitlab.domain.entity.GitlabUser;
import org.hrds.rducm.gitlab.domain.repository.GitlabUserRepository;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xy
 */
@Service
public class GitlabUserServiceImpl implements GitlabUserService {
    @Autowired
    private GitlabUserRepository gitlabUserRepository;

    @Override
    public GitlabUserViewDTO queryUserSelf() {
        // todo fixme 暂时写死
//        Long userId = DetailsHelper.getUserDetails().getUserId();
        Long userId = 10003L;
        GitlabUser gitlabUser = gitlabUserRepository.selectByUk(userId);
        User glUser = gitlabUserRepository.getUserFromGitlab(gitlabUser.getGlUserId());

        GitlabUserViewDTO gitlabUserViewDTO = ConvertUtils.convertObject(gitlabUser, GitlabUserViewDTO.class);
        gitlabUserViewDTO.setGlState(glUser.getState());
        gitlabUserViewDTO.setGlAvatarUrl(glUser.getAvatarUrl());
        gitlabUserViewDTO.setGlBio(glUser.getBio());
        gitlabUserViewDTO.setGlCreatedAt(glUser.getCreatedAt());
        gitlabUserViewDTO.setGlName(glUser.getName());
        gitlabUserViewDTO.setGlUsername(glUser.getUsername());
        gitlabUserViewDTO.setGlWebsiteUrl(glUser.getWebsiteUrl());
        gitlabUserViewDTO.setGlWebUrl(glUser.getWebUrl());

        return gitlabUserViewDTO;
    }

    @Override
    public GitlabUserViewDTO queryUser(Long userId) {
        GitlabUser gitlabUser = gitlabUserRepository.selectByUk(userId);
        return ConvertUtils.convertObject(gitlabUser, GitlabUserViewDTO.class);
    }

    /**
     * todo 需要分布式事务
     *
     * @param userId
     * @param glEmail
     * @param glUsername
     * @param glName
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUserWithRandomPassword(Long userId, String glEmail, String glUsername, String glName) {
        // 获取随机密码
        String randomPassword = RandomStringUtils.randomAlphanumeric(10);

        // <1> 往数据库添加用户
        GitlabUser gitlabUser = new GitlabUser();
        gitlabUser.setUserId(userId)
                .setInitPassword(randomPassword);
        gitlabUserRepository.insertSelective(gitlabUser);

        // <2> 调用gitlab api创建用户
        User user = gitlabUserRepository.createUserToGitlab(glEmail, glUsername, glName, randomPassword);

        // <3> gitlab调用成功, 数据库修改用户数据
        gitlabUser.setGlUserId(user.getId())
                .setGlUserName(user.getUsername())
                .setGlIsAdmin(user.getIsAdmin());
        gitlabUserRepository.updateByPrimaryKey(gitlabUser);
    }


//    /**
//     *
//     */
//    @Override
//    public void updatePasswordForUser(String password, String confirmPassword) {
//        // 校验密码是否一致
//        if (!Objects.equals(password, confirmPassword)) {
//            throw new CommonException("error.todo");
//        }
//
//        // 校验用户是否同步
//        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
//        AssertUtils.notNull(userDetails.getUserId(), "error.");
//        GitlabUser gitlabUser = new GitlabUser();
//        gitlabUser.setIamUserId(userDetails.getUserId());
//        gitlabUser = gitlabUserRepository.selectOne(gitlabUser);
//        this.checkUserSync(gitlabUser, userDetails.getUserId());
//
//        // 更新密码
//        gitlabUserApiRepository.updateUserPassword(gitlabUser.getUserId(), password);
//    }

    private void checkUserSync(GitlabUser gitlabUser, Long iamUserId) {
        if (gitlabUser == null || gitlabUser.getUserId() == null) {
            throw new CommonException("error.iam.user.sync.to.gitlab", iamUserId);
        }
    }
}
