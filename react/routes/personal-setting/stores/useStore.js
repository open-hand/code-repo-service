import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    // 制品库配置-修改默认密码
    updateProdPassword(body) {
      return axios.post('/rdupm/v1/prod-users/updatePwd', JSON.stringify(body));
    },
  }));
}
