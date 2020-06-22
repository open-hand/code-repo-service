import { action, computed, observable } from 'mobx';
import { axios } from '@choerodon/boot';

class SvnUserStore {
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
  }

  @action
  setAvatar(avatar) {
    this.avatar = avatar;
  }

  @computed
  get getAvatar() {
    return this.avatar;
  }

  // 文档库-修改默认密码
  updatePassword = (organizationId, body) => axios.post('/rdudm/v1/doc-users/updatePwd', JSON.stringify(body));
}

export default SvnUserStore;
