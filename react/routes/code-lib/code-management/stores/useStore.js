import { useLocalStore } from 'mobx-react-lite';

export default function useStore() {
  return useLocalStore((PS_ASSIGN_TAB) => ({
    tabKey: PS_ASSIGN_TAB,

    setTabKey(data) {
      this.tabKey = data;
    },
    get getTabKey() {
      return this.tabKey;
    },
    // 是否是项目管理员权限
    hasPermission: false,
    setPermission(data) {
      this.hasPermission = data;
    },
    get getPermission() {
      return this.hasPermission;
    },
  }));
}
