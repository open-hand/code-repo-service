package org.hrds.rducm.gitlab.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.RandomStringUtils;
import org.gitlab4j.api.models.User;
import org.hrds.rducm.gitlab.api.controller.dto.RdmUserViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmUserService;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xy
 */
@Service
public class RdmUserServiceImpl implements RdmUserService {
    @Autowired
    private RdmUserRepository rdmUserRepository;

    @Override
    public RdmUserViewDTO queryUserSelf() {
        // todo fixme 暂时写死
//        Long userId = DetailsHelper.getUserDetails().getUserId();
        Long userId = 10003L;
        RdmUser rdmUser = rdmUserRepository.selectByUk(userId);
        User glUser = rdmUserRepository.getUserFromGitlab(rdmUser.getGlUserId());

        RdmUserViewDTO rdmUserViewDTO = ConvertUtils.convertObject(rdmUser, RdmUserViewDTO.class);
        rdmUserViewDTO.setGlState(glUser.getState());
        rdmUserViewDTO.setGlAvatarUrl(glUser.getAvatarUrl());
        rdmUserViewDTO.setGlBio(glUser.getBio());
        rdmUserViewDTO.setGlCreatedAt(glUser.getCreatedAt());
        rdmUserViewDTO.setGlName(glUser.getName());
        rdmUserViewDTO.setGlUsername(glUser.getUsername());
        rdmUserViewDTO.setGlWebsiteUrl(glUser.getWebsiteUrl());
        rdmUserViewDTO.setGlWebUrl(glUser.getWebUrl());

        return rdmUserViewDTO;
    }

    @Override
    public RdmUserViewDTO queryUser(Long userId) {
        RdmUser rdmUser = rdmUserRepository.selectByUk(userId);
        return ConvertUtils.convertObject(rdmUser, RdmUserViewDTO.class);
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
        RdmUser rdmUser = new RdmUser();
        rdmUser.setUserId(userId)
                .setInitPassword(randomPassword);
        rdmUserRepository.insertSelective(rdmUser);

        // <2> 调用gitlab api创建用户
        User user = rdmUserRepository.createUserToGitlab(glEmail, glUsername, glName, randomPassword);

        // <3> gitlab调用成功, 数据库修改用户数据
        rdmUser.setGlUserId(user.getId())
                .setGlUserName(user.getUsername())
                .setGlIsAdmin(user.getIsAdmin());
        rdmUserRepository.updateByPrimaryKey(rdmUser);
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
//        RdmUser gitlabUser = new RdmUser();
//        gitlabUser.setIamUserId(userDetails.getUserId());
//        gitlabUser = rdmUserRepository.selectOne(gitlabUser);
//        this.checkUserSync(gitlabUser, userDetails.getUserId());
//
//        // 更新密码
//        gitlabUserApiRepository.updateUserPassword(gitlabUser.getUserId(), password);
//    }

    private void checkUserSync(RdmUser rdmUser, Long iamUserId) {
        if (rdmUser == null || rdmUser.getUserId() == null) {
            throw new CommonException("error.iam.user.sync.to.gitlab", iamUserId);
        }
    }
}
