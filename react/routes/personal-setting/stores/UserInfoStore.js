import { action, computed, observable } from 'mobx';
import { axios } from '@choerodon/boot';

class UserInfoStore {
  @observable userInfo = {};

  @observable avatar;

  @computed
  get getUserInfo() {
    return this.userInfo;
  }

  @action
  setUserInfo(data) {
    this.userInfo = data;
    this.avatar = data.imageUrl;
    // this.avatar = data.glUser.avatarUrl;
  }

  @action
  setAvatar(avatar) {
    this.avatar = avatar;
  }

  @computed
  get getAvatar() {
    return this.avatar;
  }

  // 查询用户gitLab信息
  loadUserInfo = () => axios.get('/rducm/v1/gitlab/users/self');

  // 更新密码
  updatePassword = (id, body) => axios.put(`/base/v1/users/${id}/password`, JSON.stringify(body));

  // 重置Gitlab密码
  resetPassword = (userId) => (
    axios.put(`/devops/v1/users/${userId}/git_password`)
  );
}

export default UserInfoStore;
